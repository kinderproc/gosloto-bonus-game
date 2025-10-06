package org.orglot.gosloto.bonus.games.repo.impl.sql;

public final class PuzzleItemRepositorySql {

  public static final String FIND_RANDOM_BY_USER_NOT_USED = """
      SELECT pi.*, up.*, p."name" as puzzle_name
      FROM "bonus-games".puzzle_items pi
      LEFT JOIN "bonus-games".users_puzzle up
          ON up.puzzle_id = pi.puzzle_id
          AND up.user_id = :user_id
          AND (up.collected = false OR up.collected IS NULL)
      INNER JOIN "bonus-games".puzzle p
          ON p.id = pi.puzzle_id
          AND p.rarity_id = COALESCE(
              (SELECT id FROM "bonus-games".puzzle_rarities WHERE id = :rarity_id),
              p.rarity_id
          )
      WHERE NOT EXISTS (
          SELECT 1
          FROM "bonus-games".users_puzzle_items upi
          WHERE upi.puzzle_item_id = pi.id
          AND upi.user_id = :user_id
      )
      ORDER BY RANDOM()
      LIMIT 1
      """;

  public static final String FIND_ALL_ENRICH_USER_WITH_RARITY = """
      SELECT
          p.id AS puzzle_id,
          p.url AS puzzle_url,
          p.game_type AS puzzle_game_type,
          p.name AS puzzle_name,
          p."type" AS puzzle_type,
          pr."type" AS puzzle_rarity,
          p.prize AS puzzle_prize,
          up.collected_count,
          up.collected,
          pi.id AS id,
          pi.url AS url,
          pi.position,
          upi.create_date AS create_date
      FROM
          "bonus-games".puzzle_items pi
      LEFT JOIN
          users_puzzle_items upi
          ON pi.id = upi.puzzle_item_id
          AND upi.user_id = :user_id
      LEFT JOIN
          users_puzzle up
          ON up.puzzle_id = pi.puzzle_id
          AND up.user_id = :user_id
      INNER JOIN
          puzzle p
          ON p.id = pi.puzzle_id
      INNER JOIN
          puzzle_rarities pr
          ON p.rarity_id = pr.id
      WHERE
          pr."type" = :rarity_type
      """;

  public static final String FIND_ALL_ENRICH_USER_WITHOUT_RARITY = """
      SELECT
          p.id AS puzzle_id,
          p.url AS puzzle_url,
          p.game_type AS puzzle_game_type,
          p.name AS puzzle_name,
          p."type" AS puzzle_type,
          pr."type" AS puzzle_rarity,
          p.prize AS puzzle_prize,
          up.collected_count,
          up.collected,
          pi.id AS id,
          pi.url AS url,
          pi.position,
          upi.create_date AS create_date
      FROM
          "bonus-games".puzzle_items pi
      LEFT JOIN
          users_puzzle_items upi
          ON pi.id = upi.puzzle_item_id
          AND upi.user_id = :user_id
      LEFT JOIN
          users_puzzle up
          ON up.puzzle_id = pi.puzzle_id
          AND up.user_id = :user_id
      INNER JOIN
          puzzle p
          ON p.id = pi.puzzle_id
      INNER JOIN
          puzzle_rarities pr
          ON p.rarity_id = pr.id
      """;

  public static final String CREATE_QUERY = """
      INSERT INTO "bonus-games".puzzle_items(puzzle_id, position, url)
      VALUES (:puzzleId, :position, :url)
      returning id
      """;

  public static final String UPDATE_QUERY = """
      UPDATE "bonus-games".puzzle_items
      set url = :url
      where position = :position and puzzle_id = :puzzleId
      """;

  public static final String FIND_BY_ID = """
      SELECT id, position, url, puzzle_id
      FROM "bonus-games".puzzle_items
      WHERE puzzle_id = :puzzleId
      """;
  public static final String FIND_BY_ITEM_ID = """
      SELECT id, position, url, puzzle_id
      FROM "bonus-games".puzzle_items
      WHERE id=:id
      """;

  public static final String UPDATE_ITEM_BY_ID = """
      UPDATE "bonus-games".puzzle_items
      SET url = :url,
      position = :position,
      puzzle_id=:puzzle_id
      WHERE id = :id
      returning id
      """;

}
