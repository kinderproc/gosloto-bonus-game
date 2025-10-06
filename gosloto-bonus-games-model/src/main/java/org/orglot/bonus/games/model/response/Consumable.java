package org.orglot.bonus.games.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Внутреигровой расходный материал
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Consumable {
    /**
     * Идентификатор
     */
    private UUID id;
    /**
     * Описание
     */
    private String name;
    /**
     * Стоимость в бонусных баллах
     */
    private long price;
    /**
     * Доступность
     */
    private boolean available;
    /**
     * Количество клеток, заполняемых на шкале прогресса при применении
     */
    private int weight;
    /**
     * Ссылка на изображение
     */
    private String iconUrl;
}
