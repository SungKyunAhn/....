/** 
 * @(#)Class14.java       1.0 04/09/16 *
 * 
 * Elster Load Profile Configuration Class.
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
public class Class14 {

	/* ------- CLASS 14 Offset -----------*/
	public static final int OFS_RLPSCAL = 3;	// Load Profile scaling register reload value.
	public static final int OFS_LPLEN   = 4;	// Load Profile interval length.
	public static final int OFS_DASIZE  = 5;	// Load Profile's one day's data size.
	public static final int OFS_LPMEM   = 7;	// Load Profile's Day count.
	public static final int OFS_CHANS   = 8;	// Load Profile's  Channel count. 
	public static final int OFS_IO01FLG = 9;	// The Kind of Channel 1 Load Profile.
	public static final int OFS_IO02FLG = 10;	// The Kind of Channel 2 Load Profile.
	public static final int OFS_IO03FLG = 11;	// The Kind of Channel 3 Load Profile.
	public static final int OFS_IO04FLG = 12;	// The Kind of Channel 4 Load Profile.
	
	/* -------- CLASS 14 LENGTH ----------*/
	public static final int LEN_RLPSCAL = 1;
	public static final int LEN_LPLEN   = 1;
	public static final int LEN_DASIZE  = 2;
	public static final int LEN_LPMEM   = 1;
	public static final int LEN_CHANS   = 1;
	public static final int LEN_IO01FLG = 1;
	public static final int LEN_IO02FLG = 1;
	public static final int LEN_IO03FLG = 1;
	public static final int LEN_IO04FLG = 1;
	
	private byte[] data;
	
	/**
	 * 
	 * Constructor
	 * @param data - read data (exclusion header,crch,crcl)
	 */
	public Class14(byte[] data){
		this.data = data;
	}
	
	
	/**
	 * Load profile scaling register reload value.
	 * 
	 * @return
	 */
	public int parseRLPSCAL() {
		return DataFormat.hex2signed8(data[OFS_RLPSCAL]);
	}
	
	
	/**
	 * 
	 * Load Profile interval length.
	 * 
	 * @return
	 */
	public int parseLPLEN() {
		return DataFormat.hex2unsigned8(data[OFS_LPLEN]);
	}
	
	/**
	 * 
	 * One day Load Profile data size.
	 * DASIZE = [(2880*CHANS)/LPLEN] + 6
	 * 
	 * If 2 channel and lp interval is 15 min,
	 * DASIZE = [(2880*2/15)]+6 = 390 byte.
	 * @return
	 */
	public int parseDASIZE() throws Exception {
		return DataFormat.hex2dec(data,OFS_DASIZE,LEN_DASIZE);
	}
	
	/**
	 * 
	 * Load Profile' Day count.
	 * LPMEM = [LPMEM-(EXSIZE*7)]/DASIZE
	 * @return
	 */
	public int parseLPMEM() {
		return DataFormat.hex2unsigned8(data[OFS_LPMEM]);
	}
	
	/**
	 * 
	 * Load Profile' Channel count.
	 * @return
	 */
	public int parseCHANS() {
		return DataFormat.hex2unsigned8(data[OFS_CHANS]);
	}
	
	/**
	 * 
	 * The Kind of channel 1 (Load Profile) 
	 *  ex)
	 * 0  = channel disabled
	 * 1  = kW delivered
	 * 2  = kW received
	 * 3  = reactive delivered
	 * 4  = reactive received
	 * 5  = quadrant 4
	 * 6  = quadrant 3
	 * 7  = quadrant 2
	 * 8  = quadrant 1
	 * 9  = TOU block 1
	 * 10 = TOU block 2
	 * @return
	 */
	public int parseIO01FLG() {
		return DataFormat.hex2unsigned8(data[OFS_IO01FLG]);
	}
	
	/**
	 * 
	 * The Kind of channel 2 (Load Profile) 
	 * same as IO01FLG.
	 * @return
	 */
	public int parseIO02FLG() throws Exception {
		return DataFormat.hex2dec(data,OFS_IO02FLG,LEN_IO02FLG);
	}
	
	/**
	 * 
	 * The Kind of channel 3 (Load Profile)
	 * same as IO01FLG.
	 * @return
	 */
	public int parseIO03FLG() throws Exception {
		return DataFormat.hex2dec(data,OFS_IO03FLG,LEN_IO03FLG);
	}
	
	/**
	 * 
	 * The Kind of channel 4 (Load Profile)
	 * same as IO01FLG.
	 * @return
	 */
	public int parseIO04FLG() throws Exception {
		return DataFormat.hex2dec(data,OFS_IO04FLG,LEN_IO04FLG);
	}
}
