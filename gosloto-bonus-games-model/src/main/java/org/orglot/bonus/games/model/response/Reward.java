package org.orglot.bonus.games.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orglot.bonus.games.model.PrizeSubType;

/**
 * Дополнительная награда
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reward {
    /**
     * тип награды
     */
    private PrizeType type;
    /**
     * идентификатор награды
     */
    private Long id;
    /**
     * ссылка на картинку награды
     */
    private String url;
    /**
     * ценность награды (необязательное поле)
     */
    private Integer value;
    /**
     * Подтип
     */
    private PrizeSubType prizeSubType;
    /**
     * описание
     */
    private String description;
    /**
     * Наименование
     */
    private String name;
}
