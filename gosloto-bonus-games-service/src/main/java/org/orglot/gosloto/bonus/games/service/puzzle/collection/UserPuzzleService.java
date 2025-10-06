package org.orglot.gosloto.bonus.games.service.puzzle.collection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orglot.gosloto.bonus.games.model.UserPuzzle;
import org.orglot.gosloto.bonus.games.model.UserPuzzleWithCollectedCount;
import org.orglot.gosloto.bonus.games.repo.UsersPuzzleRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPuzzleService {
  private final UsersPuzzleRepository usersPuzzleRepository;

  protected Flux<UserPuzzleWithCollectedCount> findCollectedPuzzlesAtLeastOnceByUserId(Long userId) {
    return usersPuzzleRepository.findCollectedAtLeastOnceByUserId(userId)
        .onErrorResume(e ->
            Mono.error(new RuntimeException(
                String.format("Error when findCollectedPuzzlesAtLeastOnceByUserId %s %s", userId, e.getMessage()))));
  }

  protected Mono<Integer> updateCollectedStatusFalseAndIncrementCollectedCount(Long userId, Long puzzleId) {
    return usersPuzzleRepository.updateCollectedStatusFalseAndIncrementCollectedCount(userId, puzzleId)
        .onErrorResume(e ->
            Mono.error(new RuntimeException(String.format("Error when update collected user puzzle %s %s", userId, puzzleId))))
        .switchIfEmpty(Mono.error(
            new RuntimeException(String.format("Error when update collected user puzzle (empty) %s %s", userId, puzzleId))
        ));
  }

  protected Mono<Integer> saveOrUpdateUserPuzzleCollectedStatusTrue(Long userId, Long puzzleId) {
    return usersPuzzleRepository.saveOrUpdateCollectedTrue(UserPuzzle.buildCollectedUserPuzzle(userId, puzzleId))
        .onErrorResume(e ->
            Mono.error(new RuntimeException(String.format("Error when save collected user puzzle %s %s", userId, puzzleId))));
  }

  protected Mono<Integer> saveUserPuzzle(Long userId, Long puzzleId, Instant exchangeDate) {
    return usersPuzzleRepository.save(UserPuzzle.buildUserPuzzle(userId, puzzleId, Boolean.FALSE, 1, exchangeDate))
        .onErrorResume(e ->
            Mono.error(new RuntimeException(String.format("Error when save user puzzle %s %s", userId, puzzleId))));
  }

  protected Mono<UserPuzzle> findUserPuzzle(Long userId, Long puzzleId) {
    return usersPuzzleRepository.findByUserIdAndPuzzleId(userId, puzzleId)
        .onErrorResume(e ->
            Mono.error(new RuntimeException(
                String.format("Error when findByUserIdAndPuzzleId %s %s %s", userId, puzzleId, e.getMessage()))));
  }

}
