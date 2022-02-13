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
import teammates.common.util.TimeHelper;
import teammates.common.util.TimeHelperExtension;
import teammates.e2e.util.TestProperties;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;

/**
 * SUT: {@link Const.CronJobURIs#AUTOMATED_FEEDBACK_OPENING_REMINDERS},
 *      {@link Const.CronJobURIs#AUTOMATED_FEEDBACK_CLOSING_REMINDERS},
 *      {@link Const.CronJobURIs#AUTOMATED_FEEDBACK_CLOSED_REMINDERS},
 *      {@link Const.CronJobURIs#AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS}.
 */
public class AutomatedSessionRemindersE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/AutomatedSessionRemindersE2ETest.json");

        // When running the test against a production server, email alerts will be sent
        // to the specified email address
        // The tester should manually check the email box after running the test suite
        // TODO check if we can automate this checking process

        String student1Email = TestProperties.TEST_EMAIL;
        testData.accounts.get("instructorWithEvals").setEmail(student1Email);
        testData.instructors.get("AutSesRem.instructor").setEmail(student1Email);
        testData.students.get("alice.tmms@AutSesRem.course").setEmail(student1Email);
        testData.feedbackSessions.get("closedSession").setCreatorEmail(student1Email);
        testData.feedbackSessions.get("closingSession").setCreatorEmail(student1Email);
        testData.feedbackSessions.get("openingSession").setCreatorEmail(student1Email);
        testData.feedbackSessions.get("publishedSession").setCreatorEmail(student1Email);

        // Set closing time of one feedback session to tomorrow
        FeedbackSessionAttributes closingFeedbackSession = testData.feedbackSessions.get("closingSession");
        closingFeedbackSession.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(1));

        // Set closing time of one feedback session to 30 mins ago
        FeedbackSessionAttributes closedFeedbackSession = testData.feedbackSessions.get("closedSession");
        closedFeedbackSession.setEndTime(TimeHelperExtension.getInstantMinutesOffsetFromNow(-30));

        // Set opening time for one feedback session to yesterday
        FeedbackSessionAttributes openingFeedbackSession = testData.feedbackSessions.get("openingSession");
        openingFeedbackSession.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-1));

        // Published time for one feedback session already set to some time in the past.

        removeAndRestoreDataBundle(testData);
    }

    @Override
    protected void prepareBrowser() {
        // this test does not require any browser
    }

    @Test
    @Override
    public void testAll() {
        testFeedbackSessionOpeningSoonReminders();
        testFeedbackSessionOpeningReminders();
        testFeedbackSessionClosingReminders();
        testFeedbackSessionClosedReminders();
        testFeedbackSessionPublishedReminders();
    }

    private void testFeedbackSessionOpeningSoonReminders() {
        BACKDOOR.executeGetRequest(Const.CronJobURIs.AUTOMATED_FEEDBACK_OPENING_SOON_REMINDERS, null);
    }

    private void testFeedbackSessionOpeningReminders() {
        BACKDOOR.executeGetRequest(Const.CronJobURIs.AUTOMATED_FEEDBACK_OPENING_REMINDERS, null);
    }

    private void testFeedbackSessionClosingReminders() {
        BACKDOOR.executeGetRequest(Const.CronJobURIs.AUTOMATED_FEEDBACK_CLOSING_REMINDERS, null);
    }

    private void testFeedbackSessionClosedReminders() {
        BACKDOOR.executeGetRequest(Const.CronJobURIs.AUTOMATED_FEEDBACK_CLOSED_REMINDERS, null);
    }

    private void testFeedbackSessionPublishedReminders() {
        BACKDOOR.executeGetRequest(Const.CronJobURIs.AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS, null);
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
