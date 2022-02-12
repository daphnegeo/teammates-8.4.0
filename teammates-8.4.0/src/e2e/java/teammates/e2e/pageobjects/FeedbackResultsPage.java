package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object Model for feedback results page.
 */
public class FeedbackResultsPage extends RankOptionSuper {
    public FeedbackResultsPage(Browser browser) {
        super(browser);
    }
}
