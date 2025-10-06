package org.orglot.gosloto.bonus.games.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gosloto.promo.PromoWalletGrpcClient;
import org.orglot.bonus.games.model.BonusGameType;
import org.orglot.gosloto.bonus.client.BonusServiceClient;
import org.orglot.gosloto.bonus.client.operation.bonusrefill.model.BonusRefillReason;
import org.orglot.gosloto.bonus.client.operation.bonusrefill.model.BonusRefillRequest;
import org.orglot.gosloto.bonus.client.operation.consumepoints.model.request.ConsumeAction;
import org.orglot.gosloto.bonus.client.operation.consumepoints.model.request.ConsumePointsRequest;
import org.orglot.gosloto.bonus.client.operation.consumepoints.model.request.TicketItem;
import org.orglot.gosloto.bonus.client.operation.consumepoints.model.response.ConsumePointsResponse;
import org.orglot.gosloto.bonus.games.properties.AppProperties;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseService {

  private static final String EXCLUDE = "http://";
  private static final int COEFFICIENT = 100;
  private static final String ACCRUE_POSTFIX = "_ACCRUE";
  private static final String CONSUME_POSTFIX = "_CONSUME";

  private final BonusServiceClient bonusServiceClient;
  private final PromoWalletGrpcClient promoWalletGrpcClient;
  private final AppProperties appProperties;

  public Mono<ConsumePointsResponse> consumePoints(String mobile, BonusGameType game, Integer price, UUID uuid) {
    var now = new Date();
    var consumeRequest = new ConsumePointsRequest();
    consumeRequest.setAccountId(mobile);
    consumeRequest.setTimestamp(now);
    consumeRequest.setItems(List.of(TicketItem.builder()
        .productCode(appProperties.getProductCode())
        .price(Long.valueOf(price) * COEFFICIENT)
        .build()));
    consumeRequest.setExternalTransactionId(uuid.toString() + CONSUME_POSTFIX);
    consumeRequest.setDescription(game.name());
    consumeRequest.setAction(ConsumeAction.PAY);
    return bonusServiceClient.consumePointsOperations().consumePoints(consumeRequest)
        .onErrorResume(e -> {
          var message = Optional.ofNullable(e.getMessage()).filter(t -> !t.contains(EXCLUDE)).orElse(null);
          log.error("Error while consume points {}, request {}", message, consumeRequest);
          return Mono.just(new ConsumePointsResponse(null, null, null, null, null,
              null, null, 0, "Error while sending consume points " + message));
        })
        .switchIfEmpty(Mono.just(new ConsumePointsResponse(null, null, null, null, null,
            null, null, 0, "Error while sending consume points: empty response"))
        );
  }

  public boolean refillBonus(Integer prize, String refillReason, String mobile, UUID uuid, Integer lifeTimeMonths,
                             Integer lifeTimeDays, String refillDescription) {
    boolean result;
    var request = BonusRefillRequest.builder()
        .accountId(mobile)
        .amount(prize * COEFFICIENT)
        .description(Objects.isNull(refillDescription) ? "" : refillDescription)
        .externalId((Objects.isNull(uuid) ? UUID.randomUUID().toString() : uuid.toString()) + ACCRUE_POSTFIX)
        .reason(BonusRefillReason.valueOf(refillReason))
        .lifetimeMonths(determineMonth(lifeTimeMonths, lifeTimeDays))
        .lifetimeDays(Objects.isNull(lifeTimeDays) ? 0 : lifeTimeDays)
        .build();
    try {
      result = bonusServiceClient.bonusRefillOperations().refill(request);
    } catch (Exception ex) {
      var message = Optional.ofNullable(ex.getMessage()).filter(t -> !t.contains(EXCLUDE)).orElse(null);
      log.error("Error while refill points {}, request {}", message, request, ex);
      return false;
    }
    return result;
  }

  private Integer determineMonth(Integer month, Integer days) {
    if (month == null || month == 0) {
      if (days != null && days > 0) {
        return -1;
      }
      return 0;
    }
    return month;
  }

  public Mono<Boolean> refillAttempt(Long userId, int difference, UUID sessionUUID) {
    var expiryDate = Instant.now().plus(appProperties.getAttemptExpirationDateInDays(), ChronoUnit.DAYS);
    return promoWalletGrpcClient.getPromotionOperation().refill(userId, appProperties.getWalletTypeForAttempt(),
            difference, sessionUUID.toString(), expiryDate)
        .map(response -> true)
        .doOnError(e -> log.error("Error when refillAttempt, userId {}, difference {}", userId, difference, e))
        .onErrorReturn(false)
        .defaultIfEmpty(false);
  }

  public Mono<Boolean> consumeAttempt(Long userId, int difference) {
    return promoWalletGrpcClient.getPromotionOperation().getWalletBalance(userId, appProperties.getWalletTypeForAttempt())
        .flatMap(response -> {
          if (response.getBalance() >= difference) {
            return promoWalletGrpcClient.getPromotionOperation().changeWalletBalance(userId, appProperties.getWalletTypeForAttempt(),
                    Math.negateExact(difference))
                .map(r -> true)
                .doOnError(e -> log.error("Error when consumeAttempt, userId {}, difference {}", userId, difference))
                .onErrorReturn(false)
                .defaultIfEmpty(false);
          }
          log.error("Error when consumeAttempt. not enough balance, userId {}, difference {}", userId, difference);
          return Mono.just(false);
        })
        .doOnError(e -> log.error("Error when consumeAttempt, userId {}, difference {}", userId, difference))
        .onErrorReturn(false)
        .defaultIfEmpty(false);

  }
}
