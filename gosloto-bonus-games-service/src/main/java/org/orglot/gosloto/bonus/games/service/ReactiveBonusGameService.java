package org.orglot.gosloto.bonus.games.service;

import lombok.NonNull;
import org.orglot.bonus.games.model.request.BonusGameBuy;
import org.orglot.bonus.games.model.response.BonusGameBuyStatus;
import org.orglot.bonus.games.model.response.BonusGameConfigOrLastSession;
import org.orglot.bonus.games.model.response.BonusGamePlayStatus;
import org.orglot.bonus.games.model.response.GameCompleteResult;
import org.orglot.gosloto.bonus.games.grpc.GameTypeResponseGrpc;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReactiveBonusGameService {

  Flux<GameTypeResponseGrpc> getAvailableBonusGames();

  Mono<BonusGameConfigOrLastSession> getBonusGameConfigOrSessionUUID(@NonNull String gameType, Long userId);

  Mono<BonusGameBuyStatus> buyGame(BonusGameBuy gameBuy);

  Mono<BonusGameBuyStatus> buyStatus(UUID sessionUUID, Long userId);

  Mono<BonusGamePlayStatus> play(@NonNull UUID sessionUUID, @NonNull Long userId, Integer modeNumber);

  Mono<BonusGamePlayStatus> playStatus(@NonNull UUID sessionUUID, @NonNull Long userId);

  Mono<GameCompleteResult> completeBonusGame(@NonNull UUID sessionUUID,
                                             @NonNull Long userId,
                                             Boolean isWin,
                                             int score,
                                             int avscore,
                                             String mobile);
}
