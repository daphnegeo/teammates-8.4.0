package teammates.e2e.cases;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;

/**
 * SUT: {@link Const.WebPageURIs#STUDENT_HOME_PAGE}.
 */
public class StudentHomePageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/StudentHomePageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.DataBundle#testAll(teammates.e2e.cases.StudentHomePageE2ETest)} instead
	 */
	@Test
	@Override
	public void testAll() {
		testData.testAll(this);
	}

    private List<String> getAllVisibleCourseIds() {
        List<String> courseIds = new ArrayList<>();

        for (StudentAttributes student : testData.students.values()) {
            if ("tm.e2e.SHome.student".equals(student.getGoogleId())) {
                courseIds.add(student.getCourse());
            }
        }
        return courseIds;
    }

}
