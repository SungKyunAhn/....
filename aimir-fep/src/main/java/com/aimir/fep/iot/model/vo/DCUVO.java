package com.aimir.fep.iot.model.vo;

import java.io.Serializable;

public class DCUVO extends DeviceVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6120972582556021124L;
	
	private String DCU_ID;
	
	private Integer UPLOAD_INTERVAL;
	
	private String NETWORK_CD;
	
	private String MOBILE_MODE_CD;
	
	private String LOCAL_IP;
	
	private Integer PORT;
	
	private String SERVER_IP;
	
	private String SERVER_PORT;

	private String TAS_FW_VER;
	
	private String COORDI_FW_VER;
	
	
	
	
	public String getTAS_FW_VER() {
		return TAS_FW_VER;
	}
	public void setTAS_FW_VER(String tAS_FW_VER) {
		TAS_FW_VER = tAS_FW_VER;
	}
	public String getCOORDI_FW_VER() {
		return COORDI_FW_VER;
	}
	public void setCOORDI_FW_VER(String cOORDI_FW_VER) {
		COORDI_FW_VER = cOORDI_FW_VER;
	}
	public String getDCU_ID() {
		return DCU_ID;
	}
	public void setDCU_ID(String dCU_ID) {
		DCU_ID = dCU_ID;
	}
	public Integer getUPLOAD_INTERVAL() {
		return UPLOAD_INTERVAL;
	}
	public void setUPLOAD_INTERVAL(Integer uPLOAD_INTERVAL) {
		UPLOAD_INTERVAL = uPLOAD_INTERVAL;
	}
	public String getNETWORK_CD() {
		return NETWORK_CD;
	}
	public void setNETWORK_CD(String nETWORK_CD) {
		NETWORK_CD = nETWORK_CD;
	}
	public String getMOBILE_MODE_CD() {
		return MOBILE_MODE_CD;
	}
	public void setMOBILE_MODE_CD(String mOBILE_MODE_CD) {
		MOBILE_MODE_CD = mOBILE_MODE_CD;
	}
	public String getLOCAL_IP() {
		return LOCAL_IP;
	}
	public void setLOCAL_IP(String lOCAL_IP) {
		LOCAL_IP = lOCAL_IP;
	}
	public Integer getPORT() {
		return PORT;
	}
	public void setPORT(Integer pORT) {
		PORT = pORT;
	}
	public String getSERVER_IP() {
		return SERVER_IP;
	}
	public void setSERVER_IP(String sERVER_IP) {
		SERVER_IP = sERVER_IP;
	}
	public String getSERVER_PORT() {
		return SERVER_PORT;
	}
	public void setSERVER_PORT(String sERVER_PORT) {
		SERVER_PORT = sERVER_PORT;
	}
}
