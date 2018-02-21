/** 
 * @(#)MT63.java       1.0 06/08/23 *
 * 
 * Instrumentation Profile Status.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.a1830rlnTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;


/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class MT63 implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7068544757881214026L;
	private final int OFS_SET1_STATUS_FLAG        = 0;
	private final int OFS_SET1_NBR_VALID_BLOCKS   = 1;
	private final int OFS_SET1_LAST_BLOCK_ELEMENT = 3;
	private final int OFS_SET1_LAST_BLOCK_SEQ_NUM = 5;
	private final int OFS_SET1_NBR_UNREAD_BLOCKS  = 9;
	private final int OFS_SET1_NBR_VALID_INT      = 11;
	private final int OFS_SET2_STATUS_FLAG        = 13;
	private final int OFS_SET2_NBR_VALID_BLOCKS   = 14;
	private final int OFS_SET2_LAST_BLOCK_ELEMENT = 16;
	private final int OFS_SET2_LAST_BLOCK_SEQ_NUM = 18;
	private final int OFS_SET2_NBR_UNREAD_BLOCKS  = 22;
	private final int OFS_SET2_NBR_VALID_INT      = 24;
	
	private final int LEN_SET1_STATUS_FLAG        = 1;
	private final int LEN_SET1_NBR_VALID_BLOCKS   = 2;
	private final int LEN_SET1_LAST_BLOCK_ELEMENT = 2;
	private final int LEN_SET1_LAST_BLOCK_SEQ_NUM = 4;
	private final int LEN_SET1_NBR_UNREAD_BLOCKS  = 2;
	private final int LEN_SET1_NBR_VALID_INT      = 2;
	private final int LEN_SET2_STATUS_FLAG        = 1;
	private final int LEN_SET2_NBR_VALID_BLOCKS   = 2;
	private final int LEN_SET2_LAST_BLOCK_ELEMENT = 2;
	private final int LEN_SET2_LAST_BLOCK_SEQ_NUM = 4;
	private final int LEN_SET2_NBR_UNREAD_BLOCKS  = 2;
	private final int LEN_SET2_NBR_VALID_INT      = 2;
	
	private byte[] data;

    private Log logger = LogFactory.getLog(getClass());
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MT63(byte[] data) {
		this.data = data;
	}
	
	public int getSET1_NBR_VALID_BLOCKS() throws Exception {
		return DataFormat.hex2unsigned16(
				DataFormat.LSB2MSB(
					DataFormat.select(
						data,OFS_SET1_NBR_VALID_BLOCKS,LEN_SET1_NBR_VALID_BLOCKS)));
	}
	
	public int getSET1_NBR_VALID_INT() {
		try {
			return DataFormat.hex2unsigned16(
					DataFormat.LSB2MSB(
						DataFormat.select(
							data,OFS_SET1_NBR_VALID_INT,LEN_SET1_NBR_VALID_INT)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public int getSET2_NBR_VALID_BLOCKS() throws Exception {
		return DataFormat.hex2unsigned16(
				DataFormat.LSB2MSB(
					DataFormat.select(
						data,OFS_SET2_NBR_VALID_BLOCKS,LEN_SET2_NBR_VALID_BLOCKS)));
	}
	
	public int getSET2_NBR_VALID_INT() {
		try {
			return DataFormat.hex2unsigned16(
					DataFormat.LSB2MSB(
						DataFormat.select(
							data,OFS_SET2_NBR_VALID_INT,LEN_SET2_NBR_VALID_INT)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
}
