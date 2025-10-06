package org.orglot.gosloto.bonus.games.repo.impl.sql;

public final class PuzzleRepositorySql {

  public static final String FIND_BY_ID = """
      select prize from puzzle where id = :id
      """;

  public static final String CREATE_QUERY = """
      insert into "bonus-games".puzzle(name, url, game_type, rarity_id, type, prize)
      values (
      :name,
      :url,
      :game_type,
      (
        SELECT id
        FROM "bonus-games".puzzle_rarities
        WHERE "type" = :rarity
        LIMIT 1
      ),
      :type,
      :prize
      )
      returning id;
      """;

  public static final String FIND_BY_ID_QUERY = """
      SELECT p.id, p.name, p.url, p.game_type, pr."type" AS rarity, p."type", p.prize
      FROM "bonus-games".puzzle p
      inner join "bonus-games".puzzle_rarities pr on p.rarity_id = pr.id
      WHERE p.id = :puzzleId""";

  public static final String UPDATE_QUERY = """
      update "bonus-games".puzzle
      set name = :name,
       url = :url,
       game_type = :game_type,
       rarity_id = (SELECT id FROM "bonus-games".puzzle_rarities where "type" = :rarity limit 1),
       type = :type,
       prize = :prize
      where id = :id""";

  public static final String UPDATE_PUZZLE_BY_ID = """
      update "bonus-games".puzzle
      SET name=:name,
      url=:url,
      game_type=:game_type,
      rarity_id=(
                   SELECT id
                   FROM "bonus-games".puzzle_rarities
                   WHERE "type" = :rarity
                   LIMIT 1
                 ),
      type=:type,
      prize=:prize
      where id=:id
      returning id
      """;

}
