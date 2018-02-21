/** 
 * @(#)PowerEvent.java       1.0 08/04/16 *
 * 
 * Actual Dimension Register Table.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
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
import com.aimir.fep.util.DataFormat;

/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class PowerEvent implements java.io.Serializable{

	private static final long serialVersionUID = 324177026175447980L;

	public static final int LEN_EVENT_TIME = 7;
    
	private byte[] data;
    private int ofs;
    private int eventValue;//eventvalue
       
    private static Log log = LogFactory.getLog(PowerEvent.class);
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public PowerEvent(byte[] data, int ofs, int eventValue) {
		this.data = data;
        this.ofs = ofs;
        this.eventValue = eventValue;
	}
	
    /**
     * Power Outage, Recovery
     */
    public EventLogData[] getPowerEvent() throws Exception  {
        
        int cnt = data.length/(LEN_EVENT_TIME*2);
        ofs+=0;
        EventLogData[] eventdata = new EventLogData[cnt];
        
        for(int i=0; i<cnt; i++){
            
            String datetime = new DateTimeFormat(DataFormat.select(data, ofs, LEN_EVENT_TIME)).getDateTime();
            log.debug("eventValue :"+eventValue+" datetime :"+datetime);
            eventdata[i] = new EventLogData();
            eventdata[i].setDate(datetime.substring(0,8));
            eventdata[i].setTime(datetime.substring(8,14));
            eventdata[i].setKind("STE");
            eventdata[i].setFlag(eventValue);
            if(eventValue == 25){
                eventdata[i].setMsg("Power Failure");
            }else if(eventValue == 26){
                eventdata[i].setMsg("Power Restore");
            }
            ofs+=LEN_EVENT_TIME;
        }
        if (eventdata.length <1)
            return null;
        else
            return eventdata;
    }
}
