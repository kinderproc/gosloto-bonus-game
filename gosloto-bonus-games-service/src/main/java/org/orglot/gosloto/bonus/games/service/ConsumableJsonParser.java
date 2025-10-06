package org.orglot.gosloto.bonus.games.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.response.Consumable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static org.orglot.gosloto.bonus.games.mapper.JsonMapper.MAPPER;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumableJsonParser {

    private final TypeReference<List<Consumable>> CONSUMABLE_LIST = new TypeReference<>() {
    };

    public List<Consumable> parseOrEmpty(String bet) {
        if (bet == null || bet.isBlank()) return List.of();
        try {
            JsonNode root = MAPPER.readTree(bet);

            JsonNode arr = root.isArray() ? root : root.path("consumables");
            if (arr.isMissingNode() || !arr.isArray() || arr.isEmpty()) return List.of();

            return MAPPER.convertValue(arr, CONSUMABLE_LIST);
        } catch (Exception e) {
            log.warn("Failed to parse consumables. bet='{}': {}", bet, e.getMessage(), e);
            return List.of();
        }
    }

    public Mono<List<Consumable>> parseOrEmptyReactive(String bet) {
        if (bet == null || bet.isBlank()) {
            log.warn("Failed to parse consumables. Bet is empty.");
            return Mono.just(List.of());
        }

        return Mono.fromCallable(() -> parseOrEmpty(bet))
            .subscribeOn(Schedulers.boundedElastic())
            .onErrorResume(e -> {
                log.warn("Failed to parse consumables. bet='{}': {}", bet, e.getMessage(), e);
                return Mono.just(List.of());
            });
    }
}
