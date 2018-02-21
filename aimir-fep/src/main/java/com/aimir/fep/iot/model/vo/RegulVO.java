package com.aimir.fep.iot.model.vo;

/**
 * 정기/현재 지침에 대한 VO 객체
 * Created with IntelliJ IDEA.
 * User: kaze
 * Date: 15. 1. 16
 * Time: 오전 12:19
 * To change this template use File | Settings | File Templates.
 */
public class RegulVO extends  SearchVO {
    //정기지침 여부(1:정기지침, 2:현재지침)
    private String regulType;
    //정기 검침일
    private String regulYymmdd;
    //정기검침일 UI용
    private String formatRegulYymmdd;

    //본부명
    private String bonbu;

    //도서명
    private String dosu;

    //발전기명
    private String generator;

    //계량기 번호
    private String meterNo;
    //모뎀 번호
    private String modemNo;
    //지침 수신시간
    private String receiveYymmddhhmmss;
    //지침 수신시간 UI용
    private String formatReceiveYymmddhhmmss;

    //유효
    private Double goodMorning;
    private Double goodAfter;
    private Double goodEvening;

    //무효
    private Double reacMorning;
    private Double reacAfter;
    private Double reacEvening;

    //최대 전력
    private Double maxMorning;
    private Double maxAfter;
    private Double maxEvening;

    //최대 전력 시간
    private String maxTimeMorning;
    private String maxTimeAfter;
    private String maxTimeEvening;

    //최대 전력 시간 UI용
    private String formatMaxTimeMorning;
    private String formatMaxTimeAfter;
    private String formatMaxTimeEvening;

    // 정기검침 여부 검색 조건(1:정기검침, 2: 현재점침)
    private String searchGubun="1";

    //날짜 검색 From 조건
    private String searchFromDate;

    //날짜 검색 to 조건
    private String searchToDate;

    public String getRegulYymmdd() {
        return regulYymmdd;
    }

    public void setRegulYymmdd(String regulYymmdd) {
        this.regulYymmdd = regulYymmdd;
    }

    public String getRegulType() {
        return regulType;
    }

    public void setRegulType(String regulType) {
        this.regulType = regulType;
    }

    public String getMeterNo() {
        return meterNo;
    }

    public void setMeterNo(String meterNo) {
        this.meterNo = meterNo;
    }

    public String getModemNo() {
        return modemNo;
    }

    public void setModemNo(String modemNo) {
        this.modemNo = modemNo;
    }

    public String getReceiveYymmddhhmmss() {
        return receiveYymmddhhmmss;
    }

    public void setReceiveYymmddhhmmss(String receiveYymmddhhmmss) {
        this.receiveYymmddhhmmss = receiveYymmddhhmmss;
    }

    public Double getGoodMorning() {
        return goodMorning;
    }

    public void setGoodMorning(Double goodMorning) {
        this.goodMorning = goodMorning;
    }

    public Double getGoodAfter() {
        return goodAfter;
    }

    public void setGoodAfter(Double goodAfter) {
        this.goodAfter = goodAfter;
    }

    public Double getGoodEvening() {
        return goodEvening;
    }

    public void setGoodEvening(Double goodEvening) {
        this.goodEvening = goodEvening;
    }

    public Double getReacMorning() {
        return reacMorning;
    }

    public void setReacMorning(Double reacMorning) {
        this.reacMorning = reacMorning;
    }

    public Double getReacAfter() {
        return reacAfter;
    }

    public void setReacAfter(Double reacAfter) {
        this.reacAfter = reacAfter;
    }

    public Double getReacEvening() {
        return reacEvening;
    }

    public void setReacEvening(Double reacEvening) {
        this.reacEvening = reacEvening;
    }

    public Double getMaxMorning() {
        return maxMorning;
    }

    public void setMaxMorning(Double maxMorning) {
        this.maxMorning = maxMorning;
    }

