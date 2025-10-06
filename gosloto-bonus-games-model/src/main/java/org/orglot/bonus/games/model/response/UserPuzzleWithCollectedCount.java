package org.orglot.bonus.games.model.response;

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
     * Количество обменов пазлов по редкости
     */
    private List<PuzzleRarityAndCollectedCount> collectedCountsByRarities;
}
