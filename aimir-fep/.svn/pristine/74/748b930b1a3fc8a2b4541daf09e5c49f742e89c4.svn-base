/** 
 * @(#)ST76.java       1.0 05/07/25 *
 * 
 * Event Log Data Class.
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.a3rlnqTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;


/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class ST76 implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5869765767658604647L;
	private final int OFS_EVENT_FLAGS           = 0;
	private final int OFS_NBR_VALID_ENTRIES     = 1;
	private final int OFS_LAST_ENTRY_ELEMENT    = 3;
	private final int OFS_LAST_ENTRY_SEQ_NUMBER = 5;
	private final int OFS_NBR_UNREAD_ENTRIES    = 9;
	private final int OFS_EVENT_DATA            = 11;
	
	private final int LEN_EVENT_FLAGS           = 1;
	private final int LEN_NBR_VALID_ENTRIES     = 2;
	private final int LEN_LAST_ENTRY_ELEMENT    = 2;
	private final int LEN_LAST_ENTRY_SEQ_NUMBER = 4;
	private final int LEN_NBR_UNREAD_ENTRIES    = 2;

	private final int ONE_BLOCK_SIZE = 12;//9;
	private final int LEN_DATETIME = 6;
	private final int LEN_SEQNUMER = 2;
	private final int LEN_USERID   = 2;
	private final int LEN_EVENT    = 2;
	
	private byte[] data;
	private EventLogData[] eventdata;
	private String starttime;
	private String endtime;
	
    private Log logger = LogFactory.getLog(getClass());
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST76(byte[] data) {
		this.data = data;
		parseEvent();
	}
	
	public int getNBR_VALID_ENTRIES() throws Exception {
		return DataFormat.hex2unsigned16(
				DataFormat.LSB2MSB(
					DataFormat.select(
						data,OFS_NBR_VALID_ENTRIES,LEN_NBR_VALID_ENTRIES)));
	}
	
	public String getStartTime() {
		return this.starttime;
	}

	public String getEndTime() {
		return this.endtime;
	}
	
	public EventLogData[] getEvent() {
		return this.eventdata;
	}
	
	private void parseEvent(){
		
		try {
			int eventcount = getNBR_VALID_ENTRIES();
            this.eventdata = new EventLogData[eventcount];
			byte[] temp    = new byte[eventcount*ONE_BLOCK_SIZE ];

			int offset = OFS_EVENT_DATA;
			int evt = 0;
            String kind = "STE";
			String msg = new String();
			String datetime = new String();
			for(int i = 0;i < eventcount; i++){
				datetime = getYyyymmddhhmmss(data, offset,LEN_DATETIME);
				if(i == 0)
					this.endtime = datetime;
				offset += LEN_DATETIME;
				offset += LEN_SEQNUMER;
				offset += LEN_USERID;

				evt = getEVT(data,offset,LEN_EVENT);
				msg = getEvtMessage(data,offset,LEN_EVENT);
                kind = getEVTKind(data,offset,LEN_EVENT);
				offset += LEN_EVENT;
				
                this.eventdata[i] = new EventLogData();
                this.eventdata[i].setDate(datetime.substring(0,8));
                this.eventdata[i].setTime(datetime.substring(8,14));
                this.eventdata[i].setKind(kind);
                this.eventdata[i].setFlag(evt);
                this.eventdata[i].setMsg(msg);
			}
			this.starttime = datetime;
			
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}

	}

	
    private String getEVTKind(byte[] b, int offset, int len) throws Exception
    {
        int kind = DataFormat.hex2signed16(
                DataFormat.LSB2MSB(
                        DataFormat.select(b,offset,len))) & 0x10;
        if(kind > 0){
            return "MFE";
        }else{
            return "STE";
        }
    }

	private int getEVT(byte[] b, int offset, int len) throws Exception
	{

		int evcode = DataFormat.hex2signed16(
						DataFormat.LSB2MSB(
								DataFormat.select(b,offset,len))) & 2047;
		int kind = DataFormat.hex2signed16(
				DataFormat.LSB2MSB(
						DataFormat.select(b,offset,len))) & 0x10;

		return evcode;
	}
	
	private String getEvtMessage(byte[] b,int offset, int len){

		String msg = new String();

		try{
			int evcode = DataFormat.hex2signed16(
							DataFormat.LSB2MSB(
									DataFormat.select(b,offset,len))) & 2047;
			int kind = DataFormat.hex2signed16(
							DataFormat.LSB2MSB(
									DataFormat.select(b,offset,len))) & 0x10;
            if(kind > 0){
                kind = 1;
            }else{
                kind = 0;
            }

    
			//standard event
			if(kind == 0){
				switch(evcode){
					case  1: msg = "Primary Power Down";
						break;
					case  2: msg = "Primary Power Up";
						break;
					case  3: msg = "Time Changed (old time)";
						break;
					case  4: msg = "Time Changed (new time)";
						break;
					case  7: msg = "End Device Accessed for Read";
						break;
					case  8: msg = "End Device Accessed for Write";
						break;
					case  9: msg = "Procedure Invoked";
						break;
					case  10: msg = "Table Written To";
						break;
					case  11: msg = "End Device Programmed";
						break;
					case  12: msg = "Communication Terminated Normally";
						break;
					case  13: msg = "Communication Terminated Abnormally";
						break;
					case  14: msg = "Reset List Pointers";
						break;
					case  15: msg = "Update List Pointers";
						break;
					case  16: msg = "History Log Cleared";
						break;
					case  17: msg = "History Log Pointers Updated";
						break;
					case  18: msg = "Event Log Cleared";
						break;
					case  19: msg = "Event Log Pointers Updated";
						break;						
					case  20: msg = "Demand Reset Occurred";
						break;
					case  21: msg = "Self Read Occurred";
						break;
					case  22: msg = "Daylight Savings Time On";
						break;
					case  23: msg = "Daylight Savings Time Off";
						break;
					case  24: msg = "Season Change";
						break;
					case  25: msg = "Rate Change";
						break;
					case  26: msg = "Special Schedule Activation";
						break;
					case  27: msg = "Tier Switch Change";
						break;
					case  28: msg = "Pending Table Activation";
						break;
					case  29: msg = "Pending Table Clear";
						break;
					case  30: msg = "Metering mode started";
						break;
					case  31: msg = "Metering mode stopped";
						break;
					case  32: msg = "Test mode started";
						break;
					case  33: msg = "Test mode stopped";
						break;
					case  34: msg = "Meter shop mode started";
						break;
					case  35: msg = "Meter shop mode stopped";
						break;
					case  36: msg = "Configuration error detected";
						break;
					case  37: msg = "Self check error detected";
						break;
					case  38: msg = "RAM failure detected";
						break;
					case  39: msg = "ROM failure detected";
						break;
					case  40: msg = "Non volatile memory failure detected";
						break;
					case  41: msg = "Clock error detected";
						break;
					case  42: msg = "Measurement error detected";
						break;
					case  43: msg = "Low battery detected";
						break;
					case  44: msg = "Low loss potential detected";
						break;
					case  45: msg = "Demand overload detected";
						break;
					case  46: msg = "Power failure detected";
						break;
					case  47: msg = "Tamper detect detected";
						break;
					case  48: msg = "Reverse rotation detected";
						break;
				}
			}else if(kind == 1){//manufacturer event
				switch(evcode){
					case  48: msg = "MFG Enter Tier Override";
						break;
					case  49: msg = "MFG Exit Tier Override";
						break;
					case  50: msg = "MFG Terminal cover tamper";
						break;
					case  51: msg = "MFG Main cover tamper";
						break;
					case  52: msg = "MFG External Event 0";
						break;
					case  53: msg = "MFG External Event 1";
						break;
					case  54: msg = "MFG External Event 2";
						break;
					case  55: msg = "MFG External Event 3";
						break;
					case  56: msg = "MFG Phase A OFF";
						break;
					case  57: msg = "MFG Phase A ON";
						break;
					case 58: msg = "MFG Phase B OFF";
						break;
					case 59: msg = "MFG Phase B ON";
						break;
					case 60: msg = "MFG Phase C OFF";
						break;
					case 61: msg = "MFG Phase C ON";
						break;
				}
			}

		}catch(Exception e){
			logger.warn(e.getMessage());
		}

		return msg;
	}

	
	/*
	 * 	0 = No Event
	1 = Primary Power Down
	2 = Primary Power Up
	3 = Time Changed (old time)
	4 = Time Changed (new time)
	7 = End Device Accessed for Read
	8 = End Device Accessed for Write
	9 = Procedure Invoked
	10 = Table Written To
	11 = End Device Programmed
	12 = Communication Terminated Normally
	13 = Communication Terminated Abnormally
	14 = Reset List Pointers
	15 = Update List Pointers
	16 = History Log Cleared
	17 = History Log Pointers Updated
	18 = Event Log Cleared
	19 = Event Log Pointers Updated
	
	20 = Demand Reset Occurred
	21 = Self Read Occurred
	22 = Daylight Savings Time On
	23 = Daylight Savings Time Off
	24 = Season Change
	25 = Rate Change
	26 = Special Schedule Activation
	27 = Tier Switch Change
	28 = Pending Table Activation
	29 = Pending Table Clear
	30 = Metering mode started
	31 = Metering mode stopped
	32 = Test mode started
	33 = Test mode stopped
	34 = Meter shop mode started
	35 = Meter shop mode stopped
	36 = Configuration error detected
	37 = Self check error detected
	38 = RAM failure detected
	39 = ROM failure detected
	40 = Non volatile memory failure detected
	41 = Clock error detected
	42 = Measurement error detected
	43 = Low battery detected
	44 = Low loss potential detected
	45 = Demand overload detected
	46 = Power failure detected
	47 = Tamper detect detected
	48 = Reverse rotation detected
	2048 = MFG Enter Tier Override
	2049 = MFG Exit Tier Override
	2050 = MFG Terminal cover tamper
	2051 = MFG Main cover tamper
	2052 = MFG External Event 0
	2053 = MFG External Event 1
	2054 = MFG External Event 2
	2055 = MFG External Event 3
	2056 = MFG Phase A OFF
	2057 = MFG Phase A ON
	2058 = MFG Phase B OFF
	2059 = MFG Phase B ON
	2060 = MFG Phase C OFF
	2061 = MFG Phase C ON
	 */
	private byte[] parseEVT(byte[] b, int offset, int len) throws Exception{
		
		byte[] event = new byte[2];
		int nevent = 0;
		int oevent = DataFormat.hex2unsigned16(
						DataFormat.LSB2MSB(
							DataFormat.select(b,offset,len)));
		
		switch(oevent){
			case 1: 
				nevent = 0;//powerfail
				break;
			case 2: 
				nevent = 1;//powerup
				break;
			case 3:
				nevent = 2;//time change begore
				break;
			case 4: 
				nevent = 3;//time change after
				break;
			case 18:
				nevent = 255;//event clear
				break;
			case 20:
				nevent = 6;//demand reset
				break;
			case 32:
				nevent = 4;//test mode start
				break;
			case 33:
				nevent = 5;//test mode end
				break;
			default :
				nevent = oevent;
				break;
		}
		
		event = DataFormat.dec2hex((char)nevent);
		return event;
	}

	
	private byte[] parseYyyymmddhhmmss(byte[] b, int offset, int len)
						throws Exception {

		byte[] datetime = new byte[7];
		
		int blen = data.length;
		if(blen-offset < 6)
			throw new Exception("YYMMDDHHMMSS FORMAT ERROR : "+(blen-offset));
		if(len != 6)
			throw new Exception("YYMMDDHHMMSS LEN ERROR : "+len);
		
		int idx = offset;
		int yy = DataFormat.hex2unsigned8(b[idx++]);
		int mm = DataFormat.hex2unsigned8(b[idx++]);
		int dd = DataFormat.hex2unsigned8(b[idx++]);
		int hh = DataFormat.hex2unsigned8(b[idx++]);
		int MM = DataFormat.hex2unsigned8(b[idx++]);
		int ss = DataFormat.hex2unsigned8(b[idx++]);
		
		int currcen = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;
	
		int year   = yy;
		if(year != 0){
			year = yy + currcen;
		}

		datetime[0] = (byte)((year >> 8) & 0xff);
		datetime[1] = (byte)(year & 0xff);
		datetime[2] = (byte) mm;
		datetime[3] = (byte) dd;
		datetime[4] = (byte) hh;
		datetime[5] = (byte) MM;
		datetime[6] = (byte) ss;
		
		return datetime;
		
	}
	
	private String getYyyymmddhhmmss(byte[] b, int offset, int len)
							throws Exception {
		
		int blen = b.length;
		if(blen-offset < 6)
			throw new Exception("YYMMDDHHMMSS FORMAT ERROR : "+(blen-offset));
		if(len != 6)
			throw new Exception("YYMMDDHHMMSS LEN ERROR : "+len);
		
		int idx = offset;
		
		int yy = DataFormat.hex2unsigned8(b[idx++]);
		int mm = DataFormat.hex2unsigned8(b[idx++]);
		int dd = DataFormat.hex2unsigned8(b[idx++]);
		int hh = DataFormat.hex2unsigned8(b[idx++]);
		int MM = DataFormat.hex2unsigned8(b[idx++]);
		int ss = DataFormat.hex2unsigned8(b[idx++]);

		StringBuffer ret = new StringBuffer();
		
		int currcen = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;
	
		int year   = yy;
		if(year != 0){
			year = yy + currcen;
		}
		
		ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
		ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(ss),2));
		
		return ret.toString();
			
	}
}