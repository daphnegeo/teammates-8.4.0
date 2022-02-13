package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;

import org.openqa.selenium.By;

/**
 * Page Object Model for the error reporting modal.
 */
public class ErrorReportingModal extends AppPage {

    public ErrorReportingModal(Browser browser) {
        super(browser);
    }

    public void verifyErrorMessage(String message) {
        assertEquals(browser.driver.findElement(By.id("error-message")).getText(),
                "The server returns the following error message: \"" + message + "\".");
    }
}
