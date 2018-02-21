package com.aimir.fep.iot.model.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "returnCd",
    "returnMsg"
})
@XmlRootElement(name = "ssnRoot")
public class SettingVO {
	
	@XmlElement(name = "returnCd")
	protected String returnCd;
	
	@XmlElement(name = "returnMsg")
	protected String returnMsg;

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
	
	
}
