package org.orglot.bonus.games.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Информация по билету
 */
@Data
@Accessors(chain = true)
public class LotteryTicket {

  /**
   * Дополнительные награды
   */
  private List<Reward> rewards;

  /**
   * Размер приза
   */
  private Integer totalPrize;

  /**
   * Данные билета
   */
  private LotteryTicketData data;

  /**
   * Информация по розыгрышу
   */
  private LotteryDrawInfo drawInfo;
}
