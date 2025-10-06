package org.orglot.bonus.games.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Информация для завершения бонусной игры
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BonusGameEnd {
    /**
     * Идентификатор пользователя
     */
    private Long userId;
    /**
     * Идентификатор сессии
     */
    private UUID sessionUUID;
    /**
     * Выиграл ли пользователь
     */
    private Boolean isWin;
    /**
     * Набранное пользователем количество очков
     */
    private int score;
    /**
     * Максимально возможное количество очков
     */
    private int avscore;
    /**
     * Номер телефона пользователя (для начисления бонусов)
     */
    private String mobile;
}
