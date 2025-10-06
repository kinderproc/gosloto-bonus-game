package org.orglot.gosloto.bonus.games.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.orglot.gosloto.bonus.games.service.strategy.model.BetCoupon;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BetDataServiceTest {

  public static final TypeReference<Map<String, Object>> BET_DATA_TYPE_REF = new TypeReference<>() {
  };
  public static final TypeReference<List<BetCoupon>> BET_COUPONS_TYPE_REF = new TypeReference<>() {
  };

  @Test
  void fromJsonBetDataElementTest() {
    String json = "{\"mode\": \"1\", \"price\": \"25\", \"coupons\": [{\"valid\": false, \"parity\": null, " +
        "\"drawInfo\": {\"structured\": {\"parity\": \"FIFTY_FIFTY\", \"played\": [16, 3, 4, 6, 8, 10, 13, 15, 4]}}, " +
        "\"combination\": [3, 4, 6, 8, 10, 13, 15, 16], \"extraCombination\": [3, 4, 6, 8, 10, 13, 15, 16]}], " +
        "\"indexPrice\": \"0\", \"multiplier\": 1}";

    var bet = BetDataService.fromJsonBetData(json, BET_DATA_TYPE_REF);
    var multiplier = (Integer) bet.get("multiplier");
    var coupons = BetDataService.fromJsonBetDataElement(bet.get("coupons"), BET_COUPONS_TYPE_REF);
    BetCoupon betCoupon = coupons.get(0);
    assertEquals("FIFTY_FIFTY", betCoupon.getDrawInfo().getStructured().getParity());
    assertEquals(1, multiplier);
  }
}
