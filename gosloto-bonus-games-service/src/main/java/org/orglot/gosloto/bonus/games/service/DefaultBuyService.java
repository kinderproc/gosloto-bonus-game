package org.orglot.gosloto.bonus.games.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.orglot.bonus.games.model.BonusGameType;
import org.orglot.bonus.games.model.request.BonusGameBuy;
import org.orglot.bonus.games.model.response.BonusGameBuyStatus;
import org.orglot.bonus.games.model.response.PurchaseStatus;
import org.orglot.bonus.games.model.response.Reward;
import org.orglot.gosloto.bonus.client.operation.consumepoints.model.response.ConsumePointsResponse;
import org.orglot.gosloto.bonus.games.model.Mode;
import org.orglot.gosloto.bonus.games.model.SessionState;
import org.orglot.gosloto.bonus.games.service.strategy.BonusGameBuyExecuteStrategy;
import org.orglot.gosloto.bonus.games.service.strategy.mapper.BonusGameBuyExecuteDataMapper;
import org.orglot.gosloto.components.log.LogRepository;
import org.orglot.gosloto.components.log.message.NewBonusGameSessionMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

@Slf4j
@Service
public class DefaultBuyService {

  private final LogRepository logRepository;
  private final PurchaseService purchaseService;
  private final RewardService rewardService;
  private final PrizeService prizeService;
  private final BonusGameSettingsService bonusGameSettingsService;
  private final Map<BonusGameType, BonusGameBuyExecuteStrategy> bonusGameTypeBonusGameBuyExecuteStrategyMap;

  protected final UserSessionService userSessionService;

  public DefaultBuyService(UserSessionService userSessionService,
                           LogRepository logRepository,
                           PurchaseService purchaseService,
                           RewardService rewardService,
                           PrizeService prizeService,
                           BonusGameSettingsService bonusGameSettingsService,
                           List<BonusGameBuyExecuteStrategy> bonusGameTypeBonusGameBuyExecuteStrategyList) {
    this.userSessionService = userSessionService;
    this.logRepository = logRepository;
    this.purchaseService = purchaseService;
    this.rewardService = rewardService;
    this.prizeService = prizeService;
    this.bonusGameSettingsService = bonusGameSettingsService;
    this.bonusGameTypeBonusGameBuyExecuteStrategyMap = bonusGameTypeBonusGameBuyExecuteStrategyList
        .stream()
        .collect(Collectors.toMap(BonusGameBuyExecuteStrategy::getGameType, Function.identity()));
  }

  public Mono<BonusGameBuyStatus> buyGame(BonusGameBuy gameBuy, Integer calculatedPrice, boolean needConsume) {
    return Mono.defer(() -> {
          var validate = validateGameBuy(gameBuy);
          if (StringUtils.isNotBlank(validate)) {
            return Mono.just(BonusGameBuyStatus.error(validate));
          }
          return userSessionService.findByUserIdAndGameTypeAndSessionState(gameBuy.getUserId(),
                  BonusGameType.valueOf(gameBuy.getGameType()), List.of(SessionState.START, SessionState.IN_PROGRESS))
              .map(oldSession -> new BonusGameBuyStatus(PurchaseStatus.SUCCESS, oldSession.getUuid()))
              .switchIfEmpty(Mono.defer(() -> {
                if (needConsume) {
                  return consumePointsAndCreateNewSession(gameBuy, calculatedPrice, UUID.randomUUID());
                }
                return createNewSession(null, gameBuy, calculatedPrice, UUID.randomUUID());
              }))
              .doOnError(error -> log.error("Error when buyGame: {}", error.getMessage()));
        })
        .doOnRequest(request -> log.debug("buyGame {}", gameBuy));
  }

  private Mono<BonusGameBuyStatus> consumePointsAndCreateNewSession(BonusGameBuy gameBuy,
                                                                    @NotNull Integer price,
                                                                    UUID newUUID) {
    return consumePoints(gameBuy, price, newUUID)
        .flatMap(consumePointsResponse -> createNewSession(consumePointsResponse, gameBuy, price, newUUID))
        .defaultIfEmpty(BonusGameBuyStatus.error(null))
        .doOnError(error -> log.error("Error when consumePointsAndCreateNewSession: {}", error.getMessage()));
  }

