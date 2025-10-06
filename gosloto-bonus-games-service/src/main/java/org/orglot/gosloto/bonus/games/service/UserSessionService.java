package org.orglot.gosloto.bonus.games.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.BonusGameType;
import org.orglot.gosloto.bonus.games.model.SessionState;
import org.orglot.gosloto.bonus.games.model.UserSession;
import org.orglot.gosloto.bonus.games.repo.UsersSessionsRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.validation.constraints.NotNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionService {
  private final UsersSessionsRepository usersSessionsRepository;

  public Mono<UserSession> findByUUID(UUID uuid) {
    return usersSessionsRepository.findByUUID(uuid);
  }

  public Mono<UserSession> findByUUIDAndUserId(UUID uuid, Long userId) {
    return usersSessionsRepository.findByUUIDAndUserId(uuid, userId);
  }

  public Mono<UserSession> findByUserIdAndGameTypeAndSessionState(long userId,
                                                                  @NonNull BonusGameType gameType,
                                                                  @NonNull List<SessionState> sessionStates) {
    return usersSessionsRepository.findByUserIdAndGameTypeAndSessionState(userId, gameType.name(),
        sessionStates.stream().map(SessionState::name).toList());
  }

  public Mono<UUID> createUserSession(UUID newUUID, String gameType, Long userId, String bet, String sapTransactionId,
                                      List<Integer> prize, String platform, String os) {
    if (Objects.isNull(newUUID) || Objects.isNull(prize) || CollectionUtils.isEmpty(prize.stream().filter(Objects::nonNull).toList()) ||
        Objects.isNull(gameType) || Objects.isNull(userId) || Objects.isNull(bet)) {
      return Mono.empty();
    }
    return usersSessionsRepository.createUserSession(UserSession.builder()
        .uuid(newUUID)
        .gameType(gameType)
        .userId(userId)
        .bet(bet)
        .sapTransactionId(sapTransactionId)
        .prize(prize)
        .os(os)
        .platform(platform)
        .build());
  }

  public Mono<Boolean> updateState(@NotNull UUID uuid, @NotNull SessionState sessionState) {
    return usersSessionsRepository.updateSessionState(uuid, sessionState);
  }

  public Mono<Boolean> playUserSession(UUID uuid) {
    return usersSessionsRepository.updateSessionState(uuid, SessionState.IN_PROGRESS);
  }

  public Mono<Boolean> playUserSession(UUID uuid, Integer prize) {
    return usersSessionsRepository.updateLastPrize(uuid, prize);
  }

  public Mono<Boolean> endUserSession(UUID uuid, List<Integer> prize) {
    return usersSessionsRepository.updatePrizeAndStateAndCompleteDateByUUID(uuid, prize, SessionState.COMPLETED);
  }

  public Mono<Boolean> endUserSession(UUID uuid) {
    return usersSessionsRepository.updateSessionStateAndCompleteDate(uuid, SessionState.COMPLETED);
  }
}
