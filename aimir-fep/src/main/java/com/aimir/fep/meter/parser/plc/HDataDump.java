package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Response(통신 상황 정보)
 * @author kaze
 * 2009. 6. 19.
 */
public class HDataDump implements java.io.Serializable{
	private static Log log = LogFactory.getLog(HDataDump.class);

	private int mt;
	private String meterId;
	private int dataCnt;//Sub Dump Count
	private HDataSubDump[] hDataSubDump;//HData Sub Dump

	/**
	 * @return the mt
	 */
	public int getMt() {
		return mt;
	}

	/**
	 * @param mt the mt to set
	 */
	public void setMt(int mt) {
		this.mt = mt;
	}

	/**
	 * @return the mId
	 */
	public String getMeterId() {
		return meterId;
	}

	/**
	 * @param mId the mId to set
	 */
	public void setMeterId(String mId) {
		this.meterId = mId;
	}
	/**
	 * @return the dataCnt
	 */
	public int getDataCnt() {
		return dataCnt;
	}

	/**
	 * @param dataCnt the dataCnt to set
	 */
	public void setDataCnt(int dataCnt) {
		this.dataCnt = dataCnt;
	}

	/**
	 * @return the hDataSubDump
	 */
	public HDataSubDump[] getHDataSubDump() {
		return hDataSubDump;
	}

	/**
	 * @param dataSubDump the hDataSubDump to set
	 */
	public void setHDataSubDump(HDataSubDump[] dataSubDump) {
		hDataSubDump = dataSubDump;
	}

	public HDataDump(byte[] rawData) throws Exception {
		try {
			int pos = 0;
			byte[] MT = new byte[1];//Meter Type
			byte[] MID = new byte[20];//Meter ID
			byte[] DATACNT = new byte[2];//Sub dump Count
			byte[] SUBDUMP = new byte[PLCDataConstants.HDATA_SUB_DUMP_TOTAL_LEN];//Sub Dump

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MT);
			mt=DataUtil.getIntToBytes(MT);

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MID);
			meterId=DataUtil.getString(MID).trim();

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, DATACNT);
			dataCnt=DataUtil.getIntToBytes(DATACNT);

			//Check Length
			if(rawData.length!=(MT.length+MID.length+DATACNT.length+dataCnt*PLCDataConstants.HDATA_SUB_DUMP_TOTAL_LEN)){
				throw new Exception("HData DUMP LENGTH["+rawData.length+"] IS INVALID!, CORRECT LENGTH["+(DATACNT.length+dataCnt*PLCDataConstants.HDATA_SUB_DUMP_TOTAL_LEN)+"]");
			}

			hDataSubDump=new HDataSubDump[dataCnt];
			for(int i=0;i<dataCnt;i++) {
				pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, SUBDUMP);
				hDataSubDump[i] = new HDataSubDump(SUBDUMP);
			}
		}catch (Exception e) {
			log.error("HDATA DUMP PARSING ERROR! - "+e.getMessage());
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

	    retValue.append("HDataDump ( ")
	        .append(super.toString()).append(TAB)
	        .append("mt = ").append(this.mt).append(TAB)
	    	.append("mId = ").append(this.meterId).append(TAB)
	        .append("dataCnt = ").append(this.dataCnt).append(TAB);
	    for(int i=0;i<dataCnt;i++) {
	        retValue.append("hDataSubDump = ").append(this.hDataSubDump[i]).append(TAB);
	    }
	        retValue.append(" )");

	    return retValue.toString();
	}
}
