package org.orglot.gosloto.bonus.games.repo;

import lombok.NonNull;
import org.orglot.gosloto.bonus.games.model.UserPuzzle;
import org.orglot.gosloto.bonus.games.model.UserPuzzleWithCollectedCount;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UsersPuzzleRepository {

  Mono<Integer> save(UserPuzzle userPuzzle);

  Mono<Integer> saveOrUpdateCollectedTrue(@NonNull UserPuzzle userPuzzle);

  Mono<UserPuzzle> findByUserIdAndPuzzleId(Long userId, Long puzzleId);

  Mono<Integer> updateCollectedStatusFalseAndIncrementCollectedCount(Long userId, Long puzzleId);

  Flux<UserPuzzleWithCollectedCount> findCollectedAtLeastOnceByUserId(Long userId);
}
