package teammates.lnp.cases;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.report.config.ConfigurationException;
import org.apache.jmeter.report.dashboard.GenerationException;
import org.apache.jmeter.report.dashboard.ReportGenerator;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.exception.HttpRequestFailedException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.lnp.util.BackDoor;
import teammates.lnp.util.JMeterElements;
import teammates.lnp.util.LNPResultsStatistics;
import teammates.lnp.util.LNPSpecification;
import teammates.lnp.util.LNPTestData;
import teammates.lnp.util.TestProperties;
import teammates.test.BaseTestCase;
import teammates.test.FileHelper;
import teammates.ui.request.StudentUpdateRequest;

/**
 * Base class for all L&P test cases.
 */
public abstract class BaseLNPTestCase extends BaseTestCase {

    static final String GET = HttpGet.METHOD_NAME;
    static final String POST = HttpPost.METHOD_NAME;
    static final String PUT = HttpPut.METHOD_NAME;
    static final String DELETE = HttpDelete.METHOD_NAME;

    private static final Logger log = Logger.getLogger();

    private static final int RESULT_COUNT = 3;
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
	private static final String UPDATE_STUDENT_EMAIL = "studentEmailUpdate@gmail.tmt";
	private static final String TEAM_NAME = "Team 1";
	private static final String GIVER_SECTION_NAME = "Section 1";
	private static final String RECEIVER_SECTION_NAME = "Section 1";
	private static final String FEEDBACK_SESSION_NAME = "Test Feedback Session";
	private static final String FEEDBACK_QUESTION_ID = "QuestionTest";
	private static final String FEEDBACK_QUESTION_TEXT = "Test Question description";
	private static final String FEEDBACK_RESPONSE_ID = "ResponseForQ";
	private static final double ERROR_RATE_LIMIT = 0.01;
	private static final double MEAN_RESP_TIME_LIMIT = 10;
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

    final BackDoor backdoor = BackDoor.getInstance();
    String timeStamp;
    LNPSpecification specification;

    /**
     * Returns the path to the generated JSON data bundle file.
     */
    protected String getJsonDataPath() {
        return "/" + getClass().getSimpleName() + timeStamp + ".json";
    }

    /**
     * Returns the path to the generated JMeter CSV config file.
     */
    protected String getCsvConfigPath() {
        return "/" + getClass().getSimpleName() + "Config" + timeStamp + ".csv";
    }

    /**
     * Returns the path to the generated JTL test results file.
     */
    protected String getJtlResultsPath() {
        return "/" + getClass().getSimpleName() + timeStamp + ".jtl";
    }

    @Override
    protected String getTestDataFolder() {
        return TestProperties.LNP_TEST_DATA_FOLDER;
    }

    /**
     * Returns the path to the data file, relative to the project root directory.
     */
    protected String getPathToTestDataFile(String fileName) {
        return getTestDataFolder() + fileName;
    }

    /**
     * Returns the path to the JSON test results statistics file, relative to the project root directory.
     */
    private String getPathToTestStatisticsResultsFile() {
        return String.format("%s/%sStatistics%s.json", TestProperties.LNP_TEST_RESULTS_FOLDER,
                        this.getClass().getSimpleName(), this.timeStamp);
    }

    String createFileAndDirectory(String directory, String fileName) throws IOException {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdir();
        }

        String pathToFile = directory + fileName;
        File file = new File(pathToFile);

