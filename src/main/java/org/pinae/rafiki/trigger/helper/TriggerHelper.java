package org.pinae.rafiki.trigger.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.pinae.rafiki.trigger.Trigger;

/**
 * Trigger Tools
 *
 * @author Huiyugeng
 */
public class TriggerHelper {

    /**
     * Get the trigger time of the trigger within the specified time
     *
     * @param startTime Start time
     * @param endTime   End time
     * @param trigger   Trigger
     * @return the Trigger time point
     */
    public static List<Date> getTriggerCalendar(Date startTime, Date endTime, Trigger trigger) {
        List<Date> calendarList = new ArrayList<Date>();
        if (endTime.getTime() > startTime.getTime()) {
            long time = startTime.getTime();
            while (true) {
                time = time + 1000;
                if (time <= endTime.getTime()) {
                    Date date = new Date(time);
                    if (trigger.match(date)) {
                        calendarList.add(date);
                    }
                } else {
                    break;
                }
            }
        }
        return calendarList;
    }
}
