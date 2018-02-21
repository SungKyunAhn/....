package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Response(정전/복전 데이터)
 * @author kaze
 * 2009. 6. 19.
 */
public class GDataSubDump implements java.io.Serializable{
	private static Log log = LogFactory.getLog(GDataSubDump.class);

	private int type;//Data Type(0x01:정전, 0x02:복전)
	private String dateTime;//시간 정보(yyyymmddhhmmss)
	private String dateTimeWeek;//Week(monday:01~sunday:07)
	private int logCnt;//Log Count

	/**
	 * @return the type(1:정전, 2:복전))
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the dateTime(시간 정보(yyyymmddhhmmss))
	 */
	public String getDateTime() {
		return dateTime;
	}

	/**
	 * @param dateTime the dateTime to set
	 */
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	/**
	 * @return the dateTimeWeek(Week(monday:01~sunday:07))
	 */
	public String getDateTimeWeek() {
		return dateTimeWeek;
	}

	/**
	 * @param dateTimeWeek the dateTimeWeek to set
	 */
	public void setDateTimeWeek(String dateTimeWeek) {
		this.dateTimeWeek = dateTimeWeek;
	}

	/**
	 * @return the logCnt
	 */
	public int getLogCnt() {
		return logCnt;
	}

	/**
	 * @param logCnt the logCnt to set
	 */
	public void setLogCnt(int logCnt) {
		this.logCnt = logCnt;
	}

	public GDataSubDump(byte[] rawData) throws Exception {
		try {
			//Check Length
			if(rawData.length!=PLCDataConstants.GDATA_SUB_DUMP_TOTAL_LEN) {
				throw new Exception("GDataSubDump LENGTH["+rawData.length+"] IS INVALID!");
			}
			int pos = 0;
			byte[] DATATYPE = new byte[1];//Data Type(0x01:정전, 0x02:복전)
			byte[] DTIME = new byte[7];//시간 정보
			byte[] LOGCNT = new byte[2];//Log Count

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, DATATYPE);
			type=DataUtil.getIntToBytes(DATATYPE);

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, DTIME);
			dateTime=DataUtil.getPLCDate(DTIME).trim();
			dateTimeWeek=DataUtil.getPLCWeek(DTIME).trim();

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, LOGCNT);
			logCnt=DataUtil.getIntToBytes(LOGCNT);

		}catch (Exception e) {
			log.error("FDATA SUB DUMP PARSING ERROR! - "+e.getMessage());
			e.printStackTrace();
		}
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

	    retValue.append("GDataSubDump ( ")
	        .append(super.toString()).append(TAB)
	        .append("type = ").append(this.type).append(TAB)
	        .append("dateTime = ").append(this.dateTime).append(TAB)
	        .append("dateTimeWeek = ").append(this.dateTimeWeek).append(TAB)
	        .append("logCnt = ").append(this.logCnt).append(TAB)
	        .append(" )");

	    return retValue.toString();
	}
}