        // Write data to the file; overwrite if it already exists
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        return pathToFile;
    }

    /**
     * Creates the JSON data and writes it to the file specified by {@link #getJsonDataPath()}.
     */
    void createJsonDataFile(LNPTestData testData) throws IOException {
        DataBundle jsonData = testData.generateJsonData();

        String pathToResultFile = createFileAndDirectory(TestProperties.LNP_TEST_DATA_FOLDER, getJsonDataPath());
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(pathToResultFile))) {
            bw.write(JsonUtils.toJson(jsonData));
            bw.flush();
        }
    }

    /**
     * Creates the CSV data and writes it to the file specified by {@link #getCsvConfigPath()}.
     */
    private void createCsvConfigDataFile(LNPTestData testData) throws IOException {
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

    /**
     * Converts the list of {@code values} to a CSV row.
     * @return A single string containing {@code values} separated by pipelines and ending with newline.
     */
    String convertToCsv(List<String> values) {
        StringJoiner csvRow = new StringJoiner("|", "", "\n");
        for (String value : values) {
            csvRow.add(value);
        }
        return csvRow.toString();
    }

    /**
     * Returns the L&P test results statistics.
     * @return The initialized result statistics from the L&P test results.
     * @throws IOException if there is an error when loading the result file.
     */
    private LNPResultsStatistics getResultsStatistics() throws IOException {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(Files.newBufferedReader(Paths.get(getPathToTestStatisticsResultsFile())));
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

        JsonObject endpointStats = jsonObject.getAsJsonObject("HTTP Request Sampler");
        return gson.fromJson(endpointStats, LNPResultsStatistics.class);
    }

    /**
     * Renames the default results statistics file to the name of the test.
     */
    private void renameStatisticsFile() {
        File defaultFile = new File(TestProperties.LNP_TEST_RESULTS_FOLDER + "/statistics.json");
        File lnpStatisticsFile = new File(getPathToTestStatisticsResultsFile());

        if (lnpStatisticsFile.exists()) {
            lnpStatisticsFile.delete();
        }
        if (!defaultFile.renameTo(lnpStatisticsFile)) {
            log.warning("Failed to rename generated statistics.json file.");
        }
    }

    /**
     * Setup and load the JMeter configuration and property files to run the Jmeter test.
     * @throws IOException if the save service properties file cannot be loaded.
     */
    private void setJmeterProperties() throws IOException {
        JMeterUtils.loadJMeterProperties(TestProperties.JMETER_PROPERTIES_PATH);
        JMeterUtils.setJMeterHome(TestProperties.JMETER_HOME);
        JMeterUtils.initLocale();
        SaveService.loadProperties();
    }

    /**
     * Creates the JSON test data and CSV config data files for the performance test from {@code testData}.
     */
    protected void createTestData() throws IOException, HttpRequestFailedException {
        LNPTestData testData = getTestData();
        createJsonDataFile(testData);
        persistTestData();
        createCsvConfigDataFile(testData);
    }

    /**
     * Creates the entities in the database from the JSON data file.
     */
    protected void persistTestData() throws IOException, HttpRequestFailedException {
        DataBundle dataBundle = loadDataBundle(getJsonDataPath());
        String responseBody = backdoor.removeAndRestoreDataBundle(dataBundle);

        String pathToResultFile = createFileAndDirectory(TestProperties.LNP_TEST_DATA_FOLDER, getJsonDataPath());
        String jsonValue = JsonUtils.parse(responseBody).getAsJsonObject().get("message").getAsString();
        FileHelper.saveFile(pathToResultFile, jsonValue);
    }

    /**
     * Display the L&P results on the console.
     */
    protected void displayLnpResults() throws IOException {
        LNPResultsStatistics resultsStats = getResultsStatistics();

        resultsStats.displayLnpResultsStatistics();
        specification.verifyLnpTestSuccess(resultsStats);
    }

    /**
     * Runs the JMeter test.
     * @param shouldCreateJmxFile true if the generated test plan should be saved to a `.jmx` file which
     *                            can be opened in the JMeter GUI, and false otherwise.
     */
    protected void runJmeter(boolean shouldCreateJmxFile) throws IOException {
        StandardJMeterEngine jmeter = new StandardJMeterEngine();
        setJmeterProperties();

        HashTree testPlan = getLnpTestPlan();

        if (shouldCreateJmxFile) {
            String pathToConfigFile = createFileAndDirectory(
                    TestProperties.LNP_TEST_CONFIG_FOLDER, "/" + getClass().getSimpleName() + ".jmx");
            SaveService.saveTree(testPlan, Files.newOutputStream(Paths.get(pathToConfigFile)));
        }

        // Add result collector to the test plan for generating results file
        Summariser summariser = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summariser = new Summariser(summariserName);
        }

        String resultsFile = createFileAndDirectory(TestProperties.LNP_TEST_RESULTS_FOLDER, getJtlResultsPath());
        ResultCollector resultCollector = new ResultCollector(summariser);
        resultCollector.setFilename(resultsFile);
        testPlan.add(testPlan.getArray()[0], resultCollector);

        // Run Jmeter Test
        jmeter.configure(testPlan);
        jmeter.run();

        try {
            ReportGenerator reportGenerator = new ReportGenerator(resultsFile, null);
            reportGenerator.generate();
        } catch (ConfigurationException | GenerationException e) {
            log.warning(e.getMessage());
        }

        renameStatisticsFile();
    }

    /**
     * Deletes the data that was created in the database from the JSON data file.
     */
    protected void deleteTestData() {
        DataBundle dataBundle = loadDataBundle(getJsonDataPath());
        backdoor.removeDataBundle(dataBundle);
    }

    /**
     * Deletes the JSON and CSV data files that were created.
     */
    protected void deleteDataFiles() throws IOException {
        String pathToJsonFile = getPathToTestDataFile(getJsonDataPath());
        String pathToCsvFile = getPathToTestDataFile(getCsvConfigPath());

        Files.delete(Paths.get(pathToJsonFile));
        Files.delete(Paths.get(pathToCsvFile));
    }

    /**
     * Deletes the oldest excess result .jtl file and the statistics file, if there are more than RESULT_COUNT.
     */
    protected void cleanupResults() throws IOException {
        File[] fileList = new File(TestProperties.LNP_TEST_RESULTS_FOLDER)
                .listFiles((d, s) -> {
                    return s.contains(this.getClass().getSimpleName());
                });
        Arrays.sort(fileList, (a, b) -> {
            return b.getName().compareTo(a.getName());
        });

        int jtlCounter = 0;
        int statisticsCounter = 0;
        for (File file : fileList) {
            if (file.getName().contains("Statistics")) {
                statisticsCounter++;
                if (statisticsCounter > RESULT_COUNT) {
                    Files.delete(file.toPath());
                }
            } else {
                jtlCounter++;
                if (jtlCounter > RESULT_COUNT) {
                    Files.delete(file.toPath());
                }
            }
        }
    }

    /**
     * Sanitize the string to be CSV-safe string.
     */
    protected String sanitizeForCsv(String originalString) {
        return String.format("\"%s\"", originalString.replace(System.lineSeparator(), "").replace("\"", "\"\""));
    }

    /**
     * Generates timestamp for generated statistics/CSV files in order to prevent concurrency issues.
     */
    protected void generateTimeStamp() {
        this.timeStamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("_uuuuMMddHHmmss"));
    }

	@Override
	protected LNPTestData getTestData() {
	    return new LNPTestData() {
	        protected Map<String, CourseAttributes> generateCourses() {
	            Map<String, CourseAttributes> courses = new HashMap<>();
	
	            courses.put(COURSE_NAME, CourseAttributes.builder(COURSE_ID)
	                    .withName(COURSE_NAME)
	                    .withTimezone(COURSE_TIME_ZONE)
	                    .build());
	
	            return courses;
	        }
	
	        protected Map<String, InstructorAttributes> generateInstructors() {
	            Map<String, InstructorAttributes> instructors = new HashMap<>();
	
	            instructors.put(INSTRUCTOR_NAME,
	                    InstructorAttributes.builder(COURSE_ID, INSTRUCTOR_EMAIL)
	                        .withGoogleId(INSTRUCTOR_ID)
	                        .withName(INSTRUCTOR_NAME)
	                        .withRole("Co-owner")
	                        .withIsDisplayedToStudents(true)
	                        .withDisplayedName("Co-owner")
	                        .withPrivileges(new InstructorPrivileges(
	                                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER))
	                        .build()
	            );
	
	            return instructors;
	        }
	
	        protected Map<String, StudentAttributes> generateStudents() {
	            Map<String, StudentAttributes> students = new LinkedHashMap<>();
	            StudentAttributes studentAttribute;
	
	            studentAttribute = StudentAttributes.builder(COURSE_ID, STUDENT_EMAIL)
	                    .withGoogleId(STUDENT_ID)
	                    .withName(STUDENT_NAME)
	                    .withComment("This student's name is " + STUDENT_NAME)
	                    .withSectionName(GIVER_SECTION_NAME)
	                    .withTeamName(TEAM_NAME)
	                    .build();
	
	            students.put(STUDENT_NAME, studentAttribute);
	
	            return students;
	        }
	
	        protected Map<String, FeedbackSessionAttributes> generateFeedbackSessions() {
	            Map<String, FeedbackSessionAttributes> feedbackSessions = new LinkedHashMap<>();
	
	            FeedbackSessionAttributes session = FeedbackSessionAttributes
	                    .builder(FEEDBACK_SESSION_NAME, COURSE_ID)
	                    .withCreatorEmail(INSTRUCTOR_EMAIL)
	                    .withStartTime(Instant.now().plusMillis(100))
	                    .withEndTime(Instant.now().plusSeconds(500))
	                    .withSessionVisibleFromTime(Instant.now())
	                    .withResultsVisibleFromTime(Instant.now())
	                    .build();
	
	            feedbackSessions.put(FEEDBACK_SESSION_NAME, session);
	
	            return feedbackSessions;
	        }
	
	        protected Map<String, FeedbackQuestionAttributes> generateFeedbackQuestions() {
	            List<FeedbackParticipantType> showResponses = new ArrayList<>();
	            showResponses.add(FeedbackParticipantType.RECEIVER);
	            showResponses.add(FeedbackParticipantType.INSTRUCTORS);
	
	            List<FeedbackParticipantType> showGiverName = new ArrayList<>();
	            showGiverName.add(FeedbackParticipantType.INSTRUCTORS);
	
	            List<FeedbackParticipantType> showRecepientName = new ArrayList<>();
	            showRecepientName.add(FeedbackParticipantType.INSTRUCTORS);
	
	            Map<String, FeedbackQuestionAttributes> feedbackQuestions = new LinkedHashMap<>();
	            FeedbackQuestionDetails details = new FeedbackTextQuestionDetails(FEEDBACK_QUESTION_TEXT);
	
	            feedbackQuestions.put(FEEDBACK_QUESTION_ID,
	                    FeedbackQuestionAttributes.builder()
	                            .withFeedbackSessionName(FEEDBACK_SESSION_NAME)
	                            .withQuestionDescription(FEEDBACK_QUESTION_TEXT)
	                            .withCourseId(COURSE_ID)
	                            .withQuestionDetails(details)
	                            .withQuestionNumber(1)
	                            .withGiverType(FeedbackParticipantType.STUDENTS)
	                            .withRecipientType(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF)
	                            .withShowResponsesTo(showResponses)
	                            .withShowGiverNameTo(showGiverName)
	                            .withShowRecipientNameTo(showRecepientName)
	                            .build()
	            );
	            return feedbackQuestions;
	        }
	
	        protected Map<String, FeedbackResponseAttributes> generateFeedbackResponses() {
	            Map<String, FeedbackResponseAttributes> feedbackResponses = new HashMap<>();
	
	            for (int i = 1; i <= NUMBER_OF_FEEDBACK_RESPONSES; i++) {
	                String responseText = FEEDBACK_RESPONSE_ID + " " + i;
	                FeedbackTextResponseDetails details =
	                        new FeedbackTextResponseDetails(responseText);
	
	                feedbackResponses.put(responseText,
	                        FeedbackResponseAttributes.builder("1",
	                            STUDENT_EMAIL,
	                            STUDENT_EMAIL)
	                            .withCourseId(COURSE_ID)
	                            .withFeedbackSessionName(FEEDBACK_SESSION_NAME)
	                            .withGiverSection(GIVER_SECTION_NAME)
	                            .withRecipientSection(RECEIVER_SECTION_NAME)
	                            .withResponseDetails(details)
	                            .build());
	            }
	
	            return feedbackResponses;
	        }
	
	        public List<String> generateCsvHeaders() {
	            List<String> headers = new ArrayList<>();
	
	            headers.add("loginId");
	            headers.add("courseId");
	            headers.add("studentId");
	            headers.add("studentEmail");
	            headers.add("updateData");
	
	            return headers;
	        }
	
	        public List<List<String>> generateCsvData() {
	            DataBundle dataBundle = loadDataBundle(getJsonDataPath());
	            List<List<String>> csvData = new ArrayList<>();
	
	            dataBundle.instructors.forEach((key, instructor) -> {
	                List<String> csvRow = new ArrayList<>();
	
	                csvRow.add(INSTRUCTOR_ID);
	                csvRow.add(COURSE_ID);
	                csvRow.add(STUDENT_ID);
	                csvRow.add(STUDENT_EMAIL);
	
	                StudentUpdateRequest studentUpdateRequest = new StudentUpdateRequest(
	                        STUDENT_NAME,
	                        UPDATE_STUDENT_EMAIL,
	                        TEAM_NAME,
	                        GIVER_SECTION_NAME,
	                        STUDENT_COMMENTS,
	                        false
	                );
	
	                String updateData = sanitizeForCsv(JsonUtils.toJson(studentUpdateRequest));
	                csvRow.add(updateData);
	
	                csvData.add(csvRow);
	            });
	
	            return csvData;
	        }
	    };
	}

	private Map<String, String> getRequestHeaders() {
	    Map<String, String> headers = new HashMap<>();
	
	    headers.put(Const.HeaderNames.CSRF_TOKEN, "${csrfToken}");
	    headers.put("Content-Type", "application/json");
	
	    return headers;
	}

	private String getTestEndpoint() {
	    return Const.ResourceURIs.STUDENT
	        + "?courseid=${courseId}&studentid=${studentId}&studentemail=${studentEmail}";
	}

	@Override
	protected ListedHashTree getLnpTestPlan() {
	    ListedHashTree testPlan = new ListedHashTree(JMeterElements.testPlan());
	    HashTree threadGroup = testPlan.add(
	            JMeterElements.threadGroup(NUM_INSTRUCTORS, RAMP_UP_PERIOD, 1));
	
	    threadGroup.add(JMeterElements.csvDataSet(getPathToTestDataFile(getCsvConfigPath())));
	    threadGroup.add(JMeterElements.cookieManager());
	    threadGroup.add(JMeterElements.defaultSampler());
	
	    threadGroup.add(JMeterElements.onceOnlyController())
	            .add(JMeterElements.loginSampler())
	            .add(JMeterElements.csrfExtractor("csrfToken"));
	
	    // Add HTTP sampler for test endpoint
	    HeaderManager headerManager = JMeterElements.headerManager(getRequestHeaders());
	    threadGroup.add(JMeterElements.httpSampler(getTestEndpoint(), PUT, "${updateData}"))
	            .add(headerManager);
	
	    return testPlan;
	}

	@Override
	protected void setupSpecification() {
	    this.specification = LNPSpecification.builder()
	            .withErrorRateLimit(ERROR_RATE_LIMIT)
	            .withMeanRespTimeLimit(MEAN_RESP_TIME_LIMIT)
	            .build();
	}

	@BeforeClass
	public void classSetup() throws IOException, HttpRequestFailedException {
	    generateTimeStamp();
	    createTestData();
	    setupSpecification();
	}

	@Test
	public void runLnpTest() throws IOException {
	    runJmeter(false);
	    displayLnpResults();
	}

	@AfterClass
	public void classTearDown() throws IOException {
	    deleteTestData();
	    deleteDataFiles();
	    cleanupResults();
	}

	protected Map<String, AccountAttributes> generateAccounts() {
	    return new HashMap<>();
	}

	protected Map<String, CourseAttributes> generateCourses() {
	    return new HashMap<>();
	}

	protected Map<String, InstructorAttributes> generateInstructors() {
	    return new HashMap<>();
	}

	protected Map<String, StudentAttributes> generateStudents() {
	    return new HashMap<>();
	}

	protected Map<String, FeedbackSessionAttributes> generateFeedbackSessions() {
	    return new HashMap<>();
	}

	protected Map<String, FeedbackQuestionAttributes> generateFeedbackQuestions() {
	    return new HashMap<>();
	}

	protected Map<String, FeedbackResponseAttributes> generateFeedbackResponses() {
	    return new HashMap<>();
	}

	protected Map<String, FeedbackResponseCommentAttributes> generateFeedbackResponseComments() {
	    return new HashMap<>();
	}

	protected Map<String, StudentProfileAttributes> generateProfiles() {
	    return new HashMap<>();
	}

	/**
	 * Returns a JSON data bundle containing the data relevant for the performance test.
	 */
	public DataBundle generateJsonData() {
	    DataBundle dataBundle = new DataBundle();
	
	    dataBundle.accounts = generateAccounts();
	    dataBundle.courses = generateCourses();
	    dataBundle.instructors = generateInstructors();
	    dataBundle.students = generateStudents();
	    dataBundle.feedbackSessions = generateFeedbackSessions();
	    dataBundle.feedbackQuestions = generateFeedbackQuestions();
	    dataBundle.feedbackResponses = generateFeedbackResponses();
	    dataBundle.feedbackResponseComments = generateFeedbackResponseComments();
	    dataBundle.profiles = generateProfiles();
	
	    return dataBundle;
	}
}
