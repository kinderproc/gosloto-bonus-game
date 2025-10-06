package org.orglot.bonus.games.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.UUID;

/**
 * Информация для запуска игры бонусной игры
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BonusGamePlay {
    /**
     * Идентификатор пользователя
     */
    private long userId;
    /**
     * Идентификатор сессии
     */
    @NonNull
    private UUID sessionUUID;
    /**
     * Номер игрового режима
     */
    private Integer modeNumber;
}
