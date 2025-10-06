package org.orglot.bonus.games.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    OK("ok"),
    PENDING("pending"),
    ERROR("error");

    private String text;
}
