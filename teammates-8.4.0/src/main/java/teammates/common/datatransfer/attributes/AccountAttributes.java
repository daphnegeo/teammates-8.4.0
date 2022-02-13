package teammates.common.datatransfer.attributes;

import teammates.storage.entity.Account;

/**
 * The data transfer object for {@link Account} entities.
 */
public class AccountAttributes extends EntityAttributes<Account> {

    private AccountAttributes(String googleId) {
        this.googleId = googleId;
    }

}
