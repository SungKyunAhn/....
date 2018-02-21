/*
 * @(#)Mk10_MTI.java      2011/08/12 *
 *
 * Meter Information
 * Copyright (c) 2011-2012 NuriTelecom, Inc.
 * All rights reserved. *
 * This software is the confidential and proprietary information of
 * Nuritelcom, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Nuritelecom.
 */

package com.aimir.fep.meter.parser.Mk10Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.parser.Mk6NTable.DateTimeFormat;
import com.aimir.fep.meter.parser.Mk6NTable.StatusFlag;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;

public class Mk10_MTI implements java.io.Serializable{

	private static final long serialVersionUID = -3234942991976328752L;
	public byte[] TMP_MODEL_ID_NO = new byte[10];
    public byte[] TMP_METER_ID = new byte[12];
    public byte[] TMP_SW_VER   = new byte[6];
    public byte[] TMP_SW_REV_NUMBER = new byte[4];
    public byte[] TMP_CURRENT_STATUS = new byte[16];
    public byte[] TMP_LATCHED_STATUS = new byte[16];
    public byte[] TMP_CURR_DATE_TIME = new byte[6];
    public byte[] TMP_DST_DATE_TIME = new byte[6];
    public byte[] TMP_CT_RATIO_MUL = new byte[4];
    public byte[] TMP_PT_RATIO_MUL = new byte[4];
    public byte[] TMP_CT_RATIO_DIV = new byte[4];
    public byte[] TMP_PT_RATIO_DIV = new byte[4];
    public byte[] TMP_MEASURE_METHOD = new byte[1];
    public byte[] TMP_MEASURE_OPTION = new byte[2];
    public byte[] TMP_NUM_OF_PWR_UP = new byte[4];
    public byte[] TMP_LAST_DT_OUTAGE = new byte[6];
    public byte[] TMP_LAST_DT_RECOVERY = new byte[6];

	private byte[] rawData = null;
	protected String MODEL_ID_NO;
	protected String METER_ID;
    protected String SW_VER   = "";
    protected String SW_REV_NUMBER = "";
	protected StatusFlag CURRENT_STATUS;
	protected StatusFlag LATCHED_STATUS;
	protected String CURR_DATE_TIME;
	protected String DST_DATE_TIME;
	protected Float CT_RATIO_MUL;
	protected Float PT_RATIO_MUL;
	protected Float CT_RATIO_DIV;
	protected Float PT_RATIO_DIV;
	protected int MEASURE_METHOD;
	protected int MEASURE_OPTION;
	protected int NUM_OF_PWR_UP;
	protected String LAST_DT_OUTAGE;
	protected String LAST_DT_RECOVERY;

    private static Log log = LogFactory.getLog(Mk10_MTI.class);
	/**
	 * Constructor .<p>
	 *
	 * @param data - read data (header,crch,crcl)
	 */
	public Mk10_MTI(byte[] rawData) {
        this.rawData = rawData;
        parse();
	}
	
