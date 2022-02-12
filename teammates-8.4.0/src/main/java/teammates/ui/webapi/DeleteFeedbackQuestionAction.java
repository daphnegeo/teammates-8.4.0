package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.FeedbackQuestionsVariousAttributes;
import teammates.common.util.Const;

/**
 * Deletes a feedback question.
 */
class DeleteFeedbackQuestionAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestionsVariousAttributes questionAttributes = logic.getFeedbackQuestion(feedbackQuestionId);

        if (questionAttributes == null) {
            throw new UnauthorizedAccessException("Unknown question ID");
        }

        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(questionAttributes.getCourseId(), userInfo.getId()),
                getNonNullFeedbackSession(questionAttributes.getFeedbackSessionName(), questionAttributes.getCourseId()),
                Const.InstructorPermissions.CAN_MODIFY_SESSION);

    }

    @Override
    public JsonResult execute() {
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);

        logic.deleteFeedbackQuestionCascade(feedbackQuestionId);

        return new JsonResult("Feedback question deleted!");
    }

}
