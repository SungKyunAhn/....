package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Response(최대 수요 데이터)
 * @author kaze
 * 2009. 6. 19.
 */
public class EDataDump implements java.io.Serializable{
	private static Log log = LogFactory.getLog(EDataDump.class);

	private int mt;//Meter Type
	private String meterId;//Meter Id
	private int dType;//Data Type(0x01: 현재 검침, 0x02: 정기 검침)
	private String iTime;//IRM Time(yyyymmddhhmmss)
	private String iWeek;//IRM Week(monday:01~sunday:07);
	private String mTime;//Meter Time(yyyymmddhhmmss)
	private String mWeek;//Meter Week(monday:01~sunday:07);
	private long acon;//Active Constant(유효 전력량 계기 정수)
	private long rcon;//Reactive Constant(무효 전력량 계기 정수)

	private int ap;//유효 수요 전력
	private String et;//유효 발생 시간
	private String etWeek;//유효 발생 Week
	private long tap;//누적 유효 수요 전력
	private int apa;//유효 수요 전력(A)
	private String eta;//유효 발생 시간(A)
	private String etaWeek;//유효 발생 Week(A)
	private long tapa;//누적 유효 수요 전력(A)
	private int apb;//유효 수요 전력(B)
	private String etb;//유효 발생 시간(B)
	private String etbWeek;//유효 발생 Week(B)
	private long tapb;//누적 유효 수요 전력(B)
	private int apc;//유효 수요 전력(C)
	private String etc;//유효 발생 시간(C)
	private String etcWeek;//유효 발생 Week(C)
	private long tapc;//누적 유효 수요 전력(C)

	private int rp;//무효 수요 전력
	private String rpet;//무효 발생 시간
	private String rpetWeek;//무효 발생 Week
	private long trp;//누적 무효 수요 전력
	private int rpa;//무효 수요 전력(A)
	private String rpeta;//무효 발생 시간(A)
	private String rpetaWeek;//무효 발생 Week(A)
	private long trpa;//누적 무효 수요 전력(A)
	private int rpb;//무효 수요 전력(B)
	private String rpetb;//무효 발생 시간(B)
	private String rpetbWeek;//무효 발생 Week(B)
	private long trpb;//누적 무효 수요 전력(B)
	private int rpc;//무효 수요 전력(C)
	private String rpetc;//무효 발생 시간(C)
	private String rpetcWeek;//무효 발생 Week(C)
	private long trpc;//누적 무효 수요 전력(C)

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
	 * @return the iWeek(IRM Week(monday:01~sunday:07);)
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
	 * @return the ap(유효 수요 전력)
	 */
	public int getAp() {
		return ap;
	}

	/**
	 * @param ap the ap to set
	 */
	public void setAp(int ap) {
		this.ap = ap;
	}

	/**
	 * @return the et(유효 발생 시간)
	 */
	public String getEt() {
		return et;
	}

	/**
	 * @param et the et to set
	 */
	public void setEt(String et) {
		this.et = et;
	}

	/**
	 * @return the etWeek(유효 발생 Week)
	 */
	public String getEtWeek() {
		return etWeek;
	}

	/**
	 * @param etWeek the etWeek to set
	 */
	public void setEtWeek(String etWeek) {
		this.etWeek = etWeek;
	}

	/**
	 * @return the tap(누적 유효 수효 전력)
	 */
	public long getTap() {
		return tap;
	}

	/**
	 * @param tap the tap to set
	 */
	public void setTap(long tap) {
		this.tap = tap;
	}

	/**
	 * @return the apa(유효 수요 전력(A))
	 */
	public int getApa() {
		return apa;
	}

	/**
	 * @param apa the apa to set
	 */
	public void setApa(int apa) {
		this.apa = apa;
	}

	/**
	 * @return the eta(유효 수요 발생 시간(A))
	 */
	public String getEta() {
		return eta;
	}

	/**
	 * @param eta the eta to set
	 */
	public void setEta(String eta) {
		this.eta = eta;
	}

	/**
	 * @return the etaWeek(유효 수요 발생 Week(A))
	 */
	public String getEtaWeek() {
		return etaWeek;
	}

	/**
	 * @param etaWeek the etaWeek to set
	 */
	public void setEtaWeek(String etaWeek) {
		this.etaWeek = etaWeek;
	}

	/**
	 * @return the tapa(누적 유효 수요 전력(A))
	 */
	public long getTapa() {
		return tapa;
	}

	/**
	 * @param tapa the tapa to set
	 */
	public void setTapa(long tapa) {
		this.tapa = tapa;
	}

	/**
	 * @return the apb(유효 수요 전력(B))
	 */
	public int getApb() {
		return apb;
	}

	/**
	 * @param apb the apb to set
	 */
	public void setApb(int apb) {
		this.apb = apb;
	}

