package com.aimir.fep.meter.parser.elsterA1140Table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.PowerAlarmLogData;
import com.aimir.fep.meter.parser.ElsterA1140;
import com.aimir.fep.meter.parser.elsterA1140Table.EVENT_ATTRIBUTE.EVENTATTRIBUTE;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;

/**
 * @author EJ Choi
 */
public class A1140_LP_DATA {    
	
    private Log log = LogFactory.getLog(A1140_LP_DATA.class);
    
    // 2.10.1 Load Profile Status Entry MSB -> LSB
    private static final EVENTATTRIBUTE[] STATUS = {
    	EVENTATTRIBUTE.NOT_USED,        // Never Used
        EVENTATTRIBUTE.PHASE_FAILURE,   // Phase Failure
        EVENTATTRIBUTE.REVERSE_RUNNING, // Reverse Run
        EVENTATTRIBUTE.CT_RATIO_CHANGE, // CT ratio change : Not Used에서 변경함 by eunmiae
        EVENTATTRIBUTE.BATTERY_FAIL,    // Internal Battery Failure
        EVENTATTRIBUTE.DATA_CHANGE,     // Write Access(data change)
        EVENTATTRIBUTE.TIME_SYNC,       // Time Synchronisation
        EVENTATTRIBUTE.METER_RESET     // Transient Reset
        };

    public static final int LEN_MARKER_TYPE           = 1;
    public static final int LEN_MARKER_TIME           = 4;
    public static final int LEN_MARKER_CHANNEL        = 2;
    public static final int LEN_MARKER_CHANNEL_AS_BIT = 16;
    public static final int LEN_MARKER_DEMAND_PERIOD  = 1;
    
    public static final int LEN_LP_STATUS             = 1;
    public static final int LEN_LP_STATUS_AS_BIT      = 8;
    public static final int LEN_LP_DATA               = 3;
    
    public static final int LEN_EXTERNAL_DATA_ENTRY   = 2;    
    
    public static final String CASE_OPEN				   	= "E3";    
    public static final String TERMINAL_CASE_OPEN		   	= "C1";
    public static final String MAIN_CASE_OPEN			   	= "C2";
    public static final String TERMINAL_MAIN_COVER_OPEN		= "C3";
    
    public static final String NEW_DAY_MARKER              = "E4";
    public static final String POWER_UP_MARKER             = "E5";
    public static final String POWER_DOWN_MARKER           = "E6";
    public static final String CONFIGURATION_CAHNGE_MARKER = "E8";
    public static final String TIME_CHANGE_MARKER          = "EA";
    public static final String DAYLIGHT_SAVING_MARKER      = "ED";
    public static final String LOAD_PROFILE_CLEARED_MARKER = "EB";
  

    private byte[] rawData = null;
    
    private List<A1140_LP_DATA_MARKER> markerList          = new ArrayList<A1140_LP_DATA_MARKER>();
    private List<LPData>               lpList              = new ArrayList<LPData>();
    private List<EventLogData>         lpMeterEventLogList = new ArrayList<EventLogData>();
    private List<PowerAlarmLogData>    lpPowerAlarmLogList = new ArrayList<PowerAlarmLogData>();
    
    private int totalChannelCnt    = 0;
    private int internalChannelCnt = 0;
    private int externalChannelCnt = 0;
    
    private int lpPeriod = 0;

    private String dateTime       = "";
    private String nextLpDateTime = "";
    
    private boolean lpPeriodchange = false;
    
    //이벤트 : powerUp 일경우 Close Time을 저장하기위하여 추가함.
//    private String closeDate	=	"";    
//    private String closeTime	=	"";
    public String closeDate	=	"";    
    public String closeTime	=	"";
    
    /**
     * Constructor
     */
    public A1140_LP_DATA(byte[] rawData) {
        this.rawData = rawData;
        
        try {
            parseLpData();
        }
        catch (Exception e) {
            log.error(e, e);
        }
    }
    
    public static void main(String[] args) throws Exception {
        A1140_TEST_DATA testData = new A1140_TEST_DATA();
        A1140_LP_DATA elster = new A1140_LP_DATA(testData.getTestData_lp());
        elster.parseLpData();
        //System.out.println(elster.toString());
    }
    
