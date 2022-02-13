package teammates.client.scripts;

import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackResponseComment;

/**
 * Script to set commentGiverType as INSTRUCTOR in all comments by instructor.
 *
 * <p>See issue #9083</p>
 */
public class DataMigrationForNullCommentGiverType extends
        DataMigrationEntitiesBaseScript<FeedbackResponseComment> {

    public static void main(String[] args) {
        new DataMigrationForNullCommentGiverType().doOperationRemotely();
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(FeedbackResponseComment comment) {
        return comment.getCommentGiverType() == null;
    }

    @Override
    protected void migrateEntity(FeedbackResponseComment comment) {
        comment.setCommentGiverType(FeedbackParticipantType.INSTRUCTORS);
        comment.setIsCommentFromFeedbackParticipant(false);

        saveEntityDeferred(comment);
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
