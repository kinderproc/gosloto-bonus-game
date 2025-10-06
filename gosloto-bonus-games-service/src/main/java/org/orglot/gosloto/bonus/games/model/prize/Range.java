package org.orglot.gosloto.bonus.games.model.prize;

import lombok.Builder;
import lombok.Data;
import org.orglot.bonus.games.model.response.PrizeType;

/**
 * Данные об интервале
 */
@Data
@Builder
public class Range {
    /**
     * Начало интервала
     */
    private Integer start;
    /**
     * Конец интервала
     */
    private Integer end;
    /**
     * Тип приза для заданного интервала
     */
    private PrizeType prizeType;
    /**
     * Количество для данного типа приза
     */
    private Integer count;
    /**
     * Подтип
     */
    private String prizeSubType;
    /**
     * Идентификатор приза (например, номер элемента пазла)
     */
    private Long prizeId;
}
