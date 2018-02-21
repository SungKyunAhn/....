package com.aimir.fep.schedule.job;

import java.math.BigInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.aimir.fep.iot.domain.resources.Resource;
import com.aimir.fep.iot.service.snowflake.BasicEntityIdGenerator;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.util.DataUtil;

@Component
public class IoTMDMSBeJob extends QuartzJobBean{
	private static Log logger = LogFactory.getLog(IoTMDMSBeJob.class);
	
	@Autowired
	ProcessorHandler processorHandler;
	
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		try {
			Resource resource = new Resource();
			resource.setResourceType(new BigInteger("0"));
			resource.setSeq(BasicEntityIdGenerator.getInstance().generateLongId());
			
			if(processorHandler == null) {
				processorHandler = DataUtil.getBean(ProcessorHandler.class);
			}
			
			logger.debug("["+resource.getSeq()+"] MDMS-BE '.DAT' File Timeout!!!");
			processorHandler.putServiceIoTData(ProcessorHandler.SERVICE_B_MDMSData, resource);
		}catch(Exception e) {
			logger.error(e.getMessage());
		}
	}

}
