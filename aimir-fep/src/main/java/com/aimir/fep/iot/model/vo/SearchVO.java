package com.aimir.fep.iot.model.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 검색 정보를 담고있는 VO 클래스를 정의한다.
 *
 * @author
 * @version 1.0
 * @see <pre>
 *            == 개정이력(Modification Information) ==
 *
 *             수정일         수정자           수정내용
 *            -------        --------    ---------------------------
 *             2014.12.23    김재호      검색 정보, 페이징 VO 생성
 *
 *           </pre>
 * @since 2014.12.23
 */

public class SearchVO extends PageVO {

	Date today = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /** 번호 */
    private String num;
    /** 본부 */
    private String bonbu = "";
    /** 도서 */
    private String dosu = "";

    /** 검색시작일 */
    private String startDay = sdf.format(today);
//    private String startDay = "";
    /** 검색 종료일 */
    private String endDay = sdf.format(today);
//    private String endDay = "";
    /** 모뎀번호*/
    private String modemNo = "";
    /** 계기 번호*/
    private String meterNo = "";

    /**트리  */// cnSelectedNodeid : 계량기번호 / cnSelectedNodetype : 계량기타입
    private String cnSelectedNodeid = "";

    private String cnSelectedNodetype = "";

    /** 이벤트 검색 */
    private String[] selectedEvent = {"", "", "", "", "", "", "", ""};

    /** 이벤트 firstTime */
    private String firstTime="TRUE";
    
    
    /** 발생 시간*/
    private String eventTime = "";
    /** 복구 시간*/
    private String solvedTime = "";
    /** 정전 시간*/
    private String periodTime = "";
    /** 차단 부하*/
    private String loadBreak = "";


    /** 발전기 번호 */
    private String generatorno = "";
    /** 발전기 명 */
    private String PowerGenerator = "";
    /** 계량기 시간 */
    private String meterTm = "";
    /**최종 계량기 시간 */
    private String finalMeterTm = "";

    /** 작성 시간/수정 시간 검색시 Null이어도 검색 */
    /** 작성 시간 */
    private String createDt = "";
    /** 수정 시간 */
    private String writedt = "";
    /** 날짜 */
    private String eventDate = "";
    /** 운전 가동상태 */
    private String operatingState = "";
    /** 운전상태 현재보다 30분전 시간*/
    private String to = "";
    /** 운전 정지 상태 기준값 */
    //private String value = ""; 		//MeteringData < #value# : 운전정지 상태
    private int value;
    /** 운전상태 radio 검색조건 */
    private String meteringData = "";
    /** 운전 가동상태 */
    private String operingState = "";
    /** 운전 정지상태 */
    private String operingStop = "";
    private String searchData = "";
    /** 시간오차현황 */
    private String restoreFlag = "";
    /** 시간오차상태 radio 검색조건 */
    private String timeErrorState = "";
    private String nonTimeErrorState = "";
    
    /** 복구상태 */
    private String statusCd = "";
    
    /** 이벤트 날짜 검색 */
    private String startEventDay = "";
    private String endEventDay = "";
    /** 발전,송전 구분 */
    private String gubunSoungChul = "2";

    /** 발전실적페이지 기간 선택 **/
    private String meteringDataType;

    private String meteringDataTime;

    /** 본부 조회 권한 **/
    private String locationCd;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getBonbu() {
        return bonbu;
    }

    public void setBonbu(String bonbu) {
        this.bonbu = bonbu;
    }

    public String getDosu() {
        return dosu;
    }

