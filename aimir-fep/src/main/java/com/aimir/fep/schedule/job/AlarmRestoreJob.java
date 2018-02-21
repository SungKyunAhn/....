package com.aimir.fep.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.protocol.fmp.processor.AlarmProcessor;

public class AlarmRestoreJob extends QuartzJobBean {
    private static Log log = LogFactory.getLog(AlarmRestoreJob.class);
    
    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {

        try {
            AlarmProcessor processor = new AlarmProcessor();
            processor.restore();
        }
        catch (Exception e) {
            log.error(e);
        }
    }
}
