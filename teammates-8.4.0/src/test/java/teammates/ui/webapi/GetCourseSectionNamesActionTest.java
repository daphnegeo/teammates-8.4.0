package teammates.ui.webapi;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;
import teammates.ui.output.CourseSectionNamesData;

/**
 * SUT: {@link GetCourseSectionNamesAction}.
 */
public class GetCourseSectionNamesActionTest extends BaseActionTest<GetCourseSectionNamesAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE_SECTIONS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    protected void testExecute_typicalUsage_shouldPass() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        List<String> expectedSectionNames = logic.getSectionNamesForCourse(instructor1OfCourse1.getCourseId());

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("typical success case for instructor");

        String[] params = {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };
        GetCourseSectionNamesAction getCourseSectionNamesAction = getAction(params);
        JsonResult response = getJsonResult(getCourseSectionNamesAction);

        CourseSectionNamesData courseSectionNamesData = (CourseSectionNamesData) response.getOutput();
        assertEquals(expectedSectionNames, courseSectionNamesData.getSectionNames());
    }

    @Test
    protected void testExecute_nonExistCourse_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("failed case for non-existent course");

        String[] params = {
                Const.ParamsNames.COURSE_ID, "dummy-course",
        };

        verifyEntityNotFound(params);
    }

    @Test
    @Override
    protected void testAccessControl() {
        // See test cases below
    }

    @Test
    protected void testAccessControl_testInvalidAccess_shouldPass() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] instructorLoginParams = new String[] {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        ______TS("Without login, cannot access");

        verifyInaccessibleWithoutLogin(instructorLoginParams);

        String[] instructorParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
        };

        ______TS("Without registration, cannot access");

        verifyInaccessibleForUnregisteredUsers(instructorParams);

        ______TS("Login as instructor, then can access");
        loginAsInstructor(instructor.getGoogleId());
        verifyCanAccess(instructorParams);
    }

    @Test
    protected void testAccessControl_testInstructorAccess_shouldPass() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

	@Override
	protected EntityAttributes<Account> getAccount(EntityAttributes<Account> account) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected StudentProfileAttributes getStudentProfile(StudentProfileAttributes studentProfileAttributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CourseAttributes getCourse(CourseAttributes course) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected EntityAttributes<FeedbackQuestion> getFeedbackQuestion(EntityAttributes<FeedbackQuestion> fq) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected FeedbackResponseCommentAttributes getFeedbackResponseComment(FeedbackResponseCommentAttributes frc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected FeedbackResponseAttributes getFeedbackResponse(FeedbackResponseAttributes fr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected FeedbackSessionAttributes getFeedbackSession(FeedbackSessionAttributes fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected InstructorAttributes getInstructor(InstructorAttributes instructor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected StudentAttributes getStudent(StudentAttributes student) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean doRemoveAndRestoreDataBundle(DataBundle testData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean doPutDocuments(DataBundle testData) {
		// TODO Auto-generated method stub
		return false;
	}
}
