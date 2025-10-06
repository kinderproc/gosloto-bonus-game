package org.orglot.gosloto.bonus.games.repo.impl.rowmapper;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.orglot.gosloto.bonus.games.model.PuzzleItemWithSessionCreateDate;

import java.time.Instant;
import java.util.Objects;
import java.util.function.BiFunction;

public class PuzzleItemWithSessionCreateDateRowMapper {

  public static final BiFunction<Row, RowMetadata, PuzzleItemWithSessionCreateDate> MAPPING_FUNCTION = (row, rowMetaData) ->
      PuzzleItemWithSessionCreateDate.builder()
          .id(row.get("id", Long.class))
          .url(row.get("url", String.class))
          .position(row.get("position", Integer.class))
          .puzzleId(row.get("puzzle_id", Long.class))
          .puzzleGameType(row.get("puzzle_game_type", String.class))
          .puzzleUrl(row.get("puzzle_url", String.class))
          .puzzleName(row.get("puzzle_name", String.class))
          .puzzleRarity(row.get("puzzle_rarity", String.class))
          .puzzleType(row.get("puzzle_type", String.class))
          .puzzlePrize(row.get("puzzle_prize", Integer.class))
          .userPuzzleCollectedCount(getInt(row.get("collected_count", Integer.class)))
          .userPuzzleCollected(getBoolean(row.get("collected", Boolean.class)))
          .createDate(row.get("create_date", Instant.class))
          .build();

  private static boolean getBoolean(Boolean value) {
    return !Objects.isNull(value) && value;
  }

  private static int getInt(Integer value) {
    return Objects.isNull(value) ? 0 : value;
  }
}
