package org.orglot.gosloto.bonus.games.repo;

import reactor.core.publisher.Mono;

public interface UsersPuzzleItemRepository {

  Mono<Integer> save(Long puzzleItemId, Long puzzleId, Long userId);

  Mono<Integer> deleteAllByPuzzleIdAndUserId(Long puzzleId, Long userId);

  Mono<Boolean> isCollectedByUser(Long userId, Long puzzleId);
}
