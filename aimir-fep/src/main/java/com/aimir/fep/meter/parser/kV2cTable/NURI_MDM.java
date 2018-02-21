/* 
 * @(#)NURI_MDM.java       1.0 07/05/02 *
 * 
 * Modem Information.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
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
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class NURI_MDM {
    
    public static final int OFS_FW_VER = 0;
    public static final int OFS_PACKET_CNT = 5;
    public static final int OFS_PHONE_NUM = 6;
    public static final int OFS_METER_TYPE = 23;
    public static final int OFS_CSQ_LEVEL = 24;
    public static final int OFS_ERROR_STATUS = 25;
    public static final int OFS_RESERVED = 26;
    
    public static final int LEN_FW_VER = 5;
    public static final int LEN_PACKET_CNT = 1;
    public static final int LEN_PHONE_NUM = 17;
    public static final int LEN_METER_TYPE = 1;
    public static final int LEN_CSQ_LEVEL = 1;
    public static final int LEN_ERROR_STATUS = 1;
    public static final int LEN_RESERVED = 4;

	protected byte[] rawData;

    private static Log log = LogFactory.getLog(NURI_MDM.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public NURI_MDM(byte[] rawData) {
		this.rawData = rawData;
	}	
    
    /**
     * Firmware version
     * @return
     */
    public String getFW_VER(){
        
        String ret = new String();
        try{
            ret = new String(DataFormat.select(rawData,OFS_FW_VER,LEN_FW_VER)).trim();

        }catch(Exception e){
            log.warn("invalid model->"+e.getMessage());
        }

        return ret;
    }
    
    /**
     * 0 ~ 255 : Packet send count
     * @return
     */
    public int getPACKET_CNT(){
        int ret = DataFormat.hex2unsigned8(rawData[OFS_PACKET_CNT]);
        return ret;
    }
    
    /**
     * Modem phone number
     * @return
     */
    public String getPHONE_NUM(){
        
        String ret = new String();
        try{
            ret = new String(DataFormat.select(rawData,OFS_PHONE_NUM,LEN_PHONE_NUM)).trim();

        }catch(Exception e){
            log.warn("invalid model->"+e.getMessage());
        }
        return ret;
    }
    
    /**
     * 2.1.1.1 Kind of meter type
     * @return
     */
    public int getMETER_TYPE(){
        int ret = DataFormat.hex2unsigned8(rawData[OFS_METER_TYPE]);
        return ret;
    }
    
    /**
     * Mobile RF CSQ level (0~31, 99)
     * @return
     */
    public int getCSQ_LEVEL(){
        int ret = DataFormat.hex2unsigned8(rawData[OFS_CSQ_LEVEL]);
        if(ret != 0){
            ret = -(113-(ret*2));
        }
        return ret;
    }
    
    /**
     * 2.1.1.2 Status of packet data
     * @return
     */
    public int getERROR_STATUS(){        
        int ret = DataFormat.hex2unsigned8(rawData[OFS_ERROR_STATUS]);
        return ret;
        /*
        0x00    No error
        0x01    Meter no answer
        0x02    Meter CRC error
        0x03    Meter identification error
        0x04    Meter data format error
        0x05    Meter data length error
        0x06	Parameter format or length error
		0x07	Command format error
        */
    }
    
    public String getERROR_STATUS_STRING(){

        int code = DataFormat.hex2unsigned8(rawData[OFS_ERROR_STATUS]);

        switch(code){
            case 0: return "No error["+code+"]";
            case 1: return "Meter no answer["+code+"]";
            case 2: return "Meter CRC error["+code+"]";
            case 3: return "Meter identification error["+code+"]";
            case 4: return "Meter data format error["+code+"]";
            case 5: return "Meter data length error["+code+"]";
            case 6: return "Parameter format or length error["+code+"]";
            case 7: return "Command format error["+code+"]";

            default:
                return "unknown["+code+"]";
        }
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("NURI_MDM Meter DATA[");        
            sb.append("(FW_VER=").append(getFW_VER()).append("),");
            sb.append("(PACKET_CNT=").append(getPACKET_CNT()).append("),");
            sb.append("(PHONE_NUM=").append(getPHONE_NUM()).append("),");
            sb.append("(METER_TYPE=").append(getMETER_TYPE()).append("),");
            sb.append("(CSQ_LEVEL=").append(getCSQ_LEVEL()).append("),");
            sb.append("(ERROR_STATUS_STRING=").append(getERROR_STATUS_STRING()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("NURI_MDM TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}
