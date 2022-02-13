package teammates.storage.search;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.SearchServiceException;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.InstructorsDb;

/**
 * Acts as a proxy to search service for instructor-related search features.
 */
public class InstructorSearchManager extends SearchManager<InstructorAttributes> {

    private final CoursesDb coursesDb = CoursesDb.inst();
    private final InstructorsDb instructorsDb = InstructorsDb.inst();
	private final HttpSolrClient client;
	private final boolean isResetAllowed;

    public InstructorSearchManager(String searchServiceHost, boolean isResetAllowed) {
        super(searchServiceHost, isResetAllowed);
    }

    String getCollectionName() {
        return "instructors";
    }

    @Override
    InstructorSearchDocument createDocument(InstructorAttributes instructor) {
        CourseAttributes course = coursesDb.getCourse(instructor.getCourseId());
        return new InstructorSearchDocument(instructor, course);
    }

    InstructorAttributes getAttributeFromDocument(SolrDocument document) {
        String courseId = (String) document.getFirstValue("courseId");
        String email = (String) document.getFirstValue("email");
        return instructorsDb.getInstructorById(courseId, email);
    }

    void sortResult(List<InstructorAttributes> result) {
        result.sort(Comparator.comparing((InstructorAttributes instructor) -> instructor.getCourseId())
                .thenComparing(instructor -> instructor.getRole())
                .thenComparing(instructor -> instructor.getName())
                .thenComparing(instructor -> instructor.getEmail()));
    }

    /**
     * Searches for instructors.
     */
    public List<InstructorAttributes> searchInstructors(String queryString) throws SearchServiceException {
        SolrQuery query = getBasicQuery(queryString);

        QueryResponse response = performQuery(query);
        return convertDocumentToAttributes(response.getResults());
    }

	SolrQuery getBasicQuery(String queryString) {
	    SolrQuery query = new SolrQuery();
	
	    String cleanQueryString = cleanSpecialChars(queryString);
	    query.setQuery(cleanQueryString);
	
	    query.setStart(START_INDEX);
	    query.setRows(NUM_OF_RESULTS);
	
	    return query;
	}

	QueryResponse performQuery(SolrQuery query) throws SearchServiceException {
	    if (client == null) {
	        throw new SearchServiceException("Full-text search is not available.", HttpStatus.SC_NOT_IMPLEMENTED);
	    }
	
	    QueryResponse response = null;
	
	    try {
	        response = client.query(getCollectionName(), query);
	    } catch (SolrServerException e) {
	        Throwable rootCause = e.getRootCause();
	        log.severe(String.format(ERROR_SEARCH_DOCUMENT, query.getQuery(), rootCause), e);
	        if (rootCause instanceof SocketTimeoutException) {
	            throw new SearchServiceException("A timeout was reached while processing your request. "
	                    + "Please try again later.", e, HttpStatus.SC_GATEWAY_TIMEOUT);
	        } else {
	            throw new SearchServiceException("An error has occurred while performing search. "
	                    + "Please try again later.", e, HttpStatus.SC_BAD_GATEWAY);
	        }
	    } catch (IOException e) {
	        log.severe(String.format(ERROR_SEARCH_DOCUMENT, query.getQuery(), e.getCause()), e);
	        throw new SearchServiceException("An error has occurred while performing search. "
	                + "Please try again later.", e, HttpStatus.SC_BAD_GATEWAY);
	    }
	
	    return response;
	}

