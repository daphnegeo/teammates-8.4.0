package teammates.common.datatransfer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.Const.InstructorPermissions;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.TaskQueue;
import teammates.common.util.Const.WebPageURIs;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.e2e.cases.BaseE2ETestCase;
import teammates.e2e.cases.StudentHomePageE2ETest;
import teammates.e2e.pageobjects.StudentHomePage;
import teammates.logic.core.FeedbackResponseCommentsLogicTest;
import teammates.test.BaseTestCase;
import teammates.ui.output.InstructorPrivilegeData;
import teammates.ui.output.MessageOutput;
import teammates.ui.output.RegenerateKeyData;
import teammates.ui.output.ResponseVisibleSetting;
import teammates.ui.output.SessionVisibleSetting;
import teammates.ui.request.FeedbackQuestionUpdateRequest;
import teammates.ui.request.FeedbackSessionUpdateRequest;
import teammates.ui.request.InstructorPrivilegeUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.request.StudentUpdateRequest;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.InvalidOperationException;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.RegenerateInstructorKeyAction;
import teammates.ui.webapi.RegenerateInstructorKeyActionTest;
import teammates.ui.webapi.RegenerateStudentKeyAction;
import teammates.ui.webapi.RegenerateStudentKeyActionTest;
import teammates.ui.webapi.RestoreCourseAction;
import teammates.ui.webapi.RestoreCourseActionTest;
import teammates.ui.webapi.SendJoinReminderEmailAction;
import teammates.ui.webapi.SendJoinReminderEmailActionTest;
import teammates.ui.webapi.UnpublishFeedbackSessionAction;
import teammates.ui.webapi.UnpublishFeedbackSessionActionTest;
import teammates.ui.webapi.UpdateFeedbackQuestionActionTest;
import teammates.ui.webapi.UpdateFeedbackSessionAction;
import teammates.ui.webapi.UpdateFeedbackSessionActionTest;
import teammates.ui.webapi.UpdateInstructorPrivilegeAction;
import teammates.ui.webapi.UpdateInstructorPrivilegeActionTest;
import teammates.ui.webapi.UpdateStudentActionTest;

/**
 * Holds a bundle of *Attributes data transfer objects.
 *
 * <p>This class is mainly used for serializing JSON strings.
 */
