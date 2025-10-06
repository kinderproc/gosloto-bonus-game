package org.orglot.gosloto.bonus.games.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Информация о режиме игры
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Mode {
    /**
     * Номер режима
     */
    private Integer number;
    /**
     * Цена
     */
    private Integer price;
    /**
     * Размерность выигрышей призовой шкалы
     */
    private List<ScaleItem> scale;
    /**
     * Идентификатор конфига для расчета приза
     */
    private List<Integer> randomPrizeIds;
    /**
     * Идентификаторы конфиг для расчета наград (сколько идентификаторов - столько расчетов)
     */
    private List<Integer> randomRewardIds;
    /**
     * Русскоязычное описание режима
     */
    private String description;
    /**
     * Параметр отображения цены игры
     */
    private boolean visible;
}
