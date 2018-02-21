package com.aimir.fep.meter.parser.amuKepco_dlmsTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * KEPCO DLMS LP Time
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 20. 오후 2:26:28$
 */
public class MeterLastLPDateTime {
	
	private Log log = LogFactory.getLog(MeterLastLPDateTime.class);
	
	public static final int OFS_LP_TIME 			= 0;
	public static final int LEN_LP_TIME 			= 6;
	
	private byte[] data;
   
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MeterLastLPDateTime(byte[] data) {
		this.data = data;
	}
	
	/**
	 * get Interval Time
	 * @return
	 * @throws Exception
	 */
	public String getLpDateTime() throws Exception {
		
		String yymdhm ="";
		try {
			yymdhm = getYymmddhhmm(data,OFS_LP_TIME,LEN_LP_TIME);
		}catch (Exception e) {
			log.warn("LP DATE TIME Format Error");
		}	 
		return yymdhm;
	}

	/**
	 * get Time(YY+M+D+H+M)
	 * @param data
	 * @param offset
	 * @param len
	 * @return
	 * @throws Exception
	 */
	private String getYymmddhhmm(byte[] data, int offset, int len)
	throws Exception  {
		 
		 int data_len = data.length;
		if(data_len-offset < 6)
			throw new Exception("YYMDHM FORMAT ERROR : "+(data_len-offset));
		if(len != 6)
			throw new Exception("YYMDHM LEN ERROR : "+len);
		
		int idx = offset;
		
		int year = DataFormat.hex2unsigned16(DataFormat.LSB2MSB
							(DataFormat.select(data, 0, 2)));
        idx = idx+2;
		int mm = DataFormat.hex2unsigned8(data[idx++]);
		int dd = DataFormat.hex2unsigned8(data[idx++]);
		int hh = DataFormat.hex2unsigned8(data[idx++]);
		int MM = DataFormat.hex2unsigned8(data[idx++]);
		
		StringBuffer ret = new StringBuffer();
				
		ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
		ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));

		return ret.toString();
	 }
}


