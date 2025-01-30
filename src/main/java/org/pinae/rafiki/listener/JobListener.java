package org.pinae.rafiki.listener;

/**
 * Job Listener
 *
 * @author Huiyugeng
 */
public interface JobListener {

    /**
     * Before job execution
     */
    public void beforeJobExecute();

    /**
     * After the job is executed
     */
    public void afterJobExecute();

    /**
     * When the job execution returns false
     */
    public void executeFail();

    /**
     * When the job execution throws an exception
     */
    public void executeException();
}
