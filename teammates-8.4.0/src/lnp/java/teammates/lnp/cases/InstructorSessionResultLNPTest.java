package teammates.lnp.cases;

import java.util.Map;

import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.lnp.util.JMeterElements;

/**
 * L&P Test Case for instructor viewing feedback sessions results.
 */
public class InstructorSessionResultLNPTest extends BaseLNPTestCase {
    private static final int RAMP_UP_PERIOD = 2;
    private static final int NUMBER_OF_USER_ACCOUNTS = 500;
    private static final int NUMBER_OF_QUESTIONS = 10;
    private static final int SIZE_OF_TEAM = 4;
    private static final int SIZE_OF_SECTION = 100;
    private static final String STUDENT_NAME = "LnPStudent";
    private static final String STUDENT_EMAIL = "studentEmail";
    private static final String INSTRUCTOR_EMAIL = "tmms.test@gmail.tmt";

    private static final String COURSE_ID = "TestData.CS101";
    private static final String FEEDBACK_SESSION_NAME = "Test Feedback Session";

    private static final double ERROR_RATE_LIMIT = 0.01;
    private static final double MEAN_RESP_TIME_LIMIT = 7;

    private void addLoadPageController(HashTree threadGroup, Map<String, String> argumentsMap) {
        HashTree loadPageController = threadGroup.add(JMeterElements.genericController());

        loadPageController.add(JMeterElements.defaultSampler(argumentsMap));

        String getSessionPath = Const.ResourceURIs.SESSION;
        loadPageController.add(JMeterElements.httpGetSampler(getSessionPath));

        String getQuestionsPath = Const.ResourceURIs.QUESTIONS;
        loadPageController.add(JMeterElements.httpGetSampler(getQuestionsPath));
    }

    private void addLoadSectionsController(HashTree threadGroup, Map<String, String> sectionsArgumentsMap) {
        HashTree loadSectionsController = threadGroup.add(JMeterElements.genericController());

        loadSectionsController.add(JMeterElements.defaultSampler(sectionsArgumentsMap));

        String getSectionsPath = Const.ResourceURIs.COURSE_SECTIONS;
        loadSectionsController.add(JMeterElements.httpGetSampler(getSectionsPath));
    }

    private void addLoadNoResponsePanelController(HashTree threadGroup, Map<String, String> argumentsMap) {
        HashTree loadNoResponsePanelController = threadGroup.add(JMeterElements.genericController());

        loadNoResponsePanelController.add(JMeterElements.defaultSampler(argumentsMap));
        String getStudentsPath = Const.ResourceURIs.STUDENTS;
        loadNoResponsePanelController.add(JMeterElements.httpGetSampler(getStudentsPath));

        String getSubmittedGiverSetPath = Const.ResourceURIs.SESSION_SUBMITTED_GIVER_SET;
        loadNoResponsePanelController.add(JMeterElements.httpGetSampler(getSubmittedGiverSetPath));
    }

    private void addLoadQuestionPanelController(HashTree threadGroup, Map<String, String> argumentsMap) {
        HashTree loadQuestionPanelController = threadGroup.add(JMeterElements.genericController());

        loadQuestionPanelController.add(JMeterElements.defaultSampler(argumentsMap));

        for (int i = 1; i <= NUMBER_OF_QUESTIONS; i++) {
            String getSessionResultPath =
                    String.format(Const.ResourceURIs.RESULT + "?questionid=${feedbackQuestion_%d}", i);
            loadQuestionPanelController.add(JMeterElements.httpGetSampler(getSessionResultPath));
        }
    }

    private void addLoadSectionPanelController(HashTree threadGroup, Map<String, String> argumentsMap) {
        HashTree loadSectionPanelController = threadGroup.add(
                JMeterElements.foreachController("sectionNumber", "sectionNumber"));

        loadSectionPanelController.add(JMeterElements.defaultSampler(argumentsMap));

        for (int i = 1; i <= NUMBER_OF_USER_ACCOUNTS / SIZE_OF_SECTION; i++) {
            String getSessionResultPath =
                    String.format(Const.ResourceURIs.RESULT + "?frgroupbysection=Section ${sectionNumber_%d}", i);
            loadSectionPanelController.add(JMeterElements.httpGetSampler(getSessionResultPath));
        }
    }
}
