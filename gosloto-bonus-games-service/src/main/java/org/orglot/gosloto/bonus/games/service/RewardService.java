package org.orglot.gosloto.bonus.games.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gosloto.promo.PromoWalletGrpcClient;
import org.orglot.bonus.games.model.BonusGameType;
import org.orglot.bonus.games.model.PrizeSubType;
import org.orglot.bonus.games.model.request.ApplyRewardUserData;
import org.orglot.bonus.games.model.response.PrizeType;
import org.orglot.bonus.games.model.response.Reward;
import org.orglot.gosloto.bonus.client.operation.bonusrefill.model.BonusRefillReason;
import org.orglot.gosloto.bonus.games.config.RefreshBeanConfiguration;
import org.orglot.gosloto.bonus.games.exception.IllegalProfileElementException;
import org.orglot.gosloto.bonus.games.model.UserSession;
import org.orglot.gosloto.bonus.games.model.prize.BonusGameRandoms;
import org.orglot.gosloto.bonus.games.model.prize.Range;
import org.orglot.gosloto.bonus.games.service.puzzle.collection.PuzzleCollectionService;
import org.orglot.gosloto.bonus.games.service.puzzle.collection.PuzzleItemService;
import org.orglot.gosloto.bonus.games.service.puzzle.collection.UserPuzzleItemService;
import org.orglot.gosloto.bonus.games.service.strategy.nards.enums.NardsCombinationPrizeSubType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardService {
  private static final int RANDOM_MIN = 1;
  private static final String AVATAR_TYPE = "avatar";
  private static final String FRAME_TYPE = "frame";

  private final PrizeService prizeService;
  private final PuzzleCollectionService puzzleCollectionService;
  private final PurchaseService purchaseService;
  private final BonusGameSettingsService bonusGameService;
  private final UserProfileService userProfileService;
  private final PromoWalletGrpcClient promoWalletGrpcClient;
  private final RefreshBeanConfiguration.RandomConfigsBean prizeCalculationProperties;
  private final UserPuzzleItemService userPuzzleItemService;
  private final PuzzleItemService puzzleItemService;
  private final Random random = new Random();

  public Flux<Reward> getRandomRewardsByRandomIds(List<Integer> randomIds) {
    return Flux.fromIterable(randomIds)
        .flatMap(this::getRandomRewardByRandomId);
  }

  public Flux<Reward> getRandomRewardsByRandomIdForNards(Integer randomId) {
    return Mono.just(randomId)
        .flatMapMany(id -> Mono.justOrEmpty(prizeCalculationProperties.getRandoms())
            .mapNotNull(BonusGameRandoms::getRandoms)
            .flatMapMany(Flux::fromIterable)
            .filter(config -> id.equals(config.getId()))
            .flatMap(config -> Flux.fromIterable(config.getRanges())
                .next()
                .filter(range -> NardsCombinationPrizeSubType.isCombination(range.getPrizeSubType()))
                .flatMap(range -> Mono.justOrEmpty(calculateRange(config.getRangeLimit(), config.getRanges()))
                    .map(List::of)
                    .defaultIfEmpty(Collections.emptyList())
                )
                .defaultIfEmpty(config.getRanges())
                .flatMapMany(Flux::fromIterable)
            )
        )
        .flatMap(this::buildReward);
  }

  public Mono<Reward> getRandomRewardByRandomId(Integer randomId) {
    return Mono.just(randomId)
        .flatMap(id -> {
          var randomsConfig = prizeCalculationProperties.getRandoms();
          if (Objects.isNull(randomsConfig) || Objects.isNull(randomsConfig.getRandoms())) {
            log.warn("Null prizeCalculationConfig for get rewards");
            return Mono.just(Range.builder().count(0).prizeType(PrizeType.EMPTY).build());
          }
          var prizeCalculationConfig = randomsConfig.getRandoms().stream()
              .filter(config -> id.equals(config.getId()))
              .findFirst()
              .orElse(null);
          if (Objects.isNull(prizeCalculationConfig)) {
            log.warn("Null prizeCalculationConfig for get rewards");
            return Mono.just(Range.builder().count(0).prizeType(PrizeType.EMPTY).build());
          }
          var maxInterval = prizeCalculationConfig.getRangeLimit();
          var range = calculateRange(maxInterval, prizeCalculationConfig.getRanges());
          return Objects.nonNull(range) ? Mono.just(range) : Mono.just(Range.builder().count(0).prizeType(PrizeType.EMPTY).build());
        })
        .flatMap(this::buildReward);
  }

  /**
   * Получить сгенерированные награды и применить соответствующим образом пользователю
   *
   * @param userSession бонусная сессия пользователя
   * @param mobile      номер телефона пользователя
   * @return список наград примененных пользователю
   */
  public Mono<List<Reward>> getAndApplyRewards(UserSession userSession, String mobile) {
    var gameType = BonusGameType.valueOf(userSession.getGameType());
    var bet = BetDataService.fromJsonBetData(userSession.getBet(), BetDataService.BET_DATA_TYPE_REF);
    var modes = bonusGameService.getModes(gameType);
    var price = BetDataService.getBetPrice(bet, modes);
    return getAndApplyRewards(userSession, bet, gameType, mobile, price);
  }

  public Flux<Reward> getAndApplyRewards(List<Integer> randomIds, ApplyRewardUserData applyRewardUserData) {
    return getRandomRewardsByRandomIds(randomIds)
        .filter(reward -> !PrizeType.EMPTY.equals(reward.getType()))
        .collectList()
        .flatMap(reward -> apply(reward, applyRewardUserData).collectList())
        .flatMapMany(Flux::fromIterable);
  }

  /**
   * Получить сгенерированные награды и применить соответствующим образом пользователю для типа лотерея
   *
   * @param userSession бонусная сессия пользователя
   * @param bet         данные ставки пользователя
   * @param gameType    тип игры
   * @param mobile      номер телефона пользователя
   * @param price       базовая цена билета
   * @return список наград примененных пользователю
   */
  public Mono<List<Reward>> getAndApplyRewards(UserSession userSession,
                                               Map<String, Object> bet,
                                               BonusGameType gameType,
                                               String mobile,
                                               Integer price) {
    if (Objects.isNull(price) || price.equals(BetDataService.DEFAULT_PRICE)) {
      return Mono.just(Collections.emptyList());
    }
    var randomRewardIds = prizeService
        .getRandomIds(gameType, BetDataService.getIntFromJsonString(bet.get(BetDataService.MODE)), price, false);
    return getRandomRewardsByRandomIds(randomRewardIds)
        .filter(reward -> !PrizeType.EMPTY.equals(reward.getType()))
        .collectList()
        .flatMap(rewards -> apply(rewards, ApplyRewardUserData.builder()
            .userId(userSession.getUserId())
            .mobile(mobile)
            .bonusGameSessionUUID(userSession.getUuid())
            .refillBonusDescription(userSession.getGameType())
            .refillBonusReason(BonusRefillReason.ZBONUS_SHOP.name())
            .build())
            .collectList());
  }

  public Flux<Reward> apply(List<Reward> rewards, ApplyRewardUserData applyRewardUserData) {
    return Flux.fromIterable(rewards)
        .flatMap(reward ->
            switch (reward.getType()) {
              case RANDOM_PUZZLE_ITEM_NOT_USED -> applyPuzzleItemReward(reward, applyRewardUserData.getUserId());
              case BONUS ->
                  applyBonusReward(reward, applyRewardUserData.getBonusGameSessionUUID(), applyRewardUserData.getMobile(),
                      applyRewardUserData.getRefillBonusReason(), applyRewardUserData.getRefillBonusDescription(),
                      applyRewardUserData.getRefillBonusLifeTime(), applyRewardUserData.getRefillBonusLifeTimeDays());
              case CURRENCY -> applyCurrencyReward(
                  reward,
                  applyRewardUserData.getUserId(),
                  applyRewardUserData.getRefillBonusDescription()
              );
              case PROFILE_ELEMENTS -> applyProfileElementsReward(reward, applyRewardUserData.getUserId());
              case TOYS, TOPPER, GIFT, GARLAND, ENVIRONMENT, PHYSICAL,
                   TREE, STRUCTURE, FLOWER, DECOR, INFRASTRUCTURE, ACCOMODATION, RECREATION, FUN,
                   BOOTS, SHORTS, TSHIRT, GAITERS, BALL -> Flux.just(reward);
              case ATTEMPT_AT_THE_PRIZE_GAME -> applyAttemptAtThePrizeGame(reward, applyRewardUserData.getUserId(),
                  applyRewardUserData.getBonusGameSessionUUID());
              default -> {
                  log.warn("not apply prize {}", reward.getType());
                  yield Flux.empty();
              }
            });
  }

  @Transactional(transactionManager = "connectionFactoryTransactionManager")
  public Flux<Reward> applyPuzzleItemReward(Reward reward, Long userId) {
    return Objects.isNull(reward.getValue()) || reward.getValue() <= 0 ?
        Flux.empty() :
        Flux.range(0, reward.getValue())
            .flatMap(next -> puzzleItemService.getRandomNotUsed(userId, reward.getId())
                .flatMap(randomPuzzleItemNotUsed -> {
                  reward.setUrl(randomPuzzleItemNotUsed.getUrl());
                  reward.setId(randomPuzzleItemNotUsed.getId());
                  reward.setDescription(String.format(PrizeType.RANDOM_PUZZLE_ITEM_NOT_USED.getDescription(),
                      randomPuzzleItemNotUsed.getPosition(), randomPuzzleItemNotUsed.getPuzzleName()));
                  return userPuzzleItemService.saveUserPuzzleItem(userId, randomPuzzleItemNotUsed.getPuzzleId(),
                          randomPuzzleItemNotUsed.getId())
                      .flatMap(result -> puzzleCollectionService
                          .checkCollectedByUserIdAndUpdateCollectedIfNeeded(userId, randomPuzzleItemNotUsed.getPuzzleId()))
                      .map(result -> reward);
                })
                .doOnError(error -> log.error("Error when applyPuzzleItemReward: {}", error.getMessage()))
                .onErrorResume(ex -> Mono.empty())
            );
  }

  private Flux<Reward> applyBonusReward(Reward reward, UUID sessionUUID, String mobile,
                                        String refillReason, String refillBonusDescription,
                                        Integer bonusLifeTimeMonths, Integer bonusLifeTimeDays) {
    return Mono.fromCallable(() -> purchaseService.refillBonus(reward.getValue(), refillReason,
            mobile, sessionUUID, bonusLifeTimeMonths, bonusLifeTimeDays, refillBonusDescription))
        .filter(refillSuccess -> refillSuccess)
        .flatMap(result -> Mono.just(reward))
        .switchIfEmpty(Mono.defer(() -> {
          log.error("Error when refill bonus: {} {}", reward, mobile);
          return Mono.empty();
        }))
        .onErrorResume(ex -> {
          log.error("Error when applyBonusReward: {}", ex.getMessage());
          return Mono.empty();
        })
        .flux();
  }

  private Flux<Reward> applyCurrencyReward(Reward reward, Long userId, String description) {
    return promoWalletGrpcClient.getPromotionOperation().changeWalletBalanceWithDescription(userId, reward.getPrizeSubType().name(),
            reward.getValue(), description)
        .filter(refillSuccess -> Boolean.TRUE)
        .flatMap(result -> Mono.just(reward))
        .doOnError(error -> log.error("Error when applyCurrency: {}", error.getMessage()))
        .onErrorResume(ex -> Mono.empty())
        .flux();
  }

  /**
   * Выдача награды в виде аватора или рамки пользователю
   *
   * @param reward Награда
   * @param userId ID пользователя
   * @return Выданная награда или empty(), если что-то пошло не так.
   */
  private Mono<Reward> applyProfileElementsReward(Reward reward, long userId) {
    if (reward.getPrizeSubType() == null) {
      log.warn("Error in applyProfileElementsReward(). " +
          "prizeSubType is null, expected AVATARS or FRAMES, userId: {}, reward: {}", userId, reward);
      return Mono.empty();
    }
    var elementType = switch (reward.getPrizeSubType()) {
      case AVATARS -> AVATAR_TYPE;
      case FRAMES -> FRAME_TYPE;
      default -> null;
    };
    if (elementType == null) {
      log.warn("Error in applyProfileElementsReward(). " +
          "Cannot determine element type by prizeSubType, expected AVATARS or FRAMES, userId: {}, reward: {}", userId, reward);
      return Mono.empty();
    }
    int elementId;
    try {
      elementId = reward.getId().intValue();
    } catch (NumberFormatException e) {
      log.warn("Error in applyProfileElementsReward(). " +
          "Expected number in id, userId: {}, reward: {}", userId, reward);
      return Mono.empty();
    }
    return userProfileService.addUserElement(userId, elementType, elementId)
        .thenReturn(reward)
        .flatMap(r -> userProfileService.getElementUrl(elementId).map(link -> {
          r.setUrl(link);
          return r;
        }))
        .doOnError(ex -> {
          if (ex instanceof IllegalProfileElementException) {
            log.warn("Can't add element to user: {}", ex.getMessage());
          } else {
            log.error("Error in userProfileService.addUserElement()", ex);
          }
        })
        .onErrorResume(ex -> Mono.empty());
  }

  private Mono<Reward> applyAttemptAtThePrizeGame(Reward reward, Long userId, UUID sessionUUID) {
    return purchaseService.refillAttempt(userId, reward.getValue(), sessionUUID)
        .map(result -> reward)
        .doOnError(error -> log.error("Error when applyAttemptAtThePrizeGame: {}", error.getMessage()));
  }

  private Range calculateRange(Integer maxInterval, List<Range> rules) {
    var randomNumber = getRandomNumber(maxInterval);
    return rules.stream()
        .map(rule -> checkIfNumberIsWithinInterval(randomNumber, rule.getStart(), rule.getEnd()) ? rule : null)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }

  private int getRandomNumber(Integer maxInterval) {
    return random.nextInt(maxInterval) + RANDOM_MIN;
  }

  private boolean checkIfNumberIsWithinInterval(int randomNumber, int begin, int end) {
    return randomNumber >= begin && randomNumber <= end;
  }

  private Mono<Reward> buildReward(Range range) {
    return switch (range.getPrizeType()) {
      case RANDOM_PUZZLE_ITEM_NOT_USED -> Mono.just(
          Reward.builder()
              //идентификатор редкости для выборки пазла
              .id(range.getPrizeId())
              .type(PrizeType.RANDOM_PUZZLE_ITEM_NOT_USED)
              .value(range.getCount())
              .build()
      );
      case BONUS -> Mono.just(
          Reward.builder()
              .value(range.getCount())
              .type(PrizeType.BONUS)
              .build()
      );
      case PROFILE_ELEMENTS -> buildProfileElement(range);
      case CURRENCY, TOYS, GARLAND, TOPPER, GIFT, ENVIRONMENT, PHYSICAL,
           TREE, STRUCTURE, FLOWER, DECOR, WIN_CATEGORIES_RAPIDO, WIN_CATEGORIES_ROCKETBINGO,
           ACCOMODATION, RECREATION, INFRASTRUCTURE, FUN, BALL, BOOTS, SHORTS, TSHIRT, GAITERS -> Mono.just(
          Reward.builder()
              .prizeSubType(PrizeSubType.valueOf(range.getPrizeSubType()))
              .value(range.getCount())
              .type(range.getPrizeType())
              .id(Objects.nonNull(range.getPrizeId()) ? range.getPrizeId() : null)
              .build()
      );
      case ATTEMPT_AT_THE_PRIZE_GAME -> Mono.just(
          Reward.builder()
              .value(range.getCount())
              .type(PrizeType.ATTEMPT_AT_THE_PRIZE_GAME)
              .build()
      );
      case EMPTY -> Mono.just(Reward.builder().value(0).type(PrizeType.EMPTY).build());
    };
  }

  private Mono<Reward> buildProfileElement(Range range) {
    return Objects.isNull(range.getPrizeId()) ? Mono.just(Reward.builder()
        .prizeSubType(PrizeSubType.valueOf(range.getPrizeSubType()))
        .value(range.getCount())
        .type(range.getPrizeType())
        .build()) : Mono.just(Reward.builder()
            .prizeSubType(PrizeSubType.valueOf(range.getPrizeSubType()))
            .value(range.getCount())
            .type(range.getPrizeType())
            .id(range.getPrizeId())
            .build())
        .flatMap(reward -> userProfileService.getElementUrl(range.getPrizeId().intValue()).map(link -> {
          reward.setUrl(link);
          return reward;
        }));
  }
}
