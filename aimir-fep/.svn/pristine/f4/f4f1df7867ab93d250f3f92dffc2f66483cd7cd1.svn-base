/*
 * @(#)NURI_EV.java       1.0 07/05/02 *
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

package com.aimir.fep.meter.parser.SM300Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class NURI_EV {

    public static final int OFS_NBR_EVT_LOG = 0;
    public static final int OFS_ENTRIES = 1;

    public static final int LEN_NBR_EVT_LOG = 1;

    public static final int LEN_EVENT_TIME = 6;
    public static final int LEN_EVENT_SEQ_NBR = 2;
    public static final int LEN_USER_ID = 2;
    public static final int LEN_EVENT_CODE = 2;
    public static final int LEN_EVENT_ARGUMENT = 1;

    private EventLogData[] eventdata;
    private String starttime;
    private String endtime;

	private byte[] rawData = null;

    private static Log log = LogFactory.getLog(NURI_EV.class);

	/**
	 * Constructor .<p>
	 *
	 * @param data - read data (header,crch,crcl)
	 */
	public NURI_EV(byte[] rawData) {
		this.rawData = rawData;
        parseEvent();
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

    public int getNBR_VALID_ENTRIES() throws Exception {
        return DataFormat.hex2unsigned8(rawData[OFS_NBR_EVT_LOG]);
    }

    private void parseEvent(){

        try {
            int eventcount = getNBR_VALID_ENTRIES();
            this.eventdata = new EventLogData[eventcount];

            int offset = OFS_ENTRIES;
            int evt = 0;
            String kind = "STE";
            String msg = new String();
            String datetime = new String();
            for(int i = 0;i < eventcount; i++){
                datetime = getYyyymmddhhmmss(rawData, offset,LEN_EVENT_TIME);
                if(i == 0) {
					this.endtime = datetime;
				}
                offset += LEN_EVENT_TIME;
                offset += LEN_EVENT_SEQ_NBR;
                offset += LEN_USER_ID;

                evt = getEVT(rawData,offset,LEN_EVENT_CODE);
                msg = getEvtMessage(rawData,offset,LEN_EVENT_CODE);
                kind = getEVTKind(rawData,offset,LEN_EVENT_CODE);
                offset += LEN_EVENT_CODE;
                offset += LEN_EVENT_ARGUMENT;
            }
            this.starttime = datetime;

            for(int i = 0; i < eventcount; i++){
                this.eventdata[i] = new EventLogData();
                this.eventdata[i].setDate(datetime.substring(0,8));
                this.eventdata[i].setTime(datetime.substring(8,14));
                this.eventdata[i].setFlag(evt);
                this.eventdata[i].setMsg(msg);
            }

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
                    case  0: msg = "["+evcode +"]"+ "Diagnostic 1 - Polarity,Cross Phase,Reverse Energy Flow.";
                        break;
                    case  1: msg = "["+evcode +"]"+ "Diagnostic 1 - Condition Cleared.";
                        break;
                    case  2: msg = "["+evcode +"]"+ "Diagnostic 2 - Voltage Imbalance.";
                        break;
                    case  3: msg = "["+evcode +"]"+ "Diagnostic 2 - Condition Cleared.";
                        break;
                    case  4: msg = "["+evcode +"]"+ "Diagnostic 3 - Inactive Phase Current.";
                        break;
                    case  5: msg = "["+evcode +"]"+ "Diagnostic 3 - Condition Cleared.";
                        break;
                    case  6: msg = "["+evcode +"]"+ "Diagnostic 4 - Phase Angle Alert.";
                        break;
                    case  7: msg = "["+evcode +"]"+ "Diagnostic 4 - Condition Cleared.";
                        break;
                    case  8: msg = "["+evcode +"]"+ "Diagnostic 5 - High Distortion.";
                        break;
                    case  9: msg = "["+evcode +"]"+ "Diagnostic 5 - Condition Cleared.";
                        break;
                    case 10: msg = "["+evcode +"]"+ "Diagnostic 6 - Under Voltage,Phase A.";
                        break;
                    case 11: msg = "["+evcode +"]"+ "Diagnostic 6 - Condition Cleared.";
                        break;
                    case 12: msg = "["+evcode +"]"+ "Diagnostic 7 - Over Voltage,Phase A.";
                        break;
                    case 13: msg = "["+evcode +"]"+ "Diagnostic 7 - Condition Cleared.";
                        break;
                    case 14: msg = "["+evcode +"]"+ "Diagnostic 8 - High Neutrl Current.";
                        break;
                    case 15: msg = "["+evcode +"]"+ "Diagnostic 8 - Condition Cleared.";
                        break;
                    case 16: msg = "["+evcode +"]"+ "Caution 000400 - Under Voltage.";
                        break;
                    case 17: msg = "["+evcode +"]"+ "Caution 000400 - Condition Cleared.";
                        break;
                    case 18: msg = "["+evcode +"]"+ "Caution 004000 - Demand Overload.";
                        break;
                    case 19: msg = "["+evcode +"]"+ "Caution 004000 - Condition Cleared.";
                        break;
                    case 20: msg = "["+evcode +"]"+ "Caution 400000 - Received kWh.";
                        break;
                    case 21: msg = "["+evcode +"]"+ "Caution 400000 - Condition Cleared.";
                        break;
                    case 22: msg = "["+evcode +"]"+ "Caution 040000 - Leading kvarh.";
                        break;
                    case 23: msg = "["+evcode +"]"+ "Caution 040000 - Condition Cleared.";
                        break;
                    case 24: msg = "["+evcode +"]"+ "Real Time Pricing Activation.";
                        break;
                    case 25: msg = "["+evcode +"]"+ "Real Time Pricing Deactivation.";
                        break;
                    case 28: msg = "["+evcode +"]"+ "Calibration Mode Activated.";
                        break;
                    case 30: msg = "["+evcode +"]"+ "Revenue Guard Plus Event.";
                    break;
                }
            }

        }catch(Exception e){
            log.warn(e.getMessage());
        }

        return msg;
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
