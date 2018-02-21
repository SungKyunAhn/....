/** 
 * @(#)MeterErrorFlag.java       1.0 07/11/09 *
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
public class MeterErrorFlag {
	
    public static final byte LOW_VOLTAGE_C  = (byte)0x80;
    public static final byte LOW_VOLTAGE_B  = (byte)0x40;
    public static final byte LOW_VOLTAGE_A  = (byte)0x20;
    public static final byte CONNECTION_ERROR = (byte)0x10;
    public static final byte REVERSE_TRANSMISSION = (byte)0x08;
    public static final byte LOW_CURRENT_C  = (byte)0x04;
    public static final byte LOW_CURRENT_B  = (byte)0x02;
    public static final byte LOW_CURRENT_A  = (byte)0x01;

           
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
     * CONNECTION_ERROR
     */
    public boolean getCONNECTION_ERROR() {
        int flag =(int)((data&CONNECTION_ERROR) >> 7);
        if (flag == 1){
            return true;
        }
        return false;
    }
	
    /**
     * REVERSE_TRANSMISSION
     */
    public boolean getREVERSE_TRANSMISSION() {
        int flag =(int)((data&REVERSE_TRANSMISSION) >> 6);
        if (flag == 1){
            return true;
        }
        return false;
    }
    /**
     * LOW_CURRENT_C
     */
    public boolean getLOW_CURRENT_C() {
        int flag =(int)((data&LOW_CURRENT_C) >> 5);
        if (flag == 1){
            return true;
        }
        return false;
    }
    /**
     * LOW_CURRENT_B
     */
    public boolean getLOW_CURRENT_B() {
        int flag =(int)((data&LOW_CURRENT_B) >> 4);
        if (flag == 1){
            return true;
        }
        return false;
    }
    /**
     * LOW_CURRENT_A
     */
    public boolean getLOW_CURRENT_A() {
        int flag =(int)((data&LOW_CURRENT_A) >> 3);
        if (flag == 1){
            return true;
        }
        return false;
    }
    /**
     * LOW_VOLTAGE_C
     */
    public boolean getLOW_VOLTAGE_C() {
        int flag =(int)((data&LOW_VOLTAGE_C) >> 2);
        if (flag == 1){
            return true;
        }
        return false;
    }
    /**
     * LOW_VOLTAGE_B
     */
    public boolean getLOW_VOLTAGE_B() {
        int flag =(int)((data&LOW_VOLTAGE_B) >> 1);
        if (flag == 1){
            return true;
        }
        return false;
    }
    /**
     * LOW_VOLTAGE_A
     */
    public boolean getLOW_VOLTAGE_A() {
        int flag =(int)((data&LOW_VOLTAGE_A));
        if (flag == 1){
            return true;
        }
        return false;
    }
    
    public String getLog()
    {
        StringBuffer sb = new StringBuffer();
        try{   
            if(getCONNECTION_ERROR())
                sb.append("<dt>CONNECTION_ERROR ERROR</dt>");
            if(getREVERSE_TRANSMISSION())
                sb.append("<dt>REVERSE_TRANSMISSION ERROR</dt>");
            if(getLOW_CURRENT_C())
                sb.append("<dt>LOW_CURRENT_C ERROR</dt>");
            if(getLOW_CURRENT_B())
                sb.append("<dt>LOW_CURRENT_B ERROR</dt>");
            if(getLOW_CURRENT_A())
                sb.append("<dt>LOW_CURRENT_A ERROR</dt>");
            if(getLOW_VOLTAGE_C())
                sb.append("<dt>LOW_VOLTAGE_C ERROR</dt>");
            if(getLOW_VOLTAGE_B())
                sb.append("<dt>LOW_VOLTAGE_B ERROR</dt>");
            if(getLOW_VOLTAGE_A())
                sb.append("<dt>LOW_VOLTAGE_A ERROR</dt>");
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
            sb.append("(CONNECTION_ERROR=").append(""+getCONNECTION_ERROR()).append("),");
            sb.append("(REVERSE_TRANSMISSION=").append(""+getREVERSE_TRANSMISSION()).append("),");
            sb.append("(LOW_CURRENT_C=").append(""+getLOW_CURRENT_C()).append("),");
            sb.append("(LOW_CURRENT_B=").append(""+getLOW_CURRENT_B()).append("),");
            sb.append("(LOW_CURRENT_A=").append(""+getLOW_CURRENT_A()).append("),");
            sb.append("(LOW_VOLTAGE_C=").append(""+getLOW_VOLTAGE_C()).append("),");
            sb.append("(LOW_VOLTAGE_B=").append(""+getLOW_VOLTAGE_B()).append("),");
            sb.append("(LOW_VOLTAGE_A=").append(""+getLOW_VOLTAGE_A()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("MeterErrorFlag TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}
