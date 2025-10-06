package org.orglot.gosloto.bonus.games.service.game.process.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.orglot.bonus.games.model.request.BonusGameBuy;
import org.orglot.bonus.games.model.response.BonusGameBuyStatus;
import org.orglot.bonus.games.model.response.BonusGamePlayStatus;
import org.orglot.bonus.games.model.response.PlayStatusResponse;
import org.orglot.gosloto.bonus.games.model.UserSession;
import org.orglot.gosloto.bonus.games.service.DefaultBuyService;
import org.orglot.gosloto.bonus.games.service.UserSessionService;
import org.orglot.gosloto.bonus.games.service.game.process.BonusGameProcessHandler;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public abstract class AbstractBonusGameProcessHandler implements BonusGameProcessHandler {

  protected final UserSessionService userSessionService;
  protected final DefaultBuyService defaultBuyService;

  @Override
  public Mono<BonusGameBuyStatus> buyGame(BonusGameBuy gameBuy) {
    return defaultBuyService.buyGame(gameBuy, gameBuy.getPrice(), true);
  }

  @Override
  public Mono<BonusGamePlayStatus> play(@NonNull UserSession session, Integer modeNumber) {
    return switch (session.getSessionState()) {
      case COMPLETED -> Mono.just(new BonusGamePlayStatus(PlayStatusResponse.SUCCESS, session.getUuid()));
      case EXPIRED -> Mono.just(new BonusGamePlayStatus(PlayStatusResponse.ERROR, session.getUuid()));
      case IN_PROGRESS -> Mono.just(new BonusGamePlayStatus(PlayStatusResponse.IN_PROGRESS, session.getPrize(), session.getUuid()));
      default -> userSessionService.playUserSession(session.getUuid())
          .thenReturn(new BonusGamePlayStatus(PlayStatusResponse.IN_PROGRESS, session.getPrize(), session.getUuid()));
    };
  }
}
