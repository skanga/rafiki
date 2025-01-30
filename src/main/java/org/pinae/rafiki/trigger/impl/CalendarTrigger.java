package org.pinae.rafiki.trigger.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.pinae.rafiki.trigger.AbstractTrigger;

/**
 * Calendar Triggers
 *
 * @author Huiyugeng
 */
public class CalendarTrigger extends AbstractTrigger {
    private static final Logger logger = LogManager.getLogger(CalendarTrigger.class);

    private final List<Date[]> timeList = new ArrayList<>();

    private final String[] timeFormat = {"\\d+/\\d+/\\d+\\s+\\d+:\\d+:\\d+", "\\d+-\\d+-\\d+\\s+\\d+:\\d+:\\d+"};
    private final String[] parseFormat = {"yyyy/MM/dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss"};

    @Override
    public boolean match(Date now) {

        if (super.isFinish()) {
            return false;
        }

        for (Date[] time : this.timeList) {
            if (time != null && time.length == 2) {

                Date startDate = time[0];
                Date endDate = time[1];

                if (now.equals(startDate) || now.equals(endDate)) {
                    super.incExecuteCount();
                    return true;
                }

                if (endDate == null && now.after(startDate)) {
                    super.incExecuteCount();
                    return true;
                }

                if (now.after(startDate) && now.before(endDate)) {
                    super.incExecuteCount();
                    return true;
                }

            }
        }
        return false;
    }

    /**
     *<p>Set the trigger time, support adding multiple trigger times</p>
     *
     * <p>
     * The calendar format is 'startTime - endTime'
     * The time format is 'yyyy/mm/dd HH:MM:SS' or 'yyyy-mm-dd HH:MM:SS'
     * Eg::  '2015/02/12 12:00:00 - 2015/02/13 12:00:00'
     * </p>
     *
     * @param time the Trigger time, use 'startTime - endTime' format
     */
    public void setTime(String time) {
        String startTime = "";
        String endTime = "";

        for (int i = 0; i < this.timeFormat.length; i++) {
            String absoluteTimeFormat = "(" + this.timeFormat[i] + ")\\s*-\\s*(" + this.timeFormat[i] + ")";

            if (time.matches(absoluteTimeFormat)) {
                Pattern pattern = Pattern.compile(absoluteTimeFormat);
                Matcher matcher = pattern.matcher(time);

                if (matcher.find() && matcher.groupCount() == 2) {
                    startTime = matcher.group(1);
                    endTime = matcher.group(2);
                }
            }
        }

        try {
            for (int i = 0; i < this.timeFormat.length; i++) {
                if (startTime.matches(this.timeFormat[i]) && endTime.matches(this.timeFormat[i])) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(this.parseFormat[i]);
                    Date startDate = dateFormat.parse(startTime);
                    Date endDate = dateFormat.parse(endTime);

                    this.timeList.add(new Date[]{startDate, endDate});
                }
            }
        } catch (ParseException e) {
            logger.warn(String.format("Calendar Parse Error: time=%s, exception=%s", time, e.getMessage()));
        }
    }

    /**
     * <p>Set the trigger time, support adding multiple trigger times</p>
     *
     * <p>
     * If the end time is null, the task will not stop
     * </p>
     *
     * @param startTime Start Time
     * @param endTime   End Time
     */
    public void setTime(Date startTime, Date endTime) {
        startTime = startTime == null ? new Date() : startTime;
        endTime = endTime == null ? new Date() : endTime;

        this.timeList.add(new Date[]{startTime, endTime});
    }

    /**
     * <p>Set the trigger start time, support adding multiple trigger times</p>
     *
     * @param startTime Task start time
     */
    public void setTime(Date startTime) {
        startTime = startTime == null ? new Date() : startTime;

        this.timeList.add(new Date[]{startTime, null});
    }
}
