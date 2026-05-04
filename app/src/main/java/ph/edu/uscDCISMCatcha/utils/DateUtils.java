package ph.edu.uscDCISMCatcha.utils;

import java.util.Date;
import ph.edu.uscDCISMCatcha.data.models.EventModel;

public class DateUtils {
    
    public static boolean isOverlapping(EventModel e1, EventModel e2) {
        if (e1.getStartDateTime() == null || e1.getEndDateTime() == null || 
            e2.getStartDateTime() == null || e2.getEndDateTime() == null) return false;
            
        return e1.getStartDateTime().before(e2.getEndDateTime()) &&
               e2.getStartDateTime().before(e1.getEndDateTime());
    }
}
