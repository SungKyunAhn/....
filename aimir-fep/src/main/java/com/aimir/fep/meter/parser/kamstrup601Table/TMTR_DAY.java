/* 
 * @(#)TMTR_DAY.java       1.0 2008-06-02 *
 * 
 * Meter DAY Data
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
package com.aimir.fep.meter.parser.kamstrup601Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TMTR_DAY extends TMTR_LP{    

    public static final String TABLE_KIND = "DAY";
    public static final int TABLE_CODE = 1;
	
	private byte[] rawData = null;

    private static Log log = LogFactory.getLog(TMTR_DAY.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public TMTR_DAY(byte[] rawData, String table_kind) {
        super(rawData,table_kind);
	}

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("TMTR_DAY DATA["); 
            for(int i = 0; i < hmData.length; i++){
                sb.append(hmData[i].toString());
            }
            sb.append("]\n");
        }catch(Exception e){
            log.warn("TMTR_DAY TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}
