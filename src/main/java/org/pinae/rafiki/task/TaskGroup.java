package org.pinae.rafiki.task;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.pinae.rafiki.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.pinae.rafiki.job.Job;
import org.pinae.rafiki.trigger.Trigger;

/**
 * Task Group
 *
 * @author Huiyugeng
 */
public class TaskGroup {
    private static final Logger logger = LogManager.getLogger(TaskGroup.class);

    public static final String DEFAULT = "default-group";

    /**
     * Task group status enumeration:
     * <ul>
     * <li>NOT_ANY_TASK: There is no task in the task group</li>
     * <li>READY_TO_RUN: The task group contains tasks and can be started</li>
     * <li>RUNNING: Tasks in the task group are being executed</li>
     * </ul>
     */
    public enum Status {
        NOT_ANY_TASK, READY_TO_RUN, RUNNING
    }

    /*
     * Task status
     */
    private Status status = Status.NOT_ANY_TASK;

    /*
     * Set the default task group name to: default-group
     */
    private String name;

    /*
     * Set default for max tasks
     */
    private int maxTask;

    /*
     * Task list
     */
    private final Map<String, Task> taskMap = new ConcurrentHashMap<>();

    private final ScheduledThreadPoolExecutor executor;

    /**
     * Constructor, default 20 tasks
     *
     * @param name Task group name
     */
    public TaskGroup(String name) {
        this(name, 20);
    }

    /**
     * Constructor
     *
     * @param name Task group name
     * @param maxTask Maximum number of tasks in the task group
     */
    public TaskGroup(String name, int maxTask) {
        this.name = name;
        this.maxTask = maxTask;

        this.executor = new ScheduledThreadPoolExecutor(maxTask);
    }

    /**
     * Get the task collection in the task group
     *
     * @return Task collection
     */
    public Collection<Task> getTasks() {
        return taskMap.values();
    }

    /**
     * Add tasks to the task group. If the task group has been started, start the added tasks at the same time
     *
     * @param task Task to be added
     * @throws TaskException Task addition exception
     */
    public void addTask(Task task) throws TaskException {
        if (task == null) {
            logger.error("Task add FAIL, task is NULL");
            return;
        }

        String taskName = task.getName();
        Job job = task.getJob();

        if (taskName == null) {
            task.setName(job.getName() != null ? job.getName() : job.toString());
        }
        if (this.taskMap.containsKey(taskName)) {
            throw new TaskException("Already has same task name : " + taskName);
        }

        task.setGroup(this);
        this.taskMap.put(taskName, task);

        if (this.status == Status.NOT_ANY_TASK) {
            this.status = Status.READY_TO_RUN;
        } else if (this.status == Status.RUNNING) {
            start(task);
        }
    }

    /**
     * Remove tasks from the task group according to the task name, and stop the removed tasks at the same time
     *
     * @param taskName Task name
     * @return Removed task
     * @throws TaskException Task removal exception
     */
    public Task removeTask(String taskName) throws TaskException {
        Task task = null;

        if (this.taskMap.size() > 0) {
            if (this.taskMap.containsKey(taskName)) {
                task = this.taskMap.remove(taskName);
                if (task != null) {
                    task.setStatus(Task.Status.STOP);
                    if (this.taskMap.size() == 0) {
                        this.status = Status.NOT_ANY_TASK;
                    }
                } else {
                    throw new TaskException("No such Task :" + taskName);
                }
            }
        }

        return task;
    }

    /**
     * Start all tasks in the task group
     *
     * @throws TaskException Task start exception
     */
    public void start() throws TaskException {

        Set<String> taskNameSet = this.taskMap.keySet();
        for (String taskName : taskNameSet) {
            Task task = this.taskMap.get(taskName);
            start(task);
        }
        status = Status.RUNNING;
    }

    /**
     * Start the specified task according to the task name
     *
     * @param taskName Task name
     * @throws TaskException Task start exception
     */
    public void start(String taskName) throws TaskException {
        Task task = this.taskMap.get(taskName);
        if (task != null) {
            start(task);
        } else {
            throw new TaskException("No such Task : " + taskName);
        }
    }

    /**
     * Start the specified task
     *
     * @param task Task to be started
     * @throws TaskException Task start exception
     */
    public void start(Task task) throws TaskException {
        if (task != null) {

            if (task.getStatus() == Task.Status.STOP) {
                String taskName = task.getName();
                if (StringUtils.isEmpty(taskName)) {
                    taskName = task.toString();
                }
                if (taskName.length() > 32) {
                    taskName = taskName.substring(0, 32);
                }

                Trigger trigger = task.getTrigger();
                long now = System.currentTimeMillis();
                long startTime = trigger.getStartTime().getTime() - now;

                TaskRunner taskRunner = new TaskRunner(task);

                if (task.getTrigger().isRepeat()) {
                    this.executor.scheduleWithFixedDelay(taskRunner, startTime, trigger.getRepeatInterval(), TimeUnit.MILLISECONDS);
                } else {
                    this.executor.schedule(taskRunner, startTime, TimeUnit.MILLISECONDS);
                }

                task.setRunner(taskRunner);
                task.setStatus(Task.Status.RUNNING);

                this.taskMap.put(task.getName(), task);
            } else {
                task.setStatus(Task.Status.RUNNING);
            }

        } else {
            throw new TaskException("Task is NULL");
        }
    }

    /**
     * Pause all tasks in the task group
     *
     * @throws TaskException Task pause exception
     */
    public void pause() throws TaskException {
        Set<String> taskNameSet = this.taskMap.keySet();
        for (String taskName : taskNameSet) {
            pause(taskName);
        }

        status = Status.READY_TO_RUN;
    }

    /**
     * Pause the specified task according to the task name
     *
     * @param taskName Task name
     * @throws TaskException Task pause exception
     */
    public void pause(String taskName) throws TaskException {
        Task task = this.taskMap.get(taskName);
        if (task != null) {
            task.setStatus(Task.Status.PAUSE);
        } else {
            throw new TaskException("No such Task : " + taskName);
        }
    }

    /**
     * Stop all tasks in the task group
     *
     * @throws TaskException Task stop exception
     */
    public void stop() throws TaskException {
        Set<String> taskNameSet = this.taskMap.keySet();
        for (String taskName : taskNameSet) {
            stop(taskName);
        }
        if (executor != null) {
            executor.shutdown();
        }
        status = Status.READY_TO_RUN;
    }

    /**
     * Stop the specified task according to the task name
     *
     * @param taskName Task name
     * @throws TaskException Task stop exception
     */
    public void stop(String taskName) throws TaskException {

        Task task = this.taskMap.get(taskName);
        if (task != null) {
            task.setStatus(Task.Status.STOP);
        } else {
            throw new TaskException("No such Task : " + taskName);
        }
    }

    /**
     * Get the task group name
     *
     * @return Task group name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the task group name
     *
     * @param name Task group name
     */
    public void setName(String name) {
        if (StringUtils.isNotEmpty(name)) {
            this.name = name;
        } else {
            this.name = "default-group-" + Long.toString(System.currentTimeMillis());
        }
    }

    /**
     * Get the task group status
     *
     * @return Task group status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Return the maximum number of tasks
     *
     * @return the maximum number of tasks
     */
    public int getMaxTask() {
        return maxTask;
    }

    public String toString() {
        return name;
    }
}
