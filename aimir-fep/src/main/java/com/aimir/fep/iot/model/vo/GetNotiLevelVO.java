package com.aimir.fep.iot.model.vo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.iot.domain.resources.NotiSettingReq;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "returnCd",
    "returnMsg",
    "item"
})
@XmlRootElement(name = "ssnRoot")
public class GetNotiLevelVO {
	
	@XmlElement(name = "returnCd")
	protected String returnCd;
	
	@XmlElement(name = "returnMsg")
	protected String returnMsg;
	
	@XmlElement(name = "item")
	protected List<NotiSettingReq> item;

	/*getter&setter*/
	public String getReturnCd() {
		return returnCd;
	}

	public void setReturnCd(String returnCd) {
		this.returnCd = returnCd;
	}

	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

	public List<NotiSettingReq> getItem() {
		return item;
	}

	public void setItem(List<NotiSettingReq> item) {
		this.item = item;
	}
	
	
}
