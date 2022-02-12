package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.output.CourseData;

/**
 * SUT: {@link BinCourseAction}.
 */
public class BinCourseActionTest extends BaseActionTest<BinCourseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.BIN_COURSE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Test
    protected void testExecute_nonExistentCourse_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.getGoogleId();

        loginAsInstructor(instructorId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "fake-course",
        };

        assertNull(logic.getCourse("fake-course"));

        EntityNotFoundException e = verifyEntityNotFound(submissionParams);
        assertEquals("Trying to update non-existent Entity: ", e.getMessage());
    }

    @Test
    protected void testExecute_courseAlreadyBin_shouldFail() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.getGoogleId();

        loginAsInstructor(instructorId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        logic.moveCourseToRecycleBin(instructor1OfCourse1.getCourseId());
        CourseAttributes courseInformation = logic.getCourse(instructor1OfCourse1.getCourseId());
        assertNotNull(courseInformation.getDeletedAt());

        BinCourseAction binCourseAction = getAction(submissionParams);
        JsonResult result = getJsonResult(binCourseAction);
        CourseData courseData = (CourseData) result.getOutput();

        verifyCourseData(courseData, courseInformation.getId(), courseInformation.getName(),
                courseInformation.getTimeZone());
    }

    private void verifyCourseData(CourseData data, String courseId, String courseName, String timeZone) {
        assertEquals(data.getCourseId(), courseId);
        assertEquals(data.getCourseName(), courseName);
        assertEquals(data.getTimeZone(), timeZone);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1",
        };
        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_COURSE, submissionParams);
    }
}
