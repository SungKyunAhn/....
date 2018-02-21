package com.aimir.fep.iot.model.vo;

/**
 * <pre>
 * Container 저장 
 * com.aimir.model.vo
 * CNTRVO.java
 * </pre>
 * @author  : 은미애
 * @Date    : 2015. 9. 10.
 * @Version : 
 */
public class CNTRVO {
	private String DEVICE_ID;
	private String CNTR_TYPE;
	private String CNTR_NAME;
	private Integer UPLOAD_INTERVAL;
	private String CREATE_DT;
	private String MODIFY_DT;
	public String getDEVICE_ID() {
		return DEVICE_ID;
	}
	public void setDEVICE_ID(String dEVICE_ID) {
		DEVICE_ID = dEVICE_ID;
	}
	public String getCNTR_TYPE() {
		return CNTR_TYPE;
	}
	public void setCNTR_TYPE(String cNTR_TYPE) {
		CNTR_TYPE = cNTR_TYPE;
	}
	public String getCNTR_NAME() {
		return CNTR_NAME;
	}
	public void setCNTR_NAME(String cNTR_NAME) {
		CNTR_NAME = cNTR_NAME;
	}
	public Integer getUPLOAD_INTERVAL() {
		return UPLOAD_INTERVAL;
	}
	public void setUPLOAD_INTERVAL(Integer uPLOAD_INTERVAL) {
		UPLOAD_INTERVAL = uPLOAD_INTERVAL;
	}
	public String getCREATE_DT() {
		return CREATE_DT;
	}
	public void setCREATE_DT(String cREATE_DT) {
		CREATE_DT = cREATE_DT;
	}
	public String getMODIFY_DT() {
		return MODIFY_DT;
	}
	public void setMODIFY_DT(String mODIFY_DT) {
		MODIFY_DT = mODIFY_DT;
	}

}
