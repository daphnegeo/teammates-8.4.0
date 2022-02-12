package teammates.e2e.cases;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;

/**
 * SUT: {@link Const.WebPageURIs#JOIN_PAGE}.
 */
public class StudentCourseJoinConfirmationPageE2ETest extends BaseE2ETestCase {
    private StudentAttributes newStudent;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/StudentCourseJoinConfirmationPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        newStudent = testData.students.get("alice.tmms@SCJoinConf.CS2104");
        newStudent.setGoogleId(testData.accounts.get("alice.tmms").getGoogleId());
    }

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.attributes.StudentAttributes#testAll(teammates.e2e.cases.StudentCourseJoinConfirmationPageE2ETest)} instead
	 */
	@Test
	@Override
	public void testAll() {
		newStudent.testAll(this);
	}
}
