package org.orglot.bonus.games.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

/**
 * Информация о бонусной игре
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BonusGameBuyData {

  @Positive
  private int multiplier;

  @NotEmpty
  private List<BonusGameBuyCoupon> coupons;
}
