package org.orglot.gosloto.bonus.games.repo.impl;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.orglot.gosloto.bonus.games.model.SessionState;
import org.orglot.gosloto.bonus.games.model.UserSession;
import org.orglot.gosloto.bonus.games.repo.UsersSessionsRepository;
import org.orglot.gosloto.bonus.games.repo.impl.rowmapper.UserSessionRowMapper;
import org.orglot.gosloto.bonus.games.repo.impl.sql.UserSessionRepositorySql;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Repository
@AllArgsConstructor
public class UserSessionRepositoryImpl implements UsersSessionsRepository {

  private final DatabaseClient databaseClient;

  @Override
  public Mono<UserSession> findByUserIdAndGameTypeAndSessionState(long userId,
                                                                  @NonNull String gameType,
                                                                  @NonNull List<String> sessionStates) {
    return databaseClient.sql(UserSessionRepositorySql.FIND_BY_USER_ID_AND_SESSION_STATE_AND_GAME_TYPE)
        .bind("user_id", userId)
        .bind("game_type", gameType)
        .bind("session_states", sessionStates)
        .map(UserSessionRowMapper.MAPPING_FUNCTION)
        .one();
  }

  @Override
  public Mono<UserSession> findByUserIdAndGameTypeAndSessionStateNot(long userId,
                                                                     @NonNull String gameType,
                                                                     @NonNull List<String> sessionStates) {
    return databaseClient.sql(UserSessionRepositorySql.FIND_BY_USER_ID_AND_SESSION_STATE_NOT_AND_GAME_TYPE)
        .bind("user_id", userId)
        .bind("game_type", gameType)
        .bind("session_states", sessionStates)
        .map(UserSessionRowMapper.MAPPING_FUNCTION)
        .first();
  }

  @Override
  public Mono<Boolean> updateSessionState(UUID uuid, @NonNull SessionState sessionState) {
    return databaseClient.sql(UserSessionRepositorySql.UPDATE_SESSION_STATE)
        .bind("uuid", uuid)
        .bind("session_state", sessionState.name())
        .fetch()
        .rowsUpdated()
        .map(r -> r > 0 ? Boolean.TRUE : Boolean.FALSE)
        .defaultIfEmpty(Boolean.FALSE);
  }

  @Override
  public Mono<Boolean> updateLastPrize(UUID uuid, Integer lastPrize) {
    return databaseClient.sql(UserSessionRepositorySql.UPDATE_LAST_PRIZE)
        .bind("uuid", uuid)
        .bind("last_prize", lastPrize)
        .fetch()
        .rowsUpdated()
        .map(r -> r > 0 ? Boolean.TRUE : Boolean.FALSE)
        .defaultIfEmpty(Boolean.FALSE);
  }

  @Override
  public Mono<Boolean> updateSessionStateAndCompleteDate(UUID uuid, @NonNull SessionState sessionState) {
    return databaseClient.sql(UserSessionRepositorySql.UPDATE_SESSION_STATE_AND_COMPLETE_DATE)
        .bind("uuid", uuid)
        .bind("session_state", sessionState.name())
        .bind("complete_date", Instant.now())
        .fetch()
        .rowsUpdated()
        .map(r -> r > 0 ? Boolean.TRUE : Boolean.FALSE)
        .defaultIfEmpty(Boolean.FALSE);
  }

  @Override
  public Mono<UUID> createUserSession(@NonNull UserSession us) {
    return databaseClient.sql(UserSessionRepositorySql.CREATE_USER_SESSION)
        .bind("uuid", us.getUuid())
        .bind("user_id", us.getUserId())
        .bind("game_type", us.getGameType())
        .bind("session_state", SessionState.START.name())
        .bind("bet", us.getBet())
        .bind("sap_transaction_id", Objects.isNull(us.getSapTransactionId()) ? "" : us.getSapTransactionId())
        .bind("prize", us.getPrize().toString())
        .bind("platform", us.getPlatform())
        .bind("os", Objects.isNull(us.getOs()) ? "" : us.getOs())
        .fetch()
        .rowsUpdated()
        .map(r -> us.getUuid());
  }

  @Override
  public Mono<UserSession> findByUUID(UUID uuid) {
    return databaseClient.sql(UserSessionRepositorySql.FIND_BY_ID)
        .bind("uuid", uuid)
        .map(UserSessionRowMapper.MAPPING_FUNCTION)
        .one();
  }

  @Override
  public Mono<UserSession> findByUUIDAndUserId(UUID uuid, Long userId) {
    return databaseClient.sql(UserSessionRepositorySql.FIND_BY_ID_AND_USER_ID)
        .bind("uuid", uuid)
        .bind("user_id", userId)
        .map(UserSessionRowMapper.MAPPING_FUNCTION)
        .one();
  }

  @Override
  public Mono<Boolean> updatePrizeAndStateAndCompleteDateByUUID(UUID uuid, List<Integer> prize, @NonNull SessionState sessionState) {
    return databaseClient.sql(UserSessionRepositorySql.END_USER_SESSION)
        .bind("uuid", uuid)
        .bind("session_state", sessionState.name())
        .bind("complete_date", Instant.now())
        .bind("prize", prize.toString())
        .fetch()
        .rowsUpdated()
        .map(r -> r > 0 ? Boolean.TRUE : Boolean.FALSE)
        .defaultIfEmpty(Boolean.FALSE);
  }

  @Override
  public Mono<Boolean> updateBet(UUID uuid, @NonNull String bets) {
    return databaseClient.sql(UserSessionRepositorySql.UPDATE_BET)
        .bind("uuid", uuid)
        .bind("bets", bets)
        .fetch()
        .rowsUpdated()
        .map(r -> r > 0 ? Boolean.TRUE : Boolean.FALSE)
        .defaultIfEmpty(Boolean.FALSE);
  }

}
