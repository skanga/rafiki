package org.pinae.rafiki.job;


/**
 * Job interface
 *
 * @author Huiyugeng
 */
public interface Job {

    /**
     * Get the job name
     *
     * @return Job Name
     */
    public String getName();

    /**
     * Job executor method
     *
     * @return Whether the job was successfully executed or not
     * @throws JobException Job execution exception
     */
    public boolean execute() throws JobException;
}
