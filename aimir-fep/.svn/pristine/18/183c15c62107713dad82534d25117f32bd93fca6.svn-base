/** 
 * @(#)ST005.java       1.0 06/12/14 *
 * 
 * Meter Serial Class.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.SM110Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * 
 * ex ) 
 *         : 20 20 30 30 30 30 30 30 30 30 30 30 
 * MSERIAL : 32 38 36 30 30 33 32 36 
 */

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class ST005 implements java.io.Serializable {

	private static final long serialVersionUID = 5307776341232220616L;
	public static final int OFS_MSERIAL = 0;
	public static final int LEN_MSERIAL = 20;
	
	private byte[] data;
    private static Log log = LogFactory.getLog(ST005.class);
    
    public ST005() {}
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST005(byte[] data) {
		this.data = data;
	}

	public byte[] parseMSerial() {
		
		byte[] b = new byte[]{0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30};
		
		try{
			b =  DataFormat.select(data,OFS_MSERIAL,LEN_MSERIAL);
		}catch(Exception e){
			log.warn("invalid meter id->"+e.getMessage());
		}
		return b;
	}

	public String getMSerial() {

		String mserial = "";

		try{
			mserial = new String(DataFormat.select(data,OFS_MSERIAL,LEN_MSERIAL)).trim();

		}catch(Exception e){
			log.warn("invalid meter id->"+e.getMessage());
		}

		return mserial;
	}

}
