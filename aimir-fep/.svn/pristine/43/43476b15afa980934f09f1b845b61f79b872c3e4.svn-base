/** 
 * @(#)Class53.java       1.0 05/02/18 *
 * 
 * Sag Event Log Class.
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */

package com.aimir.fep.meter.parser.a1rlqTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.util.DateTimeUtil;

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Class53 {

    private static Log logger = LogFactory.getLog(Class53.class);
	
	private byte[] data;
	private int count;
	private byte[] sagdata;
	private int ONE_BLOCK_SIZE = 7;
	private int MAX_BLOCK_COUNT = 40;
	
	
	public Class53(byte[] data){
		this.data = data;
		
		try {
			parseData();
		} catch (Exception e) {
			logger.warn(e);
		}
	}
	
	public int getEventCount(){
		return this.count;
	}
	public byte parseEventCount(){
		return (byte)this.count; 
	}
	
	public byte[] parseSagEventData(){
		return this.sagdata;
	}
	
	private void parseData() throws Exception {
		
		byte[] temp = new byte[40*9];
		int idx = 0;
		int i   = 0;
		for(; i < MAX_BLOCK_COUNT; i++){
			if(data[i*ONE_BLOCK_SIZE] == 0x00)
				break;

			byte  phase = data[i*ONE_BLOCK_SIZE];
			byte[] date = parseDate(i*ONE_BLOCK_SIZE+1,ONE_BLOCK_SIZE-1);

			System.arraycopy(date,0,temp,idx,date.length);
			idx += date.length;
			temp[idx++] = 0x01; //event type is voltage sag
			temp[idx++] = phase;
			
			this.count++;

		}

		if(idx < 10) {
			this.sagdata = new byte[0];
		}else{
			this.sagdata = new byte[idx];
			System.arraycopy(temp,0,sagdata,0,idx);
		}

	}
	
	
	private byte[] getPhase(byte b) throws Exception {
		
		byte[] temp = new byte[3];

		int idx = 0;
		if((b & 0x01) > 0 ) temp[idx++] = 'A';
		if((b & 0x02) > 0 ) temp[idx++] = 'B';
		if((b & 0x04) > 0 ) temp[idx++] = 'C';
		
		if(idx == 0) 
			throw new Exception("NO PHASE TYPE");
			
		byte[] phase = new byte[idx];
		System.arraycopy(temp,0,phase,0,idx);
		
		return phase;
	}
	
	
	/**
	 * Get Date 
	 * YYYY MM DD (4yte)
	 * @param start - start offset
	 * @param end   - end offset
	 * @return
	 */
	public byte[] parseDate(int start, int len) throws Exception {
		
		byte[] date = new byte[7];
		String s =  DataFormat.bcd2str(data,start,len);
		
		int curryy = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;
		int year   = Integer.parseInt(s.substring( 0,2));
		if(year != 0){
			year   = Integer.parseInt(s.substring( 0,2))+curryy;
		}
		int month  = Integer.parseInt(s.substring( 2, 4));
		int day    = Integer.parseInt(s.substring( 4, 6));
		int hh     = Integer.parseInt(s.substring( 6, 8));
		int mm     = Integer.parseInt(s.substring( 8,10));
		int ss     = Integer.parseInt(s.substring(10,12));
		
		date[0] = (byte) (year >> 8);
		date[1] = (byte) (0xff & year);
		date[2] = (byte) month;
		date[3] = (byte) day;
		date[4] = (byte) hh;
		date[5] = (byte) mm;
		date[6] = (byte) ss;
		
		return date;
	}
	
	
}
