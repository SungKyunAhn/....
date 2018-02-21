package com.aimir.fep.iot.model.vo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class DeviceSearchVO {

	Date today = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /** 검색시작일 */
    private String startDay = sdf.format(today);
//    private String startDay = "";
    /** 검색 종료일 */
    private String endDay = sdf.format(today);
//    private String endDay = "";
    private String firstTime="TRUE";
	private String SID;
	private String DID; // 웨어러블의 관리 번호(광주시 생성 번호)
	private String PARENT_DEVICE_ID;
	private Integer UPLOAD_INTERVAL;
	private String NETWORK_CD;

	private String LAST_COMM_DT;
	private String LAST_COMM_TM;    
	private String MOBILE_MODE_CD;
	
	private String LOCAL_IP;
	
	private Integer PORT;
	
	private String SERVER_IP;
	
	private String SERVER_PORT;

	private String DCU_ID;

	private Double GPIOX;
	private Double GPIOY;
	private String ADDR;
	private String NAME;
	private String DKEY;
	private String FW_VER;

	private MultipartFile fwFilename;
	
	private String fwVer;
	
	private String deviceType;

	private List<String> chkList;
	
	private String fwURL;

	private String TAS_FW_VER;
	private String COORDI_FW_VER;
	
	
	private String searchDt;
	
	//2016 12 12 차병준 bleList 추가
	private List<String> bleList;
	
	public List<String> getBleList() {
		return bleList;
	}
	public void setBleList(List<String> bleList) {
		this.bleList = bleList;
	}
	public String getSearchDt() {
		return searchDt;
	}
	public void setSearchDt(String searchDt) {
		this.searchDt = searchDt;
	}
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
	public String getLAST_COMM_DT() {
		return LAST_COMM_DT;
	}
	public void setLAST_COMM_DT(String lAST_COMM_DT) {
		LAST_COMM_DT = lAST_COMM_DT;
	}
	public String getLAST_COMM_TM() {
		return LAST_COMM_TM;
	}
	public void setLAST_COMM_TM(String lAST_COMM_TM) {
		LAST_COMM_TM = lAST_COMM_TM;
	}
	public MultipartFile getFwFilename() {
		return fwFilename;
	}
	public void setFwFilename(MultipartFile fwFilename) {
		this.fwFilename = fwFilename;
	}
	public String getFwVer() {
		return fwVer;
	}
	public void setFwVer(String fwVer) {
		this.fwVer = fwVer;
	}
	
	public String getFwURL() {
		return fwURL;
	}
	public void setFwURL(String fwURL) {
		this.fwURL = fwURL;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public List<String> getChkList() {
		return chkList;
	}
	public void setChkList(List<String> chkList) {
		this.chkList = chkList;
	}
	public String getDKEY() {
		return DKEY;
	}
	public void setDKEY(String dKEY) {
		DKEY = dKEY;
	}
	public String getDID() {
		return DID;
	}
	public void setDID(String dID) {
		DID = dID;
	}

	public String getPARENT_DEVICE_ID() {
		return PARENT_DEVICE_ID;
	}
	public void setPARENT_DEVICE_ID(String pARENT_DEVICE_ID) {
		PARENT_DEVICE_ID = pARENT_DEVICE_ID;
	}

	private String[] STATUS_CD ;
	
	public String getDCU_ID() {
		return DCU_ID;
	}
	public void setDCU_ID(String dCU_ID) {
		DCU_ID = dCU_ID;
	}
	public Date getToday() {
		return today;
	}
	public void setToday(Date today) {
		this.today = today;
	}
	public SimpleDateFormat getSdf() {
		return sdf;
	}
	public void setSdf(SimpleDateFormat sdf) {
		this.sdf = sdf;
	}
	public String getStartDay() {
		return startDay;
	}
	public void setStartDay(String startDay) {
		this.startDay = startDay;
	}
	public String getEndDay() {
		return endDay;
	}
	public void setEndDay(String endDay) {
		this.endDay = endDay;
	}
	
	public String getSID() {
		return SID;
	}
	public void setSID(String sID) {
		SID = sID;
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
	public Double getGPIOX() {
		return GPIOX;
	}
	public void setGPIOX(Double gPIOX) {
		GPIOX = gPIOX;
	}
	public Double getGPIOY() {
		return GPIOY;
	}
	public void setGPIOY(Double gPIOY) {
		GPIOY = gPIOY;
	}
	
	public String getADDR() {
		return ADDR;
	}
	public void setADDR(String aDDR) {
		ADDR = aDDR;
	}
	public String getNAME() {
		return NAME;
	}
	public void setNAME(String nAME) {
		NAME = nAME;
	}
	public String[] getSTATUS_CD() {
		return STATUS_CD;
	}
	public void setSTATUS_CD(String[] sTATUS_CD) {
		STATUS_CD = sTATUS_CD;
	}
	public String getFirstTime() {
		return firstTime;
	}
	public void setFirstTime(String firstTime) {
		this.firstTime = firstTime;
	}
	public String getFW_VER() {
		return FW_VER;
	}
	public void setFW_VER(String fW_VER) {
		FW_VER = fW_VER;
	}


}
