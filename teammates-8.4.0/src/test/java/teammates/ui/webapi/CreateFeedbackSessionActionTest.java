package teammates.ui.webapi;

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
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.ResponseVisibleSetting;
import teammates.ui.output.SessionVisibleSetting;
import teammates.ui.request.FeedbackSessionCreateRequest;

/**
 * SUT: {@link CreateFeedbackSessionAction}.
 */
public class CreateFeedbackSessionActionTest extends BaseActionTest<CreateFeedbackSessionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    /**
	 * @param response
	 */
	private void newFeedbackSessionAttr(FeedbackSessionData response) {
		assertEquals("new feedback session", response.getFeedbackSessionName());
        assertEquals("instructions", response.getInstructions());
        assertEquals(1444003051000L, response.getSubmissionStartTimestamp());
        assertEquals(1546003051000L, response.getSubmissionEndTimestamp());
        assertEquals(5, response.getGracePeriod().longValue());

        assertEquals(SessionVisibleSetting.CUSTOM, response.getSessionVisibleSetting());
        assertEquals(1440003051000L, response.getCustomSessionVisibleTimestamp().longValue());

        assertEquals(ResponseVisibleSetting.CUSTOM, response.getResponseVisibleSetting());
        assertEquals(1547003051000L, response.getCustomResponseVisibleTimestamp().longValue());

        assertFalse(response.getIsClosingEmailEnabled());
        assertFalse(response.getIsPublishedEmailEnabled());

        assertNotNull(response.getCreatedAtTimestamp());
	}

    @Test
    public void testExecute_masqueradeMode_shouldCreateFeedbackSession() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");

        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };
        params = addUserIdToParams(instructor1ofCourse1.getGoogleId(), params);

        FeedbackSessionCreateRequest createRequest = getTypicalCreateRequest();

        CreateFeedbackSessionAction a = getAction(createRequest, params);
        getJsonResult(a);
    }

    private FeedbackSessionCreateRequest getTypicalCreateRequest() {
        FeedbackSessionCreateRequest createRequest =
                new FeedbackSessionCreateRequest();
        createRequest.setFeedbackSessionName("new feedback session");
        createRequest.setInstructions("instructions");

        createRequest.setSubmissionStartTimestamp(1444003051000L);
        createRequest.setSubmissionEndTimestamp(1546003051000L);
        createRequest.setGracePeriod(5);

        createRequest.setSessionVisibleSetting(SessionVisibleSetting.CUSTOM);
        createRequest.setCustomSessionVisibleTimestamp(1440003051000L);

        createRequest.setResponseVisibleSetting(ResponseVisibleSetting.CUSTOM);
        createRequest.setCustomResponseVisibleTimestamp(1547003051000L);

        createRequest.setClosingEmailEnabled(false);
        createRequest.setPublishedEmailEnabled(false);

        return createRequest;
    }

    private FeedbackSessionCreateRequest getCopySessionCreateRequest(FeedbackSessionAttributes toCopySession) {
        FeedbackSessionCreateRequest createRequest = new FeedbackSessionCreateRequest();
        createRequest.setFeedbackSessionName("copied feedback session");
        createRequest.setToCopyCourseId(toCopySession.getCourseId());
        createRequest.setToCopySessionName(toCopySession.getFeedbackSessionName());
        createRequest.setInstructions(toCopySession.getInstructions());

        createRequest.setSubmissionStartTimestamp(toCopySession.getStartTime().toEpochMilli());
        createRequest.setSubmissionEndTimestamp(toCopySession.getEndTime().toEpochMilli());
        createRequest.setGracePeriod(toCopySession.getGracePeriodMinutes());

        createRequest.setSessionVisibleSetting(SessionVisibleSetting.CUSTOM);
        createRequest.setCustomSessionVisibleTimestamp(toCopySession.getSessionVisibleFromTime().toEpochMilli());

        createRequest.setResponseVisibleSetting(ResponseVisibleSetting.CUSTOM);
        createRequest.setCustomResponseVisibleTimestamp(toCopySession.getResultsVisibleFromTime().toEpochMilli());

        createRequest.setClosingEmailEnabled(toCopySession.isClosingEmailEnabled());
        createRequest.setPublishedEmailEnabled(toCopySession.isPublishedEmailEnabled());
        return createRequest;
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION, params);
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
