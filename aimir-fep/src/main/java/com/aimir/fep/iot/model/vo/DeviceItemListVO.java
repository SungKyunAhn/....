package com.aimir.fep.iot.model.vo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"devId",
		"model",
		"sensorId",
		"power",
		"custNo",
		"custType",
		"latitude",
		"longitude",
		"hVersion",
		"sVersion",
		"name",
		"desc",
		"status",
		"gender",
		"age",
		"heartRate",
		"photo1",
		"guardianName",
		"guardianMobile",
		"movingRoutes"
})

@XmlRootElement(name = "item", namespace="http://www.onem2m.org/xml/protocols")
public class DeviceItemListVO {
	
	@XmlElement(name = "devId", required = true)
	protected String devId;
	
	@XmlElement(name = "model", required = true)
	protected String model;
	
	@XmlElement(name = "sensorId", required = true)
	protected String sensorId;
	
	@XmlElement(name = "power", required = true)
	protected String power;
	
	@XmlElement(name = "custNo", required = true)
	protected String custNo;	

	@XmlElement(name = "custType", required = true)
	protected String custType;
	
	@XmlElement(name = "latitude", required = true)
	protected String latitude;
	
	@XmlElement(name = "longitude", required = true)
	protected String longitude;
	
	@XmlElement(name = "hVersion", required = true)
	protected String hVersion;
	
	@XmlElement(name = "sVersion", required = true)
	protected String sVersion;
	
	@XmlElement(name = "name", required = true)
	protected String name;
	
	@XmlElement(name = "desc", required = true)
	protected String desc;
	
	@XmlElement(name = "status", required = true)
	protected String status;
	
	@XmlElement(name = "gender", required = true)
	protected String gender;
	
	@XmlElement(name = "age", required = true)
	protected String age;
	
	@XmlElement(name = "heartRate", required = true)
	protected String heartRate;
	
	@XmlElement(name = "photo1", required = true)
	protected String photo1;

	@XmlElement(name = "guardianName", required = true)
	protected String guardianName;
	
	@XmlElement(name = "guardianMobile", required = true)
	protected String guardianMobile;
	
	@XmlElement(name = "movingRoutes", required = true)
	protected List<MovingRouteItemListVO> movingRoutes;
	
	
	/**
	 * Getter & Setter
	 * */
	
	
	public String getCustType() {
		return custType;
	}

	public void setCustType(String custType) {
		this.custType = custType;
	}
	
	public String getDevId() {
		return devId;
	}

	public void setDevId(String devId) {
		this.devId = devId;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public String getPower() {
		return power;
	}

	public void setPower(String power) {
		this.power = power;
	}
	
	public String getCustNo() {
		return custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getHeartRate() {
		return heartRate;
	}

	public void setHeartRate(String heartRate) {
		this.heartRate = heartRate;
	}

	public String getPhoto1() {
		return photo1;
	}

	public void setPhoto1(String photo1) {
		this.photo1 = photo1;
	}

	public String getGuardianName() {
		return guardianName;
	}

	public void setGuardianName(String guardianName) {
		this.guardianName = guardianName;
	}

	public String getGuardianMobile() {
		return guardianMobile;
	}

	public void setGuardianMobile(String guardianMobile) {
		this.guardianMobile = guardianMobile;
	}

	public List<MovingRouteItemListVO> getMovingRoutes() {
		return movingRoutes;
	}

	public void setMovingRoutes(List<MovingRouteItemListVO> movingRoutes) {
		this.movingRoutes = movingRoutes;
	}
	
	
}
