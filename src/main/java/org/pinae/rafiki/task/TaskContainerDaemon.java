package org.pinae.rafiki.task;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.pinae.rafiki.task.Task.Status;

/**
 * Task container daemon thread
 *
 * @author Huiyugeng
 */
public class TaskContainerDaemon implements Runnable {
    private final Logger logger = LogManager.getLogger(TaskContainerDaemon.class);

    private boolean stop = true;

    private final TaskContainer container;

    /**
     * Constructor
     *
* @param container Task container to be guarded
     */
    public TaskContainerDaemon(TaskContainer container) {
        this.container = container;
    }

    /**
* Start daemon thread
     * <p>
* Daemon thread is started by TaskContainer's start, startTask/startGroup will not start daemon thread
     */
    public void start() {
        if (stop) {
            new Thread(this, String.format("%s Container-Deamon", container.getName())).start();
            stop = false;
        }
    }

    /**
     * Stop daemon thread
     * <p>
     * Daemon thread is started by TaskContainer's stop, stopTask/stopGroup will not stop daemon thread
     */
    public void stop() {
        stop = true;
    }

    public void run() {
        while (!stop) {

            // Check whether the task in the task container has timed out, and force the timed out task to terminate
            Collection<TaskGroup> taskGroups = this.container.getTaskGroup();
            for (TaskGroup taskGroup : taskGroups) {
                Collection<Task> tasks = taskGroup.getTasks();
                for (Task task : tasks) {

                    Status status = task.getStatus();
                    long timeout = task.getTimeout();

                    TaskRunner runner = task.getRunner();
                    long startTime;

                    if (runner != null) {
                        startTime = runner.getStartTime();

                        if (status == Status.RUNNING && timeout > 0 && startTime > 0) {
                            long now = System.currentTimeMillis();

                            // Determine whether the task has timed out
                            if (now - startTime > timeout) {
                                if (!runner.isTimeout()) {
                                    logger.error(String.format("task=%s; group=%s; action=timeout", task, task.getGroup()));
                                    runner.timeout();
                                }
                            }
                        }
                    }

                }
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {

            }
        }
    }

}
