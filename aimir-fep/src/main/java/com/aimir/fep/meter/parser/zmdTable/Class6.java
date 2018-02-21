/*
 * Created on 2004. 12. 27.
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.aimir.fep.meter.parser.zmdTable;

import com.aimir.fep.util.DataFormat;

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 *
 * LandisGyr+ ZMD4 meter Class. <p>
 * Include METER DATE AND TIME. <p>
 */
public class Class6 {
	
	public final static int OFS_DATETIME = 16;
	public final static int LEN_DATETIME = 10;
	public final static int LEN_YEAR     = 2;
	public final static int LEN_MONTH    = 1;
	public final static int LEN_DAY      = 1;
	public final static int LEN_WEEK     = 1;
	public final static int LEN_HOUR     = 1;
	public final static int LEN_MIN      = 1;
	public final static int LEN_SEC      = 1;
	
	private byte[] data;
	
	/**
	 * Constructor. <p>
	 * @param data
	 */
	public Class6(byte[] data){
		this.data = data;	
	}


	public byte[] parseDatetime() throws Exception {
		return DataFormat.select(data,OFS_DATETIME,LEN_DATETIME);
	}
	
	
	/**
	 * Get Date and Time.<p>
	 * exclude 
	 * @return
	 */
	public byte[] parseDateTime(){
		
		byte[] datetime = new byte[7];

		datetime[0] = data[OFS_DATETIME];
		datetime[1] = data[OFS_DATETIME+1];
		datetime[2] = data[OFS_DATETIME+LEN_YEAR];
		datetime[3] = data[OFS_DATETIME+LEN_YEAR+LEN_MONTH];
		datetime[4] = data[OFS_DATETIME+LEN_YEAR+LEN_MONTH+LEN_DAY+LEN_WEEK];
		datetime[5] = data[OFS_DATETIME+LEN_YEAR+LEN_MONTH+LEN_DAY+LEN_WEEK+LEN_HOUR];
		datetime[6] = data[OFS_DATETIME+LEN_YEAR+LEN_MONTH+LEN_DAY+LEN_WEEK+LEN_HOUR+LEN_MIN];
		
		return datetime;
	}
}
