package org.pinae.rafiki.task;

import java.io.Serial;

/**
 * Task abnormality threw an Exception
 *
 * @author Huiyugeng
 */
public class TaskException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public TaskException() {
        super();
    }

    public TaskException(String message) {
        super(message);
    }

    public TaskException(Throwable cause) {
        super(cause);
    }

    public TaskException(String message, Throwable cause) {
        super(message, cause);
    }

}
