package teammates.client.scripts;

import com.googlecode.objectify.cmd.Query;

import teammates.storage.entity.Account;
import teammates.storage.entity.Instructor;

/**
 * Script to desanitize name of {@link Instructor} if it is sanitized.
 */
public class DataMigrationForSanitizedInstructorName
        extends DataMigrationEntitiesBaseScript<Instructor> {

    public DataMigrationForSanitizedInstructorName() {
        numberOfScannedKey.set(0L);
        numberOfAffectedEntities.set(0L);
        numberOfUpdatedEntities.set(0L);
    }

    public static void main(String[] args) {
        DataMigrationForSanitizedInstructorName migrator =
                new DataMigrationForSanitizedInstructorName();
        migrator.doOperationRemotely();
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(Instructor instructor) {
        return isSanitizedHtml(instructor.getName());
    }

    @Override
    protected void migrateEntity(Instructor instructor) {
        instructor.setName(desanitizeIfHtmlSanitized(instructor.getName()));

        saveEntityDeferred(instructor);
    }

	@Override
	protected String generateNewGoogleId(Account oldAccount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isMigrationOfGoogleIdNeeded(Account account) {
		// TODO Auto-generated method stub
		return false;
	}
}
