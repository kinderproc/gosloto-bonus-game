package org.orglot.gosloto.bonus.games.service.strategy.nards.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.orglot.gosloto.bonus.games.service.strategy.nards.model.ParityType;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum NardsParityPrizeSubType {
  NARDS_ODD("WIN_CATEGORIES_RAPIDO_ODD", ParityType.ODD),
  NARDS_EVEN("WIN_CATEGORIES_RAPIDO_EVEN", ParityType.EVEN),
  NARDS_EQUAL("WIN_CATEGORIES_RAPIDO_EQUAL", ParityType.FIFTY_FIFTY),;

  private static final Map<String, NardsParityPrizeSubType> MAP = Arrays.stream(NardsParityPrizeSubType.values())
      .collect(Collectors.toMap(NardsParityPrizeSubType::getValue, Function.identity()));

  private final String value;
  private final ParityType parityType;

  public static NardsParityPrizeSubType byValue(String value) {
    return Optional.ofNullable(MAP.get(value))
        .orElseThrow(() -> new IllegalArgumentException("Invalid NardsParityPrizeSubType: " + value));
  }
}
