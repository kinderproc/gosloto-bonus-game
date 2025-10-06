package org.orglot.gosloto.bonus.games.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PuzzleItemWithSessionCreateDate {
    /**
     * Идентификатор элемента пазла
     */
    private Long id;
    /**
     * Ссылка на изображение элемента пазла
     */
    private String url;
    /**
     * Позиция элемента пазла
     */
    private Integer position;
    /**
     * Идентификатор пазла
     */
    private Long puzzleId;
    /**
     * Ссылка на изображение пазла
     */
    private String puzzleUrl;
    /**
     * Тип игры
     */
    private String puzzleGameType;
    /**
     * Название пазла
     */
    private String puzzleName;
    /**
     * Тип пазла
     */
    private String puzzleType;
    /**
     * Редкость пазла
     */
    private String puzzleRarity;
    /**
     * Сумма приза за сбор пазла
     */
    private Integer puzzlePrize;
    /**
     * Количество обменов пазла
     */
    private Integer userPuzzleCollectedCount;
    /**
     * Собран ли пазл
     */
    private Boolean userPuzzleCollected;
    /**
     * Дата получения элемента пазла
     */
    private Instant createDate;
}
