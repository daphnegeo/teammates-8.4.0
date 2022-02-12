package teammates.ui.webapi;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.output.CourseData;

/**
 * SUT: {@link UpdateCourseAction}.
 */
public class UpdateCourseActionTest extends BaseActionTest<UpdateCourseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    private void verifySessionsInCourseHaveTimeZone(String courseId, String courseTimeZone) {
        List<FeedbackSessionAttributes> sessions = logic.getFeedbackSessionsForCourse(courseId);
        for (FeedbackSessionAttributes session : sessions) {
            assertEquals(courseTimeZone, session.getTimeZone());
        }
    }

    private void verifyCourseData(CourseData data, String courseId, String courseName, String timeZone) {
        assertEquals(data.getCourseId(), courseId);
        assertEquals(data.getCourseName(), courseName);
        assertEquals(data.getTimeZone(), timeZone);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        String courseId = instructor.getCourseId();
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_COURSE, submissionParams);
    }

}
