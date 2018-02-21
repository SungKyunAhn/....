package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Response(변압기 감시 데이터)
 * @author kaze
 * 2009. 6. 19.
 */
public class JDataDump implements java.io.Serializable{
	private static Log log = LogFactory.getLog(IDataDump.class);

	private String rTime;

	private int volA;
	private int maxVolA;
	private int minVolA;
	private int volB;
	private int maxVolB;
	private int minVolB;
	private int volC;
	private int maxVolC;
	private int minVolC;

	private int curA;
	private int maxCurA;
	private int minCurA;
	private int curB;
	private int maxCurB;
	private int minCurB;
	private int curC;
	private int maxCurC;
	private int minCurC;

	private int userRateA;
	private int userRateB;
	private int userRateC;
	private int temp;
	private byte volStatus;
	private byte curStatus;

	private long apt;//유효 전력량(Total)
	private long rpt;//무효 전력량(Total)
	private long pft;//역률(Total)
	private long apta;//유효 전력량(A)
	private long rpta;//무효 전력량(A)
	private long pfta;//역률(A)
	private long aptb;//유효 전력량(B)
	private long rptb;//무효 전력량(B)
	private long pftb;//역률(B)
	private long aptc;//유효 전력량(C)
	private long rptc;//무효 전력량(C)
	private long pftc;//역률(C)

	/**
	 * @return the rTime(Reading Time)
	 */
	public String getRTime() {
		return rTime;
	}

	/**
	 * @param time the rTime to set
	 */
	public void setRTime(String time) {
		rTime = time;
	}

	/**
	 * @return the volA
	 */
	public int getVolA() {
		return volA;
	}

	/**
	 * @param volA the volA to set
	 */
	public void setVolA(int volA) {
		this.volA = volA;
	}

	/**
	 * @return the maxVolA
	 */
	public int getMaxVolA() {
		return maxVolA;
	}

	/**
	 * @param maxVolA the maxVolA to set
	 */
	public void setMaxVolA(int maxVolA) {
		this.maxVolA = maxVolA;
	}

	/**
	 * @return the minVolA
	 */
	public int getMinVolA() {
		return minVolA;
	}

	/**
	 * @param minVolA the minVolA to set
	 */
	public void setMinVolA(int minVolA) {
		this.minVolA = minVolA;
	}

	/**
	 * @return the volB
	 */
	public int getVolB() {
		return volB;
	}

	/**
	 * @param volB the volB to set
	 */
	public void setVolB(int volB) {
		this.volB = volB;
	}

	/**
	 * @return the maxVolB
	 */
	public int getMaxVolB() {
		return maxVolB;
	}

	/**
	 * @param maxVolB the maxVolB to set
	 */
	public void setMaxVolB(int maxVolB) {
		this.maxVolB = maxVolB;
	}

	/**
	 * @return the minVolB
	 */
	public int getMinVolB() {
		return minVolB;
	}

	/**
	 * @param minVolB the minVolB to set
	 */
	public void setMinVolB(int minVolB) {
		this.minVolB = minVolB;
	}

	/**
	 * @return the volC
	 */
	public int getVolC() {
		return volC;
	}

	/**
	 * @param volC the volC to set
	 */
	public void setVolC(int volC) {
		this.volC = volC;
	}

	/**
	 * @return the maxVolC
	 */
	public int getMaxVolC() {
		return maxVolC;
	}

	/**
	 * @param maxVolC the maxVolC to set
	 */
	public void setMaxVolC(int maxVolC) {
		this.maxVolC = maxVolC;
	}

	/**
	 * @return the minVolC
	 */
	public int getMinVolC() {
		return minVolC;
	}

	/**
	 * @param minVolC the minVolC to set
	 */
	public void setMinVolC(int minVolC) {
		this.minVolC = minVolC;
	}

	/**
	 * @return the curA
	 */
	public int getCurA() {
		return curA;
	}

	/**
	 * @param curA the curA to set
	 */
	public void setCurA(int curA) {
		this.curA = curA;
	}

	/**
	 * @return the maxCurA
	 */
	public int getMaxCurA() {
		return maxCurA;
	}

	/**
	 * @param maxCurA the maxCurA to set
	 */
	public void setMaxCurA(int maxCurA) {
		this.maxCurA = maxCurA;
	}

	/**
	 * @return the minCurA
	 */
	public int getMinCurA() {
		return minCurA;
	}

