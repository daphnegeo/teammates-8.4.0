package teammates.e2e.pageobjects;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.remote.UselessFileDetector;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackConstantSumQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackConstantSumResponseDetails;
import teammates.common.datatransfer.questions.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackRankOptionsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRankOptionsResponseDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRubricResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.e2e.util.MaximumRetriesExceededException;
import teammates.e2e.util.RetryManager;
import teammates.e2e.util.Retryable;
import teammates.e2e.util.TestProperties;
import teammates.storage.entity.FeedbackQuestion;
import teammates.test.FileHelper;
import teammates.test.ThreadHelper;

/**
 * An abstract class that represents a browser-loaded page of the app and
 * provides ways to interact with it. Also contains methods to validate some
 * aspects of the page, e.g. HTML page source.
 *
 * <p>Note: We are using the Page Object pattern here.
 *
 * @see <a href="https://martinfowler.com/bliki/PageObject.html">https://martinfowler.com/bliki/PageObject.html</a>
 */
public abstract class AppPage {

    private static final String CLEAR_ELEMENT_SCRIPT;
    private static final String SCROLL_ELEMENT_TO_CENTER_AND_CLICK_SCRIPT;
    private static final String READ_TINYMCE_CONTENT_SCRIPT;
    private static final String WRITE_TO_TINYMCE_SCRIPT;
	private static final String CUSTOM_FEEDBACK_PATH_OPTION = "Custom feedback path";
	private static final String FEEDBACK_PATH_SEPARATOR = " will give feedback on ";
	private static final String CUSTOM_VISIBILITY_OPTION = "Custom visibility options";
	private static final String CURRENT_STUDENT_IDENTIFIER = "You";

