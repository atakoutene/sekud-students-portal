package database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import models.Book;
import models.Course;
import models.CourseSchedule;
import models.FinalMark;
import models.LatestAssessment;
import models.LectureNote;
import models.Login;
import models.Mark;
import models.Person;
import models.ProjectMark;
import models.Semester;
import models.Student;
import models.StudentMentor;
import models.Timetable;
import models.UsefulLink;
import util.MyDate;

/**
 *
 * @author Roger NDJEUMOU
 */
public class DatabaseConnectionManager {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/student_portal?zeroDateTimeBehavior=CONVERT_TO_NULL";
    private static final String DATABASE_USERNAME = "root";
    private static final String DATABASE_PASSWORD = "";

    private Connection connection;

    public DatabaseConnectionManager() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnectionManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Login checkLogin(Login user) {
        try {
            Statement myStatement = connection.createStatement();
            ResultSet myResultSet = myStatement.executeQuery(
                    "SELECT * FROM login WHERE reg_number='" + user.getRegNumber()
                    + "' AND password=SHA1('" + user.getPassword() + "') ;");
            if (myResultSet.next()) {
                Login newLogin = new Login(
                        myResultSet.getInt("login_id"),
                        myResultSet.getString("reg_number"),
                        myResultSet.getString("password"),
                        myResultSet.getString("login_type")
                );

                myResultSet.close();
                myStatement.close();

                return newLogin;
            }
        } catch (SQLException e) {
            System.out.println("LOGIN MANAGER ERROR: "
                    + e.getMessage());
        }

        return user;
    }

    public String getPseudo(int idLogin) {
        try {
            Statement myStatemt = connection.createStatement();
            ResultSet myRs = myStatemt.executeQuery(
                    "SELECT per_pseudo FROM person WHERE login_id="
                    + idLogin + " ;");
            if (myRs.next()) {
                return myRs.getString("per_pseudo");
            }
        } catch (SQLException e) {
            System.out.println("GET PSEUDO ERROR: " + e.getMessage());
        }

        return "";
    }

    public Person getPersonInfo(int idLogin) {
        String query = "SELECT * FROM `person` WHERE `login_id`= ?";
        try {
            PreparedStatement retrieve = connection.prepareStatement(query);
            retrieve.setInt(1, idLogin);
            ResultSet rs = retrieve.executeQuery();

            if (rs.next()) {
                // Get the profile picture as an image file
                InputStream inStream = rs.getBinaryStream("per_profile_pic");
                Image profilePic = new Image(inStream);
                // Get the other data
                return new Person(
                        rs.getInt("per_id"),
                        rs.getInt("login_id"),
                        rs.getString("per_last_name"),
                        rs.getString("per_first_name"),
                        (rs.getString("per_gender")).charAt(0),
                        rs.getDate("per_DOB"),
                        rs.getString("per_phone_number"),
                        rs.getString("per_address"),
                        rs.getString("per_email"),
                        profilePic,
                        rs.getString("per_pseudo"),
                        rs.getString("per_status"),
                        rs.getString("per_title"));
            }

        } catch (SQLException e) {
            System.out.println("GET PERSON INFO ERROR: " + e.getMessage());
        }

        return null;
    }

    public Student getStudentInfo(String regNumber) {
        String query = "SELECT `stud_id`, `per_id`, `par_id`, "
                + "`prog_id`, `stud_entrance_year`, `stud_level`,"
                + " `stud_status` FROM `student` "
                + "WHERE `stud_id`=?";

        try {
            PreparedStatement prepStatement = connection.prepareStatement(query);
            prepStatement.setString(1, regNumber);
            ResultSet rs = prepStatement.executeQuery();
            if (rs.next()) {
                return (new Student(
                        rs.getString("stud_id"),
                        rs.getInt("per_id"),
                        rs.getInt("par_id"),
                        rs.getInt("prog_id"),
                        rs.getInt("stud_level"),
                        rs.getInt("stud_entrance_year"),
                        rs.getString("stud_status")));
            }
        } catch (SQLException e) {
            System.out.println("GET STUDENT INFO ERROR: "
                    + e.getMessage());
        }

        return null;
    }

