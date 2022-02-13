package teammates.e2e.cases;

import java.time.Instant;
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
import teammates.common.datatransfer.questions.FeedbackMcqResponseDetails;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.util.TestProperties;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;

/**
 * SUT: {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}.
 */
public class FeedbackSubmitPageE2ETest extends BaseE2ETestCase {
    public StudentAttributes student;
    public InstructorAttributes instructor;

    public FeedbackSessionAttributes openSession;
    private FeedbackSessionAttributes closedSession;
    public FeedbackSessionAttributes gracePeriodSession;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackSubmitPageE2ETest.json");
        testData.feedbackSessions.get("Grace Period Session").setEndTime(Instant.now());
        student = testData.students.get("Alice");
        student.setEmail(TestProperties.TEST_EMAIL);
        removeAndRestoreDataBundle(testData);

        instructor = testData.instructors.get("FSubmit.instr");
        openSession = testData.feedbackSessions.get("Open Session");
        closedSession = testData.feedbackSessions.get("Closed Session");
        gracePeriodSession = testData.feedbackSessions.get("Grace Period Session");
    }

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.attributes.FeedbackSessionAttributes#testAll(teammates.e2e.cases.FeedbackSubmitPageE2ETest)} instead
	 */
	@Test
	@Override
	public void testAll() {
		closedSession.testAll(this);
	}

    private AppUrl getStudentSubmitPageUrl(StudentAttributes student, FeedbackSessionAttributes session) {
        return createUrl(Const.WebPageURIs.STUDENT_SESSION_SUBMISSION_PAGE)
                .withCourseId(student.getCourse())
                .withSessionName(session.getFeedbackSessionName());
    }

    private List<String> getOtherStudents(StudentAttributes currentStudent) {
        return testData.students.values().stream()
                .filter(s -> !s.equals(currentStudent))
                .map(s -> s.getName())
                .collect(Collectors.toList());
    }

    private List<String> getInstructors() {
        return testData.instructors.values().stream()
                .map(i -> i.getName())
                .collect(Collectors.toList());
    }

    private List<String> getTeammates(StudentAttributes currentStudent) {
        return testData.students.values().stream()
                .filter(s -> !s.equals(currentStudent) && s.getTeam().equals(currentStudent.getTeam()))
                .map(s -> s.getName())
                .collect(Collectors.toList());
    }

    private List<String> getOtherTeams(StudentAttributes currentStudent) {
        return new ArrayList<>(testData.students.values().stream()
                .filter(s -> !s.getTeam().equals(currentStudent.getTeam()))
                .map(s -> s.getTeam())
                .collect(Collectors.toSet()));
    }

    private FeedbackResponseAttributes getMcqResponse(String questionId, String recipient, boolean isOther, String answer) {
        FeedbackMcqResponseDetails details = new FeedbackMcqResponseDetails();
        if (isOther) {
            details.setOther(true);
            details.setOtherFieldContent(answer);
        } else {
            details.setAnswer(answer);
        }
        return FeedbackResponseAttributes.builder(questionId, student.getEmail(), recipient)
                .withResponseDetails(details)
                .build();
    }

    private FeedbackResponseCommentAttributes getFeedbackResponseComment(String responseId, String comment) {
        return FeedbackResponseCommentAttributes.builder()
                .withFeedbackResponseId(responseId)
                .withCommentGiver(student.getEmail())
                .withCommentFromFeedbackParticipant(true)
                .withCommentText(comment)
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

