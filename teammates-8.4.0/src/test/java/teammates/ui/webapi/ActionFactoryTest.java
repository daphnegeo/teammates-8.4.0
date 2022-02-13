package teammates.ui.webapi;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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

/**
 * SUT: {@link ActionFactory}.
 */
public class ActionFactoryTest extends BaseTestCase {

    @Test
    public void testGetAction() throws Exception {
        ______TS("Action exists and is retrieved");

        MockHttpServletRequest existingActionServletRequest = new MockHttpServletRequest(
                HttpGet.METHOD_NAME, Const.ResourceURIs.AUTH);
        existingActionServletRequest.addHeader(Const.HeaderNames.BACKDOOR_KEY, Config.BACKDOOR_KEY);
        Action existingAction = ActionFactory.getAction(existingActionServletRequest, HttpGet.METHOD_NAME);
        assertTrue(existingAction instanceof GetAuthInfoAction);

        ______TS("Action does not exist and ActionMappingException is thrown");

        MockHttpServletRequest nonExistentActionServletRequest = new MockHttpServletRequest(
                HttpGet.METHOD_NAME, "/blahblahblah");
        nonExistentActionServletRequest.addHeader(Const.HeaderNames.BACKDOOR_KEY, Config.BACKDOOR_KEY);
        ActionMappingException nonExistentActionException = assertThrows(ActionMappingException.class,
                () -> ActionFactory.getAction(nonExistentActionServletRequest, HttpGet.METHOD_NAME));
        assertTrue(nonExistentActionException.getMessage()
                .equals("Resource with URI /blahblahblah is not found."));

        ______TS("Method does not exist on action and ActionMappingException is thrown");

        MockHttpServletRequest nonExistentMethodOnActionServletRequest = new MockHttpServletRequest(
                HttpGet.METHOD_NAME, Const.ResourceURIs.AUTH);
        nonExistentMethodOnActionServletRequest.addHeader(Const.HeaderNames.BACKDOOR_KEY, Config.BACKDOOR_KEY);
        ActionMappingException nonExistentMethodOnActionException = assertThrows(ActionMappingException.class,
                () -> ActionFactory.getAction(nonExistentMethodOnActionServletRequest, HttpPost.METHOD_NAME));
        assertTrue(nonExistentMethodOnActionException.getMessage()
                .equals("Method [" + HttpPost.METHOD_NAME + "] is not allowed for URI "
                + Const.ResourceURIs.AUTH + "."));
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
