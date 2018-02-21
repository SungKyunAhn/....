package com.aimir.fep.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.protocol.fmp.processor.NDProcessor;

public class NDRestoreJob extends QuartzJobBean {
    private static Log log = LogFactory.getLog(NDRestoreJob.class);
    
    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {

        try {
            NDProcessor processor = new NDProcessor();
            processor.restore();
        }
        catch (Exception e) {
            log.error(e);
        }
    }
}
