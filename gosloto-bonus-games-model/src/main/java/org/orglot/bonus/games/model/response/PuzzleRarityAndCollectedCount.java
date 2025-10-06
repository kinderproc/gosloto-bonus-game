package org.orglot.bonus.games.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Пазл пользователя
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PuzzleRarityAndCollectedCount {
  /**
   * Редкость пазла
   */
  private String rarity;
  /**
   * Количество обменов пазла
   */
  private Integer collectedCount;
}
