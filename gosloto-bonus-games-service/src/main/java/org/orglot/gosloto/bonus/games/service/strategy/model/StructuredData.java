package org.orglot.gosloto.bonus.games.service.strategy.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class StructuredData {
  /**
   * Данные по четности
   */
  private String parity;

  /**
   * Выпавшие комбинации
   */
  private List<Integer> played;
}
