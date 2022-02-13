package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

import teammates.common.datatransfer.questions.FeedbackRubricResponseDetails;
import teammates.common.util.AppUrl;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.Const.EntityType;
import teammates.common.util.Const.WebPageURIs;
import teammates.e2e.cases.BaseE2ETestCase;
import teammates.e2e.cases.FeedbackRubricQuestionE2ETest;
import teammates.e2e.cases.StudentCourseJoinConfirmationPageE2ETest;
import teammates.e2e.pageobjects.CourseJoinConfirmationPage;
import teammates.e2e.pageobjects.StudentHomePage;
import teammates.storage.entity.CourseStudent;
import teammates.test.BaseTestCase;

/**
 * The data transfer object for {@link CourseStudent} entities.
 */
public class StudentAttributes extends EntityAttributes<CourseStudent> {

    private String email;
    private String course;
    private String name;
    private String googleId;
    private String comments;
    private String team;
    private String section;
    private transient String key;
    private transient Instant createdAt;
    private transient Instant updatedAt;

    private StudentAttributes(String courseId, String email) {
        this.course = courseId;
        this.email = email;

        this.googleId = "";
        this.section = Const.DEFAULT_SECTION;
        this.createdAt = Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP;
        this.updatedAt = Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP;
    }

    /**
     * Gets the {@link StudentAttributes} instance of the given {@link CourseStudent}.
     */
    public static StudentAttributes valueOf(CourseStudent student) {
        StudentAttributes studentAttributes = new StudentAttributes(student.getCourseId(), student.getEmail());
        studentAttributes.name = student.getName();
        if (student.getGoogleId() != null) {
            studentAttributes.googleId = student.getGoogleId();
        }
        studentAttributes.team = student.getTeamName();
        if (student.getSectionName() != null) {
            studentAttributes.section = student.getSectionName();
        }
        studentAttributes.comments = student.getComments();
        studentAttributes.key = student.getRegistrationKey();
        if (student.getCreatedAt() != null) {
            studentAttributes.createdAt = student.getCreatedAt();
        }
        if (student.getUpdatedAt() != null) {
            studentAttributes.updatedAt = student.getUpdatedAt();
        }

        return studentAttributes;
    }

    /**
     * Return a builder for {@link StudentAttributes}.
     */
    public static Builder builder(String courseId, String email) {
        return new Builder(courseId, email);
    }

    public boolean isRegistered() {
        return googleId != null && !googleId.trim().isEmpty();
    }

