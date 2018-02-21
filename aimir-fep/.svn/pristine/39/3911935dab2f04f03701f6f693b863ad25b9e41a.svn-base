/*
 * @(#)Mk10_EV.java       1.0 2011/08/12 *
 *
 * Load Profile.
 * Copyright (c) 2011-2012 NuriTelecom, Inc.
 * All rights reserved. *
 * This software is the confidential and proprietary information of
 * Nuritelcom, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Nuritelecom.
 */

package com.aimir.fep.meter.parser.Mk10Table;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.PowerAlarmLogData;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;
import com.aimir.util.DateTimeUtil;

public class Mk10_EV implements java.io.Serializable{

	private static final long serialVersionUID = 6334049270273495975L;

	Log log = LogFactory.getLog(Mk10_EV.class);

	public byte[] rawData = null;
    public int channelCnt;
    public int eventCnt;
    public EventLogData[] eventData;
    public PowerAlarmLogData[] powerAlarmLogData;
    public String statusFlag="";
    public String meterId="";

    private static final int OFS_NBR_EVT_ENTRIES = 0;
    private static final int OFF_EVT_DATA = 2;

    private static final int LEN_NBR_EVT_ENTRIES=2;
    private int LEN_EVT_DATA=6;

    DecimalFormat dformat = new DecimalFormat("#0.000000");


	/**
	 * Constructor .<p>
	 * @param data - read data (header,crch,crcl)
	 */
	public Mk10_EV(byte[] rawData, String meterId) {
		this.rawData = rawData;
		try {
			this.meterId=meterId;
			parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


    public Mk10_EV() {
	}

    public int getEventCnt() throws Exception {
    	eventCnt=DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_NBR_EVT_ENTRIES, LEN_NBR_EVT_ENTRIES));
    	return eventCnt;
    }

    public EventLogData[] parse() throws Exception {
    	log.info("//-------------------------------------------------------");
		log.info("//  Mk10 Event Parser Start");
		log.info("//-------------------------------------------------------");
		eventCnt=getEventCnt();

    	ArrayList<EventLogData> eventList = new ArrayList<EventLogData>();
    	ArrayList<PowerAlarmLogData> powerAlarmList = new ArrayList<PowerAlarmLogData>();

        for(int i = 0; i < eventCnt; i++){
            byte[] eventEntry = new byte[LEN_EVT_DATA];
            eventEntry =DataFormat.select(rawData,OFF_EVT_DATA+(i*LEN_EVT_DATA),LEN_EVT_DATA);
            EventLogData tempEventData= parseEvent(eventEntry);
            
            if(tempEventData.getMsg().equals(EVENTATTRIBUTE.POWER_DOWN.getName())) {            	
            	
            	PowerAlarmLogData pl = new PowerAlarmLogData();
            	
        		pl.setFlag(EVENTATTRIBUTE.POWER_DOWN.getCode());
        		pl.setMsg(EVENTATTRIBUTE.POWER_DOWN.getName());
        		pl.setDate(tempEventData.getDate());          // yyyymmdd
        		pl.setTime(tempEventData.getTime());             // hhmmss   	
        		powerAlarmList.add(pl);

            }
            if(tempEventData.getMsg().equals(EVENTATTRIBUTE.POWER_UP.getName())) {
            	
            	
            	PowerAlarmLogData pl = new PowerAlarmLogData();
            	
        		pl.setFlag(EVENTATTRIBUTE.POWER_UP.getCode());
        		pl.setMsg(EVENTATTRIBUTE.POWER_UP.getName());
        		pl.setDate(tempEventData.getDate());          // yyyymmdd
        		pl.setTime(tempEventData.getTime());             // hhmmss  
        		pl.setCloseDate(tempEventData.getDate());     // yyyymmdd
        		pl.setCloseTime(tempEventData.getTime());        // hhmmss
        		powerAlarmList.add(pl);

            }
            
            EventLogData logData = new EventLogData();
            logData.setDate(tempEventData.getDate());
            logData.setTime(tempEventData.getTime());
            logData.setKind("STE");
            logData.setMsg(tempEventData.getMsg());
            logData.setFlag(tempEventData.getFlag());
            if(!logData.getMsg().equals("Unknown")){
            	eventList.add(logData);
            }
        }
        
        if(eventList != null && eventList.size() > 0){
        	log.debug("event log length = "+ eventList.size());
        	this.eventData = (EventLogData[]) eventList.toArray(new EventLogData[0]);

        }
        
        
        if(powerAlarmList != null && powerAlarmList.size() > 0){
        	log.debug("Power Alarm Log Length = "+ powerAlarmList.size());
        	this.powerAlarmLogData = (PowerAlarmLogData[]) powerAlarmList.toArray(new PowerAlarmLogData[0]);

        }
        return eventData;
    }

