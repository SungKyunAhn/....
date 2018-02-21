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
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class KAMSTRUPKMP_MDM {
    
    private byte[] FW_VER = new byte[5];
    private byte[] SENDING_FLAG = new byte[1];
    private byte[] PHONE_NUM = new byte[20];
    private byte[] METER_VENDOR = new byte[1];
    private byte[] CSQ_LEVEL = new byte[1];
    private byte[] ERROR_STATUS = new byte[1];
    private byte[] METER_TYPE = new byte[2];
    private byte[] METER_VERSION = new byte[2];
    private byte[] HW_VER = new byte[2];
    private byte[] UNIT = new byte[1];
    private byte[] DECIMAL_FORMAT = new byte[1];
    private byte[] IMSI_NUM = new byte[21];
    private byte[] IMEI_NUM = new byte[16];
    private byte[] SERIAL_NUM = new byte[16];

    private byte[] rawData;
    
    private static Log log = LogFactory.getLog(KAMSTRUPKMP_MDM.class);
    
    public KAMSTRUPKMP_MDM(byte[] rawData)
    {
		this.rawData = rawData;
		parse();
    }
    
    
    public void parse() {
    	int pos = 0;
        System.arraycopy(rawData, pos, FW_VER, 0, FW_VER.length);
        pos += FW_VER.length;
        System.arraycopy(rawData, pos, SENDING_FLAG, 0, SENDING_FLAG.length);
        pos += SENDING_FLAG.length;
        System.arraycopy(rawData, pos, PHONE_NUM, 0, PHONE_NUM.length);
        pos += PHONE_NUM.length;
        System.arraycopy(rawData, pos, METER_VENDOR, 0, METER_VENDOR.length);
        pos += METER_VENDOR.length;
        System.arraycopy(rawData, pos, CSQ_LEVEL, 0, CSQ_LEVEL.length);
        pos += CSQ_LEVEL.length;
        System.arraycopy(rawData, pos, ERROR_STATUS, 0, ERROR_STATUS.length);
        pos += ERROR_STATUS.length;
        System.arraycopy(rawData, pos, METER_TYPE, 0, METER_TYPE.length);
        pos += METER_TYPE.length;
        System.arraycopy(rawData, pos, METER_VERSION , 0, METER_VERSION.length);
        pos += METER_VERSION.length; 
        System.arraycopy(rawData, pos, HW_VER , 0, HW_VER.length);
        pos += HW_VER.length;
        System.arraycopy(rawData, pos, UNIT , 0, UNIT.length);
        pos += UNIT.length;
        System.arraycopy(rawData, pos, DECIMAL_FORMAT , 0, DECIMAL_FORMAT.length);
        pos += DECIMAL_FORMAT.length;
        System.arraycopy(rawData, pos, IMSI_NUM , 0, IMSI_NUM.length);
        pos += IMSI_NUM.length;
        System.arraycopy(rawData, pos, IMEI_NUM , 0, IMEI_NUM.length);
        pos += IMEI_NUM.length;
        System.arraycopy(rawData, pos, SERIAL_NUM , 0, SERIAL_NUM.length);
        pos += SERIAL_NUM.length;
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
        int ret = DataFormat.hex2unsigned8(SENDING_FLAG[0]) & 0x02;   
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
        int ret = DataFormat.hex2unsigned8(SENDING_FLAG[0]) & 0x80;   
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
        int ret = DataFormat.hex2unsigned8(SENDING_FLAG[0]) & 0x01;   
        if(ret > 0){
            return true;
        }
        return false;
    }
    
    /**
     * 2.1.1.1 Kind of meter type
     * @return
     */
    public int getMETER_VENDOR(){
        int ret = DataFormat.hex2unsigned8(METER_TYPE[0]);
        return ret;
    }
    
    public String getMETER_TYPE(){
         String meterType = "Unknown";
         
        if (METER_TYPE[0] == 0x53 && METER_TYPE[1] == 0x00)
        	meterType = "Meter K382B / K382C";
        else if (METER_TYPE[0] == 0x53 && METER_TYPE[1] == 0x01)
        	meterType = "Meter K382Jx2,3,4,5,6,7,8,9";
        else if (METER_TYPE[0] == 0x53 && METER_TYPE[1] == 0x03)
        	meterType = "Meter K382Kx2,3,5,7";        
        else if (METER_TYPE[0] == 0x55 && METER_TYPE[1] == 0x00)
        	meterType = "Meter K382D / K382E";
        else if (METER_TYPE[0] == 0x55 && METER_TYPE[1] == 0x01)
        	meterType = "Meter K382JxB,C,D,F,G";
        else if (METER_TYPE[0] == 0x55 && METER_TYPE[1] == 0x02)
        	meterType = "Meter K382 NTA";
        else if (METER_TYPE[0] == 0x55 && METER_TYPE[1] == 0x03)
        	meterType = "Meter K382KxB,C,E,G";        
        else if (METER_TYPE[0] == 0x56 && METER_TYPE[1] == 0x00)
        	meterType = "meter K162B / K162C";
        else if (METER_TYPE[0] == 0x56 && METER_TYPE[1] == 0x01)
        	meterType = "meter K162Jx3,6,7";
        else if (METER_TYPE[0] == 0x56 && METER_TYPE[1] == 0x03)
        	meterType = "meter K162Kx3,7";        
        else if (METER_TYPE[0] == 0x57 && METER_TYPE[1] == 0x00)
        	meterType = "Meter K162D / K162E";
        else if (METER_TYPE[0] == 0x57 && METER_TYPE[1] == 0x01)
        	meterType = "Meter K162JxC,F,G";
        else if (METER_TYPE[0] == 0x57 && METER_TYPE[1] == 0x03)
        	meterType = "Meter K162KxC,G";        
        else if (METER_TYPE[0] == 0x58 && METER_TYPE[1] == 0x00)
        	meterType = "Meter K282B / K282C";
        else if (METER_TYPE[0] == 0x58 && METER_TYPE[1] == 0x01)
        	meterType = "Meter K282Jx2,3,4,5,6,7,8,9";
        else if (METER_TYPE[0] == 0x58 && METER_TYPE[1] == 0x03)
        	meterType = "Meter K282Kx2,3,5,7";        
        else if (METER_TYPE[0] == 0x59 && METER_TYPE[1] == 0x00)
        	meterType = "Meter K282D / K282E";
        else if (METER_TYPE[0] == 0x59 && METER_TYPE[1] == 0x01)
        	meterType = "Meter K282JxB,C,D,E,F,G";
        else if (METER_TYPE[0] == 0x59 && METER_TYPE[1] == 0x03)
        	meterType = "Meter K282KxB,C,E,G";
        else if (METER_TYPE[0] == 0x60 && METER_TYPE[1] == 0x00)
        	meterType = "Meter K351B";
        else if (METER_TYPE[0] == 0x61 && METER_TYPE[1] == 0x00)
        	meterType = "Meter K351B Aron";

        return meterType;
    }
    
    public String getMeterVersion()
    {
        String ret = new String();
        try{
            ret = "Rev.";
            char[] ch = new char[26];
            int idx = 0;
            for (char i = 'A'; i <= 'Z'; i++) {
                ch[idx++] = i;
            }    
            int swRevision = DataUtil.getIntToByte(METER_VERSION[0]);
            if(swRevision <=25 ){
                ret += ch[swRevision-1];
            }else if(swRevision == 32){
            	ret += "AA";
            }else if(swRevision == 33){
            	ret += "AB";
            }

            ret += DataUtil.getIntToByte(METER_VERSION[1]);

        }catch(Exception e){
            log.warn("invalid meterversion->"+e.getMessage());
        }
        return ret;
    }
    
    /**
     * Mobile RF CSQ level (0~31, 99)
     * @return
     */
    public int getCSQ_LEVEL(){
    	
        int ret = DataFormat.hex2unsigned8(CSQ_LEVEL[0]);

        if(ret != 0){
            ret = -(113-(ret*2));
        }
        return ret;
    }
    
    public String getERROR_STATUS_STRING(){

        int code = DataFormat.hex2unsigned8(ERROR_STATUS[0]);

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
                ret = new String(PHONE_NUM).trim();
            }
        }catch(Exception e){
            log.warn("invalid phone num->"+e.getMessage());
        }
        return ret;
    }
    
    public String getId(){
        
        String ret = "";
        try{
            ret = new String(PHONE_NUM).trim();
        }catch(Exception e){
            log.warn("invalid id->"+e.getMessage());
        }
        return ret;
    }
    
    /**
     * Firmware version
     * @return
     */
    public String getFW_VER(){
        
        String ret = new String();
        try{
            ret = new String(FW_VER).trim();

        }catch(Exception e){
            log.warn("invalid model->"+e.getMessage());
        }

        return ret;
    }
    
    public String getHW_VER()
    {
        String ret = new String();
        try{
            ret = new String(HW_VER).trim();

        }catch(Exception e){
            log.warn("invalid hw ver->"+e.getMessage());
        }
        return ret;
    }
    
    public String getSERIAL_NUM() {
        String ret = new String();
        try{
            ret = new String(SERIAL_NUM).trim();

        }catch(Exception e){
            log.warn("invalid serial->"+e.getMessage());
        }
        return ret;
    }
    
    public String getIMSI_NUM() {
        String ret = new String();
        try{
            ret = new String(IMSI_NUM).trim();

        }catch(Exception e){
            log.warn("invalid imsi num->"+e.getMessage());
        }
        return ret;
    }
    
    public String getIMEI_NUM() {
        String ret = new String();
        try{
            ret = new String(IMEI_NUM).trim();

        }catch(Exception e){
            log.warn("invalid imei num->"+e.getMessage());
        }
        return ret;
    }
    
    public String getUNIT(){
    	
    	int unit = DataFormat.hex2unsigned8(UNIT[0]);
    	
        switch(unit){
        case 1:
            return "Wh";
        case 2:
            return "kWh";
        case 3:
            return "MWh";
        case 4:
            return "GWh";
        case 13:
            return "varh";
        case 14:
            return "kvarh";
        case 15:
            return "Mvarh";
        case 16:
            return "Gvarh";
        case 17:
            return "VAh";
        case 18:
            return "kVAh";
        case 19:
            return "MVAh";
        case 20:
            return "GVAh";
        case 21:
            return "W";
        case 22:
            return "kW";
        case 23:
            return "MW";
        case 24:
            return "GW";
        case 25:
            return "var";
        case 26:
            return "kvar";
        case 27:
            return "Mvar";
        case 28:
            return "Gvar";
        case 29:
            return "VA";
        case 30:
            return "kVA";
        case 31:
            return "MVA";
        case 32:
            return "GVA";
        case 33:
            return "V";
        case 34:
            return "A";
        case 35:
            return "kV";
        case 36:
            return "kA";
        case 37:
            return "c";
        case 38:
            return "K";
        case 39:
            return "l";
        case 40:
            return "m3";
        case 46:
            return "h";
        case 47:
            return "clock";
        case 48:
            return "dato1";
        case 51:
            return "number";
        case 53:
            return "RTC";
        case 54:
            return "ASCII coded data";
        case 55:
            return "m3 x 10";
        case 56:
            return "ton x 10";
        case 57:
            return "Gj x 10";   
        default:
            return "Unknown";
        }
    }
    
    public double getDECIMAL_FORMAT() { 
    	
        int signInt=0;
        int signExp=0;
        int exp=0;        
        double siEx=0;
    	
        byte byteSiEx= DECIMAL_FORMAT[0];
        signInt=(byteSiEx & 128)/128;
        signExp=(byteSiEx & 64)/64;
        exp=((byteSiEx&8) + (byteSiEx&4) + (byteSiEx&2) + (byteSiEx&1));
        siEx=Math.pow(-1, signInt)*Math.pow(10, Math.pow(-1, signExp)*exp);//-1^SI*-1^SE*exponent  
        return siEx;
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
            sb.append("(METER_VENDOR=").append(getMETER_VENDOR()).append("),");
            sb.append("(METER_TYPE=").append(getMETER_TYPE()).append("),");
            sb.append("(METER_VERSION=").append(getMeterVersion()).append("),");
            sb.append("(CSQ_LEVEL=").append(getCSQ_LEVEL()).append("),");            
            sb.append("(UNIT=").append(getUNIT()).append("),");
            sb.append("(DECIMAL_FORMAT=").append(getDECIMAL_FORMAT()).append("),");
            sb.append("(IMSI_NUM=").append(getIMSI_NUM()).append("),");
            sb.append("(IMEI_NUM=").append(getIMEI_NUM()).append("),");
            sb.append("(SERIAL_NUM=").append(getSERIAL_NUM()).append("),");
            
            sb.append("(ERROR_STATUS_STRING=").append(getERROR_STATUS_STRING()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("NURI_MDM TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}
