/** 
 * @(#)MT075.java       1.0 06/06/12 *
 * 
 * Scale Factors Table Class.
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
 * ex)
 * V_SQR_HR_LN_SF : 35 0c 
 * V_RMS_SF       : d4 30 
 * I_N_SQR_SF     : 40 1f 00 
 * VA_SF          : fa 00 00 
 * VAH_SF         : fa 00 
 */
/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class MT075 {
	
	public static final int OFS_V_SQR_HR_LN_SF = 0;
	public static final int OFS_V_SQR_HR_LL_SF = 2;
	public static final int OFS_I_SQR_HR_SF    = 4;
	public static final int OFS_I_N_SQR_SF     = 7;
	public static final int OFS_VA_SF          = 10;
	public static final int OFS_VAH_SF         = 13;
	
	public static final int LEN_V_SQR_HR_LN_SF = 2;
	public static final int LEN_V_SQR_HR_LL_SF = 2;
	public static final int LEN_I_SQR_HR_SF    = 3;
	public static final int LEN_I_N_SQR_SF     = 3;
	public static final int LEN_VA_SF          = 3;
	public static final int LEN_VAH_SF         = 2;
	
	private byte[] data;
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MT075(byte[] data) {
		this.data = data;
	}

	/**
	 * This scale factor applies to line-to-neutral voltages.
	 */
	public int getV_SQR_HR_LN_SF() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_V_SQR_HR_LN_SF,LEN_V_SQR_HR_LN_SF)));
	}
	
	/**
	 * This scale factor applies to line-to-line voltages.
	 */
	public int getV_SQR_HR_LL_SF() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_V_SQR_HR_LL_SF,LEN_V_SQR_HR_LL_SF)));
	}
	
	/**
	 * Current scale factor.
	 */
	public int getI_SQR_HR_SF() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_I_SQR_HR_SF,LEN_I_SQR_HR_SF)));
	}
	
	/**
	 * Neutral current scale factor.
	 */
	public int getI_N_SQR_SF() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_I_N_SQR_SF,LEN_I_N_SQR_SF)));
	}
	
	public int getVA_SF() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_VA_SF,LEN_VA_SF)));
	}
	
	public int getVAH_SF() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_VAH_SF,LEN_VAH_SF)));
	}

}
