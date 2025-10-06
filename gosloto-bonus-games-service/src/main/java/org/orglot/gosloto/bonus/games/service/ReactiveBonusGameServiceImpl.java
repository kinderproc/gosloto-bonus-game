package org.orglot.gosloto.bonus.games.service;

import com.google.protobuf.NullValue;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.BonusGameType;
import org.orglot.bonus.games.model.request.BonusGameBuy;
import org.orglot.bonus.games.model.response.BonusGameBuyStatus;
import org.orglot.bonus.games.model.response.BonusGameConfigOrLastSession;
import org.orglot.bonus.games.model.response.BonusGamePlayStatus;
import org.orglot.bonus.games.model.response.BonusGameTransferStatus;
import org.orglot.bonus.games.model.response.GameCompleteResult;
import org.orglot.bonus.games.model.response.PlayStatusResponse;
import org.orglot.bonus.games.model.response.PurchaseStatus;
import org.orglot.gosloto.bonus.games.grpc.GameTypeResponseGrpc;
import org.orglot.gosloto.bonus.games.grpc.model.NullableInteger;
import org.orglot.gosloto.bonus.games.grpc.model.NullableString;
import org.orglot.gosloto.bonus.games.model.SessionState;
import org.orglot.gosloto.bonus.games.model.UserSession;
import org.orglot.gosloto.bonus.games.service.game.process.BonusGameProcessHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.orglot.gosloto.bonus.games.service.PrizeService.NO_WIN;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReactiveBonusGameServiceImpl implements ReactiveBonusGameService {
  private static final List<BonusGameType> AVAILABLE_GAMES = Arrays.stream(BonusGameType.values()).toList();

  private final UserSessionService userSessionService;
  private final ModeGameConfigService modeGameConfigService;
  private final List<BonusGameProcessHandler> bonusGameProcessHandlers;
  private final BonusGameSettingsService bonusGameSettingsService;
  private final ConsumableJsonParser consumableJsonParser;

  public Flux<GameTypeResponseGrpc> getAvailableBonusGames() {
    return Flux.fromIterable(AVAILABLE_GAMES)
        .map(gameType -> {
          var gameConfig = bonusGameSettingsService.getByGameType(gameType.name());
          return GameTypeResponseGrpc.newBuilder()
              .setGameType(gameType.name())
              .setVisible(gameConfig.isVisible())
              .setSmallIcon(Objects.isNull(gameConfig.getGraphic()) || Objects.isNull(gameConfig.getGraphic().getSmallIcon()) ?
                  NullableString.newBuilder().setNull(NullValue.NULL_VALUE).build() :
                  NullableString.newBuilder().setValue(gameConfig.getGraphic().getSmallIcon()).build())
              .setBigIcon(Objects.isNull(gameConfig.getGraphic()) || Objects.isNull(gameConfig.getGraphic().getBigIcon()) ?
                  NullableString.newBuilder().setNull(NullValue.NULL_VALUE).build() :
                  NullableString.newBuilder().setValue(gameConfig.getGraphic().getBigIcon()).build())
              .setSmallHover(Objects.isNull(gameConfig.getGraphic()) || Objects.isNull(gameConfig.getGraphic().getSmallHover()) ?
                  NullableString.newBuilder().setNull(NullValue.NULL_VALUE).build() :
                  NullableString.newBuilder().setValue(gameConfig.getGraphic().getSmallHover()).build())
              .setGameUrl(Objects.isNull(gameConfig.getGameUrl()) ?
                  NullableString.newBuilder().setNull(NullValue.NULL_VALUE).build() :
                  NullableString.newBuilder().setValue(gameConfig.getGameUrl()).build())
              .setMpIcon(Objects.isNull(gameConfig.getGraphic()) || Objects.isNull(gameConfig.getGraphic().getMpIcon()) ?
                  NullableString.newBuilder().setNull(NullValue.NULL_VALUE).build() :
                  NullableString.newBuilder().setValue(gameConfig.getGraphic().getMpIcon()).build())
              .setTitle(Objects.isNull(gameConfig.getTitle()) ?
                  NullableString.newBuilder().setNull(NullValue.NULL_VALUE).build() :
                  NullableString.newBuilder().setValue(gameConfig.getTitle()).build())
              .setRules(Objects.isNull(gameConfig.getRules()) ?
                  NullableString.newBuilder().setNull(NullValue.NULL_VALUE).build() :
                  NullableString.newBuilder().setValue(gameConfig.getRules()).build())
              .setGameSpec(gameConfig.getGameSpec())
              .setOrder(gameConfig.getOrder())
              .setPrize(Objects.isNull(gameConfig.getPrize()) ?
                  NullableInteger.newBuilder().setNull(NullValue.NULL_VALUE).build() :
                  NullableInteger.newBuilder().setValue(gameConfig.getPrize()).build())
              .setCurrency(Objects.isNull(gameConfig.getCurrency()) ?
                  NullableInteger.newBuilder().setNull(NullValue.NULL_VALUE).build() :
                  NullableInteger.newBuilder().setValue(gameConfig.getCurrency()).build())
              .setPuzzle(Objects.isNull(gameConfig.getPuzzle()) ?
                  NullableInteger.newBuilder().setNull(NullValue.NULL_VALUE).build() :
                  NullableInteger.newBuilder().setValue(gameConfig.getPuzzle()).build())
              .setPuzzleColor(Objects.isNull(gameConfig.getPuzzleColor()) ?
                  NullableString.newBuilder().setNull(NullValue.NULL_VALUE).build() :
                  NullableString.newBuilder().setValue(gameConfig.getPuzzleColor()).build())
              .setPrice(Objects.isNull(gameConfig.getPrice()) ?
                  NullableInteger.newBuilder().setNull(NullValue.NULL_VALUE).build() :
                  NullableInteger.newBuilder().setValue(gameConfig.getPrice()).build())
              .build();
        })
        .doOnError(e -> log.error("Error when get config for bonus game. Error {}", e.getMessage()))
        .onErrorContinue((e, i) -> Mono.empty());
  }

  /**
   * Получение активной сессии пользователя по типу игры,
   * в случае ее отсутствия - всех возможных ставок игры (режимов, доступных пазлов и т.п.)
   *
   * @param gameType - название игры
   * @param userId   - идентификатор пользователя
   * @return UUID активной сессии или возможные ставки игры
   */
  public Mono<BonusGameConfigOrLastSession> getBonusGameConfigOrSessionUUID(@NonNull String gameType, Long userId) {
    return userSessionService.findByUserIdAndGameTypeAndSessionState(userId, BonusGameType.valueOf(gameType),
            List.of(SessionState.START, SessionState.IN_PROGRESS))
        .flatMap(userSession -> {
          BonusGameType bonusGameType = BonusGameType.valueOf(userSession.getGameType());
          if (isUserSessionExpired(userSession)) {
            return processExpiredUserSession(userSession.getUuid(),
                bonusGameType);
          }
          return processExistAndNotExpiredUserSession(userSession, bonusGameType);
        })
        .switchIfEmpty(Mono.defer(() -> buildWithGameConfig(gameType)))
        .doOnError(error -> log.error("Error when getBonusGameConfigOrSessionUUID: {}", error.getMessage()));
  }

  private Mono<BonusGameConfigOrLastSession> buildWithGameConfig(String gameType) {
    return modeGameConfigService.getGameConfig(BonusGameType.valueOf(gameType))
        .map(BonusGameConfigOrLastSession::buildWithGameConfig);
  }

  private Mono<BonusGameConfigOrLastSession> processExpiredUserSession(UUID uuid, BonusGameType game) {
    return userSessionService.updateState(uuid, SessionState.EXPIRED)
        .then(buildWithGameConfig(game.name()));
  }

  private Mono<BonusGameConfigOrLastSession> processExistAndNotExpiredUserSession(UserSession userSession,
                                                                                  BonusGameType game) {
    var bet = BetDataService.fromJsonBetData(userSession.getBet(), BetDataService.BET_DATA_TYPE_REF);
    var mode = BetDataService.getIntFromJsonString(bet.get(BetDataService.MODE));
    var price = BetDataService.getIntFromJsonString(bet.get(BetDataService.PRICE));
    var scale = bonusGameSettingsService.getScale(userSession.getGameType(), price);
    var consumables = consumableJsonParser.parseOrEmpty(userSession.getBet());
    return modeGameConfigService.getGameConfig(game)
        .map(modeTypeGameConfig -> BonusGameConfigOrLastSession.buildWithSessionUUID(
                userSession.getUuid(),
                mode,
                price,
                scale,
                userSession.getPrize(),
                modeTypeGameConfig,
                consumables
            )
        );
  }

  public Mono<BonusGameBuyStatus> buyGame(BonusGameBuy gameBuy) {
    log.debug("buyGame: gameBuy={}", gameBuy);
    return Mono.just(BonusGameType.valueOf(gameBuy.getGameType()))
        .flatMap(game -> {
          var handler = bonusGameProcessHandlers.stream()
              .filter(h -> h.supportedGame(game))
              .findFirst()
              .orElseThrow(() -> new RuntimeException("Game type is not supported" + game));
          return handler.buyGame(gameBuy);
        })
        .defaultIfEmpty(BonusGameBuyStatus.error("session not found"))
        .doOnError(error -> log.error("Error when buyGame: {}", error.getMessage()));
  }

  @Deprecated
  public Mono<BonusGameBuyStatus> buyStatus(UUID sessionUUID, Long userId) {
    return userSessionService.findByUUIDAndUserId(sessionUUID, userId)
        .map(userSession -> new BonusGameBuyStatus(PurchaseStatus.SUCCESS, userSession.getUuid()))
        .defaultIfEmpty(BonusGameBuyStatus.error("session not found"))
        .doOnError(error -> log.error("Error when buyStatus: {}", error.getMessage()));
  }

  private Mono<BonusGamePlayStatus> play(UserSession session) {
    return switch (session.getSessionState()) {
      case COMPLETED -> Mono.just(new BonusGamePlayStatus(PlayStatusResponse.SUCCESS, session.getUuid()));
      case EXPIRED -> Mono.just(new BonusGamePlayStatus(PlayStatusResponse.ERROR, session.getUuid()));
      case IN_PROGRESS ->
          Mono.just(new BonusGamePlayStatus(PlayStatusResponse.IN_PROGRESS, session.getPrize(), session.getUuid()));
      default -> userSessionService.playUserSession(session.getUuid())
          .thenReturn(new BonusGamePlayStatus(PlayStatusResponse.IN_PROGRESS, session.getPrize(), session.getUuid()));
    };
  }

  public Mono<BonusGamePlayStatus> play(@NonNull UUID sessionUUID, @NonNull Long userId, Integer modeNumber) {
    log.debug("playBonusGame: sessionUUID={}, userId={}", sessionUUID, userId);
    return userSessionService.findByUUIDAndUserId(sessionUUID, userId)
        .flatMap(userSession -> {
          var game = BonusGameType.valueOf(userSession.getGameType());
          var handler = bonusGameProcessHandlers.stream()
              .filter(h -> h.supportedGame(game))
              .findFirst()
              .orElseThrow(() -> new RuntimeException("Game type is not supported" + game));
          return handler.play(userSession, modeNumber);
        })
        .defaultIfEmpty(BonusGamePlayStatus.error("session not found"))
        .doOnError(error -> log.error("Error when playBonusGame: {}", error.getMessage()));
  }

  public Mono<BonusGamePlayStatus> playStatus(@NonNull UUID sessionUUID, @NonNull Long userId) {
    return userSessionService.findByUUIDAndUserId(sessionUUID, userId)
        .flatMap(this::play)
        .defaultIfEmpty(new BonusGamePlayStatus(PlayStatusResponse.ERROR, "session not found"));
  }

  public Mono<GameCompleteResult> completeBonusGame(@NonNull UUID sessionUUID,
                                                    @NonNull Long userId,
                                                    Boolean isWin,
                                                    int score,
                                                    int avscore,
                                                    String mobile) {
    log.debug("completeBonusGame: sessionUUID={}, userId={}, isWin={}", sessionUUID, userId, isWin);
    return userSessionService.findByUUIDAndUserId(sessionUUID, userId)
        .flatMap(userSession -> {
          var game = BonusGameType.valueOf(userSession.getGameType());
          if (Objects.isNull(userSession.getPrize())) {
            log.warn("Prize is null for session uuid {}, set no win", sessionUUID);
            userSession.setPrize(List.of(NO_WIN));
          }
          var handler = bonusGameProcessHandlers.stream()
              .filter(h -> h.supportedGame(game))
              .findFirst()
              .orElseThrow(() -> new RuntimeException("Game type is not supported" + game));
          return handler.completeBonusGame(userSession, isWin, score, avscore, mobile);
        })
        .defaultIfEmpty(GameCompleteResult.error("session not found"))
        .doOnError(error -> log.error("Error when completeBonusGame: {}", error.getMessage()));
  }

  @Deprecated
  public Mono<BonusGameTransferStatus> transferStatus(UUID uuid, Long userId) {
    return userSessionService.findByUUID(uuid)
        .map(userSession -> {
          if (!userSession.getUserId().equals(userId)) {
            return new BonusGameTransferStatus(PurchaseStatus.ERROR, "user");
          }
          return new BonusGameTransferStatus(PurchaseStatus.SUCCESS);
        })
        .defaultIfEmpty(new BonusGameTransferStatus(PurchaseStatus.ERROR, "session not found"))
        .doOnError(error -> log.error("Error when transferStatus: {}", error.getMessage()));
  }

  private Boolean isUserSessionExpired(UserSession userSession) {
    return LocalDateTime.from(userSession.getCreateDate().atZone(ZoneId.systemDefault()))
        .plusDays(30)
        .isBefore(LocalDateTime.now());
  }

}
