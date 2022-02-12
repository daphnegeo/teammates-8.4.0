package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link SendJoinReminderEmailActionTest}.
 */
public class SendJoinReminderEmailActionTest extends BaseActionTest<SendJoinReminderEmailAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.JOIN_REMIND;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalBundle.instructors.get("instructor1OfCourse1").getCourseId(),
        };

        ______TS("Sending registration emails to all students");

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT, submissionParams);

        ______TS("Sending registration emails to student");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalBundle.instructors.get("instructor1OfCourse1").getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, typicalBundle.students.get("student1InCourse1").getEmail(),
        };
        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT, submissionParams);

        ______TS("Sending registration emails to instructor");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalBundle.instructors.get("instructor1OfCourse1").getCourseId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, typicalBundle.instructors.get("instructor1OfCourse1").getEmail(),
        };
        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, submissionParams);
    }

}
