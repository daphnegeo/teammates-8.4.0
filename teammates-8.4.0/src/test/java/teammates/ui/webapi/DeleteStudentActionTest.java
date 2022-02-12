package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;

/**
 * SUT: {@link DeleteStudentAction}.
 */
public class DeleteStudentActionTest extends BaseActionTest<DeleteStudentAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student5InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        verifyAccessibleForAdmin(submissionParams);
        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT, submissionParams);
    }

}
