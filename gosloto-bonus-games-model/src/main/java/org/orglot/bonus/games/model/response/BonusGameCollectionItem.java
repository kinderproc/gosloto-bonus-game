package org.orglot.bonus.games.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Информация об элементе пазла
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BonusGameCollectionItem {
    /**
     * Идентификатор элемента пазла
     */
    private Long id;
    /**
     * Урл на картинку элемента пазла
     */
    private String url;
    /**
     * Порядковый номер элемента пазла
     */
    private Integer puzzleItemId;
}
