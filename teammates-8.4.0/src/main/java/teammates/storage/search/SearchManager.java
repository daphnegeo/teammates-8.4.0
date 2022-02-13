package teammates.storage.search;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;

/**
 * Acts as a proxy to search service.
 *
 * @param <T> type of entity to be returned
 */
abstract class SearchManager<T extends EntityAttributes<?>> {

    protected static final Logger log = Logger.getLogger();

    protected static final String ERROR_DELETE_DOCUMENT =
            "Failed to delete document(s) %s in Solr. Root cause: %s ";
    protected static final String ERROR_SEARCH_DOCUMENT =
            "Failed to search for document(s) %s from Solr. Root cause: %s ";
    protected static final String ERROR_SEARCH_NOT_IMPLEMENTED =
            "Search service is not implemented";
    protected static final String ERROR_PUT_DOCUMENT =
            "Failed to put document %s into Solr. Root cause: %s ";
    protected static final String ERROR_RESET_COLLECTION =
            "Failed to reset collections. Root cause: %s ";

    protected static final int START_INDEX = 0;
    protected static final int NUM_OF_RESULTS = Const.SEARCH_QUERY_SIZE_LIMIT;

    SearchManager(String searchServiceHost, boolean isResetAllowed) {
        this.isResetAllowed = Config.isDevServer() && isResetAllowed;

        if (StringHelper.isEmpty(searchServiceHost)) {
            this.client = null;
        } else {
            this.client = new HttpSolrClient.Builder(searchServiceHost)
                    .withConnectionTimeout(2000) // timeout for connecting to Solr server
                    .withSocketTimeout(5000) // timeout for reading data
                    .build();
        }
    }

}
