package teammates.e2e.pageobjects;

import java.util.List;
import java.util.Set;

import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.storage.entity.FeedbackQuestion;

public class VerifyResponseDetailsParameter {
	public EntityAttributes<FeedbackQuestion> question;
	public List<FeedbackResponseAttributes> givenResponses;
	public List<FeedbackResponseAttributes> otherResponses;
	public Set<String> visibleGivers;
	public Set<String> visibleRecipients;

	public VerifyResponseDetailsParameter(EntityAttributes<FeedbackQuestion> question,
			List<FeedbackResponseAttributes> givenResponses, List<FeedbackResponseAttributes> otherResponses,
			Set<String> visibleGivers, Set<String> visibleRecipients) {
		this.question = question;
		this.givenResponses = givenResponses;
		this.otherResponses = otherResponses;
		this.visibleGivers = visibleGivers;
		this.visibleRecipients = visibleRecipients;
	}
}