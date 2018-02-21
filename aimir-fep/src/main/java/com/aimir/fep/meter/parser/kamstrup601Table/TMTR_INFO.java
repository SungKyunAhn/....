/* 
 * @(#)TMTR_INFO.java       1.0 2008-06-02 *
 * 
 * Meter Information
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
package com.aimir.fep.meter.parser.kamstrup601Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.parser.kdhTable.MeterCautionFlag;
import com.aimir.fep.util.DataFormat;

/**
 * @author YK.Park
 */
public class TMTR_INFO implements java.io.Serializable{    

    public static final int LEN_TMTR_INFO = 5;
	public static final int LEN_METER_ID = 4;
	public static final int LEN_METER_STATUS = 1;
	public static final int OFS_METER_ID = 0;
    public static final int OFS_METER_STATUS = 4;
	
	private byte[] rawData = null;

    private static Log log = LogFactory.getLog(TMTR_INFO.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public TMTR_INFO(byte[] rawData) {
        this.rawData = rawData;
	}

    public MeterCautionFlag getMeterCautionFlag() throws Exception {
       return new MeterCautionFlag(rawData[OFS_METER_STATUS]);
    }    
    
    public String getMETER_ID(){
        String id = "";
        try{
            id =  DataFormat.hex2dec(
                                    rawData,OFS_METER_ID,LEN_METER_ID)+"";
        }catch(Exception e){
            log.warn("METER_ID->"+e.getMessage());
        }
        
        return id;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("TMTR_INFO DATA[");        
            sb.append("(Meter Id=").append(getMETER_ID()).append("),");
            sb.append("(MeterCautionFlag=").append(getMeterCautionFlag().toString()).append(')');            
            sb.append("]\n");
        }catch(Exception e){
            log.warn("TMTR_INFO TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }

}
