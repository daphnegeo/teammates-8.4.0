package teammates.logic.core;

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
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;

/**
 * SUT: {@link ProfilesLogic}.
 */
public class ProfilesLogicTest extends BaseLogicTest {

    private final ProfilesLogic profilesLogic = ProfilesLogic.inst();

    @Override
    protected void prepareTestData() {
        // no test data used for this test
    }

    @Test
    public void testStudentProfileFunctions() throws Exception {

        // 4 functions are tested together as:
        //      => The functions are very simple (one-liners)
        //      => They are fundamentally related and easily tested together
        //      => It saves time during tests

        ______TS("get SP");
        StudentProfileAttributes expectedSpa = StudentProfileAttributes.builder("id")
                .withShortName("shortName")
                .withEmail("personal@email.com")
                .withInstitute("institute")
                .withNationality("American")
                .withGender(StudentProfileAttributes.Gender.FEMALE)
                .withMoreInfo("moreInfo")
                .build();

        StudentProfileAttributes updateSpa = profilesLogic.updateOrCreateStudentProfile(
                StudentProfileAttributes.updateOptionsBuilder(expectedSpa.getGoogleId())
                        .withShortName(expectedSpa.getShortName())
                        .withEmail(expectedSpa.getEmail())
                        .withInstitute(expectedSpa.getInstitute())
                        .withNationality(expectedSpa.getNationality())
                        .withGender(expectedSpa.getGender())
                        .withMoreInfo(expectedSpa.getMoreInfo())
                        .build());

        StudentProfileAttributes actualSpa = profilesLogic.getStudentProfile(expectedSpa.getGoogleId());
        expectedSpa.setModifiedDate(actualSpa.getModifiedDate());
        assertEquals(expectedSpa.toString(), actualSpa.toString());
        assertEquals(expectedSpa.toString(), updateSpa.toString());
    }

    @Test
    public void testDeleteStudentProfile() throws Exception {
        // more tests in ProfilesDbTest

        profilesLogic.updateOrCreateStudentProfile(
                StudentProfileAttributes.updateOptionsBuilder("sp.logic.test")
                        .withShortName("Test Name")
                        .build());
        StudentProfileAttributes savedProfile = profilesLogic.getStudentProfile("sp.logic.test");
        assertNotNull(savedProfile);

        profilesLogic.deleteStudentProfile("sp.logic.test");
        // check that profile get deleted and picture get deleted
        verifyAbsentInDatabase(savedProfile);
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
