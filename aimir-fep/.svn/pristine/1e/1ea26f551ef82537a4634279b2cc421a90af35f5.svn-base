/** 
 * @(#)MT51.java       1.0 05/07/25 *
 * 
 * PQM Actual Service Class.
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


/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class MT51 {
	
	private final int OFS_SVC_DEF = 5;
	private final int LEN_SVC_DEF = 2;
	
	private byte[] data;

    private static Log logger = LogFactory.getLog(MT51.class);
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MT51(byte[] data) {
		this.data = data;
	}
	
	/**
	 * Two Voltage:   0x00 (3P 3W)
	 * One Voltage:   0x40 (1P 2W)
	 * Three Voltage: 0x80 (3P 4W)
	 * 2 1/2 Voltage: 0xC0 (1P 3W)
	 * UNKNOWN: 0xFF
	 * @return
	 */
	public byte getMeterElement() {
		
		byte melement = (byte)0xFF;
		try{
			byte[] svc_def = DataFormat.LSB2MSB(
								DataFormat.select(
									data,OFS_SVC_DEF,LEN_SVC_DEF));
			int melements = (int)((svc_def[1] & 0x7F) >> 5);
			int wires = (int)((svc_def[1] & 0x07) >> 2);
			

			switch(melements){
				case 2:
					if(wires == 0)
						melement = 0x00;
					else
						melement = (byte)0x80;
					break;
				case 3:
					if(wires == 0)
						melement = (byte)0xC0;
					break;
				default:
					melement = (byte)0xFF;
			}
		}catch(Exception e){
			logger.warn(e.getMessage());
		}
		return melement;

	}

}
