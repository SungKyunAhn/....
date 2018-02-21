package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Response(통신 상황 정보)
 * @author kaze
 * 2009. 6. 19.
 */
public class HData extends PLCData {
	private static Log log = LogFactory.getLog(HData.class);
	private int dumpCnt;
	private HDataDump[] hDataDump;

	/**
	 * @return the dumpCnt(Dump Data Count)
	 */
	public int getDumpCnt() {
		return dumpCnt;
	}

	/**
	 * @param dumpCnt the dumpCnt to set
	 */
	public void setDumpCnt(int dumpCnt) {
		this.dumpCnt = dumpCnt;
	}

	/**
	 * @return the hDataDump
	 */
	public HDataDump[] getHDataDump() {
		return hDataDump;
	}

	/**
	 * @param dataDump the hDataDump to set
	 */
	public void setHDataDump(HDataDump[] dataDump) {
		hDataDump = dataDump;
	}

	public HData(PLCDataFrame pdf) {
		super(pdf);
		try {
			byte[] rawData = pdf.getData();
			int pos = 0;
			byte[] COUNT = new byte[2];
			byte[] DATADUMP;

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, COUNT);
			dumpCnt=DataUtil.getIntToBytes(COUNT);
			hDataDump=new HDataDump[dumpCnt];
			for(int i=0;i<dumpCnt;i++) {
				byte[] DCNT = new byte[2];
				DataUtil.copyBytes(!PLCDataConstants.isConvert, pos+21, rawData, DCNT);
				int subDumpCnt=DataUtil.getIntToBytes(DCNT);

				DATADUMP = new byte[23+PLCDataConstants.HDATA_SUB_DUMP_TOTAL_LEN*subDumpCnt];
				pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, DATADUMP);
				hDataDump[i] = new HDataDump(DATADUMP);
			}
		}catch (Exception e) {
			log.error("HDATA PARSING ERROR! - "+e.getMessage());
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

	    retValue.append("HData ( ")
	        .append(super.toString()).append(TAB)
	        .append("dumpCnt = ").append(this.dumpCnt).append(TAB);
	    for(int i=0;i<dumpCnt;i++) {
	        retValue.append("hDataDump = ").append(this.hDataDump[i]).append(TAB);
	    }
	        retValue.append(" )");

	    return retValue.toString();
	}
}
