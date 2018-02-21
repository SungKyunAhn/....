package com.aimir.fep.meter.parser.a3rlnqTable;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

public class A3_MT implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6559226302812793427L;
	private Log log = LogFactory.getLog(A3_MT.class);

	private byte[] rawData = null;

	private byte[] ED_MODEL = new byte[12];
	private byte[] METER_SERIAL = new byte[8];
	private byte[] CLOCK_CALENDAR = new byte[7];
	private byte[] INSTRUMENT_SCALE = new byte[1];
	private byte[] METER_CONSTANT = new byte[6];
	private byte[] METER_CONSTANT_SCALE = new byte[1];
	private byte[] CT = new byte[6];
	private byte[] VT = new byte[6];
	private byte[] METER_ELEMENT = new byte[2];
	private byte[] METER_STATUS = new byte[15];

	private String modelName = null;
	private String meterSerial = null;
	private String timestamp = null;
	private Double constant = null;
	private long ke = 1L;
	private int meter_constant_scale = 0;
	private String meterElement = null;
	private Double ct = null;
	private Double vt = null;
	private String meterLog = null;
	private Double instrumentScale = null;

	public A3_MT(byte[] rawData) {
		this.rawData = rawData;
		parse();
	}

	public void parse() {
		int pos = 0;

		System.arraycopy(rawData, pos, ED_MODEL, 0, ED_MODEL.length);
		pos += ED_MODEL.length;
		this.modelName = new String(ED_MODEL).trim();
		log.debug("ED_MODEL[" + modelName + "]");

		System.arraycopy(rawData, pos, METER_SERIAL, 0, METER_SERIAL.length);
		pos += METER_SERIAL.length;
		this.meterSerial = new String(METER_SERIAL).trim();
		log.debug("METER_SERIAL[" + meterSerial + "]");

		System
				.arraycopy(rawData, pos, CLOCK_CALENDAR, 0,
						CLOCK_CALENDAR.length);
		byte[] yyyy = new byte[2];
		byte[] MM = new byte[1];
		byte[] dd = new byte[1];
		byte[] hh = new byte[1];
		byte[] mm = new byte[1];
		byte[] ss = new byte[1];

		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(CLOCK_CALENDAR);
			bis.read(yyyy);
			bis.read(MM);
			bis.read(dd);
			bis.read(hh);
			bis.read(mm);
			bis.read(ss);
			bis.close();

			String y = DataFormat.getIntTo2Byte(yyyy) + "";
			String M = DataFormat.getIntToBytes(MM) + "";
			String d = DataFormat.getIntToBytes(dd) + "";
			String h = DataFormat.getIntToBytes(hh) + "";
			String m = DataFormat.getIntToBytes(mm) + "";
			String s = DataFormat.getIntToBytes(ss) + "";

			if (DataFormat.getIntToBytes(MM) < 10)
				M = "0" + DataFormat.getIntToBytes(MM);
			if (DataFormat.getIntToBytes(dd) < 10)
				d = "0" + DataFormat.getIntToBytes(dd);
			if (DataFormat.getIntToBytes(hh) < 10)
				h = "0" + DataFormat.getIntToBytes(hh);
			if (DataFormat.getIntToBytes(mm) < 10)
				m = "0" + DataFormat.getIntToBytes(mm);
			if (DataFormat.getIntToBytes(ss) < 10)
				s = "0" + DataFormat.getIntToBytes(ss);

			this.timestamp = y + M + d + h + m + s;
		} catch (IOException e1) {
			e1.printStackTrace();
			log.debug(e1, e1);
		}

		pos += CLOCK_CALENDAR.length;
		log.debug("CLOCK_CALENDAR[" + timestamp + "]");

		System.arraycopy(rawData, pos, INSTRUMENT_SCALE, 0,
				INSTRUMENT_SCALE.length);
		pos += INSTRUMENT_SCALE.length;

		this.instrumentScale = Math.pow(10, DataFormat
				.hex2signed8(INSTRUMENT_SCALE[0]));
		log.debug("INSTRUMENT_SCALE[" + instrumentScale + "]");

		System
				.arraycopy(rawData, pos, METER_CONSTANT, 0,
						METER_CONSTANT.length);
		pos += METER_CONSTANT.length;

		try {
			this.constant = DataFormat.hex2long(DataFormat
					.LSB2MSB(METER_CONSTANT))
					* instrumentScale.doubleValue();
		} catch (Exception e1) {
			e1.printStackTrace();
			log.debug(e1, e1);
		}

		try {
			this.ke = DataFormat.hex2long(DataFormat.LSB2MSB(METER_CONSTANT));
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e, e);
		}
		log.debug("METER_CONSTANT[" + constant + "]");
		log.debug("KE[" + ke + "]");

		// MT15 Meter Constant Scale : Adjusted Ke Scale Factor
		System.arraycopy(rawData, pos, METER_CONSTANT_SCALE, 0,
				METER_CONSTANT_SCALE.length);
		this.meter_constant_scale = DataFormat
				.hex2signed8(METER_CONSTANT_SCALE[0]);
		pos += METER_CONSTANT_SCALE.length;
		log.debug("METER_CONSTANT_SCALE[" + METER_CONSTANT_SCALE[0] + "]");

		System.arraycopy(rawData, pos, CT, 0, CT.length);
		pos += CT.length;
		try {
			this.ct = DataFormat.hex2long(DataFormat.LSB2MSB(CT))
					* instrumentScale.doubleValue();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		log.debug("CT[" + ct + "]");

		System.arraycopy(rawData, pos, VT, 0, VT.length);
		pos += VT.length;
		try {
			this.vt = DataFormat.hex2long(DataFormat.LSB2MSB(VT))
					* instrumentScale.doubleValue();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug("VT[" + vt + "]");

		System.arraycopy(rawData, pos, METER_ELEMENT, 0, METER_ELEMENT.length);
		pos += METER_ELEMENT.length;
		this.meterElement = setMeterElement(METER_ELEMENT);
		log.debug("METER_ELEMENT[" + this.meterElement + "]");

		System.arraycopy(rawData, pos, METER_STATUS, 0, METER_STATUS.length);
		pos += METER_STATUS.length;
		this.meterLog = setMeterStatus(METER_STATUS);
		log.debug("METER_STATUS[" + meterLog + "]");
	}
	
	/**
	 * Two Voltage:   0x00 (3P 3W)
	 * One Voltage:   0x40 (1P 2W)
	 * Three Voltage: 0x80 (3P 4W)
	 * 2 1/2 Voltage: 0xC0 (1P 3W)
	 * UNKNOWN: 0xFF
	 * 
	 * Table MT_51
	 * @return
	 */
	public String setMeterElement(byte[] temp) {
		
		String melement = "Unknown";
		try{
			byte[] svc_def = DataFormat.LSB2MSB(temp);
			int melements = (int)((svc_def[1] & 0x7F) >> 5);
			int wires = (int)((svc_def[1] & 0x07) >> 2);
			

			switch(melements){
				case 2:
					if(wires == 0)
						melement = "3P3W";
					else
						melement = "3P4W";
					break;
				case 3:
					if(wires == 0)
						melement = "1P3W";
					break;
				default:
					melement = "Unknown";
			}
		}catch(Exception e){
			log.warn(e.getMessage());
		}
		return melement;

	}
	
	/**
	 * @param status
	 * @return
	 * 
	 * Table ST_03
	 */
	public String setMeterStatus(byte[] status){

		StringBuffer sb = new StringBuffer();
		StringBuffer msg = new StringBuffer();
		
		String[] st = new String[]{
				"low battery",
				"clock error",
				"registered memory error",
				"ROM failure",
				"RAM failure",
				"self check error",
				"configuration error",
				"unprogrammed",
				"filler",
				"filler",
				"reverse rotation",
				"tamper detect",
				"power failure",
				"demand overload",
				"low loss potential",
				"measurement error",
				"spare",
				"button press clear data",
				"button press demand reset",
				"time changed",
				"pending table activated",
				"self rdad data available",
				"previous season data available",
				"demand reset data available"
			};
		
		try{
			for(int i = 0; i < status.length; i++){
				int convertInt = DataFormat.hex2dec(status, i, 1);
				String convertStr 
					= Util.frontAppendNStr('0',Integer.toBinaryString(convertInt),8);
				sb.append(convertStr);
			}
						
			for(int i = 0; i < st.length;i++){
				if(sb.charAt(i) == '1'){
					msg.append(st[i]);
				}
			}
		}catch(Exception e){
			log.warn(e.getMessage());
		}
		
		return msg.toString();

	}

	public String getModelName() {
		return this.modelName;
	}

	public String getMeterSerial() {
		return this.meterSerial;
	}

	public String getTimeStamp() {
		return this.timestamp;
	}

	public Double getConstant() {
		return this.constant;
	}

	public long getKE() {
		return this.ke;
	}

	public int getMeterConstantScale() {
		return this.meter_constant_scale;
	}

	public String getMeterElement() {
		return this.meterElement;
	}

	public Double getCT() {
		return this.ct;
	}

	public Double getVT() {
		return this.vt;
	}

	public Double getInstrumentScale() {
		return this.instrumentScale;
	}

	public String getMeterLog() {
		return this.meterLog;
	}

}
