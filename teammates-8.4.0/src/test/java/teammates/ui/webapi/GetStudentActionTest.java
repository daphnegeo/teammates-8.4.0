package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.output.JoinState;
import teammates.ui.output.StudentData;

/**
 * SUT: {@link GetStudentAction}.
 */
public class GetStudentActionTest extends BaseActionTest<GetStudentAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    private void assertStudentDataMatches(StudentData studentData, StudentAttributes student,
                                          boolean isRequestFromInstructor) {
        assertNull(studentData.getGoogleId());
        assertNull(studentData.getKey());
        assertEquals(student.getEmail(), studentData.getEmail());
        assertEquals(student.getCourse(), studentData.getCourseId());
        assertEquals(student.getName(), studentData.getName());

        assertEquals(student.getTeam(), studentData.getTeamName());
        assertEquals(student.getSection(), studentData.getSectionName());

        if (isRequestFromInstructor) {
            assertEquals(student.getComments(), studentData.getComments());

            if (student.isRegistered()) {
                assertTrue(studentData.getJoinState().equals(JoinState.JOINED));
            } else {
                assertTrue(studentData.getJoinState().equals(JoinState.NOT_JOINED));
            }
        } else {
            assertNull(studentData.getComments());
            assertNull(studentData.getJoinState());
        }
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        StudentAttributes student2InCourse2 = typicalBundle.students.get("student2InCourse2");

        ______TS("Student - must be in the course");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
        };

        verifyAccessibleForStudentsOfTheSameCourse(submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student2InCourse2.getCourse(),
        };

        verifyInaccessibleForStudents(submissionParams);

        ______TS("Student - cannot access another student's details");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student2InCourse2.getCourse(),
                Const.ParamsNames.STUDENT_EMAIL, student2InCourse2.getEmail(),
        };

        verifyInaccessibleForStudents(submissionParams);

        ______TS("Student - cannot access a non-existent email");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student2InCourse2.getCourse(),
                Const.ParamsNames.STUDENT_EMAIL, "TEST_EMAIL",
        };

        verifyInaccessibleForStudents(submissionParams);

        ______TS("Instructor - must be in same course as student");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };
        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);

        InstructorAttributes helperOfCourse1 = typicalBundle.instructors.get("helperOfCourse1");
        loginAsInstructor(helperOfCourse1.getGoogleId());
        verifyCannotAccess(submissionParams);

        grantInstructorWithSectionPrivilege(helperOfCourse1,
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS,
                new String[] {"Section 1"});
        verifyCanAccess(submissionParams);

        ______TS("Instructor - must provide student email");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
        };

        verifyInaccessibleForInstructors(submissionParams);

        ______TS("Unregistered Student - can access with key");

        StudentAttributes unregStudent =
                logic.getStudentForEmail("idOfTypicalCourse1", "student1InCourse1@gmail.tmt");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, unregStudent.getCourse(),
        };

        verifyInaccessibleForUnregisteredUsers(submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, unregStudent.getCourse(),
                Const.ParamsNames.REGKEY, "RANDOM_KEY",
        };

        verifyInaccessibleForUnregisteredUsers(submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, unregStudent.getCourse(),
                Const.ParamsNames.REGKEY, unregStudent.getKey(),
        };

        verifyAccessibleForUnregisteredUsers(submissionParams);
    }
}
