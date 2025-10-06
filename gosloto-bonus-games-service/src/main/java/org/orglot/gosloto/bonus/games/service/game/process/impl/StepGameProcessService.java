package org.orglot.gosloto.bonus.games.service.game.process.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.BonusGameSpecType;
import org.orglot.bonus.games.model.BonusGameType;
import org.orglot.bonus.games.model.response.BonusGamePlayStatus;
import org.orglot.bonus.games.model.response.GameCompleteResult;
import org.orglot.bonus.games.model.response.PlayStatusResponse;
import org.orglot.bonus.games.model.response.Reward;
import org.orglot.gosloto.bonus.client.operation.bonusrefill.model.BonusRefillReason;
import org.orglot.gosloto.bonus.games.model.SessionState;
import org.orglot.gosloto.bonus.games.model.UserSession;
import org.orglot.gosloto.bonus.games.service.BonusGameSettingsService;
import org.orglot.gosloto.bonus.games.service.DefaultBuyService;
import org.orglot.gosloto.bonus.games.service.PrizeService;
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
public class StepGameProcessService extends AbstractBonusGameProcessHandler {

    private static final Integer FIRST_STEP = 1;
    private static final Integer SECOND_STEP = 2;

    private final PrizeService prizeService;
    private final PurchaseService purchaseService;
    private final RewardService rewardService;
    private final LogRepository logRepository;
    private final BonusGameSettingsService bonusGameSettingsService;

    public StepGameProcessService(UserSessionService userSessionService,
                                  DefaultBuyService defaultBuyService,
                                  PrizeService prizeService,
                                  PurchaseService purchaseService,
                                  RewardService rewardService,
                                  LogRepository logRepository,
                                  BonusGameSettingsService bonusGameSettingsService) {
        super(userSessionService, defaultBuyService);
        this.prizeService = prizeService;
        this.purchaseService = purchaseService;
        this.rewardService = rewardService;
        this.logRepository = logRepository;
        this.bonusGameSettingsService = bonusGameSettingsService;
    }

    @Override
    public boolean supportedGame(BonusGameType game) {
        return BonusGameSpecType.STEP.equals(bonusGameSettingsService.getSpecType(game));
    }

    @Override
    public Mono<BonusGamePlayStatus> play(@NonNull UserSession session, Integer modeNumber) {
        return switch (session.getSessionState()) {
            case COMPLETED -> Mono.just(new BonusGamePlayStatus(PlayStatusResponse.SUCCESS, session.getUuid()));
            case EXPIRED -> Mono.just(new BonusGamePlayStatus(PlayStatusResponse.ERROR, session.getUuid()));
            case START -> firstPlay(session, modeNumber);
            default -> this.playInternal(session, modeNumber);
        };
    }

    private Mono<BonusGamePlayStatus> firstPlay(@NonNull UserSession session, Integer modeNumber) {
        if (!List.of(FIRST_STEP, SECOND_STEP).contains(modeNumber)) {
            return Mono.just(BonusGamePlayStatus.error("firstPlay error, sessionUUID " + session.getUuid() + " mode number " + modeNumber));
        }
        return userSessionService.playUserSession(session.getUuid())
            .flatMap(v -> playInternal(session, modeNumber));
    }

    private Mono<BonusGamePlayStatus> playInternal(@NonNull UserSession session, Integer modeNumber) {
        if (FIRST_STEP.equals(modeNumber)) {
            return Mono.just(new BonusGamePlayStatus(PlayStatusResponse.IN_PROGRESS, session.getPrize(), session.getUuid()));
        }
        int lastPrize = calculateLastPrize(modeNumber, session);
        if (lastPrize == 0) {
            return Mono.just(new BonusGamePlayStatus(PlayStatusResponse.IN_PROGRESS, List.of(NO_WIN), session.getUuid()));
        }
        var randomIds = prizeService.getRandomIds(BonusGameType.valueOf(session.getGameType()), modeNumber, lastPrize, true);
        return rewardService.getRandomRewardsByRandomIds(randomIds)
            .collectList()
            .flatMap(rewards -> {
                var prizes = rewards.stream().map(Reward::getValue).toList();
                return userSessionService.playUserSession(session.getUuid(), prizes.stream().mapToInt(Integer::intValue).sum())
                    .thenReturn(new BonusGamePlayStatus(PlayStatusResponse.IN_PROGRESS, prizes, session.getUuid()));
            });
    }

