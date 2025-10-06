package org.orglot.gosloto.bonus.games.repo.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orglot.gosloto.bonus.games.repo.UsersPuzzleItemRepository;
import org.orglot.gosloto.bonus.games.repo.impl.sql.UsersPuzzleItemRepositorySql;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@AllArgsConstructor
public class UsersPuzzleItemRepositoryImpl implements UsersPuzzleItemRepository {

  private final DatabaseClient databaseClient;

  @Override
  public Mono<Integer> save(Long puzzleItemId, Long puzzleId, Long userId) {
    return databaseClient.sql(UsersPuzzleItemRepositorySql.SAVE)
        .bind("puzzle_item_id", puzzleItemId)
        .bind("puzzle_id", puzzleId)
        .bind("user_id", userId)
        .fetch()
        .rowsUpdated();
  }

  @Override
  public Mono<Integer> deleteAllByPuzzleIdAndUserId(Long puzzleId, Long userId) {
    return databaseClient.sql(UsersPuzzleItemRepositorySql.DELETE)
        .bind("puzzle_id", puzzleId)
        .bind("user_id", userId)
        .fetch()
        .rowsUpdated();
  }

  @Override
  public Mono<Boolean> isCollectedByUser(Long userId, Long puzzleId) {
    return databaseClient.sql(UsersPuzzleItemRepositorySql.IS_PUZZLE_ITEMS_COLLECTED_BY_USER_ID_AND_PUZZLE_ID)
        .bind("puzzle_id", puzzleId)
        .bind("user_id", userId)
        .map((row, rowMetaData) -> row.get("is_complete", Boolean.class))
        .one();
  }

}
