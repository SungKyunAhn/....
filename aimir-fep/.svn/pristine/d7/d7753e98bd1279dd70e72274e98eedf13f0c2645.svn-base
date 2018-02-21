/** 
 * @(#)MeterStatus.java       1.0 08/03/31 *
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
public class MeterStatus {
	
    public static final int OFS_ERROR_INFO  = 1;
    public static final int OFS_CAUTION_INFO  = 2;
    public static final int OFS_EVENT_INFO  = 3;
    
    //ERROR_INFO
    public static final byte POTENTIAL_ERROR  = (byte)0x08;
    public static final byte EEPROM_ERROR  = (byte)0x04;
    public static final byte RAM_ERROR  = (byte)0x02;
    public static final byte BATTERY_MISSING  = (byte)0x01;
      
    //CAUTION_INFO
    public static final byte LOW_BATTERY  = (byte)0x04;
    public static final byte UN_PROGRAMMED  = (byte)0x02;
    public static final byte DEMAND_OVERFLOW  = (byte)0x01;
      
    //EVENT_INFO
    public static final byte POWER_OUTAGE  = (byte)0x10;
    public static final byte MAX_DEMAND  = (byte)0x08;
    public static final byte DEMAND_RESET  = (byte)0x04;
    public static final byte TIME_CHANGED  = (byte)0x02;
    public static final byte PROGRAM_CHANGED  = (byte)0x01;
      
	private byte[] rawData;
	private byte errorData;
	private byte cautionData;
	private byte eventData;
    private static Log log = LogFactory.getLog(MeterStatus.class);
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MeterStatus(byte[] data) {
		this.rawData = data;
		errorData = rawData[OFS_ERROR_INFO];
		cautionData = rawData[OFS_CAUTION_INFO];
		eventData = rawData[OFS_EVENT_INFO];
	}
	
	 /**
     * POTENTIAL_ERROR
     */
    public boolean getPOTENTIAL_ERROR() {
        int flag =(int)(errorData&POTENTIAL_ERROR);
        if (flag !=0){
            return true;
        }
        return false;
    }
    
	/**
     * EEPROM_ERROR
     */
    public boolean getEEPROM_ERROR() {
        int flag =(int)(errorData&EEPROM_ERROR);
        if (flag !=0){
            return true;
        }
        return false;
    }
    /**
     * RAM_ERROR
     */
    public boolean getRAM_ERROR() {
        int flag =(int)(errorData&RAM_ERROR);
        if (flag !=0){
            return true;
        }
        return false;
    }
    /**
     * BATTERY_MISSING
     */
    public boolean getBATTERY_MISSING() {
        int flag =(int)(errorData&BATTERY_MISSING);
        if (flag !=0){
            return true;
        }
        return false;
    }
    /**
     * LOW_BATTERY
     */
    public boolean getLOW_BATTERY() {
        int flag =(int)(cautionData&LOW_BATTERY);
        if (flag !=0){
            return true;
        }
        return false;
    }
    /**
     * UN_PROGRAMMED
     */
    public boolean getUN_PROGRAMMED() {
        int flag =(int)(cautionData&UN_PROGRAMMED);
        if (flag !=0){
            return true;
        }
        return false;
    }
    /**
     * DEMAND_OVERFLOW
     */
    public boolean getDEMAND_OVERFLOW() {
        int flag =(int)(cautionData&DEMAND_OVERFLOW);
        if (flag !=0){
            return true;
        }
        return false;
    }
    /**
     * POWER_OUTAGE
     */
    public boolean getPOWER_OUTAGE() {
        int flag =(int)(eventData&POWER_OUTAGE);
        if (flag !=0){
            return true;
        }
        return false;
    }
    /**
     * MAX_DEMAND
     */
    public boolean getMAX_DEMAND() {
        int flag =(int)(eventData&MAX_DEMAND);
        if (flag !=0){
            return true;
        }
        return false;
    }
    /**
     * DEMAND_RESET
     */
    public boolean getDEMAND_RESET() {
        int flag =(int)(eventData&DEMAND_RESET);
        if (flag !=0){
            return true;
        }
        return false;
    }
    /**
     * TIME_CHANGED
     */
    public boolean getTIME_CHANGED() {
        int flag =(int)(eventData&TIME_CHANGED);
        if (flag !=0){
            return true;
        }
        return false;
    }
    /**
     * PROGRAM_CHANGED
     */
    public boolean getPROGRAM_CHANGED() {
        int flag =(int)(eventData&PROGRAM_CHANGED);
        if (flag !=0){
            return true;
        }
        return false;
    }

    public String getLog()
    {
        StringBuffer sb = new StringBuffer();
        try{  

            if(getPOTENTIAL_ERROR())
                sb.append("<dt>POTENTIAL_ERROR</dt>");
            if(getEEPROM_ERROR())
                sb.append("<dt>EEPROM_ERROR</dt>");
            if(getRAM_ERROR())
                sb.append("<dt>RAM_ERROR</dt>");
            if(getBATTERY_MISSING())
                sb.append("<dt>BATTERY_MISSING</dt>");
            
            if(getLOW_BATTERY())
                sb.append("<dt>LOW_BATTERY CAUTION</dt>");
            if(getUN_PROGRAMMED())
                sb.append("<dt>UN_PROGRAMMED CAUTION</dt>");
            if(getDEMAND_OVERFLOW())
                sb.append("<dt>DEMAND_OVERFLOW CAUTION</dt>");
            
            if(getPOWER_OUTAGE())
                sb.append("<dt>POWER_OUTAGE EVENT</dt>");
            if(getMAX_DEMAND())
                sb.append("<dt>MAX_DEMAND EVENT</dt>");
            if(getDEMAND_RESET())
                sb.append("<dt>DEMAND_RESET EVENT</dt>");
            if(getTIME_CHANGED())
                sb.append("<dt>TIME_CHANGED EVENT</dt>");
            if(getPROGRAM_CHANGED())
                sb.append("<dt>PROGRAM_CHANGED EVENT</dt>");
            
        }catch(Exception e){
            log.warn("MeterStatus TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("MeterStatus DATA[");        

            sb.append("(POTENTIAL_ERROR=").append(""+getPOTENTIAL_ERROR()).append("),");
            sb.append("(EEPROM_ERROR=").append(""+getEEPROM_ERROR()).append("),");
            sb.append("(RAM_ERROR=").append(""+getRAM_ERROR()).append("),");
            sb.append("(BATTERY_MISSING=").append(""+getBATTERY_MISSING()).append("),");
            
            sb.append("(LOW_BATTERY=").append(""+getLOW_BATTERY()).append("),");
            sb.append("(UN_PROGRAMMED=").append(""+getUN_PROGRAMMED()).append("),");
            sb.append("(DEMAND_OVERFLOW=").append(""+getDEMAND_OVERFLOW()).append("),");
            
            sb.append("(POWER_OUTAGE=").append(""+getPOWER_OUTAGE()).append("),");
            sb.append("(MAX_DEMAND=").append(""+getMAX_DEMAND()).append("),");
            sb.append("(DEMAND_RESET=").append(""+getDEMAND_RESET()).append("),");
            sb.append("(TIME_CHANGED=").append(""+getTIME_CHANGED()).append("),");
            sb.append("(PROGRAM_CHANGED=").append(""+getPROGRAM_CHANGED()).append(')');
            
            sb.append("]\n");
        }catch(Exception e){
            log.warn("MeterStatus TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
    
}
