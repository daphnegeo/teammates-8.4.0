package teammates.common.datatransfer.attributes;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.Const.InstructorPermissions;
import teammates.common.util.Const.WebPageURIs;
import teammates.common.util.FieldValidator;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.e2e.cases.BaseE2ETestCase;
import teammates.e2e.cases.InstructorCourseEditPageE2ETest;
import teammates.e2e.pageobjects.InstructorCourseEditPage;
import teammates.storage.entity.Course;
import teammates.test.BaseTestCase;

/**
 * The data transfer object for {@link Course} entities.
 */
public class CourseAttributes extends EntityAttributes<Course> implements Comparable<CourseAttributes> {

    private static final Logger log = Logger.getLogger();

    private Instant createdAt;
    private Instant deletedAt;
    private String name;
    private String timeZone;
    private String id;
    private String institute;

    private CourseAttributes(String courseId) {
        this.id = courseId;
        this.timeZone = Const.DEFAULT_TIME_ZONE;
        this.institute = Const.UNKNOWN_INSTITUTION;
        this.createdAt = Instant.now();
        this.deletedAt = null;
    }

    /**
     * Gets the {@link CourseAttributes} instance of the given {@link Course}.
     */
    public static CourseAttributes valueOf(Course course) {
        CourseAttributes courseAttributes = new CourseAttributes(course.getUniqueId());

        courseAttributes.name = course.getName();

        String courseTimeZone;
        try {
            ZoneId.of(course.getTimeZone());
            courseTimeZone = course.getTimeZone();
        } catch (DateTimeException e) {
            log.severe("Timezone '" + course.getTimeZone() + "' of course '" + course.getUniqueId()
                    + "' is not supported. UTC will be used instead.");
            courseTimeZone = Const.DEFAULT_TIME_ZONE;
        }
        courseAttributes.timeZone = courseTimeZone;
        courseAttributes.institute = course.getInstitute();

        if (course.getCreatedAt() != null) {
            courseAttributes.createdAt = course.getCreatedAt();
        }
        courseAttributes.deletedAt = course.getDeletedAt();

        return courseAttributes;
    }

    /**
     * Returns a builder for {@link CourseAttributes}.
     */
    public static Builder builder(String courseId) {
        return new Builder(courseId);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getInstitute() {
        return institute;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public boolean isCourseDeleted() {
        return this.deletedAt != null;
    }

    @Override
    public List<String> getInvalidityInfo() {

        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForCourseId(getId()), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForCourseName(getName()), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForInstituteName(getInstitute()), errors);

        return errors;
    }

    @Override
    public Course toEntity() {
        return new Course(getId(), getName(), getTimeZone(), getInstitute(), createdAt, deletedAt);
    }

    @Override
    public String toString() {
        return "[" + CourseAttributes.class.getSimpleName() + "] id: " + getId() + " name: " + getName()
               + " institute: " + getInstitute() + " timeZone: " + getTimeZone();
    }

    @Override
    public int hashCode() {
        return (this.id + this.name + this.institute).hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            CourseAttributes otherCourse = (CourseAttributes) other;
            return Objects.equals(this.id, otherCourse.id)
                    && Objects.equals(this.institute, otherCourse.institute)
                    && Objects.equals(this.name, otherCourse.name);
        } else {
            return false;
        }
    }

    @Override
    public void sanitizeForSaving() {
        this.institute = SanitizationHelper.sanitizeTitle(institute);
    }

    @Override
    public int compareTo(CourseAttributes o) {
        if (o == null) {
            return 0;
        }
        return o.createdAt.compareTo(createdAt);
    }

    /**
     * Sorts the list of courses by the course ID.
     */
    public static void sortById(List<CourseAttributes> courses) {
        courses.sort(Comparator.comparing(CourseAttributes::getId));
    }

    /**
     * Updates with {@link UpdateOptions}.
     */
    public void update(UpdateOptions updateOptions) {
        updateOptions.nameOption.ifPresent(s -> name = s);
        updateOptions.timeZoneOption.ifPresent(s -> timeZone = s);
        updateOptions.instituteOption.ifPresent(s -> institute = s);
    }

