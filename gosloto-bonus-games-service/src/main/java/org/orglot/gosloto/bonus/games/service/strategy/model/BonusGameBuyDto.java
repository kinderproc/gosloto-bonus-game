package org.orglot.gosloto.bonus.games.service.strategy.model;

import lombok.Data;
import lombok.experimental.Accessors;

//todo добавить в маппинг
@Data
@Accessors(chain = true)
public class BonusGameBuyDto {

  /**
   * Идентификатор пользователя
   */
  private Long userId;

  /**
   * Тип игры
   */
  private String gameType;

  /**
   * Режим игры
   */
  private Integer mode;

  /**
   * базовая стоимость при текущем режиме игры
   */
  private Integer modePrice;
}
