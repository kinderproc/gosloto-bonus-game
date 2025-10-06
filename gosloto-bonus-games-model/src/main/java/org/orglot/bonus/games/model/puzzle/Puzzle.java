package org.orglot.bonus.games.model.puzzle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orglot.bonus.games.model.BonusGameType;

/**
 * Пазл
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Puzzle {
    /**
     * Ид
     */
    private Long id;
    /**
     * Тип бонусной игры
     */
    private BonusGameType gameType;
    /**
     * Ссылка на изображение
     */
    private String url;
    /**
     * Название
     */
    private String name;
    /**
     * Редкость
     */
    private String rarity;
    /**
     * Количество бонусов за пазл
     */
    private Integer prize;
    /**
     * Тип пазла
     */
    private String type;
    /**
     * Сколько раз собрали пазл
     */
    private Integer collected;
    /**
     * Статус собранности пазла (true - собран)
     */
    private Boolean status;

    public Puzzle(BonusGameType gameType, String url) {
        this.gameType = gameType;
        this.url = url;
    }
}
