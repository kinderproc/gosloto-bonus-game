package org.orglot.gosloto.bonus.games.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Конфигурации сервиса
 */
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {
  /**
   * Срок годности награды (попытка сыграть в призовую игру) в днях
   */
  private int attemptExpirationDateInDays;
  /**
   * Код для начисления бонусов через САП за бонусные игры
   */
  private String productCode;
  /**
   * Наименование типа валюты для попыток сыграть в призовую игру
   */
  private String walletTypeForAttempt;
}
