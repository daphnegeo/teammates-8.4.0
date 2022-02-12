package teammates.lnp.cases;

import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * L&P Test Case for course update cascade API.
 */
public class InstructorCourseUpdateLNPTest extends BaseLNPTestCase {
    private static final int NUM_INSTRUCTORS = 1;
    private static final int RAMP_UP_PERIOD = NUM_INSTRUCTORS * 2;

    private static final int NUM_FEEDBACK_SESSIONS = 500;

    private static final String COURSE_ID = "TestData.CS101";
    private static final String COURSE_NAME = "LnPCourse";
    private static final String COURSE_TIME_ZONE = "UTC";

    private static final String UPDATE_COURSE_NAME = "updatedCourse";
    private static final String UPDATE_COURSE_TIME_ZONE = "GMT";

    private static final String INSTRUCTOR_ID = "LnPInstructor_id";
    private static final String INSTRUCTOR_NAME = "LnPInstructor";
    private static final String INSTRUCTOR_EMAIL = "tmms.test@gmail.tmt";

    private static final String FEEDBACK_SESSION_NAME = "Test Feedback Session";

    private static final double ERROR_RATE_LIMIT = 0.01;
    private static final double MEAN_RESP_TIME_LIMIT = 10;
}
