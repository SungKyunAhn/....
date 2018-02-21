/* 
 * @(#)PstnCommStat.java       1.0 09/07/7 *
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
 
package com.aimir.fep.meter.parser.pstnErrorTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.PSTNLogData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * @author Kang Soyi ksoyi@nuritelecom.com
 */
public class PstnCommStat {
    
	public static final int OFS_PROTOCOL_TYPE = 0;
    public static final int OFS_OPERATION_DATETIME = 1;
    public static final int OFS_PORT_NUMBER = 29;
    public static final int OFS_CALL_DATETIME = 30;
    public static final int OFS_PHONE_NUMBER = 37;
    public static final int OFS_CALL_RESULT = 57;
    public static final int OFS_CALL_RESULT_DATETIME = 58;
    
	public static final int LEN_PROTOCOL_TYPE = 1;
    public static final int LEN_OPERATION_DATETIME = 7;
    public static final int LEN_PORT_NUMBER = 1;
    public static final int LEN_CALL_DATETIME = 7;
    public static final int LEN_PHONE_NUMBER = 20;
    public static final int LEN_CALL_RESULT = 1;
    public static final int LEN_CALL_RESULT_DATETIME = 7;
    
    private com.aimir.fep.meter.data.PSTNLogData commLog;
	private byte[] rawData = null;
    
    private Log log = LogFactory.getLog(PstnCommStat.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public PstnCommStat(byte[] rawData) {
		
		this.rawData = rawData;
		parseLog();
	}

    public PSTNLogData getData() {
        return this.commLog;
    }
    
    private void parseLog(){
    	
        try{
        	commLog = new PSTNLogData();
        	
        	commLog.setProtocolType(""+DataFormat.hex2unsigned8(rawData[OFS_PROTOCOL_TYPE]));
        	
        	if(commLog.getProtocolType().equals("0")){ //lan
        		commLog = null;
        		log.info("LAN type.");
        	}
        	else{
	        	commLog.setOperationDateTime(getDateTime(DataFormat.select(rawData,OFS_OPERATION_DATETIME,LEN_OPERATION_DATETIME))); //////
	        	commLog.setPortNo(""+DataFormat.hex2unsigned8(rawData[OFS_PORT_NUMBER]));
	        	commLog.setCallDateTime(getDateTime(DataFormat.select(rawData,OFS_CALL_DATETIME,LEN_CALL_DATETIME)));
	        	commLog.setPhoneNumber(new String(DataFormat.select(rawData,OFS_PHONE_NUMBER,LEN_PHONE_NUMBER)).trim());        
	        	commLog.setCallResult(""+DataFormat.hex2unsigned8(rawData[OFS_CALL_RESULT]));
	        	if(commLog.getCallResult().equals("0"))
	        		commLog.setCallResultDateTime(getDateTime(DataFormat.select(rawData,OFS_CALL_RESULT_DATETIME,LEN_CALL_RESULT_DATETIME)));
        	}
        }catch(Exception e){
        	log.error("patn LOG PARSING ERROR :"+ e,e);
        }
    }    

	public String getDateTime(byte[] date)throws Exception{
    	int blen = date.length;
		if(blen <7)
			throw new Exception("YYYYMMDD LEN ERROR : "+blen);
		
		int idx = 0;
		
		int year = DataFormat.hex2unsigned16(DataFormat.LSB2MSB(DataFormat.select(date, 0, 2)));
        idx =idx+2;
		int mm = DataFormat.hex2unsigned8(date[idx++]);
		int dd = DataFormat.hex2unsigned8(date[idx++]);		

		int hh = 0;
		int MM = 0;
		int ss =0;
		
		hh = DataFormat.hex2unsigned8(date[idx++]);
		MM = DataFormat.hex2unsigned8(date[idx++]);
		ss = DataFormat.hex2unsigned8(date[idx++]);

		StringBuffer ret = new StringBuffer();
		
		ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
		ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
		
		ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(ss),2));

		return ret.toString();
    }
	
}