    public ArrayList<Object> getSchedule(Student student, int idSemester,
            String day) {

        ArrayList<CourseSchedule> schedules = new ArrayList<>();
        ArrayList<Course> courses = new ArrayList<>();

        String query = "SELECT `sched_id`, course_schedule.`course_id`, "
                + "`sched_day`, `start_time`, `end_time`, `room`, course.course_title "
                + "FROM `course_schedule` "
                + "JOIN course USING (course_id) "
                + "JOIN enrollment USING (course_id) "
                + "JOIN student USING (stud_id) "
                + "WHERE stud_id = ? AND sched_day = ? AND enrollment.semester_id = ? "
                + "ORDER BY start_time ASC;";

        try {
            PreparedStatement prepStatement = connection.prepareStatement(query);
            prepStatement.setString(1, student.getId());
            prepStatement.setString(2, day);
            prepStatement.setInt(3, idSemester);

            ResultSet rs = prepStatement.executeQuery();
            while (rs.next()) {
                schedules.add(
                        new CourseSchedule(
                                rs.getInt("sched_id"),
                                rs.getString("course_id"),
                                rs.getString("sched_day"),
                                rs.getTime("start_time"),
                                rs.getTime("end_time"),
                                rs.getString("room"))
                );
                courses.add(
                        new Course(
                                rs.getString("course_id"),
                                rs.getString("course_title"))
                );
            }
        } catch (SQLException e) {
            System.out.println("GET SCHEDULE ERROR: "
                    + e.getMessage());
        }

        ArrayList<Object> objects = new ArrayList<>();
        objects.add(schedules);
        objects.add(courses);

        return objects;
    }

    public Timetable getTimetable(int idProgram, String semester, int year) {
        String query = "SELECT `timetable_id`, timetable.`semester_id`, timetable.`depart_id`, `timetable` "
                + "FROM `timetable` "
                + "JOIN semester ON timetable.semester_id = semester.semester_id "
                + "JOIN program ON timetable.depart_id = program.depart_id "
                + "WHERE semester.semester_name = ? AND "
                + "semester.semester_year = ? AND "
                + "program.prog_id = ? ;";

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, semester);
            ps.setInt(2, year);
            ps.setInt(3, idProgram);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                File timetableFile = getFile("output/timetable",
                        rs.getBinaryStream("timetable"));
                return (new Timetable(
                        rs.getInt("timetable_id"),
                        rs.getInt("semester_id"),
                        rs.getInt("depart_id"),
                        timetableFile));

            }
        } catch (SQLException e) {
            System.out.println("GET TIMETABLE ERROR: "
                    + e.getMessage());
        }

        return null;
    }
