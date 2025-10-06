package org.orglot.gosloto.bonus.games.service.game.process.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.BonusGameSpecType;
import org.orglot.bonus.games.model.BonusGameType;
import org.orglot.bonus.games.model.request.BonusGameBuy;
import org.orglot.bonus.games.model.request.BonusGameBuyCoupon;
import org.orglot.bonus.games.model.request.BonusGameBuyData;
import org.orglot.bonus.games.model.response.BonusGameBuyStatus;
import org.orglot.bonus.games.model.response.GameCompleteResult;
import org.orglot.bonus.games.model.response.LotteryTicket;
import org.orglot.bonus.games.model.response.PlayStatusResponse;
import org.orglot.bonus.games.model.validate.group.LotteryValid;
import org.orglot.gosloto.bonus.client.operation.bonusrefill.model.BonusRefillReason;
import org.orglot.gosloto.bonus.games.mapper.LotteryMapper;
import org.orglot.gosloto.bonus.games.model.SessionState;
import org.orglot.gosloto.bonus.games.model.UserSession;
import org.orglot.gosloto.bonus.games.service.BetDataService;
import org.orglot.gosloto.bonus.games.service.BonusGameSettingsService;
import org.orglot.gosloto.bonus.games.service.DefaultBuyService;
import org.orglot.gosloto.bonus.games.service.PurchaseService;
import org.orglot.gosloto.bonus.games.service.RewardService;
import org.orglot.gosloto.bonus.games.service.UserSessionService;
import org.orglot.gosloto.bonus.games.service.strategy.model.BetCoupon;
import org.orglot.gosloto.bonus.games.validation.GoslotoValidator;
import org.orglot.gosloto.components.log.LogRepository;
import org.orglot.gosloto.components.log.message.NewBonusGameSessionMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.orglot.bonus.games.model.response.GameCompleteResult.REFILL_ERROR;

@Slf4j
@Service
public class LotteryGameProcessService extends AbstractBonusGameProcessHandler {

  public static final TypeReference<Map<String, Object>> BET_DATA_TYPE_REF = new TypeReference<>() {
  };
  public static final TypeReference<List<BetCoupon>> BET_COUPONS_TYPE_REF = new TypeReference<>() {
  };

  private final BonusGameSettingsService bonusGameSettingsService;
  private final PurchaseService purchaseService;
  private final LogRepository logRepository;
  private final RewardService rewardService;
  private final GoslotoValidator goslotoValidator;

  public LotteryGameProcessService(UserSessionService userSessionService,
                                   BonusGameSettingsService bonusGameSettingsService,
                                   DefaultBuyService defaultBuyService,
                                   PurchaseService purchaseService,
                                   LogRepository logRepository,
                                   RewardService rewardService,
                                   GoslotoValidator goslotoValidator) {
    super(userSessionService, defaultBuyService);
    this.bonusGameSettingsService = bonusGameSettingsService;
    this.rewardService = rewardService;
    this.purchaseService = purchaseService;
    this.logRepository = logRepository;
    this.goslotoValidator = goslotoValidator;
  }

  @Override
  public boolean supportedGame(BonusGameType game) {
    return BonusGameSpecType.LOTTERY.equals(bonusGameSettingsService.getSpecType(game));
  }

  @Override
  public Mono<BonusGameBuyStatus> buyGame(BonusGameBuy gameBuy) {
    goslotoValidator.validateAndExceptionIfFalse(gameBuy, "gameBuy", LotteryValid.class);
    return defaultBuyService.buyGame(gameBuy, calculatePrice(gameBuy), true);
  }

