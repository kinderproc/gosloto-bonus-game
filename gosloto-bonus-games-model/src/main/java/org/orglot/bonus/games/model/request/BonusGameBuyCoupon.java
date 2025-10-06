package org.orglot.bonus.games.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Данные билета
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BonusGameBuyCoupon {
  private List<Integer> combination;
  private List<Integer> extraCombination;
  private String parity;
}
