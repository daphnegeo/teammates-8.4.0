package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.e2e.cases.InstructorFeedbackReportPageE2ETest;
import teammates.storage.entity.FeedbackResponse;

/**
 * The data transfer object for {@link FeedbackResponse} entities.
 */
public class FeedbackResponseAttributes extends EntityAttributes<FeedbackResponse> {

    private String feedbackQuestionId;
    /**
     * Depending on the question giver type, {@code giver} may contain the giver's email, the team name,
     * "anonymous", etc.
     */
    private String giver;
    /**
     * Depending on the question recipient type, {@code recipient} may contain the recipient's email, the team
     * name, "%GENERAL%", etc.
     */
    private String recipient;
    private String feedbackSessionName;
    private String courseId;
    private FeedbackResponseDetails responseDetails;
    private String giverSection;
    private String recipientSection;
    private transient Instant createdAt;
    private transient Instant updatedAt;
    private transient String feedbackResponseId;

    private FeedbackResponseAttributes(String feedbackQuestionId, String giver, String recipient) {
        this.feedbackQuestionId = feedbackQuestionId;
        this.giver = giver;
        this.recipient = recipient;

        this.giverSection = Const.DEFAULT_SECTION;
        this.recipientSection = Const.DEFAULT_SECTION;
        this.feedbackResponseId = FeedbackResponse.generateId(feedbackQuestionId, giver, recipient);
    }

    public FeedbackResponseAttributes(FeedbackResponseAttributes copy) {
        this.feedbackResponseId = copy.getId();
        this.feedbackSessionName = copy.feedbackSessionName;
        this.courseId = copy.courseId;
        this.feedbackQuestionId = copy.feedbackQuestionId;
        this.giver = copy.giver;
        this.giverSection = copy.giverSection;
        this.recipient = copy.recipient;
        this.recipientSection = copy.recipientSection;
        this.createdAt = copy.createdAt;
        this.updatedAt = copy.updatedAt;
        this.responseDetails = copy.getResponseDetailsCopy();
    }

    /**
     * Gets the {@link FeedbackResponseAttributes} instance of the given {@link FeedbackResponse}.
     */
    public static FeedbackResponseAttributes valueOf(FeedbackResponse fr) {
        FeedbackResponseAttributes fra =
                new FeedbackResponseAttributes(
                        fr.getFeedbackQuestionId(), fr.getGiverEmail(), fr.getRecipientEmail());

        fra.feedbackResponseId = fr.getId();
        fra.feedbackSessionName = fr.getFeedbackSessionName();
        fra.courseId = fr.getCourseId();
        if (fr.getGiverSection() != null) {
            fra.giverSection = fr.getGiverSection();
        }
        if (fr.getRecipientSection() != null) {
            fra.recipientSection = fr.getRecipientSection();
        }
        fra.responseDetails = deserializeResponseFromSerializedString(fr.getAnswer(), fr.getFeedbackQuestionType());
        fra.createdAt = fr.getCreatedAt();
        fra.updatedAt = fr.getUpdatedAt();

        return fra;
    }

    public FeedbackQuestionType getFeedbackQuestionType() {
        return responseDetails.getQuestionType();
    }

    public void setFeedbackQuestionId(String feedbackQuestionId) {
        this.feedbackQuestionId = feedbackQuestionId;
    }

    public String getGiver() {
        return giver;
    }

