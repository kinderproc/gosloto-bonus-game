package org.orglot.gosloto.bonus.games.client.operations.main;

import org.orglot.bonus.games.model.puzzle.PuzzleItem;
import org.orglot.bonus.games.model.puzzle.PuzzleItemRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PuzzleItemsOperations {

  /**
   * Сохранить список элементов пазла.
   *
   * @param puzzleId идентификатор пазла
   * @param items    список элементов для сохранения
   * @return пустой результат при успешном выполнении
   */
  Mono<Void> savePuzzleItems(Long puzzleId, List<PuzzleItemRequest> items);

  /**
   * Создать новый элемент пазла.
   *
   * @param item элемент пазла
   * @return идентификатор созданного элемента
   */
  Mono<Long> create(PuzzleItem item);

  /**
   * Получить все элементы пазла.
   *
   * @param puzzleId идентификатор пазла
   * @return поток элементов пазла
   */
  Flux<PuzzleItem> getItems(Long puzzleId);

  /**
   * Обновить список элементов пазла.
   *
   * @param puzzleId идентификатор пазла
   * @param items    список новых элементов
   * @return идентификатор пазла после обновления
   */
  Mono<Long> updatePuzzleItems(Long puzzleId, List<PuzzleItemRequest> items);

  /**
   * Обновить элемент пазла по его идентификатору.
   *
   * @param item элемент пазла с обновлёнными данными
   * @return идентификатор обновлённого элемента
   */
  Mono<Long> updateById(PuzzleItem item);

  /**
   * Получить элемент пазла по его идентификатору.
   *
   * @param id идентификатор элемента
   * @return найденный элемент пазла
   */
  Mono<PuzzleItem> getById(Long id);

}
