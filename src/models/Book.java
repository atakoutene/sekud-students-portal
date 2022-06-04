package models;

/**
 *
 * @author Roger NDJEUMOU
 */
public class Book {
    private int id ;
    private String idCourse ;
    private String title ;
    private String refereces ;
    private String availability ;
    private String link ;

    public Book() {
    }

    public Book(int id, String idCourse, String title, 
            String refereces, String availability, String link) {
        this.id = id;
        this.idCourse = idCourse;
        this.title = title;
        this.refereces = refereces;
        this.availability = availability;
        this.link = link;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRefereces() {
        return refereces;
    }

    public void setRefereces(String refereces) {
        this.refereces = refereces;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
    
}
