/** 
 * @(#)ST15.java       1.0 05/07/25 *
 * 
 * Meter KE,CT,VT Class.
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
public class ST15 implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9173646684831539361L;
	public static final int OFS_KE = 25;
	public static final int LEN_KE = 6;
	
	public static final int OFS_CT = 38;
	public static final int LEN_CT = 6;
	
	public static final int OFS_VT = 44;
	public static final int LEN_VT = 6;
	
	private byte[] data;
	private double instscale;

    private Log logger = LogFactory.getLog(getClass());
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST15(byte[] data, double instscale) {
		this.data = data;
		this.instscale = instscale;
	}
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST15(double instscale) {
		this.instscale = instscale;
	}
	
	public long getKE() throws Exception {
		return DataFormat.hex2long(
				DataFormat.LSB2MSB(
					DataFormat.select(data,OFS_KE,LEN_KE)));
	}

	/*
	public byte[] parseKE() throws Exception {
		long val = DataFormat.hex2long(
					DataFormat.LSB2MSB(DataFormat.select(data,OFS_KE,LEN_KE)));
		
		return DataFormat.dec2hex((int)(val*Math.pow(10,instscale)));
	}*/
	
	
	public Double getKE(byte[] ke) {
		try {
			return DataFormat.hex2long(DataFormat.LSB2MSB(ke))*instscale;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1.0;
	}

	public Double getCT(byte[] ct) {
		try {
			return DataFormat.hex2long(DataFormat.LSB2MSB(ct))*instscale;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1.0;
	}

	public Double getVT(byte[] vt) {
		try {
			return DataFormat.hex2long(DataFormat.LSB2MSB(vt))*instscale;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1.0;
	}



	
	public byte[] parseCT() throws Exception {
		long val = DataFormat.hex2long(
					DataFormat.LSB2MSB(DataFormat.select(data,OFS_CT,LEN_CT)));
		return DataFormat.dec2hex((int)(val*instscale));
	}
	
	public byte[] parseVT() throws Exception {
		long val = DataFormat.hex2long(
					DataFormat.LSB2MSB(DataFormat.select(data,OFS_VT,LEN_VT)));
		return DataFormat.dec2hex((int)(val*instscale));
	}


}
