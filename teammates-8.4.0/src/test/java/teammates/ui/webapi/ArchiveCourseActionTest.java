package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.output.CourseArchiveData;

/**
 * SUT: {@link ArchiveCourseAction}.
 */
public class ArchiveCourseActionTest extends BaseActionTest<ArchiveCourseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE_ARCHIVE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    private void verifyCourseArchive(CourseArchiveData courseArchiveData, String courseId, boolean isArchived) {
        assertEquals(courseArchiveData.getCourseId(), courseId);
        assertEquals(courseArchiveData.getIsArchived(), isArchived);
    }

    @Override
    @Test
    protected void testAccessControl() {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalBundle.instructors.get("instructor1OfCourse1").getCourseId(),
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
