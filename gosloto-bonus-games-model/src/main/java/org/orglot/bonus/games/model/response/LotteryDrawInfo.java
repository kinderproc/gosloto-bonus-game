package org.orglot.bonus.games.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Информация по розыгрышу
 */
@Data
@Accessors(chain = true)
public class LotteryDrawInfo {
  /**
   * Ставка на четность
   */
  private String parity;

  /**
   * Выпавшие комбинации
   */
  private List<Integer> played;
}
