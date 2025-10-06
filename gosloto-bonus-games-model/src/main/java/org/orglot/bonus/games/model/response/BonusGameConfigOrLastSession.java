package org.orglot.bonus.games.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orglot.bonus.games.model.response.config.ModeTypeGameConfig;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Модель с конфигурацией для запуска игры, либо сессия уже запущенной игры
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BonusGameConfigOrLastSession {

    /**
     * SessionUUID
     */
    @Deprecated
    private UUID sessionUUID;

    /**
     * Данные о последней запущенной сессии бонусной игры
     */
    private LastSession lastSession;

    /**
     * Возможные конфигурации игры для выбора пользователем
     */
    private ModeTypeGameConfig gameConfig;

    public static BonusGameConfigOrLastSession empty() {
       return BonusGameConfigOrLastSession.builder().build();
    }

    public static BonusGameConfigOrLastSession buildWithSessionUUID(UUID sessionUUID,
                                                                    Integer mode,
                                                                    Integer price,
                                                                    Map<Integer, Integer> scale,
                                                                    List<Integer> prize,
                                                                    ModeTypeGameConfig gameConfig,
                                                                    List<Consumable> consumables) {
        return BonusGameConfigOrLastSession.builder()
                .lastSession(LastSession.builder()
                        .sessionUUID(sessionUUID)
                        .modeNumber(mode)
                        .price(price)
                        .prizes(prize)
                        .scale(scale)
                        .consumables(consumables)
                        .build())
                .sessionUUID(sessionUUID)
                .gameConfig(gameConfig)
                .build();
    }

    public static BonusGameConfigOrLastSession buildWithGameConfig(ModeTypeGameConfig gameConfig) {
        return BonusGameConfigOrLastSession.builder()
                .gameConfig(gameConfig)
                .build();
    }
}
