package org.orglot.gosloto.bonus.games.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Конфигурация для графики
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Graphic {
    /**
     * Маленькая иконка (ссылка)
     */
    private String smallIcon;
    /**
     * Большая иконка (ссылка)
     */
    private String bigIcon;
    /**
     * Маленькая иконка (ховер) (ссылка)
     */
    private String smallHover;
    /**
     * арт для окна победы (ссылка)
     */
    private String win;
    /**
     * арт для окна поражения (ссылка)
     */
    private String lose;
    /**
     * Иконка для МП (ссылка)
     */
    private String mpIcon;
}
