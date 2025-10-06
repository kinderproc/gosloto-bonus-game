package org.orglot.gosloto.bonus.games.model.prize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Конфигурация рандома для рассчета призов и наград в бонусных играх
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BonusGameRandom {
    /**
     * Идентификатор списка интервалов
     */
    private int id;
    /**
     * Наименование
     */
    private String name;
    /**
     * Верхний диапазон вероятности
     */
    private int rangeLimit;
    /**
     * Список интервалов
     */
    private List<Range> ranges;
}
