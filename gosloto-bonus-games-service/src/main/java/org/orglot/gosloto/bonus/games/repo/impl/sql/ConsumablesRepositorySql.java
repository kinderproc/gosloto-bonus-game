package org.orglot.gosloto.bonus.games.repo.impl.sql;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConsumablesRepositorySql {

    public String FIND_BY_ID = """
        SELECT id, type,
               bonus_game_type,
               price,
               name,
               weight,
               icon_url
          FROM "bonus-games".consumables
         WHERE id = :id
    """;
}
