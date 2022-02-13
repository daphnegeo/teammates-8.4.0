package teammates.common.util;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link Url}, {@link AppUrl}.
 */
public class UrlTest extends BaseTestCase {

    @Test
    public void testToString() {

        ______TS("local file URL (no clear distinction on its base URL)");

        Url url = new Url("file:///C:/path/to/file.ext");
        assertEquals("/C:/path/to/file.ext", url.toString());
        assertEquals("file:///C:/path/to/file.ext", url.toAbsoluteString());

        ______TS("web URL with no relative path");

        url = new Url("http://www.google.com");
        assertEquals("", url.toString());
        assertEquals("http://www.google.com", url.toAbsoluteString());

        ______TS("typical web URL");

        url = new Url("http://www.google.com/page?key1=value1");
        assertEquals("/page?key1=value1", url.toString());
        assertEquals("http://www.google.com/page?key1=value1", url.toAbsoluteString());

        ______TS("malformed URL: no protocol");

        assertThrows(AssertionError.class, () -> new Url("www.google.com/page"));

        ______TS("malformed URL: unknown protocol");

        assertThrows(AssertionError.class, () -> new Url("randomprotocol://www.google.com/page"));

    }

    @Test
    public void testAppendParameters() {

        ______TS("static method addParamToUrl");

        String url = "http://www.google.com";
        assertEquals(url, Url.addParamToUrl(url, null, "value"));
        assertEquals(url, Url.addParamToUrl(url, "", "value"));
        assertEquals(url, Url.addParamToUrl(url, "key", null));
        assertEquals(url, Url.addParamToUrl(url, "key", ""));
        assertEquals(url + "?key1=value1", Url.addParamToUrl(url, "key1", "value1"));
        url = "http://www.google.com?key1=value1";
        assertEquals(url + "&key2=value2", Url.addParamToUrl(url, "key2", "value2"));
        url = "http://www.google.com?key1=value1&key2=value2";
        assertEquals(url, Url.addParamToUrl(url, "key1", "newvalue1"));
        assertEquals(url, Url.addParamToUrl(url, "key2", "newvalue2"));
        assertEquals(url + "&key3=" + SanitizationHelper.sanitizeForUri("#& ?"), Url.addParamToUrl(url, "key3", "#& ?"));

        ______TS("in-place method withParam");

        Url newUrl = new Url("http://www.google.com/page");
        newUrl.withParam("key1", "value1");
        assertEquals("/page?key1=value1", newUrl.toString());
        newUrl.withParam("key1", "newvalue1");
        assertEquals("http://www.google.com/page?key1=value1", newUrl.toAbsoluteString());
    }

    @Test
    public void testAppUrlAssertion() {

        ______TS("typical non-empty case");

        AppUrl url = new AppUrl("http://www.google.com/page?key1=value1");
        assertEquals("/page?key1=value1", url.toString());
        assertEquals("http://www.google.com/page?key1=value1", url.toAbsoluteString());

        ______TS("empty path case");

        url = new AppUrl("http://www.google.com");
        assertEquals("", url.toString());
        assertEquals("http://www.google.com", url.toAbsoluteString());

        ______TS("malformed URL: not http(s)");

        assertThrows(AssertionError.class, () -> new AppUrl("file:///C:/path/to/file.ext"));

    }

	@Override
	protected EntityAttributes<Account> getAccount(EntityAttributes<Account> account) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected StudentProfileAttributes getStudentProfile(StudentProfileAttributes studentProfileAttributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CourseAttributes getCourse(CourseAttributes course) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected EntityAttributes<FeedbackQuestion> getFeedbackQuestion(EntityAttributes<FeedbackQuestion> fq) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected FeedbackResponseCommentAttributes getFeedbackResponseComment(FeedbackResponseCommentAttributes frc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected FeedbackResponseAttributes getFeedbackResponse(FeedbackResponseAttributes fr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected FeedbackSessionAttributes getFeedbackSession(FeedbackSessionAttributes fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected InstructorAttributes getInstructor(InstructorAttributes instructor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected StudentAttributes getStudent(StudentAttributes student) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean doRemoveAndRestoreDataBundle(DataBundle testData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean doPutDocuments(DataBundle testData) {
		// TODO Auto-generated method stub
		return false;
	}

}
