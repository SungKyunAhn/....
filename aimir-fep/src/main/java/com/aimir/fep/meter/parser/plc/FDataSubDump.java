package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Response(LP 데이터)
 * @author kaze
 * 2009. 6. 19.
 */
public class FDataSubDump implements java.io.Serializable{
	private static Log log = LogFactory.getLog(FDataSubDump.class);

	private String iTime;//IRM 시간
	private String iWeek;//IRM Week
	private String cTime;//Capture 시간
	private String cWeek;//Capture Week
	private Long fap;//피상 전력 LP
	private Long larap;//지상 무효 전력 LP
	private Long lerap;//진상 무효 전력 LP
	private Long ap;//순방향 무효 전력 LP
	private Integer status;//상태 정보

	/**
	 * @return the iTime(IRM 시간)
	 */
	public String getITime() {
		return iTime;
	}

	/**
	 * @param time the iTime to set
	 */
	public void setITime(String time) {
		iTime = time;
	}

	/**
	 * @return the iWeek(IRM Week)
	 */
	public String getIWeek() {
		return iWeek;
	}

	/**
	 * @param week the iWeek to set
	 */
	public void setIWeek(String week) {
		iWeek = week;
	}

	/**
	 * @return the cTime(Capture 시간)
	 */
	public String getCTime() {
		return cTime;
	}

	/**
	 * @param time the cTime to set
	 */
	public void setCTime(String time) {
		cTime = time;
	}

	/**
	 * @return the cWeek(Capture Week)
	 */
	public String getCWeek() {
		return cWeek;
	}

	/**
	 * @param week the cWeek to set
	 */
	public void setCWeek(String week) {
		cWeek = week;
	}

	/**
	 * @return the fap(피상 전력 LP)
	 */
	public Long getFap() {
		return fap;
	}

	/**
	 * @param fap the fap to set
	 */
	public void setFap(Long fap) {
		this.fap = fap;
	}

	/**
	 * @return the larap(지상 무효 전력 LP)
	 */
	public Long getLarap() {
		return larap;
	}

	/**
	 * @param larap the larap to set
	 */
	public void setLarap(Long larap) {
		this.larap = larap;
	}

	/**
	 * @return the lerap(진상 무효 전력 LP)
	 */
	public Long getLerap() {
		return lerap;
	}

	/**
	 * @param lerap the lerap to set
	 */
	public void setLerap(Long lerap) {
		this.lerap = lerap;
	}

	/**
	 * @return the ap(순방향 무효 전력 LP)
	 */
	public Long getAp() {
		return ap;
	}

	/**
	 * @param ap the ap to set
	 */
	public void setAp(Long ap) {
		this.ap = ap;
	}

	/**
	 * @return the status(상태 정보)
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	public FDataSubDump(byte[] rawData) throws Exception {
		try {
			//Check Length
			if(rawData.length!=PLCDataConstants.FDATA_SUB_DUMP_TOTAL_LEN) {
				throw new Exception("FDataSubDump LENGTH["+rawData.length+"] IS INVALID!");
			}
			int pos = 0;
			byte[] ITIME = new byte[7];//IRM 시간
			byte[] CTIME = new byte[7];//Capture 시간
			byte[] FAP = new byte[4];//피상 전력 LP
			byte[] LARAP = new byte[4];//지상 무효 전력 LP
			byte[] LERAP = new byte[4];//진상 무효 전력 LP
			byte[] AP = new byte[4];//순방향 무효 전력 LP
			byte[] STATUS = new byte[1];//상태 정보

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, ITIME);
			iTime=DataUtil.getPLCDate(ITIME).trim();
			iWeek=DataUtil.getPLCWeek(ITIME).trim();

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, CTIME);
			cTime=DataUtil.getPLCDate(CTIME).trim();
			cWeek=DataUtil.getPLCWeek(CTIME).trim();

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, FAP);
			fap=DataUtil.getLongToBytes(FAP);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, LARAP);
			larap=DataUtil.getLongToBytes(LARAP);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, LERAP);
			lerap=DataUtil.getLongToBytes(LERAP);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, AP);
			ap=DataUtil.getLongToBytes(AP);

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, STATUS);
			status=DataUtil.getIntToBytes(LERAP);
			log.debug("iTime["+iTime+"], cTime["+cTime+"], fap["+fap+"], larap["+larap+"], lerap["+lerap+"], ap["+ap+"], status["+status+"]");
		}catch (Exception e) {
			log.error("FDATA SUB DUMP PARSING ERROR! - "+e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 *
	 */
	public FDataSubDump() {
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

	    retValue.append("FDataSubDump ( ")
	        .append(super.toString()).append(TAB)
	        .append("iTime = ").append(this.iTime).append(TAB)
	        .append("iWeek = ").append(this.iWeek).append(TAB)
	        .append("cTime = ").append(this.cTime).append(TAB)
	        .append("cWeek = ").append(this.cWeek).append(TAB)
	        .append("fap = ").append(this.fap).append(TAB)
	        .append("larap = ").append(this.larap).append(TAB)
	        .append("lerap = ").append(this.lerap).append(TAB)
	        .append("ap = ").append(this.ap).append(TAB)
	        .append("status = ").append(this.status).append(TAB)
	        .append(" )");

	    return retValue.toString();
	}
}
