package teammates.e2e.cases;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMcqResponseDetails;
import teammates.e2e.pageobjects.AppPage;
import teammates.e2e.pageobjects.FeedbackSubmitPage;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}, {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}
 *      specifically for MCQ questions.
 */
public class FeedbackMcqQuestionE2ETest extends BaseFeedbackQuestionE2ETest {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackMcqQuestionE2ETest.json");
        removeAndRestoreDataBundle(testData);

        instructor = testData.instructors.get("instructor");
        course = testData.courses.get("course");
        feedbackSession = testData.feedbackSessions.get("openSession");
        student = testData.students.get("alice.tmms@FMcqQn.CS2104");
    }

    @Test
    @Override
    public void testAll() {
        testEditPage();
        logout();
        testSubmitPage();
    }

    @Override
    protected void testEditPage() {
        AppPage feedbackEditPage = loginToFeedbackEditPage();

        ______TS("verify loaded question");
        EntityAttributes<FeedbackQuestion> loadedQuestion = testData.feedbackQuestions.get("qn1ForFirstSession").getCopy();
        FeedbackMcqQuestionDetails questionDetails = (FeedbackMcqQuestionDetails) loadedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.verifyMcqQuestionDetails(1, questionDetails);

        ______TS("add new question");
        // add new question exactly like loaded question
        loadedQuestion.setQuestionNumber(2);
        feedbackEditPage.addMcqQuestion(loadedQuestion);

        feedbackEditPage.verifyMcqQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(loadedQuestion);

        ______TS("copy question");
        EntityAttributes<FeedbackQuestion> copiedQuestion = testData.feedbackQuestions.get("qn1ForSecondSession");
        questionDetails = (FeedbackMcqQuestionDetails) copiedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.copyQuestion(copiedQuestion.getCourseId(),
                copiedQuestion.getQuestionDetailsCopy().getQuestionText());
        copiedQuestion.setCourseId(course.getId());
        copiedQuestion.setFeedbackSessionName(feedbackSession.getFeedbackSessionName());
        copiedQuestion.setQuestionNumber(3);

        feedbackEditPage.verifyMcqQuestionDetails(3, questionDetails);
        verifyPresentInDatabase(copiedQuestion);

        ______TS("edit question");
        questionDetails = (FeedbackMcqQuestionDetails) loadedQuestion.getQuestionDetailsCopy();
        questionDetails.setHasAssignedWeights(false);
        questionDetails.setMcqWeights(new ArrayList<>());
        questionDetails.setOtherEnabled(false);
        questionDetails.setMcqOtherWeight(0);
        questionDetails.setNumOfMcqChoices(4);
        List<String> choices = questionDetails.getMcqChoices();
        choices.add("Edited choice");
        questionDetails.setMcqChoices(choices);
        loadedQuestion.setQuestionDetails(questionDetails);
        feedbackEditPage.editMcqQuestion(2, questionDetails);
        feedbackEditPage.waitForPageToLoad();

        feedbackEditPage.verifyMcqQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(loadedQuestion);
    }

    @Override
    protected void testSubmitPage() {
        FeedbackSubmitPage feedbackSubmitPage = loginToFeedbackSubmitPage();

        ______TS("verify loaded question");
        EntityAttributes<FeedbackQuestion> question = testData.feedbackQuestions.get("qn1ForFirstSession");
        feedbackSubmitPage.verifyMcqQuestion(1, "",
                (FeedbackMcqQuestionDetails) question.getQuestionDetailsCopy());

        ______TS("verify question with generated options");
        feedbackSubmitPage.verifyGeneratedMcqQuestion(3, "", getGeneratedStudentOptions());

        ______TS("submit response");
        String questionId = getFeedbackQuestion(question).getId();
        FeedbackResponseAttributes response = getResponse(questionId, false, "UI");
        feedbackSubmitPage.submitMcqResponse(1, "", response);

        verifyPresentInDatabase(response);

        ______TS("check previous response");
        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyMcqResponse(1, "", response);

        ______TS("edit response");
        response = getResponse(questionId, true, "This is the edited response.");
        feedbackSubmitPage.submitMcqResponse(1, "", response);

        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyMcqResponse(1, "", response);
        verifyPresentInDatabase(response);
    }

    private List<String> getGeneratedStudentOptions() {
        return testData.students.values().stream()
                .filter(s -> s.getCourse().equals(student.getCourse()))
                .map(s -> s.getName() + " (" + s.getTeam() + ")")
                .collect(Collectors.toList());
    }

    private FeedbackResponseAttributes getResponse(String questionId, boolean isOther, String answer) {
        FeedbackMcqResponseDetails details = new FeedbackMcqResponseDetails();
        if (isOther) {
            details.setOther(true);
            details.setOtherFieldContent(answer);
        } else {
            details.setAnswer(answer);
        }
        return FeedbackResponseAttributes.builder(questionId, student.getEmail(), "%GENERAL%")
                .withResponseDetails(details)
                .build();
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
