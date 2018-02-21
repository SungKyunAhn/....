/* 
 * @(#)A2RL_EV.java       1.0 08/10/27 *
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
 
package com.aimir.fep.meter.parser.a2rlTable;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.util.DataFormat;
import com.aimir.util.DateTimeUtil;

/**
 * @author Kang Soyi ksoyi@nuritelecom.com
 */
public class A2RL_EV {
    
    public static final int OFS_NBR_EVT_LOG = 0; 
    public static final int OFS_EVT_LOG = 1;
    
    public static final int LEN_NBR_EVT_LOG = 1;

    public static final int OFS_EVT_KIND = 0;
    public static final int OFS_EVT_DATETIME = 1;
    
    public static final int LEN_EVT_KIND = 1;
    public static final int LEN_EVT_DATETIME = 6;
    
    //EVENT CODE
    public static final int EVENT_POWER_FAIL =0;
    public static final int EVENT_POWER_UP =1;
    
    public static final int EVENT_TIME_CHANGED_BEFORE =2;
    public static final int EVENT_TIME_CHANGED_AFTER =3;
    
    public static final int EVENT_TEST_MODE_STRT =4;
    public static final int EVENT_TEST_MODE_END =5;
    public static final int EVENT_DEMAND_RESET=6;
    public static final int EVENT_EVENTLOG_RESET = 255;
    
    private EventLogData[] eventdata;
    private int totalDemandCount;
    
	private byte[] rawData = null;
    
    private Log log = LogFactory.getLog(A2RL_EV.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public A2RL_EV(byte[] rawData) {
		
		this.rawData = rawData;
		parseEvent();
	}
    
    public EventLogData[] getEvent() {
        return this.eventdata;
    }
    
    public int getNBR_NBR_EVT_LOG(){
    	return DataFormat.hex2unsigned8(rawData[OFS_NBR_EVT_LOG]);
    }
    
    private void parseEvent(){
    	
    	int ofs = OFS_EVT_LOG;
        int cnt = getNBR_NBR_EVT_LOG();

        ArrayList<EventLogData> evList = new ArrayList<EventLogData>();
        
        try{
        for(int i=0; i<cnt; i++){
            
        	int eventcode = DataFormat.hex2unsigned8(rawData[ofs]);
        	ofs += LEN_EVT_KIND;
            String datetime = getDateTime(DataFormat.select(rawData, ofs, LEN_EVT_DATETIME));
            ofs += LEN_EVT_DATETIME;
            
            if(eventcode<=6){
	            String evtMessage = "";
	            String evtKind ="STS";
	            switch(eventcode){
	            case 0 :
	            	evtMessage ="Power Fail";
	            	evtKind="STE";
	            	break;
	            case 1 :
	            	evtMessage ="Power Up";
	            	evtKind="STE";
	            	break;
	            case 2 :
	            	evtMessage ="Time Changed - Before";
	            	break;
	            case 3 :
	            	evtMessage ="Time Changed - After";
	            	break;
	            case 4 :
	            	evtMessage ="Start of Test Mode";
	            	break;
	            case 5 :
	            	evtMessage ="End of Test Mode";
	            	break;
	            case 6 :
	            	evtMessage ="Demand Reset";
	            	break;
	            default :
	            	evtMessage ="";
	            	break;
	            }
	            
	            EventLogData ev = new EventLogData();
	            ev.setDate(datetime.substring(0,8));
	            ev.setTime(datetime.substring(8,14));
	            ev.setKind(evtKind);
	            ev.setFlag(eventcode);
	            ev.setMsg(evtMessage);
	            evList.add(ev);
            }
        }
        
        if(evList!=null)
        	log.debug("evList size() :"+ evList.size());
        else
        	log.debug("evList is null");
        
        if(evList != null && evList.size() > 0){
            Object[] obj = evList.toArray();            
            eventdata = new EventLogData[obj.length];

            System.arraycopy(obj, 0, eventdata, 0, obj.length);
            /*
            for(int i = 0; i < obj.length; i++){
            	eventdata[i] = (EventLogData)obj[i];
            }
            */
        }
        
        }catch(Exception e){
        	log.error("EVENT LOG PARSING ERROR :"+ e);
        }
    }
    
	public String getDateTime(byte[] datetime) throws Exception {

		String s =  DataFormat.bcd2str(datetime);	
		
		int curryy = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;
		
		int year   = Integer.parseInt(s.substring( 0,2));
		if(year != 0){
			year   = curryy+Integer.parseInt(s.substring( 0,2));
		}
		
		return ""+year+""+s.substring(2);
	}
	
}
