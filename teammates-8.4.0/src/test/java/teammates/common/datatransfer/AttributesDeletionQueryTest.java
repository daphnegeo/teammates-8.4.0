package teammates.common.datatransfer;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link AttributesDeletionQuery}.
 */
public class AttributesDeletionQueryTest extends BaseTestCase {

    @Test
    public void testBuilder_invalidCombination_shouldThrowException() {
        // nothing inside query
        assertThrows(AssertionError.class, () -> {
            AttributesDeletionQuery.builder().build();
        });

        // course id with question id
        assertThrows(AssertionError.class, () -> {
            AttributesDeletionQuery.builder()
                    .withCourseId("courseId")
                    .withQuestionId("questionId")
                    .build();
        });
        assertThrows(AssertionError.class, () -> {
            AttributesDeletionQuery.builder()
                    .withQuestionId("questionId")
                    .withCourseId("courseId")
                    .build();
        });

        // course id with response id
        assertThrows(AssertionError.class, () -> {
            AttributesDeletionQuery.builder()
                    .withCourseId("courseId")
                    .withResponseId("responseId")
                    .build();
        });
        assertThrows(AssertionError.class, () -> {
            AttributesDeletionQuery.builder()
                    .withResponseId("responseId")
                    .withCourseId("courseId")
                    .build();
        });

        // session name without course id
        assertThrows(AssertionError.class, () -> {
            AttributesDeletionQuery.builder()
                    .withFeedbackSessionName("sessionName")
                    .build();
        });

        // session name with question id
        assertThrows(AssertionError.class, () -> {
            AttributesDeletionQuery.builder()
                    .withCourseId("courseId")
                    .withFeedbackSessionName("sessionName")
                    .withQuestionId("questionId")
                    .build();
        });
        assertThrows(AssertionError.class, () -> {
            AttributesDeletionQuery.builder()
                    .withCourseId("courseId")
                    .withQuestionId("questionId")
                    .withFeedbackSessionName("sessionName")
                    .build();
        });
        assertThrows(AssertionError.class, () -> {
            AttributesDeletionQuery.builder()
                    .withQuestionId("questionId")
                    .withCourseId("courseId")
                    .withFeedbackSessionName("sessionName")
                    .build();
        });

        // session name with response id
        assertThrows(AssertionError.class, () -> {
            AttributesDeletionQuery.builder()
                    .withCourseId("courseId")
                    .withFeedbackSessionName("sessionName")
                    .withResponseId("responseId")
                    .build();
        });
        assertThrows(AssertionError.class, () -> {
            AttributesDeletionQuery.builder()
                    .withCourseId("courseId")
                    .withResponseId("responseId")
                    .withFeedbackSessionName("sessionName")
                    .build();
        });
        assertThrows(AssertionError.class, () -> {
            AttributesDeletionQuery.builder()
                    .withResponseId("responseId")
                    .withCourseId("courseId")
                    .withFeedbackSessionName("sessionName")
                    .build();
        });

        // question id with response id
        assertThrows(AssertionError.class, () -> {
            AttributesDeletionQuery.builder()
                    .withQuestionId("questionId")
                    .withResponseId("responseId")
                    .build();
        });
        assertThrows(AssertionError.class, () -> {
            AttributesDeletionQuery.builder()
                    .withResponseId("responseId")
                    .withQuestionId("questionId")
                    .build();
        });
    }

    @Test
    public void testBuilder_validCombination_shouldBuildCorrectQuery() {
        // build deletion of course
        AttributesDeletionQuery query = AttributesDeletionQuery.builder()
                .withCourseId("courseId")
                .build();

        assertEquals("courseId", query.getCourseId());
        assertNull(query.getFeedbackSessionName());
        assertNull(query.getQuestionId());
        assertNull(query.getResponseId());
        assertTrue(query.isCourseIdPresent());
        assertFalse(query.isFeedbackSessionNamePresent());
        assertFalse(query.isQuestionIdPresent());
        assertFalse(query.isResponseIdPresent());

        // build deletion of session
        query = AttributesDeletionQuery.builder()
                .withCourseId("courseId")
                .withFeedbackSessionName("sessionName")
                .build();
        assertEquals("courseId", query.getCourseId());
        assertEquals("sessionName", query.getFeedbackSessionName());
        assertNull(query.getQuestionId());
        assertNull(query.getResponseId());
        assertTrue(query.isCourseIdPresent());
        assertTrue(query.isFeedbackSessionNamePresent());
        assertFalse(query.isQuestionIdPresent());
        assertFalse(query.isResponseIdPresent());

        // build deletion of question
        query = AttributesDeletionQuery.builder()
                .withQuestionId("questionId")
                .build();
        assertNull(query.getCourseId());
        assertNull(query.getFeedbackSessionName());
        assertEquals("questionId", query.getQuestionId());
        assertNull(query.getResponseId());
        assertFalse(query.isCourseIdPresent());
        assertFalse(query.isFeedbackSessionNamePresent());
        assertTrue(query.isQuestionIdPresent());
        assertFalse(query.isResponseIdPresent());

        // build deletion of response
        query = AttributesDeletionQuery.builder()
                .withResponseId("responseId")
                .build();
        assertNull(query.getCourseId());
        assertNull(query.getFeedbackSessionName());
        assertNull(query.getQuestionId());
        assertEquals("responseId", query.getResponseId());
        assertFalse(query.isCourseIdPresent());
        assertFalse(query.isFeedbackSessionNamePresent());
        assertFalse(query.isQuestionIdPresent());
        assertTrue(query.isResponseIdPresent());
    }

    @Test
    public void testBuilder_nullInput_shouldThrowException() {
        assertThrows(AssertionError.class, () -> {
            AttributesDeletionQuery.builder()
                    .withCourseId(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            AttributesDeletionQuery.builder()
                    .withFeedbackSessionName(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            AttributesDeletionQuery.builder()
                    .withQuestionId(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            AttributesDeletionQuery.builder()
                    .withResponseId(null)
                    .build();
        });
    }

	@Override
	protected boolean doPutDocuments(DataBundle testData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean doRemoveAndRestoreDataBundle(DataBundle testData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected StudentAttributes getStudent(StudentAttributes student) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected InstructorAttributes getInstructor(InstructorAttributes instructor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected FeedbackSessionAttributes getFeedbackSession(FeedbackSessionAttributes fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected FeedbackResponseAttributes getFeedbackResponse(FeedbackResponseAttributes fr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected FeedbackResponseCommentAttributes getFeedbackResponseComment(FeedbackResponseCommentAttributes frc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected EntityAttributes<FeedbackQuestion> getFeedbackQuestion(EntityAttributes<FeedbackQuestion> fq) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CourseAttributes getCourse(CourseAttributes course) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected StudentProfileAttributes getStudentProfile(StudentProfileAttributes studentProfileAttributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected EntityAttributes<Account> getAccount(EntityAttributes<Account> account) {
		// TODO Auto-generated method stub
		return null;
	}
}
