package org.orglot.gosloto.bonus.games.validation;

public interface GoslotoValidator {
  <T> void validateAndExceptionIfFalse(T object, String modelName, Class<?>... groups);

  <T> void validateAndExceptionIfFalse(T object, String modelName);

  <T> boolean isValid(T object, String modelName, Class<?>... groups);

  <T> boolean isValid(T object, String modelName);
}
