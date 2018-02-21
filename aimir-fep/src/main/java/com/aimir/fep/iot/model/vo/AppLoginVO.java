package com.aimir.fep.iot.model.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"returnCd",
		"returnMsg",
		"accessCode",
		"userName"
		
})

@XmlRootElement(name = "ssnRoot", namespace="http://www.onem2m.org/xml/protocols")
public class AppLoginVO {
	@XmlElement(name = "returnCd", required = true)
	protected String returnCd;
	
	@XmlElement(name = "returnMsg", required = true)
	protected String returnMsg;
	
	@XmlElement(name = "accessCode", required = true)
	protected String accessCode;
	
	@XmlElement(name = "userName", required = true)
	protected String userName;
	


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

	public String getAccessCode() {
		return accessCode;
	}

	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String toStr(){
		if(accessCode!=null && userName!=null){
			return "OK";
		}else{
			return null;
		}
	}
	
}
