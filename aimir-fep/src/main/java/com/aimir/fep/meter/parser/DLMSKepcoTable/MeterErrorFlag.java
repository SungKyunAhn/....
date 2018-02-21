/** 
 * @(#)MeterErrorFlag.java       1.0 08/04/16 *
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
public class MeterErrorFlag {
	
	public static final byte CURRENT_CIRCUIT_DISCONNECT = (byte)0x20;
    public static final byte ACTIVE_POWER_FLOW = (byte)0x10;
    public static final byte NEUTRAL_LINE_CONNECTION_ERROR = (byte)0x08;
    public static final byte LOW_VOLTAGE_PHASE3  = (byte)0x04;
    public static final byte LOW_VOLTAGE_PHASE2  = (byte)0x02;
    public static final byte LOW_VOLTAGE_PHASE1  = (byte)0x01;
           
	private byte data;
    private static Log log = LogFactory.getLog(MeterErrorFlag.class);
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MeterErrorFlag(byte data) {
		this.data = data;
	}

    /**
     * CURRENT_CIRCUIT_DISCONNECT
     */
    public boolean getCURRENT_CIRCUIT_DISCONNECT() {
        byte flag =(byte)(data&CURRENT_CIRCUIT_DISCONNECT) ;
        if (flag != 0){
            return true;
        }
        return false;
    }    
    
    /**
     * ACTIVE_POWER_FLOW
     */
    public boolean getACTIVE_POWER_FLOW() {
        byte flag =(byte)(data&ACTIVE_POWER_FLOW) ;
        if (flag != 0){
            return true;
        }
        return false;
    }   
    
    /**
     * NEUTRAL_LINE_CONNECTION_ERROR
     */
    public boolean getNEUTRAL_LINE_CONNECTION_ERROR() {
        byte flag =(byte)(data&NEUTRAL_LINE_CONNECTION_ERROR) ;
        if (flag != 0){
            return true;
        }
        return false;
    }   
    
    /**
     * LOW_VOLTAGE_PHASE3
     */
    public boolean getLOW_VOLTAGE_PHASE3() {
        byte flag =(byte)(data&LOW_VOLTAGE_PHASE3) ;
        if (flag != 0){
            return true;
        }
        return false;
    }   
    
    /**
     * LOW_VOLTAGE_PHASE2
     */
    public boolean getLOW_VOLTAGE_PHASE2() {
        byte flag =(byte)(data&LOW_VOLTAGE_PHASE2) ;
        if (flag != 0){
            return true;
        }
        return false;
    } 
    
    /**
     * LOW_VOLTAGE_PHASE1
     */
    public boolean getLOW_VOLTAGE_PHASE1() {
        byte flag =(byte)(data&LOW_VOLTAGE_PHASE1) ;
        if (flag != 0){
            return true;
        }
        return false;
    } 

    public String getLog()
    {
        StringBuffer sb = new StringBuffer();
        try{   
            if(getCURRENT_CIRCUIT_DISCONNECT())
                sb.append("<dt>CURRENT_CIRCUIT_DISCONNECT ERROR</dt>");
            if(getACTIVE_POWER_FLOW())
                sb.append("<dt>ACTIVE_POWER_FLOW ERROR</dt>");
            if(getNEUTRAL_LINE_CONNECTION_ERROR())
                sb.append("<dt>NEUTRAL_LINE_CONNECTION_ERROR ERROR</dt>");
            if(getLOW_VOLTAGE_PHASE3())
                sb.append("<dt>LOW_VOLTAGE_PHASE3 ERROR</dt>");
            if(getLOW_VOLTAGE_PHASE2())
                sb.append("<dt>LOW_VOLTAGE_PHASE2 ERROR</dt>");
            if(getLOW_VOLTAGE_PHASE1())
                sb.append("<dt>LOW_VOLTAGE_PHASE1 ERROR</dt>");
        }catch(Exception e){
            log.warn("MeterErrorFlag TO STRING ERR=>"+e.getMessage());
        }
        return sb.toString();
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("MeterErrorFlag DATA[");        
            sb.append("(CURRENT_CIRCUIT_DISCONNECT=").append(""+getCURRENT_CIRCUIT_DISCONNECT()).append("),");
            sb.append("(ACTIVE_POWER_FLOW=").append(""+getACTIVE_POWER_FLOW()).append("),");
            sb.append("(NEUTRAL_LINE_CONNECTION_ERROR=").append(""+getNEUTRAL_LINE_CONNECTION_ERROR()).append("),");
            sb.append("(LOW_VOLTAGE_PHASE3=").append(""+getLOW_VOLTAGE_PHASE3()).append("),");
            sb.append("(LOW_VOLTAGE_PHASE2=").append(""+getLOW_VOLTAGE_PHASE2()).append("),");
            sb.append("(LOW_VOLTAGE_PHASE1=").append(""+getLOW_VOLTAGE_PHASE1()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("MeterErrorFlag TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}
