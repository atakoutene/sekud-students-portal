/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author leopo
 */
import controllers.HomeController;
import database.DatabaseConnectionManager;
import java.util.ArrayList;
import java.util.Date;
import javafx.scene.Cursor;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
    
    VBox centerContainer ;
    ///////////////////
    String idStudent;
    public LoadBarChart(String idStudent, VBox container) {
        this.idStudent=idStudent;
        this.centerContainer = container ;
        objects = manager.retrieveCoursesAndAttendenceRateFromDatabase(idStudent);
        if (!objects.isEmpty()) {
            courses = (ArrayList<Course>) objects.get(0);
            attendance_Rate = (ArrayList<Double>) objects.get(1);
            
            courseTitles = new ArrayList<>() ;
            for (int i = 0; i < courses.size(); i++) {
                courseTitles.add(courses.get(i).getIdCourse());
            }
        }
       // datePresent = manager.retrieveDatePresentForACourseFromDatabase(idStudent, idCourse);
       // dateAbsent = manager.retrieveDateAbsentForACourseFromDatabase(idStudent, idCourse);

    }

    public BarChart<String, Number> getAttendanceChart() {

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Courses");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Attendance Rate");

        XYChart.Series series = new XYChart.Series();
        series.setName("Student Attendance");

        for (int i = 0; i < courses.size(); i++) {
            series.getData().add(new XYChart.Data(courseTitles.get(i), attendance_Rate.get(i)));
        }

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.getData().add(series);
        
        for (XYChart.Series<String,Number> serie1: barChart.getData()){
            for (XYChart.Data<String, Number> item: serie1.getData()){
                item.getNode().setOnMousePressed((MouseEvent event) -> {
                    //System.out.println("you clicked "+item.toString()+serie1.toString());
                    LoadPresenceAbsenceDate presenceAbsence = new LoadPresenceAbsenceDate(idStudent,item.getXValue());
                    centerContainer.getChildren().remove(0);
                    centerContainer.getChildren().add(0, presenceAbsence.getDatePresentAbsentForACourse()) ;
                    //item.getXValue();
                });
            }
        }
        
        barChart.setCursor(Cursor.HAND);
        return barChart;

    }

    public boolean isAttendanceAvailable(){
        return ! attendance_Rate.isEmpty() ;
    }

}
