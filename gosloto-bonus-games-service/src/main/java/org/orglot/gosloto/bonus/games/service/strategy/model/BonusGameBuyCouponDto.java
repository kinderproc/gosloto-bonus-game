package org.orglot.gosloto.bonus.games.service.strategy.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class BonusGameBuyCouponDto {
  private List<Integer> combination;
  private List<Integer> extraCombination;
  private String parity;
}
