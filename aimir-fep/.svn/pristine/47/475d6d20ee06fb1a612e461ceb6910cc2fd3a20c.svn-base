/*
 * @(#)Mk6N_MTR.java       2008/08/11 *
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

package com.aimir.fep.meter.parser.Mk6NTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * @author kaze kaze@nuritelecom.com
 */
public class Mk6N_MTR implements java.io.Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8654195101502311686L;
	//Length
    public static final int LEN_KIND_METER_DATA = 1;
    public static final int LEN_MODEL_ID_NO = 10;
    public static final int LEN_METER_ID = 12;
    public static final int LEN_EZIO_STATUS = 1;
    public static final int LEN_CURRENT_STATUS = 16;
    public static final int LEN_LATCHED_STATUS = 16;
    public static final int LEN_CURR_DATE_TIME = 6;
    public static final int LEN_DST_DATE_TIME = 6;
    public static final int LEN_DST_ACTIVE = 1;
    public static final int LEN_CT_RATIO_MUL = 4;
    public static final int LEN_PT_RATIO_MUL = 4;
    public static final int LEN_CT_RATIO_DIV = 4;
    public static final int LEN_PT_RATIO_DIV = 4;
    public static final int LEN_MEASURE_METHOD = 1;
    public static final int LEN_MEASURE_OPTION = 2;
    public static final int LEN_NUM_OF_PWR_UP = 4;
    public static final int LEN_LAST_DT_OUTAGE = 6;
    public static final int LEN_LAST_DT_RECOVERY = 6;

    //Offset
    public static final int OFF_KIND_METER_DATA = 0;
    public static final int OFF_MODEL_ID_NO = 1;
    public static final int OFF_METER_ID = 11;
    public static final int OFF_EZIO_STATUS = 23;
    public static final int OFF_CURRENT_STATUS = 24;
    public static final int OFF_LATCHED_STATUS = 40;
    public static final int OFF_CURR_DATE_TIME = 56;
    public static final int OFF_DST_DATE_TIME = 62;
    public static final int OFF_DST_ACTIVE = 68;
    public static final int OFF_CT_RATIO_MUL = 69;
    public static final int OFF_PT_RATIO_MUL = 73;
    public static final int OFF_CT_RATIO_DIV = 77;
    public static final int OFF_PT_RATIO_DIV = 81;
    public static final int OFF_MEASURE_METHOD = 85;
    public static final int OFF_MEASURE_OPTION = 86;
    public static final int OFF_NUM_OF_PWR_UP = 88;
    public static final int OFF_LAST_DT_OUTAGE = 92;
    public static final int OFF_LAST_DT_RECOVERY = 98;

	private byte[] rawData = null;
	protected String MODEL_ID_NO;
	protected String METER_ID;
	protected int EZIO_STATUS;
	protected String STRING_EZIO_STATUS;
	protected StatusFlag CURRENT_STATUS;
	protected StatusFlag LATCHED_STATUS;
	protected String CURR_DATE_TIME;
	protected String DST_DATE_TIME;
	protected Boolean DST_ACTIVE;
	protected Float CT_RATIO_MUL;
	protected Float PT_RATIO_MUL;
	protected Float CT_RATIO_DIV;
	protected Float PT_RATIO_DIV;
	protected int MEASURE_METHOD;
	protected int MEASURE_OPTION;
	protected int NUM_OF_PWR_UP;
	protected String LAST_DT_OUTAGE;
	protected String LAST_DT_RECOVERY;

    private static Log log = LogFactory.getLog(Mk6N_MTR.class);
	/**
	 * Constructor .<p>
	 *
	 * @param data - read data (header,crch,crcl)
	 */
	public Mk6N_MTR(byte[] rawData) {
        this.rawData = rawData;
	}

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("Mk6N_MTR DATA[");
            sb.append("(ModelIDNo =").append(getMODEL_ID_NO()).append("),");
            sb.append("(MeterID =").append(getMETER_ID()).append("),");
            sb.append("(EZIOStatus =").append(getSTRING_EZIO_STATUS()).append("),");
            sb.append("(CurrentStatus =").append(getCURRENT_STATUS().toString()).append("),");
            sb.append("(LatchedStatus =").append(getLATCHED_STATUS().toString()).append("),");
            sb.append("(CurrDateTime =").append(getCURR_DATE_TIME()).append("),");
            sb.append("(DSTDateTime =").append(getDST_DATE_TIME()).append("),");
            sb.append("(DSTActive =").append(getDST_ACTIVE()).append("),");
            sb.append("(CTRatioMul =").append(getCT_RATIO_MUL()).append("),");
            sb.append("(PTRatioMul =").append(getPT_RATIO_MUL()).append("),");
            sb.append("(CTRatioDiv =").append(getCT_RATIO_DIV()).append("),");
            sb.append("(PTRatioDIV =").append(getPT_RATIO_DIV()).append("),");
            sb.append("(MeasureMethod =").append(getMEASURE_METHOD()).append("),");
            sb.append("(MeasureOption =").append(getMEASURE_OPTION()).append("),");
            sb.append("(NumOfPwrUp =").append(getNUM_OF_PWR_UP()).append("),");
            sb.append("(LastDTOutage =").append(getLAST_DT_OUTAGE()).append("),");
            sb.append("(LastDTRecovery=").append(getLAST_DT_RECOVERY()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.error("Mk6N_MTR TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }

	/**
	 * @return rawData
	 */
	public byte[] getRawData() {
		return rawData;
	}

	/**
	 * @return MODEL_ID_NO
	 */
	public String getMODEL_ID_NO() {
        try{
            MODEL_ID_NO
                = new String(DataFormat.select(rawData,OFF_MODEL_ID_NO,LEN_MODEL_ID_NO)).trim();

            if(MODEL_ID_NO == null || MODEL_ID_NO.equals("")){
            	MODEL_ID_NO = "Unknown";
            }
        }catch(Exception e){
            log.error("invalid model id no->"+e.getMessage());
        }
        return MODEL_ID_NO;
	}

	/**
	 * @return METER_ID
	 */
	public String getMETER_ID() {
		try{
			METER_ID
                = new String(DataFormat.select(rawData,OFF_METER_ID,LEN_METER_ID)).trim();

            if(METER_ID == null || METER_ID.equals("")){
            	METER_ID = "Unknown";
            }
        }catch(Exception e){
            log.error("invalid meter id->"+e.getMessage());
        }
		return METER_ID;
	}

	/**
	 * @return EZIO_STATUS
	 */
	public int getEZIO_STATUS() {
		EZIO_STATUS = rawData[OFF_EZIO_STATUS];
		return EZIO_STATUS;
	}

	/**
	 * @return STRING_EZIO_STATUS
	 */
	public String getSTRING_EZIO_STATUS(){
		switch(getEZIO_STATUS()){
			case 0:
				STRING_EZIO_STATUS="System is inactive - no devices defined";
				break;
			case 1:
				STRING_EZIO_STATUS="System is starting";
				break;
			case 2:
				STRING_EZIO_STATUS="System is active and running OK";
				break;
			case 3:
				STRING_EZIO_STATUS="System has problems - check setup and wiring";
				break;
			default:
				STRING_EZIO_STATUS="Unknown";
				break;
		}
		return STRING_EZIO_STATUS;
	}

	/**
	 * @return CURRENT_STATUS
	 */
	public StatusFlag getCURRENT_STATUS() {
		try {
			CURRENT_STATUS=new StatusFlag(new String(rawData,OFF_CURRENT_STATUS,LEN_CURRENT_STATUS));
		} catch (Exception e) {
			log.error("Error get current status->"+e.getMessage());
		}
		return CURRENT_STATUS;
	}

	/**
	 * @return LATCHED_STATUS
	 */
	public StatusFlag getLATCHED_STATUS() {
		try {
			LATCHED_STATUS=new StatusFlag(new String(rawData,OFF_LATCHED_STATUS,LEN_LATCHED_STATUS));
		} catch (Exception e) {
			log.error("Error get latched status->"+e.getMessage());
		}
		return LATCHED_STATUS;
	}

	/**
	 * @return CURR_DATE_TIME
	 */
	public String getCURR_DATE_TIME() {
		try {
			CURR_DATE_TIME =(new DateTimeFormat(DataFormat.select(rawData,OFF_CURR_DATE_TIME,LEN_CURR_DATE_TIME))).getDateTime();
		} catch (Exception e) {
			log.error("Error get current date time->"+e.getMessage());
		}
		return CURR_DATE_TIME;
	}

	/**
	 * @return DST_DATE_TIME
	 */
	public String getDST_DATE_TIME() {
		try {
			DST_DATE_TIME = (new DateTimeFormat(DataFormat.select(rawData,OFF_DST_DATE_TIME,LEN_DST_DATE_TIME))).getDateTime();
		} catch (Exception e) {
			log.error("Error get dst date time->"+e.getMessage());
		}
		return DST_DATE_TIME;
	}

	/**
	 * @return DST_ACTIVE
	 */
	public Boolean getDST_ACTIVE() {
		if(rawData[OFF_DST_ACTIVE]==1){
			DST_ACTIVE=true;
		}else{
			DST_ACTIVE=false;
		}
		return DST_ACTIVE;
	}

	/**
	 * @return CT_RATIO_MUL
	 */
	public Float getCT_RATIO_MUL() {
		try {
			CT_RATIO_MUL=DataFormat.bytesToFloat(DataFormat.select(rawData, OFF_CT_RATIO_MUL, LEN_CT_RATIO_MUL));
		} catch (Exception e) {
			log.error("Error get Current transformer ratios multiplier->"+e.getMessage());
		}
		return CT_RATIO_MUL;
	}

	/**
	 * @return PT_RATIO_MUL
	 */
	public Float getPT_RATIO_MUL() {
		try {
			PT_RATIO_MUL=DataFormat.bytesToFloat(DataFormat.select(rawData, OFF_PT_RATIO_MUL, LEN_PT_RATIO_MUL));
		} catch (Exception e) {
			log.error("Error get Voltage transformer ratios multiplier->"+e.getMessage());
		}
		return PT_RATIO_MUL;
	}

	/**
	 * @return CT_RATIO_DIV
	 */
	public Float getCT_RATIO_DIV() {
		try {
			CT_RATIO_DIV=DataFormat.bytesToFloat(DataFormat.select(rawData, OFF_CT_RATIO_DIV, LEN_CT_RATIO_DIV));
		} catch (Exception e) {
			log.error("Error get Current transformer ratios divisor->"+e.getMessage());
		}
		return CT_RATIO_DIV;
	}

	/**
	 * @return PT_RATIO_DIV
	 */
	public Float getPT_RATIO_DIV() {
		try {
			PT_RATIO_DIV=DataFormat.bytesToFloat(DataFormat.select(rawData, OFF_PT_RATIO_DIV, LEN_PT_RATIO_DIV));
		} catch (Exception e) {
			log.error("Error get Voltage transformer ratios divisor->"+e.getMessage());
		}
		return PT_RATIO_DIV;
	}

	/**
	 * @return MEASURE_METHOD
	 */
	public Integer getMEASURE_METHOD() {
		MEASURE_METHOD=rawData[OFF_MEASURE_METHOD];
		return MEASURE_METHOD;
	}

	/**
	 * @return MEASURE_OPTION
	 */
	public Integer getMEASURE_OPTION() {
		try {
			MEASURE_OPTION=DataFormat.hex2unsigned16(DataFormat.select(rawData,OFF_MEASURE_OPTION,LEN_MEASURE_OPTION));
		} catch (Exception e) {
			log.error("Error get Measure Option->"+e.getMessage());
		}
		return MEASURE_OPTION;
	}

	/**
	 * @return NUM_OF_PWR_UP
	 */
	public Integer getNUM_OF_PWR_UP() {
		try {
			NUM_OF_PWR_UP=(int)DataFormat.hex2unsigned32(DataFormat.select(rawData,OFF_NUM_OF_PWR_UP,LEN_NUM_OF_PWR_UP));
		} catch (Exception e) {
			log.error("Error get Num of pwr up->"+e.getMessage());
		}
		return NUM_OF_PWR_UP;
	}

	/**
	 * @return LAST_DT_OUTAGE
	 */
	public String getLAST_DT_OUTAGE() {
		try {
			LAST_DT_OUTAGE = (new DateTimeFormat(DataFormat.select(rawData,OFF_LAST_DT_OUTAGE,LEN_LAST_DT_OUTAGE))).getDateTime();
		} catch (Exception e) {
			log.error("Error get time of the last power loss->"+e.getMessage());
		}
		return LAST_DT_OUTAGE;
	}

	/**
	 * @return LAST_DT_RECOVERY
	 */
	public String getLAST_DT_RECOVERY() {
		try {
			LAST_DT_RECOVERY = (new DateTimeFormat(DataFormat.select(rawData,OFF_LAST_DT_RECOVERY,LEN_LAST_DT_RECOVERY))).getDateTime();
		} catch (Exception e) {
			log.error("Error get time of the last power up->"+e.getMessage());
		}
		return LAST_DT_RECOVERY;
	}
}
