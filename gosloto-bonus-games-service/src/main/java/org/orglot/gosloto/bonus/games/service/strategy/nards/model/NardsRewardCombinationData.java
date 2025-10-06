package org.orglot.gosloto.bonus.games.service.strategy.nards.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.orglot.bonus.games.model.response.PrizeType;
import org.orglot.gosloto.bonus.games.service.strategy.nards.enums.NardsCombinationPrizeSubType;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class NardsRewardCombinationData {

  /**
   * тип награды
   */
  @NotNull
  private PrizeType type;

  /**
   * Подтип
   */
  @NotNull
  private NardsCombinationPrizeSubType prizeSubType;

  /**
   * Размер выигрыша
   */
  private Integer prize;
}
