package com.aimir.fep.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.protocol.fmp.processor.MDProcessor;
import com.aimir.fep.util.DataUtil;

public class MDRestoreJob extends QuartzJobBean {
    private static Log log = LogFactory.getLog(MDRestoreJob.class);
    
    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {

        try {
            MDProcessor processor = DataUtil.getBean(MDProcessor.class);
            
            processor.restore();
        }
        catch (Exception e) {
            log.error(e);
        }
    }
}
