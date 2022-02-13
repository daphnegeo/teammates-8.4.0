package teammates.ui.webapi;

import javax.servlet.http.HttpServletResponse;

/**
 * Represents the result of executing an {@link Action}.
 */
public abstract class ActionResult {

    ActionResult(int statusCode) {
        this.statusCode = statusCode;
    }

}
