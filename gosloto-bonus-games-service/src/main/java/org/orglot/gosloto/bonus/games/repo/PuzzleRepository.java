package org.orglot.gosloto.bonus.games.repo;

import org.orglot.bonus.games.model.puzzle.Puzzle;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PuzzleRepository {

    Mono<Integer> findById(Long id);

    /**
     * Получить пазл по идентификатору.
     *
     * @param puzzleId идентификатор пазла
     * @return объект {@link Puzzle}, либо ошибка, если пазл не найден
     */
    Mono<Puzzle> getPuzzle(Long puzzleId);

    /**
     * Получить список пазлов с фильтрацией и пагинацией.
     *
     * @param offset оффсет страницы
     * @param limit  лимит страницы
     * @param name   фильтр по имени (может быть null)
     * @param rarity фильтр по редкости (может быть null)
     * @return поток пазлов, удовлетворяющих условиям
     */
    Flux<Puzzle> getPuzzles(int offset, int limit, String name, String rarity);

    /**
     * Сохранить новый пазл.
     *
     * @param name     название пазла
     * @param url      URL изображения пазла
     * @param gameType тип игры
     * @param rarityId идентификатор редкости
     * @param type     тип пазла
     * @param prize    награда за пазл
     * @return идентификатор созданного пазла
     */
    Mono<Long> save(String name, String url, String gameType, String rarityId, String type, Integer prize);

    /**
     * Обновить пазл по объекту.
     *
     * @param puzzle объект пазла с обновлёнными данными
     * @return идентификатор обновлённого пазла
     */
    Mono<Long> updatePuzzleById(Puzzle puzzle);

    /**
     * Обновить данные пазла по идентификатору.
     *
     * @param id       идентификатор пазла
     * @param name     новое имя
     * @param url      новый URL
     * @param gameType новый тип игры
     * @param rarity   новая редкость
     * @param type     новый тип пазла
     * @param prize    новая награда
     * @return количество обновлённых записей
     */
    Mono<Integer> update(Long id, String name, String url, String gameType, String rarity, String type, Integer prize);
    
}
