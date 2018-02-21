/** 
 * @(#)MT62.java       1.0 06/08/23 *
 * 
 * Instrumentation Profile Control Table.
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
public class MT62 implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3674521469978816839L;
	private final int LEN_LP_SEL_SET1  = 3;
	private final int LEN_INT_FMT_SET1 = 1;
	private final int LEN_SCALARS_SET1 = 2;
	private final int LEN_DIVISOR_SET1 = 2;
	
	private final int LEN_LP_SEL_SET2  = 3;
	private final int LEN_INT_FMT_SET2 = 1;
	private final int LEN_SCALARS_SET2 = 2;
	private final int LEN_DIVISOR_SET2 = 2;

	private byte[] data;

    private Log logger = LogFactory.getLog(getClass());
	private int set1_channels;
	private int set2_channels;
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MT62(byte[] data, int nbr_chns_set1, int nbr_chns_set2) {
		this.data = data;
		this.set1_channels = nbr_chns_set1;
		this.set2_channels = nbr_chns_set2;
	}
	
	public int[] getSet1SourceID(){
		
		int[] srcid = new int[this.set1_channels];
		
		int offset = 0;
		
		try {
			for(int i = 0; i < this.set1_channels; i++){
				byte[] temp;
				temp = DataFormat.select(data, offset, LEN_LP_SEL_SET1);
				offset += LEN_LP_SEL_SET1;
				srcid[i] = temp[1]&0xFF;//source_select
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}

		return srcid;
		
	}
	
	public int getSourceID(int chanid){
		
		int[] srcid = new int[this.set1_channels];
		
		int offset = 0;
		
		try {
			for(int i = 0; i < this.set1_channels; i++){
				byte[] temp;
				temp = DataFormat.select(data, offset, LEN_LP_SEL_SET1);
				offset += LEN_LP_SEL_SET1;
				srcid[i] = temp[1]&0xFF;//source_select
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}

		return srcid[chanid];
		
	}
	
	public int getSET1SCALAR(int chanid) {
		
		int offset = LEN_LP_SEL_SET1*this.set1_channels
				   + LEN_INT_FMT_SET1
		           + LEN_SCALARS_SET1*chanid;
		           
		try {
			return DataFormat.hex2unsigned16(
				DataFormat.LSB2MSB(
					DataFormat.select(data,offset,LEN_SCALARS_SET1)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1;
		
	}
	
	public int getSET1DIVISOR(int chanid) {
		
		int offset = LEN_LP_SEL_SET1*this.set1_channels
				   + LEN_INT_FMT_SET1
		           + LEN_SCALARS_SET1*this.set1_channels
				   + LEN_DIVISOR_SET1*chanid;
		           
		try {
			return DataFormat.hex2unsigned16(
				DataFormat.LSB2MSB(
					DataFormat.select(data,offset,LEN_DIVISOR_SET1)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1;
		
	}
	
	
	public byte[] parseSET1SCALAR(int chanid) throws Exception {
		
		int offset = LEN_LP_SEL_SET1*this.set1_channels
				   + LEN_INT_FMT_SET1
				   + LEN_SCALARS_SET1*chanid;
		           
		return DataFormat.dec2hex((char)
			(DataFormat.hex2unsigned16(
			DataFormat.LSB2MSB(
				DataFormat.select(data,offset,LEN_SCALARS_SET1)))));
		
	}
	
	public int[] getSET1DIVISOR() {
		
		int[] set1divisors = new int[set1_channels];
		for (int i = 0; i < set1_channels; i++){
			set1divisors[i] = getSET1DIVISOR(i);
		}
		
		return set1divisors;
	}
	
	public int[] getSET2DIVISOR()  {
		
		int[] set2divisors = new int[set2_channels];
		for (int i = 0; i < set2_channels; i++){
			set2divisors[i] = getSET2DIVISOR(i);
		}
		
		return set2divisors;
	}
	
	public byte[] parseSET1DIVISOR(int chanid) throws Exception {
		
		int offset = LEN_LP_SEL_SET1*this.set1_channels
				   + LEN_INT_FMT_SET1
				   + LEN_SCALARS_SET1*this.set1_channels
				   + LEN_DIVISOR_SET1*chanid;
		           
		return DataFormat.dec2hex((char)
			(DataFormat.hex2unsigned16(
			DataFormat.LSB2MSB(
				DataFormat.select(data,offset,LEN_DIVISOR_SET1)))));
		
	}
	
	
	public int getSET2SCALAR(int chanid) throws Exception {
		
		int offset = LEN_LP_SEL_SET1*this.set1_channels
				   + LEN_INT_FMT_SET1
				   + LEN_SCALARS_SET1*this.set1_channels
				   + LEN_DIVISOR_SET1*this.set1_channels
				   + LEN_LP_SEL_SET2*this.set2_channels
				   + LEN_INT_FMT_SET2
				   + LEN_SCALARS_SET2*chanid;
		           
		return DataFormat.hex2unsigned16(
			DataFormat.LSB2MSB(
				DataFormat.select(data,offset,LEN_SCALARS_SET2)));
		
	}
	
	public int getSET2DIVISOR(int chanid) {
		
		int offset = LEN_LP_SEL_SET1*this.set1_channels
				   + LEN_INT_FMT_SET1
				   + LEN_SCALARS_SET1*this.set1_channels
				   + LEN_DIVISOR_SET1*this.set1_channels
				   + LEN_LP_SEL_SET2*this.set2_channels
				   + LEN_INT_FMT_SET2
				   + LEN_SCALARS_SET2*set2_channels
				   + LEN_DIVISOR_SET2*chanid;
		           
		try {
			return DataFormat.hex2unsigned16(
				DataFormat.LSB2MSB(
					DataFormat.select(data,offset,LEN_DIVISOR_SET2)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1;
		
	}
	
	
	public byte[] parseSET2SCALAR(int chanid) throws Exception {
		
		int offset = LEN_LP_SEL_SET1*this.set1_channels
				   + LEN_INT_FMT_SET1
				   + LEN_SCALARS_SET1*this.set1_channels
				   + LEN_DIVISOR_SET1*this.set1_channels
				   + LEN_LP_SEL_SET2*this.set2_channels
				   + LEN_INT_FMT_SET2
				   + LEN_SCALARS_SET2*chanid;
				   
		return DataFormat.dec2hex((char)
			(DataFormat.hex2unsigned16(
			DataFormat.LSB2MSB(
				DataFormat.select(data,offset,LEN_SCALARS_SET2)))));
		
	}
	
	public byte[] parseSET2DIVISOR(int chanid) throws Exception {
		
		int offset = LEN_LP_SEL_SET1*this.set1_channels
				   + LEN_INT_FMT_SET1
				   + LEN_SCALARS_SET1*this.set1_channels
				   + LEN_DIVISOR_SET1*this.set1_channels
				   + LEN_LP_SEL_SET2*this.set2_channels
				   + LEN_INT_FMT_SET2
				   + LEN_SCALARS_SET2*set2_channels
				   + LEN_DIVISOR_SET2*chanid;
		           
		return DataFormat.dec2hex((char)
			(DataFormat.hex2unsigned16(
			DataFormat.LSB2MSB(
				DataFormat.select(data,offset,LEN_DIVISOR_SET2)))));
		
	}
	
	
}
