package org.orglot.gosloto.bonus.games.client.operations.main;

import org.orglot.bonus.games.model.puzzle.PuzzleModel;
import org.orglot.bonus.games.model.response.Rarity;
import org.orglot.bonus.games.model.response.UserPuzzleWithCollectedCount;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PuzzleCollectionOperations {

  /**
   * Метод снимает признак, что коллекция пазлов пользователя собрана, увеличивает счетчик сбора коллекции и начисляет пользователю бонусы
   * @param userId ид пользователя
   * @param puzzleId ид пазла
   * @param mobile номер телефона пользователя
   */
  Mono<Void> collectPuzzleAndRefillBonusToUser(long userId, long puzzleId, String mobile);

  /**
   * Метод возвращает редкости пазлов с возможностью фильтрации по признаку отображения
   * @param display - признак отображения
   * @return список элементов справочника редкости пазлов
   */
  Flux<Rarity> getRaritiesByDisplay(Boolean display);

  /**
   * Возвращает коллекции пазлов пользователя сгруппированных по типу
   * и с информацией по количеству обмена по каждой редкости из данной группы
   * @param userId ид пользователя
   *
   * @return коллекции пазлов пользователя сгруппированных по типу
   */
  Flux<UserPuzzleWithCollectedCount> getUserCollectionGroupingByType(long userId);

  /**
   * Формирование и начисление случайного(не занятого) набора пазлов определенного типа
   * @param userId идентификатор пользователя в пользу которого начисляют пазлы
   * @param puzzleId тип пазла
   * @param count количество пазлов
   * @return начисленный набор пазлов
   */
  Flux<PuzzleModel> randomPuzzles(long userId, Long puzzleId, int count);
}
