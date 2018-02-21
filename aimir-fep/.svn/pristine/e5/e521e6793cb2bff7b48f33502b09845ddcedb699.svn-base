package com.aimir.fep.protocol.fmp.frame.service.entry;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.service.Entry;
import com.aimir.fep.util.Hex;

/**
 * modemSPNMSEntry
 *
 * @author Sung Han Lim (designer@nuritelecom.com)
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "modemSPNMSEntry", propOrder = {
		"moSPId",
		"moSPParentNodeId",
		"moSPRssi",
		"moSPLQI",
		"moSPEtx",
		"moSPCpuUsage",
		"moSPMemoryUsage",
		"moSPTxDataPacketSize",
		"moSPHopCount"
})

public class modemSPNMSEntry extends Entry {
	public OCTET moSPId = new OCTET(8);
	public OCTET moSPParentNodeId = new OCTET(8);
	public BYTE moSPRssi = new BYTE();
	public BYTE moSPLQI = new BYTE();
	public WORD moSPEtx = new WORD(2);
	public BYTE moSPCpuUsage = new BYTE();
	public BYTE moSPMemoryUsage = new BYTE();
	public UINT moSPTxDataPacketSize = new UINT(4);
	public BYTE moSPHopCount = new BYTE(2);
	
	
	
	@XmlTransient
    public OCTET getMoSPId() {
		return moSPId;
	}

	public void setMoSPId(OCTET moSPId) {
		this.moSPId = moSPId;
	}

	@XmlTransient
	public OCTET getMoSPParentNodeId() {
		return moSPParentNodeId;
	}

	public void setMoSPParentNodeId(OCTET moSPParentNodeId) {
		this.moSPParentNodeId = moSPParentNodeId;
	}

	@XmlTransient
	public BYTE getMoSPRssi() {
		return moSPRssi;
	}

	public void setMoSPRssi(BYTE moSPRssi) {
		this.moSPRssi = moSPRssi;
	}

	@XmlTransient
	public BYTE getMoSPLQI() {
		return moSPLQI;
	}

	public void setMoSPLQI(BYTE moSPLQI) {
		this.moSPLQI = moSPLQI;
	}
	
	@XmlTransient
	public WORD getMoSPEtx() {
		return moSPEtx;
	}

	public void setMoSPEtx(WORD moSPEtx) {
		this.moSPEtx = moSPEtx;
	}

	@XmlTransient
	public BYTE getMoSPCpuUsage() {
		return moSPCpuUsage;
	}

	public void setMoSPCpuUsage(BYTE moSPCpuUsage) {
		this.moSPCpuUsage = moSPCpuUsage;
	}

	@XmlTransient
	public BYTE getMoSPMemoryUsage() {
		return moSPMemoryUsage;
	}

	public void setMoSPMemoryUsage(BYTE moSPMemoryUsage) {
		this.moSPMemoryUsage = moSPMemoryUsage;
	}

	@XmlTransient
	public UINT getMoSPTxDataPacketSize() {
		return moSPTxDataPacketSize;
	}

	public void setMoSPTxDataPacketSize(UINT moSPTxDataPacketSize) {
		this.moSPTxDataPacketSize = moSPTxDataPacketSize;
	}
	
	@XmlTransient
	public BYTE getMoSPHopCount() {
		return moSPHopCount;
	}

	public void setMoSPHopCount(BYTE moSPHopCount) {
		this.moSPHopCount = moSPHopCount;
	}
	
	public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("CLASS["+this.getClass().getName()+"]\n");
//        sb.append("moSPId: " + moSPId + "\n");
//        sb.append("moSPParentNodeId: " + moSPParentNodeId + "\n");
        sb.append("moSPId: " + moSPId.toHexString() + "\n");
        sb.append("moSPParentNodeId: " + moSPParentNodeId.toHexString() + "\n");
        sb.append("moSPRssi: " + moSPRssi + "\n");
        sb.append("moSPLQI: " + moSPLQI + "\n");
        sb.append("moSPEtx: " + moSPEtx + "\n");
        sb.append("moSPCpuUsage: " + moSPCpuUsage + "\n");
        sb.append("moSPMemoryUsage: " + moSPMemoryUsage + "\n");
        sb.append("moSPTxDataPacketSize: " + moSPTxDataPacketSize + "\n");
        sb.append("moSPHopCount: " + moSPHopCount + "\n");
        
		return sb.toString();
	}

}
