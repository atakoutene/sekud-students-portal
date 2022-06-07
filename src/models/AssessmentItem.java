package models;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author NAOUSSI
 */
public class AssessmentItem {
    private final SimpleStringProperty title;
    private final SimpleStringProperty type;
    private final SimpleStringProperty course;
    private final SimpleStringProperty dueDate;

    public AssessmentItem(String title, String type, String course, String dueDate) {
        this.title = new SimpleStringProperty(title);
        this.type = new SimpleStringProperty(type);
        this.course = new SimpleStringProperty(course);
        this.dueDate = new SimpleStringProperty(dueDate);
    }

    public String getTitle() {
        return title.get();
    }
    
    public void setTitle(String title){
        this.title.set(title);
    }
    
    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }
    
    public String getCourse() {
        return course.get();
    }
    
    public void setCourse(String course) {
        this.course.set(course);
    }

    public String getDueDate() {
        return dueDate.get();
    }
    
    public void setDueDate(String dueDate) {
        this.dueDate.set(dueDate);
    }
  
}
