package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Response(검침 데이터)
 * @author kaze
 * 2009. 6. 19.
 */
public class DData extends PLCData {
	private static Log log = LogFactory.getLog(DData.class);

	private int dumpCnt;
	private DDataDump[] dDataDump;

	/**
	 * @return the dumpCnt
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
	 * @return the dDataDump(검침 데이터)
	 */
	public DDataDump[] getDDataDump() {
		return dDataDump;
	}

	/**
	 * @param dataDump the dDataDump to set
	 */
	public void setDDataDump(DDataDump[] dataDump) {
		dDataDump = dataDump;
	}

	public DData(PLCDataFrame pdf) {
		super(pdf);
		try {
			byte[] rawData = pdf.getData();
			int pos = 0;
			byte[] COUNT = new byte[2];
			byte[] DATADUMP = new byte[PLCDataConstants.DDATA_DUMP_TOTAL_LEN];
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, COUNT);
			dumpCnt=DataUtil.getIntToBytes(COUNT);
			//Check Length
			if(rawData.length!=(COUNT.length+dumpCnt*PLCDataConstants.DDATA_DUMP_TOTAL_LEN)){
				throw new Exception("DData LENGTH["+rawData.length+"] IS INVALID!");
			}
			dDataDump=new DDataDump[dumpCnt];
			for(int i=0;i<dumpCnt;i++) {
				pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, DATADUMP);
				dDataDump[i] = new DDataDump(DATADUMP);
			}
		}catch (Exception e) {
			log.error("DDATA PARSING ERROR! - "+e.getMessage());
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

	    retValue.append("DData ( ")
	        .append(super.toString()).append(TAB)
	        .append("dumpCnt = ").append(this.dumpCnt).append(TAB);
	        for(int i=0;i<dumpCnt;i++) {
	        	retValue.append("dDataDump = ").append(this.dDataDump[i]).append(TAB);
	        }
	        retValue.append(" )");

	    return retValue.toString();
	}
}
