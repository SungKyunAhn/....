/*
 * @(#)Mk10_IS.java       1.0 2011/08/12 *
 *
 * Instrument.
 * Copyright (c) 2011-2012 NuriTelecom, Inc.
 * All rights reserved. *
 * This software is the confidential and proprietary information of
 * Nuritelcom, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Nuritelecom.
 */

package com.aimir.fep.meter.parser.Mk10Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

public class Mk10_IS implements java.io.Serializable{

	private static final long serialVersionUID = 6071855459581160694L;

	private byte[] rawData = null;
	
	private byte[] PH_A_VOLTAGE = new byte[4];
	private byte[] PH_B_VOLTAGE = new byte[4];
	private byte[] PH_C_VOLTAGE = new byte[4];
	private byte[] PH_A_CURRENT = new byte[4];
	private byte[] PH_B_CURRENT = new byte[4];
	private byte[] PH_C_CURRENT = new byte[4];
	private byte[] PH_A_ANGLE = new byte[4];
	private byte[] PH_B_ANGLE = new byte[4];
	private byte[] PH_C_ANGLE = new byte[4];
	private byte[] PH_A_WATT = new byte[4];
	private byte[] PH_B_WATT = new byte[4];
	private byte[] PH_C_WATT = new byte[4];
	private byte[] PH_A_VAR = new byte[4];
	private byte[] PH_B_VAR = new byte[4];
	private byte[] PH_C_VAR = new byte[4];
	private byte[] PH_A_VA = new byte[4];
	private byte[] PH_B_VA = new byte[4];
	private byte[] PH_C_VA = new byte[4];
	private byte[] FREQUENCY = new byte[4];
	private byte[] PH_A_FUND_VOLTAGE = new byte[4];
	private byte[] PH_B_FUND_VOLTAGE = new byte[4];
	private byte[] PH_C_FUND_VOLTAGE = new byte[4];
	private byte[] PH_A_VOLTAGE_PQM = new byte[4];
	private byte[] PH_B_VOLTAGE_PQM = new byte[4];
	private byte[] PH_C_VOLTAGE_PQM = new byte[4];
	private byte[] VOLTAGE_Z_SEQ = new byte[4];
	private byte[] VOLTAGE_P_SEQ = new byte[4];
	private byte[] VOLTAGE_N_SEQ = new byte[4];

    private static Log log = LogFactory.getLog(Mk10_IS.class);

	/**
	 * Constructor .<p>
	 *
	 * @param data - read data (header,crch,crcl)
	 */
	public Mk10_IS(byte[] rawData) {
		this.rawData = rawData;
		parse();
	}
	
