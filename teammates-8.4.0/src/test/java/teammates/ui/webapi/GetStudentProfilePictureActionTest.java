package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;

/**
 * SUT: {@link GetStudentProfilePictureAction}.
 */
public class GetStudentProfilePictureActionTest extends BaseActionTest<GetStudentProfilePictureAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_PROFILE_PICTURE;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        StudentAttributes student2InCourse1 = typicalBundle.students.get("student2InCourse1");
        StudentAttributes student1InCourse3 = typicalBundle.students.get("student1InCourse3");
        StudentAttributes student5InCourse1 = typicalBundle.students.get("student5InCourse1");

        ______TS("Failure case: student can only view his own team in the course");

        //student from another team
        logoutUser();
        loginAsStudent(student5InCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        verifyCannotAccess(submissionParams);

        //student from another course
        logoutUser();
        loginAsStudent(student1InCourse3.getGoogleId());

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        verifyCannotAccess(submissionParams);

        ______TS("Success case: student can only view his own team in the course");

        logoutUser();
        loginAsStudent(student2InCourse1.getGoogleId());

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        verifyCanAccess(submissionParams);

        ______TS("Success case: student can view his own photo but instructor or admin cannot");

        logoutUser();
        loginAsStudent(student1InCourse1.getGoogleId());

        verifyCanAccess();
        verifyInaccessibleForInstructors();
        verifyInaccessibleForAdmin();

        ______TS("Success/Failure case: only instructors with privilege can view photo");

        logoutUser();

        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);

        InstructorAttributes helperOfCourse1 = typicalBundle.instructors.get("helperOfCourse1");
        loginAsInstructor(helperOfCourse1.getGoogleId());
        verifyCannotAccess(submissionParams);

        grantInstructorWithSectionPrivilege(helperOfCourse1,
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS,
                new String[] {"Section 1"});
        verifyCanAccess(submissionParams);

        ______TS("Failure case: error in params (passing in non-existent email/id)");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "RANDOM_COURSE",
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        verifyInaccessibleForStudents(submissionParams);
        verifyInaccessibleForInstructors(submissionParams);
        verifyInaccessibleForAdmin(submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getId(),
                Const.ParamsNames.STUDENT_EMAIL, "RANDOM_EMAIL",
        };

        verifyInaccessibleForStudents(submissionParams);
        verifyInaccessibleForInstructors(submissionParams);
        verifyInaccessibleForAdmin(submissionParams);
    }
}
