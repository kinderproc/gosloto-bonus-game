package org.orglot.bonus.games.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlayStatusResponse {
    /**
     * Игра запущена
     */
    IN_PROGRESS("in_progress"),
    /**
     * Игра завершена
     */
    SUCCESS("success"),
    /**
     * Игра запущена/завершена с ошибкой
     */
    ERROR("error");

    private String text;
}
