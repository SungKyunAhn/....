package com.aimir.fep.iot.model.vo;

import java.io.Serializable;

public class SensorVO extends DeviceVO implements Serializable {

	private static final long serialVersionUID = -3370575299047201462L;

	private String SID;
	private String DID;
	private String LOCAL_IP;
	private String NETWORK_CD;
	private Integer UPLOAD_INTERVAL;
	private Double RSSI;
	private int WAKEUP_PERIOD;
	private int GPS_PERIOD;
	private int HRM_PERIOD;
	private int BEACON_PERIOD;
	
	//추가
	//2016 11 28 차병준
	private String DCU_DT;
	
	

	public String getDCU_DT() {
		return DCU_DT;
	}
	public void setDCU_DT(String dCU_DT) {
		DCU_DT = dCU_DT;
	}
	public String getSID() {
		return SID;
	}
	public void setSID(String sID) {
		SID = sID;
	}
	public String getDID() {
		return DID;
	}
	public void setDID(String dID) {
		DID = dID;
	}
	public String getLOCAL_IP() {
		return LOCAL_IP;
	}
	public void setLOCAL_IP(String lOCAL_IP) {
		LOCAL_IP = lOCAL_IP;
	}
	public String getNETWORK_CD() {
		return NETWORK_CD;
	}
	public void setNETWORK_CD(String nETWORK_CD) {
		NETWORK_CD = nETWORK_CD;
	}
	public Integer getUPLOAD_INTERVAL() {
		return UPLOAD_INTERVAL;
	}
	public void setUPLOAD_INTERVAL(Integer uPLOAD_INTERVAL) {
		UPLOAD_INTERVAL = uPLOAD_INTERVAL;
	}
	public Double getRSSI() {
		return RSSI;
	}
	public void setRSSI(Double rSSI) {
		RSSI = rSSI;
	}
	public int getWAKEUP_PERIOD() {
		return WAKEUP_PERIOD;
	}
	public void setWAKEUP_PERIOD(int wAKEUP_PERIOD) {
		WAKEUP_PERIOD = wAKEUP_PERIOD;
	}
	public int getGPS_PERIOD() {
		return GPS_PERIOD;
	}
	public void setGPS_PERIOD(int gPS_PERIOD) {
		GPS_PERIOD = gPS_PERIOD;
	}
	public int getHRM_PERIOD() {
		return HRM_PERIOD;
	}
	public void setHRM_PERIOD(int hRM_PERIOD) {
		HRM_PERIOD = hRM_PERIOD;
	}
	public int getBEACON_PERIOD() {
		return BEACON_PERIOD;
	}
	public void setBEACON_PERIOD(int bEACON_PERIOD) {
		BEACON_PERIOD = bEACON_PERIOD;
	}

}
