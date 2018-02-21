/* 
 * @(#)TMTR_MONTH.java       1.0 2008-06-02 *
 * 
 * Meter Month Data
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

public class TMTR_MONTH extends TMTR_LP{    

    public static final String TABLE_KIND = "MONTH";
    public static final int TABLE_CODE = 2;
	
	private byte[] rawData = null;

    private static Log log = LogFactory.getLog(TMTR_MONTH.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public TMTR_MONTH(byte[] rawData, String table_kind) {
        super(rawData,table_kind);
	}
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("TMTR_MONTH DATA["); 
            for(int i = 0; i < hmData.length; i++){
                sb.append(hmData[i].toString());
            }
            sb.append("]\n");
        }catch(Exception e){
            log.warn("TMTR_MONTH TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }       
}
