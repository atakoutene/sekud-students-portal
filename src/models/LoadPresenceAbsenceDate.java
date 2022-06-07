/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import database.DatabaseConnectionManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author leopo
 */
public class LoadPresenceAbsenceDate {

    DatabaseConnectionManager manager = new DatabaseConnectionManager();
    ArrayList<Date> datePresent;
    ArrayList<Date> dateAbsent;
    String courseName = "";

    public LoadPresenceAbsenceDate(String idStudent, String idCourse) {
        ArrayList<Object> objects1 = manager.retrieveDatePresentForACourseFromDatabase(idStudent, idCourse);
        ArrayList<Object> objects2 = manager.retrieveDateAbsentForACourseFromDatabase(idStudent, idCourse);

        if (!objects1.isEmpty() && !objects2.isEmpty()) {
            datePresent = (ArrayList<Date>) objects1.get(0);
            dateAbsent = (ArrayList<Date>) objects2.get(0);
            courseName = (String) objects1.get(1);
        }
    }

    public TitledPane getDatePresentAbsentForACourse() {

//        VBox pane = new VBox();
//        Label label = new Label("You were present on:\n");
//        pane.getChildren().add(label);
//        for(Date date: datePresent){
//            label = new Label(date.toString()+"\n");
//            pane.getChildren().add(label);
//        }
//        label = new Label("\n\nYou were absent on:\n");
//        pane.getChildren().add(label);
//        for(Date date: dateAbsent){
//            label = new Label(date.toString()+"\n");
//            pane.getChildren().add(label);
//        }
        //RadioButton radio = new RadioButton();
        TitledPane pane = new TitledPane();
        pane.setCollapsible(false);
        pane.setText("Attendance Rate - " + courseName);
        TableView<ClassAbsenceAndPresenceDate> tableView = new TableView<>();
        //TableColumn<ClassAbsenceAndPresenceDate, Date> dateColumn = new TableColumn<>("Dates");
        TableColumn<ClassAbsenceAndPresenceDate, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("newDateFormat"));

        TableColumn<ClassAbsenceAndPresenceDate, String> statues = new TableColumn<>("Status");
        statues.setCellValueFactory(new PropertyValueFactory<>("statue"));
        ObservableList<ClassAbsenceAndPresenceDate> categories = FXCollections.observableArrayList();

        ClassAbsenceAndPresenceDate category = new ClassAbsenceAndPresenceDate();
        Calendar cal = Calendar.getInstance();
        for (Date date : datePresent) {

            cal.setTime(date);
            LocalDate newDate = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
            String newDateFormat = new String(cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH) + " " + getEquivalentDayOfWeek(cal.get(Calendar.DAY_OF_WEEK)));
            //category= new ClassAbsenceAndPresenceDate(date, "Present");
            category = new ClassAbsenceAndPresenceDate(newDateFormat, "Present");                 ////////////////////////////
            categories.add(category);
        }
        for (Date date : dateAbsent) {
            cal.setTime(date);
            LocalDate newDate = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
            String newDateFormat = new String(cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH) + " " + getEquivalentDayOfWeek(cal.get(Calendar.DAY_OF_WEEK)));
            category = new ClassAbsenceAndPresenceDate(newDateFormat, "Absent");
            categories.add(category);
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////// For sorting the objects using comparator interface.
        ClassAbsenceAndPResenceDateComparator comparator = new ClassAbsenceAndPResenceDateComparator();
        categories.sort(comparator.reversed());  /// the .reversed() is for ascending order.
        //categories.sorted();//////////////////////////////////////////////////////////////////////////////////////       <-------------
        tableView.setItems(categories);
        tableView.setFixedCellSize(35);
        tableView.getColumns().addAll(dateColumn, statues);
        tableHeightHelper(tableView, (int) tableView.getFixedCellSize());
        pane.setContent(tableView);   ///////////////////////////////////////////////////
        return pane;
    }

//    public String getEquivalentMonth(int month){
//        String monthName= "";
//        switch(month){
//            case 1: monthName = "January"; break;
//            case 2: monthName = "February"; break;
//            case 3: monthName = "March"; break;
//            case 4: monthName = "April"; break;
//            case 5: monthName = "May"; break;
//            case 6: monthName = "June"; break;
//            case 7: monthName = "Jully"; break;
//            case 8: monthName = "August"; break;
//            case 9: monthName = "September"; break;
//            case 10: monthName = "October"; break;
//            case 11: monthName = "November"; break;
//            case 12: monthName = "December"; break;
//         
//        }
//        return monthName;
//    }
    public String getEquivalentDayOfWeek(int dayOfWeek) {
        String dayOfWeekName = "";
        switch (dayOfWeek) {
            case 2 -> dayOfWeekName = "Monday";
            case 3 -> dayOfWeekName = "Tuesday";
            case 4 -> dayOfWeekName = "Wednesday";
            case 5 -> dayOfWeekName = "Thursday";
            case 6 -> dayOfWeekName = "Friday";
            case 7 -> dayOfWeekName = "Saturday";
            case 8 -> dayOfWeekName = "Sunday";

        }
        return dayOfWeekName;
    }

    public void tableHeightHelper(TableView<?> table, int rowHeight) {
        table.prefHeightProperty().bind(Bindings.max(1, Bindings.size(table.getItems()))
                .multiply(rowHeight)
                .add(rowHeight)
        );
        table.minHeightProperty().bind(table.prefHeightProperty());
        table.maxHeightProperty().bind(table.prefHeightProperty());
    }

}
