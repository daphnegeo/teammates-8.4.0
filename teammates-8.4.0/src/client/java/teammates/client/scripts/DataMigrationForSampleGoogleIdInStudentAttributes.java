package teammates.client.scripts;

import com.googlecode.objectify.cmd.Query;

import teammates.storage.entity.Account;
import teammates.storage.entity.CourseStudent;

/**
 * Script to remove google ID of {@link CourseStudent} if it is fake google ID.
 */
public class DataMigrationForSampleGoogleIdInStudentAttributes
        extends DataMigrationEntitiesBaseScript<CourseStudent> {

    public static void main(String[] args) {
        DataMigrationForSampleGoogleIdInStudentAttributes migrator =
                new DataMigrationForSampleGoogleIdInStudentAttributes();
        migrator.doOperationRemotely();
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(CourseStudent student) {
        return true;
    }

    @Override
    protected void migrateEntity(CourseStudent student) {
        student.setGoogleId("");

        saveEntityDeferred(student);
    }

	@Override
	protected boolean isMigrationOfGoogleIdNeeded(Account account) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected String generateNewGoogleId(Account oldAccount) {
		// TODO Auto-generated method stub
		return null;
	}
}
