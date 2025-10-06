package org.orglot.gosloto.bonus.games.service.strategy.rocketbingo.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.orglot.bonus.games.model.PrizeSubType;
import org.orglot.bonus.games.model.response.Reward;
import org.springframework.util.CollectionUtils;

import java.util.List;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Positive;

@Data
@Accessors(chain = true)
public class RocketbingoWinCombinationCalculateData {

  /**
   * Итоговая сгенерированная комбинация из 35 чисел
   */
  private List<Integer> played;

  /**
   * Множитель награды
   */
  @Positive
  private int multiplier;

  /**
   * Награда
   */
  private Reward reward;

  /**
   * Подтип награды
   */
  private PrizeSubType prizeSubType;

  @AssertTrue
  public boolean isValid() {
    return !CollectionUtils.isEmpty(played);
  }
}
