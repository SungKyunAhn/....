/* 
 * @(#)SCE8711_IS.java       1.0 09/05/12 *
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
 
package com.aimir.fep.meter.parser.actarisSCE8711Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * @author Kang, Soyi
 */
public class SCE8711_IS {

	private byte[] rawData = null;

	public static final int OFS_VOLTAGE_PHA = 0;
	public static final int OFS_VOLTAGE_PHB = 2;
	public static final int OFS_VOLTAGE_PHC = 4;
    
	public static final int OFS_CURRENT_PHA = 6;
	public static final int OFS_CURRENT_PHB = 8;
	public static final int OFS_CURRENT_PHC = 10;
	
	public static final int LEN_IS_DATA = 2;
	
    private Log log = LogFactory.getLog(SCE8711_IS.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public SCE8711_IS(byte[] rawData) {
		this.rawData = rawData;
	}

    ////////
    public double getVOLTAGE_PHA() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.select(
                    rawData,OFS_VOLTAGE_PHA,LEN_IS_DATA))*0.01;
    }
    public double getVOLTAGE_PHB() throws Exception {
        return DataFormat.hex2dec(               
                DataFormat.select(
                    rawData,OFS_VOLTAGE_PHB,LEN_IS_DATA))*0.01;
    }
    public double getVOLTAGE_PHC() throws Exception {
        return DataFormat.hex2dec(            
                DataFormat.select(
                    rawData,OFS_VOLTAGE_PHC,LEN_IS_DATA))*0.01;
    }
    /////////////
    public double getCURRENT_PHA() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.select(
                    rawData,OFS_CURRENT_PHA,LEN_IS_DATA))*0.01;
    }
    public double getCURRENT_PHB() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.select(
                    rawData,OFS_CURRENT_PHB,LEN_IS_DATA))*0.01;
    }
    public double getCURRENT_PHC() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.select(
                    rawData,OFS_CURRENT_PHC,LEN_IS_DATA))*0.01;
    }
        
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("SCE8711_IS_IS Meter DATA[");        
            sb.append("(VOLTAGE_PHA=").append(""+getVOLTAGE_PHA()).append("),");
            sb.append("(VOLTAGE_PHB=").append(""+getVOLTAGE_PHB()).append("),");
            sb.append("(VOLTAGE_PHC=").append(""+getVOLTAGE_PHC()).append("),");
            sb.append("(CURRENT_PHA=").append(""+getCURRENT_PHA()).append("),");
            sb.append("(CURRENT_PHB=").append(""+getCURRENT_PHB()).append("),");
            sb.append("(CURRENT_PHC=").append(""+getCURRENT_PHC()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("SCE8711_IS TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }

}
