package com.aimir.fep.iot.model.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"sensorId",
		"inclinedPlane",
		"minThrs",
		"maxThrs"
})

@XmlRootElement(name = "item", namespace="http://www.onem2m.org/xml/protocols")
public class CuttingAreaDegListVO {
	@XmlElement(name = "sensorId", required = true)
	protected String sensorId;
	
	@XmlElement(name = "inclinedPlane", required = true)
	protected String inclinedPlane;
	
	@XmlElement(name = "minThrs", required = true)
	protected String minThrs;
	
	@XmlElement(name = "maxThrs", required = true)
	protected String maxThrs;

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public String getInclinedPlane() {
		return inclinedPlane;
	}

	public void setInclinedPlane(String inclinedPlane) {
		this.inclinedPlane = inclinedPlane;
	}

	public String getMinThrs() {
		return minThrs;
	}

	public void setMinThrs(String minThrs) {
		this.minThrs = minThrs;
	}

	public String getMaxThrs() {
		return maxThrs;
	}

	public void setMaxThrs(String maxThrs) {
		this.maxThrs = maxThrs;
	}
	
	
}
