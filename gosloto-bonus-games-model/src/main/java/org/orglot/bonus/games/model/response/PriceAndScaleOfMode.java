package org.orglot.bonus.games.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Информация о цене и шкале
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceAndScaleOfMode {
    /**
     * Cтоимость режима
     */
    private Integer price;

    /**
     * Шкала для игры, соответствующая цене (из конфига)
     */
    private Map<Integer, Integer> scale;
}
