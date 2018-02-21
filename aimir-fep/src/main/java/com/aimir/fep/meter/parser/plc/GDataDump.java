package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Response(정전/복전 데이터)
 * @author kaze
 * 2009. 6. 19.
 */
public class GDataDump implements java.io.Serializable{
	private static Log log = LogFactory.getLog(GDataDump.class);

	private int mt;//Meter Type
	private String meterId;//Meter Id
	private int dCnt;//Sub Dump Count
	private GDataSubDump[] gDataSubDump;//GData Sub Dump

	/**
	 * @return the mt(Meter Type)
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
	 * @return the meterId
	 */
	public String getMeterId() {
		return meterId;
	}

	/**
	 * @param meterId the meterId to set
	 */
	public void setMeterId(String meterId) {
		this.meterId = meterId;
	}

	/**
	 * @return the dCnt(Sub Dump Data count)
	 */
	public int getDCnt() {
		return dCnt;
	}

	/**
	 * @param cnt the dCnt to set
	 */
	public void setDCnt(int cnt) {
		dCnt = cnt;
	}

	/**
	 * @return the gDataSubDump
	 */
	public GDataSubDump[] getGDataSubDump() {
		return gDataSubDump;
	}

	/**
	 * @param dataSubDump the gDataSubDump to set
	 */
	public void setGDataSubDump(GDataSubDump[] dataSubDump) {
		gDataSubDump = dataSubDump;
	}

	public GDataDump(byte[] rawData) throws Exception {
		try {
			int pos = 0;
			byte[] MT = new byte[1];//Meter Type
			byte[] MID = new byte[20];//Meter ID
			byte[] DCNT = new byte[2];//Sub dump Count
			byte[] SUBDUMP = new byte[PLCDataConstants.GDATA_SUB_DUMP_TOTAL_LEN];//Sub Dump

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MT);
			mt=DataUtil.getIntToBytes(MT);

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MID);
			meterId=DataUtil.getString(MID).trim();

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, DCNT);
			dCnt=DataUtil.getIntToBytes(DCNT);

			//Check Length
			if(rawData.length!=(MT.length+MID.length+DCNT.length+dCnt*PLCDataConstants.GDATA_SUB_DUMP_TOTAL_LEN)){
				throw new Exception("GData DUMP LENGTH["+rawData.length+"] IS INVALID!");
			}

			gDataSubDump=new GDataSubDump[dCnt];
			for(int i=0;i<dCnt;i++) {
				pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, SUBDUMP);
				gDataSubDump[i] = new GDataSubDump(SUBDUMP);
			}
		}catch (Exception e) {
			log.error("GDATA DUMP PARSING ERROR! - "+e.getMessage());
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

	    retValue.append("GDataDump ( ")
	        .append(super.toString()).append(TAB)
	        .append("mt = ").append(this.mt).append(TAB)
	        .append("meterId = ").append(this.meterId).append(TAB)
	        .append("dCnt = ").append(this.dCnt).append(TAB);
	    for(int i=0;i<dCnt;i++) {
	        retValue.append("gDataSubDump = ").append(this.gDataSubDump[i]).append(TAB);
	    }
	    retValue.append(" )");

	    return retValue.toString();
	}
}
