package com.aimir.fep.meter.parser.rdata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 실시간 검침을 지원하기 위한 RealTime Metering Frame에 대한 파서
 * @author  kaze
 */
public class RealMeteringFrame implements java.io.Serializable
{
	
	private static final long serialVersionUID = 1078663351828465967L;
	private static Log log = LogFactory.getLog(RealMeteringFrame.class);
	
	
	protected byte[] rawData = null;	
        
}
