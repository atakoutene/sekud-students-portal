package models;

import java.sql.Date;

/**
 *
 * @author Roger NDJEUMOU
 */
public class ProjectMark {
    private int id ;
    private String idCourse ;
    private String idStudent ;
    private String title ;
    private double mark ;
    private Date date ;

    public ProjectMark() {
    }

    public ProjectMark(int id, String idCourse, String idStudent,
            String title, double mark, Date date) {
        this.id = id;
        this.idCourse = idCourse;
        this.idStudent = idStudent;
        this.title = title;
        this.mark = mark;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdCourse() {
        return idCourse;
    }

    public void setIdCourse(String idCourse) {
        this.idCourse = idCourse;
    }

    public String getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(String idStudent) {
        this.idStudent = idStudent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getMark() {
        return mark;
    }

    public void setMark(double mark) {
        this.mark = mark;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
}
