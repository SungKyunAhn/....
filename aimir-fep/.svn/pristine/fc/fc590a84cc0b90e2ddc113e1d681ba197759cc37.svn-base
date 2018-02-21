/** 
 * @(#)Class50.java       1.0 05/02/18 *
 * 
 * PowerTools Monitor Counters and Timers Class.
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
public class Class50 {

    private static Log logger = LogFactory.getLog(Class50.class);
	
	private int OFS_PQMCNTR1 = 0;
	private int OFS_PQMCNTR2 = 6;
	private int OFS_PQMCNTR3 = 12;
	private int OFS_PQMCNTR4 = 18;
	private int OFS_PQMCNTR5 = 24;
	private int OFS_PQMCNTR6 = 30;
	private int OFS_PQMCNTR7 = 36;
	private int OFS_PQMCNTR8 = 42;
	private int OFS_PQMCNTR9 = 48;	
	private int OFS_PQMTMR1  = 2;
	private int OFS_PQMTMR2  = 8;
	private int OFS_PQMTMR3  = 14;
	private int OFS_PQMTMR4  = 20;
	private int OFS_PQMTMR5  = 26;
	private int OFS_PQMTMR6  = 32;
	private int OFS_PQMTMR7  = 38;
	private int OFS_PQMTMR8  = 44;
	private int OFS_PQMTMR9  = 50;	
	private int LEN_PQMCNTR1 = 2;
	private int LEN_PQMCNTR2 = 2;
	private int LEN_PQMCNTR3 = 2;
	private int LEN_PQMCNTR4 = 2;
	private int LEN_PQMCNTR5 = 2;
	private int LEN_PQMCNTR6 = 2;
	private int LEN_PQMCNTR7 = 2;
	private int LEN_PQMCNTR8 = 2;
	private int LEN_PQMCNTR9 = 2;	
	private int LEN_PQMTMR1  = 4;
	private int LEN_PQMTMR2  = 4;
	private int LEN_PQMTMR3  = 4;
	private int LEN_PQMTMR4  = 4;
	private int LEN_PQMTMR5  = 4;
	private int LEN_PQMTMR6  = 4;
	private int LEN_PQMTMR7  = 4;
	private int LEN_PQMTMR8  = 4;
	private int LEN_PQMTMR9  = 4;
	
	private int OFS_WARNFLAG = 90;
	private byte[] data;
	
	public Class50(byte[] data){
		this.data = data;
	}

	/**
	 * PQM Service voltage Cumulative count.
	 * @return - 2byte hex data.
	 * @throws Exception
	 */
	public byte[] parseSvcVolCount()
					throws Exception {
		int val 
			= DataFormat.hex2dec(data,OFS_PQMCNTR1,LEN_PQMCNTR1);
		val = val & 0x7FFF;
		byte[] b = new byte[2];
		
		b[0] = (byte) ((val >> 8) & 0xFF);
		b[1] = (byte) (val & 0xFF);
		return b;					
	}
	
	/**
	 * PQM Service Voltage Cumulative Duration.(seconds)
	 * @return - 4 byte hex data.
	 * @throws Exception
	 */
	public byte[] parseSvcVolDuration() 
					throws Exception {
		return DataFormat.select(data,OFS_PQMTMR1,LEN_PQMTMR1);
	}
	
	public byte parseSvcVolStatus() {
		if((data[OFS_PQMCNTR1] & 0x80) > 0)
			return 0x01;
		else 
			return 0x00;
	}
	
	/**
	 * PQM Low Voltage Cumulative Count.
	 * @return - 2byte hex data.
	 * @throws Exception
	 */
	public byte[] parseLowVolCount() 
					throws Exception {

		int val 
			= DataFormat.hex2dec(data,OFS_PQMCNTR2,LEN_PQMCNTR2);
		val = val & 0x7FFF;
		byte[] b = new byte[2];
		
		b[0] = (byte) ((val >> 8) & 0xFF);
		b[1] = (byte) (val & 0xFF);
		return b;
	}
	
	/**
	 * PQM Low Voltage Cumulative Duration.(seconds)
	 * @return - 4 byte hex data.
	 * @throws Exception
	 */
	public byte[] parseLowVolDuration() 
					throws Exception {
		return DataFormat.select(data,OFS_PQMTMR2,LEN_PQMTMR2);
	}
	
	/**
	 * PQM Low Voltage Status.
	 * @return - 1 byte
	 */
	public byte parseLowVolStatus(){
		if((data[OFS_PQMCNTR2] & 0x80) > 0)
			return 0x01;
		else 
			return 0x00;
	}
	
	/**
	 * PQM High Voltage Cumulative count.
	 * @return -  2 byte hex data.
	 * @throws Exception
	 */
	public byte[] parseHighVolCount() 
					throws Exception {
		int val 
			= DataFormat.hex2dec(data,OFS_PQMCNTR3,LEN_PQMCNTR3);
		val = val & 0x7FFF;
		byte[] b = new byte[2];
		
		b[0] = (byte) ((val >> 8) & 0xFF);
		b[1] = (byte) (val & 0xFF);
		return b;
	}
	
	/**
	 * PQM High Voltage Cumulative Duration.(seconds)
	 * @return - 4 byte hex data.
	 * @throws Exception
	 */
	public byte[] parseHighVolDuration() 
					throws Exception {
		return DataFormat.select(data,OFS_PQMTMR3,LEN_PQMTMR3);
	}
	
	/**
	 * PQM High Voltage Status.
	 * @return
	 */
	public byte parseHighVolStatus(){
		if((data[OFS_PQMCNTR3] & 0x80) > 0)
			return 0x01;
		else 
			return 0x00;
	}
	
	/**
	 * PQM Power Frequency & Reverse Power Cumulative Count.
	 * @return - 2 byte hex data.
	 * @throws Exception
	 */
	public byte[] parseReversePowerCount() 
					throws Exception {

		int val 
			= DataFormat.hex2dec(data,OFS_PQMCNTR4,LEN_PQMCNTR4);
		val = val & 0x7FFF;
		byte[] b = new byte[2];
		
		b[0] = (byte) ((val >> 8) & 0xFF);
		b[1] = (byte) (val & 0xFF);
		return b;
	}
	
	/**
	 * PQM Power Frequency & Reverse Power 
	 * Cumulative Duration.(seconds)
	 * @return -  4 byte hex data.
	 * @throws Exception
	 */
	public byte[] parseReversePowerDuration() 
					throws Exception {
		return DataFormat.select(data,OFS_PQMTMR4,LEN_PQMTMR4);
	}
	
	/**
	 * PQM Power Frequency & Reverse Power
	 * Status.
	 * @return
	 */
	public byte parseReversePowerStatus(){
		if((data[OFS_PQMCNTR4] & 0x80) > 0)
			return 0x01;
		else 
			return 0x00;
	}
	
	/**
	 * PQM Low Current Cumulative Count.
	 * @return - 2 byte hex data.
	 * @throws Exception
	 */
	public byte[] parseLowCurrentCount()
					throws Exception {

		int val 
			= DataFormat.hex2dec(data,OFS_PQMCNTR5,LEN_PQMCNTR5);
		val = val & 0x7FFF;
		byte[] b = new byte[2];
		
		b[0] = (byte) ((val >> 8) & 0xFF);
		b[1] = (byte) (val & 0xFF);
		return b;
	}
	
	/**
	 * PQM Low Current Duration.(seconds)
	 * @return -  4 byte hex data.
	 * @throws Exception
	 */
	public byte[] parseLowCurrentDuration()
					throws Exception {
		return DataFormat.select(data,OFS_PQMTMR5,LEN_PQMTMR5);
	}
	
	/**
	 * PQM Low Current Status.
	 * @return
	 */
	public byte parseLowCurrentStatus(){
		if((data[OFS_PQMCNTR5] & 0x80) > 0)
			return 0x01;
		else 
			return 0x00;
	}
	
	/**
	 * PQM Power Factor Cumulative Count.
	 * @return - 2 byte hex data.
	 * @throws Exception
	 */
	public byte[] parsePowerFactorCount()
					throws Exception {

		int val 
			= DataFormat.hex2dec(data,OFS_PQMCNTR6,LEN_PQMCNTR6);
		val = val & 0x7FFF;
		byte[] b = new byte[2];
		
		b[0] = (byte) ((val >> 8) & 0xFF);
		b[1] = (byte) (val & 0xFF);
		return b;
	}
	
	/**
	 * PQM Power Factor Cumulative Duration.(seconds)
	 * @return - 4 byte hex data.
	 * @throws Exception
	 */
	public byte[] parsePowerFactorDuration()
					throws Exception {
		return DataFormat.select(data,OFS_PQMTMR6,LEN_PQMTMR6);
	}
	
	/**
	 * PQM Power Factor Status.
	 * @return
	 */
	public byte parsePowerFactorStatus(){
		if((data[OFS_PQMCNTR6] & 0x80) > 0)
			return 0x01;
		else 
			return 0x00;
	}
	
	/**
	 * PQM 2nd Harmonic Cumulative Count.
	 * @return - 2 byte hex data.
	 * @throws Exception
	 */
	public byte[] parseHarmonicCount()
					throws Exception {

		int val 
			= DataFormat.hex2dec(data,OFS_PQMCNTR7,LEN_PQMCNTR7);
		val = val & 0x7FFF;
		byte[] b = new byte[2];
		
		b[0] = (byte) ((val >> 8) & 0xFF);
		b[1] = (byte) (val & 0xFF);
		return b;
	}
	
	/**
	 * PQM 2nd Harmonic Cumulative Duration.(seconds)
	 * @return -  4 byte hex data.
	 * @throws Exception
	 */
	public byte[] parseHarmonicDuration()
					throws Exception {
		return DataFormat.select(data,OFS_PQMTMR7,LEN_PQMTMR7);
	}
	
	/**
	 * PQM 2nd Harmonic Status.
	 * @return
	 */
	public byte parseHarmonicStatus(){
		if((data[OFS_PQMCNTR7] & 0x80) > 0)
			return 0x01;
		else 
			return 0x00;
	}
	
	/**
	 * PQM THD(Total Harmonic Distortion) Current 
	 * Cumulative count.
	 * @return -  2 byte hex data.
	 * @throws Exception
	 */
	public byte[] parseTHDCurrCount()
					throws Exception {

		int val 
			= DataFormat.hex2dec(data,OFS_PQMCNTR8,LEN_PQMCNTR8);
		val = val & 0x7FFF;
		byte[] b = new byte[2];
		
		b[0] = (byte) ((val >> 8) & 0xFF);
		b[1] = (byte) (val & 0xFF);
		return b;
	}
	
	/**
	 * PQM THD(Total Harmonic Distortion) Current 
	 * Cumulative Duration.(seconds)
	 * @return
	 * @throws Exception
	 */
	public byte[] parseTHDCurrDuration()
					throws Exception {
		return DataFormat.select(data,OFS_PQMTMR8,LEN_PQMTMR8);
	}
	
	/**
	 * PQM THD(Total Harmonic Distortion) Current 
	 * status.
	 * @return
	 */
	public byte parseTHDCurrStatus(){
		if((data[OFS_PQMCNTR8] & 0x80) > 0)
			return 0x01;
		else 
			return 0x00;
	}
	
	/**
	 * PQM THD(Total Harmonic Distortion) Voltage 
	 * Cumulative Count.
	 * @return -  2 byte hex data.
	 * @throws Exception
	 */
	public byte[] parseTHDVolCount() 
					throws Exception {

		int val 
			= DataFormat.hex2dec(data,OFS_PQMCNTR9,LEN_PQMCNTR9);
		val = val & 0x7FFF;
		byte[] b = new byte[2];
		
		b[0] = (byte) ((val >> 8) & 0xFF);
		b[1] = (byte) (val & 0xFF);
		return b;
	}
	
	/**
	 * PQM THD(Total Harmonic Distortion) Voltage
	 * Cumulative Duration.(seconds)
	 * @return -  4 byte hex data.
	 * @throws Exception
	 */
	public byte[] parseTHDVolDuration()
					throws Exception {
		return DataFormat.select(data,OFS_PQMTMR9,LEN_PQMTMR9);
	}
	
	/**
	 * PQM THD(Total Harmonic Distortion) 
	 * Status.
	 * @return
	 */
	public byte parseTHDVolStatus(){
		if((data[OFS_PQMCNTR9] & 0x80) > 0)
			return 0x01;
		else 
			return 0x00;
	}
	
	
	/**
	 * PQM Warning Flag
	 * @return - 4 byte hex data
	 * @throws Exception
	 */
	public byte[] parseWarn() throws Exception {
		
		byte[] warn = new byte[4];
		DataFormat.select(data,OFS_WARNFLAG,2);
		System.arraycopy(data,0,warn,2,2);
		return warn;
	}
}
