package org.orglot.bonus.games.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Модель с информацией по статусу покупки бонусной игры
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BonusGameBuyStatus {
    /**
     * Статус покупки
     */
    private PurchaseStatus status;
    /**
     * Заполняется в случае наличии ошибки при оплате (PurchaseStatus = ERROR)
     */
    private String error;
    /**
     * Идентификатор сессии
     */
    private UUID sessionUUID;
    /**
     * Расходники (ресурсы)
     */
    private List<Consumable> consumables;

    public BonusGameBuyStatus(String error) {
        this.status = PurchaseStatus.ERROR;
        this.error = error;
    }

    public BonusGameBuyStatus(PurchaseStatus status) {
        this.status = status;
    }

    public BonusGameBuyStatus(PurchaseStatus status, UUID sessionUUID) {
        this.status = status;
        this.sessionUUID = sessionUUID;
    }

    public BonusGameBuyStatus(PurchaseStatus status, UUID sessionUUID, List<Consumable> consumables) {
        this.status = status;
        this.sessionUUID = sessionUUID;
        this.consumables = consumables;
    }

    public static BonusGameBuyStatus empty() {
        return BonusGameBuyStatus.builder().build();
    }

    public static BonusGameBuyStatus error(String message) {
        return new BonusGameBuyStatus(message);
    }

}
