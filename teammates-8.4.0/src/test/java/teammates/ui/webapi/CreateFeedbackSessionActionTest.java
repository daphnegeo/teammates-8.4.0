package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
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

}
