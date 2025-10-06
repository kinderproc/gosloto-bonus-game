package org.orglot.gosloto.bonus.games.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Элемент шкалы
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScaleItem {
    /**
     * Отметка
     */
    private int mark;
    /**
     * Приз
     */
    private int prize;
}
