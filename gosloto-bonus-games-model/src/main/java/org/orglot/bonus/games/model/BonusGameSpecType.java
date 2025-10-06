package org.orglot.bonus.games.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Типы логики бонусных игр
 */
@RequiredArgsConstructor
@Getter
public enum BonusGameSpecType {
    /**
     * Тип предрасчитанный
     */
    INSTANT,
    /**
     * Тип скилловый
     */
    SKILL,
    /**
     * Тип многошаговый
     */
    STEP,
    /**
     * Тип бонусный
     */
    PRIZE,
    /**
     * Тип лотерея
     */
    LOTTERY,
    /**
     * Тип сессионная
     */
    SESSION
}
