/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.time.LocalDate;
import java.util.Date;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author leopo
 */
public class ClassAbsenceAndPresenceDate {
    //Date date;
    String statue;
    String newDateFormat;
    public ClassAbsenceAndPresenceDate(){
        
    }

    public ClassAbsenceAndPresenceDate(String newDateFormat, String statue){
        this.newDateFormat = new String(newDateFormat);
        this.statue = new String(statue);
    }    
    
//    public ClassAbsenceAndPresenceDate(Date date, String statue) {
//        this.date = date;
//        this.statue = new SimpleStringProperty(statue);
//    }
//
//    public Date getDate() {
//        return date;
//    }
//
//    public void setDate(Date date) {
//        this.date = date;
//    }
//
//    public String getStatue() {
//        return statue.get();
//    }
//
//    public void setStatue(String statue) {
//        this.statue = new SimpleStringProperty(statue);
//    }
//    

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
