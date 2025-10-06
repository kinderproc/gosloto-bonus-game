package org.orglot.gosloto.bonus.games.repo.impl;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.orglot.gosloto.bonus.games.model.UserPuzzle;
import org.orglot.gosloto.bonus.games.model.UserPuzzleWithCollectedCount;
import org.orglot.gosloto.bonus.games.repo.UsersPuzzleRepository;
import org.orglot.gosloto.bonus.games.repo.impl.rowmapper.UserPuzzleRowMapper;
import org.orglot.gosloto.bonus.games.repo.impl.sql.UsersPuzzleRepositorySql;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@AllArgsConstructor
public class UsersPuzzleRepositoryImpl implements UsersPuzzleRepository {

  private final DatabaseClient databaseClient;

  @Override
  public Mono<Integer> save(@NonNull UserPuzzle userPuzzle) {
    return databaseClient.sql(UsersPuzzleRepositorySql.SAVE)
        .bind("puzzle_id", userPuzzle.getPuzzleId())
        .bind("user_id", userPuzzle.getUserId())
        .bind("collected_count", userPuzzle.getCollectedCount())
        .bind("collected", userPuzzle.getCollected())
        .bind("exchange_date", userPuzzle.getExchangeDate())
        .fetch()
        .rowsUpdated();
  }

  public Mono<Integer> saveOrUpdateCollectedTrue(@NonNull UserPuzzle userPuzzle) {
    return databaseClient.sql(UsersPuzzleRepositorySql.SAVE_OR_UPDATE_COLLECTED_TRUE)
        .bind("puzzle_id", userPuzzle.getPuzzleId())
        .bind("user_id", userPuzzle.getUserId())
        .fetch()
        .rowsUpdated();
  }

  @Override
  public Mono<UserPuzzle> findByUserIdAndPuzzleId(Long userId, Long puzzleId) {
    return databaseClient.sql(UsersPuzzleRepositorySql.SELECT_BY_PUZZLE_AND_USER)
        .bind("puzzle_id", puzzleId)
        .bind("user_id", userId)
        .map(UserPuzzleRowMapper.MAPPING_FUNCTION)
        .one();
  }

  @Override
  public Mono<Integer> updateCollectedStatusFalseAndIncrementCollectedCount(Long userId, Long puzzleId) {
    return databaseClient.sql(UsersPuzzleRepositorySql.UPDATE_COLLECTED_STATUS_FALSE_AND_INCREMENT_COLLECTED_COUNT)
        .bind("puzzle_id", puzzleId)
        .bind("user_id", userId)
        .fetch()
        .rowsUpdated();
  }

  @Override
  public Flux<UserPuzzleWithCollectedCount> findCollectedAtLeastOnceByUserId(Long userId) {
    return databaseClient.sql(UsersPuzzleRepositorySql.SELECT_USER_PUZZLES_COLLECTED_AT_LEAST_ONCE)
        .bind("user_id", userId)
        .map(UserPuzzleRowMapper.MAPPING_FUNCTION_WITH_COLLECTED_COUNT)
        .all();
  }
}
