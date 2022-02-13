package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.ui.output.AccountData;

/**
 * Gets account's information.
 */
class GetAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String googleId = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);

        EntityAttributes<Account> accountInfo = logic.getAccount(googleId);
        if (accountInfo == null) {
            throw new EntityNotFoundException("Account does not exist.");
        }

        AccountData output = new AccountData(accountInfo);
        return new JsonResult(output);
    }

}
