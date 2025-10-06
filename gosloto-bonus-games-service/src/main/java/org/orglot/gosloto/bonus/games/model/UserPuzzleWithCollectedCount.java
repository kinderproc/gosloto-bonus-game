package org.orglot.gosloto.bonus.games.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Пазл пользователя
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPuzzleWithCollectedCount {
    /**
     * Ссылка на изображение элемента пазла
     */
    private String url;
    /**
     * Название пазла
     */
    private String name;
    /**
     * Тип пазла
     */
    private String type;
    /**
     * Редкость пазла
     */
    private String rarity;
    /**
     * Количество обменов пазла
     */
    private Integer collectedCount;
    /**
     * Количество обменов пазлов по редкости
     */
    private List<PuzzleRarityAndCollectedCount> collectedCountsByRarities;
}
