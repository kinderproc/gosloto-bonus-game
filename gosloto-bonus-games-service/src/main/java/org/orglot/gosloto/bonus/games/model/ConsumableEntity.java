package org.orglot.gosloto.bonus.games.model;

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
public class ConsumableEntity {
    /**
     * Идентификатор
     */
    private UUID id;
    /**
     * Тип расходного материала
     */
    private Integer type;
    /**
     * Идентификатор игры
     */
    private String bonusGameType;
    /**
     * Описание
     */
    private String name;
    /**
     * Стоимость в бонусных баллах
     */
    private Long price;
    /**
     * Доступность
     */
    private Boolean available;
    /**
     * Количество клеток, заполняемых на шкале прогресса при применении
     */
    private Integer weight;
    /**
     * Ссылка на изображение
     */
    private String iconUrl;
}
