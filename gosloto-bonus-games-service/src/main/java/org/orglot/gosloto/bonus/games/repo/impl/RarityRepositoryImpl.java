package org.orglot.gosloto.bonus.games.repo.impl;

import lombok.AllArgsConstructor;
import org.orglot.gosloto.bonus.games.model.Rarity;
import org.orglot.gosloto.bonus.games.repo.RarityRepository;
import org.orglot.gosloto.bonus.games.repo.impl.rowmapper.RarityRowMapper;
import org.orglot.gosloto.bonus.games.repo.impl.sql.RarityRepositorySql;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Objects;

@Repository
@AllArgsConstructor
public class RarityRepositoryImpl implements RarityRepository {

  private final DatabaseClient databaseClient;

  @Override
  public Flux<Rarity> findAllByDisplay(Boolean display) {
    if (Objects.isNull(display)) {
      return findAll();
    }
    return databaseClient.sql(RarityRepositorySql.FIND_ALL_BY_DISPLAY)
        .bind("display", display)
        .map(RarityRowMapper.MAPPING_FUNCTION)
        .all();
  }

  private Flux<Rarity> findAll() {
    return databaseClient.sql(RarityRepositorySql.FIND_ALL)
        .map(RarityRowMapper.MAPPING_FUNCTION)
        .all();
  }

}
