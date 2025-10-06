package org.orglot.gosloto.bonus.games.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Информация о пазле пользователя
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPuzzle {
  /**
   * Идентификатор
   */
  private Long id;
  /**
   * Идентификатор пазла
   */
  private Long puzzleId;
  /**
   * Идентификатор пользователя
   */
  private Long userId;
  /**
   * Количество сборов
   */
  private Integer collectedCount;
  /**
   * Собран ли пазл
   */
  private Boolean collected;
  /**
   * Дата последнего обмена пазла на бонусы
   */
  private Instant exchangeDate;

  public static UserPuzzle buildCollectedUserPuzzle(Long userId, Long puzzleId) {
    return UserPuzzle.builder()
        .userId(userId)
        .puzzleId(puzzleId)
        .collected(true)
        .collectedCount(0)
        .build();
  }

  public static UserPuzzle buildUserPuzzle(Long userId, Long puzzleId, boolean isCollected, int collectedCount, Instant exchangeDate) {
    return UserPuzzle.builder()
        .userId(userId)
        .puzzleId(puzzleId)
        .collected(isCollected)
        .collectedCount(collectedCount)
        .exchangeDate(exchangeDate)
        .build();
  }
}
