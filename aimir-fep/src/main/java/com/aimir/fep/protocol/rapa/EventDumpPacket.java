package com.aimir.fep.protocol.rapa;

public class EventDumpPacket {

	byte[] mid = new byte[11];	// meter id
	byte[] itime = new byte[7];	// 검침정보 수집시의 dcu 시간
	byte[] mtime = new byte[7];	// 해당 검침정보 발생일자, 시간
	byte[] reserved = new byte[4];	// reserved
	byte[] ctime = new byte[7];	// 정전발생시간
	
	private String meterId;
	private String strITime;
	private String strMTime;
	private int reservedVal;
	private String strCTime;	
	
	public byte[] getMid() {
		return mid;
	}
	public void setMid(byte[] mid) {
		this.mid = mid;
	}
	public byte[] getItime() {
		return itime;
	}
	public void setItime(byte[] itime) {
		this.itime = itime;
	}
	public byte[] getMtime() {
		return mtime;
	}
	public void setMtime(byte[] mtime) {
		this.mtime = mtime;
	}
	public byte[] getReserved() {
		return reserved;
	}
	public void setReserved(byte[] reserved) {
		this.reserved = reserved;
	}
	public byte[] getCtime() {
		return ctime;
	}
	public void setCtime(byte[] ctime) {
		this.ctime = ctime;
	}
	public String getMeterId() {
		return meterId;
	}
	public void setMeterId(String meterId) {
		this.meterId = meterId;
	}
	public String getStrITime() {
		return strITime;
	}
	public void setStrITime(String strITime) {
		this.strITime = strITime;
	}
	public String getStrMTime() {
		return strMTime;
	}
	public void setStrMTime(String strMTime) {
		this.strMTime = strMTime;
	}
	public int getReservedVal() {
		return reservedVal;
	}
	public void setReservedVal(int reservedVal) {
		this.reservedVal = reservedVal;
	}
	public String getStrCTime() {
		return strCTime;
	}
	public void setStrCTime(String strCTime) {
		this.strCTime = strCTime;
	}
	
}
