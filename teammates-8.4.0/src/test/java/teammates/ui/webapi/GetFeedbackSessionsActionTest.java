package teammates.ui.webapi;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

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
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.FeedbackSessionPublishStatus;
import teammates.ui.output.FeedbackSessionSubmissionStatus;
import teammates.ui.output.FeedbackSessionsData;
import teammates.ui.output.ResponseVisibleSetting;
import teammates.ui.output.SessionVisibleSetting;

/**
 * SUT: {@link GetFeedbackSessionsAction}.
 */
public class GetFeedbackSessionsActionTest extends BaseActionTest<GetFeedbackSessionsAction> {

    private List<FeedbackSessionAttributes> sessionsInCourse1;
    private List<FeedbackSessionAttributes> sessionsInCourse2;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSIONS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    protected void testExecute_asInstructorWithCourseId_shouldReturnAllSessionsForCourse() {
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        loginAsInstructor(instructor2OfCourse1.getGoogleId());

        String[] submissionParam = {
                Const.ParamsNames.COURSE_ID, instructor2OfCourse1.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(5, fsData.getFeedbackSessions().size());
        assertAllInstructorSessionsMatch(fsData, sessionsInCourse1);
    }

    @Test
    protected void testExecute_asInstructorWithRecycleBinFlagTrue_shouldReturnAllSoftDeletedSessionsForInstructor() {
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor2OfCourse1.getGoogleId());

        String[] submissionParam = {
                Const.ParamsNames.IS_IN_RECYCLE_BIN, "true",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(1, fsData.getFeedbackSessions().size());
        FeedbackSessionData fs = fsData.getFeedbackSessions().get(0);
        assertAllInformationMatch(fs, session1InCourse1);
    }

    @Test
    protected void testExecute_asInstructorWithRecycleBinFlagFalse_shouldReturnAllSessionsForInstructor() {
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        loginAsInstructor(instructor2OfCourse1.getGoogleId());

        String[] submissionParam = {
                Const.ParamsNames.IS_IN_RECYCLE_BIN, "false",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(5, fsData.getFeedbackSessions().size());
        assertAllInstructorSessionsMatch(fsData, sessionsInCourse1);
    }

    @Test
    protected void testExecute_instructorAsStudent_shouldReturnAllSessionsForStudent() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsStudentInstructor(instructor1OfCourse1.getGoogleId());
        String[] submissionParam = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(2, fsData.getFeedbackSessions().size());
        assertAllStudentSessionsMatch(fsData, sessionsInCourse2);
    }

    @Test
    protected void testExecute_instructorAsStudentWithCourseId_shouldReturnAllSessionsForCourseOfStudent() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student2InCourse2 = typicalBundle.students.get("student2InCourse2");

        loginAsStudentInstructor(instructor1OfCourse1.getGoogleId());

        String[] submissionParam = {
                Const.ParamsNames.COURSE_ID, student2InCourse2.getCourse(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();
        assertAllStudentSessionsMatch(fsData, sessionsInCourse2);
    }

    @Test
    protected void testExecute_instructorAsStudentWithInvalidCourseId_shouldReturnEmptyList() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsStudentInstructor(instructor1OfCourse1.getGoogleId());

        String[] submissionParam = {
                Const.ParamsNames.COURSE_ID, "invalid-course-id",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(0, fsData.getFeedbackSessions().size());
    }

    @Test
    protected void testExecute_asStudentWithCourseId_shouldReturnAllSessionsForCourse() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.getGoogleId());

        String[] submissionParam = {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(4, fsData.getFeedbackSessions().size());
        assertAllStudentSessionsMatch(fsData, sessionsInCourse1.subList(0, 4));

    }

    @Test
    protected void testExecute_asStudent_shouldReturnAllSessionsForAccount() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.getGoogleId());

        String[] submissionParam = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetFeedbackSessionsAction a = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(a).getOutput();

