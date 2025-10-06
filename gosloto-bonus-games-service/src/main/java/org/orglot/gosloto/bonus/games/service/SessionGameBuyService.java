package org.orglot.gosloto.bonus.games.service;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.request.BonusGameBuy;
import org.orglot.bonus.games.model.response.BonusGameBuyStatus;
import org.orglot.bonus.games.model.response.Consumable;
import org.orglot.gosloto.bonus.games.model.UserSession;
import org.orglot.gosloto.bonus.games.repo.ConsumablesRepository;
import org.orglot.gosloto.bonus.games.service.strategy.BonusGameBuyExecuteStrategy;
import org.orglot.gosloto.components.log.LogRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.orglot.gosloto.bonus.games.mapper.JsonMapper.MAPPER;

@Service
@Slf4j
public class SessionGameBuyService extends DefaultBuyService {

    private final ConsumableJsonParser consumableJsonParser;
    private final ConsumablesRepository consumablesRepository;

    public SessionGameBuyService(UserSessionService userSessionService,
                                 LogRepository logRepository,
                                 PurchaseService purchaseService,
                                 RewardService rewardService,
                                 PrizeService prizeService,
                                 BonusGameSettingsService bonusGameSettingsService,
                                 List<BonusGameBuyExecuteStrategy> bonusGameTypeBonusGameBuyExecuteStrategyList,
                                 ConsumableJsonParser consumableJsonParser,
                                 ConsumablesRepository consumablesRepository) {
        super(userSessionService, logRepository, purchaseService, rewardService, prizeService, bonusGameSettingsService,
                bonusGameTypeBonusGameBuyExecuteStrategyList);
        this.consumableJsonParser = consumableJsonParser;
        this.consumablesRepository = consumablesRepository;
    }

    @Override
    public Mono<BonusGameBuyStatus> buyGame(BonusGameBuy gameBuy, Integer calculatedPrice, boolean needConsume) {
        return super.buyGame(gameBuy, calculatedPrice, needConsume)
            .flatMap(status -> updateConsumables(status, gameBuy));
    }

    private Mono<BonusGameBuyStatus> updateConsumables(BonusGameBuyStatus status, BonusGameBuy gameBuy) {
        return userSessionService.findByUUID(status.getSessionUUID())
            .flatMap(session -> consumableJsonParser.parseOrEmptyReactive(session.getBet())
                    .filter(c -> gameBuy.getConsumableId())
                .map(consumables -> {
                    consumables
                    gameBuy.getConsumableId()
                    status.setConsumables(consumables);
                    return status;
                })
                .switchIfEmpty(consumablesRepository.findById(UUID.fromString(gameBuy.getConsumableId()))
                    .map(c -> {
                            var consumable = Consumable.builder()
                                .id(c.getId())
                                .name(c.getName())
                                .price(c.getPrice())
                                .available(c.getAvailable())
                                .iconUrl(c.getIconUrl())
                                .build();
                            status.setConsumables(List.of(consumable));
                            return status;
                        }
                    )
                    .switchIfEmpty(Mono.error(new RuntimeException("Can't find consumable with id " + gameBuy.getConsumableId())))
                )
            );
    }
}
