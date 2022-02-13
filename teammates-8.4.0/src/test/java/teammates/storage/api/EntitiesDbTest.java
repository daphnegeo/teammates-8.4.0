package teammates.storage.api;

import static teammates.common.util.FieldValidator.COURSE_ID_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.REASON_INCORRECT_FORMAT;

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
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.FieldValidator;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;
import teammates.test.AssertHelper;
import teammates.test.BaseTestCaseWithLocalDatabaseAccess;

/**
 * SUT: {@link teammates.storage.api.EntitiesDb}.
 */
public class EntitiesDbTest extends BaseTestCaseWithLocalDatabaseAccess {

    // We are using CoursesDb to test EntitiesDb here.
    private final CoursesDb coursesDb = CoursesDb.inst();

    @Test
    public void testCreateEntity() throws Exception {
        /*Explanation:
         * The SUT (i.e. EntitiesDb::createEntity) has 4 paths. Therefore, we
         * have 4 test cases here, one for each path.
         */

        ______TS("success: typical case");
        CourseAttributes c = CourseAttributes
                .builder("Computing101-fresh")
                .withName("Basic Computing")
                .withTimezone("UTC")
                .withInstitute("Test institute")
                .build();
        coursesDb.deleteCourse(c.getId());
        verifyAbsentInDatabase(c);
        coursesDb.createEntity(c);
        verifyPresentInDatabase(c);

        ______TS("fails: entity already exists");
        EntityAlreadyExistsException eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> coursesDb.createEntity(c));
        assertEquals(
                String.format(EntitiesDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS, c.toString()), eaee.getMessage());
        coursesDb.deleteCourse(c.getId());

        ______TS("fails: invalid parameters");
        CourseAttributes invalidCourse = CourseAttributes
                .builder("invalid id spaces")
                .withName("Basic Computing")
                .withTimezone("UTC")
                .withInstitute("Test institute")
                .build();
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> coursesDb.createEntity(invalidCourse));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        COURSE_ID_ERROR_MESSAGE, invalidCourse.getId(),
                        FieldValidator.COURSE_ID_FIELD_NAME, REASON_INCORRECT_FORMAT,
                        FieldValidator.COURSE_ID_MAX_LENGTH),
                ipe.getMessage());

        ______TS("fails: null parameter");
        assertThrows(AssertionError.class, () -> coursesDb.createEntity(null));
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
