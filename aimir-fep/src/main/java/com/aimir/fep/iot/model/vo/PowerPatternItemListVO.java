package com.aimir.fep.iot.model.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "readingDate",
    "lpInterval",
    "fapData",
    "baseLoad"
})
@XmlRootElement(name = "item")
public class PowerPatternItemListVO {
	@XmlElement(name = "readingDate")
	protected String readingDate;
	
	@XmlElement(name = "lpInterval")
	protected String lpInterval;
	
	@XmlElement(name = "fapData")
	protected String fapData;
	
	@XmlElement(name = "baseLoad")
	protected String baseLoad;


	public String getReadingDate() {
		return readingDate;
	}

	public void setReadingDate(String readingDate) {
		this.readingDate = readingDate;
	}

	public String getLpInterval() {
		return lpInterval;
	}

	public void setLpInterval(String lpInterval) {
		this.lpInterval = lpInterval;
	}

	public String getFapData() {
		return fapData;
	}

	public void setFapData(String fapData) {
		this.fapData = fapData;
	}
	
	public String getBaseLoad() {
		return baseLoad;
	}

	public void setBaseLoad(String baseLoad) {
		this.baseLoad = baseLoad;
	}
}
