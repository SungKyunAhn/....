/** 
 * @(#)MeterCautionFlag.java       1.0 07/11/09 *
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
public class MeterCautionFlag {
	
  //  public static final byte TOU_SUM = (byte)0x80;
  //  public static final byte LP_MEMORY_CHECKSUM = (byte)0x40;
    public static final byte BATT_ERROR  = (byte)0x08;
  //  public static final byte RAM_ERROR  = (byte)0x10;
    public static final byte MEMORY_ERROR  = (byte)0x04;
    public static final byte RTC_ERROR  = (byte)0x02;
    public static final byte PROGRAM_INIT  = (byte)0x01;
           
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
     * BATT_ERROR
     */
    public boolean getBATT_ERROR() {
        int flag =(int)((data&BATT_ERROR) >> 5);
        if (flag == 1){
            return true;
        }
        return false;
    }

    /**
     * MEMORY_ERROR
     */
    public boolean getMEMORY_ERROR() {
        int flag =(int)((data&MEMORY_ERROR) >> 3);
        if (flag == 1){
            return true;
        }
        return false;
    }
    /**
     * RTC_ERROR
     */
    public boolean getRTC_ERROR() {
        int flag =(int)((data&RTC_ERROR) >> 2);
        if (flag == 1){
            return true;
        }
        return false;
    }

    /**
     * PROGRAM_INIT
     */
    public boolean getPROGRAM_INIT() {
        int flag =(int)((data&PROGRAM_INIT));
        if (flag == 1){
            return true;
        }
        return false;
    }
    
    public String getLog()
    {
        StringBuffer sb = new StringBuffer();
        try{  

            if(getBATT_ERROR())
                sb.append("<dt>BATT_ERROR CAUTION</dt>");
            if(getMEMORY_ERROR())
                sb.append("<dt>MEMORY_ERROR CAUTION</dt>");
            if(getRTC_ERROR())
                sb.append("<dt>RTC_ERROR CAUTION</dt>");
            if(getPROGRAM_INIT())
                sb.append("<dt>PROGRAM_INIT CAUTION</dt>");
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
            sb.append("(BATT_ERROR=").append(""+getBATT_ERROR()).append("),");
            sb.append("(MEMORY_ERROR=").append(""+getMEMORY_ERROR()).append("),");
            sb.append("(RTC_ERROR=").append(""+getRTC_ERROR()).append("),");
            sb.append("(PROGRAM_INIT=").append(""+getPROGRAM_INIT()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("MeterCautionFlag TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}
