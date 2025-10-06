package org.orglot.gosloto.bonus.games.service.strategy.rocketbingo.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import javax.validation.Valid;

@Data
@Accessors(chain = true)
public class RocketbingoCombinationData {
  /**
   * Путь к комбинации пользователя
   */
  private List<Integer> numbers;

  /**
   * Путь к выигрышной комбинации
   */
  @Valid
  private RocketbingoWinCombinationCalculateData structured;

}
