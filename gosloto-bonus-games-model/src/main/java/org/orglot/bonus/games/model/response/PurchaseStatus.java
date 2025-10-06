package org.orglot.bonus.games.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PurchaseStatus {

    /**
     * Статус присваивается в момент перехода пользователем на страницу оплаты
     */
    WAITING("waiting"),
    /**
     * Статус присваивается в момент обработки оплаты
     */
    IN_PROGRESS("in_progress"),
    /**
     * Статус присваивается после успешной оплаты
     */
    SUCCESS("ok"),
    /**
     * Статус присваивается после оплаты с ошибкой
     */
    ERROR("error");

    private String text;
}
