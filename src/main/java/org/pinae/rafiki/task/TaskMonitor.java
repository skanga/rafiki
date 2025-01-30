package org.pinae.rafiki.task;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Task execution monitoring
 *
 * @author Huiyugeng
 */
public class TaskMonitor implements Runnable {
    private final Logger logger = LogManager.getLogger(TaskMonitor.class);

    private boolean stop = true;

    private final TaskContainer container;

    /**
     * Constructor
     *
     * @param container Task container to be monitored
     */
    public TaskMonitor(TaskContainer container) {
        this.container = container;
    }

    /**
     * Start monitoring thread
     */
    public void start() {
        if (stop) {
            new Thread(this, String.format("%s Container-Monitor", container.getName())).start();
            stop = false;
        }
    }

    /**
     * Stop monitoring thread
     */
    public void stop() {
        stop = true;
    }

    /**
     * Task notification
     *
     * @param task Task information
     * @param status Task status
     */
    public void notify(Task task, int status) {
        if (!stop) {
        }
    }

    public void run() {
        while (!stop) {
        }
    }
}