  @Override
  public Mono<GameCompleteResult> completeBonusGame(@NonNull UserSession userSession,
                                                    Boolean isWin,
                                                    int score,
                                                    int avscore,
                                                    String mobile) {
    return getLotteryTickets(userSession, mobile)
        .flatMap(tickets -> Mono.just(userSession)
            .filter(session -> !List.of(SessionState.COMPLETED, SessionState.EXPIRED)
                .contains(session.getSessionState())
            )
            .flatMap(session -> {
              var prizeSum = tickets
                  .stream()
                  .map(LotteryTicket::getTotalPrize)
                  .mapToInt(Integer::intValue)
                  .sum();
              if (prizeSum > 0) {
                return Mono.fromCallable(() ->
                        purchaseService.refillBonus(prizeSum, BonusRefillReason.ZBONUS_SHOP.name(), mobile,
                            session.getUuid(), null, null, session.getGameType()))
                    .filter(refillSuccess -> refillSuccess)
                    .flatMap(refillSuccess -> userSessionService.endUserSession(session.getUuid()))
                    .doOnNext(endUserSession ->
                        logRepository.log(new NewBonusGameSessionMessage(
                            session.getGameType(), null, prizeSum,
                            NewBonusGameSessionMessage.SessionState.COMPLETED
                        ))
                    )
                    .map(result -> GameCompleteResult.builder()
                        .status(PlayStatusResponse.SUCCESS)
                        .tickets(tickets)
                        .build()
                    )
                    .switchIfEmpty(Mono.just(GameCompleteResult.error(REFILL_ERROR)))
                    .doOnError(error -> log.error("Error when completeBonusGame with prize: {}", error.getMessage()));
              } else {
                return userSessionService.endUserSession(session.getUuid())
                    .doOnNext(endUserSession ->
                        logRepository.log(new NewBonusGameSessionMessage(
                            session.getGameType(), null, prizeSum,
                            NewBonusGameSessionMessage.SessionState.COMPLETED
                        ))
                    )
                    .map(result -> GameCompleteResult.builder()
                        .status(PlayStatusResponse.SUCCESS)
                        .tickets(tickets)
                        .build()
                    );
              }
            })
            .defaultIfEmpty(
                GameCompleteResult.builder()
                    .status(PlayStatusResponse.SUCCESS)
                    .tickets(tickets)
                    .build()
            )
        )
        .doOnError(error -> log.error("Error when completeBonusGame: {}", error.getMessage()));
  }

  private int calculatePrice(BonusGameBuy gameBuy) {
    BonusGameBuyData gameData = gameBuy.getGameData();
    int multiplier = gameData.getMultiplier();
    List<BonusGameBuyCoupon> coupons = gameData.getCoupons();
    Integer baseCouponPrice = gameBuy.getPrice();
    return coupons
        .stream()
        .mapToInt(coupon -> calculatePrice(coupon, baseCouponPrice))
        .sum() * multiplier;
  }

  private int calculatePrice(BonusGameBuyCoupon coupon, int baseCouponPrice) {
    if (!CollectionUtils.isEmpty(coupon.getCombination()) && Objects.nonNull(coupon.getParity())) {
      return 2 * baseCouponPrice;
    }
    return baseCouponPrice;
  }

  private Mono<List<LotteryTicket>> getLotteryTickets(UserSession userSession, String mobile) {
    var bet = BetDataService.fromJsonBetData(userSession.getBet(), BET_DATA_TYPE_REF);
    var multiplier = (Integer) bet.get("multiplier");
    var gameType = BonusGameType.valueOf(userSession.getGameType());
    var betModePrice = BetDataService.getBetModePrice(bet);
    var coupons = BetDataService.fromJsonBetDataElement(bet.get("coupons"), BET_COUPONS_TYPE_REF);
    return Flux.fromIterable(coupons)
        .doOnNext(coupon -> goslotoValidator.validateAndExceptionIfFalse(coupon, "BetCoupon"))
        .flatMap(coupon -> rewardService.getAndApplyRewards(userSession, bet, gameType, mobile, betModePrice)
            .map(rewards -> LotteryMapper.MAPPER.toLotteryTicket(
                    rewards,
                    coupon,
                    multiplier
                )
            )
        ).collectList();
  }
}
