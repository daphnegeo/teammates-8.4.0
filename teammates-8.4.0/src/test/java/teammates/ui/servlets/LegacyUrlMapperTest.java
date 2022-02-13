package teammates.ui.servlets;

import org.apache.http.client.methods.HttpGet;
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
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;
import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletRequest;
import teammates.test.MockHttpServletResponse;

/**
 * SUT: {@link LegacyUrlMapper}.
 */
public class LegacyUrlMapperTest extends BaseTestCase {

    private static final LegacyUrlMapper MAPPER = new LegacyUrlMapper();

    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;

    private void setupMocks(String requestUrl) {
        mockRequest = new MockHttpServletRequest(HttpGet.METHOD_NAME, requestUrl);
        mockResponse = new MockHttpServletResponse();
    }

    @Test
    public void allTests() throws Exception {

        ______TS("Legacy instructor course join URL");

        setupMocks(Const.LegacyURIs.INSTRUCTOR_COURSE_JOIN);
        mockRequest.addParam(Const.ParamsNames.REGKEY, "regkey");

        MAPPER.doGet(mockRequest, mockResponse);

        String newInstructorJoinUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey("regkey")
                .withEntityType(Const.EntityType.INSTRUCTOR)
                .toString();
        assertEquals(newInstructorJoinUrl, mockResponse.getRedirectUrl());

        ______TS("Legacy student course join URL");

        setupMocks(Const.LegacyURIs.STUDENT_COURSE_JOIN_NEW);
        mockRequest.addParam(Const.ParamsNames.REGKEY, "regkey");

        MAPPER.doGet(mockRequest, mockResponse);

        String newStudentJoinUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey("regkey")
                .withEntityType(Const.EntityType.STUDENT)
                .toString();
        assertEquals(newStudentJoinUrl, mockResponse.getRedirectUrl());

        ______TS("Invalid legacy URL: redirect to home page");

        setupMocks("/page/invalidPage");

        MAPPER.doGet(mockRequest, mockResponse);

        assertEquals("/", mockResponse.getRedirectUrl());

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
