package teammates.common.datatransfer.questions;

import teammates.common.util.Const;

/**
 * Contains common abstractions between rank options and rank recipients questions.
 */
public abstract class FeedbackRankQuestionDetails extends FeedbackQuestionDetails {

    FeedbackRankQuestionDetails(FeedbackQuestionType questionType, String questionText) {
        super(questionType, questionText);
        minOptionsToBeRanked = Const.POINTS_NO_VALUE;
        maxOptionsToBeRanked = Const.POINTS_NO_VALUE;
    }
}
