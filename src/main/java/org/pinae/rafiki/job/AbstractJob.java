package org.pinae.rafiki.job;

/**
 * Abstract job class
 *
 * @author Huiyugeng
 */
public abstract class AbstractJob implements Job {
    /**
     * Job sequence number, used for global unique identification,
     * task sequence number = task name - current timestamp
     */
    private String serial;

    /**
     * Job Name
     */
    private String name;

    public AbstractJob() {
        setName(this.toString());
    }

    public void setName(String name) {
        this.name = name;

        this.serial = name + "-" + Long.toString(System.currentTimeMillis());
    }

    public String getName() {
        return name;
    }

    public String getSerial() {
        return serial;
    }

    public String toString() {
        return name;
    }

    public abstract boolean execute() throws JobException;
}
