/* 
 * @(#)SCE8711_EV.java       1.0 09/05/12 *
 * 
 * Event Log.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.actarisSCE8711Table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * @author Kang Soyi ksoyi@nuritelecom.com
 */
public class SCE8711_EV {
    
    public static final int OFS_NBR_EVT_LOG_BLOCK = 0; 
    public static final int OFS_EVT_LOG_BLOCK = 1;
    
    public static final int LEN_NBR_EVT_LOG_BLOCK = 1; 
    public static final int LEN_DATE_EVENT_DATA = 5;
    public static final int LEN_TIME_EVENT_DATA = 7;
    
    public static final int LEN_NBR_EVT_LOG = 1;
    public static final int LEN_EVT_DATETIME = 12;
    public static final int LEN_EVT_KIND = 1;
    
    private EventLogData[] eventdata;
	private byte[] rawData = null;
    
    private Log log = LogFactory.getLog(SCE8711_EV.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public SCE8711_EV(byte[] rawData) {
		
		this.rawData = rawData;
		parseEvent();
	}
    
    public EventLogData[] getEvent() {
        return this.eventdata;
    }
    
    public int getNBR_NBR_EVT_LOG_BLOCK(){
    	return DataFormat.hex2unsigned8(rawData[OFS_NBR_EVT_LOG_BLOCK]);
    }
    
    private void parseEvent(){
    	
        int dateCnt = getNBR_NBR_EVT_LOG_BLOCK();
        int ofs = OFS_EVT_LOG_BLOCK;
        
        ArrayList<EventLogData> evList = new ArrayList<EventLogData>();
        
        try{
        	/*
        	Map meterEventMap = null;
	        if(dateCnt>0)
	        	meterEventMap = getMeterEventClassMap("Actaris", "05");//vendor, model
	        */
	        for(int i=0; i<dateCnt; i++){
	        	int eventCnt = DataFormat.hex2unsigned16(DataFormat.select(rawData, ofs,2));
	        	ofs+=2;
	        	byte[] dateTime = new byte[LEN_EVT_DATETIME]; 
	        	System.arraycopy(DataFormat.select(rawData, ofs, LEN_DATE_EVENT_DATA), 0, dateTime, 0, LEN_DATE_EVENT_DATA);
	        	ofs += LEN_DATE_EVENT_DATA;
	        	
	        	for(int j=0; j<eventCnt; j++){	        		
	        		System.arraycopy(rawData, ofs, dateTime, LEN_DATE_EVENT_DATA, LEN_TIME_EVENT_DATA);
	        		ofs += LEN_TIME_EVENT_DATA;
	        		
	        		String datetime = new DLMSDateTime(dateTime).getDateTime();
	        		int eventcode = DataFormat.hex2unsigned8(rawData[ofs++]);
	        		int eventparam = DataFormat.hex2unsigned8(rawData[ofs++]);
	        		
	        		//if(meterEventMap.containsKey(""+eventcode)){
	        		 	//EventLogData tmpEv =(EventLogData)meterEventMap.get(""+eventcode);
		                EventLogData ev = new EventLogData();
		                ev.setDate(datetime.substring(0,8));
		                ev.setTime(datetime.substring(8,14));
		                //ev.setKind(tmpEv.getKind());
		                ev.setFlag(eventcode);
		                //ev.setMsg(tmpEv.getMsg());
		                ev.setAppend(getParameterMessage(eventparam)); /////////// parameter message
		                evList.add(ev);
		             /*
		        	 }else{
		        		 log.warn("it doesn't be registered in the event code table : "+eventcode);
		        	 }
		        	 */
	        	}
	        }
	        
	        if(evList!=null)
	        	log.debug("evList size() :"+ evList.size());
	        else
	        	log.debug("evList is null");
	        
	        if(evList != null && evList.size() > 0){
	            Object[] obj = evList.toArray();            
	            eventdata = new EventLogData[obj.length];
	            
	            for(int i = 0; i < obj.length; i++){
	            	eventdata[i] = (EventLogData)obj[i];
	            }
	        }
        
        }catch(Exception e){
        	log.error("EVENT LOG PARSING ERROR :"+ e,e);
        }
    }

    private String getDateTime(byte[] date)  throws Exception{
    	int blen = date.length;
		if(blen != 4)
			throw new Exception("YYYYMMDDHHMMSS LEN ERROR : "+blen);
		
		int MASK_YEAR 	= 0x00000FFF;
		int MASK_MONTH 	= 0x0000F000;
		int MASK_DAY 	= 0x001F0000;
		int MASK_HOUR 	= 0x03e00000;
		int MASK_MIN 	= 0xFC000000;

		int yy = (int)DataFormat.hex2unsigned32(date)& MASK_YEAR;
		int mm = ((int)DataFormat.hex2unsigned32(date)& MASK_MONTH) >> 12;
		int dd = ((int)DataFormat.hex2unsigned32(date)& MASK_DAY )  >> 16;
		int hh = ((int)DataFormat.hex2unsigned32(date)& MASK_HOUR)  >> 21;
		int MM = ((int)DataFormat.hex2unsigned32(date)& MASK_MIN )  >> 26;
		int ss = 0;

		StringBuffer ret = new StringBuffer();
				
		ret.append(Util.frontAppendNStr('0',Integer.toString(yy),4));
		ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(ss),2));
		
