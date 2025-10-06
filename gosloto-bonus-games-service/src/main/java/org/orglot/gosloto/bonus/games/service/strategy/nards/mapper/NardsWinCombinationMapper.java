package org.orglot.gosloto.bonus.games.service.strategy.nards.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.orglot.bonus.games.model.PrizeSubType;
import org.orglot.bonus.games.model.response.Reward;
import org.orglot.gosloto.bonus.games.service.strategy.model.BetCoupon;
import org.orglot.gosloto.bonus.games.service.strategy.model.BonusGameBuyCouponDto;
import org.orglot.gosloto.bonus.games.service.strategy.model.DrawInfo;
import org.orglot.gosloto.bonus.games.service.strategy.nards.enums.NardsCombinationPrizeSubType;
import org.orglot.gosloto.bonus.games.service.strategy.nards.enums.NardsParityPrizeSubType;
import org.orglot.gosloto.bonus.games.service.strategy.nards.model.NardsRewardCombinationData;
import org.orglot.gosloto.bonus.games.service.strategy.nards.model.NardsRewardParityData;
import org.orglot.gosloto.bonus.games.service.strategy.nards.model.NardsWinCalculateData;
import org.orglot.gosloto.bonus.games.service.strategy.nards.model.NardsWinCalculateResult;
import org.orglot.gosloto.bonus.games.service.strategy.nards.model.NardsWinCombinationCalculateData;
import org.orglot.gosloto.bonus.games.service.strategy.nards.model.ParityType;
import reactor.util.function.Tuple2;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper
public interface NardsWinCombinationMapper {

  NardsWinCombinationMapper MAPPER = Mappers.getMapper(NardsWinCombinationMapper.class);

  @Mapping(target = "combinationDigitCount", source = "combinationData.prizeSubType.combinationDigitCount")
  @Mapping(target = "extraCombinationDigitCount", source = "combinationData.prizeSubType.extraCombinationDigitCount")
  NardsWinCombinationCalculateData toNardsWinCombinationCalculateData(NardsWinCalculateData data);

  @Mapping(target = "combinationData", source = "combinationReward")
  @Mapping(target = "parityData", source = "parityRewards")
  @Mapping(target = "combination", source = "coupon.combination")
  @Mapping(target = "extraCombination", source = "coupon.extraCombination")
  @Mapping(target = "multiplier", source = "multiplier")
  @Mapping(target = "parity", source = "coupon.parity")
  NardsWinCalculateData toNardsWinCalculateData(Tuple2<Reward, PrizeSubType> combinationReward,
                                                Collection<Tuple2<Reward, PrizeSubType>> parityRewards,
                                                BonusGameBuyCouponDto coupon,
                                                int multiplier);

  default NardsRewardCombinationData toNardsRewardCombinationData(Tuple2<Reward, PrizeSubType> tuple) {
    return Optional.ofNullable(tuple)
        .map(t -> {
              Reward reward = t.getT1();
              return new NardsRewardCombinationData()
                  .setType(reward.getType())
                  .setPrizeSubType(toNardsCombinationPrizeSubType(t.getT2()))
                  .setPrize(reward.getValue());
            }
        ).orElse(null);
  }

  default Map<ParityType, NardsRewardParityData> toNardsParityData(Collection<Tuple2<Reward, PrizeSubType>> rewards) {
    return Optional.ofNullable(rewards)
        .map(tuples -> tuples
            .stream()
            .map(t -> {
              Reward reward = t.getT1();
              return new NardsRewardParityData()
                  .setType(reward.getType())
                  .setPrizeSubType(toNardsParityPrizeSubType(t.getT2()))
                  .setPrize(reward.getValue());
                }

            )
            .collect(Collectors.toMap(parityData -> parityData.getPrizeSubType().getParityType(), Function.identity()))
        ).orElse(null);
  }

  @Mapping(target = "combination", source = "couponDto.combination")
  @Mapping(target = "extraCombination", source = "couponDto.extraCombination")
  @Mapping(target = "parity", source = "couponDto.parity")
  @Mapping(target = "drawInfo", source = "nardsWinCalculateResult")
  BetCoupon toBetCoupon(BonusGameBuyCouponDto couponDto, NardsWinCalculateResult nardsWinCalculateResult);

  @Mapping(target = "structured.parity", source = "nardsWinCalculateResult.parity")
  @Mapping(target = "structured.played", source = "nardsWinCalculateResult.winCombination")
  DrawInfo toDrawInfo(NardsWinCalculateResult nardsWinCalculateResult);

  default NardsCombinationPrizeSubType toNardsCombinationPrizeSubType(PrizeSubType prizeSubType) {
    return Optional.ofNullable(prizeSubType)
        .map(PrizeSubType::name)
        .map(NardsCombinationPrizeSubType::byValue)
        .orElse(null);
  }

  default NardsParityPrizeSubType toNardsParityPrizeSubType(PrizeSubType prizeSubType) {
    return Optional.ofNullable(prizeSubType)
        .map(PrizeSubType::name)
        .map(NardsParityPrizeSubType::byValue)
        .orElse(null);
  }
}
