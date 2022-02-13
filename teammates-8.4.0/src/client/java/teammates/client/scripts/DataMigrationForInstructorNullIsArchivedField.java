package teammates.client.scripts;

import java.lang.reflect.Field;

import com.googlecode.objectify.cmd.Query;

import teammates.storage.entity.Account;
import teammates.storage.entity.Instructor;

/**
 * Script to change all null value to false in the isArchived field for Instructor entity.
 */
public class DataMigrationForInstructorNullIsArchivedField extends DataMigrationEntitiesBaseScript<Instructor> {

    public static void main(String[] args) {
        new DataMigrationForInstructorNullIsArchivedField().doOperationRemotely();
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(Instructor instructor) {
        try {
            Field isArchivedField = instructor.getClass().getDeclaredField("isArchived");
            isArchivedField.setAccessible(true);
            return isArchivedField.get(instructor) == null;
        } catch (ReflectiveOperationException e) {
            return true;
        }
    }

    @Override
    protected void migrateEntity(Instructor instructor) {
        instructor.setIsArchived(instructor.getIsArchived());

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
