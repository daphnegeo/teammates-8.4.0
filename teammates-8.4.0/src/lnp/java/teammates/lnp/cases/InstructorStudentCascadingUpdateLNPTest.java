package teammates.lnp.cases;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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
import teammates.common.exception.HttpRequestFailedException;
import teammates.lnp.util.LNPTestData;
import teammates.lnp.util.TestProperties;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;

/**
 * L&P Test Case for cascading batch updating students.
 */
public class InstructorStudentCascadingUpdateLNPTest extends BaseLNPTestCase {
    private static final int NUM_INSTRUCTORS = 1;
    private static final int RAMP_UP_PERIOD = NUM_INSTRUCTORS * 2;

    private static final int NUM_STUDENTS = 1000;
    private static final int NUM_STUDENTS_PER_SECTION = 50;
    private static final int NUMBER_OF_FEEDBACK_QUESTIONS = 20;

    private static final String INSTRUCTOR_NAME = "LnPInstructor";
    private static final String INSTRUCTOR_ID = "LnPInstructor_id";
    private static final String INSTRUCTOR_EMAIL = "tmms.test@gmail.tmt";
    private static final String COURSE_NAME = "tmms.test.gma-demo";
    private static final String COURSE_ID = "tmms.test.gma-demo";

    private static final String STUDENT_NAME_PREFIX = "LnPStudent";
    private static final String STUDENT_ID_PREFIX = "LnPStudent.tmms";
    private static final String STUDENT_EMAIL_SUBFIX = "@gmail.tmt";

    private static final String FEEDBACK_RESPONSE_PREFIX = "LnPResponse";
    private static final String FEEDBACK_SESSION_NAME = "LnPSession";

    private static final double ERROR_RATE_LIMIT = 0.01;
    private static final double MEAN_RESP_TIME_LIMIT = 60;

    // To generate multiple csv files for multiple sections
    private static int csvTestDataIndex;
    private static LNPTestData testData;

    @Override
    protected void createTestData() throws IOException, HttpRequestFailedException {
        LNPTestData testData = getTestData();
        createJsonDataFile(testData);
        persistTestData();
    }

    @Override
    protected String getCsvConfigPath() {
        return "/" + getClass().getSimpleName() + "Config_" + csvTestDataIndex + timeStamp + ".csv";
    }

    /**
     * Generates csv data for each request, distinguished by csvTestDataIndex.
     */
    protected void createCsvConfigDataFile() throws IOException {
        List<String> headers = testData.generateCsvHeaders();
        List<List<String>> valuesList = testData.generateCsvData();

        String pathToCsvFile = createFileAndDirectory(TestProperties.LNP_TEST_DATA_FOLDER, getCsvConfigPath());
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(pathToCsvFile))) {
            // Write headers and data to the CSV file
            bw.write(convertToCsv(headers));

            for (List<String> values : valuesList) {
                bw.write(convertToCsv(values));
            }

            bw.flush();
        }
    }

    @Override
    protected void deleteDataFiles() throws IOException {
        String pathToJsonFile = getPathToTestDataFile(getJsonDataPath());

        csvTestDataIndex = 0;
        for (int i = 0; i < NUM_STUDENTS / NUM_STUDENTS_PER_SECTION; i++) {
            String pathToCsvFile = getPathToTestDataFile(getCsvConfigPath());
            Files.delete(Paths.get(pathToCsvFile));
            csvTestDataIndex++;
        }

        Files.delete(Paths.get(pathToJsonFile));
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
