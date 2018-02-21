/**
 * @(#)ST076.java       1.0 07/01/29 *
 *
 * Event Log Data Class.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. *
 * This software is the confidential and proprietary information of
 * Nuritelcom, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Nuritelecom.
 */

package com.aimir.fep.meter.parser.SM110Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */

/**
 * I210(SM110)
 *
 * Standard Events
 * Code    Event
 * 01  Primary Power Down
 * 02  Primary Power Up
 * 07  Meter Accessed for Read
 * 08  Meter Accessed for Write
 * 11  Meter Programmed
 * 20  Demand Reset Occurred
 * 21  Self-read Occurred
 * 32  Test Mode Enter
 * 33  Test Mode Exit
 *
 * Manufacturer Events
 * Code Event
 * 16  Caution 000400 - Under Voltage
 * 17  Caution 000400 Condition Cleared
 * 18  Caution 004000 - Demand Overload
 * 19  Caution 004000 Condition Cleared
 * 20  Caution 400000 - Received kWh
 * 21  Caution 400000 Condition Cleared
 * 22  Caution 040000 - Leading kvarh
 * 23  Caution 040000 Condition Cleared
 * 24  Real Time Pricing Activation
 * 25  Real Time Pricing Deactivation
 * 28  Calibration Mode Activated
 * 33  DC detection condition
 * 34  DC detection condition cleared
 */
public class ST076 implements java.io.Serializable {

	private static final long serialVersionUID = 5675261008088415202L;
	private final int OFS_EVENT_FLAGS           = 0;
	private final int OFS_NBR_VALID_ENTRIES     = 0;
	private final int OFS_LAST_ENTRY_ELEMENT    = 3;
	private final int OFS_LAST_ENTRY_SEQ_NUMBER = 5;
	private final int OFS_NBR_UNREAD_ENTRIES    = 9;
	private final int OFS_EVENT_DATA            = 11;

	private final int LEN_EVENT_FLAGS           = 1;
	private final int LEN_NBR_VALID_ENTRIES     = 2;
	private final int LEN_LAST_ENTRY_ELEMENT    = 2;
	private final int LEN_LAST_ENTRY_SEQ_NUMBER = 4;
	private final int LEN_NBR_UNREAD_ENTRIES    = 2;

	private final int ONE_BLOCK_SIZE = 9;

	public final static int LEN_PARAM  = 11;
	public final static int LEN_ENTRIES  = 13;
	private final int LEN_DATETIME = 6;
	private final int LEN_SEQNUMER = 2;
	private final int LEN_USERID   = 2;
	private final int LEN_EVENT    = 2;
	private final int LEN_ARG      = 1;

	private byte[] data;
	private EventLogData[] eventdata;
	private String starttime;
	private String endtime;

    private static Log log = LogFactory.getLog(ST076.class);

    public ST076() {}
    
	/**
	 * Constructor .<p>
	 *
	 * @param data - read data (header,crch,crcl)
	 */
	public ST076(byte[] data) {
		this.data = data;
		parseEvent();
	}

	public int getNBR_VALID_ENTRIES() throws Exception {
		return DataFormat.hex2unsigned16(
                   DataFormat.LSB2MSB(
                       DataFormat.select(data, OFS_NBR_VALID_ENTRIES, LEN_NBR_VALID_ENTRIES)));
	}

	public int getNBR_UNREAD_ENTRIES() throws Exception {
		return DataFormat.hex2unsigned16(
                   DataFormat.LSB2MSB(
                       DataFormat.select(data, OFS_NBR_UNREAD_ENTRIES, LEN_NBR_UNREAD_ENTRIES)));
	}

