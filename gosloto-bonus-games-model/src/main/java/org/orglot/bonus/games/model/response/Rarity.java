package org.orglot.bonus.games.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Элемент справочника редкости
 */
@Getter
@AllArgsConstructor
public class Rarity {
  /**
   * Ключ
   */
  private String name;
  /**
   * Наименование
   */
  private String title;
  /**
   * Порядковый номер
   */
  private Long order;
}
