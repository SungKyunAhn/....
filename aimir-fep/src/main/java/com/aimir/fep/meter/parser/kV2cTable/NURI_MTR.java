/* 
 * @(#)NURI_MTR.java       1.0 07/05/02 *
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
 
package com.aimir.fep.meter.parser.kV2cTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class NURI_MTR {    
    
    public int OFS_MANUFACTURER = 0;
    public int OFS_ED_MODEL = 4;
    public int OFS_METER_ID = 12;
    public int OFS_MODE_AND_STATUS = 28;
    public int OFS_CLOCK_CALENDAR = 33;
    //if total length 83 then calendar length=6 else if 86 then length = 9
    public int OFS_VA_SF = 42;
    public int OFS_VAH_SF = 45;
    public int OFS_DISP_SCALAR = 47;
    public int OFS_DISP_MULTIPLIER = 48;
    public int OFS_SERVICE_OVERRIDE = 52;
    public int OFS_DEFAULT_DSP_CASE = 53;
    public int OFS_SERVICE_IN_USE = 54;
    public int OFS_CUR_TRANS_RATIO = 55;
    public int OFS_POT_TRANS_RATIO = 57;
    public int OFS_PROGRAM_ID = 59;
    public int OFS_DT_LAST_PROGRAMMED = 61;
    public int OFS_PROGRAMMER_NAME = 66;
    public int OFS_ACTUAL_REGISTER = 76;

    public int LEN_MANUFACTURER = 4;
    public int LEN_ED_MODEL = 8;
    public int LEN_METER_ID = 16;
    public int LEN_MODE_AND_STATUS = 5;
    public int LEN_CLOCK_CALENDAR = 9;
    public int LEN_VA_SF = 3;
    public int LEN_VAH_SF = 2;
    public int LEN_DISP_SCALAR = 1;
    public int LEN_DISP_MULTIPLIER = 4;
    public int LEN_SERVICE_OVERRIDE = 1;
    public int LEN_DEFAULT_DSP_CASE = 1;
    public int LEN_SERVICE_IN_USE = 1;
    public int LEN_CUR_TRANS_RATIO = 2;
    public int LEN_POT_TRANS_RATIO = 2;
    public int LEN_PROGRAM_ID = 2;
    public int LEN_DT_LAST_PROGRAMMED = 5;
    public int LEN_PROGRAMMER_NAME = 10;
    public int LEN_ACTUAL_REGISTER = 10;
    
    protected String MANUFACTURER;          //String 4    Manufacturer code   ST1
    protected String ED_MODEL;              //String 8    Model identifier of this device ST1
    protected String METER_ID;              //String 16   Serial number of meter  ST1
    protected String MODE_AND_STATUS;       //Unsigned 40 End device mode and status (all data of ST3)    ST3
    protected String CLOCK_CALENDAR;        //Unsigned 72 Current date and time   ST55
    protected String VA_SF;                 //Unsigned 24 Power scale factor  MT75
    protected String VAH_SF;                //Unsigned 16 Energy scale factor MT75
    protected String DISP_SCALAR;           //Unsigned 8  Scale to be applied to quantities before display    MT70
    protected String DISP_MULTIPLIER;       //Unsigned 32 Display Multiplier  MT70
    protected String SERVICE_OVERRIDE;      //Unsigned 8  Service to use when automatic detection is overridden.  MT86
    protected String DEFAULT_DSP_CASE;      //Unsigned 8  DSP case to use when automatic detection is overridden. MT86
    protected String SERVICE_IN_USE;        //Unsigned 8  Indicates current electrical service    MT87
    protected String CUR_TRANS_RATIO;       //Unsigned 16 Current transformer ratio   MT67
    protected String POT_TRANS_RATIO;       //Unsigned 16 Voltage transformer ratio   MT67
    protected String PROGRAM_ID;            //Unsigned 16 ID of meter program MT67
    protected String DT_LAST_PROGRAMMED;    //Unsigned 40 Date and time meter was last programmed MT78
    protected String PROGRAMMER_NAME;       //Unsigned 80 Name of person who last programmed the meter    MT78
    protected String ACTUAL_REGISTER;       //Unsigned 80 Actual register table (All data of ST 21)   ST21
	
	private byte[] rawData = null;

    private static Log log = LogFactory.getLog(NURI_MTR.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public NURI_MTR(byte[] rawData) {
        this.rawData = rawData;
        if(rawData.length == 83){
            LEN_CLOCK_CALENDAR = 6;
            OFS_VA_SF = 39;
            OFS_VAH_SF = 42;
            OFS_DISP_SCALAR = 44;
            OFS_DISP_MULTIPLIER = 45;
            OFS_SERVICE_OVERRIDE = 49;
            OFS_DEFAULT_DSP_CASE = 50;
            OFS_SERVICE_IN_USE = 51;
            OFS_CUR_TRANS_RATIO = 52;
            OFS_POT_TRANS_RATIO = 54;
            OFS_PROGRAM_ID = 56;
            OFS_DT_LAST_PROGRAMMED = 58;
            OFS_PROGRAMMER_NAME = 63;
            OFS_ACTUAL_REGISTER = 73;
        }
	}
    
    public String getMANUFACTURER(){
        
        String manufacturer = new String();
        try{
            manufacturer = new String(
                               DataFormat.select(
                                   rawData,OFS_MANUFACTURER,LEN_MANUFACTURER)).trim();

        }catch(Exception e){
            log.warn("invalid model->"+e.getMessage());
        }

        return manufacturer;
    }
    
    public String getED_MODEL(){
        
        String model = new String();
        try{
            model = new String(DataFormat.select(rawData,OFS_ED_MODEL,LEN_ED_MODEL)).trim();

        }catch(Exception e){
            log.warn("invalid model->"+e.getMessage());
        }

        return model;
    }

    public String getMETER_ID() {

        String mserial = new String("11111111");

        try{
            mserial 
                = new String(DataFormat.select(rawData,OFS_METER_ID,LEN_METER_ID)).trim();
            
            if(mserial == null || mserial.equals("")){
                mserial = "11111111";
            }

        }catch(Exception e){
            log.warn("invalid meter id->"+e.getMessage());
        }

        return mserial;
    }

    public String getDateTime(){
        
        String datetime = new String();
        try{
            byte[] calendar 
                = DataFormat.select(rawData, OFS_CLOCK_CALENDAR, LEN_CLOCK_CALENDAR);
            ST055 st055 = new ST055(calendar);
            datetime = st055.getDateTime();
        }catch(Exception e){
            log.warn("invalid meter id->"+e.getMessage());
        }

        return datetime;
    }
        
    public int getVA_SF() throws Exception {
        return DataFormat.hex2dec(
            DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_VA_SF,LEN_VA_SF)));
    }
    
    public int getVAH_SF() throws Exception {
        return DataFormat.hex2dec(
            DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_VAH_SF,LEN_VAH_SF)));
    }
    
    public int getDISP_SCALAR() throws Exception {
        int ds = DataFormat.hex2unsigned8(rawData[OFS_DISP_SCALAR]);

        /*        
        switch(ds){
            case 0:
                return 1;
            case 1:
                return Math.pow(10,-1);
            case 2:
                return Math.pow(10,-2);
            case 3:
                return Math.pow(10,-3);
            default:
                return 1;
        }
        */
        return ds;
    }
    
    public int getDISP_MULTIPLIER() throws Exception {
        return DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                    DataFormat.select(
                        rawData,OFS_DISP_MULTIPLIER,LEN_DISP_MULTIPLIER)));
    }
    
    public int getCUR_TRANS_RATIO() throws Exception {
        return DataFormat.hex2dec(
            DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_CUR_TRANS_RATIO,LEN_CUR_TRANS_RATIO)));
    }
    
    public int getPOT_TRANS_RATIO() throws Exception {
        return DataFormat.hex2dec(
            DataFormat.LSB2MSB(
                DataFormat.select(
                    rawData,OFS_POT_TRANS_RATIO,LEN_POT_TRANS_RATIO)));
    }
    
    public ST021 getST021() throws Exception {
        return new ST021(
            DataFormat.select(
                rawData,OFS_ACTUAL_REGISTER,LEN_ACTUAL_REGISTER));
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("NURI_MTR Meter DATA[");        
            sb.append("(MANUFACTURER=").append(getMANUFACTURER()).append("),");
            sb.append("(ED_MODEL=").append(getED_MODEL()).append("),");
            sb.append("(METER_ID=").append(getMETER_ID()).append("),");
            sb.append("(DateTime=").append(getDateTime()).append("),");
            sb.append("(VA_SF=").append(getVA_SF()).append("),");
            sb.append("(VAH_SF=").append(getVAH_SF()).append("),");
            sb.append("(DISP_SCALAR=").append(getDISP_SCALAR()).append("),");
            sb.append("(DISP_MULTIPLIER=").append(getDISP_MULTIPLIER()).append("),");
            sb.append("(CUR_TRANS_RATIO=").append(getCUR_TRANS_RATIO()).append("),");
            sb.append("(POT_TRANS_RATIO=").append(getPOT_TRANS_RATIO()).append(')');
            sb.append("(ST021=").append(getST021()).append("),");
            sb.append("]\n");
        }catch(Exception e){
            log.warn("NURI_MTR TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }

}
