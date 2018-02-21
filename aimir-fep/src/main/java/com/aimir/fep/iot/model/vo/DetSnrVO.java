package com.aimir.fep.iot.model.vo;

public class DetSnrVO extends DeviceVO{

	private String RNUM;
	
	private String SID;
	
	private String ADDR;
	
	private Double GPIOX;
	
	private Double GPIOY;
	
	private Double GPIOZ;
	
	private Integer UPLOAD_INTERVAL;
	
	private String HW_VER;
	
	private String FW_VER;
	
	private String DKEY;
	
	private String PARENT_DEVICE_TYPE;
	
	private String PARENT_DEVICE_ID;
	
	private String VENDOR;
	
	private String MODEL;
	
	private String MODIFY_DT;
	
	private String CREATE_DT;
	
	private String INSTALL_DT;
	
	private String STATUS_CD;
	
	private String stcheck;
	
	private String LAST_COMM_DT;	
	
	private Integer SLOPE_ANGLE;
	
	private Integer SHOCK_VALUE;

	private Integer SLOPE_THRESHOLD;
	
	private Integer SHOCK_THRESHOLD;
	
	private Integer STATUS_ANG;
	
	private Integer STATUS_SHK;
	
	private String STATUS_NAME;
	
	private String YEAR;
	private String MONTH;
	private String DAY;
	private String HHMMSS;
	private String YYYYMMDDHHMMSS;
	private String startDate;
	private String endDate;
	private String YYYYMMDD;
	private String WAKEUP_PERIOD;
	private String HRM_PERIOD;
	private String GPS_PERIOD;
	private String BEACON_PERIOD;
	
	private Double INCLINEDEG;
	private Double MAX_THRESHOLD;
	private Double MIN_THRESHOLD;
	public String getSID() {
		return SID;
	}

	public void setSID(String sID) {
		SID = sID;
	}

	public String getADDR() {
		return ADDR;
	}

