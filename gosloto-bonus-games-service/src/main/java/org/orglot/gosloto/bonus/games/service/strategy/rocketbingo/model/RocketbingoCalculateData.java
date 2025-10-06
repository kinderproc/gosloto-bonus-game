package org.orglot.gosloto.bonus.games.service.strategy.rocketbingo.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import javax.validation.constraints.AssertTrue;

@Data
@Accessors(chain = true)
public class RocketbingoCalculateData {
  private List<Integer> numbers;
  private List<Integer> winningNumbers;

  @AssertTrue
  public boolean isValid() {
    return ObjectUtils.allNotNull(numbers, winningNumbers) ||
        ObjectUtils.allNull(numbers, winningNumbers);
  }

}
