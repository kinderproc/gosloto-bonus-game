package org.orglot.gosloto.bonus.games.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Сессия игры пользователя
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSession {
    private UUID uuid;
    /**
     * идентификатор пользователя
     */
    private Long userId;
    /**
     * тип игры
     */
    private String gameType;
    /**
     * статус
     */
    private SessionState sessionState;
    /**
     * Выигрыш (рассчитывается при покупке)
     */
    private List<Integer> prize;
    /**
     * Сумма приза на текущем шаге (только для STEP игр, номер режима = номер шага)
     */
    private Integer lastPrize;
    /**
     * Ставка пользователя (json)
     */
    private String bet;
    /**
     * идентификатор транзакции в SAP
     */
    private String sapTransactionId;
    /**
     * дата создания
     */
    private Instant createDate;
    /**
     * дата обновления
     */
    private Instant updateDate;
    /**
     * тип подключения
     */
    private String platform;
    /**
     * операционная система
     */
    private String os;
}
