/**
 * 
 */
package com.aimir.fep.protocol.nip.frame;

import java.io.ByteArrayOutputStream;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.aimir.fep.util.Hex;

public class NetworkStatusNBPLC extends NetworkStatus {
	private byte[] parentNode = new byte[2];
	private byte lqiSNR;

	public String getParentNode() {
		return Hex.decode(parentNode);
	}

	public void setParentNode(String parentNode) {
		this.parentNode = Hex.encode(parentNode);
	}

	public byte getLqiSNR() {
		return lqiSNR;
	}

	public void setLqiSNR(byte lqiSNR) {
		this.lqiSNR = lqiSNR;
	}

	public void setParentNode(byte[] parentNode) {
		this.parentNode = parentNode;
	}

	public void decode(byte[] bx) {
		int pos = 0;
		System.arraycopy(bx, pos, parentNode, 0, parentNode.length);
		pos += parentNode.length;

		byte[] data = new byte[1];
		System.arraycopy(bx, pos, data, 0, data.length);
		lqiSNR = data[0];
	}

	public byte[] encode() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(parentNode);
		out.write(lqiSNR);

		byte[] b = out.toByteArray();
		out.close();

		return b;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public void setRssi(byte rssi) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEtx(byte[] etx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCpu(byte cpu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMemory(byte memory) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTotalTx(byte[] totalTx) {
		// TODO Auto-generated method stub
		
	}
}