    public Double getMaxAfter() {
        return maxAfter;
    }

    public void setMaxAfter(Double maxAfter) {
        this.maxAfter = maxAfter;
    }

    public Double getMaxEvening() {
        return maxEvening;
    }

    public void setMaxEvening(Double maxEvening) {
        this.maxEvening = maxEvening;
    }

    public String getMaxTimeMorning() {
        return maxTimeMorning;
    }

    public void setMaxTimeMorning(String maxTimeMorning) {
        this.maxTimeMorning = maxTimeMorning;
    }

    public String getMaxTimeAfter() {
        return maxTimeAfter;
    }

    public void setMaxTimeAfter(String maxTimeAfter) {
        this.maxTimeAfter = maxTimeAfter;
    }

    public String getMaxTimeEvening() {
        return maxTimeEvening;
    }

    public void setMaxTimeEvening(String maxTimeEvening) {
        this.maxTimeEvening = maxTimeEvening;
    }

    public String getSearchGubun() {
        return searchGubun;
    }

    public void setSearchGubun(String searchGubun) {
        this.searchGubun = searchGubun;
    }

    public String getSearchFromDate() {
        return searchFromDate;
    }

    public void setSearchFromDate(String searchFromDate) {
        this.searchFromDate = searchFromDate;
    }

    public String getSearchToDate() {
        return searchToDate;
    }

    public void setSearchToDate(String searchToDate) {
        this.searchToDate = searchToDate;
    }

    public String getFormatRegulYymmdd() {
        return formatRegulYymmdd;
    }

    public void setFormatRegulYymmdd(String formatRegulYymmdd) {
        this.formatRegulYymmdd = formatRegulYymmdd;
    }

    public String getFormatReceiveYymmddhhmmss() {
        return formatReceiveYymmddhhmmss;
    }

    public void setFormatReceiveYymmddhhmmss(String formatReceiveYymmddhhmmss) {
        this.formatReceiveYymmddhhmmss = formatReceiveYymmddhhmmss;
    }

    public String getFormatMaxTimeMorning() {
        if(getMaxTimeMorning()!=null && getMaxTimeMorning().length()>=12){
            return getMaxTimeMorning().substring(0,4)+"-"+getMaxTimeMorning().substring(4,6)+"-"+getMaxTimeMorning().substring(6,8)+" "+getMaxTimeMorning().substring(8,10)+":"+getMaxTimeMorning().substring(10,12);
        }
        return formatMaxTimeMorning;
    }

    public void setFormatMaxTimeMorning(String formatMaxTimeMorning) {
        this.formatMaxTimeMorning = formatMaxTimeMorning;
    }

    public String getFormatMaxTimeAfter() {
        if(getMaxTimeAfter()!=null && getMaxTimeAfter().length()>=12){
            return getMaxTimeAfter().substring(0,4)+"-"+getMaxTimeAfter().substring(4,6)+"-"+getMaxTimeAfter().substring(6,8)+" "+getMaxTimeAfter().substring(8,10)+":"+getMaxTimeAfter().substring(10,12);
        }
        return formatMaxTimeAfter;
    }

    public void setFormatMaxTimeAfter(String formatMaxTimeAfter) {
        this.formatMaxTimeAfter = formatMaxTimeAfter;
    }

    public String getFormatMaxTimeEvening() {
        if(getMaxTimeEvening()!=null && getMaxTimeEvening().length()>=12){
            return getMaxTimeEvening().substring(0,4)+"-"+getMaxTimeEvening().substring(4,6)+"-"+getMaxTimeEvening().substring(6,8)+" "+getMaxTimeEvening().substring(8,10)+":"+getMaxTimeEvening().substring(10,12);
        }
        return formatMaxTimeEvening;
    }

    public void setFormatMaxTimeEvening(String formatMaxTimeEvening) {
        this.formatMaxTimeEvening = formatMaxTimeEvening;
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

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }
}
