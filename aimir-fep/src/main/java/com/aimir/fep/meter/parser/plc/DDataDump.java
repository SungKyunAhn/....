package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Response(검침 데이터)
 * @author kaze
 * 2009. 6. 19.
 */
public class DDataDump implements java.io.Serializable{
	private static Log log = LogFactory.getLog(DDataDump.class);

	private int mt;//Meter Type
	private String meterId;//Meter Id
	private int dType;//Data Type(0x01: 현재 검침, 0x02: 정기 검침)
	private String iTime;//IRM Time(yyyymmddhhmmss)
	private String iWeek;//IRM Week(monday:01~sunday:07);
	private String mTime;//Meter Time(yyyymmddhhmmss)
	private String mWeek;//Meter Week(monday:01~sunday:07);
	private long acon;//Active Constant(유효 전력량 계기 정수)
	private long rcon;//Reactive Constant(무효 전력량 계기 정수)
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
	 * @return the dType(1:현재 검침, 2:정기 검침)
	 */
	public int getDType() {
		return dType;
	}

	/**
	 * @param type the dType to set
	 */
	public void setDType(int type) {
		dType = type;
	}

	/**
	 * @return the iTime(IRM Time(yyyymmddhhmmss))
	 */
	public String getITime() {
		return iTime;
	}

	/**
	 * @param time the iTime to set
	 */
	public void setITime(String time) {
		iTime = time;
	}

	/**
	 * @return the iWeek(IRM Week(monday:01~sunday:07))
	 */
	public String getIWeek() {
		return iWeek;
	}

	/**
	 * @param week the iWeek to set
	 */
	public void setIWeek(String week) {
		iWeek = week;
	}

	/**
	 * @return the mTime(Meter Time(yyyymmddhhmmss))
	 */
	public String getMTime() {
		return mTime;
	}

	/**
	 * @param time the mTime to set
	 */
	public void setMTime(String time) {
		mTime = time;
	}

	/**
	 * @return the mWeek(Meter Week(monday:01~sunday:07))
	 */
	public String getMWeek() {
		return mWeek;
	}

	/**
	 * @param week the mWeek to set
	 */
	public void setMWeek(String week) {
		mWeek = week;
	}

	/**
	 * @return the acon(Active Constant:유효 전력량 계기 정수)
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
	 * @return the rcon(Reactive Constant:무효 전력량 계기 정수)
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
	 * @return the apt(유효 전력량(Total))
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
	 * @return the rpt(무효 전력량(Total))
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
	 * @return the pft(역률(Total))
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
	 * @return the apta(유효 전력량(A))
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
	 * @return the rpta(무효 전력량(A)))
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
	 * @return the pfta(역률(A))
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
	 * @return the aptb(유효 전력량(B))
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
	 * @return the rptb(무효 전력량(B))
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
	 * @return the pftb(역률(B))
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
	 * @return the aptc(유효 전력량(C))
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
	 * @return the rptc(무효 전력량(C))
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
	 * @return the pftc(역률(C))
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

	public DDataDump(byte[] rawData) throws Exception {
		try {
			//Check Length
			if(rawData.length!=PLCDataConstants.DDATA_DUMP_TOTAL_LEN) {
				throw new Exception("DDataDUMP LENGTH["+rawData.length+"] IS INVALID!");
			}
			int pos = 0;
			byte[] MT = new byte[1];//Meter Type
			byte[] MID = new byte[20];//Meter ID
			byte[] DTYPE = new byte[1];//Data Type(0x01: 현재 검침, 0x02: 정기 검침)
			byte[] ITIME = new byte[7];//IRM Time(year, month, day, week(monday:0x01~sunday:0x07), hour, min, sec)
			byte[] MTIME = new byte[7];//Meter Time(year, month, day, week(monday:0x01~sunday:0x07), hour, min, sec)
			byte[] ACON = new byte[4];//Active Constant(유효 전력량 계기 정수)
			byte[] RCON = new byte[4];//Reactive Constant(무효 전력량 계기 정수)
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

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MT);
			mt=DataUtil.getIntToBytes(MT);

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MID);
			meterId=DataUtil.getString(MID).trim();

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, DTYPE);
			dType=DataUtil.getIntToBytes(DTYPE);

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, ITIME);
			iTime=DataUtil.getPLCDate(ITIME).trim();
			iWeek=DataUtil.getPLCWeek(ITIME).trim();

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MTIME);
			mTime=DataUtil.getPLCDate(MTIME).trim();
			mWeek=DataUtil.getPLCWeek(MTIME).trim();

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, ACON);
			acon=DataUtil.getLongToBytes(ACON);

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, RCON);
			rcon=DataUtil.getLongToBytes(RCON);

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
			log.error("DDATA DUMP PARSING ERROR! - "+e.getMessage());
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

	    retValue.append("DDataDump ( ")
	        .append(super.toString()).append(TAB)
	        .append("mt = ").append(this.mt).append(TAB)
	        .append("meterId = ").append(this.meterId).append(TAB)
	        .append("dType = ").append(this.dType).append(TAB)
	        .append("iTime = ").append(this.iTime).append(TAB)
	        .append("iWeek = ").append(this.iWeek).append(TAB)
	        .append("mTime = ").append(this.mTime).append(TAB)
	        .append("mWeek = ").append(this.mWeek).append(TAB)
	        .append("acon = ").append(this.acon).append(TAB)
	        .append("rcon = ").append(this.rcon).append(TAB)
	        .append("apt = ").append(this.apt).append(TAB)
	        .append("rpt = ").append(this.rpt).append(TAB)
	        .append("pft = ").append(this.pft).append(TAB)
	        .append("apta = ").append(this.apta).append(TAB)
	        .append("rpta = ").append(this.rpta).append(TAB)
	        .append("pfta = ").append(this.pfta).append(TAB)
	        .append("aptb = ").append(this.aptb).append(TAB)
	        .append("rptb = ").append(this.rptb).append(TAB)
	        .append("pftb = ").append(this.pftb).append(TAB)
	        .append("aptc = ").append(this.aptc).append(TAB)
	        .append("rptc = ").append(this.rptc).append(TAB)
	        .append("pftc = ").append(this.pftc).append(TAB)
	        .append(" )");

	    return retValue.toString();
	}
}
