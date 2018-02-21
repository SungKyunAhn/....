/** 
 * @(#)LPIntervalStatus.java       1.0 07/11/09 *
 * 
 * Actual Dimension Register Table.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
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
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class LPIntervalStatus {
	
    public static final short POWER_RECOVERY = (short)0x8000;
    public static final short POWER_FAILURE = (short)0x4000;
    public static final short TIME_CHANGE  = (short)0x2000;
    public static final short REVERSION  = (short)0x1000;
    public static final short BATT_REPLACEMENT  = (short)0x0800;
    public static final short MISSING_PHASE  = (short)0x0400;
    public static final short SEASON_CHANGE  = (short)0x0200;
    public static final short PROGRAM_CHAGNE  = (short)0x0100;
    public static final short METERING  = (short)0x0004;
    public static final short TIME_RATE  = (short)0x0003;
           
	private byte[] data;
    private short dataValue =0;
    private static Log log = LogFactory.getLog(LPIntervalStatus.class);
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public LPIntervalStatus(byte[] data)  throws Exception {
		this.data = data;
        dataValue = (short)DataFormat.hex2dec(data);
	}
	
    /**
     * POWER_RECOVERY
     */
    public boolean getPOWER_RECOVERY() {
        short val =(short)((dataValue&POWER_RECOVERY) >> 15);
        if(val == 1){
            return true;
        }
        return false;
    }
    
    /**
     * POWER_FAILURE
     */
    public boolean getPOWER_FAILURE() {
        short val =(short)((dataValue&POWER_FAILURE) >> 14);
        if(val == 1){
            return true;
        }
        return false;
    }
    /**
     * TIME_CHANGE
     */
    public boolean getTIME_CHANGE() {
        short val =(short)((dataValue&TIME_CHANGE) >> 13);
        if(val == 1){
            return true;
        }
        return false;
    }
    
    /**
     * REVERSION
     */
    public boolean getREVERSION() {
        short val =(short)((dataValue&REVERSION) >> 12);
        if(val == 1){
            return true;
        }
        return false;
    }
    
    /**
     * BATT_REPLACEMENT
     */
    public boolean getBATT_REPLACEMENT() {
        short val =(short)((dataValue&BATT_REPLACEMENT) >> 11);
        if(val == 1){
            return true;
        }
        return false;
    }
    
    /**
     * MISSING_PHASE
     */
    public boolean getMISSING_PHASE() {
        short val =(short)((dataValue&MISSING_PHASE) >> 10);
        if(val == 1){
            return true;
        }
        return false;
    }
    
    /**
     * SEASON_CHANGE
     */
    public boolean getSEASON_CHANGE() {
        short val =(short)((dataValue&SEASON_CHANGE) >> 9);
        if(val == 1){
            return true;
        }
        return false;
    }
    
    /**
     * PROGRAM_CHAGNE
     */
    public boolean getPROGRAM_CHAGNE() {
        short val =(short)((dataValue&PROGRAM_CHAGNE) >> 8);
        if(val == 1){
            return true;
        }
        return false;
    }
    
    /**
     * METERING
     */
    public boolean getMETERING() {
        short val =(short)((dataValue&METERING) >> 2);
        if(val == 1){
            return true;
        }
        return false;
    }
    
    /**
     * TIME_RATE
     */
    public int getTIME_RATE() {
        return (int)(dataValue&TIME_RATE);
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("LPIntervalStatus DATA[");        
            sb.append("(POWER_RECOVERY=").append(""+getPOWER_RECOVERY()).append("),");
            sb.append("(POWER_FAILURE=").append(""+getPOWER_FAILURE()).append("),");
            sb.append("(TIME_CHANGE=").append(""+getTIME_CHANGE()).append("),");
            sb.append("(REVERSION=").append(""+getREVERSION()).append("),");
            sb.append("(BATT_REPLACEMENT=").append(""+getBATT_REPLACEMENT()).append("),");
            sb.append("(MISSING_PHASE=").append(""+getMISSING_PHASE()).append("),");
            sb.append("(SEASON_CHANGE=").append(""+getSEASON_CHANGE()).append("),");
            sb.append("(PROGRAM_CHAGNE=").append(""+getPROGRAM_CHAGNE()).append("),");
            sb.append("(METERING=").append(""+getMETERING()).append("),");
            sb.append("(TIME_RATE=").append(""+getTIME_RATE()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("LPIntervalStatus TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}
