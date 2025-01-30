package org.pinae.rafiki.listener;

/**
 * Task Listener
 *
 * @author Huiyugeng
 */
public interface TaskListener {
    /**
     * Executes after Task is started
     */
    public void start();

    /**
     * Executes after Task is stopped
     */
    public void finish();
}