        assertEquals(4, fsData.getFeedbackSessions().size());
        assertAllStudentSessionsMatch(fsData, sessionsInCourse1.subList(0, 4));
    }

    @Test
    protected void testExecute_unknownEntityType_shouldFail() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.getGoogleId());

        verifyHttpParameterFailure();
    }

    @Test
    @Override
    protected void testAccessControl() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        StudentAttributes student1InCourse2 = typicalBundle.students.get("student1InCourse2");
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        InstructorAttributes instructor1OfCourse2 = typicalBundle.instructors.get("instructor1OfCourse2");

        loginAsStudent(student1InCourse1.getGoogleId());

        ______TS("student can access");
        String[] studentEntityParam = studentcanaccess();

        ______TS("student of the same course can access");
        String[] courseParam = studentofthesaemcoursecanaccess(student1InCourse2);

        ______TS("Student of another course cannot access");
        studentofanothercoursecannotaccess(student1InCourse1, courseParam);

        ______TS("instructor can access");
        instructorcanaccess(instructor1OfCourse2);

        ______TS("instructor of the same course can access");
        String[] instructorAndCourseIdParam = instructorofthesamecoursecanaccess(student1InCourse2);

        ______TS("instructor of another course cannot access");
        loginAsInstructor(instructor2OfCourse1.getGoogleId());
        verifyCannotAccess(instructorAndCourseIdParam);

        ______TS("instructor as student can access");
        loginAsStudentInstructor(instructor1OfCourse1.getGoogleId());
        verifyCanAccess(studentEntityParam);

        ______TS("instructor as student can access for course");
        instructorasstudentcanaccessforacourse(instructor1OfCourse1, studentEntityParam, courseParam);
    }

	/**
	 * @param instructor1OfCourse1
	 * @param studentEntityParam
	 * @param courseParam
	 */
	private void instructorasstudentcanaccessforacourse(InstructorAttributes instructor1OfCourse1,
			String[] studentEntityParam, String[] courseParam) {
		loginAsStudentInstructor(instructor1OfCourse1.getGoogleId());
        verifyCanAccess(courseParam);

        String[] adminEntityParam = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
        };

        verifyAccessibleForAdmin(adminEntityParam);
        verifyInaccessibleForUnregisteredUsers(studentEntityParam);
        verifyInaccessibleWithoutLogin();
	}

	/**
	 * @param student1InCourse2
	 * @return
	 */
	private String[] instructorofthesamecoursecanaccess(StudentAttributes student1InCourse2) {
		String[] instructorAndCourseIdParam = {
                Const.ParamsNames.COURSE_ID, student1InCourse2.getCourse(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };
        verifyCanAccess(instructorAndCourseIdParam);
		return instructorAndCourseIdParam;
	}

	/**
	 * @param instructor1OfCourse2
	 */
	private void instructorcanaccess(InstructorAttributes instructor1OfCourse2) {
		loginAsInstructor(instructor1OfCourse2.getGoogleId());

        String[] instructorParam = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        verifyCanAccess(instructorParam);
	}

	/**
	 * @param student1InCourse1
	 * @param courseParam
	 */
	private void studentofanothercoursecannotaccess(StudentAttributes student1InCourse1, String[] courseParam) {
		loginAsStudent(student1InCourse1.getGoogleId());
        verifyCannotAccess(courseParam);
	}

	/**
	 * @param student1InCourse2
	 * @return
	 */
	private String[] studentofthesaemcoursecanaccess(StudentAttributes student1InCourse2) {
		loginAsStudent(student1InCourse2.getGoogleId());
        String[] courseParam = {
                Const.ParamsNames.COURSE_ID, student1InCourse2.getCourse(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };
        verifyCanAccess(courseParam);
		return courseParam;
	}

	/**
	 * @return
	 */
	private String[] studentcanaccess() {
		String[] studentEntityParam = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };
        verifyCanAccess(studentEntityParam);
		return studentEntityParam;
	}

    private void assertInformationHiddenForStudent(FeedbackSessionData data) {
        assertNull(data.getGracePeriod());
        assertNull(data.getSessionVisibleSetting());
        assertNull(data.getCustomSessionVisibleTimestamp());
        assertNull(data.getResponseVisibleSetting());
        assertNull(data.getCustomResponseVisibleTimestamp());
        assertNull(data.getIsClosingEmailEnabled());
        assertNull(data.getIsPublishedEmailEnabled());
        assertEquals(data.getCreatedAtTimestamp(), 0);
    }

    private void assertInformationHidden(FeedbackSessionData data) {
        assertNull(data.getGracePeriod());
        assertNull(data.getIsClosingEmailEnabled());
        assertNull(data.getIsPublishedEmailEnabled());
        assertEquals(data.getCreatedAtTimestamp(), 0);
    }

    private void assertPartialInformationMatch(FeedbackSessionData data, FeedbackSessionAttributes expectedSession) {
        partialInfoMatchMethod(data, expectedSession);

        if (!expectedSession.isVisible()) {
            assertEquals(FeedbackSessionSubmissionStatus.NOT_VISIBLE, data.getSubmissionStatus());
        } else if (expectedSession.isOpened()) {
            assertEquals(FeedbackSessionSubmissionStatus.OPEN, data.getSubmissionStatus());
        } else if (expectedSession.isClosed()) {
            assertEquals(FeedbackSessionSubmissionStatus.CLOSED, data.getSubmissionStatus());
        } else if (expectedSession.isInGracePeriod()) {
            assertEquals(FeedbackSessionSubmissionStatus.GRACE_PERIOD, data.getSubmissionStatus());
        } else if (expectedSession.isVisible() && !expectedSession.isOpened()) {
            assertEquals(FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN, data.getSubmissionStatus());
        }

        if (expectedSession.getDeletedTime() == null) {
            assertNull(data.getDeletedAtTimestamp());
        } else {
            assertEquals(expectedSession.getDeletedTime().toEpochMilli(), data.getDeletedAtTimestamp().longValue());
        }

        assertInformationHidden(data);
    }

	/**
	 * @param data
	 * @param expectedSession
	 */
	private void partialInfoMatchMethod(FeedbackSessionData data, FeedbackSessionAttributes expectedSession) {
		String timeZone = expectedSession.getTimeZone();
        assertEquals(expectedSession.getCourseId(), data.getCourseId());
        assertEquals(timeZone, data.getTimeZone());
        assertEquals(expectedSession.getFeedbackSessionName(), data.getFeedbackSessionName());
        assertEquals(expectedSession.getInstructions(), data.getInstructions());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(expectedSession.getStartTime(),
                timeZone, true).toEpochMilli(),
                data.getSubmissionStartTimestamp());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(expectedSession.getEndTime(),
                timeZone, true).toEpochMilli(),
                data.getSubmissionEndTimestamp());
	}

    private void assertAllInformationMatch(FeedbackSessionData data, FeedbackSessionAttributes expectedSession) {
    	partialInfoMatchMethod(data, expectedSession);
        assertEquals(expectedSession.getGracePeriodMinutes(), data.getGracePeriod().longValue());

        Instant sessionVisibleTime = expectedSession.getSessionVisibleFromTime();
        if (sessionVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            assertEquals(data.getSessionVisibleSetting(), SessionVisibleSetting.AT_OPEN);
        } else {
            assertEquals(data.getSessionVisibleSetting(), SessionVisibleSetting.CUSTOM);
            assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(sessionVisibleTime,
                    timeZone, true).toEpochMilli(),
                    data.getCustomSessionVisibleTimestamp().longValue());
        }

        Instant responseVisibleTime = expectedSession.getResultsVisibleFromTime();
        if (responseVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
            assertEquals(ResponseVisibleSetting.AT_VISIBLE, data.getResponseVisibleSetting());
        } else if (responseVisibleTime.equals(Const.TIME_REPRESENTS_LATER)) {
            assertEquals(ResponseVisibleSetting.LATER, data.getResponseVisibleSetting());
        } else {
            assertEquals(ResponseVisibleSetting.CUSTOM, data.getResponseVisibleSetting());
            assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(responseVisibleTime,
                    timeZone, true).toEpochMilli(),
                    data.getCustomResponseVisibleTimestamp().longValue());
        }

        if (!expectedSession.isVisible()) {
            assertEquals(FeedbackSessionSubmissionStatus.NOT_VISIBLE, data.getSubmissionStatus());
        } else if (expectedSession.isOpened()) {
            assertEquals(FeedbackSessionSubmissionStatus.OPEN, data.getSubmissionStatus());
        } else if (expectedSession.isClosed()) {
            assertEquals(FeedbackSessionSubmissionStatus.CLOSED, data.getSubmissionStatus());
        } else if (expectedSession.isInGracePeriod()) {
            assertEquals(FeedbackSessionSubmissionStatus.GRACE_PERIOD, data.getSubmissionStatus());
        } else if (expectedSession.isVisible() && !expectedSession.isOpened()) {
            assertEquals(FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN, data.getSubmissionStatus());
        }

        if (expectedSession.isPublished()) {
            assertEquals(FeedbackSessionPublishStatus.PUBLISHED, data.getPublishStatus());
        } else {
            assertEquals(FeedbackSessionPublishStatus.NOT_PUBLISHED, data.getPublishStatus());
        }

        assertEquals(expectedSession.isClosingEmailEnabled(), data.getIsClosingEmailEnabled());
        assertEquals(expectedSession.isPublishedEmailEnabled(), data.getIsPublishedEmailEnabled());

        assertEquals(expectedSession.getCreatedTime().toEpochMilli(), data.getCreatedAtTimestamp());
        if (expectedSession.getDeletedTime() == null) {
            assertNull(data.getDeletedAtTimestamp());
        } else {
            assertEquals(expectedSession.getDeletedTime().toEpochMilli(), data.getDeletedAtTimestamp().longValue());
        }
    }

    private void assertAllInstructorSessionsMatch(FeedbackSessionsData sessionsData,
                                                  List<FeedbackSessionAttributes> expectedSessions) {

        sessionMatching(sessionsData, expectedSessions);
    }

	/**
	 * @param sessionsData
	 * @param expectedSessions
	 */
	private void sessionMatching(FeedbackSessionsData sessionsData, List<FeedbackSessionAttributes> expectedSessions) {
		assertEquals(sessionsData.getFeedbackSessions().size(), expectedSessions.size());
        for (FeedbackSessionData sessionData : sessionsData.getFeedbackSessions()) {
            List<FeedbackSessionAttributes> matchedSessions =
                    expectedSessions.stream().filter(session -> session.getFeedbackSessionName().equals(
                            sessionData.getFeedbackSessionName())
                            && session.getCourseId().equals(sessionData.getCourseId())).collect(Collectors.toList());

            assertEquals(1, matchedSessions.size());
            assertAllInformationMatch(sessionData, matchedSessions.get(0));
            assertPartialInformationMatch(sessionData, matchedSessions.get(0));
            assertInformationHiddenForStudent(sessionData);
        }
	}

    private void assertAllStudentSessionsMatch(FeedbackSessionsData sessionsData,
                                               List<FeedbackSessionAttributes> expectedSessions) {

    	  sessionMatching(sessionsData, expectedSessions);

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
