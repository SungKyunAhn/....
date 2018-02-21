/** 
 * @(#)MeterCautionFlag.java       1.0 08/4/16 *
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
 
package com.aimir.fep.meter.parser.DLMSKepcoTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class MeterCautionFlag {
	
    public static final byte NOT_PROGRAMMED  = (byte)0x10;
    public static final byte LOW_BATTERY  = (byte)0x08;
    public static final byte BATTERY_MISSING  = (byte)0x04;
    public static final byte MEMORY_ERROR  = (byte)0x02;
    public static final byte RTC_RESET  = (byte)0x01;
           
	private byte data;
    private static Log log = LogFactory.getLog(MeterCautionFlag.class);
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MeterCautionFlag(byte data) {
		this.data = data;
	}
	
    /**
     * NOT_PROGRAMMED
     */
    public boolean getNOT_PROGRAMMED() {
        byte flag =(byte)(data&NOT_PROGRAMMED) ;
        if (flag != 0){
            return true;
        }
        return false;
    } 
    
    /**
     * LOW_BATTERY
     */
    public boolean getLOW_BATTERY() {
        byte flag =(byte)(data&LOW_BATTERY) ;
        if (flag != 0){
            return true;
        }
        return false;
    } 
       
    /**
     * BATTERY_MISSING
     */
    public boolean getBATTERY_MISSING() {
        byte flag =(byte)(data&BATTERY_MISSING) ;
        if (flag != 0){
            return true;
        }
        return false;
    } 
    
    /**
     * MEMORY_ERROR
     */
    public boolean getMEMORY_ERROR() {
        byte flag =(byte)(data&MEMORY_ERROR) ;
        if (flag != 0){
            return true;
        }
        return false;
    }
    
    /**
     * RTC_RESET
     */
    public boolean getRTC_RESET() {
        byte flag =(byte)(data&RTC_RESET) ;
        if (flag != 0){
            return true;
        }
        return false;
    }

    public String getLog()
    {
        StringBuffer sb = new StringBuffer();
        try{  

            if(getNOT_PROGRAMMED())
                sb.append("<dt>NOT_PROGRAMMED CAUTION</dt>");
            if(getLOW_BATTERY())
                sb.append("<dt>LOW_BATTERY CAUTION</dt>");
            if(getBATTERY_MISSING())
                sb.append("<dt>BATTERY_MISSING CAUTION</dt>");
            if(getMEMORY_ERROR())
                sb.append("<dt>MEMORY_ERROR CAUTION</dt>");
            if(getRTC_RESET())
                sb.append("<dt>RTC_RESET CAUTION</dt>");
        }catch(Exception e){
            log.warn("MeterCautionFlag TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("MeterCautionFlag DATA[");        
            sb.append("(NOT_PROGRAMMED=").append(""+getNOT_PROGRAMMED()).append("),");
            sb.append("(LOW_BATTERY=").append(""+getLOW_BATTERY()).append("),");
            sb.append("(BATTERY_MISSING=").append(""+getBATTERY_MISSING()).append("),");
            sb.append("(MEMORY_ERROR=").append(""+getMEMORY_ERROR()).append("),");
            sb.append("(RTC_RESET=").append(""+getRTC_RESET()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("MeterCautionFlag TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}