    public LPData[] getLpData() {
        return (LPData[])lpList.toArray(new LPData[0]);
    }
    
    public int getLpPeriod() {
        return this.lpPeriod;
    }
    
    public List<EventLogData> getLpMeterEventLog() {
        return this.lpMeterEventLogList;
    }
    
    public List<PowerAlarmLogData> getLpPowerAlarmLog() {
        return this.lpPowerAlarmLogList;
    }
    
    private void parseLpData() throws Exception {
        int firstByte = 0x00;
        int offset    = 0;
        boolean startLpFlag = false;     // lp 저장을 시작했는지 저장. marker를 만나면 false로 리셋.
        
        byte[] b = null;
        while (offset < rawData.length) {
            firstByte = DataFormat.getIntToBytes(DataFormat.select(rawData, offset, LEN_MARKER_TYPE));
            A1140_LP_DATA_MARKER timeMarker = new A1140_LP_DATA_MARKER();
            
            if (checkMarker(firstByte)) {
                startLpFlag = false;
                
                A1140_LP_DATA_MARKER marker = new A1140_LP_DATA_MARKER();
                
                //Marker
                String markerType = Hex.decode(DataFormat.select(rawData, offset, LEN_MARKER_TYPE));
                offset += LEN_MARKER_TYPE;
                marker.setType(markerType);
                log.debug("MARKET_TYPE=["+markerType+"]");
                
                String markerDateTime = "";
                if(firstByte==0xE3){
                	int E3Data = DataFormat.getIntToBytes(DataFormat.select(rawData, offset, LEN_MARKER_TYPE));
                	offset += LEN_MARKER_TYPE;
                	log.debug("E3Data : " + E3Data);
                	
                	if(E3Data == 1)
                		marker.setType(TERMINAL_CASE_OPEN);
                	else if (E3Data == 2)                	
                		marker.setType(MAIN_CASE_OPEN);
                	else if (E3Data == 3)
                		marker.setType(TERMINAL_MAIN_COVER_OPEN);                	
                	else 
                		continue;
                	
                	String tmpE3DateTime = "";
                	if(nextLpDateTime == null || nextLpDateTime.equals("")){
                		tmpE3DateTime = markerList.get(markerList.size()-1).getDateTime();
                		tmpE3DateTime = dateTimeConvertByEvent(tmpE3DateTime);
                	} else {
                		log.debug("else");
                		tmpE3DateTime = nextLpDateTime;
                	}
                	
                	if(tmpE3DateTime.length() == 12)
                		tmpE3DateTime += "00";
                	
                	markerDateTime = tmpE3DateTime;
                } else {  
                	//yyyyMMddHHmmss
	                markerDateTime = ElsterA1140.convertTimestamp("MARER_DATETIME", DataFormat.select(rawData, offset, LEN_MARKER_TIME));
	                offset += LEN_MARKER_TIME;
                }
                
                log.debug("MARKET_DATETIME=["+markerDateTime+"]");
                marker.setDateTime(markerDateTime);
                
                //이벤트 타입이 CONFIGURATION_CAHNGE_MARKER, TIME_CHANGE_MARKER 일경우 LP저장 시간을 변경해준다.
                // NEW_DAY_MARKER는 밑에서 변경해준다.
                if (markerType.equals(TIME_CHANGE_MARKER) | markerType.equals(CONFIGURATION_CAHNGE_MARKER))
                	dateTime = dateTimeConvertByEvent(markerDateTime.substring(0, 12));
                
                if (checkLongMarker(firstByte)) {
                    int markerChannel = DataFormat.getIntToBytes(DataFormat.select(rawData, offset, LEN_MARKER_CHANNEL));
                    offset += LEN_MARKER_CHANNEL;
                    parseChannelCount(markerChannel);
                    marker.setChannel(markerChannel);
                    log.debug("markerChannel=[" + markerChannel + "]");
                    
                    String markerLpPeriod = Hex.decode(DataFormat.select(rawData, offset, LEN_MARKER_DEMAND_PERIOD));
                    offset += LEN_MARKER_DEMAND_PERIOD;
                    if(lpPeriod != Integer.parseInt((String) marker.getLpPeriodTable().get(markerLpPeriod+""))){
                    	lpPeriodchange = true;
                    }
                    lpPeriod = Integer.parseInt((String) marker.getLpPeriodTable().get(markerLpPeriod+""));                     
                    marker.setPeriod(lpPeriod);
                    log.debug("markerLpPeriod=[" + markerLpPeriod + "] lpPeriod=[" + lpPeriod + "]");
                    
                    if (markerType.equals(NEW_DAY_MARKER)) {
//                        dateTime = markerDateTime.substring(0, 8) + "0000";    // yyyymmdd만 가져오고 hhmm 은 00시 00분으로 셋팅.
                    	 dateTime = dateTimeConvertByEvent(markerDateTime.substring(0, 12));
                    	 lpPeriodchange = false;
                    }
                }
                markerList.add(marker);
                addEventList(marker);
                
                //시간관련 이벤트가 발생했는지 판단하기 위한 로직 추가.
                if(marker.getType().equals(TIME_CHANGE_MARKER) || marker.getType().equals(CONFIGURATION_CAHNGE_MARKER))
                	timeMarker = marker;
                
            } else if (firstByte == 0xFF){      // lp data end.
                log.debug("END");
                break;
            } else if (firstByte == 0xE2) {     // External Data Entry
                offset += LEN_MARKER_TYPE;
                
                int length = DataFormat.getIntToBytes(DataFormat.select(rawData, offset, LEN_EXTERNAL_DATA_ENTRY));
                offset += LEN_EXTERNAL_DATA_ENTRY;          
                // external data entry 처리 부분 추가 요망
                log.debug("EXTERNAL_DATA_ENTRY=["+Hex.decode(b)+"]");
                
                offset += length;
            } else {
                if (lpPeriod > 0) {
                    LPData lpData = null;
                    Double[] ch   = null;
                    
                    String lpField = null;
                    Double lpValue = 0.0;
                    long duration  = 0;
                    int lpStatus   = 0;
                    int lpScal     = 0;
                    int flag       = 0;
                    int zeroCnt    = 0;
                    
                    boolean sumLpFlag = false;     // 이전 lp에 현재 lp를 더해야되는지 판단
                    
                    // 앞에 marker를 받았을 경우
                    if (!startLpFlag) {
                        startLpFlag = true;
                        
                        if (markerList.size() > 1) {
                            // 현재 marker와 이전 marker를 가져온다.
                            A1140_LP_DATA_MARKER beforeMarker = markerList.get(markerList.size() - 2);
                            A1140_LP_DATA_MARKER afterMarker  = markerList.get(markerList.size() - 1);
                            // 현재 marker가 power up 일때
                            if (afterMarker.getType().equals(POWER_UP_MARKER)) {
                                // 이전 marker가 power down 또는 new day 일때
                                // 로직이 이상함 확인이 필요함
                                if (beforeMarker.getType().equals(POWER_DOWN_MARKER) || beforeMarker.getType().equals(NEW_DAY_MARKER)) {
                                    
                                    if(nextLpDateTime.equals("")) nextLpDateTime = afterMarker.getDateTime();
                                    duration = Util.getDuration(nextLpDateTime+"00", afterMarker.getDateTime());

                                    if (duration >= lpPeriod) {
                                        zeroCnt = (int) (duration / lpPeriod);     // 0 으로  셋팅해 줄 lp 개수를 구한다. 
                                        if (zeroCnt > 0) {
                                            for (int i = 0; i < zeroCnt; i++) {
                                                log.debug("lp i="+i);
                                                lpData = new LPData();
                                                ch = new Double[totalChannelCnt];
                                                for (int j = 0; j < totalChannelCnt; j++) {
                                                        ch[j] = 0.0;
                                                }
                                                lpData.setDatetime(dateTime);
                                                lpData.setLPChannelCnt(totalChannelCnt);
                                                lpData.setFlag(flag);
                                                lpData.setCh(ch);
                                                if(!lpPeriodchange)
                                                	lpList.add(lpData);
                                                addDateTimeBylpPeriod();     // lpTime 증가
                                            }
                                        }
                                    }
                                }
//                            } else if (afterMarker.getType().equals(TIME_CHANGE_MARKER) || afterMarker.getType().equals(CONFIGURATION_CAHNGE_MARKER)) {
                            } else if (timeMarker.getType() != null && (timeMarker.getType().equals(TIME_CHANGE_MARKER) || timeMarker.getType().equals(CONFIGURATION_CAHNGE_MARKER))) {
                            	
                            	if(lpList != null && lpList.size() > 0){
	                            	if(lpList.get(lpList.size()-1).getDatetime().equals(dateTimeConvertByEvent(afterMarker.getDateTime()))){
	                            		sumLpFlag = true;                            		
	                            	}
                            	}
//                                if (beforeMarker.getType().equals(NEW_DAY_MARKER)) {
//                                    duration = Util.getDuration(beforeMarker.getDateTime(), afterMarker.getDateTime());
//                                    // timesync로 인해 new day가 발생한 경우
//                                    if (duration < lpPeriod) {
//                                        goDateTimeToNow(afterMarker.getDateTime());
//                                    } else {
//                                        sumLpFlag = true;     // 바로 이전 lp에 현재 lp 값을 합해준다.
//                                    }
//                                } else {
//                                    sumLpFlag = true;
//                                }
                            } else if (afterMarker.getType().equals(LOAD_PROFILE_CLEARED_MARKER)) {
                                goDateTimeToNow(afterMarker.getDateTime());
                            }
                        }
                    }

                    lpStatus = DataFormat.getIntToBytes(DataFormat.select(rawData, offset, LEN_LP_STATUS));
                    log.debug("LP STATUS:"+Hex.decode(DataFormat.select(rawData, offset, LEN_LP_STATUS)));
                    addEventList(lpStatus);
                    offset += LEN_LP_STATUS;
                    
                    lpData = new LPData();
                    ch = new Double[totalChannelCnt];
                    
                    log.debug("totalChannelCnt:"+totalChannelCnt);
                    for (int i = 0; i < totalChannelCnt; i++) {
                        b = DataFormat.select(rawData, offset, LEN_LP_DATA);
                        // DataFormat.convertEndian(b);
                        lpField = Hex.decode(b);
                        offset += LEN_LP_DATA;
                        
                        lpScal = Integer.parseInt(lpField.substring(5));
                        lpValue = Double.parseDouble(lpField.substring(0, 5));
                        // mW->W(*1000)->kW(*1000)
                        //lpValue = lpValue / Math.pow(10, lpScal+6);
                        lpValue = (lpValue * Math.pow(10, lpScal))/1000000;

                        // lpValue = (lpValue * ke)/1000;
                        if (sumLpFlag)
                        	log.debug("lpField=["+lpField+"] lpValue_ch" + i + " " + lpList.get(lpList.size()-1).getDatetime() + "=[ beforeLP + " + lpValue + "]");
                        
                        if (!sumLpFlag)
                            log.debug("lpField=["+lpField+"] lpValue_ch" + i + " " + dateTime + "=[" + lpValue + "]");
                        
                        ch[i] = lpValue; // new Double(dformat.format(lpValue));
                    }
                    if (sumLpFlag) {
                    	log.debug("LP adds value. DateTime is " + lpList.get(lpList.size()-1).getDatetime());
                        LPData beforeLp = lpList.get(lpList.size() - 1);
                        Double[] beforeCh = beforeLp.getCh();
                        
                        // 채널 수가 똑같을때만 실행되도록 추가해야되는지.
                        if (beforeCh.length == totalChannelCnt)
                        for (int i = 0; i < totalChannelCnt; i++) {
                            ch[i] = ch[i] + beforeCh[i];
                        }
                        lpData.setDatetime(beforeLp.getDatetime());
                        lpData.setLPChannelCnt(totalChannelCnt);
                        lpData.setFlag(flag);
                        lpData.setCh(ch);
                        if(!lpPeriodchange)
                        	lpList.set(lpList.size() - 1, lpData);
                    } else {
                        lpData.setDatetime(dateTime);
                        lpData.setLPChannelCnt(totalChannelCnt);
                        lpData.setFlag(flag);
                        lpData.setCh(ch);
                        if(!lpPeriodchange)
                        	lpList.add(lpData);
                        addDateTimeBylpPeriod();
                        nextLpDateTime = dateTime;
                    }
                } else {
                    log.warn("lpPeriod Wrong !!!");
                }
            }
        }
    }
    
