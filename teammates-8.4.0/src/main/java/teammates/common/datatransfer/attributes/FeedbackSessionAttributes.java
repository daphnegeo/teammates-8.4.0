package teammates.common.datatransfer.attributes;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.Const.WebPageURIs;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.e2e.cases.BaseE2ETestCase;
import teammates.e2e.cases.FeedbackResultsPageE2ETest;
import teammates.e2e.cases.FeedbackSubmitPageE2ETest;
import teammates.e2e.cases.InstructorAuditLogsPageE2ETest;
import teammates.e2e.pageobjects.FeedbackResultsPage;
import teammates.e2e.pageobjects.FeedbackSubmitPage;
import teammates.e2e.pageobjects.InstructorAuditLogsPage;
import teammates.storage.entity.FeedbackSession;
import teammates.test.BaseTestCase;

/**
 * The data transfer object for {@link FeedbackSession} entities.
 */
public class FeedbackSessionAttributes extends EntityAttributes<FeedbackSession> {

    private String feedbackSessionName;
    private String courseId;

    private String creatorEmail;
    private String instructions;
    private Instant createdTime;
    private Instant deletedTime;
    private Instant startTime;
    private Instant endTime;
    private Instant sessionVisibleFromTime;
    private Instant resultsVisibleFromTime;
    private String timeZone;
    private Duration gracePeriod;
    private boolean sentOpeningSoonEmail;
    private boolean sentOpenEmail;
    private boolean sentClosingEmail;
    private boolean sentClosedEmail;
    private boolean sentPublishedEmail;
    private boolean isOpeningEmailEnabled;
    private boolean isClosingEmailEnabled;
    private boolean isPublishedEmailEnabled;

    private FeedbackSessionAttributes(String feedbackSessionName, String courseId) {
        this.feedbackSessionName = feedbackSessionName;
        this.courseId = courseId;

        this.instructions = "";
        this.createdTime = Instant.now();

        this.isOpeningEmailEnabled = true;
        this.isClosingEmailEnabled = true;
        this.isPublishedEmailEnabled = true;

        this.timeZone = Const.DEFAULT_TIME_ZONE;
        this.gracePeriod = Duration.ZERO;

    }

    /**
     * Gets the {@link FeedbackSessionAttributes} instance of the given {@link FeedbackSession}.
     */
    public static FeedbackSessionAttributes valueOf(FeedbackSession fs) {
        FeedbackSessionAttributes feedbackSessionAttributes =
                new FeedbackSessionAttributes(fs.getFeedbackSessionName(), fs.getCourseId());

        feedbackSessionAttributes.creatorEmail = fs.getCreatorEmail();
        if (fs.getInstructions() != null) {
            feedbackSessionAttributes.instructions = fs.getInstructions();
        }
        feedbackSessionAttributes.createdTime = fs.getCreatedTime();
        feedbackSessionAttributes.deletedTime = fs.getDeletedTime();
        feedbackSessionAttributes.startTime = fs.getStartTime();
        feedbackSessionAttributes.endTime = fs.getEndTime();
        feedbackSessionAttributes.sessionVisibleFromTime = fs.getSessionVisibleFromTime();
        feedbackSessionAttributes.resultsVisibleFromTime = fs.getResultsVisibleFromTime();
        feedbackSessionAttributes.timeZone = fs.getTimeZone();
        feedbackSessionAttributes.gracePeriod = Duration.ofMinutes(fs.getGracePeriod());
        feedbackSessionAttributes.sentOpeningSoonEmail = fs.isSentOpeningSoonEmail();
        feedbackSessionAttributes.sentOpenEmail = fs.isSentOpenEmail();
        feedbackSessionAttributes.sentClosingEmail = fs.isSentClosingEmail();
        feedbackSessionAttributes.sentClosedEmail = fs.isSentClosedEmail();
        feedbackSessionAttributes.sentPublishedEmail = fs.isSentPublishedEmail();
        feedbackSessionAttributes.isOpeningEmailEnabled = fs.isOpeningEmailEnabled();
        feedbackSessionAttributes.isClosingEmailEnabled = fs.isClosingEmailEnabled();
        feedbackSessionAttributes.isPublishedEmailEnabled = fs.isPublishedEmailEnabled();

        return feedbackSessionAttributes;
    }

    /**
     * Returns a builder for {@link FeedbackSessionAttributes}.
     */
    public static Builder builder(String feedbackSessionName, String courseId) {
        return new Builder(feedbackSessionName, courseId);
    }

    /**
     * Gets a deep copy of this object.
     */
    public FeedbackSessionAttributes getCopy() {
        return valueOf(toEntity());
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    /**
     * Gets the instructions of the feedback session.
     */
    public String getInstructionsString() {
        if (instructions == null) {
            return null;
        }

        return SanitizationHelper.sanitizeForRichText(instructions);
    }

    @Override
    public FeedbackSession toEntity() {
        return new FeedbackSession(feedbackSessionName, courseId, creatorEmail, instructions,
                createdTime, deletedTime, startTime, endTime, sessionVisibleFromTime, resultsVisibleFromTime,
                timeZone, getGracePeriodMinutes(),
                sentOpeningSoonEmail, sentOpenEmail, sentClosingEmail, sentClosedEmail, sentPublishedEmail,
                isOpeningEmailEnabled, isClosingEmailEnabled, isPublishedEmailEnabled);
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        // Check for null fields.

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField(
                FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME, feedbackSessionName), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField(
                FieldValidator.COURSE_ID_FIELD_NAME, courseId), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("instructions to students", instructions), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField(
                "time for the session to become visible", sessionVisibleFromTime), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("session time zone", timeZone), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("creator's email", creatorEmail), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("session creation time", createdTime), errors);

        // Early return if any null fields
        if (!errors.isEmpty()) {
            return errors;
        }

        addNonEmptyError(FieldValidator.getInvalidityInfoForFeedbackSessionName(feedbackSessionName), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForCourseId(courseId), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(creatorEmail), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForGracePeriod(gracePeriod), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("submission opening time", startTime), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("submission closing time", endTime), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField(
                "time for the responses to become visible", resultsVisibleFromTime), errors);

        // Early return if any null fields
        if (!errors.isEmpty()) {
            return errors;
        }

        addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForSessionStartAndEnd(startTime, endTime), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForVisibilityStartAndSessionStart(
                sessionVisibleFromTime, startTime), errors);

