package org.orglot.gosloto.bonus.games.repo.impl.rowmapper;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.orglot.bonus.games.model.puzzle.PuzzleItem;

import java.util.function.BiFunction;

public class PuzzleItemRowMapper {

  public static final BiFunction<Row, RowMetadata, PuzzleItem> MAPPING_FUNCTION = (row, rowMetaData) ->
      PuzzleItem.builder()
          .id(row.get("id", Long.class))
          .puzzleId(row.get("puzzle_id", Long.class))
          .position(row.get("position", Integer.class))
          .url(row.get("url", String.class))
          .puzzleName(row.get("puzzle_name", String.class))
          .build();

}
