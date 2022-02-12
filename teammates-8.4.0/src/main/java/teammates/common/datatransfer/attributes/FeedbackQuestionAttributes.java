package teammates.common.datatransfer.attributes;

import java.util.ArrayList;

import teammates.storage.entity.FeedbackQuestion;

/**
 * The data transfer object for {@link FeedbackQuestion} entities.
 */
public class FeedbackQuestionAttributes extends FeedbackQuestionsVariousAttributes
        implements Comparable<FeedbackQuestionAttributes> {

    private FeedbackQuestionAttributes() {
        super();
		this.showResponsesTo = new ArrayList<>();
        this.showGiverNameTo = new ArrayList<>();
        this.showRecipientNameTo = new ArrayList<>();
    }
}
