package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Response(LP 데이터)
 * @author kaze
 * 2009. 6. 19.
 */
public class FDataDump implements java.io.Serializable{
	private static Log log = LogFactory.getLog(FDataDump.class);

	private int mt;//Meter Type
	private String meterId;//Meter Id
	private long acon;//Active Constant(유효 전력량 계기 정수)
	private long rcon;//Reactive Constant(무효 전력량 계기 정수)
	private int dCnt;//Sub Dump Count
	private FDataSubDump[] fDataSubDump;//FData Sub Dump

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
	 * @return the acon(Active Constant(유효 전력량 계기 정수))
	 */
	public long getAcon() {
		return acon;
	}

	/**
	 * @param acon the acon to set
	 */
	public void setAcon(long acon) {
		this.acon = acon;
	}

	/**
	 * @return the rcon(Reactive Constant(무효 전력량 계기 정수))
	 */
	public long getRcon() {
		return rcon;
	}

	/**
	 * @param rcon the rcon to set
	 */
	public void setRcon(long rcon) {
		this.rcon = rcon;
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
	 * @return the fDataSubDump
	 */
	public FDataSubDump[] getFDataSubDump() {
		return fDataSubDump;
	}

	/**
	 * @param dataSubDump the fDataSubDump to set
	 */
	public void setFDataSubDump(FDataSubDump[] dataSubDump) {
		fDataSubDump = dataSubDump;
	}

	public FDataDump(byte[] rawData) throws Exception {
		try {
			int pos = 0;
			byte[] MT = new byte[1];//Meter Type
			byte[] MID = new byte[20];//Meter ID
			byte[] ACON = new byte[4];//Active Constant(유효 전력량 계기 정수)
			byte[] RCON = new byte[4];//Reactive Constant(무효 전력량 계기 정수)
			byte[] DCNT = new byte[2];//Sub dump Count
			byte[] SUBLPDUMP = new byte[PLCDataConstants.FDATA_SUB_DUMP_TOTAL_LEN];//Sub LP Dump

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MT);
			mt=DataUtil.getIntToBytes(MT);

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MID);
			meterId=DataUtil.getString(MID).trim();

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, ACON);
			acon=DataUtil.getLongToBytes(ACON);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, RCON);
			rcon=DataUtil.getLongToBytes(RCON);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, DCNT);
			dCnt=DataUtil.getIntToBytes(DCNT);

			//Check Length
			if(rawData.length!=(MT.length+MID.length+ACON.length+RCON.length+DCNT.length+(dCnt*PLCDataConstants.FDATA_SUB_DUMP_TOTAL_LEN))){
				throw new Exception("FData DUMP REAL LENGTH["+rawData.length+"] CORRECT LENGTH["+((MT.length+MID.length+ACON.length+RCON.length+DCNT.length+(dCnt*PLCDataConstants.FDATA_SUB_DUMP_TOTAL_LEN)))+"] IS INVALID!");
			}

			fDataSubDump=new FDataSubDump[dCnt];
			for(int i=0;i<dCnt;i++) {
				pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, SUBLPDUMP);
				fDataSubDump[i] = new FDataSubDump(SUBLPDUMP);
			}
		}catch (Exception e) {
			log.error("FDATA DUMP PARSING ERROR! - "+e.getMessage());
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

	    retValue.append("FDataDump ( ")
	        .append(super.toString()).append(TAB)
	        .append("mt = ").append(this.mt).append(TAB)
	        .append("meterId = ").append(this.meterId).append(TAB)
	        .append("acon = ").append(this.acon).append(TAB)
	        .append("rcon = ").append(this.rcon).append(TAB)
	        .append("dCnt = ").append(this.dCnt).append(TAB);
	        for(int i=0;i<dCnt;i++) {
	        	retValue.append("fDataSubDump = ").append(this.fDataSubDump[i]).append(TAB);
	        }
	        retValue.append(" )");

	    return retValue.toString();
	}
}
