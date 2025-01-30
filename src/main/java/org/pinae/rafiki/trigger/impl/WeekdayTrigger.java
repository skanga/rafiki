package org.pinae.rafiki.trigger.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Weekday trigger
 *
 * @author Huiyugeng
 */
public class WeekdayTrigger extends EverydayTrigger {
    private TimeZone zone = TimeZone.getDefault();

    private final List<Integer> weekdayList = new ArrayList<>();

    @Override
    public boolean match(Date now) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(now.getTime());
        calendar.setTimeZone(this.zone);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (this.weekdayList.contains(dayOfWeek)) {
            return super.match(now);
        } else {
            return false;
        }
    }

    /**
     * <p>Set the trigger time zone</p>
     *
     * <p>
     * For example "GMT-8"
     * If the time zone is set to null, TimeZone.getDefault() will be used
     * </p>
     *
     * @param zone Time Zone
     */
    public void setTimeZone(String zone) {
        this.zone = TimeZone.getTimeZone(zone);
    }

    /**
     * <p>Set the trigger week X</P>
     *
     * <p>Value range: 1-7, where 1 is Sunday and 7 is Saturday</p>
     *
     * @param weekday Trigger time
     */
    public void setWeekday(int weekday) {
        this.weekdayList.add(weekday);
    }

    /**
     * <p>Set the trigger week X</P>
     *
     * <p>Value range: SUN, MON, TUE, WED, THU, FRI, SAT</p>
     *
     * @param weekday Trigger time
     */
    public void setWeekday(String weekday) {
        String[] dayOfWeeks = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        for (int i = 0; i < 7; i++) {
            String dayOfWeek = dayOfWeeks[i];
            if (dayOfWeek.equalsIgnoreCase(weekday)) {
                this.weekdayList.add(i + 1);
                break;
            }
        }
    }
}
