package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

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
import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletResponse;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link JsonResult}.
 */
public class JsonResultTest extends BaseTestCase {

    @Test
    public void testConstructorAndSendResponse() throws Exception {

        ______TS("json result with output message only");

        JsonResult result = new JsonResult("output message");

        MessageOutput output = (MessageOutput) result.getOutput();
        assertEquals("output message", output.getMessage());
        assertEquals(0, result.getCookies().size());

        MockHttpServletResponse resp = new MockHttpServletResponse();
        result.send(resp);
        assertEquals(0, resp.getCookies().size());

        ______TS("json result with output message and cookies");

        List<Cookie> cookies = new ArrayList<>();
        cookies.add(new Cookie("cookieName", "cookieValue"));
        result = new JsonResult(new MessageOutput("output message"), cookies);

        output = (MessageOutput) result.getOutput();
        assertEquals("output message", output.getMessage());
        assertEquals(1, result.getCookies().size());

        MockHttpServletResponse respWithCookie = new MockHttpServletResponse();
        result.send(respWithCookie);
        assertEquals(1, respWithCookie.getCookies().size());
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
