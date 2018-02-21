package com.aimir.fep.meter.parser.amuKepco_2_5_0Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * Data Time Format
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 9. 오후 5:26:33$
 */
public class DateTimeFormat {

	private Log log = LogFactory.getLog(DateTimeFormat.class);
	 
	public static final int OFS_DATE_TIME 			= 0;
	public static final int LEN_DATE_TIME 			= 7;
	
	private byte[] data;
   

	/**
	 * Constructor
	 */
	public DateTimeFormat(byte[] data) {
		this.data = data;
	}
	
	/**
	 * get Date Time
	 * @return
	 * @throws Exception
	 */
	public String getDateTime() throws Exception {
		
		String yyyymmddhhmm ="";
		try {
			yyyymmddhhmm = getYyyymmddhhmmss(data,OFS_DATE_TIME,LEN_DATE_TIME);
		}catch (Exception e) {
			log.warn("Date Time Format Error");
		}	 
		return yyyymmddhhmm;
	}
	
	/**
	 * get Time (YYYY + MM + DD + HH+ MM + SS)
	 * @param data
	 * @param offset
	 * @param len
	 * @return
	 * @throws Exception
	 */
	private String getYyyymmddhhmmss(byte[] data, int offset, int len)
							throws Exception {
		
		int data_len = data.length;
		if(data_len-offset < 7)
			throw new Exception("YYYYMMDDHHMMSS FORMAT ERROR : "+(data_len-offset));
		if(len != 7)
			throw new Exception("YYYYMMDDHHMMSS LEN ERROR : "+len);
		
		int idx = offset;
		
		int year = DataFormat.hex2unsigned16(
				DataFormat.LSB2MSB(DataFormat.select(data, 0, 2)));
        idx =idx+2;
		int mm = DataFormat.hex2unsigned8(data[idx++]);
		int dd = DataFormat.hex2unsigned8(data[idx++]);
		int hh = DataFormat.hex2unsigned8(data[idx++]);
		int MM = DataFormat.hex2unsigned8(data[idx++]);
		int ss = DataFormat.hex2unsigned8(data[idx++]);

		StringBuffer ret = new StringBuffer();
				
		ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
		ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(ss),2));
		
		return ret.toString();
			
	}
}


