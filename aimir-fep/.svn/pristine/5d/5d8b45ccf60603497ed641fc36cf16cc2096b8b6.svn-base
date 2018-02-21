package com.aimir.fep.trap.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.service.entry.byteEntry;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class IHD_ReceiveDataFrame {
	private static Log log = LogFactory.getLog(IHD_ReceiveDataFrame.class);
	private byte[] receiveBytes = null;
	private byte stx;
	private byte etx;
	private byte sendTarget;
	private byte receiveTarget;
	private byte[] dataLength = new byte[2]; //Big Endian
	private byte[] data;
	
	public static final int OFS_STX  			= 0;
	public static final int OFS_SEND_TARGET  	= 1;
	public static final int OFS_RECEIVE_TARGET  = 2;
	public static final int OFS_DATA_LENGTH  	= 3;
	public static final int OFS_DATA     		= 5;
	
	
	public IHD_ReceiveDataFrame(byte[] receiveBytes) throws Exception {
		log.debug("IHD_ReceiveDataFrame start!!");
		log.debug("recevieBytes : " + Hex.getHexDump(receiveBytes));
		this.receiveBytes 	= receiveBytes;
		this.parse();
	}
	
	@SuppressWarnings("static-access")
	public void parse() throws Exception{
		//STX
		this.stx = this.receiveBytes[0];
//		System.arraycopy(this.receiveBytes, this.OFS_STX, this.stx, 0, 1);
		
		//SendTarget
		this.sendTarget = this.receiveBytes[1];
//		System.arraycopy(this.receiveBytes, this.OFS_SEND_TARGET, this.sendTarget, 0, 1);
		
		//ReceiveTarget
		this.receiveTarget = this.receiveBytes[2];
//		System.arraycopy(this.receiveBytes, this.OFS_RECEIVE_TARGET, this.receiveTarget, 0, 1);
		
		//DataLength (Big Endian)
		System.arraycopy(this.receiveBytes, 3, this.dataLength, 0, 2);
		DataUtil.convertEndian(this.dataLength);
		
		//data
		int length = DataUtil.getIntTo2Byte(this.dataLength);
		data = new byte[length];
		System.arraycopy(this.receiveBytes, this.OFS_DATA, this.data, 0, length);
		
		//ETX
		this.etx = this.receiveBytes[this.OFS_DATA+length];
//		System.arraycopy(this.receiveBytes, this.OFS_DATA+length, this.etx, 0, 1);
	}

	public byte[] getReceiveBytes() {
		return receiveBytes;
	}

	public void setReceiveBytes(byte[] receiveBytes) {
		this.receiveBytes = receiveBytes;
	}

	public byte getStx() {
		return stx;
	}

	public void setStx(byte stx) {
		this.stx = stx;
	}

	public byte getEtx() {
		return etx;
	}

	public void setEtx(byte etx) {
		this.etx = etx;
	}

	public byte getSendTarget() {
		return sendTarget;
	}

	public void setSendTarget(byte sendTarget) {
		this.sendTarget = sendTarget;
	}

	public byte getReceiveTarget() {
		return receiveTarget;
	}

	public void setReceiveTarget(byte receiveTarget) {
		this.receiveTarget = receiveTarget;
	}

	public byte[] getDataLength() {
		return dataLength;
	}

	public void setDataLength(byte[] dataLength) {
		this.dataLength = dataLength;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	

	
}
