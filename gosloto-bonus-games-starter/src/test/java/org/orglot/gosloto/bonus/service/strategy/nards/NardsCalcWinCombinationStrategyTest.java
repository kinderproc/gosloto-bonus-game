package org.orglot.gosloto.bonus.service.strategy.nards;

import org.junit.jupiter.api.RepeatedTest;
import org.orglot.bonus.games.model.response.PrizeType;
import org.orglot.gosloto.bonus.games.service.strategy.nards.NardsCalculateWinCombinationProcessor;
import org.orglot.gosloto.bonus.games.service.strategy.nards.enums.NardsCombinationPrizeSubType;
import org.orglot.gosloto.bonus.games.service.strategy.nards.enums.NardsParityPrizeSubType;
import org.orglot.gosloto.bonus.games.service.strategy.nards.model.NardsRewardCombinationData;
import org.orglot.gosloto.bonus.games.service.strategy.nards.model.NardsRewardParityData;
import org.orglot.gosloto.bonus.games.service.strategy.nards.model.NardsWinCalculateData;
import org.orglot.gosloto.bonus.games.service.strategy.nards.model.ParityType;
import org.orglot.gosloto.bonus.games.service.strategy.nards.service.NardsCalcWinCombinationService;
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NardsCalcWinCombinationStrategyTest {

  private final NardsCalcWinCombinationService strategy =
      new NardsCalcWinCombinationService(
          new NardsCalculateWinCombinationProcessor()
      );

  @RepeatedTest(50)
  void shouldCalculateResultWhenSetAllFieldsAndAllPrize() {
    NardsWinCalculateData nardsWinCalculateData = new NardsWinCalculateData()
        .setCombination(List.of(1, 2, 7, 8, 12, 13, 14, 15))
        .setExtraCombination(List.of(4))
        .setMultiplier(2)
        .setCombinationData(
            new NardsRewardCombinationData()
                .setType(PrizeType.BONUS)
                .setPrizeSubType(NardsCombinationPrizeSubType.NARDS_5_0)
                .setPrize(5)
        )
        .setParityData(
            Map.of(
                ParityType.ODD,
                new NardsRewardParityData()
                    .setType(PrizeType.BONUS)
                    .setPrizeSubType(NardsParityPrizeSubType.NARDS_ODD)
                    .setPrize(5),
                ParityType.EVEN,
                new NardsRewardParityData()
                    .setType(PrizeType.BONUS)
                    .setPrizeSubType(NardsParityPrizeSubType.NARDS_EVEN)
                    .setPrize(6),
                ParityType.FIFTY_FIFTY,
                new NardsRewardParityData()
                    .setType(PrizeType.BONUS)
                    .setPrizeSubType(NardsParityPrizeSubType.NARDS_EQUAL)
                    .setPrize(10)
            )
        );

    strategy.calculate(nardsWinCalculateData)
        .as(StepVerifier::create)
        .assertNext(result -> {
          int parityPrize = 0;
          var parityData = nardsWinCalculateData.getParityData();
          if (result.getParity() == nardsWinCalculateData.getParity()) {
            parityPrize = parityData.get(nardsWinCalculateData.getParity()).getPrize();
          }
          var combinationData = nardsWinCalculateData.getCombinationData();
          var prizeSubType = combinationData.getPrizeSubType();
          int expectedPrize = (parityPrize + combinationData.getPrize()) *
              nardsWinCalculateData.getMultiplier();
          assertEquals(expectedPrize, result.getPrize());
          Set<Integer> oldCombinationSet = new HashSet<>(nardsWinCalculateData.getCombination());
          Set<Integer> oldExtraCombinationSet = new HashSet<>(nardsWinCalculateData.getExtraCombination());
          int expectedNewCombinationDigits = 8 - prizeSubType.getCombinationDigitCount();
          int expectedNewExtraCombinationDigits = 1 - prizeSubType.getExtraCombinationDigitCount();
          assertEquals(
              expectedNewCombinationDigits,
              result.getWinCombination().stream().limit(8).filter(d -> !oldCombinationSet.contains(d)).count()
          );
          assertEquals(
              expectedNewExtraCombinationDigits,
              result.getWinCombination().stream().skip(8).filter(d -> !oldExtraCombinationSet.contains(d)).count()
          );
        })
        .verifyComplete();
  }

  @RepeatedTest(50)
  void shouldCalculateResultWhenSetOnlyCombinationPrize() {
    NardsWinCalculateData nardsWinCalculateData = new NardsWinCalculateData()
        .setCombination(List.of(5, 12, 7, 10, 3, 4, 9, 8))
        .setExtraCombination(List.of(1))
        .setMultiplier(3)
        .setParityData(
            Map.of(
                ParityType.ODD,
                new NardsRewardParityData()
                    .setType(PrizeType.BONUS)
                    .setPrizeSubType(NardsParityPrizeSubType.NARDS_ODD)
                    .setPrize(5),
                ParityType.EVEN,
                new NardsRewardParityData()
                    .setType(PrizeType.BONUS)
                    .setPrizeSubType(NardsParityPrizeSubType.NARDS_EVEN)
                    .setPrize(6),
                ParityType.FIFTY_FIFTY,
                new NardsRewardParityData()
                    .setType(PrizeType.BONUS)
                    .setPrizeSubType(NardsParityPrizeSubType.NARDS_EQUAL)
                    .setPrize(10)
            )
        )
        .setCombinationData(
            new NardsRewardCombinationData()
                .setType(PrizeType.BONUS)
                .setPrizeSubType(NardsCombinationPrizeSubType.NARDS_0_0)
                .setPrize(5)
        );

    strategy.calculate(nardsWinCalculateData)
        .as(StepVerifier::create)
        .assertNext(result -> {
          assertEquals(15, result.getPrize());
          Set<Integer> oldCombinationSet = new HashSet<>(nardsWinCalculateData.getCombination());
          Set<Integer> oldExtraCombinationSet = new HashSet<>(nardsWinCalculateData.getExtraCombination());
          int extraCombinationDigitCount = nardsWinCalculateData
              .getCombinationData()
              .getPrizeSubType()
              .getExtraCombinationDigitCount();
          int expectedNewExtraCombinationDigits = 1 - extraCombinationDigitCount;
          long expectedNewCombinationDigits = result
              .getWinCombination()
              .stream()
              .limit(8)
              .filter(d -> !oldCombinationSet.contains(d))
              .count();
          assertTrue(5 <= expectedNewCombinationDigits);
          assertEquals(
              expectedNewExtraCombinationDigits,
              result.getWinCombination().stream().skip(8).filter(d -> !oldExtraCombinationSet.contains(d)).count()
          );
        })
        .verifyComplete();
  }

  @RepeatedTest(50)
  void shouldCalculateResultWhenSetOnlyParityPrize() {
    NardsWinCalculateData nardsWinCalculateData = new NardsWinCalculateData()
        .setMultiplier(3)
        .setParity(ParityType.FIFTY_FIFTY)
        .setParityData(
            Map.of(
                ParityType.ODD,
                new NardsRewardParityData()
                    .setType(PrizeType.BONUS)
                    .setPrizeSubType(NardsParityPrizeSubType.NARDS_ODD)
                    .setPrize(5),
                ParityType.EVEN,
                new NardsRewardParityData()
                    .setType(PrizeType.BONUS)
                    .setPrizeSubType(NardsParityPrizeSubType.NARDS_EVEN)
                    .setPrize(6),
                ParityType.FIFTY_FIFTY,
                new NardsRewardParityData()
                    .setType(PrizeType.BONUS)
                    .setPrizeSubType(NardsParityPrizeSubType.NARDS_EQUAL)
                    .setPrize(10)
            )
        );

    strategy.calculate(nardsWinCalculateData)
        .as(StepVerifier::create)
        .assertNext(result -> {
          int parityPrize = 0;
          var parityData = nardsWinCalculateData.getParityData();
          if (result.getParity() == nardsWinCalculateData.getParity()) {
            parityPrize = parityData.get(nardsWinCalculateData.getParity()).getPrize();
          }
          int expectedPrize = parityPrize * nardsWinCalculateData.getMultiplier();
          assertEquals(expectedPrize, result.getPrize());
          List<Integer> winCombination = result.getWinCombination();
          assertNotNull(winCombination);
          assertEquals(
              8,
              winCombination.stream().limit(8).filter(d -> 20 >= d && 0 < d).count()
          );
          assertEquals(
              1,
              winCombination.stream().skip(8).filter(d -> 4 >= d && 0 < d).count()
          );
        })
        .verifyComplete();
  }
}