    public void setGiver(String giver) {
        this.giver = giver;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getGiverSection() {
        return giverSection;
    }

    public String getRecipientSection() {
        return recipientSection;
    }

    public FeedbackResponseDetails getResponseDetails() {
        return responseDetails;
    }

    public void setResponseDetails(FeedbackResponseDetails newFeedbackResponseDetails) {
        responseDetails = newFeedbackResponseDetails.getDeepCopy();
    }

    public String getSerializedFeedbackResponseDetail() {
        return responseDetails.getJsonString();
    }

    public FeedbackResponseDetails getResponseDetailsCopy() {
        return responseDetails.getDeepCopy();
    }

    private static FeedbackResponseDetails deserializeResponseFromSerializedString(
            String serializedResponseDetails, FeedbackQuestionType questionType) {
        if (questionType == FeedbackQuestionType.TEXT) {
            // For Text questions, the answer simply contains the response text, not a JSON
            return new FeedbackTextResponseDetails(serializedResponseDetails);
        }
        return JsonUtils.fromJson(serializedResponseDetails, questionType.getResponseDetailsClass());
    }

    /**
     * Returns a builder for {@link FeedbackResponseAttributes}.
     */
    public static Builder builder(String feedbackQuestionId, String giver, String recipient) {
        return new Builder(feedbackQuestionId, giver, recipient);
    }

    public List<FeedbackQuestionAttributes> getQuestionsByCourse(InstructorFeedbackReportPageE2ETest instructorFeedbackReportPageE2ETest, String courseId) {
	    return instructorFeedbackReportPageE2ETest.testData.feedbackQuestions.values().stream()
	            .filter(question -> question.getCourseId().equals(courseId))
	            .collect(Collectors.toList());
	}

	public List<StudentAttributes> getNotRespondedStudents(InstructorFeedbackReportPageE2ETest instructorFeedbackReportPageE2ETest, String courseId) {
	    Set<String> responders = instructorFeedbackReportPageE2ETest.testData.feedbackResponses.values().stream()
	            .filter(response -> response.getCourseId().equals(courseId))
	            .map(FeedbackResponseAttributes::getGiver)
	            .collect(Collectors.toSet());
	
	    return instructorFeedbackReportPageE2ETest.testData.students.values().stream()
	            .filter(student -> !responders.contains(student.getEmail()) && student.getCourse().equals(courseId))
	            .collect(Collectors.toList());
	}

	public String getTeamName(FeedbackParticipantType type, String participant, Collection<StudentAttributes> students) {
	    if (type.equals(FeedbackParticipantType.NONE)) {
	        return "No Specific Team";
	    } else if (type.equals(FeedbackParticipantType.TEAMS)) {
	        return participant;
	    } else if (type.equals(FeedbackParticipantType.INSTRUCTORS)) {
	        return "Instructors";
	    }
	    String teamName = students.stream()
	            .filter(student -> student.getEmail().equals(participant))
	            .findFirst()
	            .map(StudentAttributes::getTeam)
	            .orElse(null);
	
	    if (teamName == null) {
	        throw new RuntimeException("cannot find section name");
	    }
	
	    return teamName;
	}

	/**
     * A builder for {@link FeedbackResponseCommentAttributes}.
     */
    public static class Builder extends BasicBuilder<FeedbackResponseAttributes, Builder> {

        private FeedbackResponseAttributes fra;

        private Builder(String feedbackQuestionId, String giver, String recipient) {
            super(new UpdateOptions(""));
            thisBuilder = this;

            assert feedbackQuestionId != null;
            assert giver != null;
            assert recipient != null;
            fra = new FeedbackResponseAttributes(feedbackQuestionId, giver, recipient);
        }

        public Builder withCourseId(String courseId) {
            assert courseId != null;
            fra.courseId = courseId;

            return this;
        }

        public Builder withFeedbackSessionName(String feedbackSessionName) {
            assert feedbackSessionName != null;
            fra.feedbackSessionName = feedbackSessionName;

            return this;
        }

        @Override
        public FeedbackResponseAttributes build() {
            fra.update(updateOptions);

            return fra;
        }
    }

    /**
     * Helper class to specific the fields to update in {@link FeedbackResponseAttributes}.
     */
    public static class UpdateOptions {
        private String feedbackResponseId;

        private UpdateOption<String> giverOption = UpdateOption.empty();
        private UpdateOption<String> giverSectionOption = UpdateOption.empty();
        private UpdateOption<String> recipientOption = UpdateOption.empty();
        private UpdateOption<String> recipientSectionOption = UpdateOption.empty();
        private UpdateOption<FeedbackResponseDetails> responseDetailsUpdateOption = UpdateOption.empty();

        private UpdateOptions(String feedbackResponseId) {
            assert feedbackResponseId != null;

            this.feedbackResponseId = feedbackResponseId;
        }

        public String getFeedbackResponseId() {
            return feedbackResponseId;
        }

        @Override
        public String toString() {
            return "FeedbackResponseAttributes.UpdateOptions ["
                    + "feedbackResponseId = " + feedbackResponseId
                    + ", giver = " + giverOption
                    + ", giverSection = " + giverSectionOption
                    + ", recipient = " + recipientOption
                    + ", recipientSection = " + recipientSectionOption
                    + ", responseDetails = " + JsonUtils.toJson(responseDetailsUpdateOption)
                    + "]";
        }

        /**
         * Builder class to build {@link UpdateOptions}.
         */
        public static class Builder extends BasicBuilder<UpdateOptions, Builder> {

            private Builder(String feedbackResponseId) {
                super(new UpdateOptions(feedbackResponseId));
                thisBuilder = this;
            }

            public Builder withGiver(String giver) {
                assert giver != null;

                updateOptions.giverOption = UpdateOption.of(giver);
                return thisBuilder;
            }

            public Builder withRecipient(String recipient) {
                assert recipient != null;

                updateOptions.recipientOption = UpdateOption.of(recipient);
                return thisBuilder;
            }

            @Override
            public UpdateOptions build() {
                return updateOptions;
            }

        }

    }

    /**
     * Basic builder to build {@link FeedbackResponseAttributes} related classes.
     *
     * @param <T> type to be built
     * @param <B> type of the builder
     */
    abstract static class BasicBuilder<T, B extends BasicBuilder<T, B>> {

        UpdateOptions updateOptions;
        B thisBuilder;

        BasicBuilder(UpdateOptions updateOptions) {
            this.updateOptions = updateOptions;
        }

        public B withGiverSection(String giverSection) {
            assert giverSection != null;

            updateOptions.giverSectionOption = UpdateOption.of(giverSection);
            return thisBuilder;
        }

        public B withRecipientSection(String recipientSection) {
            assert recipientSection != null;

            updateOptions.recipientSectionOption = UpdateOption.of(recipientSection);
            return thisBuilder;
        }

        public B withResponseDetails(FeedbackResponseDetails responseDetails) {
            assert responseDetails != null;

            updateOptions.responseDetailsUpdateOption = UpdateOption.of(responseDetails.getDeepCopy());
            return thisBuilder;
        }

        public abstract T build();

    }

}
