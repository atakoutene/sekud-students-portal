package controllers;

import database.DatabaseConnectionManager;
import handlers.AssessmentManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import main.Main;
import models.Book;
import models.Course;
import models.CourseSchedule;
import models.LectureNote;
import models.Login;
import models.Person;
import models.Semester;
import models.Student;
import models.StudentMentor;
import models.Timetable;
import models.UsefulLink;
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
    Semester currentSemeter;

    ArrayList<CourseSchedule> schedules;
    ArrayList<Course> enrolledCourses;

    DatabaseConnectionManager manager
            = new DatabaseConnectionManager();

    @FXML
    private VBox boxClassesToday;

    @FXML
    private Label dateTimeLabel;

    private TitledPane coursesTitledPane;

    @FXML
    private VBox helpContainer;

    @FXML
    private VBox mainContainer;

    @FXML
    private ScrollPane mainScrollPane;

    private TitledPane resourcesTitledPane;
    private TitledPane assessmentTitledPane;

    @FXML
    private ImageView userPhoto;

    @FXML
    private Button btnHome;

    @FXML
    private Label welcomeLabel;

    @FXML
    void openTimetable(ActionEvent event) {
        (new Main()).viewPDF(timetable.getTimetable());
    }

    @FXML
    void loadHomeContainers(ActionEvent event) {
        markMenuButtonAsActive(btnHome);
        goToHome();
    }

    @FXML
    void signOutHandler(ActionEvent event) throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add((new Main()).appLogo);
        alert.setTitle("Sign Out");
        alert.setHeaderText("Do you really want to log out?");
        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yesButton, cancelButton);
        alert.showAndWait()
                .filter(response -> response.getButtonData() == ButtonBar.ButtonData.YES)
                .ifPresent((response) -> {
                    try {
                        main.Main.loadLoginView(event);
                    } catch (Exception ex) {
                        System.out.println("SIGN OUT HANDLER ERROR: "
                                + ex.getMessage());
                    }
                });

    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        markMenuButtonAsActive(btnHome);
        // Get user login info from stage and 
        // use it to retrieve user personal info
        loginInfo = (Login) main.Main.getStage().getUserData();
        personalInfo = manager.getPersonInfo(loginInfo.getId());

        if (loginInfo.getType().equalsIgnoreCase("STUDENT")) {
            studentInfo = manager.getStudentInfo(loginInfo.getRegNumber());
            timetable = manager.getTimetable(studentInfo.getIdProgram(), "Spring", 2022);
            currentSemeter = manager.getCurrentSemester();

            setMyClassesToday();
            setMyCourses();
            setMyHelp();
        }

        setWelcomeLabel();
        setDateTimeLabel();
        setProfilePicture();
    }

    private void goToAssessments(Course course, String idStudent) {
        assessmentTitledPane = new TitledPane();
        assessmentTitledPane.setCollapsible(false);
        assessmentTitledPane.setText("Assessment Marks - "
                + course.getTitle());

        AssessmentManager assessment
                = new AssessmentManager(idStudent, course.getIdCourse());
        if (assessment.isAnyMarkAvailable()) {
            Accordion accordion = new Accordion();

            TitledPane overallMarksTitledPane = assessment.getOverallMarksPane();
            accordion.getPanes().add(overallMarksTitledPane);
            accordion.setExpandedPane(overallMarksTitledPane);

            if (assessment.areMarksAvailable(AssessmentManager.ASSIGNMENT)) {
                accordion.getPanes().add(assessment.getMarksPane(AssessmentManager.ASSIGNMENT));
            }

            if (assessment.areMarksAvailable(AssessmentManager.QUIZ)) {
                accordion.getPanes().add(assessment.getMarksPane(AssessmentManager.QUIZ));
            }

            if (assessment.areMarksAvailable(AssessmentManager.TEST)) {
                accordion.getPanes().add(assessment.getMarksPane(AssessmentManager.TEST));
            }

            if (assessment.areMarksAvailable(AssessmentManager.PROJECT)) {
                accordion.getPanes().add(assessment.getMarksPane(AssessmentManager.PROJECT));
            }

            assessmentTitledPane.setContent(accordion);
        } else {
            assessmentTitledPane.setContent(
                    new Label("No marks available for this course"));
        }

        if (mainContainer.getChildren().contains(coursesTitledPane)) {
            mainContainer.getChildren().remove(coursesTitledPane);
        }

        mainContainer.getChildren().add(0, assessmentTitledPane);
        scrollToTop();
    }

    private void goToHome() {
        if (mainContainer.getChildren().contains(resourcesTitledPane)) {
            mainContainer.getChildren().remove(resourcesTitledPane);
        }

        if (mainContainer.getChildren().contains(assessmentTitledPane)) {
            mainContainer.getChildren().remove(assessmentTitledPane);
        }

        if (!mainContainer.getChildren().contains(coursesTitledPane)) {
            mainContainer.getChildren().add(0, coursesTitledPane);
        }
    }

    private void goToResources(Course course) {

        resourcesTitledPane = new TitledPane();
        resourcesTitledPane.setCollapsible(false);
        resourcesTitledPane.setText("Resources - "
                + course.getTitle());

        ArrayList<LectureNote> notes = manager.getCourseLectureNotes(course.getIdCourse());
        ArrayList<Book> books = manager.getCourseBooks(course.getIdCourse());
        ArrayList<UsefulLink> usefulLinks = manager.getCourseUsefulLinks(course.getIdCourse());

        if (notes.isEmpty() && books.isEmpty()
                && usefulLinks.isEmpty()) {
            resourcesTitledPane.setContent(
                    new Label("No resource available for this course.")
            );
        } else {
            Accordion accordion = new Accordion();

            if (!notes.isEmpty()) {
                TitledPane notesTitledPane = new TitledPane();
                notesTitledPane.setText("Lecture notes");

                VBox notesContainer = new VBox(5);
                for (LectureNote note : notes) {
                    Label description = new Label(note.getDescription());
                    description.setWrapText(true);
                    description.setAlignment(Pos.CENTER_LEFT);
                    description.setTextAlignment(TextAlignment.LEFT);

                    Button btnViewPDF = new Button("View");
                    btnViewPDF.setOnAction(event -> {
                        (new Main()).viewPDF(note.getNote());
                    });

                    BorderPane borderPane = new BorderPane();
                    borderPane.setCenter(description);
                    borderPane.setRight(new StackPane(btnViewPDF));

                    notesContainer.getChildren().add(borderPane);
                    notesContainer.getChildren().add(new Separator());
                }

                notesTitledPane.setContent(notesContainer);
                accordion.getPanes().add(notesTitledPane);
                accordion.setExpandedPane(notesTitledPane);
            }

            if (!books.isEmpty()) {
                TitledPane booksTitledPane = new TitledPane();
                booksTitledPane.setText("Books");

                VBox booksContainer = new VBox(5);

                for (Book book : books) {
                    Label title = new Label(book.getTitle());
                    title.setWrapText(true);

                    Label reference = new Label(book.getRefereces());
                    reference.setTextAlignment(TextAlignment.JUSTIFY);
                    reference.setWrapText(true);

                    String avail = book.getAvailability().equals("PDF")
                            ? "Available in PDF format."
                            : "Available online.";

                    Label availability = new Label(avail);

                    Button btnGetBook = new Button("Get it");
                    btnGetBook.setTooltip(new Tooltip("Click to download \n"
                            + "the book from the web."));
                    btnGetBook.setOnAction(event -> {
                        (new Main()).openBrowser(book.getLink());
                    });

                    BorderPane borderPane = new BorderPane();
                    borderPane.setLeft(availability);
                    borderPane.setRight(btnGetBook);

                    VBox box = new VBox(5);
                    box.getChildren().addAll(title, reference, borderPane, new Separator());

                    booksContainer.getChildren().add(box);
                }

                booksTitledPane.setContent(booksContainer);
                accordion.getPanes().add(booksTitledPane);
            }

            if (!usefulLinks.isEmpty()) {
                TitledPane linksTitledPane = new TitledPane();
                linksTitledPane.setText("Useful links");

                VBox linksContainer = new VBox(5);
                for (UsefulLink usefulLink : usefulLinks) {
                    Label description = new Label(usefulLink.getDescription());
                    description.setWrapText(true);

                    Button btnFollowLink = new Button("Follow");
                    btnFollowLink.setTooltip(
                            new Tooltip("Click to open the link in your browser"));
                    btnFollowLink.setOnAction(event -> {
                        (new Main()).openBrowser(usefulLink.getLink());
                    });

                    BorderPane borderPane = new BorderPane();
                    borderPane.setCenter(description);
                    borderPane.setRight(new StackPane(btnFollowLink));

                    linksContainer.getChildren().addAll(borderPane, new Separator());
                }
                linksTitledPane.setContent(linksContainer);
                accordion.getPanes().add(linksTitledPane);
            }

            resourcesTitledPane.setContent(accordion);
        }

        if (mainContainer.getChildren().contains(coursesTitledPane)) {
            mainContainer.getChildren().remove(coursesTitledPane);
        }

        mainContainer.getChildren().add(0, resourcesTitledPane);
        scrollToTop();

    }

    private void setMyCourses() {
        coursesTitledPane = new TitledPane();
        coursesTitledPane.setText("My Courses");
        coursesTitledPane.setCollapsible(false);

        ArrayList<Object> objects
                = manager.getEnrolledCoursesAndLecturerInfo(studentInfo.getId(), currentSemeter.getId());
        if (!objects.isEmpty()) {
            Accordion coursesAccordion = new Accordion();

            enrolledCourses = (ArrayList<Course>) objects.get(0);
            ArrayList<Person> lecturerPersonInfos = (ArrayList<Person>) objects.get(1);

            if (!enrolledCourses.isEmpty()) {
                for (int i = 0; i < enrolledCourses.size(); i++) {
                    Course course = enrolledCourses.get(i);
                    Person lecturer = lecturerPersonInfos.get(i);

                    TitledPane titledPane = new TitledPane();
                    titledPane.setText(course.getTitle() + " ("
                            + course.getIdCourse() + ")");

                    Label descriptionLabel = new Label(course.getDescription());
                    descriptionLabel.setWrapText(true);
                    descriptionLabel.setTextAlignment(TextAlignment.JUSTIFY);

                    Label lecturerLabel = new Label("Lecturer: "
                            + lecturer.toString());
                    lecturerLabel.setWrapText(true);

                    Label creditAndGradeLabel = new Label("Credit hours: "
                            + course.getCredit() + "\t Passing grade: "
                            + course.getPassingGrade());
                    creditAndGradeLabel.setWrapText(true);

                    HBox hBox = new HBox(15);
                    hBox.setAlignment(Pos.CENTER);

                    Button btnAssessMark = new Button("Assessment marks");
                    btnAssessMark.setTooltip(new Tooltip("View detailed assessment marks."));
                    btnAssessMark.setOnAction(event -> {
                        goToAssessments(course, studentInfo.getId());
                    });
                    Button btnAttendance = new Button("Attendance");
                    btnAttendance.setTooltip(new Tooltip("View attendance details."));
                    Button btnResources = new Button("Resources");
                    btnResources.setTooltip(new Tooltip("Get available course documentatiion."));
                    btnResources.setOnAction(event -> {
                        goToResources(course);
                    });
                    Button btnSyllabus = new Button("Syllabus");
                    btnSyllabus.setTooltip(new Tooltip("View syllabus for "
                            + course.getIdCourse()));
                    btnSyllabus.setOnAction(event -> {
                        (new Main()).viewPDF(course.getSyllabus());
                    });

                    hBox.getChildren().addAll(btnAssessMark, btnAttendance,
                            btnResources, btnSyllabus);

                    VBox vBox = new VBox(10);
                    vBox.getChildren().add(descriptionLabel);
                    vBox.getChildren().add(lecturerLabel);
                    vBox.getChildren().add(creditAndGradeLabel);
                    vBox.getChildren().add(hBox);

                    titledPane.setContent(vBox);
                    coursesAccordion.getPanes().add(titledPane);
                }
            }
            coursesTitledPane.setContent(coursesAccordion);
        }
        mainContainer.getChildren().add(0, coursesTitledPane);
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
                String content = courses.get(i).getTitle();
                String periodAndHall = sched.getStartTime()
                        + " - " + sched.getEndTime() + " in "
                        + sched.getRoom();

                Label classToday = new Label(periodAndHall
                        + " \n  " + content);
                classToday.setWrapText(true);

                boxClassesToday.getChildren().add(classToday);
            }
        } else {
            Label noClassLabel = new Label("No class today.");
            boxClassesToday.getChildren().add(noClassLabel);
        }

        Button btnViewTimetable = new Button("My Timetable");
        btnViewTimetable.setAlignment(Pos.CENTER);
        btnViewTimetable.setOnAction((ActionEvent event) -> {
            openTimetable(event);
        });

        StackPane pane = new StackPane(btnViewTimetable);

        boxClassesToday.getChildren().add(pane);
    }

    private void setMyHelp() {
        Label label
                = new Label("Contact any of your student mentors:");
        label.setWrapText(true);
        helpContainer.getChildren().add(label);

        ArrayList<Object> objects
                = manager.getMentorInfo(studentInfo.getId(),
                        currentSemeter.getId(),
                        studentInfo.getIdProgram());

        if (!objects.isEmpty()) {
            ArrayList<StudentMentor> mentors
                    = (ArrayList<StudentMentor>) objects.get(0);
            ArrayList<Person> persons
                    = (ArrayList<Person>) objects.get(1);

            for (int i = 0; i < mentors.size(); i++) {
                StudentMentor mentor = mentors.get(i);
                Person person = persons.get(i);

                Label lblMentor = new Label(person.getLastName()
                        + " " + person.getFirstName() + "\n"
                        + person.getEmail() + "\n"
                        + person.getPhoneNumber() + "\n"
                        + mentor.getAvailability());
                lblMentor.setWrapText(true);

                helpContainer.getChildren().addAll(lblMentor, new Separator());
            }
        }
    }

    private void markMenuButtonAsActive(Button menuButton) {
        menuButton.setStyle("-fx-font: normal normal 20px 'Inter Black' ;"
                + "-fx-background-color: rgba(0,0,0,0);"
                + "-fx-text-fill: #D1AC6D ;"
                + "-fx-underline: true ;");
        if (menuButton.getScene() != null) {
            Stage window = (Stage) menuButton.getScene().getWindow();
            window.setTitle(Main.APP_NAME + " - " + menuButton.getText());
        }
    }

    private void disactivateOtherMenuButtons(Button exceptMe) {
        if (exceptMe.getId().equals(btnHome.getId())) {
            // set the style of the othe buttons to normal.
        }
    }

    private void setWelcomeLabel() {
        welcomeLabel.setText("Welcome "
                + manager.getPseudo(loginInfo.getId())
        );
    }

    private void setDateTimeLabel() {
        dateTimeLabel.setText(dateUtil.toString());
    }

    private void setProfilePicture() {
        userPhoto.setImage(personalInfo.getPhoto());
    }

    private void scrollToTop() {
        mainScrollPane.setVvalue(mainScrollPane.getVmin());
    }

}
