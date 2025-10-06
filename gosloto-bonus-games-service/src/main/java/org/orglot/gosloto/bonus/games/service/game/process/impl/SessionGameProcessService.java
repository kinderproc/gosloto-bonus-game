package org.orglot.gosloto.bonus.games.service.game.process.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.orglot.bonus.games.model.BonusGameSpecType;
import org.orglot.bonus.games.model.BonusGameType;
import org.orglot.bonus.games.model.request.BonusGameBuy;
import org.orglot.bonus.games.model.response.BonusGameBuyStatus;
import org.orglot.bonus.games.model.response.BonusGamePlayStatus;
import org.orglot.bonus.games.model.response.GameCompleteResult;
import org.orglot.bonus.games.model.response.PlayStatusResponse;
import org.orglot.gosloto.bonus.client.operation.bonusrefill.model.BonusRefillReason;
import org.orglot.gosloto.bonus.games.model.SessionState;
import org.orglot.gosloto.bonus.games.model.UserSession;
import org.orglot.gosloto.bonus.games.service.BonusGameSettingsService;
import org.orglot.gosloto.bonus.games.service.ConsumableJsonParser;
import org.orglot.gosloto.bonus.games.service.DefaultBuyService;
import org.orglot.gosloto.bonus.games.service.PurchaseService;
import org.orglot.gosloto.bonus.games.service.RewardService;
import org.orglot.gosloto.bonus.games.service.SessionGameBuyService;
import org.orglot.gosloto.bonus.games.service.UserSessionService;
import org.orglot.gosloto.components.log.LogRepository;
import org.orglot.gosloto.components.log.message.NewBonusGameSessionMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static org.orglot.bonus.games.model.response.GameCompleteResult.REFILL_ERROR;
import static org.orglot.gosloto.bonus.games.service.PrizeService.NO_WIN;

@Slf4j
@Service
public class SessionGameProcessService extends AbstractBonusGameProcessHandler {

  private final PurchaseService purchaseService;
  private final LogRepository logRepository;
  private final RewardService rewardService;
  private final BonusGameSettingsService bonusGameSettingsService;
  private final ConsumableJsonParser consumableJsonParser;
  private final SessionGameBuyService sessionGameBuyService;

  public SessionGameProcessService(UserSessionService userSessionService,
                                   PurchaseService purchaseService,
                                   LogRepository logRepository,
                                   RewardService rewardService,
                                   BonusGameSettingsService bonusGameSettingsService,
                                   DefaultBuyService defaultBuyService,
                                   ConsumableJsonParser consumableJsonParser,
                                   SessionGameBuyService sessionGameBuyService) {
    super(userSessionService, defaultBuyService);
    this.purchaseService = purchaseService;
    this.logRepository = logRepository;
    this.rewardService = rewardService;
    this.bonusGameSettingsService = bonusGameSettingsService;
    this.consumableJsonParser = consumableJsonParser;
    this.sessionGameBuyService = sessionGameBuyService;
  }

  @Override
  public boolean supportedGame(BonusGameType game) {
    return BonusGameSpecType.SESSION.equals(bonusGameSettingsService.getSpecType(game));
  }

  @Override
  public Mono<GameCompleteResult> completeBonusGame(@NonNull UserSession userSession, Boolean isWin, int score, int avscore,
                                                    String mobile) {
    if (userSession.getSessionState() == SessionState.COMPLETED || userSession.getSessionState() == SessionState.EXPIRED) {
      int total = (userSession.getPrize() == null) ? NO_WIN : userSession.getPrize().stream().mapToInt(Integer::intValue).sum();
        return Mono.just(GameCompleteResult.builder()
          .totalPrize(total)
          .status(PlayStatusResponse.SUCCESS)
          .build());
    }

    var prizeSum = (userSession.getPrize() == null) ? 0 : userSession.getPrize().stream().mapToInt(Integer::intValue).sum();
    var reward = rewardService.getAndApplyRewards(userSession, mobile);
    if (prizeSum > 0) {
      return Mono.fromCallable(() ->
        purchaseService.refillBonus(prizeSum, BonusRefillReason.ZBONUS_SHOP.name(), mobile,
          userSession.getUuid(), null, null, userSession.getGameType()))
          .filter(refillSuccess -> refillSuccess)
          .flatMap(refillSuccess -> userSessionService.endUserSession(userSession.getUuid()))
          .doOnNext(endUserSession ->
            logRepository.log(new NewBonusGameSessionMessage(
              userSession.getGameType(), null, prizeSum,
              NewBonusGameSessionMessage.SessionState.COMPLETED
            ))
          )
            .flatMap(result -> reward.map(rewards -> GameCompleteResult.builder()
                .rewards(rewards)
                .totalPrize(prizeSum)
                .status(PlayStatusResponse.SUCCESS)
                .build()))
            .switchIfEmpty(Mono.just(GameCompleteResult.error(REFILL_ERROR)))
            .doOnError(error -> log.error("Error when completeBonusGame with prize: {}", error.getMessage()));
      } else {
        return userSessionService.endUserSession(userSession.getUuid())
          .doOnNext(endUserSession ->
            logRepository.log(new NewBonusGameSessionMessage(
              userSession.getGameType(), null, prizeSum,
              NewBonusGameSessionMessage.SessionState.COMPLETED
            ))
          )
          .flatMap(result -> reward.map(rewards -> GameCompleteResult.builder()
            .rewards(rewards)
            .totalPrize(prizeSum)
            .status(PlayStatusResponse.SUCCESS)
            .build()))
          .doOnError(error -> log.error("Error when completeBonusGame: {}", error.getMessage()));
      }
  }

  @Override
  public Mono<BonusGamePlayStatus> play(@NonNull UserSession session, Integer modeNumber) {
      return super.play(session, modeNumber)
          .flatMap(status ->
              consumableJsonParser.parseOrEmptyReactive(session.getBet())
                  .map(consumables -> {
                      status.setConsumables(consumables);
                      return status;
                  })
          );
  }

  @Override
  public Mono<BonusGameBuyStatus> buyGame(BonusGameBuy gameBuy) {
    return sessionGameBuyService.buyGame(gameBuy, gameBuy.getPrice(), true);
  }
}
