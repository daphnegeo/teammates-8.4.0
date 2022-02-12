package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.StudentUpdateRequest;

/**
 * SUT: {@link UpdateStudentAction}.
 */
public class UpdateStudentActionTest extends BaseActionTest<UpdateStudentAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Test
    public void testExecute_withTeamNameAlreadyExistsInAnotherSection_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        StudentAttributes student5InCourse1 = typicalBundle.students.get("student5InCourse1");

        assertNotEquals(student1InCourse1.getSection(), student5InCourse1.getSection());

        StudentUpdateRequest updateRequest = new StudentUpdateRequest(student1InCourse1.getName(),
                student1InCourse1.getEmail(), student5InCourse1.getTeam(), student1InCourse1.getSection(),
                student1InCourse1.getComments(), true);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        InvalidOperationException ioe = verifyInvalidOperation(updateRequest, submissionParams);
        assertEquals("Team \"Team 1.2\" is detected in both Section \"Section 1\" "
                        + "and Section \"Section 2\". Please use different team names in different sections.",
                ioe.getMessage());

        verifyNoTasksAdded();
    }

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.DataBundle#testExecute_withSectionAlreadyHasMaxNumberOfStudents_shouldFail(teammates.ui.webapi.UpdateStudentActionTest)} instead
	 */
	@Test
	public void testExecute_withSectionAlreadyHasMaxNumberOfStudents_shouldFail() throws Exception {
		typicalBundle.testExecute_withSectionAlreadyHasMaxNumberOfStudents_shouldFail(this);
	}

    @Test
    public void testExecute_withEmptySectionName_shouldBeUpdatedWithDefaultSectionName() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student5InCourse1 = typicalBundle.students.get("student5InCourse1");

        StudentUpdateRequest emptySectionUpdateRequest =
                new StudentUpdateRequest(student5InCourse1.getName(), student5InCourse1.getEmail(),
                        student5InCourse1.getTeam(), "", student5InCourse1.getComments(), true);

        String[] emptySectionSubmissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student5InCourse1.getEmail(),
        };

        UpdateStudentAction updateEmptySectionAction =
                getAction(emptySectionUpdateRequest, emptySectionSubmissionParams);
        JsonResult emptySectionActionOutput = getJsonResult(updateEmptySectionAction);

        MessageOutput emptySectionMsgOutput = (MessageOutput) emptySectionActionOutput.getOutput();
        assertEquals("Student has been updated", emptySectionMsgOutput.getMessage());
        verifyNoEmailsSent();

        // verify student in database
        StudentAttributes actualStudent =
                logic.getStudentForEmail(student5InCourse1.getCourse(), student5InCourse1.getEmail());
        assertEquals(student5InCourse1.getCourse(), actualStudent.getCourse());
        assertEquals(student5InCourse1.getName(), actualStudent.getName());
        assertEquals(student5InCourse1.getEmail(), actualStudent.getEmail());
        assertEquals(student5InCourse1.getTeam(), actualStudent.getTeam());
        assertEquals(Const.DEFAULT_SECTION, actualStudent.getSection());
        assertEquals(student5InCourse1.getComments(), actualStudent.getComments());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student3InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT, submissionParams);
    }
}