	public void parse() {
		int pos = 0;
		
		System.arraycopy(rawData, pos, FREQUENCY, 0, FREQUENCY.length);
		pos += FREQUENCY.length;		
		
		System.arraycopy(rawData, pos, PH_A_VOLTAGE, 0, PH_A_VOLTAGE.length);
		pos += PH_A_VOLTAGE.length;
		System.arraycopy(rawData, pos, PH_B_VOLTAGE, 0, PH_B_VOLTAGE.length);
		pos += PH_B_VOLTAGE.length;
		System.arraycopy(rawData, pos, PH_C_VOLTAGE, 0, PH_C_VOLTAGE.length);
		pos += PH_C_VOLTAGE.length;
		System.arraycopy(rawData, pos, PH_A_CURRENT, 0, PH_A_CURRENT.length);
		pos += PH_A_CURRENT.length;
		System.arraycopy(rawData, pos, PH_B_CURRENT, 0, PH_B_CURRENT.length);
		pos += PH_B_CURRENT.length;
		System.arraycopy(rawData, pos, PH_C_CURRENT, 0, PH_C_CURRENT.length);
		pos += PH_C_CURRENT.length;
		System.arraycopy(rawData, pos, PH_A_ANGLE, 0, PH_A_ANGLE.length);
		pos += PH_A_ANGLE.length;
		System.arraycopy(rawData, pos, PH_B_ANGLE, 0, PH_B_ANGLE.length);
		pos += PH_B_ANGLE.length;
		System.arraycopy(rawData, pos, PH_C_ANGLE, 0, PH_C_ANGLE.length);
		pos += PH_C_ANGLE.length;
		System.arraycopy(rawData, pos, PH_A_WATT, 0, PH_A_WATT.length);
		pos += PH_A_WATT.length;
		System.arraycopy(rawData, pos, PH_B_WATT, 0, PH_B_WATT.length);
		pos += PH_B_WATT.length;
		System.arraycopy(rawData, pos, PH_C_WATT, 0, PH_C_WATT.length);
		pos += PH_C_WATT.length;
		System.arraycopy(rawData, pos, PH_A_VAR, 0, PH_A_VAR.length);
		pos += PH_A_VAR.length;
		System.arraycopy(rawData, pos, PH_B_VAR, 0, PH_B_VAR.length);
		pos += PH_B_VAR.length;
		System.arraycopy(rawData, pos, PH_C_VAR, 0, PH_C_VAR.length);
		pos += PH_C_VAR.length;
		System.arraycopy(rawData, pos, PH_A_VA, 0, PH_A_VA.length);
		pos += PH_A_VA.length;
		System.arraycopy(rawData, pos, PH_B_VA, 0, PH_B_VA.length);
		pos += PH_B_VA.length;
		System.arraycopy(rawData, pos, PH_C_VA, 0, PH_C_VA.length);
		pos += PH_C_VA.length;
		
		System.arraycopy(rawData, pos, PH_A_FUND_VOLTAGE, 0, PH_A_FUND_VOLTAGE.length);
		pos += PH_A_FUND_VOLTAGE.length;
		System.arraycopy(rawData, pos, PH_B_FUND_VOLTAGE, 0, PH_B_FUND_VOLTAGE.length);
		pos += PH_B_FUND_VOLTAGE.length;
		System.arraycopy(rawData, pos, PH_C_FUND_VOLTAGE, 0, PH_C_FUND_VOLTAGE.length);
		pos += PH_C_FUND_VOLTAGE.length;
		System.arraycopy(rawData, pos, PH_A_VOLTAGE_PQM, 0, PH_A_VOLTAGE_PQM.length);
		pos += PH_A_VOLTAGE_PQM.length;
		System.arraycopy(rawData, pos, PH_B_VOLTAGE_PQM, 0, PH_B_VOLTAGE_PQM.length);
		pos += PH_B_VOLTAGE_PQM.length;
		System.arraycopy(rawData, pos, PH_C_VOLTAGE_PQM, 0, PH_C_VOLTAGE_PQM.length);
		pos += PH_C_VOLTAGE_PQM.length;
		System.arraycopy(rawData, pos, VOLTAGE_Z_SEQ, 0, VOLTAGE_Z_SEQ.length);
		pos += VOLTAGE_Z_SEQ.length;
		System.arraycopy(rawData, pos, VOLTAGE_P_SEQ, 0, VOLTAGE_P_SEQ.length);
		pos += VOLTAGE_P_SEQ.length;
		System.arraycopy(rawData, pos, VOLTAGE_N_SEQ, 0, VOLTAGE_N_SEQ.length);
		pos += VOLTAGE_N_SEQ.length;

	}


	public float getFREQUENCY() throws Exception {
		return DataFormat.bytesToFloat(FREQUENCY);
	}

	public float getPH_A_VOLTAGE() throws Exception {
		return DataFormat.bytesToFloat(PH_A_VOLTAGE);
	}

	public float getPH_B_VOLTAGE() throws Exception {
		return DataFormat.bytesToFloat(PH_B_VOLTAGE);
	}

	public float getPH_C_VOLTAGE() throws Exception {
		return DataFormat.bytesToFloat(PH_C_VOLTAGE);
	}

	public float getPH_A_CURRENT() throws Exception {
		return DataFormat.bytesToFloat(PH_A_CURRENT);
	}

	public float getPH_B_CURRENT() throws Exception {
		return DataFormat.bytesToFloat(PH_B_CURRENT);
	}

	public float getPH_C_CURRENT() throws Exception {
		return DataFormat.bytesToFloat(PH_C_CURRENT);
	}

	public float getPH_A_ANGLE() throws Exception {
		return DataFormat.bytesToFloat(PH_A_ANGLE);
	}

	public float getPH_B_ANGLE() throws Exception {
		return DataFormat.bytesToFloat(PH_B_ANGLE);
	}

	public float getPH_C_ANGLE() throws Exception {
		return DataFormat.bytesToFloat(PH_C_ANGLE);
	}

	public float getPH_A_WATT() throws Exception {
		return DataFormat.bytesToFloat(PH_A_WATT)/1000;
	}

	public float getPH_B_WATT() throws Exception {
		return DataFormat.bytesToFloat(PH_B_WATT)/1000;
	}

	public float getPH_C_WATT() throws Exception {
		return DataFormat.bytesToFloat(PH_C_WATT)/1000;
	}

	public float getPH_A_VAR() throws Exception {
		return DataFormat.bytesToFloat(PH_A_VAR)/1000;
	}

	public float getPH_B_VAR() throws Exception {
		return DataFormat.bytesToFloat(PH_B_VAR)/1000;
	}

	public float getPH_C_VAR() throws Exception {
		return DataFormat.bytesToFloat(PH_C_VAR)/1000;
	}

	public float getPH_A_VA() throws Exception {
		return DataFormat.bytesToFloat(PH_A_VA)/1000;
	}

	public float getPH_B_VA() throws Exception {
		return DataFormat.bytesToFloat(PH_B_VA)/1000;
	}

