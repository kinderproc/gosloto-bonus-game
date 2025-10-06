package org.orglot.gosloto.bonus.games.model.prize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Конфигурация рандомов для рассчета призов и наград в бонусных играх
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BonusGameRandoms {
    /**
     * Список рандомов
     */
    private List<BonusGameRandom> randoms;
}
