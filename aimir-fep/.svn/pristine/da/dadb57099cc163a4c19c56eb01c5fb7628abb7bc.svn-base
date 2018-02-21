/* 
 * @(#)VIF.java       1.0 2008-06-02 *
 * 
 * Variable Information
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
/**
 * @author YK.Park
 */
package com.aimir.fep.meter.parser.kdhTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.Util;


public class VIF {    

    public static final int EXTENSION_BIT_CODE         = 0x80;
    public static final int FIELD_UNKNOWN              = -1;
    public static final int FIELD_HEAT_GCAL            = 0;
    public static final int FIELD_HEAT_MWH             = 1;
    public static final int FIELD_FLOW_LITER           = 2;
    public static final int FIELD_FLOW_M3              = 3;   
    public static final int FIELD_SUPPLYTEMPERATURE    = 4;
    public static final int FIELD_RETRIVALTEMPERATURE  = 5;
    public static final int FIELD_TEMPERATUREVARIATION = 6;
    public static final int FIELD_SUPPLYPRESSURE       = 7;
    public static final int FIELD_DATE                 = 8;
    public static final int FIELD_DATETIME             = 9;

    public static final String[] VIF_TABLE = {
        "E0000nnn",
        "E0001nnn", 
        "E0010nnn",
        "E0011nnn",
        "E01000nn",
        "E01001nn",
        "E01010nn",
        "E01011nn",
        "E1100000",
        "E1100001"
    };
	
	private byte rawData = 0x00;

    private static Log log = LogFactory.getLog(VIF.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public VIF(byte rawData) {
        this.rawData = rawData;
	}
    
    public double getMultiply()
    {
        double multiply = 1.0;
        
        int vif = getVIF();
        if(vif == FIELD_UNKNOWN || 
           vif == FIELD_DATE || 
           vif == FIELD_DATETIME)
        {
            return 1;
        }        
        else
        {
            String pattern = VIF_TABLE[vif].substring(4,8);
            
            if(pattern.charAt(0) == 'n'){
                return 0.0001;
            }
            else if(pattern.charAt(1) == 'n')
            {
                return 0.001;
            }
            else if(pattern.charAt(2) == 'n')
            {
               return 0.01; 
            }
            else if(pattern.charAt(3) == 'n')
            {
               return 0.1; 
            }
            else
            {
                return 1;
            }            
        }
    }
    
    public int getVIF()
    {
        int convertInt = rawData & 0x7F;
        String convertStr 
            = Util.frontAppendNStr('0',Integer.toBinaryString(convertInt),8);
        
        //log.debug(convertStr);
        
        if (compare(VIF_TABLE[FIELD_HEAT_GCAL],convertStr))
        {
            return FIELD_HEAT_GCAL;
        }
        if (compare(VIF_TABLE[FIELD_HEAT_MWH],convertStr))
        {
            return FIELD_HEAT_MWH;
        }
        if (compare(VIF_TABLE[FIELD_FLOW_LITER],convertStr))
        {
            return FIELD_FLOW_LITER;
        }
        if (compare(VIF_TABLE[FIELD_FLOW_M3],convertStr))
        {
            return FIELD_FLOW_M3;
        }
        if (compare(VIF_TABLE[FIELD_SUPPLYTEMPERATURE],convertStr))
        {
            return FIELD_SUPPLYTEMPERATURE;
        }
        if (compare(VIF_TABLE[FIELD_RETRIVALTEMPERATURE],convertStr))
        {
            return FIELD_RETRIVALTEMPERATURE;
        }
        if (compare(VIF_TABLE[FIELD_TEMPERATUREVARIATION],convertStr))
        {
            return FIELD_TEMPERATUREVARIATION;
        }
        if (compare(VIF_TABLE[FIELD_SUPPLYPRESSURE],convertStr))
        {
            return FIELD_SUPPLYPRESSURE;
        }
        if (compare(VIF_TABLE[FIELD_DATE],convertStr))
        {
            return FIELD_DATE;
        }
        if (compare(VIF_TABLE[FIELD_DATETIME],convertStr))
        {
            return FIELD_DATETIME;
        }
        return FIELD_UNKNOWN;        
    }
    
    private boolean compare(String a1, String a2)
    {
        int endAddress = 0;
        endAddress = a1.indexOf("n");
        if(endAddress < 0){
            endAddress = 8;
        }
        
        if(a1.substring(1,endAddress).equals(a2.substring(1,endAddress))){
            return true;
        }
        
        return false;
    }
    
    public boolean isVIFE()
    {
        if((rawData & EXTENSION_BIT_CODE) > 0){
            return true;
        }
        return false;
    }

    public String toString()
    {
        int vif = getVIF();
        
        switch(vif){
            case FIELD_UNKNOWN              : return "unknown";
            case FIELD_HEAT_GCAL            : return "heat[Gcal]";
            case FIELD_HEAT_MWH             : return "heat[Mwh]";
            case FIELD_FLOW_LITER           : return "flow[L]";
            case FIELD_FLOW_M3              : return "flow[m3]";
            case FIELD_SUPPLYTEMPERATURE    : return "supply temperature";
            case FIELD_RETRIVALTEMPERATURE  : return "retrival temperature";
            case FIELD_TEMPERATUREVARIATION : return "temperature variation";
            case FIELD_SUPPLYPRESSURE       : return "supply pressure";
            case FIELD_DATE                 : return "Date";
            case FIELD_DATETIME             : return "Date and Time";
        }
        

        return "";
    }

}
