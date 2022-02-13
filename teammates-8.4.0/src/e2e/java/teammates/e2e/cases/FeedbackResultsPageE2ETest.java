package teammates.e2e.cases;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AppPage;
import teammates.e2e.pageobjects.VerifyResponseDetailsParameter;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;

/**
 * SUT: {@link Const.WebPageURIs#SESSION_RESULTS_PAGE}.
 */
public class FeedbackResultsPageE2ETest extends BaseE2ETestCase {
    public AppPage resultsPage;
    private FeedbackSessionAttributes openSession;
    public List<FeedbackQuestionAttributes> questions = new ArrayList<>();

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackResultsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        openSession = testData.feedbackSessions.get("Open Session");
        for (int i = 1; i <= testData.feedbackQuestions.size(); i++) {
            questions.add(testData.feedbackQuestions.get("qn" + i));
        }
    }

    @Test
    @Override
    public void testAll() {

        testAllTSmethod();

    }

	/**
	 * @deprecated Use {@link teammates.common.datatransfer.attributes.FeedbackSessionAttributes#testAllTSmethod(teammates.e2e.cases.FeedbackResultsPageE2ETest)} instead
	 * 
	 */
	private void testAllTSmethod() {
		openSession.testAllTSmethod(this);
	}

    private void verifyLoadedQuestions(StudentAttributes currentStudent) {
        Set<FeedbackQuestionAttributes> qnsWithResponse = getQnsWithResponses(currentStudent);
        questions.forEach(qn -> {
            if (qnsWithResponse.contains(qn)) {
                resultsPage.verifyQuestionDetails(qn.getQuestionNumber(), qn);
            } else {
                resultsPage.verifyQuestionNotPresent(qn.getQuestionNumber());
            }
        });
    }

    private void verifyLoadedQuestions(InstructorAttributes currentInstructor) {
        Set<FeedbackQuestionAttributes> qnsWithResponse = getQnsWithResponses(currentInstructor);
        questions.forEach(qn -> {
            if (qnsWithResponse.contains(qn)) {
                resultsPage.verifyQuestionDetails(qn.getQuestionNumber(), qn);
            } else {
                resultsPage.verifyQuestionNotPresent(qn.getQuestionNumber());
            }
        });
    }

    public void verifyResponseDetails(StudentAttributes currentStudent, EntityAttributes<FeedbackQuestion> question) {
        List<FeedbackResponseAttributes> givenResponses = getGivenResponses(currentStudent, question);
        List<FeedbackResponseAttributes> otherResponses = getOtherResponses(currentStudent, question);
        Set<String> visibleGivers = getVisibleGivers(currentStudent, question);
        Set<String> visibleRecipients = getVisibleRecipients(currentStudent, question);
        resultsPage.verifyResponseDetails(new VerifyResponseDetailsParameter(question, givenResponses, otherResponses, visibleGivers, visibleRecipients));
    }

    public void verifyResponseDetails(InstructorAttributes currentInstructor, EntityAttributes<FeedbackQuestion> question) {
        List<FeedbackResponseAttributes> givenResponses = getGivenResponses(currentInstructor, question);
        List<FeedbackResponseAttributes> otherResponses = getOtherResponses(currentInstructor, question);
        Set<String> visibleGivers = getVisibleGivers(currentInstructor, question);
        Set<String> visibleRecipients = getVisibleRecipients(currentInstructor, question);
        resultsPage.verifyResponseDetails(new VerifyResponseDetailsParameter(question, givenResponses, otherResponses, visibleGivers, visibleRecipients));
    }

    private void verifyCommentDetails(int questionNum, FeedbackResponseCommentAttributes comment,
                                      StudentAttributes currentStudent) {
        String editor = "";
        String giver = "";
        if (comment.getLastEditorEmail() != null) {
            editor = getIdentifier(currentStudent, comment.getLastEditorEmail());
        }
        if (!comment.getCommentGiverType().equals(FeedbackParticipantType.STUDENTS)) {
            giver = getIdentifier(currentStudent, comment.getCommentGiver());
        }
        resultsPage.verifyCommentDetails(questionNum, giver, editor, comment.getCommentText());
    }

    private Set<FeedbackQuestionAttributes> getQnsWithResponses(StudentAttributes currentStudent) {
        return questions.stream()
                .filter(qn -> getGivenResponses(currentStudent, qn).size() > 0
                        || getOtherResponses(currentStudent, qn).size() > 0)
                .collect(Collectors.toSet());
    }

    private Set<FeedbackQuestionAttributes> getQnsWithResponses(InstructorAttributes currentInstructor) {
        return questions.stream()
                .filter(qn -> getGivenResponses(currentInstructor, qn).size() > 0
                        || getOtherResponses(currentInstructor, qn).size() > 0)
                .collect(Collectors.toSet());
    }

    private List<FeedbackResponseAttributes> getGivenResponses(StudentAttributes currentStudent,
                                                               EntityAttributes<FeedbackQuestion> question) {
        List<FeedbackResponseAttributes> givenResponses = testData.feedbackResponses.values().stream()
                .filter(f -> f.getFeedbackQuestionId().equals(Integer.toString(question.getQuestionNumber()))
                        && f.getGiver().equals(currentStudent.getEmail()))
                .collect(Collectors.toList());
        return editIdentifiers(currentStudent, givenResponses);
    }

    private List<FeedbackResponseAttributes> getGivenResponses(InstructorAttributes currentInstructor,
                                                               EntityAttributes<FeedbackQuestion> question) {
        List<FeedbackResponseAttributes> givenResponses = testData.feedbackResponses.values().stream()
                .filter(f -> f.getFeedbackQuestionId().equals(Integer.toString(question.getQuestionNumber()))
                        && f.getGiver().equals(currentInstructor.getEmail()))
                .collect(Collectors.toList());
        return editIdentifiers(currentInstructor, givenResponses);
    }

    private List<FeedbackResponseAttributes> getOtherResponses(StudentAttributes currentStudent,
                                                               EntityAttributes<FeedbackQuestion> question) {
        Set<String> visibleResponseGivers = getRelevantUsers(currentStudent, question.getShowResponsesTo());
        visibleResponseGivers.add(currentStudent.getEmail());

        List<FeedbackResponseAttributes> questionResponses = testData.feedbackResponses.values().stream()
                .filter(fr -> fr.getFeedbackQuestionId().equals(Integer.toString(question.getQuestionNumber())))
                .collect(Collectors.toList());

        List<FeedbackResponseAttributes> selfEvaluationResponses = questionResponses.stream()
                .filter(fr -> fr.getGiver().equals(currentStudent.getEmail())
                        && fr.getRecipient().equals(currentStudent.getEmail()))
                .collect(Collectors.toList());

        List<FeedbackResponseAttributes> responsesByOthers = questionResponses.stream()
                .filter(fr -> !fr.getGiver().equals(currentStudent.getEmail())
                        && visibleResponseGivers.contains(fr.getGiver()))
                .collect(Collectors.toList());

        List<FeedbackResponseAttributes> responsesToSelf = new ArrayList<>();
        if (visibleResponseGivers.contains("RECEIVER")) {
            responsesToSelf = questionResponses.stream()
                    .filter(fr -> !fr.getGiver().equals(currentStudent.getEmail())
                            && fr.getRecipient().equals(currentStudent.getEmail()))
                    .collect(Collectors.toList());
        }

        List<FeedbackResponseAttributes> otherResponses = otherResponsesMethod();
        otherResponses.addAll(selfEvaluationResponses);
        otherResponses.addAll(responsesByOthers);
        otherResponses.addAll(responsesToSelf);

        return editIdentifiers(currentStudent, otherResponses);
    }

    private List<FeedbackResponseAttributes> getOtherResponses(InstructorAttributes currentInstructor,
                                                               EntityAttributes<FeedbackQuestion> question) {
        Set<String> visibleResponseGivers = getRelevantUsersForInstructors(question.getShowResponsesTo());
        visibleResponseGivers.add(currentInstructor.getEmail());

        List<FeedbackResponseAttributes> questionResponses = testData.feedbackResponses.values().stream()
                .filter(fr -> fr.getFeedbackQuestionId().equals(Integer.toString(question.getQuestionNumber())))
                .collect(Collectors.toList());

        List<FeedbackResponseAttributes> selfEvaluationResponses = questionResponses.stream()
                .filter(fr -> fr.getGiver().equals(currentInstructor.getEmail())
                        && fr.getRecipient().equals(currentInstructor.getEmail()))
                .collect(Collectors.toList());

        List<FeedbackResponseAttributes> responsesByOthers = questionResponses.stream()
                .filter(fr -> !fr.getGiver().equals(currentInstructor.getEmail())
                        && visibleResponseGivers.contains(fr.getGiver()))
                .collect(Collectors.toList());

        List<FeedbackResponseAttributes> responsesToSelf = new ArrayList<>();
        if (visibleResponseGivers.contains("RECEIVER") || visibleResponseGivers.contains("INSTRUCTORS")) {
            responsesToSelf = questionResponses.stream()
                    .filter(fr -> !fr.getGiver().equals(currentInstructor.getEmail())
                            && fr.getRecipient().equals(currentInstructor.getEmail()))
                    .collect(Collectors.toList());
        }

        List<FeedbackResponseAttributes> otherResponses = otherResponsesMethod();
        otherResponses.addAll(selfEvaluationResponses);
        otherResponses.addAll(responsesByOthers);
        otherResponses.addAll(responsesToSelf);

        return editIdentifiers(currentInstructor, otherResponses);
    }

	/**
	 * @return
	 * @deprecated Use {@link teammates.common.datatransfer.attributes.FeedbackSessionAttributes#otherResponsesMethod()} instead
	 */
	private List<FeedbackResponseAttributes> otherResponsesMethod() {
		return openSession.otherResponsesMethod();
	}

    private Set<String> getVisibleGivers(StudentAttributes currentStudent, EntityAttributes<FeedbackQuestion> question) {
        return getRelevantUsers(currentStudent, question.getShowGiverNameTo()).stream()
                .map(user -> getIdentifier(currentStudent, user))
                .collect(Collectors.toSet());
    }

    private Set<String> getVisibleGivers(InstructorAttributes currentInstructor, EntityAttributes<FeedbackQuestion> question) {
        return getRelevantUsersForInstructors(question.getShowGiverNameTo()).stream()
                .map(user -> getIdentifier(currentInstructor, user))
                .collect(Collectors.toSet());
    }

    private Set<String> getVisibleRecipients(StudentAttributes currentStudent, EntityAttributes<FeedbackQuestion> question) {
        return getRelevantUsers(currentStudent, question.getShowRecipientNameTo()).stream()
                .map(user -> getIdentifier(currentStudent, user))
                .collect(Collectors.toSet());
    }

    private Set<String> getVisibleRecipients(InstructorAttributes currentInstructor, EntityAttributes<FeedbackQuestion> question) {
        return getRelevantUsersForInstructors(question.getShowRecipientNameTo()).stream()
                .map(user -> getIdentifier(currentInstructor, user))
                .collect(Collectors.toSet());
    }

    private Set<String> getRelevantUsers(StudentAttributes giver, List<FeedbackParticipantType> relevantParticipants) {
        Set<String> relevantUsers = new HashSet<>();
        List<StudentAttributes> students = new ArrayList<>();
        if (relevantParticipants.contains(FeedbackParticipantType.STUDENTS)) {
            students.addAll(getOtherStudents(giver));
        } else if (relevantParticipants.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS)) {
            students.addAll(getOtherTeammates(giver));
        }
        students.forEach(s -> relevantUsers.add(s.getEmail()));
        students.forEach(s -> relevantUsers.add(s.getTeam()));

        if (relevantParticipants.contains(FeedbackParticipantType.RECEIVER)) {
            relevantUsers.add("RECEIVER");
        }

        return relevantUsers;
    }

    private Set<String> getRelevantUsersForInstructors(List<FeedbackParticipantType> relevantParticipants) {
        Set<String> relevantUsers = new HashSet<>();
        if (relevantParticipants.contains(FeedbackParticipantType.RECEIVER)) {
            relevantUsers.add("RECEIVER");
        }
        if (relevantParticipants.contains(FeedbackParticipantType.INSTRUCTORS)) {
            relevantUsers.add("INSTRUCTORS");
        }
        return relevantUsers;
    }

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.attributes.FeedbackSessionAttributes#getOtherTeammates(teammates.e2e.cases.FeedbackResultsPageE2ETest,StudentAttributes)} instead
	 */
	private Set<StudentAttributes> getOtherTeammates(StudentAttributes currentStudent) {
		return openSession.getOtherTeammates(this, currentStudent);
	}

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.attributes.FeedbackSessionAttributes#getOtherStudents(teammates.e2e.cases.FeedbackResultsPageE2ETest,StudentAttributes)} instead
	 */
	private Set<StudentAttributes> getOtherStudents(StudentAttributes currentStudent) {
		return openSession.getOtherStudents(this, currentStudent);
	}

    private List<FeedbackResponseAttributes> editIdentifiers(StudentAttributes currentStudent,
                                                             List<FeedbackResponseAttributes> responses) {
        List<FeedbackResponseAttributes> editedResponses = deepCopyResponses(responses);
        editedResponses.forEach(fr -> {
            fr.setGiver(getIdentifier(currentStudent, fr.getGiver()));
            fr.setRecipient(getIdentifier(currentStudent, fr.getRecipient()));
        });
        return editedResponses;
    }

    private List<FeedbackResponseAttributes> editIdentifiers(InstructorAttributes currentInstructor,
                                                             List<FeedbackResponseAttributes> responses) {
        List<FeedbackResponseAttributes> editedResponses = deepCopyResponses(responses);
        editedResponses.forEach(fr -> {
            fr.setGiver(getIdentifier(currentInstructor, fr.getGiver()));
            fr.setRecipient(getIdentifier(currentInstructor, fr.getRecipient()));
        });
        return editedResponses;
    }

    private String getIdentifier(StudentAttributes currentStudent, String user) {
        if (currentStudent.getEmail().equals(user)) {
            return "You";
        }
        if (Const.GENERAL_QUESTION.equals(user)) {
            return Const.USER_NOBODY_TEXT;
        }
        if (user.equals(currentStudent.getTeam())) {
            return "Your Team (" + user + ")";
        }
        String identifier = getInstructorName(user);
        if (identifier == null) {
            identifier = getStudentName(user);
        }
        if (identifier == null) {
            identifier = user;
        }
        return identifier;
    }

    private String getIdentifier(InstructorAttributes currentInstructor, String user) {
        return identifierMethod(currentInstructor, user);
    }

	/**
	 * @param currentInstructor
	 * @param user
	 * @return
	 * @deprecated Use {@link teammates.common.datatransfer.attributes.FeedbackSessionAttributes#identifierMethod(teammates.e2e.cases.FeedbackResultsPageE2ETest,InstructorAttributes,String)} instead
	 */
	private String identifierMethod(InstructorAttributes currentInstructor, String user) {
		return openSession.identifierMethod(this, currentInstructor, user);
	}

    public String getStudentName(String studentEmail) {
        return testData.students.values().stream()
               .filter(s -> s.getEmail().equals(studentEmail))
               .map(StudentAttributes::getName)
               .findFirst()
               .orElse(null);
    }

    public String getInstructorName(String instructorEmail) {
        return testData.instructors.values().stream()
                .filter(s -> s.getEmail()
                        .equals(instructorEmail))
                .map(InstructorAttributes::getName)
                .findFirst()
                .orElse(null);
    }

    private List<FeedbackResponseAttributes> deepCopyResponses(List<FeedbackResponseAttributes> responses) {
        List<FeedbackResponseAttributes> copiedResponses = new ArrayList<>();
        for (FeedbackResponseAttributes response : responses) {
            copiedResponses.add(new FeedbackResponseAttributes(response));
        }
        return copiedResponses;
    }

    private void verifyExpectedRubricStats() {
        FeedbackRubricQuestionDetails rubricsQnDetails =
                (FeedbackRubricQuestionDetails) testData.feedbackQuestions.get("qn10").getQuestionDetailsCopy();
        List<String> subQns = rubricsQnDetails.getRubricSubQuestions();
        statsList(subQns);
    }

	/**
	 * @param subQns
	 * @deprecated Use {@link teammates.common.datatransfer.attributes.FeedbackSessionAttributes#statsList(teammates.e2e.cases.FeedbackResultsPageE2ETest,List<String>)} instead
	 */
	private void statsList(List<String> subQns) {
		openSession.statsList(this, subQns);
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
