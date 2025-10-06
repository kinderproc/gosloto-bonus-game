package org.orglot.gosloto.bonus.games.service.strategy;

import org.orglot.bonus.games.model.BonusGameType;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface LotteryGameCalculateWinCombinationService<T, R> {

  Mono<R> calculate(@Valid @NotNull T data);

  BonusGameType getGameType();
}
