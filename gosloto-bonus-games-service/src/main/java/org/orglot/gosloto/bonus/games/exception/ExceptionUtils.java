package org.orglot.gosloto.bonus.games.exception;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class ExceptionUtils {

  public static Throwable getRootCause(Throwable throwable) {
    Throwable rootCause = throwable;
    while (Objects.nonNull(rootCause.getCause()) && rootCause.getCause() != rootCause) {
      rootCause = rootCause.getCause();
    }
    return rootCause;
  }

  public static String formatStackTrace(Throwable throwable) {
    return Arrays.stream(throwable.getStackTrace())
      .map(StackTraceElement::toString)
      .collect(Collectors.joining(",\n"));
  }
}
