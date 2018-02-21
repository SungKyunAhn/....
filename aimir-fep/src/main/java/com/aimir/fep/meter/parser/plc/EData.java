package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Response(최대 수요 데이터)
 * @author kaze
 * 2009. 6. 19.
 */
public class EData extends PLCData {
	private static Log log = LogFactory.getLog(EData.class);
	private int dumpCnt;
	private EDataDump[] eDataDump;

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
	 * @return the eDataDump(최대 수요 데이터)
	 */
	public EDataDump[] getEDataDump() {
		return eDataDump;
	}

	/**
	 * @param dataDump the eDataDump to set
	 */
	public void setEDataDump(EDataDump[] dataDump) {
		eDataDump = dataDump;
	}

	public EData(PLCDataFrame pdf) {
		super(pdf);
		try {
			byte[] rawData = pdf.getData();
			int pos = 0;
			byte[] COUNT = new byte[2];
			byte[] DATADUMP = new byte[PLCDataConstants.EDATA_DUMP_TOTAL_LEN];
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, COUNT);
			dumpCnt=DataUtil.getIntToBytes(COUNT);
			log.debug("dumpCnt: "+dumpCnt);
			//Check Length
			if(rawData.length!=(COUNT.length+dumpCnt*PLCDataConstants.EDATA_DUMP_TOTAL_LEN)){
				throw new Exception("EData LENGTH["+rawData.length+"] IS INVALID!, CORRECT LENGTH["+(COUNT.length+dumpCnt*PLCDataConstants.EDATA_DUMP_TOTAL_LEN)+"]");
			}
			eDataDump=new EDataDump[dumpCnt];
			for(int i=0;i<dumpCnt;i++) {
				pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, DATADUMP);
				eDataDump[i] = new EDataDump(DATADUMP);
			}
		}catch (Exception e) {
			log.error("EDATA PARSING ERROR! - "+e.getMessage());
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

	    retValue.append("EData ( ")
	        .append(super.toString()).append(TAB)
	        .append("dumpCnt = ").append(this.dumpCnt).append(TAB);
	    	for(int i=0;i<dumpCnt;i++) {
        		retValue.append("fDataDump = ").append(this.eDataDump[i]).append(TAB);
        	}
	        retValue.append(" )");

	    return retValue.toString();
	}
}
