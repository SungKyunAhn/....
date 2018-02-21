package com.aimir.fep.protocol.rapa;

import java.util.List;

public class LPDumpPacket {

	byte[] mid = new byte[11];	// METER ID
	byte[] dCount = new byte[2];	// Sub Dump Packet수
	byte[] lCount = new byte[2];	// Sub LP Packet
	
	private List<SUBLPCurrentMeterDataFrame> subLPCurrentMeterDataFrameList; // 현재검침FRAME
	private List<SUBLPDataFrame> subLPDataFrameList; // 현재검침FRAME
	
	private String meterId;
	private short dCountVal;
	private short lCountVal;
	
	public byte[] getMid() {
		return mid;
	}
	public void setMid(byte[] mid) {
		this.mid = mid;
	}
	public byte[] getdCount() {
		return dCount;
	}
	public void setdCount(byte[] dCount) {
		this.dCount = dCount;
	}
	public byte[] getlCount() {
		return lCount;
	}
	public void setlCount(byte[] lCount) {
		this.lCount = lCount;
	}
	public List<SUBLPCurrentMeterDataFrame> getSubLPCurrentMeterDataFrameList() {
		return subLPCurrentMeterDataFrameList;
	}
	public void setSubLPCurrentMeterDataFrameList(List<SUBLPCurrentMeterDataFrame> subLPCurrentMeterDataFrameList) {
		this.subLPCurrentMeterDataFrameList = subLPCurrentMeterDataFrameList;
	}
	public List<SUBLPDataFrame> getSubLPDataFrameList() {
		return subLPDataFrameList;
	}
	public void setSubLPDataFrameList(List<SUBLPDataFrame> subLPDataFrameList) {
		this.subLPDataFrameList = subLPDataFrameList;
	}
	public String getMeterId() {
		return meterId;
	}
	public void setMeterId(String meterId) {
		this.meterId = meterId;
	}
	public short getdCountVal() {
		return dCountVal;
	}
	public void setdCountVal(short dCountVal) {
		this.dCountVal = dCountVal;
	}
	public short getlCountVal() {
		return lCountVal;
	}
	public void setlCountVal(short lCountVal) {
		this.lCountVal = lCountVal;
	}	
	
}
