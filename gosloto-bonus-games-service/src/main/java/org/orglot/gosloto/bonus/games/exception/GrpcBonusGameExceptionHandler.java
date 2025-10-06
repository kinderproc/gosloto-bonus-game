package org.orglot.gosloto.bonus.games.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class GrpcBonusGameExceptionHandler {

  public static StatusRuntimeException forException(ApiGrpcBonusGameException apiException) {
    var throwable = apiException.getDelegatedThrowable();
    if (throwable instanceof NullPointerException) {
      throwable = new GrpcBonusGameException((NullPointerException) throwable);
    }
    String fileName = null;
    String lineNumber = null;
    String className = null;
    String methodName = null;
    if (Objects.nonNull(ExceptionUtils.getRootCause(throwable).getStackTrace()) &&
        ExceptionUtils.getRootCause(throwable).getStackTrace().length > 0) {
      var rootCause = ExceptionUtils.getRootCause(throwable).getStackTrace()[0];
      fileName = rootCause.getFileName();
      lineNumber = String.valueOf(rootCause.getLineNumber());
      methodName = rootCause.getMethodName();
      className = rootCause.getClassName();
    }

    if (throwable instanceof GrpcBonusGameException grpcException) {
      if (Objects.nonNull(grpcException.getStatus())) {
        var trace = ExceptionUtils.formatStackTrace(throwable);
        return grpcException.getStatus()
          .withCause(throwable)
          .withDescription(grpcException.getLocalizedMessage())
          .augmentDescription(formatExceptionMessage(apiException.getApiClazz(), apiException.getApiMethod(),
            grpcException.getClazz(), grpcException.getMethod(), fileName, lineNumber,
            trace))
          .asRuntimeException();
      }
    }

    var trace = ExceptionUtils.formatStackTrace(throwable);
    return Status.UNKNOWN
      .withCause(throwable)
      .withDescription(throwable.getLocalizedMessage())
      .augmentDescription(formatExceptionMessage(apiException.getApiClazz(), apiException.getApiMethod(),
        className, methodName, fileName, lineNumber,
        trace))
      .asRuntimeException();
  }

  public static StatusRuntimeException forException(Throwable throwable) {
    if (throwable instanceof NullPointerException) {
      throwable = new GrpcBonusGameException((NullPointerException) throwable);
    }
    String fileName = null;
    String lineNumber = null;
    String className = null;
    String methodName = null;
    if (Objects.nonNull(ExceptionUtils.getRootCause(throwable).getStackTrace()) &&
        ExceptionUtils.getRootCause(throwable).getStackTrace().length > 0) {
      var rootCause = ExceptionUtils.getRootCause(throwable).getStackTrace()[0];
      fileName = rootCause.getFileName();
      lineNumber = String.valueOf(rootCause.getLineNumber());
      methodName = rootCause.getMethodName();
      className = rootCause.getClassName();
    }
    if (throwable instanceof GrpcBonusGameException grpcException) {
      if (Objects.nonNull(grpcException.getStatus())) {
        var trace = ExceptionUtils.formatStackTrace(throwable);
        return grpcException.getStatus()
          .withCause(throwable)
          .withDescription(grpcException.getLocalizedMessage())
          .augmentDescription(formatExceptionMessage(grpcException.getClazz(), grpcException.getMethod(), fileName,
            lineNumber, trace))
          .asRuntimeException();
      }
    }

    var trace = ExceptionUtils.formatStackTrace(throwable);
    return Status.UNKNOWN
      .withCause(throwable)
      .withDescription(throwable.getLocalizedMessage())
      .augmentDescription(formatExceptionMessage(className, methodName, fileName, lineNumber, trace))
      .asRuntimeException();
  }

  private static String formatExceptionMessage(String apiName, String apiMethod, String clazz, String method, String file, String line,
                                               String stackTrae) {
    return String.format("Error in api %s.%s. Error while processing %s.%s. file- %s, line - %s, \n stackTrace-%s", apiName, apiMethod,
      clazz, method, file, line, stackTrae);
  }

  private static String formatExceptionMessage(String clazz, String method, String file, String line, String stackTrace) {
    return String.format("Error while processing %s.%s. file - %s, line - %s, \n stackTrace-%s", clazz, method, file, line, stackTrace);
  }

}