	public float getPH_C_VA() throws Exception {
		return DataFormat.bytesToFloat(PH_C_VA)/1000;
	}
	
	public float getPH_A_FUND_VOLTAGE() throws Exception {
		return DataFormat.bytesToFloat(PH_A_FUND_VOLTAGE);
	}
	public float getPH_B_FUND_VOLTAGE() throws Exception {
		return DataFormat.bytesToFloat(PH_B_FUND_VOLTAGE);
	}
	public float getPH_C_FUND_VOLTAGE() throws Exception {
		return DataFormat.bytesToFloat(PH_C_FUND_VOLTAGE);
	}
	
	public float getPH_A_VOLTAGE_PQM() throws Exception {
		return DataFormat.bytesToFloat(PH_A_VOLTAGE_PQM);
	}
	
	public float getPH_B_VOLTAGE_PQM() throws Exception {
		return DataFormat.bytesToFloat(PH_B_VOLTAGE_PQM);
	}
	
	public float getPH_C_VOLTAGE_PQM() throws Exception {
		return DataFormat.bytesToFloat(PH_C_VOLTAGE_PQM);
	}
	
	public float getVOLTAGE_Z_SEQ() throws Exception {
		return DataFormat.bytesToFloat(VOLTAGE_Z_SEQ);
	}
	
	public float getVOLTAGE_P_SEQ() throws Exception {
		return DataFormat.bytesToFloat(VOLTAGE_P_SEQ);
	}
	
	public float getVOLTAGE_N_SEQ() throws Exception {
		return DataFormat.bytesToFloat(VOLTAGE_N_SEQ);
	}
	
	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation
	 * of this object.
	 */
	public String toString()
	{
	    StringBuffer retValue = new StringBuffer();
	    try{
	    retValue.append("Mk10_IS [ ")
	        .append("FREQUENCY = ").append(getFREQUENCY()).append('\n')
	        .append("PH_A_VOLTAGE = ").append(getPH_A_VOLTAGE()).append('\n')
	        .append("PH_B_VOLTAGE = ").append(getPH_B_VOLTAGE()).append('\n')
	        .append("PH_C_VOLTAGE = ").append(getPH_C_VOLTAGE()).append('\n')
	        .append("PH_A_CURRENT = ").append(getPH_A_CURRENT()).append('\n')
	        .append("PH_B_CURRENT = ").append(getPH_B_CURRENT()).append('\n')
	        .append("PH_C_CURENT = ").append(getPH_C_CURRENT()).append('\n')
	        .append("PH_A_ANGLE = ").append(getPH_A_ANGLE()).append('\n')
	        .append("PH_B_ANGLE = ").append(getPH_B_ANGLE()).append('\n')
	        .append("PH_C_ANGLE = ").append(getPH_C_ANGLE()).append('\n')
	        .append("PH_A_WATT = ").append(getPH_A_WATT()).append('\n')
	        .append("PH_B_WATT = ").append(getPH_B_WATT()).append('\n')
	        .append("PH_C_WATT = ").append(getPH_C_WATT()).append('\n')
	        .append("PH_A_VAR = ").append(getPH_A_VAR()).append('\n')
	        .append("PH_B_VAR = ").append(getPH_B_VAR()).append('\n')
	        .append("PH_C_VAR = ").append(getPH_C_VAR()).append('\n')
	        .append("PH_A_VA = ").append(getPH_A_VA()).append('\n')
	        .append("PH_B_VA = ").append(getPH_B_VA()).append('\n')
	        .append("PH_C_VA = ").append(getPH_C_VA()).append('\n')
	        .append("PH_A_FUND_VOLTAGE = ").append(getPH_A_FUND_VOLTAGE()).append('\n')
	        .append("PH_B_FUND_VOLTAGE = ").append(getPH_B_FUND_VOLTAGE()).append('\n')
	        .append("PH_C_FUND_VOLTAGE = ").append(getPH_C_FUND_VOLTAGE()).append('\n')
	        .append("PH_A_VOLTAGE_PQM = ").append(getPH_A_VOLTAGE_PQM()).append('\n')
	        .append("PH_B_VOLTAGE_PQM = ").append(getPH_B_VOLTAGE_PQM()).append('\n')
	        .append("PH_C_VOLTAGE_PQM = ").append(getPH_C_VOLTAGE_PQM()).append('\n')
	        .append("VOLTAGE_Z_SEQ = ").append(getVOLTAGE_Z_SEQ()).append('\n')
	        .append("VOLTAGE_P_SEQ = ").append(getVOLTAGE_P_SEQ()).append('\n')
	        .append("VOLTAGE_N_SEQ = ").append(getVOLTAGE_N_SEQ()).append('\n')
	        .append(" ]");
	    }catch(Exception e){
	    	log.error("Mk10_IS TO STRING ERR=>"+e.getMessage());
	    }
	    return retValue.toString();
	}
}
