package org.orglot.bonus.games.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class LotteryTicketDataCombination {

  /**
   * Выбранные комбинации в первом поле
   */
  private List<Integer> numbers;

  /**
   * Выбранные комбинации во втором поле
   */
  private List<Integer> extraNumbers;

  /**
   * Ставка на четность
   */
  private String parity;
}
