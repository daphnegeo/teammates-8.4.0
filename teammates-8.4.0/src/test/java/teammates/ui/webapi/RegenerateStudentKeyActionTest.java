package teammates.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;

/**
 * SUT: {@link RegenerateStudentKeyAction}.
 */
public class RegenerateStudentKeyActionTest extends BaseActionTest<RegenerateStudentKeyAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_KEY;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    public void baseClassSetup() {
        loginAsAdmin();
    }

    @Test
    protected void testExecute_notEnoughParameters() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        ______TS("Invalid parameters");

        //no parameters
        verifyHttpParameterFailure();

        //null student email
        String[] invalidParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
        };
        verifyHttpParameterFailure(invalidParams);

        //null course id
        invalidParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };
        verifyHttpParameterFailure(invalidParams);
    }

    @Test
    protected void testExecute_nonExistentCourse_shouldFail() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        ______TS("course does not exist");

        String[] nonExistingParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, "non-existent-course",
        };

        assertNull(logic.getCourse("non-existent-course"));

        verifyEntityNotFound(nonExistingParams);
    }

    @Test
    protected void testExecute_nonExistentStudentInCourse_shouldFail() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        ______TS("student with email address does not exist in course");

        String[] nonExistingParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, "non-existent-student@abc.com",
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
        };

        assertNull(logic.getStudentForEmail(student1InCourse1.getCourse(), "non-existent-student@abc.com"));

        verifyEntityNotFound(nonExistingParams);
    }

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.DataBundle#testExecute_regenerateStudentKey(teammates.ui.webapi.RegenerateStudentKeyActionTest)} instead
	 */
	@Test
	protected void testExecute_regenerateStudentKey() {
		typicalBundle.testExecute_regenerateStudentKey(this);
	}

    @Override
    @Test
    protected void testExecute() {
        // see individual tests
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }
}
