package org.orglot.gosloto.bonus.games.repo.impl;

import io.r2dbc.spi.Row;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.puzzle.PuzzleItem;
import org.orglot.gosloto.bonus.games.model.PuzzleItemWithSessionCreateDate;
import org.orglot.gosloto.bonus.games.repo.PuzzleItemRepository;
import org.orglot.gosloto.bonus.games.repo.impl.rowmapper.PuzzleItemRowMapper;
import org.orglot.gosloto.bonus.games.repo.impl.rowmapper.PuzzleItemWithSessionCreateDateRowMapper;
import org.orglot.gosloto.bonus.games.repo.impl.sql.PuzzleItemRepositorySql;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Repository
@AllArgsConstructor
public class PuzzleItemRepositoryImpl implements PuzzleItemRepository {

  private final DatabaseClient databaseClient;

  /**
   * Поиск случайного не выданного элемента пазла пользователя
   *
   * @param userId   - идентификатор пользователя
   * @param rarityId - идентификатор редкости
   * @return элемент пазла или empty
   */
  @Override
  public Mono<PuzzleItem> findRandomByUserNotUsed(Long userId, Long rarityId) {
    var a = databaseClient.sql(PuzzleItemRepositorySql.FIND_RANDOM_BY_USER_NOT_USED)
        .bind("user_id", userId);
    if (Objects.isNull(rarityId)) {
      return a.bindNull("rarity_id", Long.class)
          .map(PuzzleItemRowMapper.MAPPING_FUNCTION)
          .one();
    }
    return a.bind("rarity_id", rarityId)
        .map(PuzzleItemRowMapper.MAPPING_FUNCTION)
        .one();
  }

  /**
   * Поиск всех выданных элементов пазла пользователя с обогащенными данными о пазле и дате выдачи
   *
   * @param userId - идентификатор пользователя
   * @return элементы пазла или empty
   */
  @Override
  public Flux<PuzzleItemWithSessionCreateDate> findAllEnrichUser(Long userId, String rarityType) {
    if (Objects.nonNull(rarityType)) {
      return databaseClient.sql(PuzzleItemRepositorySql.FIND_ALL_ENRICH_USER_WITH_RARITY)
          .bind("user_id", userId)
          .bind("rarity_type", rarityType)
          .map(PuzzleItemWithSessionCreateDateRowMapper.MAPPING_FUNCTION)
          .all();
    }

    return databaseClient.sql(PuzzleItemRepositorySql.FIND_ALL_ENRICH_USER_WITHOUT_RARITY)
        .bind("user_id", userId)
        .map(PuzzleItemWithSessionCreateDateRowMapper.MAPPING_FUNCTION)
        .all();
  }

  /**
   * Сохранение фрагмента пазла
   *
   * @param puzzleId     идентификатор пазла
   * @param puzzleItemId идентификатор фрагмента пазла
   * @param url          url фрагмента пазла
   * @return идентификатор созданного элемента
   */
  @Override
  public Mono<Long> create(Long puzzleId, Integer puzzleItemId, String url) {
    return databaseClient.sql(PuzzleItemRepositorySql.CREATE_QUERY)
        .bind("puzzleId", puzzleId)
        .bind("position", puzzleItemId)
        .bind("url", url)
        .map((row, rowNumber) -> row.get("id", Long.class))
        .one();
  }

  /**
   * Получение фрагментов пазла
   *
   * @param puzzleId идентификатор пазла
   * @return фрагменты пазла
   */
  @Override
  public Flux<PuzzleItem> getItems(Long puzzleId) {
    return databaseClient.sql(PuzzleItemRepositorySql.FIND_BY_ID)
        .bind("puzzleId", puzzleId)
        .map((row, rowMetaData) -> toPuzzleItem(row))
        .all();
  }

  /**
   * Обновление фрагментов пазлов
   *
   * @param puzzleId     идентификатор пазла
   * @param position     порядковый номер фрагмента пазла
   * @param url          url фрагмента
   */
  @Override
  public Mono<Integer> update(Long puzzleId, Integer position, String url) {
    return databaseClient.sql(PuzzleItemRepositorySql.UPDATE_QUERY)
        .bind("puzzleId", puzzleId)
        .bind("position", position)
        .bind("url", url)
        .fetch()
        .rowsUpdated();
  }

  /**
   * Updates a puzzle item by its id.
   *
   * @param puzzleItem the puzzle item to be updated
   * @return a Mono of Long
   */
  @Override
  public Mono<Long> updateById(PuzzleItem puzzleItem) {
    return databaseClient.sql(PuzzleItemRepositorySql.UPDATE_ITEM_BY_ID)
        .bind("url", puzzleItem.getUrl())
        .bind("position", puzzleItem.getPosition())
        .bind("puzzle_id", puzzleItem.getPuzzleId())
        .bind("id", puzzleItem.getId())
        .map((row, rowMetaData) -> row.get("id", Long.class))
        .one();
  }

  /**
   * Retrieves a puzzle item by its id.
   *
   * @param id the id of the puzzle item
   * @return a Mono of PuzzleItem
   */
  @Override
  public Mono<PuzzleItem> getById(Long id) {
    return databaseClient.sql(PuzzleItemRepositorySql.FIND_BY_ITEM_ID)
        .bind("id", id)
        .map((row, rowMetaData) -> toPuzzleItem(row))
        .one();
  }

  private PuzzleItem toPuzzleItem(Row row) {
    return PuzzleItem.builder()
        .id(row.get("id", Long.class))
        .position(row.get("position", Integer.class))
        .url(row.get("url", String.class))
        .puzzleId(row.get("puzzle_id", Long.class))
        .build();
  }

}
