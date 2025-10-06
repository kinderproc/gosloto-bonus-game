package org.orglot.gosloto.bonus.games.service.strategy;

import org.orglot.bonus.games.model.BonusGameType;
import org.orglot.gosloto.bonus.games.service.strategy.model.BonusGameBuyExecuteData;
import reactor.core.publisher.Mono;

import java.util.UUID;

@FunctionalInterface
public interface BonusGameBuyExecuteStrategy {

  Mono<UUID> execute(BonusGameBuyExecuteData data);

  default BonusGameType getGameType() {
    return null;
  }
}
