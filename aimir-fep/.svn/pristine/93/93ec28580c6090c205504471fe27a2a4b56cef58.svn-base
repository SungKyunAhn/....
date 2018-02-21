/* 
 * @(#)KAMSTRUP_MDM.java       1.0 08/12/07 *
 * 
 * Modem Information.
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.parser.kV2cTable.NURI_MDM;
import com.aimir.fep.util.DataFormat;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class KAMSTRUP_MDM extends NURI_MDM {
    
    public static final int OFS_FW_VER = 0;
    public static final int OFS_SENDING_FLAG = 5;
    public static final int OFS_PHONE_NUM = 6;
    public static final int OFS_METER_TYPE = 26;
    public static final int OFS_CSQ_LEVEL = 27;
    public static final int OFS_ERROR_STATUS = 28;
    public static final int OFS_METERVERSION = 29;
    public static final int OFS_HW_VER = 30;
    
    public static final int LEN_FW_VER = 5;
    public static final int LEN_SENDING_FLAG = 1;
    public static final int LEN_PHONE_NUM = 20;
    public static final int LEN_METER_TYPE = 1;
    public static final int LEN_CSQ_LEVEL = 1;
    public static final int LEN_ERROR_STATUS = 1;
    public static final int LEN_METERVERSION  = 1;
    public static final int LEN_HW_VER = 2;
   
    
    private static Log log = LogFactory.getLog(KAMSTRUP_MDM.class);
    
    public KAMSTRUP_MDM(byte[] rawData)
    {
        super(rawData);
    }
    
    /**
     *  7 Bit (MSB) recovery metering data flag
        …       Don’t care  Reserved
        2 Bit       
        1 Bit   0   Event data + metering data (default).
                1   Event data .
        0 Bit (LSB) 0   Modem key is Phone number  (default).
                    1   Modem key is SIM card serial number
     */
    public boolean isEventOnly()
    {
        int ret = DataFormat.hex2unsigned8(rawData[OFS_SENDING_FLAG]) & 0x02;   
        if(ret > 0){
            return true;
        }
        return false;
    }
    
    /**
     *  7 Bit (MSB) recovery metering data flag
        …       Don’t care  Reserved
        2 Bit       
        1 Bit   0   Event data + metering data (default).
                1   Event data .
        0 Bit (LSB) 0   Modem key is Phone number  (default).
                    1   Modem key is SIM card serial number
     */
    public boolean isRecoveryMetering()
    {
        int ret = DataFormat.hex2unsigned8(rawData[OFS_SENDING_FLAG]) & 0x80;   
        if(ret > 0){
            return true;
        }
        return false;
            
    }
    
    /**
     *  7 Bit (MSB) recovery metering data flag
        …       Don’t care  Reserved
        2 Bit       
        1 Bit   0   Event data + metering data (default).
                1   Event data .
        0 Bit (LSB) 0   Modem key is Phone number  (default).
                    1   Modem key is SIM card serial number
     */
    public boolean isSimNumber()
    {
        int ret = DataFormat.hex2unsigned8(rawData[OFS_SENDING_FLAG]) & 0x01;   
        if(ret > 0){
            return true;
        }
        return false;
    }
    
    /**
     * 2.1.1.1 Kind of meter type
     * @return
     */
    public int getMETER_TYPE(){
        int ret = DataFormat.hex2unsigned8(rawData[OFS_METER_TYPE]);
        return ret;
    }
    
    public String getMeterVersion()
    {
        String ret = new String();
        try{
            ret = new String(DataFormat.select(rawData,OFS_METERVERSION,LEN_METERVERSION)).trim();

        }catch(Exception e){
            log.warn("invalid meterversion->"+e.getMessage());
        }
        return "382"+ret;
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
    
    public String getERROR_STATUS_STRING(){

        int code = DataFormat.hex2unsigned8(rawData[OFS_ERROR_STATUS]);

        switch(code){
            case 0: return "No error["+code+"]";
            case 1: return "Meter no answer["+code+"]";
            case 2: return "Meter CRC error["+code+"]";
            case 3: return "Meter identification error["+code+"]";
            case 4: return "Meter data format error["+code+"]";
            case 5: return "Meter data length error["+code+"]";
            default:
                return "unknown["+code+"]";
        }
    }
    
    /**
     * Modem phone number
     * @return
     */
    public String getPHONE_NUM(){
        
        String ret = "";
        try{
            if(!isSimNumber()){
                ret = new String(DataFormat.select(rawData,OFS_PHONE_NUM,LEN_PHONE_NUM)).trim();
            }
        }catch(Exception e){
            log.warn("invalid phone num->"+e.getMessage());
        }
        return ret;
    }
    
    public String getId(){
        
        String ret = "";
        try{
            ret = new String(DataFormat.select(rawData,OFS_PHONE_NUM,LEN_PHONE_NUM)).trim();
        }catch(Exception e){
            log.warn("invalid id->"+e.getMessage());
        }
        return ret;
    }
    
    public String getHW_VER()
    {
        String ret = new String();
        try{
            ret = new String(DataFormat.select(rawData,OFS_HW_VER,LEN_HW_VER)).trim();

        }catch(Exception e){
            log.warn("invalid hw ver->"+e.getMessage());
        }
        return ret;
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("NURI_MDM Meter DATA[");        
            sb.append("(FW_VER=").append(getFW_VER()).append("),");
            sb.append("(HW_VER=").append(getHW_VER()).append("),");
            sb.append("(isEventOnly=").append(isEventOnly()).append("),");
            sb.append("(isSimNumber=").append(isSimNumber()).append("),");
            sb.append("(isRecoveryMetering=").append(isRecoveryMetering()).append("),");
            sb.append("(PHONE_NUM=").append(getPHONE_NUM()).append("),");
            sb.append("(ID=").append(getId()).append("),");
            sb.append("(METER_TYPE=").append(getMETER_TYPE()).append("),");
            sb.append("(METER_VERSION=").append(getMeterVersion()).append("),");
            sb.append("(CSQ_LEVEL=").append(getCSQ_LEVEL()).append("),");
            sb.append("(ERROR_STATUS_STRING=").append(getERROR_STATUS_STRING()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("NURI_MDM TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}
