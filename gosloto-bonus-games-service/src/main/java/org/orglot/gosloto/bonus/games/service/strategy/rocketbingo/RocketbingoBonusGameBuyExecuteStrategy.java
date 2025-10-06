package org.orglot.gosloto.bonus.games.service.strategy.rocketbingo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.BonusGameType;
import org.orglot.bonus.games.model.response.PrizeType;
import org.orglot.bonus.games.model.response.Reward;
import org.orglot.gosloto.bonus.games.service.BetDataService;
import org.orglot.gosloto.bonus.games.service.BonusGameSettingsService;
import org.orglot.gosloto.bonus.games.service.RewardService;
import org.orglot.gosloto.bonus.games.service.UserSessionService;
import org.orglot.gosloto.bonus.games.service.strategy.BonusGameBuyExecuteStrategy;
import org.orglot.gosloto.bonus.games.service.strategy.model.BetCoupon;
import org.orglot.gosloto.bonus.games.service.strategy.model.BonusGameBuyExecuteData;
import org.orglot.gosloto.bonus.games.service.strategy.model.DrawInfo;
import org.orglot.gosloto.bonus.games.service.strategy.model.StructuredData;
import org.orglot.gosloto.bonus.games.service.strategy.rocketbingo.model.RocketbingoWinCombinationCalculateData;
import org.orglot.gosloto.bonus.games.service.strategy.rocketbingo.service.RocketbingoCalcWinCombinationService;
import org.orglot.gosloto.components.log.LogRepository;
import org.orglot.gosloto.components.log.message.NewBonusGameSessionMessage;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RocketbingoBonusGameBuyExecuteStrategy implements BonusGameBuyExecuteStrategy {

  private static final int MAX_REWARDS_COUNT = 2;

  private final RewardService rewardService;
  private final UserSessionService userSessionService;
  private final BonusGameSettingsService bonusGameSettingsService;
  private final RocketbingoCalcWinCombinationService rocketbingoCalcWinCombinationService;
  private final LogRepository logRepository;

  @Override
  public Mono<UUID> execute(BonusGameBuyExecuteData data) {
    List<Integer> rewardsIds = data.getRewardsIds();
    return Flux.fromIterable(data.getCoupons())
        .flatMap(couponDto -> rewardService.getRandomRewardsByRandomIds(rewardsIds)
            .take(1)
            .single()
            .flatMap(firstReward -> Mono.justOrEmpty(firstReward)
                .filterWhen(reward -> Mono.justOrEmpty(reward.getPrizeSubType())
                    .map(prizeSubType -> PrizeType.WIN_CATEGORIES_ROCKETBINGO == prizeSubType.getType())
                    .defaultIfEmpty(false)
                )
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Not valid prizeSubType for ROCKETBINGO")))
                .map(Reward::getId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Reward value must not be null for ROCKETBINGO")))
                .flatMap(realId -> {
                  return rewardService.getRandomRewardByRandomId(realId.intValue())
                      .map(realReward -> Tuples.of(firstReward, realReward));
                })
            )
            .single()
            .flatMap(tuple -> {
              Reward firstReward = tuple.getT1();
              Reward realReward = tuple.getT2();
              var calcData = new RocketbingoWinCombinationCalculateData()
                  .setMultiplier(data.getMultiplier())
                  .setReward(realReward)
                  .setPrizeSubType(firstReward.getPrizeSubType())
                  .setPlayed(couponDto.getCombination());
              return rocketbingoCalcWinCombinationService
                  .calculate(calcData)
                  .map(winResult -> Tuples.of(couponDto, winResult));
            })
        )
        .collectList()
        .flatMap(list -> {
          List<BetCoupon> betCoupons = list.stream().map(tuple -> {
            var coupon = tuple.getT1();
            var winResult = tuple.getT2();
            var drawInfo = new DrawInfo().setStructured(
                new StructuredData().setPlayed(winResult.getWinCombination())
            );
            return new BetCoupon()
                .setCombination(coupon.getCombination())
                .setExtraCombination(Collections.emptyList())
                .setParity(null)
                .setPrize(winResult.getPrize())
                .setDrawInfo(drawInfo);
          }).toList();
          var gameBuy = data.getBonusGameBuy();
          String betData = BetDataService.toLotteryJsonBetData(
              gameBuy.getMode(),
              data.getPrice(),
              data.getPrice(),
              bonusGameSettingsService.getPriceIndex(
                  BonusGameType.valueOf(gameBuy.getGameType()),
                  data.getPrice()
              ),
              data.getMultiplier(),
              betCoupons
          );
          List<Integer> prizeList = betCoupons.stream()
              .map(BetCoupon::getPrize)
              .map(p -> Objects.requireNonNullElse(p, 0))
              .toList();
          return userSessionService
              .createUserSession(
                  data.getNewUUID(),
                  gameBuy.getGameType(),
                  gameBuy.getUserId(),
                  betData,
                  data.getSapTransactionId(),
                  prizeList,
                  data.getPlatform(),
                  data.getOs()
              )
              .doOnNext(uuid -> logRepository.log(
                  new NewBonusGameSessionMessage(
                      gameBuy.getGameType(),
                      data.getPrice(),
                      prizeList.stream().mapToInt(Integer::intValue).sum(),
                      NewBonusGameSessionMessage.SessionState.CREATE
                  )
              ));
        });
  }

  @Override
  public BonusGameType getGameType() {
    return BonusGameType.BINGOLUKOMORIE;
  }
}
