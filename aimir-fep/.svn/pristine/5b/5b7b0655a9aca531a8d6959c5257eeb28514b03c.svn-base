package com.aimir.fep.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.protocol.fmp.processor.MDProcessor;
import com.aimir.fep.schedule.task.OnDemandConverterTask;
import com.aimir.fep.util.DataUtil;

/**
 * converter type 모뎀에 on demand 명령을 날리는 job.
 * @author kskim
 *
 */
public class OnDemandConverterJob extends QuartzJobBean {
	private static Log log = LogFactory.getLog(OnDemandConverterJob.class);
	
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		OnDemandConverterTask task = DataUtil.getBean(OnDemandConverterTask.class);
		try {
			log.debug("OnDemandConverterJob");
			task.OnDemand();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e);
		}
	}

}
