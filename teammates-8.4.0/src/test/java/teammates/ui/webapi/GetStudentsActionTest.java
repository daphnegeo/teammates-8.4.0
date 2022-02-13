package teammates.ui.webapi;

import java.util.List;

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
import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;
import teammates.ui.output.JoinState;
import teammates.ui.output.StudentData;
import teammates.ui.output.StudentsData;

/**
 * SUT: {@link GetStudentsAction}.
 */
public class GetStudentsActionTest extends BaseActionTest<GetStudentsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    public void testExecute_withOnlyCourseId_shouldReturnAllStudentsOfTheCourse() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
        };
        GetStudentsAction action = getAction(submissionParams);
        JsonResult jsonResult = getJsonResult(action);

        StudentsData output = (StudentsData) jsonResult.getOutput();
        List<StudentData> students = output.getStudents();

        assertEquals(5, students.size());
        StudentData typicalStudent = students.get(0);
        assertNull(typicalStudent.getGoogleId());
        assertNull(typicalStudent.getKey());
        assertEquals("idOfTypicalCourse1", typicalStudent.getCourseId());
        assertEquals("student1InCourse1@gmail.tmt", typicalStudent.getEmail());
        assertEquals("student1 In Course1</td></div>'\"", typicalStudent.getName());
        assertEquals(JoinState.JOINED, typicalStudent.getJoinState());
        assertEquals("comment for student1InCourse1</td></div>'\"", typicalStudent.getComments());
        assertEquals("Team 1.1</td></div>'\"", typicalStudent.getTeamName());
        assertEquals("Section 1", typicalStudent.getSectionName());
    }

    @Test
    public void testExecute_withCourseIdAndTeamName_shouldReturnAllStudentsOfTheTeam() {
        StudentAttributes studentAttributes = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(studentAttributes.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, studentAttributes.getCourse(),
                Const.ParamsNames.TEAM_NAME, studentAttributes.getTeam(),
        };
        GetStudentsAction action = getAction(submissionParams);
        JsonResult jsonResult = getJsonResult(action);

        StudentsData output = (StudentsData) jsonResult.getOutput();
        List<StudentData> students = output.getStudents();

        assertEquals(4, students.size());
        StudentData typicalStudent = students.get(0);
        assertNull(typicalStudent.getGoogleId());
        assertNull(typicalStudent.getKey());
        assertEquals("idOfTypicalCourse1", typicalStudent.getCourseId());
        assertEquals("student1InCourse1@gmail.tmt", typicalStudent.getEmail());
        assertEquals("student1 In Course1</td></div>'\"", typicalStudent.getName());
        assertNull(typicalStudent.getJoinState()); // information is hidden
        assertNull(typicalStudent.getComments()); // information is hidden
        assertEquals("Team 1.1</td></div>'\"", typicalStudent.getTeamName());
        assertEquals("Section 1", typicalStudent.getSectionName());
    }

    @Test
    @Override
    protected void testAccessControl() {
        ______TS("unknown courseId for (instructor access)");
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "randomId",
        };
        verifyCannotAccess(submissionParams);

        ______TS("unknown courseId and/or teamName (student access)");
        StudentAttributes studentAttributes = typicalBundle.students.get("student1InCourse1");

        loginAsStudent(studentAttributes.getGoogleId());
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "randomId",
        };
        verifyCannotAccess(submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, studentAttributes.getCourse(),
                Const.ParamsNames.TEAM_NAME, "randomTeamName",
        };
        verifyCannotAccess(submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "randomId",
                Const.ParamsNames.TEAM_NAME, "randomTeamName",
        };
        verifyCannotAccess(submissionParams);

        ______TS("unknown login entity");
        loginAsUnregistered("unregistered");
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };
        verifyCannotAccess(params);

        params = new String[] {
                Const.ParamsNames.COURSE_ID, studentAttributes.getCourse(),
                Const.ParamsNames.TEAM_NAME, studentAttributes.getTeam(),
        };
        verifyCannotAccess(params);

    }

    @Test
    public void testAccessControl_withOnlyCourseId_shouldDoAuthenticationOfInstructor() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_withCourseIdAndTeamName_shouldDoAuthenticationOfStudent() {
        StudentAttributes studentAttributes = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(studentAttributes.getGoogleId());

        ______TS("Acccess students' own team should pass");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, studentAttributes.getCourse(),
                Const.ParamsNames.TEAM_NAME, studentAttributes.getTeam(),
        };
        verifyCanAccess(submissionParams);

        ______TS("Acccess other team should fail");
        StudentAttributes otherStudent = typicalBundle.students.get("student5InCourse1");
        assertEquals(otherStudent.getCourse(), studentAttributes.getCourse());
        assertNotEquals(otherStudent.getTeam(), studentAttributes.getTeam());
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, studentAttributes.getCourse(),
                Const.ParamsNames.TEAM_NAME, otherStudent.getTeam(),
        };
        verifyCannotAccess(submissionParams);
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
