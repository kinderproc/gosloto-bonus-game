package org.orglot.gosloto.bonus.games.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.orglot.bonus.games.model.response.LotteryDrawInfo;
import org.orglot.bonus.games.model.response.LotteryTicket;
import org.orglot.bonus.games.model.response.LotteryTicketData;
import org.orglot.bonus.games.model.response.LotteryTicketDataCombination;
import org.orglot.bonus.games.model.response.Reward;
import org.orglot.gosloto.bonus.games.service.strategy.model.BetCoupon;
import org.orglot.gosloto.bonus.games.service.strategy.model.DrawInfo;

import java.util.List;
import java.util.Optional;

@Mapper
public interface LotteryMapper {

  LotteryMapper MAPPER = Mappers.getMapper(LotteryMapper.class);

  @Mapping(target = "rewards", source = "rewards")
  @Mapping(target = "totalPrize", source = "betCoupon.prize")
  @Mapping(target = "data", expression = "java(toLotteryTicketData(betCoupon, multiplayer))")
  @Mapping(target = "drawInfo", source = "betCoupon.drawInfo")
  LotteryTicket toLotteryTicket(List<Reward> rewards,
                                BetCoupon betCoupon,
                                Integer multiplayer);

  @Mapping(target = "parity", source = "drawInfo.structured.parity")
  @Mapping(target = "played", source = "drawInfo.structured.played")
  LotteryDrawInfo toLotteryDrawInfo(DrawInfo drawInfo);

  @Mapping(target = "combinations", source = "betCoupon")
  @Mapping(target = "multiplier", source = "multiplayer")
  LotteryTicketData toLotteryTicketData(BetCoupon betCoupon, Integer multiplayer);

  default LotteryTicketDataCombination toLotteryTicketDataCombination(BetCoupon betCoupon) {
    return Optional.ofNullable(betCoupon)
        .filter(BetCoupon::isValid)
        .map(coupon -> new LotteryTicketDataCombination()
            .setNumbers(coupon.getCombination())
            .setExtraNumbers(coupon.getExtraCombination())
            .setParity(coupon.getParity())
        )
        .orElse(null);
  }
}
