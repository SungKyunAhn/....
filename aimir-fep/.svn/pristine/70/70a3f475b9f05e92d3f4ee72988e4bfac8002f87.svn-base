/* 
 * @(#)SCE8711_MTR.java       1.0 09/05/12 *
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
 
package com.aimir.fep.meter.parser.actarisSCE8711Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * @author Kang Soyi ksoyi@nuritelecom.com
 */
public class SCE8711_MTR {    
    
    public static final int OFS_METER_FATAL_ERROR = 0;
    public static final int OFS_METER_NON_FATAL_ERROR = 1;
    public static final int OFS_CT_NUMERATOR = 6;
    public static final int OFS_CT_DENOMINATOR = 8;
    public static final int OFS_VT_NUMERATOR = 9;
    public static final int OFS_VT_DENOMINATOR = 13;
    public static final int OFS_LP_PERIOD = 15;
    public static final int OFS_DEMAND_FACTOR = 16;
    public static final int OFS_ENERGY_FACTOR = 18;
    public static final int OFS_DATETIME = 20;
    
    public static final int LEN_METER_FATAL_ERROR = 1;
    public static final int LEN_METER_NON_FATAL_ERROR = 5;
    public static final int LEN_CT_NUMERATOR = 2;
    public static final int LEN_CT_DENOMINATOR = 1;
    public static final int LEN_VT_NUMERATOR = 4;
    public static final int LEN_VT_DENOMINATOR = 2;
    public static final int LEN_LP_PERIOD = 1;
    public static final int LEN_DEMAND_FACTOR = 2;
    public static final int LEN_ENERGY_FACTOR = 2;
    public static final int LEN_DATETIME = 12;
    
	private byte[] rawData = null;

    private Log log = LogFactory.getLog(SCE8711_MTR.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public SCE8711_MTR(byte[] rawData) {
        this.rawData = rawData;
	}
	
	public Double getCT1_RATIO() throws Exception{
		return new Double((double)DataFormat.hex2long(DataFormat.select(rawData, OFS_CT_NUMERATOR, LEN_CT_NUMERATOR))); 
	}
	
	public Double getVT1_RATIO() throws Exception{ 
		return new Double((double)DataFormat.hex2unsigned32(DataFormat.select(rawData, OFS_VT_NUMERATOR, LEN_VT_NUMERATOR))); 
	}
	
	public Double getCT2_RATIO() throws Exception{
		return new Double((double)DataFormat.hex2long(DataFormat.select(rawData, OFS_CT_DENOMINATOR, LEN_CT_DENOMINATOR))); 
	}
	
	public Double getVT2_RATIO() throws Exception{ 
		return new Double((double)DataFormat.hex2long(DataFormat.select(rawData, OFS_VT_DENOMINATOR, LEN_VT_DENOMINATOR))); 
	}
	
	public int getLP_PERIOD() throws Exception{ 
		return (int)DataFormat.hex2unsigned8(rawData[OFS_LP_PERIOD]);
	}
	
	public int getDEMAND_FACTOR() throws Exception{
		return (int)(DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_DEMAND_FACTOR, LEN_DEMAND_FACTOR))); 
	}
	
	public int getENERGY_FACTOR() throws Exception{
		return (int)(DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_ENERGY_FACTOR, LEN_ENERGY_FACTOR))); 
	}
	
	/*
    public MeterErrorFlag getMeterErrorFlag()  throws Exception {
       return new MeterErrorFlag(DataFormat.select(rawData, OFS_METER_STATUS, LEN_METER_STATUS));
    }
	*/
    public String getMeterDateTime(){
        String date="";
        try{
           date = new DLMSDateTime(DataFormat.select(rawData, OFS_DATETIME, LEN_DATETIME)).getDateTime();
        }catch(Exception e){
            log.warn("DateTime->"+e.getMessage());
        }
        return date;
    }
    
    public String getMETER_ID(){
        return "";
    }
    
    private String getDateTime(byte[] date) throws Exception{
    	int blen = date.length;
		if(blen != 9)
			throw new Exception("YYYYMMDDHHMMSS LEN ERROR : "+blen);
		
		int idx = 0;
		
		int year = DataFormat.hex2unsigned16(DataFormat.select(date, 0, 2));
        idx =idx+2;
		int mm = DataFormat.hex2unsigned8(date[idx++]);
		byte dst = date[idx++];
		int dd = DataFormat.hex2unsigned8(date[idx++]);
		int dayOfWeek = DataFormat.hex2unsigned8(date[idx++]);
		int hh = DataFormat.hex2unsigned8(date[idx++]);
		int MM = DataFormat.hex2unsigned8(date[idx++]);
		int ss = DataFormat.hex2unsigned8(date[idx++]);

		StringBuffer ret = new StringBuffer();
				
		ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
		ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(ss),2));
		
		return ret.toString();
    }
    
    /*
    public BatteryLogData[] getBatteryLog() throws Exception {
    	ArrayList<BatteryLogData> battLogList = new ArrayList<BatteryLogData>();
        BatteryLogData[] batterydata = null;
        
		String meterDatetime = getMeterDateTime();
		
		if(getMeterErrorFlag().getINSUFFICIENT_BATTERY_VOLT()){
			BatteryLogData batt = new BatteryLogData();
			batt.setDate(meterDatetime.substring(0,8));
			batt.setTime(meterDatetime.substring(8));
			batt.setState(1);//0: Normal 1: voltage error, 2: life-time error
			battLogList.add(batt);
		}else{
			BatteryLogData batt = new BatteryLogData();
			batt.setDate(meterDatetime.substring(0,8));
			batt.setTime(meterDatetime.substring(8));
			batt.setState(0);//0: Normal 1: voltage error, 2: life-time error
			battLogList.add(batt);
		}
				
		if(battLogList!=null)
        	log.debug("battLogList size() :"+ battLogList.size());
        else
        	log.debug("battLogList is null");
        
        if(battLogList != null && battLogList.size() > 0){
            Object[] obj = battLogList.toArray();            
            batterydata = new BatteryLogData[obj.length];
            
            for(int i = 0; i < obj.length; i++){
            	batterydata[i] = (BatteryLogData)obj[i];
            }
        }
        return batterydata;        
    }
    */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("SCE8711_MTR DATA[");        
            sb.append("(CT1_RATIO=").append(""+getCT1_RATIO()).append("),");
            sb.append("(VT1_RATIO=").append(""+getVT1_RATIO()).append("),");
            sb.append("(CT2_RATIO=").append(""+getCT2_RATIO()).append("),");
            sb.append("(VT2_RATIO=").append(""+getVT2_RATIO()).append("),");
            sb.append("(LP_PERIOD=").append(""+getLP_PERIOD()).append("),");
      //      sb.append("(MeterErrorFlag=").append(getMeterErrorFlag().toString()).append("),");
            sb.append("]\n");
        }catch(Exception e){
            log.error("SCE8711_MTR TO STRING ERR=>"+e.getMessage(),e);
        }
        return sb.toString();
    }
}
