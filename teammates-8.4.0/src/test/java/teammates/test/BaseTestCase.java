package teammates.test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Base class for all test cases.
 */
public abstract class BaseTestCase {

    private static final int VERIFICATION_RETRY_COUNT = 5;
	private static final int VERIFICATION_RETRY_DELAY_IN_MS = 1000;
	private static final int OPERATION_RETRY_COUNT = 5;
	private static final int OPERATION_RETRY_DELAY_IN_MS = 1000;

	/**
     * Test Segment divider. Used to divide a test case into logical sections.
     * The weird name is for easy spotting.
     *
     * @param description
     *            of the logical section. This will be printed.
     */
    // CHECKSTYLE.OFF:AbbreviationAsWordInName|MethodName the weird name is for easy spotting.
    public static void ______TS(String description) {
        print(" * " + description);
    }
    // CHECKSTYLE.ON:AbbreviationAsWordInName|MethodName

    @BeforeClass
    public void printTestClassHeader() {
        print("[============================="
                + getClass().getCanonicalName()
                + "=============================]");
    }

    @AfterClass
    public void printTestClassFooter() {
        print(getClass().getCanonicalName() + " completed");
    }

    protected static void print(String message) {
        System.out.println(message);
    }

    protected String getTestDataFolder() {
        return TestProperties.TEST_DATA_FOLDER;
    }

    /**
     * Creates a DataBundle as specified in typicalDataBundle.json.
     */
    protected DataBundle getTypicalDataBundle() {
        return loadDataBundle("/typicalDataBundle.json");
    }

