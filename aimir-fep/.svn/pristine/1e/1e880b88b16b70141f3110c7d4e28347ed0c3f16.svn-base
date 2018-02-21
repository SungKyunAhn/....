package com.aimir.fep.meter.parser.MX2Table;

import java.io.IOException;

import com.aimir.fep.util.DataUtil;

/**
 * @author kskim
 */
public class TOUEndMessage implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5804928045821042451L;
	String calendarNo;
	int selfReading;
	public String getCalendarNo() {
		return calendarNo;
	}
	public void setCalendarNo(String calendarNo) {
		this.calendarNo = calendarNo;
	}
	public int getSelfReading() {
		return selfReading;
	}
	public void setSelfReading(int selfReading) {
		this.selfReading = selfReading;
	}
	public byte[] toByteArray() throws Exception {
		byte[] data = new byte[3];
		byte[] bcd = DataUtil.getBCD(calendarNo);
		
		System.arraycopy(bcd, 0, data, 0, 2);
		
		data[2] = (byte) this.selfReading;
		
		return data;
	}
}
