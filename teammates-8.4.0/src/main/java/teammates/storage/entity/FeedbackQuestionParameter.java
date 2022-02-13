package teammates.storage.entity;

import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackQuestionType;

public class FeedbackQuestionParameter {
	public String feedbackSessionName;
	public String courseId;
	public String questionText;
	public String questionDescription;
	public int questionNumber;
	public FeedbackQuestionType questionType;
	public FeedbackParticipantType giverType;
	public FeedbackParticipantType recipientType;
	public int numberOfEntitiesToGiveFeedbackTo;
	public List<FeedbackParticipantType> showResponsesTo;
	public List<FeedbackParticipantType> showGiverNameTo;
	public List<FeedbackParticipantType> showRecipientNameTo;

	public FeedbackQuestionParameter(String feedbackSessionName, String courseId, String questionText,
			String questionDescription, int questionNumber, FeedbackQuestionType questionType,
			FeedbackParticipantType giverType, FeedbackParticipantType recipientType,
			int numberOfEntitiesToGiveFeedbackTo, List<FeedbackParticipantType> showResponsesTo,
			List<FeedbackParticipantType> showGiverNameTo, List<FeedbackParticipantType> showRecipientNameTo) {
		this.feedbackSessionName = feedbackSessionName;
		this.courseId = courseId;
		this.questionText = questionText;
		this.questionDescription = questionDescription;
		this.questionNumber = questionNumber;
		this.questionType = questionType;
		this.giverType = giverType;
		this.recipientType = recipientType;
		this.numberOfEntitiesToGiveFeedbackTo = numberOfEntitiesToGiveFeedbackTo;
		this.showResponsesTo = showResponsesTo;
		this.showGiverNameTo = showGiverNameTo;
		this.showRecipientNameTo = showRecipientNameTo;
	}
}