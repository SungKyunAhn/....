package com.aimir.fep.meter.parser.elsterA1700Table;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.PowerAlarmLogData;
import com.aimir.fep.meter.parser.ElsterA1700;
import com.aimir.fep.meter.parser.elsterA1700Table.EVENT_ATTRIBUTE.EVENTATTRIBUTE;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;

/**
 * @author EJ Choi
 */
public class A1700_LP_DATA {    
    private Log log = LogFactory.getLog(A1700_LP_DATA.class);
    
    // 2.10.1 Load Profile Status Entry MSB -> LSB
    private static final EVENTATTRIBUTE[] STATUS = {EVENTATTRIBUTE.NOT_USED, 
        EVENTATTRIBUTE.PHASE_FAILURE,
        EVENTATTRIBUTE.REVERSE_RUNNING,
        EVENTATTRIBUTE.NOT_USED,
        EVENTATTRIBUTE.BATTERY_FAIL,
        EVENTATTRIBUTE.DATA_CHANGE,
        EVENTATTRIBUTE.TIME_SYNC,
        EVENTATTRIBUTE.METER_RESET};
    
    public static final int LEN_MARKER_TYPE           = 1;
    public static final int LEN_MARKER_TIME           = 4;
    public static final int LEN_MARKER_CHANNEL        = 2;
    public static final int LEN_MARKER_CHANNEL_AS_BIT = 16;
    public static final int LEN_MARKER_DEMAND_PERIOD  = 1;
    
    public static final int LEN_LP_STATUS             = 1;
    public static final int LEN_LP_STATUS_AS_BIT      = 8;
    public static final int LEN_LP_DATA               = 3;
    
    public static final int LEN_EXTERNAL_DATA_ENTRY   = 2;
    
    public static final String EXTERNAL_DATA_ENTRY_MARKER  = "E2";
    public static final String NEW_DAY_MARKER              = "E4";
    public static final String POWER_UP_MARKER             = "E5";
    public static final String POWER_DOWN_MARKER           = "E6";
    public static final String CONFIGURATION_CAHNGE_MARKER = "E8";
    public static final String TIME_CHANGE_MARKER          = "EA";
    public static final String DAYLIGHT_SAVING_MARKER      = "ED";
    public static final String LOAD_PROFILE_CLEARED_MARKER = "EB";
    public static final String FORCED_END_OF_DEMAND_MARKER = "E9";

    private byte[] rawData = null;
    
    private List<A1700_LP_DATA_MARKER> markerList          = new ArrayList<A1700_LP_DATA_MARKER>();
    private List<LPData>               lpList              = new ArrayList<LPData>();
    private List<EventLogData>         lpMeterEventLogList = new ArrayList<EventLogData>();
    private List<PowerAlarmLogData>    lpPowerAlarmLogList = new ArrayList<PowerAlarmLogData>();
    
    private int totalChannelCnt    = 0;
    private int internalChannelCnt = 0;
    private int externalChannelCnt = 0;
    
    private int lpPeriod = 0;

    private String dateTime       = "";
    private String nextLpDateTime = "";
    
    // DecimalFormat dformat = new DecimalFormat("#0.000000");

    /**
     * Constructor
     */
    public A1700_LP_DATA(byte[] rawData) {
        this.rawData = rawData;
    }
    
    public static void main(String[] args) throws Exception {
        A1700_TEST_DATA testData = new A1700_TEST_DATA();
        A1700_LP_DATA elster = new A1700_LP_DATA(testData.getTestData_lp());
        elster.parseLpData();
//      System.out.println(elster.toString());
    }
    