	/**
	 * @param minCurA the minCurA to set
	 */
	public void setMinCurA(int minCurA) {
		this.minCurA = minCurA;
	}

	/**
	 * @return the curB
	 */
	public int getCurB() {
		return curB;
	}

	/**
	 * @param curB the curB to set
	 */
	public void setCurB(int curB) {
		this.curB = curB;
	}

	/**
	 * @return the maxCurB
	 */
	public int getMaxCurB() {
		return maxCurB;
	}

	/**
	 * @param maxCurB the maxCurB to set
	 */
	public void setMaxCurB(int maxCurB) {
		this.maxCurB = maxCurB;
	}

	/**
	 * @return the minCurB
	 */
	public int getMinCurB() {
		return minCurB;
	}

	/**
	 * @param minCurB the minCurB to set
	 */
	public void setMinCurB(int minCurB) {
		this.minCurB = minCurB;
	}

	/**
	 * @return the curC
	 */
	public int getCurC() {
		return curC;
	}

	/**
	 * @param curC the curC to set
	 */
	public void setCurC(int curC) {
		this.curC = curC;
	}

	/**
	 * @return the maxCurC
	 */
	public int getMaxCurC() {
		return maxCurC;
	}

	/**
	 * @param maxCurC the maxCurC to set
	 */
	public void setMaxCurC(int maxCurC) {
		this.maxCurC = maxCurC;
	}

	/**
	 * @return the minCurC
	 */
	public int getMinCurC() {
		return minCurC;
	}

	/**
	 * @param minCurC the minCurC to set
	 */
	public void setMinCurC(int minCurC) {
		this.minCurC = minCurC;
	}

	/**
	 * @return the userRateA
	 */
	public int getUserRateA() {
		return userRateA;
	}

	/**
	 * @param userRateA the userRateA to set
	 */
	public void setUserRateA(int userRateA) {
		this.userRateA = userRateA;
	}

	/**
	 * @return the userRateB
	 */
	public int getUserRateB() {
		return userRateB;
	}

	/**
	 * @param userRateB the userRateB to set
	 */
	public void setUserRateB(int userRateB) {
		this.userRateB = userRateB;
	}

	/**
	 * @return the userRateC
	 */
	public int getUserRateC() {
		return userRateC;
	}

	/**
	 * @param userRateC the userRateC to set
	 */
	public void setUserRateC(int userRateC) {
		this.userRateC = userRateC;
	}

	/**
	 * @return the temp
	 */
	public int getTemp() {
		return temp;
	}

	/**
	 * @param temp the temp to set
	 */
	public void setTemp(int temp) {
		this.temp = temp;
	}

	/**
	 * @return the volStatus
	 */
	public byte getVolStatus() {
		return volStatus;
	}

	/**
	 * @param volStatus the volStatus to set
	 */
	public void setVolStatus(byte volStatus) {
		this.volStatus = volStatus;
	}

	/**
	 * @return the curStatus
	 */
	public byte getCurStatus() {
		return curStatus;
	}

	/**
	 * @param curStatus the curStatus to set
	 */
	public void setCurStatus(byte curStatus) {
		this.curStatus = curStatus;
	}

	/**
	 * @return the apt
	 */
	public long getApt() {
		return apt;
	}

	/**
	 * @param apt the apt to set
	 */
	public void setApt(long apt) {
		this.apt = apt;
	}

	/**
	 * @return the rpt
	 */
	public long getRpt() {
		return rpt;
	}

	/**
	 * @param rpt the rpt to set
	 */
	public void setRpt(long rpt) {
		this.rpt = rpt;
	}

	/**
	 * @return the pft
	 */
	public long getPft() {
		return pft;
	}

	/**
	 * @param pft the pft to set
	 */
	public void setPft(long pft) {
		this.pft = pft;
	}

	/**
	 * @return the apta
	 */
	public long getApta() {
		return apta;
	}

	/**
	 * @param apta the apta to set
	 */
	public void setApta(long apta) {
		this.apta = apta;
	}

	/**
	 * @return the rpta
	 */
	public long getRpta() {
		return rpta;
	}

	/**
	 * @param rpta the rpta to set
	 */
	public void setRpta(long rpta) {
		this.rpta = rpta;
	}

	/**
	 * @return the pfta
	 */
	public long getPfta() {
		return pfta;
	}

