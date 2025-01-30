package org.pinae.rafiki.task;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.pinae.rafiki.StringUtils;

/**
 * Task Container
 *
 * @author Huiyugeng
 */
public class TaskContainer {
    private static Logger logger = LogManager.getLogger(TaskContainer.class);

    /*
     * Set the default container name to: default-container
     */
    private String name = "default-container";

    /*
     * Daemon thread, starts executing when the container starts & ends when the container stops
     */
    private final TaskContainerDaemon daemon = new TaskContainerDaemon(this);

    /*
     * Task Group List <name, task group>
     */
    private final Map<String, TaskGroup> taskGroupMap = new ConcurrentHashMap<String, TaskGroup>();

    /*
     * Maximum task group data volume, default 10
     */
    private int maxGroup = 10;

    /*
     * Task Group Counters
     */
    private int groupCounter = 0;

    /*
     * Task Counter
     */
    private int taskCounter = 0;

    /**
     * Constructor
     * <p>
     * Construct a task container named default and add a default task group
     */
    public TaskContainer() {
        taskGroupMap.put(TaskGroup.DEFAULT, new TaskGroup(TaskGroup.DEFAULT));
    }

    /**
     * Constructor
     * <p>
     * Constructs a task container with the specified name and adds a default task group
     *
     * @param name Task container name
     */
    public TaskContainer(String name) {
        this.name = name;
        taskGroupMap.put(TaskGroup.DEFAULT, new TaskGroup(TaskGroup.DEFAULT));
    }

    /**
     * Add a task group to the task container. The default number of tasks is 20.
     *
     * @param groupName Task Group Name
     * @return Added task group
     */
    public TaskGroup addGroup(String groupName) {
        return addGroup(groupName, 20);
    }

    /**
     * Add a task group to the task container
     *
     * @param groupName Task Group Name
     * @param maxTask   Maximum number of tasks in a task group
     * @return Added task group
     */
    public TaskGroup addGroup(String groupName, int maxTask) {

        TaskGroup group = null;

        if (!this.taskGroupMap.containsKey(groupName)) {
            if (this.groupCounter < this.maxGroup) {
                group = new TaskGroup(groupName);
                this.taskGroupMap.put(groupName, group);
                this.groupCounter++;
            } else {
                logger.error(String.format("container=%s; exception=max group count is %d", name, maxGroup));
            }
        }

        return group;
    }

    /**
     * Add a task to the specified task group
     * <p>
     * If the task group does not exist, create a new task group
     *
     * @param task      Task
     * @param groupName Task Group Name
     * @throws TaskException Task addition exception, usually because the task group has been started, adding an exception thrown during task startup
     */
    public void addTask(Task task, String groupName) throws TaskException {
        TaskGroup group = this.taskGroupMap.get(groupName);

        if (group == null) {
            group = addGroup(groupName);
        }

        int maxTask = group.getMaxTask();

        if (group != null) {
            if (this.taskCounter < maxTask && task != null) {
                group.addTask(task);
                this.taskGroupMap.put(groupName, group);

                this.taskCounter++;

                logger.debug(String.format("task=%s; group=%s; action=add", task.getName(), groupName));
            } else {
                logger.error(String.format("container %s; exception=max task count is %d", name, maxTask));
            }
        }

    }

    /**
     * Adding tasks to the default task group
     *
     * @param task Task
     * @throws TaskException Task addition exception
     */
    public void addTask(Task task) throws TaskException {
        this.addTask(task, TaskGroup.DEFAULT);
    }

    /**
     * Remove a task from the default task group
     *
     * @param taskName Task Name
     * @throws TaskException When the task name does not exist, a task removal exception is thrown
     */
    public void removeTask(String taskName) throws TaskException {
        this.removeTask(taskName, TaskGroup.DEFAULT);
    }

    /**
     * Removes a task from the specified task group.
     * <p>
     * When removing, the task will be forced to stop running.
     *
     * @param taskName  Task Name
     * @param groupName Task Group Name
     * @throws TaskException A task removal exception is thrown when the task group or task does not exist
     */
    public void removeTask(String taskName, String groupName) throws TaskException {
        TaskGroup group = this.taskGroupMap.get(groupName);
        if (group != null) {
            group.stop(taskName);

            Task task = group.removeTask(taskName);
            if (task != null) {
                logger.debug(String.format("task=%s; group=%s; action=remove", task.getName(), groupName));
            }
            this.taskCounter--;
        }
    }

    /**
     * Remove a task group from a task container
     * <p>
     * When removing a task group, all tasks in the task group will be forced to stop
     *
     * @param groupName Task Group Name
     * @throws TaskException Task Group Removes Exception
     */
    public void removeGroup(String groupName) throws TaskException {
        if (!groupName.equals(TaskGroup.DEFAULT)) {

            stopGroup(groupName);
            TaskGroup taskGroup = this.taskGroupMap.remove(groupName);
            if (taskGroup != null) {
                this.groupCounter--;
            }
            logger.debug(String.format("group=%s; action=remove", groupName));
        }
    }

    /**
     * Start a task in the default task group based on the task name
     *
     * @param taskName Task Name
     * @throws TaskException Task startup exception
     */
    public void startTask(String taskName) throws TaskException {
        startTask(taskName, TaskGroup.DEFAULT);
    }

