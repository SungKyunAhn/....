package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Response(통신 상황 정보)
 * @author kaze
 * 2009. 6. 19.
 */
public class HDataSubDump implements java.io.Serializable{
	private static Log log = LogFactory.getLog(GDataSubDump.class);

	private String dTime;//시간 정보(yyyymmddhhmmss)
	private String dWeek;//Week(monday:01~sunday:07)
	private int ptx;//packet Transmission
	private int prx;//packet Receive
	private int mtx;//Meter Transmission
	private int mrx;//Meter Receive

	/**
	 * @return the dTime(yyyymmddhhmmss)
	 */
	public String getDTime() {
		return dTime;
	}

	/**
	 * @param time the dTime to set
	 */
	public void setDTime(String time) {
		dTime = time;
	}

	/**
	 * @return the dWeek(monday:01~sunday:07)
	 */
	public String getDWeek() {
		return dWeek;
	}

	/**
	 * @param week the dWeek to set
	 */
	public void setDWeek(String week) {
		dWeek = week;
	}

	/**
	 * @return the ptx(packet transmission)
	 */
	public int getPtx() {
		return ptx;
	}

	/**
	 * @param ptx the ptx to set
	 */
	public void setPtx(int ptx) {
		this.ptx = ptx;
	}

	/**
	 * @return the prx(packet receive)
	 */
	public int getPrx() {
		return prx;
	}

	/**
	 * @param prx the prx to set
	 */
	public void setPrx(int prx) {
		this.prx = prx;
	}

	/**
	 * @return the mtx(meter transmission)
	 */
	public int getMtx() {
		return mtx;
	}

	/**
	 * @param mtx the mtx to set
	 */
	public void setMtx(int mtx) {
		this.mtx = mtx;
	}

	/**
	 * @return the mrx(Meter Receive)
	 */
	public int getMrx() {
		return mrx;
	}

	/**
	 * @param mrx the mrx to set
	 */
	public void setMrx(int mrx) {
		this.mrx = mrx;
	}

	public HDataSubDump(byte[] rawData) throws Exception {
		try {
			//Check Length
			if(rawData.length!=PLCDataConstants.HDATA_SUB_DUMP_TOTAL_LEN) {
				throw new Exception("HDataSubDump LENGTH["+rawData.length+"] IS INVALID!");
			}
			int pos = 0;
			byte[] DTIME = new byte[7];//시간 정보
			byte[] PTX = new byte[2];//Packet Transmission
			byte[] PRX = new byte[2];//Packet Receive
			byte[] MTX = new byte[2];//Meter Transmission
			byte[] MRX = new byte[2];//Meter Receive

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, DTIME);
			dTime=DataUtil.getPLCDate(DTIME).trim();
			dWeek=DataUtil.getPLCWeek(DTIME).trim();

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, PTX);
			prx=DataUtil.getIntToBytes(PTX);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, PRX);
			prx=DataUtil.getIntToBytes(PRX);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, MTX);
			mtx=DataUtil.getIntToBytes(MTX);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, MRX);
			mrx=DataUtil.getIntToBytes(MRX);

		}catch (Exception e) {
			log.error("HDATA SUB DUMP PARSING ERROR! - "+e.getMessage());
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

	    retValue.append("HDataSubDump ( ")
	        .append(super.toString()).append(TAB)
	        .append("dTime = ").append(this.dTime).append(TAB)
	        .append("dWeek = ").append(this.dWeek).append(TAB)
	        .append("ptx = ").append(this.ptx).append(TAB)
	        .append("prx = ").append(this.prx).append(TAB)
	        .append("mtx = ").append(this.mtx).append(TAB)
	        .append("mrx = ").append(this.mrx).append(TAB)
	        .append(" )");

	    return retValue.toString();
	}
}
