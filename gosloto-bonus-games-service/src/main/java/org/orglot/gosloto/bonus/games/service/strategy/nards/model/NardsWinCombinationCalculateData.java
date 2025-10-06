package org.orglot.gosloto.bonus.games.service.strategy.nards.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class NardsWinCombinationCalculateData {
  private List<Integer> combination;
  private List<Integer> extraCombination;
  private int combinationDigitCount;
  private int extraCombinationDigitCount;
}
