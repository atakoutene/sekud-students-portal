package models;

/**
 *
 * @author leopo
 */
public class ClassAbsenceAndPresenceDate {

    //Date date;
    String statue;
    String newDateFormat;

    public ClassAbsenceAndPresenceDate() {

    }

    public ClassAbsenceAndPresenceDate(String newDateFormat, String statue) {
        this.newDateFormat = new String(newDateFormat);
        this.statue = new String(statue);
    }

    public String getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }

    public String getNewDateFormat() {
        return newDateFormat;
    }

    public void setNewDateFormat(String newDateFormat) {
        this.newDateFormat = newDateFormat;
    }

}
