/* 
 * @(#)DLMSKepco_EV.java       1.0 08/04/16 *
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
 
package com.aimir.fep.meter.parser.DLMSKepcoTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.util.Util;

/**
 * @author Kang Soyi ksoyi@nuritelecom.com
 */
public class DLMSKepco_EV implements java.io.Serializable{

	private static final long serialVersionUID = -8254968671936082762L;

	public static final int LEN_POWER_EVENT = 7;
        
    private EventLogData[] eventdata;

    private byte[] rawData = null;
    private byte[] meterStatus = null;
    
    private static Log log = LogFactory.getLog(DLMSKepco_EV.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public DLMSKepco_EV(byte[] rawData, byte[] meterStatus) {
		this.rawData = rawData;
		this.meterStatus = meterStatus;
        parseEvent();
	}
    
    public EventLogData[] getEvent() {
        return this.eventdata;
    }

    private void parseEvent(){
        
        try {
        	EventLogData[] ev = null;
        	EventLogData[] ev2 = new EventLogData[11];
        	
        	if(rawData!=null && rawData.length>0){
	            int ofs=0;
	            
	            int cntOfOutageEvent = (rawData.length)/(LEN_POWER_EVENT*2);
	            log.debug("totalEventCount :"+ cntOfOutageEvent);
	            //OUTAGE, Recovery
	            int eventValue = 25;
	            EventLogData[][] outageLog = new EventLogData[2][];
	            for (int i=0; i<2; i++){
	                PowerEvent outage = new PowerEvent(rawData, ofs, eventValue++);
	                outageLog[i] = outage.getPowerEvent();
	                
	                ofs += LEN_POWER_EVENT*cntOfOutageEvent;
	            }
	            log.debug(" POWER_EVENT ofs :"+ ofs);  
	            //TOTAL CONCAT
	            
	            if(cntOfOutageEvent>0){
	                ev = new EventLogData[cntOfOutageEvent*2];
	                int offs =0;
	                for(int i=0; i<outageLog.length; i++){
	                    if(outageLog[i]!=null && outageLog[i].length>0){
	                        System.arraycopy(outageLog[i], 0, ev, offs, outageLog[i].length);
	                        offs+= outageLog[i].length;
	                    }
	                }
	            }
        	}
        
            int eventCount=0;
            
        	if(meterStatus!=null && meterStatus.length>0){
        		MeterCautionFlag caution = new MeterCautionFlag(meterStatus[0]);
        		MeterErrorFlag error = new MeterErrorFlag(meterStatus[1]);
        		
                String datetime = Util.currYymmddHHMMss(); // event time == server's time
                if(caution.getNOT_PROGRAMMED()){
                    ev2[eventCount] = new EventLogData();
                    ev2[eventCount].setDate(datetime.substring(0,8));
                    ev2[eventCount].setTime(datetime.substring(8,14));
                    ev2[eventCount].setKind("STS");
                    ev2[eventCount].setFlag(1);
                    ev2[eventCount++].setMsg("NOT PROGRAMMED");
                }
                if(caution.getLOW_BATTERY()){
                	ev2[eventCount] = new EventLogData();
                	ev2[eventCount].setDate(datetime.substring(0,8));
                	ev2[eventCount].setTime(datetime.substring(8,14));
                	ev2[eventCount].setKind("STS");
                	ev2[eventCount].setFlag(2);
                	ev2[eventCount++].setMsg("LOW BATTERY");
                }
                if(caution.getBATTERY_MISSING()){
                	ev2[eventCount] = new EventLogData();
                	ev2[eventCount].setDate(datetime.substring(0,8));
                	ev2[eventCount].setTime(datetime.substring(8,14));
                	ev2[eventCount].setKind("STS");
                	ev2[eventCount].setFlag(3);
                	ev2[eventCount++].setMsg("BATTERY MISSING");
                }
                if(caution.getMEMORY_ERROR()){
                	ev2[eventCount] = new EventLogData();
                	ev2[eventCount].setDate(datetime.substring(0,8));
                	ev2[eventCount].setTime(datetime.substring(8,14));
                	ev2[eventCount].setKind("STS");
                	ev2[eventCount].setFlag(4);
                	ev2[eventCount++].setMsg("MEMORY ERROR");
                }
                if(caution.getRTC_RESET()){
                	ev2[eventCount] = new EventLogData();
                	ev2[eventCount].setDate(datetime.substring(0,8));
                	ev2[eventCount].setTime(datetime.substring(8,14));
                	ev2[eventCount].setKind("STS");
                	ev2[eventCount].setFlag(5);
                	ev2[eventCount++].setMsg("RTC RESET");
                }
                if(error.getCURRENT_CIRCUIT_DISCONNECT()){
                	ev2[eventCount] = new EventLogData();
                	ev2[eventCount].setDate(datetime.substring(0,8));
                	ev2[eventCount].setTime(datetime.substring(8,14));
                	ev2[eventCount].setKind("STS");
                	ev2[eventCount].setFlag(6);
                	ev2[eventCount++].setMsg("CURRENT CIRCUIT DISCONNECT");
                }
                if(error.getACTIVE_POWER_FLOW()){
                	ev2[eventCount] = new EventLogData();
                	ev2[eventCount].setDate(datetime.substring(0,8));
                	ev2[eventCount].setTime(datetime.substring(8,14));
                	ev2[eventCount].setKind("STS");
                	ev2[eventCount].setFlag(7);
                	ev2[eventCount++].setMsg("ACTIVE POWER FLOW");
                }
                if(error.getNEUTRAL_LINE_CONNECTION_ERROR()){
                	ev2[eventCount] = new EventLogData();
                	ev2[eventCount].setDate(datetime.substring(0,8));
                	ev2[eventCount].setTime(datetime.substring(8,14));
                	ev2[eventCount].setKind("STS");
                	ev2[eventCount].setFlag(8);
                	ev2[eventCount++].setMsg("NEUTRAL LINE CONNECTION ERROR");
                }
                if(error.getLOW_VOLTAGE_PHASE3()){
                	ev2[eventCount] = new EventLogData();
                	ev2[eventCount].setDate(datetime.substring(0,8));
                	ev2[eventCount].setTime(datetime.substring(8,14));
                	ev2[eventCount].setKind("STS");
                    ev2[eventCount].setFlag(9);
                    ev2[eventCount++].setMsg("LOW VOLTAGE PHASE3");
                }
                if(error.getLOW_VOLTAGE_PHASE2()){
                	ev2[eventCount] = new EventLogData();
                	ev2[eventCount].setDate(datetime.substring(0,8));
                    ev2[eventCount].setTime(datetime.substring(8,14));
                    ev2[eventCount].setKind("STS");
                    ev2[eventCount].setFlag(10);
                    ev2[eventCount++].setMsg("LOW VOLTAGE PHASE2");
                }
                if(error.getLOW_VOLTAGE_PHASE1()){
                	ev2[eventCount] = new EventLogData();
                	ev2[eventCount].setDate(datetime.substring(0,8));
                	ev2[eventCount].setTime(datetime.substring(8,14));
                	ev2[eventCount].setKind("STS");
                	ev2[eventCount].setFlag(11);
                	ev2[eventCount++].setMsg("LOW VOLTAGE PHASE1");
                }
        	}
            
            EventLogData[] event = null;
            int ofs =0;
            if(ev!=null && ev.length >0){
            	//eventCount +=ev.length;
            	event = new EventLogData[ev.length+eventCount];
            	System.arraycopy(ev, 0, event, ofs, ev.length);
            	ofs += ev.length;
            }
        	if(eventCount>0){
        		if(event==null){
        			event = new EventLogData[eventCount];
        		}
                 System.arraycopy(ev2, 0, event, ofs, eventCount);
            }
        	eventdata = event;
        	 
        } catch (Exception e) {
        	log.error("DLMSKepco_EV parseEvent error",e);
        }
    }

}
