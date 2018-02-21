package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Response(LP 데이터)
 * @author kaze
 * 2009. 6. 19.
 */
public class FData extends PLCData {
	private static Log log = LogFactory.getLog(FData.class);
	private int dumpCnt;
	private FDataDump[] fDataDump;

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
	 * @return the fDataDump
	 */
	public FDataDump[] getFDataDump() {
		return fDataDump;
	}

	/**
	 * @param dataDump the fDataDump to set
	 */
	public void setFDataDump(FDataDump[] dataDump) {
		fDataDump = dataDump;
	}

	public FData(PLCDataFrame pdf) {
		super(pdf);
		try {
			byte[] rawData = pdf.getData();
			int pos = 0;
			byte[] COUNT = new byte[2];
			byte[] DATADUMP;

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, COUNT);
			dumpCnt=DataUtil.getIntToBytes(COUNT);
			//log.debug("FData DumpCnt["+dumpCnt+"]");
			fDataDump=new FDataDump[dumpCnt];
			for(int i=0;i<dumpCnt;i++) {
				byte[] DCNT = new byte[2];
				DataUtil.copyBytes(!PLCDataConstants.isConvert, pos+29, rawData, DCNT);
				//log.debug("FData Dump["+i+"] SubDumpCnt["+DataUtil.getIntToBytes(DCNT)+"]");
				int subDumpCnt=DataUtil.getIntToBytes(DCNT);

				DATADUMP = new byte[31+PLCDataConstants.FDATA_SUB_DUMP_TOTAL_LEN*subDumpCnt];
				pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, DATADUMP);
				fDataDump[i] = new FDataDump(DATADUMP);
			}
		}catch (Exception e) {
			log.error("FDATA PARSING ERROR! - "+e.getMessage());
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

	    retValue.append("FData ( ")
	        .append(super.toString()).append(TAB)
	        .append("dumpCnt = ").append(this.dumpCnt).append(TAB);
	        for(int i=0;i<dumpCnt;i++) {
	        	retValue.append("fDataDump = ").append(this.fDataDump[i]).append(TAB);
	        }
	        retValue.append(" )");

	    return retValue.toString();
	}
}
