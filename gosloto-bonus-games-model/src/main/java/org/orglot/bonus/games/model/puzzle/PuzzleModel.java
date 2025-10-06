package org.orglot.bonus.games.model.puzzle;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class PuzzleModel {
    private long id;
    private String url;
    private String description;
}
