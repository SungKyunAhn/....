package com.aimir.fep.iot.model.vo;


import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"returnCd",
		"returnMsg",
		"totalItem",
		"curentPage",
		"item"
})

@XmlRootElement(name = "ssnRoot", namespace="http://www.onem2m.org/xml/protocols")
public class DeviceListVO {
	@XmlElement(name = "returnCd", required = true)
	protected String returnCd;
	
	@XmlElement(name = "returnMsg", required = true)
	protected String returnMsg;
	
	@XmlElement(name = "totalItem", required = true)
	protected String totalItem;
	
	@XmlElement(name = "curentPage", required = true)
	protected String curentPage;
	
	@XmlElement(name = "item", required = true)
	protected List<DeviceItemListVO> item;

	
	/**
	 * Getter & Setter
	 * */
	
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

	public String getTotalItem() {
		return totalItem;
	}

	public void setTotalItem(String totalItem) {
		this.totalItem = totalItem;
	}

	public String getCurentPage() {
		return curentPage;
	}

	public void setCurentPage(String curentPage) {
		this.curentPage = curentPage;
	}

	public List<DeviceItemListVO> getItem() {
		return item;
	}

	public void setItem(List<DeviceItemListVO> item) {
		this.item = item;
	}
	
	

	
	
}
