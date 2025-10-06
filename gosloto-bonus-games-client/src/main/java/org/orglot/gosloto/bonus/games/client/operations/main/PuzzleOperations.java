package org.orglot.gosloto.bonus.games.client.operations.main;

import org.orglot.bonus.games.model.puzzle.Puzzle;
import org.orglot.bonus.games.model.puzzle.PuzzleRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PuzzleOperations {

  /**
   * Поиск пазлов.
   *
   * @param offset       смещение (0-based)
   * @param limit        количество элементов
   * @param searchString строка поиска (может быть null)
   * @param rarity       редкость (может быть null)
   */
  Flux<Puzzle> search(int offset, int limit, String searchString, String rarity);

  /**
   * Получить пазл по его идентификатору.
   *
   * @param puzzleId идентификатор пазла
   * @return Mono пазл, или ошибка, если пазл не найден
   */
  Mono<Puzzle> getPuzzle(Long puzzleId);

  /**
   * Сохранить новый пазл.
   *
   * @param request данные пазла
   * @return идентификатор созданного пазла
   */
  Mono<Long> save(PuzzleRequest request);

  /**
   * Обновить данные пазла по идентификатору.
   *
   * @param puzzleId идентификатор пазла
   * @param request  новые данные пазла
   * @return количество обновлённых записей
   */
  Mono<Integer> update(Long puzzleId, PuzzleRequest request);

  /**
   * Обновить пазл целиком по переданному объекту.
   *
   * @param puzzle обновлённый объект пазла
   * @return идентификатор обновлённого пазла
   */
  Mono<Long> updatePuzzleById(Puzzle puzzle);

}
