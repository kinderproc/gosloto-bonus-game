package org.orglot.gosloto.bonus.games.service.strategy.nards;

import org.orglot.gosloto.bonus.games.service.strategy.nards.model.NardsWinCombinationCalculateData;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@Component
public class NardsCalculateWinCombinationProcessor {

  private static final Random RANDOM = new Random();
  private static final int GAME_DIGIT_COUNT = 8;
  private static final int FLOOR_EXTRA = 1;
  private static final int BOUND_EXTRA = 4;

  public List<Integer> calculate(NardsWinCombinationCalculateData data) {
    List<Integer> combination = data.getCombination();
    if (CollectionUtils.isEmpty(combination)) {
      List<Integer> randomResult = generateCombination(GAME_DIGIT_COUNT, d -> true);
      randomResult.add(generateExtra());
      return randomResult;
    }
    Set<Integer> originalCombination = new HashSet<>(combination);
    if (originalCombination.size() != GAME_DIGIT_COUNT) {
      throw new IllegalArgumentException("Combination must contain at least 8 numbers.");
    }
    // Шаг 1: Замена от 5 до 8 чисел
    int combinationDigitCount = data.getCombinationDigitCount();
    int replaceCount = combinationDigitCount > 0 ?
        GAME_DIGIT_COUNT - combinationDigitCount :
        5 + RANDOM.nextInt(4);
    List<Integer> indices = new ArrayList<>();
    for (int i = 0; i < originalCombination.size(); i++) {
      indices.add(i);
    }
    Collections.shuffle(indices);
    List<Integer> indicesToReplace = indices.subList(0, replaceCount);

    List<Integer> selectedNewValues = generateCombination(replaceCount, i -> !originalCombination.contains(i));

    // Заменяем выбранные индексы
    List<Integer> modifiedCombination = new ArrayList<>(originalCombination);
    for (int i = 0; i < replaceCount; i++) {
      int indexToReplace = indicesToReplace.get(i);
      modifiedCombination.set(indexToReplace, selectedNewValues.get(i));
    }

    // Шаг 2: Обновляем extraCombination (1–4)
    List<Integer> extraCombination = data.getExtraCombination();
    int extraCombinationDigitCount = data.getExtraCombinationDigitCount();
    if (CollectionUtils.isEmpty(extraCombination)) {
      modifiedCombination.add(generateExtra());
      return modifiedCombination;
    }
    Integer oldExtra = extraCombination.get(0);
    if (extraCombinationDigitCount > 0) {
      modifiedCombination.add(oldExtra);
      return modifiedCombination;
    }
    Integer newExtra = IntStream.rangeClosed(1, 4)
        .filter(i -> i != oldExtra)
        .boxed()
        .toList()
        .get(RANDOM.nextInt(3));
    modifiedCombination.add(newExtra);
    return modifiedCombination;
  }

  private List<Integer> generateCombination(int lastIndex, Predicate<Integer> addListCondition) {
    List<Integer> randomList = new ArrayList<>();
    IntStream.rangeClosed(1, 20)
        .boxed()
        .filter(addListCondition)
        .forEach(randomList::add);
    Collections.shuffle(randomList);
    return randomList.subList(0, lastIndex);
  }

  private int generateExtra() {
    return RANDOM.nextInt(BOUND_EXTRA) + FLOOR_EXTRA;
  }
}
