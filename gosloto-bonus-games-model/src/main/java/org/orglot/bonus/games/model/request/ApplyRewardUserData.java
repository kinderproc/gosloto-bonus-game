package org.orglot.bonus.games.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Информация для применения награды пользователю
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyRewardUserData {
  /**
   * Ид пользователя
   */
  private Long userId;
  /**
   * Мобильный номер пользователя
   */
  private String mobile;
  /**
   * Идентификатор сессии бонусной игры
   */
  private UUID bonusGameSessionUUID;
  /**
   * Описание для начисления бонусов (для SAP)
   */
  private String refillBonusDescription;
  /**
   * Причина для начисления бонусов (для SAP)
   */
  private String refillBonusReason;
  /**
   * Срок действия бонусов (для SAP) в месяцах
   */
  private Integer refillBonusLifeTime;
  /**
   * Срок действия бонусов (для SAP) в днях
   */
  private Integer refillBonusLifeTimeDays;
}
