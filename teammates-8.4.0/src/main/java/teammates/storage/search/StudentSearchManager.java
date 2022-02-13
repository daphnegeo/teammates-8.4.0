package teammates.storage.search;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.SearchServiceException;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.StudentsDb;

/**
 * Acts as a proxy to search service for student-related search features.
 */
public class StudentSearchManager extends SearchManager<StudentAttributes> {

    private final CoursesDb coursesDb = CoursesDb.inst();
    private final StudentsDb studentsDb = StudentsDb.inst();
	private final HttpSolrClient client;
	private final boolean isResetAllowed;

    public StudentSearchManager(String searchServiceHost, boolean isResetAllowed) {
        super(searchServiceHost, isResetAllowed);
    }

    String getCollectionName() {
        return "students";
    }

    @Override
    StudentSearchDocument createDocument(StudentAttributes student) {
        CourseAttributes course = coursesDb.getCourse(student.getCourse());
        return new StudentSearchDocument(student, course);
    }

    StudentAttributes getAttributeFromDocument(SolrDocument document) {
        String courseId = (String) document.getFirstValue("courseId");
        String email = (String) document.getFirstValue("email");
        return studentsDb.getStudentForEmail(courseId, email);
    }

    void sortResult(List<StudentAttributes> result) {
        result.sort(Comparator.comparing((StudentAttributes student) -> student.getCourse())
                .thenComparing(student -> student.getSection())
                .thenComparing(student -> student.getTeam())
                .thenComparing(student -> student.getName())
                .thenComparing(student -> student.getEmail()));
    }

    /**
     * Searches for students.
     *
     * @param instructors the constraint that restricts the search result
     */
    public List<StudentAttributes> searchStudents(String queryString, List<InstructorAttributes> instructors)
            throws SearchServiceException {
        SolrQuery query = getBasicQuery(queryString);

        List<String> courseIdsWithViewStudentPrivilege;
        if (instructors == null) {
            courseIdsWithViewStudentPrivilege = new ArrayList<>();
        } else {
            courseIdsWithViewStudentPrivilege = instructors.stream()
                    .filter(i -> i.getPrivileges().getCourseLevelPrivileges().isCanViewStudentInSections())
                    .map(ins -> ins.getCourseId())
                    .collect(Collectors.toList());
            if (courseIdsWithViewStudentPrivilege.isEmpty()) {
                return new ArrayList<>();
            }
            String courseIdFq = String.join("\" OR \"", courseIdsWithViewStudentPrivilege);
            query.addFilterQuery("courseId:(\"" + courseIdFq + "\")");
        }

        QueryResponse response = performQuery(query);
        SolrDocumentList documents = response.getResults();

        // Sanity check such that the course ID of the students match exactly.
        // In ideal case, this check is not expected to do anything,
        // i.e. the resulting list should be the same as the incoming list.

        List<SolrDocument> filteredDocuments = documents.stream()
                .filter(document -> {
                    if (instructors == null) {
                        return true;
                    }
                    String courseId = (String) document.getFirstValue("courseId");
                    return courseIdsWithViewStudentPrivilege.contains(courseId);
                })
                .collect(Collectors.toList());

        return convertDocumentToAttributes(filteredDocuments);
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
	public void putDocument(StudentAttributes attributes) throws SearchServiceException {
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

	List<StudentAttributes> convertDocumentToAttributes(List<SolrDocument> documents) {
	    if (documents == null) {
	        return new ArrayList<>();
	    }
	
	    List<StudentAttributes> result = new ArrayList<>();
	
	    for (SolrDocument document : documents) {
	        StudentAttributes attribute = getAttributeFromDocument(document);
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
