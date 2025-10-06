package org.orglot.gosloto.bonus.games.service.puzzle.collection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orglot.gosloto.bonus.games.repo.UsersPuzzleItemRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPuzzleItemService {

  private final UsersPuzzleItemRepository usersPuzzleItemRepository;

  public Mono<Integer> saveUserPuzzleItem(Long userId, Long puzzleId, Long puzzleItemId) {
    return usersPuzzleItemRepository.save(puzzleItemId, puzzleId, userId)
        .onErrorResume(e ->
            Mono.error(new RuntimeException(String.format("Error when save user puzzle item %s %s %s", userId, puzzleId, puzzleItemId))));
  }

  protected Mono<Integer> deleteUserPuzzleItems(Long userId, Long puzzleId) {
    return usersPuzzleItemRepository.deleteAllByPuzzleIdAndUserId(puzzleId, userId)
        .onErrorResume(e ->
            Mono.error(new RuntimeException(String.format("Error when deleteUserPuzzleItems %s, %s", puzzleId, userId))));
  }

  protected Mono<Boolean> isCollectedUserPuzzle(Long userId, Long puzzleId) {
    return usersPuzzleItemRepository.isCollectedByUser(userId, puzzleId)
        .onErrorResume(e ->
            Mono.error(new RuntimeException(String.format("Error when check collected user puzzle %s %s", userId, puzzleId))))
        .switchIfEmpty(
            Mono.error(new RuntimeException(String.format("Error when check collected user puzzle (empty) %s %s", userId, puzzleId))));
  }

}