	/**
	 * @return the etb(유효 발생 시간(B))
	 */
	public String getEtb() {
		return etb;
	}

	/**
	 * @param etb the etb to set
	 */
	public void setEtb(String etb) {
		this.etb = etb;
	}

	/**
	 * @return the etbWeek유효 발생 Week(B)
	 */
	public String getEtbWeek() {
		return etbWeek;
	}

	/**
	 * @param etbWeek the etbWeek to set
	 */
	public void setEtbWeek(String etbWeek) {
		this.etbWeek = etbWeek;
	}

	/**
	 * @return the tapb(누적 유효 수요 전력(B))
	 */
	public long getTapb() {
		return tapb;
	}

	/**
	 * @param tapb the tapb to set
	 */
	public void setTapb(long tapb) {
		this.tapb = tapb;
	}

	/**
	 * @return the apc(유효 수요 전력(C))
	 */
	public int getApc() {
		return apc;
	}

	/**
	 * @param apc the apc to set
	 */
	public void setApc(int apc) {
		this.apc = apc;
	}

	/**
	 * @return the etc(유효 발생 시간(C))
	 */
	public String getEtc() {
		return etc;
	}

	/**
	 * @param etc the etc to set
	 */
	public void setEtc(String etc) {
		this.etc = etc;
	}

	/**
	 * @return the etcWeek(유효 발생 Week(C))
	 */
	public String getEtcWeek() {
		return etcWeek;
	}

	/**
	 * @param etcWeek the etcWeek to set
	 */
	public void setEtcWeek(String etcWeek) {
		this.etcWeek = etcWeek;
	}

	/**
	 * @return the tapc(누적 유효 수요 전력(C))
	 */
	public long getTapc() {
		return tapc;
	}

	/**
	 * @param tapc the tapc to set
	 */
	public void setTapc(long tapc) {
		this.tapc = tapc;
	}

	/**
	 * @return the rp(무효 수요 전력)
	 */
	public int getRp() {
		return rp;
	}

	/**
	 * @param rp the rp to set
	 */
	public void setRp(int rp) {
		this.rp = rp;
	}

	/**
	 * @return the rpet(무효 발생 시간)
	 */
	public String getRpet() {
		return rpet;
	}

	/**
	 * @param rpet the rpet to set
	 */
	public void setRpet(String rpet) {
		this.rpet = rpet;
	}

	/**
	 * @return the rpetWeek(무효 발생 Week)
	 */
	public String getRpetWeek() {
		return rpetWeek;
	}

	/**
	 * @param rpetWeek the rpetWeek to set
	 */
	public void setRpetWeek(String rpetWeek) {
		this.rpetWeek = rpetWeek;
	}

	/**
	 * @return the trp(누적 무효 수요 전력)
	 */
	public long getTrp() {
		return trp;
	}

	/**
	 * @param trp the trp to set
	 */
	public void setTrp(long trp) {
		this.trp = trp;
	}

	/**
	 * @return the rpa(무효 수요 전력(A))
	 */
	public int getRpa() {
		return rpa;
	}

	/**
	 * @param rpa the rpa to set
	 */
	public void setRpa(int rpa) {
		this.rpa = rpa;
	}

	/**
	 * @return the rpeta(무효 발생 시간(A))
	 */
	public String getRpeta() {
		return rpeta;
	}

	/**
	 * @param rpeta the rpeta to set
	 */
	public void setRpeta(String rpeta) {
		this.rpeta = rpeta;
	}

	/**
	 * @return the rpetaWeek(무효 발생 Week(A))
	 */
	public String getRpetaWeek() {
		return rpetaWeek;
	}

	/**
	 * @param rpetaWeek the rpetaWeek to set
	 */
	public void setRpetaWeek(String rpetaWeek) {
		this.rpetaWeek = rpetaWeek;
	}

	/**
	 * @return the trpa(누적 무효 수요 전력(A))
	 */
	public long getTrpa() {
		return trpa;
	}

	/**
	 * @param trpa the trpa to set
	 */
	public void setTrpa(long trpa) {
		this.trpa = trpa;
	}

	/**
	 * @return the rpb(무효 수요 전력(B))
	 */
	public int getRpb() {
		return rpb;
	}

	/**
	 * @param rpb the rpb to set
	 */
	public void setRpb(int rpb) {
		this.rpb = rpb;
	}

	/**
	 * @return the rpetb(무효 발생 시간(B))
	 */
	public String getRpetb() {
		return rpetb;
	}

	/**
	 * @param rpetb the rpetb to set
	 */
	public void setRpetb(String rpetb) {
		this.rpetb = rpetb;
	}

	/**
	 * @return the rpetbWeek(무효 발생 Week(B))
	 */
	public String getRpetbWeek() {
		return rpetbWeek;
	}

