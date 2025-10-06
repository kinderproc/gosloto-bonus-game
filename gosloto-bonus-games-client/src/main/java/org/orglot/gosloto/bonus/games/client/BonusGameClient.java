package org.orglot.gosloto.bonus.games.client;

import org.orglot.gosloto.bonus.games.client.operations.main.BonusGameOperations;
import org.orglot.gosloto.bonus.games.client.operations.main.PuzzleCollectionOperations;
import org.orglot.gosloto.bonus.games.client.operations.main.PuzzleItemsOperations;
import org.orglot.gosloto.bonus.games.client.operations.main.PuzzleOperations;
import org.orglot.gosloto.bonus.games.client.operations.main.RewardOperations;

public interface BonusGameClient {
    BonusGameOperations bonusGameOperations();

    RewardOperations rewardOperations();

    PuzzleCollectionOperations puzzleCollectionOperations();

    /**
     * Доступ к операциям с элементами пазлов.
     *
     * @return интерфейс PuzzleItemsOperations
     */
    PuzzleItemsOperations puzzleItemsOperations();

    /**
     * Доступ к операциям с пазлами.
     *
     * @return интерфейс PuzzleOperations
     */
    PuzzleOperations puzzleOperations();
}
