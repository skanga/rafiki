package org.pinae.rafiki.job;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class DelayJob implements Job {
    private static final Logger logger = LogManager.getLogger(DelayJob.class);
    private int jobId = 0;
    public DelayJob() {
    }

    public DelayJob(int jobId) {
        this.jobId = jobId;
    }

    public String getName() {
        return "DelayJob";
    }

    public boolean execute() throws JobException {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            logger.error(String.format("getTrigger Exception: exception=%s", e.getMessage()));
        }
        logger.info(String.format("Delay Job %d Finish: time=%d", jobId, System.currentTimeMillis()));

        return true;
    }
}