	/**
	 * @param pfta the pfta to set
	 */
	public void setPfta(long pfta) {
		this.pfta = pfta;
	}

	/**
	 * @return the aptb
	 */
	public long getAptb() {
		return aptb;
	}

	/**
	 * @param aptb the aptb to set
	 */
	public void setAptb(long aptb) {
		this.aptb = aptb;
	}

	/**
	 * @return the rptb
	 */
	public long getRptb() {
		return rptb;
	}

	/**
	 * @param rptb the rptb to set
	 */
	public void setRptb(long rptb) {
		this.rptb = rptb;
	}

	/**
	 * @return the pftb
	 */
	public long getPftb() {
		return pftb;
	}

	/**
	 * @param pftb the pftb to set
	 */
	public void setPftb(long pftb) {
		this.pftb = pftb;
	}

	/**
	 * @return the aptc
	 */
	public long getAptc() {
		return aptc;
	}

	/**
	 * @param aptc the aptc to set
	 */
	public void setAptc(long aptc) {
		this.aptc = aptc;
	}

	/**
	 * @return the rptc
	 */
	public long getRptc() {
		return rptc;
	}

	/**
	 * @param rptc the rptc to set
	 */
	public void setRptc(long rptc) {
		this.rptc = rptc;
	}

	/**
	 * @return the pftc
	 */
	public long getPftc() {
		return pftc;
	}

	/**
	 * @param pftc the pftc to set
	 */
	public void setPftc(long pftc) {
		this.pftc = pftc;
	}

