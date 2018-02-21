/* 
 * @(#)DLMSKepco_MTR.java       1.0 07/11/08 *
 * 
 * Meter Information
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
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
package com.aimir.fep.meter.parser.DLMSKepcoTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * @author Kang Soyi ksoyi@nuritelecom.com
 */
public class DLMSKepco_MTR implements java.io.Serializable{    

	private static final long serialVersionUID = -514675314432494197L;
	public static final int LEN_MANUFACTURAR = 1;
	public static final int LEN_KIND_OF_METER = 1;
	public static final int LEN_METER_ID = 8;
	public static final int LEN_REG_K = 2;
	public static final int LEN_VT = 2;
	public static final int LEN_CT = 2;
	public static final int LEN_BILLDAY = 1;
	public static final int LEN_RESOLUTION = 1; //LP INTERVAL
	public static final int LEN_LAST_METERING_DATETIME= 6;
	public static final int LEN_LAST_DEMAND_RESET_DATETIME= 9;
	public static final int LEN_CURRENT_METER_DATETIME= 8;
	   
	public static final int OFS_MANUFACTURAR = 0;
	public static final int OFS_KIND_OF_METER = 1;
	public static final int OFS_METER_ID = 2;
	public static final int OFS_METER_ERROR_FLAG = 10;
	public static final int OFS_METER_CAUTION_FLAG = 11;
	public static final int OFS_REG_K = 12;
	public static final int OFS_VT = 14;
	public static final int OFS_CT = 16;
	public static final int OFS_BILLDAY = 18;
	public static final int OFS_RESOLUTION = 19; //LP INTERVAL
	public static final int OFS_LAST_METERING_DATETIME= 20;
	public static final int OFS_LAST_DEMAND_RESET_DATETIME= 26;
	public static final int OFS_CURRENT_METER_DATETIME= 35;
	
	private byte[] rawData = null;

    private static Log log = LogFactory.getLog(DLMSKepco_MTR.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public DLMSKepco_MTR(byte[] rawData) {
        this.rawData = rawData;
	}
            
	public int getKindOfMeter() throws Exception {
		 byte kind = rawData[OFS_KIND_OF_METER];
		 return kind;
	}
	
    public MeterManufacture getMeterManufacture() throws Exception {
        return new MeterManufacture(
            DataFormat.select(
                rawData,OFS_METER_ID,LEN_METER_ID));
    }
    
    public int getREG_K() throws Exception {
        return DataFormat.hex2dec(DataFormat.select(
                    rawData,OFS_REG_K,LEN_REG_K));
    }
        
    public int getCT() throws Exception {
        return DataFormat.hex2dec(DataFormat.select(
                    rawData,OFS_CT,LEN_CT));
    }
    
    public int getVT() throws Exception {
        return DataFormat.hex2dec(DataFormat.select(
                    rawData,OFS_VT,LEN_VT));
    }
    
    public int getBillDay() throws Exception {
        return DataFormat.hex2unsigned8(rawData[OFS_BILLDAY]);
    }
        
    public int getResolution() throws Exception {
        return DataFormat.hex2unsigned8(rawData[OFS_RESOLUTION]);
    }
    
    public String getLAST_METERING_DATE() throws Exception {
      	String datetime = "";
    	try{
    		datetime = new DateTimeFormat(DataFormat.select(
                    rawData,OFS_LAST_METERING_DATETIME,LEN_LAST_METERING_DATETIME)).getDateTime();
    	}catch(Exception e){
            log.warn("LAST_METERING_DATE->"+e.getMessage());
        }

    	return datetime;
    }
    public String getLAST_DEMAND_RESET_DATETIME() throws Exception {
    	String datetime = "";
    	try{
    		datetime = new DateTimeFormat(DataFormat.select(
                    rawData,OFS_LAST_DEMAND_RESET_DATETIME,LEN_LAST_DEMAND_RESET_DATETIME)).getDateTime();
    	}catch(Exception e){
            log.warn("LAST_DEMAND_RESET_DATETIME->"+e.getMessage());
        }
    	return datetime;
    }
    
    public String getCURRENT_METER_DATETIME(){
        
        String res = new String();
        try{
            res = new DateTimeFormat(DataFormat.select(
                      rawData,OFS_CURRENT_METER_DATETIME,LEN_CURRENT_METER_DATETIME)).getDateTime();
        }catch(Exception e){
            log.warn("CURRENT_METER_DATETIME->"+e.getMessage());
        }
        return res;
    }
    
    public MeterErrorFlag getMeterErrorFlag() throws Exception {
       return new MeterErrorFlag(rawData[OFS_METER_ERROR_FLAG]);
    }
     
    public MeterCautionFlag getMeterCautionFlag() throws Exception {
       return new MeterCautionFlag(rawData[OFS_METER_CAUTION_FLAG]);
    }    
   
    public String getDateTime(){
        String date="";
        try{
           date = getCURRENT_METER_DATETIME();
        }catch(Exception e){
            log.warn("DateTime->"+e.getMessage());
        }
        return date;
    }
    
    public String getMETER_ID(){
        String res = new String();
        try{
            res = new String(
                    DataFormat.select(rawData,OFS_METER_ID,LEN_METER_ID)).trim();

        }catch(Exception e){
            log.warn("METER_SERIAL->"+e.getMessage());
        }
        return res;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("DLMSKepco_MTR DATA[");        
            sb.append("(CT=").append(""+getCT()).append("),");
            sb.append("(VT=").append(""+getVT()).append("),");
            sb.append("(REG_K=").append(""+getREG_K()).append("),");
            sb.append("(MeterManufacture=").append(getMeterManufacture().toString()).append("),");
            sb.append("(CURRENT_METER_DATETIME=").append(getCURRENT_METER_DATETIME()).append("),");
            sb.append("(MeterErrorFlag=").append(getMeterErrorFlag().toString()).append("),");
            sb.append("(MeterCautionFlag=").append(getMeterCautionFlag().toString()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("DLMSKepco_MTR TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }

}
