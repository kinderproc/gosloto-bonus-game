package org.orglot.gosloto.bonus.games.service.strategy.nards.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum NardsCombinationPrizeSubType {

  NARDS_8_1("WIN_CATEGORIES_RAPIDO_8_1", 8, 1),
  NARDS_8_0("WIN_CATEGORIES_RAPIDO_8_0", 8, 0),
  NARDS_7_1("WIN_CATEGORIES_RAPIDO_7_1", 7, 1),
  NARDS_7_0("WIN_CATEGORIES_RAPIDO_7_0", 7, 0),
  NARDS_6_0("WIN_CATEGORIES_RAPIDO_6_0", 6, 0),
  NARDS_6_1("WIN_CATEGORIES_RAPIDO_6_1", 6, 1),
  NARDS_5_0("WIN_CATEGORIES_RAPIDO_5_0", 5, 0),
  NARDS_5_1("WIN_CATEGORIES_RAPIDO_5_1", 5, 1),
  NARDS_4_1("WIN_CATEGORIES_RAPIDO_4_1", 4, 1),
  NARDS_0_0("WIN_CATEGORIES_RAPIDO_0_0", 0, 0);

  private static final Map<String, NardsCombinationPrizeSubType> MAP = Arrays.stream(NardsCombinationPrizeSubType.values())
      .collect(Collectors.toMap(NardsCombinationPrizeSubType::getValue, Function.identity()));

  private static final Set<String> VALUES = Arrays.stream(NardsCombinationPrizeSubType.values())
      .map(NardsCombinationPrizeSubType::getValue)
      .collect(Collectors.toSet());

  private final String value;
  private final int combinationDigitCount;
  private final int extraCombinationDigitCount;

  public static NardsCombinationPrizeSubType byValue(String value) {
    return Optional.ofNullable(MAP.get(value))
        .orElseThrow(() -> new IllegalArgumentException("Invalid NardsCombinationPrizeSubType: " + value));
  }

  public static boolean isCombination(String value) {
    return VALUES.contains(value);
  }
}
