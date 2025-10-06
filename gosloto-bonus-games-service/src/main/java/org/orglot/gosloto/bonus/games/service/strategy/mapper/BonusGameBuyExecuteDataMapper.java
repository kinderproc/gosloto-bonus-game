package org.orglot.gosloto.bonus.games.service.strategy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.orglot.bonus.games.model.request.BonusGameBuy;
import org.orglot.bonus.games.model.request.BonusGameBuyCoupon;
import org.orglot.bonus.games.model.request.BonusGameBuyData;
import org.orglot.gosloto.bonus.games.service.strategy.model.BonusGameBuyCouponDto;
import org.orglot.gosloto.bonus.games.service.strategy.model.BonusGameBuyDto;
import org.orglot.gosloto.bonus.games.service.strategy.model.BonusGameBuyExecuteData;

import java.util.List;
import java.util.UUID;

@Mapper
public interface BonusGameBuyExecuteDataMapper {

  BonusGameBuyExecuteDataMapper MAPPER = Mappers.getMapper(BonusGameBuyExecuteDataMapper.class);

  @Mapping(target = "multiplier", source = "gameData.multiplier")
  @Mapping(target = "coupons", source = "gameData.coupons")
  @Mapping(target = "bonusGameBuy", source = "bonusGameBuy")
  @Mapping(target = "price", source = "price")
  BonusGameBuyExecuteData toBonusGameBuyExecuteData(BonusGameBuy bonusGameBuy,
                                                    BonusGameBuyData gameData,
                                                    UUID newUUID,
                                                    String sapTransactionId,
                                                    String platform,
                                                    String os,
                                                    List<Integer> rewardsIds,
                                                    Integer price);

  @Mapping(target = "modePrice", source = "price")
  BonusGameBuyDto toBonusGameBuyDto(BonusGameBuy bonusGameBuy);

  List<BonusGameBuyCouponDto> toBonusGameBuyCouponDtoList(List<BonusGameBuyCoupon> coupons);

  BonusGameBuyCouponDto toBonusGameBuyCouponDto(BonusGameBuyCoupon coupon);
}