    public DataBundle loadDataBundle(String jsonFileName) {
        try {
            String pathToJsonFile = getTestDataFolder() + jsonFileName;
            String jsonString = FileHelper.readFile(pathToJsonFile);
            return JsonUtils.fromJson(jsonString, DataBundle.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Populates the feedback question and response IDs within the data bundle.
     *
     * <p>For tests where simulated database is used, the backend will assign the question and response IDs
     * when the entities are persisted into the database, and modify the relation IDs accordingly.
     * However, for tests that do not use simulated database (e.g. pure data structure tests),
     * the assignment of IDs have to be simulated.
     */
    protected void populateQuestionAndResponseIds(DataBundle dataBundle) {
        Map<String, Map<Integer, String>> sessionToQuestionNumberToId = new HashMap<>();

        dataBundle.feedbackQuestions.forEach((key, question) -> {
            // Assign the same ID as the key as a later function requires a match between the key and the question ID
            question.setId(key);
            Map<Integer, String> questionNumberToId = sessionToQuestionNumberToId.computeIfAbsent(
                    question.getCourseId() + "%" + question.getFeedbackSessionName(), k -> new HashMap<>());
            questionNumberToId.put(question.getQuestionNumber(), key);
        });

        dataBundle.feedbackResponses.forEach((key, response) -> {
            response.setId(key);
            String feedbackQuestionId = sessionToQuestionNumberToId
                    .get(response.getCourseId() + "%" + response.getFeedbackSessionName())
                    .get(Integer.valueOf(response.getFeedbackQuestionId()));
            response.setFeedbackQuestionId(feedbackQuestionId);
        });
    }

    protected void verifyPresentInDatabase(DataBundle data) {
	    data.accounts.values().forEach(this::verifyPresentInDatabase);
	
	    data.instructors.values().forEach(this::verifyPresentInDatabase);
	
	    data.courses.values().stream()
	            .filter(course -> !course.isCourseDeleted())
	            .forEach(this::verifyPresentInDatabase);
	
	    data.students.values().forEach(this::verifyPresentInDatabase);
	}

	public void verifyPresentInDatabase(EntityAttributes<?> expected) {
	    int retryLimit = VERIFICATION_RETRY_COUNT;
	    EntityAttributes<?> actual = getEntity(expected);
	    while (actual == null && retryLimit > 0) {
	        retryLimit--;
	        ThreadHelper.waitFor(VERIFICATION_RETRY_DELAY_IN_MS);
	        actual = getEntity(expected);
	    }
	    verifyEquals(expected, actual);
	}

	private EntityAttributes<?> getEntity(EntityAttributes<?> expected) {
	    if (expected instanceof AccountAttributes) {
	        return getAccount((EntityAttributes<Account>) expected);
	
	    } else if (expected instanceof StudentProfileAttributes) {
	        return getStudentProfile((StudentProfileAttributes) expected);
	
	    } else if (expected instanceof CourseAttributes) {
	        return getCourse((CourseAttributes) expected);
	
	    } else if (expected instanceof FeedbackQuestionAttributes) {
	        return getFeedbackQuestion((EntityAttributes<FeedbackQuestion>) expected);
	
	    } else if (expected instanceof FeedbackResponseCommentAttributes) {
	        return getFeedbackResponseComment((FeedbackResponseCommentAttributes) expected);
	
	    } else if (expected instanceof FeedbackResponseAttributes) {
	        return getFeedbackResponse((FeedbackResponseAttributes) expected);
	
	    } else if (expected instanceof FeedbackSessionAttributes) {
	        return getFeedbackSession((FeedbackSessionAttributes) expected);
	
	    } else if (expected instanceof InstructorAttributes) {
	        return getInstructor((InstructorAttributes) expected);
	
	    } else if (expected instanceof StudentAttributes) {
	        return getStudent((StudentAttributes) expected);
	
	    } else {
	        throw new RuntimeException("Unknown entity type!");
	    }
	}

	public void verifyAbsentInDatabase(EntityAttributes<?> entity) {
	    int retryLimit = VERIFICATION_RETRY_COUNT;
	    EntityAttributes<?> actual = getEntity(entity);
	    while (actual != null && retryLimit > 0) {
	        retryLimit--;
	        ThreadHelper.waitFor(VERIFICATION_RETRY_DELAY_IN_MS);
	        actual = getEntity(entity);
	    }
	    assertNull(actual);
	}

	private void verifyEquals(EntityAttributes<?> expected, EntityAttributes<?> actual) {
	    if (expected instanceof AccountAttributes) {
	        EntityAttributes<Account> expectedAccount = ((EntityAttributes<Account>) expected).getCopy();
	        EntityAttributes<Account> actualAccount = (EntityAttributes<Account>) actual;
	        equalizeIrrelevantData(expectedAccount, actualAccount);
	        assertEquals(JsonUtils.toJson(expectedAccount), JsonUtils.toJson(actualAccount));
	
	    } else if (expected instanceof StudentProfileAttributes) {
	        StudentProfileAttributes expectedProfile = ((StudentProfileAttributes) expected).getCopy();
	        StudentProfileAttributes actualProfile = (StudentProfileAttributes) actual;
	        equalizeIrrelevantData(expectedProfile, actualProfile);
	        assertEquals(JsonUtils.toJson(expectedProfile), JsonUtils.toJson(actualProfile));
	
	    } else if (expected instanceof CourseAttributes) {
	        CourseAttributes expectedCourse = (CourseAttributes) expected;
	        CourseAttributes actualCourse = (CourseAttributes) actual;
	        equalizeIrrelevantData(expectedCourse, actualCourse);
	        assertEquals(JsonUtils.toJson(expectedCourse), JsonUtils.toJson(actualCourse));
	
	    } else if (expected instanceof FeedbackQuestionAttributes) {
	        EntityAttributes<FeedbackQuestion> expectedFq = (EntityAttributes<FeedbackQuestion>) expected;
	        EntityAttributes<FeedbackQuestion> actualFq = (EntityAttributes<FeedbackQuestion>) actual;
	        equalizeIrrelevantData(expectedFq, actualFq);
	        assertEquals(JsonUtils.toJson(expectedFq), JsonUtils.toJson(actualFq));
	
	    } else if (expected instanceof FeedbackResponseCommentAttributes) {
	        FeedbackResponseCommentAttributes expectedFrc = (FeedbackResponseCommentAttributes) expected;
	        FeedbackResponseCommentAttributes actualFrc = (FeedbackResponseCommentAttributes) actual;
	        assertEquals(expectedFrc.getCourseId(), actualFrc.getCourseId());
	        assertEquals(expectedFrc.getCommentGiver(), actualFrc.getCommentGiver());
	        assertEquals(expectedFrc.getFeedbackSessionName(), actualFrc.getFeedbackSessionName());
	        assertEquals(expectedFrc.getCommentText(), actualFrc.getCommentText());
	
	    } else if (expected instanceof FeedbackResponseAttributes) {
	        FeedbackResponseAttributes expectedFr = (FeedbackResponseAttributes) expected;
	        FeedbackResponseAttributes actualFr = (FeedbackResponseAttributes) actual;
	        equalizeIrrelevantData(expectedFr, actualFr);
	        assertEquals(JsonUtils.toJson(expectedFr), JsonUtils.toJson(actualFr));
	
	    } else if (expected instanceof FeedbackSessionAttributes) {
	        FeedbackSessionAttributes expectedFs = ((FeedbackSessionAttributes) expected).getCopy();
	        FeedbackSessionAttributes actualFs = (FeedbackSessionAttributes) actual;
	        equalizeIrrelevantData(expectedFs, actualFs);
	        assertEquals(JsonUtils.toJson(expectedFs), JsonUtils.toJson(actualFs));
	
	    } else if (expected instanceof InstructorAttributes) {
	        InstructorAttributes expectedInstructor = ((InstructorAttributes) expected).getCopy();
	        InstructorAttributes actualInstructor = (InstructorAttributes) actual;
	        equalizeIrrelevantData(expectedInstructor, actualInstructor);
	        assertEquals(JsonUtils.toJson(expectedInstructor), JsonUtils.toJson(actualInstructor));
	
	    } else if (expected instanceof StudentAttributes) {
	        StudentAttributes expectedStudent = ((StudentAttributes) expected).getCopy();
	        StudentAttributes actualStudent = (StudentAttributes) actual;
	        equalizeIrrelevantData(expectedStudent, actualStudent);
	        assertEquals(JsonUtils.toJson(expectedStudent), JsonUtils.toJson(actualStudent));
	
	    } else {
	        throw new RuntimeException("Unknown entity type!");
	    }
	}

	protected abstract EntityAttributes<Account> getAccount(EntityAttributes<Account> account);

	private void equalizeIrrelevantData(EntityAttributes<Account> expected, EntityAttributes<Account> actual) {
	    // Ignore time field as it is stamped at the time of creation in testing
	    expected.setCreatedAt(actual.getCreatedAt());
	}

	private void equalizeIrrelevantData(StudentProfileAttributes expected, StudentProfileAttributes actual) {
	    expected.setModifiedDate(actual.getModifiedDate());
	}

	private void equalizeIrrelevantData(CourseAttributes expected, CourseAttributes actual) {
	    // Ignore time field as it is stamped at the time of creation in testing
	    expected.setCreatedAt(actual.getCreatedAt());
	}

	private void equalizeIrrelevantData(EntityAttributes<FeedbackQuestion> expected, EntityAttributes<FeedbackQuestion> actual) {
	    expected.setId(actual.getId());
	}

	private void equalizeIrrelevantData(FeedbackResponseAttributes expected, FeedbackResponseAttributes actual) {
	    expected.setId(actual.getId());
	}

	private void equalizeIrrelevantData(FeedbackSessionAttributes expected, FeedbackSessionAttributes actual) {
	    expected.setCreatedTime(actual.getCreatedTime());
	    // Not available in FeedbackSessionData and thus ignored
	    expected.setCreatorEmail(actual.getCreatorEmail());
	}

	private void equalizeIrrelevantData(InstructorAttributes expected, InstructorAttributes actual) {
	    // pretend keys match because the key is generated only before storing into database
	    if (actual.getKey() != null) {
	        expected.setKey(actual.getKey());
	    }
	}

	private void equalizeIrrelevantData(StudentAttributes expected, StudentAttributes actual) {
	    // For these fields, we consider null and "" equivalent.
	    if (expected.getGoogleId() == null && actual.getGoogleId().isEmpty()) {
	        expected.setGoogleId("");
	    }
	    if (expected.getTeam() == null && actual.getTeam().isEmpty()) {
	        expected.setTeam("");
	    }
	    if (expected.getComments() == null && actual.getComments().isEmpty()) {
	        expected.setComments("");
	    }
	
	    // pretend keys match because the key is generated only before storing into database
	    if (actual.getKey() != null) {
	        expected.setKey(actual.getKey());
	    }
	}

	protected abstract StudentProfileAttributes getStudentProfile(StudentProfileAttributes studentProfileAttributes);

	protected abstract CourseAttributes getCourse(CourseAttributes course);

	protected abstract EntityAttributes<FeedbackQuestion> getFeedbackQuestion(EntityAttributes<FeedbackQuestion> fq);

	protected abstract FeedbackResponseCommentAttributes getFeedbackResponseComment(FeedbackResponseCommentAttributes frc);

	protected abstract FeedbackResponseAttributes getFeedbackResponse(FeedbackResponseAttributes fr);

	protected abstract FeedbackSessionAttributes getFeedbackSession(FeedbackSessionAttributes fs);

	protected abstract InstructorAttributes getInstructor(InstructorAttributes instructor);

	protected abstract StudentAttributes getStudent(StudentAttributes student);

	public void removeAndRestoreDataBundle(DataBundle testData) {
	    int retryLimit = OPERATION_RETRY_COUNT;
	    boolean isOperationSuccess = doRemoveAndRestoreDataBundle(testData);
	    while (!isOperationSuccess && retryLimit > 0) {
	        retryLimit--;
	        print("Re-trying removeAndRestoreDataBundle");
	        ThreadHelper.waitFor(OPERATION_RETRY_DELAY_IN_MS);
	        isOperationSuccess = doRemoveAndRestoreDataBundle(testData);
	    }
	    assertTrue(isOperationSuccess);
	}

	protected abstract boolean doRemoveAndRestoreDataBundle(DataBundle testData);

	protected void putDocuments(DataBundle testData) {
	    int retryLimit = OPERATION_RETRY_COUNT;
	    boolean isOperationSuccess = doPutDocuments(testData);
	    while (!isOperationSuccess && retryLimit > 0) {
	        retryLimit--;
	        print("Re-trying putDocuments");
	        ThreadHelper.waitFor(OPERATION_RETRY_DELAY_IN_MS);
	        isOperationSuccess = doPutDocuments(testData);
	    }
	    assertTrue(isOperationSuccess);
	}

	protected abstract boolean doPutDocuments(DataBundle testData);

	/**
     * Invokes the method named {@code methodName} as defined in the {@code definingClass}.
     * @param definingClass     the class which defines the method
     * @param parameterTypes    the parameter types of the method,
     *                          which must be passed in the same order defined in the method
     * @param invokingObject    the object which invokes the method, can be {@code null} if the method is static
     * @param args              the arguments to be passed to the method invocation
     */
    protected static Object invokeMethod(Class<?> definingClass, String methodName, Class<?>[] parameterTypes,
                                         Object invokingObject, Object[] args)
            throws ReflectiveOperationException {
        Method method = definingClass.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(invokingObject, args);
    }

    protected static String getPopulatedErrorMessage(String messageTemplate, String userInput,
                                                     String fieldName, String errorReason)
            throws ReflectiveOperationException {
        return getPopulatedErrorMessage(messageTemplate, userInput, fieldName, errorReason, 0);
    }

    protected static String getPopulatedErrorMessage(String messageTemplate, String userInput,
                                                     String fieldName, String errorReason, int maxLength)
            throws ReflectiveOperationException {
        return (String) invokeMethod(FieldValidator.class, "getPopulatedErrorMessage",
                                     new Class<?>[] { String.class, String.class, String.class, String.class, int.class },
                                     null, new Object[] { messageTemplate, userInput, fieldName, errorReason, maxLength });
    }

    protected static String getPopulatedEmptyStringErrorMessage(String messageTemplate, String fieldName, int maxLength)
            throws ReflectiveOperationException {
        return (String) invokeMethod(FieldValidator.class, "getPopulatedEmptyStringErrorMessage",
                new Class<?>[] { String.class, String.class, int.class },
                null, new Object[] { messageTemplate, fieldName, maxLength });
    }

    /*
     * Here are some of the most common assertion methods provided by JUnit.
     * They are copied here to prevent repetitive importing in test classes.
     */

    public static void assertTrue(boolean condition) {
        Assert.assertTrue(condition);
    }

    protected static void assertTrue(String message, boolean condition) {
        Assert.assertTrue(message, condition);
    }

    protected static void assertFalse(boolean condition) {
        Assert.assertFalse(condition);
    }

    protected static void assertFalse(String message, boolean condition) {
        Assert.assertFalse(message, condition);
    }

    protected static void assertEquals(int expected, int actual) {
        Assert.assertEquals(expected, actual);
    }

    protected static void assertEquals(String message, int expected, int actual) {
        Assert.assertEquals(message, expected, actual);
    }

    protected static void assertEquals(long expected, long actual) {
        Assert.assertEquals(expected, actual);
    }

    protected static void assertEquals(double expected, double actual, double delta) {
        Assert.assertEquals(expected, actual, delta);
    }

    protected static void assertEquals(Object expected, Object actual) {
        Assert.assertEquals(expected, actual);
    }

    protected static void assertEquals(String message, Object expected, Object actual) {
        Assert.assertEquals(message, expected, actual);
    }

    protected static void assertArrayEquals(byte[] expected, byte[] actual) {
        Assert.assertArrayEquals(expected, actual);
    }

    protected static void assertNotEquals(Object first, Object second) {
        Assert.assertNotEquals(first, second);
    }

    protected static void assertNotSame(Object unexpected, Object actual) {
        Assert.assertNotSame(unexpected, actual);
    }

    protected static void assertNull(Object object) {
        Assert.assertNull(object);
    }

    protected static void assertNull(String message, Object object) {
        Assert.assertNull(message, object);
    }

    protected static void assertNotNull(Object object) {
        Assert.assertNotNull(object);
    }

    protected static void assertNotNull(String message, Object object) {
        Assert.assertNotNull(message, object);
    }

    protected static void fail(String message) {
        Assert.fail(message);
    }

    // This method is adapted from JUnit 5's assertThrows.
    // Once we upgrade to JUnit 5, their built-in method shall be used instead.
    @SuppressWarnings({
            "unchecked",
            "PMD.AvoidCatchingThrowable", // As per reference method's specification
    })
    protected static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable) {
        try {
            executable.execute();
        } catch (Throwable actualException) {
            if (expectedType.isInstance(actualException)) {
                return (T) actualException;
            } else {
                String message = String.format("Expected %s to be thrown, but %s was instead thrown.",
                        getCanonicalName(expectedType), getCanonicalName(actualException.getClass()));
                throw new AssertionError(message, actualException);
            }
        }

        String message = String.format("Expected %s to be thrown, but nothing was thrown.", getCanonicalName(expectedType));
        throw new AssertionError(message);
    }

    private static String getCanonicalName(Class<?> clazz) {
        String canonicalName = clazz.getCanonicalName();
        return canonicalName == null ? clazz.getName() : canonicalName;
    }

    /**
     * {@code Executable} is a functional interface that can be used to
     * implement any generic block of code that potentially throws a
     * {@link Throwable}.
     *
     * <p>The {@code Executable} interface is similar to {@link Runnable},
     * except that an {@code Executable} can throw any kind of exception.
     */
    // This interface is adapted from JUnit 5's Executable interface.
    // Once we upgrade to JUnit 5, this interface shall no longer be necessary.
    public interface Executable {

        /**
         * Executes a block of code, potentially throwing a {@link Throwable}.
         */
        // CHECKSTYLE.OFF:IllegalThrows
        void execute() throws Throwable;
        // CHECKSTYLE.ON:IllegalThrows

    }

}
