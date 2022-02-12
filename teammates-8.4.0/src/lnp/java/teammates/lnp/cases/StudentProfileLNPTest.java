package teammates.lnp.cases;

import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * L&P Test Case for student profile API endpoint.
 */
public final class StudentProfileLNPTest extends BaseLNPTestCase {

    private static final int NUMBER_OF_USER_ACCOUNTS = 10;
    private static final int RAMP_UP_PERIOD = 2;

    private static final String STUDENT_NAME = "LnPStudent";
    private static final String STUDENT_EMAIL = "personalEmail";

    private static final double ERROR_RATE_LIMIT = 0.01;
    private static final double MEAN_RESP_TIME_LIMIT = 1;

}
