package org.orglot.gosloto.bonus.games.service.strategy.nards.service;

import lombok.RequiredArgsConstructor;
import org.orglot.bonus.games.model.BonusGameType;
import org.orglot.gosloto.bonus.games.service.strategy.LotteryGameCalculateWinCombinationService;
import org.orglot.gosloto.bonus.games.service.strategy.nards.NardsCalculateWinCombinationProcessor;
import org.orglot.gosloto.bonus.games.service.strategy.nards.mapper.NardsWinCombinationMapper;
import org.orglot.gosloto.bonus.games.service.strategy.nards.model.NardsRewardCombinationData;
import org.orglot.gosloto.bonus.games.service.strategy.nards.model.NardsRewardParityData;
import org.orglot.gosloto.bonus.games.service.strategy.nards.model.NardsWinCalculateData;
import org.orglot.gosloto.bonus.games.service.strategy.nards.model.NardsWinCalculateResult;
import org.orglot.gosloto.bonus.games.service.strategy.nards.model.ParityType;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.orglot.bonus.games.model.response.PrizeType.BONUS;
import static org.orglot.gosloto.bonus.games.service.strategy.nards.model.ParityType.UNKNOWN;

@Component
@Validated
@RequiredArgsConstructor
@SuppressWarnings("LineLength")
public class NardsCalcWinCombinationService implements LotteryGameCalculateWinCombinationService<NardsWinCalculateData, NardsWinCalculateResult> {

  private final NardsCalculateWinCombinationProcessor nardsCalculateWinCombinationProcessor;

  @Override
  public Mono<NardsWinCalculateResult> calculate(@Valid @NotNull NardsWinCalculateData data) {
    List<Integer> wibComb = nardsCalculateWinCombinationProcessor.calculate(
        NardsWinCombinationMapper.MAPPER.toNardsWinCombinationCalculateData(data)
    );
    ParityType parity = calculateParity(wibComb.subList(0, wibComb.size() - 1));
    return Mono.zip(
            Mono.justOrEmpty(data.getCombinationData())
                .filter(reward -> BONUS == reward.getType())
                .mapNotNull(NardsRewardCombinationData::getPrize)
                .defaultIfEmpty(0),
            Mono.justOrEmpty(data.getParity())
                .filter(p -> UNKNOWN != p)
                .filter(selectedParity -> selectedParity == parity)
                .mapNotNull(selectedParity -> data.getParityData().get(selectedParity))
                .filter(reward -> BONUS == reward.getType())
                .mapNotNull(NardsRewardParityData::getPrize)
                .defaultIfEmpty(0)
        )
        .map(tuple -> tuple.getT1() + tuple.getT2())
        .flatMap(prize -> Mono.justOrEmpty(data.getMultiplier())
            .map(m -> prize * m)
            .defaultIfEmpty(prize)
        )
        .map(prize -> new NardsWinCalculateResult()
            .setWinCombination(wibComb)
            .setParity(parity)
            .setPrize(prize)
        );
  }

  private ParityType calculateParity(List<Integer> winCombination) {
    Map<Boolean, Long> counts = winCombination.stream()
        .collect(Collectors.partitioningBy(n -> n % 2 == 0, Collectors.counting()));
    long even = counts.getOrDefault(true, 0L);
    long odd = counts.getOrDefault(false, 0L);
    if (even > odd) return ParityType.EVEN;
    if (odd > even) return ParityType.ODD;
    return ParityType.FIFTY_FIFTY;
  }

  @Override
  public BonusGameType getGameType() {
    return BonusGameType.NARDS;
  }
}