	public void setADDR(String aDDR) {
		ADDR = aDDR;
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

	public Double getGPIOZ() {
		return GPIOZ;
	}

	public void setGPIOZ(Double gPIOZ) {
		GPIOZ = gPIOZ;
	}

	public Integer getUPLOAD_INTERVAL() {
		return UPLOAD_INTERVAL;
	}

	public void setUPLOAD_INTERVAL(Integer uPLOAD_INTERVAL) {
		UPLOAD_INTERVAL = uPLOAD_INTERVAL;
	}

	public String getHW_VER() {
		return HW_VER;
	}

	public void setHW_VER(String hW_VER) {
		HW_VER = hW_VER;
	}

	public String getFW_VER() {
		return FW_VER;
	}

	public void setFW_VER(String fW_VER) {
		FW_VER = fW_VER;
	}

	public String getDKEY() {
		return DKEY;
	}

	public void setDKEY(String dKEY) {
		DKEY = dKEY;
	}

	public String getPARENT_DEVICE_TYPE() {
		return PARENT_DEVICE_TYPE;
	}

	public void setPARENT_DEVICE_TYPE(String pARENT_DEVICE_TYPE) {
		PARENT_DEVICE_TYPE = pARENT_DEVICE_TYPE;
	}

	public String getPARENT_DEVICE_ID() {
		return PARENT_DEVICE_ID;
	}

	public void setPARENT_DEVICE_ID(String pARENT_DEVICE_ID) {
		PARENT_DEVICE_ID = pARENT_DEVICE_ID;
	}

	public String getVENDOR() {
		return VENDOR;
	}

	public void setVENDOR(String vENDOR) {
		VENDOR = vENDOR;
	}

	public String getMODEL() {
		return MODEL;
	}

	public void setMODEL(String mODEL) {
		MODEL = mODEL;
	}

	public String getMODIFY_DT() {
		return MODIFY_DT;
	}

	public void setMODIFY_DT(String mODIFY_DT) {
		MODIFY_DT = mODIFY_DT;
	}

	public String getCREATE_DT() {
		return CREATE_DT;
	}

	public void setCREATE_DT(String cREATE_DT) {
		CREATE_DT = cREATE_DT;
	}

	public String getINSTALL_DT() {
		return INSTALL_DT;
	}

	public void setINSTALL_DT(String iNSTALL_DT) {
		INSTALL_DT = iNSTALL_DT;
	}

	public String getSTATUS_CD() {
		return STATUS_CD;
	}

	public void setSTATUS_CD(String sTATUS_CD) {
		STATUS_CD = sTATUS_CD;
	}

	public String getLAST_COMM_DT() {
		return LAST_COMM_DT;
	}

	public void setLAST_COMM_DT(String lAST_COMM_DT) {
		LAST_COMM_DT = lAST_COMM_DT;
	}

	public Integer getSLOPE_ANGLE() {
		return SLOPE_ANGLE;
	}

	public void setSLOPE_ANGLE(Integer sLOPE_ANGLE) {
		SLOPE_ANGLE = sLOPE_ANGLE;
	}

	public Integer getSHOCK_VALUE() {
		return SHOCK_VALUE;
	}

	public void setSHOCK_VALUE(Integer sHOCK_VALUE) {
		SHOCK_VALUE = sHOCK_VALUE;
	}

	public Integer getSLOPE_THRESHOLD() {
		return SLOPE_THRESHOLD;
	}

	public void setSLOPE_THRESHOLD(Integer sLOPE_THRESHOLD) {
		SLOPE_THRESHOLD = sLOPE_THRESHOLD;
	}

	public Integer getSHOCK_THRESHOLD() {
		return SHOCK_THRESHOLD;
	}

	public void setSHOCK_THRESHOLD(Integer sHOCK_THRESHOLD) {
		SHOCK_THRESHOLD = sHOCK_THRESHOLD;
	}	
	
	public Integer getSTATUS_ANG() {
		return STATUS_ANG;
	}

	public void setSTATUS_ANG(Integer sTATUS_ANG) {
		STATUS_ANG = sTATUS_ANG;
	}
	
	public Integer getSTATUS_SHK() {
		return STATUS_SHK;
	}

	public void setSTATUS_SHK(Integer sTATUS_SHK) {
		STATUS_SHK = sTATUS_SHK;
	}

	public String getSTATUS_NAME() {
		return STATUS_NAME;
	}

	public void setSTATUS_NAME(String sTATUS_NAME) {
		STATUS_NAME = sTATUS_NAME;
	}

	public String getRNUM() {
		return RNUM;
	}

	public void setRNUM(String rNUM) {
		RNUM = rNUM;
	}

	public String getStcheck() {
		return stcheck;
	}

	public void setStcheck(String stcheck) {
		this.stcheck = stcheck;
	}

	public String getYEAR() {
		return YEAR;
	}

	public void setYEAR(String yEAR) {
		YEAR = yEAR;
	}

	public String getMONTH() {
		return MONTH;
	}

	public void setMONTH(String mONTH) {
		MONTH = mONTH;
	}

	public String getDAY() {
		return DAY;
	}

	public void setDAY(String dAY) {
		DAY = dAY;
	}

	public String getHHMMSS() {
		return HHMMSS;
	}

	public void setHHMMSS(String hHMMSS) {
		HHMMSS = hHMMSS;
	}

	public String getYYYYMMDDHHMMSS() {
		return YYYYMMDDHHMMSS;
	}

	public void setYYYYMMDDHHMMSS(String yYYYMMDDHHMMSS) {
		YYYYMMDDHHMMSS = yYYYMMDDHHMMSS;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}


	public String getYYYYMMDD() {
		return YYYYMMDD;
	}

	public void setYYYYMMDD(String yYYYMMDD) {
		YYYYMMDD = yYYYMMDD;
	}

	public String getWAKEUP_PERIOD() {
		return WAKEUP_PERIOD;
	}

	public void setWAKEUP_PERIOD(String wAKEUP_PERIOD) {
		WAKEUP_PERIOD = wAKEUP_PERIOD;
	}

	public String getHRM_PERIOD() {
		return HRM_PERIOD;
	}

	public void setHRM_PERIOD(String hRM_PERIOD) {
		HRM_PERIOD = hRM_PERIOD;
	}

	public String getGPS_PERIOD() {
		return GPS_PERIOD;
	}

	public void setGPS_PERIOD(String gPS_PERIOD) {
		GPS_PERIOD = gPS_PERIOD;
	}

	public String getBEACON_PERIOD() {
		return BEACON_PERIOD;
	}

	public void setBEACON_PERIOD(String bEACON_PERIOD) {
		BEACON_PERIOD = bEACON_PERIOD;
	}

	public Double getINCLINEDEG() {
		return INCLINEDEG;
	}

	public void setINCLINEDEG(Double iNCLINEDEG) {
		INCLINEDEG = iNCLINEDEG;
	}

	public Double getMAX_THRESHOLD() {
		return MAX_THRESHOLD;
	}

	public void setMAX_THRESHOLD(Double mAX_THRESHOLD) {
		MAX_THRESHOLD = mAX_THRESHOLD;
	}

	public Double getMIN_THRESHOLD() {
		return MIN_THRESHOLD;
	}

	public void setMIN_THRESHOLD(Double mIN_THRESHOLD) {
		MIN_THRESHOLD = mIN_THRESHOLD;
	}
	
	
	
}

