package org.orglot.gosloto.bonus.games.exception;

/**
 * Исключение для операций с элементами в профиле пользователя
 */
public class IllegalProfileElementException extends RuntimeException {

  public IllegalProfileElementException(String message) {
    super(message);
  }

}
