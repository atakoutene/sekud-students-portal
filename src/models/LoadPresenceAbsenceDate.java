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

    public TitledPane getDatePresentAbsentForACourse(String courseTitle) {

        TitledPane pane = new TitledPane();
        pane.setCollapsible(false);
        pane.setText("Attendance Rate - " + (courseTitle.equals("") ? courseName : courseTitle));
        TableView<ClassAbsenceAndPresenceDate> tableView = new TableView<>();
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

        ClassAbsenceAndPResenceDateComparator comparator = new ClassAbsenceAndPResenceDateComparator();
        categories.sort(comparator.reversed());  /// the .reversed() is for ascending order.

        tableView.setItems(categories);
        tableView.setFixedCellSize(35);
        tableView.getColumns().addAll(dateColumn, statues);
        tableHeightHelper(tableView, (int) tableView.getFixedCellSize());
        pane.setContent(tableView);

        return pane;
    }

    public String getEquivalentDayOfWeek(int dayOfWeek) {
        String dayOfWeekName = "";
        switch (dayOfWeek) {
            case 2 ->
                dayOfWeekName = "Monday";
            case 3 ->
                dayOfWeekName = "Tuesday";
            case 4 ->
                dayOfWeekName = "Wednesday";
            case 5 ->
                dayOfWeekName = "Thursday";
            case 6 ->
                dayOfWeekName = "Friday";
            case 7 ->
                dayOfWeekName = "Saturday";
            case 8 ->
                dayOfWeekName = "Sunday";

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

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

}
