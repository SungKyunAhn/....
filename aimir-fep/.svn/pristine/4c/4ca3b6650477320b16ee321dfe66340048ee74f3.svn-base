package com.aimir.fep.util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "groupTypeInfo", propOrder = {
    "groupType",
    "groupName",
    "member"
})
public class GroupTypeInfo implements java.io.Serializable {
	
	private String groupType;
	
	private String groupName;
	
	private String member;

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}
	
	public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("groupType info[");
        sb.append("(groupType=").append(groupType).append("),");
        sb.append("(groupName=").append(groupName).append("),");
        sb.append("(member=").append(member).append("),");
        sb.append("]\n");

        return sb.toString();
    }
	
}
