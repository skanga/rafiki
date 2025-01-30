package org.pinae.rafiki.trigger;

/**
 * Task trigger exception
 *
 * @author Huiyugeng
 */
public class TriggerException extends Exception {

    private static final long serialVersionUID = 1L;

    public TriggerException() {
        super();
    }

    public TriggerException(String message) {
        super(message);
    }

    public TriggerException(Throwable cause) {
        super(cause);
    }

    public TriggerException(String message, Throwable cause) {
        super(message, cause);
    }

}
