package org.pinae.rafiki.trigger.impl;

import java.util.Date;

import org.pinae.rafiki.trigger.AbstractTrigger;

/**
 * Simple Periodic trigger
 *
 * @author Huiyugeng
 */
public class SimpleTrigger extends AbstractTrigger {
    private long lastExecuteTime = 0;

    /**
     * Constructor
     */
    public SimpleTrigger() {
    }

    /**
     * Constructor
     *
     * @param repeatCount    how many repetitions are needed
     * @param repeatInterval how long before it repeats
     */
    public SimpleTrigger(int repeatCount, long repeatInterval) {
        super.setRepeatCount(repeatCount);
        super.setRepeatInterval(repeatInterval);
    }

    @Override
    public boolean match(Date now) {

        if (super.isFinish()) {
            return false;
        }

        if (now.getTime() - this.lastExecuteTime < getRepeatInterval()) {
            return false;
        } else {
            this.lastExecuteTime = System.currentTimeMillis();
            super.incExecuteCount();
            return true;
        }
    }
}
