package org.orglot.bonus.games.model.puzzle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Элемент пазла
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PuzzleItem {
    /**
     * идентификатор элемента пазла
     */
    private Long id;
    /**
     * координата элемента пазла
     */
    private Integer position;
    /**
     * идентификатор пазла
     */
    private Long puzzleId;
    /**
     * ссылка на картинку элемента пазла
     */
    private String url;
    /**
     * наименование пазла
     */
    private String puzzleName;
}
