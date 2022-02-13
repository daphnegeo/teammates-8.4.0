package teammates.storage.search;

import teammates.common.datatransfer.attributes.EntityAttributes;

/**
 * Defines how we store document for indexing/searching.
 *
 * @param <T> type of entity to be converted into document
 */
abstract class SearchDocument<T extends EntityAttributes<?>> {

    SearchDocument(T attribute) {
        this.attribute = attribute;
    }

}
