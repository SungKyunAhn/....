package com.aimir.fep.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Group Info
 * 
 * @author goodjob
 * <pre>
 * &lt;complexType name="groupInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="groupKey" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="groupName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="memberCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="memberInfo" type="{http://server.ws.command.fep.aimir.com/}memberInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "groupInfo", propOrder = {
    "groupKey",
    "groupName",
    "memberCount",
    "memberInfo"
})
public class GroupInfo implements java.io.Serializable
{

	private static final long serialVersionUID = -9108052345298760406L;
	private int groupKey = 0;
	private String groupName = null;
    private int memberCount = 0;
    @XmlElement(nillable = true)
    private List<MemberInfo> memberInfo= new ArrayList<MemberInfo>(0);

    public GroupInfo() { }

    
    /**
	 * @return the groupKey
	 */
	public int getGroupKey() {
		return groupKey;
	}


	/**
	 * @param groupKey the groupKey to set
	 */
	public void setGroupKey(int groupKey) {
		this.groupKey = groupKey;
	}


	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}



	public List<MemberInfo> getMemberInfo() {
		return memberInfo;
	}



	public void setMemberInfo(List<MemberInfo> memberInfo) {
		this.memberInfo = memberInfo;
	}



	public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("group info[");
        sb.append("(groupKey=").append(groupKey).append("),");
        sb.append("(groupName=").append(groupName).append("),");
        sb.append("(memberCount=").append(memberCount).append("),");
        sb.append("(memberInfo=");
        for(int i = 0; i < memberInfo.size(); i++ ){
            sb.append(memberInfo.get(i).toString()).append("),");
        }
        sb.append(')');
        sb.append("]\n");

        return sb.toString();
    }
}
