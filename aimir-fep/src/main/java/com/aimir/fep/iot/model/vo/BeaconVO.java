package com.aimir.fep.iot.model.vo;

/**
 * 비콘 데이타 업로드
 * @author 차병준
 * @data   2016 12 06
 *
 */
public class BeaconVO extends DeviceVO  {
	private String SID;
	private String DID;
	private String MSGTYPE;
	private String PAIRID;
	private String LAST_COMM_DT;

	
	

	public String getLAST_COMM_DT() {
		return LAST_COMM_DT;
	}
	public void setLAST_COMM_DT(String lAST_COMM_DT) {
		LAST_COMM_DT = lAST_COMM_DT;
	}
	public String getDID() {
		return DID;
	}
	public void setDID(String dID) {
		DID = dID;
	}
	public String getSID() {
		return SID;
	}
	public void setSID(String sID) {
		SID = sID;
	}
	public String getMSGTYPE() {
		return MSGTYPE;
	}
	public void setMSGTYPE(String mSGTYPE) {
		MSGTYPE = mSGTYPE;
	}
	public String getPAIRID() {
		return PAIRID;
	}
	public void setPAIRID(String pAIRID) {
		PAIRID = pAIRID;
	}
	
	

}
