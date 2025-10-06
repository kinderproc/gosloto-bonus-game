package org.orglot.gosloto.bonus.games.model;

import java.util.EnumSet;
import java.util.List;

public enum SessionState {
    START,
    IN_PROGRESS,
    COMPLETED,
    EXPIRED;

    private static final List<String> stringValues = EnumSet.allOf(SessionState.class).stream().map(String::valueOf).toList();

    public static List<String> getStringValues() {
        return stringValues;
    }
}
