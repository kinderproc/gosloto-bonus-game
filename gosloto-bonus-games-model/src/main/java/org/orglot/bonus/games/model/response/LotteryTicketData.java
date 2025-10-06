package org.orglot.bonus.games.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LotteryTicketData {
  /**
   * Множитель
   */
  private Integer multiplier;

  /**
   * Выбранные комбинации
   */
  private LotteryTicketDataCombination combinations;
}
