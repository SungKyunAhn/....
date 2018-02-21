/** 
 * @(#)ST003.java       1.0 06/06/01 *
 * 
 * End Device Mode and Meter Status Class.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.kV2cTable;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * 01
 * STD_STATUS1 : 00 00
 * STD_STATUS2 : 00
 * MFG_STATUS  : 10 
 */
/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class ST003 {

    private static Log log = LogFactory.getLog(ST003.class);

	public static final int OFS_STD_STATUS1 = 1;
	public static final int LEN_STD_STATUS1 = 2;
	
	public static final int OFS_STD_STATUS2 = 3;
	public static final int LEN_STD_STATUS2 = 1;
	
	public static final int OFS_MFG_STATUS  = 4;
	public static final int LEN_MFG_STATUS  = 1;
	
    private byte[] data;
    private String meterTime;
    
    private static final String[] STATUS_BIT_TRUE = 
    {
        "<dt>Device is not in metering mode</dt>",
        "<dt>Device is not in test mode</dt>",
        "",
        "",
        "",
        "",
        "",
        "",
        
        "<dt>Device is unprogrammed</dt>",
        "",
        "",
        "<dt>Device did detect a RAM error</dt>",
        "<dt>Device did detect a ROM error</dt>",
        "<dt>Device did detect a NVRAM error</dt>",
        "<dt>Device did detect a clock error</dt>",
        "<dt>Device did detect a bad voltage</dt>",
        
        "<dt>low battery</dt>",
        "<dt>Device did detect potential below predetermined value</dt>",
        "<dt>Device did detect a demand threshold overload</dt>",
        "",
        "",
        "",
        "",
        "",
        
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        
        "<dt>DSP Error</dt>",
        "",
        "<dt>Device did detect expiration of the watchdog timer.</dt>",
        "<dt>Device did detect received kWh.</dt>",
        "<dt>Device did detect leading kvarh.</dt>",
        "<dt>Device was interrupted during programming; the new program was lost.</dt>",
        "<dt>Device did detect an error in the code section of flash memory.</dt>",
        "<dt>Device did detect a checksum error in the data section of flash.</dt>"
    };
    
    private static final String[] STATUS_BIT_FALSE = 
    {
        "<dt>Device is in metering mode</dt>",
        "<dt>Device is in test mode</dt>",
        "",
        "",
        "",
        "",
        "",
        "",

        "<dt>Device is programmed</dt>",
        "",
        "",
        "",
        "",
        "",
        "",
        "",

        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",

        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",

        "",
        "",
        "",
        "",
        "",
        "",
        "",
        ""                             
    };

    /**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
    public ST003(byte[] data, String meterTime) {
		this.data = data;
	}
	
    List<EventLogData> eventlist = null;
    public EventLogData[] getEventLog(){
        if(eventlist != null && eventlist.size() > 0){
            EventLogData[] evlog = null;
            Object[] obj = eventlist.toArray();
            
            evlog = new EventLogData[obj.length];
            for(int i = 0; i < obj.length; i++){
                evlog[i] = (EventLogData)obj[i];
            }
            return evlog;
        }
        else{
            return null;
        }
    }
    
	public String getMeterLog(){

		StringBuffer sb = new StringBuffer();
		StringBuffer msg = new StringBuffer();
		
		try{
            byte[] _status = DataFormat.select(data,0, 5);
			for(int i = 0; i < _status.length; i++){
				int convertInt = DataFormat.hex2dec(_status, i, 1);
				String convertStr 
					= Util.frontAppendNStr('0',Integer.toBinaryString(convertInt),8);
				sb.append(convertStr);
			}
						
			for(int i = 0; i < STATUS_BIT_TRUE.length;i++){
				if(sb.charAt(i) == '1'){
				    if (!STATUS_BIT_TRUE[i].equals("")) {
                        status = MeterStatus.Abnormal;
                        msg.append(STATUS_BIT_TRUE[i]);
                        int flag = ((i+1)/8)*10+(i%8);
                        String kind = "STS";//standard
                        if(flag >= 40 ) 
                            kind = "MFS";//manufacture                        
                        makeEventObject(meterTime,kind,flag);
                    }
				}else{
                    msg.append(STATUS_BIT_FALSE[i]);
                }
			}
		}catch(Exception e){
			log.warn(e.getMessage());
		}
		
		return msg.toString();
	}

	private MeterStatus status;
    public MeterStatus getStatus() {
        return status;
    }
    
    private void makeEventObject(String datetime, String kind, int flag){
        EventLogData eventlog = new EventLogData();
        eventlog.setDate(datetime.substring(0,8));
        eventlog.setTime(datetime.substring(8,14));
        eventlog.setKind(kind);
        eventlog.setFlag(flag);
        eventlist.add(eventlog);
    }
}
