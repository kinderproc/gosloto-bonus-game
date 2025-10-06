package org.orglot.gosloto.bonus.games.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.BonusGameType;
import org.orglot.bonus.games.model.response.GameModeResponse;
import org.orglot.bonus.games.model.response.PriceAndScaleOfMode;
import org.orglot.bonus.games.model.response.config.ModeTypeGameConfig;
import org.orglot.gosloto.bonus.games.model.Mode;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModeGameConfigService {

  private final BonusGameSettingsService bonusGameSettingsService;

  /**
   * Получение конфигов по типу игры из свойств сервиса (games-config)
   *
   * @param game - тип игры
   * @return ModeTypeGameConfig
   */
  public Mono<ModeTypeGameConfig> getGameConfig(BonusGameType game) {
    return Mono.just(bonusGameSettingsService.getModes(game))
        .flatMapMany(Flux::fromIterable)
        .filter(Mode::isVisible)
        .map(mode -> buildGameModeResponse(mode.getPrice(), game, mode.getNumber(), mode.getDescription()))
        .collectList()
        .map(gameModeResponseList -> new ModeTypeGameConfig(gameModeResponseList, game.name()));
  }

  private GameModeResponse buildGameModeResponse(Integer price, BonusGameType game, Integer mode, String description) {
    var scale = List.of(PriceAndScaleOfMode.builder()
        .price(price)
        .scale(bonusGameSettingsService.getScale(game.name(), price))
        .build());
    return GameModeResponse.builder()
        .description(description)
        .modeNumber(mode)
        .priceAndScale(scale)
        .build();
  }
}
