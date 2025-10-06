package org.orglot.gosloto.bonus.games.service.strategy.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DrawInfo {
  /**
   * Данные тиража
   */
  private StructuredData structured;
}
