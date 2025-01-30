package org.pinae.rafiki.task;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.pinae.rafiki.job.Job;
import org.pinae.rafiki.listener.JobListener;
import org.pinae.rafiki.listener.TaskListener;
import org.pinae.rafiki.trigger.AbstractTrigger;

/**
 * Task Executor
 *
 * @author Huiyugeng
 */
public final class TaskRunner implements Runnable {
    private static final Logger logger = LogManager.getLogger(TaskRunner.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /*
     * Tasks to be executed
     */
    private final Task task;
    /*
     * Task listener
     */
    private TaskListener taskListener;

    /*
     * Jobs to be executed
     */
    private final Job job;
    /*
     * Job listener
     */
    private JobListener jobListener;

    /*
     * Task trigger
     */
    private AbstractTrigger trigger;

    /*
     * Task start time: When the trigger meets the conditions, set the start time of the task execution, and set it to -1 when the task ends
     */
    private long startTime;

    /*
     * Whether this execution has timed out: true This execution has timed out; false This execution has not timed out
     */
    private boolean timeoutFlag = false;

    /**
     * Constructor
     *
     * @param task Tasks to be executed
     */
    protected TaskRunner(Task task) {
        this.task = task;

        if (task instanceof TaskListener) {
            this.taskListener = (TaskListener) task;
        }

        if (this.taskListener != null) {
            this.taskListener.start();
        }

        this.job = task.getJob();
        if (task.getTrigger() instanceof AbstractTrigger) {
            this.trigger = (AbstractTrigger) task.getTrigger();
        }

        if (this.job instanceof JobListener) {
            this.jobListener = (JobListener) job;
        }

    }

    public void run() {

        if (this.trigger == null) {
            return;
        }

        if (!this.trigger.isFinish() && this.task.getStatus() != Task.Status.STOP) {

            Date now = new Date();
            if (this.trigger.match(now) && this.task.getStatus() == Task.Status.RUNNING && this.task.getStatus() != Task.Status.PAUSE) {

                this.startTime = System.currentTimeMillis();

                logger.debug(String.format("task=%s; group=%s; date=%s; action=start", task, task.getGroup(), dateFormat.format(new Date())));

                try {

                    if (this.jobListener != null) {
                        this.jobListener.beforeJobExecute();
                    }

                    if (!this.job.execute()) {
                        if (this.jobListener != null) {
                            this.jobListener.executeFail();
                        }
                    }

                    if (this.jobListener != null) {
                        this.jobListener.afterJobExecute();
                    }

                } catch (Exception e) {
                    logger.debug(String.format("task=%s; group=%s; date=%s; exception=%s", task, task.getGroup(), dateFormat.format(new Date()),
                            e.getMessage()));

                    if (this.jobListener != null) {
                        this.jobListener.executeException();
                    }
                }

                long endTime = System.currentTimeMillis();
                logger.debug(String.format("task=%s; group=%s; date=%s; action=stop; used=%s ms", task, task.getGroup(),
                        dateFormat.format(new Date()), Long.toString(endTime - startTime)));

                this.startTime = -1;
                this.timeoutFlag = false;
            }
        } else {
            logger.debug(String.format("task=%s; group=%s; date=%s; action=finish", task, task.getGroup(), dateFormat.format(new Date())));

            if (this.taskListener != null) {
                this.taskListener.finish();
            }
        }

    }

    /**
     * Set the timeout flag to true
     */
    public void timeout() {
        this.timeoutFlag = true;
    }

    /**
     * Return the timeout flag
     *
     * @return Timeout flag
     */
    public boolean isTimeout() {
        return timeoutFlag;
    }

    /**
     * Return the start time of this task execution
     */
    public long getStartTime() {
        return this.startTime;
    }

}
