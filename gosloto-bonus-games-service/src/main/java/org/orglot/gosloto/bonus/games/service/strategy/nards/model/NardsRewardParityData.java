package org.orglot.gosloto.bonus.games.service.strategy.nards.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.orglot.bonus.games.model.response.PrizeType;
import org.orglot.gosloto.bonus.games.service.strategy.nards.enums.NardsParityPrizeSubType;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class NardsRewardParityData {

  /**
   * тип награды
   */
  @NotNull
  private PrizeType type;

  /**
   * Подтип
   */
  @NotNull
  private NardsParityPrizeSubType prizeSubType;

  /**
   * Размер выигрыша
   */
  private Integer prize;
}
