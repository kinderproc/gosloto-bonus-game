package org.orglot.gosloto.bonus.games.service.strategy.nards.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class NardsWinCalculateResult {
  private List<Integer> winCombination;
  private ParityType parity;
  private Integer prize;
}
