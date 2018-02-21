package com.aimir.fep.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.schedule.task.onDemandConverterTaskA1800;
import com.aimir.fep.util.DataUtil;

public class onDemandConverterJobA1800 extends QuartzJobBean {
	
	private static Log log = LogFactory.getLog(onDemandConverterJobA1800.class);

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {

		onDemandConverterTaskA1800 task = DataUtil.getBean(onDemandConverterTaskA1800.class);
		try {
			log.debug("OnDemandConverterJobA1800");
			task.OnDemand();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e);
		}
		
	}
}
