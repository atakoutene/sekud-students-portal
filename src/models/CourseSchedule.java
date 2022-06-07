package models;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Roger NDJEUMOU
 */
public class CourseSchedule {

    int idSchedule;
    String idCourse;
    String day;
    Time startTime;
    Time endTime;
    String room;

    public CourseSchedule(int idSchedule, String idCourse,
            String day, Time startTime, Time endTime, String room) {
        this.idSchedule = idSchedule;
        this.idCourse = idCourse;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
    }

    public int getIdSchedule() {
        return idSchedule;
    }

    public void setIdSchedule(int idSchedule) {
        this.idSchedule = idSchedule;
    }

    public String getIdCourse() {
        return idCourse;
    }

    public void setIdCourse(String idCourse) {
        this.idCourse = idCourse;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStartTime() {
        return getFormattedTime(startTime);
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return getFormattedTime(endTime);
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    private String getFormattedTime(Time time) {
        Date myDate = new Date(time.getTime());
        DateFormat df = new SimpleDateFormat("hh:mm a");
        return df.format(myDate);
    }

}
