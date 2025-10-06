package org.orglot.gosloto.bonus.games.repo.impl.sql;

public final class UsersPuzzleItemRepositorySql {

  public static final String SAVE = """
      INSERT INTO users_puzzle_items
      (puzzle_id, user_id, puzzle_item_id)
      VALUES(:puzzle_id, :user_id, :puzzle_item_id)
      """;

  public static final String DELETE = """
      DELETE FROM "bonus-games".users_puzzle_items
      WHERE puzzle_id = :puzzle_id and user_id = :user_id;                                       
      """;

  public static final String IS_PUZZLE_ITEMS_COLLECTED_BY_USER_ID_AND_PUZZLE_ID = """
      SELECT
          (SELECT COUNT(DISTINCT upi.puzzle_item_id)
           FROM users_puzzle_items upi
           WHERE
               upi.user_id = :user_id
               AND upi.puzzle_id = :puzzle_id
               AND (upi.create_date > COALESCE(
                   (SELECT up.exchange_date
                    FROM users_puzzle up
                    WHERE up.puzzle_id = :puzzle_id
                      AND up.user_id = :user_id
                    LIMIT 1),
                   '1970-01-01'::timestamp))
          ) = (SELECT COUNT(*)
               FROM puzzle_items
               WHERE puzzle_id = :puzzle_id) AS is_complete
      """;
}
