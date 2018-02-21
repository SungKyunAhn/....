/* 
 * @(#)NURI_IS.java       1.0 07/05/02 *
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
 
package com.aimir.fep.meter.parser.kV2cTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class NURI_IS {
	
    public static final int OFS_PRESENT_REGISTER = 0;
    public static final int OFS_VI_ANGLES = 167;
    public static final int OFS_VI_MAGNITUDES = 179;
    
    public static final int LEN_PRESENT_REGISTER = 167;
    public static final int LEN_VI_ANGLES = 12;
    public static final int LEN_VI_MAGNITUDES = 12;
    
    public static final int OFS_CURRENT_ANGLE_PHA    = 167;
    public static final int OFS_VOLTAGE_ANGLE_PHA    = 169;
    public static final int OFS_CURRENT_ANGLE_PHB    = 171;
    public static final int OFS_VOLTAGE_ANGLE_PHB    = 173;
    public static final int OFS_CURRENT_ANGLE_PHC    = 175;
    public static final int OFS_VOLTAGE_ANGLE_PHC    = 177;
    
    public static final int OFS_CURRENT_MAG_PHA      = 179;
    public static final int OFS_VOLTAGE_MAG_PHA      = 181;
    public static final int OFS_CURRENT_MAG_PHB      = 183;
    public static final int OFS_VOLTAGE_MAG_PHB      = 185;
    public static final int OFS_CURRENT_MAG_PHC      = 187;
    public static final int OFS_VOLTAGE_MAG_PHC      = 189;
    
    public static final int LEN_CURRENT_ANGLE_PHA    = 2;
    public static final int LEN_VOLTAGE_ANGLE_PHA    = 2;
    public static final int LEN_CURRENT_ANGLE_PHB    = 2;
    public static final int LEN_VOLTAGE_ANGLE_PHB    = 2;
    public static final int LEN_CURRENT_ANGLE_PHC    = 2;
    public static final int LEN_VOLTAGE_ANGLE_PHC    = 2;
    
    public static final int LEN_CURRENT_MAG_PHA      = 2;
    public static final int LEN_VOLTAGE_MAG_PHA      = 2;
    public static final int LEN_CURRENT_MAG_PHB      = 2;
    public static final int LEN_VOLTAGE_MAG_PHB      = 2;
    public static final int LEN_CURRENT_MAG_PHC      = 2;
    public static final int LEN_VOLTAGE_MAG_PHC      = 2;
    
	private byte[] rawData = null;

    private static Log log = LogFactory.getLog(NURI_IS.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public NURI_IS(byte[] rawData) {
		this.rawData = rawData;
	}
    
    public MT110 getMT110() throws Exception {
        MT110 mt110 = new MT110(
            DataFormat.select(rawData,OFS_PRESENT_REGISTER,LEN_PRESENT_REGISTER));
        return mt110;
    }
    
    public double getCURRENT_ANGLE_PHA() throws Exception {
        
        int val = 0;
        val =  DataFormat.hex2dec(
            DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_CURRENT_ANGLE_PHA,LEN_CURRENT_ANGLE_PHA)));
        
        if(val != 32767 && val > 0){
            return val/10;
        }else{
            return val;
        }
    }
    
    public double getVOLTAGE_ANGLE_PHA() throws Exception {
        
        int val = 0;
        val = DataFormat.hex2dec(
            DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_CURRENT_ANGLE_PHA,LEN_CURRENT_ANGLE_PHA)));
        
        if(val != 32767 && val > 0){
            return val/10;
        }else{
            return val;
        }
    }
    
    public double getCURRENT_ANGLE_PHB() throws Exception {
        
        int val = 0;
        val = DataFormat.hex2dec(
            DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_CURRENT_ANGLE_PHB,LEN_CURRENT_ANGLE_PHB)));
        
        if(val != 32767 && val > 0){
            return val/10;
        }else{
            return val;
        }
    }
    
    public double getVOLTAGE_ANGLE_PHB() throws Exception {
        
        int val = 0;
        val = DataFormat.hex2dec(
            DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_VOLTAGE_ANGLE_PHB,LEN_VOLTAGE_ANGLE_PHB)));
        
        if(val != 32767 && val > 0){
            return val/10;
        }else{
            return val;
        }
    }
    
    public double getCURRENT_ANGLE_PHC() throws Exception {
        
        int val = 0;
        val = DataFormat.hex2dec(
            DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_CURRENT_ANGLE_PHC,LEN_CURRENT_ANGLE_PHC)));    
        
        if(val != 32767 && val > 0){
            return val/10;
        }else{
            return val;
        }
    }
    
    public double getVOLTAGE_ANGLE_PHC() throws Exception {
        
        int val = 0;
        val = DataFormat.hex2dec(
            DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_VOLTAGE_ANGLE_PHC,LEN_VOLTAGE_ANGLE_PHC)));    
        
        if(val != 32767 && val > 0){
            return val/10;
        }else{
            return val;
        }
    }

    public int getCURRENT_MAG_PHA() throws Exception {
        return DataFormat.hex2dec(
            DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_CURRENT_MAG_PHA,LEN_CURRENT_MAG_PHA)));
    }
    
    public int getVOLTAGE_MAG_PHA() throws Exception {
        return DataFormat.hex2dec(
            DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_VOLTAGE_MAG_PHA,LEN_VOLTAGE_MAG_PHA)));
    }
    
    public int getCURRENT_MAG_PHB() throws Exception {
        return DataFormat.hex2dec(
            DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_CURRENT_MAG_PHB,LEN_CURRENT_MAG_PHB)));
    }
    
    public int getVOLTAGE_MAG_PHB() throws Exception {
        return DataFormat.hex2dec(
            DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_VOLTAGE_MAG_PHB,LEN_VOLTAGE_MAG_PHB)));
    }
    
    public int getCURRENT_MAG_PHC() throws Exception {
        return DataFormat.hex2dec(
            DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_CURRENT_MAG_PHC,LEN_CURRENT_MAG_PHC)));
    }
    
    public int getVOLTAGE_MAG_PHC() throws Exception {
        return DataFormat.hex2dec(
            DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_VOLTAGE_MAG_PHC,LEN_VOLTAGE_MAG_PHC)));
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("NURI_IS Meter DATA[");        
            sb.append("(CURRENT_ANGLE_PHA=").append(getCURRENT_ANGLE_PHA()).append("),");
            sb.append("(VOLTAGE_ANGLE_PHA=").append(getVOLTAGE_ANGLE_PHA()).append("),");
            sb.append("(CURRENT_ANGLE_PHB=").append(getCURRENT_ANGLE_PHB()).append("),");
            sb.append("(VOLTAGE_ANGLE_PHB=").append(getVOLTAGE_ANGLE_PHB()).append("),");
            sb.append("(CURRENT_ANGLE_PHC=").append(getCURRENT_ANGLE_PHC()).append("),");
            sb.append("(VOLTAGE_ANGLE_PHC=").append(getVOLTAGE_ANGLE_PHC()).append("),");
            sb.append("(CURRENT_MAG_PHA=").append(getCURRENT_MAG_PHA()).append("),");
            sb.append("(VOLTAGE_MAG_PHA=").append(getVOLTAGE_MAG_PHA()).append("),");
            sb.append("(CURRENT_MAG_PHB=").append(getCURRENT_MAG_PHB()).append("),");
            sb.append("(VOLTAGE_MAG_PHB=").append(getVOLTAGE_MAG_PHB()).append("),");
            sb.append("(CURRENT_MAG_PHC=").append(getCURRENT_MAG_PHC()).append("),");
            sb.append("(VOLTAGE_MAG_PHC=").append(getVOLTAGE_MAG_PHC()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("NURI_MDM TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }

}
