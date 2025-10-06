package org.orglot.bonus.games.model.response.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.orglot.bonus.games.model.response.GameModeResponse;

import java.util.List;

/**
 * Модель конфигураций (для запуска) бонусной игры режимного типа (для актуальных игр)
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ModeTypeGameConfig {

    /**
     * Режимы игры и информация по режимам
     */
    private List<GameModeResponse> modes;

    /**
     * Тип игры
     */
    private String gameType;
}
