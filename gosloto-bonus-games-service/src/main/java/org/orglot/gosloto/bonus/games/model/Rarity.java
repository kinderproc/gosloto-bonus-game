package org.orglot.gosloto.bonus.games.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Редкость
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rarity {
  /**
   * Идентификатор
   */
  private Long id;
  /**
   * Тип
   */
  private String type;
  /**
   * Отображаемое наименование
   */
  private String title;
  /**
   * Отображать ли
   */
  private boolean display;
}
