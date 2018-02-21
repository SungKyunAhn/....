package com.aimir.fep.protocol.nip.frame;

import com.aimir.fep.util.Hex;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.ByteArrayOutputStream;

public class NetworkStatusSub1GhzForSORIA extends NetworkStatus {
	private byte[] parentNode = new byte[8];
	private byte rssi;
	private byte lqi;
    private byte[] etx = new byte[2];
	private byte cpu;
	private byte memory;
	private byte[] totalTx = new byte[4];

	public String getParentNode() {
		return Hex.decode(parentNode);
	}

	public void setParentNode(String parentNode) {
		this.parentNode = Hex.encode(parentNode);
	}

	public byte getRssi() {
		return rssi;
	}

	public void setRssi(byte rssi) {
		this.rssi = rssi;
	}

    public byte getLqi() {
        return lqi;
    }

    public void setLqi(byte lqi) {
        this.lqi = lqi;
    }

	public byte[] getEtx() {
		return etx;
	}

	public void setEtx(byte[] etx) {
		this.etx = etx;
	}

	public byte getCpu() {
		return cpu;
	}

	public void setCpu(byte cpu) {
		this.cpu = cpu;
	}

	public byte getMemory() {
		return memory;
	}

	public void setMemory(byte memory) {
		this.memory = memory;
	}

	public byte[] getTotalTx() {
		return totalTx;
	}

	public void setTotalTx(byte[] totalTx) {
		this.totalTx = totalTx;
	}

	public void decode(byte[] bx) {
		int pos = 0;

		System.arraycopy(bx, pos, parentNode, 0, parentNode.length);
		pos += parentNode.length;

		byte[] data = new byte[1];
		System.arraycopy(bx, pos, data, 0, data.length);
		rssi = data[0];
		pos += data.length;

        data = new byte[1];
        System.arraycopy(bx, pos, data, 0, data.length);
        lqi = data[0];
        pos += data.length;

        System.arraycopy(bx, pos, etx, 0, etx.length);
		pos += etx.length;

		data = new byte[1];
		System.arraycopy(bx, pos, data, 0, data.length);
		cpu = data[0];
		pos += data.length;

		data = new byte[1];
		System.arraycopy(bx, pos, data, 0, data.length);
		memory = data[0];
		pos += data.length;

		System.arraycopy(bx, pos, totalTx, 0, totalTx.length);

	}

	public byte[] encode() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(parentNode);
		out.write(rssi);
        out.write(lqi);
		out.write(etx);
		out.write(cpu);
		out.write(memory);
		out.write(totalTx);
		byte[] b = out.toByteArray();
		out.close();

		return b;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
