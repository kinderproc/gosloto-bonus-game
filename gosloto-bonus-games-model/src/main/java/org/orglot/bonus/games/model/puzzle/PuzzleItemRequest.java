package org.orglot.bonus.games.model.puzzle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO для создания или обновления элемента пазла.
 * Определяет позицию элемента в пазле и URL изображения.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PuzzleItemRequest {

  /**
   * Позиция элемента пазла (от 1 до 25).
   */
  private Integer puzzleItemId;

  /**
   * URL изображения элемента пазла.
   */
  private String url;

}
