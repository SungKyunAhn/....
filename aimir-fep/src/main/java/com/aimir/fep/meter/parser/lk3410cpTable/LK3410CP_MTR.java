/* 
 * @(#)LK3410CP_MTR.java       1.0 07/11/08 *
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
package com.aimir.fep.meter.parser.lk3410cpTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * @author Kang Soyi ksoyi@nuritelecom.com
 */
public class LK3410CP_MTR {    
    
    public static final int LEN_PROGRAM_NAME_VER = 8;
    public static final int LEN_CT_PT = 4;
    public static final int LEN_REG_K = 2;
    public static final int LEN_SCALE_FACTOR = 1;
    public static final int LEN_PULSE_INITIATOR = 1;
    public static final int LEN_KYZ_DIVISOR = 1;
    public static final int LEN_DATA_FORMAT = 5;
    public static final int LEN_METERING_DATE =1;
    public static final int LEN_METER_ID = 6;
    public static final int LEN_SUPPLY_TYPE = 1;
    public static final int LEN_CURRENT_METER_DATETIME = 7;
    public static final int LEN_CURRENT_PROGRAM_DATE =7;
    public static final int LEN_METER_ERROR_FLAG =1;
    public static final int LEN_METER_EVENT_FLAG =1;
    public static final int LEN_METER_CAUTION_FLAG =1;
    
    public static final int OFS_PROGRAM_NAME_VER = 0;
    public static final int OFS_CT_PT = 8;
    public static final int OFS_REG_K = 12;
    public static final int OFS_SCALE_FACTOR = 14;
    public static final int OFS_PULSE_INITIATOR = 15;
    public static final int OFS_KYZ_DIVISOR = 16;
    public static final int OFS_DATA_FORMAT = 17;
    public static final int OFS_METERING_DATE =22;
    public static final int OFS_METER_ID = 23;
    public static final int OFS_SUPPLY_TYPE = 29;
    public static final int OFS_CURRENT_METER_DATETIME = 30;
    public static final int OFS_CURRENT_PROGRAM_DATE =37;
    public static final int OFS_METER_ERROR_FLAG =44;
    public static final int OFS_METER_EVENT_FLAG =45;
    public static final int OFS_METER_CAUTION_FLAG =46;
    
    protected ProgramNameVer PROGRAM_NAME_VER;
    protected int CT_PT;
    protected int REG_K;
    protected int SCALE_FACTOR;
    protected double PULSE_INITIATOR;
    protected String KYZ_DIVISOR;
    protected String DATA_FORMAT;
    protected String METERING_DATE;
    protected String METER_ID;
    protected String CURRENT_METER_DATETIME;
    protected String CURRENT_PROGRAM_DATE;
    protected String METER_ERROR_FLAG;
    protected String METER_EVENT_FLAG;
    protected String CAUTION_FLAG;
    
	private byte[] rawData = null;

    private static Log log = LogFactory.getLog(LK3410CP_MTR.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public LK3410CP_MTR(byte[] rawData) {
        this.rawData = rawData;
	}
    
    public ProgramNameVer getProgramNameVer() throws Exception {
        return new ProgramNameVer(
            DataFormat.select(
                rawData,OFS_PROGRAM_NAME_VER,LEN_PROGRAM_NAME_VER));
    }
    
    public int getCT_PT() throws Exception {
        return DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(
                    rawData,OFS_CT_PT,LEN_CT_PT)));
    }
    
    public int getREG_K() throws Exception {
        return DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(
                    rawData,OFS_REG_K,LEN_REG_K)));
    }
    
    public int getSCALE_FACTOR() throws Exception {
        return DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(
                    rawData,OFS_SCALE_FACTOR,LEN_SCALE_FACTOR)));
    }
    
    public int getPULSE_INITIATOR() throws Exception{
        return DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(
                   rawData,OFS_PULSE_INITIATOR,LEN_PULSE_INITIATOR)));

    }

    public String getKYZ_DIVISOR(){
        
        String res = new String();
        try{
            res = new String(DataFormat.select(rawData,OFS_KYZ_DIVISOR,LEN_KYZ_DIVISOR)).trim();

        }catch(Exception e){
            log.warn("PULSE_INITIATOR->"+e.getMessage());
        }

        return res;
    }
    
    public DataFormatForMetering getDataFormatForMetering() throws Exception {
        return new DataFormatForMetering(
            DataFormat.select(
                rawData,OFS_DATA_FORMAT,LEN_DATA_FORMAT));
    }
    
    public int getMETERING_DATE() throws Exception {
        return DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(
                    rawData,OFS_METERING_DATE,LEN_METERING_DATE)));
    }
    
    public MeterManufacture getMeterManufacture() throws Exception {
        return new MeterManufacture(
            DataFormat.select(
                rawData,OFS_METER_ID,LEN_METER_ID));
    }
    
    public MeterTypeConfig getMeterTypeConfig() throws Exception {
        return new MeterTypeConfig(rawData[OFS_SUPPLY_TYPE]);
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
    
   public String getCURRENT_PROGRAM_DATE(){
        
        String res = new String();
        try{
            res = new DateTimeFormat(DataFormat.select(
                      rawData,OFS_CURRENT_PROGRAM_DATE,LEN_CURRENT_PROGRAM_DATE)).getDateTime();

        }catch(Exception e){
            log.warn("CURRENT_PROGRAM_DATE->"+e.getMessage());
        }

        return res;
    }
   
    public MeterErrorFlag getMeterErrorFlag() throws Exception {
       return new MeterErrorFlag(rawData[OFS_METER_ERROR_FLAG]);
    }
    
    public MeterEventFlag getMeterEventFlag() throws Exception {
       return new MeterEventFlag(rawData[OFS_METER_EVENT_FLAG]);
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
        String id = "";
        try{
            id =  getMeterManufacture().getMeterId();
        }catch(Exception e){
            log.warn("METER_ID->"+e.getMessage());
        }
        
        return id;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("LK3410CP_MTR DATA[");        
            sb.append("(ProgramNameVer =").append(getProgramNameVer().toString()).append("),");
            sb.append("(CT_PT=").append(""+getCT_PT()).append("),");
            sb.append("(REG_K=").append(""+getREG_K()).append("),");
            sb.append("(SCALE_FACTOR=").append(""+getSCALE_FACTOR()).append("),");
            sb.append("(PULSE_INITIATOR=").append(""+getPULSE_INITIATOR()).append("),");
            sb.append("(KYZ_DIVISOR=").append(getKYZ_DIVISOR()).append("),");
            sb.append("(DataFormatForMetering=").append(getDataFormatForMetering().toString()).append("),");
            sb.append("(METERING_DATE=").append(""+getMETERING_DATE()).append("),");
            sb.append("(MeterManufacture=").append(getMeterManufacture().toString()).append("),");
            sb.append("(CURRENT_METER_DATETIME=").append(getCURRENT_METER_DATETIME()).append("),");
            sb.append("(CURRENT_PROGRAM_DATE=").append(getCURRENT_PROGRAM_DATE()).append("),");
            sb.append("(MeterErrorFlag=").append(getMeterErrorFlag().toString()).append("),");
            sb.append("(MeterEventFlag=").append(getMeterEventFlag().toString()).append("),");
            sb.append("(MeterCautionFlag=").append(getMeterCautionFlag().toString()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("LK3410CP_MTR TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }

}