public ArrayList<Object> retrieveCoursesAndAttendenceRateFromDatabase(String idStudent){
        ArrayList<Object> object = new ArrayList<>();
        ArrayList<Course> courses = new ArrayList<>();
        ArrayList<Double> attendanceRate = new ArrayList<>();
        String query = "SELECT course.course_id, course.course_title,course_sessions.semester_id, "
                + "(COUNT(attendance.attendance_date)/course_sessions.num_sessions)*100.0 AS attendance_rate "
                + "FROM course_sessions "
                + "JOIN attendance ON course_sessions.course_id = attendance.course_id "
                + "JOIN course ON attendance.course_id=course.course_id "
                + "WHERE attendance.stud_id= ? "
                + "GROUP BY course.course_title "
                + "HAVING course_sessions.semester_id = MAX(course_sessions.semester_id) "
                + "ORDER BY course.course_title;";
        
        try{
           PreparedStatement prepStatement = connection.prepareStatement(query);
           prepStatement.setString(1, idStudent); 
           ResultSet rs = prepStatement.executeQuery();
           while(rs.next()){
               courses.add(new Course(rs.getString("course_id"),
                       rs.getString("course_title")));
               attendanceRate.add(rs.getDouble("attendance_rate"));
           }
           rs.close();
        }
        catch(SQLException e){
            System.out.println("retrieve Courses And Attendence Rate From Database Error: " 
                            + e.getMessage());
        }
        
        object.add(courses);
        object.add(attendanceRate);
        
        return object;
}
    public ArrayList<Object> retrieveDatePresentForACourseFromDatabase(String idStudent, String idCourse){
        ArrayList<Object> object = new ArrayList<>();
        String courseName ="";
        ArrayList<java.util.Date> dates_present = new ArrayList<>();
        //ArrayList<LocalDate> dates_present = new ArrayList<>();
        String query = "SELECT attendance.`attendance_date`, attendance.semester_id, course.course_title "
                + "FROM `attendance` JOIN course on attendance.course_id = course.course_id "
                + "where attendance.course_id = ? and attendance.`stud_id` = ? "
                + "GROUP by attendance.`attendance_date` "
                + "having attendance.semester_id = MAX(attendance.semester_id);";
        try{
            PreparedStatement prepStatement = connection.prepareStatement(query);
            prepStatement.setString(1, idCourse);
            prepStatement.setString(2, idStudent);
            ResultSet rs = prepStatement.executeQuery();
           while(rs.next()){
               //System.out.println(rs.getDate("attendance_date").getDate()+" "+rs.getDate("attendance_date").getMonth()+" "+(rs.getDate("attendance_date").getYear() + 1900));
               dates_present.add(new java.util.Date(rs.getDate("attendance_date").getTime()));  // the getTime() method is udes to convert sql_date to util_date.
               //dates_present.add(new LocalDate((rs.getDate("attendance_date").getYear() + 1900),(rs.getDate("attendance_date").getMonth() + 1), rs.getDate("attendance_date").getDate());
               courseName = rs.getString("course_title");
           }
           
           rs.close();

        }
        catch(SQLException e){
            System.out.println("GET retrieve Date Present For A Course From Database ERROR: "
                    + e.getMessage());  
        }
        
        //return dates_present;
        object.add(dates_present);
        object.add(courseName);
        return object;
    }
    
    public ArrayList<Object> retrieveDateAbsentForACourseFromDatabase(String idStudent, String idCourse){
        ArrayList<Object> object = new ArrayList<>();
        ArrayList<java.util.Date> dates_absent = new ArrayList<>();
        String query = "SELECT attendance.`attendance_date`, attendance.semester_id FROM `attendance` "
                + "WHERE attendance.course_id = ? and (attendance.attendance_date,attendance.semester_id) "
                + "NOT IN (SELECT attendance.`attendance_date` as date_present, attendance.semester_id "
                + "FROM `attendance` where attendance.course_id = ? and attendance.`stud_id` = ? "
                + "GROUP by attendance.`attendance_date` having attendance.semester_id = MAX(attendance.semester_id))"
                + " GROUP BY attendance.`attendance_date` HAVING attendance.semester_id = MAX(attendance.semester_id);";
        try{
            PreparedStatement prepStatement = connection.prepareStatement(query);
            prepStatement.setString(1, idCourse);
            prepStatement.setString(2, idCourse);
            prepStatement.setString(3, idStudent);            
            ResultSet rs = prepStatement.executeQuery();
           while(rs.next()){
               dates_absent.add(new java.util.Date(rs.getDate("attendance_date").getTime()));  // the getTime() method is udes to convert sql_date to util_date.
           }
           rs.close();

        }
        catch(SQLException e){
            System.out.println("GET retrieve Date Absent For A Course From Database ERROR: "
                    + e.getMessage());  
        }
        
        //return dates_absent;
        object.add(dates_absent);
        return object;
    }
    
    public Semester getCurrentSemester() {
        Semester semester = new Semester();

        String query = "SELECT `semester_id`, `semester_name`, `semester_year` "
                + "FROM `semester` WHERE `semester_name`= ? "
                + "AND `semester_year`= ? ;";
        // Set the semester name from the current date
        MyDate md = new MyDate();
        String semName = "Spring";
        if (md.getMonth() > 7) // Fall semester starts from september
        {
            semName = "Fall";
        }

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, semName);
            ps.setInt(2, md.getYear());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                semester.setId(rs.getInt("semester_id"));
                semester.setName(rs.getString("semester_name"));
                semester.setYear(rs.getInt("semester_year"));
            }
        } catch (SQLException e) {
            System.out.println("GET CURRENT SEMESTER ERROR: " + e.getMessage());
        }

        return semester;
    }

    public ArrayList<Object> getEnrolledCoursesAndLecturerInfo(String idStudent, int idSemester) {
        ArrayList<Object> infos = new ArrayList<>();

        String query = "SELECT course.`course_id`, `course_title`, "
                + "`course_description`, `course_credit`, "
                + "`course_passing_score`, `course_syllabus`, "
                + "lect_id, person.per_id, person.per_last_name, "
                + "person.per_first_name, person.per_phone_number, "
                + "person.per_email, person.per_title "
                + "FROM `course` JOIN enrollment USING (course_id) "
                + "JOIN lecturer USING (lect_id) JOIN person USING (per_id) "
                + "WHERE enrollment.stud_id = ? AND enrollment.semester_id = ? "
                + "ORDER BY course_title ASC;";

        ArrayList<Course> courses = new ArrayList<>();
        ArrayList<Person> persons = new ArrayList<>();

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, idStudent);
            ps.setInt(2, idSemester);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                File syllabus = getFile("output/syllabus " + rs.getString("course_id"),
                        rs.getBinaryStream("course_syllabus"));
                courses.add(new Course(
                        rs.getString("course_id"),
                        rs.getString("course_title"),
                        rs.getString("course_description"),
                        rs.getInt("course_credit"),
                        rs.getInt("course_passing_score"),
                        syllabus,
                        rs.getInt("lect_id")
                ));
                persons.add(new Person(
                        rs.getInt("per_id"),
                        rs.getString("per_last_name"),
                        rs.getString("per_first_name"),
                        rs.getString("per_phone_number"),
                        rs.getString("per_email"),
                        rs.getString("per_title")
                ));
            }

            infos.add(courses);
            infos.add(persons);

        } catch (SQLException e) {
            System.out.println(
                    "GET ENROLLED COURSES AND LECTURE INFO ERROR: "
                    + e.getMessage());
        }

        return infos;
    }

    public ArrayList<Book> getCourseBooks(String idCourse) {
        ArrayList<Book> books = new ArrayList<>();
        String query = "SELECT `book_id`, `course_id`, "
                + "`book_title`, `book_references`, "
                + "`book_availability`, `book_link` "
                + "FROM `book` "
                + "WHERE `course_id`= ? ;";

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, idCourse);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                books.add(
                        new Book(
                                rs.getInt("book_id"),
                                idCourse,
                                rs.getString("book_title"),
                                rs.getString("book_references"),
                                rs.getString("book_availability"),
                                rs.getString("book_link")
                        )
                );
            }
        } catch (SQLException e) {
            System.out.println(
                    "GET COURSE BOOKS ERROR: "
                    + e.getMessage());
        }

        return books;
    }

    public ArrayList<UsefulLink> getCourseUsefulLinks(String idCourse) {
        ArrayList<UsefulLink> links = new ArrayList<>();
        String query = "SELECT `link_id`, `course_id`, "
                + "`description`, `link` "
                + "FROM `useful_link` "
                + "WHERE `course_id`= ? ;";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, idCourse);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                links.add(
                        new UsefulLink(
                                rs.getInt("link_id"),
                                idCourse,
                                rs.getString("description"),
                                rs.getString("link")
                        )
                );
            }
        } catch (SQLException e) {
            System.out.println("GET COURSE USEFUL LINK ERROR: "
                    + e.getMessage());
        }

        return links;
    }

    public ArrayList<LectureNote> getCourseLectureNotes(String idCourse) {
        ArrayList<LectureNote> notes = new ArrayList<>();
        String query = "SELECT `note_id`, `course_id`, "
                + "`note_description`, `note_pdf` "
                + "FROM `lecture_note` WHERE `course_id`= ? ;";

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, idCourse);
            ResultSet rs = ps.executeQuery();
            int i = 1;
            while (rs.next()) {
                File noteFile = getFile("output/" + idCourse
                        + " - Lecture note " + i,
                        rs.getBinaryStream("note_pdf"));
                notes.add(
                        new LectureNote(
                                rs.getInt("note_id"),
                                idCourse,
                                rs.getString("note_description"),
                                noteFile
                        )
                );
                i++;
            }
        } catch (SQLException e) {
            System.out.println("GET COURSE LECTURE NOTES ERROR: "
                    + e.getMessage());
        }
        return notes;
    }

    public ArrayList<Object> getMentorInfo(String idStudent,
            int idSemeter, int idProgram) {
        ArrayList<Object> mentorsInfo = new ArrayList<>();
        ArrayList<StudentMentor> mentors = new ArrayList<>();
        ArrayList<Person> persons = new ArrayList<>();
        String query = "SELECT student_mentor.`stud_id`, "
                + "`availability`, person.per_last_name, "
                + "person.per_first_name, person.per_phone_number, "
                + "person.per_email "
                + "FROM `student_mentor` "
                + "JOIN student USING (`stud_id`) "
                + "JOIN person USING (per_id) "
                + "WHERE `stud_id` != ? AND `program_id` = ? "
                + "AND `semester_id`=? "
                + "ORDER BY person.per_last_name ASC;";

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, idStudent);
            ps.setInt(2, idProgram);
            ps.setInt(3, idSemeter);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                mentors.add(
                        new StudentMentor(
                                rs.getString("stud_id"),
                                rs.getString("availability")
                        )
                );
                persons.add(
                        new Person(
                                rs.getString("per_last_name"),
                                rs.getString("per_first_name"),
                                rs.getString("per_phone_number"),
                                rs.getString("per_email")
                        )
                );
            }
            mentorsInfo.add(mentors);
            mentorsInfo.add(persons);
        } catch (SQLException e) {
            System.out.println(
                    "GET MENTOR INFO ERROR: "
                    + e.getMessage());
        }

        return mentorsInfo;
    }

    public ArrayList<Mark> getAllAssignmentMarks(String idCourse, String idStudent) {
        ArrayList<Mark> assignmentMarks = new ArrayList<>();
        String query = "SELECT `ass_id`, `ass_mark`, `ass_date` "
                + "FROM `assignment_mark` "
                + "WHERE `stud_id`=? AND `course_id`=? "
                + "ORDER BY `ass_date` ASC";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, idStudent);
            ps.setString(2, idCourse);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                assignmentMarks.add(
                        new Mark(
                                rs.getInt("ass_id"),
                                idCourse,
                                idStudent,
                                rs.getDouble("ass_mark"),
                                rs.getDate("ass_date")
                        )
                );
            }
        } catch (SQLException e) {
            System.out.println("GET ALL ASSIGNMENT MARKS ERROR: "
                    + e.getMessage());
        }
        return assignmentMarks;
    }

    public double getAverageAssignmentMark(String idCourse, String idStudent) {
        String query = "SELECT AVG(`ass_mark`) AS avg_mark "
                + "FROM `assignment_mark` "
                + "WHERE `stud_id`=? AND `course_id`=? ";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, idStudent);
            ps.setString(2, idCourse);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("avg_mark");
            }
        } catch (SQLException e) {
            System.out.println("GET AVERAGE ASSIGNMENT MARK ERROR: "
                    + e.getMessage());
        }
        return 0;
    }

    public ArrayList<Mark> getAllQuizMarks(String idCourse, String idStudent) {
        ArrayList<Mark> quizMarks = new ArrayList<>();
        String query = "SELECT `quiz_id`,`quiz_mark`,`quiz_date` "
                + "FROM `quiz_mark` "
                + "WHERE `stud_id`=? AND `course_id`=? "
                + "ORDER BY `quiz_date` ASC;";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, idStudent);
            ps.setString(2, idCourse);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                quizMarks.add(
                        new Mark(
                                rs.getInt("quiz_id"),
                                idCourse,
                                idStudent,
                                rs.getDouble("quiz_mark"),
                                rs.getDate("quiz_date")
                        )
                );
            }
        } catch (SQLException e) {
            System.out.println("GET ALL QUIZ MARKS ERROR: "
                    + e.getMessage());
        }
        return quizMarks;
    }

    public double getAverageQuizMark(String idCourse, String idStudent) {
        String query = "SELECT AVG(`quiz_mark`) AS avg_mark "
                + "FROM `quiz_mark` "
                + "WHERE `stud_id`=? AND `course_id`=? ";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, idStudent);
            ps.setString(2, idCourse);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("avg_mark");
            }
        } catch (SQLException e) {
            System.out.println("GET AVERAGE QUIZ MARK ERROR: "
                    + e.getMessage());
        }
        return 0;
    }

    public ArrayList<Mark> getAllTestMarks(String idCourse, String idStudent) {
        ArrayList<Mark> testMarks = new ArrayList<>();
        String query = "SELECT `test_id`,`test_mark`,`test_date` "
                + "FROM `test_mark` "
                + "WHERE `stud_id`=? AND `course_id`=? "
                + "ORDER BY `test_date` ASC ;";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, idStudent);
            ps.setString(2, idCourse);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                testMarks.add(
                        new Mark(
                                rs.getInt("test_id"),
                                idCourse,
                                idStudent,
                                rs.getDouble("test_mark"),
                                rs.getDate("test_date")
                        )
                );
            }
        } catch (SQLException e) {
            System.out.println("GET ALL TEST MARKS ERROR: "
                    + e.getMessage());
        }
        return testMarks;
    }

    public double getAverageTestMark(String idCourse, String idStudent) {
        String query = "SELECT AVG(`test_mark`) AS avg_mark "
                + "FROM `test_mark` "
                + "WHERE `stud_id`=? AND `course_id`=? ";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, idStudent);
            ps.setString(2, idCourse);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("avg_mark");
            }
        } catch (SQLException e) {
            System.out.println("GET AVERAGE TEST MARK ERROR: "
                    + e.getMessage());
        }
        return 0;
    }

    public ArrayList<ProjectMark> getAllProjectMarks(String idCourse, String idStudent) {
        ArrayList<ProjectMark> projectMarks = new ArrayList<>();
        String query = "SELECT `project_id`,project_name,`project_mark`,`project_date` "
                + "FROM `project_mark` "
                + "WHERE `stud_id`=? AND `course_id`=? "
                + "ORDER BY `project_date` ASC ;";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, idStudent);
            ps.setString(2, idCourse);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                projectMarks.add(
                        new ProjectMark(
                                rs.getInt("project_id"),
                                idCourse,
                                idStudent, rs.getString("project_name"),
                                rs.getDouble("project_mark"),
                                rs.getDate("project_date")
                        )
                );
            }
        } catch (SQLException e) {
            System.out.println("GET ALL PROJECT MARKS ERROR: "
                    + e.getMessage());
        }
        return projectMarks;
    }

    public double getAverageProjectMark(String idCourse, String idStudent) {
        String query = "SELECT AVG(`project_mark`) AS avg_mark "
                + "FROM `project_mark` "
                + "WHERE `stud_id`=? AND `course_id`=? ";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, idStudent);
            ps.setString(2, idCourse);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("avg_mark");
            }
        } catch (SQLException e) {
            System.out.println("GET AVERAGE PROJECT MARK ERROR: "
                    + e.getMessage());
        }
        return 0;
    }

    public models.Parent getParentInfo(String idStudent) {
        String query = "SELECT `par_name`, `par_phone`, `par_email` "
                + "FROM `parent` "
                + "JOIN student USING (par_id) "
                + "WHERE student.stud_id = ? ;";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, idStudent);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new models.Parent(
                        rs.getString("par_name"),
                        rs.getString("par_phone"),
                        rs.getString("par_email")
                );
            }
        } catch (SQLException e) {
            System.out.println("GET PARENT INFO ERROR: "
                    + e.getMessage());
        }

        return null;
    }

    public ArrayList<LatestAssessment> getLatestAssessment(String idStudent) {

        ArrayList<LatestAssessment> latestAssessments 
                = new ArrayList<>();
        
        String query = "SELECT `title`,`type`,`due_date`,"
                + "latest_assessment.`course_id`, course.course_title\n"
                + "FROM `latest_assessment` "
                + "JOIN enrollment USING(course_id) "
                + "JOIN course USING(course_id) "
                + "WHERE due_date >= SYSDATE() AND enrollment.stud_id = ? "
                + "ORDER BY due_date;";

        try {
            PreparedStatement prepStatement = connection.prepareStatement(query);
            prepStatement.setString(1, idStudent);

            ResultSet rs = prepStatement.executeQuery();
            while (rs.next()) {
                latestAssessments.add(new LatestAssessment(
                        rs.getString("type"), 
                        rs.getString("title"), 
                        rs.getString("course_id"), 
                        rs.getDate("due_date"), 
                        rs.getString("course_title"))
                );
            }
        } catch (SQLException e) {
            System.out.println("GET LATEST ASSESSMENTS ERRORS: "
                    + e.getMessage());
        }

        return latestAssessments;
    }

    //By Fredy To retrieve final mark of a specific student from the database 
   public ArrayList<Object> getStudentFinalMarks(String regNumber) {
       
       ArrayList<Object> objects = new ArrayList<>();
       ArrayList<FinalMark> marks = new ArrayList<>();
       ArrayList<Semester> semesters = new ArrayList<>();
       ArrayList<Course> courses = new ArrayList<>();
       
        String query = "SELECT `course_id`,`final_mark`,semester.semester_name,semester.semester_year,final_mark.semester_id " +
                       "FROM `final_mark` JOIN semester ON final_mark.semester_id = semester.semester_id " +
                       "WHERE final_mark.stud_id = ? ;";
        
        try {
            PreparedStatement prepStatement = connection.prepareStatement(query);
            prepStatement.setString(1, regNumber);
            ResultSet rs = prepStatement.executeQuery();
            while (rs.next()) {
                FinalMark newFinalMark = new FinalMark(rs.getDouble("final_mark"));
                Semester newSemester = new Semester(rs.getInt("semester_id"),rs.getString("semester_name"),rs.getInt("semester_year"));
                Course newCourse = new Course(rs.getString("course_id"));
                marks.add(newFinalMark);
                semesters.add(newSemester);
                courses.add(newCourse);
            }

        } catch (SQLException e) {
            System.out.println("GET STUDENT FINAL MARKS ERROR: "
                    + e.getMessage());
        }

            objects.add(marks);
            objects.add(semesters);
            objects.add(courses);
            return objects;
    }
   
   //By Fredy to retrieve the first and last name of the case student from the database
   public Person getStudentNameFromFinalMarkTable(String regNumber) {
       
      
        String query = "SELECT person.per_last_name, person.per_first_name " +
                       "FROM `final_mark` JOIN student ON final_mark.stud_id = student.stud_id " +
                       "JOIN person ON student.per_id = person.per_id " +
                       "WHERE final_mark.stud_id = ? GROUP BY person.per_last_name;";
        
        try {
            PreparedStatement prepStatement = connection.prepareStatement(query);
            prepStatement.setString(1, regNumber);
            ResultSet rs = prepStatement.executeQuery();
            if (rs.next()) {
                Person person = new Person(rs.getString("per_last_name"),rs.getString("per_first_name"));
                return person;
            }
            
        } catch (SQLException e) {
            System.out.println("GET STUDENT NAME FROM FINAL MARK  ERROR: "
                    + e.getMessage());
        }

        return null;
    }
   
   //By Fredy to retrieve the distinct semester id of the student from the final mark table
   public int getStudentDistinctSemesterIdFromFinalMarkTable(String regNumber) {
        int count = 0;
      
        String query = "SELECT COUNT(DISTINCT semester_id) AS numberOfSemester " +
                       "FROM final_mark " +
                       "WHERE stud_id = ?;";
        
        try {
            PreparedStatement prepStatement = connection.prepareStatement(query);
            prepStatement.setString(1, regNumber);
            ResultSet rs = prepStatement.executeQuery();
            if (rs.next()) {
                count = rs.getInt("numberOfSemester");
            }
            return count;
        } catch (SQLException e) {
            System.out.println("GET DISTINCT SEMESTER ID FROM FINAL MARK TABLE ERRORS: "
                    + e.getMessage());
        }

        return 0;
    }
   
   //By Fredy to retrieve the program name and the faculty name of a specific student
   public ArrayList<String> getProgramNameAndFacultyNameFromASpecificStudent(String regNumber) {
        ArrayList<String> result = new ArrayList<>();
      
        String query = "SELECT faculty.fac_name, department.depart_name " +
                       "FROM `student` JOIN program USING(prog_id) " +
                       "JOIN department USING(depart_id) " +
                       "JOIN faculty USING(fac_id) " +
                       "WHERE student.stud_id = ? ;";
        
        try {
            PreparedStatement prepStatement = connection.prepareStatement(query);
            prepStatement.setString(1, regNumber);
            ResultSet rs = prepStatement.executeQuery();
            if (rs.next()) {
                result.add(rs.getString("fac_name"));
                result.add(rs.getString("depart_name"));
            }
            return result;
        } catch (SQLException e) {
            System.out.println("GET PROGRAM NAME AND FACULTY NAME FROM A SPECIFIC STUDENT ERRORS: "
                    + e.getMessage());
        }

        return null;
    }
   
   //By Fredy to retrieve the level name of a student
   public String getLevelNameOfASpecificStudent(String regNumber) {
        String s = "";
      
        String query = "SELECT `level_name`  " +
                       "FROM `school_level` " +
                       "WHERE `level_id` = (SELECT student.stud_level FROM student WHERE student.stud_id = ?);";
        
        try {
            PreparedStatement prepStatement = connection.prepareStatement(query);
            prepStatement.setString(1, regNumber);
            ResultSet rs = prepStatement.executeQuery();
            if (rs.next()) {
                s = rs.getString("level_name");
            }
            return s;
        } catch (SQLException e) {
            System.out.println("GET LEVEL NAME OF A SPECIFIC STUDENT ERRORS: "
                    + e.getMessage());
        }

        return null;
    }
   
   //By Fredy to update the pseudo of a person
   public void setPersonPseudo(String regNumber, String pseudo) {
      
        String query = "UPDATE `person` " +
                       "SET `per_pseudo`= ? " +
                       "WHERE person.per_id = (SELECT student.per_id FROM student WHERE student.stud_id = ?);";
    //UPDATE `person` SET `per_pseudo`=  "Koodjo" WHERE person.per_id = (SELECT student.per_id FROM student WHERE student.stud_id = "21S034");    
        
        try {
            PreparedStatement prepStatement = connection.prepareStatement(query);
            prepStatement.setString(1, pseudo);
            prepStatement.setString(2, regNumber);
            prepStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SET PERSON PSEUDO ERRORS: "
                    + e.getMessage());
        }
    }   

    private File getFile(String outputFileName,
            InputStream inputStream) {
        try {
            // Set up a handle to the output file
            File file = new File(outputFileName + ".pdf");
            FileOutputStream output = new FileOutputStream(file);

            // Read Blob and store in output file
            byte[] buffer = new byte[10 * 1024];
            while (inputStream.read(buffer) > 0) {
                output.write(buffer);
            }

            return file;
        } catch (IOException e) {
            System.out.println("GET FILE ERROR: "
                    + e.getMessage());
        }

        return null;
    }
    
}
