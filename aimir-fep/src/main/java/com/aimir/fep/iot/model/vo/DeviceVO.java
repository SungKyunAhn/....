package com.aimir.fep.iot.model.vo;

public class DeviceVO {

	private String DKEY;
	private String TYPE_CD;
	private String KIND_CD;
	private String FW_VER;
	private String FW_URL;
	private String FW_NAME;
	private String FW_STATUS;
	private String HW_VER;
	private String NAME;
	private String ADDR;
	private Double GPIOX;
	private Double GPIOY;
	private Double GPIOZ;
	private String INSTALL_DT;
	private String CREATE_DT;
	private String MODIFY_DT;
	private String VENDOR;
	private String MODEL;
	private String PARENT_DEVICE_TYPE;
	private String PARENT_DEVICE_ID;
	private String LAST_COMM_DT;
	
	//추가 : gpsTm
	//2016 11 28 차병준
	private String GPI_TM;
	private String STATUS;
	private String THREED_TM;
	private String THREEDIOX;
	private String THREEDIOY;
	private String THREEDIOZ;
	private String THREEDPACE;
	
	//추가 : 변압기
	//2017 04 27 차병준
	private Double VIB;
	private Double DEGREE_X;
	private Double DEGREE_Y;
	private String TEMP_IN;
	private String TEMP_OUT;
	private int TEMP;
	private String NOISE;
	private String TEMP_PERIOD;
	private String VIB_PERIOD;
	private String DEGREE_PERIOD;
	private String NOISE_PERIOD;
	private String HUM_PERIOD;
	private String HUM;
	
	
	

	public int getTEMP() {
		return TEMP;
	}
	public void setTEMP(int tEMP) {
		TEMP = tEMP;
	}
	public String getHUM() {
		return HUM;
	}
	public void setHUM(String hUM) {
		HUM = hUM;
	}
	public Double getVIB() {
		return VIB;
	}
	public void setVIB(Double vIB) {
		VIB = vIB;
	}
	public Double getDEGREE_X() {
		return DEGREE_X;
	}
	public void setDEGREE_X(Double dEGREE_X) {
		DEGREE_X = dEGREE_X;
	}
	public Double getDEGREE_Y() {
		return DEGREE_Y;
	}
	public void setDEGREE_Y(Double dEGREE_Y) {
		DEGREE_Y = dEGREE_Y;
	}
	public String getTEMP_IN() {
		return TEMP_IN;
	}
	public void setTEMP_IN(String tEMP_IN) {
		TEMP_IN = tEMP_IN;
	}
	public String getTEMP_OUT() {
		return TEMP_OUT;
	}
	public void setTEMP_OUT(String tEMP_OUT) {
		TEMP_OUT = tEMP_OUT;
	}
	public String getNOISE() {
		return NOISE;
	}
	public void setNOISE(String nOISE) {
		NOISE = nOISE;
	}
	public String getTEMP_PERIOD() {
		return TEMP_PERIOD;
	}
	public void setTEMP_PERIOD(String tEMP_PERIOD) {
		TEMP_PERIOD = tEMP_PERIOD;
	}
	public String getVIB_PERIOD() {
		return VIB_PERIOD;
	}
	public void setVIB_PERIOD(String vIB_PERIOD) {
		VIB_PERIOD = vIB_PERIOD;
	}
	public String getDEGREE_PERIOD() {
		return DEGREE_PERIOD;
	}
	public void setDEGREE_PERIOD(String dEGREE_PERIOD) {
		DEGREE_PERIOD = dEGREE_PERIOD;
	}
	public String getNOISE_PERIOD() {
		return NOISE_PERIOD;
	}
	public void setNOISE_PERIOD(String nOISE_PERIOD) {
		NOISE_PERIOD = nOISE_PERIOD;
	}
	public String getHUM_PERIOD() {
		return HUM_PERIOD;
	}
	public void setHUM_PERIOD(String hUM_PERIOD) {
		HUM_PERIOD = hUM_PERIOD;
	}
	public String getTHREEDPACE() {
		return THREEDPACE;
	}
	public void setTHREEDPACE(String tHREEDPACE) {
		THREEDPACE = tHREEDPACE;
	}
	public String getTHREED_TM() {
		return THREED_TM;
	}
	public void setTHREED_TM(String tHREED_TM) {
		THREED_TM = tHREED_TM;
	}
	public String getTHREEDIOX() {
		return THREEDIOX;
	}
	public void setTHREEDIOX(String tHREEDIOX) {
		THREEDIOX = tHREEDIOX;
	}
	public String getTHREEDIOY() {
		return THREEDIOY;
	}
	public void setTHREEDIOY(String tHREEDIOY) {
		THREEDIOY = tHREEDIOY;
	}
	public String getTHREEDIOZ() {
		return THREEDIOZ;
	}
	public void setTHREEDIOZ(String tHREEDIOZ) {
		THREEDIOZ = tHREEDIOZ;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getGPI_TM() {
		return GPI_TM;
	}
	public void setGPI_TM(String gPITM) {
		GPI_TM = gPITM;
	}
	public String getLAST_COMM_DT() {
		return LAST_COMM_DT;
	}
	public void setLAST_COMM_DT(String lAST_COMM_DT) {
		LAST_COMM_DT = lAST_COMM_DT;
	}
	public String getDKEY() {
		return DKEY;
	}
	public void setDKEY(String dKEY) {
		DKEY = dKEY;
	}
	public String getTYPE_CD() {
		return TYPE_CD;
	}
	public void setTYPE_CD(String tYPE_CD) {
		TYPE_CD = tYPE_CD;
	}
	public String getKIND_CD() {
		return KIND_CD;
	}
	public void setKIND_CD(String kIND_CD) {
		KIND_CD = kIND_CD;
	}
	public String getFW_VER() {
		return FW_VER;
	}
	public void setFW_VER(String fW_VER) {
		FW_VER = fW_VER;
	}
	public String getFW_URL() {
		return FW_URL;
	}
	public void setFW_URL(String fW_URL) {
		FW_URL = fW_URL;
	}

	public String getFW_NAME() {
		return FW_NAME;
	}
	public void setFW_NAME(String fW_NAME) {
		FW_NAME = fW_NAME;
	}
	
	public String getFW_STATUS() {
		return FW_STATUS;
	}
	public void setFW_STATUS(String fW_STATUS) {
		FW_STATUS = fW_STATUS;
	}
	public String getHW_VER() {
		return HW_VER;
	}
	public void setHW_VER(String hW_VER) {
		HW_VER = hW_VER;
	}
	public String getNAME() {
		return NAME;
	}
	public void setNAME(String nAME) {
		NAME = nAME;
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
	public String getINSTALL_DT() {
		return INSTALL_DT;
	}
	public void setINSTALL_DT(String iNSTALL_DT) {
		INSTALL_DT = iNSTALL_DT;
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
	
}
