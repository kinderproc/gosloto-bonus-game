package org.orglot.gosloto.bonus.games.service.puzzle.collection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.puzzle.Puzzle;
import org.orglot.bonus.games.model.puzzle.PuzzleRequest;
import org.orglot.gosloto.bonus.games.repo.PuzzleRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class PuzzleService {

  private final PuzzleRepository puzzleRepository;

  protected Mono<Integer> findPuzzleById(Long puzzleId) {
    return puzzleRepository.findById(puzzleId)
        .switchIfEmpty(Mono.error(new RuntimeException(String.format("Error when find puzzle (empty) %s", puzzleId))))
        .onErrorResume(e ->
            Mono.error(new RuntimeException(String.format("Error when find puzzle %s", puzzleId))));
  }

  /**
   * Поиск пазлов
   *
   * @return список пазлов
   */
  public Flux<Puzzle> search(int offset, int limit, String searchString, String rarity) {
    return puzzleRepository.getPuzzles(offset, limit, searchString, rarity);
  }

  /**
   * Получение пазла
   *
   * @param puzzleId идентификатор пазла
   * @return пазл
   */
  public Mono<Puzzle> getPuzzle(Long puzzleId) {
    return puzzleRepository.getPuzzle(puzzleId);
  }

  /**
   * Сохранение пазла
   *
   * @param puzzleRequest Данные пазла
   * @return результат сохранения
   */
  public Mono<Long> save(PuzzleRequest puzzleRequest) {
    return puzzleRepository.save(puzzleRequest.getName(), puzzleRequest.getUrl(), puzzleRequest.getGameType(),
            puzzleRequest.getRarity(), puzzleRequest.getType(), puzzleRequest.getPrize());
  }

  /**
   * Updates a puzzle.
   *
   * @param puzzleId      the id of the puzzle
   * @param puzzleRequest the puzzle request
   * @return a Mono of Integer
   */
  public Mono<Integer> update(Long puzzleId, PuzzleRequest puzzleRequest) {
    return puzzleRepository.update(puzzleId, puzzleRequest.getName(), puzzleRequest.getUrl(),
        puzzleRequest.getGameType(), puzzleRequest.getRarity(), puzzleRequest.getType(),
        puzzleRequest.getPrize());
  }

  /**
   * Updates a puzzle by its id.
   *
   * @param puzzle the puzzle to be updated
   * @return a Mono of Long
   */
  public Mono<Long> updatePuzzleById(Puzzle puzzle) {
    return puzzleRepository.updatePuzzleById(puzzle);
  }

}
