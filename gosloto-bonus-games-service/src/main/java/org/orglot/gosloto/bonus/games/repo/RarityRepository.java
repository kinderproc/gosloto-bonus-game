package org.orglot.gosloto.bonus.games.repo;

import org.orglot.gosloto.bonus.games.model.Rarity;
import reactor.core.publisher.Flux;

public interface RarityRepository {

  Flux<Rarity> findAllByDisplay(Boolean display);

}
