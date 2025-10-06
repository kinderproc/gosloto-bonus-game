package org.orglot.gosloto.bonus.games.service.strategy.nards;

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
import org.orglot.gosloto.bonus.games.service.strategy.model.BonusGameBuyDto;
import org.orglot.gosloto.bonus.games.service.strategy.model.BonusGameBuyExecuteData;
import org.orglot.gosloto.bonus.games.service.strategy.nards.enums.NardsCombinationPrizeSubType;
import org.orglot.gosloto.bonus.games.service.strategy.nards.mapper.NardsWinCombinationMapper;
import org.orglot.gosloto.bonus.games.service.strategy.nards.service.NardsCalcWinCombinationService;
import org.orglot.gosloto.components.log.LogRepository;
import org.orglot.gosloto.components.log.message.NewBonusGameSessionMessage;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NardsBonusGameBuyExecuteStrategy implements BonusGameBuyExecuteStrategy {

  private final RewardService rewardService;
  private final UserSessionService userSessionService;
  private final BonusGameSettingsService bonusGameSettingsService;
  private final NardsCalcWinCombinationService nardsCalcWinCombinationService;
  private final LogRepository logRepository;

  @Override
  public Mono<UUID> execute(BonusGameBuyExecuteData data) {
    List<Integer> rewardsIds = data.getRewardsIds();
    log.info("Start execute calulate NARDS game data for rewards: {}", rewardsIds);
    return Flux.fromIterable(data.getCoupons())
        .flatMap(couponDto -> Flux.fromIterable(rewardsIds)
                .flatMap(rewardService::getRandomRewardsByRandomIdForNards)
            .flatMap(firstReward -> Mono.justOrEmpty(firstReward)
                .filterWhen(reward -> Mono.justOrEmpty(reward.getPrizeSubType())
                    .map(prizeSubType -> PrizeType.WIN_CATEGORIES_RAPIDO == prizeSubType.getType())
                    .defaultIfEmpty(false)
                )
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Not valid prizeSubType for NARDS")))
                .mapNotNull(Reward::getId)
                .filter(id -> Integer.MAX_VALUE >= id)
                .map(Long::intValue)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Reward id must not be null or long for NARDS")))
                .flatMap(rewardService::getRandomRewardByRandomId)
                .zipWith(Mono.just(firstReward.getPrizeSubType()))
            )
            .collectMultimap(tuple -> NardsCombinationPrizeSubType.isCombination(tuple.getT2().name()))
            .flatMap(isCombinationMap -> nardsCalcWinCombinationService
                .calculate(NardsWinCombinationMapper.MAPPER.toNardsWinCalculateData(
                        isCombinationMap.get(true).stream().findFirst().orElse(null),
                        isCombinationMap.get(false),
                        couponDto,
                        data.getMultiplier()
                    )
                )
            )
            .map(result -> NardsWinCombinationMapper.MAPPER.toBetCoupon(couponDto, result))
        )
        .collectList()
        .flatMap(coupons -> {
          BonusGameBuyDto gameBuy = data.getBonusGameBuy();
          Integer price = data.getPrice();
          var bet = BetDataService.toLotteryJsonBetData(
              gameBuy.getMode(),
              price,
              gameBuy.getModePrice(),
              bonusGameSettingsService.getPriceIndex(BonusGameType.valueOf(gameBuy.getGameType()), price),
              data.getMultiplier(),
              coupons
          );
          return Flux.fromIterable(coupons)
              .map(BetCoupon::getPrize)
              .collectList()
              .flatMap(prizeList -> userSessionService.createUserSession(data.getNewUUID(), gameBuy.getGameType(), gameBuy.getUserId(),
                  bet, data.getSapTransactionId(), prizeList, data.getPlatform(), data.getOs())
              ).doOnNext(uuid ->
                  logRepository.log(
                      new NewBonusGameSessionMessage(
                          gameBuy.getGameType(),
                          price,
                          coupons.stream().map(BetCoupon::getPrize).mapToInt(Integer::intValue).sum(),
                          NewBonusGameSessionMessage.SessionState.CREATE)
                  )
              );
        });
  }

  @Override
  public BonusGameType getGameType() {
    return BonusGameType.NARDS;
  }
}
