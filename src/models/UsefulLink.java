package models;

/**
 *
 * @author Roger NDJEUMOU
 */
public class UsefulLink {
    private int id ;
    private String idCourse ;
    private String description ;
    private String link ;

    public UsefulLink() {
    }

    public UsefulLink(int id, String idCourse, String description,
            String link) {
        this.id = id;
        this.idCourse = idCourse;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
    
}
