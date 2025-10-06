package org.orglot.gosloto.bonus.games.repo;

import lombok.NonNull;
import org.orglot.gosloto.bonus.games.model.SessionState;
import org.orglot.gosloto.bonus.games.model.UserSession;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Repository
public interface UsersSessionsRepository {

    Mono<UserSession> findByUserIdAndGameTypeAndSessionState(long userId,
                                                             @NonNull String gameType,
                                                             @NonNull List<String> sessionStates);

    Mono<UserSession> findByUserIdAndGameTypeAndSessionStateNot(long userId,
                                                                @NonNull String gameType,
                                                                @NonNull List<String> sessionStates);

    Mono<Boolean> updateSessionState(UUID uuid, @NonNull SessionState sessionState);

    Mono<Boolean> updateLastPrize(UUID uuid, Integer lastPrize);

    Mono<Boolean> updateSessionStateAndCompleteDate(UUID uuid, @NonNull SessionState sessionState);

    Mono<UUID> createUserSession(UserSession userSession);

    Mono<UserSession> findByUUID(UUID uuid);

    Mono<UserSession> findByUUIDAndUserId(UUID uuid, Long userId);

    Mono<Boolean> updatePrizeAndStateAndCompleteDateByUUID(UUID uuid, List<Integer> prizes, @NonNull SessionState sessionState);

    Mono<Boolean> updateBet(UUID uuid, @NonNull String bets);

}
