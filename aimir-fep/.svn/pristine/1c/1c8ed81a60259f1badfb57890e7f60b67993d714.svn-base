/** 
 * @(#)DataFormatForMetering.java       1.0 07/11/09 *
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

import com.aimir.fep.util.DataFormat;

/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class DataFormatForMetering {
	
    public static final int OFS_KW_FORMAT  = 0;
    public static final int OFS_KWH_FORMAT  = 1;
    public static final int OFS_PF_FORMAT  = 2;
    public static final int OFS_V_FORMAT  = 3;
    public static final int OFS_HZ_FORMAT  = 4;
           
	private byte[] data;
    private static Log log = LogFactory.getLog(DataFormatForMetering.class);
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public DataFormatForMetering(byte[] data) {
		this.data = data;
	}
	
    /**
     * decimal count of KW data
     */
    public int getKW_FORMAT() {
        return DataFormat.hex2unsigned8(data[OFS_KW_FORMAT]);
    }
	
    /**
     * decimal count of KWh data
     */
    public int getKWH_FORMAT() {
        return DataFormat.hex2unsigned8(data[OFS_KWH_FORMAT]);
    }
    
    /**
     * decimal count of PF data
     */
    public int getPF_FORMAT() {
        return DataFormat.hex2unsigned8(data[OFS_PF_FORMAT]);
    }
    
    /**
     * decimal count of V data
     */
    public int getV_FORMAT() {
        return DataFormat.hex2unsigned8(data[OFS_V_FORMAT]);
    }
    
    /**
     * decimal count of Hz data
     */
    public int getHZ_FORMAT() {
        return DataFormat.hex2unsigned8(data[OFS_HZ_FORMAT]);
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("DataFormatForMetering DATA[");        
            sb.append("(KW_FORMAT=").append(getKW_FORMAT()).append("),");
            sb.append("(KWH_FORMAT=").append(getKWH_FORMAT()).append("),");
            sb.append("(PF_FORMAT()=").append(getPF_FORMAT()).append("),");
            sb.append("(V_FORMAT()=").append(getV_FORMAT()).append("),");
            sb.append("(HZ_FORMAT=").append(getHZ_FORMAT()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("DataFormatForMetering TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }


}
