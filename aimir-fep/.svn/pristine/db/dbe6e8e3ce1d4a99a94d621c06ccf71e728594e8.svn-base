/** 
 * @(#)ST001.java       1.0 06/12/14 *
 * 
 * Manufacturer Identification Table.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.kV2cTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * 
 * ex ) 
 *         : 20 20 30 30 30 30 30 30 30 30 30 30 
 * MSERIAL : 32 38 36 30 30 33 32 36 
 */

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class ST001 {
	
    public static final int OFS_MANUFACTURER = 0;
    public static final int OFS_ED_MODEL = 4;
    public static final int OFS_HW_VERSION_NUMBER = 12;
    public static final int OFS_HW_REVISION_NUMBER = 13;
    public static final int OFS_FW_VERSION_NUMBER = 14;
    public static final int OFS_FW_REVISION_NUMBER = 15;
    
    public static final int LEN_MANUFACTURER = 4;
    public static final int LEN_ED_MODEL = 8;
    public static final int LEN_HW_VERSION_NUMBER = 1;
    public static final int LEN_HW_REVISION_NUMBER = 1;
    public static final int LEN_FW_VERSION_NUMBER = 1;
    public static final int LEN_FW_REVISION_NUMBER = 1;
    
	public static final int OFS_MSERIAL = 16;
	public static final int LEN_MSERIAL = 16;
	
	private byte[] data;
    private static Log log = LogFactory.getLog(ST001.class);
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST001(byte[] data) {
		this.data = data;
	}

    public String getMANUFACTURER(){
        String val = new String();

        try{
            val = new String(
                      DataFormat.select(data,OFS_MANUFACTURER,LEN_MANUFACTURER)).trim();

        }catch(Exception e){
            log.warn("invalid value->"+e.getMessage());
        }

        return val;
    }
    
    public String getED_MODEL(){
        String val = new String();

        try{
            val = new String(
                      DataFormat.select(data,OFS_ED_MODEL,LEN_ED_MODEL)).trim();

        }catch(Exception e){
            log.warn("invalid value->"+e.getMessage());
        }

        return val;
    }
    
    public int getHW_VERSION_NUMBER(){
       return DataFormat.hex2unsigned8(data[OFS_HW_VERSION_NUMBER]);
    }
    
    public int getHW_REVISION_NUMBER(){
        return DataFormat.hex2unsigned8(data[OFS_HW_REVISION_NUMBER]);
    }
    
    public int getFW_VERSION_NUMBER(){
        return DataFormat.hex2unsigned8(data[OFS_FW_VERSION_NUMBER]);
    }
    
    public int getFW_REVISION_NUMBER(){
        return DataFormat.hex2unsigned8(data[OFS_FW_REVISION_NUMBER]);
    }
    
	public byte[] parseMSerial() {
		
		byte[] b = new byte[]{0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30};
		
		try{
			b =  DataFormat.select(data,OFS_MSERIAL,LEN_MSERIAL);
		}catch(Exception e){
			log.warn("invalid meter id->"+e.getMessage());
		}
		return b;
	}

	public String getMSerial() {

		String mserial = new String("11111111");

		try{
			mserial = new String(DataFormat.select(data,OFS_MSERIAL,LEN_MSERIAL)).trim();
			
			if(mserial == null || mserial.equals("")){
				mserial = "11111111";
			}

		}catch(Exception e){
			log.warn("invalid meter id->"+e.getMessage());
		}

		return mserial;
	}

}
