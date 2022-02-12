package teammates.lnp.cases;

import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * L&P Test Case for instructor's student enrollment API endpoint.
 */
public class InstructorStudentEnrollmentLNPTest extends BaseLNPTestCase {

    private static final int NUM_INSTRUCTORS = 10;
    private static final int RAMP_UP_PERIOD = NUM_INSTRUCTORS * 2;

    private static final int NUM_STUDENTS_PER_INSTRUCTOR = 100;
    private static final int NUM_STUDENTS_PER_SECTION = 25;

    private static final String INSTRUCTOR_NAME = "LnPInstructor";
    private static final String INSTRUCTOR_EMAIL = "personalEmail";
    private static final String COURSE_NAME = "LnPCourse";

    private static final double ERROR_RATE_LIMIT = 0.01;
    private static final double MEAN_RESP_TIME_LIMIT = 80;

}
