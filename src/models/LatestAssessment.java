package models;

import java.sql.Date;
import util.MyDate;

/**
 *
 * @author NAOUSSI
 */
public class LatestAssessment {

    private int idAssessment;
    private String assessmentType;
    private String title;
    private String idCourse;
    private Date dueDate;
    private String courseTitle;

    public LatestAssessment() {
    }

    public LatestAssessment(String assessmentType, String title,
            String idCourse, Date dueDate, String courseTitle) {
        this.assessmentType = assessmentType;
        this.title = title;
        this.idCourse = idCourse;
        this.dueDate = dueDate;
        this.courseTitle = courseTitle;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public int getIdAssessment() {
        return idAssessment;
    }

    public void setIdAssessment(int idAssessment) {
        this.idAssessment = idAssessment;
    }

    public String getAssesssmentType() {
        return assessmentType;
    }

    public void setAssesssmentType(String assesssmentType) {
        this.assessmentType = assesssmentType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIdCourse() {
        return idCourse;
    }

    public void setIdCourse(String idCourse) {
        this.idCourse = idCourse;
    }

    public String getDueDate() {
        return (new MyDate(dueDate.getTime())).getStringDate();
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }
}
