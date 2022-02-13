package teammates.storage.api;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;
import teammates.test.AssertHelper;
import teammates.test.BaseTestCaseWithLocalDatabaseAccess;

/**
 * SUT: {@link AccountsDb}.
 */
public class AccountsDbTest extends BaseTestCaseWithLocalDatabaseAccess {

    private final AccountsDb accountsDb = AccountsDb.inst();

    @Test
    public void testGetAccount() throws Exception {
        EntityAttributes<Account> a = createNewAccount();

        ______TS("typical success case without");
        EntityAttributes<Account> retrieved = accountsDb.getAccount(a.getGoogleId());
        assertNotNull(retrieved);

        ______TS("typical success with student profile");
        retrieved = accountsDb.getAccount(a.getGoogleId());
        assertNotNull(retrieved);

        ______TS("expect null for non-existent account");
        retrieved = accountsDb.getAccount("non.existent");
        assertNull(retrieved);

        ______TS("failure: null parameter");
        assertThrows(AssertionError.class, () -> accountsDb.getAccount(null));

        // delete created account
        accountsDb.deleteAccount(a.getGoogleId());
    }

    @Test
    public void testCreateAccount() throws Exception {

        ______TS("typical success case");
        AccountAttributes a = AccountAttributes.builder("test.account")
                .withName("Test account Name")
                .withIsInstructor(false)
                .withEmail("fresh-account@email.com")
                .withInstitute("TEAMMATES Test Institute 1")
                .build();

        accountsDb.createEntity(a);

        ______TS("duplicate account, creation fail");

        AccountAttributes duplicatedAccount = AccountAttributes.builder("test.account")
                .withName("name2")
                .withEmail("test2@email.com")
                .withInstitute("de2v")
                .withIsInstructor(false)
                .build();
        assertThrows(EntityAlreadyExistsException.class, () -> {
            accountsDb.createEntity(duplicatedAccount);
        });

        accountsDb.deleteAccount(a.getGoogleId());

        // Should we not allow empty fields?
        ______TS("failure case: invalid parameter");
        a.setEmail("invalid email");
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> accountsDb.createEntity(a));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.EMAIL_ERROR_MESSAGE, "invalid email",
                        FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.EMAIL_MAX_LENGTH),
                ipe.getMessage());

        ______TS("failure: null parameter");
        assertThrows(AssertionError.class, () -> accountsDb.createEntity(null));
    }

    @Test
    public void testUpdateAccount_noChangeToAccount_shouldNotIssueSaveRequest() throws Exception {
        EntityAttributes<Account> a = createNewAccount();

        EntityAttributes<Account> updatedAccount =
                accountsDb.updateAccount(
                        AccountAttributes.updateOptionsBuilder(a.getGoogleId())
                                .build());

        // please verify the log message manually to ensure that saving request is not issued
        assertEquals(JsonUtils.toJson(a), JsonUtils.toJson(updatedAccount));

        updatedAccount =
                accountsDb.updateAccount(
                        AccountAttributes.updateOptionsBuilder(a.getGoogleId())
                                .withIsInstructor(a.isInstructor())
                                .build());

        // please verify the log message manually to ensure that saving request is not issued
        assertEquals(JsonUtils.toJson(a), JsonUtils.toJson(updatedAccount));
    }

    @Test
    public void testUpdateAccount() throws Exception {
        EntityAttributes<Account> a = createNewAccount();

        ______TS("typical edit success case");
        assertFalse(a.isInstructor());
        EntityAttributes<Account> updatedAccount = accountsDb.updateAccount(
                AccountAttributes.updateOptionsBuilder(a.getGoogleId())
                        .withIsInstructor(true)
                        .build()
        );

        EntityAttributes<Account> actualAccount = accountsDb.getAccount(a.getGoogleId());

        assertTrue(actualAccount.isInstructor());
        assertTrue(updatedAccount.isInstructor());

        ______TS("non-existent account");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> accountsDb.updateAccount(
                        AccountAttributes.updateOptionsBuilder("non.existent")
                                .withIsInstructor(true)
                                .build()
                ));
        AssertHelper.assertContains(AccountsDb.ERROR_UPDATE_NON_EXISTENT, ednee.getMessage());

        ______TS("failure: null parameter");

        assertThrows(AssertionError.class,
                () -> accountsDb.updateAccount(null));

        accountsDb.deleteAccount(a.getGoogleId());
    }

    // the test is to ensure that optimized saving policy is implemented without false negative
    @Test
    public void testUpdateAccount_singleFieldUpdate_shouldUpdateCorrectly() throws Exception {
        EntityAttributes<Account> typicalAccount = createNewAccount();

        assertFalse(typicalAccount.isInstructor());
        EntityAttributes<Account> updatedAccount = accountsDb.updateAccount(
                AccountAttributes.updateOptionsBuilder(typicalAccount.getGoogleId())
                        .withIsInstructor(true)
                        .build());
        EntityAttributes<Account> actualAccount = accountsDb.getAccount(typicalAccount.getGoogleId());
        assertTrue(actualAccount.isInstructor());
        assertTrue(updatedAccount.isInstructor());
    }

    @Test
    public void testDeleteAccount() throws Exception {
        EntityAttributes<Account> a = createNewAccount();

        ______TS("silent deletion of non-existent account");

        accountsDb.deleteAccount("not_exist");
        assertNotNull(accountsDb.getAccount(a.getGoogleId()));

        ______TS("typical success case");
        EntityAttributes<Account> newAccount = accountsDb.getAccount(a.getGoogleId());
        assertNotNull(newAccount);

        accountsDb.deleteAccount(a.getGoogleId());

        EntityAttributes<Account> newAccountDeleted = accountsDb.getAccount(a.getGoogleId());
        assertNull(newAccountDeleted);

        ______TS("silent deletion of same account");
        accountsDb.deleteAccount(a.getGoogleId());

        ______TS("failure null parameter");

        assertThrows(AssertionError.class,
                () -> accountsDb.deleteAccount(null));
    }

    private EntityAttributes<Account> createNewAccount() throws Exception {
        EntityAttributes<Account> a = getNewAccountAttributes();
        return accountsDb.putEntity(a);
    }

    private EntityAttributes<Account> getNewAccountAttributes() {
        return AccountAttributes.builder("valid.googleId")
                .withName("Valid Fresh Account")
                .withIsInstructor(false)
                .withEmail("valid@email.com")
                .withInstitute("TEAMMATES Test Institute 1")
                .build();
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
