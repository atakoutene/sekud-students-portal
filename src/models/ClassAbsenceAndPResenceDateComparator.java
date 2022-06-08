package models;

import java.util.Comparator;

/**
 *
 * @author leopo
 */
public class ClassAbsenceAndPResenceDateComparator implements Comparator<ClassAbsenceAndPresenceDate> {

    @Override
    public int compare(ClassAbsenceAndPresenceDate o1, ClassAbsenceAndPresenceDate o2) {
        return o1.getNewDateFormat()
                .compareTo(o2.getNewDateFormat());
    }

}
