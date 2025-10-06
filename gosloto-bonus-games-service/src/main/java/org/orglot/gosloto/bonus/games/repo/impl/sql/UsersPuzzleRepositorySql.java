package org.orglot.gosloto.bonus.games.repo.impl.sql;

public final class UsersPuzzleRepositorySql {

  public static final String SAVE = """
                         INSERT INTO users_puzzle
                         (puzzle_id, user_id, collected_count, collected, exchange_date)
                         VALUES(:puzzle_id, :user_id, :collected_count, :collected, :exchange_date)
            """;

  public static final String SAVE_OR_UPDATE_COLLECTED_TRUE = """
                  INSERT INTO "bonus-games".users_puzzle (puzzle_id, user_id, collected_count, collected)
                  VALUES (:puzzle_id, :user_id, 0, true)
                  ON CONFLICT (user_id, puzzle_id)
                  DO UPDATE SET collected = true
      """;

  public static final String UPDATE_COLLECTED_STATUS_FALSE_AND_INCREMENT_COLLECTED_COUNT = """
                          UPDATE users_puzzle
                          SET collected = false, collected_count = collected_count + 1, exchange_date = now()
                          WHERE user_id = :user_id and puzzle_id = :puzzle_id
            """;

  public static final String SELECT_BY_PUZZLE_AND_USER = """
                    SELECT *
                    FROM "bonus-games".users_puzzle
                    where user_id = :user_id and puzzle_id = :puzzle_id limit 1
            """;

  public static final String SELECT_USER_PUZZLES_COLLECTED_AT_LEAST_ONCE = """
      SELECT p.name,
             p."type",
             p2.url,
             pr."type" AS rarity_type,
             up.collected_count
      FROM puzzle p
      INNER JOIN puzzle_rarities pr ON pr.id = p.rarity_id
      LEFT JOIN users_puzzle up ON p.id = up.puzzle_id AND up.user_id = :user_id AND up.collected_count > 0
      LEFT JOIN puzzle p2 ON p2."type" = p."type"
                          AND p2.rarity_id = (SELECT id FROM "bonus-games".puzzle_rarities WHERE "type" = 'COLLECTED' LIMIT 1)
      WHERE up.collected_count > 0
      """;
}
