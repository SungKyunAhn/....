package com.aimir.fep.meter.parser.MX2Table;

import com.aimir.fep.util.DataUtil;

/**
 * @author kskim
 */
public class TOUNRHoliday extends TOUFRHoliday implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2057157816755952883L;
	String yy;

	public String getYear() {
		return yy;
	}

	public void setYear(String year) {
		this.yy = year;
	}
	
	public byte[] toByteArray() throws Exception {
		if(this.yy == null)
			throw new Exception("Can not found Year data");
		
		final int LEN = 61;
		byte[] data = new byte[LEN];
		byte[] nrb = super.toByteArray();
		
		//year
		System.arraycopy(DataUtil.getBCD(this.yy), 0, data, 0, 1);
		
		//holidy data
		System.arraycopy(nrb, 0, data, 1, nrb.length);
		
		return data;
	}
}
