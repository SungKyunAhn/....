package com.aimir.fep.schedule.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.fmp.exception.FMPMcuException;

public class onDemandConverterTaskA1800 {

	private static Log log = LogFactory.getLog(onDemandConverterTaskA1800.class);
 
	@Autowired
	CommandGW commandGw;
	
	public onDemandConverterTaskA1800(){
		
	}
	
	public void OnDemand() throws FMPMcuException, Exception {

			commandGw.cmdOnDemandMeter(null, "06629025", "11040051", "0", "", "");

	}

}
