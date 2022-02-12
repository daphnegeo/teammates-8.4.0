package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Represents the instructor feedback edit page of the website.
 */
public class InstructorFeedbackEditPage extends InstructorFeedbackPage {
    public InstructorFeedbackEditPage(Browser browser) {
        super(browser);
    }
}
