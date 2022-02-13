package teammates.ui.webapi;

import org.apache.http.HttpStatus;
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
import teammates.test.TestProperties;
import teammates.ui.output.InstructorsData;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link SearchInstructorsAction}.
 */
public class SearchInstructorsActionTest extends BaseActionTest<SearchInstructorsAction> {

    private final InstructorAttributes acc = typicalBundle.instructors.get("instructor1OfCourse1");

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SEARCH_INSTRUCTORS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    protected void testExecute_notEnoughParameters_shouldFail() {
        loginAsAdmin();
        verifyHttpParameterFailure();
    }

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.attributes.InstructorAttributes#testExecute_searchCourseId_shouldSucceed(teammates.ui.webapi.SearchInstructorsActionTest)} instead
	 */
	@Test
	protected void testExecute_searchCourseId_shouldSucceed() {
		acc.testExecute_searchCourseId_shouldSucceed(this);
	}

    @Test
    protected void testExecute_searchDisplayedName_shouldSucceed() {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        loginAsAdmin();
        String[] submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, acc.getDisplayedName() };
        SearchInstructorsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        InstructorsData response = (InstructorsData) result.getOutput();
        assertTrue(response.getInstructors().stream()
                .filter(i -> i.getName().equals(acc.getName()))
                .findAny()
                .isPresent());
    }

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.attributes.InstructorAttributes#testExecute_searchEmail_shouldSucceed(teammates.ui.webapi.SearchInstructorsActionTest)} instead
	 */
	@Test
	protected void testExecute_searchEmail_shouldSucceed() {
		acc.testExecute_searchEmail_shouldSucceed(this);
	}

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.attributes.InstructorAttributes#testExecute_searchGoogleId_shouldSucceed(teammates.ui.webapi.SearchInstructorsActionTest)} instead
	 */
	@Test
	protected void testExecute_searchGoogleId_shouldSucceed() {
		acc.testExecute_searchGoogleId_shouldSucceed(this);
	}

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.attributes.InstructorAttributes#testExecute_searchName_shouldSucceed(teammates.ui.webapi.SearchInstructorsActionTest)} instead
	 */
	@Test
	protected void testExecute_searchName_shouldSucceed() {
		acc.testExecute_searchName_shouldSucceed(this);
	}

    @Test
    protected void testExecute_searchNoMatch_shouldBeEmpty() {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        loginAsAdmin();
        String[] submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, "noMatch" };
        SearchInstructorsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        InstructorsData response = (InstructorsData) result.getOutput();
        assertEquals(0, response.getInstructors().size());
    }

    @Test
    public void testExecute_noSearchService_shouldReturn501() {
        if (TestProperties.isSearchServiceActive()) {
            return;
        }

        loginAsAdmin();
        String[] params = new String[] {
                Const.ParamsNames.SEARCH_KEY, "anything",
        };
        SearchInstructorsAction a = getAction(params);
        JsonResult result = getJsonResult(a, HttpStatus.SC_NOT_IMPLEMENTED);
        MessageOutput output = (MessageOutput) result.getOutput();

        assertEquals("Full-text search is not available.", output.getMessage());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
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
