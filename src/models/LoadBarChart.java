package models;

/**
 *
 * @author leopo
 */
import database.DatabaseConnectionManager;
import java.util.ArrayList;
import java.util.Date;
import javafx.scene.Cursor;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class LoadBarChart {

    DatabaseConnectionManager manager = new DatabaseConnectionManager();
    ArrayList<String> courseTitles;
    ArrayList<Double> attendance_Rate;
    ArrayList<Course> courses;
    ArrayList<Date> datePresent;
    ArrayList<Date> dateAbsent;
    ArrayList<Object> objects;

    ScrollPane centerScrollContainer;

    String idStudent;

    public LoadBarChart(ScrollPane centerContainer) {
        this.centerScrollContainer = centerContainer;
    }

    public LoadBarChart(String idStudent, ScrollPane container) {
        this.idStudent = idStudent;
        this.centerScrollContainer = container;
        objects = manager.retrieveCoursesAndAttendenceRateFromDatabase(idStudent);
        if (!objects.isEmpty()) {
            courses = (ArrayList<Course>) objects.get(0);
            attendance_Rate = (ArrayList<Double>) objects.get(1);

            courseTitles = new ArrayList<>();
            for (int i = 0; i < courses.size(); i++) {
                courseTitles.add(courses.get(i).getIdCourse());
            }
        }

    }

    public BarChart<String, Number> getAttendanceChart() {

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Courses");
        xAxis.setTickLabelRotation(90);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Attendance Rate");

        XYChart.Series series = new XYChart.Series();
        series.setName("Student Attendance");

        for (int i = 0; i < courses.size(); i++) {
            series.getData().add(new XYChart.Data(courseTitles.get(i), attendance_Rate.get(i)));
        }

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.getData().add(series);
        barChart.setPrefWidth(160);

        for (XYChart.Series<String, Number> serie1 : barChart.getData()) {
            for (XYChart.Data<String, Number> item : serie1.getData()) {
                item.getNode().setOnMousePressed((MouseEvent event) -> {
                    loadAttendance(idStudent, item.getXValue(), "");
                });
            }
        }

        barChart.setCursor(Cursor.HAND);
        return barChart;
    }

    public boolean isAttendanceAvailable() {
        return !attendance_Rate.isEmpty();
    }

    public void loadAttendance(String studentId, String courseId, String courseTitle) {
        LoadPresenceAbsenceDate presenceAbsence 
                = new LoadPresenceAbsenceDate(studentId, courseId);
        centerScrollContainer.setContent(presenceAbsence.getDatePresentAbsentForACourse(courseTitle));
    }
}
