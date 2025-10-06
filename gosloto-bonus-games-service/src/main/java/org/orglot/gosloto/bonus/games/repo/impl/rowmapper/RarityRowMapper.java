package org.orglot.gosloto.bonus.games.repo.impl.rowmapper;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.orglot.gosloto.bonus.games.model.Rarity;

import java.util.function.BiFunction;

public class RarityRowMapper {

  public static final BiFunction<Row, RowMetadata, Rarity> MAPPING_FUNCTION = (row, rowMetaData) -> Rarity.builder()
      .id(row.get("id", Long.class))
      .type(row.get("type", String.class))
      .title(row.get("title", String.class))
      .display(Boolean.TRUE.equals(row.get("display", Boolean.class)))
      .build();

}