    private void addEventList(A1140_LP_DATA_MARKER marker) {
        //add meter & power event log
        EventLogData      eLog = new EventLogData();
        PowerAlarmLogData pLog = new PowerAlarmLogData();
        boolean b = false;
        
        if (marker.getType().equals(POWER_UP_MARKER)) {
            eLog.setDate(marker.getDateTime().substring(0, 8));
            eLog.setTime(marker.getDateTime().substring(8));
            eLog.setFlag(EVENTATTRIBUTE.POWER_UP.getCode());
            eLog.setMsg(EVENTATTRIBUTE.POWER_UP.getName());
            eLog.setAppend(EVENTATTRIBUTE.POWER_UP.getName());
            eLog.setKind("STE");

            //LP Event에서 Power Down, Power Up 이벤트 두개가 모두 존재시 Open시간 저장.
            if(!closeDate.equals("")){
	            pLog.setDate(closeDate);
	            pLog.setTime(closeTime);
            }else{	//Down Event가 없을 경우 같은 값을 저장한다.
            	pLog.setDate(marker.getDateTime().substring(0, 8));
                pLog.setTime(marker.getDateTime().substring(8));            	
            }
            
            //Close Date & Time은 Power Up 이벤트 시간을 저장한다.
            pLog.setCloseDate(marker.getDateTime().substring(0, 8));
            pLog.setCloseTime(marker.getDateTime().substring(8));
            
            pLog.setFlag(EVENTATTRIBUTE.POWER_UP.getCode());
            pLog.setMsg(EVENTATTRIBUTE.POWER_UP.getName());
            lpPowerAlarmLogList.add(pLog);
            b = true;
        } else if (marker.getType().equals(POWER_DOWN_MARKER)) {
            eLog.setDate(marker.getDateTime().substring(0, 8));
            eLog.setTime(marker.getDateTime().substring(8));
            eLog.setFlag(EVENTATTRIBUTE.POWER_DOWN.getCode());
            eLog.setMsg(EVENTATTRIBUTE.POWER_DOWN.getName());
            eLog.setAppend(EVENTATTRIBUTE.POWER_DOWN.getName());
            eLog.setKind("STE");
            
            pLog.setDate(marker.getDateTime().substring(0, 8));
            pLog.setTime(marker.getDateTime().substring(8));
            pLog.setFlag(EVENTATTRIBUTE.POWER_DOWN.getCode());
            pLog.setMsg(EVENTATTRIBUTE.POWER_DOWN.getName());
            
            closeDate = marker.getDateTime().substring(0, 8);
            closeTime = marker.getDateTime().substring(8);
            lpPowerAlarmLogList.add(pLog);
            b = true;
        } else if (marker.getType().equals(CONFIGURATION_CAHNGE_MARKER)) {
            eLog.setDate(marker.getDateTime().substring(0, 8));
            eLog.setTime(marker.getDateTime().substring(8));
            eLog.setFlag(EVENTATTRIBUTE.CONFIG_CHANGE.getCode());
            eLog.setMsg(EVENTATTRIBUTE.CONFIG_CHANGE.getName());
            eLog.setAppend(EVENTATTRIBUTE.CONFIG_CHANGE.getName());
            eLog.setKind("STE");
            b = true;
        } else if (marker.getType().equals(TIME_CHANGE_MARKER)) {
            eLog.setDate(marker.getDateTime().substring(0, 8));
            eLog.setTime(marker.getDateTime().substring(8));
            eLog.setFlag(EVENTATTRIBUTE.TIME_CHANGE.getCode());
            eLog.setMsg(EVENTATTRIBUTE.TIME_CHANGE.getName());
            eLog.setAppend(EVENTATTRIBUTE.TIME_CHANGE.getName());
            eLog.setKind("STE");
            b = true;
        } else if (marker.getType().equals(DAYLIGHT_SAVING_MARKER)) {
            eLog.setDate(marker.getDateTime().substring(0, 8));
            eLog.setTime(marker.getDateTime().substring(8));
            eLog.setFlag(EVENTATTRIBUTE.DAYLIGHT_SAVING.getCode());
            eLog.setMsg(EVENTATTRIBUTE.DAYLIGHT_SAVING.getName());
            eLog.setAppend(EVENTATTRIBUTE.DAYLIGHT_SAVING.getName());
            eLog.setKind("STE");
            b = true;
        } else if (marker.getType().equals(LOAD_PROFILE_CLEARED_MARKER)) {
            eLog.setDate(marker.getDateTime().substring(0, 8));
            eLog.setTime(marker.getDateTime().substring(8));
            eLog.setFlag(EVENTATTRIBUTE.LP_CLEARED.getCode());
            eLog.setMsg(EVENTATTRIBUTE.LP_CLEARED.getName());
            eLog.setAppend(EVENTATTRIBUTE.LP_CLEARED.getName());
            eLog.setKind("STE");
            b = true;
        } else if (marker.getType().equals(TERMINAL_CASE_OPEN)){
        	eLog.setDate(marker.getDateTime().substring(0, 8));
            eLog.setTime(marker.getDateTime().substring(8));
            eLog.setFlag(EVENTATTRIBUTE.TERMINAL_COVER_OPEN.getCode());
            eLog.setMsg(EVENTATTRIBUTE.TERMINAL_COVER_OPEN.getName());
            eLog.setAppend(EVENTATTRIBUTE.TERMINAL_COVER_OPEN.getName());
            eLog.setKind("STE");
            b = true;
        } else if (marker.getType().equals(MAIN_CASE_OPEN)){
        	eLog.setDate(marker.getDateTime().substring(0, 8));
            eLog.setTime(marker.getDateTime().substring(8));
            eLog.setFlag(EVENTATTRIBUTE.MAIN_COVER_OPEN.getCode());
            eLog.setMsg(EVENTATTRIBUTE.MAIN_COVER_OPEN.getName());
            eLog.setAppend(EVENTATTRIBUTE.MAIN_COVER_OPEN.getName());
            eLog.setKind("STE");
            b = true;      
        } else if (marker.getType().equals(TERMINAL_MAIN_COVER_OPEN)){
        	eLog.setDate(marker.getDateTime().substring(0, 8));
            eLog.setTime(marker.getDateTime().substring(8));
            eLog.setFlag(EVENTATTRIBUTE.TERMINAL_MAIN_COVER_OPEN.getCode());
            eLog.setMsg(EVENTATTRIBUTE.TERMINAL_MAIN_COVER_OPEN.getName());
            eLog.setAppend(EVENTATTRIBUTE.TERMINAL_MAIN_COVER_OPEN.getName());
            eLog.setKind("STE");
            b = true;
        }
        if(b){
            lpMeterEventLogList.add(eLog);
//            lpPowerAlarmLogList.add(pLog);
        }
    }

