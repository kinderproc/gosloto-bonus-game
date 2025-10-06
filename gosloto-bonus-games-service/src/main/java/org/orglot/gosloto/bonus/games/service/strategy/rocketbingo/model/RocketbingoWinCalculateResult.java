package org.orglot.gosloto.bonus.games.service.strategy.rocketbingo.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class RocketbingoWinCalculateResult {

  /**
   * выигрышная комбинация
   */
  private List<Integer> winCombination;
  /**
   * приз
   */
  private Integer prize;

}
