package ph.edu.uscDCISMCatcha.utils;

import ph.edu.uscDCISMCatcha.data.models.EventModel;

public class DateUtils {

    public static boolean isOverlapping(EventModel e1, EventModel e2) {
        if (e1.getStartDateTime() == null || e1.getEndDateTime() == null ||
                e2.getStartDateTime() == null || e2.getEndDateTime() == null) return false;

        return e1.getStartDateTime().compareTo(e2.getEndDateTime()) < 0 &&
                e2.getStartDateTime().compareTo(e1.getEndDateTime()) < 0;
    }
}
