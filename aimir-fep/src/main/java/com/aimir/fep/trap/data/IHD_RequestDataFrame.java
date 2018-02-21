package com.aimir.fep.trap.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.service.entry.byteEntry;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class IHD_RequestDataFrame {
	private static Log log = LogFactory.getLog(IHD_RequestDataFrame.class);
	private byte[] requestBytes = null;
	private byte sendTarget;
	private byte receiveTarget;
	private byte CMD;
	private byte[] dataFrame;
	private byte[] dataLength ; //Big Endian
	private byte[] data;
	
	public static final int OFS_STX  			= 0;
	public static final int OFS_SEND_TARGET  	= 1;
	public static final int OFS_RECEIVE_TARGET  = 2;
	public static final int OFS_DATA_LENGTH  	= 3;
	public static final int OFS_DATA     		= 5;
	
	
	public IHD_RequestDataFrame(){
	}


	public byte[] getBytes(String sendTarget, String receiveTarget, String dataCMD, String dataTypeFrame) throws Exception {

		this.sendTarget = Hex.encode(sendTarget)[0];
		this.receiveTarget = Hex.encode(receiveTarget)[0];
		this.CMD = Hex.encode(dataCMD)[0];
		this.dataFrame = Hex.encode(dataTypeFrame);
		
		this.data = new byte[this.dataFrame.length+1];
		this.data = DataUtil.append(Hex.encode(dataCMD), this.dataFrame);
		
		this.requestBytes = new byte[6+this.data.length];
		
		// + STX, SendTarget, ReceiveTarget
		byte[] head = new byte[3];
		head[0] = Hex.encode("02")[0];
		head[1] = this.sendTarget;
		head[2] = this.receiveTarget;
		
		this.dataLength = DataUtil.get2ByteToInt(this.data.length);
		DataUtil.convertEndian(this.dataLength);	
		
		// + DATALENGTH
		this.requestBytes = DataUtil.append(head, this.dataLength);
		// + DATA
		this.requestBytes = DataUtil.append(this.requestBytes, this.data);
		// + ETX
		this.requestBytes = DataUtil.append(this.requestBytes, Hex.encode("03"));
		
		
		return this.requestBytes;
	}

		
}
