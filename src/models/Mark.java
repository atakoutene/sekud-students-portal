package models;

import java.sql.Date;

/**
 *
 * @author Roger NDJEUMOU
 */
public class Mark {
    private int id ;
    private String idCourse ;
    private String idStudent ;
    private double mark ;
    private Date date ;

    public Mark() {
    }

    public Mark(int id, String idCourse, 
            String idStudent, double mark, Date date) {
        this.id = id;
        this.idCourse = idCourse;
        this.idStudent = idStudent;
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
