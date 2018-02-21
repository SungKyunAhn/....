package com.aimir.fep.iot.model.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"latitude",
		"longitude",
		"devId",
		"date"
})

@XmlRootElement(name = "movingRoutes", namespace="http://www.onem2m.org/xml/protocols")
public class MovingRouteItemListVO {
	@XmlElement(name = "latitude", required = true)
	protected String latitude;
	
	@XmlElement(name = "longitude", required = true)
	protected String longitude;
	
	@XmlElement(name = "devId", required = true)
	protected String devId;
	
	@XmlElement(name = "date", required = true)
	protected String date;	
	
	
	/*getter & setter*/
	
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	public String getDevId() {
		return devId;
	}

	public void setDevId(String devId) {
		this.devId = devId;
	}
	
	
}
