package org.orglot.gosloto.bonus.games.exception;

import lombok.Getter;

@Getter
public class ApiGrpcBonusGameException extends RuntimeException {
    private final String apiClazz;
    private final String apiMethod;
    private final Throwable delegatedThrowable;

    public ApiGrpcBonusGameException(Throwable cause, String apiClazz, String apiMethod) {
        super(cause);
        this.delegatedThrowable = cause;
        this.apiClazz = apiClazz;
        this.apiMethod = apiMethod;
    }
}
