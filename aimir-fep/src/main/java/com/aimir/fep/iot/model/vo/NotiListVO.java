package com.aimir.fep.iot.model.vo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"returnCd",
		"returnMsg",
		"item"
})

@XmlRootElement(name = "ssnRoot", namespace="http://www.onem2m.org/xml/protocols")
public class NotiListVO {
	
	@XmlElement(name = "returnCd", required = true)
	protected String returnCd;
	
	@XmlElement(name = "returnMsg", required = true)
	protected  String  returnMsg;

	@XmlElement(name = "item", required = true)
	protected List<NotiItemListVO> item;
	


	public String getReturnCd() {
		return returnCd;
	}

	public void setReturnCd(String returnCd) {
		this.returnCd = returnCd;
	}

	
	public String getReturnMsg() {
		return returnMsg;
	}

	public List<NotiItemListVO> getItem() {
		return item;
	}

	public void setItem(List<NotiItemListVO> item) {
		this.item = item;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}


	
}
