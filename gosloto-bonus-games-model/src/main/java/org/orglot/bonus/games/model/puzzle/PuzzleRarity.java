package org.orglot.bonus.games.model.puzzle;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PuzzleRarity {

    ORDINARY("Обычный"),
    RARE("Редкий"),
    UNIQUE("Уникальный"),
    SPECIAL("Особый"),
    EXCEPTIONAL("Исключительный");

    private final String description;

}
