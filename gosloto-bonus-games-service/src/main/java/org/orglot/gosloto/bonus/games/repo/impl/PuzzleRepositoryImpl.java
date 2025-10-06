package org.orglot.gosloto.bonus.games.repo.impl;

import io.r2dbc.spi.Row;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.BonusGameType;
import org.orglot.bonus.games.model.puzzle.Puzzle;
import org.orglot.gosloto.bonus.games.repo.PuzzleRepository;
import org.orglot.gosloto.bonus.games.repo.impl.sql.PuzzleRepositorySql;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Objects;

@Slf4j
@Repository
@AllArgsConstructor
public class PuzzleRepositoryImpl implements PuzzleRepository {

  private final DatabaseClient databaseClient;

  @Override
  public Mono<Integer> findById(Long id) {
    return databaseClient.sql(PuzzleRepositorySql.FIND_BY_ID)
        .bind("id", id)
        .map((row, rowMetaData) -> row.get("prize", Integer.class))
        .one();
  }

  /**
   * Получение пазла
   *
   * @param puzzleId идентификатор пазла
   * @return пазл
   */
  @Override
  public Mono<Puzzle> getPuzzle(Long puzzleId) {
    return databaseClient.sql(PuzzleRepositorySql.FIND_BY_ID_QUERY)
        .bind("puzzleId", puzzleId)
        .map((row, rowMetaData) -> toPuzzle(row))
        .one();
  }

  @Override
  public Mono<Integer> update(Long id, String name, String url, String gameType, String rarity, String type, Integer prize) {
    DatabaseClient.GenericExecuteSpec spec = databaseClient.sql(PuzzleRepositorySql.UPDATE_QUERY)
        .bind("name", name)
        .bind("url", url)
        .bind("rarity", rarity)
        .bind("type", type)
        .bind("prize", prize)
        .bind("id", id);

    spec = bindIfNotNull(spec, "game_type", gameType, String.class);

    return spec.fetch().rowsUpdated();
  }

  @Override
  public Flux<Puzzle> getPuzzles(int offset, int limit, String searchString, String rarity) {
    var params = new HashMap<String, Object>();
    var query = new StringBuilder("""
                select
                  p.id, p.name, p.type, p.game_type, pr."type" as rarity, p.url, p.prize
                from
                  "bonus-games".puzzle p
                inner join "bonus-games".puzzle_rarities pr on p.rarity_id = pr.id
                where true=true
                """);

    if (Objects.nonNull(searchString)) {
      query.append(" and (p.name ilike :searchString or p.type ilike :searchString)");
      params.put("searchString", String.format("%%%s%%", searchString));
    }

    if (Objects.nonNull(rarity)) {
      query.append(" and pr.\"type\" = :rarity");
      params.put("rarity", rarity);
    }

    query.append(" order by id desc limit :limit offset :offset");

    var spec = databaseClient.sql(query.toString())
        .bind("offset", offset)
        .bind("limit", limit + 1);
    for (var entry : params.entrySet()) {
      spec = spec.bind(entry.getKey(), entry.getValue());
    }
    return spec.map((row, rowMetaData) -> toPuzzle(row))
        .all();
  }

  /**
   * Сохранение пазлов
   *
   * @param name     название пазла
   * @param url      ссылка на изображение пазла
   * @param gameType тип игры
   * @param rarity название редкости
   * @param type     тип пазла
   * @param prize    количество бонусов за пазл
   * @return результат сохранения
   */
  @Override
  public Mono<Long> save(String name, String url, String gameType, String rarity, String type, Integer prize) {
    DatabaseClient.GenericExecuteSpec spec = databaseClient.sql(PuzzleRepositorySql.CREATE_QUERY)
        .bind("name", name)
        .bind("url", url)
        .bind("rarity", rarity)
        .bind("type", type)
        .bind("prize", prize);

    spec = bindIfNotNull(spec, "game_type", gameType, String.class);

    return spec.map((row, rowMetaData) -> row.get("id", Long.class))
        .one();
  }

  /**
   * Updates a puzzle by its id.
   *
   * @param puzzle the puzzle to be updated
   * @return a Mono of Long
   */
  @Override
  public Mono<Long> updatePuzzleById(Puzzle puzzle) {
    return databaseClient.sql(PuzzleRepositorySql.UPDATE_PUZZLE_BY_ID)
        .bind("id", puzzle.getId())
        .bind("name", puzzle.getName())
        .bind("url", puzzle.getUrl())
        .bind("game_type", puzzle.getGameType())
        .bind("rarity", puzzle.getRarity())
        .bind("type", puzzle.getType())
        .bind("prize", puzzle.getPrize())
        .map((row, rowMetaData) -> row.get("id", Long.class))
        .one();
  }

  private Puzzle toPuzzle(Row puzzle) {
    String typeStr = puzzle.get("game_type", String.class);
    BonusGameType bonusGameType;
    try {
      bonusGameType = BonusGameType.valueOf(typeStr);
    } catch (IllegalArgumentException | NullPointerException e) {
      log.error("Unknown BonusGameType value from DB: '{}', setting to UNKNOWN", typeStr);
      bonusGameType = BonusGameType.UNKNOWN;
    }
    return Puzzle.builder()
        .id(puzzle.get("id", Long.class))
        .name(puzzle.get("name", String.class))
        .type(puzzle.get("type", String.class))
        .gameType(bonusGameType)
        .rarity(puzzle.get("rarity", String.class))
        .url(puzzle.get("url", String.class))
        .prize(puzzle.get("prize", Integer.class))
        .build();
  }

  private <T> DatabaseClient.GenericExecuteSpec bindIfNotNull(DatabaseClient.GenericExecuteSpec spec,
                                                              String param, T value, Class<T> type) {
    if (value != null) {
      return spec.bind(param, value);
    } else {
      return spec.bindNull(param, type);
    }
  }

}
