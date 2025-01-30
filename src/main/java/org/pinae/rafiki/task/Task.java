package org.pinae.rafiki.task;

import org.pinae.rafiki.job.Job;
import org.pinae.rafiki.trigger.Trigger;

/**
 * Task
 *
 * @author Huiyugeng
 */
public class Task {
    public enum Status {
        STOP, RUNNING, PAUSE
    }

    /*
     * Task sequence number, globally unique, task sequence number = task name - timestamp
     */
    private String serial;

    /*
     * Task Name
     */
    private String name;

    /*
     * The task group to which the task belongs
     */
    private TaskGroup group;

    /*
     * Job operation
     */
    private Job job;

    /*
     * Trigger
     */
    private Trigger trigger;

    /*
     * Task timeout: 0 means never timeout
     */
    private long timeout;

    /*
     * Task Executor
     */
    private TaskRunner runner;

    /*
     *  Task status, 0: STOP, 1: RUNNING, 2: PAUSE
     */
    private Status status = Status.STOP;

    /**
     * Constructor
     */
    public Task() {
    }

    /**
     * Constructor
     *
     * @param name    Task Name
     * @param job     Tasks
     * @param trigger Task Trigger
     */
    public Task(String name, Job job, Trigger trigger) {
        setName(name);
        setJob(job);
        setTrigger(trigger);
    }

    /**
     * Set the task name
     * When setting the task name, also set the task sequence number
     *
     * @param name task name
     */
    public void setName(String name) {
        if (name != null) {
            this.name = name;
        } else {
            this.name = "NONE";
        }

        this.serial = name + "-" + Long.toString(System.currentTimeMillis());
    }

    /**
     * Get the task name
     *
     * @return Task Name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the task group to which the task belongs
     *
     * @return Task Group
     */
    public TaskGroup getGroup() {
        return group;
    }

    /**
     * Add the task to the specified task group
     *
     * @param group Task Group
     */
    public void setGroup(TaskGroup group) {
        this.group = group;
    }

    /**
     * Setting up task jobs
     *
     * @param job Job
     */
    public void setJob(Job job) {
        this.job = job;
    }

    /**
     * Get Task Jobs
     *
     * @return Job
     */
    public Job getJob() {
        return job;
    }

    /**
     * Setting up task triggers
     *
     * @param trigger Trigger
     */
    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    /**
     * Get task trigger
     *
     * @return Trigger
     */
    public Trigger getTrigger() {
        return trigger;
    }

    /**
     * Get the task sequence number
     *
     * @return Task sequence number
     */
    public String getSerial() {
        return serial;
    }

    /**
     * Get task status
     *
     * @return Status (STOP, RUNNING, PAUSE)
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the task status
     *
     * @param status The current status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get task timeout
     *
     * @return Task timeout (ms)
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Set task timeout
     *
     * @param timeout Task timeout (ms)
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Get the task executor
     *
     * @return Task Executor
     */
    public TaskRunner getRunner() {
        return runner;
    }

    /**
     * Set the Task Runner
     *
     * @param runner Task Runner
     */
    public void setRunner(TaskRunner runner) {
        this.runner = runner;
    }

    public String toString() {
        return serial;
    }
}
