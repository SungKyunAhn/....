/** 
 * @(#)Class52.java       1.0 05/02/18 *
 * 
 * PowerTools System Voltage Flicker Data Class.
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


/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Class52 {

    private static Log logger = LogFactory.getLog(Class52.class);
	
	private int OFS_CUMSAGCOUNT_A = 9;
	private int OFS_CUMSAGCOUNT_B = 11;
	private int OFS_CUMSAGCOUNT_C = 13;
	
	private int OFS_CUMSAGDURL_A  = 15;
	private int OFS_CUMSAGDURL_B  = 17;
	private int OFS_CUMSAGDURL_C  = 19;
	private int OFS_CUMSAGDURH_A  = 21;
	private int OFS_CUMSAGDURH_B  = 23;
	private int OFS_CUMSAGDURH_C  = 25;
	
	private byte[] data;
	
	public Class52(byte[] data){
		this.data = data;
	}
	
	
	/**
	 * Cumulative number of voltage sags PHASE A.
	 * @return - 2 byte hex data
	 * @throws Exception
	 */
	public byte[] parseCumSagCntA() throws Exception {
		return DataFormat.select(data,OFS_CUMSAGCOUNT_A,2);
	}
	
	/**
	 * Cumulative number of voltage sags PHASE B.
	 * @return - 2 byte hex data
	 * @throws Exception
	 */
	public byte[] parseCumSagCntB() throws Exception {
		return DataFormat.select(data,OFS_CUMSAGCOUNT_B,2);
	}
	
	/**
	 * Cumulative number of voltage sags PHASE C.
	 * @return - 2 byte hex data
	 * @throws Exception
	 */
	public byte[] parseCumSagCntC() throws Exception {
		return DataFormat.select(data,OFS_CUMSAGCOUNT_C,2);
	}
	
	
	/**
	 * Cumulative duration on PHASE A (milliseconds)
	 * @return - 8 byte hex data
	 * @throws Exception
	 */
	public byte[] parseCumSagDurA() throws Exception {
		
		long duration = 0;
		
		duration = 
		    (long)(DataFormat.hex2dec(data,OFS_CUMSAGDURL_A,2)*7.8125)
		  + (long)(DataFormat.hex2dec(data,OFS_CUMSAGDURH_A,2)*512*1000);
		return DataFormat.dec2hex(duration);
	}
	
	/**
	 * Cumulative duration on PHASE B (milliseconds)
	 * @return - 8 byte hex data
	 * @throws Exception
	 */
	public byte[] parseCumSagDurB() throws Exception {
		
		long duration = 0;
		
		duration =
		    (long)(DataFormat.hex2dec(data,OFS_CUMSAGDURL_B,2)*7.8125)
		  + (long)(DataFormat.hex2dec(data,OFS_CUMSAGDURH_B,2)*512*1000);
		return DataFormat.dec2hex(duration);
	}
	
	/**
	 * Cumulative duration on PHASE A (milliseconds)
	 * @return - 8 byte hex data
	 * @throws Exception
	 */
	public byte[] parseCumSagDurC() throws Exception {
		
		long duration = 0;
		
		duration =
		    (long)(DataFormat.hex2dec(data,OFS_CUMSAGDURL_C,2)*7.8125)
		  + (long)(DataFormat.hex2dec(data,OFS_CUMSAGDURH_C,2)*512*1000);
		return DataFormat.dec2hex(duration);
	}
	
}
