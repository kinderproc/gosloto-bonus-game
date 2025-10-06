package org.orglot.gosloto.bonus.games.client.operations.main;

import org.orglot.bonus.games.model.request.BonusGameBuy;
import org.orglot.bonus.games.model.request.BonusGameEnd;
import org.orglot.bonus.games.model.request.BonusGamePlay;
import org.orglot.bonus.games.model.response.AvailableBonusGames;
import org.orglot.bonus.games.model.response.BonusGameBuyStatus;
import org.orglot.bonus.games.model.response.BonusGameCollection;
import org.orglot.bonus.games.model.response.BonusGameConfigOrLastSession;
import org.orglot.bonus.games.model.response.BonusGamePlayStatus;
import org.orglot.bonus.games.model.response.BonusGameTransferStatus;
import org.orglot.bonus.games.model.response.GameCompleteResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface BonusGameOperations {
    Flux<String> getBonusGames();

    Mono<AvailableBonusGames> getAvailableBonusGames();

    Mono<BonusGameConfigOrLastSession> getBonusGameConfig(Long userId, String gameType);

    Mono<BonusGameBuyStatus> buy(BonusGameBuy gameBuy);

    @Deprecated
    Mono<BonusGameBuyStatus> buyStatus(UUID sessionUUID, long userId);

    Mono<BonusGamePlayStatus> play(BonusGamePlay gamePlay);

    Mono<BonusGamePlayStatus> playStatus(UUID sessionUUID, long userId);

    Mono<List<BonusGameCollection>> getUserCollection(long userId, String rarityType);

    Mono<GameCompleteResult> bonusGameEnd(BonusGameEnd bonusGameEnd);

    Mono<BonusGameTransferStatus> transferStatus(UUID sessionUUID, long userId);

}
