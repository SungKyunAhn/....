package com.aimir.fep.iot.model.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"sensorId",
		"manufactuer",
		"model",
		"AcqSenodrId",
		"latitude",
		"longitude",
		"hVersion",
		"sVersion",
		"inclinedPlane",
		"AngleX",
		"AngleY",
		"AngleZ",
		"minThrs",
		"maxThrs",
		"status"
})

@XmlRootElement(name = "item", namespace="http://www.onem2m.org/xml/protocols")
public class CuttingAreaItemListVO {
	
	@XmlElement(name = "sensorId", required = true)
	protected String sensorId;
	
	@XmlElement(name = "manufactuer", required = true)
	protected String manufactuer;
	
	@XmlElement(name = "model", required = true)
	protected String model;
	
	@XmlElement(name = "AcqSenodrId", required = true)
	protected String AcqSenodrId;
	
	@XmlElement(name = "latitude", required = true)
	protected String latitude;
	
	@XmlElement(name = "longitude", required = true)
	protected String longitude;
	
	@XmlElement(name = "hVersion", required = true)
	protected String hVersion;
	
	@XmlElement(name = "sVersion", required = true)
	protected String sVersion;
	
	@XmlElement(name = "inclinedPlane", required = true)
	protected double inclinedPlane;
	
	@XmlElement(name = "AngleX", required = true)
	protected double AngleX;
	
	@XmlElement(name = "AngleY", required = true)
	protected double AngleY;
	
	@XmlElement(name = "AngleZ", required = true)
	protected double AngleZ;
	
	@XmlElement(name = "minThrs", required = true)
	protected String minThrs;
	
	@XmlElement(name = "maxThrs", required = true)
	protected String maxThrs;
	
	@XmlElement(name = "status", required = true)
	protected String status;

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public String getManufactuer() {
		return manufactuer;
	}

	public void setManufactuer(String manufactuer) {
		this.manufactuer = manufactuer;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getAcqSenodrId() {
		return AcqSenodrId;
	}

	public void setAcqSenodrId(String acqSenodrId) {
		AcqSenodrId = acqSenodrId;
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

	public String gethVersion() {
		return hVersion;
	}

	public void sethVersion(String hVersion) {
		this.hVersion = hVersion;
	}

	public String getsVersion() {
		return sVersion;
	}

	public void setsVersion(String sVersion) {
		this.sVersion = sVersion;
	}

	public double getInclinedPlane() {
		return inclinedPlane;
	}

	public void setInclinedPlane(double inclinedPlane) {
		this.inclinedPlane = inclinedPlane;
	}

	public double getAngleX() {
		return AngleX;
	}

	public void setAngleX(double angleX) {
		AngleX = angleX;
	}

	public double getAngleY() {
		return AngleY;
	}

	public void setAngleY(double angleY) {
		AngleY = angleY;
	}

	public double getAngleZ() {
		return AngleZ;
	}

	public void setAngleZ(double angleZ) {
		AngleZ = angleZ;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
