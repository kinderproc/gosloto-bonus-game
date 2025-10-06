package org.orglot.gosloto.bonus.games.service.strategy.rocketbingo.service;

import lombok.RequiredArgsConstructor;
import org.orglot.bonus.games.model.BonusGameType;
import org.orglot.bonus.games.model.response.Reward;
import org.orglot.gosloto.bonus.games.service.strategy.LotteryGameCalculateWinCombinationService;
import org.orglot.gosloto.bonus.games.service.strategy.rocketbingo.RocketbingoCalculateWinCombinationProcessor;
import org.orglot.gosloto.bonus.games.service.strategy.rocketbingo.enums.RocketbingoTypes;
import org.orglot.gosloto.bonus.games.service.strategy.rocketbingo.model.RocketbingoWinCalculateResult;
import org.orglot.gosloto.bonus.games.service.strategy.rocketbingo.model.RocketbingoWinCombinationCalculateData;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Component
@Validated
@RequiredArgsConstructor
@SuppressWarnings("LineLength")
public class RocketbingoCalcWinCombinationService implements LotteryGameCalculateWinCombinationService<RocketbingoWinCombinationCalculateData, RocketbingoWinCalculateResult> {

  private final RocketbingoCalculateWinCombinationProcessor combinationProcessor;

  /**
   * Метод берет полученную случайным образом категорию приза и определяет победил ли пользователь исходя из ее типа, исходя из этого понимаем получит ли пользователь награду
   * в процессоре определяется выигрышная комбинация, а полученная награда умножается на входной множитель
   */
  @Override
  public Mono<RocketbingoWinCalculateResult> calculate(RocketbingoWinCombinationCalculateData data) {
    var type = RocketbingoTypes.valueOf(data.getPrizeSubType().name());
    boolean isWin = type != RocketbingoTypes.WITHOUT;
    List<Integer> winComb = combinationProcessor.calculate(data.getPlayed(), type);

    int combPrize = isWin ? Optional.ofNullable(data.getReward()).map(Reward::getValue).orElse(0) : 0;
    int totalPrize = Optional.ofNullable(data.getMultiplier())
        .map(m -> combPrize * m)
        .orElse(combPrize);
    var result = new RocketbingoWinCalculateResult()
        .setWinCombination(winComb)
        .setPrize(totalPrize);

    return Mono.just(result);
  }

  @Override
  public BonusGameType getGameType() {
    return BonusGameType.BINGOLUKOMORIE;
  }
}
