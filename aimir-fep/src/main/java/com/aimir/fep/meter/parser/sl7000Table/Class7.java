/** 
 * @(#)Class7.java       1.0 04/10/19 *
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
package com.aimir.fep.meter.parser.sl7000Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 *
 * ACRATIS SL7000 meter Class. <p> * 
 * Include Billing Data Information. <p>
 */
public class Class7 {

	public final static int OFS_NUMBEROFRESET = 52;
	public final static int OFS_SOURCEOFRESET = 54;
	public final static int OFS_RESETDATATIME = 57;
	public final static int LEN_RESETDATATIME = 8;
	
	public final static int OFS_YEAR     = 57;
	public final static int OFS_MONTH    = 59;
	public final static int OFS_DAY      = 60;
	
	public final static int OFS_HOUR     = 62;
	public final static int OFS_MIN      = 63;
	public final static int OFS_SEC      = 64;	
	
	public final static int OFS_TOT_ACTIVE_EF    = 168;		// [1]  signed 8 pow(10,x) Active Energy+ Format Total.
	public final static int OFS_TOT_REACTIVE_EF  = 208;		// [1]  signed 8 pow(10,x) Reactive Energy+ Format Total.
	public final static int OFS_RATE_ACTIVE_EF   = 1313;	// [1]  signed 8 pow(10,x) Active Energy+ Format Rate.
	public final static int OFS_RATE_REACTIVE_EF = 1382;	// [1]  signed 8 pow(10,x) Reactive Energy+ Format Rate. 
	public final static int OFS_RATE_APM_DF      = 1465;	// [1]  signed 8 pow(10,x) Active Power+ Max. Demand Format Rate.
	public final static int OFS_RATE_APC_DF      = 2651;	// [1]  signed 8 pow(10,x) Active Power+ Cumm. Demand Format Rate.
	public final static int OFS_RATE_RPM_DF      = 1912;	// [1]  signed 8 pow(10,x) Reactive Power+ Max. Demand Format Rate.
	public final static int OFS_RATE_RPC_DF      = 2684;	// [1]  signed 8 pow(10,x) Reactive Power+ Cumm. Demand Format Rate.	
	
	public final static int OFS_TOT_ACTIVE_ENERGY   = 161;	//4	// [8]  Active+ Energy Total.
	public final static int OFS_TOT_REACTIVE_ENERGY = 343;	//4	// [8]  Reactive + Energy Total.
	
	public final static int OFS_RATE1_ACTIVE   = 1306;		// [8]  Active+ Energy Rate1.
	public final static int OFS_RATE2_ACTIVE   = 1329;		// [8]	Active+ Energy Rate2.
	public final static int OFS_RATE3_ACTIVE   = 1352;		// [8]	Active+ Energy Rate3.
	public final static int OFS_RATE1_REACTIVE = 1375;		// [8]  Reactive+ Energy Rate1.
	public final static int OFS_RATE2_REACTIVE = 1398;		// [8]	Reactive+ Energy Rate2.
	public final static int OFS_RATE3_REACTIVE = 1435;		// [8]  Reactive+ Energy Rate3.
	
	public final static int OFS_RATE1_ACTIVE_MAX     = 1460;	// [4]  Active Power+ Max. Demand Rate1.
	public final static int OFS_RATE1_ACTIVE_MAXTIME = 1470;	// [8]  Active Power+ Max. Demand Date Time Rate1.
	public final static int OFS_RATE2_ACTIVE_MAX     = 1609;	// [4]  Active Power+ Max. Demand Rate2.
	public final static int OFS_RATE2_ACTIVE_MAXTIME = 1619;	// [8]  Active Power+ Max. Demand Date Time Rate2.
	public final static int OFS_RATE3_ACTIVE_MAX     = 1758;	// [4]  Active Power+ Max. Demand Rate3.
	public final static int OFS_RATE3_ACTIVE_MAXTIME = 1768;	// [8]  Active Power+ Max. Demand Date Time Rate3.
	
	public final static int OFS_RATE1_REACTIVE_MAX     = 1907;	// [4]  Reactive Power+ Max. Demand Rate1.
	public final static int OFS_RATE1_REACTIVE_MAXTIME = 1917;	// [8]  Reactive Power+ Max. Demand Date Time Rate1.
	public final static int OFS_RATE2_REACTIVE_MAX     = 2056;	// [4]  Reactive Power+ Max. Demand Rate2.
	public final static int OFS_RATE2_REACTIVE_MAXTIME = 2066;	// [8]  Reactive Power+ Max. Demand Date Time Rate2.
	public final static int OFS_RATE3_REACTIVE_MAX     = 2205;	// [4]  Reactive Power+ Max. Demand Rate3.
	public final static int OFS_RATE3_REACTIVE_MAXTIME = 2215;	// [8]  Reactive Power+ Max. Demand Date Time Rate3.
	