	public int getLAST_ENTRY_ELEMENT() throws Exception {
		return DataFormat.hex2unsigned16(
				DataFormat.LSB2MSB(
					DataFormat.select(
						data,OFS_LAST_ENTRY_ELEMENT,LEN_LAST_ENTRY_ELEMENT)));
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
			int eventcount = getNBR_UNREAD_ENTRIES();
            log.debug("eventcount : "+eventcount);

            this.eventdata = new EventLogData[eventcount];

			int offset = OFS_EVENT_DATA;
			int evt = 0;
            String kind = "STE";
			String msg = new String();
			String datetime = new String();
			for(int i = 0;i < eventcount; i++){
				datetime = getYyyymmddhhmmss(data, offset,LEN_DATETIME);
				if(i == 0) {
					this.endtime = datetime;
				}
				offset += LEN_DATETIME;
				offset += LEN_SEQNUMER;
				offset += LEN_USERID;

				evt = getEVT(data,offset,LEN_EVENT);
				msg = getEvtMessage(data,offset,LEN_EVENT);
                kind = getEVTKind(data,offset,LEN_EVENT);
				offset += LEN_EVENT;
				offset += LEN_ARG;

                this.eventdata[i] = new EventLogData();
                this.eventdata[i].setDate(datetime.substring(0,8));
                this.eventdata[i].setTime(datetime.substring(8,14));
                this.eventdata[i].setKind(kind);
                this.eventdata[i].setFlag(evt);
                this.eventdata[i].setMsg(msg);

                log.debug("datetime,kind,eventflag,msg=>"+datetime+","+kind+","+evt+","+msg);
			}
			this.starttime = datetime;

		} catch (Exception e) {
			log.warn(e.getMessage());
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
		//int nevcode = 0;
		//nevcode = evcode | (kind << 15);

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
            
            /*
			//standard event
			if(kind == 0){
				switch(evcode){
					case  1: msg = "["+evcode +"]"+ "Primary Power Down";
						break;
					case  2: msg = "["+evcode +"]"+ "Primary Power Up";
						break;
					case  7: msg = "["+evcode +"]"+ "End Device Accessed for Read";
						break;
					case  8: msg = "["+evcode +"]"+ "End Device Accessed for Write";
						break;
					case 11: msg = "["+evcode +"]"+ "End Device Programmed";
						break;
					case 20: msg = "["+evcode +"]"+ "Demand Reset Occurred";
						break;
					case 21: msg = "["+evcode +"]"+ "Self-read Occurred";
						break;
					case 32: msg = "["+evcode +"]"+ "Test Mode Enter";
						break;
					case 33: msg = "["+evcode +"]"+ "Test Mode Exit";
						break;
				}
			}else if(kind == 1){//manufacturer event
				switch(evcode){
					case  0: msg = "["+evcode +"]"+ "Diagnostic 1 - Polarity,Cross Phase,Reverse Energy Flow";
						break;
					case  1: msg = "["+evcode +"]"+ "Diagnostic 1 - Condition Cleared";
						break;
					case  2: msg = "["+evcode +"]"+ "Diagnostic 2 - Voltage Imbalance";
						break;
					case  3: msg = "["+evcode +"]"+ "Diagnostic 2 - Condition Cleared";
						break;
					case  4: msg = "["+evcode +"]"+ "Diagnostic 3 - Inactive Phase Current";
						break;
					case  5: msg = "["+evcode +"]"+ "Diagnostic 3 - Condition Cleared";
						break;
					case  6: msg = "["+evcode +"]"+ "Diagnostic 4 - Phase Angle Alert";
						break;
					case  7: msg = "["+evcode +"]"+ "Diagnostic 4 - Condition Cleared";
						break;
					case  8: msg = "["+evcode +"]"+ "Diagnostic 5 - High Distortion";
						break;
					case  9: msg = "["+evcode +"]"+ "Diagnostic 5 - Condition Cleared";
						break;
					case 10: msg = "["+evcode +"]"+ "Diagnostic 6 - Under Voltage,Phase A";
						break;
					case 11: msg = "["+evcode +"]"+ "Diagnostic 6 - Condition Cleared";
						break;
					case 12: msg = "["+evcode +"]"+ "Diagnostic 7 - Over Voltage,Phase A";
						break;
					case 13: msg = "["+evcode +"]"+ "Diagnostic 7 - Condition Cleared";
						break;
					case 14: msg = "["+evcode +"]"+ "Diagnostic 8 - High Neutrl Current";
						break;
					case 15: msg = "["+evcode +"]"+ "Diagnostic 8 - Condition Cleared";
						break;
					case 16: msg = "["+evcode +"]"+ "Caution 000400 - Under Voltage";
						break;
					case 17: msg = "["+evcode +"]"+ "Caution 000400 - Condition Cleared";
						break;
					case 18: msg = "["+evcode +"]"+ "Caution 004000 - Demand Overload";
						break;
					case 19: msg = "["+evcode +"]"+ "Caution 004000 - Condition Cleared";
						break;
					case 20: msg = "["+evcode +"]"+ "Caution 400000 - Received kWh";
						break;
					case 21: msg = "["+evcode +"]"+ "Caution 400000 - Condition Cleared";
						break;
					case 22: msg = "["+evcode +"]"+ "Caution 040000 - Leading kvarh";
						break;
					case 23: msg = "["+evcode +"]"+ "Caution 040000 - Condition Cleared";
						break;
					case 24: msg = "["+evcode +"]"+ "Real Time Pricing Activation";
						break;
					case 25: msg = "["+evcode +"]"+ "Real Time Pricing Deactivation";
						break;
					case 28: msg = "["+evcode +"]"+ "Calibration Mode Activated";
						break;
					case 30: msg = "["+evcode +"]"+ "Revenue Guard Plus Event";
					break;
				}
			}
			*/
            
			//standard event
			if(kind == 0){
				switch(evcode){
					case  1: msg = "Primary Power Down";
						break;
					case  2: msg = "Primary Power Up";
						break;
					case  7: msg = "End Device Accessed for Read";
						break;
					case  8: msg = "End Device Accessed for Write";
						break;
					case 11: msg = "Meter Programmed";
						break;
					case 20: msg = "Demand Reset Occurred";
						break;
					case 21: msg = "Self-read Occurred";
						break;
					case 32: msg = "Test Mode Start";
						break;
					case 33: msg = "Test Mode Exit";
						break;
				}
			}else if(kind == 1){//manufacturer event
				switch(evcode){
					case  0: msg = "Polarity,Cross Phase,Reverse Energy Flow";
						break;
					case  1: msg = "Polarity,Cross Phase,Reverse Energy Flow Condition Cleared";
						break;
					case  2: msg = "Voltage Imbalance";
						break;
					case  3: msg = "Voltage Imbalance Condition Cleared";
						break;
					case  4: msg = "Inactive Phase Current";
						break;
					case  5: msg = "Inactive Phase Current Condition Cleared";
						break;
					case  6: msg = "Phase Angle Alert";
						break;
					case  7: msg = "Phase Angle Alert Condition Cleared";
						break;
					case  8: msg = "High Distortion";
						break;
					case  9: msg = "High Distortion Condition Cleared";
						break;
					case 10: msg = "Under Voltage,Phase A";
						break;
					case 11: msg = "Under Voltage,Phase A Condition Cleared";
						break;
					case 12: msg = "Over Voltage,Phase A";
						break;
					case 13: msg = "Over Voltage,Phase A Condition Cleared";
						break;
					case 14: msg = "High Neutrl Current";
						break;
					case 15: msg = "High Neutrl Current Condition Cleared";
						break;
					case 16: msg = "Under Voltage";
						break;
					case 17: msg = "Under Voltage Condition Cleared";
						break;
					case 18: msg = "Demand Overload";
						break;
					case 19: msg = "Demand Overload Condition Cleared";
						break;
					case 20: msg = "Received kWh";
						break;
					case 21: msg = "Received kWh Condition Cleared";
						break;
					case 22: msg = "Leading kvarh";
						break;
					case 23: msg = "Condition Cleared";
						break;
					case 24: msg = "Real Time Pricing Activation";
						break;
					case 25: msg = "Real Time Pricing Deactivation";
						break;
					case 28: msg = "Calibration Mode Activated";
						break;
					case 30: msg = "Revenue Guard Plus Event";
					break;
				}
			}

		}catch(Exception e){
			log.warn(e.getMessage());
		}

		return msg;
	}

	private byte[] parseYyyymmddhhmmss(byte[] b, int offset, int len)
						throws Exception {

		byte[] datetime = new byte[7];

		int blen = data.length;
		if(blen-offset < 6) {
			throw new Exception("YYMMDDHHMMSS FORMAT ERROR : "+(blen-offset));
		}
		if(len != 6) {
			throw new Exception("YYMMDDHHMMSS LEN ERROR : "+len);
		}

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
		if(blen-offset < 6) {
			throw new Exception("YYMMDDHHMMSS FORMAT ERROR : "+(blen-offset));
		}
		if(len != 6) {
			throw new Exception("YYMMDDHHMMSS LEN ERROR : "+len);
		}

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