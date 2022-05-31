package controllers;

import database.DatabaseConnectionManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import main.Main;
import models.Course;
import models.CourseSchedule;
import models.Lecturer;
import models.Login;
import models.Person;
import models.Semester;
import models.Student;
import models.Timetable;
import util.MyDate;

/**
 * FXML Controller class
 *
 * @author Roger NDJEUMOU
 */
public class HomeController implements Initializable {

    MyDate dateUtil = new MyDate();
    Login loginInfo;
    Person personalInfo;
    Student studentInfo;
    Timetable timetable;
    Semester currentSemeter ;
    
    ArrayList<CourseSchedule> schedules;
    ArrayList<Course> enrolledCourses ;
    
    DatabaseConnectionManager manager
            = new DatabaseConnectionManager();

    @FXML
    private VBox boxClassesToday;

    @FXML
    private Label dateTimeLabel;

    @FXML
    private Accordion myCoursesContainer;
    
    @FXML
    private ImageView userPhoto;

    @FXML
    private Label welcomeLabel;

    @FXML
    void openTimetable(ActionEvent event) {
        (new Main()).viewPDF(timetable.getTimetable());
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Get user login info from stage and 
        // use it to retrieve user personal info
        loginInfo = (Login) main.Main.getStage().getUserData();
        personalInfo = manager.getPersonInfo(loginInfo.getId());

        if (loginInfo.getType().equalsIgnoreCase("STUDENT")) {
            studentInfo = manager.getStudentInfo(loginInfo.getRegNumber());
            timetable = manager.getTimetable(studentInfo.getIdProgram(), "Spring", 2022);
            currentSemeter = manager.getCurrentSemester() ;

            setMyClassesToday();
            setMyCourses();
        }

        setWelcomeLabel();
        setDateTimeLabel();
        setProfilePicture();
    }
    
    private void setMyCourses() {
        ArrayList<Object> objects = 
                manager.getEnrolledCoursesAndLecturerInfo(studentInfo.getId(), currentSemeter.getId()) ;
        if ( ! objects.isEmpty() ){
            enrolledCourses = (ArrayList<Course>) objects.get(0) ;
            ArrayList<Lecturer> lecturers = (ArrayList<Lecturer>) objects.get(1) ;
            ArrayList<Person> lecturerPersonInfos = (ArrayList<Person>) objects.get(2) ;
            
            if ( ! enrolledCourses.isEmpty() ){
                for (int i=0; i < enrolledCourses.size(); i++){
                    Course course = enrolledCourses.get(i) ;
                    Person lecturer = lecturerPersonInfos.get(i) ;
                    
                    TitledPane titledPane = new TitledPane();
                    titledPane.setText(course.getTitle());
                    
                    
                    Label descriptionLabel = new Label(course.getDescription()) ;
                    descriptionLabel.setWrapText(true);
                    
                    Label lecturerLabel = new Label("Taught by: "
                            + lecturer.toString());
                    lecturerLabel.setWrapText(true);
                    
                    Label creditAndGradeLabel = new Label("Credit hours: "
                            + course.getCredit() + "\t Passing grade: "
                            + course.getPassingGrade());
                    creditAndGradeLabel.setWrapText(true);
                                        
                    HBox hBox = new HBox(15) ;
                    hBox.setAlignment(Pos.CENTER);
                    
                    Button btnAssessMark = new Button("Assessment marks") ;
                    Button btnAttendance = new Button("Attendance") ;
                    Button btnResources = new Button("Resources") ;
                    Button btnSyllabus = new Button("Syllabus") ;
                    
                    hBox.getChildren().addAll(btnAssessMark, btnAttendance,
                            btnResources, btnSyllabus) ;
                    
                    VBox vBox = new VBox(10) ;
                    vBox.getChildren().add(descriptionLabel) ;
                    vBox.getChildren().add(lecturerLabel) ;
                    vBox.getChildren().add(creditAndGradeLabel) ;
                    vBox.getChildren().add(hBox) ;
                    
                    titledPane.setContent(vBox);
                    myCoursesContainer.getPanes().add(titledPane) ;
                }
            }
        }
    }

    private void setMyClassesToday() {
        ArrayList<Object> objects
                = manager.getSchedule(studentInfo, currentSemeter.getId(),
                        dateUtil.getWeekDay());
        
        schedules = (ArrayList<CourseSchedule>) objects.get(0);
        ArrayList<Course> courses = (ArrayList<Course>) objects.get(1);

        if (!schedules.isEmpty()) {
            
            for (int i = 0; i < schedules.size(); i++) {
                CourseSchedule sched = schedules.get(i);
                String content = courses.get(i).getTitle() + " ("
                        + courses.get(i).getIdCourse() + ")";
                String periodAndHall = sched.getStartTime()
                        + " - " + sched.getEndTime() + " in "
                        + sched.getRoom();

                Label classToday = new Label(periodAndHall
                        + " \n  " + content);
                classToday.setWrapText(true);

                boxClassesToday.getChildren().add(classToday);
            }
        } else {
            Label noClassLabel = new Label("No class today.") ;
            boxClassesToday.getChildren().add(noClassLabel);
        }
        
        Button btnViewTimetable = new Button("My Timetable");
        btnViewTimetable.setAlignment(Pos.CENTER);
        btnViewTimetable.setOnAction((ActionEvent event) -> {
            openTimetable(event);
        });
        
        StackPane pane = new StackPane(btnViewTimetable) ;
        
        boxClassesToday.getChildren().add(pane) ;
    }

    private void setWelcomeLabel() {
        welcomeLabel.setText("Welcome \""
                + manager.getPseudo(loginInfo.getId()) + "\"");
    }

    private void setDateTimeLabel() {
        dateTimeLabel.setText(dateUtil.toString());
    }

    private void setProfilePicture() {
        userPhoto.setImage(personalInfo.getPhoto());
    }

}
