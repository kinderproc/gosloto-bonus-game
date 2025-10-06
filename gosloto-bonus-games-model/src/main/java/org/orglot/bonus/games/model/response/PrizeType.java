package org.orglot.bonus.games.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Тип приза, который может выиграть пользователь
 */
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public enum PrizeType {
    /**
     * Бонусные баллы
     */
    BONUS,
    /**
     * Элемент пазла
     */
    RANDOM_PUZZLE_ITEM_NOT_USED("Фрагмент пазла №%s \"%s\""),
    /**
     * Выигрышные категории Рапидо
     */
    WIN_CATEGORIES_RAPIDO("Выигрышные категории Рапидо"),
    /**
     * Выигрышные категории Рокетбинго
     */
    WIN_CATEGORIES_ROCKETBINGO("Выигрышные категории Рокетбинго"),
    /**
     * Кастомная валюта
     */
    CURRENCY,
    /**
     * Рамки и аватарки
     */
    PROFILE_ELEMENTS,
    /**
     * Игрушки
     */
    TOYS,
    /**
     * Гирлянды
     */
    GARLAND,
    /**
     * Навершие
     */
    TOPPER,
    /**
     * Подароки
     */
    GIFT,
    /**
     * Окружение для елки
     */
    ENVIRONMENT,
    /**
     * Физические награды
     */
    PHYSICAL,
    /**
     * Попытка сыграть в призовую игру
     */
    ATTEMPT_AT_THE_PRIZE_GAME,
    /**
     * Деревья
     */
    TREE,
    /**
     * Строения
     */
    STRUCTURE,
    /**
     * Клумбы
     */
    FLOWER,
    /**
     * Декор
     */
    DECOR,
    ACCOMODATION,
    RECREATION,
    INFRASTRUCTURE,
    FUN,
    BOOTS,
    SHORTS,
    TSHIRT,
    GAITERS,
    BALL,
    /**
     * Без выигрыша
     */
    EMPTY;

    /**
     * Описание награды
     */
    private String description;

}
