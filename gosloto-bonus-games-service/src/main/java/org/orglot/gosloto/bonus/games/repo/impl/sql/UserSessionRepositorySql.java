package org.orglot.gosloto.bonus.games.repo.impl.sql;

public final class UserSessionRepositorySql {
    public static final String CREATE_USER_SESSION = """
            insert into users_sessions
            (uuid, user_id, game_type, session_state, bet, sap_transaction_id, prize, create_date, update_date, platform, os)
            values
            (:uuid, :user_id, :game_type, :session_state, CAST(:bet AS JSONB), :sap_transaction_id, :prize, now(), now(), :platform, :os)
            """;

    public static final String UPDATE_SESSION_STATE = """
            update users_sessions SET session_state = :session_state WHERE uuid = :uuid
            """;

    public static final String UPDATE_SESSION_STATE_AND_COMPLETE_DATE = """
            update users_sessions SET session_state = :session_state,  complete_date = :complete_date WHERE uuid = :uuid
            """;

    public static final String UPDATE_LAST_PRIZE = """
            update users_sessions SET last_prize = :last_prize WHERE uuid = :uuid
            """;

    public static final String FIND_BY_USER_ID_AND_SESSION_STATE_AND_GAME_TYPE = """
            select * from users_sessions where game_type = :game_type and user_id = :user_id and session_state in(:session_states)
            limit 1
            """;

    public static final String FIND_BY_USER_ID_AND_SESSION_STATE_NOT_AND_GAME_TYPE = """
            select * from users_sessions where game_type = :game_type and user_id = :user_id and not(session_state in(:session_states))
            order by create_date desc
            """;

    public static final String FIND_BY_ID = "select * from users_sessions where uuid = :uuid";

    public static final String FIND_BY_ID_AND_USER_ID = "select * from users_sessions where uuid = :uuid and user_id = :user_id";

    public static final String END_USER_SESSION = """
            update users_sessions set session_state = :session_state, prize = :prize, complete_date = :complete_date where uuid = :uuid
            """;

    public static final String UPDATE_BET = """
            update users_sessions set bets = :bets, where uuid = :uuid
            """;
}
