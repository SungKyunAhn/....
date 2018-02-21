package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 변압기 감시 Configuration data Response
 * @author kaze
 * 2009. 6. 19.
 */
public class MData extends PLCData {
	private static Log log = LogFactory.getLog(MData.class);
	private String tId;//변압기 전산화 번호
	private int capacity;//상별 용량(kVA)
	private int overVol;//과전압 임계치
	private int lowVol;//저전압 임계치
	private int overLoad;//과부하 단위
	private int volUnit;//전압 경보 단위
	private int period;//측정 주기(분)

	/**
	 * @return the tId(변압기 전산화 번호)
	 */
	public String getTId() {
		return tId;
	}

	/**
	 * @param id the tId to set
	 */
	public void setTId(String id) {
		tId = id;
	}

	/**
	 * @return the capacity(상병 용량(kVA))
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * @param capacity the capacity to set
	 */
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	/**
	 * @return the overVol(과전압 임계치)
	 */
	public int getOverVol() {
		return overVol;
	}

	/**
	 * @param overVol the overVol to set
	 */
	public void setOverVol(int overVol) {
		this.overVol = overVol;
	}

	/**
	 * @return the lowVol(저전압 임계치)
	 */
	public int getLowVol() {
		return lowVol;
	}

	/**
	 * @param lowVol the lowVol to set
	 */
	public void setLowVol(int lowVol) {
		this.lowVol = lowVol;
	}

	/**
	 * @return the overLoad(과부하 단위)
	 */
	public int getOverLoad() {
		return overLoad;
	}

	/**
	 * @param overLoad the overLoad to set
	 */
	public void setOverLoad(int overLoad) {
		this.overLoad = overLoad;
	}

	/**
	 * @return the volUnit(전압 경보 단위)
	 */
	public int getVolUnit() {
		return volUnit;
	}

	/**
	 * @param volUnit the volUnit to set
	 */
	public void setVolUnit(int volUnit) {
		this.volUnit = volUnit;
	}

	/**
	 * @return the period(측정 주기(분))
	 */
	public int getPeriod() {
		return period;
	}

	/**
	 * @param period the period to set
	 */
	public void setPeriod(int period) {
		this.period = period;
	}

	public MData(PLCDataFrame pdf) {
		super(pdf);
		try {
			byte[] rawData = pdf.getData();
			//Check Length
			if(rawData.length!=PLCDataConstants.MDATA_TOTAL_LEN){
				throw new Exception("MData LENGTH["+rawData.length+"] IS INVALID!");
			}
			int pos = 0;
			byte[] TID = new byte[9];
			byte[] CAPACITY = new byte[2];
			byte[] OVERVOL = new byte[2];
			byte[] LOWVOL = new byte[2];
			byte[] OVERLOAD = new byte[1];
			byte[] VOLUNIT = new byte[1];
			byte[] PERIOD = new byte[1];

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, TID);
			tId=DataUtil.getString(TID).trim();
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, CAPACITY);
			capacity=DataUtil.getIntToBytes(CAPACITY);
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, OVERVOL);
			overVol=DataUtil.getIntToBytes(OVERVOL);
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, LOWVOL);
			lowVol=DataUtil.getIntToBytes(LOWVOL);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, OVERLOAD);
			overLoad=DataUtil.getIntToBytes(OVERLOAD);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, VOLUNIT);
			volUnit=DataUtil.getIntToBytes(VOLUNIT);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, PERIOD);
			period=DataUtil.getIntToBytes(PERIOD);
		}catch (Exception e) {
			log.error("MDATA PARSING ERROR! - "+e.getMessage());
			e.printStackTrace();
		}
	}

    /* (non-Javadoc)
	 * @see nuri.aimir.moa.protocol.fmp.frame.plc.PLCData#getServiceType()
	 */
	@Override
	public Integer getServiceType() {
		return new Integer(3);
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

	    retValue.append("MData ( ")
	        .append(super.toString()).append(TAB)
	        .append("tId = ").append(this.tId).append(TAB)
	        .append("capacity = ").append(this.capacity).append(TAB)
	        .append("overVol = ").append(this.overVol).append(TAB)
	        .append("lowVol = ").append(this.lowVol).append(TAB)
	        .append("overLoad = ").append(this.overLoad).append(TAB)
	        .append("volUnit = ").append(this.volUnit).append(TAB)
	        .append("period = ").append(this.period).append(TAB)
	        .append(" )");

	    return retValue.toString();
	}
}
