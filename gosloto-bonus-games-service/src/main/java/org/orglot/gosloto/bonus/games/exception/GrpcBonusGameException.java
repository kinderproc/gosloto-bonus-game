package org.orglot.gosloto.bonus.games.exception;

import io.grpc.Status;
import lombok.Getter;

import java.util.Objects;

@Getter
public class GrpcBonusGameException extends RuntimeException {
  private final Status status;
  private final String clazz;
  private final String method;

  public GrpcBonusGameException(NullPointerException npe) {
    super(npe);
    var rootCause = ExceptionUtils.getRootCause(npe);
    if (Objects.nonNull(rootCause.getStackTrace()) && rootCause.getStackTrace().length > 0) {
      var rootStackTrace = rootCause.getStackTrace()[0];
      this.clazz = rootStackTrace.getClassName();
      this.method = rootStackTrace.getMethodName();
    } else {
      clazz = null;
      method = null;
    }
    this.status = Status.fromThrowable(npe);
  }

}
