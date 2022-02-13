package teammates.e2e.cases;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AdminTimezonePage;
import teammates.e2e.pageobjects.IanaTimezonePage;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Verifies that the timezone databases in moment-timezone and java.time are consistent and up-to-date.
 *
 * <p>Implemented as a browser test as both back-end and front-end methods are involved.
 */
public class TimezoneSyncerTest extends BaseE2ETestCase {

    private static final String IANA_TIMEZONE_DATABASE_URL = "https://www.iana.org/time-zones";
    private static final int DAYS_TO_UPDATE_TZ = 120;

    @Override
    protected void prepareTestData() {
        // no test data used in this test
    }

    @Test
    @Override
    public void testAll() {
        AdminTimezonePage timezonePage = loginAdminToPage(
                createUrl(Const.WebPageURIs.ADMIN_TIMEZONE_PAGE), AdminTimezonePage.class);

        ______TS("ensure the front-end and the back-end have the same timezone database version");
        String javaOffsets = timezonePage.getJavaTimezoneOffsets();
        String momentOffsets = timezonePage.getMomentTimezoneOffsets();
        assertEquals(
                "The timezone database versions are not in sync. For information on updating the timezone databases, "
                + "see the maintainer guide in the TEAMMATES ops repository.",
                timezonePage.getJavaTimezoneVersion(),
                timezonePage.getMomentTimezoneVersion()
        );
        if (!javaOffsets.equals(momentOffsets)) {
            // Show diff when running test in Gradle
            assertEquals("<expected>" + System.lineSeparator() + javaOffsets + "</expected>",
                    "<actual>" + System.lineSeparator() + momentOffsets + "</actual>");
        }

        ______TS("ensure the timezone databases are up-to-date");
        String currentTzVersion = timezonePage.getMomentTimezoneVersion();
        IanaTimezonePage ianaPage = getNewPageInstance(
                new AppUrl(IANA_TIMEZONE_DATABASE_URL), IanaTimezonePage.class);
        String latestTzVersion = ianaPage.getVersion();

        if (!currentTzVersion.equals(latestTzVersion)) {
            // find the release day
            String releaseDateString = ianaPage.getReleaseDate();
            Pattern datePattern = Pattern.compile("\\(Released (.+)\\)");
            Matcher matcher = datePattern.matcher(releaseDateString);
            assertTrue(matcher.find());

            LocalDate releaseDate = LocalDate.parse(matcher.group(1), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate nowDate = Instant.now().atZone(ZoneId.of(Const.DEFAULT_TIME_ZONE)).toLocalDate();

            assertTrue(
                    "The timezone database version is not up-to-date for more than " + DAYS_TO_UPDATE_TZ + " days,"
                            + " please update them according to the maintenance guide.",
                    releaseDate.plusDays(DAYS_TO_UPDATE_TZ).isAfter(nowDate));

        }
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
