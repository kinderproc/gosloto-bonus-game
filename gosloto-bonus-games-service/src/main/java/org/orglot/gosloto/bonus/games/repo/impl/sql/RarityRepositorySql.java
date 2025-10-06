package org.orglot.gosloto.bonus.games.repo.impl.sql;

public final class RarityRepositorySql {

  public static final String FIND_ALL_BY_DISPLAY = """
            select * from puzzle_rarities
            where display = :display
            """;

  public static final String FIND_ALL = """
            select * from puzzle_rarities
            """;
}
