package org.orglot.gosloto.bonus.games.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.orglot.bonus.games.model.BonusGameSpecType;
import org.orglot.bonus.games.model.BonusGameType;
import org.orglot.gosloto.bonus.games.config.BonusGameConfigRefreshBean;
import org.orglot.gosloto.bonus.games.model.BonusGameConfig;
import org.orglot.gosloto.bonus.games.model.Mode;
import org.orglot.gosloto.bonus.games.model.ScaleItem;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class BonusGameSettingsService {

  private final List<BonusGameConfigRefreshBean> gameConfigs;

  public boolean hasScale(String gameType, Integer price) {
    return getByGameType(gameType)
        .getModes().stream()
        .filter(mode -> price.equals(mode.getPrice()))
        .map(Mode::getScale)
        .anyMatch(scale -> !CollectionUtils.isEmpty(scale));
  }

  public Map<Integer, Integer> getScale(String gameType, Integer price) {
    return getByGameType(gameType).getModes().stream()
        .filter(mode -> price.equals(mode.getPrice()))
        .map(Mode::getScale)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(Collections.emptyList())
        .stream()
        .collect(Collectors.toMap(ScaleItem::getMark, ScaleItem::getPrize));
  }

  public List<Mode> getModes(BonusGameType game) {
    var gameType = game.name();
    return Optional.of(getByGameType(gameType))
        .map(BonusGameConfig::getModes)
        .orElseThrow(() -> new RuntimeException("Modes for game type " + gameType + " not found"));
  }

  public BonusGameSpecType getSpecType(BonusGameType game) {
    return BonusGameSpecType.valueOf(StringUtils.toRootUpperCase(getByGameType(game.name()).getGameSpec()));
  }

  public BonusGameConfig getByGameType(String gameType) {
    return gameConfigs.stream().map(BonusGameConfigRefreshBean::getConfig)
        .filter(Objects::nonNull)
        .filter(bonusGameConfig -> bonusGameConfig.getGameType().equals(gameType))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Config for game type " + gameType + " not found"));
  }

  /**
   * Рассчитывается порядковый номер стоимости из списка всех стоимостей игры
   *
   * @param game  тип игры
   * @param price стоимость сессии
   * @return порядковый номер стоимости из списка всех стоимостей игры
   */
  public Integer getPriceIndex(BonusGameType game, int price) {
    var prices = getModes(game)
        .stream()
        .sorted((o1, o2) -> {
          Integer modeAndPrice1 = Integer.parseInt(String.valueOf(o1.getNumber() + o1.getPrice()));
          Integer modeAndPrice2 = Integer.parseInt(String.valueOf(o2.getNumber() + o2.getPrice()));
          return modeAndPrice1.compareTo(modeAndPrice2);
        })
        .map(Mode::getPrice).toList();
    return IntStream.range(0, prices.size())
        .filter(i -> price == prices.get(i))
        .findFirst()
        .orElse(0);
  }

}
