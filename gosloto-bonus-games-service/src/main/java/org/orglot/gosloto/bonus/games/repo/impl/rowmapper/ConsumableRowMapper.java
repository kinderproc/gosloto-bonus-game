package org.orglot.gosloto.bonus.games.repo.impl.rowmapper;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.experimental.UtilityClass;
import org.orglot.gosloto.bonus.games.model.ConsumableEntity;

import java.util.UUID;
import java.util.function.BiFunction;

@UtilityClass
public class ConsumableRowMapper {

    public BiFunction<Row, RowMetadata, ConsumableEntity> MAPPING_FUNCTION = (row, rowMetadata) -> ConsumableEntity.builder()
        .id(row.get("id", UUID.class))
        .type(row.get("type", Integer.class))
        .bonusGameType(row.get("bonus_game_type", String.class))
        .name(row.get("name", String.class))
        .price(row.get("price", Long.class))
        .available(row.get("available", Boolean.class))
        .weight(row.get("weight", Integer.class))
        .iconUrl(row.get("icon_url", String.class))
        .build();
}
