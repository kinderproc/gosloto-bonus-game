package org.orglot.gosloto.bonus.games.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.BonusGameType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrizeService {

  public static final int NO_WIN = 0;

  private final BonusGameSettingsService bonusGameSettingsService;

  /**
   * Получение идентификаторов рандомов наград или приза
   *
   * @param game тип игры
   * @param modeNumber номер режима
   * @param price стоимость режима
   * @param isPrize нужно ли получить приз
   * @return список идентификаторов рандомов
   */
  public List<Integer> getRandomIds(BonusGameType game, Integer modeNumber, Integer price, boolean isPrize) {
    var modeData = bonusGameSettingsService.getModes(game).stream()
        .filter(mode -> mode.getNumber().equals(modeNumber) && mode.getPrice().equals(price))
        .findFirst()
        .orElse(null);

    if (Objects.isNull(modeData) || Objects.isNull(modeData.getRandomPrizeIds())) {
      log.warn("Null modeData or null randomPrizeId for get prize. Game {}, mode {}, price {}", game.name(), modeNumber, price);
      return null;
    }

    return isPrize ? modeData.getRandomPrizeIds() : modeData.getRandomRewardIds();
  }
}