    public String getRegistrationUrl() {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(key)
                .withEntityType(Const.EntityType.STUDENT)
                .toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * Sorts the list of students by the section name, then team name, then name.
     */
    public static void sortBySectionName(List<StudentAttributes> students) {
        students.sort(Comparator.comparing((StudentAttributes student) -> student.section)
                .thenComparing(student -> student.team)
                .thenComparing(student -> student.name));
    }

    /**
     * Sorts the list of students by the team name, then name.
     */
    public static void sortByTeamName(List<StudentAttributes> students) {
        students.sort(Comparator.comparing((StudentAttributes student) -> student.team)
                .thenComparing(student -> student.name));
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public FeedbackResponseAttributes getResponse(String questionId, FeedbackRubricQuestionE2ETest feedbackRubricQuestionE2ETest, List<Integer> answers) {
	    FeedbackRubricResponseDetails details = new FeedbackRubricResponseDetails();
	    details.setAnswer(answers);
	    return FeedbackResponseAttributes.builder(questionId, feedbackRubricQuestionE2ETest.student.getEmail(), getEmail())
	            .withResponseDetails(details)
	            .build();
	}

	@Test
	public void testAll(StudentCourseJoinConfirmationPageE2ETest studentCourseJoinConfirmationPageE2ETest) {
	    BaseTestCase.______TS("Click join link: invalid key");
	    String courseId = studentCourseJoinConfirmationPageE2ETest.testData.courses.get("SCJoinConf.CS2104").getId();
	    String invalidKey = "invalidKey";
	    AppUrl joinLink = BaseE2ETestCase.createUrl(WebPageURIs.JOIN_PAGE)
	            .withRegistrationKey(invalidKey)
	            .withCourseId(courseId)
	            .withEntityType(EntityType.STUDENT);
	    CourseJoinConfirmationPage confirmationPage = studentCourseJoinConfirmationPageE2ETest.loginToPage(
	            joinLink, CourseJoinConfirmationPage.class, getGoogleId());
	
	    confirmationPage.verifyDisplayedMessage("The course join link is invalid. You may have "
	            + "entered the URL incorrectly or the URL may correspond to a/an student that does not exist.");
	
	    BaseTestCase.______TS("Click join link: valid key");
	    joinLink = BaseE2ETestCase.createUrl(WebPageURIs.JOIN_PAGE)
	            .withRegistrationKey(studentCourseJoinConfirmationPageE2ETest.getKeyForStudent(this))
	            .withCourseId(courseId)
	            .withEntityType(EntityType.STUDENT);
	    confirmationPage = studentCourseJoinConfirmationPageE2ETest.getNewPageInstance(joinLink, CourseJoinConfirmationPage.class);
	
	    confirmationPage.verifyJoiningUser(getGoogleId());
	    confirmationPage.confirmJoinCourse(StudentHomePage.class);
	
	    BaseTestCase.______TS("Already joined, no confirmation page");
	
	    studentCourseJoinConfirmationPageE2ETest.getNewPageInstance(joinLink, StudentHomePage.class);
	}

	/**
     * Returns a {@link UpdateOptions.Builder} to build {@link UpdateOptions} for a student.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(String courseId, String email) {
        return new UpdateOptions.Builder(courseId, email);
    }

    /**
     * A builder class for {@link StudentAttributes}.
     */
    public static class Builder extends BasicBuilder<StudentAttributes, Builder> {

        private final StudentAttributes studentAttributes;

        private Builder(String courseId, String email) {
            super(new UpdateOptions(courseId, email));
            thisBuilder = this;

            studentAttributes = new StudentAttributes(courseId, email);
        }

        @Override
        public StudentAttributes build() {
            studentAttributes.update(updateOptions);

            return studentAttributes;
        }
    }

    /**
     * Helper class to specify the fields to update in {@link StudentAttributes}.
     */
    public static class UpdateOptions {
        private String courseId;
        private String email;

        private UpdateOption<String> newEmailOption = UpdateOption.empty();
        private UpdateOption<String> nameOption = UpdateOption.empty();
        private UpdateOption<String> commentOption = UpdateOption.empty();
        private UpdateOption<String> googleIdOption = UpdateOption.empty();
        private UpdateOption<String> teamNameOption = UpdateOption.empty();
        private UpdateOption<String> sectionNameOption = UpdateOption.empty();

        private UpdateOptions(String courseId, String email) {
            assert courseId != null;
            assert email != null;

            this.courseId = courseId;
            this.email = email;
        }

        public String getCourseId() {
            return courseId;
        }

        public String getEmail() {
            return email;
        }

        @Override
        public String toString() {
            return "StudentAttributes.UpdateOptions ["
                    + "courseId = " + courseId
                    + ", email = " + email
                    + ", newEmail = " + newEmailOption
                    + ", name = " + nameOption
                    + ", comment = " + commentOption
                    + ", googleId = " + googleIdOption
                    + ", teamName = " + teamNameOption
                    + ", sectionName = " + sectionNameOption
                    + "]";
        }

        /**
         * Builder class to build {@link UpdateOptions}.
         */
        public static class Builder extends BasicBuilder<UpdateOptions, Builder> {

            private Builder(String courseId, String email) {
                super(new UpdateOptions(courseId, email));
                thisBuilder = this;
            }

            public Builder withNewEmail(String email) {
                assert email != null;

                updateOptions.newEmailOption = UpdateOption.of(email);
                return thisBuilder;
            }

            @Override
            public UpdateOptions build() {
                return updateOptions;
            }

        }

    }

    /**
     * Basic builder to build {@link StudentAttributes} related classes.
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

        public B withName(String name) {
            assert name != null;

            updateOptions.nameOption = UpdateOption.of(name);
            return thisBuilder;
        }

        public B withComment(String comment) {
            assert comment != null;

            updateOptions.commentOption = UpdateOption.of(comment);
            return thisBuilder;
        }

        public B withGoogleId(String googleId) {
            // google id can be set to null
            updateOptions.googleIdOption = UpdateOption.of(googleId);
            return thisBuilder;
        }

        public B withTeamName(String teamName) {
            assert teamName != null;

            updateOptions.teamNameOption = UpdateOption.of(teamName);
            return thisBuilder;
        }

        public B withSectionName(String sectionName) {
            assert sectionName != null;

            updateOptions.sectionNameOption = UpdateOption.of(sectionName);
            return thisBuilder;
        }

        public abstract T build();

    }
}
