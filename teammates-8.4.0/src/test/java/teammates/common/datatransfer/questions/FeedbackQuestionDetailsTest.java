package teammates.common.datatransfer.questions;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
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
 * SUT: {@link FeedbackQuestionDetails}.
 */
public class FeedbackQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testEquals() {

        ______TS("Same object with different references, should be same");
        FeedbackQuestionDetails ftqd1 = new FeedbackTextQuestionDetails("text question");
        FeedbackQuestionDetails ftqd2 = ftqd1;
        assertEquals(ftqd1, ftqd2);

        ______TS("One input is null, should be different");
        ftqd1 = new FeedbackTextQuestionDetails("text question");
        ftqd2 = null;
        assertNotEquals(ftqd1, ftqd2);

        ______TS("Different classes, should be different");
        ftqd1 = new FeedbackTextQuestionDetails("text question");
        ftqd2 = new FeedbackMcqQuestionDetails();
        assertNotEquals(ftqd1, ftqd2);

        ______TS("Some attributes are different, should be different");
        ftqd1 = new FeedbackTextQuestionDetails("first question");
        ftqd2 = new FeedbackTextQuestionDetails("second question");
        assertNotEquals(ftqd1, ftqd2);

        ftqd2 = new FeedbackTextQuestionDetails("first question");
        ((FeedbackTextQuestionDetails) ftqd1).setRecommendedLength(50);
        assertNotEquals(ftqd1, ftqd2);

        ______TS("All attributes are same, should be same");
        ((FeedbackTextQuestionDetails) ftqd2).setRecommendedLength(50);
        assertEquals(ftqd1, ftqd2);

    }

	@Override
	protected EntityAttributes<Account> getAccount(EntityAttributes<Account> account) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected StudentProfileAttributes getStudentProfile(StudentProfileAttributes studentProfileAttributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CourseAttributes getCourse(CourseAttributes course) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected EntityAttributes<FeedbackQuestion> getFeedbackQuestion(EntityAttributes<FeedbackQuestion> fq) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected FeedbackResponseCommentAttributes getFeedbackResponseComment(FeedbackResponseCommentAttributes frc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected FeedbackResponseAttributes getFeedbackResponse(FeedbackResponseAttributes fr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected FeedbackSessionAttributes getFeedbackSession(FeedbackSessionAttributes fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected InstructorAttributes getInstructor(InstructorAttributes instructor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected StudentAttributes getStudent(StudentAttributes student) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean doRemoveAndRestoreDataBundle(DataBundle testData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean doPutDocuments(DataBundle testData) {
		// TODO Auto-generated method stub
		return false;
	}
}
