package org.orglot.bonus.games.model.puzzle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO для создания или обновления пазла.
 * Содержит базовые характеристики пазла, включая название, тип,
 * игровую категорию, редкость, URL изображения и приз.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PuzzleRequest {

  /**
   * Название пазла.
   */
  private String name;

  /**
   * Тип пазла.
   */
  private String type;

  /**
   * Тип игры, к которой относится пазл.
   */
  private String gameType;

  /**
   * Редкость пазла.
   */
  private String rarity;

  /**
   * URL изображения пазла.
   */
  private String url;

  /**
   * Приз за сбор пазла (в условных единицах).
   */
  private Integer prize;

}
