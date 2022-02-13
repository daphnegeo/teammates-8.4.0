package teammates.common.datatransfer.attributes;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link StudentProfileAttributes.Gender}.
 */
public class GenderTest extends BaseTestCase {

    @Test
    public void testGetGenderEnumValue() {
        // invalid values
        assertEquals(StudentProfileAttributes.Gender.OTHER,
                StudentProfileAttributes.Gender.getGenderEnumValue(null));
        assertEquals(StudentProfileAttributes.Gender.OTHER,
                StudentProfileAttributes.Gender.getGenderEnumValue("'\"'invalidGender"));
        assertEquals(StudentProfileAttributes.Gender.OTHER,
                StudentProfileAttributes.Gender.getGenderEnumValue("invalidGender"));

        // valid values
        assertEquals(StudentProfileAttributes.Gender.MALE,
                StudentProfileAttributes.Gender.getGenderEnumValue("MALE"));
        assertEquals(StudentProfileAttributes.Gender.FEMALE,
                StudentProfileAttributes.Gender.getGenderEnumValue("female"));
        assertEquals(StudentProfileAttributes.Gender.OTHER,
                StudentProfileAttributes.Gender.getGenderEnumValue("oTheR"));
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
