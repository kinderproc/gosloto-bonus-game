package org.orglot.gosloto.bonus.games.service.strategy.nards.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Accessors(chain = true)
public class NardsWinCalculateData {

  @Valid
  private NardsRewardCombinationData combinationData;

  @Valid
  @NotEmpty
  private Map<@NotNull ParityType, @Valid @NotNull NardsRewardParityData> parityData;

  private List<Integer> combination;
  private List<Integer> extraCombination;
  private ParityType parity;

  @Positive
  private int multiplier;

  @AssertTrue
  public boolean isValid() {
    return ObjectUtils.allNotNull(combinationData, combination) ||
        ObjectUtils.allNull(combinationData, combination) &&
        !CollectionUtils.isEmpty(extraCombination) ||
        ObjectUtils.isNotEmpty(parityData);
  }
}