	/**
	 * Creates or updates search document for the given entity.
	 */
	public void putDocument(InstructorAttributes attributes) throws SearchServiceException {
	    if (client == null) {
	        log.warning(ERROR_SEARCH_NOT_IMPLEMENTED);
	        return;
	    }
	
	    if (attributes == null) {
	        return;
	    }
	
	    Map<String, Object> searchableFields = createDocument(attributes).getSearchableFields();
	    SolrInputDocument document = new SolrInputDocument();
	    searchableFields.forEach((key, value) -> document.addField(key, value));
	
	    try {
	        client.add(getCollectionName(), Collections.singleton(document));
	        client.commit(getCollectionName());
	    } catch (SolrServerException e) {
	        log.severe(String.format(ERROR_PUT_DOCUMENT, document, e.getRootCause()), e);
	        throw new SearchServiceException(e, HttpStatus.SC_BAD_GATEWAY);
	    } catch (IOException e) {
	        log.severe(String.format(ERROR_PUT_DOCUMENT, document, e.getCause()), e);
	        throw new SearchServiceException(e, HttpStatus.SC_BAD_GATEWAY);
	    }
	}

	/**
	 * Removes search documents based on the given keys.
	 */
	public void deleteDocuments(List<String> keys) {
	    if (client == null) {
	        log.warning(ERROR_SEARCH_NOT_IMPLEMENTED);
	        return;
	    }
	
	    if (keys.isEmpty()) {
	        return;
	    }
	
	    try {
	        client.deleteById(getCollectionName(), keys);
	        client.commit(getCollectionName());
	    } catch (SolrServerException e) {
	        log.severe(String.format(ERROR_DELETE_DOCUMENT, keys, e.getRootCause()), e);
	    } catch (IOException e) {
	        log.severe(String.format(ERROR_DELETE_DOCUMENT, keys, e.getCause()), e);
	    }
	}

	/**
	 * Resets the data for all collections if, and only if called during component tests.
	 */
	public void resetCollections() {
	    if (client == null || !isResetAllowed) {
	        return;
	    }
	
	    try {
	        client.deleteByQuery(getCollectionName(), "*:*");
	        client.commit(getCollectionName());
	    } catch (SolrServerException e) {
	        log.severe(String.format(ERROR_RESET_COLLECTION, e.getRootCause()), e);
	    } catch (IOException e) {
	        log.severe(String.format(ERROR_RESET_COLLECTION, e.getCause()), e);
	    }
	}

	private String cleanSpecialChars(String queryString) {
	    String htmlTagStripPattern = "<[^>]*>";
	
	    // Solr special characters: + - && || ! ( ) { } [ ] ^ " ~ * ? : \ /
	    String res = queryString.replaceAll(htmlTagStripPattern, "")
	            .replace("\\", "\\\\")
	            .replace("+", "\\+")
	            .replace("-", "\\-")
	            .replace("&&", "\\&&")
	            .replace("||", "\\||")
	            .replace("!", "\\!")
	            .replace("(", "\\(")
	            .replace(")", "\\)")
	            .replace("{", "\\{")
	            .replace("}", "\\}")
	            .replace("[", "\\[")
	            .replace("]", "\\]")
	            .replace("^", "\\^")
	            .replace("~", "\\~")
	            .replace("?", "\\?")
	            .replace(":", "\\:")
	            .replace("/", "\\/");
	
	    // imbalanced double quotes are invalid
	    int count = StringUtils.countMatches(res, "\"");
	    if (count % 2 == 1) {
	        res = res.replace("\"", "");
	    }
	
	    // use exact match only when there's email-like input
	    if (res.contains("@") && count == 0) {
	        return "\"" + res + "\"";
	    } else {
	        return res;
	    }
	}

	List<InstructorAttributes> convertDocumentToAttributes(List<SolrDocument> documents) {
	    if (documents == null) {
	        return new ArrayList<>();
	    }
	
	    List<InstructorAttributes> result = new ArrayList<>();
	
	    for (SolrDocument document : documents) {
	        InstructorAttributes attribute = getAttributeFromDocument(document);
	        if (attribute == null) {
	            // search engine out of sync as SearchManager may fail to delete documents
	            // the chance is low and it is generally not a big problem
	            String id = (String) document.getFirstValue("id");
	            deleteDocuments(Collections.singletonList(id));
	            continue;
	        }
	        result.add(attribute);
	    }
	    sortResult(result);
	
	    return result;
	}

}
