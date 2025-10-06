package org.orglot.bonus.games.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Информация о режиме
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameModeResponse {
    /**
     * Номер режима
     */
    private Integer modeNumber;
    /**
     * Список стоимостей данного режима и соответствующие им шкалы
     */
    private List<PriceAndScaleOfMode> priceAndScale = new ArrayList<>();
    /**
     * Русскоязычное описание режима
     */
    private String description;
}
