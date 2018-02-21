/*
 * @(#)Mk6N_IS.java       1.0 2008/08/21 *
 *
 * Instrument.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. *
 * This software is the confidential and proprietary information of
 * Nuritelcom, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Nuritelecom.
 */

package com.aimir.fep.meter.parser.Mk6NTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * @author kaze kaze@nuritelecom.com
 */
public class Mk6N_PQ implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5292353106765026285L;

	private byte[] rawData = null;

	public float PH_A_FUND_VOLTAGE = 0;
	public float PH_B_FUND_VOLTAGE = 0;
	public float PH_C_FUND_VOLTAGE = 0;
	public float PH_A_VOLTAGE_PQM = 0;
	public float PH_B_VOLTAGE_PQM = 0;
	public float PH_C_VOLTAGE_PQM = 0;
	public float VOLTAGE_Z_SEQ = 0;
	public float VOLTAGE_P_SEQ = 0;
	public float VOLTAGE_N_SEQ = 0;
	public float PH_A_FUND_CURRENT = 0;
	public float PH_B_FUND_CURRENT = 0;
	public float PH_C_FUND_CURRENT = 0;
	public float PH_A_CURRENT_PQM = 0;
	public float PH_B_CURRENT_PQM = 0;
	public float PH_C_CURRENT_PQM = 0;
	public float CURRENT_Z_SEQ = 0;
	public float CURRENT_P_SEQ = 0;
	public float CURRENT_N_SEQ = 0;
	public float THD_PH_A_CURRENT = 0;
	public float THD_PH_B_CURRENT = 0;
	public float THD_PH_C_CURRENT = 0;
	public float THD_PH_A_VOLTAGE = 0;
	public float THD_PH_B_VOLTAGE = 0;
	public float THD_PH_C_VOLTAGE = 0;

	public static final int OFF_PH_A_FUND_VOLTAGE = 0;
	public static final int OFF_PH_B_FUND_VOLTAGE = 4;
	public static final int OFF_PH_C_FUND_VOLTAGE = 8;
	public static final int OFF_PH_A_VOLTAGE_PQM = 12;
	public static final int OFF_PH_B_VOLTAGE_PQM = 16;
	public static final int OFF_PH_C_VOLTAGE_PQM = 20;
	public static final int OFF_VOLTAGE_Z_SEQ = 24;
	public static final int OFF_VOLTAGE_P_SEQ = 28;
	public static final int OFF_VOLTAGE_N_SEQ = 32;
	public static final int OFF_PH_A_FUND_CURRENT = 36;
	public static final int OFF_PH_B_FUND_CURRENT = 40;
	public static final int OFF_PH_C_FUND_CURRENT = 44;
	public static final int OFF_PH_A_CURRENT_PQM = 48;
	public static final int OFF_PH_B_CURRENT_PQM = 52;
	public static final int OFF_PH_C_CURRENT_PQM = 56;
	public static final int OFF_CURRENT_Z_SEQ = 60;
	public static final int OFF_CURRENT_P_SEQ = 64;
	public static final int OFF_CURRENT_N_SEQ = 68;
	public static final int OFF_THD_PH_A_CURRENT = 72;
	public static final int OFF_THD_PH_B_CURRENT = 76;
	public static final int OFF_THD_PH_C_CURRENT = 80;
	public static final int OFF_THD_PH_A_VOLTAGE = 84;
	public static final int OFF_THD_PH_B_VOLTAGE = 88;
	public static final int OFF_THD_PH_C_VOLTAGE = 92;

	public static final int LEN_PH_A_FUND_VOLTAGE = 4;
	public static final int LEN_PH_B_FUND_VOLTAGE = 4;
	public static final int LEN_PH_C_FUND_VOLTAGE = 4;
	public static final int LEN_PH_A_VOLTAGE_PQM = 4;
	public static final int LEN_PH_B_VOLTAGE_PQM = 4;
	public static final int LEN_PH_C_VOLTAGE_PQM = 4;
	public static final int LEN_VOLTAGE_Z_SEQ = 4;
	public static final int LEN_VOLTAGE_P_SEQ = 4;
	public static final int LEN_VOLTAGE_N_SEQ = 4;
	public static final int LEN_PH_A_FUND_CURRENT = 4;
	public static final int LEN_PH_B_FUND_CURRENT = 4;
	public static final int LEN_PH_C_FUND_CURRENT = 4;
	public static final int LEN_PH_A_CURRENT_PQM = 4;
	public static final int LEN_PH_B_CURRENT_PQM = 4;
	public static final int LEN_PH_C_CURRENT_PQM = 4;
	public static final int LEN_CURRENT_Z_SEQ = 4;
	public static final int LEN_CURRENT_P_SEQ = 4;
	public static final int LEN_CURRENT_N_SEQ = 4;
	public static final int LEN_THD_PH_A_CURRENT = 4;
	public static final int LEN_THD_PH_B_CURRENT = 4;
	public static final int LEN_THD_PH_C_CURRENT = 4;
	public static final int LEN_THD_PH_A_VOLTAGE = 4;
	public static final int LEN_THD_PH_B_VOLTAGE = 4;
	public static final int LEN_THD_PH_C_VOLTAGE = 4;


    private static Log log = LogFactory.getLog(Mk6N_PQ.class);

	/**
	 * Constructor .<p>
	 *
	 * @param data - read data (header,crch,crcl)
	 */
	public Mk6N_PQ(byte[] rawData) {
		this.rawData = rawData;
	}

	public Double getPH_A_FUND_VOLTAGE() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_A_FUND_VOLTAGE, LEN_PH_A_FUND_VOLTAGE));
	}

	public Double getPH_B_FUND_VOLTAGE() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_B_FUND_VOLTAGE, LEN_PH_B_FUND_VOLTAGE));
	}

	public Double getPH_C_FUND_VOLTAGE() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_C_FUND_VOLTAGE, LEN_PH_C_FUND_VOLTAGE));
	}

	public Double getPH_A_VOLTAGE_PQM() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_A_VOLTAGE_PQM, LEN_PH_A_VOLTAGE_PQM));
	}

	public Double getPH_B_VOLTAGE_PQM() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_B_VOLTAGE_PQM, LEN_PH_B_VOLTAGE_PQM));
	}

	public Double getPH_C_VOLTAGE_PQM() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_C_VOLTAGE_PQM, LEN_PH_C_VOLTAGE_PQM));
	}

	public Double getVOLTAGE_Z_SEQ() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_VOLTAGE_Z_SEQ, LEN_VOLTAGE_Z_SEQ));
	}

	public Double getVOLTAGE_P_SEQ() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_VOLTAGE_P_SEQ, LEN_VOLTAGE_P_SEQ));
	}

	public Double getVOLTAGE_N_SEQ() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_VOLTAGE_N_SEQ, LEN_VOLTAGE_N_SEQ));
	}

	public Double getPH_A_FUND_CURRENT() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_A_FUND_CURRENT, LEN_PH_A_FUND_CURRENT));
	}

	public Double getPH_B_FUND_CURRENT() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_B_FUND_CURRENT, LEN_PH_B_FUND_CURRENT));
	}

	public Double getPH_C_FUND_CURRENT() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_C_FUND_CURRENT, LEN_PH_C_FUND_CURRENT));
	}

	public Double getPH_A_CURRENT_PQM() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_A_CURRENT_PQM, LEN_PH_A_CURRENT_PQM));
	}

	public Double getPH_B_CURRENT_PQM() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_B_CURRENT_PQM, LEN_PH_B_CURRENT_PQM));
	}

	public Double getPH_C_CURRENT_PQM() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_C_CURRENT_PQM, LEN_PH_C_CURRENT_PQM));
	}

	public Double getCURRENT_Z_SEQ() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_CURRENT_Z_SEQ, LEN_CURRENT_Z_SEQ));
	}

	public Double getCURRENT_P_SEQ() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_CURRENT_P_SEQ, LEN_CURRENT_P_SEQ));
	}

	public Double getCURRENT_N_SEQ() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_CURRENT_N_SEQ, LEN_CURRENT_N_SEQ));
	}

	public Double getTHD_PH_A_CURRENT() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_THD_PH_A_CURRENT, LEN_THD_PH_A_CURRENT));
	}

	public Double getTHD_PH_B_CURRENT() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_THD_PH_B_CURRENT, LEN_THD_PH_B_CURRENT));
	}

	public Double getTHD_PH_C_CURRENT() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_THD_PH_C_CURRENT, LEN_THD_PH_C_CURRENT));
	}

	public Double getTHD_PH_A_VOLTAGE() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_THD_PH_A_VOLTAGE, LEN_THD_PH_A_VOLTAGE));
	}

	public Double getTHD_PH_B_VOLTAGE() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_THD_PH_B_VOLTAGE, LEN_THD_PH_B_VOLTAGE));
	}

	public Double getTHD_PH_C_VOLTAGE() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_THD_PH_C_VOLTAGE, LEN_THD_PH_C_VOLTAGE));
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
		    retValue.append("Mk6N_PQ [ ")
		        .append("PH_A_FUND_VOLTAGE = ").append(getPH_A_FUND_VOLTAGE()).append('\n')
		        .append("PH_B_FUND_VOLTAGE = ").append(getPH_B_FUND_VOLTAGE()).append('\n')
		        .append("PH_C_FUND_VOLTAGE = ").append(getPH_C_FUND_VOLTAGE()).append('\n')
		        .append("PH_A_VOLTAGE_PQM = ").append(getPH_A_VOLTAGE_PQM()).append('\n')
		        .append("PH_B_VOLTAGE_PQM = ").append(getPH_B_VOLTAGE_PQM()).append('\n')
		        .append("PH_C_VOLTAGE_PQM = ").append(getPH_C_VOLTAGE_PQM()).append('\n')
		        .append("VOLTAGE_Z_SEQ = ").append(getVOLTAGE_Z_SEQ()).append('\n')
		        .append("VOLTAGE_P_SEQ = ").append(getVOLTAGE_P_SEQ()).append('\n')
		        .append("VOLTAGE_N_SEQ = ").append(getVOLTAGE_N_SEQ()).append('\n')
		        .append("PH_A_FUND_CURRENT = ").append(getPH_A_FUND_CURRENT()).append('\n')
		        .append("PH_B_FUND_CURRENT = ").append(getPH_B_FUND_CURRENT()).append('\n')
		        .append("PH_C_FUND_CURRENT = ").append(getPH_C_FUND_CURRENT()).append('\n')
		        .append("PH_A_CURRENT_PQM = ").append(getPH_A_CURRENT_PQM()).append('\n')
		        .append("PH_B_CURRENT_PQM = ").append(getPH_B_CURRENT_PQM()).append('\n')
		        .append("PH_C_CURRENT_PQM = ").append(getPH_C_CURRENT_PQM()).append('\n')
		        .append("CURRENT_Z_SEQ = ").append(getCURRENT_Z_SEQ()).append('\n')
		        .append("CURRENT_P_SEQ = ").append(getCURRENT_P_SEQ()).append('\n')
		        .append("CURRENT_N_SEQ = ").append(getCURRENT_N_SEQ()).append('\n')
		        .append("THD_PH_A_CURRENT = ").append(getTHD_PH_A_CURRENT()).append('\n')
		        .append("THD_PH_B_CURRENT = ").append(getTHD_PH_B_CURRENT()).append('\n')
		        .append("THD_PH_C_CURRENT = ").append(getTHD_PH_C_CURRENT()).append('\n')
		        .append("THD_PH_A_VOLTAGE = ").append(getTHD_PH_A_VOLTAGE()).append('\n')
		        .append("THD_PH_B_VOLTAGE = ").append(getTHD_PH_B_VOLTAGE()).append('\n')
		        .append("THD_PH_C_VOLTAGE = ").append(getTHD_PH_C_VOLTAGE()).append('\n')
		        .append(" ]");
	    }catch(Exception e){
	    	log.error("Mk6N_PQ TO STRING ERR=>"+e.getMessage());
	    }
	    return retValue.toString();
	}
}
