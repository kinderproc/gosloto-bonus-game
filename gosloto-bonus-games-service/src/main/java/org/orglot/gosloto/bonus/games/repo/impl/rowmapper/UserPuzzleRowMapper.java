package org.orglot.gosloto.bonus.games.repo.impl.rowmapper;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.orglot.gosloto.bonus.games.model.UserPuzzle;
import org.orglot.gosloto.bonus.games.model.UserPuzzleWithCollectedCount;

import java.time.Instant;
import java.util.function.BiFunction;

public class UserPuzzleRowMapper {

  public static final BiFunction<Row, RowMetadata, UserPuzzle> MAPPING_FUNCTION = (row, rowMetaData) -> UserPuzzle.builder()
      .id(row.get("id", Long.class))
      .puzzleId(row.get("puzzle_id", Long.class))
      .userId(row.get("user_id", Long.class))
      .collected(row.get("collected", Boolean.class))
      .collectedCount(row.get("collected_count", Integer.class))
      .exchangeDate(row.get("exchange_date", Instant.class))
      .build();

  public static final BiFunction<Row, RowMetadata, UserPuzzleWithCollectedCount> MAPPING_FUNCTION_WITH_COLLECTED_COUNT =
      (row, rowMetaData) -> UserPuzzleWithCollectedCount.builder()
          .name(row.get("name", String.class))
          .url(row.get("url", String.class))
          .rarity(row.get("rarity_type", String.class))
          .type(row.get("type", String.class))
          .collectedCount(row.get("collected_count", Integer.class))
          .build();
}
