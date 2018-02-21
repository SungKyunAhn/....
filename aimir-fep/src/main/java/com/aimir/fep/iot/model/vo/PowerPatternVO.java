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
    "searchType",
    "item"
})
@XmlRootElement(name = "ssnRoot")
public class PowerPatternVO {
	@XmlElement(name = "returnCd")
	protected String returnCd;
	
	@XmlElement(name = "returnMsg")
	protected String returnMsg;

	@XmlElement(name = "searchType")
	protected String searchType;
	
	@XmlElement(name = "item")
	protected List<PowerPatternItemListVO> item;

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

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public List<PowerPatternItemListVO> getItem() {
		return item;
	}

	public void setItem(List<PowerPatternItemListVO> item) {
		this.item = item;
	}




}
