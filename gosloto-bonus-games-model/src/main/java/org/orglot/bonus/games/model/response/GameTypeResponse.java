package org.orglot.bonus.games.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Информация о типе игры
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameTypeResponse {
    /**
     * Наименование игры
     */
    private String gameType;
    /**
     * Активна ли игра
     */
    private boolean visible;
    /**
     * Маленькая иконка
     */
    private String smallIcon;
    /**
     * Большая иконка
     */
    private String bigIcon;
    /**
     * Маленькая иконка (ховер)
     */
    private String smallHover;
    /**
     * URL, на который редиректить в WebView для МП
     */
    private String gameUrl;
    /**
     * Иконка для МП
     */
    private String mpIcon;
    /**
     * Правила игры
     */
    private String rules;
    /**
     * Заголовок
     */
    private String title;
    /**
     * Порядок отображения
     */
    private int order;
    /**
     * Тип игры
     */
    private String gameSpec;
    /**
     * Максимальный приз
     */
    private Integer prize;
    /**
     * Максимальное количество валюты акции
     */
    private Integer currency;
    /**
     * Количество пазлов
     */
    private Integer puzzle;
    /**
     * Цвет редкости пазла
     */
    private String puzzleColor;
    /**
     * Стоимость
     */
    private Integer price;
}
