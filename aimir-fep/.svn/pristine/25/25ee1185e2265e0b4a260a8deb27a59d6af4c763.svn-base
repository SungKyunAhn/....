package com.aimir.fep.meter.parser.plc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;

/**
 * IRM Status Response / IRM Trap
 * @author kaze
 * 2009. 6. 19.
 */
public class BData extends PLCData {
	private static Log log = LogFactory.getLog(BData.class);
	private String fepIp;
	private int fepPort;
	private String ip;
	private int port;
	private String macAddr;
	private String iTime;
	private String iWeek;
	private String fwv;
	private int dummy1;
	private String dummy2;

	/**
	 * @return the fepIp
	 */
	public String getFepIp() {
		return fepIp;
	}

	/**
	 * @param fepIp the fepIp to set
	 */
	public void setFepIp(String fepIp) {
		this.fepIp = fepIp;
	}

	/**
	 * @return the fepPort
	 */
	public int getFepPort() {
		return fepPort;
	}

	/**
	 * @param fepPort the fepPort to set
	 */
	public void setFepPort(int fepPort) {
		this.fepPort = fepPort;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the macAddr
	 */
	public String getMacAddr() {
		return macAddr;
	}

	/**
	 * @param macAddr the macAddr to set
	 */
	public void setMacAddr(String macAddr) {
		this.macAddr = macAddr;
	}

	/**
	 * @return the iTime(yyyymmddhhmmss)
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
	 * @return the iWeek(monday:01~sunday:07)
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
	 * @return the fwv(ver1.0 or ver1.1 or Unknown)
	 */
	public String getFwv() {
		return fwv;
	}

	/**
	 * @param fwv the fwv to set
	 */
	public void setFwv(String fwv) {
		this.fwv = fwv;
	}

	/**
	 * @return the dummy1
	 */
	public int getDummy1() {
		return dummy1;
	}

	/**
	 * @param dummy1 the dummy1 to set
	 */
	public void setDummy1(int dummy1) {
		this.dummy1 = dummy1;
	}

	/**
	 * @return the dummy2
	 */
	public String getDummy2() {
		return dummy2;
	}

	/**
	 * @param dummy2 the dummy2 to set
	 */
	public void setDummy2(String dummy2) {
		this.dummy2 = dummy2;
	}

	public BData(PLCDataFrame pdf) {
		super(pdf);
		try {
			byte[] rawData = pdf.getData();
			//Check Length
			if(rawData.length!=PLCDataConstants.BDATA_TOTAL_LEN) {
				throw new Exception("BData LENGTH["+rawData.length+"] IS INVALID!");
			}
			int pos = 0;
			byte[] FEPIP = new byte[6];
			byte[] FEPPORT = new byte[4];
			byte[] IRMIP = new byte[6];
			byte[] IRMPORT = new byte[4];
			byte[] MACADDR = new byte[6];
			byte[] ITIME = new byte[7];
			byte[] FW = new byte[1];
			byte[] DUMMY1 = new byte[1];
			byte[] DUMMY2 = new byte[6];

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, FEPIP);// 6byte
			fepIp=DataUtil.getPLCIp(FEPIP).trim();

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, FEPPORT);// 4byte
			fepPort=DataUtil.getIntToBytes(FEPPORT);

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, IRMIP);// 6byte
			ip=DataUtil.getPLCIp(IRMIP).trim();

			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, IRMPORT);// 4byte
			port=DataUtil.getIntToBytes(IRMPORT);

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MACADDR);// 6byte
			macAddr=DataUtil.getPLCMacAddr(MACADDR).trim();

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, ITIME);// 7byte
			iTime=DataUtil.getPLCDate(ITIME).trim();
			iWeek=DataUtil.getPLCWeek(ITIME).trim();

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, FW);// 1byte
			fwv=DataUtil.getPLCVer(FW).trim();

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, DUMMY1);// 1byte
			dummy1=DataUtil.getIntToBytes(DUMMY1);

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, DUMMY2);// 6byte
			dummy2=DataUtil.getString(DUMMY2).trim();
		}catch(Exception e) {
			log.error("BDATA PARSING ERROR! - "+e.getMessage());
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

	    retValue.append("BData ( ")
	        .append(super.toString()).append(TAB)
	        .append("fepIp = ").append(this.fepIp).append(TAB)
	        .append("fepPort = ").append(this.fepPort).append(TAB)
	        .append("ip = ").append(this.ip).append(TAB)
	        .append("port = ").append(this.port).append(TAB)
	        .append("macAddr = ").append(this.macAddr).append(TAB)
	        .append("iTime = ").append(this.iTime).append(TAB)
	        .append("iWeek = ").append(this.iWeek).append(TAB)
	        .append("fwv = ").append(this.fwv).append(TAB)
	        .append("dummy1 = ").append(this.dummy1).append(TAB)
	        .append("dummy2 = ").append(this.dummy2).append(TAB)
	        .append(" )");

	    return retValue.toString();
	}
}
