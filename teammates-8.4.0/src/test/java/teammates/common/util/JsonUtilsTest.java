package teammates.common.util;

import java.util.ArrayList;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link JsonUtils}.
 */
public class JsonUtilsTest extends BaseTestCase {

    @Test
    public void testFeedbackQuestionDetailsAdaptor_withComposedQuestionDetails_shouldSerializeToConcreteClass() {
        FeedbackTextQuestionDetails questionDetails = new FeedbackTextQuestionDetails("Question text.");

        ArrayList<FeedbackParticipantType> participants = new ArrayList<>();
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER);

        EntityAttributes<FeedbackQuestion> fqa = FeedbackQuestionAttributes.builder()
                .withCourseId("testingCourse")
                .withFeedbackSessionName("testFeedbackSession")
                .withGiverType(FeedbackParticipantType.INSTRUCTORS)
                .withRecipientType(FeedbackParticipantType.SELF)
                .withNumberOfEntitiesToGiveFeedbackTo(Const.MAX_POSSIBLE_RECIPIENTS)
                .withQuestionNumber(1)
                .withQuestionDetails(questionDetails)
                .withShowGiverNameTo(participants)
                .withShowRecipientNameTo(participants)
                .withShowResponsesTo(participants)
                .build();

        assertEquals("{\n"
                + "  \"feedbackSessionName\": \"testFeedbackSession\",\n"
                + "  \"courseId\": \"testingCourse\",\n"
                + "  \"questionDetails\": {\n"
                + "    \"shouldAllowRichText\": true,\n"
                + "    \"questionType\": \"TEXT\",\n"
                + "    \"questionText\": \"Question text.\"\n"
                + "  },\n"
                + "  \"questionNumber\": 1,\n"
                + "  \"giverType\": \"INSTRUCTORS\",\n"
                + "  \"recipientType\": \"SELF\",\n"
                + "  \"numberOfEntitiesToGiveFeedbackTo\": -100,\n"
                + "  \"showResponsesTo\": [\n"
                + "    \"RECEIVER\"\n"
                + "  ],\n"
                + "  \"showGiverNameTo\": [\n"
                + "    \"RECEIVER\"\n"
                + "  ],\n"
                + "  \"showRecipientNameTo\": [\n"
                + "    \"RECEIVER\"\n"
                + "  ]\n"
                + "}", JsonUtils.toJson(fqa));

        assertEquals("{\"feedbackSessionName\":\"testFeedbackSession\","
                + "\"courseId\":\"testingCourse\",\"questionDetails\":{\"shouldAllowRichText\":true,\"questionType\":"
                + "\"TEXT\","
                + "\"questionText\":\"Question text.\"},\"questionNumber\":1,"
                + "\"giverType\":\"INSTRUCTORS\",\"recipientType\":\"SELF\",\"numberOfEntitiesToGiveFeedbackTo\":-100,"
                + "\"showResponsesTo\":[\"RECEIVER\"],\"showGiverNameTo\":[\"RECEIVER\"],"
                + "\"showRecipientNameTo\":[\"RECEIVER\"]}",
                JsonUtils.toCompactJson(fqa));
    }

    @Test
    public void testFeedbackResponseDetailsAdaptor_withComposedResponseDetails_shouldSerializeToConcreteClass() {
        FeedbackResponseAttributes fra =
                FeedbackResponseAttributes.builder(
                        "questionId", "giver@email.com", "recipient@email.com")
                .withFeedbackSessionName("Session1")
                .withCourseId("CS3281")
                .withGiverSection("giverSection")
                .withRecipientSection("recipientSection")
                .withResponseDetails(new FeedbackTextResponseDetails("My answer"))
                .build();

        assertEquals("{\n"
                + "  \"feedbackQuestionId\": \"questionId\",\n"
                + "  \"giver\": \"giver@email.com\",\n"
                + "  \"recipient\": \"recipient@email.com\",\n"
                + "  \"feedbackSessionName\": \"Session1\",\n"
                + "  \"courseId\": \"CS3281\",\n"
                + "  \"responseDetails\": {\n"
                + "    \"answer\": \"My answer\",\n"
                + "    \"questionType\": \"TEXT\"\n"
                + "  },\n"
                + "  \"giverSection\": \"giverSection\",\n"
                + "  \"recipientSection\": \"recipientSection\"\n"
                + "}", JsonUtils.toJson(fra));

        assertEquals("{\"feedbackQuestionId\":\"questionId\",\"giver\":\"giver@email.com\","
                + "\"recipient\":\"recipient@email.com\",\"feedbackSessionName\":\"Session1\","
                + "\"courseId\":\"CS3281\",\"responseDetails\":{\"answer\":\"My answer\","
                + "\"questionType\":\"TEXT\"},\"giverSection\":\"giverSection\","
                + "\"recipientSection\":\"recipientSection\"}",
                JsonUtils.toCompactJson(fra));
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