	/**
	 * @param rpetbWeek the rpetbWeek to set
	 */
	public void setRpetbWeek(String rpetbWeek) {
		this.rpetbWeek = rpetbWeek;
	}

	/**
	 * @return the trpb(누적 무효 수요 전력(B))
	 */
	public long getTrpb() {
		return trpb;
	}

	/**
	 * @param trpb the trpb to set
	 */
	public void setTrpb(long trpb) {
		this.trpb = trpb;
	}

	/**
	 * @return the rpc(무효 수요 전력(C))
	 */
	public int getRpc() {
		return rpc;
	}

	/**
	 * @param rpc the rpc to set
	 */
	public void setRpc(int rpc) {
		this.rpc = rpc;
	}

	/**
	 * @return the rpetc(무효 발생 시간(C))
	 */
	public String getRpetc() {
		return rpetc;
	}

	/**
	 * @param rpetc the rpetc to set
	 */
	public void setRpetc(String rpetc) {
		this.rpetc = rpetc;
	}

	/**
	 * @return the rpetcWeek(무효 발생 Week(C))
	 */
	public String getRpetcWeek() {
		return rpetcWeek;
	}

	/**
	 * @param rpetcWeek the rpetcWeek to set
	 */
	public void setRpetcWeek(String rpetcWeek) {
		this.rpetcWeek = rpetcWeek;
	}

	/**
	 * @return the trpc(누적 무효 수요 전력(C))
	 */
	public long getTrpc() {
		return trpc;
	}

	/**
	 * @param trpc the trpc to set
	 */
	public void setTrpc(long trpc) {
		this.trpc = trpc;
	}

