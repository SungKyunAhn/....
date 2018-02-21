package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Trap Info.(PLC 통신 오류 Trap)
 * @author kaze
 * 2009. 6. 19.
 */
public class KData extends PLCData {
	private static Log log = LogFactory.getLog(KData.class);
	private int errorCode;
	private String errCodeStr="Unknown";

	/**
	 * @return the errorCode
	 */
	public int getErrorCode() {
		return errorCode;
	}


	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the errCodeStr
	 */
	public String getErrCodeStr() {
		return errCodeStr;
	}


	/**
	 * @param errCodeStr the errCodeStr to set
	 */
	public void setErrCodeStr(String errCodeStr) {
		this.errCodeStr = errCodeStr;
	}


	public KData(PLCDataFrame pdf) {
		super(pdf);
		try {
			byte[] rawData = pdf.getData();
			//Check Length
			if(rawData.length!=PLCDataConstants.KDATA_TOTAL_LEN){
				throw new Exception("KData LENGTH["+rawData.length+"] IS INVALID!");
			}
			byte[] ERRORCODE = new byte[1];
			int pos = 0;
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, ERRORCODE);
			errorCode=DataUtil.getIntToBytes(ERRORCODE);
			if(errorCode==0x01) {
				errCodeStr="PMU MAC ID Invalid";
			}else if(errorCode==0x02) {
				errCodeStr="IRM->PMU CRC Error";
			}else if(errorCode==0x03) {
				errCodeStr="IMR->PMU Invalid Packet Error";
			}else if(errorCode==0x04) {
				errCodeStr="PSU Response Time Out";
			}
		}catch (Exception e) {
			log.error("KDATA PARSING ERROR! - "+e.getMessage());
			e.printStackTrace();
		}
	}

    /* (non-Javadoc)
	 * @see nuri.aimir.moa.protocol.fmp.frame.plc.PLCData#getServiceType()
	 */
	@Override
	public Integer getServiceType() {
		return new Integer(4);
	}


	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation
	 * of this object.
	 */
	public String toString()
	{
	    final String TAB = "\n";

	    StringBuffer retValue = new StringBuffer();

	    retValue.append("KData ( ")
	        .append(super.toString()).append(TAB)
	        .append("errorCode = ").append(this.errorCode).append(TAB)
	        .append("errCodeStr = ").append(this.errCodeStr).append(TAB)
	        .append(" )");

	    return retValue.toString();
	}
}
