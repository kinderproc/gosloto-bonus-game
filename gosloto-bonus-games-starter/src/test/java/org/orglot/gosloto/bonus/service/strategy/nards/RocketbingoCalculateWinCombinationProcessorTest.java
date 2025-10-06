package org.orglot.gosloto.bonus.service.strategy.nards;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.orglot.gosloto.bonus.games.service.strategy.rocketbingo.RocketbingoCalculateWinCombinationProcessor;
import org.orglot.gosloto.bonus.games.service.strategy.rocketbingo.enums.RocketbingoTypes;

import java.util.Random;

class RocketbingoCalculateWinCombinationProcessorTest {
    private final RocketbingoCalculateWinCombinationProcessor testInstance = new RocketbingoCalculateWinCombinationProcessor();

    @Test
    void processIncorrectNumbers() {
        var data = new Random().ints(11, 75)
                .limit(100)
                .boxed()
                .toList();

        Assertions.assertThatThrownBy(() -> testInstance.calculate(data, RocketbingoTypes.DIAGONAL_T3))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void processCorrectNumbers() {
        var data = new Random().ints(11, 75)
                .limit(24)
                .boxed()
                .toList();

        Assertions.assertThatThrownBy(() -> testInstance.calculate(data, RocketbingoTypes.DIAGONAL_T3))
            .isInstanceOf(IllegalArgumentException.class);
    }

}
