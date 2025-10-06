package org.orglot.bonus.games.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orglot.bonus.games.model.response.Consumable;
import org.orglot.bonus.games.model.validate.group.LotteryValid;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Информация для покупки бонусной игры
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BonusGameBuy {
    /**
     * Идентификатор пользователя
     */
    private Long userId;
    /**
     * Номер телефона пользователя
     */
    private String mobile;
    /**
     * Тип игры
     */
    private String gameType;
    /**
     * Режим игры
     */
    private Integer mode;
    /**
     * Стоимость игры
     */
    @NotNull(groups = LotteryValid.class)
    private Integer price;
    /**
     * Источник
     */
    private String devicePlatform;
    /**
     * Данные игры
     */
    @Valid
    @NotNull(groups = LotteryValid.class)
    private BonusGameBuyData gameData;
    /**
     * Расходник (ресурс)
     */
    private String consumableId;
}
