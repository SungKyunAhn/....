/* 
 * @(#)LGRW3410_EV.java       1.0 08/05/29 *
 * 
 * Event Log.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.lgrw3410Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * @author Kang Soyi ksoyi@nuritelecom.com
 */
public class LGRW3410_EV {
    
    public static final int OFS_METER_STATUS = 8;
	private final int LEN_METER_STATUS=4;
	
    private EventLogData[] eventdata;
    private byte[] rawData = null;
    
    private static Log log = LogFactory.getLog(LGRW3410_EV.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public LGRW3410_EV(byte[] rawData) {
		this.rawData = rawData;
        parseEvent();
	}
    
    public EventLogData[] getEvent() {
        return this.eventdata;
    }

    private void parseEvent(){
        
        try {
        	MeterStatus ms = new MeterStatus(
                    DataFormat.select(
                    		rawData,OFS_METER_STATUS, LEN_METER_STATUS));
        	
            int eventCount=0;
            EventLogData[] eventdata = new EventLogData[12];
            String datetime = Util.currYymmddHHMMss(); // event time == server's time
            if(ms.getPOTENTIAL_ERROR()){
                eventdata[eventCount] = new EventLogData();
                eventdata[eventCount].setDate(datetime.substring(0,8));
                eventdata[eventCount].setTime(datetime.substring(8,14));
                eventdata[eventCount].setKind("STS");
                eventdata[eventCount].setFlag(1);
                eventdata[eventCount++].setMsg("POTENTIAL ERROR");
            }
            if(ms.getEEPROM_ERROR()){
                eventdata[eventCount] = new EventLogData();
                eventdata[eventCount].setDate(datetime.substring(0,8));
                eventdata[eventCount].setTime(datetime.substring(8,14));
                eventdata[eventCount].setKind("STS");
                eventdata[eventCount].setFlag(2);
                eventdata[eventCount++].setMsg("EEPROM ERROR");
            }
            if(ms.getRAM_ERROR()){
                eventdata[eventCount] = new EventLogData();
                eventdata[eventCount].setDate(datetime.substring(0,8));
                eventdata[eventCount].setTime(datetime.substring(8,14));
                eventdata[eventCount].setKind("STS");
                eventdata[eventCount].setFlag(3);
                eventdata[eventCount++].setMsg("RAM ERROR");
            }
            if(ms.getBATTERY_MISSING()){
                eventdata[eventCount] = new EventLogData();
                eventdata[eventCount].setDate(datetime.substring(0,8));
                eventdata[eventCount].setTime(datetime.substring(8,14));
                eventdata[eventCount].setKind("STS");
                eventdata[eventCount].setFlag(4);
                eventdata[eventCount++].setMsg("BATTERY MISSING");
            }
            if(ms.getLOW_BATTERY()){
                eventdata[eventCount] = new EventLogData();
                eventdata[eventCount].setDate(datetime.substring(0,8));
                eventdata[eventCount].setTime(datetime.substring(8,14));
                eventdata[eventCount].setKind("STS");
                eventdata[eventCount].setFlag(5);
                eventdata[eventCount++].setMsg("LOW BATTERY");
            }
            if(ms.getUN_PROGRAMMED()){
                eventdata[eventCount] = new EventLogData();
                eventdata[eventCount].setDate(datetime.substring(0,8));
                eventdata[eventCount].setTime(datetime.substring(8,14));
                eventdata[eventCount].setKind("STS");
                eventdata[eventCount].setFlag(6);
                eventdata[eventCount++].setMsg("UN PROGRAMMED");
            }
            if(ms.getDEMAND_OVERFLOW()){
                eventdata[eventCount] = new EventLogData();
                eventdata[eventCount].setDate(datetime.substring(0,8));
                eventdata[eventCount].setTime(datetime.substring(8,14));
                eventdata[eventCount].setKind("STS");
                eventdata[eventCount].setFlag(7);
                eventdata[eventCount++].setMsg("DEMAND OVERFLOW");
            }
            if(ms.getPOWER_OUTAGE()){
                eventdata[eventCount] = new EventLogData();
                eventdata[eventCount].setDate(datetime.substring(0,8));
                eventdata[eventCount].setTime(datetime.substring(8,14));
                eventdata[eventCount].setKind("STS");
                eventdata[eventCount].setFlag(8);
                eventdata[eventCount++].setMsg("POWER OUTAGE");
            }
            if(ms.getMAX_DEMAND()){
                eventdata[eventCount] = new EventLogData();
                eventdata[eventCount].setDate(datetime.substring(0,8));
                eventdata[eventCount].setTime(datetime.substring(8,14));
                eventdata[eventCount].setKind("STS");
                eventdata[eventCount].setFlag(9);
                eventdata[eventCount++].setMsg("MAX DEMAND");
            }
            if(ms.getDEMAND_RESET()){
                eventdata[eventCount] = new EventLogData();
                eventdata[eventCount].setDate(datetime.substring(0,8));
                eventdata[eventCount].setTime(datetime.substring(8,14));
                eventdata[eventCount].setKind("STS");
                eventdata[eventCount].setFlag(10);
                eventdata[eventCount++].setMsg("DEMAND RESET");
            }
            if(ms.getTIME_CHANGED()){
                eventdata[eventCount] = new EventLogData();
                eventdata[eventCount].setDate(datetime.substring(0,8));
                eventdata[eventCount].setTime(datetime.substring(8,14));
                eventdata[eventCount].setKind("STS");
                eventdata[eventCount].setFlag(11);
                eventdata[eventCount++].setMsg("TIME CHANGED");
            }
            if(ms.getPROGRAM_CHANGED()){
                eventdata[eventCount] = new EventLogData();
                eventdata[eventCount].setDate(datetime.substring(0,8));
                eventdata[eventCount].setTime(datetime.substring(8,14));
                eventdata[eventCount].setKind("STS");
                eventdata[eventCount].setFlag(12);
                eventdata[eventCount++].setMsg("PROGRAM CHANGED");
            }
           
            if(eventCount>0){
                EventLogData[] ev = new EventLogData[eventCount];
                System.arraycopy(eventdata, 0, ev, 0, eventCount);
                eventdata = ev;
            }
            log.debug("LGRW3410_EV eventCount : "+eventCount);
        } catch (Exception e) {
        	log.error("LGRW3410_EV parseEvent error",e);
        }
    }
}
