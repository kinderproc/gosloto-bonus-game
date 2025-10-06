package org.orglot.gosloto.bonus.games.validation.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.orglot.gosloto.bonus.games.validation.GoslotoValidator;
import org.orglot.gosloto.bonus.games.validation.exception.GoslotoValidateException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoslotoValidatorImpl implements GoslotoValidator {

  private static final String VALID_EXCEPTION_MESSAGE = "Not valid model fields for types: %s, modelName: %s";
  private static final String VALID_SUCCESS_MESSAGE = "Received correct fields for model, modelName: {}";
  private static final String VALID_EXCEPTION_MESSAGE_WITHOUT_GROUP = "Not valid model fields, modelName: %s";
  private static final String LOG_ERRORS_MESSAGE = "Not valid model fields. Found errors : {}";

  private final Validator validator;

  @Override
  public <T> void validateAndExceptionIfFalse(T object, String modelName, Class<?>... groups) {
    var violationsSet = new HashSet<>(validator.validate(object));
    violationsSet.addAll(validator.validate(object, groups));
    checkViolationsAndExceptionIfFalse(
        violationsSet,
        modelName,
        String.format(VALID_EXCEPTION_MESSAGE, Arrays.toString(groups), modelName)
    );
  }

  @Override
  public <T> void validateAndExceptionIfFalse(T object, String modelName) {
    checkViolationsAndExceptionIfFalse(
        new HashSet<>(validator.validate(object)),
        modelName,
        String.format(VALID_EXCEPTION_MESSAGE_WITHOUT_GROUP, modelName)
    );
  }

  @Override
  public <T> boolean isValid(T object, String modelName, Class<?>... groups) {
    var violationsSet = new HashSet<>(validator.validate(object));
    violationsSet.addAll(validator.validate(object, groups));
    return checkViolationsAndTrueIfNotFound(violationsSet, modelName);
  }

  @Override
  public <T> boolean isValid(T object, String modelName) {
    return checkViolationsAndTrueIfNotFound(new HashSet<>(validator.validate(object)), modelName);
  }

  private <S> boolean checkViolationsAndTrueIfNotFound(Set<ConstraintViolation<S>> violations,
                                                       String modelName) {
    if (!violations.isEmpty()) {
      logErrors(violations);
      return false;
    }
    log.debug(VALID_SUCCESS_MESSAGE, modelName);
    return true;
  }

  private <S> void checkViolationsAndExceptionIfFalse(Set<ConstraintViolation<S>> violations,
                                                      String modelName,
                                                      String errorMessage) {
    if (!checkViolationsAndTrueIfNotFound(violations, modelName)) {
      throw new GoslotoValidateException(errorMessage);
    }
  }

  private <S> void logErrors(Set<ConstraintViolation<S>> violations) {
    List<String> violationsList = violations.stream().map(e -> e.getPropertyPath() + ": " + e.getMessage())
        .collect(Collectors.toList());
    String stringViolations = StringUtils.join(", ", violationsList);
    log.error(LOG_ERRORS_MESSAGE, stringViolations);
  }
}