		return ret.toString();
    }
	
    /**
     * 기존에 metereventclass에 미터 이벤트를 넣어 놓고 꺼내서 사용하는 로직은 사용하지 않음
     * @param meterId
     * @return Map Key:descr, Value:id     
    public Map getMeterEventClassMap(String vendor, String model) {

    	Map eventClassMap =new HashMap();

		StringBuffer sql = new StringBuffer();

		sql.append(" SELECT ID, VALUE, KIND, DESCR \n");
		sql.append(" FROM METEREVENTCLASS").append(" \n");
		sql.append(" WHERE METERTYPE= 1 \n");
		sql.append(" 	AND VENDOR = ? \n");
		sql.append("    AND MODEL=? \n");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection dbConn = null;

		// log.debug("getCountOfEventLog SQL["+sql.toString()+"]");

		try {

			dbConn = JDBCUtil.getConnection();
			pstmt = dbConn.prepareStatement(JDBCUtil.toDB(sql.toString()));
			pstmt.setString(1, vendor);
			pstmt.setString(2, model);

			rs = pstmt.executeQuery();
			while (rs.next()){
                EventLogData ev = new EventLogData();
                ev.setKind(rs.getString("kind"));
                ev.setMsg(rs.getString("descr"));
				eventClassMap.put(""+rs.getString("value").trim(), ev);
			}
		} catch (Exception ex) {
			log.error("getMeterEventClassMap :: query[" + sql.toString() + "]");
			log.error(ex, ex);
		} finally {
			JDBCUtil.close(rs, pstmt, dbConn);
		}

		return eventClassMap;
	}
	*/
    
    public static String getParameterMessage(int param) {
        if (EVENTPARAMETER.INTERNAL_RAM_ERROR.getCode() == param)
            return EVENTPARAMETER.INTERNAL_RAM_ERROR.getName();
        else if (EVENTPARAMETER.EXTERNAL_RAM_ERROR.getCode() == param)
            return EVENTPARAMETER.EXTERNAL_RAM_ERROR.getName();
        
        else if (EVENTPARAMETER.INTERNAL_PROGRAM_MEMORY_ERROR.getCode() == param)
            return EVENTPARAMETER.INTERNAL_PROGRAM_MEMORY_ERROR.getName();
        else if (EVENTPARAMETER.EXTERNAL_PROGRAM_MEMORY_ERROR.getCode() == param)
            return EVENTPARAMETER.EXTERNAL_PROGRAM_MEMORY_ERROR.getName();
        
        else if (EVENTPARAMETER.NON_VOLATILE_MEMORY_FATAL_ERROR.getCode() == param)
            return EVENTPARAMETER.NON_VOLATILE_MEMORY_FATAL_ERROR.getName();
        else if (EVENTPARAMETER.SEVRAL_EXTERNAL_CLOCK_INCOHERENCES.getCode() == param)
            return EVENTPARAMETER.SEVRAL_EXTERNAL_CLOCK_INCOHERENCES.getName();
        
        else if (EVENTPARAMETER.WATCHDOG_ACTIVITY.getCode() == param)
            return EVENTPARAMETER.WATCHDOG_ACTIVITY.getName();
        
        else if (EVENTPARAMETER.EXTERNAL_CLOCK_INCOHERENCE.getCode() == param)
            return EVENTPARAMETER.EXTERNAL_CLOCK_INCOHERENCE.getName();
        else if (EVENTPARAMETER.CONFIGURATION_INCOHERENCE.getCode() == param)
            return EVENTPARAMETER.CONFIGURATION_INCOHERENCE.getName();
        
        else if (EVENTPARAMETER.NON_VOLATILE_MEMORY_NON_FATAL_ERROR.getCode() == param)
            return EVENTPARAMETER.NON_VOLATILE_MEMORY_NON_FATAL_ERROR.getName();
        else if (EVENTPARAMETER.PROGRAMMING_INCOHERENCE.getCode() == param)
            return EVENTPARAMETER.PROGRAMMING_INCOHERENCE.getName();
        else if (EVENTPARAMETER.COVER_OPENING.getCode() == param)
            return EVENTPARAMETER.COVER_OPENING.getName();
        
        else if (EVENTPARAMETER.NO_INTERNAL_CONSUMPTION.getCode() == param)
            return EVENTPARAMETER.NO_INTERNAL_CONSUMPTION.getName();
        else if (EVENTPARAMETER.NO_EXTERNAL_CONSUMPTION.getCode() == param)
            return EVENTPARAMETER.NO_EXTERNAL_CONSUMPTION.getName();
        
        else if (EVENTPARAMETER.ZERO_SEQUENCE_U.getCode() == param)
            return EVENTPARAMETER.ZERO_SEQUENCE_U.getName();
        else if (EVENTPARAMETER.ZERO_SEQUENCE_I.getCode() == param)
            return EVENTPARAMETER.ZERO_SEQUENCE_I.getName();
        
        else if (EVENTPARAMETER.CLOCK_LOSS.getCode() == param)
            return EVENTPARAMETER.CLOCK_LOSS.getName();
        else if (EVENTPARAMETER.EXTERNAL_ALARM.getCode() == param)
            return EVENTPARAMETER.EXTERNAL_ALARM.getName();
        
        else if (EVENTPARAMETER.CURRENT_REVERSAL_PHASE_1.getCode() == param)
            return EVENTPARAMETER.CURRENT_REVERSAL_PHASE_1.getName();
        else if (EVENTPARAMETER.CURRENT_REVERSAL_PHASE_2.getCode() == param)
            return EVENTPARAMETER.CURRENT_REVERSAL_PHASE_2.getName();
        else if (EVENTPARAMETER.CURRENT_REVERSAL_PHASE_3.getCode() == param)
            return EVENTPARAMETER.CURRENT_REVERSAL_PHASE_3.getName();
        
        else if (EVENTPARAMETER.TEMPERATURE_ALARM.getCode() == param)
            return EVENTPARAMETER.TEMPERATURE_ALARM.getName();
        
        else if (EVENTPARAMETER.VOLTAGE_CUT_PHASE1.getCode() == param)
            return EVENTPARAMETER.VOLTAGE_CUT_PHASE1.getName();
        else if (EVENTPARAMETER.VOLTAGE_CUT_PHASE2.getCode() == param)
            return EVENTPARAMETER.VOLTAGE_CUT_PHASE2.getName();
        else if (EVENTPARAMETER.VOLTAGE_CUT_PHASE3.getCode() == param)
            return EVENTPARAMETER.VOLTAGE_CUT_PHASE3.getName();
        
        else if (EVENTPARAMETER.VOLTAGE_SAG_PHASE1.getCode() == param)
            return EVENTPARAMETER.VOLTAGE_SAG_PHASE1.getName();
        else if (EVENTPARAMETER.VOLTAGE_SAG_PHASE2.getCode() == param)
            return EVENTPARAMETER.VOLTAGE_SAG_PHASE2.getName();
        else if (EVENTPARAMETER.VOLTAGE_SAG_PHASE3.getCode() == param)
            return EVENTPARAMETER.VOLTAGE_SAG_PHASE3.getName();
        else if (EVENTPARAMETER.VOLTAGE_SAG_PHASE3.getCode() == param)
            return EVENTPARAMETER.VOLTAGE_SAG_PHASE3.getName();
        
        else if (EVENTPARAMETER.VOLTAGE_SWELL_PHASE1.getCode() == param)
            return EVENTPARAMETER.VOLTAGE_SWELL_PHASE1.getName();
        else if (EVENTPARAMETER.VOLTAGE_SWELL_PHASE2.getCode() == param)
            return EVENTPARAMETER.VOLTAGE_SWELL_PHASE2.getName();
        else if (EVENTPARAMETER.VOLTAGE_SWELL_PHASE3.getCode() == param)
            return EVENTPARAMETER.VOLTAGE_SWELL_PHASE3.getName();
        
        else if (EVENTPARAMETER.BATTERY.getCode() == param)
            return EVENTPARAMETER.BATTERY.getName();
        else if (EVENTPARAMETER.EXCESS_DEMAND.getCode() == param)
            return EVENTPARAMETER.EXCESS_DEMAND.getName();

        else
            return "";
    }
    
    public enum EVENTPARAMETER {
        INTERNAL_RAM_ERROR(0, "INTERNAL_RAM_ERROR"),
        EXTERNAL_RAM_ERROR(1, "EXTERNAL_RAM_ERROR"),
        INTERNAL_PROGRAM_MEMORY_ERROR(2, "INTERNAL_PROGRAM_MEMORY_ERROR"),
        EXTERNAL_PROGRAM_MEMORY_ERROR(3, "EXTERNAL_PROGRAM_MEMORY_ERROR"),
        NON_VOLATILE_MEMORY_FATAL_ERROR(4, "NON_VOLATILE_MEMORY_FATAL_ERROR"),
        SEVRAL_EXTERNAL_CLOCK_INCOHERENCES(5, "SEVRAL_EXTERNAL_CLOCK_INCOHERENCES"),
        WATCHDOG_ACTIVITY(6, "WATCHDOG_ACTIVITY"),
        EXTERNAL_CLOCK_INCOHERENCE(7, "EXTERNAL_CLOCK_INCOHERENCE"),
        CONFIGURATION_INCOHERENCE(8, "CONFIGURATION_INCOHERENCE"),
        NON_VOLATILE_MEMORY_NON_FATAL_ERROR(9, "NON_VOLATILE_MEMORY_NON_FATAL_ERROR"),
        PROGRAMMING_INCOHERENCE(11, "PROGRAMMING_INCOHERENCE"),
        COVER_OPENING(12, "COVER_OPENING"),
        NO_INTERNAL_CONSUMPTION(20, "NO_INTERNAL_CONSUMPTION"),
        NO_EXTERNAL_CONSUMPTION(21, "NO_EXTERNAL_CONSUMPTION"),
        ZERO_SEQUENCE_U(22, "ZERO_SEQUENCE_U"),
        ZERO_SEQUENCE_I(23, "ZERO_SEQUENCE_I"),
        CLOCK_LOSS(24, "CLOCK_LOSS"),
        EXTERNAL_ALARM(25, "EXTERNAL_ALARM"),
        CURRENT_REVERSAL_PHASE_1(26, "CURRENT_REVERSAL_PHASE_1"),
        CURRENT_REVERSAL_PHASE_2(27, "CURRENT_REVERSAL_PHASE_2"),
        CURRENT_REVERSAL_PHASE_3(28, "CURRENT_REVERSAL_PHASE_3"),
        TEMPERATURE_ALARM(29, "TEMPERATURE_ALARM"),
        VOLTAGE_CUT_PHASE1(30, "VOLTAGE_CUT_PHASE1"),
        VOLTAGE_CUT_PHASE2(31, "VOLTAGE_CUT_PHASE2"),
        VOLTAGE_CUT_PHASE3(32, "VOLTAGE_CUT_PHASE3"),
        VOLTAGE_SAG_PHASE1(33, "VOLTAGE_SAG_PHASE1"),
        VOLTAGE_SAG_PHASE2(34, "VOLTAGE_SAG_PHASE2"),
        VOLTAGE_SAG_PHASE3(35, "VOLTAGE_SAG_PHASE3"),
        VOLTAGE_SWELL_PHASE1(36, "VOLTAGE_SWELL_PHASE1"),
        VOLTAGE_SWELL_PHASE2(37, "VOLTAGE_SWELL_PHASE2"),
        VOLTAGE_SWELL_PHASE3(38, "VOLTAGE_SWELL_PHASE3"),
        BATTERY(39, "BATTERY"),
        EXCESS_DEMAND(40, "EXCESS_DEMAND");
        //        EXTERNAL_PROGRAM_MEMORY_ERROR(3, "EXTERNAL_PROGRAM_MEMORY_ERROR");

        private int code;
        private String name;

        EVENTPARAMETER(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode()
        {
            return code;
        }

        public void setCode(int code)
        {
            this.code = code;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }
    }   

}
