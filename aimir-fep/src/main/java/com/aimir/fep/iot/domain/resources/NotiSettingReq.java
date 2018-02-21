package com.aimir.fep.iot.domain.resources;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
	"custNo",
    "notiEW001",
    "notiEW002",
    "notiAW001",
    "notiAW002",
    "notiEM002",
    "notiAW003",
    "radius",
    "latitude",
    "longitude",
    "notiStr",
    "telNumber",
    "isVal1",
    "isVal2",
    "isVal3",
    "isVal4",
    "isVal5",
    "isVal6",
    "isVal7",
    "custType"
})
@XmlRootElement(name = "item")
public class NotiSettingReq {
	@XmlElement(name = "custNo")
	protected String custNo;	
	
	@XmlElement(name = "notiEW001")
	protected String notiEW001;
	
	@XmlElement(name = "notiEW002")
	protected String notiEW002;
	
	@XmlElement(name = "notiAW001")
	protected String notiAW001;
	
	@XmlElement(name = "notiAW002")
	protected String notiAW002;
	
	@XmlElement(name = "notiEM002")
	protected String notiEM002;
	
	@XmlElement(name = "notiAW003")
	protected String notiAW003;
	
	@XmlElement(name = "radius")
	protected String radius;

	@XmlElement(name = "latitude")
	protected String latitude;
	
	@XmlElement(name = "longitude")
	protected String longitude;
	
	
	
	private String notiStr;
	
	private String telNumber;
	
	private String isVal1;
	private String isVal2;
	private String isVal3;
	private String isVal4;
	private String isVal5;
	private String isVal6;
	private String isVal7;
	private String custType;

	
	
	
	public String getIsVal1() {
		return isVal1;
	}

	public void setIsVal1(String isVal1) {
		this.isVal1 = isVal1;
	}

	public String getIsVal2() {
		return isVal2;
	}

	public void setIsVal2(String isVal2) {
		this.isVal2 = isVal2;
	}

	public String getIsVal3() {
		return isVal3;
	}

	public void setIsVal3(String isVal3) {
		this.isVal3 = isVal3;
	}

	public String getIsVal4() {
		return isVal4;
	}

	public void setIsVal4(String isVal4) {
		this.isVal4 = isVal4;
	}

	public String getIsVal5() {
		return isVal5;
	}

	public void setIsVal5(String isVal5) {
		this.isVal5 = isVal5;
	}

	public String getIsVal6() {
		return isVal6;
	}

	public void setIsVal6(String isVal6) {
		this.isVal6 = isVal6;
	}

	public String getIsVal7() {
		return isVal7;
	}

	public void setIsVal7(String isVal7) {
		this.isVal7 = isVal7;
	}

	public String getTelNumber() {
		return telNumber;
	}

	public void setTelNumber(String telNumber) {
		this.telNumber = telNumber;
	}

	public String getNotiStr() {
		return notiStr;
	}

	public void setNotiStr(String notiStr) {
		this.notiStr = notiStr;
	}

	public String getCustNo() {
		return custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getNotiEW001() {
		return notiEW001;
	}

	public void setNotiEW001(String notiEW001) {
		this.notiEW001 = notiEW001;
	}

	public String getNotiEW002() {
		return notiEW002;
	}

	public void setNotiEW002(String notiEW002) {
		this.notiEW002 = notiEW002;
	}

	public String getNotiAW001() {
		return notiAW001;
	}

	public void setNotiAW001(String notiAW001) {
		this.notiAW001 = notiAW001;
	}

	public String getNotiAW002() {
		return notiAW002;
	}

	public void setNotiAW002(String notiAW002) {
		this.notiAW002 = notiAW002;
	}

	public String getNotiEM002() {
		return notiEM002;
	}

	public void setNotiEM002(String notiEM002) {
		this.notiEM002 = notiEM002;
	}

	public String getNotiAW003() {
		return notiAW003;
	}

	public void setNotiAW003(String notiAW003) {
		this.notiAW003 = notiAW003;
	}

	public String getRadius() {
		return radius;
	}

	public void setRadius(String radius) {
		this.radius = radius;
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

	public String getCustType() {
		return custType;
	}

	public void setCustType(String custType) {
		this.custType = custType;
	}

	
}
