package org.orglot.gosloto.bonus.games.service.puzzle.collection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.puzzle.PuzzleItem;
import org.orglot.bonus.games.model.puzzle.PuzzleItemRequest;
import org.orglot.gosloto.bonus.games.model.PuzzleItemWithSessionCreateDate;
import org.orglot.gosloto.bonus.games.repo.PuzzleItemRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PuzzleItemService {

  private final PuzzleItemRepository puzzleItemRepository;

  /**
   * Получение случайного элемента пазла конкретного пользователя указанной редкости ранее еще не полученного и из несобранного пазла,
   * в ином случае empty.
   *
   * @param userId   идентификатор пользователя
   * @param rarityId идентификатор редкости
   * @return элемент пазла или empty
   */
  public Mono<PuzzleItem> getRandomNotUsed(Long userId, Long rarityId) {
    return puzzleItemRepository.findRandomByUserNotUsed(userId, rarityId)
        .onErrorResume(e -> {
          log.error("Error when get random not used puzzle item for user {}, error: ", userId, e);
          return Mono.empty();
        });
  }

  protected Mono<List<PuzzleItemWithSessionCreateDate>> findAllEnrichUser(Long userId, String rarityType) {
    return puzzleItemRepository.findAllEnrichUser(userId, rarityType)
        .collectList()
        .onErrorResume(e -> {
          log.error("Error when findAllEnrichUser {}, error: ", userId, e);
          return Mono.empty();
        });
  }

  /**
   * Сохранение фрагментов пазла
   *
   * @param puzzleId    идентификатор пазла
   * @param puzzleItems фрагменты для пазла
   */
  public Mono<Void> save(Long puzzleId, List<PuzzleItemRequest> puzzleItems) {
    return Flux.fromIterable(puzzleItems)
        .flatMap(puzzleItem ->
            puzzleItemRepository.create(puzzleId, puzzleItem.getPuzzleItemId(), puzzleItem.getUrl()))
        .then();
  }

  public Mono<Long> create(PuzzleItem puzzleItem) {
    return puzzleItemRepository.create(puzzleItem.getPuzzleId(), puzzleItem.getPosition(), puzzleItem.getUrl());
  }

  /**
   * Получение фрагментов пазлов
   *
   * @param puzzleId идентификатор пазла
   * @return фрагменты пазла
   */
  public Flux<PuzzleItem> getItems(Long puzzleId) {
    return puzzleItemRepository.getItems(puzzleId);
  }

  /**
   * Обновление фрагмента пазла
   *
   * @param puzzleId    идентификатор пазла
   * @param puzzleItems список фрагментов
   * @return количество обновленных записей
   */
  public Mono<Long> update(Long puzzleId, List<PuzzleItemRequest> puzzleItems) {
    return Flux.fromIterable(puzzleItems)
        .flatMap(puzzleItem ->
            puzzleItemRepository.update(puzzleId, puzzleItem.getPuzzleItemId(), puzzleItem.getUrl())
        )
        .count();
  }

  /**
   * Updates a puzzle item by its id.
   *
   * @param puzzle the puzzle item to be updated
   * @return a Mono of Long
   */
  public Mono<Long> updateById(PuzzleItem puzzle) {
    return puzzleItemRepository.updateById(puzzle);
  }

  /**
   * Retrieves a puzzle item by its id.
   *
   * @param id the id of the puzzle item
   * @return a Mono of PuzzleItem
   */
  public Mono<PuzzleItem> getById(Long id) {
    return puzzleItemRepository.getById(id);
  }

}