	public final static int OFS_RATE1_ACTIVE_CUM = 2644;	// [8] Active Power+ Cum. Demand Rate1.
	public final static int OFS_RATE2_ACTIVE_CUM = 2655;	// [8] Active Power+ Cum. Demand Rate2.
	public final static int OFS_RATE3_ACTIVE_CUM = 2666;	// [8] Active Power+ Cum. Demand Rate3.
	
	public final static int OFS_RATE1_REACTIVE_CUM = 2677;	// [8] Reactive Power+ Cum. Demand Rate1.
	public final static int OFS_RATE2_REACTIVE_CUM = 2688;	// [8] Reactive Power+ Cum. Demand Rate2.
	public final static int OFS_RATE3_REACTIVE_CUM = 2713;	// [8] Reactive Power+ Cum. Demand Rate3.	
	
	private byte[] data;
	
    private static Log logger = LogFactory.getLog(Class7.class);
	
	/**
	 * Contructor. <p>
	 * @param data - read data <p>
	 */
	public Class7(byte[] data){
		this.data = data;
		logger.debug("class7 data\n"+Util.getHexString(data));
	}
	
	
	/**
	 * Get Number of Reset. <p>
	 * @return
	 */
	public byte parseResetCount(){
		return data[OFS_NUMBEROFRESET];
	}
	
	
	/**
	 * Get Reset Data Time. <p>
	 * @return
	 */
	public byte[] parseResetTime() {
		
		byte[] datetime = new byte[7];
							
		/* length 8 */		
		datetime[0] = data[OFS_YEAR];
		datetime[1] = data[OFS_YEAR+1];
		datetime[2] = data[OFS_MONTH];
		datetime[3] = data[OFS_DAY];
		datetime[4] = data[OFS_HOUR];
		datetime[5] = data[OFS_MIN];
		datetime[6] = data[OFS_SEC];
		
		return datetime;
	}
	
	
	/**
	 * Get Active Energy+ Format Total signed 8 pow(10,x).
	 * @return
	 */
	public byte parseTAEF(){
		return data[OFS_TOT_ACTIVE_EF];
	}
	
	
	/**
	 * Get  Reactive Energy+ Format Total. signed 8 pow(10,x)
	 * @return
	 */
	public byte parseTREF(){
		return data[OFS_TOT_REACTIVE_EF];
	}

	/**
	 * Get Active Energy+ Format Rate signed 8 pow(10,x)
	 * @return
	 */
	public byte parseRAEF(){
		return data[OFS_RATE_ACTIVE_EF];
	}
	
	/**
	 * Get Reactive Energy+ Format Rate signed 8 pow(10,x)
	 * @return
	 */
	public byte parseRREF(){
		return data[OFS_RATE_REACTIVE_EF];
	}
	
	/**
	 * Get Active Power+ Max. Demand Format Rate signed 8 pow(10,x)
	 * @return
	 */
	public byte parseRAPMDF(){
		return data[OFS_RATE_APM_DF];
	}
	
	/**
	 * Get Active Power+ Cumm. Demand Format Rate signed 8 pow(10,x)
	 * @return
	 */
	public byte parseRAPCDF(){
		return data[OFS_RATE_APC_DF];
	}
	
	/**
	 * Get Reactive Power+ Max. Demand Format Rate signed 8 pow(10,x)
	 * @return
	 */
	public byte parseRRPMDF(){
		return data[OFS_RATE_RPM_DF];
	}
	
	/**
	 * Get Reactive Power+ Cumm. Demand Format Rate signed 8 pow(10,x)
	 * @return
	 */
	public byte parseRRPCDF(){
		return data[OFS_RATE_RPC_DF];
	}


	/**
	 * [8]  Get Active+ Energy Total.
	 * @return
	 */
	public byte[] parseTAE() throws Exception {
		return DataFormat.select(data,OFS_TOT_ACTIVE_ENERGY,4);
	}
	
	
	/**
	 * [8]  Get Reactive + Energy Total.
	 * @return
	 */
	public byte[] parseTRE() throws Exception {
		return DataFormat.select(data,OFS_TOT_REACTIVE_ENERGY,4);
	}


	/**
	 * [8]  Get Active+ Energy Rate1.
	 * @return
	 */
	public byte[] parseR1AE() throws Exception {
		return DataFormat.select(data,OFS_RATE1_ACTIVE,4);
	}	
	
	/**
	 * [8]	Get Active+ Energy Rate2.
	 * @return
	 */
	public byte[] parseR2AE() throws Exception {
		return DataFormat.select(data,OFS_RATE2_ACTIVE,4);
	}
	
	/**
	 * [8]	Get Active+ Energy Rate3.
	 * @return
	 */
	public byte[] parseR3AE() throws Exception {
		return DataFormat.select(data,OFS_RATE3_ACTIVE,4);
	}
	
	/**
	 * [8]  Get Reactive+ Energy Rate1.
	 * @return
	 */
	public byte[] parseR1RE() throws Exception {
		return DataFormat.select(data,OFS_RATE1_REACTIVE,4);
	}
	
