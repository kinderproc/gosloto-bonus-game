package org.orglot.gosloto.bonus.games.model;

import lombok.Getter;

import java.util.Objects;

@Getter
public class PuzzleKey {
  /**
   * Идентификатор пазла
   */
  private final Long puzzleId;
  /**
   * Название пазла
   */
  private final String puzzleName;
  /**
   * Сумма приза за сбор пазла
   */
  private final Integer puzzlePrize;
  /**
   * Тип пазла
   */
  private final String puzzleType;
  /**
   * Тип игры
   */
  private final String puzzleGameType;
  /**
   * Редкость пазла
   */
  private final String puzzleRarity;
  /**
   * Ссылка на изображение пазла
   */
  private final String puzzleUrl;
  /**
   * Собран ли пазл
   */
  private final Boolean userPuzzleCollected;
  /**
   * Количество обменов пазла
   */
  private final Integer userPuzzleCollectedCount;

  public PuzzleKey(Long puzzleId, String puzzleName, Integer puzzlePrize, String puzzleType,
                   String puzzleGameType, String puzzleRarity, String puzzleUrl,
                   Boolean userPuzzleCollected, Integer userPuzzleCollectedCount) {
    this.puzzleId = puzzleId;
    this.puzzleName = puzzleName;
    this.puzzlePrize = puzzlePrize;
    this.puzzleType = puzzleType;
    this.puzzleGameType = puzzleGameType;
    this.puzzleRarity = puzzleRarity;
    this.puzzleUrl = puzzleUrl;
    this.userPuzzleCollected = userPuzzleCollected;
    this.userPuzzleCollectedCount = userPuzzleCollectedCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PuzzleKey puzzleKey = (PuzzleKey) o;
    return Objects.equals(puzzleId, puzzleKey.puzzleId) &&
        Objects.equals(puzzleName, puzzleKey.puzzleName) &&
        Objects.equals(puzzlePrize, puzzleKey.puzzlePrize) &&
        Objects.equals(puzzleType, puzzleKey.puzzleType) &&
        Objects.equals(puzzleGameType, puzzleKey.puzzleGameType) &&
        Objects.equals(puzzleRarity, puzzleKey.puzzleRarity) &&
        Objects.equals(puzzleUrl, puzzleKey.puzzleUrl) &&
        Objects.equals(userPuzzleCollected, puzzleKey.userPuzzleCollected) &&
        Objects.equals(userPuzzleCollectedCount, puzzleKey.userPuzzleCollectedCount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(puzzleId, puzzleName, puzzlePrize, puzzleType, puzzleGameType,
        puzzleRarity, puzzleUrl, userPuzzleCollected, userPuzzleCollectedCount);
  }
}