        Instant actualSessionVisibleFromTime = sessionVisibleFromTime;

        if (actualSessionVisibleFromTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            actualSessionVisibleFromTime = startTime;
        }

        addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForVisibilityStartAndResultsPublish(
                actualSessionVisibleFromTime, resultsVisibleFromTime), errors);

        return errors;
    }

    /**
     * Returns true if session's start time is opening from now to anytime before
     * now() + the specific number of {@param hours} supplied in the argument.
     */
    public boolean isOpeningInHours(long hours) {
        return startTime.isAfter(Instant.now())
                && Instant.now().plus(Duration.ofHours(hours)).isAfter(startTime);
    }

    /**
     * Returns true if the feedback session is closed after the number of specified hours.
     */
    public boolean isClosedAfter(long hours) {
        return Instant.now().plus(Duration.ofHours(hours)).isAfter(endTime);
    }

    /**
     * Returns true if the feedback session is closing (almost closed) after the number of specified hours.
     */
    public boolean isClosingWithinTimeLimit(long hours) {
        Instant now = Instant.now();
        Duration difference = Duration.between(now, endTime);
        // If now and start are almost similar, it means the feedback session
        // is open for only 24 hours.
        // Hence we do not send a reminder e-mail for feedback session.
        return now.isAfter(startTime)
               && difference.compareTo(Duration.ofHours(hours - 1)) >= 0
               && difference.compareTo(Duration.ofHours(hours)) < 0;
    }

    /**
     * Returns true if the feedback session opens after the number of specified hours.
     */
    public boolean isOpeningWithinTimeLimit(long hours) {
        Instant now = Instant.now();
        Duration difference = Duration.between(now, startTime);

        return now.isBefore(startTime)
                && difference.compareTo(Duration.ofHours(hours - 1)) >= 0
                && difference.compareTo(Duration.ofHours(hours)) < 0;
    }

    /**
     * Checks if the session closed some time in the last one hour from calling this function.
     *
     * @return true if the session closed within the past hour; false otherwise.
     */
    public boolean isClosedWithinPastHour() {
        Instant now = Instant.now();
        Instant given = endTime.plus(gracePeriod);
        return given.isBefore(now) && Duration.between(given, now).compareTo(Duration.ofHours(1)) < 0;
    }

    /**
     * Returns {@code true} if it is after the closing time of this feedback session; {@code false} if not.
     */
    public boolean isClosed() {
        return Instant.now().isAfter(endTime.plus(gracePeriod));
    }

    /**
     * Returns true if the session is currently open and accepting responses.
     */
    public boolean isOpened() {
        Instant now = Instant.now();
        return (now.isAfter(startTime) || now.equals(startTime)) && now.isBefore(endTime);
    }

    /**
     * Returns true if the session is currently close but is still accept responses.
     */
    public boolean isInGracePeriod() {
        Instant now = Instant.now();
        Instant gracedEnd = endTime.plus(gracePeriod);
        return (now.isAfter(endTime) || now.equals(endTime)) && (now.isBefore(gracedEnd) || now.equals(gracedEnd));
    }

    /**
     * Returns {@code true} has not opened before and is waiting to open,
     * {@code false} if session has opened before.
     */
    public boolean isWaitingToOpen() {
        return Instant.now().isBefore(startTime);
    }

    /**
     * Returns {@code true} if the session is visible; {@code false} if not.
     *         Does not care if the session has started or not.
     */
    public boolean isVisible() {
        Instant visibleTime = this.sessionVisibleFromTime;

        if (visibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            visibleTime = this.startTime;
        }

        Instant now = Instant.now();
        return now.isAfter(visibleTime) || now.equals(visibleTime);
    }

    /**
     * Returns {@code true} if the results of the feedback session is visible; {@code false} if not.
     *         Does not care if the session has ended or not.
     */
    public boolean isPublished() {
        Instant publishTime = this.resultsVisibleFromTime;

        if (publishTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
            return isVisible();
        }
        if (publishTime.equals(Const.TIME_REPRESENTS_LATER)) {
            return false;
        }
        if (publishTime.equals(Const.TIME_REPRESENTS_NOW)) {
            return true;
        }

        Instant now = Instant.now();
        return now.isAfter(publishTime) || now.equals(publishTime);
    }

    /**
     * Returns true if the given email is the same as the creator email of the feedback session.
     */
    public boolean isCreator(String instructorEmail) {
        return creatorEmail.equals(instructorEmail);
    }

    @Override
    public void sanitizeForSaving() {
        this.instructions = SanitizationHelper.sanitizeForRichText(instructions);
    }

    @Override
    public String toString() {
        return "FeedbackSessionAttributes [feedbackSessionName="
               + feedbackSessionName + ", courseId=" + courseId
               + ", creatorEmail=" + creatorEmail + ", instructions=" + instructions
               + ", createdTime=" + createdTime + ", deletedTime=" + deletedTime
               + ", startTime=" + startTime
               + ", endTime=" + endTime + ", sessionVisibleFromTime="
               + sessionVisibleFromTime + ", resultsVisibleFromTime="
               + resultsVisibleFromTime + ", timeZone=" + timeZone
               + ", gracePeriod=" + getGracePeriodMinutes() + "min"
               + ", sentOpeningSoonEmail=" + sentOpeningSoonEmail
               + ", sentOpenEmail=" + sentOpenEmail
               + ", sentClosingEmail=" + sentClosingEmail
               + ", sentClosedEmail=" + sentClosedEmail
               + ", sentPublishedEmail=" + sentPublishedEmail
               + ", isOpeningEmailEnabled=" + isOpeningEmailEnabled
               + ", isClosingEmailEnabled=" + isClosingEmailEnabled
               + ", isPublishedEmailEnabled=" + isPublishedEmailEnabled
               + "]";
    }

    @Override
    public int hashCode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.feedbackSessionName).append(this.courseId)
                .append(this.instructions).append(this.creatorEmail);
        return stringBuilder.toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            FeedbackSessionAttributes otherFeedbackSession = (FeedbackSessionAttributes) other;
            return Objects.equals(this.feedbackSessionName, otherFeedbackSession.feedbackSessionName)
                    && Objects.equals(this.courseId, otherFeedbackSession.courseId)
                    && Objects.equals(this.instructions, otherFeedbackSession.instructions)
                    && Objects.equals(this.creatorEmail, otherFeedbackSession.creatorEmail);
        } else {
            return false;
        }
    }

    public void setFeedbackSessionName(String feedbackSessionName) {
        this.feedbackSessionName = feedbackSessionName;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Instant getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Instant createdTime) {
        this.createdTime = createdTime;
    }

    public Instant getDeletedTime() {
        return deletedTime;
    }

    public void setDeletedTime(Instant deletedTime) {
        this.deletedTime = deletedTime;
    }

    public boolean isSessionDeleted() {
        return this.deletedTime != null;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Instant getSessionVisibleFromTime() {
        return sessionVisibleFromTime;
    }

    public void setSessionVisibleFromTime(Instant sessionVisibleFromTime) {
        this.sessionVisibleFromTime = sessionVisibleFromTime;
    }

    public Instant getResultsVisibleFromTime() {
        return resultsVisibleFromTime;
    }

    public void setResultsVisibleFromTime(Instant resultsVisibleFromTime) {
        this.resultsVisibleFromTime = resultsVisibleFromTime;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public long getGracePeriodMinutes() {
        return gracePeriod.toMinutes();
    }

    public void setGracePeriodMinutes(long gracePeriodMinutes) {
        this.gracePeriod = Duration.ofMinutes(gracePeriodMinutes);
    }

    public boolean isSentOpeningSoonEmail() {
        return sentOpeningSoonEmail;
    }

    public void setSentOpeningSoonEmail(boolean sentOpeningSoonEmail) {
        this.sentOpeningSoonEmail = sentOpeningSoonEmail;
    }

    public boolean isSentOpenEmail() {
        return sentOpenEmail;
    }

    public void setSentOpenEmail(boolean sentOpenEmail) {
        this.sentOpenEmail = sentOpenEmail;
    }

    public boolean isSentClosingEmail() {
        return sentClosingEmail;
    }

    public void setSentClosingEmail(boolean sentClosingEmail) {
        this.sentClosingEmail = sentClosingEmail;
    }

    public boolean isSentClosedEmail() {
        return sentClosedEmail;
    }

    public void setSentClosedEmail(boolean sentClosedEmail) {
        this.sentClosedEmail = sentClosedEmail;
    }

    public boolean isSentPublishedEmail() {
        return sentPublishedEmail;
    }

    public void setSentPublishedEmail(boolean sentPublishedEmail) {
        this.sentPublishedEmail = sentPublishedEmail;
    }

    public boolean isOpeningEmailEnabled() {
        return isOpeningEmailEnabled;
    }

    public void setOpeningEmailEnabled(boolean isOpeningEmailEnabled) {
        this.isOpeningEmailEnabled = isOpeningEmailEnabled;
    }

    public boolean isClosingEmailEnabled() {
        return isClosingEmailEnabled;
    }

    public void setClosingEmailEnabled(boolean isClosingEmailEnabled) {
        this.isClosingEmailEnabled = isClosingEmailEnabled;
    }

    public boolean isPublishedEmailEnabled() {
        return isPublishedEmailEnabled;
    }

    public void setPublishedEmailEnabled(boolean isPublishedEmailEnabled) {
        this.isPublishedEmailEnabled = isPublishedEmailEnabled;
    }

    /**
     * Updates with {@link UpdateOptions}.
     */
    public void update(UpdateOptions updateOptions) {
        updateOptions.instructionsOption.ifPresent(s -> instructions = s);
        updateOptions.startTimeOption.ifPresent(s -> startTime = s);
        updateOptions.endTimeOption.ifPresent(s -> endTime = s);
        updateOptions.sessionVisibleFromTimeOption.ifPresent(s -> sessionVisibleFromTime = s);
        updateOptions.resultsVisibleFromTimeOption.ifPresent(s -> resultsVisibleFromTime = s);
        updateOptions.timeZoneOption.ifPresent(s -> timeZone = s);
        updateOptions.gracePeriodOption.ifPresent(s -> gracePeriod = s);
        updateOptions.sentOpeningSoonEmailOption.ifPresent(s -> sentOpeningSoonEmail = s);
        updateOptions.sentOpenEmailOption.ifPresent(s -> sentOpenEmail = s);
        updateOptions.sentClosingEmailOption.ifPresent(s -> sentClosingEmail = s);
        updateOptions.sentClosedEmailOption.ifPresent(s -> sentClosedEmail = s);
        updateOptions.sentPublishedEmailOption.ifPresent(s -> sentPublishedEmail = s);
        updateOptions.isClosingEmailEnabledOption.ifPresent(s -> isClosingEmailEnabled = s);
        updateOptions.isPublishedEmailEnabledOption.ifPresent(s -> isPublishedEmailEnabled = s);
    }

    /**
	 * @param feedbackResultsPageE2ETest TODO
	 * 
	 */
	public void testAllTSmethod(FeedbackResultsPageE2ETest feedbackResultsPageE2ETest) {
		BaseTestCase.______TS("unregistered student: can access results");
	    StudentAttributes unregistered = feedbackResultsPageE2ETest.testData.students.get("Unregistered");
	    AppUrl url = BaseE2ETestCase.createUrl(WebPageURIs.SESSION_RESULTS_PAGE)
	            .withCourseId(unregistered.getCourse())
	            .withStudentEmail(unregistered.getEmail())
	            .withSessionName(getFeedbackSessionName())
	            .withRegistrationKey(feedbackResultsPageE2ETest.getKeyForStudent(unregistered));
	    feedbackResultsPageE2ETest.resultsPage = feedbackResultsPageE2ETest.getNewPageInstance(url, FeedbackResultsPage.class);
	
	    feedbackResultsPageE2ETest.resultsPage.verifyFeedbackSessionDetails(this);
	
	    BaseTestCase.______TS("unregistered student: questions with responses loaded");
	    feedbackResultsPageE2ETest.verifyLoadedQuestions(unregistered);
	
	    BaseTestCase.______TS("registered student: can access results");
	    StudentAttributes student = feedbackResultsPageE2ETest.testData.students.get("Alice");
	    url = BaseE2ETestCase.createUrl(WebPageURIs.STUDENT_SESSION_RESULTS_PAGE)
	            .withCourseId(getCourseId())
	            .withSessionName(getFeedbackSessionName());
	    feedbackResultsPageE2ETest.resultsPage = feedbackResultsPageE2ETest.loginToPage(url, FeedbackResultsPage.class, student.getGoogleId());
	
	    feedbackResultsPageE2ETest.resultsPage.verifyFeedbackSessionDetails(this);
	
	    BaseTestCase.______TS("registered student: questions with responses loaded");
	    feedbackResultsPageE2ETest.verifyLoadedQuestions(student);
	
	    BaseTestCase.______TS("verify responses");
	    feedbackResultsPageE2ETest.questions.forEach(question -> feedbackResultsPageE2ETest.verifyResponseDetails(student, question));
	
	    BaseTestCase.______TS("verify statistics - numscale");
	    String[] expectedNumScaleStats = { student.getTeam(), "You", "3.83", "4.5", "3", "3.5" };
	
	    feedbackResultsPageE2ETest.resultsPage.verifyNumScaleStatistics(5, expectedNumScaleStats);
	
	    BaseTestCase.______TS("verify statistics - rubric");
	    feedbackResultsPageE2ETest.verifyExpectedRubricStats();
	
	    BaseTestCase.______TS("verify statistics - contribution");
	    String[] expectedContribStats = {
	            "of me: E +20%",
	            "of others:  E +50%, E -50%",
	            "of me: E +71%",
	            "of others:  E -20%, E -31%",
	    };
	
	    feedbackResultsPageE2ETest.resultsPage.verifyContributionStatistics(11, expectedContribStats);
	
	    BaseTestCase.______TS("verify comments");
	    feedbackResultsPageE2ETest.verifyCommentDetails(2, feedbackResultsPageE2ETest.testData.feedbackResponseComments.get("qn2Comment1"), student);
	    feedbackResultsPageE2ETest.verifyCommentDetails(2, feedbackResultsPageE2ETest.testData.feedbackResponseComments.get("qn2Comment2"), student);
	    feedbackResultsPageE2ETest.verifyCommentDetails(3, feedbackResultsPageE2ETest.testData.feedbackResponseComments.get("qn3Comment1"), student);
	    feedbackResultsPageE2ETest.verifyCommentDetails(3, feedbackResultsPageE2ETest.testData.feedbackResponseComments.get("qn3Comment2"), student);
	
	    BaseTestCase.______TS("registered instructor: can access results");
	    feedbackResultsPageE2ETest.logout();
	    InstructorAttributes instructor = feedbackResultsPageE2ETest.testData.instructors.get("FRes.instr");
	    url = BaseE2ETestCase.createUrl(WebPageURIs.INSTRUCTOR_SESSION_RESULTS_PAGE)
	            .withCourseId(getCourseId())
	            .withSessionName(getFeedbackSessionName());
	    feedbackResultsPageE2ETest.resultsPage = feedbackResultsPageE2ETest.loginToPage(url, FeedbackResultsPage.class, instructor.getGoogleId());
	
	    feedbackResultsPageE2ETest.resultsPage.verifyFeedbackSessionDetails(this);
	
	    BaseTestCase.______TS("registered instructor: questions with responses loaded");
	    feedbackResultsPageE2ETest.verifyLoadedQuestions(instructor);
	
	    BaseTestCase.______TS("verify responses");
	    feedbackResultsPageE2ETest.questions.forEach(question -> feedbackResultsPageE2ETest.verifyResponseDetails(instructor, question));
	}

	/**
	 * @return
	 */
	public List<FeedbackResponseAttributes> otherResponsesMethod() {
		List<FeedbackResponseAttributes> otherResponses = new ArrayList<>();
		return otherResponses;
	}

	public Set<StudentAttributes> getOtherTeammates(FeedbackResultsPageE2ETest feedbackResultsPageE2ETest, StudentAttributes currentStudent) {
	    return feedbackResultsPageE2ETest.testData.students.values().stream()
	            .filter(s -> s.getTeam().equals(currentStudent.getTeam())
	            && !s.equals(currentStudent))
	            .collect(Collectors.toSet());
	}

	public Set<StudentAttributes> getOtherStudents(FeedbackResultsPageE2ETest feedbackResultsPageE2ETest, StudentAttributes currentStudent) {
	    return feedbackResultsPageE2ETest.testData.students.values().stream()
	            .filter(s -> s.getCourse().equals(currentStudent.getCourse())
	            && !s.equals(currentStudent))
	            .collect(Collectors.toSet());
	}

	/**
	 * @param feedbackResultsPageE2ETest TODO
	 * @param currentInstructor
	 * @param user
	 * @return
	 */
	public String identifierMethod(FeedbackResultsPageE2ETest feedbackResultsPageE2ETest, InstructorAttributes currentInstructor, String user) {
		if (currentInstructor.getEmail().equals(user)) {
	        return "You";
	    }
	    if (Const.GENERAL_QUESTION.equals(user)) {
	        return Const.USER_NOBODY_TEXT;
	    }
	    String identifier = feedbackResultsPageE2ETest.getInstructorName(user);
	    if (identifier == null) {
	        identifier = feedbackResultsPageE2ETest.getStudentName(user);
	    }
	    if (identifier == null) {
	        identifier = user;
	    }
	    return identifier;
	}

	/**
	 * @param feedbackResultsPageE2ETest TODO
	 * @param subQns
	 */
	public void statsList(FeedbackResultsPageE2ETest feedbackResultsPageE2ETest, List<String> subQns) {
		String[] formattedSubQns = { "a) " + subQns.get(0), "b) " + subQns.get(1), "c) " + subQns.get(2) };
	
	    String[][] expectedRubricStats = {
	            {
	                    formattedSubQns[0],
	                    "33.33% (1) [1]",
	                    "33.33% (1) [2]",
	                    "0% (0) [3]",
	                    "0% (0) [4]",
	                    "33.33% (1) [5]",
	                    "2.67",
	            },
	            {
	                    formattedSubQns[1],
	                    "0% (0) [0.01]",
	                    "0% (0) [0.02]",
	                    "33.33% (1) [0.03]",
	                    "0% (0) [0.04]",
	                    "66.67% (2) [0.05]",
	                    "0.04",
	            },
	            {
	                    formattedSubQns[2],
	                    "0% (0) [2]",
	                    "0% (0) [1]",
	                    "0% (0) [0]",
	                    "66.67% (2) [-1]",
	                    "33.33% (1) [-2]",
	                    "-1.33",
	            },
	    };
	
	    String[][] expectedRubricStatsExcludingSelf = {
	            {
	                    formattedSubQns[0],
	                    "50% (1) [1]",
	                    "0% (0) [2]",
	                    "0% (0) [3]",
	                    "0% (0) [4]",
	                    "50% (1) [5]",
	                    "3",
	            },
	            {
	                    formattedSubQns[1],
	                    "0% (0) [0.01]",
	                    "0% (0) [0.02]",
	                    "0% (0) [0.03]",
	                    "0% (0) [0.04]",
	                    "100% (2) [0.05]",
	                    "0.05",
	            },
	            {
	                    formattedSubQns[2],
	                    "0% (0) [2]",
	                    "0% (0) [1]",
	                    "0% (0) [0]",
	                    "50% (1) [-1]",
	                    "50% (1) [-2]",
	                    "-1.5",
	            },
	    };
	
	    String[] studentNames = { "Anonymous student", "Benny Charles", "Charlie Davis", "You" };
	    String[] studentTeams = { "", "Team 1", "Team 1", "Team 1" };
	
	    String[][] expectedRubricStatsPerRecipient = new String[studentNames.length * formattedSubQns.length][3];
	    // The actual calculated stats are not verified for this table
	    // Checking the recipient presence in the table is sufficient for E2E purposes
	    for (int i = 0; i < studentNames.length; i++) {
	        for (int j = 0; j < formattedSubQns.length; j++) {
	            int index = i * formattedSubQns.length + j;
	            expectedRubricStatsPerRecipient[index][0] = studentTeams[i];
	            expectedRubricStatsPerRecipient[index][1] = studentNames[i];
	            expectedRubricStatsPerRecipient[index][2] = formattedSubQns[j];
	        }
	    }
	
	    feedbackResultsPageE2ETest.resultsPage.verifyRubricStatistics(10, expectedRubricStats, expectedRubricStatsExcludingSelf,
	            expectedRubricStatsPerRecipient);
	}

	@Test
	public void testAll(FeedbackSubmitPageE2ETest feedbackSubmitPageE2ETest) {
	    AppUrl url = BaseE2ETestCase.createUrl(WebPageURIs.INSTRUCTOR_SESSION_SUBMISSION_PAGE)
	            .withCourseId(feedbackSubmitPageE2ETest.openSession.getCourseId())
	            .withSessionName(feedbackSubmitPageE2ETest.openSession.getFeedbackSessionName());
	    FeedbackSubmitPage submitPage = feedbackSubmitPageE2ETest.loginToPage(url, FeedbackSubmitPage.class, feedbackSubmitPageE2ETest.instructor.getGoogleId());
	
	    BaseTestCase.______TS("verify loaded session data");
	    submitPage.verifyFeedbackSessionDetails(feedbackSubmitPageE2ETest.openSession);
	
	    BaseTestCase.______TS("questions with giver type instructor");
	    submitPage.verifyNumQuestions(1);
	    submitPage.verifyQuestionDetails(1, feedbackSubmitPageE2ETest.testData.feedbackQuestions.get("qn5InSession1"));
	
	    BaseTestCase.______TS("questions with giver type students");
	    feedbackSubmitPageE2ETest.logout();
	    submitPage = feedbackSubmitPageE2ETest.loginToPage(feedbackSubmitPageE2ETest.getStudentSubmitPageUrl(feedbackSubmitPageE2ETest.student, feedbackSubmitPageE2ETest.openSession), FeedbackSubmitPage.class,
	            feedbackSubmitPageE2ETest.student.getGoogleId());
	
	    submitPage.verifyNumQuestions(4);
	    submitPage.verifyQuestionDetails(1, feedbackSubmitPageE2ETest.testData.feedbackQuestions.get("qn1InSession1"));
	    submitPage.verifyQuestionDetails(2, feedbackSubmitPageE2ETest.testData.feedbackQuestions.get("qn2InSession1"));
	    submitPage.verifyQuestionDetails(3, feedbackSubmitPageE2ETest.testData.feedbackQuestions.get("qn3InSession1"));
	    submitPage.verifyQuestionDetails(4, feedbackSubmitPageE2ETest.testData.feedbackQuestions.get("qn4InSession1"));
	
	    BaseTestCase.______TS("verify recipients: students");
	    submitPage.verifyLimitedRecipients(1, 3, feedbackSubmitPageE2ETest.getOtherStudents(feedbackSubmitPageE2ETest.student));
	
	    BaseTestCase.______TS("verify recipients: instructors");
	    submitPage.verifyRecipients(2, feedbackSubmitPageE2ETest.getInstructors(), "Instructor");
	
	    BaseTestCase.______TS("verify recipients: team mates");
	    submitPage.verifyRecipients(3, feedbackSubmitPageE2ETest.getTeammates(feedbackSubmitPageE2ETest.student), "Student");
	
	    BaseTestCase.______TS("verify recipients: teams");
	    submitPage.verifyRecipients(4, feedbackSubmitPageE2ETest.getOtherTeams(feedbackSubmitPageE2ETest.student), "Team");
	
	    BaseTestCase.______TS("submit partial response");
	    int[] unansweredQuestions = { 1, 2, 3, 4 };
	    submitPage.verifyWarningMessageForPartialResponse(unansweredQuestions);
	
	    BaseTestCase.______TS("cannot submit in closed session");
	    AppUrl closedSessionUrl = feedbackSubmitPageE2ETest.getStudentSubmitPageUrl(feedbackSubmitPageE2ETest.student, this);
	    submitPage = feedbackSubmitPageE2ETest.getNewPageInstance(closedSessionUrl, FeedbackSubmitPage.class);
	    submitPage.verifyCannotSubmit();
	
	    BaseTestCase.______TS("can submit in grace period");
	    AppUrl gracePeriodSessionUrl = feedbackSubmitPageE2ETest.getStudentSubmitPageUrl(feedbackSubmitPageE2ETest.student, feedbackSubmitPageE2ETest.gracePeriodSession);
	    submitPage = feedbackSubmitPageE2ETest.getNewPageInstance(gracePeriodSessionUrl, FeedbackSubmitPage.class);
	    FeedbackQuestionsVariousAttributes question = feedbackSubmitPageE2ETest.testData.feedbackQuestions.get("qn1InGracePeriodSession");
	    String questionId = feedbackSubmitPageE2ETest.getFeedbackQuestion(question).getId();
	    String recipient = "Team 2";
	    FeedbackResponseAttributes response = feedbackSubmitPageE2ETest.getMcqResponse(questionId, recipient, false, "UI");
	    submitPage.submitMcqResponse(1, recipient, response);
	
	    feedbackSubmitPageE2ETest.verifyPresentInDatabase(response);
	
	    BaseTestCase.______TS("add comment");
	    String responseId = feedbackSubmitPageE2ETest.getFeedbackResponse(response).getId();
	    int qnToComment = 1;
	    String comment = "<p>new comment</p>";
	    submitPage.addComment(qnToComment, recipient, comment);
	
	    submitPage.verifyComment(qnToComment, recipient, comment);
	    feedbackSubmitPageE2ETest.verifyPresentInDatabase(feedbackSubmitPageE2ETest.getFeedbackResponseComment(responseId, comment));
	
	    BaseTestCase.______TS("edit comment");
	    comment = "<p>edited comment</p>";
	    submitPage.editComment(qnToComment, recipient, comment);
	
	    submitPage.verifyComment(qnToComment, recipient, comment);
	    feedbackSubmitPageE2ETest.verifyPresentInDatabase(feedbackSubmitPageE2ETest.getFeedbackResponseComment(responseId, comment));
	
	    BaseTestCase.______TS("delete comment");
	    submitPage.deleteComment(qnToComment, recipient);
	
	    submitPage.verifyStatusMessage("Your comment has been deleted!");
	    submitPage.verifyNoCommentPresent(qnToComment, recipient);
	    feedbackSubmitPageE2ETest.verifyAbsentInDatabase(feedbackSubmitPageE2ETest.getFeedbackResponseComment(responseId, comment));
	
	    BaseTestCase.______TS("preview as instructor");
	    feedbackSubmitPageE2ETest.logout();
	    url = BaseE2ETestCase.createUrl(WebPageURIs.INSTRUCTOR_SESSION_SUBMISSION_PAGE)
	            .withCourseId(feedbackSubmitPageE2ETest.openSession.getCourseId())
	            .withSessionName(feedbackSubmitPageE2ETest.openSession.getFeedbackSessionName())
	            .withParam("previewas", feedbackSubmitPageE2ETest.instructor.getEmail());
	    submitPage = feedbackSubmitPageE2ETest.loginToPage(url, FeedbackSubmitPage.class, feedbackSubmitPageE2ETest.instructor.getGoogleId());
	
	    submitPage.verifyFeedbackSessionDetails(feedbackSubmitPageE2ETest.openSession);
	    submitPage.verifyNumQuestions(1);
	    submitPage.verifyQuestionDetails(1, feedbackSubmitPageE2ETest.testData.feedbackQuestions.get("qn5InSession1"));
	    submitPage.verifyCannotSubmit();
	
	    BaseTestCase.______TS("preview as student");
	    url = BaseE2ETestCase.createUrl(WebPageURIs.SESSION_SUBMISSION_PAGE)
	            .withCourseId(feedbackSubmitPageE2ETest.openSession.getCourseId())
	            .withSessionName(feedbackSubmitPageE2ETest.openSession.getFeedbackSessionName())
	            .withParam("previewas", feedbackSubmitPageE2ETest.student.getEmail());
	    submitPage = feedbackSubmitPageE2ETest.getNewPageInstance(url, FeedbackSubmitPage.class);
	
	    submitPage.verifyFeedbackSessionDetails(feedbackSubmitPageE2ETest.openSession);
	    submitPage.verifyNumQuestions(4);
	    submitPage.verifyQuestionDetails(1, feedbackSubmitPageE2ETest.testData.feedbackQuestions.get("qn1InSession1"));
	    submitPage.verifyQuestionDetails(2, feedbackSubmitPageE2ETest.testData.feedbackQuestions.get("qn2InSession1"));
	    submitPage.verifyQuestionDetails(3, feedbackSubmitPageE2ETest.testData.feedbackQuestions.get("qn3InSession1"));
	    submitPage.verifyQuestionDetails(4, feedbackSubmitPageE2ETest.testData.feedbackQuestions.get("qn4InSession1"));
	    submitPage.verifyCannotSubmit();
	
	    BaseTestCase.______TS("moderating instructor cannot see questions without instructor visibility");
	    url = BaseE2ETestCase.createUrl(WebPageURIs.SESSION_SUBMISSION_PAGE)
	            .withCourseId(feedbackSubmitPageE2ETest.gracePeriodSession.getCourseId())
	            .withSessionName(feedbackSubmitPageE2ETest.gracePeriodSession.getFeedbackSessionName())
	            .withParam("moderatedperson", feedbackSubmitPageE2ETest.student.getEmail())
	            .withParam("moderatedquestionId", questionId);
	    submitPage = feedbackSubmitPageE2ETest.getNewPageInstance(url, FeedbackSubmitPage.class);
	
	    submitPage.verifyFeedbackSessionDetails(feedbackSubmitPageE2ETest.gracePeriodSession);
	    // One out of two questions in grace period session should not be visible
	    submitPage.verifyNumQuestions(1);
	    submitPage.verifyQuestionDetails(1, question);
	
	    BaseTestCase.______TS("submit moderated response");
	    response = feedbackSubmitPageE2ETest.getMcqResponse(questionId, recipient, false, "Algo");
	    submitPage.submitMcqResponse(1, recipient, response);
	
	    feedbackSubmitPageE2ETest.verifyPresentInDatabase(response);
	}

	@Test
	public void testAll(InstructorAuditLogsPageE2ETest instructorAuditLogsPageE2ETest) {
	    AppUrl url = BaseE2ETestCase.createUrl(WebPageURIs.INSTRUCTOR_AUDIT_LOGS_PAGE);
	    InstructorAuditLogsPage auditLogsPage = instructorAuditLogsPageE2ETest.loginToPage(url, InstructorAuditLogsPage.class, instructorAuditLogsPageE2ETest.instructor.getGoogleId());
	
	    BaseTestCase.______TS("verify default datetime");
	    String currentLogsFromDate = auditLogsPage.getLogsFromDate();
	    String currentLogsToDate = auditLogsPage.getLogsToDate();
	    String currentLogsFromTime = auditLogsPage.getLogsFromTime();
	    String currentLogsToTime = auditLogsPage.getLogsToTime();
	
	    auditLogsPage.setLogsFromDateTime(Instant.now().minus(1, ChronoUnit.DAYS),
	            ZoneId.systemDefault().getId());
	    auditLogsPage.setLogsToDateTime(Instant.now(), ZoneId.systemDefault().getId());
	
	    BaseTestCase.assertEquals(currentLogsFromDate, auditLogsPage.getLogsFromDate());
	    BaseTestCase.assertEquals(currentLogsToDate, auditLogsPage.getLogsToDate());
	    BaseTestCase.assertEquals(currentLogsFromTime, "23:59H");
	    BaseTestCase.assertEquals(currentLogsToTime, "23:59H");
	
	    BaseTestCase.______TS("verify logs output");
	    instructorAuditLogsPageE2ETest.logout();
	    AppUrl studentSubmissionPageUrl = BaseE2ETestCase.createUrl(WebPageURIs.STUDENT_SESSION_SUBMISSION_PAGE)
	            .withCourseId(instructorAuditLogsPageE2ETest.course.getId())
	            .withSessionName(getFeedbackSessionName());
	    FeedbackSubmitPage studentSubmissionPage = instructorAuditLogsPageE2ETest.loginToPage(studentSubmissionPageUrl,
	            FeedbackSubmitPage.class, instructorAuditLogsPageE2ETest.student.getGoogleId());
	
	    StudentAttributes receiver = instructorAuditLogsPageE2ETest.testData.students.get("benny.tmms@IAuditLogs.CS2104");
	    FeedbackQuestionsVariousAttributes question = instructorAuditLogsPageE2ETest.testData.feedbackQuestions.get("qn1");
	    String questionId = instructorAuditLogsPageE2ETest.getFeedbackQuestion(question).getId();
	    FeedbackTextResponseDetails details = new FeedbackTextResponseDetails("Response");
	    FeedbackResponseAttributes response =
	            FeedbackResponseAttributes.builder(questionId, instructorAuditLogsPageE2ETest.student.getEmail(), instructorAuditLogsPageE2ETest.instructor.getEmail())
	                    .withResponseDetails(details)
	                    .build();
	
	    studentSubmissionPage.submitTextResponse(1, receiver.getName(), response);
	
	    instructorAuditLogsPageE2ETest.logout();
	    auditLogsPage = instructorAuditLogsPageE2ETest.loginToPage(url, InstructorAuditLogsPage.class, instructorAuditLogsPageE2ETest.instructor.getGoogleId());
	    auditLogsPage.setCourseId(instructorAuditLogsPageE2ETest.course.getId());
	    auditLogsPage.startSearching();
	
	    BaseTestCase.assertTrue(auditLogsPage.isLogPresentForSession(instructorAuditLogsPageE2ETest.feedbackQuestion.getFeedbackSessionName()));
	}

	/**
     * Returns a {@link UpdateOptions.Builder} to build {@link UpdateOptions} for a session.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(String feedbackSessionName, String courseId) {
        return new UpdateOptions.Builder(feedbackSessionName, courseId);
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build on top of {@code updateOptions}.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(UpdateOptions updateOptions) {
        return new UpdateOptions.Builder(updateOptions);
    }

    /**
     * A builder for {@link FeedbackSessionAttributes}.
     */
    public static class Builder extends BasicBuilder<FeedbackSessionAttributes, Builder> {
        private final FeedbackSessionAttributes feedbackSessionAttributes;

        private Builder(String feedbackSessionName, String courseId) {
            super(new UpdateOptions(feedbackSessionName, courseId));
            thisBuilder = this;

            feedbackSessionAttributes = new FeedbackSessionAttributes(feedbackSessionName, courseId);
        }

        public Builder withCreatorEmail(String creatorEmail) {
            assert creatorEmail != null;

            feedbackSessionAttributes.creatorEmail = creatorEmail;

            return this;
        }

        @Override
        public FeedbackSessionAttributes build() {
            feedbackSessionAttributes.update(updateOptions);

            return feedbackSessionAttributes;
        }
    }

    /**
     * Helper class to specific the fields to update in {@link FeedbackSessionAttributes}.
     */
    public static class UpdateOptions {
        private String courseId;
        private String feedbackSessionName;

        private UpdateOption<String> instructionsOption = UpdateOption.empty();
        private UpdateOption<Instant> startTimeOption = UpdateOption.empty();
        private UpdateOption<Instant> endTimeOption = UpdateOption.empty();
        private UpdateOption<Instant> sessionVisibleFromTimeOption = UpdateOption.empty();
        private UpdateOption<Instant> resultsVisibleFromTimeOption = UpdateOption.empty();
        private UpdateOption<String> timeZoneOption = UpdateOption.empty();
        private UpdateOption<Duration> gracePeriodOption = UpdateOption.empty();
        private UpdateOption<Boolean> sentOpeningSoonEmailOption = UpdateOption.empty();
        private UpdateOption<Boolean> sentOpenEmailOption = UpdateOption.empty();
        private UpdateOption<Boolean> sentClosingEmailOption = UpdateOption.empty();
        private UpdateOption<Boolean> sentClosedEmailOption = UpdateOption.empty();
        private UpdateOption<Boolean> sentPublishedEmailOption = UpdateOption.empty();
        private UpdateOption<Boolean> isClosingEmailEnabledOption = UpdateOption.empty();
        private UpdateOption<Boolean> isPublishedEmailEnabledOption = UpdateOption.empty();

        private UpdateOptions(String feedbackSessionName, String courseId) {
            assert feedbackSessionName != null;
            assert courseId != null;

            this.feedbackSessionName = feedbackSessionName;
            this.courseId = courseId;
        }

        public String getCourseId() {
            return courseId;
        }

        public String getFeedbackSessionName() {
            return feedbackSessionName;
        }

        @Override
        public String toString() {
            return "StudentAttributes.UpdateOptions ["
                    + "feedbackSessionName = " + feedbackSessionName
                    + ", courseId = " + courseId
                    + ", instructions = " + instructionsOption
                    + ", startTime = " + startTimeOption
                    + ", endTime = " + endTimeOption
                    + ", sessionVisibleFromTime = " + sessionVisibleFromTimeOption
                    + ", resultsVisibleFromTime = " + resultsVisibleFromTimeOption
                    + ", timeZone = " + timeZoneOption
                    + ", gracePeriod = " + gracePeriodOption
                    + ", sentOpeningSoonEmail = " + sentOpeningSoonEmailOption
                    + ", sentOpenEmail = " + sentOpenEmailOption
                    + ", sentClosingEmail = " + sentClosingEmailOption
                    + ", sentClosedEmail = " + sentClosedEmailOption
                    + ", sentPublishedEmail = " + sentPublishedEmailOption
                    + ", isClosingEmailEnabled = " + isClosingEmailEnabledOption
                    + ", isPublishedEmailEnabled = " + isPublishedEmailEnabledOption
                    + "]";
        }

        /**
         * Builder class to build {@link UpdateOptions}.
         */
        public static class Builder extends BasicBuilder<UpdateOptions, Builder> {

            private Builder(UpdateOptions updateOptions) {
                super(updateOptions);
                assert updateOptions != null;
                thisBuilder = this;
            }

            private Builder(String feedbackSessionName, String courseId) {
                super(new UpdateOptions(feedbackSessionName, courseId));
                thisBuilder = this;
            }

            public Builder withSentOpeningSoonEmail(boolean sentOpeningSoonEmailOption) {
                updateOptions.sentOpeningSoonEmailOption = UpdateOption.of(sentOpeningSoonEmailOption);
                return this;
            }

            public Builder withSentOpenEmail(boolean sentOpenEmail) {
                updateOptions.sentOpenEmailOption = UpdateOption.of(sentOpenEmail);
                return this;
            }

            public Builder withSentClosingEmail(boolean sentClosingEmail) {
                updateOptions.sentClosingEmailOption = UpdateOption.of(sentClosingEmail);
                return this;
            }

            public Builder withSentClosedEmail(boolean sentClosedEmail) {
                updateOptions.sentClosedEmailOption = UpdateOption.of(sentClosedEmail);
                return this;
            }

            public Builder withSentPublishedEmail(boolean sentPublishedEmail) {
                updateOptions.sentPublishedEmailOption = UpdateOption.of(sentPublishedEmail);
                return this;
            }

            @Override
            public UpdateOptions build() {
                return updateOptions;
            }

        }

    }

    /**
     * Basic builder to build {@link FeedbackSessionAttributes} related classes.
     *
     * @param <T> type to be built
     * @param <B> type of the builder
     */
    private abstract static class BasicBuilder<T, B extends BasicBuilder<T, B>> {

        UpdateOptions updateOptions;
        B thisBuilder;

        BasicBuilder(UpdateOptions updateOptions) {
            this.updateOptions = updateOptions;
        }

        public B withInstructions(String instruction) {
            assert instruction != null;

            updateOptions.instructionsOption = UpdateOption.of(instruction);
            return thisBuilder;
        }

        public B withStartTime(Instant startTime) {
            assert startTime != null;

            updateOptions.startTimeOption = UpdateOption.of(startTime);
            return thisBuilder;
        }

        public B withEndTime(Instant endTime) {
            assert endTime != null;

            updateOptions.endTimeOption = UpdateOption.of(endTime);
            return thisBuilder;
        }

        public B withSessionVisibleFromTime(Instant sessionVisibleFromTime) {
            assert sessionVisibleFromTime != null;

            updateOptions.sessionVisibleFromTimeOption = UpdateOption.of(sessionVisibleFromTime);
            return thisBuilder;
        }

        public B withResultsVisibleFromTime(Instant resultsVisibleFromTime) {
            assert resultsVisibleFromTime != null;

            updateOptions.resultsVisibleFromTimeOption = UpdateOption.of(resultsVisibleFromTime);
            return thisBuilder;
        }

        public B withTimeZone(String timeZone) {
            assert timeZone != null;

            updateOptions.timeZoneOption = UpdateOption.of(timeZone);
            return thisBuilder;
        }

        public B withGracePeriod(Duration gracePeriod) {
            assert gracePeriod != null;

            updateOptions.gracePeriodOption = UpdateOption.of(gracePeriod);
            return thisBuilder;
        }

        public B withIsClosingEmailEnabled(boolean isClosingEmailEnabled) {
            updateOptions.isClosingEmailEnabledOption = UpdateOption.of(isClosingEmailEnabled);
            return thisBuilder;
        }

        public B withIsPublishedEmailEnabled(boolean isPublishedEmailEnabled) {
            updateOptions.isPublishedEmailEnabledOption = UpdateOption.of(isPublishedEmailEnabled);
            return thisBuilder;
        }

        public abstract T build();

    }
}
