package models;

import java.io.File;

/**
 *
 * @author Roger NDJEUMOU
 */
public class LectureNote {
    private int id ;
    private String idCourse ;
    private String description ;
    private File note ;

    public LectureNote(int id, String idCourse, 
            String description, File note) {
        this.id = id;
        this.idCourse = idCourse;
        this.description = description;
        this.note = note;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public File getNote() {
        return note;
    }

    public void setNote(File note) {
        this.note = note;
    }
    
}
