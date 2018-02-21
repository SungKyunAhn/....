package com.aimir.fep.iot.model.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"ipAddress",
		"analysisOption",
		"lastCommDt",
		"event",
		"thumbUrl1",
		"thumbUrl2",
		"thumbUrl3",
		"latitude",
		"longitude"
})

@XmlRootElement(name = "item", namespace="http://www.onem2m.org/xml/protocols")
public class IpCarmeraItemListVO {
	@XmlElement(name = "ipAddress", required = true)
	protected String ipAddress;
	
	@XmlElement(name = "analysisOption", required = true)
	protected String analysisOption;
	
	@XmlElement(name = "lastCommDt", required = true)
	protected String lastCommDt;
	
	@XmlElement(name = "event", required = true)
	protected String event;
	
	@XmlElement(name = "thumbUrl1", required = true)
	protected String thumbUrl1;
	
	@XmlElement(name = "thumbUrl2", required = true)
	protected String thumbUrl2;
	
	@XmlElement(name = "thumbUrl3", required = true)
	protected String thumbUrl3;
	
	@XmlElement(name = "latitude", required = true)
	protected String latitude;
	
	@XmlElement(name = "longitude", required = true)
	protected String longitude;

	
	
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getAnalysisOption() {
		return analysisOption;
	}

	public void setAnalysisOption(String analysisOption) {
		this.analysisOption = analysisOption;
	}

	public String getLastCommDt() {
		return lastCommDt;
	}

	public void setLastCommDt(String lastCommDt) {
		this.lastCommDt = lastCommDt;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getThumbUrl1() {
		return thumbUrl1;
	}

	public void setThumbUrl1(String thumbUrl1) {
		this.thumbUrl1 = thumbUrl1;
	}

	public String getThumbUrl2() {
		return thumbUrl2;
	}

	public void setThumbUrl2(String thumbUrl2) {
		this.thumbUrl2 = thumbUrl2;
	}

	public String getThumbUrl3() {
		return thumbUrl3;
	}

	public void setThumbUrl3(String thumbUrl3) {
		this.thumbUrl3 = thumbUrl3;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	
	
}
