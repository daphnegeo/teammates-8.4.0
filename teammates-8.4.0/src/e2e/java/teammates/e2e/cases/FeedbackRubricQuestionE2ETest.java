package teammates.e2e.cases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.e2e.pageobjects.AppPage;
import teammates.e2e.pageobjects.FeedbackSubmitPage;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}, {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}
 *      specifically for Rubric questions.
 */
public class FeedbackRubricQuestionE2ETest extends BaseFeedbackQuestionE2ETest {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackRubricQuestionE2ETest.json");
        removeAndRestoreDataBundle(testData);

        instructor = testData.instructors.get("instructor");
        course = testData.courses.get("course");
        feedbackSession = testData.feedbackSessions.get("openSession");
        student = testData.students.get("alice.tmms@FRubricQn.CS2104");
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
        FeedbackRubricQuestionDetails questionDetails =
                (FeedbackRubricQuestionDetails) loadedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.verifyRubricQuestionDetails(1, questionDetails);

        ______TS("add new question");
        // add new question exactly like loaded question
        loadedQuestion.setQuestionNumber(2);
        feedbackEditPage.addRubricQuestion(loadedQuestion);

        feedbackEditPage.verifyRubricQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(loadedQuestion);

        ______TS("copy question");
        EntityAttributes<FeedbackQuestion> copiedQuestion = testData.feedbackQuestions.get("qn1ForSecondSession");
        questionDetails = (FeedbackRubricQuestionDetails) copiedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.copyQuestion(copiedQuestion.getCourseId(),
                copiedQuestion.getQuestionDetailsCopy().getQuestionText());
        copiedQuestion.setCourseId(course.getId());
        copiedQuestion.setFeedbackSessionName(feedbackSession.getFeedbackSessionName());
        copiedQuestion.setQuestionNumber(3);

        feedbackEditPage.verifyRubricQuestionDetails(3, questionDetails);
        verifyPresentInDatabase(copiedQuestion);

        ______TS("edit question");
        // add a new choice
        questionDetails = (FeedbackRubricQuestionDetails) loadedQuestion.getQuestionDetailsCopy();
        List<String> choices = questionDetails.getRubricChoices();
        choices.add("Edited choice.");
        List<List<String>> descriptions = questionDetails.getRubricDescriptions();
        descriptions.get(0).add("Edit description.");
        descriptions.get(1).add("Edit description 1.");
        questionDetails.setNumOfRubricChoices(3);
        // edit existing descriptions
        descriptions.get(0).set(1, "Edit description 2");
        descriptions.get(1).set(0, "");
        // edit existing subquestion
        List<String> subQns = questionDetails.getRubricSubQuestions();
        subQns.set(0, "Edited subquestion.");
        // add a new subquestion
        subQns.add("Added subquestion.");
        questionDetails.setNumOfRubricSubQuestions(3);
        descriptions.add(Arrays.asList("", "test", ""));
        // remove assigned weights
        questionDetails.setHasAssignedWeights(false);
        questionDetails.setRubricWeightsForEachCell(new ArrayList<>());
        loadedQuestion.setQuestionDetails(questionDetails);
        feedbackEditPage.editRubricQuestion(2, questionDetails);
        feedbackEditPage.waitForPageToLoad();

        feedbackEditPage.verifyRubricQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(loadedQuestion);
    }

    @Override
    protected void testSubmitPage() {
        FeedbackSubmitPage feedbackSubmitPage = loginToFeedbackSubmitPage();

        ______TS("verify loaded question");
        EntityAttributes<FeedbackQuestion> question = testData.feedbackQuestions.get("qn1ForFirstSession");
        StudentAttributes receiver = testData.students.get("benny.tmms@FRubricQn.CS2104");
        feedbackSubmitPage.verifyRubricQuestion(1, receiver.getName(),
                (FeedbackRubricQuestionDetails) question.getQuestionDetailsCopy());

        ______TS("submit response");
        String questionId = getFeedbackQuestion(question).getId();
        FeedbackResponseAttributes response = getResponse(questionId, receiver, Arrays.asList(1, 1));
        feedbackSubmitPage.submitRubricResponse(1, receiver.getName(), response);

        verifyPresentInDatabase(response);

        ______TS("check previous response");
        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyRubricResponse(1, receiver.getName(), response);

        ______TS("edit response");
        response = getResponse(questionId, receiver, Arrays.asList(0, 0));
        feedbackSubmitPage.submitRubricResponse(1, receiver.getName(), response);

        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyRubricResponse(1, receiver.getName(), response);
        verifyPresentInDatabase(response);
    }

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.attributes.StudentAttributes#getResponse(String,teammates.e2e.cases.FeedbackRubricQuestionE2ETest,List<Integer>)} instead
	 */
	private FeedbackResponseAttributes getResponse(String questionId, StudentAttributes receiver, List<Integer> answers) {
		return receiver.getResponse(questionId, this, answers);
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
