package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Meter Status Response/Meter Trap
 * @author kaze
 * 2009. 6. 19.
 */
public class IData extends PLCData {
	private static Log log = LogFactory.getLog(IData.class);
	private int dumpCnt;
	private IDataDump[] iDataDump;

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
	 * @return the iDataDump
	 */
	public IDataDump[] getIDataDump() {
		return iDataDump;
	}

	/**
	 * @param dataDump the iDataDump to set
	 */
	public void setIDataDump(IDataDump[] dataDump) {
		iDataDump = dataDump;
	}

	public IData(PLCDataFrame pdf) {
		super(pdf);
		try {
			byte[] rawData=pdf.getData();
			int pos = 0;
			byte[] COUNT = new byte[2];
			byte[] DATADUMP = new byte[PLCDataConstants.IDATA_DUMP_TOTAL_LEN];
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, COUNT);
			dumpCnt=DataUtil.getIntToBytes(COUNT);
			//Check Length
			if(rawData.length!=(COUNT.length+dumpCnt*PLCDataConstants.IDATA_DUMP_TOTAL_LEN)){
				throw new Exception("IData LENGTH["+rawData.length+"] IS INVALID, CORRECT LENGTH["+(COUNT.length+dumpCnt*PLCDataConstants.IDATA_DUMP_TOTAL_LEN)+"]!");
			}
			iDataDump=new IDataDump[dumpCnt];
			for(int i=0;i<dumpCnt;i++) {
				pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, DATADUMP);
				iDataDump[i] = new IDataDump(DATADUMP);
			}
		}catch (Exception e) {
			log.error("IDATA PARSING ERROR! - "+e.getMessage());
			log.error(e,e);
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

	    retValue.append("IData ( ")
	        .append(super.toString()).append(TAB)
	        .append("dumpCnt = ").append(this.dumpCnt).append(TAB);
	    for(int i=0;i<dumpCnt;i++) {
	        retValue.append("iDataDump = ").append(this.iDataDump[i]).append(TAB);
	    }
	        retValue.append(" )");

	    return retValue.toString();
	}
}
