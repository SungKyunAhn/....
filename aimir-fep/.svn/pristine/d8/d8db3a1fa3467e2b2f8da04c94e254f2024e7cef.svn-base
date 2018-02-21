package com.aimir.fep.meter.parser.amuKepco_2_5_0Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * KEPCO v2.5.0 Error Data Field
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 12. 오후 4:12:02$
 */
public class KEPCO_2_5_0_ERROR {

	private static Log log = LogFactory.getLog(KEPCO_2_5_0_ERROR.class);
	
	private byte[] rawData;
	
	private static final int OFS_ERROR_CODE 		= 0;
	private static final int LEN_ERROR_CODE 		= 2;
	/**
	 * Constructor
	 */
	public KEPCO_2_5_0_ERROR(byte[] rawData) {
		this.rawData = rawData;
	}	
	
	
	/**
	 * get Error Code
	 * @return
	 * @throws Exception
	 */
	public int getErrorCode() throws Exception {        
		
		int ret = DataFormat.hex2unsigned16(
	        		DataFormat.select(rawData, OFS_ERROR_CODE, LEN_ERROR_CODE));
		log.debug("Error Code : " + ret);
	    return ret;
	}
	
	/**
	 * get System Status 
	 * @return
	 * @throws Exception
	 */
	public SystemStatus getSystemStatus() throws Exception {        
		
		return new SystemStatus(DataFormat.select(rawData, OFS_ERROR_CODE, LEN_ERROR_CODE));
	}
}


