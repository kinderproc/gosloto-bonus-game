package org.orglot.gosloto.bonus.games.service.game.process.impl;

import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.BonusGameSpecType;
import org.orglot.bonus.games.model.BonusGameType;
import org.orglot.bonus.games.model.request.BonusGameBuy;
import org.orglot.bonus.games.model.response.BonusGameBuyStatus;
import org.orglot.bonus.games.model.response.GameCompleteResult;
import org.orglot.bonus.games.model.response.PlayStatusResponse;
import org.orglot.gosloto.bonus.client.operation.bonusrefill.model.BonusRefillReason;
import org.orglot.gosloto.bonus.games.model.SessionState;
import org.orglot.gosloto.bonus.games.model.UserSession;
import org.orglot.gosloto.bonus.games.service.BetDataService;
import org.orglot.gosloto.bonus.games.service.BonusGameSettingsService;
import org.orglot.gosloto.bonus.games.service.DefaultBuyService;
import org.orglot.gosloto.bonus.games.service.PurchaseService;
import org.orglot.gosloto.bonus.games.service.RewardService;
import org.orglot.gosloto.bonus.games.service.UserSessionService;
import org.orglot.gosloto.components.log.LogRepository;
import org.orglot.gosloto.components.log.message.NewBonusGameSessionMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static org.orglot.bonus.games.model.response.GameCompleteResult.REFILL_ERROR;
import static org.orglot.gosloto.bonus.games.service.PrizeService.NO_WIN;

@Slf4j
@Service
public class PrizeGameProcessService extends AbstractBonusGameProcessHandler {

  private final BonusGameSettingsService bonusGameSettingsService;
  private final PurchaseService purchaseService;
  private final LogRepository logRepository;
  private final RewardService rewardService;

  public PrizeGameProcessService(UserSessionService userSessionService,
                                 BonusGameSettingsService bonusGameSettingsService,
                                 PurchaseService purchaseService,
                                 LogRepository logRepository,
                                 RewardService rewardService,
                                 DefaultBuyService defaultBuyService) {
    super(userSessionService, defaultBuyService);
    this.bonusGameSettingsService = bonusGameSettingsService;
    this.purchaseService = purchaseService;
    this.logRepository = logRepository;
    this.rewardService = rewardService;
  }

  @Override
  public boolean supportedGame(BonusGameType game) {
    return BonusGameSpecType.PRIZE.equals(bonusGameSettingsService.getSpecType(game));
  }

  @Override
  public Mono<BonusGameBuyStatus> buyGame(BonusGameBuy gameBuy) {
    return defaultBuyService.buyGame(gameBuy, gameBuy.getPrice(), false);
  }

  @Override
  public Mono<GameCompleteResult> completeBonusGame(UserSession userSession, Boolean isWin, int score, int avscore, String mobile) {
    return Mono.just(userSession)
        .filter(session -> !List.of(SessionState.COMPLETED, SessionState.EXPIRED).contains(session.getSessionState()))
        .flatMap(session -> {
              if (Objects.isNull(isWin) && avscore == 0) {
                return Mono.just(GameCompleteResult.error("avscore is null"));
              }
              var bet = BetDataService.fromJsonBetData(userSession.getBet(), BetDataService.BET_DATA_TYPE_REF);
              var price = BetDataService.getIntFromJsonString(bet.get(BetDataService.PRICE));
              return purchaseService.consumeAttempt(userSession.getUserId(), price)
                  .filter(result -> result)
                  .flatMap(result -> {
                    var reward = rewardService.getAndApplyRewards(userSession, mobile);
                    int prizeSum = calculateTotalPrize(userSession, isWin, score, avscore);
                    if (prizeSum > 0) {
                      return Mono.fromCallable(() ->
                              purchaseService.refillBonus(prizeSum, BonusRefillReason.ZBONUS_SHOP.name(), mobile,
                                  userSession.getUuid(), null, null, session.getGameType()))
                          .filter(refillSuccess -> refillSuccess)
                          .flatMap(refillSuccess -> userSessionService.endUserSession(session.getUuid(), userSession.getPrize()))
                          .doOnNext(endUserSession ->
                              logRepository.log(new NewBonusGameSessionMessage(session.getGameType(), null, prizeSum,
                                  NewBonusGameSessionMessage.SessionState.COMPLETED))
                          )
                          .flatMap(r -> reward.map(rewards -> GameCompleteResult.builder()
                              .rewards(rewards)
                              .totalPrize(prizeSum)
                              .status(PlayStatusResponse.SUCCESS)
                              .build()))
                          .switchIfEmpty(Mono.just(GameCompleteResult.error(REFILL_ERROR)))
                          .doOnError(error -> log.error("Error when completeBonusGame with prize: {}", error.getMessage()));
                    } else {
                      return userSessionService.endUserSession(session.getUuid(), List.of(NO_WIN))
                          .doOnNext(endUserSession ->
                              logRepository.log(new NewBonusGameSessionMessage(session.getGameType(), null, prizeSum,
                                  NewBonusGameSessionMessage.SessionState.COMPLETED))
                          )
                          .flatMap(r -> reward.map(rewards -> GameCompleteResult.builder()
                              .rewards(rewards)
                              .totalPrize(NO_WIN)
                              .status(PlayStatusResponse.SUCCESS)
                              .build()));
                    }
                  })
                  .defaultIfEmpty(GameCompleteResult.builder()
                      .totalPrize(userSession.getPrize().stream().mapToInt(Integer::intValue).sum())
                      .status(PlayStatusResponse.ERROR)
                      .build());
            }
        )
        .switchIfEmpty(Mono.just(GameCompleteResult.builder()
            .totalPrize(userSession.getPrize().stream().mapToInt(Integer::intValue).sum())
            .status(PlayStatusResponse.SUCCESS)
            .build())
        )
        .doOnError(error -> log.error("Error when completeBonusGame: {}", error.getMessage()));
  }

  private int calculateTotalPrize(UserSession userSession, Boolean isWin, int score, int avscore) {
    var bet = BetDataService.fromJsonBetData(userSession.getBet(), BetDataService.BET_DATA_TYPE_REF);
    var modes = bonusGameSettingsService.getModes(BonusGameType.valueOf(userSession.getGameType()));
    var price = BetDataService.getBetPrice(bet, modes);
    double prize = price;
    if (score != 0 || avscore != 0) {
      if (bonusGameSettingsService.hasScale(userSession.getGameType(), price)) {
        prize = (double) score / (double) avscore * prize;
      } else {
        prize = ((double) score / (double) avscore >= 1) ? prize : NO_WIN;
      }
    } else if (Objects.nonNull(isWin)) {
      prize = isWin ? prize : NO_WIN;
    } else {
      prize = NO_WIN;
    }
    return (int) prize;
  }
}
