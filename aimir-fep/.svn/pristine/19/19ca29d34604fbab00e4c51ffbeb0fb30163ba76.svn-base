/** 
 * @(#)MeterEventFlag.java       1.0 07/11/09 *
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

/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class MeterEventFlag {
	
    public static final byte MANUAL_RECOVERY  = (byte)0x08;
    public static final byte BATTERY_REPLACE  = (byte)0x04;
    public static final byte SEASON_CHANGE  = (byte)0x03;
    public static final byte SCHEDULED_PROGRAM_ON  = (byte)0x01;
           
	private byte data;
    private static Log log = LogFactory.getLog(MeterEventFlag.class);
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MeterEventFlag(byte data) {
		this.data = data;
	}

    /**
     * MANUAL_RECOVERY
     */
    public boolean getMANUAL_RECOVERY() {
        int flag =(int)((data&MANUAL_RECOVERY) >> 3);
        if (flag == 1){
            return true;
        }
        return false;
    }
    /**
     * BATTERY_REPLACE
     */
    public boolean getBATTERY_REPLACE() {
        int flag =(int)((data&BATTERY_REPLACE) >> 2);
        if (flag == 1){
            return true;
        }
        return false;
    }
    /**
     * SEASON_CHANGE
     */
    public boolean getSEASON_CHANGE() {
        int flag =(int)((data&SEASON_CHANGE) >> 1);
        if (flag == 1){
            return true;
        }
        return false;
    }
    /**
     * SCHEDULED_PROGRAM_ON
     */
    public boolean getSCHEDULED_PROGRAM_ON() {
        int flag =(int)((data&SCHEDULED_PROGRAM_ON));
        if (flag == 1){
            return true;
        }
        return false;
    }
    
    public String getLog()
    {
        StringBuffer sb = new StringBuffer();
        try{
            if(getMANUAL_RECOVERY())
                sb.append("MANUAL_RECOVERY OCCURED</dt>");
            if(getBATTERY_REPLACE())
                sb.append("BATTERY_REPLACE OCCURED</dt>");
            if(getSEASON_CHANGE())
                sb.append("SEASON_CHANGE OCCURED</dt>");
            if(getSCHEDULED_PROGRAM_ON())
                sb.append("SCHEDULED_PROGRAM_ON OCCURED</dt>");
        }catch(Exception e){
            log.warn("MeterEventFlag TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("MeterEventFlag DATA[");        
            sb.append("(MANUAL_RECOVERY=").append(""+getMANUAL_RECOVERY()).append("),");
            sb.append("(BATTERY_REPLACE=").append(""+getBATTERY_REPLACE()).append("),");
            sb.append("(SEASON_CHANGE=").append(""+getSEASON_CHANGE()).append("),");
            sb.append("(SCHEDULED_PROGRAM_ON=").append(""+getSCHEDULED_PROGRAM_ON()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("MeterEventFlag TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}
