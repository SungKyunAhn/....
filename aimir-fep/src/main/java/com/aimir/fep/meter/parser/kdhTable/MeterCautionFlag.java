/** 
 * @(#)MeterCautionFlag.java       1.0 2008-06-02 *
 * 
 * Actual Dimension Register Table.
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.kdhTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class MeterCautionFlag {
	
    public static final int NORMAL                       = 0;
    public static final int NO_MTRDATA_CONTENTS          = 1;
    public static final int WRONG_MTRDATA                = 2;
    public static final int NO_MTRDATA_IN_DATETIMEFIELD  = 3;
    public static final int LESS_THAN_THE_REQUESTED_DATA = 4;
           
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
     * NO_MTRDATA_CONTENTS
     */
    public boolean getNO_MTRDATA_CONTENTS() {
        int flag = data & 0xFF;
        if (flag == NO_MTRDATA_CONTENTS){
            return true;
        }
        return false;
    } 
    
    /**
     * WRONG_MTRDATA
     */
    public boolean getWRONG_MTRDATA() {
        int flag = data & 0xFF;
        if (flag == WRONG_MTRDATA){
            return true;
        }
        return false;
    } 
       
    /**
     * NO_MTRDATA_IN_DATETIMEFIELD
     */
    public boolean getNO_MTRDATA_IN_DATETIMEFIELD() {
        int flag = data & 0xFF;
        if (flag == NO_MTRDATA_IN_DATETIMEFIELD){
            return true;
        }
        return false;
    } 
    
    /**
     * LESS_THAN_THE_REQUESTED_DATA
     */
    public boolean getLESS_THAN_THE_REQUESTED_DATA() {
        int flag = data & 0xFF;
        if (flag == LESS_THAN_THE_REQUESTED_DATA){
            return true;
        }
        return false;
    }
    
    /**
     * NORMAL
     */
    public boolean getNORMAL() {
        int flag = data & 0xFF;
        if (flag == NORMAL){
            return true;
        }
        return false;
    }

    public String getLog()
    {
        StringBuffer sb = new StringBuffer();
        try{  

            if(getNO_MTRDATA_CONTENTS())
                sb.append("<dt>No metering data contents</dt>");
            if(getWRONG_MTRDATA())
                sb.append("<dt>Wrong data</dt>");
            if(getNO_MTRDATA_IN_DATETIMEFIELD())
                sb.append("<dt>No metering data in requested date time field</dt>");
            if(getLESS_THAN_THE_REQUESTED_DATA())
                sb.append("<dt>Less than the requested data</dt>");
            if(getNORMAL())
                sb.append("<dt>Normal</dt>");
        }catch(Exception e){
            log.warn("MeterCautionFlag TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
    
    public int getStatusCode(){
        return (int)(data & 0xFF);
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("MeterCautionFlag DATA[");        
            sb.append("(NO_MTRDATA_CONTENTS=").append(""+getNO_MTRDATA_CONTENTS()).append("),");
            sb.append("(WRONG_MTRDATA=").append(""+getWRONG_MTRDATA()).append("),");
            sb.append("(NO_MTRDATA_IN_DATETIMEFIELD=").append(""+getNO_MTRDATA_IN_DATETIMEFIELD()).append("),");
            sb.append("(LESS_THAN_THE_REQUESTED_DATA=").append(""+getLESS_THAN_THE_REQUESTED_DATA()).append("),");
            sb.append("(NORMAL=").append(""+getNORMAL()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("MeterCautionFlag TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}
