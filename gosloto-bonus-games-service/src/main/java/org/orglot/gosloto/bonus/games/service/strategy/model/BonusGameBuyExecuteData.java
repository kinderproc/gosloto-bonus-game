package org.orglot.gosloto.bonus.games.service.strategy.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class BonusGameBuyExecuteData {
  private BonusGameBuyDto bonusGameBuy;
  private UUID newUUID;
  private String sapTransactionId;
  private String platform;
  private String os;
  private List<Integer> rewardsIds;
  private int multiplier;
  private Integer price;
  private List<BonusGameBuyCouponDto> coupons;
}
