/** 
 * @(#)Class2.java       1.0 04/09/16 *
 * 
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.a1rlTable;

import com.aimir.fep.util.DataFormat;

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Class2 {
	
	public static final int OFS_UMTRSN = 0;

	public static final int LEN_UMTRSN = 4;
	
	private byte[] data;
    
	public Class2(byte[] data) {
		this.data = data;
	}
	
	/**
	 * Meter Serial Number.
	 * @return
	 */
	public String parseUMTRSN() {
		
		String mserial = new String("00000000");
		try{
			mserial =  DataFormat.bcd2str(data,OFS_UMTRSN,LEN_UMTRSN);
			
			if(mserial == null || mserial.equals("")){
				mserial = "00000000";
			}
		}catch(Exception e){
			//logger.warn(e.getMessage());
		}
		return mserial;
	}

}