	public void parse() {
		int pos = 0;
		
		System.arraycopy(rawData, pos, TMP_MODEL_ID_NO, 0, TMP_MODEL_ID_NO.length);
		pos += TMP_MODEL_ID_NO.length;		
		System.arraycopy(rawData, pos, TMP_METER_ID, 0, TMP_METER_ID.length);
		pos += TMP_METER_ID.length;		
		System.arraycopy(rawData, pos, TMP_SW_VER, 0, TMP_SW_VER.length);
		pos += TMP_SW_VER.length;		
		System.arraycopy(rawData, pos, TMP_SW_REV_NUMBER, 0, TMP_SW_REV_NUMBER.length);
		pos += TMP_SW_REV_NUMBER.length;		
		System.arraycopy(rawData, pos, TMP_CURRENT_STATUS, 0, TMP_CURRENT_STATUS.length);
		pos += TMP_CURRENT_STATUS.length;		
		System.arraycopy(rawData, pos, TMP_LATCHED_STATUS, 0, TMP_LATCHED_STATUS.length);
		pos += TMP_LATCHED_STATUS.length;		
		System.arraycopy(rawData, pos, TMP_CURR_DATE_TIME, 0, TMP_CURR_DATE_TIME.length);
		pos += TMP_CURR_DATE_TIME.length;		
		System.arraycopy(rawData, pos, TMP_DST_DATE_TIME, 0, TMP_DST_DATE_TIME.length);
		pos += TMP_DST_DATE_TIME.length;		
		System.arraycopy(rawData, pos, TMP_CT_RATIO_MUL, 0, TMP_CT_RATIO_MUL.length);
		pos += TMP_CT_RATIO_MUL.length;		
		System.arraycopy(rawData, pos, TMP_PT_RATIO_MUL, 0, TMP_PT_RATIO_MUL.length);
		pos += TMP_PT_RATIO_MUL.length;		
		System.arraycopy(rawData, pos, TMP_CT_RATIO_DIV, 0, TMP_CT_RATIO_DIV.length);
		pos += TMP_CT_RATIO_DIV.length;		
		System.arraycopy(rawData, pos, TMP_PT_RATIO_DIV, 0, TMP_PT_RATIO_DIV.length);
		pos += TMP_PT_RATIO_DIV.length;		
		System.arraycopy(rawData, pos, TMP_MEASURE_METHOD, 0, TMP_MEASURE_METHOD.length);
		pos += TMP_MEASURE_METHOD.length;		
		System.arraycopy(rawData, pos, TMP_MEASURE_OPTION, 0, TMP_MEASURE_OPTION.length);
		pos += TMP_MEASURE_OPTION.length;		
		System.arraycopy(rawData, pos, TMP_NUM_OF_PWR_UP, 0, TMP_NUM_OF_PWR_UP.length);
		pos += TMP_NUM_OF_PWR_UP.length;		
		System.arraycopy(rawData, pos, TMP_LAST_DT_OUTAGE, 0, TMP_LAST_DT_OUTAGE.length);
		pos += TMP_LAST_DT_OUTAGE.length;		
		System.arraycopy(rawData, pos, TMP_LAST_DT_RECOVERY, 0, TMP_LAST_DT_RECOVERY.length);
		pos += TMP_LAST_DT_RECOVERY.length;		
	}

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("Mk10_MTR DATA[");
            sb.append("(ModelIDNo =").append(getMODEL_ID_NO()).append("),");
            sb.append("(MeterID =").append(getMETER_ID()).append("),");            
            sb.append("(SW Ver =").append(getSW_VER()).append("),");
            sb.append("(SW Revision =").append(getSW_REV_NUMBER()).append("),");            
            sb.append("(CurrentStatus =").append(getCURRENT_STATUS().toString()).append("),");
            sb.append("(LatchedStatus =").append(getLATCHED_STATUS().toString()).append("),");
            sb.append("(CurrDateTime =").append(getCURR_DATE_TIME()).append("),");
            sb.append("(DSTDateTime =").append(getDST_DATE_TIME()).append("),");
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
            log.error("Mk10_MTR TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }

	/**
	 * @return rawData
	 */
	public byte[] getrawData() {
		return rawData;
	}

	/**
	 * @return MODEL_ID_NO
	 */
	public String getMODEL_ID_NO() {
        try{
            MODEL_ID_NO
                = new String(TMP_MODEL_ID_NO).trim();

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
                = new String(TMP_METER_ID).trim();

            if(METER_ID == null || METER_ID.equals("")){
            	METER_ID = "Unknown";
            }
        }catch(Exception e){
            log.error("invalid meter id->"+e.getMessage());
        }
		return METER_ID;
	}
	
	/**
	 * @return SW_VER
	 */
	public String getSW_VER() {
		try{
			SW_VER
                = new String(TMP_SW_VER).trim();

            if(SW_VER == null || SW_VER.equals("")){
            	SW_VER = "-";
            }
        }catch(Exception e){
            log.error("invalid SW_VER->"+e.getMessage());
        }
		return SW_VER;
	}

	/**
	 * @return SW_VER REV
	 */
	public String getSW_REV_NUMBER() {
		try{
			SW_REV_NUMBER = DataUtil.getLongToBytes(TMP_SW_REV_NUMBER)+"";

            if(SW_REV_NUMBER == null || SW_REV_NUMBER.equals("")){
            	SW_REV_NUMBER = "-";
            }
        }catch(Exception e){
            log.error("invalid SW_REV_NUMBER->"+e.getMessage());
        }
		return SW_REV_NUMBER;
	}
	
	/**
	 * @return CURRENT_STATUS
	 */
	public StatusFlag getCURRENT_STATUS() {
		try {
			CURRENT_STATUS=new StatusFlag(new String(TMP_CURRENT_STATUS));
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
			LATCHED_STATUS=new StatusFlag(new String(TMP_LATCHED_STATUS));
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
			CURR_DATE_TIME =(new DateTimeFormat(TMP_CURR_DATE_TIME)).getDateTime();
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
			DST_DATE_TIME = (new DateTimeFormat(TMP_DST_DATE_TIME)).getDateTime();
		} catch (Exception e) {
			log.error("Error get dst date time->"+e.getMessage());
		}
		return DST_DATE_TIME;
	}


	/**
	 * @return CT_RATIO_MUL
	 */
	public Float getCT_RATIO_MUL() {
		try {
			CT_RATIO_MUL=DataFormat.bytesToFloat(TMP_CT_RATIO_MUL);
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
			PT_RATIO_MUL=DataFormat.bytesToFloat(TMP_PT_RATIO_MUL);
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
			CT_RATIO_DIV=DataFormat.bytesToFloat(TMP_CT_RATIO_DIV);
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
			PT_RATIO_DIV=DataFormat.bytesToFloat(TMP_PT_RATIO_DIV);
		} catch (Exception e) {
			log.error("Error get Voltage transformer ratios divisor->"+e.getMessage());
		}
		return PT_RATIO_DIV;
	}

	/**
	 * @return MEASURE_METHOD
	 */
	public Integer getMEASURE_METHOD() {
		try {
			MEASURE_METHOD=DataFormat.hex2dec(TMP_MEASURE_METHOD);
		} catch (Exception e) {
			log.error("Error get MEASURE_METHOD->"+e.getMessage());
		}
		return MEASURE_METHOD;
	}

	/**
	 * @return MEASURE_OPTION
	 */
	public Integer getMEASURE_OPTION() {
		try {
			MEASURE_OPTION=DataFormat.hex2unsigned16(TMP_MEASURE_OPTION);
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
			NUM_OF_PWR_UP=(int)DataFormat.hex2unsigned32(TMP_NUM_OF_PWR_UP);
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
			LAST_DT_OUTAGE = (new DateTimeFormat(TMP_LAST_DT_OUTAGE)).getDateTime();
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
			LAST_DT_RECOVERY = (new DateTimeFormat(TMP_LAST_DT_RECOVERY)).getDateTime();
		} catch (Exception e) {
			log.error("Error get time of the last power up->"+e.getMessage());
		}
		return LAST_DT_RECOVERY;
	}
}
