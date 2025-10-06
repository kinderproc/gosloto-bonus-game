package org.orglot.gosloto.bonus.games.service.strategy.rocketbingo;

import org.orglot.gosloto.bonus.games.service.strategy.rocketbingo.enums.RocketbingoTypes;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
public class RocketbingoCalculateWinCombinationProcessor {

  /** Класс для генерации случайных чисел в итоговой комбинации */
  private static final Random RANDOM = new Random();

  /** Максимальное количество чисел в комбинации рокетбинго */
  private static final int GAME_DIGIT_COUNT = 24;

  /** Минимальное допустимое значение числа в комбинации рокетбинго */
  private static final int MIN_NUMBER = 1;

  /** Максимальное допустимое значение числа в комбинации рокетбинго */
  private static final int MAX_NUMBER = 75;

  /** Максимальное количество чисел для генерации выигрышной комбинации */
  private static final int MAX_NUMBER_COUNT = 35;

  public List<Integer> calculate(List<Integer> combination, RocketbingoTypes type) {
    if (combination.size() != GAME_DIGIT_COUNT) {
      throw new IllegalArgumentException("Combination must contain at least 24 numbers.");
    }
    if (!combination.stream().allMatch(num -> num >= MIN_NUMBER && num <= MAX_NUMBER)) {
      throw new IllegalArgumentException("Combination must contain numbers from 1 to 75.");
    }
    if (type == RocketbingoTypes.WITHOUT) {
      return generateLoseCombination(combination);
    }
    List<Integer> winIndexNumbers =
        type.getWinningNumbers()
            .get(RANDOM.nextInt(type.getWinningNumbers().size()));
    return generateWinCombination(combination, winIndexNumbers, type);
  }

  private List<Integer> generateWinCombination(List<Integer> player, List<Integer> indexes, RocketbingoTypes type) {
    List<Integer> winCombination = new ArrayList<>(Collections.nCopies(MAX_NUMBER_COUNT, null));
    var used = new HashSet<>();
    List<Integer> requiredNumbers = new ArrayList<>();
    for (int index : indexes) {
      int number = player.get(index - 1);
      requiredNumbers.add(number);
      used.add(number);
    }
    List<Integer> pool = IntStream.rangeClosed(MIN_NUMBER, MAX_NUMBER)
        .filter(n -> !used.contains(n))
        .boxed()
        .collect(Collectors.toCollection(ArrayList::new));
    Collections.shuffle(pool, RANDOM);
    int pos = 0;
    var maxOrder = type.getMaxOrderNumber();
    for (int n : requiredNumbers) {
      winCombination.set(pos++, n);
    }
    while (pos < maxOrder) {
      winCombination.set(pos++, pool.remove(pool.size() - 1));
    }
    Collections.shuffle(winCombination.subList(0, maxOrder), RANDOM);
    for (int i = maxOrder; i < MAX_NUMBER_COUNT; i++) {
      winCombination.set(i, pool.remove(pool.size() - 1));
    }
    return winCombination;
  }

  private List<Integer> generateLoseCombination(List<Integer> combination) {
    return Stream.generate(this::randomCombination)
        .filter(randomCombination -> !isWinCombination(randomCombination, combination))
        .findFirst()
        .orElseThrow();
  }

  private List<Integer> randomCombination() {
    return RANDOM.ints(MIN_NUMBER, MAX_NUMBER + 1)
        .limit(MAX_NUMBER_COUNT)
        .boxed()
        .toList();
  }

  private boolean isWinCombination(List<Integer> randomCombination, List<Integer> combination) {
    return Arrays.stream(RocketbingoTypes.values())
        .filter(t -> t != RocketbingoTypes.WITHOUT)
        .flatMap(t -> t.getWinningNumbers().stream())
        .filter(combination::containsAll)
        .anyMatch(randomCombination::containsAll);
  }

}