	public JDataDump(byte[] rawData) throws Exception {
		try {
			//Check Length
			if(rawData.length!=PLCDataConstants.JDATA_DUMP_TOTAL_LEN) {
				throw new Exception("JDataDUMP LENGTH["+rawData.length+"] IS INVALID!");
			}
			int pos = 0;
			byte[] RTIME = new byte[7];

			byte[] VOLA = new byte[2];
			byte[] MAXVOLA = new byte[1];
			byte[] MINVOLA = new byte[1];
			byte[] VOLB = new byte[2];
			byte[] MAXVOLB = new byte[1];
			byte[] MINVOLB = new byte[1];
			byte[] VOLC = new byte[2];
			byte[] MAXVOLC = new byte[1];
			byte[] MINVOLC = new byte[1];

			byte[] CURA = new byte[2];
			byte[] MAXCURA = new byte[1];
			byte[] MINCURA = new byte[1];
			byte[] CURB = new byte[2];
			byte[] MAXCURB = new byte[1];
			byte[] MINCURB = new byte[1];
			byte[] CURC = new byte[2];
			byte[] MAXCURC = new byte[1];
			byte[] MINCURC = new byte[1];

			byte[] USERRATEA = new byte[2];
			byte[] USERRATEB = new byte[2];
			byte[] USERRATEC = new byte[2];
			byte[] TEMP = new byte[2];
			byte[] VOLSTATUS = new byte[1];
			byte[] CURSTATUS = new byte[1];

			byte[] APT = new byte[4];//유효 전력량(Total)
			byte[] RPT = new byte[4];//무효 전력량(Total)
			byte[] PFT = new byte[4];//역률(Total)
			byte[] APTA = new byte[4];//유효 전력량(A)
			byte[] RPTA = new byte[4];//무효 전력량(A)
			byte[] PFTA = new byte[4];//역률(A)
			byte[] APTB = new byte[4];//유효 전력량(B)
			byte[] RPTB = new byte[4];//무효 전력량(B)
			byte[] PFTB = new byte[4];//역률(B)
			byte[] APTC = new byte[4];//유효 전력량(C)
			byte[] RPTC = new byte[4];//무효 전력량(C)
			byte[] PFTC = new byte[4];//역률(C)

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, RTIME);
			rTime=DataUtil.getPLCDate(RTIME).trim();

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, VOLA);
			volA=DataUtil.getIntToBytes(VOLA);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MAXVOLA);
			maxVolA=DataUtil.getIntToBytes(MAXVOLA);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MINVOLA);
			minVolA=DataUtil.getIntToBytes(MINVOLA);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, VOLB);
			volB=DataUtil.getIntToBytes(VOLB);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MAXVOLB);
			maxVolB=DataUtil.getIntToBytes(MAXVOLB);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MINVOLB);
			minVolB=DataUtil.getIntToBytes(MINVOLB);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, VOLC);
			volC=DataUtil.getIntToBytes(VOLC);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MAXVOLC);
			maxVolC=DataUtil.getIntToBytes(MAXVOLC);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MINVOLC);
			minVolC=DataUtil.getIntToBytes(MINVOLC);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, CURA);
			curA=DataUtil.getIntToBytes(CURA);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MAXCURA);
			maxVolA=DataUtil.getIntToBytes(MAXCURA);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MINCURA);
			minVolA=DataUtil.getIntToBytes(MINCURA);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, CURB);
			curB=DataUtil.getIntToBytes(CURB);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MAXCURB);
			maxVolB=DataUtil.getIntToBytes(MAXCURB);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MINCURB);
			minVolB=DataUtil.getIntToBytes(MINCURB);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, CURC);
			curC=DataUtil.getIntToBytes(CURC);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MAXCURC);
			maxVolC=DataUtil.getIntToBytes(MAXCURC);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MINCURC);
			minVolC=DataUtil.getIntToBytes(MINCURC);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, USERRATEA);
			userRateA=DataUtil.getIntToBytes(USERRATEA);
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, USERRATEB);
			userRateB=DataUtil.getIntToBytes(USERRATEB);
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, USERRATEC);
			userRateC=DataUtil.getIntToBytes(USERRATEC);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, TEMP);
			temp=DataUtil.getIntToBytes(TEMP);

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, VOLSTATUS);
			volStatus=VOLSTATUS[0];
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, CURSTATUS);
			curStatus=CURSTATUS[0];

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, APT);
			apt=DataUtil.getLongToBytes(APT);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, RPT);
			rpt=DataUtil.getLongToBytes(RPT);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, PFT);
			pft=DataUtil.getLongToBytes(PFT);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, APTA);
			apta=DataUtil.getLongToBytes(APTA);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, RPTA);
			rpta=DataUtil.getLongToBytes(RPTA);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, PFTA);
			pfta=DataUtil.getLongToBytes(PFTA);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, APTB);
			aptb=DataUtil.getLongToBytes(APTB);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, RPTB);
			rptb=DataUtil.getLongToBytes(RPTB);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, PFTB);
			pftb=DataUtil.getLongToBytes(PFTB);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, APTC);
			aptc=DataUtil.getLongToBytes(APTC);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, RPTC);
			rptc=DataUtil.getLongToBytes(RPTC);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, PFTC);
			pftc=DataUtil.getLongToBytes(PFTC);
		}catch (Exception e) {
			log.error("JDATA DUMP PARSING ERROR! - "+e.getMessage());
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

	    retValue.append("JDataDump ( ")
	        .append(super.toString()).append(TAB)
	        .append("rTime = ").append(this.rTime).append(TAB)
	        .append("volA = ").append(this.volA).append(TAB)
	        .append("maxVolA = ").append(this.maxVolA).append(TAB)
	        .append("minVolA = ").append(this.minVolA).append(TAB)
	        .append("volB = ").append(this.volB).append(TAB)
	        .append("maxVolB = ").append(this.maxVolB).append(TAB)
	        .append("minVolB = ").append(this.minVolB).append(TAB)
	        .append("volC = ").append(this.volC).append(TAB)
	        .append("maxVolC = ").append(this.maxVolC).append(TAB)
	        .append("minVolC = ").append(this.minVolC).append(TAB)
	        .append("curA = ").append(this.curA).append(TAB)
	        .append("maxCurA = ").append(this.maxCurA).append(TAB)
	        .append("minCurA = ").append(this.minCurA).append(TAB)
	        .append("curB = ").append(this.curB).append(TAB)
	        .append("maxCurB = ").append(this.maxCurB).append(TAB)
	        .append("minCurB = ").append(this.minCurB).append(TAB)
	        .append("curC = ").append(this.curC).append(TAB)
	        .append("maxCurC = ").append(this.maxCurC).append(TAB)
	        .append("minCurC = ").append(this.minCurC).append(TAB)
	        .append("userRateA = ").append(this.userRateA).append(TAB)
	        .append("userRateB = ").append(this.userRateB).append(TAB)
	        .append("userRateC = ").append(this.userRateC).append(TAB)
	        .append("temp = ").append(this.temp).append(TAB)
	        .append("volStatus = ").append(this.volStatus).append(TAB)
	        .append("curStatus = ").append(this.curStatus).append(TAB)
	        .append(" )");

	    return retValue.toString();
	}
}
