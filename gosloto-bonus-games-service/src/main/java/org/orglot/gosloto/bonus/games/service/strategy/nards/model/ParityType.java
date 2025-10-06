package org.orglot.gosloto.bonus.games.service.strategy.nards.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum ParityType {
  ODD("ODD"),
  EVEN("EVEN"),
  FIFTY_FIFTY("FIFTY_FIFTY"),
  UNKNOWN(null);

  private static final Map<String, ParityType> MAP = Arrays.stream(ParityType.values())
      .collect(Collectors.toMap(ParityType::getValue, Function.identity()));

  private final String value;

  public static ParityType fromValue(String value) {
    return MAP.getOrDefault(value, UNKNOWN);
  }
}
