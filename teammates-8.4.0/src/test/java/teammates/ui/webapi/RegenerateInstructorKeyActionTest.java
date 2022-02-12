package teammates.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;

/**
 * SUT: {@link RegenerateInstructorKeyAction}.
 */
public class RegenerateInstructorKeyActionTest extends BaseActionTest<RegenerateInstructorKeyAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_KEY;
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
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("Invalid parameters");

        //no parameters
        verifyHttpParameterFailure();

        //null instructor email
        String[] invalidParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };
        verifyHttpParameterFailure(invalidParams);

        //null course id
        invalidParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.getEmail(),
        };
        verifyHttpParameterFailure(invalidParams);
    }

    @Test
    protected void testExecute_nonExistentCourse_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("course does not exist");

        String[] nonExistingParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, "non-existent-course",
        };

        assertNull(logic.getCourse("non-existent-course"));

        verifyEntityNotFound(nonExistingParams);
    }

    @Test
    protected void testExecute_nonExistentInstructorInCourse_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("instructor with email address does not exist in course");

        String[] nonExistingParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, "non-existent-instructor@abc.com",
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        assertNull(logic.getInstructorForEmail(instructor1OfCourse1.getCourseId(), "non-existent-instructor@abc.com"));

        verifyEntityNotFound(nonExistingParams);
    }

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.DataBundle#testExecute_regenerateInstructorKey(teammates.ui.webapi.RegenerateInstructorKeyActionTest)} instead
	 */
	@Test
	protected void testExecute_regenerateInstructorKey() {
		typicalBundle.testExecute_regenerateInstructorKey(this);
	}

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }
}
