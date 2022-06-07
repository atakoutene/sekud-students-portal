/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.util.Comparator;

/**
 *
 * @author leopo
 */
public class ClassAbsenceAndPResenceDateComparator implements Comparator<ClassAbsenceAndPresenceDate>{

    @Override
    public int compare(ClassAbsenceAndPresenceDate o1, ClassAbsenceAndPresenceDate o2) {
        return o1.getNewDateFormat()
               .compareTo(o2.getNewDateFormat());
    }
    
    
}
