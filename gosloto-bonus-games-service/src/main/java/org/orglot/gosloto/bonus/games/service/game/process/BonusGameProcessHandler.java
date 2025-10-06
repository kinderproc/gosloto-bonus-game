package org.orglot.gosloto.bonus.games.service.game.process;

import lombok.NonNull;
import org.orglot.bonus.games.model.BonusGameType;
import org.orglot.bonus.games.model.request.BonusGameBuy;
import org.orglot.bonus.games.model.response.BonusGameBuyStatus;
import org.orglot.bonus.games.model.response.BonusGamePlayStatus;
import org.orglot.bonus.games.model.response.GameCompleteResult;
import org.orglot.gosloto.bonus.games.model.UserSession;
import reactor.core.publisher.Mono;

public interface BonusGameProcessHandler {

    /**
     * Поддерживается ли данный тип игры хэндлером
     *
     * @param game - тип игры
     * @return если поддерживается - true, не поддерживается - false
     */
    boolean supportedGame(BonusGameType game);

    Mono<BonusGameBuyStatus> buyGame(BonusGameBuy gameBuy);

    Mono<BonusGamePlayStatus> play(@NonNull UserSession session, Integer modeNumber);

    Mono<GameCompleteResult> completeBonusGame(UserSession userSession,
                                               Boolean isWin,
                                               int score,
                                               int avscore,
                                               String mobile);

}
