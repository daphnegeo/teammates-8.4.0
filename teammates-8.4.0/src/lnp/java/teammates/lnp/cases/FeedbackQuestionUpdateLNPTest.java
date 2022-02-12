package teammates.lnp.cases;

import java.util.ArrayList;

import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.ui.output.NumberOfEntitiesToGiveFeedbackToSetting;
import teammates.ui.request.FeedbackQuestionUpdateRequest;

/**
* L&P Test Case for feedback question update cascade API.
*/
public class FeedbackQuestionUpdateLNPTest extends BaseLNPTestCase {
    private static final int NUM_INSTRUCTORS = 1;
    private static final int RAMP_UP_PERIOD = NUM_INSTRUCTORS * 2;

    private static final int NUMBER_OF_FEEDBACK_QUESTIONS = 10;
    private static final int NUMBER_OF_FEEDBACK_RESPONSES = 500;

    private static final String COURSE_ID = "TestData.CS101";
    private static final String COURSE_NAME = "LnPCourse";
    private static final String COURSE_TIME_ZONE = "UTC";

    private static final String INSTRUCTOR_ID = "LnPInstructor_id";
    private static final String INSTRUCTOR_NAME = "LnPInstructor";
    private static final String INSTRUCTOR_EMAIL = "tmms.test@gmail.tmt";

    private static final String STUDENT_ID = "LnPStudent.tmms";
    private static final String STUDENT_NAME = "LnPStudent";
    private static final String STUDENT_EMAIL = "studentEmail@gmail.tmt";

    private static final String TEAM_NAME = "Team 1";
    private static final String GIVER_SECTION_NAME = "Section 1";
    private static final String RECEIVER_SECTION_NAME = "Section 1";

    private static final String FEEDBACK_SESSION_NAME = "Test Feedback Session";

    private static final String FEEDBACK_QUESTION_ID = "QuestionTest";
    private static final String FEEDBACK_QUESTION_TEXT = "Test Question";

    private static final String UPDATE_FEEDBACK_QUESTION_BRIEF = "update the new question brief";
    private static final String UPDATE_FEEDBACK_QUESTION_TEXT = "update the new question text";

    private static final String FEEDBACK_RESPONSE_ID = "ResponseForQ";

    private static final double ERROR_RATE_LIMIT = 0.01;
    private static final double MEAN_RESP_TIME_LIMIT = 1;

    private FeedbackQuestionUpdateRequest getTypicalTextQuestionUpdateRequest() {
        FeedbackTextQuestionDetails textQuestionDetails = new FeedbackTextQuestionDetails();
        textQuestionDetails.setRecommendedLength(800);

        FeedbackQuestionUpdateRequest updateRequest = new FeedbackQuestionUpdateRequest();
        updateRequest.setQuestionNumber(1);
        updateRequest.setQuestionBrief(UPDATE_FEEDBACK_QUESTION_BRIEF);
        updateRequest.setQuestionDescription(UPDATE_FEEDBACK_QUESTION_TEXT);

        updateRequest.setQuestionDetails(textQuestionDetails);
        updateRequest.setQuestionType(FeedbackQuestionType.TEXT);
        updateRequest.setGiverType(FeedbackParticipantType.STUDENTS);
        updateRequest.setRecipientType(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF);
        updateRequest.setNumberOfEntitiesToGiveFeedbackToSetting(NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED);

        updateRequest.setShowResponsesTo(new ArrayList<>());
        updateRequest.setShowGiverNameTo(new ArrayList<>());
        updateRequest.setShowRecipientNameTo(new ArrayList<>());

        return updateRequest;
    }
}
