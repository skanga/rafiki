package org.pinae.rafiki.trigger.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import org.pinae.rafiki.trigger.AbstractTrigger;
import org.pinae.rafiki.trigger.TriggerException;

/**
 * Cron Format Trigger
 *
 * @author Huiyugeng
 */
public class CronTrigger extends AbstractTrigger {
    private String cron;

    private CronParser cronParser;
    private TimeZone zone = TimeZone.getDefault();

    /**
     * Constructor
     */
    public CronTrigger() {
        super.setRepeat(true);
        super.setRepeatCount(0);
    }

    /**
     * Constructor
     *
     * @param cron Cron format trigger condition
     * @throws TriggerException Trigger exception
     */
    public CronTrigger(String cron) throws TriggerException {
        this(TimeZone.getDefault(), cron);
    }

    /**
     * Constructor
     *
     * @param zone Time Zone
     * @param cron Cron format trigger condition
     */
    public CronTrigger(TimeZone zone, String cron) {
        this();

        this.zone = zone;
        setCron(cron);
    }

    /**
     * <p>Set Cron format trigger condition</p>
     *
     * <p>
     * Cron format: second minute hour Day-of-month month Day-of-week year
     * <p>
     * Month: JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC
     * Weekday: SUN, MON, TUE, WED, THU, FRI, SAT
     * </p>
     *
     * <ul>
     * <li>Start every 10 seconds: 0-59/10 * * * * * *</li>
     * <li>Start every second from 10 minutes to 30 minutes every hour: * 10-30 * * * * *</li>
     * <li>Start every second in August: * * * * AUG * *</li>
     * <li>Start every 10 seconds from August 10th to 20th: 0-59/10 * * 10-20 AUG * *</li>
     * <li>Start every 10 seconds on Friday: 0-59/10 * * * * FRI *</li>
     * </ul>
     *
     * @param cron Unix cron text
     */
    public void setCron(String cron) {
        this.cron = cron;
        this.cronParser = new CronParser(cron);
    }

    /**
     * Get Cron expression
     *
     * @return cron expression
     */
    public String getCron() {
        return this.cron;
    }

    /**
     * <p>Set trigger time zone</p>
     *
     * <p>
     * For example, "GMT-8"
     * If the time zone is set to null, TimeZone.getDefault() will be used
     * </p>
     *
     * @param zone Time Zone
     */
    public void setTimeZone(String zone) {
        if (zone == null) {
            this.zone = TimeZone.getDefault();
        } else {
            this.zone = TimeZone.getTimeZone(zone);
        }
    }

    public boolean match(Date now) {

        if (super.isFinish()) {
            return false;
        }

        boolean cronMatch = this.cronParser.match(this.zone, now.getTime());
        if (cronMatch) {
            super.incExecuteCount();

            return true;
        } else {
            return false;
        }
    }

    /**
     * Cron parsing
     */
    private class CronParser {

        private Set<Integer> secondSet = new TreeSet<>();
        private Set<Integer> minuteSet = new TreeSet<>();
        private Set<Integer> hourSet = new TreeSet<>();
        private Set<Integer> dayOfMonthSet = new TreeSet<>();
        private Set<Integer> monthSet = new TreeSet<>();
        private Set<Integer> dayOfWeekSet = new TreeSet<>();
        private Set<Integer> yearSet = new TreeSet<>();

        public CronParser(String cron) {
            String[] cronItem = cron.split(" ");
            // Year is an optional field
            if (cronItem.length == 6 || cronItem.length == 7) {
                secondSet = parseInteger(cronItem[0], 0, 59);
                minuteSet = parseInteger(cronItem[1], 0, 59);
                hourSet = parseInteger(cronItem[2], 0, 23);
                dayOfMonthSet = parseInteger(cronItem[3], 1, 31);
                monthSet = parseMonth(cronItem[4]);
                dayOfWeekSet = parseDayOfWeek(cronItem[5]);
                if (cronItem.length == 7) {
                    yearSet = parseInteger(cronItem[6], 1970, 2100);
                }
            }
        }

        private Set<Integer> parseInteger(String value, int min, int max) {
            Set<Integer> result = new TreeSet<>();

            String[] rangeItems;
            if (value.contains(",")) {
                rangeItems = value.split(",");
            } else {
                rangeItems = new String[1];
                rangeItems[0] = value;
            }

            for (String rangeItem : rangeItems) {
                Set<Integer> tempResult = new TreeSet<>();

                int interval = 1;

                if (rangeItem.contains("/")) {
                    String[] temp = rangeItem.split("/");
                    rangeItem = temp[0];
                    interval = Integer.parseInt(temp[1]);

                    if (interval < 1) {
                        interval = 1;
                    }
                }

                if (rangeItem.contains("*")) {
                    tempResult.addAll(addToSet(min, max));
                } else if (rangeItem.contains("-")) {
                    String[] item = rangeItem.split("-");
                    tempResult.addAll(addToSet(Integer.parseInt(item[0]), Integer.parseInt(item[1])));
                } else {
                    tempResult.add(Integer.parseInt(rangeItem));
                }

                int count = 0;
                for (Integer item : tempResult) {
                    if (count % interval == 0) {
                        result.add(item);
                    }
                    count++;
                }
            }

            return result;
        }

        private Set<Integer> addToSet(int start, int end) {
            Set<Integer> result = new TreeSet<>();
            for (int i = start; i <= end; i++) {
                result.add(i);
            }
            return result;
        }

        private Set<Integer> parseMonth(String value) {
            String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
            for (int i = 0; i < 12; i++) {
                value = value.replaceAll(months[i], Integer.toString(i));
            }
            return parseInteger(value, 0, 11);
        }

        private Set<Integer> parseDayOfWeek(String value) {
            String[] dayOfWeeks = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
            for (int i = 0; i < 7; i++) {
                value = value.replaceAll(dayOfWeeks[i], Integer.toString(i + 1));
            }
            return parseInteger(value, 1, 7);
        }

        public boolean match(TimeZone zone, long time) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeInMillis(time);
            gc.setTimeZone(zone);
            int second = gc.get(Calendar.SECOND);
            int minute = gc.get(Calendar.MINUTE);
            int hour = gc.get(Calendar.HOUR_OF_DAY);
            int dayOfMonth = gc.get(Calendar.DAY_OF_MONTH);
            int month = gc.get(Calendar.MONTH);
            int dayOfWeek = gc.get(Calendar.DAY_OF_WEEK);
            int year = gc.get(Calendar.YEAR);

            return secondSet.contains(second) && minuteSet.contains(minute) && hourSet.contains(hour) && dayOfMonthSet.contains(dayOfMonth)
                    && monthSet.contains(month) && dayOfWeekSet.contains(dayOfWeek) && yearSet.contains(year);
        }
    }
}