    private EventLogData parseEvent(byte[] eventEntry) throws Exception{
    	EventLogData eventData = new EventLogData();
    	
		String eventDate=getEventDate(DataFormat.hex2unsigned32(DataFormat.LSB2MSB(DataFormat.select(eventEntry, 2, 4))));
		int eventCode = DataFormat.getIntTo2Byte(DataFormat.LSB2MSB(DataFormat.select(eventEntry, 0, 2)));
		
		if(eventCode >= EVENTATTRIBUTE.POWER_UP.getCode() && eventCode <=  EVENTATTRIBUTE.POWER_UP15.getCode()){
			eventCode = EVENTATTRIBUTE.POWER_UP.getCode();
		}
		eventData.setDate(eventDate.substring(0, 8));
		eventData.setTime(eventDate.substring(8, 14));
		eventData.setMsg(getEVENTATTRIBUTE(eventCode) == null ? "Unknown" : getEVENTATTRIBUTE(eventCode).getName());
		eventData.setFlag(eventCode);
		eventData.setKind("STE");
		log.info("Event Date: "+new OCTET(eventEntry).toHexString()+","+eventData.getDate()+eventData.getTime()+","+eventData.getMsg());
    	return eventData;
    }

    /*
     * Time/date as 6 bytes:
{Date}{Month}{Year}{Hour}{Minute}{Second}
Displays on an LCD formatted as alternating date and
time components. HH:MM:SS and DD/MM/YY.
The internal version of this is an unsigned 32bit
seconds since 1/1/1996. Used in a few instances,
where it is noted. Note that the mk6 uses time since
1996 for internal time.
     */
	public String getEventDate(long sec)
			throws Exception {

		String dateString = new String();

		try {
			Calendar c = Calendar.getInstance();

			int yy = 1996;
			int mm = 1;
			int day = 1;
			int HH = 0;
			int MM = 0;
			int SS = 0;

			/* why mm-1 because month start from 0 */
			c.set(yy, mm-1, day, HH, MM, SS);
			long temp = c.getTimeInMillis();
			c.setTimeInMillis(temp+sec*1000);
			dateString = DateTimeUtil.getDateString(c.getTime());

		} catch (Exception e) {
			throw new Exception("Util.addMinYymmdd() : " + e.getMessage());
		}
		return dateString;
	}

    public int getValue(Map eventClass){
    	int max=0;
    	int value=0;
    	Set set = eventClass.keySet();
    	Iterator iterator = set.iterator();

    	while (iterator.hasNext()) {
    	    String mapKey = (String)iterator.next();
    	    String mapValue = (String)eventClass.get(mapKey);
    	    if(Integer.parseInt(mapValue.substring(mapValue.lastIndexOf(".")+1))>max){
    	    	max=Integer.parseInt(mapValue.substring(mapValue.lastIndexOf(".")+1));
    	    }
    	}
    	value=++max;
    	return value;
    }

    public EventLogData[] getEvent() {
        return this.eventData;
    }
    
    public PowerAlarmLogData[] getPowerAlarmLog() {    	
    	return this.powerAlarmLogData;
    }

	public String getStatusFlag() {
		return statusFlag;
	}

	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation
	 * of this object.
	 */
	public String toString()
	{
	    StringBuffer retValue = new StringBuffer();

	    retValue.append("Mk10_EV [ ")
	        .append("rawData = ").append(Hex.decode(this.rawData)).append('\n')
	        .append("eventCnt = ").append(this.eventCnt).append('\n')
	        .append(" ]");

	    return retValue.toString();
	}
	
    public static EVENTATTRIBUTE getEVENTATTRIBUTE(int code) {
        for (EVENTATTRIBUTE type : EVENTATTRIBUTE.values()) {
            if (type.getCode() == code)
                return type;
        }

        return null;
    }
	
	public enum EVENTATTRIBUTE {

		POWER_UP             ( 4112, "Power Up"),
		POWER_UP1            ( 4113, "Power Up"),
		POWER_UP2            ( 4114, "Power Up"),
		POWER_UP3            ( 4115, "Power Up"),
		POWER_UP4            ( 4116, "Power Up"),
		POWER_UP5            ( 4117, "Power Up"),
		POWER_UP6            ( 4118, "Power Up"),
		POWER_UP7            ( 4119, "Power Up"),
		POWER_UP8            ( 4120, "Power Up"),
		POWER_UP9            ( 4121, "Power Up"),
		POWER_UP10           ( 4122, "Power Up"),
		POWER_UP11           ( 4123, "Power Up"),
		POWER_UP12           ( 4124, "Power Up"),
		POWER_UP13           ( 4125, "Power Up"),
		POWER_UP14           ( 4126, "Power Up"),
		POWER_UP15           ( 4127, "Power Up"),
		POWER_DOWN           ( 4096, "Power Down"),
		USER_CHANGE_SETTING1 ( 8256, "User changed settings in Setup 1 on port"),
		USER_CHANGE_SETTING2 ( 8257, "User changed settings in Setup 2 on port"),
		USER_CHANGE_SETTING3 ( 8258, "User changed settings in Setup 3 on port"),
		TIME_CHANGE          ( 8399, "System time changed"),
		TIME_CHANGE_ON_PORT  ( 8384, "System time change on port"),
		EFA_V          		 ( 12301, "EFA V- Condition became Active from Unlatched"),
		EFA_N				 ( 12290, "EFA N- Condition became Active from Unlatched"),
	    DEMAND_RESET         ( 20480, "Demand Reset"),
	    MANUAL_DEMAND_RESET  ( 20481, "Manual Demand Reset");

        private int code;
        private String name;

        EVENTATTRIBUTE(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    } 

}