package org.orglot.bonus.games.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BonusGameTransferStatus {
    /**
     * Статус покупки
     */
    private PurchaseStatus status;
    /**
     * Заполняется в случае наличии ошибки при оплате (PurchaseStatus = ERROR)
     */
    private String error;

    public BonusGameTransferStatus(PurchaseStatus status) {
        this.status = status;
    }

    public static BonusGameTransferStatus empty() {
        return BonusGameTransferStatus.builder().build();
    }

}
