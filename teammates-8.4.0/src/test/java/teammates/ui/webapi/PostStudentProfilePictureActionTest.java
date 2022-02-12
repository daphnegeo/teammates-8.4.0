package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link PostStudentProfilePictureAction}.
 */
public class PostStudentProfilePictureActionTest extends BaseActionTest<PostStudentProfilePictureAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_PROFILE_PICTURE;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyInaccessibleWithoutLogin();
        verifyInaccessibleForUnregisteredUsers();
    }
}
