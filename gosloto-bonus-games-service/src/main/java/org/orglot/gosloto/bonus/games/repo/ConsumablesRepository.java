package org.orglot.gosloto.bonus.games.repo;

import org.orglot.gosloto.bonus.games.model.ConsumableEntity;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ConsumablesRepository {

    Mono<ConsumableEntity> findById(UUID id);
}
