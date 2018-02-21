package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Response(정전/복전 데이터)
 * @author kaze
 * 2009. 6. 19.
 */
public class GData extends PLCData {
	private static Log log = LogFactory.getLog(GData.class);
	private int dumpCnt;
	private GDataDump[] gDataDump;

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
	 * @return the gDataDump
	 */
	public GDataDump[] getGDataDump() {
		return gDataDump;
	}

	/**
	 * @param dataDump the gDataDump to set
	 */
	public void setGDataDump(GDataDump[] dataDump) {
		gDataDump = dataDump;
	}

	public GData(PLCDataFrame pdf) {
		super(pdf);
		try {
			byte[] rawData = pdf.getData();
			int pos = 0;
			byte[] COUNT = new byte[2];
			byte[] DATADUMP;

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, COUNT);
			dumpCnt=DataUtil.getIntToBytes(COUNT);
			gDataDump=new GDataDump[dumpCnt];
			for(int i=0;i<dumpCnt;i++) {
				byte[] DCNT = new byte[2];
				DataUtil.copyBytes(!PLCDataConstants.isConvert, pos+21, rawData, DCNT);
				int subDumpCnt=DataUtil.getIntToBytes(DCNT);

				DATADUMP = new byte[23+PLCDataConstants.GDATA_SUB_DUMP_TOTAL_LEN*subDumpCnt];
				pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, DATADUMP);
				gDataDump[i] = new GDataDump(DATADUMP);
			}
		}catch (Exception e) {
			log.error("GDATA PARSING ERROR! - "+e.getMessage());
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

	    retValue.append("GData ( ")
	        .append(super.toString()).append(TAB)
	        .append("dumpCnt = ").append(this.dumpCnt).append(TAB);
	    for(int i=0;i<dumpCnt;i++) {
	        retValue.append("gDataDump = ").append(this.gDataDump[i]).append(TAB);
	    }
	        retValue.append(" )");

	    return retValue.toString();
	}
}