    public LPData[] getLpData() throws Exception {
        log.debug("parseLpData.....start ==="+lpList.size());
        parseLpData();
        log.debug("parseLpData.....end lpList ="+lpList+"lpList.size() "+lpList.size());
        if (lpList != null && lpList.size() > 0) {  
            LPData[] lpData = null;
            Object[] obj = lpList.toArray();            
            lpData = new LPData[obj.length];
            for (int i = 0; i < obj.length; i++) {
                lpData[i] = (LPData)obj[i];
            }
            return lpData;
        } else {
            return null;
        }
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
        log.debug("parseLpData().....start ==="+lpList.size());
        int firstByte = 0x00;
        int offset    = 0;
        boolean startLpFlag = false;     // lp 저장을 시작했는지 저장. marker를 만나면 false로 리셋.
        
        firstByte = DataFormat.getIntToBytes(DataFormat.select(rawData, offset, LEN_MARKER_TYPE));
        
        DecimalFormat df = new DecimalFormat("0.#####");
        byte[] b = null;
        while (offset < rawData.length) {
            firstByte = DataFormat.getIntToBytes(DataFormat.select(rawData, offset, LEN_MARKER_TYPE));
                
            if (checkMarker(firstByte)) {
                startLpFlag = false;
                
                A1700_LP_DATA_MARKER marker = new A1700_LP_DATA_MARKER();
                
                String markerType = Hex.decode(DataFormat.select(rawData, offset, LEN_MARKER_TYPE));
                log.debug("markerType=[" + markerType + "]");
                offset += LEN_MARKER_TYPE;
                marker.setType(markerType);
                
                String markerDateTime = ElsterA1700.convertTimestamp("MARKER_DATETIME", DataFormat.select(rawData, offset, LEN_MARKER_TIME));
                offset += LEN_MARKER_TIME; 
                marker.setDateTime(markerDateTime);
                
                if (checkLongMarker(firstByte)) {
                    b = DataFormat.select(rawData, offset, LEN_MARKER_CHANNEL);
                    int markerChannel = DataFormat.getIntToBytes(b);
                    offset += LEN_MARKER_CHANNEL;
                    parseChannelCount(markerChannel);
                    marker.setChannel(markerChannel);
                    log.debug("markerChannel=[" + markerChannel + "] RAW=[" + Hex.decode(b) + "]");

                    b = DataFormat.select(rawData, offset, LEN_MARKER_DEMAND_PERIOD);
                    String markerLpPeriod = Hex.decode(b);
                    offset += LEN_MARKER_DEMAND_PERIOD;
                    lpPeriod = Integer.parseInt((String) marker.getLpPeriodTable().get(markerLpPeriod+""));
                    marker.setPeriod(lpPeriod);
                    log.debug("markerLpPeriod=[" + markerLpPeriod + "] lpPeriod=[" + lpPeriod + "]");
                    
                    if (markerType.equals(NEW_DAY_MARKER)) {
                        dateTime = markerDateTime.substring(0, 8) + "0000";    // yyyymmdd만 가져오고 hhmm 은 00시 00분으로 셋팅.
                    }
                }
                markerList.add(marker);
                addEventList(marker);
            } else if (firstByte == 0xFF){      // lp data end.
                log.debug("END");
                break;
            } else if (firstByte == 0xE2) {     // External Data Entry
                offset += LEN_MARKER_TYPE;
                
                b = DataFormat.select(rawData, offset, LEN_EXTERNAL_DATA_ENTRY);
                int length = DataFormat.getIntToBytes(b);
                log.debug("EXTERNAL_DATA_ENTRY=["+Hex.decode(b)+"]");
                offset += LEN_EXTERNAL_DATA_ENTRY;
                
                // external data entry 처리 부분 추가 요망
                offset += length;
            }
            else {
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
                            A1700_LP_DATA_MARKER beforeMarker = markerList.get(markerList.size() - 2);
                            A1700_LP_DATA_MARKER afterMarker  = markerList.get(markerList.size() - 1);
                            log.debug("before=[" + beforeMarker.getType() + "], after=[" + afterMarker.getType() + "]");
                            // 현재 marker가 power up 일때
                            if (afterMarker.getType().equals(POWER_UP_MARKER)) {
                                // 이전 marker가 power down 또는 new day 일때
                                if (beforeMarker.getType().equals(POWER_DOWN_MARKER) || beforeMarker.getType().equals(NEW_DAY_MARKER)) {
                                    duration = Util.getDuration(nextLpDateTime+"00", afterMarker.getDateTime());
                                    //duration = 0 ;//임시로 0으로 세팅 함
                                    log.debug(afterMarker.getDateTime() + "<->" + nextLpDateTime + "=");
                                    log.debug("duration=[" + duration + "], lpPeriod=[" + lpPeriod + "]");
                                    if (duration >= lpPeriod) {
                                        zeroCnt = (int) (duration / lpPeriod);     // 0 으로  셋팅해 줄 lp 개수를 구한다. 
                                        log.debug("duration=[" + duration + "], zeroCnt=[" + zeroCnt + "]");
                                        if (zeroCnt > 0) {
                                            for (int i = 0; i < zeroCnt; i++) {
                                                log.debug("lp i="+i);
                                                lpData = new LPData();
                                                ch = new Double[totalChannelCnt];
                                                for (int j = 0; j < totalChannelCnt; j++) {
                                                        ch[j] = 0.0; // new Double(dformat.format(0));
                                                }
                                                lpData.setDatetime(dateTime);
                                                lpData.setLPChannelCnt(totalChannelCnt);
                                                lpData.setFlag(flag);
                                                lpData.setCh(ch);
                                                lpList.add(lpData);
                                                addDateTimeBylpPeriod();     // lpTime 증가
                                            }
                                        }
                                    }
                                }
                            } else if (afterMarker.getType().equals(TIME_CHANGE_MARKER) || 
                                    afterMarker.getType().equals(CONFIGURATION_CAHNGE_MARKER)) {
                                if (beforeMarker.getType().equals(NEW_DAY_MARKER)) {
                                    duration = Util.getDuration(beforeMarker.getDateTime(), afterMarker.getDateTime());
                                    log.debug("duration=[" + duration + "]");
                                    // timesync로 인해 new day가 발생한 경우
                                    if (duration < lpPeriod) {
                                        goDateTimeToNow(afterMarker.getDateTime());
                                    } else {
                                        sumLpFlag = true;     // 바로 이전 lp에 현재 lp 값을 합해준다.
                                    }
                                } else {
                                    sumLpFlag = true;
                                }
                            } else if (afterMarker.getType().equals(LOAD_PROFILE_CLEARED_MARKER)) {
                                goDateTimeToNow(afterMarker.getDateTime());
                            }
                        }
                    }

                    b = DataFormat.select(rawData, offset, LEN_LP_STATUS);
                    log.debug(Hex.decode(b));
                    lpStatus = DataFormat.getIntToBytes(b);
                    addEventList(lpStatus);
                    offset += LEN_LP_STATUS;
                    log.debug("lpStatus=[" + lpStatus + "]");
                    
                    lpData = new LPData();
                    ch = new Double[totalChannelCnt];
                    log.debug("totalChannelCnt "+totalChannelCnt);
                    for (int i = 0; i < totalChannelCnt; i++) {
                        b = DataFormat.select(rawData, offset, LEN_LP_DATA);
                        lpField = Hex.decode(b);
                        offset += LEN_LP_DATA;
                        
                        lpScal = Integer.parseInt(lpField.substring(5));
                        lpValue = Double.parseDouble(lpField.substring(0, 5));
                        // mW->W(*1000)->kW(*1000)
                        lpValue = lpValue / Math.pow(10, 6-lpScal);
                        // lpValue = (lpValue * ke)/1000;
                        
                        log.debug("lpField=["+lpField+"] lpScal["+lpScal+"] lpValue_ch" + i + " " + dateTime + "=[" + df.format(lpValue) + "]");
                        
                        ch[i] = lpValue; // new Double(dformat.format(lpValue));
                    }
                    
                    if (sumLpFlag) {
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
                        lpList.set(lpList.size() - 1, lpData);
                    } else {
                        lpData.setDatetime(dateTime);
                        lpData.setLPChannelCnt(totalChannelCnt);
                        lpData.setFlag(flag);
                        lpData.setCh(ch);
                        lpList.add(lpData);
                        addDateTimeBylpPeriod();
                        nextLpDateTime = dateTime;
                    }
                } else {
                    log.warn("lpPeriod Wrong !!!");
                }
            }
        }
        
        log.debug("LP_Data         "+lpList.size());
    }
    
    private void addEventList(A1700_LP_DATA_MARKER marker) {
        //add meter & power event log
        EventLogData      eLog = new EventLogData();
        PowerAlarmLogData pLog = new PowerAlarmLogData();
        
        if (marker.getType().equals(POWER_UP_MARKER)) {
            eLog.setDate(marker.getDateTime().substring(0, 8));
            eLog.setTime(marker.getDateTime().substring(8));
            eLog.setFlag(EVENTATTRIBUTE.POWER_UP.getCode());
            eLog.setMsg(EVENTATTRIBUTE.POWER_UP.getName());
            eLog.setAppend(EVENTATTRIBUTE.POWER_UP.getName());
            
            pLog.setDate(marker.getDateTime().substring(0, 8));
            pLog.setTime(marker.getDateTime().substring(8));
            pLog.setFlag(EVENTATTRIBUTE.POWER_UP.getCode());
            pLog.setMsg(EVENTATTRIBUTE.POWER_UP.getName());
        } else if (marker.getType().equals(POWER_DOWN_MARKER)) {
            eLog.setDate(marker.getDateTime().substring(0, 8));
            eLog.setTime(marker.getDateTime().substring(8));
            eLog.setFlag(EVENTATTRIBUTE.POWER_DOWN.getCode());
            eLog.setMsg(EVENTATTRIBUTE.POWER_DOWN.getName());
            eLog.setAppend(EVENTATTRIBUTE.POWER_DOWN.getName());
            
            pLog.setDate(marker.getDateTime().substring(0, 8));
            pLog.setTime(marker.getDateTime().substring(8));
            pLog.setFlag(EVENTATTRIBUTE.POWER_DOWN.getCode());
            pLog.setMsg(EVENTATTRIBUTE.POWER_DOWN.getName());
        } else if (marker.getType().equals(CONFIGURATION_CAHNGE_MARKER)) {
            eLog.setDate(marker.getDateTime().substring(0, 8));
            eLog.setTime(marker.getDateTime().substring(8));
            eLog.setFlag(EVENTATTRIBUTE.CONFIG_CHANGE.getCode());
            eLog.setMsg(EVENTATTRIBUTE.CONFIG_CHANGE.getName());
            eLog.setAppend(EVENTATTRIBUTE.CONFIG_CHANGE.getName());
        } else if (marker.getType().equals(TIME_CHANGE_MARKER)) {
            eLog.setDate(marker.getDateTime().substring(0, 8));
            eLog.setTime(marker.getDateTime().substring(8));
            eLog.setFlag(EVENTATTRIBUTE.TIME_CHANGE.getCode());
            eLog.setMsg(EVENTATTRIBUTE.TIME_CHANGE.getName());
            eLog.setAppend(EVENTATTRIBUTE.TIME_CHANGE.getName());
        } else if (marker.getType().equals(DAYLIGHT_SAVING_MARKER)) {
            eLog.setDate(marker.getDateTime().substring(0, 8));
            eLog.setTime(marker.getDateTime().substring(8));
            eLog.setFlag(EVENTATTRIBUTE.DAYLIGHT_SAVING.getCode());
            eLog.setMsg(EVENTATTRIBUTE.DAYLIGHT_SAVING.getName());
            eLog.setAppend(EVENTATTRIBUTE.DAYLIGHT_SAVING.getName());
        } else if (marker.getType().equals(LOAD_PROFILE_CLEARED_MARKER)) {
            eLog.setDate(marker.getDateTime().substring(0, 8));
            eLog.setTime(marker.getDateTime().substring(8));
            eLog.setFlag(EVENTATTRIBUTE.LP_CLEARED.getCode());
            eLog.setMsg(EVENTATTRIBUTE.LP_CLEARED.getName());
            eLog.setAppend(EVENTATTRIBUTE.LP_CLEARED.getName());
        } else if (marker.getType().equals(FORCED_END_OF_DEMAND_MARKER)) {
            eLog.setDate(marker.getDateTime().substring(0, 8));
            eLog.setTime(marker.getDateTime().substring(8));
            eLog.setFlag(EVENTATTRIBUTE.FORCED_END_OF_DEMAND.getCode());
            eLog.setMsg(EVENTATTRIBUTE.FORCED_END_OF_DEMAND.getName());
            eLog.setAppend(EVENTATTRIBUTE.FORCED_END_OF_DEMAND.getName());
        }
        lpMeterEventLogList.add(eLog);
        lpPowerAlarmLogList.add(pLog);
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
                marker == 0xE9 || marker == 0xEA || marker == 0xEB || marker == 0xED) {
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
    
    public String toString() {
        StringBuffer sb = new StringBuffer();

        try {
            sb.append("A1700_LP_DATA[\n" );
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
}