    static {
        try {
            CLEAR_ELEMENT_SCRIPT = FileHelper.readFile("src/e2e/resources/scripts/clearElementWithoutEvents.js");
            SCROLL_ELEMENT_TO_CENTER_AND_CLICK_SCRIPT = FileHelper
                    .readFile("src/e2e/resources/scripts/scrollElementToCenterAndClick.js");
            READ_TINYMCE_CONTENT_SCRIPT = FileHelper.readFile("src/e2e/resources/scripts/readTinyMCEContent.js");
            WRITE_TO_TINYMCE_SCRIPT = FileHelper.readFile("src/e2e/resources/scripts/writeToTinyMCE.js");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Browser instance the page is loaded into. */
    protected Browser browser;

    /** Use for retrying due to transient UI issues. */
    protected RetryManager uiRetryManager = new RetryManager((TestProperties.TEST_TIMEOUT + 1) / 2);
	@FindBy(id = "btn-fs-edit")
	private WebElement fsEditButton;
	@FindBy(id = "btn-fs-save")
	private WebElement fsSaveButton;
	@FindBy(id = "btn-fs-copy")
	private WebElement fsCopyButton;
	@FindBy(id = "edit-course-id")
	private WebElement courseIdTextBox;
	@FindBy(id = "time-zone")
	private WebElement timezoneDropDown;
	@FindBy(id = "course-name")
	private WebElement courseNameTextBox;
	@FindBy(id = "edit-session-name")
	private WebElement sessionNameTextBox;
	@FindBy(id = "instructions")
	private WebElement instructionsEditor;
	@FindBy(id = "submission-start-date")
	private WebElement startDateBox;
	@FindBy(id = "submission-start-time")
	private WebElement startTimeDropdown;
	@FindBy(id = "submission-end-date")
	private WebElement endDateBox;
	@FindBy(id = "submission-end-time")
	private WebElement endTimeDropdown;
	@FindBy(id = "grace-period")
	private WebElement gracePeriodDropdown;
	@FindBy(id = "submission-status")
	private WebElement submissionStatusTextBox;
	@FindBy(id = "published-status")
	private WebElement publishStatusTextBox;
	@FindBy(id = "btn-change-visibility")
	private WebElement changeVisibilityButton;
	@FindBy(id = "session-visibility-custom")
	private WebElement customSessionVisibleTimeButton;
	@FindBy(id = "session-visibility-date")
	private WebElement sessionVisibilityDateBox;
	@FindBy(id = "session-visibility-time")
	private WebElement sessionVisibilityTimeDropdown;
	@FindBy(id = "session-visibility-at-open")
	private WebElement openSessionVisibleTimeButton;
	@FindBy(id = "response-visibility-custom")
	private WebElement customResponseVisibleTimeButton;
	@FindBy(id = "response-visibility-date")
	private WebElement responseVisibilityDateBox;
	@FindBy(id = "response-visibility-time")
	private WebElement responseVisibilityTimeDropdown;
	@FindBy(id = "response-visibility-immediately")
	private WebElement immediateResponseVisibleTimeButton;
	@FindBy(id = "response-visibility-manually")
	private WebElement manualResponseVisibleTimeButton;
	@FindBy(id = "btn-change-email")
	private WebElement changeEmailButton;
	@FindBy(id = "email-opening")
	private WebElement openingSessionEmailCheckbox;
	@FindBy(id = "email-closing")
	private WebElement closingSessionEmailCheckbox;
	@FindBy(id = "email-published")
	private WebElement publishedSessionEmailCheckbox;
	@FindBy(id = "btn-new-question")
	private WebElement addNewQuestionButton;
	@FindBy(id = "btn-copy-question")
	private WebElement copyQuestionButton;
	@FindBy(id = "preview-student")
	private WebElement previewAsStudentDropdown;
	@FindBy(id = "btn-preview-student")
	private WebElement previewAsStudentButton;
	@FindBy(id = "preview-instructor")
	private WebElement previewAsInstructorDropdown;
	@FindBy(id = "btn-preview-instructor")
	private WebElement previewAsInstructorButton;
	@FindBy(id = "course-id")
	private WebElement courseId;
	@FindBy(id = "session-name")
	private WebElement sessionName;
	@FindBy(id = "opening-time")
	private WebElement sessionOpeningTime;
	@FindBy(id = "closing-time")
	private WebElement sessionClosingTime;
	private String name;
	private String name;
	private String name;
	private String name;

    /**
     * Used by subclasses to create a {@code AppPage} object to wrap around the
     * given {@code browser} object. Fails if the page content does not match
     * the page type, as defined by the sub-class.
     */
    public AppPage(Browser browser) {
        this.browser = browser;

        boolean isCorrectPageType;

        try {
            isCorrectPageType = containsExpectedPageContents();

            if (isCorrectPageType) {
                return;
            }
        } catch (Exception e) {
            // ignore and try again
        }

        // To minimize test failures due to eventual consistency, we try to
        //  reload the page and compare once more.
        System.out.println("#### Incorrect page type: going to try reloading the page.");

        ThreadHelper.waitFor(2000);

        reloadPage();

        isCorrectPageType = containsExpectedPageContents();

        if (isCorrectPageType) {
            return;
        }

        System.out.println("######### Not in the correct page! ##########");
        throw new IllegalStateException("Not in the correct page!");
    }

    /**
     * Gets a new page object representation of the currently open web page in the browser.
     *
     * <p>Fails if the new page content does not match content expected in a page of
     * the type indicated by the parameter {@code typeOfPage}.
     */
    public static <T extends AppPage> T getNewPageInstance(Browser currentBrowser, Class<T> typeOfPage) {
        waitUntilAnimationFinish(currentBrowser);
        try {
            Constructor<T> constructor = typeOfPage.getConstructor(Browser.class);
            T page = constructor.newInstance(currentBrowser);
            PageFactory.initElements(currentBrowser.driver, page);
            page.waitForPageToLoad();
            return page;
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof IllegalStateException) {
                throw (IllegalStateException) e.getCause();
            }
            throw new RuntimeException(e);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fails if the new page content does not match content expected in a page of
     * the type indicated by the parameter {@code newPageType}.
     */
    public <T extends AppPage> T changePageType(Class<T> newPageType) {
        return getNewPageInstance(browser, newPageType);
    }

    public <E> E waitFor(ExpectedCondition<E> expectedCondition) {
        WebDriverWait wait = new WebDriverWait(browser.driver, TestProperties.TEST_TIMEOUT);
        return wait.until(expectedCondition);
    }

    /**
     * Waits until the page is fully loaded.
     */
    public void waitForPageToLoad() {
        waitForPageToLoad(false);
    }

    /**
     * Waits until the page is fully loaded.
     *
     * @param excludeToast Set this to true if toast message's disappearance should not be counted
     *         as criteria for page load's completion.
     */
    public void waitForPageToLoad(boolean excludeToast) {
        browser.waitForPageLoad(excludeToast);
    }

    public void waitForElementVisibility(WebElement element) {
        waitFor(ExpectedConditions.visibilityOf(element));
    }

    public void waitForElementVisibility(By by) {
        waitFor(ExpectedConditions.visibilityOfElementLocated(by));
    }

    public void waitForElementToBeClickable(WebElement element) {
        waitFor(ExpectedConditions.elementToBeClickable(element));
    }

    public static void waitUntilAnimationFinish(Browser browser) {
        WebDriverWait wait = new WebDriverWait(browser.driver, 3);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("ng-animating")));
        ThreadHelper.waitFor(1000);
    }

    public void waitUntilAnimationFinish() {
        waitUntilAnimationFinish(browser);
    }

    /**
     * Waits until an element is no longer attached to the DOM or the timeout expires.
     * @param element the WebElement
     * {@link TestProperties#TEST_TIMEOUT} expires
     * @see org.openqa.selenium.support.ui.FluentWait#until(java.util.function.Function)
     */
    public void waitForElementStaleness(WebElement element) {
        waitFor(ExpectedConditions.stalenessOf(element));
    }

    public void verifyUnclickable(WebElement element) {
        if (element.getTagName().equals("a")) {
            assertTrue(element.getAttribute("class").contains("disabled"));
        } else {
            assertNotNull(element.getAttribute("disabled"));
        }
    }

    /**
     * Waits for a confirmation modal to appear and click the confirm button.
     */
    public void waitForConfirmationModalAndClickOk() {
        waitForModalShown();
        WebElement okayButton = browser.driver.findElement(By.className("modal-btn-ok"));
        waitForElementToBeClickable(okayButton);
        clickDismissModalButtonAndWaitForModalHidden(okayButton);
    }

    private void waitForModalShown() {
        // Possible exploration: Change to listening to modal shown event as
        // this is based on the implementation detail assumption that once modal-backdrop is added the modal is shown
        waitForElementVisibility(By.className("modal-backdrop"));
    }

    void waitForModalHidden(WebElement modalBackdrop) {
        // Possible exploration: Change to listening to modal hidden event as
        // this is based on the implementation detail assumption that once modal-backdrop is removed the modal is hidden
        waitForElementStaleness(modalBackdrop);
    }

    /**
     * Waits for the element to appear in the page, up to the timeout specified.
     */
    public WebElement waitForElementPresence(By by) {
        return waitFor(ExpectedConditions.presenceOfElementLocated(by));
    }

    public void reloadPage() {
        browser.goToUrl(browser.driver.getCurrentUrl());
        waitForPageToLoad();
    }

    protected Object executeScript(String script, Object... args) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) browser.driver;
        return javascriptExecutor.executeScript(script, args);
    }

    /**
     * Returns the HTML source of the currently loaded page.
     */
    public String getPageSource() {
        return browser.driver.getPageSource();
    }

    public String getTitle() {
        return browser.driver.getTitle();
    }

    public String getPageTitle() {
        return waitForElementPresence(By.tagName("h1")).getText();
    }

    public void click(By by) {
        WebElement element = browser.driver.findElement(by);
        click(element);
    }

    protected void click(WebElement element) {
        executeScript("arguments[0].click();", element);
    }

    /**
     * Simulates the clearing and sending of keys to an element.
     *
     * <p><b>Note:</b> This method is not the same as using {@link WebElement#clear} followed by {@link WebElement#sendKeys}.
     * It avoids double firing of the {@code change} event which may occur when {@link WebElement#clear} is followed by
     * {@link WebElement#sendKeys}.
     *
     * @see AppPage#clearWithoutEvents(WebElement)
     */
    private void clearAndSendKeys(WebElement element, CharSequence... keysToSend) {
        Map<String, Object> result = clearWithoutEvents(element);
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) result.get("errors");
        if (errors != null) {
            throw new InvalidElementStateException(errors.get("detail"));
        }

        element.sendKeys(keysToSend);
    }

    /**
     * Clears any kind of editable element, but without firing the {@code change} event (unlike {@link WebElement#clear()}).
     * Avoid using this method if {@link WebElement#clear()} meets the requirements as this method depends on implementation
     * details.
     */
    private Map<String, Object> clearWithoutEvents(WebElement element) {
        // This method is a close mirror of HtmlUnitWebElement#clear(), except that events are not handled. Note that
        // HtmlUnitWebElement is mirrored as opposed to RemoteWebElement (which is used with actual browsers) for convenience
        // and the implementation can differ.
        checkNotNull(element);

        // Adapted from ExpectedConditions#stalenessOf which forces a staleness check. This allows a meaningful
        // StaleElementReferenceException to be thrown rather than just getting a boolean from ExpectedConditions.
        element.isEnabled();

        // Fail safe in case the implementation of staleness checks is changed
        if (isExpectedCondition(ExpectedConditions.stalenessOf(element))) {
            throw new AssertionError(
                    "Element is stale but should have been caught earlier by element.isEnabled().");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) executeScript(CLEAR_ELEMENT_SCRIPT, element);
        return result;
    }

    protected void fillTextBox(WebElement textBoxElement, String value) {
        try {
            scrollElementToCenterAndClick(textBoxElement);
        } catch (WebDriverException e) {
            // It is important that a text box element is clickable before we fill it but due to legacy reasons we continue
            // attempting to fill the text box element even if it's not clickable (which may lead to an unexpected failure
            // later on)
            System.out.println("Unexpectedly not able to click on the text box element because of: ");
            System.out.println(e);
        }

        // If the intended value is empty `clear` works well enough for us
        if (value.isEmpty()) {
            textBoxElement.clear();
            return;
        }

        // Otherwise we need to do special handling of entering input because `clear` and `sendKeys` work differently.
        // See documentation for `clearAndSendKeys` for more details.
        clearAndSendKeys(textBoxElement, value);

        textBoxElement.sendKeys(Keys.TAB); // blur the element to receive events
    }

    protected void fillFileBox(RemoteWebElement fileBoxElement, String fileName) {
        if (fileName.isEmpty()) {
            fileBoxElement.clear();
        } else {
            fileBoxElement.setFileDetector(new UselessFileDetector());
            String filePath = new File(fileName).getAbsolutePath();
            fileBoxElement.sendKeys(filePath);
        }
    }

    /**
     * Get rich text from editor.
     */
    protected String getEditorRichText(WebElement editor) {
        waitForElementPresence(By.tagName("iframe"));
        String id = editor.findElement(By.tagName("textarea")).getAttribute("id");
        return (String) ((JavascriptExecutor) browser.driver)
                .executeAsyncScript(READ_TINYMCE_CONTENT_SCRIPT, id);
    }

    /**
     * Write rich text to editor.
     */
    protected void writeToRichTextEditor(WebElement editor, String text) {
        waitForElementPresence(By.tagName("iframe"));
        String id = editor.findElement(By.tagName("textarea")).getAttribute("id");
        ((JavascriptExecutor) browser.driver).executeAsyncScript(WRITE_TO_TINYMCE_SCRIPT, id, text);
    }

    /**
     * Select the option, if it is not already selected.
     * No action taken if it is already selected.
     */
    protected void markOptionAsSelected(WebElement option) {
        waitForElementVisibility(option);
        if (!option.isSelected()) {
            click(option);
        }
    }

    /**
     * Unselect the option, if it is not already unselected.
     * No action taken if it is already unselected'.
     */
    protected void markOptionAsUnselected(WebElement option) {
        waitForElementVisibility(option);
        if (option.isSelected()) {
            click(option);
        }
    }

    /**
     * Returns the text of the option selected in the dropdown.
     */
    protected String getSelectedDropdownOptionText(WebElement dropdown) {
        Select select = new Select(dropdown);
        try {
            uiRetryManager.runUntilNoRecognizedException(new Retryable("Wait for dropdown text to load") {
                public void run() {
                    String txt = select.getFirstSelectedOption().getText();
                    assertNotEquals("", txt);
                }
            }, WebDriverException.class, AssertionError.class);
            return select.getFirstSelectedOption().getText();
        } catch (MaximumRetriesExceededException e) {
            return select.getFirstSelectedOption().getText();
        }
    }

    /**
     * Selects option in dropdown based on visible text.
     */
    protected void selectDropdownOptionByText(WebElement dropdown, String text) {
        Select select = new Select(dropdown);
        select.selectByVisibleText(text);
    }

    /**
     * Selects option in dropdown based on value.
     */
    protected void selectDropdownOptionByValue(WebElement dropdown, String value) {
        Select select = new Select(dropdown);
        select.selectByValue(value);
    }

    /**
     * Asserts that all values in the body of the given table are equal to the expectedTableBodyValues.
     */
    protected void verifyTableBodyValues(WebElement table, String[][] expectedTableBodyValues) {
        List<WebElement> rows = table.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
        assertTrue(expectedTableBodyValues.length <= rows.size());
        for (int rowIndex = 0; rowIndex < expectedTableBodyValues.length; rowIndex++) {
            verifyTableRowValues(rows.get(rowIndex), expectedTableBodyValues[rowIndex]);
        }
    }

    /**
     * Asserts that all values in the given table row are equal to the expectedRowValues.
     */
    protected void verifyTableRowValues(WebElement row, String[] expectedRowValues) {
        List<WebElement> cells = row.findElements(By.tagName("td"));
        assertTrue(expectedRowValues.length <= cells.size());
        for (int cellIndex = 0; cellIndex < expectedRowValues.length; cellIndex++) {
            assertEquals(expectedRowValues[cellIndex], cells.get(cellIndex).getText());
        }
    }

    /**
     * Clicks the element and clicks 'Yes' in the follow up dialog box.
     * Fails if there is no dialog box.
     * @return the resulting page.
     */
    public AppPage clickAndConfirm(WebElement elementToClick) {
        click(elementToClick);
        waitForConfirmationModalAndClickOk();
        return this;
    }

    /**
     * Returns True if there is a corresponding element for the given locator.
     */
    public boolean isElementPresent(By by) {
        return browser.driver.findElements(by).size() != 0;
    }

    /**
     * Returns True if there is a corresponding element for the given id or name.
     */
    public boolean isElementPresent(String elementId) {
        try {
            browser.driver.findElement(By.id(elementId));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean isElementVisible(By by) {
        try {
            return browser.driver.findElement(by).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Returns true if the expected condition is evaluated to true immediately.
     * @see ExpectedConditions
     */
    private boolean isExpectedCondition(ExpectedCondition<?> expectedCondition) {
        Object value = expectedCondition.apply(browser.driver);
        if (value == null) {
            return false;
        }

        if (value.getClass() == Boolean.class) {
            return (boolean) value;
        } else {
            return true;
        }
    }

    /**
     * Clicks a button (can be inside or outside the modal) that dismisses the modal and waits for the modal to be hidden.
     * The caller must ensure the button is in the modal or a timeout will occur while waiting for the modal to be hidden.
     * @param dismissModalButton a button that dismisses the modal
     */
    public void clickDismissModalButtonAndWaitForModalHidden(WebElement dismissModalButton) {
        // Note: Should first check if the button can actually dismiss the modal otherwise the state will be consistent.
        // However, it is too difficult to check.

        WebElement modalBackdrop = browser.driver.findElement(By.className("modal-backdrop"));

        click(dismissModalButton);
        waitForModalHidden(modalBackdrop);
    }

    /**
     * Scrolls element to center and clicks on it.
     *
     * <p>As compared to {@link org.openqa.selenium.interactions.Actions#moveToElement(WebElement)}, this method is
     * more reliable as the element will not get blocked by elements such as the header.
     *
     * <p>Furthermore, {@link org.openqa.selenium.interactions.Actions#moveToElement(WebElement)} is currently not
     * working in Geckodriver.
     *
     * <p><b>Note:</b> A "scroll into view" Actions primitive is in progress and may allow scrolling element to center.
     * Tracking issue:
     * <a href="https://github.com/w3c/webdriver/issues/1005">Missing "scroll into view" Actions primitive</a>.
     *
     * <p>Also note that there are some other caveats, for example
     * {@code new Actions(browser.driver).moveToElement(...).click(...).perform()} does not behave consistently across
     * browsers.
     * <ul>
     * <li>In FirefoxDriver, the element is scrolled to and then a click is attempted on the element.
     * <li>In ChromeDriver, the mouse is scrolled to the element and then a click is attempted on the mouse coordinate,
     * which means another element can actually be clicked (such as the header or a blocking pop-up).
     * </ul>
     *
     * <p>ChromeDriver also automatically scrolls to an element when clicking an element if it is not in the viewport.
     */
    void scrollElementToCenterAndClick(WebElement element) {
        // TODO: migrate to `scrollIntoView` when Geckodriver is adopted
        scrollElementToCenter(element);
        element.click();
    }

    /**
     * Scrolls element to center.
     */
    void scrollElementToCenter(WebElement element) {
        executeScript(SCROLL_ELEMENT_TO_CENTER_AND_CLICK_SCRIPT, element);
    }

    /**
     * Asserts message in toast is equal to the expected message.
     */
    public void verifyStatusMessage(String expectedMessage) {
        verifyStatusMessageWithLinks(expectedMessage, new String[] {});
    }

    /**
     * Asserts message in toast is equal to the expected message and contains the expected links.
     */
    public void verifyStatusMessageWithLinks(String expectedMessage, String[] expectedLinks) {
        WebElement[] statusMessage = new WebElement[1];
        try {
            uiRetryManager.runUntilNoRecognizedException(new Retryable("Verify status to user") {
                public void run() {
                    statusMessage[0] = waitForElementPresence(By.className("toast-body"));
                    assertEquals(expectedMessage, statusMessage[0].getText());
                }
            }, WebDriverException.class, AssertionError.class);
        } catch (MaximumRetriesExceededException e) {
            statusMessage[0] = waitForElementPresence(By.className("toast-body"));
            assertEquals(expectedMessage, statusMessage[0].getText());
        } finally {
            if (expectedLinks.length > 0) {
                List<WebElement> actualLinks = statusMessage[0].findElements(By.tagName("a"));
                for (int i = 0; i < expectedLinks.length; i++) {
                    assertTrue(actualLinks.get(i).getAttribute("href").contains(expectedLinks[i]));
                }
            }
        }
    }

    /**
     * Switches to the new browser window just opened.
     */
    protected void switchToNewWindow() {
        browser.switchToNewWindow();
    }

    /**
     * Closes current window and switches back to parent window.
     */
    public void closeCurrentWindowAndSwitchToParentWindow() {
        browser.closeCurrentWindowAndSwitchToParentWindow();
    }

    String getDisplayGiverName(FeedbackParticipantType type) {
        switch (type) {
        case SELF:
            return "Feedback session creator (i.e., me)";
        case STUDENTS:
            return "Students in this course";
        case INSTRUCTORS:
            return "Instructors in this course";
        case TEAMS:
            return "Teams in this course";
        default:
            throw new IllegalArgumentException("Unknown FeedbackParticipantType: " + type);
        }
    }

    String getDisplayRecipientName(FeedbackParticipantType type) {
        switch (type) {
        case SELF:
            return "Giver (Self feedback)";
        case STUDENTS:
        case STUDENTS_EXCLUDING_SELF:
            return "Other students in the course";
        case INSTRUCTORS:
            return "Instructors in the course";
        case TEAMS:
        case TEAMS_EXCLUDING_SELF:
            return "Other teams in the course";
        case OWN_TEAM:
            return "Giver's team";
        case OWN_TEAM_MEMBERS:
            return "Giver's team members";
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            return "Giver's team members and Giver";
        case NONE:
            return "Nobody specific (For general class feedback)";
        default:
            throw new IllegalArgumentException("Unknown FeedbackParticipantType: " + type);
        }
    }

    String getDisplayedDateTime(Instant instant, String timeZone, String pattern) {
        ZonedDateTime zonedDateTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(instant, timeZone, false)
                .atZone(ZoneId.of(timeZone));
        return DateTimeFormatter.ofPattern(pattern).format(zonedDateTime);
    }

	@Override
	protected boolean containsExpectedPageContents() {
	    return getPageTitle().contains("Edit Feedback Session");
	}

	public void verifySessionDetails(CourseAttributes course, FeedbackSessionAttributes feedbackSession) {
	    waitForElementPresence(By.id("instructions"));
	    assertEquals(getCourseId(), course.getId());
	    assertEquals(getCourseName(), course.getName());
	    assertEquals(getTimeZone(), feedbackSession.getTimeZone());
	    assertEquals(getFeedbackSessionName(), feedbackSession.getFeedbackSessionName());
	    assertEquals(getInstructions(), feedbackSession.getInstructions());
	    assertEquals(getStartDate(), getDateString(feedbackSession.getStartTime(), feedbackSession.getTimeZone()));
	    assertEquals(getStartTime(), getTimeString(feedbackSession.getStartTime(), feedbackSession.getTimeZone()));
	    assertEquals(getEndDate(), getDateString(feedbackSession.getEndTime(), feedbackSession.getTimeZone()));
	    assertEquals(getEndTime(), getTimeString(feedbackSession.getEndTime(), feedbackSession.getTimeZone()));
	    assertEquals(getGracePeriod(), feedbackSession.getGracePeriodMinutes() + " min");
	    verifySubmissionStatus(feedbackSession);
	    verifyPublishedStatus(feedbackSession);
	    verifyVisibilitySettings(feedbackSession);
	    verifyEmailSettings(feedbackSession);
	}

	private void verifySubmissionStatus(FeedbackSessionAttributes feedbackSession) {
	    String submissionStatus = getSubmissionStatus();
	    if (feedbackSession.isClosed()) {
	        assertEquals(submissionStatus, "Closed");
	    } else if (feedbackSession.isVisible() && (feedbackSession.isOpened() || feedbackSession.isInGracePeriod())) {
	        assertEquals(submissionStatus, "Open");
	    } else {
	        assertEquals(submissionStatus, "Awaiting");
	    }
	}

	private void verifyPublishedStatus(FeedbackSessionAttributes feedbackSession) {
	    String publishedStatus = getPublishedStatus();
	    if (feedbackSession.isPublished()) {
	        assertEquals(publishedStatus, "Published");
	    } else {
	        assertEquals(publishedStatus, "Not Published");
	    }
	}

	private void verifyVisibilitySettings(FeedbackSessionAttributes feedbackSession) {
	    Instant sessionVisibleTime = feedbackSession.getSessionVisibleFromTime();
	    Instant responseVisibleTime = feedbackSession.getResultsVisibleFromTime();
	
	    // Default settings, assert setting section not expanded
	    if (sessionVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)
	            && responseVisibleTime.equals(Const.TIME_REPRESENTS_LATER)) {
	        assertTrue(isElementPresent("btn-change-visibility"));
	        return;
	    }
	    verifySessionVisibilitySettings(sessionVisibleTime, feedbackSession);
	    verifyResponseVisibilitySettings(responseVisibleTime, feedbackSession);
	}

	private void verifySessionVisibilitySettings(Instant sessionVisibleTime, FeedbackSessionAttributes feedbackSession) {
	    if (sessionVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
	        assertTrue(openSessionVisibleTimeButton.isSelected());
	    } else {
	        assertTrue(customSessionVisibleTimeButton.isSelected());
	        assertEquals(getSessionVisibilityDate(), getDateString(feedbackSession.getSessionVisibleFromTime(),
	                feedbackSession.getTimeZone()));
	        assertEquals(getSessionVisibilityTime(), getTimeString(feedbackSession.getSessionVisibleFromTime(),
	                feedbackSession.getTimeZone()));
	    }
	}

	private void verifyResponseVisibilitySettings(Instant responseVisibleTime, FeedbackSessionAttributes feedbackSession) {
	    if (responseVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
	        assertTrue(immediateResponseVisibleTimeButton.isSelected());
	    } else if (responseVisibleTime.equals(Const.TIME_REPRESENTS_LATER)) {
	        assertTrue(manualResponseVisibleTimeButton.isSelected());
	    } else {
	        assertTrue(customSessionVisibleTimeButton.isSelected());
	        assertEquals(getResponseVisibilityDate(), getDateString(feedbackSession.getResultsVisibleFromTime(),
	                feedbackSession.getTimeZone()));
	        assertEquals(getResponseVisibilityTime(), getTimeString(feedbackSession.getResultsVisibleFromTime(),
	                feedbackSession.getTimeZone()));
	    }
	}

	private void verifyEmailSettings(FeedbackSessionAttributes feedbackSession) {
	    boolean isOpeningEmailEnabled = feedbackSession.isOpeningEmailEnabled();
	    boolean isClosingEmailEnabled = feedbackSession.isClosingEmailEnabled();
	    boolean isPublishedEmailEnabled = feedbackSession.isPublishedEmailEnabled();
	
	    // Default settings, assert setting section not expanded
	    if (isOpeningEmailEnabled && isClosingEmailEnabled && isPublishedEmailEnabled) {
	        assertTrue(isElementPresent("btn-change-email"));
	        return;
	    }
	    if (isOpeningEmailEnabled) {
	        assertTrue(openingSessionEmailCheckbox.isSelected());
	    }
	    if (isClosingEmailEnabled) {
	        assertTrue(closingSessionEmailCheckbox.isSelected());
	    }
	    if (isPublishedEmailEnabled) {
	        assertTrue(publishedSessionEmailCheckbox.isSelected());
	    }
	}

	public void editSessionDetails(FeedbackSessionAttributes newFeedbackSessionDetails) {
	    click(fsEditButton);
	    setInstructions(newFeedbackSessionDetails.getInstructions());
	    setSessionStartDateTime(newFeedbackSessionDetails.getStartTime(), newFeedbackSessionDetails.getTimeZone());
	    setSessionEndDateTime(newFeedbackSessionDetails.getEndTime(), newFeedbackSessionDetails.getTimeZone());
	    selectGracePeriod(newFeedbackSessionDetails.getGracePeriodMinutes());
	    setVisibilitySettings(newFeedbackSessionDetails);
	    setEmailSettings(newFeedbackSessionDetails);
	    click(fsSaveButton);
	}

	public void copySessionToOtherCourse(CourseAttributes otherCourse, String sessionName) {
	    click(fsCopyButton);
	    WebElement copyFsModal = waitForElementPresence(By.id("copy-course-modal"));
	
	    fillTextBox(copyFsModal.findElement(By.id("copy-session-name")), sessionName);
	    List<WebElement> options = copyFsModal.findElements(By.className("form-check"));
	    for (WebElement option : options) {
	        String courseId = option.findElement(By.cssSelector("label span")).getText();
	        if (courseId.equals(otherCourse.getId())) {
	            click(option.findElement(By.tagName("input")));
	            break;
	        }
	    }
	    click(browser.driver.findElement(By.id("btn-confirm-copy-course")));
	}

	public void deleteSession() {
	    clickAndConfirm(waitForElementPresence(By.id("btn-fs-delete")));
	}

	public FeedbackSubmitPage previewAsStudent(StudentAttributes student) {
	    selectDropdownOptionByText(previewAsStudentDropdown, String.format("[%s] %s", student.getTeam(), student.getName()));
	    click(previewAsStudentButton);
	    ThreadHelper.waitFor(2000);
	    switchToNewWindow();
	    return changePageType(FeedbackSubmitPage.class);
	}

	public FeedbackSubmitPage previewAsInstructor(InstructorAttributes instructor) {
	    selectDropdownOptionByText(previewAsInstructorDropdown, instructor.getName());
	    click(previewAsInstructorButton);
	    ThreadHelper.waitFor(2000);
	    switchToNewWindow();
	    return changePageType(FeedbackSubmitPage.class);
	}

	public void verifyNumQuestions(int expected) {
	    assertEquals(getNumQuestions(), expected);
	}

	public void verifyQuestionDetails(int questionNum, EntityAttributes<FeedbackQuestion> feedbackQuestion) {
	    scrollElementToCenter(getQuestionForm(questionNum));
	    assertEquals(feedbackQuestion.getQuestionType(), getQuestionType(questionNum));
	    assertEquals(feedbackQuestion.getQuestionNumber(), getQuestionNumber(questionNum));
	    assertEquals(feedbackQuestion.getQuestionDetailsCopy().getQuestionText(), getQuestionBrief(questionNum));
	    assertEquals(getQuestionDescription(questionNum), feedbackQuestion.getQuestionDescription());
	    verifyFeedbackPathSettings(questionNum, feedbackQuestion);
	    verifyQuestionVisibilitySettings(questionNum, feedbackQuestion);
	}

	private void verifyFeedbackPathSettings(int questionNum, EntityAttributes<FeedbackQuestion> feedbackQuestion) {
	    assertEquals(getDisplayGiverName(feedbackQuestion.getGiverType()), getFeedbackGiver(questionNum));
	    String feedbackReceiver = getFeedbackReceiver(questionNum);
	    assertEquals(getDisplayRecipientName(feedbackQuestion.getRecipientType()), feedbackReceiver);
	
	    if (feedbackReceiver.equals(getDisplayRecipientName(FeedbackParticipantType.INSTRUCTORS))
	            || feedbackReceiver.equals(getDisplayRecipientName(FeedbackParticipantType.STUDENTS_EXCLUDING_SELF))
	            || feedbackReceiver.equals(getDisplayRecipientName(FeedbackParticipantType.TEAMS_EXCLUDING_SELF))) {
	        verifyNumberOfEntitiesToGiveFeedbackTo(questionNum, feedbackQuestion.getNumberOfEntitiesToGiveFeedbackTo());
	    }
	}

	private void verifyNumberOfEntitiesToGiveFeedbackTo(int questionNum, int numberOfEntitiesToGiveFeedbackTo) {
	    WebElement questionForm = getQuestionForm(questionNum);
	    WebElement feedbackPathPanel = questionForm.findElement(By.tagName("tm-feedback-path-panel"));
	    if (numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS) {
	        assertTrue(feedbackPathPanel.findElement(By.id("unlimited-recipients")).isSelected());
	    } else {
	        assertTrue(feedbackPathPanel.findElement(By.id("custom-recipients")).isSelected());
	        assertEquals(feedbackPathPanel.findElement(By.id("custom-recipients-number")).getAttribute("value"),
	                Integer.toString(numberOfEntitiesToGiveFeedbackTo));
	    }
	}

	private void verifyQuestionVisibilitySettings(int questionNum, EntityAttributes<FeedbackQuestion> feedbackQuestion) {
	    WebElement questionForm = getQuestionForm(questionNum);
	    WebElement visibilityPanel = questionForm.findElement(By.tagName("tm-visibility-panel"));
	    String visibility = visibilityPanel.findElement(By.cssSelector("#btn-question-visibility span")).getText();
	    List<FeedbackParticipantType> showResponsesTo = feedbackQuestion.getShowResponsesTo();
	    List<FeedbackParticipantType> showGiverNameTo = feedbackQuestion.getShowGiverNameTo();
	    List<FeedbackParticipantType> showRecipientNameTo = feedbackQuestion.getShowRecipientNameTo();
	
	    switch (visibility) {
	    case "Shown anonymously to recipient and giver's team members, visible to instructors":
	        assertTrue(showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS));
	        assertTrue(showResponsesTo.contains(FeedbackParticipantType.RECEIVER));
	        assertTrue(showResponsesTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS));
	        assertEquals(showResponsesTo.size(), 3);
	
	        assertTrue(showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
	        assertEquals(showGiverNameTo.size(), 1);
	
	        assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
	        assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER));
	        assertEquals(showRecipientNameTo.size(), 2);
	        break;
	
	    case "Visible to instructors only":
	        assertTrue(showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS));
	        assertEquals(showResponsesTo.size(), 1);
	
	        assertTrue(showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
	        assertEquals(showGiverNameTo.size(), 1);
	
	        assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
	        assertEquals(showRecipientNameTo.size(), 1);
	        break;
	
	    case "Shown anonymously to recipient and instructors":
	        assertTrue(showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS));
	        assertTrue(showResponsesTo.contains(FeedbackParticipantType.RECEIVER));
	        assertEquals(showResponsesTo.size(), 2);
	
	        assertEquals(showGiverNameTo.size(), 0);
	
	        assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
	        assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER));
	        assertEquals(showRecipientNameTo.size(), 2);
	        break;
	
	    case "Shown anonymously to recipient, visible to instructors":
	        assertTrue(showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS));
	        assertTrue(showResponsesTo.contains(FeedbackParticipantType.RECEIVER));
	        assertEquals(showResponsesTo.size(), 2);
	
	        assertTrue(showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
	        assertEquals(showGiverNameTo.size(), 1);
	
	        assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
	        assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER));
	        assertEquals(showRecipientNameTo.size(), 2);
	        break;
	
	    case "Shown anonymously to recipient and giver/recipient's team members, visible to instructors":
	        assertTrue(showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS));
	        assertTrue(showResponsesTo.contains(FeedbackParticipantType.RECEIVER));
	        assertTrue(showResponsesTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS));
	        assertTrue(showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
	        assertEquals(showResponsesTo.size(), 4);
	
	        assertTrue(showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
	        assertEquals(showGiverNameTo.size(), 1);
	
	        assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
	        assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER));
	        assertEquals(showRecipientNameTo.size(), 2);
	        break;
	
	    case "Visible to recipient and instructors":
	        assertTrue(showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS));
	        assertTrue(showResponsesTo.contains(FeedbackParticipantType.RECEIVER));
	        assertEquals(showResponsesTo.size(), 2);
	
	        assertTrue(showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
	        assertTrue(showGiverNameTo.contains(FeedbackParticipantType.RECEIVER));
	        assertEquals(showGiverNameTo.size(), 2);
	
	        assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
	        assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER));
	        assertEquals(showRecipientNameTo.size(), 2);
	        break;
	
	    default:
	        verifyCustomQuestionVisibility(questionNum, feedbackQuestion);
	        break;
	    }
	}

	private void verifyCustomQuestionVisibility(int questionNum, EntityAttributes<FeedbackQuestion> feedbackQuestion) {
	    WebElement questionForm = getQuestionForm(questionNum);
	    WebElement visibilityPanel = questionForm.findElement(By.tagName("tm-visibility-panel"));
	    String visibility = visibilityPanel.findElement(By.cssSelector("#btn-question-visibility span")).getText();
	    assertEquals(visibility, CUSTOM_VISIBILITY_OPTION);
	
	    FeedbackParticipantType giver = feedbackQuestion.getGiverType();
	    FeedbackParticipantType receiver = feedbackQuestion.getRecipientType();
	    WebElement customVisibilityTable = visibilityPanel.findElement(By.id("custom-visibility-table"));
	    assertVisibilityBoxesSelected(customVisibilityTable, giver, receiver, feedbackQuestion.getShowResponsesTo(), 1);
	    assertVisibilityBoxesSelected(customVisibilityTable, giver, receiver, feedbackQuestion.getShowGiverNameTo(), 2);
	    assertVisibilityBoxesSelected(customVisibilityTable, giver, receiver, feedbackQuestion.getShowRecipientNameTo(), 3);
	}

	private void assertVisibilityBoxesSelected(WebElement table, FeedbackParticipantType giver, FeedbackParticipantType receiver, List<FeedbackParticipantType> participants, int colNum) {
	    List<FeedbackParticipantType> possibleTypes = new ArrayList<>(Arrays.asList(FeedbackParticipantType.RECEIVER,
	            FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
	            FeedbackParticipantType.STUDENTS, FeedbackParticipantType.INSTRUCTORS));
	    if (!giver.equals(FeedbackParticipantType.STUDENTS)) {
	        possibleTypes.remove(FeedbackParticipantType.OWN_TEAM_MEMBERS);
	    }
	    if (!receiver.equals(FeedbackParticipantType.STUDENTS)) {
	        possibleTypes.remove(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
	    }
	    if (receiver.equals(FeedbackParticipantType.NONE)
	            || receiver.equals(FeedbackParticipantType.SELF)
	            || receiver.equals(FeedbackParticipantType.OWN_TEAM)) {
	        possibleTypes.remove(FeedbackParticipantType.RECEIVER);
	        possibleTypes.remove(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
	    }
	
	    List<WebElement> rows = table.findElements(By.tagName("tr"));
	    int index = colNum - 1;
	    for (FeedbackParticipantType participant : participants) {
	        assertTrue(rows.get(possibleTypes.indexOf(participant)).findElements(By.tagName("input")).get(index)
	                .isSelected());
	    }
	}

	public void addTemplateQuestion(int optionNum) {
	    addNewQuestion(1);
	    WebElement templateQuestionModal = waitForElementPresence(By.id("template-question-modal"));
	
	    click(templateQuestionModal.findElements(By.tagName("input")).get(optionNum - 1));
	    clickAndWaitForNewQuestion(browser.driver.findElement(By.id("btn-confirm-template")));
	}

	public void copyQuestion(String courseId, String questionText) {
	    click(copyQuestionButton);
	    WebElement copyQuestionModal = waitForElementPresence(By.id("copy-question-modal"));
	
	    List<WebElement> rows = copyQuestionModal.findElements(By.cssSelector("tbody tr"));
	    for (WebElement row : rows) {
	        List<WebElement> cells = row.findElements(By.tagName("td"));
	        if (cells.get(1).getText().equals(courseId) && cells.get(4).getText().equals(questionText)) {
	            markOptionAsSelected(cells.get(0).findElement(By.tagName("input")));
	        }
	    }
	    clickAndWaitForNewQuestion(browser.driver.findElement(By.id("btn-confirm-copy-question")));
	}

	public void editQuestionNumber(int questionNum, int newQuestionNumber) {
	    clickEditQuestionButton(questionNum);
	    selectDropdownOptionByText(getQuestionForm(questionNum).findElement(By.id("question-number-dropdown")),
	            Integer.toString(newQuestionNumber));
	    clickSaveQuestionButton(questionNum);
	}

	public void editQuestionDetails(int questionNum, EntityAttributes<FeedbackQuestion> feedbackQuestion) {
	    clickEditQuestionButton(questionNum);
	    inputQuestionDetails(questionNum, feedbackQuestion);
	    clickSaveQuestionButton(questionNum);
	}

	private void inputQuestionDetails(int questionNum, EntityAttributes<FeedbackQuestion> feedbackQuestion) {
	    setQuestionBrief(questionNum, feedbackQuestion.getQuestionDetailsCopy().getQuestionText());
	    setQuestionDescription(questionNum, feedbackQuestion.getQuestionDescription());
	    FeedbackQuestionType questionType = feedbackQuestion.getQuestionType();
	    if (!questionType.equals(FeedbackQuestionType.CONTRIB)) {
	        setFeedbackPath(questionNum, feedbackQuestion);
	        setQuestionVisibility(questionNum, feedbackQuestion);
	    }
	}

	public void duplicateQuestion(int questionNum) {
	    clickAndWaitForNewQuestion(getQuestionForm(questionNum).findElement(By.id("btn-duplicate-question")));
	}

	public void deleteQuestion(int questionNum) {
	    clickAndConfirm(getQuestionForm(questionNum).findElement(By.id("btn-delete-question")));
	}

	public void verifyTextQuestionDetails(int questionNum, FeedbackTextQuestionDetails questionDetails) {
	    String recommendLength = getRecommendedTextLengthField(questionNum).getAttribute("value");
	    assertEquals(recommendLength, questionDetails.getRecommendedLength().toString());
	}

	public void addTextQuestion(EntityAttributes<FeedbackQuestion> feedbackQuestion) {
	    addNewQuestion(2);
	    int questionNum = getNumQuestions();
	    inputQuestionDetails(questionNum, feedbackQuestion);
	    FeedbackTextQuestionDetails questionDetails =
	            (FeedbackTextQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
	    fillTextBox(getRecommendedTextLengthField(questionNum), questionDetails.getRecommendedLength().toString());
	    clickSaveNewQuestionButton();
	}

	public void editTextQuestion(int questionNum, FeedbackTextQuestionDetails textQuestionDetails) {
	    clickEditQuestionButton(questionNum);
	    WebElement recommendedTextLengthField = getRecommendedTextLengthField(questionNum);
	    waitForElementToBeClickable(recommendedTextLengthField);
	    fillTextBox(recommendedTextLengthField, textQuestionDetails.getRecommendedLength().toString());
	    clickSaveQuestionButton(questionNum);
	}

	public void verifyMcqQuestionDetails(int questionNum, FeedbackMcqQuestionDetails questionDetails) {
	    if (verifyGeneratedOptions(questionNum, questionDetails.getGenerateOptionsFor())) {
	        return;
	    }
	    verifyOptions(questionNum, questionDetails.getMcqChoices());
	    verifyOptionWeights(questionNum, questionDetails.isHasAssignedWeights(), questionDetails.getMcqWeights());
	    verifyOtherOption(questionNum, questionDetails.isOtherEnabled(), questionDetails.getMcqOtherWeight());
	}

	public void addMcqQuestion(EntityAttributes<FeedbackQuestion> feedbackQuestion) {
	    addNewQuestion(3);
	    int questionNum = getNumQuestions();
	    inputQuestionDetails(questionNum, feedbackQuestion);
	    FeedbackMcqQuestionDetails questionDetails = (FeedbackMcqQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
	    inputMcqDetails(questionNum, questionDetails);
	    clickSaveNewQuestionButton();
	}

	public void editMcqQuestion(int questionNum, FeedbackMcqQuestionDetails questionDetails) {
	    clickEditQuestionButton(questionNum);
	    inputMcqDetails(questionNum, questionDetails);
	    clickSaveQuestionButton(questionNum);
	}

	public void verifyMsqQuestionDetails(int questionNum, FeedbackMsqQuestionDetails questionDetails) {
	    verifyMaxOptions(questionNum, questionDetails.getMaxSelectableChoices());
	    verifyMinOptions(questionNum, questionDetails.getMinSelectableChoices());
	    if (verifyGeneratedOptions(questionNum, questionDetails.getGenerateOptionsFor())) {
	        return;
	    }
	    verifyOptions(questionNum, questionDetails.getMsqChoices());
	    verifyOptionWeights(questionNum, questionDetails.isHasAssignedWeights(), questionDetails.getMsqWeights());
	    verifyOtherOption(questionNum, questionDetails.isOtherEnabled(), questionDetails.getMsqOtherWeight());
	}

	public void addMsqQuestion(EntityAttributes<FeedbackQuestion> feedbackQuestion) {
	    addNewQuestion(4);
	    int questionNum = getNumQuestions();
	    inputQuestionDetails(questionNum, feedbackQuestion);
	    FeedbackMsqQuestionDetails questionDetails = (FeedbackMsqQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
	    inputMsqDetails(questionNum, questionDetails);
	    clickSaveNewQuestionButton();
	}

	public void editMsqQuestion(int questionNum, FeedbackMsqQuestionDetails msqQuestionDetails) {
	    clickEditQuestionButton(questionNum);
	    inputMsqDetails(questionNum, msqQuestionDetails);
	    clickSaveQuestionButton(questionNum);
	}

	public void verifyNumScaleQuestionDetails(int questionNum, FeedbackNumericalScaleQuestionDetails questionDetails) {
	    assertEquals(getMinNumscaleInput(questionNum).getAttribute("value"),
	            Integer.toString(questionDetails.getMinScale()));
	    assertEquals(getNumScaleIncrementInput(questionNum).getAttribute("value"),
	            getDoubleString(questionDetails.getStep()));
	    assertEquals(getMaxNumscaleInput(questionNum).getAttribute("value"),
	            Integer.toString(questionDetails.getMaxScale()));
	}

	public void addNumScaleQuestion(EntityAttributes<FeedbackQuestion> feedbackQuestion) {
	    addNewQuestion(5);
	    int questionNum = getNumQuestions();
	    inputQuestionDetails(questionNum, feedbackQuestion);
	    FeedbackNumericalScaleQuestionDetails questionDetails =
	            (FeedbackNumericalScaleQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
	    inputNumScaleDetails(questionNum, questionDetails);
	    clickSaveNewQuestionButton();
	}

	public void editNumScaleQuestion(int questionNum, FeedbackNumericalScaleQuestionDetails questionDetails) {
	    clickEditQuestionButton(questionNum);
	    inputNumScaleDetails(questionNum, questionDetails);
	    clickSaveQuestionButton(questionNum);
	}

	public void verifyConstSumQuestionDetails(int questionNum, FeedbackConstantSumQuestionDetails questionDetails) {
	    if (!questionDetails.isDistributeToRecipients()) {
	        verifyOptions(questionNum, questionDetails.getConstSumOptions());
	    }
	
	    if (questionDetails.isPointsPerOption()) {
	        assertTrue(getConstSumPerOptionPointsRadioBtn(questionNum).isSelected());
	        assertEquals(getConstSumPerOptionPointsInput(questionNum).getAttribute("value"),
	                Integer.toString(questionDetails.getPoints()));
	        assertFalse(getConstSumTotalPointsRadioBtn(questionNum).isSelected());
	    } else {
	        assertTrue(getConstSumTotalPointsRadioBtn(questionNum).isSelected());
	        assertEquals(getConstSumTotalPointsInput(questionNum).getAttribute("value"),
	                Integer.toString(questionDetails.getPoints()));
	        assertFalse(getConstSumPerOptionPointsRadioBtn(questionNum).isSelected());
	    }
	
	    if (questionDetails.isForceUnevenDistribution()) {
	        String distributeFor = questionDetails.getDistributePointsFor();
	        assertTrue(getConstSumUnevenDistributionCheckbox(questionNum).isSelected());
	        assertEquals(getSelectedDropdownOptionText(getConstSumUnevenDistributionDropdown(questionNum)).trim(),
	                "All options".equals(distributeFor) ? "Every option" : distributeFor);
	    } else {
	        assertFalse(getConstSumUnevenDistributionCheckbox(questionNum).isSelected());
	    }
	}

	public void addConstSumOptionQuestion(EntityAttributes<FeedbackQuestion> feedbackQuestion) {
	    addNewQuestion(6);
	    addConstSumQuestion(feedbackQuestion);
	}

	public void addConstSumRecipientQuestion(EntityAttributes<FeedbackQuestion> feedbackQuestion) {
	    addNewQuestion(7);
	    addConstSumQuestion(feedbackQuestion);
	}

	public void addConstSumQuestion(EntityAttributes<FeedbackQuestion> feedbackQuestion) {
	    int questionNum = getNumQuestions();
	    inputQuestionDetails(questionNum, feedbackQuestion);
	    FeedbackConstantSumQuestionDetails questionDetails =
	            (FeedbackConstantSumQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
	    inputConstSumDetails(questionNum, questionDetails);
	    clickSaveNewQuestionButton();
	}

	public void editConstSumQuestion(int questionNum, FeedbackConstantSumQuestionDetails csQuestionDetails) {
	    clickEditQuestionButton(questionNum);
	    inputConstSumDetails(questionNum, csQuestionDetails);
	    clickSaveQuestionButton(questionNum);
	}

	public void verifyContributionQuestionDetails(int questionNum, FeedbackContributionQuestionDetails questionDetails) {
	    assertEquals(questionDetails.isNotSureAllowed(), getAllowNotSureContributionCheckbox(questionNum).isSelected());
	}

	public void addContributionQuestion(EntityAttributes<FeedbackQuestion> feedbackQuestion) {
	    addNewQuestion(8);
	    int questionNum = getNumQuestions();
	    inputQuestionDetails(questionNum, feedbackQuestion);
	    FeedbackContributionQuestionDetails questionDetails =
	            (FeedbackContributionQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
	    inputContributionDetails(questionNum, questionDetails);
	    clickSaveNewQuestionButton();
	}

	public void editContributionQuestion(int questionNum, FeedbackContributionQuestionDetails questionDetails) {
	    clickEditQuestionButton(questionNum);
	    inputContributionDetails(questionNum, questionDetails);
	    clickSaveQuestionButton(questionNum);
	}

	public void verifyRubricQuestionDetails(int questionNum, FeedbackRubricQuestionDetails questionDetails) {
	    int numChoices = questionDetails.getNumOfRubricChoices();
	    List<String> choices = questionDetails.getRubricChoices();
	    for (int i = 0; i < numChoices; i++) {
	        assertEquals(choices.get(i), getRubricChoiceInputs(questionNum).get(i).getAttribute("value"));
	    }
	
	    int numSubQn = questionDetails.getNumOfRubricSubQuestions();
	    List<String> subQuestions = questionDetails.getRubricSubQuestions();
	    List<List<String>> descriptions = questionDetails.getRubricDescriptions();
	    for (int i = 0; i < numSubQn; i++) {
	        List<WebElement> textAreas = getRubricTextareas(questionNum, i + 2);
	        assertEquals(subQuestions.get(i), textAreas.get(0).getAttribute("value"));
	        for (int j = 0; j < numChoices; j++) {
	            assertEquals(descriptions.get(i).get(j), textAreas.get(j + 1).getAttribute("value"));
	        }
	    }
	
	    if (questionDetails.isHasAssignedWeights()) {
	        assertTrue(getWeightCheckbox(questionNum).isSelected());
	        List<List<Double>> weights = questionDetails.getRubricWeights();
	        for (int i = 0; i < numSubQn; i++) {
	            List<WebElement> rubricWeights = getRubricWeights(questionNum, i + 2);
	            for (int j = 0; j < numChoices; j++) {
	                assertEquals(rubricWeights.get(j).getAttribute("value"),
	                        getDoubleString(weights.get(i).get(j)));
	            }
	        }
	    } else {
	        assertFalse(getWeightCheckbox(questionNum).isSelected());
	    }
	}

	public void addRubricQuestion(EntityAttributes<FeedbackQuestion> feedbackQuestion) {
	    addNewQuestion(9);
	    int questionNum = getNumQuestions();
	    inputQuestionDetails(questionNum, feedbackQuestion);
	    FeedbackRubricQuestionDetails questionDetails =
	            (FeedbackRubricQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
	    inputRubricDetails(questionNum, questionDetails);
	    clickSaveNewQuestionButton();
	}

	public void editRubricQuestion(int questionNum, FeedbackRubricQuestionDetails questionDetails) {
	    clickEditQuestionButton(questionNum);
	    inputRubricDetails(questionNum, questionDetails);
	    clickSaveQuestionButton(questionNum);
	}

	public void verifyRankQuestionDetails(int questionNum, FeedbackQuestionDetails questionDetails) {
	    if (questionDetails instanceof FeedbackRankOptionsQuestionDetails) {
	        FeedbackRankOptionsQuestionDetails optionDetails = (FeedbackRankOptionsQuestionDetails) questionDetails;
	        verifyOptions(questionNum, optionDetails.getOptions());
	    }
	    assertEquals(getAllowDuplicateRankCheckbox(questionNum).isSelected(), questionDetails.isAreDuplicatesAllowed());
	    verifyMaxOptions(questionNum, questionDetails.getMaxOptionsToBeRanked());
	    verifyMinOptions(questionNum, questionDetails.getMinOptionsToBeRanked());
	}

	public void addRankOptionsQuestion(EntityAttributes<FeedbackQuestion> feedbackQuestion) {
	    addNewQuestion(10);
	    int questionNum = getNumQuestions();
	    inputQuestionDetails(questionNum, feedbackQuestion);
	    FeedbackRankOptionsQuestionDetails questionDetails =
	            (FeedbackRankOptionsQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
	    inputRankDetails(questionNum, questionDetails);
	    clickSaveNewQuestionButton();
	}

	public void addRankRecipientsQuestion(EntityAttributes<FeedbackQuestion> feedbackQuestion) {
	    addNewQuestion(11);
	    int questionNum = getNumQuestions();
	    inputQuestionDetails(questionNum, feedbackQuestion);
	    FeedbackQuestionDetails questionDetails =
	            (FeedbackQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
	    inputRankDetails(questionNum, questionDetails);
	    clickSaveNewQuestionButton();
	}

	public void editRankQuestion(int questionNum, FeedbackQuestionDetails questionDetails) {
	    clickEditQuestionButton(questionNum);
	    inputRankDetails(questionNum, questionDetails);
	    clickSaveQuestionButton(questionNum);
	}

	private String getCourseId() {
	    return courseIdTextBox.getText();
	}

	private String getCourseName() {
	    return courseNameTextBox.getText();
	}

	private String getTimeZone() {
	    return timezoneDropDown.getText();
	}

	private String getFeedbackSessionName() {
	    return sessionNameTextBox.getText();
	}

	private String getInstructions() {
	    return getEditorRichText(instructionsEditor.findElement(By.tagName("editor")));
	}

	private String getStartDate() {
	    return startDateBox.findElement(By.tagName("input")).getAttribute("value");
	}

	private String getStartTime() {
	    return getSelectedDropdownOptionText(startTimeDropdown.findElement(By.tagName("select")));
	}

	private String getEndDate() {
	    return endDateBox.findElement(By.tagName("input")).getAttribute("value");
	}

	private String getEndTime() {
	    return getSelectedDropdownOptionText(endTimeDropdown.findElement(By.tagName("select")));
	}

	private String getSessionVisibilityDate() {
	    return sessionVisibilityDateBox.findElement(By.tagName("input")).getAttribute("value");
	}

	private String getSessionVisibilityTime() {
	    return getSelectedDropdownOptionText(sessionVisibilityTimeDropdown.findElement(By.tagName("select")));
	}

	private String getResponseVisibilityDate() {
	    return responseVisibilityDateBox.findElement(By.tagName("input"))
	            .getAttribute("value");
	}

	private String getResponseVisibilityTime() {
	    return getSelectedDropdownOptionText(responseVisibilityTimeDropdown.findElement(By.tagName("select")));
	}

	private String getGracePeriod() {
	    return getSelectedDropdownOptionText(gracePeriodDropdown);
	}

	private String getSubmissionStatus() {
	    return submissionStatusTextBox.getText();
	}

	private String getPublishedStatus() {
	    return publishStatusTextBox.getText();
	}

	private String getDateString(Instant instant, String timeZone) {
	    return getDisplayedDateTime(instant, timeZone, "EE, dd MMM, yyyy");
	}

	private String getTimeString(Instant instant, String timeZone) {
	    ZonedDateTime dateTime = instant.atZone(ZoneId.of(timeZone));
	    if (dateTime.getHour() == 0 && dateTime.getMinute() == 0) {
	        return "23:59H";
	    }
	    return getDisplayedDateTime(instant, timeZone, "HH:00") + "H";
	}

	private void setInstructions(String newInstructions) {
	    writeToRichTextEditor(instructionsEditor.findElement(By.tagName("editor")), newInstructions);
	}

	private void setSessionStartDateTime(Instant startInstant, String timeZone) {
	    setDateTime(startDateBox.findElement(By.tagName("input")), startTimeDropdown, startInstant, timeZone);
	}

	private void setSessionEndDateTime(Instant endInstant, String timeZone) {
	    setDateTime(endDateBox.findElement(By.tagName("input")), endTimeDropdown, endInstant, timeZone);
	}

	private void setVisibilityDateTime(Instant startInstant, String timeZone) {
	    setDateTime(sessionVisibilityDateBox.findElement(By.tagName("input")),
	            sessionVisibilityTimeDropdown, startInstant, timeZone);
	}

	private void setResponseDateTime(Instant endInstant, String timeZone) {
	    setDateTime(responseVisibilityDateBox.findElement(By.tagName("input")),
	            responseVisibilityTimeDropdown, endInstant, timeZone);
	}

	private void setDateTime(WebElement dateBox, WebElement timeBox, Instant startInstant, String timeZone) {
	    fillTextBox(dateBox, getDateString(startInstant, timeZone));
	
	    selectDropdownOptionByText(timeBox.findElement(By.tagName("select")), getTimeString(startInstant, timeZone));
	}

	private void selectGracePeriod(long gracePeriodMinutes) {
	    selectDropdownOptionByText(gracePeriodDropdown, gracePeriodMinutes + " min");
	}

	private void setVisibilitySettings(FeedbackSessionAttributes newFeedbackSession) {
	    showVisibilitySettings();
	
	    setSessionVisibilitySettings(newFeedbackSession);
	    setResponseVisibilitySettings(newFeedbackSession);
	}

	private void setSessionVisibilitySettings(FeedbackSessionAttributes newFeedbackSession) {
	    Instant sessionDateTime = newFeedbackSession.getSessionVisibleFromTime();
	    if (sessionDateTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
	        click(openSessionVisibleTimeButton);
	    } else {
	        click(customSessionVisibleTimeButton);
	        setVisibilityDateTime(sessionDateTime, newFeedbackSession.getTimeZone());
	    }
	}

	private void setResponseVisibilitySettings(FeedbackSessionAttributes newFeedbackSession) {
	    Instant responseDateTime = newFeedbackSession.getResultsVisibleFromTime();
	    if (responseDateTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
	        click(immediateResponseVisibleTimeButton);
	    } else if (responseDateTime.equals(Const.TIME_REPRESENTS_LATER)) {
	        click(manualResponseVisibleTimeButton);
	    } else {
	        click(customResponseVisibleTimeButton);
	        setResponseDateTime(responseDateTime, newFeedbackSession.getTimeZone());
	    }
	}

	private void setEmailSettings(FeedbackSessionAttributes newFeedbackSessionDetails) {
	    showEmailSettings();
	    if (newFeedbackSessionDetails.isOpeningEmailEnabled() != openingSessionEmailCheckbox.isSelected()) {
	        click(openingSessionEmailCheckbox);
	    }
	    if (newFeedbackSessionDetails.isClosingEmailEnabled() != closingSessionEmailCheckbox.isSelected()) {
	        click(closingSessionEmailCheckbox);
	    }
	    if (newFeedbackSessionDetails.isPublishedEmailEnabled() != publishedSessionEmailCheckbox.isSelected()) {
	        click(publishedSessionEmailCheckbox);
	    }
	}

	private void showVisibilitySettings() {
	    if (isElementPresent(By.id("btn-change-visibility"))) {
	        click(changeVisibilityButton);
	    }
	}

	private void showEmailSettings() {
	    if (isElementPresent(By.id("btn-change-email"))) {
	        click(changeEmailButton);
	    }
	}

	private int getNumQuestions() {
	    return browser.driver.findElements(By.tagName("tm-question-edit-form")).size();
	}

	private WebElement getQuestionForm(int questionNum) {
	    return browser.driver.findElements(By.tagName("tm-question-edit-form")).get(questionNum - 1);
	}

	private FeedbackQuestionType getQuestionType(int questionNum) {
	    String questionDetails = getQuestionForm(questionNum).findElement(By.id("question-header")).getText();
	    String questionType = questionDetails.split(" \\d+ ")[1].trim();
	
	    switch (questionType) {
	    case "Essay question":
	        return FeedbackQuestionType.TEXT;
	    case "Multiple-Choice (single answer) question":
	        return FeedbackQuestionType.MCQ;
	    case "Multiple-choice (multiple answers) question":
	        return FeedbackQuestionType.MSQ;
	    case "Numerical Scale Question":
	        return FeedbackQuestionType.NUMSCALE;
	    case "Distribute points (among options) question":
	        return FeedbackQuestionType.CONSTSUM_OPTIONS;
	    case "Distribute points (among recipients) question":
	        return FeedbackQuestionType.CONSTSUM_RECIPIENTS;
	    case "Team contribution question":
	        return FeedbackQuestionType.CONTRIB;
	    case "Rubric question":
	        return FeedbackQuestionType.RUBRIC;
	    case "Rank (options) question":
	        return FeedbackQuestionType.RANK_OPTIONS;
	    case "Rank (recipients) question":
	        return FeedbackQuestionType.RANK_RECIPIENTS;
	    default:
	        throw new IllegalArgumentException("Unknown FeedbackQuestionType");
	    }
	}

	private int getQuestionNumber(int questionNum) {
	    return Integer.parseInt(getQuestionForm(questionNum).findElement(By.id("question-number")).getText());
	}

	private String getQuestionBrief(int questionNum) {
	    return getQuestionForm(questionNum).findElement(By.id("question-brief")).getAttribute("value");
	}

	private String getQuestionDescription(int questionNum) {
	    WebElement editor = waitForElementPresence(By.cssSelector("#question-form-" + questionNum + " editor"));
	    return getEditorRichText(editor);
	}

	private String getFeedbackGiver(int questionNum) {
	    String feedbackPath = getFeedbackPath(questionNum);
	    if (feedbackPath.equals(CUSTOM_FEEDBACK_PATH_OPTION)) {
	        return getSelectedDropdownOptionText(getQuestionForm(questionNum)
	                .findElement(By.tagName("tm-feedback-path-panel"))
	                .findElement(By.id("giver-type")));
	    }
	    return feedbackPath.split(FEEDBACK_PATH_SEPARATOR)[0];
	}

	private String getFeedbackReceiver(int questionNum) {
	    String feedbackPath = getFeedbackPath(questionNum);
	    if (feedbackPath.equals(CUSTOM_FEEDBACK_PATH_OPTION)) {
	        return getSelectedDropdownOptionText(getQuestionForm(questionNum)
	                .findElement(By.tagName("tm-feedback-path-panel"))
	                .findElement(By.id("receiver-type")));
	    }
	    return feedbackPath.split(FEEDBACK_PATH_SEPARATOR)[1];
	}

	private String getFeedbackPath(int questionNum) {
	    WebElement feedbackPathPanel = getQuestionForm(questionNum).findElement(By.tagName("tm-feedback-path-panel"));
	    return feedbackPathPanel.findElement(By.cssSelector("#btn-feedback-path span")).getText();
	}

	private void setQuestionBrief(int questionNum, String newBrief) {
	    fillTextBox(getQuestionForm(questionNum).findElement(By.id("question-brief")), newBrief);
	}

	private void setQuestionDescription(int questionNum, String newDescription) {
	    WebElement editor = waitForElementPresence(By.cssSelector("#question-form-" + questionNum + " editor"));
	    writeToRichTextEditor(editor, newDescription);
	}

	private void setFeedbackPath(int questionNum, EntityAttributes<FeedbackQuestion> feedbackQuestion) {
	    FeedbackParticipantType newGiver = feedbackQuestion.getGiverType();
	    FeedbackParticipantType newRecipient = feedbackQuestion.getRecipientType();
	    String feedbackPath = getFeedbackPath(questionNum);
	    WebElement questionForm = getQuestionForm(questionNum).findElement(By.tagName("tm-feedback-path-panel"));
	    if (!feedbackPath.equals(CUSTOM_FEEDBACK_PATH_OPTION)) {
	        selectFeedbackPathDropdownOption(questionNum, CUSTOM_FEEDBACK_PATH_OPTION + "...");
	    }
	    // Set to type STUDENT first to adjust NumberOfEntitiesToGiveFeedbackTo
	    selectDropdownOptionByText(questionForm.findElement(By.id("giver-type")),
	            getDisplayGiverName(FeedbackParticipantType.STUDENTS));
	    selectDropdownOptionByText(questionForm.findElement(By.id("receiver-type")),
	            getDisplayRecipientName(FeedbackParticipantType.STUDENTS));
	    if (feedbackQuestion.getNumberOfEntitiesToGiveFeedbackTo() == Const.MAX_POSSIBLE_RECIPIENTS) {
	        click(questionForm.findElement(By.id("unlimited-recipients")));
	    } else {
	        click(questionForm.findElement(By.id("custom-recipients")));
	        fillTextBox(questionForm.findElement(By.id("custom-recipients-number")),
	                Integer.toString(feedbackQuestion.getNumberOfEntitiesToGiveFeedbackTo()));
	    }
	
	    selectDropdownOptionByText(questionForm.findElement(By.id("giver-type")), getDisplayGiverName(newGiver));
	    selectDropdownOptionByText(questionForm.findElement(By.id("receiver-type")),
	            getDisplayRecipientName(newRecipient));
	}

	private void selectFeedbackPathDropdownOption(int questionNum, String text) {
	    WebElement questionForm = getQuestionForm(questionNum);
	    WebElement feedbackPathPanel = questionForm.findElement(By.tagName("tm-feedback-path-panel"));
	    click(feedbackPathPanel.findElement(By.id("btn-feedback-path")));
	    WebElement dropdown = feedbackPathPanel.findElement(By.id("feedback-path-dropdown"));
	    List<WebElement> options = dropdown.findElements(By.className("dropdown-item"));
	    for (WebElement option : options) {
	        if (option.getText().equals(text)) {
	            click(option);
	            return;
	        }
	    }
	}

	private void clickEditQuestionButton(int questionNum) {
	    click(getQuestionForm(questionNum).findElement(By.id("btn-edit-question")));
	}

	private void clickSaveQuestionButton(int questionNum) {
	    WebElement saveButton = getQuestionForm(questionNum).findElement(By.id("btn-save-question"));
	    click(saveButton);
	    ThreadHelper.waitFor(1000);
	}

	private void setQuestionVisibility(int questionNum, EntityAttributes<FeedbackQuestion> feedbackQuestion) {
	    WebElement questionForm = getQuestionForm(questionNum);
	    WebElement visibilityPanel = questionForm.findElement(By.tagName("tm-visibility-panel"));
	    String visibility = visibilityPanel.findElement(By.cssSelector("#btn-question-visibility span")).getText();
	    if (!visibility.equals(CUSTOM_VISIBILITY_OPTION)) {
	        selectVisibilityDropdownOption(questionNum, CUSTOM_VISIBILITY_OPTION + "...");
	    }
	
	    FeedbackParticipantType giver = feedbackQuestion.getGiverType();
	    FeedbackParticipantType receiver = feedbackQuestion.getRecipientType();
	    WebElement customVisibilityTable = visibilityPanel.findElement(By.id("custom-visibility-table"));
	    selectVisibilityBoxes(customVisibilityTable, giver, receiver, feedbackQuestion.getShowResponsesTo(), 1);
	    selectVisibilityBoxes(customVisibilityTable, giver, receiver, feedbackQuestion.getShowGiverNameTo(), 2);
	    selectVisibilityBoxes(customVisibilityTable, giver, receiver, feedbackQuestion.getShowRecipientNameTo(), 3);
	}

	private void selectVisibilityBoxes(WebElement table, FeedbackParticipantType giver, FeedbackParticipantType receiver, List<FeedbackParticipantType> participants, int colNum) {
	    List<FeedbackParticipantType> possibleTypes = new ArrayList<>(Arrays.asList(FeedbackParticipantType.RECEIVER,
	            FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
	            FeedbackParticipantType.STUDENTS, FeedbackParticipantType.INSTRUCTORS));
	    if (!giver.equals(FeedbackParticipantType.STUDENTS)) {
	        possibleTypes.remove(FeedbackParticipantType.OWN_TEAM_MEMBERS);
	    }
	    if (!receiver.equals(FeedbackParticipantType.STUDENTS)) {
	        possibleTypes.remove(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
	    }
	    if (receiver.equals(FeedbackParticipantType.NONE)
	            || receiver.equals(FeedbackParticipantType.SELF)
	            || receiver.equals(FeedbackParticipantType.OWN_TEAM)) {
	        possibleTypes.remove(FeedbackParticipantType.RECEIVER);
	        possibleTypes.remove(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
	    }
	
	    List<WebElement> rows = table.findElements(By.tagName("tr"));
	    int index = colNum - 1;
	    for (FeedbackParticipantType participant : participants) {
	        markOptionAsSelected(rows.get(possibleTypes.indexOf(participant)).findElements(By.tagName("input")).get(index));
	    }
	}

	private void selectVisibilityDropdownOption(int questionNum, String text) {
	    WebElement questionForm = getQuestionForm(questionNum);
	    WebElement visibilityPanel = questionForm.findElement(By.tagName("tm-visibility-panel"));
	    click(visibilityPanel.findElement(By.id("btn-question-visibility")));
	    WebElement dropdown = visibilityPanel.findElement(By.id("question-visibility-dropdown"));
	    List<WebElement> options = dropdown.findElements(By.className("dropdown-item"));
	    for (WebElement option : options) {
	        if (option.getText().equals(text)) {
	            click(option);
	            return;
	        }
	    }
	}

	private void clickAndWaitForNewQuestion(WebElement button) {
	    int newQuestionNum = getNumQuestions() + 1;
	    click(button);
	    waitForElementPresence(By.id("question-form-" + newQuestionNum));
	}

	private void addNewQuestion(int optionNumber) {
	    click(addNewQuestionButton);
	    WebElement newQuestionDropdown = waitForElementPresence(By.id("new-question-dropdown"));
	    WebElement optionButton = newQuestionDropdown.findElements(By.tagName("button")).get(optionNumber - 1);
	    if (optionNumber == 1) {
	        click(optionButton);
	    } else {
	        clickAndWaitForNewQuestion(optionButton);
	    }
	}

	private void clickSaveNewQuestionButton() {
	    WebElement saveButton = browser.driver.findElement(By.id("btn-save-new"));
	    click(saveButton);
	    waitForElementStaleness(saveButton);
	}

	private WebElement getRecommendedTextLengthField(int questionNum) {
	    return getQuestionForm(questionNum)
	            .findElement(By.tagName("tm-text-question-edit-details-form"))
	            .findElement(By.id("recommended-length"));
	}

	private WebElement getGenerateOptionsCheckbox(int questionNum) {
	    return getQuestionForm(questionNum).findElement(By.id("generate-checkbox"));
	}

	private WebElement getGenerateOptionsDropdown(int questionNum) {
	    return getQuestionForm(questionNum).findElement(By.id("generate-dropdown"));
	}

	private WebElement getWeightCheckbox(int questionNum) {
	    return getQuestionForm(questionNum).findElement(By.id("weights-checkbox"));
	}

	private WebElement getOtherOptionCheckbox(int questionNum) {
	    return getQuestionForm(questionNum).findElement(By.id("other-checkbox"));
	}

	private String getGeneratedOptionString(FeedbackParticipantType type) {
	    switch (type) {
	    case STUDENTS:
	        return "students";
	    case STUDENTS_EXCLUDING_SELF:
	        return "students (excluding self)";
	    case TEAMS:
	        return "teams";
	    case TEAMS_EXCLUDING_SELF:
	        return "teams (excluding own team)";
	    case INSTRUCTORS:
	        return "instructors";
	    default:
	        return "unknown";
	    }
	}

	private String getDoubleString(Double value) {
	    return value % 1 == 0 ? Integer.toString(value.intValue()) : Double.toString(value);
	}

	private WebElement getOptionsSection(int questionNum) {
	    return getQuestionForm(questionNum).findElement(By.id("options-section"));
	}

	private List<WebElement> getOptionInputs(int questionNum) {
	    WebElement optionsSection = getOptionsSection(questionNum);
	    return optionsSection.findElements(By.cssSelector("input[type='text']"));
	}

	private List<WebElement> getOptionWeightInputs(int questionNum) {
	    WebElement optionsSection = getOptionsSection(questionNum);
	    return optionsSection.findElements(By.cssSelector("tm-weight-field input"));
	}

	private WebElement getOtherWeightInput(int questionNum) {
	    return getQuestionForm(questionNum).findElement(By.id("other-weight"));
	}

	private boolean verifyGeneratedOptions(int questionNum, FeedbackParticipantType participantType) {
	    if (!participantType.equals(FeedbackParticipantType.NONE)) {
	        assertTrue(getGenerateOptionsCheckbox(questionNum).isSelected());
	        assertEquals(getSelectedDropdownOptionText(getGenerateOptionsDropdown(questionNum)),
	                getGeneratedOptionString(participantType));
	        return true;
	    }
	    assertFalse(getGenerateOptionsCheckbox(questionNum).isSelected());
	    return false;
	}

	private void verifyOptions(int questionNum, List<String> options) {
	    List<WebElement> inputs = getOptionInputs(questionNum);
	    for (int i = 0; i < options.size(); i++) {
	        assertEquals(options.get(i), inputs.get(i).getAttribute("value"));
	    }
	}

	private void verifyOptionWeights(int questionNum, boolean hasWeights, List<Double> weights) {
	    if (hasWeights) {
	        assertTrue(getWeightCheckbox(questionNum).isSelected());
	        List<WebElement> weightInputs = getOptionWeightInputs(questionNum);
	        for (int i = 0; i < weights.size(); i++) {
	            assertEquals(getDoubleString(weights.get(i)), weightInputs.get(i).getAttribute("value"));
	        }
	    } else {
	        assertFalse(getWeightCheckbox(questionNum).isSelected());
	    }
	}

	private void verifyOtherOption(int questionNum, boolean hasOther, Double weight) {
	    if (hasOther) {
	        assertTrue(getOtherOptionCheckbox(questionNum).isSelected());
	        if (weight > 0) {
	            String otherWeight = getOtherWeightInput(questionNum).getAttribute("value");
	            assertEquals(getDoubleString(weight), otherWeight);
	        }
	    } else {
	        assertFalse(getOtherOptionCheckbox(questionNum).isSelected());
	    }
	}

	private void inputMcqDetails(int questionNum, FeedbackMcqQuestionDetails questionDetails) {
	    if (inputGenerateOptions(questionNum, questionDetails.getGenerateOptionsFor())) {
	        return;
	    }
	
	    inputOptions(questionNum, questionDetails.getMcqChoices());
	    inputOptionWeights(questionNum, questionDetails.isHasAssignedWeights(), questionDetails.getMcqWeights());
	    inputOtherChoice(questionNum, questionDetails.isOtherEnabled(), questionDetails.getMcqOtherWeight());
	}

	private boolean inputGenerateOptions(int questionNum, FeedbackParticipantType participantType) {
	    if (!participantType.equals(FeedbackParticipantType.NONE)) {
	        markOptionAsSelected(getGenerateOptionsCheckbox(questionNum));
	        selectDropdownOptionByText(getGenerateOptionsDropdown(questionNum),
	                getGeneratedOptionString(participantType));
	        clickSaveQuestionButton(questionNum);
	        return true;
	    }
	    markOptionAsUnselected(getGenerateOptionsCheckbox(questionNum));
	    return false;
	}

	private void inputOptions(int questionNum, List<String> options) {
	    List<WebElement> inputs = getOptionInputs(questionNum);
	    int numInputsNeeded = options.size() - inputs.size();
	    if (numInputsNeeded > 0) {
	        for (int i = 0; i < numInputsNeeded; i++) {
	            click(getQuestionForm(questionNum).findElement(By.id("btn-add-option")));
	        }
	        inputs = getOptionInputs(questionNum);
	    }
	    if (numInputsNeeded < 0) {
	        for (int i = 0; i < -numInputsNeeded; i++) {
	            click(getOptionsSection(questionNum).findElement(By.tagName("button")));
	        }
	        inputs = getOptionInputs(questionNum);
	    }
	
	    for (int i = 0; i < options.size(); i++) {
	        fillTextBox(inputs.get(i), options.get(i));
	    }
	}

	private void inputOptionWeights(int questionNum, boolean hasWeights, List<Double> weights) {
	    if (hasWeights) {
	        markOptionAsSelected(getWeightCheckbox(questionNum));
	        List<WebElement> weightInputs = getOptionWeightInputs(questionNum);
	        for (int i = 0; i < weights.size(); i++) {
	            fillTextBox(weightInputs.get(i), getDoubleString(weights.get(i)));
	        }
	    } else {
	        markOptionAsUnselected(getWeightCheckbox(questionNum));
	    }
	}

	private void inputOtherChoice(int questionNum, boolean hasOther, Double otherWeight) {
	    if (hasOther) {
	        markOptionAsSelected(getOtherOptionCheckbox(questionNum));
	        if (otherWeight > 0) {
	            fillTextBox(getOtherWeightInput(questionNum), getDoubleString(otherWeight));
	        }
	    } else {
	        markOptionAsUnselected(getOtherOptionCheckbox(questionNum));
	    }
	}

	private WebElement getMaxOptionsCheckbox(int questionNum) {
	    return getQuestionForm(questionNum).findElement(By.id("max-options-checkbox"));
	}

	private WebElement getMaxOptionsInput(int questionNum) {
	    return getQuestionForm(questionNum).findElement(By.id("max-options"));
	}

	private WebElement getMinOptionsCheckbox(int questionNum) {
	    return getQuestionForm(questionNum).findElement(By.id("min-options-checkbox"));
	}

	private WebElement getMinOptionsInput(int questionNum) {
	    return getQuestionForm(questionNum).findElement(By.id("min-options"));
	}

	private void verifyMaxOptions(int questionNum, int maxOptions) {
	    if (maxOptions == Const.POINTS_NO_VALUE) {
	        assertFalse(getMaxOptionsCheckbox(questionNum).isSelected());
	    } else {
	        assertTrue(getMaxOptionsCheckbox(questionNum).isSelected());
	        assertEquals(getMaxOptionsInput(questionNum).getAttribute("value"),
	                Integer.toString(maxOptions));
	    }
	}

	private void verifyMinOptions(int questionNum, int minOptions) {
	    if (minOptions == Const.POINTS_NO_VALUE) {
	        assertFalse(getMinOptionsCheckbox(questionNum).isSelected());
	    } else {
	        assertTrue(getMinOptionsCheckbox(questionNum).isSelected());
	        assertEquals(getMinOptionsInput(questionNum).getAttribute("value"),
	                Integer.toString(minOptions));
	    }
	}

	private void inputMsqDetails(int questionNum, FeedbackMsqQuestionDetails questionDetails) {
	    if (inputGenerateOptions(questionNum, questionDetails.getGenerateOptionsFor())) {
	        return;
	    }
	
	    inputOptions(questionNum, questionDetails.getMsqChoices());
	    inputOptionWeights(questionNum, questionDetails.isHasAssignedWeights(), questionDetails.getMsqWeights());
	    inputOtherChoice(questionNum, questionDetails.isOtherEnabled(), questionDetails.getMsqOtherWeight());
	    inputMaxOptions(questionNum, questionDetails.getMaxSelectableChoices());
	    inputMinOptions(questionNum, questionDetails.getMinSelectableChoices());
	}

	private void inputMaxOptions(int questionNum, int maxOptions) {
	    if (maxOptions == Const.POINTS_NO_VALUE) {
	        markOptionAsUnselected(getMaxOptionsCheckbox(questionNum));
	    } else {
	        markOptionAsSelected(getMaxOptionsCheckbox(questionNum));
	        fillTextBox(getMaxOptionsInput(questionNum), Integer.toString(maxOptions));
	    }
	}

	private void inputMinOptions(int questionNum, int minOptions) {
	    if (minOptions == Const.POINTS_NO_VALUE) {
	        markOptionAsUnselected(getMinOptionsCheckbox(questionNum));
	    } else {
	        markOptionAsSelected(getMinOptionsCheckbox(questionNum));
	        fillTextBox(getMinOptionsInput(questionNum), Integer.toString(minOptions));
	    }
	}

	private WebElement getMinNumscaleInput(int questionNum) {
	    return getQuestionForm(questionNum).findElement(By.id("min-value"));
	}

	private WebElement getMaxNumscaleInput(int questionNum) {
	    return getQuestionForm(questionNum).findElement(By.id("max-value"));
	}

	private WebElement getNumScaleIncrementInput(int questionNum) {
	    return getQuestionForm(questionNum).findElement(By.id("increment-value"));
	}

	private void inputNumScaleDetails(int questionNum, FeedbackNumericalScaleQuestionDetails questionDetails) {
	    inputNumScaleValue(getMinNumscaleInput(questionNum), Integer.toString(questionDetails.getMinScale()));
	    inputNumScaleValue(getNumScaleIncrementInput(questionNum), getDoubleString(questionDetails.getStep()));
	    inputNumScaleValue(getMaxNumscaleInput(questionNum), Integer.toString(questionDetails.getMaxScale()));
	}

	private void inputNumScaleValue(WebElement input, String value) {
	    input.clear();
	    input.sendKeys(value);
	}

	private WebElement getConstSumTotalPointsRadioBtn(int questionNum) {
	    return getQuestionForm(questionNum).findElement(By.id("total-points-radio"));
	}

	private WebElement getConstSumTotalPointsInput(int questionNum) {
	    return getQuestionForm(questionNum).findElement(By.id("total-points"));
	}

	private WebElement getConstSumPerOptionPointsRadioBtn(int questionNum) {
	    return getQuestionForm(questionNum).findElement(By.id("per-option-points-radio"));
	}

	private WebElement getConstSumPerOptionPointsInput(int questionNum) {
	    return getQuestionForm(questionNum).findElement(By.id("per-option-points"));
	}

	private WebElement getConstSumUnevenDistributionCheckbox(int questionNum) {
	    return getQuestionForm(questionNum).findElement(By.id("uneven-distribution-checkbox"));
	}

	private WebElement getConstSumUnevenDistributionDropdown(int questionNum) {
	    return getQuestionForm(questionNum).findElement(By.id("uneven-distribution-dropdown"));
	}

	private void inputConstSumDetails(int questionNum, FeedbackConstantSumQuestionDetails questionDetails) {
	    if (!questionDetails.isDistributeToRecipients()) {
	        inputOptions(questionNum, questionDetails.getConstSumOptions());
	    }
	    if (questionDetails.isPointsPerOption()) {
	        click(getConstSumPerOptionPointsRadioBtn(questionNum));
	        fillTextBox(getConstSumPerOptionPointsInput(questionNum), Integer.toString(questionDetails.getPoints()));
	    } else {
	        click(getConstSumTotalPointsRadioBtn(questionNum));
	        fillTextBox(getConstSumTotalPointsInput(questionNum), Integer.toString(questionDetails.getPoints()));
	    }
	    String distributeFor = questionDetails.getDistributePointsFor();
	    if (questionDetails.isForceUnevenDistribution()) {
	        markOptionAsSelected(getConstSumUnevenDistributionCheckbox(questionNum));
	        selectDropdownOptionByText(getConstSumUnevenDistributionDropdown(questionNum),
	                "All options".equals(distributeFor) ? "Every option" : distributeFor);
	    } else {
	        markOptionAsUnselected(getConstSumUnevenDistributionCheckbox(questionNum));
	    }
	}

	private WebElement getAllowNotSureContributionCheckbox(int questionNum) {
	    return getQuestionForm(questionNum).findElement(By.id("not-sure-checkbox"));
	}

	private void inputContributionDetails(int questionNum, FeedbackContributionQuestionDetails questionDetails) {
	    if (questionDetails.isNotSureAllowed()) {
	        markOptionAsSelected(getAllowNotSureContributionCheckbox(questionNum));
	    } else {
	        markOptionAsUnselected(getAllowNotSureContributionCheckbox(questionNum));
	    }
	}

	private WebElement getRubricRow(int questionNum, int rowNumber) {
	    return getQuestionForm(questionNum).findElements(By.cssSelector("tm-rubric-question-edit-details-form tr"))
	            .get(rowNumber - 1);
	}

	private List<WebElement> getRubricChoiceInputs(int questionNum) {
	    return getRubricRow(questionNum, 1).findElements(By.tagName("input"));
	}

	private List<WebElement> getRubricTextareas(int questionNum, int rowNum) {
	    return getRubricRow(questionNum, rowNum).findElements(By.tagName("textarea"));
	}

	private List<WebElement> getRubricWeights(int questionNum, int rowNum) {
	    return getRubricRow(questionNum, rowNum).findElements(By.tagName("input"));
	}

	private WebElement getRubricDeleteSubQnBtn(int questionNum, int rowNum) {
	    return getRubricRow(questionNum, rowNum).findElement(By.id("btn-delete-subquestion"));
	}

	private WebElement getRubricDeleteChoiceBtn(int questionNum, int colNum) {
	    return getRubricRow(questionNum, getNumRubricRows(questionNum)).findElements(By.id("btn-delete-choice")).get(colNum);
	}

	private int getNumRubricRows(int questionNum) {
	    return getQuestionForm(questionNum).findElements(By.cssSelector("#rubric-table tr")).size();
	}

	private int getNumRubricCols(int questionNum) {
	    return getRubricRow(questionNum, 1).findElements(By.tagName("td")).size();
	}

	private void inputRubricDetails(int questionNum, FeedbackRubricQuestionDetails questionDetails) {
	    int numSubQn = questionDetails.getNumOfRubricSubQuestions();
	    int numChoices = questionDetails.getNumOfRubricChoices();
	    adjustNumRubricFields(questionNum, numSubQn, numChoices);
	
	    List<String> choices = questionDetails.getRubricChoices();
	    for (int i = 0; i < numChoices; i++) {
	        fillTextBox(getRubricChoiceInputs(questionNum).get(i), choices.get(i));
	    }
	
	    List<String> subQuestions = questionDetails.getRubricSubQuestions();
	    List<List<String>> descriptions = questionDetails.getRubricDescriptions();
	    for (int i = 0; i < numSubQn; i++) {
	        List<WebElement> textAreas = getRubricTextareas(questionNum, i + 2);
	        fillTextBox(textAreas.get(0), subQuestions.get(i));
	        for (int j = 0; j < numChoices; j++) {
	            fillTextBox(textAreas.get(j + 1), descriptions.get(i).get(j));
	            if (descriptions.get(i).get(j).isEmpty()) {
	                // using clear does not send the required event
	                // as a workaround, after clearing without event, enter a random character and delete it
	                textAreas.get(j + 1).sendKeys("a");
	                textAreas.get(j + 1).sendKeys(Keys.BACK_SPACE);
	            }
	        }
	    }
	
	    if (questionDetails.isHasAssignedWeights()) {
	        markOptionAsSelected(getWeightCheckbox(questionNum));
	        List<List<Double>> weights = questionDetails.getRubricWeights();
	        for (int i = 0; i < numSubQn; i++) {
	            for (int j = 0; j < numChoices; j++) {
	                fillTextBox(getRubricWeights(questionNum, i + 2).get(j), getDoubleString(weights.get(i).get(j)));
	            }
	        }
	    } else {
	        markOptionAsUnselected(getWeightCheckbox(questionNum));
	    }
	}

	private void adjustNumRubricFields(int questionNum, int numSubQn, int numChoices) {
	    int numSubQnsNeeded = numSubQn - (getNumRubricRows(questionNum) - 2);
	    int numChoicesNeeded = numChoices - (getNumRubricCols(questionNum) - 1);
	    if (numSubQnsNeeded > 0) {
	        for (int i = 0; i < numSubQnsNeeded; i++) {
	            click(getQuestionForm(questionNum).findElement(By.id("btn-add-row")));
	        }
	    }
	    if (numChoicesNeeded > 0) {
	        for (int i = 0; i < numChoicesNeeded; i++) {
	            click(getQuestionForm(questionNum).findElement(By.id("btn-add-col")));
	        }
	    }
	    if (numSubQnsNeeded < 0) {
	        for (int i = 0; i < -numSubQnsNeeded; i++) {
	            click(getRubricDeleteSubQnBtn(questionNum, 2));
	        }
	    }
	    if (numChoicesNeeded < 0) {
	        for (int i = 0; i < -numChoicesNeeded; i++) {
	            clickAndConfirm(getRubricDeleteChoiceBtn(questionNum, 2));
	        }
	    }
	}

	private WebElement getAllowDuplicateRankCheckbox(int questionNum) {
	    return getQuestionForm(questionNum).findElement(By.id("duplicate-rank-checkbox"));
	}

	private void inputRankDetails(int questionNum, FeedbackQuestionDetails questionDetails) {
	    if (questionDetails instanceof FeedbackRankOptionsQuestionDetails) {
	        FeedbackRankOptionsQuestionDetails optionDetails = (FeedbackRankOptionsQuestionDetails) questionDetails;
	        inputOptions(questionNum, optionDetails.getOptions());
	    }
	    if (questionDetails.isAreDuplicatesAllowed()) {
	        markOptionAsSelected(getAllowDuplicateRankCheckbox(questionNum));
	    } else {
	        markOptionAsUnselected(getAllowDuplicateRankCheckbox(questionNum));
	    }
	    inputMaxOptions(questionNum, questionDetails.getMaxOptionsToBeRanked());
	    inputMinOptions(questionNum, questionDetails.getMinOptionsToBeRanked());
	}

	public void verifyFeedbackSessionDetails(FeedbackSessionAttributes feedbackSession) {
	    assertEquals(getCourseId(), feedbackSession.getCourseId());
	    assertEquals(getFeedbackSessionName(), feedbackSession.getFeedbackSessionName());
	    assertDateEquals(getOpeningTime(), feedbackSession.getStartTime(), feedbackSession.getTimeZone());
	    assertDateEquals(getClosingTime(), feedbackSession.getEndTime(), feedbackSession.getTimeZone());
	}

	/**
	 * @deprecated Use {@link #verifyResponseDetails(VerifyResponseDetailsParameter)} instead
	 */
	public void verifyResponseDetails(EntityAttributes<FeedbackQuestion> question, List<FeedbackResponseAttributes> givenResponses, List<FeedbackResponseAttributes> otherResponses, Set<String> visibleGivers, Set<String> visibleRecipients) {
		verifyResponseDetails(new VerifyResponseDetailsParameter(question, givenResponses, otherResponses,
				visibleGivers, visibleRecipients));
	}

	public void verifyResponseDetails(VerifyResponseDetailsParameter parameterObjectVerifyResponseDetailsParameter) {
	    if (!hasDisplayedResponses(parameterObjectVerifyResponseDetailsParameter.question)) {
	        return;
	    }
	    verifyGivenResponses(parameterObjectVerifyResponseDetailsParameter.question, parameterObjectVerifyResponseDetailsParameter.givenResponses);
	    verifyOtherResponses(parameterObjectVerifyResponseDetailsParameter.question, parameterObjectVerifyResponseDetailsParameter.otherResponses, parameterObjectVerifyResponseDetailsParameter.visibleGivers, parameterObjectVerifyResponseDetailsParameter.visibleRecipients);
	}

	public void verifyQuestionNotPresent(int questionNum) {
	    try {
	        getQuestionResponsesSection(questionNum);
	        fail("Question " + questionNum + " should not be present.");
	    } catch (NoSuchElementException e) {
	        // success
	    }
	}

	public void verifyNumScaleStatistics(int questionNum, String[] expectedStats) {
	    verifyTableRowValues(getNumScaleStatistics(questionNum), expectedStats);
	}

	public void verifyRubricStatistics(int questionNum, String[][] expectedStats, String[][] expectedStatsExcludingSelf, String[][] expectedStatsPerRecipient) {
	    WebElement excludeSelfCheckbox = getRubricExcludeSelfCheckbox(questionNum);
	    markOptionAsUnselected(excludeSelfCheckbox);
	    verifyTableBodyValues(getRubricStatistics(questionNum), expectedStats);
	
	    markOptionAsSelected(excludeSelfCheckbox);
	    verifyTableBodyValues(getRubricStatistics(questionNum), expectedStatsExcludingSelf);
	
	    sortRubricPerRecipientStatsPerCriterion(questionNum, 2);
	    verifyTableBodyValues(getRubricPerRecipientStatsPerCriterion(questionNum), expectedStatsPerRecipient);
	
	    sortRubricPerRecipientStatsOverall(questionNum, 2);
	    verifyTableBodyValues(getRubricPerRecipientStatsPerCriterion(questionNum), expectedStatsPerRecipient);
	}

	public void verifyContributionStatistics(int questionNum, String[] expectedStats) {
	    WebElement questionSection = getQuestionResponsesSection(questionNum);
	    assertEquals(questionSection.findElement(By.id("own-view-me")).getText(), expectedStats[0]);
	    assertEquals(questionSection.findElement(By.id("own-view-others")).getText().trim(), expectedStats[1]);
	    assertEquals(questionSection.findElement(By.id("team-view-me")).getText(), expectedStats[2]);
	    assertEquals(questionSection.findElement(By.id("team-view-others")).getText().trim(), expectedStats[3]);
	}

	public void verifyCommentDetails(int questionNum, String commentGiver, String commentEditor, String commentString) {
	    WebElement commentField = getCommentField(questionNum, commentString);
	    if (commentGiver.isEmpty()) {
	        assertTrue(isCommentByResponseGiver(commentField));
	    } else {
	        assertEquals(commentGiver, getCommentGiver(commentField));
	    }
	    if (!commentEditor.isEmpty()) {
	        assertEquals(commentEditor, getCommentEditor(commentField));
	    }
	}

	private boolean hasDisplayedResponses(EntityAttributes<FeedbackQuestion> question) {
	    return !question.getQuestionDetailsCopy().getQuestionType().equals(FeedbackQuestionType.CONTRIB);
	}

	private void verifyGivenResponses(EntityAttributes<FeedbackQuestion> question, List<FeedbackResponseAttributes> givenResponses) {
	    for (FeedbackResponseAttributes response : givenResponses) {
	        WebElement responseField = getGivenResponseField(question.getQuestionNumber(), response.getRecipient());
	        assertTrue(isResponseEqual(question, responseField, response));
	    }
	}

	private void verifyOtherResponses(EntityAttributes<FeedbackQuestion> question, List<FeedbackResponseAttributes> otherResponses, Set<String> visibleGivers, Set<String> visibleRecipients) {
	    Set<String> recipients = getRecipients(otherResponses);
	    for (String recipient : recipients) {
	        List<FeedbackResponseAttributes> expectedResponses = otherResponses.stream()
	                .filter(r -> r.getRecipient().equals(recipient)
	                    && (question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)
	                    || question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS)
	                    || question.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS)
	                    || question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)))
	                .collect(Collectors.toList());
	
	        verifyResponseForRecipient(question, recipient, expectedResponses, visibleGivers, visibleRecipients);
	    }
	}

	private Set<String> getRecipients(List<FeedbackResponseAttributes> responses) {
	    return responses.stream().map(FeedbackResponseAttributes::getRecipient).collect(Collectors.toSet());
	}

	private void verifyResponseForRecipient(EntityAttributes<FeedbackQuestion> question, String recipient, List<FeedbackResponseAttributes> otherResponses, Set<String> visibleGivers, Set<String> visibleRecipients) {
	    List<WebElement> responseViews = getAllResponseViews(question.getQuestionNumber());
	    for (FeedbackResponseAttributes response : otherResponses) {
	        boolean isRecipientVisible = visibleRecipients.contains(response.getGiver())
	                || recipient.equals(CURRENT_STUDENT_IDENTIFIER);
	        boolean isGiverVisible = visibleGivers.contains(response.getGiver())
	                || (visibleGivers.contains("RECEIVER") && response.getRecipient().equals(CURRENT_STUDENT_IDENTIFIER))
	                || response.getGiver().equals(CURRENT_STUDENT_IDENTIFIER);
	        boolean isGiverVisibleToInstructor = question.getRecipientType() == FeedbackParticipantType.INSTRUCTORS
	                && visibleGivers.contains("INSTRUCTORS");
	        if (isRecipientVisible) {
	            int recipientIndex = getRecipientIndex(question.getQuestionNumber(), recipient);
	            WebElement responseView = responseViews.get(recipientIndex);
	            List<WebElement> responsesFields = getAllResponseFields(responseView);
	            if (isGiverVisible || isGiverVisibleToInstructor) {
	                int giverIndex = getGiverIndex(responseView, response.getGiver());
	                assertTrue(isResponseEqual(question, responsesFields.get(giverIndex), response));
	            } else {
	                assertTrue(isAnyAnonymousResponseEqual(question, responseView, response));
	            }
	        } else {
	            verifyAnonymousResponseView(question, otherResponses, isGiverVisible);
	        }
	    }
	}

	private void verifyAnonymousResponseView(EntityAttributes<FeedbackQuestion> question, List<FeedbackResponseAttributes> expectedResponses, boolean isGiverVisible) {
	    List<WebElement> anonymousViews = getAllResponseViews(question.getQuestionNumber()).stream()
	            .filter(v -> isAnonymous(v.findElement(By.id("response-recipient")).getText()))
	            .collect(Collectors.toList());
	    if (anonymousViews.isEmpty()) {
	        fail("No anonymous views found");
	    }
	
	    boolean hasCorrectResponses = true;
	    for (WebElement responseView : anonymousViews) {
	        hasCorrectResponses = true;
	        List<WebElement> responseFields = getAllResponseFields(responseView);
	        for (FeedbackResponseAttributes response : expectedResponses) {
	            if (isGiverVisible) {
	                int giverIndex = getGiverIndex(responseView, response.getGiver());
	                if (!isResponseEqual(question, responseFields.get(giverIndex), response)) {
	                    hasCorrectResponses = false;
	                    break;
	                }
	            } else if (!isAnyAnonymousResponseEqual(question, responseView, response)) {
	                hasCorrectResponses = false;
	                break;
	            }
	        }
	        if (hasCorrectResponses) {
	            break;
	        }
	    }
	    assertTrue(hasCorrectResponses);
	}

	private boolean isResponseEqual(EntityAttributes<FeedbackQuestion> question, WebElement responseField, FeedbackResponseAttributes response) {
	    if (question.getQuestionType().equals(FeedbackQuestionType.RUBRIC)) {
	        return isRubricResponseEqual(responseField, response);
	    } else {
	        return getAnswerString(question, response.getResponseDetailsCopy()).equals(responseField.getText());
	    }
	}

	private boolean isRubricResponseEqual(WebElement responseField, FeedbackResponseAttributes response) {
	    FeedbackRubricResponseDetails responseDetails = (FeedbackRubricResponseDetails) response.getResponseDetailsCopy();
	    List<Integer> answers = responseDetails.getAnswer();
	    for (int i = 0; i < answers.size(); i++) {
	        WebElement rubricRow = responseField.findElements(By.cssSelector("#rubric-answers tr")).get(i);
	        WebElement rubricCell = rubricRow.findElements(By.tagName("td")).get(answers.get(i) + 1);
	        if (rubricCell.findElements(By.className("fa-check")).size() == 0) {
	            return false;
	        }
	    }
	    return true;
	}

	private boolean isAnonymous(String identifier) {
	    return identifier.contains(Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT);
	}

	private boolean isAnyAnonymousResponseEqual(EntityAttributes<FeedbackQuestion> question, WebElement responseView, FeedbackResponseAttributes response) {
	    List<WebElement> giverNames = responseView.findElements(By.id("response-giver"));
	    List<WebElement> responseFields = getAllResponseFields(responseView);
	    for (int i = 0; i < giverNames.size(); i++) {
	        if (isAnonymous(giverNames.get(i).getText()) && isResponseEqual(question, responseFields.get(i), response)) {
	            return true;
	        }
	    }
	    return false;
	}

	private String getOpeningTime() {
	    return sessionOpeningTime.getText();
	}

	private String getClosingTime() {
	    return sessionClosingTime.getText();
	}

	private void assertDateEquals(String actual, Instant instant, String timeZone) {
	    String dateStrWithAbbr = getDateStringWithAbbr(instant, timeZone);
	    String dateStrWithOffset = getDateStringWithOffset(instant, timeZone);
	
	    assertTrue(actual.equals(dateStrWithAbbr) || actual.equals(dateStrWithOffset));
	}

	private String getDateStringWithAbbr(Instant instant, String timeZone) {
	    return getDisplayedDateTime(instant, timeZone, "EE, dd MMM, yyyy, hh:mm a z");
	}

	private String getDateStringWithOffset(Instant instant, String timeZone) {
	    return getDisplayedDateTime(instant, timeZone, "EE, dd MMM, yyyy, hh:mm a X");
	}

	private String getQuestionText(int questionNum) {
	    return getQuestionResponsesSection(questionNum).findElement(By.id("question-text")).getText().trim();
	}

	private String getMcqAddInfo(FeedbackMcqQuestionDetails questionDetails) {
	    String additionalInfo = "Multiple-choice (single answer) question options:" + TestProperties.LINE_SEPARATOR;
	    return appendMultiChoiceInfo(additionalInfo, questionDetails.getGenerateOptionsFor(),
	            questionDetails.getMcqChoices(), questionDetails.isOtherEnabled());
	}

	private String getMsqAddInfo(FeedbackMsqQuestionDetails questionDetails) {
	    String additionalInfo = "Multiple-choice (multiple answers) question options:" + TestProperties.LINE_SEPARATOR;
	    return appendMultiChoiceInfo(additionalInfo, questionDetails.getGenerateOptionsFor(),
	            questionDetails.getMsqChoices(), questionDetails.isOtherEnabled());
	}

	private String appendMultiChoiceInfo(String info, FeedbackParticipantType generateOptionsFor, List<String> choices, boolean isOtherEnabled) {
	    StringBuilder additionalInfo = new StringBuilder(info);
	    if (generateOptionsFor.equals(FeedbackParticipantType.NONE)) {
	        additionalInfo = appendOptions(additionalInfo, choices);
	        if (isOtherEnabled) {
	            additionalInfo.append(TestProperties.LINE_SEPARATOR).append("Other");
	        }
	    } else {
	        additionalInfo.append("The options for this question is automatically generated from the list of all ")
	                .append(getDisplayGiverName(generateOptionsFor).toLowerCase())
	                .append('.');
	
	    }
	    return additionalInfo.toString();
	}

	private String getRubricAddInfo(FeedbackRubricQuestionDetails questionDetails) {
	    StringBuilder additionalInfo = new StringBuilder("Rubric question sub-questions:");
	    additionalInfo.append(TestProperties.LINE_SEPARATOR);
	    return appendOptions(additionalInfo, questionDetails.getRubricSubQuestions()).toString();
	}

	private String getNumScaleAddInfo(FeedbackNumericalScaleQuestionDetails questionDetails) {
	    return "Numerical-scale question:" + TestProperties.LINE_SEPARATOR
	            + "Minimum value: " + questionDetails.getMinScale()
	            + ". Increment: " + questionDetails.getStep()
	            + ". Maximum value: " + questionDetails.getMaxScale() + ".";
	}

	private String getRankOptionsAddInfo(FeedbackRankOptionsQuestionDetails questionDetails) {
	    StringBuilder additionalInfo = new StringBuilder("Rank (options) question options:");
	    additionalInfo.append(TestProperties.LINE_SEPARATOR);
	    return appendOptions(additionalInfo, questionDetails.getOptions()).toString();
	}

	private String getConstSumOptionsAddInfo(FeedbackConstantSumQuestionDetails questionDetails) {
	    StringBuilder additionalInfo = new StringBuilder("Distribute points (among options) question options:");
	    additionalInfo.append(TestProperties.LINE_SEPARATOR);
	    additionalInfo = appendOptions(additionalInfo, questionDetails.getConstSumOptions());
	    additionalInfo.append(TestProperties.LINE_SEPARATOR);
	    if (questionDetails.isPointsPerOption()) {
	        additionalInfo.append("Points per option: ");
	    } else {
	        additionalInfo.append("Total points: ");
	    }
	    additionalInfo.append(questionDetails.getPoints());
	    return additionalInfo.toString();
	}

	private String getConstSumRecipientsAddInfo(FeedbackConstantSumQuestionDetails questionDetails) {
	    StringBuilder additionalInfo = new StringBuilder("Distribute points (among recipients) question");
	    additionalInfo.append(TestProperties.LINE_SEPARATOR);
	    if (questionDetails.isPointsPerOption()) {
	        additionalInfo.append("Points per recipient: ");
	    } else {
	        additionalInfo.append("Total points: ");
	    }
	    additionalInfo.append(questionDetails.getPoints());
	    return additionalInfo.toString();
	}

	private StringBuilder appendOptions(StringBuilder info, List<String> options) {
	    StringBuilder additionalInfo = info;
	    for (String option : options) {
	        additionalInfo.append(option).append(TestProperties.LINE_SEPARATOR);
	    }
	    return additionalInfo.deleteCharAt(additionalInfo.length() - 1);
	}

	private WebElement getQuestionResponsesSection(int questionNum) {
	    return browser.driver.findElement(By.id("question-" + questionNum + "-responses"));
	}

	private void showAdditionalInfo(int qnNumber) {
	    WebElement additionalInfoLink = getQuestionResponsesSection(qnNumber).findElement(By.id("additional-info-link"));
	    if (additionalInfoLink.getText().equals("[more]")) {
	        click(additionalInfoLink);
	        waitUntilAnimationFinish();
	    }
	}

	private String getAdditionalInfo(int questionNum) {
	    showAdditionalInfo(questionNum);
	    return getQuestionResponsesSection(questionNum).findElement(By.id("additional-info")).getText();
	}

	private WebElement getGivenResponseField(int questionNum, String receiver) {
	    int recipientIndex = getGivenRecipientIndex(questionNum, receiver);
	    return getQuestionResponsesSection(questionNum)
	            .findElements(By.cssSelector("#given-responses tm-single-response"))
	            .get(recipientIndex);
	}

	private int getGivenRecipientIndex(int questionNum, String recipient) {
	    List<WebElement> recipients = getQuestionResponsesSection(questionNum)
	            .findElements(By.cssSelector("#given-responses #response-recipient"));
	    for (int i = 0; i < recipients.size(); i++) {
	        if (recipients.get(i).getText().split("To: ")[1].equals(recipient)) {
	            return i;
	        }
	    }
	    throw new AssertionError("Recipient not found: " + recipient);
	}

	private String getAdditionalInfoString(EntityAttributes<FeedbackQuestion> question) {
	    switch (question.getQuestionType()) {
	    case TEXT:
	        return "";
	    case MCQ:
	        return getMcqAddInfo((FeedbackMcqQuestionDetails) question.getQuestionDetailsCopy());
	    case MSQ:
	        return getMsqAddInfo((FeedbackMsqQuestionDetails) question.getQuestionDetailsCopy());
	    case RUBRIC:
	        return getRubricAddInfo((FeedbackRubricQuestionDetails) question.getQuestionDetailsCopy());
	    case NUMSCALE:
	        return getNumScaleAddInfo((FeedbackNumericalScaleQuestionDetails) question.getQuestionDetailsCopy());
	    case CONTRIB:
	        return "Team contribution question";
	    case RANK_OPTIONS:
	        return getRankOptionsAddInfo((FeedbackRankOptionsQuestionDetails) question.getQuestionDetailsCopy());
	    case RANK_RECIPIENTS:
	        return "Rank (recipients) question";
	    case CONSTSUM_OPTIONS:
	        return getConstSumOptionsAddInfo((FeedbackConstantSumQuestionDetails) question.getQuestionDetailsCopy());
	    case CONSTSUM_RECIPIENTS:
	        return getConstSumRecipientsAddInfo((FeedbackConstantSumQuestionDetails) question.getQuestionDetailsCopy());
	    default:
	        throw new AssertionError("Unknown question type: " + question.getQuestionType());
	    }
	}

	private String getAnswerString(EntityAttributes<FeedbackQuestion> question, FeedbackResponseDetails response) {
	    switch(response.getQuestionType()) {
	    case TEXT:
	    case NUMSCALE:
	    case RANK_RECIPIENTS:
	        return response.getAnswerString();
	    case MCQ:
	    case MSQ:
	        return response.getAnswerString().replace(", ", TestProperties.LINE_SEPARATOR);
	    case RANK_OPTIONS:
	        return getRankOptionsAnsString((FeedbackRankOptionsQuestionDetails) question.getQuestionDetailsCopy(),
	                (FeedbackRankOptionsResponseDetails) response);
	    case CONSTSUM:
	        return getConstSumOptionsAnsString((FeedbackConstantSumQuestionDetails) question.getQuestionDetailsCopy(),
	                (FeedbackConstantSumResponseDetails) response);
	    case RUBRIC:
	    case CONTRIB:
	        return "";
	    default:
	        throw new AssertionError("Unknown question type: " + response.getQuestionType());
	    }
	}

	private String getRankOptionsAnsString(FeedbackRankOptionsQuestionDetails question, FeedbackRankOptionsResponseDetails responseDetails) {
	    List<String> options = question.getOptions();
	    List<Integer> answers = responseDetails.getAnswers();
	    List<String> answerStrings = new ArrayList<>();
	    for (int i = 1; i <= options.size(); i++) {
	        answerStrings.add(i + ": " + options.get(answers.indexOf(i)));
	    }
	    return String.join(TestProperties.LINE_SEPARATOR, answerStrings);
	}

	private String getConstSumOptionsAnsString(FeedbackConstantSumQuestionDetails question, FeedbackConstantSumResponseDetails responseDetails) {
	    if (question.isDistributeToRecipients()) {
	        return responseDetails.getAnswerString();
	    }
	    List<String> options = question.getConstSumOptions();
	    List<Integer> answers = responseDetails.getAnswers();
	    List<String> answerStrings = new ArrayList<>();
	    for (int i = 0; i < options.size(); i++) {
	        answerStrings.add(options.get(i) + ": " + answers.get(i));
	    }
	    answerStrings.sort(Comparator.naturalOrder());
	    return String.join(TestProperties.LINE_SEPARATOR, answerStrings);
	}

	private List<WebElement> getAllResponseViews(int questionNumber) {
	    return getQuestionResponsesSection(questionNumber).findElements(By.tagName("tm-student-view-responses"));
	}

	private List<WebElement> getAllResponseFields(WebElement responseView) {
	    return responseView.findElements(By.tagName("tm-single-response"));
	}

	private WebElement getNumScaleStatistics(int questionNum) {
	    return getQuestionResponsesSection(questionNum).findElement(By.cssSelector("#numscale-statistics tbody tr"));
	}

	private WebElement getRubricExcludeSelfCheckbox(int questionNum) {
	    return getQuestionResponsesSection(questionNum).findElement(By.id("exclude-self-checkbox"));
	}

	private WebElement getRubricStatistics(int questionNum) {
	    return getQuestionResponsesSection(questionNum).findElement(By.id("rubric-statistics"));
	}

	private WebElement getRubricPerRecipientStatsPerCriterion(int questionNum) {
	    return getQuestionResponsesSection(questionNum).findElement(By.id("rubric-recipient-statistics-per-criterion"));
	}

	private void sortRubricPerRecipientStatsPerCriterion(int questionNum, int colNum) {
	    click(getRubricPerRecipientStatsPerCriterion(questionNum).findElements(By.tagName("th")).get(colNum - 1));
	}

	private WebElement getRubricPerRecipientStatsOverall(int questionNum) {
	    return getQuestionResponsesSection(questionNum).findElement(By.id("rubric-recipient-statistics-overall"));
	}

	private void sortRubricPerRecipientStatsOverall(int questionNum, int colNum) {
	    click(getRubricPerRecipientStatsOverall(questionNum).findElements(By.tagName("th")).get(colNum - 1));
	}

	private boolean isCommentByResponseGiver(WebElement commentField) {
	    return commentField.findElements(By.id("by-response-giver")).size() > 0;
	}

	private String getCommentGiver(WebElement commentField) {
	    String commentGiverDescription = commentField.findElement(By.id("comment-giver-name")).getText();
	    return commentGiverDescription.split(" commented")[0];
	}

	private String getCommentEditor(WebElement commentField) {
	    String editDescription = commentField.findElement(By.id("last-editor-name")).getText();
	    return editDescription.split("edited by ")[1];
	}

	private List<WebElement> getCommentFields(int questionNum) {
	    return getQuestionResponsesSection(questionNum).findElements(By.tagName("tm-comment-row"));
	}

	private WebElement getCommentField(int questionNum, String commentString) {
	    List<WebElement> commentFields = getCommentFields(questionNum);
	    for (WebElement comment : commentFields) {
	        if (comment.findElement(By.id("comment-text")).getText().equals(commentString)) {
	            return comment;
	        }
	    }
	    throw new AssertionError("Comment field not found");
	}

	private int getGiverIndex(WebElement response, String giver) {
	    List<WebElement> givers = response.findElements(By.id("response-giver"));
	    for (int i = 0; i < givers.size(); i++) {
	        if (givers.get(i).getText().contains(giver)) {
	            return i;
	        }
	    }
	    throw new AssertionError("Giver not found: " + giver);
	}

	/**
	 * Returns the name of the task.
	 */
	public String getName() {
	    return name;
	}

	private int getRecipientIndex(int questionNum, String recipient) {
	    List<WebElement> recipients = getQuestionResponsesSection(questionNum).findElements(By.id("response-recipient"));
	    for (int i = 0; i < recipients.size(); i++) {
	        if (recipients.get(i).getText().split("To: ")[1].equals(recipient)) {
	            return i;
	        }
	    }
	    throw new AssertionError("Recipient not found: " + recipient);
	}

	/**
	 * Returns the name of the task.
	 */
	public String getName() {
	    return name;
	}

}