    public void setDosu(String dosu) {
        this.dosu = dosu;
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

    public String getModemNo() {
        return modemNo;
    }

    public void setModemNo(String modemNo) {
        this.modemNo = modemNo;
    }

    public String getMeterNo() {
        return meterNo;
    }

    public void setMeterNo(String meterNo) {
        this.meterNo = meterNo;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getSolvedTime() {
        return solvedTime;
    }

    public void setSolvedTime(String solvedTime) {
        this.solvedTime = solvedTime;
    }

    public String getPeriodTime() {
        return periodTime;
    }

    public void setPeriodTime(String periodTime) {
        this.periodTime = periodTime;
    }

    public String getLoadBreak() {
        return loadBreak;
    }

    public void setLoadBreak(String loadBreak) {
        this.loadBreak = loadBreak;
    }

    //날짜
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

    //발전기 목록조회
    public String getgeneratorno() {
        return generatorno;
    }

    public void setgeneratorno(String generatorno) {
        this.generatorno = generatorno;
    }

    public String getPowerGenerator() {
        return PowerGenerator;
    }

    public void setPowerGenerator(String powergenerator) {
        this.PowerGenerator = powergenerator;
    }

    public String getcnSelectedNodeid() {
        return cnSelectedNodeid;
    }

    public void setcnSelectedNodeid(String cnSelectedNodeid) {
        this.cnSelectedNodeid = cnSelectedNodeid;
    }

    public String getcnSelectedNodetype() {
        return cnSelectedNodetype;
    }

    public void setcnSelectedNodetype(String cnSelectedNodetype) {
        this.cnSelectedNodetype = cnSelectedNodetype;
    }

    public String getWritedt() {
        return writedt;
    }

    public void setWritedt(String writedt) {
        this.writedt = writedt;
    }

    public String getCreateDt() {
        return createDt;
    }

    public void setCreateDt(String createDt) {
        this.createDt = createDt;
    }

    public String getRestoreFlag() {
        return restoreFlag;
    }

    public void setRestoreFlag(String restoreFlag) {
        this.restoreFlag = restoreFlag;
    }

    public String getOperatingState() {
        return operatingState;
    }

    public void setOperatingState(String operatingState) {
        this.operatingState = operatingState;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMeteringData() {
        return meteringData;
    }

    public void setMeteringData(String meteringData) {
        this.meteringData = meteringData;
    }
    
    /*public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}*/
	
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
    //라디오 검색단
    public String getOperingState() {
        return operingState;
    }

    public void setOperingState(String operingState) {
        this.operingState = operingState;
    }

    public String getOperingStop() {
        return operingStop;
    }

    public void setOperingStop(String operingStop) {
        this.operingStop = operingStop;
    }

    public String getSearchData() {
        return searchData;
    }

    public void setSearchData(String searchData) {
        this.searchData = searchData;
    }

    public String getTimeErrorState() {
        return timeErrorState;
    }

    public void setTimeErrorState(String timeErrorState) {
        this.timeErrorState = timeErrorState;
    }

    public String getNonTimeErrorState() {
        return nonTimeErrorState;
    }

    public void setNonTimeErrorState(String nonTimeErrorState) {
        this.nonTimeErrorState = nonTimeErrorState;
    }

    //이벤트 날짜
    public String getStartEventDay() {
        return startEventDay;
    }

    public void setStartEventDay(String startEventDay) {
        this.startEventDay = startEventDay;
    }

    public String getEndEventDay() {
        return endEventDay;
    }

    public void setEndEventDay(String endEventDay) {
        this.endEventDay = endEventDay;
    }


    //시간오차로그 조회
    
    public String getStatusCd() {
		return statusCd;
	}

	public void setStatusCd(String statusCd) {
		this.statusCd = statusCd;
	}
    
    public String getMeterTm() {
        return meterTm;
    }

    public void setMeterTm(String meterTm) {
        this.meterTm = meterTm;
    }

    public String getFinalMeterTm() {
        return finalMeterTm;
    }

    public void setFinalMeterTm(String finalMeterTm) {
        this.finalMeterTm = finalMeterTm;
    }


    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String[] getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(String[] selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public void setSelectedEvent(int i, String selectedEvent) {
        this.selectedEvent[i] = selectedEvent;
    }

    public String getGubunSoungChul() {
        return gubunSoungChul;
    }

    public void setGubunSoungChul(String gubunSoungChul) {
        this.gubunSoungChul = gubunSoungChul;
    }

    public String getMeteringDataType() {
        return meteringDataType;
    }

    public void setMeteringDataType(String meteringDataType) {
        this.meteringDataType = meteringDataType;
    }

    public String getMeteringDataTime() {
        return meteringDataTime;
    }

    public void setMeteringDataTime(String meteringDataTime) {
        this.meteringDataTime = meteringDataTime;
    }

	public String getFirstTime() {
		return firstTime;
	}

	public void setFirstTime(String firstTime) {
		this.firstTime = firstTime;
	}

    public String getLocationCd() {
        return locationCd;
    }

    public void setLocationCd(String locationCd) {
        this.locationCd = locationCd;
    }
}

