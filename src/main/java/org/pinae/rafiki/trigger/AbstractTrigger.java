package org.pinae.rafiki.trigger;

import java.util.Date;

/**
 * Task trigger abstract class
 *
 * @author Huiyugeng
 */
public abstract class AbstractTrigger implements Trigger {

    /*
     * Trigger name
     */
    private String name;

    /*
     * Trigger start time
     */
    private Date startTime = new Date();

    /*
     * Trigger end time
     */
    private Date endTime = null;

    /*
     * Whether to execute repeatedly
     */
    private boolean repeat = true;

    /*
     * Trigger cycle period (ms)
     */
    private long repeatInterval = 1000;

    /*
     * Trigger cycle times
     */
    private int repeatCount = 0;

    /*
     * Trigger start delay (ms)
     */
    private long startDelayTime = 0;

    /*
     * Trigger hit times
     */
    private long executeCount = 0;

    /**
     * Constructor, build default trigger name
     */
    public AbstractTrigger() {
        name = this.toString();
    }

    /**
     * Constructor
     *
     * @param name Trigger name
     */
    public AbstractTrigger(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartTime() {
        return startTime;
    }


    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public long getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(long repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
        if (repeatCount > 0) {
            this.repeat = true;
        }
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setEndDelayTime(long endDelayTime) {
        this.endTime = new Date((new Date()).getTime() + endDelayTime);
    }

    public long getStartDelayTime() {
        return startDelayTime;
    }

    public void setStartDelayTime(long startDelayTime) {
        this.startDelayTime = startDelayTime;
        this.startTime = new Date((new Date()).getTime() + startDelayTime);
    }

    public void incExecuteCount() {
        executeCount++;
    }

    public boolean isFinish() {

        if (repeatCount != 0 && repeatCount <= executeCount) {
            return true;
        }

        long now = new Date().getTime();
        if (endTime != null && endTime.getTime() <= now) {
            return true;
        }

        return false;
    }

    public abstract boolean match(Date now);

    public Trigger clone() throws CloneNotSupportedException {
        Object cloneObj = super.clone();
        if (cloneObj instanceof Trigger) {
            return (Trigger) cloneObj;
        }
        return null;
    }


}
