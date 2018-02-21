/* 
 * @(#)LK3410CP_IS.java       1.0 07/11/13 *
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
 
package com.aimir.fep.meter.parser.lk3410cpTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class LK3410CP_IS {

	private byte[] rawData = null;

     public static final int LEN_POSITIVE_W_PHA = 4;
     public static final int LEN_POSITIVE_W_PHB = 4;
     public static final int LEN_POSITIVE_W_PHC = 4;
     public static final int LEN_POSITIVE_W_TOTAL = 4;
     
     public static final int LEN_NEGATIVE_W_PHA = 4;
     public static final int LEN_NEGATIVE_W_PHB = 4;
     public static final int LEN_NEGATIVE_W_PHC = 4;
     public static final int LEN_NEGATIVE_W_TOTAL = 4;
     
     public static final int LEN_LAG_VAR_PHA = 4;
     public static final int LEN_LAG_VAR_PHB = 4;
     public static final int LEN_LAG_VAR_PHC = 4;
     public static final int LEN_LAG_VAR_TOTAL = 4;
     
     public static final int LEN_LEAD_VAR_PHA = 4;
     public static final int LEN_LEAD_VAR_PHB = 4;
     public static final int LEN_LEAD_VAR_PHC = 4;
     public static final int LEN_LEAD_VAR_TOTAL = 4;
     
     public static final int LEN_VA_PHA = 4;
     public static final int LEN_VA_PHB = 4;
     public static final int LEN_VA_PHC = 4;
     public static final int LEN_VA_TOTAL = 4;
     
     public static final int LEN_VOLTAGE_PHA = 4;
     public static final int LEN_VOLTAGE_PHB = 4;
     public static final int LEN_VOLTAGE_PHC = 4;
    
     public static final int LEN_CURRENT_PHA = 4;
     public static final int LEN_CURRENT_PHB = 4;
     public static final int LEN_CURRENT_PHC = 4;
     
     public static final int LEN_ENERGY_HIGH = 1;
     
     public static final int LEN_FREQUENCY = 4;
     
     public static final int LEN_PF_PHA = 4;
     public static final int LEN_PF_PHB = 4;
     public static final int LEN_PF_PHC = 4;
    
     public static final int LEN_BATTERY_VOLTAGE = 4;
     
     public static final int LEN_VOLTAGE_THD_PHA = 4;
     public static final int LEN_VOLTAGE_THD_PHB = 4;
     public static final int LEN_VOLTAGE_THD_PHC = 4;
    
     public static final int LEN_CURRENT_THD_PHA = 4;
     public static final int LEN_CURRENT_THD_PHB = 4;
     public static final int LEN_CURRENT_THD_PHC = 4;
     
     ////////////
     public static final int OFS_POSITIVE_W_PHA = 0;
     public static final int OFS_POSITIVE_W_PHB = 4;
     public static final int OFS_POSITIVE_W_PHC = 8;
     public static final int OFS_POSITIVE_W_TOTAL = 12;
     
     public static final int OFS_NEGATIVE_W_PHA = 16;
     public static final int OFS_NEGATIVE_W_PHB = 20;
     public static final int OFS_NEGATIVE_W_PHC = 24;
     public static final int OFS_NEGATIVE_W_TOTAL = 28;
     
     public static final int OFS_LAG_VAR_PHA = 32;
     public static final int OFS_LAG_VAR_PHB = 36;
     public static final int OFS_LAG_VAR_PHC = 40;
     public static final int OFS_LAG_VAR_TOTAL = 44;
     
     public static final int OFS_LEAD_VAR_PHA = 48;
     public static final int OFS_LEAD_VAR_PHB = 52;
     public static final int OFS_LEAD_VAR_PHC = 56;
     public static final int OFS_LEAD_VAR_TOTAL = 60;
     
     public static final int OFS_VA_PHA = 64;
     public static final int OFS_VA_PHB = 68;
     public static final int OFS_VA_PHC = 72;
     public static final int OFS_VA_TOTAL = 76;
     
     public static final int OFS_VOLTAGE_PHA = 80;
     public static final int OFS_VOLTAGE_PHB = 84;
     public static final int OFS_VOLTAGE_PHC = 88;
    
     public static final int OFS_CURRENT_PHA = 92;
     public static final int OFS_CURRENT_PHB = 96;
     public static final int OFS_CURRENT_PHC = 100;
     
     public static final int OFS_ENERGY_HIGH = 104;
     
     public static final int OFS_FREQUENCY = 105;
     
     public static final int OFS_PF_PHA = 109;
     public static final int OFS_PF_PHB = 113;
     public static final int OFS_PF_PHC = 117;
    
     public static final int OFS_BATTERY_VOLTAGE = 121;
     
     public static final int OFS_VOLTAGE_THD_PHA = 125;
     public static final int OFS_VOLTAGE_THD_PHB = 129;
     public static final int OFS_VOLTAGE_THD_PHC = 133;
    
     public static final int OFS_CURRENT_THD_PHA = 137;
     public static final int OFS_CURRENT_THD_PHB = 141;
     public static final int OFS_CURRENT_THD_PHC = 145;
     
    private static Log log = LogFactory.getLog(LK3410CP_IS.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public LK3410CP_IS(byte[] rawData) {
		this.rawData = rawData;
	}
    
    public double getPOSITIVE_W_PHA() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_POSITIVE_W_PHA,LEN_POSITIVE_W_PHA)))*0.001;
    }

    public double getPOSITIVE_W_PHB() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_POSITIVE_W_PHB,LEN_POSITIVE_W_PHB)))*0.001;
    }
    public double getPOSITIVE_W_PHC() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_POSITIVE_W_PHC,LEN_POSITIVE_W_PHC)))*0.001;
    }
    public double getPOSITIVE_W_TOTAL() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_POSITIVE_W_TOTAL,LEN_POSITIVE_W_TOTAL)))*0.001;
    }
    ////////
    public double getNEGATIVE_W_PHA() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_NEGATIVE_W_PHA,LEN_NEGATIVE_W_PHA)))*0.001;
    }
    public double getNEGATIVE_W_PHB() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_NEGATIVE_W_PHB,LEN_NEGATIVE_W_PHB)))*0.001;
    }
    public double getNEGATIVE_W_PHC() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_NEGATIVE_W_PHC,LEN_NEGATIVE_W_PHC)))*0.001;
    }
    public double getNEGATIVE_W_TOTAL() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_NEGATIVE_W_TOTAL,LEN_NEGATIVE_W_TOTAL)))*0.001;
    }
    /////////
    
    public double getLAG_VAR_PHA() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_LAG_VAR_PHA,LEN_LAG_VAR_PHA)))*0.001;
    }
    public double getLAG_VAR_PHB() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_LAG_VAR_PHB,LEN_LAG_VAR_PHB)))*0.001;
    }
    public double getLAG_VAR_PHC() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_LAG_VAR_PHC,LEN_LAG_VAR_PHC)))*0.001;
    }
    public double getLAG_VAR_TOTAL() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_LAG_VAR_TOTAL,LEN_LAG_VAR_TOTAL)))*0.001;
    }
    ////////
    public double getLEAD_VAR_PHA() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_LEAD_VAR_PHA,LEN_LEAD_VAR_PHA)))*0.001;
    }
    public double getLEAD_VAR_PHB() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_LEAD_VAR_PHB,LEN_LEAD_VAR_PHB)))*0.001;
    }
    public double getLEAD_VAR_PHC() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_LEAD_VAR_PHC,LEN_LEAD_VAR_PHC)))*0.001;
    }
    public double getLEAD_VAR_TOTAL() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_LEAD_VAR_TOTAL,LEN_LEAD_VAR_TOTAL)))*0.001;
    }
    //////////
    public double getVA_PHA() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_VA_PHA,LEN_VA_PHA)))*0.001;
    }
    public double getVA_PHB() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_VA_PHB,LEN_VA_PHB)))*0.001;
    }
    public double getVA_PHC() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_VA_PHC,LEN_VA_PHC)))*0.001;
    }
    public double getVA_TOTAL() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_VA_TOTAL,LEN_VA_TOTAL)))*0.001;
    }
    ////////
    public double getVOLTAGE_PHA() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_VOLTAGE_PHA,LEN_VOLTAGE_PHA)))*0.001;
    }
    public double getVOLTAGE_PHB() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(                 
                DataFormat.select(
                    rawData,OFS_VOLTAGE_PHB,LEN_VOLTAGE_PHB)))*0.001;
    }
    public double getVOLTAGE_PHC() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(                  
                DataFormat.select(
                    rawData,OFS_VOLTAGE_PHC,LEN_VOLTAGE_PHC)))*0.001;
    }
    /////////////
    public double getCURRENT_PHA() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_CURRENT_PHA,LEN_CURRENT_PHA)))*0.001;
    }
    public double getCURRENT_PHB() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_CURRENT_PHB,LEN_CURRENT_PHB)))*0.001;
    }
    public double getCURRENT_PHC() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_CURRENT_PHC,LEN_CURRENT_PHC)))*0.001;
    }
    //////////
    
    public double getENERGY_HIGH() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_ENERGY_HIGH,LEN_ENERGY_HIGH)))*0.001;
    }
    
    public double getFREQUENCY() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_FREQUENCY,LEN_FREQUENCY)))*0.001;
    }
    //////////
    public double getPF_PHA() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_PF_PHA,LEN_PF_PHA)))*0.01;
    }
    public double getPF_PHB() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_PF_PHB,LEN_PF_PHB)))*0.01;
    }
    public double getPF_PHC() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_PF_PHC,LEN_PF_PHC)))*0.01;
    }
    ///////////
    public double getBATTERY_VOLTAGE() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_BATTERY_VOLTAGE,LEN_BATTERY_VOLTAGE)))*0.001;
    }
    ////////////
    public double getVOLTAGE_THD_PHA() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_VOLTAGE_THD_PHA,LEN_VOLTAGE_THD_PHA)));
    }
    public double getVOLTAGE_THD_PHB() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_VOLTAGE_THD_PHB,LEN_VOLTAGE_THD_PHB)));
    }
    public double getVOLTAGE_THD_PHC() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_VOLTAGE_THD_PHC,LEN_VOLTAGE_THD_PHC)));
    }
    ///////////////
    
    public double getCURRENT_THD_PHA() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_CURRENT_THD_PHA,LEN_CURRENT_THD_PHA)))*0.01;
    }

    public double getCURRENT_THD_PHB() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_CURRENT_THD_PHB,LEN_CURRENT_THD_PHB)))*0.01;
    }

    public double getCURRENT_THD_PHC() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_CURRENT_THD_PHC,LEN_CURRENT_THD_PHC)))*0.01;
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("LK3410CP_IS Meter DATA[");        
            sb.append("(POSITIVE_W_PHA=").append(""+getPOSITIVE_W_PHA()).append("),");
            sb.append("(POSITIVE_W_PHB=").append(""+getPOSITIVE_W_PHB()).append("),");
            sb.append("(POSITIVE_W_PHC=").append(""+getPOSITIVE_W_PHC()).append("),");
            sb.append("(POSITIVE_W_TOTAL=").append(""+getPOSITIVE_W_TOTAL()).append("),");
            sb.append("(NEGATIVE_W_PHA=").append(""+getNEGATIVE_W_PHA()).append("),");
            sb.append("(NEGATIVE_W_PHB=").append(""+getNEGATIVE_W_PHB()).append("),");
            sb.append("(NEGATIVE_W_PHC=").append(""+getNEGATIVE_W_PHC()).append("),");
            sb.append("(NEGATIVE_W_TOTAL=").append(""+getNEGATIVE_W_TOTAL()).append("),");
            sb.append("(LAG_VAR_PHA=").append(""+getLAG_VAR_PHA()).append("),");
            sb.append("(LAG_VAR_PHB=").append(""+getLAG_VAR_PHB()).append("),");
            sb.append("(LAG_VAR_PHC=").append(""+getLAG_VAR_PHC()).append("),");
            sb.append("(LAG_VAR_TOTAL=").append(""+getLAG_VAR_TOTAL()).append("),");
            sb.append("(LEAD_VAR_PHA=").append(""+getLEAD_VAR_PHA()).append("),");
            sb.append("(LEAD_VAR_PHB=").append(""+getLEAD_VAR_PHB()).append("),");
            sb.append("(LEAD_VAR_PHC=").append(""+getLEAD_VAR_PHC()).append("),");
            sb.append("(LEAD_VAR_TOTAL=").append(""+getLEAD_VAR_TOTAL()).append("),");
            sb.append("(VA_PHA=").append(""+getVA_PHA()).append("),");
            sb.append("(VA_PHB=").append(""+getVA_PHB()).append("),");
            sb.append("(VA_PHC=").append(""+getVA_PHC()).append("),");
            sb.append("(VA_TOTAL=").append(""+getVA_TOTAL()).append("),");
            sb.append("(VOLTAGE_PHA=").append(""+getVOLTAGE_PHA()).append("),");
            sb.append("(VOLTAGE_PHB=").append(""+getVOLTAGE_PHB()).append("),");
            sb.append("(VOLTAGE_PHC=").append(""+getVOLTAGE_PHC()).append("),");
            sb.append("(ENERGY_HIGH=").append(""+getENERGY_HIGH()).append("),");
            sb.append("(FREQUENCY=").append(""+getFREQUENCY()).append("),");
            sb.append("(PF_PHA=").append(""+getPF_PHA()).append("),");
            sb.append("(PF_PHB=").append(""+getPF_PHB()).append("),");
            sb.append("(PF_PHC=").append(""+getPF_PHC()).append("),");
            sb.append("(BATTERY_VOLTAGE=").append(""+getBATTERY_VOLTAGE()).append("),");
            sb.append("(VOLTAGE_THD_PHA=").append(""+getVOLTAGE_THD_PHA()).append("),");
            sb.append("(VOLTAGE_THD_PHB=").append(""+getVOLTAGE_THD_PHB()).append("),");
            sb.append("(VOLTAGE_THD_PHC=").append(""+getVOLTAGE_THD_PHC()).append("),");
            sb.append("(CURRENT_ANGLE_PHA=").append(""+getCURRENT_THD_PHA()).append("),");
            sb.append("(CURRENT_ANGLE_PHB=").append(""+getCURRENT_THD_PHB()).append("),");
            sb.append("(CURRENT_ANGLE_PHC=").append(""+getCURRENT_THD_PHC()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("LK3410CP_IS TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }

}