    /**
     * lpStatus를 파싱한 후, 이벤트로그에 저장하기 위해 리스트에 추가한다.
     * @param status
     */
    private void addEventList(int status) {
        String statusByte     = Integer.toBinaryString(status);
        String blank          = "";
        EventLogData eventLog = null;

        if (statusByte.length() < LEN_LP_STATUS_AS_BIT) {
            for (int i = statusByte.length(); i < LEN_LP_STATUS_AS_BIT; i++) {
                blank += "0";
            }
            statusByte = blank + statusByte;
        }

        // MSB->LSB 첫번째 비트는 버린다.
        for (int i = 0; i < statusByte.length(); i++) {
            if (statusByte.charAt(i) == '1') {
                eventLog = new EventLogData();
                eventLog.setDate(dateTime.substring(0, 8));      // yyyymmdd
                eventLog.setTime(dateTime.substring(8)+"00");    // hhmmss
                eventLog.setFlag(STATUS[i].getCode());
                eventLog.setMsg(STATUS[i].getName());
                eventLog.setAppend(STATUS[i].getName());
                lpMeterEventLogList.add(eventLog);
            }
        }
    }

    /**
     * 현재 시간으로 dateTime을 증가시킨다.
     * @param now
     * @throws Exception
     */
    private void goDateTimeToNow(String now) throws Exception {
        String before = dateTime;
        int cnt = 0;
        
        long duration = Util.getDuration(before + "00", now);
        cnt = (int) duration / lpPeriod;
        for (int i = 0; i < cnt; i++) {
            addDateTimeBylpPeriod();
        }
    }
    
