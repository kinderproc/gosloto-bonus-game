package org.orglot.bonus.games.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Модель с результатом завершения бонусной игры
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameCompleteResult {

    public static final String REFILL_ERROR = "refill error";
    public static final String PRIZE_NULL = "prize null";
    public static final String PRIZE_ERROR = "prize count error";

    /**
     * Сумма выигрыша
     */
    private Integer totalPrize;
    /**
     * Статус игры
     */
    private PlayStatusResponse status;
    /**
     * Заполняется в случае наличии ошибки при оплате (PlayStatusResponse = ERROR)
     */
    private String error;
    /**
     * Дополнительная награда
     */
    private List<Reward> rewards;

    /**
     * Данные по билетам при типе lottery
     */
    private List<LotteryTicket> tickets;

    public GameCompleteResult(String error) {
        this.error = error;
        this.status = PlayStatusResponse.ERROR;
    }

    public static GameCompleteResult error(String error) {
        return new GameCompleteResult(error);
    }

    public static GameCompleteResult empty() {
        return GameCompleteResult.builder().build();
    }
}
