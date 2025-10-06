package org.orglot.gosloto.bonus.games.repo.impl.rowmapper;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.orglot.gosloto.bonus.games.model.SessionState;
import org.orglot.gosloto.bonus.games.model.UserSession;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

public class UserSessionRowMapper {

    public static final BiFunction<Row, RowMetadata, UserSession> MAPPING_FUNCTION = (row, rowMetaData) -> UserSession.builder()
        .uuid(row.get("uuid", UUID.class))
        .userId(row.get("user_id", Long.class))
        .gameType(row.get("game_type", String.class))
        .sessionState(getSessionState(row.get("session_state", String.class)))
        .createDate(row.get("create_date", Instant.class))
        .updateDate(row.get("update_date", Instant.class))
        .bet(row.get("bet", String.class))
        .lastPrize(row.get("last_prize", Integer.class))
        .prize(getPrizeList(row.get("prize", String.class)))
        .build();

    private static final String REGEX_ARRAY_FORMAT = "[\\[,\\]]+";

    private static List<Integer> getPrizeList(String prize) {
        if (Objects.isNull(prize)) {
            return null;
        }
        List<Integer> values = new ArrayList<>();
        prize = prize.replaceAll(REGEX_ARRAY_FORMAT, "");
        var prizes = prize.split("\\s+");
        for (String s : prizes) {
            values.add(Integer.valueOf(s));
        }
        return values;
    }

    private static SessionState getSessionState(String sessionState) {
        return Optional.ofNullable(sessionState)
            .filter(s -> SessionState.getStringValues().contains(s))
            .map(SessionState::valueOf)
            .orElse(null);
    }
}