    /**
     * Start the tasks in the task group according to the task group name and task name
     *
     * @param taskName  Task Name
     * @param groupName Task Group Name
     * @throws TaskException Task startup exception
     */
    public void startTask(String taskName, String groupName) throws TaskException {
        TaskGroup group = this.taskGroupMap.get(groupName);
        if (group != null) {
            group.start(taskName);
        }
        logger.debug(String.format("task=%s, group=%s; action=start", taskName, groupName));
    }

    /**
     * Start all tasks in the task group according to the task group name
     *
     * @param groupName Task Group Name
     * @throws TaskException Task group startup exception
     */
    public void startGroup(String groupName) throws TaskException {
        TaskGroup group = this.taskGroupMap.get(groupName);
        if (group != null) {
            group.start();
        }
        logger.debug(String.format("group=%s; action=start", groupName));
    }

    /**
     * Start all tasks in the task container (do not start the container daemon thread)
     *
     * @throws TaskException Task container startup exception
     */
    public void start() throws TaskException {
        start(false);
    }

    /**
     * Start all tasks in the task container
     *
     * @param daemon Whether to start the container daemon thread
     * @throws TaskException Task container startup exception
     */
    public void start(boolean daemon) throws TaskException {
        if (daemon) {
            this.daemon.start();
        }
        Set<String> groupNameSet = this.taskGroupMap.keySet();
        for (String groupName : groupNameSet) {
            if (StringUtils.isNotEmpty(groupName)) {
                this.startGroup(groupName);
            }
        }
    }

    /**
     * Pause all tasks in the task container
     *
     * @throws TaskException Task suspension exception
     */
    public void pause() throws TaskException {
        Set<String> groupNameSet = this.taskGroupMap.keySet();
        for (String groupName : groupNameSet) {
            pauseGroup(groupName);
        }
    }

    /**
     * Pause tasks in the default task group by task name
     *
     * @param taskName Task Name
     * @throws TaskException Task suspension exception
     */
    public void pauseTask(String taskName) throws TaskException {
        pauseTask(taskName, TaskGroup.DEFAULT);
    }

    /**
     * Pause tasks in a task group according to the task group name and task name
     *
     * @param taskName  Task Name
     * @param groupName Task Group Name
     * @throws TaskException Task Pause Exception
     */
    public void pauseTask(String taskName, String groupName) throws TaskException {
        TaskGroup taskGroup = this.taskGroupMap.get(groupName);
        if (taskGroup != null) {
            taskGroup.pause(taskName);
        }
    }

    /**
     * Pause all tasks in a task according to the task group name
     *
     * @param groupName Task Group Name
     * @throws TaskException Task Pause Exception
     */
    public void pauseGroup(String groupName) throws TaskException {
        TaskGroup taskGroup = this.taskGroupMap.get(groupName);
        if (taskGroup != null) {
            taskGroup.pause();
        }
    }

    /**
     * Stop tasks in the default task group according to the task name
     *
     * @param taskName Task name
     * @throws TaskException Task Stop Exception
     */
    public void stopTask(String taskName) throws TaskException {
        stopTask(taskName, TaskGroup.DEFAULT);
    }

    /**
     * Stop tasks in a task group according to the task group name and task name
     *
     * @param taskName  Task Name
     * @param groupName Task Group Name
     * @throws TaskException Task Stop Exception
     */
    public void stopTask(String taskName, String groupName) throws TaskException {
        TaskGroup group = (TaskGroup) this.taskGroupMap.get(groupName);
        if (group != null) {
            group.stop(taskName);
            logger.debug(String.format("task=%s, group=%s; action=stop", taskName, groupName));
        }
    }

    /**
     * Stop all tasks in a task according to the task group name
     *
     * @param groupName Task Group Name
     * @throws TaskException Task Stop Exception
     */
    public void stopGroup(String groupName) throws TaskException {
        TaskGroup group = (TaskGroup) this.taskGroupMap.get(groupName);
        if (group != null) {
            group.stop();
            logger.debug(String.format("group=%s; action=stop", groupName));
        }
    }

    /**
     * Stop all tasks in a task container
     * <p>
     * When stopping a task container, stop the container daemon thread at the same time
     *
     * @throws TaskException Task Stop Exception
     */
    public void stop() throws TaskException {
        this.daemon.stop();

        Set<String> groupNameSet = this.taskGroupMap.keySet();
        for (String groupName : groupNameSet) {
            if (StringUtils.isNotEmpty(groupName)) {
                this.stopGroup(groupName);
            }
        }
    }

    /**
     * Get the task container name
     *
     * @return Task container name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the task container name
     *
     * @param name Task container name
     */
    public void setName(String name) {
        if (StringUtils.isNotEmpty(name)) {
            this.name = name;
        } else {
            this.name = "default-container-" + Long.toString(System.currentTimeMillis());
        }
    }

    /**
     * Get the maximum number of task groups
     *
     * @return Maximum number of task groups
     */
    public int getMaxGroup() {
        return maxGroup;
    }

    /**
     * Set the maximum number of task groups
     *
     * @param maxGroup Maximum number of task groups
     */
    public void setMaxGroup(int maxGroup) {
        this.maxGroup = maxGroup;
    }


    /**
     * Get a task group collection
     *
     * @return Task group collection
     */
    public Collection<TaskGroup> getTaskGroup() {
        return taskGroupMap.values();
    }

    public String toString() {
        return name;
    }

}
