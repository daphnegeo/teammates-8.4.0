package teammates.e2e.cases;

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
import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_AUDIT_LOGS_PAGE}.
 */
public class InstructorAuditLogsPageE2ETest extends BaseE2ETestCase {
    public InstructorAttributes instructor;
    private CourseAttributes course;
    private FeedbackSessionAttributes feedbackSession;
    private EntityAttributes<FeedbackQuestion> feedbackQuestion;
    private StudentAttributes student;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorAuditLogsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        instructor = testData.instructors.get("instructor");
        course = testData.courses.get("course");
        student = testData.students.get("alice.tmms@IAuditLogs.CS2104");
        feedbackQuestion = testData.feedbackQuestions.get("qn1");
        feedbackSession = testData.feedbackSessions.get("openSession");
    }

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.attributes.FeedbackSessionAttributes#testAll(teammates.e2e.cases.InstructorAuditLogsPageE2ETest)} instead
	 */
	@Test
	@Override
	public void testAll() {
		feedbackSession.testAll(this);
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
