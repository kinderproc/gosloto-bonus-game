package org.orglot.bonus.games.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Информация о пазле для конкретной игры
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BonusGameCollection {
    /**
     * Дата создания
     */
    private Instant createDate;
    /**
     * Идентификатор пазла
     */
    private Long puzzleID;
    /**
     * Урл на изображение пазла
     */
    private String puzzleURL;
    /**
     * Тип игры
     */
    private String gameType;
    /**
     * Редкость игры
     */
    private String rarity;
    /**
     * Количество бонусов
     */
    private Integer prize;
    /**
     * Название пазла
     */
    private String name;
    /**
     * Тип пазла
     */
    private String type;
    /**
     * Количество операций по обмену пазлов на бонусы
     */
    private Integer collected;
    /**
     * Статус пазла
     */
    private Boolean status;
    /**
     * Список элементов пазла
     */
    private List<BonusGameCollectionItem> puzzleItems;
}
