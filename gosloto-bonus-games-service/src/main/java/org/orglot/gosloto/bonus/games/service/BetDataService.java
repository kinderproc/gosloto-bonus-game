package org.orglot.gosloto.bonus.games.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.orglot.gosloto.bonus.games.exception.BetParseException;
import org.orglot.gosloto.bonus.games.model.Mode;
import org.orglot.gosloto.bonus.games.service.strategy.model.BetCoupon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import static org.orglot.gosloto.bonus.games.mapper.JsonMapper.MAPPER;

@Slf4j
@UtilityClass
public class BetDataService {
  public final TypeReference<Map<String, Object>> BET_DATA_TYPE_REF = new TypeReference<>() {
  };

  public final String PRICE = "price";
  public final String MODE_PRICE = "modePrice";
  public final String INDEX_PRICE = "indexPrice";
  public final String MODE = "mode";
  public final String MULTIPLIER = "multiplier";
  public final Integer DEFAULT_PRICE = 0;
  public final String COUPONS = "coupons";

  /**
   * Формируется json из данных по сессии, необходимых для сохранения в базе и использования их на следующих этапах
   * жизненного цикла сессии
   *
   * @param mode       номер режима
   * @param price      идентификатор цены
   * @param indexPrice порядковый номер стоимости из списка всех стоимостей игры
   * @return string json
   */
  public String toJsonBetData(Integer mode, Integer price, Integer indexPrice) {
    Map<String, String> bet = new HashMap<>();
    bet.put(MODE, mode.toString());
    bet.put(PRICE, price.toString());
    bet.put(INDEX_PRICE, indexPrice.toString());
    try {
      return MAPPER.writeValueAsString(bet);
    } catch (JsonProcessingException e) {
      log.error("Error when parse bet to json: mode {}, price {}", mode, price);
      throw new BetParseException();
    }
  }

  /**
   * Формируется json из данных по сессии для игры Нарды,
   * необходимых для сохранения в базе и использования их на следующих этапах
   * жизненного цикла сессии
   *
   * @param mode       номер режима
   * @param price      цены игры
   * @param modePrice  базовая цена игры
   * @param indexPrice порядковый номер стоимости из списка всех стоимостей игры
   * @param coupons    данные по билетам
   * @return string json
   */
  public String toLotteryJsonBetData(Integer mode,
                                     Integer price,
                                     Integer modePrice,
                                     Integer indexPrice,
                                     Integer multiplier,
                                     List<BetCoupon> coupons) {
    Map<String, Object> bet = new HashMap<>();
    bet.put(MODE, mode.toString());
    bet.put(PRICE, price.toString());
    bet.put(MODE_PRICE, modePrice.toString());
    bet.put(INDEX_PRICE, indexPrice.toString());
    bet.put(MULTIPLIER, multiplier);
    bet.put(COUPONS, coupons);
    try {
      return MAPPER.writeValueAsString(bet);
    } catch (JsonProcessingException e) {
      log.error("Error when parse bet to json for Nards: mode {}, price {}", mode, price);
      throw new BetParseException();
    }
  }

  public <T> T fromJsonBetData(String betData, TypeReference<T> typeRef) {
    try {
      return MAPPER.readValue(betData, typeRef);
    } catch (JsonProcessingException e) {
      log.error("Error when parse bet from json: betdata {}", betData);
      throw new BetParseException();
    }
  }

  public <T> T fromJsonBetDataElement(Object betDataElement, TypeReference<T> typeRef) {
    try {
      return MAPPER.convertValue(betDataElement, typeRef);
    } catch (IllegalArgumentException e) {
      log.error("Error when parse from json: betdata element {}", betDataElement);
      throw new BetParseException();
    }
  }

  public <T> Integer getBetPrice(Map<String, T> bet, List<Mode> mode) {
    return getBetPrice(bet, mode, (b, p) -> p);
  }

  public <T> Integer getBetPrice(Map<String, T> bet,
                                 List<Mode> mode,
                                 BiFunction<Map<String, T>, Integer, Integer> priceCalculator) {
    if (Objects.nonNull(bet.get(INDEX_PRICE))) {
      var indexPrice = getIntFromJsonString(bet.get(INDEX_PRICE));
      var sortedPrice = mode
          .stream()
          .sorted((o1, o2) -> {
            Integer modeAndPrice1 = Integer.parseInt(String.valueOf(o1.getNumber() + o1.getPrice()));
            Integer modeAndPrice2 = Integer.parseInt(String.valueOf(o2.getNumber() + o2.getPrice()));
            return modeAndPrice1.compareTo(modeAndPrice2);
          }).map(Mode::getPrice).toList();
      return IntStream.range(0, sortedPrice.size())
          .filter(i -> indexPrice == i)
          .map(sortedPrice::get)
          .map(p -> priceCalculator.apply(bet, p))
          .findFirst()
          .orElse(0);
    } else if (Objects.nonNull(bet.get(PRICE))) {
      return getBetPrice(bet, priceCalculator);
    } else {
      return DEFAULT_PRICE;
    }
  }

  public <T> Integer getBetPrice(Map<String, T> bet,
                                 BiFunction<Map<String, T>, Integer, Integer> priceCalculator) {
    return getBetPrice(bet, PRICE, priceCalculator);
  }

  public <T> Integer getBetPrice(Map<String, T> bet,
                                 String fieldName,
                                 BiFunction<Map<String, T>, Integer, Integer> priceCalculator) {
    return priceCalculator.apply(bet, getIntFromJsonString(bet.get(fieldName)));
  }

  public <T> Integer getBetModePrice(Map<String, T> bet) {
    return getIntFromJsonString(bet.get(MODE_PRICE));
  }

  public int getIntFromJsonString(Object value) {
    return Integer.parseInt(String.valueOf(value));
  }
}