    /**
     * dateTime을 lpPeriod만큼 증가시킨다.
     * @return now dateTime
     * @throws Exception 
     */
    private void addDateTimeBylpPeriod() throws Exception {
        dateTime = Util.addMinYymmdd(dateTime, lpPeriod).substring(0, 12);     // yyyymmddhhmm
    }
    
    /**
     * Marker는 5byte, 8byte로 구분. 8byte에 해당하는지 체크
     * @param marker
     * @return
     */
    private boolean checkLongMarker(int marker) {
        if (marker == 0xE4 || marker == 0xE8) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Marker에 해당하는지 체크
     * @param marker
     * @return
     */
    private boolean checkMarker(int marker) {
        if (marker == 0xE4 || marker == 0xE5 || marker == 0xE6 || marker == 0xE8 || 
                marker == 0xE9 || marker == 0xEA || marker == 0xEB || marker == 0xED || marker == 0xE3) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Channel 수 구하기
     * 0~10 : internal, 11~14 : external, 15 : not used
     * @param channel
     */
    private void parseChannelCount(int channel) {
        String chByte = Integer.toBinaryString(channel);
        internalChannelCnt = 0;
        externalChannelCnt = 0;
        
        String blank = "";
        if (chByte.length() < LEN_MARKER_CHANNEL_AS_BIT) {
            for (int i = chByte.length(); i < LEN_MARKER_CHANNEL_AS_BIT; i++) {
                blank += "0";
            }
            chByte = blank + chByte;
        }

        for (int i = chByte.length() - 1; i > 0; i--) {
            if (chByte.charAt(i) == '1') {
                if (i > 10) {
                    externalChannelCnt++;
                } else {
                    internalChannelCnt++;
                }
            }
        }
        totalChannelCnt = internalChannelCnt + externalChannelCnt;
    }
    
    public int getChannelCount() {
        return totalChannelCnt;
    }
    
    /**
     * Elster는 sec로 timestamp를 보내기때문에 1000을 더 곱해준다.
     * @param timestamp
     * @return
     */
    private long getTimestampForElster(long timestamp) {
        return timestamp * 1000;
    }
    
    @SuppressWarnings("unchecked")
    public String toString() {
        try {
            StringBuffer sb = new StringBuffer();
            
            try {
                sb.append("A1400_LP_DATA[\n" );
                Iterator iter = null;
                sb.append("  (MARKER={");
                iter = markerList.iterator();
                while (iter.hasNext()) {
                    sb.append(iter.next().toString());
                }
                sb.append("  }),\n");
                sb.append("  (METER_EVENT={");
                iter = lpMeterEventLogList.iterator();
                while (iter.hasNext()) {
                    sb.append(iter.next().toString());
                }
                sb.append("  }),\n");
                sb.append("  (POWER_ALARM={");
                iter = lpPowerAlarmLogList.iterator();
                while (iter.hasNext()) {
                    sb.append(iter.next().toString());
                }
                sb.append("  }),\n");
                sb.append("  (LP={");
                iter = lpList.iterator();
                while (iter.hasNext()) {
                    sb.append(iter.next());
                }
                sb.append("  })\n");
                sb.append("]\n");
            } catch (Exception e) {
                log.error(e);
            }
            return sb.toString();
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }
    
    /**
     * 시간 변경이벤트 발생시 입력받은 시간값을 LP주기에 맞게 변경후 반환
     * @param oldDateTime
     * @return newDateTime(lpPeriod 계산)
     */
    private String dateTimeConvertByEvent(String oldDateTime){
    	String newDateTime = ""; //yyyymmddhhmm
    	String yyyy 	= oldDateTime.substring(0, 4);
    	String mm 		= oldDateTime.substring(4, 6);
    	String dd 		= oldDateTime.substring(6, 8);
    	String hh 		= oldDateTime.substring(8, 10);
    	String MM 		= oldDateTime.substring(10, 12); 
    	int oldMin		= Integer.parseInt(MM);
    	String newMin	= "";
    	
    	int calculatedMinutes = oldMin - (oldMin % this.lpPeriod);
    	if(calculatedMinutes < 10) newMin = "0" + calculatedMinutes;
    	else newMin = "" + calculatedMinutes;
    	newDateTime = yyyy + mm + dd + hh + newMin;
    	
    	return newDateTime;
    }
}
