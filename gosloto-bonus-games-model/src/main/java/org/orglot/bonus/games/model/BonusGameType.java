package org.orglot.bonus.games.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Getter
public enum BonusGameType {
    SWEETS_CARNIVAL,
    LUCKY_CHARGE,
    TREASURE_ISLAND,
    MINESWEEPER,
    SUPERKUZMICH,
    PLINKO,
    SNAKE,
    JUMPERKUZMICH,
    SCRATCHES,
    AEROHOCKEY,
    PINBALL,
    RL30,
    LUCKY_SYMBOLS,
    LUCKY_HUNT,
    HORSE_RACING,
    JUMPERKUZMICH_PRIZE,
    PINBALL_PRIZE,
    SNAKE_PRIZE,
    AEROHOCKEY_PRIZE,
    MINESWEEPER_PRIZE,
    SHAROMANIA,
    NARDS,
    TREE_OF_LUCK,
    BINGOLUKOMORIE,
    UNKNOWN,
    SLOTS,
    TREE_OF_WISHES;

    private static final List<String> stringValues = Arrays.stream(BonusGameType.values()).map(BonusGameType::name).toList();

    public static List<String> getStringValues() {
        return stringValues;
    }
}
