package teammates.storage.search;

import java.util.HashMap;
import java.util.Map;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * The {@link SearchDocument} object that defines how we store document for students.
 */
class StudentSearchDocument extends SearchDocument<StudentAttributes> {

    private final CourseAttributes course;
	final StudentAttributes attribute;

    StudentSearchDocument(StudentAttributes student, CourseAttributes course) {
        super(student);
        this.course = course;
    }

    Map<String, Object> getSearchableFields() {
        Map<String, Object> fields = new HashMap<>();
        StudentAttributes student = attribute;
        String[] searchableTexts = {
                student.getName(), student.getEmail(), student.getCourse(),
                course == null ? "" : course.getName(),
                student.getTeam(), student.getSection(),
        };

        fields.put("id", student.getId());
        fields.put("_text_", String.join(" ", searchableTexts));
        fields.put("courseId", student.getCourse());
        fields.put("email", student.getEmail());

        return fields;
    }

}
