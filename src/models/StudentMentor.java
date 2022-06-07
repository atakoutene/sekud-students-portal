package models;

/**
 *
 * @author Roger NDJEUMOU
 */
public class StudentMentor {
    private String idStudent ;
    private int idProgram ;
    private int idSemester ;
    private String availability ;

    public StudentMentor() {
    }

    public StudentMentor(String idStudent, String availability) {
        this.idStudent = idStudent;
        this.availability = availability;
    }
    

    public StudentMentor(String idStudent, int idProgram, 
            int idSemester, String availability) {
        this.idStudent = idStudent;
        this.idProgram = idProgram;
        this.idSemester = idSemester;
        this.availability = availability;
    }

    public String getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(String idStudent) {
        this.idStudent = idStudent;
    }

    public int getIdProgram() {
        return idProgram;
    }

    public void setIdProgram(int idProgram) {
        this.idProgram = idProgram;
    }

    public int getIdSemester() {
        return idSemester;
    }

    public void setIdSemester(int idSemester) {
        this.idSemester = idSemester;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
    
}
