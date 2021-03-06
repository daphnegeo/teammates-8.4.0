package teammates.e2e.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object Model for login page in development server.
 */
public class DevServerLoginPage extends AppPage {

    @FindBy(id = "email")
    private WebElement emailTextBox;

    @FindBy(id = "btn-login")
    private WebElement loginButton;

    public DevServerLoginPage(Browser browser) {
        super(browser);
    }

    public void loginAsUser(String username) {
        fillTextBox(emailTextBox, username);
        click(loginButton);
        waitForPageToLoad();
    }

}
