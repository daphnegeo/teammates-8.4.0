package teammates.e2e.cases;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_EDIT_PAGE}.
 */
public class InstructorCourseEditPageE2ETest extends BaseE2ETestCase {
    CourseAttributes course;
    public InstructorAttributes[] instructors = new InstructorAttributes[5];

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCourseEditPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        course = testData.courses.get("ICEdit.CS2104");
        instructors[0] = testData.instructors.get("ICEdit.helper");
        instructors[1] = testData.instructors.get("ICEdit.manager");
        instructors[2] = testData.instructors.get("ICEdit.observer");
        instructors[3] = testData.instructors.get("ICEdit.coowner");
        instructors[4] = testData.instructors.get("ICEdit.tutor");
    }

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.attributes.CourseAttributes#testAll(teammates.e2e.cases.InstructorCourseEditPageE2ETest)} instead
	 */
	@Test
	@Override
	public void testAll() {
		course.testAll(this);
	}
}
