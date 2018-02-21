/** 
 * @(#)LastOutageTime.java       1.0 07/11/14 *
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
 
package com.aimir.fep.meter.parser.lk3410cpTable;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.util.DataFormat;

/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class LastOutageTime {
	
    public static final int LEN_EVENT_TIME = 7;
    
    public static final int OFS_EVENT_TIME = 1;
    
    
	private byte[] data;
    private int ofs;
    private int eventValue;//eventvalue
       
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public LastOutageTime(byte[] data, int ofs) {
		this.data = data;
        this.ofs = ofs;
	}
	
    /**
     * DEMAND_RESET_LOG
     */
    public EventLogData[] getLastOutageTime() throws Exception  {
        
        EventLogData[] eventdata = new EventLogData[2];
        eventValue =25;
        for(int i=0; i<2; i++){
            String datetime = new DateTimeFormat(DataFormat.select(data, ofs, LEN_EVENT_TIME)).getDateTime();
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
            eventValue++;
        }
       
        return eventdata;
    }
    
}
