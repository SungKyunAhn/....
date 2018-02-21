package com.aimir.fep.protocol.fmp.frame.service.entry;

import javax.xml.bind.annotation.XmlTransient;

import com.aimir.fep.protocol.fmp.datatype.BOOL;
import com.aimir.fep.protocol.fmp.datatype.HEX;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.service.Entry;

public class modemG3Entry extends Entry {

	public HEX moG3Id = new HEX(8);
	
	public WORD moG3ShortId = new WORD(2);
	
	public OCTET moG3NodeKind = new OCTET(20);
	
	public WORD moG3NoOfMeters = new WORD(2);
	
	public WORD moG3FwVer = new WORD(2);
	
	public WORD moG3FwBuild = new WORD(2);
	
	public WORD moG3HwVer = new WORD(2);
	
	public BOOL moG3Status = new BOOL();
	
	public TIMESTAMP moG3LastOnLine = new TIMESTAMP(7);
	
	public TIMESTAMP moG3LastOffLine = new TIMESTAMP(7);
	
	public TIMESTAMP moG3Install = new TIMESTAMP(7); 
	
	@XmlTransient
	public HEX getMoG3Id() {
		return moG3Id;
	}

	public void setMoG3Id(HEX moG3Id) {
		this.moG3Id = moG3Id;
	}

	@XmlTransient
	public WORD getMoG3ShortId() {
		return moG3ShortId;
	}

	public void setMoG3ShortId(WORD moG3ShortId) {
		this.moG3ShortId = moG3ShortId;
	}

	@XmlTransient
	public OCTET getMoG3NodeKind() {
		return moG3NodeKind;
	}

	public void setMoG3NodeKind(OCTET moG3NodeKind) {
		this.moG3NodeKind = moG3NodeKind;
	}

	@XmlTransient
	public WORD getMoG3NoOfMeters() {
		return moG3NoOfMeters;
	}

	public void setMoG3NoOfMeters(WORD moG3NoOfMeters) {
		this.moG3NoOfMeters = moG3NoOfMeters;
	}

	@XmlTransient
	public WORD getMoG3FwVer() {
		return moG3FwVer;
	}

	public void setMoG3FwVer(WORD moG3FwVer) {
		this.moG3FwVer = moG3FwVer;
	}

	@XmlTransient
	public WORD getMoG3FwBuild() {
		return moG3FwBuild;
	}

	public void setMoG3FwBuild(WORD moG3FwBuild) {
		this.moG3FwBuild = moG3FwBuild;
	}

	@XmlTransient
	public WORD getMoG3HwVer() {
		return moG3HwVer;
	}

	public void setMoG3HwVer(WORD moG3HwVer) {
		this.moG3HwVer = moG3HwVer;
	}

	@XmlTransient
	public BOOL getMoG3Status() {
		return moG3Status;
	}

	public void setMoG3Status(BOOL moG3Status) {
		this.moG3Status = moG3Status;
	}

	@XmlTransient
	public TIMESTAMP getMoG3LastOnLine() {
		return moG3LastOnLine;
	}

	public void setMoG3LastOnLine(TIMESTAMP moG3LastOnLine) {
		this.moG3LastOnLine = moG3LastOnLine;
	}

	@XmlTransient
	public TIMESTAMP getMoG3LastOffLine() {
		return moG3LastOffLine;
	}

	public void setMoG3LastOffLine(TIMESTAMP moG3LastOffLine) {
		this.moG3LastOffLine = moG3LastOffLine;
	}

	@XmlTransient
	public TIMESTAMP getMoG3Install() {
		return moG3Install;
	}

	public void setMoG3Install(TIMESTAMP moG3Install) {
		this.moG3Install = moG3Install;
	}

	@Override
	public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("CLASS["+this.getClass().getName()+"]\n");
        sb.append("moG3Id: " + moG3Id + "\n");
        sb.append("moG3ShortId" + moG3ShortId  + "\n");
        sb.append("moG3NodeKind : " + moG3NodeKind  + "\n");
        sb.append("moG3NoOfMeters : " + moG3NoOfMeters  + "\n");
        sb.append("moG3FwVer : " + moG3FwVer + "\n");
        sb.append("moG3FwBuild : " + moG3FwBuild + "\n");
        sb.append("moG3HwVer : " + moG3HwVer + "\n");
        sb.append("moG3Status  : " + moG3Status  + "\n");
        sb.append("moG3LastOnLine  : " + moG3LastOnLine  + "\n");
        sb.append("moG3LastOffLine  : " + moG3LastOffLine  + "\n");
        sb.append("moG3Install  : " + moG3Install  + "\n");

        return sb.toString();
	}

}
