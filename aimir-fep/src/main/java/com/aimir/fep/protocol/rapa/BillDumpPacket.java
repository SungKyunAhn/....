package com.aimir.fep.protocol.rapa;

import java.util.List;

public class BillDumpPacket {

	byte[] mid = new byte[11];	// meter id
	byte[] dCount = new byte[2];	// dump packet count
	
	private String meterId;
	private short dCountVal;
	
	private List<SUBBillDataFrame> subBillDataFrame; // sub dump packet

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

	public List<SUBBillDataFrame> getSubBillDataFrame() {
		return subBillDataFrame;
	}

	public void setSubBillDataFrame(List<SUBBillDataFrame> subBillDataFrame) {
		this.subBillDataFrame = subBillDataFrame;
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

}