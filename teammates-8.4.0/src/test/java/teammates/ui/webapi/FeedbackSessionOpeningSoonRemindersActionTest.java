package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.common.util.TimeHelperExtension;

/**
 * SUT: {@link FeedbackSessionOpeningSoonRemindersAction}.
 */
public class FeedbackSessionOpeningSoonRemindersActionTest
        extends BaseActionTest<FeedbackSessionOpeningSoonRemindersAction> {

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_OPENING_SOON_REMINDERS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        verifyOnlyAdminCanAccess();
    }

    /**
	 * @param session1
	 * @throws InvalidParametersException
	 * @throws EntityDoesNotExistException
	 */
	private void setendMethod(FeedbackSessionAttributes session1)
			throws InvalidParametersException, EntityDoesNotExistException {
		session1.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(3));
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withSentOpeningSoonEmail(true)
                        .withSentOpenEmail(true)
                        .withStartTime(session1.getStartTime())
                        .withEndTime(session1.getEndTime())
                        .build());

        // allow session to be off the time limit to ensure that sentOpeningEmail is marked false
        session1.setStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(24).plusSeconds(10));
        session1.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(3)); // random date in future
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withStartTime(session1.getStartTime())
                        .withEndTime(session1.getEndTime())
                        .build());
	}

}