    @Override
    public Mono<GameCompleteResult> completeBonusGame(UserSession userSession, Boolean isWin, int score,
                                                      int avscore, String mobile) {
        return Mono.just(userSession)
            .filter(session -> !List.of(SessionState.COMPLETED, SessionState.EXPIRED).contains(session.getSessionState()))
            .flatMap(session -> {
                var prizeSum = Objects.isNull(userSession.getLastPrize()) ?
                    Objects.isNull(userSession.getPrize()) ? NO_WIN : userSession.getPrize().stream().mapToInt(Integer::intValue).sum() :
                    userSession.getLastPrize();
                var rewards = rewardService.getAndApplyRewards(userSession, mobile);
                if (prizeSum > 0) {
                    return refillBonusAndCompleteSession(prizeSum, userSession, mobile, rewards);
                } else {
                    return completeSession(session, rewards);
                }
            })
            .switchIfEmpty(Mono.just(GameCompleteResult.builder()
                .totalPrize(userSession.getPrize().stream().mapToInt(Integer::intValue).sum())
                .status(PlayStatusResponse.SUCCESS)
                .build())
            )
            .doOnError(error -> log.error("Error when completeBonusGame: {}", error.getMessage()));
    }

    private Mono<GameCompleteResult> refillBonusAndCompleteSession(Integer prizeSum, UserSession session, String mobile,
                                                                   Mono<List<Reward>> reward) {
        return Mono.fromCallable(() -> purchaseService.refillBonus(prizeSum, BonusRefillReason.ZBONUS_SHOP.name(), mobile,
                session.getUuid(), null, null, session.getGameType()))
            .filter(refillSuccess -> refillSuccess)
            .flatMap(refillSuccess -> userSessionService.endUserSession(session.getUuid(), List.of(prizeSum)))
            .doOnNext(endUserSession -> logRepository.log(new NewBonusGameSessionMessage(session.getGameType(), null, prizeSum,
                NewBonusGameSessionMessage.SessionState.COMPLETED)))
            .flatMap(result -> reward.map(rewards -> GameCompleteResult.builder()
                .rewards(rewards)
                .totalPrize(prizeSum)
                .status(PlayStatusResponse.SUCCESS)
                .build()))
            .switchIfEmpty(Mono.just(GameCompleteResult.error(REFILL_ERROR)))
            .doOnError(error -> log.error("Error when completeBonusGame with prize: {}", error.getMessage()));
    }

    private Mono<GameCompleteResult> completeSession(UserSession session, Mono<List<Reward>> reward) {
        return userSessionService.endUserSession(session.getUuid(), List.of(NO_WIN))
            .doOnNext(endUserSession ->
                logRepository.log(new NewBonusGameSessionMessage(session.getGameType(), null, NO_WIN,
                    NewBonusGameSessionMessage.SessionState.COMPLETED)))
            .flatMap(result -> reward.map(rewards -> GameCompleteResult.builder()
                .rewards(rewards)
                .totalPrize(NO_WIN)
                .status(PlayStatusResponse.SUCCESS)
                .build()));
    }

    private int calculateLastPrize(Integer modeNumber, UserSession session) {
        if (SECOND_STEP.equals(modeNumber)) {
            return session.getPrize().stream().mapToInt(Integer::intValue).sum();
        } else if (modeNumber > SECOND_STEP) {
            if (Objects.nonNull(session.getLastPrize())) {
                return session.getLastPrize();
            }
            log.warn("For more then second step on play last prize is null, user session {}, modeNumber {}", session, modeNumber);
            return NO_WIN;
        } else {
            log.warn("For status PLAYING error modeNumber, user session {}, modeNumber {}", session, modeNumber);
            return NO_WIN;
        }
    }
}
