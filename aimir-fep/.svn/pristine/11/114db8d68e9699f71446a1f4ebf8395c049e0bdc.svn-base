package com.aimir.fep.protocol.fmp.frame.service.entry;

import javax.xml.bind.annotation.XmlTransient;

import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.protocol.fmp.frame.service.Entry;

/**
 * groupInfoEntry
 *
 * @author goodjob
 */
public class groupInfoEntry extends Entry {
	public OCTET pGroupName = 
			new OCTET(256);
	
	public UINT nMemberCnt = 
		new UINT(4);
	
	public OCTET pMemberInfo = new OCTET();

	@XmlTransient
	public OCTET getPGroupName() {
		return pGroupName;
	}

	public void setPGroupName(OCTET pGroupName) {
		this.pGroupName = pGroupName;
	}

	@XmlTransient
	public UINT getNMemberCnt() {
		return nMemberCnt;
	}

	public void setNMemberCnt(UINT nMemberCnt) {
		this.nMemberCnt = nMemberCnt;
	}

	@XmlTransient
	public OCTET getPMemberInfo() {
		return pMemberInfo;
	}

	public void setPMemberInfo(OCTET pMemberInfo) {
		this.pMemberInfo = pMemberInfo;
	}

	public String toString()
    {
        StringBuffer sb = new StringBuffer();

		sb.append("CLASS["+this.getClass().getName()+"]\n");
		sb.append("pGroupName: " + pGroupName + "\n");
		sb.append("nMemberCnt: " + nMemberCnt + "\n");
        sb.append("pMemberInfo: " + pMemberInfo.toHexString() + "\n");
        return sb.toString();
    }
}
