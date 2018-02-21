/** 
 * @(#)Class1_1.java       1.0 04/10/19 *
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
package com.aimir.fep.meter.parser.sl7000Table;

import com.aimir.fep.util.DataFormat;

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 * 
 * ACRATIS SL7000 meter Class. <p> 
 * And CT_VT_RATIO Information. <p>
 * 
 */
public class Class1_1 {

	public final static int OFS_CTRATIO_NUM = 21;	// offset current transformer ratio numerator.
	public final static int OFS_CTRATIO_DNM = 24;	// offset current transformer ratio denominator.
	public final static int OFS_VTRATIO_NUM = 26;	// offset voltage transformer ratio numerator.
	public final static int OFS_VTRATIO_DNM = 31;	// offset voltage transformer ratio denominator.

	public final static int LEN_CTRATIO_NUM = 2;	// length current transformer ratio numerator.
	public final static int LEN_CTRATIO_DNM = 1;	// length current transformer ratio denominator.
	public final static int LEN_VTRATIO_NUM = 4;	// length voltage transformer ratio numerator.
	public final static int LEN_VTRATIO_DNM = 2;	// length voltage transformer ratio denominator.

	private byte[] data;
	
	/**
	 * Constructor 
	 * 
	 * @param data - read data
	 */
	public Class1_1(byte[] data) {
		this.data = data;
	}
	
	/**
	 * Get CT Ratio (Current Transformer Ratio). <p>
	 * @return
	 */
	public byte[] parseCTRATIO(){
		
		int ct_ratio_numerator   = 0;
		int ct_ratio_denominator = 0;
		int ct_ratio = 0;
		
		/* length 4 */
		byte[] val = {0x00,0x00,0x00,0x00};
		
		ct_ratio_numerator   = DataFormat.hex2unsigned8(data[OFS_CTRATIO_NUM]);
		ct_ratio_numerator   = ct_ratio_numerator << 8;
		ct_ratio_numerator  |= DataFormat.hex2unsigned8(data[OFS_CTRATIO_NUM+1]);
		ct_ratio_denominator = DataFormat.hex2unsigned8(data[OFS_CTRATIO_DNM]);					
		ct_ratio = ct_ratio_numerator / ct_ratio_denominator;
					
		val[2] = (byte)((ct_ratio >> 8) & 0xff);
		val[3] = (byte)(ct_ratio & 0xff);
		
		return val;
	}

	/**
	 * Get VT Ratio (Voltage Transformer Ratio). <p>
	 * @return
	 */
	public byte[] parseVTRATIO() {
		
		int vt_ratio_numerator   = 0;
		int vt_ratio_denominator = 0;
		int vt_ratio = 0;
		
		/* length 4 */
		byte[] val = {0x00,0x00,0x00,0x00};
		
		vt_ratio_numerator = DataFormat.hex2unsigned8(data[OFS_VTRATIO_NUM]);
		vt_ratio_numerator <<= 8;
		vt_ratio_numerator |= DataFormat.hex2unsigned8(data[OFS_VTRATIO_NUM+1]);
		vt_ratio_numerator <<= 8;
		vt_ratio_numerator |= DataFormat.hex2unsigned8(data[OFS_VTRATIO_NUM+2]);
		vt_ratio_numerator <<= 8;
		vt_ratio_numerator |= DataFormat.hex2unsigned8(data[OFS_VTRATIO_NUM+3]);
		vt_ratio_denominator = DataFormat.hex2unsigned8(data[OFS_VTRATIO_DNM]);
		vt_ratio_denominator <<= 8;
		vt_ratio_denominator |= DataFormat.hex2unsigned8(data[OFS_VTRATIO_DNM+1]);
						
		vt_ratio = vt_ratio_numerator / vt_ratio_denominator;

		val[0] = (byte)((vt_ratio >> 24) & 0xff);
		val[1] = (byte)((vt_ratio >> 16) & 0xff);
		val[2] = (byte)((vt_ratio >> 8)  & 0xff);
		val[3] = (byte)( vt_ratio & 0xff);		
		
		return val;
	}

}