	/**
	 * [8]	Get Reactive+ Energy Rate2.
	 * @return
	 */
	public byte[] parseR2RE() throws Exception {		
		return DataFormat.select(data,OFS_RATE2_REACTIVE,4);
	}
	
	/**
	 * [8]  Get Reactive+ Energy Rate3.
	 * @return
	 */
	public byte[] parseR3RE() throws Exception {
		return DataFormat.select(data,OFS_RATE3_REACTIVE,4);
	}	
	
	/**
	 * [4]  Active Power+ Max. Demand Rate1.
	 * @return
	 */
	public byte[] parseR1AM() throws Exception {
		return DataFormat.select(data,OFS_RATE1_ACTIVE_MAX,2);
	}
		
	/**
	 * [8]  Active Power+ Max. Demand Date Time Rate1.
	 * @return
	 */
	public byte[] parseR1AMT() throws Exception {
		return DataFormat.select(data,OFS_RATE1_ACTIVE_MAXTIME,6);
	}
	
	/**
	 * [4]  Active Power+ Max. Demand Rate2.
	 * @return
	 */
	public byte[] parseR2AM() throws Exception {
		return DataFormat.select(data,OFS_RATE2_ACTIVE_MAX,2);
	}
	
	/**
	 * [8]  Active Power+ Max. Demand Date Time Rate2.
	 * @return
	 */
	public byte[] parseR2AMT() throws Exception {
		return DataFormat.select(data,OFS_RATE2_ACTIVE_MAXTIME,6);
	}
	
	/**
	 * [4]  Active Power+ Max. Demand Rate3.
	 * @return
	 */
	public byte[] parseR3AM() throws Exception {
		return DataFormat.select(data,OFS_RATE3_ACTIVE_MAX,2);
	}
	
	/**
	 * [8]  Active Power+ Max. Demand Date Time Rate3.
	 * @return
	 */
	public byte[] parseR3AMT() throws Exception {
		return DataFormat.select(data,OFS_RATE3_ACTIVE_MAXTIME,6);
	}
	
	/**
	 * [4]  Reactive Power+ Max. Demand Rate1.
	 * @return
	 */
	public byte[] parseR1RM() throws Exception {
		return DataFormat.select(data,OFS_RATE1_REACTIVE_MAX,2);
	}
	
	/**
	 * [8]  Reactive Power+ Max. Demand Date Time Rate1.
	 * @return
	 */
	public byte[] parseR1RMT() throws Exception {
		return DataFormat.select(data,OFS_RATE1_REACTIVE_MAXTIME,6);
	}
	
	/**
	 * [4]  Reactive Power+ Max. Demand Rate2.
	 * @return
	 */
	public byte[] parseR2RM() throws Exception {
		return DataFormat.select(data,OFS_RATE2_REACTIVE_MAX,2);
	}
	
	/**
	 * [8]  Reactive Power+ Max. Demand Date Time Rate2.
	 * @return
	 */
	public byte[] parseR2RMT() throws Exception {
		return DataFormat.select(data,OFS_RATE2_REACTIVE_MAXTIME,6);
	}
	
	/**
	 * [4]  Reactive Power+ Max. Demand Rate3.
	 * @return
	 */
	public byte[] parseR3RM() throws Exception {
		return DataFormat.select(data,OFS_RATE3_REACTIVE_MAX,2);
	}
	
	/**
	 * [8]  Reactive Power+ Max. Demand Date Time Rate3.
	 * @return
	 */
	public byte[] parseR3RMT() throws Exception {
		return DataFormat.select(data,OFS_RATE3_REACTIVE_MAXTIME,6);
	}
	
	/**
	 * [8] Active Power+ Cum. Demand Rate1.
	 * @return
	 */
	public byte[] parseR1AC() throws Exception {
		return DataFormat.select(data,OFS_RATE1_ACTIVE_CUM,4);
	}
	
	/**
	 * [8] Active Power+ Cum. Demand Rate2.
	 * @return
	 */
	public byte[] parseR2AC() throws Exception {
		return DataFormat.select(data,OFS_RATE2_ACTIVE_CUM,4);
	}
	
	/**
	 * [8] Active Power+ Cum. Demand Rate3.
	 * @return
	 */
	public byte[] parseR3AC() throws Exception {
		return DataFormat.select(data,OFS_RATE3_ACTIVE_CUM,4);
	}
	
	/**
	 * [8] Reactive Power+ Cum. Demand Rate1.
	 * @return
	 */
	public byte[] parseR1RC() throws Exception {
		return DataFormat.select(data,OFS_RATE1_REACTIVE_CUM,4);
	}
	
	/**
	 * [8] Reactive Power+ Cum. Demand Rate2.
	 * @return
	 */
	public byte[] parseR2RC() throws Exception {
		return DataFormat.select(data,OFS_RATE2_REACTIVE_CUM,4);
	}
	
	/**
	 * [8] Reactive Power+ Cum. Demand Rate3.
	 * @return
	 */
	public byte[] parseR3RC() throws Exception {
		return DataFormat.select(data,OFS_RATE3_REACTIVE_CUM,4);
	}
	
}
