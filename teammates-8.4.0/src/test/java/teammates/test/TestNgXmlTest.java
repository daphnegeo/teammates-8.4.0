package teammates.test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

/**
 * Verifies that the testng-component.xml configuration file contains all the component test cases in the project.
 */
public class TestNgXmlTest extends BaseTestCase {

    @Test
    public void checkTestsInTestNg() throws Exception {
        String testNgXml = FileHelper.readFile("./src/test/resources/testng-component.xml");

        // <class name, package name>
        Map<String, String> testFiles = getTestFiles(testNgXml, "./src/test/java/teammates");

        testFiles.forEach((key, value) -> assertTrue(isTestFileIncluded(testNgXml, value, key)));
    }

    /**
     * Files to be checked in testng-component.xml are added to testFiles.
     *
     * @param testNgXml    Contents of testng-component.xml
     * @param rootPath     Root path of test files
     * @return             Map containing {@code <class name, package name>}
     */
    private Map<String, String> getTestFiles(String testNgXml, String rootPath) {
        // BaseComponentTestCase, BaseTestCase (files in current directory) excluded because
        // base classes are extended by the actual tests

        return addFilesToTestsRecursively(rootPath, true, "teammates", testNgXml);
    }

    private boolean isTestFileIncluded(String testNgXml, String packageName, String testClassName) {
        return testNgXml.contains("<class name=\"" + packageName + "." + testClassName + "\" />");
    }

    /**
     * Recursively adds files from testng-component.xml which are to be checked.
     *
     * @param path                            Check files and directories in the current path
     *
     * @param areFilesInCurrentDirExcluded    If true, files in the current path are not
     *                                        added to tests but sub-directories are still checked
     *
     * @param packageName                     Package name of the current file
     * @param testNgXml                       Contents of testng-component.xml
     *
     * @return                                Map containing {@code <class name, package name>} including
     *                                        current file or tests in the current directory
     */
    private Map<String, String> addFilesToTestsRecursively(String path, boolean areFilesInCurrentDirExcluded,
            String packageName, String testNgXml) {

        Map<String, String> testFiles = new HashMap<>();
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) {
            return testFiles;
        }

        for (File file : listOfFiles) {
            String name = file.getName();

            if (file.isFile() && name.endsWith(".java") && !name.startsWith("package-info")
                    && !areFilesInCurrentDirExcluded) {
                testFiles.put(name.replace(".java", ""), packageName);

            } else if (file.isDirectory() && !name.endsWith("browsertests") && !name.endsWith("pageobjects")
                    && !name.endsWith("architecture")) {
                // If the package name is in TestNG in the form of <package name="teammates.package.name" />
                // then files in the current directory are excluded because the whole package would be tested by TestNG.

                testFiles.putAll(
                        addFilesToTestsRecursively(path + "/" + name,
                                                   isPackageNameInTestNg(packageName + "." + name, testNgXml),
                                                   packageName + "." + name, testNgXml));
            }
        }

        return testFiles;
    }

    private boolean isPackageNameInTestNg(String packageName, String testNgXml) {
        return testNgXml.contains("<package name=\"" + packageName + "\" />");
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
