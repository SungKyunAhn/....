/** 
 * @(#)MT067.java       1.0 06/12/14 *
 * 
 * Meter Program Constants 2 Table Class.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.kV2cTable;

import com.aimir.fep.util.DataFormat;

/**
00 40 94 52 a3 03 
00 40 94 52 a3 03 
00 40 
94 52 
a3 03 
00 40 94 52 a3 03 
00 40 94 52 a3 03 
00 40 94 52 a3 03 
00 40 
94 52 
a3 03 00 40 
94 52 a3 03 
00 40 94 52 a3 03 00 40 94 52 a3 03 00 40 94 52 a3 03 00 40 94 52 a3 03 
00 40 94 52 a3 03 00 40 94 52 a3 03 00 40 94 52 a3 03 00 40 94 52 a3 03 
00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 
00 40 94 52 a3 03 00 40 94 52 a3 03 00 40 94 52 a3 03 00 00 00 00 00 00 
00 00 00 00 00 00 0a 00 0a 00 01 00 30 30 30 30 30 30 30 30 30 30 30 30 
ff ff ff ff ff ff 3c 00 1e 00 00 00 00 00 00 20 20 20 
 */

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class MT067 {
	
	public static final int OFS_ENERGY_WRAPTEST_CONST   = 0;
	public static final int OFS_RESERVED1               = 12;
	public static final int OFS_DEMAND_WRAPTEST_CONST   = 120;
	public static final int OFS_RESERVED2               = 132;
	public static final int OFS_CUR_TRANS_RATIO         = 150;
	public static final int OFS_POT_TRANS_RATIO         = 152;
	public static final int OFS_PROGRAM_ID              = 154;
	public static final int OFS_USER_DEFINED_FIELD1     = 156;
	public static final int OFS_USER_DEFINED_FIELD2     = 162;
	public static final int OFS_USER_DEFINED_FIELD3     = 168;
	public static final int OFS_P_FAIL_RECOGNITION_TIME = 174;
	public static final int OFS_LP_OUTAGE_DURATION      = 176;
	public static final int OFS_DEMAND_RESET_TIMEOUT    = 178;
	public static final int OFS_DEFAULT_TOU_RATE        = 182;
	public static final int OFS_RESERVED3               = 183;
	
	public static final int LEN_ENERGY_WRAPTEST_CONST   = 12;
	public static final int LEN_RESERVED1               = 108;
	public static final int LEN_DEMAND_WRAPTEST_CONST   = 12;
	public static final int LEN_RESERVED2               = 18;
	public static final int LEN_CUR_TRANS_RATIO         = 2;
	public static final int LEN_POT_TRANS_RATIO         = 2;
	public static final int LEN_PROGRAM_ID              = 2;
	public static final int LEN_USER_DEFINED_FIELD1     = 6;
	public static final int LEN_USER_DEFINED_FIELD2     = 6;
	public static final int LEN_USER_DEFINED_FIELD3     = 6;
	public static final int LEN_P_FAIL_RECOGNITION_TIME = 2;
	public static final int LEN_LP_OUTAGE_DURATION      = 2;
	public static final int LEN_DEMAND_RESET_TIMEOUT    = 4;
	public static final int LEN_DEFAULT_TOU_RATE        = 1;
	public static final int LEN_RESERVED3               = 3;
	
	private byte[] data;
	
	/**
	 * Constructor .<p>
	 */
	public MT067(byte[] data) {
		this.data = data;
	}

	public int getCUR_TRANS_RATIO() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_CUR_TRANS_RATIO,LEN_CUR_TRANS_RATIO)));
	}
	
	public int getPOT_TRANS_RATIO() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_POT_TRANS_RATIO,LEN_POT_TRANS_RATIO)));
	}
	
	public byte[] parseCUR_TRANS_RATIO() throws Exception {
		return DataFormat.dec2hex(
			DataFormat.hex2dec(
				DataFormat.LSB2MSB(
					DataFormat.select(
						data,OFS_CUR_TRANS_RATIO,LEN_CUR_TRANS_RATIO))));
	}
	
	public byte[] parsePOT_TRANS_RATIO() throws Exception {
		return DataFormat.dec2hex(
			DataFormat.hex2dec(
				DataFormat.LSB2MSB(
					DataFormat.select(
						data,OFS_POT_TRANS_RATIO,LEN_POT_TRANS_RATIO))));
	}


}
