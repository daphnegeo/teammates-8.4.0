package teammates.e2e.cases;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionsVariousAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_AUDIT_LOGS_PAGE}.
 */
public class InstructorAuditLogsPageE2ETest extends BaseE2ETestCase {
    public InstructorAttributes instructor;
    private CourseAttributes course;
    private FeedbackSessionAttributes feedbackSession;
    private FeedbackQuestionsVariousAttributes feedbackQuestion;
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
}
