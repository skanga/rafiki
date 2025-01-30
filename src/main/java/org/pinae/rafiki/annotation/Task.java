package org.pinae.rafiki.annotation;

import org.pinae.rafiki.task.TaskGroup;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Task {
    String name();

    String group() default TaskGroup.DEFAULT;
}
