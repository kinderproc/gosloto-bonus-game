package org.orglot.bonus.games.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Модель с информацией по статусу запуска бонусной игры
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BonusGamePlayStatus {
    /**
     * Статус запуска
     */
    private PlayStatusResponse status;
    /**
     * Заполняется в случае наличии ошибки при оплате (PlayStatusResponse = ERROR)
     */
    private String error;
    /**
     * Сумма выигрыша
     */
    private List<Integer> prizes;
    /**
     * Идентификатор сессии
     */
    private UUID sessionUUID;
    /**
     * Выигрышные позиции
     */
    private Map<Integer, Integer> scale;
    /**
     * Расходники (ресурсы)
     */
    private List<Consumable> consumables;

    public BonusGamePlayStatus(PlayStatusResponse status, String error) {
        this.status = status;
        this.error = error;
    }

    public BonusGamePlayStatus(String error) {
        this.status = PlayStatusResponse.ERROR;
        this.error = error;
    }

    public BonusGamePlayStatus(PlayStatusResponse status, UUID sessionUUID) {
        this.status = status;
        this.sessionUUID = sessionUUID;
    }

    public BonusGamePlayStatus(PlayStatusResponse status, List<Integer> prizes, UUID sessionUUID) {
        this.status = status;
        this.prizes = prizes;
        this.sessionUUID = sessionUUID;
    }

    public static BonusGamePlayStatus empty() {
        return BonusGamePlayStatus.builder().build();
    }

    public static BonusGamePlayStatus error(String error) {
        return new BonusGamePlayStatus(error);
    }

}
