package teammates.lnp.cases;

public abstract class studentUpdateVarious extends BaseLNPTestCase {

	private static final int NUM_INSTRUCTORS = 1;
	private static final int RAMP_UP_PERIOD = NUM_INSTRUCTORS * 2;
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
	private static final String STUDENT_COMMENTS = "This is test student comment";
	private static final String TEAM_NAME = "Team 1";
	private static final String GIVER_SECTION_NAME = "Section 1";
	private static final String RECEIVER_SECTION_NAME = "Section 1";
	private static final String UPDATE_GIVER_SECTION_NAME = "Section 2";
	private static final String FEEDBACK_SESSION_NAME = "Test Feedback Session";
	private static final String FEEDBACK_QUESTION_ID = "QuestionTest";
	private static final String FEEDBACK_QUESTION_TEXT = "Test Question description";
	private static final String FEEDBACK_RESPONSE_ID = "ResponseForQ";
	private static final double ERROR_RATE_LIMIT = 0.01;
	private static final double MEAN_RESP_TIME_LIMIT = 10;

	public studentUpdateVarious() {
		super();
	}

}