/** 
 * @(#)MT53.java       1.0 05/07/25 *
 * 
 * Sag Log Class.
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.a3rlnqTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.util.DateTimeUtil;


/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class MT53 {
	
	private final int OFS_STATUS           = 0;
	private final int OFS_SEQ_NUM          = 1;
	private final int OFS_NUM_VALID_ENTRY  = 5; 
	private final int OFS_NUM_UNREAD_ENTRY = 7;
	private final int OFS_SAGLOGDATA       = 9;
	
	private final int LEN_STATUS           = 1; 
	private final int LEN_SEQ_NUM          = 4;
	private final int LEN_NUM_VALID_ENTRY  = 2;
	private final int LEN_NUM_UNREAD_ENTRY = 2;
	
	private final int LEN_SAG_TIME         = 6;
	
	
	private byte[] data;

    private static Log logger = LogFactory.getLog(MT53.class);
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MT53(byte[] data) {
		this.data = data;
	}
	
	public int getNUM_VALID_ENTRY() throws Exception {
		return DataFormat.hex2unsigned16(
				DataFormat.LSB2MSB(
					DataFormat.select(
						data,OFS_NUM_VALID_ENTRY,LEN_NUM_VALID_ENTRY)));
	}


	public byte[] parseSagLog() {
		
		int ofs = OFS_SAGLOGDATA;
		int sagcnt;
		try {
			
			sagcnt = getNUM_VALID_ENTRY();

			byte[] temp = new byte[sagcnt*9];
			int pos = 0;
			
			for(int i = 0; i < sagcnt; i++){

				byte sag_phase = data[ofs++];

				byte[] sagtime = parseYyyymmddhhmmss(data,ofs,LEN_SAG_TIME);
				ofs += LEN_SAG_TIME;
				
				//if(sag_phase != 0xFF){//0xFF means event cleared.
				System.arraycopy(sagtime,0,temp,pos,sagtime.length);
				pos += sagtime.length;
				temp[pos++] = 0x01;
				temp[pos++] = sag_phase;
				//}
			}

			byte[] sagevent = new byte[pos];
			System.arraycopy(temp,0,sagevent,0,pos);
		
			return sagevent;
		
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
		return null;

	}
	
	
	private byte[] parseYyyymmddhhmmss(byte[] b, int offset, int len)
						throws Exception {

		byte[] datetime = new byte[7];
		
		int blen = data.length;
		if(blen-offset < 6)
			throw new Exception("YYMMDDHHMMSS FORMAT ERROR : "+(blen-offset));
		if(len != 6)
			throw new Exception("YYMMDDHHMMSS LEN ERROR : "+len);
		
		int idx = offset;
		int yy = DataFormat.hex2unsigned8(b[idx++]);
		int mm = DataFormat.hex2unsigned8(b[idx++]);
		int dd = DataFormat.hex2unsigned8(b[idx++]);
		int hh = DataFormat.hex2unsigned8(b[idx++]);
		int MM = DataFormat.hex2unsigned8(b[idx++]);
		int ss = DataFormat.hex2unsigned8(b[idx++]);
		
		int currcen = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;
	
		int year   = yy;
		if(year != 0){
			year = yy + currcen;
		}

		datetime[0] = (byte)((year >> 8) & 0xff);
		datetime[1] = (byte)(year & 0xff);
		datetime[2] = (byte) MM;
		datetime[3] = (byte) dd;
		datetime[4] = (byte) hh;
		datetime[5] = (byte) mm;
		datetime[6] = (byte) ss;
		
		return datetime;
		
	}


}
