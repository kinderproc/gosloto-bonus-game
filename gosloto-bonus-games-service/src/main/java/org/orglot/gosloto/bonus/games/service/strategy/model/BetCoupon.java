package org.orglot.gosloto.bonus.games.service.strategy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BetCoupon {

  /**
   * Игровая комбинация
   */
  private List<Integer> combination;

  /**
   * Игровая комбинация по дополнительным полям
   */
  private List<Integer> extraCombination;

  /**
   * Ставка на четность
   */
  private String parity;

  /**
   * Сумма выигрыша по билету
   */
  @NotNull
  private Integer prize;

  /**
   * Данные тиража
   */
  private DrawInfo drawInfo;

  @AssertTrue
  @JsonIgnore
  public boolean isValid() {
    return !CollectionUtils.isEmpty(combination) || Objects.nonNull(parity);
  }
}
