package org.orglot.gosloto.bonus.games.service.puzzle.collection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.response.BonusGameCollection;
import org.orglot.bonus.games.model.response.BonusGameCollectionItem;
import org.orglot.gosloto.bonus.client.operation.bonusrefill.model.BonusRefillReason;
import org.orglot.gosloto.bonus.games.model.PuzzleItemWithSessionCreateDate;
import org.orglot.gosloto.bonus.games.model.PuzzleKey;
import org.orglot.gosloto.bonus.games.model.PuzzleRarityAndCollectedCount;
import org.orglot.gosloto.bonus.games.model.UserPuzzle;
import org.orglot.gosloto.bonus.games.model.UserPuzzleWithCollectedCount;
import org.orglot.gosloto.bonus.games.service.PurchaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PuzzleCollectionService {
  private static final String REFILL_DESCRIPTION = "collection_assemble";
  private final PurchaseService purchaseService;
  private final TransactionalOperator transactionalOperator;
  private final PuzzleService puzzleService;
  private final PuzzleItemService puzzleItemService;
  private final UserPuzzleService userPuzzleService;
  private final UserPuzzleItemService userPuzzleItemService;

  public Mono<List<BonusGameCollection>> getUserCollection(Long userId, String rarityType) {
    return puzzleItemService.findAllEnrichUser(userId, rarityType)
        .map(puzzleItems -> puzzleItems.stream()
            .collect(Collectors.groupingBy(
                item -> new PuzzleKey(
                    item.getPuzzleId(),
                    item.getPuzzleName(),
                    item.getPuzzlePrize(),
                    item.getPuzzleType(),
                    item.getPuzzleGameType(),
                    item.getPuzzleRarity(),
                    item.getPuzzleUrl(),
                    item.getUserPuzzleCollected(),
                    item.getUserPuzzleCollectedCount()
                )
            ))
            .entrySet()
            .stream()
            .map(entry -> buildCollection(entry.getKey(),
                entry.getValue().stream().filter(item -> Objects.nonNull(item.getCreateDate())).toList())
            )
            .sorted(Comparator.comparing((BonusGameCollection s) -> Objects.nonNull(s.getPuzzleItems()) ?
                    s.getPuzzleItems().size() : 0, Comparator.reverseOrder())
                .thenComparing(BonusGameCollection::getPuzzleID,
                    Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList()));
  }

  /**
   * Список пазлов пользователя сгруппированных по типу с количеством обменов по каждому типу редкости
   *
   * @param userId идентификатор пользователя
   * @return список
   */
  public Flux<UserPuzzleWithCollectedCount> getUserCollectionGroupingByType(Long userId) {
    return userPuzzleService.findCollectedPuzzlesAtLeastOnceByUserId(userId)
        .groupBy(UserPuzzleWithCollectedCount::getType)
        .flatMap(userPuzzleTypeGroupFlux -> userPuzzleTypeGroupFlux.collectList()
            .map(this::buildUserPuzzleWithCollectedCount)
        );
  }

  public Mono<Integer> collectAndRefillBonusTransactional(Long userId, Long puzzleId, String mobile) {
    return transactionalOperator.execute(tx -> collectAndRefillBonus(userId, puzzleId, mobile))
        .single()
        .doOnError(error -> log.error("Error when collectAndRefillBonus: userId {}, puzzleId {}, error {}", userId, puzzleId, error));
  }

  public Mono<Boolean> checkCollectedPuzzleItemsAndCreateUserPuzzleCollectedIfNeeded(Long userId, Long puzzleId) {
    return userPuzzleItemService.isCollectedUserPuzzle(userId, puzzleId)
        .flatMap(isCollected -> {
          if (isCollected) {
            return userPuzzleService.saveUserPuzzle(userId, puzzleId, Instant.now()).then(Mono.just(Boolean.TRUE));
          }
          return Mono.just(Boolean.FALSE);
        });
  }

  public Mono<Boolean> checkCollectedByUserIdAndUpdateCollectedIfNeeded(Long userId, Long puzzleId) {
    return userPuzzleItemService.isCollectedUserPuzzle(userId, puzzleId)
        .flatMap(isCollected -> {
          if (isCollected) {
            return userPuzzleService.saveOrUpdateUserPuzzleCollectedStatusTrue(userId, puzzleId)
                .then(Mono.just(Boolean.TRUE));
          }
          return Mono.just(Boolean.FALSE);
        });
  }

  public Mono<Integer> collectAndRefillBonus(Long userId, Long puzzleId, String mobile) {
    return userPuzzleService.findUserPuzzle(userId, puzzleId)
        .flatMap(this::checkCollectedUserPuzzle)
        .switchIfEmpty(checkCollectedPuzzleItemsAndCreateUserPuzzleCollectedIfNeeded(userId, puzzleId))
        .flatMap(isCollected -> isCollected ?
            userPuzzleItemService.deleteUserPuzzleItems(userId, puzzleId).then(refillBonusToUserForCollectedPuzzle(puzzleId, mobile)) :
            Mono.just(0)
        );
  }

  private Mono<Integer> refillBonusToUserForCollectedPuzzle(Long puzzleId, String mobile) {
    return puzzleService.findPuzzleById(puzzleId)
        .flatMap(puzzlePrize ->
            Mono.fromCallable(() -> purchaseService.refillBonus(Objects.nonNull(puzzlePrize) ? puzzlePrize : 0,
                    BonusRefillReason.ZBONUS_SHOP.name(), mobile, UUID.randomUUID(), null, null, REFILL_DESCRIPTION))
                .map(isSuccess -> {
                  if (!isSuccess) {
                    throw new RuntimeException(String.format("Error when refillBonusToUserForCollectedPuzzle %s %s", puzzleId, mobile));
                  }
                  return puzzlePrize;
                })
        );
  }

  private Mono<Boolean> checkCollectedUserPuzzle(UserPuzzle userPuzzle) {
    return userPuzzle.getCollected() ?
        userPuzzleService.updateCollectedStatusFalseAndIncrementCollectedCount(userPuzzle.getUserId(), userPuzzle.getPuzzleId())
            .thenReturn(true) :
        Mono.fromRunnable(
                () -> log.warn("This puzzle {} not collected for user {}", userPuzzle.getPuzzleId(), userPuzzle.getUserId())
            )
            .thenReturn(false);
  }

  private BonusGameCollection buildCollection(PuzzleKey puzzle, List<PuzzleItemWithSessionCreateDate> items) {
    var collection = BonusGameCollection.builder()
        .puzzleURL(puzzle.getPuzzleUrl())
        .puzzleID(puzzle.getPuzzleId())
        .gameType(puzzle.getPuzzleGameType())
        .name(puzzle.getPuzzleName())
        .rarity(puzzle.getPuzzleRarity())
        .prize(puzzle.getPuzzlePrize())
        .collected(puzzle.getUserPuzzleCollectedCount())
        .type(puzzle.getPuzzleType())
        .status(puzzle.getUserPuzzleCollected());
    if (Objects.nonNull(items)) {
      collection.createDate(getCreationDate(items));
      collection.puzzleItems(items.stream()
          .map(item -> new BonusGameCollectionItem(item.getPuzzleId(), item.getUrl(), item.getPosition()))
          .sorted(Comparator.comparing(BonusGameCollectionItem::getId))
          .collect(Collectors.toList()));
    }
    return collection.build();
  }

  private Instant getCreationDate(List<PuzzleItemWithSessionCreateDate> puzzles) {
    if (Objects.nonNull(puzzles)) {
      return puzzles.stream()
          .filter(p -> Objects.nonNull(p.getCreateDate()))
          .sorted(Comparator.comparing(PuzzleItemWithSessionCreateDate::getCreateDate))
          .map(PuzzleItemWithSessionCreateDate::getCreateDate)
          .findFirst().orElse(null);
    }
    return null;
  }

  private UserPuzzleWithCollectedCount buildUserPuzzleWithCollectedCount(
      List<UserPuzzleWithCollectedCount> userPuzzleWithCollectedCountList
  ) {
    return UserPuzzleWithCollectedCount.builder()
        .url(userPuzzleWithCollectedCountList.stream().findFirst().map(UserPuzzleWithCollectedCount::getUrl).orElse(null))
        .type(userPuzzleWithCollectedCountList.stream().findFirst().map(UserPuzzleWithCollectedCount::getType).orElse(null))
        .name(userPuzzleWithCollectedCountList.stream().findFirst().map(UserPuzzleWithCollectedCount::getName).orElse(null))
        .collectedCountsByRarities(userPuzzleWithCollectedCountList.stream()
            .map(up -> PuzzleRarityAndCollectedCount.builder()
                .rarity(up.getRarity())
                .collectedCount(up.getCollectedCount())
                .build())
            .collect(Collectors.toList())
        )
        .build();
  }
}
