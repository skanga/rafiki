package org.pinae.rafiki.trigger.impl;

import org.pinae.rafiki.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Daily Trigger
 *
 * @author Huiyugeng
 */
public class DailyTrigger extends EverydayTrigger {

    private final List<String> dayList = new ArrayList<>();

    @Override
    public boolean match(Date now) {

        String date = new SimpleDateFormat("yyyy/MM/dd").format(now);

        if (this.dayList.contains(date)) {
            return super.match(now);
        } else {
            return false;
        }

    }

    /**
     * <p>Set trigger date</p>
     *
     * <p>
     * Date format: 'yyyy/mm/dd' Support multiple dates separated by ';'
     * For example, '2015/03/04; 2015/05/05'
     * </p>
     *
     * @param dateText Trigger date
     */
    public void setDate(String dateText) {
        if (StringUtils.isNotBlank(dateText)) {
            if (dateText.contains(";")) {
                String[] dates = dateText.split(";");
                for (String date : dates) {
                    if (StringUtils.isNotBlank(date)) {
                        this.dayList.add(date.trim());
                    }
                }
            } else {
                this.dayList.add(dateText);
            }
        }
    }
}
