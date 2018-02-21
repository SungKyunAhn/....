/** 
 * @(#)MeterTypeConfig.java       1.0 07/11/09 *
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
public class MeterTypeConfig {
	
    public static final byte METER_LEVEL  = (byte)0xc0;
    public static final byte RATED_VOLTAGE  = (byte)0x30;
    public static final byte RATED_CURRENT  = (byte)0x0c;
    public static final byte SUPPLY_TYPE  = (byte)0x03;
           
	private byte data;
    private static Log log = LogFactory.getLog(MeterTypeConfig.class);
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MeterTypeConfig(byte data) {
		this.data = data;
	}
	
    /**
     * METER_LEVEL
     */
    public double getMETER_LEVEL() {
        int level = (int)((data&METER_LEVEL) >> 6);
        if (level == 0){
            return 1.0;
        }else if(level ==1){
            return 0.5;
        }else if(level == 2){
            return 0.2;
        }else return 0.0;
    }
	
    /**
     * RATED_VOLTAGE
     */
    public int getRATED_VOLTAGE() {
        int vol = (int)((data&RATED_VOLTAGE) >> 4);
        if (vol == 0){
            return 220;
        }else if(vol ==1){
            return 110;
        }else return 0;
    }
    
    /**
     * RATED_CURRENT
     */
    public int getRATED_CURRENT() {
        int vol = (int)((data&RATED_CURRENT) >> 2);
        if (vol == 0){
            return 5;
        }else if(vol ==1){
            return 40;
        }else if(vol ==2 ){
            return 120;
        }else{
            return 0;
        }
    }
    
    /**
     * SUPPLY_TYPE
     */
    public int getSUPPLY_TYPE() {
        int vol = (int)((data&SUPPLY_TYPE));
        if(vol == 2)
            return 4;
        else if(vol ==3)
            return 3;
        else return vol+1;
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("MeterTypeConfig DATA[");        
            sb.append("(METER_LEVEL=").append(""+getMETER_LEVEL()).append("),");
            sb.append("(RATED_VOLTAGE=").append(""+getRATED_VOLTAGE()).append("),");
            sb.append("(RATED_CURRENT=").append(""+getRATED_CURRENT()).append("),");
            sb.append("(SUPPLY_TYPE()=").append(""+getSUPPLY_TYPE()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("MeterTypeConfig TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }


}
