package org.orglot.gosloto.bonus.games.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BonusGameConfig {
    /**
     * Наименование игры
     */
    private String gameType;
    /**
     * Отображаемое название игры
     */
    private String title;
    /**
     * Описание игры
     */
    private String description;
    /**
     * Текст правил игры
     */
    private String rules;
    /**
     * Видимость игры для пользователя
     */
    private boolean visible;
    /**
     * Тип игры
     */
    private String gameSpec;
    /**
     * URL для МП
     */
    private String gameUrl;
    /**
     * Порядок отображения
     */
    private int order;
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
    /**
     * Конфигурации режимов игры
     */
    private final List<Mode> modes = new ArrayList<>();
    /**
     * Конфигурация графики
     */
    private Graphic graphic;

}
