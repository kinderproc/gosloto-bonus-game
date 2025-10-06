package org.orglot.bonus.games.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * Модель со списком доступных бонусных игр
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableBonusGames {

    /**
     * Список бонусных игр с параметрами
     */
    private List<GameTypeResponse> games;

    public static AvailableBonusGames empty() {
        return AvailableBonusGames.builder().games(Collections.emptyList()).build();
    }
}
