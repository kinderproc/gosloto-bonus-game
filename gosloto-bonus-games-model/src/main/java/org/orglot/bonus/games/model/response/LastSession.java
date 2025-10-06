package org.orglot.bonus.games.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Данные о последней запущенной сессии бонусной игры
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LastSession {
    /**
     * UUID сессии
     */
    private UUID sessionUUID;
    /**
     * Номер режима игры
     */
    private Integer modeNumber;
    /**
     * Выбранная стоимость данного режима
     */
    private Integer price;
    /**
     * Приз, который может выиграть пользователь (предрасчитанный)
     */
    private List<Integer> prizes;
    /**
     * Шкала для игры, соответствующая цене (из конфига)
     */
    private Map<Integer, Integer> scale;
    /**
     * Список расходников (ресурсов)
     */
    private List<Consumable> consumables;

}