  private Mono<ConsumePointsResponse> consumePoints(BonusGameBuy gameBuy, Integer price, UUID newUUID) {
    return purchaseService.consumePoints(gameBuy.getMobile(), BonusGameType.valueOf(gameBuy.getGameType()), price, newUUID);
  }

  private Mono<BonusGameBuyStatus> createNewSession(ConsumePointsResponse consumePointsResponse,
                                                    BonusGameBuy gameBuy,
                                                    Integer price,
                                                    UUID newUUID) {
    var randomIds = prizeService.getRandomIds(
        BonusGameType.valueOf(gameBuy.getGameType()),
        gameBuy.getMode(),
        gameBuy.getPrice(),
        true
    );
    var sapTransactionId = Objects.nonNull(consumePointsResponse) ? consumePointsResponse.getSapTransactionId() : null;
    var platformOs = resolvePlatformOs(gameBuy.getDevicePlatform());
    var platform = platformOs.getLeft();
    var os = platformOs.getRight();
    if (Objects.isNull(consumePointsResponse) ||
        Objects.isNull(consumePointsResponse.getCode()) && Objects.isNull(consumePointsResponse.getMessage())) {
      var bonusGameType = BonusGameType.valueOf(gameBuy.getGameType());
      var bonusGameBuyExecuteData = Optional.ofNullable(gameBuy.getGameData())
          .map(gameData -> BonusGameBuyExecuteDataMapper.MAPPER.toBonusGameBuyExecuteData(
              gameBuy,
              gameData,
              newUUID,
              sapTransactionId,
              platform,
              os,
              randomIds,
              price
            )
          ).orElse(null);
      return bonusGameTypeBonusGameBuyExecuteStrategyMap.getOrDefault(
              bonusGameType,
              data -> {
                //TODO вынести в общую схему логики BetData
                var bet = BetDataService.toJsonBetData(gameBuy.getMode(), price,
                    bonusGameSettingsService.getPriceIndex(bonusGameType, price));
                return rewardService.getRandomRewardsByRandomIds(randomIds)
                    .collectList()
                    .defaultIfEmpty(List.of())
                    .flatMap(rewards -> userSessionService
                        .createUserSession(
                            newUUID,
                            gameBuy.getGameType(),
                            gameBuy.getUserId(),
                            bet,
                            sapTransactionId,
                            rewards.stream().map(Reward::getValue).toList(),
                            platform,
                            os
                        )
                        .doOnNext(uuid -> logRepository
                            .log(
                                new NewBonusGameSessionMessage(
                                    gameBuy.getGameType(),
                                    price,
                                    rewards.stream().map(Reward::getValue).mapToInt(Integer::intValue).sum(),
                                    NewBonusGameSessionMessage.SessionState.CREATE)
                            )
                        )
                    );
              }
          )
          .execute(bonusGameBuyExecuteData)
          .map(uuid -> new BonusGameBuyStatus(PurchaseStatus.SUCCESS, uuid))
          .defaultIfEmpty(BonusGameBuyStatus/**/.error("Error when create user session"));
    } else {
      return Mono.just(BonusGameBuyStatus.error(consumePointsResponse.getMessage()));
    }
  }

  private String validateGameBuy(BonusGameBuy gameBuy) {
    if (Objects.isNull(gameBuy.getGameType())) {
      return "game type required";
    }
    if (Objects.isNull(gameBuy.getMobile())) {
      return "mobile required";
    }
    if (Objects.isNull(gameBuy.getMode()) || Objects.isNull(gameBuy.getPrice())) {
      return "mode required";
    }
    var gameModes = bonusGameSettingsService.getModes(BonusGameType.valueOf(gameBuy.getGameType())).stream()
        .map(Mode::getNumber).toList();
    var gamePrice = bonusGameSettingsService.getModes(BonusGameType.valueOf(gameBuy.getGameType())).stream()
        .filter(mode -> mode.getNumber().equals(gameBuy.getMode()))
        .map(Mode::getPrice).toList();
    if (!gameModes.contains(gameBuy.getMode())) {
      return "mode not found";
    }
    if (!gamePrice.contains(gameBuy.getPrice())) {
      return "price not found";
    }
    if (Objects.isNull(gameBuy.getUserId())) {
      return "user required";
    }
    return null;
  }

  private Pair<String, String> resolvePlatformOs(String platformFromHeader) {
    String platform = StringUtils.isBlank(platformFromHeader) ? "DESKTOP" : platformFromHeader;
    return Pair.of(platform, null);
  }
}
