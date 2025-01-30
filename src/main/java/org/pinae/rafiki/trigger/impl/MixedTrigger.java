package org.pinae.rafiki.trigger.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.pinae.rafiki.trigger.Trigger;
import org.pinae.rafiki.trigger.AbstractTrigger;

/**
 * Mixed trigger
 * <p>
 * Supports mixed triggering of different types of triggers
 *
 * @author Huiyugeng
 */
public class MixedTrigger extends AbstractTrigger {

    public final static int AND = 1;
    public final static int OR = 0;
    private int operate = AND;

    private Set<Trigger> triggerSet = new HashSet<Trigger>();

    /**
     * Constructor
     */
    public MixedTrigger() {
        super.setRepeat(true);
        super.setRepeatCount(0);
    }

    @Override
    public boolean match(Date now) {

        if (super.isFinish()) {
            return false;
        }

        for (Trigger trigger : this.triggerSet) {
            if (trigger != null) {

                if (now.getTime() > trigger.getStartTime().getTime()) {

                    boolean triggerMatch = trigger.match(now);

                    if (!triggerMatch && this.operate == AND) {
                        return false;
                    } else if (triggerMatch && this.operate == OR) {
                        super.incExecuteCount();
                        return true;
                    }
                }
            }
        }

        if (this.operate == AND) {
            super.incExecuteCount();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Add triggers to mixed triggers
     *
     * @param trigger The trigger to be added
     */
    public void addTrigger(Trigger trigger) {
        if (trigger != null) {
            if (trigger.isRepeat()) {
                this.setRepeat(true);
            }
            this.triggerSet.add(trigger);
        }
    }

    /**
     * <p>Set the mixed trigger operation</p>
     * <p>
     * For example: MultiTrigger.AND -- When all triggers in the mixed trigger meet the trigger condition, the mixed trigger is triggered
     * MultiTrigger.OR -- When any trigger in the mixed trigger meets the trigger condition, the mixed trigger is triggered
     *
     * @param operate Mixed trigger condition
     */
    public void setOperate(int operate) {
        this.operate = operate;
    }

}
