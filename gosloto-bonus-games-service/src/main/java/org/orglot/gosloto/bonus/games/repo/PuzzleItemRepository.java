package org.orglot.gosloto.bonus.games.repo;

import org.orglot.bonus.games.model.puzzle.PuzzleItem;
import org.orglot.gosloto.bonus.games.model.PuzzleItemWithSessionCreateDate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PuzzleItemRepository {

  Mono<PuzzleItem> findRandomByUserNotUsed(Long userId, Long rarityId);

  Flux<PuzzleItemWithSessionCreateDate> findAllEnrichUser(Long userId, String rarityType);

  /**
   * Создать новый элемент пазла.
   *
   * @param puzzleId    идентификатор пазла
   * @param puzzleItemId позиция (номер) элемента в пазле
   * @param url         URL изображения элемента
   * @return идентификатор созданного элемента
   */
  Mono<Long> create(Long puzzleId, Integer puzzleItemId, String url);

  /**
   * Получить все элементы для указанного пазла.
   *
   * @param puzzleId идентификатор пазла
   * @return поток элементов пазла
   */
  Flux<PuzzleItem> getItems(Long puzzleId);

  /**
   * Обновить элемент пазла.
   *
   * @param puzzleId идентификатор пазла
   * @param position позиция (номер) элемента
   * @param url      новый URL изображения
   * @return количество обновлённых записей
   */
  Mono<Integer> update(Long puzzleId, Integer position, String url);

  /**
   * Обновить элемент пазла по идентификатору.
   *
   * @param puzzleItem объект элемента пазла
   * @return идентификатор обновлённого элемента
   */
  Mono<Long> updateById(PuzzleItem puzzleItem);

  /**
   * Получить элемент пазла по его идентификатору.
   *
   * @param id идентификатор элемента
   * @return найденный элемент пазла
   */
  Mono<PuzzleItem> getById(Long id);

}
