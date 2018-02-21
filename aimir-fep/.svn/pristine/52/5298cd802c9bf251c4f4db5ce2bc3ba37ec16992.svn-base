package com.aimir.fep.meter.parser.MX2Table;

import com.aimir.fep.util.DataUtil;

/**
 * @author kskim
 */
public class TOUActivationDate implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7499839843839604455L;
	int calendarType=-1;
	String yyMMdd;

	//---
	
	public TOUActivationDate(){}
	

	public int getCalendarType() {
		return calendarType;
	}

	public void setCalendarType(int calendarType) {
		this.calendarType = calendarType;
	}

	public String getYyMMdd() {
		return yyMMdd;
	}

	public void setYyMMdd(String yyMMdd) {
		this.yyMMdd = yyMMdd;
	}


	public byte[] toByteArray() throws Exception {
		byte[] data = new byte[4];
		
		if(this.calendarType<0 || this.calendarType>1)
			throw new Exception("Can not found Calendar type data");
		
		data[0] = (byte) this.calendarType;
		
		if(this.yyMMdd==null && this.yyMMdd.length()!=6)
			throw new Exception("Can not found YYMMDD data");
		
		byte[] bcd = DataUtil.getBCD(getYyMMdd());
		
		System.arraycopy(bcd, 0, data, 1, 3);
		
		return data;
	}
	
	
	
	
}