	public EDataDump(byte[] rawData) throws Exception {
		try {
			//Check Length
			if(rawData.length!=PLCDataConstants.EDATA_DUMP_TOTAL_LEN) {
				throw new Exception("EDataDUMP LENGTH["+rawData.length+"] IS INVALID, CORRECT LENGTH["+PLCDataConstants.EDATA_DUMP_TOTAL_LEN+"]!");
			}
			int pos = 0;
			byte[] MT = new byte[1];//Meter Type
			byte[] MID = new byte[20];//Meter ID
			byte[] DTYPE = new byte[1];//Data Type(0x01: 현재 검침, 0x02: 정기 검침)
			byte[] ITIME = new byte[7];//IRM Time(year, month, day, week(monday:0x01~sunday:0x07), hour, min, sec)
			byte[] MTIME = new byte[7];//Meter Time(year, month, day, week(monday:0x01~sunday:0x07), hour, min, sec)
			byte[] ACON = new byte[4];//Active Constant(유효 전력량 계기 정수)
			byte[] RCON = new byte[4];//Reactive Constant(무효 전력량 계기 정수)

			byte[] AP = new byte[2];//유효 수요 전력
			byte[] ET = new byte[7];//유효 발생 시간
			byte[] TAP = new byte[4];//누적 유효 수요 전력
			byte[] APA = new byte[2];//유효 수요 전력(A)
			byte[] ETA = new byte[7];//유효 발생 시간(A)
			byte[] TAPA = new byte[4];//누적 유효 수요 전력(A)
			byte[] APB = new byte[2];//유효 수요 전력(B)
			byte[] ETB = new byte[7];//유효 발생 시간(B)
			byte[] TAPB = new byte[4];//누적 유효 수요 전력(B)
			byte[] APC = new byte[2];//유효 수요 전력(C)
			byte[] ETC = new byte[7];//유효 발생 시간(C)
			byte[] TAPC = new byte[4];//누적 유효 수요 전력(C)

			byte[] RP = new byte[2];//무효 수요 전력
			byte[] RPET = new byte[7];//무효 발생 시간
			byte[] TRP = new byte[4];//누적 무효 수요 전력
			byte[] RPA = new byte[2];//무효 수요 전력(A)
			byte[] RPETA = new byte[7];//무효 발생 시간(A)
			byte[] TRPA = new byte[4];//누적 무효 수요 전력(A)
			byte[] RPB = new byte[2];//무효 수요 전력(B)
			byte[] RPETB = new byte[7];//무효 발생 시간(B)
			byte[] TRPB = new byte[4];//누적 무효 수요 전력(B)
			byte[] RPC = new byte[2];//무효 수요 전력(C)
			byte[] RPETC = new byte[7];//무효 발생 시간(C)
			byte[] TRPC = new byte[4];//누적 무효 수요 전력(C)

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

			//유효 전력
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, AP);
			ap=DataUtil.getIntToBytes(AP);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, ET);
			et=DataUtil.getPLCDate(ET).trim();
			etWeek=DataUtil.getPLCWeek(ET).trim();
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, TAP);
			tap=DataUtil.getLongToBytes(TAP);
			//유효 전력(A)
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, APA);
			apa=DataUtil.getIntToBytes(APA);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, ETA);
			eta=DataUtil.getPLCDate(ETA).trim();
			etaWeek=DataUtil.getPLCWeek(ETA).trim();
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, TAPA);
			tapa=DataUtil.getLongToBytes(TAPA);
			//유효 전력(B)
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, APB);
			apb=DataUtil.getIntToBytes(APB);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, ETB);
			etb=DataUtil.getPLCDate(ETB).trim();
			etbWeek=DataUtil.getPLCWeek(ETB).trim();
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, TAPB);
			tapb=DataUtil.getLongToBytes(TAPB);
			//유효 전력(B)
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, APC);
			apc=DataUtil.getIntToBytes(APC);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, ETC);
			etc=DataUtil.getPLCDate(ETC).trim();
			etcWeek=DataUtil.getPLCWeek(ETC).trim();
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, TAPC);
			tapc=DataUtil.getLongToBytes(TAPC);

			//무효 전력
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, RP);
			rp=DataUtil.getIntToBytes(RP);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, RPET);
			rpet=DataUtil.getPLCDate(RPET).trim();
			rpetWeek=DataUtil.getPLCWeek(RPET).trim();
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, TRP);
			trp=DataUtil.getLongToBytes(TRP);
			//무효 전력(A)
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, RPA);
			rpa=DataUtil.getIntToBytes(RPA);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, RPETA);
			rpeta=DataUtil.getPLCDate(RPETA).trim();
			rpetaWeek=DataUtil.getPLCWeek(RPETA).trim();
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, TRPA);
			trpa=DataUtil.getLongToBytes(TRPA);
			//무효 전력(B)
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, RPB);
			rpb=DataUtil.getIntToBytes(RPB);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, RPETB);
			rpetb=DataUtil.getPLCDate(RPETB).trim();
			rpetbWeek=DataUtil.getPLCWeek(RPETB).trim();
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, TRPB);
			trpb=DataUtil.getLongToBytes(TRPB);
			//무효 전력(C)
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, RPC);
			rpc=DataUtil.getIntToBytes(RPC);
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, RPETC);
			rpetc=DataUtil.getPLCDate(RPETC).trim();
			rpetcWeek=DataUtil.getPLCWeek(RPETC).trim();
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, TRPC);
			trpc=DataUtil.getLongToBytes(TRPC);
		}catch (Exception e) {
			log.error("EDATA DUMP PARSING ERROR! - "+e.getMessage());
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

	    retValue.append("EDataDump ( ")
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
	        .append("ap = ").append(this.ap).append(TAB)
	        .append("et = ").append(this.et).append(TAB)
	        .append("etWeek = ").append(this.etWeek).append(TAB)
	        .append("tap = ").append(this.tap).append(TAB)
	        .append("apa = ").append(this.apa).append(TAB)
	        .append("eta = ").append(this.eta).append(TAB)
	        .append("etaWeek = ").append(this.etaWeek).append(TAB)
	        .append("tapa = ").append(this.tapa).append(TAB)
	        .append("apb = ").append(this.apb).append(TAB)
	        .append("etb = ").append(this.etb).append(TAB)
	        .append("etbWeek = ").append(this.etbWeek).append(TAB)
	        .append("tapb = ").append(this.tapb).append(TAB)
	        .append("apc = ").append(this.apc).append(TAB)
	        .append("etc = ").append(this.etc).append(TAB)
	        .append("etcWeek = ").append(this.etcWeek).append(TAB)
	        .append("tapc = ").append(this.tapc).append(TAB)
	        .append("rp = ").append(this.rp).append(TAB)
	        .append("rpet = ").append(this.rpet).append(TAB)
	        .append("rpetWeek = ").append(this.rpetWeek).append(TAB)
	        .append("trp = ").append(this.trp).append(TAB)
	        .append("rpa = ").append(this.rpa).append(TAB)
	        .append("rpeta = ").append(this.rpeta).append(TAB)
	        .append("rpetaWeek = ").append(this.rpetaWeek).append(TAB)
	        .append("trpa = ").append(this.trpa).append(TAB)
	        .append("rpb = ").append(this.rpb).append(TAB)
	        .append("rpetb = ").append(this.rpetb).append(TAB)
	        .append("rpetbWeek = ").append(this.rpetbWeek).append(TAB)
	        .append("trpb = ").append(this.trpb).append(TAB)
	        .append("rpc = ").append(this.rpc).append(TAB)
	        .append("rpetc = ").append(this.rpetc).append(TAB)
	        .append("rpetcWeek = ").append(this.rpetcWeek).append(TAB)
	        .append("trpc = ").append(this.trpc).append(TAB)
	        .append(" )");

	    return retValue.toString();
	}
}