    @Test
	public void testAll(InstructorCourseEditPageE2ETest instructorCourseEditPageE2ETest) {
	    BaseTestCase.______TS("verify cannot edit without privilege");
	    // log in as instructor with no edit privilege
	    AppUrl url = BaseE2ETestCase.createUrl(WebPageURIs.INSTRUCTOR_COURSE_EDIT_PAGE)
	            .withCourseId(getId());
	    InstructorCourseEditPage editPage = instructorCourseEditPageE2ETest.loginToPage(url, InstructorCourseEditPage.class, instructorCourseEditPageE2ETest.instructors[2].getGoogleId());
	
	    editPage.verifyCourseNotEditable();
	    editPage.verifyInstructorsNotEditable();
	    editPage.verifyAddInstructorNotAllowed();
	
	    BaseTestCase.______TS("verify loaded data");
	    // re-log in as instructor with edit privilege
	    instructorCourseEditPageE2ETest.logout();
	    url = BaseE2ETestCase.createUrl(WebPageURIs.INSTRUCTOR_COURSE_EDIT_PAGE)
	            .withCourseId(getId());
	    editPage = instructorCourseEditPageE2ETest.loginToPage(url, InstructorCourseEditPage.class, instructorCourseEditPageE2ETest.instructors[3].getGoogleId());
	
	    editPage.verifyCourseDetails(this);
	    editPage.verifyInstructorDetails(instructorCourseEditPageE2ETest.instructors[0]);
	    editPage.verifyInstructorDetails(instructorCourseEditPageE2ETest.instructors[1]);
	    editPage.verifyInstructorDetails(instructorCourseEditPageE2ETest.instructors[2]);
	    editPage.verifyInstructorDetails(instructorCourseEditPageE2ETest.instructors[3]);
	    editPage.verifyInstructorDetails(instructorCourseEditPageE2ETest.instructors[4]);
	
	    BaseTestCase.______TS("add instructor");
	    InstructorAttributes newInstructor = InstructorAttributes
	            .builder(getId(), "ICEdit.test@gmail.tmt")
	            .withName("Teammates Test")
	            .withIsDisplayedToStudents(true)
	            .withDisplayedName("Instructor")
	            .withRole("Tutor")
	            .build();
	
	    editPage.addInstructor(newInstructor);
	    editPage.verifyStatusMessage("\"The instructor " + newInstructor.getName() + " has been added successfully. "
	            + "An email containing how to 'join' this course will be sent to " + newInstructor.getEmail()
	            + " in a few minutes.\"");
	    editPage.verifyInstructorDetails(newInstructor);
	    instructorCourseEditPageE2ETest.verifyPresentInDatabase(newInstructor);
	
	    BaseTestCase.______TS("resend invite");
	    editPage.resendInstructorInvite(newInstructor);
	    editPage.verifyStatusMessage("An email has been sent to " + newInstructor.getEmail());
	
	    BaseTestCase.______TS("edit instructor");
	    instructorCourseEditPageE2ETest.instructors[0].setName("Edited Name");
	    instructorCourseEditPageE2ETest.instructors[0].setEmail("ICEdit.edited@gmail.tmt");
	    instructorCourseEditPageE2ETest.instructors[0].getPrivileges().updatePrivilege(InstructorPermissions.CAN_MODIFY_SESSION, true);
	    instructorCourseEditPageE2ETest.instructors[0].getPrivileges().updatePrivilege(InstructorPermissions.CAN_MODIFY_STUDENT, false);
	    instructorCourseEditPageE2ETest.instructors[0].getPrivileges().updatePrivilege("Section 2",
	            InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, true);
	    instructorCourseEditPageE2ETest.instructors[0].getPrivileges().updatePrivilege("Section 1", "First feedback session",
	            InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, true);
	
	    editPage.editInstructor(1, instructorCourseEditPageE2ETest.instructors[0]);
	    editPage.toggleCustomCourseLevelPrivilege(1, InstructorPermissions.CAN_MODIFY_SESSION);
	    editPage.toggleCustomCourseLevelPrivilege(1, InstructorPermissions.CAN_MODIFY_STUDENT);
	    editPage.toggleCustomSectionLevelPrivilege(1, 1, "Section 2",
	            InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
	    editPage.toggleCustomSessionLevelPrivilege(1, 2, "Section 1", "First feedback session",
	            InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS);
	    editPage.verifyStatusMessage("The instructor " + instructorCourseEditPageE2ETest.instructors[0].getName() + " has been updated.");
	    editPage.verifyInstructorDetails(instructorCourseEditPageE2ETest.instructors[0]);
	
	    // verify in database by reloading
	    editPage.reloadPage();
	    editPage.verifyInstructorDetails(instructorCourseEditPageE2ETest.instructors[0]);
	
	    BaseTestCase.______TS("delete instructor");
	    editPage.deleteInstructor(newInstructor);
	    editPage.verifyStatusMessage("Instructor is successfully deleted.");
	    editPage.verifyNumInstructorsEquals(5);
	    instructorCourseEditPageE2ETest.verifyAbsentInDatabase(newInstructor);
	
	    BaseTestCase.______TS("edit course");
	    String newName = "New Course Name";
	    String newTimeZone = "Asia/Singapore";
	    setName(newName);
	    setTimeZone(newTimeZone);
	
	    editPage.editCourse(this);
	    editPage.verifyStatusMessage("The course has been edited.");
	    editPage.verifyCourseDetails(this);
	    instructorCourseEditPageE2ETest.verifyPresentInDatabase(this);
	
	    BaseTestCase.______TS("delete course");
	    editPage.deleteCourse();
	    editPage.verifyStatusMessage("The course " + getId() + " has been deleted. "
	            + "You can restore it from the Recycle Bin manually.");
	    BaseTestCase.assertTrue(InstructorCourseEditPageE2ETest.BACKDOOR.isCourseInRecycleBin(getId()));
	}

	/**
     * Returns a {@link UpdateOptions.Builder} to build {@link UpdateOptions} for a course.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(String courseId) {
        return new UpdateOptions.Builder(courseId);
    }

    /**
     * A builder for {@link CourseAttributes}.
     */
    public static class Builder extends BasicBuilder<CourseAttributes, Builder> {

        private final CourseAttributes courseAttributes;

        private Builder(String courseId) {
            super(new UpdateOptions(courseId));
            thisBuilder = this;

            courseAttributes = new CourseAttributes(courseId);
        }

        @Override
        public CourseAttributes build() {
            courseAttributes.update(updateOptions);

            return courseAttributes;
        }
    }

    /**
     * Helper class to specific the fields to update in {@link AccountAttributes}.
     */
    public static class UpdateOptions {
        private String courseId;

        private UpdateOption<String> nameOption = UpdateOption.empty();
        private UpdateOption<String> timeZoneOption = UpdateOption.empty();
        private UpdateOption<String> instituteOption = UpdateOption.empty();

        private UpdateOptions(String courseId) {
            assert courseId != null;

            this.courseId = courseId;
        }

        public String getCourseId() {
            return courseId;
        }

        @Override
        public String toString() {
            return "CourseAttributes.UpdateOptions ["
                    + "courseId = " + courseId
                    + ", name = " + nameOption
                    + ", timezone = " + timeZoneOption
                    + ", institute = " + instituteOption
                    + "]";
        }

        /**
         * Builder class to build {@link UpdateOptions}.
         */
        public static class Builder extends BasicBuilder<UpdateOptions, Builder> {

            private Builder(String courseId) {
                super(new UpdateOptions(courseId));
                thisBuilder = this;
            }

            @Override
            public UpdateOptions build() {
                return updateOptions;
            }

        }

    }

    /**
     * Basic builder to build {@link CourseAttributes} related classes.
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

        public B withTimezone(String timezone) {
            assert timezone != null;

            updateOptions.timeZoneOption = UpdateOption.of(timezone);
            return thisBuilder;
        }

        public B withInstitute(String institute) {
            assert institute != null;

            updateOptions.instituteOption = UpdateOption.of(institute);
            return thisBuilder;
        }

        public abstract T build();

    }
}
