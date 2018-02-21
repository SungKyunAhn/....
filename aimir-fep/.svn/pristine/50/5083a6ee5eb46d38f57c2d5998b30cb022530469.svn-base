/* 
 * @(#)TMTR_EVENT.java       1.0 2008-06-02 *
 * 
 * Meter Event Data
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
/**
 * @author YK.Park
 */
package com.aimir.fep.meter.parser.kdhTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;


public class TMTR_EVENT implements java.io.Serializable{    

    public static final int TABLE_CODE = 0x0F;
	
    public static final int FAULT_BATTERY_LOW_ALARM   = 2;
    public static final int FAULT_SUPPLY_TEMP_ALARM   = 4;
    public static final int FAULT_RETRIVAL_TEMP_ALARM = 5;    
    public static final int FAULT_AC_FAIL_ALARM       = 7;
    public static final int FAULT_RTC_FAIL_ALARM      = 9;
    
    public static final int LEN_FAULT_CODE = 1;
    public static final int LEN_STARTTIME  = 4;
    public static final int LEN_ENDTIME    = 4;
    public static final int LEN_FAULT      = 9;
    
    EventLogData[] eventLog = null;
    
    public static final String[] FAULT_TABLE = 
    {
         "",
         "",
         "Battery low alarm",
         "",
         "Supply temperature sensor alarm",
         "Retrival temperature sensor alarm",
         "",
         "AC Fail alarm",
         "",
         "RTC Fail alarm"
    };
	
	private byte[] rawData = null;

    private static Log log = LogFactory.getLog(TMTR_EVENT.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public TMTR_EVENT(byte[] rawData) {
        this.rawData = rawData;
        parse();
	}
    
    public void parse()
    {
        int code = 0;
        String startTime = null;
        String endTime = null;
        int eventCount = rawData.length/LEN_FAULT;
        int offset = 0;
        eventLog = new EventLogData[eventCount];
        log.debug("eventCount :"+eventCount);
        for(int i = 0; i < eventCount; i++)
        {
        	log.debug("offset :"+offset);
            code = (int)(rawData[offset] & 0xFF);
            startTime = new DATETIME(rawData,offset+LEN_FAULT_CODE,LEN_STARTTIME).getDateTime();
            endTime = new DATETIME(rawData,offset+LEN_FAULT_CODE+LEN_STARTTIME,LEN_ENDTIME).getDateTime();          
            offset += LEN_FAULT*i;
            
            eventLog[i] = new EventLogData();
            eventLog[i].setDate(startTime.substring(0,8));
            eventLog[i].setTime(startTime.substring(8,14));
            eventLog[i].setKind("STE");
            eventLog[i].setFlag(code);
            eventLog[i].setMsg(FAULT_TABLE[code]);
            log.debug("eventCount "+i+" end");
        }
        log.debug("parse end :");
    }
    
    public EventLogData[] getEventLogData()
    {
        return eventLog;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("TMTR_EVENT DATA[");  
            for(int i = 0; i < eventLog.length; i++)
            {
                sb.append(eventLog[i].toString());
            }
            sb.append("]\n");
        }catch(Exception e){
            log.warn("TMTR_EVENT TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }

}