// CHECKSTYLE.OFF:JavadocVariable each field represents different entity types
public class DataBundle {
    public Map<String, AccountAttributes> accounts = new LinkedHashMap<>();
    public Map<String, CourseAttributes> courses = new LinkedHashMap<>();
    public Map<String, InstructorAttributes> instructors = new LinkedHashMap<>();
    public Map<String, StudentAttributes> students = new LinkedHashMap<>();
    public Map<String, FeedbackSessionAttributes> feedbackSessions = new LinkedHashMap<>();
    public Map<String, FeedbackQuestionAttributes> feedbackQuestions = new LinkedHashMap<>();
    public Map<String, FeedbackResponseAttributes> feedbackResponses = new LinkedHashMap<>();
    public Map<String, FeedbackResponseCommentAttributes> feedbackResponseComments = new LinkedHashMap<>();
    public Map<String, StudentProfileAttributes> profiles = new LinkedHashMap<>();
	@Test
	public void testDeleteFeedbackResponseComments_deleteByResponseId(FeedbackResponseCommentsLogicTest feedbackResponseCommentsLogicTest) {
	
	    BaseTestCase.______TS("typical success case");
	
	    FeedbackResponseCommentAttributes frComment = feedbackResponseCommentsLogicTest.restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q3S1C1");
	    feedbackResponseCommentsLogicTest.verifyPresentInDatabase(frComment);
	    feedbackResponseCommentsLogicTest.frcLogic.deleteFeedbackResponseComments(
	            AttributesDeletionQuery.builder()
	                    .withResponseId(frComment.getFeedbackResponseId())
	                    .build());
	    feedbackResponseCommentsLogicTest.verifyAbsentInDatabase(frComment);
	}
	@Test
	public void testAll(StudentHomePageE2ETest studentHomePageE2ETest) {
	
	    AppUrl url = BaseE2ETestCase.createUrl(WebPageURIs.STUDENT_HOME_PAGE);
	    StudentHomePage homePage = studentHomePageE2ETest.loginToPage(url, StudentHomePage.class, "tm.e2e.SHome.student");
	
	    List<String> courseIds = studentHomePageE2ETest.getAllVisibleCourseIds();
	
	    for (int i = 0; i < courseIds.size(); i++) {
	        String courseId = courseIds.get(i);
	
	        homePage.verifyVisibleCourseToStudents(courseId, i);
	
	        String feedbackSessionName = feedbackSessions.entrySet().stream()
	                .filter(feedbackSession -> courseId.equals(feedbackSession.getValue().getCourseId()))
	                .map(x -> x.getValue().getFeedbackSessionName())
	                .collect(Collectors.joining());
	
	        homePage.verifyVisibleFeedbackSessionToStudents(feedbackSessionName, i);
	    }
	}
	@Test
	public void testExecute_withSectionAlreadyHasMaxNumberOfStudents_shouldFail(UpdateStudentActionTest updateStudentActionTest) throws Exception {
	    InstructorAttributes instructor1OfCourse1 = instructors.get("instructor1OfCourse1");
	    String courseId = instructor1OfCourse1.getCourseId();
	    String sectionInMaxCapacity = "sectionInMaxCapacity";
	
	    StudentAttributes studentToJoinMaxSection = StudentAttributes
	            .builder(courseId, "studentToJoinMaxSection@test.com")
	            .withName("studentToJoinMaxSection ")
	            .withSectionName("RandomUniqueSection")
	            .withTeamName("RandomUniqueTeamName")
	            .withComment("cmt")
	            .build();
	
	    updateStudentActionTest.logic.createStudent(studentToJoinMaxSection);
	
	    for (int i = 0; i < Const.SECTION_SIZE_LIMIT; i++) {
	        StudentAttributes addedStudent = StudentAttributes
	                .builder(courseId, i + "email@test.com")
	                .withName("Name " + i)
	                .withSectionName(sectionInMaxCapacity)
	                .withTeamName("Team " + i)
	                .withComment("cmt" + i)
	                .build();
	
	        updateStudentActionTest.logic.createStudent(addedStudent);
	    }
	
	    List<StudentAttributes> studentList = updateStudentActionTest.logic.getStudentsForCourse(courseId);
	
	    BaseTestCase.assertEquals(Const.SECTION_SIZE_LIMIT,
	            studentList.stream().filter(student -> student.getSection().equals(sectionInMaxCapacity)).count());
	    BaseTestCase.assertEquals(courseId, studentToJoinMaxSection.getCourse());
	
	    StudentUpdateRequest updateRequest =
	            new StudentUpdateRequest(studentToJoinMaxSection.getName(), studentToJoinMaxSection.getEmail(),
	                    studentToJoinMaxSection.getTeam(), sectionInMaxCapacity,
	                    studentToJoinMaxSection.getComments(), true);
	
	    String[] submissionParams = new String[] {
	            ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
	            ParamsNames.STUDENT_EMAIL, studentToJoinMaxSection.getEmail(),
	    };
	
	    InvalidOperationException ioe = updateStudentActionTest.verifyInvalidOperation(updateRequest, submissionParams);
	    BaseTestCase.assertEquals("You are trying enroll more than 100 students in section \"sectionInMaxCapacity\". "
	                    + "To avoid performance problems, please do not enroll more than 100 students in a single section.",
	            ioe.getMessage());
	
	    updateStudentActionTest.verifyNoTasksAdded();
	}
	@Test
	public void testExecute_lastInstructorWithModifyInstructorPrivilege_shouldPreserve(UpdateInstructorPrivilegeActionTest updateInstructorPrivilegeActionTest) {
	    InstructorAttributes instructor1OfCourse4 = instructors.get("instructor1OfCourse4");
	
	    BaseTestCase.assertTrue(instructor1OfCourse4.getPrivileges().isAllowedForPrivilege(
	            InstructorPermissions.CAN_MODIFY_COURSE));
	    BaseTestCase.assertFalse(instructor1OfCourse4.getPrivileges().isAllowedForPrivilege(
	            InstructorPermissions.CAN_MODIFY_SESSION));
	    BaseTestCase.assertTrue(instructor1OfCourse4.getPrivileges().isAllowedForPrivilege(
	            InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
	    BaseTestCase.assertTrue(instructor1OfCourse4.getPrivileges().isAllowedForPrivilege(
	            InstructorPermissions.CAN_MODIFY_STUDENT));
	    BaseTestCase.assertTrue(instructor1OfCourse4.getPrivileges().isAllowedForPrivilege(
	            InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
	    BaseTestCase.assertTrue(instructor1OfCourse4.getPrivileges().isAllowedForPrivilege(
	            InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
	    BaseTestCase.assertTrue(instructor1OfCourse4.getPrivileges().isAllowedForPrivilege(
	            InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
	    BaseTestCase.assertTrue(instructor1OfCourse4.getPrivileges().isAllowedForPrivilege(
	            InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
	
	    List<InstructorAttributes> instructorsWithModifyInstructorPrivilege =
	            updateInstructorPrivilegeActionTest.logic.getInstructorsForCourse(instructor1OfCourse4.getCourseId()).stream().filter(
	                    instructor -> instructor.getPrivileges().isAllowedForPrivilege(
	                    InstructorPermissions.CAN_MODIFY_INSTRUCTOR)).collect(Collectors.toList());
	    BaseTestCase.assertEquals(1, instructorsWithModifyInstructorPrivilege.size());
	    BaseTestCase.assertEquals(instructor1OfCourse4.getGoogleId(), instructorsWithModifyInstructorPrivilege.get(0).getGoogleId());
	
	    String[] submissionParams = new String[] {
	            ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse4.getEmail(),
	            ParamsNames.COURSE_ID, instructor1OfCourse4.getCourseId(),
	    };
	
	    InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();
	    InstructorPrivileges privileges = instructor1OfCourse4.getPrivileges();
	    privileges.getCourseLevelPrivileges().setCanModifyInstructor(false);
	    reqBody.setPrivileges(privileges);
	
	    UpdateInstructorPrivilegeAction action = updateInstructorPrivilegeActionTest.getAction(reqBody, submissionParams);
	
	    JsonResult result = updateInstructorPrivilegeActionTest.getJsonResult(action);
	
	    InstructorPrivilegeData response = (InstructorPrivilegeData) result.getOutput();
	    InstructorPermissionSet courseLevelPrivilegesAfterUpdate = response.getPrivileges().getCourseLevelPrivileges();
	    BaseTestCase.assertTrue(courseLevelPrivilegesAfterUpdate.isCanModifyCourse());
	    BaseTestCase.assertFalse(courseLevelPrivilegesAfterUpdate.isCanModifySession());
	    BaseTestCase.assertTrue(courseLevelPrivilegesAfterUpdate.isCanModifyStudent());
	    BaseTestCase.assertTrue(courseLevelPrivilegesAfterUpdate.isCanModifyInstructor());
	    BaseTestCase.assertTrue(courseLevelPrivilegesAfterUpdate.isCanViewStudentInSections());
	    BaseTestCase.assertTrue(courseLevelPrivilegesAfterUpdate.isCanSubmitSessionInSections());
	    BaseTestCase.assertTrue(courseLevelPrivilegesAfterUpdate.isCanViewSessionInSections());
	    BaseTestCase.assertTrue(courseLevelPrivilegesAfterUpdate.isCanModifySessionCommentsInSections());
	}
	@Test
	public void testExecute_validSessionLevelInput_shouldSucceed(UpdateInstructorPrivilegeActionTest updateInstructorPrivilegeActionTest) {
	    InstructorAttributes helper1OfCourse1 = instructors.get("helperOfCourse1");
	
	    BaseTestCase.assertFalse(helper1OfCourse1.getPrivileges().isAllowedForPrivilege("Tutorial1", "Session1",
	            InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
	    BaseTestCase.assertFalse(helper1OfCourse1.getPrivileges().isAllowedForPrivilege("Tutorial1", "Session1",
	            InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
	    BaseTestCase.assertFalse(helper1OfCourse1.getPrivileges().isAllowedForPrivilege("Tutorial1", "Session1",
	            InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
	
	    String[] submissionParams = new String[] {
	            ParamsNames.INSTRUCTOR_EMAIL, helper1OfCourse1.getEmail(),
	            ParamsNames.COURSE_ID, helper1OfCourse1.getCourseId(),
	    };
	
	    InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();
	    InstructorPrivileges privilege = new InstructorPrivileges();
	    privilege.updatePrivilege("Tutorial1", "Session1",
	            InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, true);
	    privilege.updatePrivilege("Tutorial1", "Session1",
	            InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, true);
	    privilege.updatePrivilege("Tutorial1", "Session1",
	            InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, true);
	    privilege.updatePrivilege("Tutorial1", "Session1",
	            InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
	    reqBody.setPrivileges(privilege);
	
	    UpdateInstructorPrivilegeAction action = updateInstructorPrivilegeActionTest.getAction(reqBody, submissionParams);
	
	    JsonResult result = updateInstructorPrivilegeActionTest.getJsonResult(action);
	
	    InstructorPrivilegeData response = (InstructorPrivilegeData) result.getOutput();
	    InstructorPermissionSet sessionLevelPrivilege = response.getPrivileges().getSessionLevelPrivileges()
	            .get("Tutorial1").get("Session1");
	    BaseTestCase.assertFalse(sessionLevelPrivilege.isCanModifyCourse());
	    BaseTestCase.assertFalse(sessionLevelPrivilege.isCanModifySession());
	    BaseTestCase.assertFalse(sessionLevelPrivilege.isCanModifyStudent());
	    BaseTestCase.assertFalse(sessionLevelPrivilege.isCanModifyInstructor());
	    BaseTestCase.assertFalse(sessionLevelPrivilege.isCanViewStudentInSections());
	    BaseTestCase.assertTrue(sessionLevelPrivilege.isCanSubmitSessionInSections());
	    BaseTestCase.assertTrue(sessionLevelPrivilege.isCanViewSessionInSections());
	    BaseTestCase.assertTrue(sessionLevelPrivilege.isCanModifySessionCommentsInSections());
	
	    // verify the privilege has indeed been updated
	    InstructorAttributes instructor = updateInstructorPrivilegeActionTest.logic.getInstructorForGoogleId(
	            helper1OfCourse1.getCourseId(), helper1OfCourse1.getGoogleId());
	
	    BaseTestCase.assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
	            InstructorPermissions.CAN_MODIFY_COURSE));
	    BaseTestCase.assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
	            InstructorPermissions.CAN_MODIFY_SESSION));
	    BaseTestCase.assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
	            InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
	    BaseTestCase.assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
	            InstructorPermissions.CAN_MODIFY_STUDENT));
	    BaseTestCase.assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
	            InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
	    BaseTestCase.assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
	            InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
	    BaseTestCase.assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
	            InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
	    BaseTestCase.assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
	            InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
	
	    BaseTestCase.assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
	            "Tutorial1", InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
	    BaseTestCase.assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
	            "Tutorial1", InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
	    BaseTestCase.assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
	            "Tutorial1", InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
	    BaseTestCase.assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
	            "Tutorial1", InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
	
	    BaseTestCase.assertTrue(instructor.getPrivileges().isAllowedForPrivilege(
	            "Tutorial1", "Session1", InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
	    BaseTestCase.assertTrue(instructor.getPrivileges().isAllowedForPrivilege(
	            "Tutorial1", "Session1", InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
	    BaseTestCase.assertTrue(instructor.getPrivileges().isAllowedForPrivilege(
	            "Tutorial1", "Session1", InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
	}
	@Test
	public void testExecute_differentFeedbackSessionVisibleResponseVisibleSetting_shouldConvertToSpecialTime(UpdateFeedbackSessionActionTest updateFeedbackSessionActionTest)
	        throws Exception {
	    InstructorAttributes instructor1ofCourse1 = instructors.get("instructor1OfCourse1");
	    FeedbackSessionAttributes session = feedbackSessions.get("session1InCourse1");
	    CourseAttributes course = courses.get("typicalCourse1");
	
	    updateFeedbackSessionActionTest.loginAsInstructor(instructor1ofCourse1.getGoogleId());
	
	    BaseTestCase.______TS("success: Custom time zone, At open show session, 'later' show results");
	
	    updateFeedbackSessionActionTest.logic.updateCourseCascade(
	            CourseAttributes.updateOptionsBuilder(course.getId())
	                    .withTimezone("Asia/Kathmandu")
	                    .build());
	
	    String[] param = new String[] {
	            ParamsNames.COURSE_ID, session.getCourseId(),
	            ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
	    };
	    FeedbackSessionUpdateRequest updateRequest = updateFeedbackSessionActionTest.getTypicalFeedbackSessionUpdateRequest();
	    updateRequest.setSessionVisibleSetting(SessionVisibleSetting.AT_OPEN);
	    updateRequest.setResponseVisibleSetting(ResponseVisibleSetting.LATER);
	
	    UpdateFeedbackSessionAction a = updateFeedbackSessionActionTest.getAction(updateRequest, param);
	    updateFeedbackSessionActionTest.getJsonResult(a);
	
	    session = updateFeedbackSessionActionTest.logic.getFeedbackSession(session.getFeedbackSessionName(), session.getCourseId());
	    BaseTestCase.assertEquals(Const.TIME_REPRESENTS_FOLLOW_OPENING, session.getSessionVisibleFromTime());
	    BaseTestCase.assertEquals(Const.TIME_REPRESENTS_LATER, session.getResultsVisibleFromTime());
	
	    BaseTestCase.______TS("success: At open session visible time, custom results visible time, UTC");
	
	    updateFeedbackSessionActionTest.logic.updateCourseCascade(
	            CourseAttributes.updateOptionsBuilder(course.getId())
	                    .withTimezone("UTC")
	                    .build());
	
	    param = new String[] {
	            ParamsNames.COURSE_ID, session.getCourseId(),
	            ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
	    };
	    updateRequest = updateFeedbackSessionActionTest.getTypicalFeedbackSessionUpdateRequest();
	    updateRequest.setSessionVisibleSetting(SessionVisibleSetting.AT_OPEN);
	
	    a = updateFeedbackSessionActionTest.getAction(updateRequest, param);
	    updateFeedbackSessionActionTest.getJsonResult(a);
	
	    session = updateFeedbackSessionActionTest.logic.getFeedbackSession(session.getFeedbackSessionName(), session.getCourseId());
	    BaseTestCase.assertEquals(Const.TIME_REPRESENTS_FOLLOW_OPENING, session.getSessionVisibleFromTime());
	    BaseTestCase.assertEquals(1547003051000L, session.getResultsVisibleFromTime().toEpochMilli());
	}
	@Test
	public void testExecute_startTimeEarlierThanVisibleTime_shouldGiveInvalidParametersError(UpdateFeedbackSessionActionTest updateFeedbackSessionActionTest) {
	    InstructorAttributes instructor1ofCourse1 = instructors.get("instructor1OfCourse1");
	    FeedbackSessionAttributes session = feedbackSessions.get("session1InCourse1");
	
	    updateFeedbackSessionActionTest.loginAsInstructor(instructor1ofCourse1.getGoogleId());
	
	    String[] param = new String[] {
	            ParamsNames.COURSE_ID, session.getCourseId(),
	            ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
	    };
	    FeedbackSessionUpdateRequest updateRequest = updateFeedbackSessionActionTest.getTypicalFeedbackSessionUpdateRequest();
	    updateRequest.setCustomSessionVisibleTimestamp(
	            updateRequest.getSubmissionStartTime().plusSeconds(10).toEpochMilli());
	
	    InvalidHttpRequestBodyException ihrbe = updateFeedbackSessionActionTest.verifyHttpRequestBodyFailure(updateRequest, param);
	    BaseTestCase.assertEquals("The start time for this feedback session cannot be "
	            + "earlier than the time when the session will be visible.", ihrbe.getMessage());
	}
	@Test
	public void testExecute_invalidGiverRecipientType_shouldThrowException(UpdateFeedbackQuestionActionTest updateFeedbackQuestionActionTest) {
	    InstructorAttributes instructor1ofCourse1 = instructors.get("instructor1OfCourse1");
	    FeedbackSessionAttributes session = feedbackSessions.get("session1InCourse1");
	    FeedbackQuestionAttributes typicalQuestion =
	            updateFeedbackQuestionActionTest.logic.getFeedbackQuestion(session.getFeedbackSessionName(), session.getCourseId(), 1);
	
	    updateFeedbackQuestionActionTest.loginAsInstructor(instructor1ofCourse1.getGoogleId());
	
	    String[] param = new String[] {
	            ParamsNames.FEEDBACK_QUESTION_ID, typicalQuestion.getFeedbackQuestionId(),
	    };
	    FeedbackQuestionUpdateRequest updateRequest = updateFeedbackQuestionActionTest.getTypicalTextQuestionUpdateRequest();
	    updateRequest.setGiverType(FeedbackParticipantType.TEAMS);
	    updateRequest.setRecipientType(FeedbackParticipantType.OWN_TEAM_MEMBERS);
	
	    updateFeedbackQuestionActionTest.verifyHttpRequestBodyFailure(updateRequest, param);
	
	    // question is not updated
	    BaseTestCase.assertEquals(typicalQuestion.getQuestionDescription(),
	            updateFeedbackQuestionActionTest.logic.getFeedbackQuestion(typicalQuestion.getId()).getQuestionDescription());
	}
	@Test
	public void testExecute_invalidRecommendedLength_shouldThrowException(UpdateFeedbackQuestionActionTest updateFeedbackQuestionActionTest) {
	    InstructorAttributes instructor1ofCourse1 = instructors.get("instructor1OfCourse1");
	    FeedbackSessionAttributes session = feedbackSessions.get("session1InCourse1");
	    FeedbackQuestionAttributes typicalQuestion =
	            updateFeedbackQuestionActionTest.logic.getFeedbackQuestion(session.getFeedbackSessionName(), session.getCourseId(), 1);
	
	    updateFeedbackQuestionActionTest.loginAsInstructor(instructor1ofCourse1.getGoogleId());
	
	    String[] param = new String[] {
	            ParamsNames.FEEDBACK_QUESTION_ID, typicalQuestion.getFeedbackQuestionId(),
	    };
	
	    FeedbackQuestionUpdateRequest updateRequest = updateFeedbackQuestionActionTest.getTypicalTextQuestionUpdateRequest();
	    FeedbackTextQuestionDetails textQuestionDetails = new FeedbackTextQuestionDetails();
	    // set recommended length as a negative integer
	    textQuestionDetails.setRecommendedLength(-1);
	    updateRequest.setQuestionDetails(textQuestionDetails);
	
	    updateFeedbackQuestionActionTest.verifyHttpRequestBodyFailure(updateRequest, param);
	
	    // question is not updated
	    BaseTestCase.assertEquals(typicalQuestion.getQuestionDescription(),
	            updateFeedbackQuestionActionTest.logic.getFeedbackQuestion(typicalQuestion.getId()).getQuestionDescription());
	
	    // recommended length does not change
	    BaseTestCase.assertNull(((FeedbackTextQuestionDetails) typicalQuestion.getQuestionDetailsCopy()).getRecommendedLength());
	}
	@Test
	public void testExecute_invalidQuestionNumber_shouldThrowException(UpdateFeedbackQuestionActionTest updateFeedbackQuestionActionTest) {
	    InstructorAttributes instructor1ofCourse1 = instructors.get("instructor1OfCourse1");
	    FeedbackSessionAttributes session = feedbackSessions.get("session1InCourse1");
	    FeedbackQuestionAttributes typicalQuestion =
	            updateFeedbackQuestionActionTest.logic.getFeedbackQuestion(session.getFeedbackSessionName(), session.getCourseId(), 1);
	
	    updateFeedbackQuestionActionTest.loginAsInstructor(instructor1ofCourse1.getGoogleId());
	
	    String[] param = new String[] {
	            ParamsNames.FEEDBACK_QUESTION_ID, typicalQuestion.getFeedbackQuestionId(),
	    };
	    FeedbackQuestionUpdateRequest updateRequest = updateFeedbackQuestionActionTest.getTypicalTextQuestionUpdateRequest();
	    updateRequest.setQuestionNumber(-1);
	
	    updateFeedbackQuestionActionTest.verifyHttpRequestBodyFailure(updateRequest, param);
	
	    // question is not updated
	    BaseTestCase.assertEquals(typicalQuestion.getQuestionDescription(),
	            updateFeedbackQuestionActionTest.logic.getFeedbackQuestion(typicalQuestion.getId()).getQuestionDescription());
	}
	@Test
	public void testExecute(UnpublishFeedbackSessionActionTest unpublishFeedbackSessionActionTest) {
	    InstructorAttributes instructor1OfCourse1 = instructors.get("instructor1OfCourse1");
	    CourseAttributes typicalCourse1 = courses.get("typicalCourse1");
	    FeedbackSessionAttributes sessionPublishedInCourse1 = feedbackSessions.get("closedSession");
	    FeedbackSessionAttributes session1InCourse1 = feedbackSessions.get("session1InCourse1");
	
	    unpublishFeedbackSessionActionTest.loginAsInstructor(instructor1OfCourse1.getGoogleId());
	
	    BaseTestCase.______TS("Not enough parameters");
	
	    unpublishFeedbackSessionActionTest.verifyHttpParameterFailure();
	    unpublishFeedbackSessionActionTest.verifyHttpParameterFailure(ParamsNames.COURSE_ID, typicalCourse1.getId());
	    unpublishFeedbackSessionActionTest.verifyHttpParameterFailure(ParamsNames.FEEDBACK_SESSION_NAME,
	            sessionPublishedInCourse1.getFeedbackSessionName());
	
	    BaseTestCase.______TS("Typical success case");
	
	    BaseTestCase.assertTrue(sessionPublishedInCourse1.isPublished());
	    String[] params = new String[] {
	            ParamsNames.COURSE_ID, typicalCourse1.getId(),
	            ParamsNames.FEEDBACK_SESSION_NAME, sessionPublishedInCourse1.getFeedbackSessionName(),
	    };
	
	    UnpublishFeedbackSessionAction a = unpublishFeedbackSessionActionTest.getAction(params);
	    unpublishFeedbackSessionActionTest.getJsonResult(a);
	
	    // session is unpublished
	    BaseTestCase.assertFalse(unpublishFeedbackSessionActionTest.logic.getFeedbackSession(sessionPublishedInCourse1.getFeedbackSessionName(),
	            typicalCourse1.getId()).isPublished());
	
	    // sent unpublish email task is added
	    BaseTestCase.assertEquals(1, unpublishFeedbackSessionActionTest.mockTaskQueuer.getTasksAdded().size());
	
	    BaseTestCase.______TS("Typical case, session is not published yet");
	
	    BaseTestCase.assertFalse(session1InCourse1.isPublished());
	    params = new String[] {
	            ParamsNames.COURSE_ID, typicalCourse1.getId(),
	            ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
	    };
	
	    a = unpublishFeedbackSessionActionTest.getAction(params);
	    unpublishFeedbackSessionActionTest.getJsonResult(a);
	
	    // session is still unpublished
	    BaseTestCase.assertFalse(unpublishFeedbackSessionActionTest.logic.getFeedbackSession(sessionPublishedInCourse1.getFeedbackSessionName(),
	            typicalCourse1.getId()).isPublished());
	
	    // sent unpublish email task should not be added
	    unpublishFeedbackSessionActionTest.verifyNoEmailsSent();
	}
	@Test
	public void testExecute(SendJoinReminderEmailActionTest sendJoinReminderEmailActionTest) throws Exception {
	    InstructorAttributes instructor1OfCourse1 = instructors.get("instructor1OfCourse1");
	    String instructorId = instructor1OfCourse1.getGoogleId();
	    String courseId = instructor1OfCourse1.getCourseId();
	
	    BaseTestCase.______TS("Not enough parameters");
	
	    sendJoinReminderEmailActionTest.verifyHttpParameterFailure();
	    sendJoinReminderEmailActionTest.verifyHttpParameterFailure(ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.getEmail());
	
	    BaseTestCase.______TS("Typical case: Send email to remind an instructor to register for the course");
	
	    sendJoinReminderEmailActionTest.loginAsInstructor(instructorId);
	    InstructorAttributes anotherInstructorOfCourse1 = instructors.get("instructorNotYetJoinCourse1");
	    String[] submissionParams = new String[] {
	            ParamsNames.COURSE_ID, courseId,
	            ParamsNames.INSTRUCTOR_EMAIL, anotherInstructorOfCourse1.getEmail(),
	    };
	
	    SendJoinReminderEmailAction sendJoinReminderEmailAction = sendJoinReminderEmailActionTest.getAction(submissionParams);
	    JsonResult result = sendJoinReminderEmailActionTest.getJsonResult(sendJoinReminderEmailAction);
	
	    MessageOutput msg = (MessageOutput) result.getOutput();
	    BaseTestCase.assertEquals("An email has been sent to " + anotherInstructorOfCourse1.getEmail(), msg.getMessage());
	
	    sendJoinReminderEmailActionTest.verifySpecifiedTasksAdded(TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_QUEUE_NAME, 1);
	
	    TaskWrapper taskAdded = sendJoinReminderEmailActionTest.mockTaskQueuer.getTasksAdded().get(0);
	    Map<String, String> paramMap = taskAdded.getParamMap();
	    BaseTestCase.assertEquals(courseId, paramMap.get(ParamsNames.COURSE_ID));
	    BaseTestCase.assertEquals(anotherInstructorOfCourse1.getEmail(), paramMap.get(ParamsNames.INSTRUCTOR_EMAIL));
	
	    BaseTestCase.______TS("Typical case: Send email to remind a student to register for the course");
	
	    StudentAttributes student1InCourse1 = students.get("student1InCourse1");
	    submissionParams = new String[] {
	            ParamsNames.COURSE_ID, courseId,
	            ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
	    };
	
	    sendJoinReminderEmailAction = sendJoinReminderEmailActionTest.getAction(submissionParams);
	    result = sendJoinReminderEmailActionTest.getJsonResult(sendJoinReminderEmailAction);
	
	    msg = (MessageOutput) result.getOutput();
	    BaseTestCase.assertEquals("An email has been sent to " + student1InCourse1.getEmail(), msg.getMessage());
	
	    sendJoinReminderEmailActionTest.verifySpecifiedTasksAdded(TaskQueue.STUDENT_COURSE_JOIN_EMAIL_QUEUE_NAME, 1);
	
	    taskAdded = sendJoinReminderEmailActionTest.mockTaskQueuer.getTasksAdded().get(0);
	    paramMap = taskAdded.getParamMap();
	    BaseTestCase.assertEquals(courseId, paramMap.get(ParamsNames.COURSE_ID));
	    BaseTestCase.assertEquals(student1InCourse1.getEmail(), paramMap.get(ParamsNames.STUDENT_EMAIL));
	
	    BaseTestCase.______TS("Masquerade mode: Send emails to all unregistered student to remind registering for the course");
	
	    sendJoinReminderEmailActionTest.loginAsAdmin();
	    StudentAttributes unregisteredStudent1 = StudentAttributes
	            .builder(courseId, "unregistered1@email.com")
	            .withName("Unregistered student 1")
	            .withSectionName("Section 1")
	            .withTeamName("Team Unregistered")
	            .withComment("")
	            .build();
	    StudentAttributes unregisteredStudent2 = StudentAttributes
	            .builder(courseId, "unregistered2@email.com")
	            .withName("Unregistered student 2")
	            .withSectionName("Section 1")
	            .withTeamName("Team Unregistered")
	            .withComment("")
	            .build();
	    sendJoinReminderEmailActionTest.logic.createStudent(unregisteredStudent1);
	    sendJoinReminderEmailActionTest.logic.createStudent(unregisteredStudent2);
	
	    // Reassign the attributes to retrieve their keys
	    unregisteredStudent1 = sendJoinReminderEmailActionTest.logic.getStudentForEmail(courseId, unregisteredStudent1.getEmail());
	    unregisteredStudent2 = sendJoinReminderEmailActionTest.logic.getStudentForEmail(courseId, unregisteredStudent2.getEmail());
	
	    submissionParams = new String[] {
	            ParamsNames.COURSE_ID, courseId,
	    };
	    sendJoinReminderEmailAction = sendJoinReminderEmailActionTest.getAction(sendJoinReminderEmailActionTest.addUserIdToParams(instructorId, submissionParams));
	    result = sendJoinReminderEmailActionTest.getJsonResult(sendJoinReminderEmailAction);
	
	    msg = (MessageOutput) result.getOutput();
	    BaseTestCase.assertEquals("Emails have been sent to unregistered students.", msg.getMessage());
	
	    // 2 unregistered students, thus 2 emails queued to be sent
	    sendJoinReminderEmailActionTest.verifySpecifiedTasksAdded(TaskQueue.STUDENT_COURSE_JOIN_EMAIL_QUEUE_NAME, 2);
	
	    List<TaskWrapper> tasksAdded = sendJoinReminderEmailActionTest.mockTaskQueuer.getTasksAdded();
	    for (TaskWrapper task : tasksAdded) {
	        paramMap = task.getParamMap();
	        BaseTestCase.assertEquals(courseId, paramMap.get(ParamsNames.COURSE_ID));
	    }
	
	    sendJoinReminderEmailActionTest.logic.deleteStudentCascade(courseId, unregisteredStudent1.getEmail());
	    sendJoinReminderEmailActionTest.logic.deleteStudentCascade(courseId, unregisteredStudent2.getEmail());
	
	    BaseTestCase.______TS("Typical case: no unregistered students in course");
	
	    submissionParams = new String[] {
	            ParamsNames.COURSE_ID, courseId,
	    };
	    sendJoinReminderEmailAction = sendJoinReminderEmailActionTest.getAction(sendJoinReminderEmailActionTest.addUserIdToParams(instructorId, submissionParams));
	    result = sendJoinReminderEmailActionTest.getJsonResult(sendJoinReminderEmailAction);
	
	    msg = (MessageOutput) result.getOutput();
	    BaseTestCase.assertEquals("Emails have been sent to unregistered students.", msg.getMessage());
	
	    // no unregistered students, thus no emails sent
	    sendJoinReminderEmailActionTest.verifyNoTasksAdded();
	
	    BaseTestCase.______TS("Failure case: Invalid email parameter");
	
	    String invalidEmail = "invalidEmail.com";
	    String[] invalidInstructorEmailSubmissionParams = new String[] {
	            ParamsNames.COURSE_ID, courseId,
	            ParamsNames.INSTRUCTOR_EMAIL, invalidEmail,
	    };
	
	    EntityNotFoundException entityNotFoundException = sendJoinReminderEmailActionTest.verifyEntityNotFound(
	            sendJoinReminderEmailActionTest.addUserIdToParams(instructorId, invalidInstructorEmailSubmissionParams));
	    BaseTestCase.assertEquals("Instructor with email " + invalidEmail + " does not exist "
	            + "in course " + courseId + "!", entityNotFoundException.getMessage());
	
	    String[] invalidStudentEmailSubmissionParams = new String[] {
	            ParamsNames.COURSE_ID, courseId,
	            ParamsNames.STUDENT_EMAIL, invalidEmail,
	    };
	
	    entityNotFoundException = sendJoinReminderEmailActionTest.verifyEntityNotFound(invalidStudentEmailSubmissionParams);
	    BaseTestCase.assertEquals("Student with email " + invalidEmail + " does not exist "
	            + "in course " + courseId + "!", entityNotFoundException.getMessage());
	
	    BaseTestCase.______TS("Failure case: Invalid course id parameter");
	
	    String[] invalidCourseIdSubmissionParams = new String[] {
	            ParamsNames.COURSE_ID, "invalidCourseId",
	            ParamsNames.INSTRUCTOR_EMAIL, anotherInstructorOfCourse1.getEmail(),
	    };
	
	    entityNotFoundException = sendJoinReminderEmailActionTest.verifyEntityNotFound(invalidCourseIdSubmissionParams);
	    BaseTestCase.assertEquals("Course with ID invalidCourseId does not exist!", entityNotFoundException.getMessage());
	}
	@Test
	public void testExecute(RestoreCourseActionTest restoreCourseActionTest) throws Exception {
	    InstructorAttributes instructor1OfCourse1 = instructors.get("instructor1OfCourse1");
	    String instructorId = instructor1OfCourse1.getGoogleId();
	    String courseId = instructor1OfCourse1.getCourseId();
	
	    restoreCourseActionTest.loginAsInstructor(instructorId);
	
	    BaseTestCase.______TS("Not in recycle bin but valid course");
	
	    String[] submissionParams = new String[] {
	            ParamsNames.COURSE_ID, courseId,
	    };
	
	    RestoreCourseAction action = restoreCourseActionTest.getAction(submissionParams);
	    JsonResult result = restoreCourseActionTest.getJsonResult(action);
	    MessageOutput message = (MessageOutput) result.getOutput();
	
	    BaseTestCase.assertEquals("The course " + courseId + " has been restored.", message.getMessage());
	    BaseTestCase.assertNull(restoreCourseActionTest.logic.getCourse(instructor1OfCourse1.getCourseId()).getDeletedAt());
	
	    BaseTestCase.______TS("Typical case, restore a deleted course from Recycle Bin");
	
	    submissionParams = new String[] {
	            ParamsNames.COURSE_ID, courseId,
	    };
	
	    restoreCourseActionTest.logic.moveCourseToRecycleBin(courseId);
	    CourseAttributes deletedCourse = restoreCourseActionTest.logic.getCourse(courseId);
	    BaseTestCase.assertNotNull(deletedCourse);
	    BaseTestCase.assertTrue(deletedCourse.isCourseDeleted());
	
	    action = restoreCourseActionTest.getAction(submissionParams);
	    result = restoreCourseActionTest.getJsonResult(action);
	    message = (MessageOutput) result.getOutput();
	
	    BaseTestCase.assertEquals("The course " + courseId + " has been restored.", message.getMessage());
	    BaseTestCase.assertNull(restoreCourseActionTest.logic.getCourse(instructor1OfCourse1.getCourseId()).getDeletedAt());
	
	    BaseTestCase.______TS("Not enough parameters");
	
	    restoreCourseActionTest.verifyHttpParameterFailure();
	
	    BaseTestCase.______TS("Non-Existent Course");
	
	    String[] nonExistentCourse = new String[] {
	            ParamsNames.COURSE_ID, "123C",
	    };
	    restoreCourseActionTest.verifyEntityNotFound(nonExistentCourse);
	}
	@Test
	public void testExecute_regenerateStudentKey(RegenerateStudentKeyActionTest regenerateStudentKeyActionTest) {
	    StudentAttributes student1InCourse1 = students.get("student1InCourse1");
	    BaseTestCase.______TS("Successfully sent regenerated links email");
	
	    String[] param = new String[] {
	            ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
	            ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
	    };
	
	    RegenerateStudentKeyAction a = regenerateStudentKeyActionTest.getAction(param);
	    JsonResult result = regenerateStudentKeyActionTest.getJsonResult(a);
	
	    RegenerateKeyData output = (RegenerateKeyData) result.getOutput();
	
	    BaseTestCase.assertEquals(RegenerateStudentKeyAction.SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT, output.getMessage());
	    BaseTestCase.assertNotEquals(student1InCourse1.getKey(), output.getNewRegistrationKey());
	
	    regenerateStudentKeyActionTest.verifyNumberOfEmailsSent(1);
	
	    EmailWrapper emailSent = regenerateStudentKeyActionTest.mockEmailSender.getEmailsSent().get(0);
	    BaseTestCase.assertEquals(String.format(EmailType.STUDENT_COURSE_LINKS_REGENERATED.getSubject(),
	                                courses.get("typicalCourse1").getName(), student1InCourse1.getCourse()),
	                 emailSent.getSubject());
	    BaseTestCase.assertEquals(student1InCourse1.getEmail(), emailSent.getRecipient());
	}
	@Test
	public void testExecute_regenerateInstructorKey(RegenerateInstructorKeyActionTest regenerateInstructorKeyActionTest) {
	    InstructorAttributes instructor1OfCourse1 = instructors.get("instructor1OfCourse1");
	    BaseTestCase.______TS("Successfully sent regenerated links email");
	
	    String[] param = new String[] {
	            ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.getEmail(),
	            ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
	    };
	
	    RegenerateInstructorKeyAction a = regenerateInstructorKeyActionTest.getAction(param);
	    JsonResult result = regenerateInstructorKeyActionTest.getJsonResult(a);
	
	    RegenerateKeyData output = (RegenerateKeyData) result.getOutput();
	
	    BaseTestCase.assertEquals(RegenerateInstructorKeyAction.SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT, output.getMessage());
	    BaseTestCase.assertNotEquals(instructor1OfCourse1.getKey(), output.getNewRegistrationKey());
	
	    regenerateInstructorKeyActionTest.verifyNumberOfEmailsSent(1);
	
	    EmailWrapper emailSent = regenerateInstructorKeyActionTest.mockEmailSender.getEmailsSent().get(0);
	    BaseTestCase.assertEquals(String.format(EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED.getSubject(),
	                               courses.get("typicalCourse1").getName(),
	                               instructor1OfCourse1.getCourseId()),
	                 emailSent.getSubject());
	    BaseTestCase.assertEquals(instructor1OfCourse1.getEmail(), emailSent.getRecipient());
	}
}
