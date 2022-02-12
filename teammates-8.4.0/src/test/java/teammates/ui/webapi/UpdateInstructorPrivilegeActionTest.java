package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.output.InstructorPrivilegeData;
import teammates.ui.request.InstructorPrivilegeUpdateRequest;

/**
 * SUT: {@link UpdateInstructorPrivilegeAction}.
 */
public class UpdateInstructorPrivilegeActionTest extends BaseActionTest<UpdateInstructorPrivilegeAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_PRIVILEGE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    protected void testExecute() {
        // see individual tests
    }

    @Test
    protected void testExecute_validCourseLevelInput_shouldSucceed() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        assertTrue(instructor1OfCourse1.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_COURSE));
        assertTrue(instructor1OfCourse1.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION));
        assertTrue(instructor1OfCourse1.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        assertTrue(instructor1OfCourse1.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        assertTrue(instructor1OfCourse1.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(instructor1OfCourse1.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(instructor1OfCourse1.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertTrue(instructor1OfCourse1.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();
        reqBody.setPrivileges(new InstructorPrivileges());

        UpdateInstructorPrivilegeAction action = getAction(reqBody, submissionParams);

        JsonResult result = getJsonResult(action);

        InstructorPrivilegeData response = (InstructorPrivilegeData) result.getOutput();
        InstructorPermissionSet courseLevelPrivilege = response.getPrivileges().getCourseLevelPrivileges();
        assertFalse(courseLevelPrivilege.isCanModifyCourse());
        assertFalse(courseLevelPrivilege.isCanModifySession());
        assertFalse(courseLevelPrivilege.isCanModifyStudent());
        assertFalse(courseLevelPrivilege.isCanModifyInstructor());
        assertFalse(courseLevelPrivilege.isCanViewStudentInSections());
        assertFalse(courseLevelPrivilege.isCanSubmitSessionInSections());
        assertFalse(courseLevelPrivilege.isCanViewSessionInSections());
        assertFalse(courseLevelPrivilege.isCanModifySessionCommentsInSections());

        // verify the privilege has indeed been updated
        InstructorAttributes instructor = logic.getInstructorForGoogleId(
                instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getGoogleId());

        assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_COURSE));
        assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION));
        assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
    }

    @Test
    protected void testExecute_validSectionLevelInput_shouldSucceed() {
        InstructorAttributes helper1OfCourse1 = typicalBundle.instructors.get("helperOfCourse1");

        assertFalse(helper1OfCourse1.getPrivileges().isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        assertFalse(helper1OfCourse1.getPrivileges().isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertFalse(helper1OfCourse1.getPrivileges().isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertFalse(helper1OfCourse1.getPrivileges().isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, helper1OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, helper1OfCourse1.getCourseId(),
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();
        InstructorPrivileges privilege = new InstructorPrivileges();
        privilege.updatePrivilege("TUT1", Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, true);
        privilege.updatePrivilege("TUT1", Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, true);
        privilege.updatePrivilege("TUT1", Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, true);
        privilege.updatePrivilege("TUT1", Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        reqBody.setPrivileges(privilege);

        UpdateInstructorPrivilegeAction action = getAction(reqBody, submissionParams);

        JsonResult result = getJsonResult(action);

        InstructorPrivilegeData response = (InstructorPrivilegeData) result.getOutput();
        InstructorPermissionSet sectionLevelPrivilege = response.getPrivileges().getSectionLevelPrivileges().get("TUT1");
        assertFalse(sectionLevelPrivilege.isCanModifyCourse());
        assertFalse(sectionLevelPrivilege.isCanModifySession());
        assertFalse(sectionLevelPrivilege.isCanModifyStudent());
        assertFalse(sectionLevelPrivilege.isCanModifyInstructor());
        assertTrue(sectionLevelPrivilege.isCanViewStudentInSections());
        assertTrue(sectionLevelPrivilege.isCanSubmitSessionInSections());
        assertTrue(sectionLevelPrivilege.isCanViewSessionInSections());
        assertTrue(sectionLevelPrivilege.isCanModifySessionCommentsInSections());

        // verify the privilege has indeed been updated
        InstructorAttributes instructor = logic.getInstructorForGoogleId(
                helper1OfCourse1.getCourseId(), helper1OfCourse1.getGoogleId());

        assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_COURSE));
        assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION));
        assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        assertTrue(instructor.getPrivileges().isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(instructor.getPrivileges().isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(instructor.getPrivileges().isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertTrue(instructor.getPrivileges().isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
    }

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.DataBundle#testExecute_validSessionLevelInput_shouldSucceed(teammates.ui.webapi.UpdateInstructorPrivilegeActionTest)} instead
	 */
	@Test
	protected void testExecute_validSessionLevelInput_shouldSucceed() {
		typicalBundle.testExecute_validSessionLevelInput_shouldSucceed(this);
	}

    @Test
    protected void testExecute_requestPrivilegesInconsistent_shouldBeAutoFixed() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        assertTrue(instructor1OfCourse1.getPrivileges().isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(instructor1OfCourse1.getPrivileges().isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(instructor1OfCourse1.getPrivileges().isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertTrue(instructor1OfCourse1.getPrivileges().isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();
        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        reqBody.setPrivileges(privileges);

        UpdateInstructorPrivilegeAction action = getAction(reqBody, submissionParams);

        JsonResult result = getJsonResult(action);

        InstructorPrivilegeData response = (InstructorPrivilegeData) result.getOutput();
        InstructorPermissionSet courseLevelPrivilegesAfterUpdate = response.getPrivileges().getCourseLevelPrivileges();
        assertTrue(courseLevelPrivilegesAfterUpdate.isCanViewSessionInSections());
        assertTrue(courseLevelPrivilegesAfterUpdate.isCanModifySessionCommentsInSections());
    }

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.DataBundle#testExecute_lastInstructorWithModifyInstructorPrivilege_shouldPreserve(teammates.ui.webapi.UpdateInstructorPrivilegeActionTest)} instead
	 */
	@Test
	protected void testExecute_lastInstructorWithModifyInstructorPrivilege_shouldPreserve() {
		typicalBundle.testExecute_lastInstructorWithModifyInstructorPrivilege_shouldPreserve(this);
	}

    @Test
    protected void testExecute_withNullPrivileges_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();

        verifyHttpRequestBodyFailure(reqBody, submissionParams);
    }

    @Test
    protected void testExecute_withInvalidInstructorEmail_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, "invalid-instructor-email",
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();
        reqBody.setPrivileges(new InstructorPrivileges());

        EntityNotFoundException enfe = verifyEntityNotFound(reqBody, submissionParams);
        assertEquals("Instructor does not exist.", enfe.getMessage());

    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor3OfCourse1");

        ______TS("only instructors of the same course with modify permission can access");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, submissionParams);

        ______TS("invalid course id cannot access");

        String[] invalidCouseIdParams = new String[] {
                Const.ParamsNames.COURSE_ID, "invalid-course-id",
        };

        verifyCannotAccess(invalidCouseIdParams);
    }
}

