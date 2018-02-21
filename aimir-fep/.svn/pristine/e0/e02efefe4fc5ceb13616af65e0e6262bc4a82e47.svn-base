/** 
 * @(#)MT54.java       1.0 05/07/25 *
 * 
 * Sag Log Status Class.
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
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
public class MT54 implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7864053335040551155L;
	private final int OFS_A_SAG_TIMER        = 1; 
	private final int OFS_A_CUM_SAG_COUNTER  = 3; 
	private final int OFS_A_CUM_SAG_TIMER    = 5; 

	private final int OFS_B_SAG_TIMER        = 9; 
	private final int OFS_B_CUM_SAG_COUNTER  = 11; 
	private final int OFS_B_CUM_SAG_TIMER    = 13; 

	private final int OFS_C_SAG_TIMER        = 17; 
	private final int OFS_C_CUM_SAG_COUNTER  = 19; 
	private final int OFS_C_CUM_SAG_TIMER    = 21; 
	
	private final int LEN_A_SAG_TIMER        = 2;
	private final int LEN_A_CUM_SAG_COUNTER  = 2; 
	private final int LEN_A_CUM_SAG_TIMER    = 4; 

	private final int LEN_B_SAG_TIMER        = 2; 
	private final int LEN_B_CUM_SAG_COUNTER  = 2; 
	private final int LEN_B_CUM_SAG_TIMER    = 4; 

	private final int LEN_C_SAG_TIMER        = 2; 
	private final int LEN_C_CUM_SAG_COUNTER  = 2; 
	private final int LEN_C_CUM_SAG_TIMER    = 4; 
	
	private byte[] data;

    private Log logger = LogFactory.getLog(getClass());
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MT54(byte[] data) {
		this.data = data;
	}
	
	public int getA_CUM_SAG_COUNTER() throws Exception {
		return DataFormat.hex2dec(
				DataFormat.LSB2MSB(
				DataFormat.select(
						data,OFS_A_CUM_SAG_COUNTER,LEN_A_CUM_SAG_COUNTER)));
	}
	
	public long getA_CUM_SAG_TIMER() 
	throws Exception {
		long f_phase_dur
			= (long)(DataFormat.hex2dec(
						DataFormat.LSB2MSB(
								DataFormat.select(
										data,OFS_A_CUM_SAG_TIMER,LEN_A_CUM_SAG_TIMER)))*8.33);

		return f_phase_dur;
	}
	
	public int getB_CUM_SAG_COUNTER() throws Exception {
		return DataFormat.hex2dec(
				DataFormat.LSB2MSB(
				DataFormat.select(
						data,OFS_B_CUM_SAG_COUNTER,LEN_B_CUM_SAG_COUNTER)));
	}
	
	public long getB_CUM_SAG_TIMER() 
	throws Exception {
		long f_phase_dur
			= (long)(DataFormat.hex2dec(
						DataFormat.LSB2MSB(
								DataFormat.select(
										data,OFS_B_CUM_SAG_TIMER,LEN_B_CUM_SAG_TIMER)))*8.33);

		return f_phase_dur;
	}
	
	public int getC_CUM_SAG_COUNTER() throws Exception {
		return DataFormat.hex2dec(
				DataFormat.LSB2MSB(
				DataFormat.select(
						data,OFS_C_CUM_SAG_COUNTER,LEN_C_CUM_SAG_COUNTER)));
	}
	
	public long getC_CUM_SAG_TIMER() 
	throws Exception {
		long f_phase_dur
			= (long)(DataFormat.hex2dec(
						DataFormat.LSB2MSB(
								DataFormat.select(
										data,OFS_C_CUM_SAG_TIMER,LEN_C_CUM_SAG_TIMER)))*8.33);

		return f_phase_dur;
	}

	public byte[] parseA_CUM_SAG_COUNTER() 
							throws Exception {
		return DataFormat.LSB2MSB(
			DataFormat.select(
				data,OFS_A_CUM_SAG_COUNTER,LEN_A_CUM_SAG_COUNTER));
	}

	public byte[] parseA_CUM_SAG_TIMER() 
							throws Exception {
		long f_phase_dur
			= (long)(DataFormat.hex2dec(
						DataFormat.LSB2MSB(
							DataFormat.select(
								data,OFS_A_CUM_SAG_TIMER,LEN_A_CUM_SAG_TIMER)))*8.33);
					
		return DataFormat.dec2hex(f_phase_dur);
	}
	
	public byte[] parseB_CUM_SAG_COUNTER() 
							throws Exception {
		return DataFormat.LSB2MSB(
			DataFormat.select(
				data,OFS_B_CUM_SAG_COUNTER,LEN_B_CUM_SAG_COUNTER));
	}

	public byte[] parseB_CUM_SAG_TIMER() 
							throws Exception {
		long f_phase_dur
			= (long)(DataFormat.hex2dec(
						DataFormat.LSB2MSB(
							DataFormat.select(
								data,OFS_B_CUM_SAG_TIMER,LEN_B_CUM_SAG_TIMER)))*8.33);
					
		return DataFormat.dec2hex(f_phase_dur);
	}
	
	public byte[] parseC_CUM_SAG_COUNTER() 
							throws Exception {
		return DataFormat.LSB2MSB(
			DataFormat.select(
				data,OFS_C_CUM_SAG_COUNTER,LEN_C_CUM_SAG_COUNTER));
	}

	public byte[] parseC_CUM_SAG_TIMER() 
							throws Exception {
		long f_phase_dur
			= (long)(DataFormat.hex2dec(
						DataFormat.LSB2MSB(
							DataFormat.select(
								data,OFS_C_CUM_SAG_TIMER,LEN_C_CUM_SAG_TIMER)))*8.33);
					
		return DataFormat.dec2hex(f_phase_dur);
	}

}
