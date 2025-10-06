package org.orglot.gosloto.bonus.games.repo.impl;

import lombok.RequiredArgsConstructor;
import org.orglot.gosloto.bonus.games.model.ConsumableEntity;
import org.orglot.gosloto.bonus.games.repo.ConsumablesRepository;
import org.orglot.gosloto.bonus.games.repo.impl.rowmapper.ConsumableRowMapper;
import org.orglot.gosloto.bonus.games.repo.impl.sql.ConsumablesRepositorySql;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ConsumablesRepositoryImpl implements ConsumablesRepository {

    private final DatabaseClient databaseClient;

    @Override
    public Mono<ConsumableEntity> findById(UUID id) {
        return databaseClient.sql(ConsumablesRepositorySql.FIND_BY_ID)
                .bind("id", id)
                .map(ConsumableRowMapper.MAPPING_FUNCTION)
                .one();
    };
}
