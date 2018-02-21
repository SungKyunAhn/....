package com.aimir.fep.meter.saver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.DLMSStype;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

@Service
public class DLMSStypeMDSaver extends AbstractMDSaver {
    private static Log log = LogFactory.getLog(DLMSStypeMDSaver.class);
	
    @Override
    public boolean save(IMeasurementData md) throws Exception {
        try {
            DLMSStype parser = (DLMSStype) md.getMeterDataParser();
    
            // 마지막 LP 데이타를 가져와서 LP를 다시 구성한다.
            LPData lastLp = null; // getLastLp(parser.getMeterID(), parser.getMeter().getInstallProperty(), 
                    // parser.getMeter().getPulseConstant());
            LPData[] lplist = parser.getLPData();
            
            // log.debug("active pulse constant:" +
            // parser.getActivePulseConstant());
            // log.debug("currentDemand:" + currentDemand);
            
            if (lplist == null || lplist.length <= 1) {
                log.debug("LPSIZE => 0");
            } else {
                log.info("lplist[0]:"+lplist[1]);
                log.info("lplist[0].getDatetime():"+lplist[1].getDatetime());
    			
                String startlpdate = lplist[1].getDatetime();
                String lpdatetime = startlpdate;
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
                cal.setTime(sdf.parse(startlpdate));
                List<Double>[] chValue = new ArrayList[lplist[1].getCh().length];
                List<Integer> flag = new ArrayList<Integer>();
                double baseValue = lplist[1].getCh()[1];
    	        
                for (int i = 1; i < lplist.length; i++) {
                    if (!lpdatetime.equals(lplist[i].getDatetime())) {
                        saveLPData(chValue, flag, startlpdate, baseValue, parser);
    	        		
                        startlpdate = lplist[i].getDatetime();
                        lpdatetime = startlpdate;
                        baseValue = lplist[i].getLpValue();
                        flag = new ArrayList<Integer>();
                        chValue = new ArrayList[lplist[i].getCh().length];
                    }
                    flag.add(lplist[i].getFlag());
            		
                    for (int ch = 0; ch < chValue.length; ch++) {
                        if (chValue[ch] == null) chValue[ch] = new ArrayList<Double>();
            			
                        if (ch+1 <= lplist[i].getCh().length)
                            chValue[ch].add(lplist[i].getCh()[ch]);
                        else
                            chValue[ch].add(0.0);
                    }
                    cal.add(Calendar.MINUTE, parser.getMeter().getLpInterval());
                    lpdatetime = sdf.format(cal.getTime());
                }
                saveLPData(chValue, flag, startlpdate, baseValue, parser);
    	        
                
                try {
                    /*saveMeteringData(MeteringType.Normal, md.getTimeStamp().substring(0,8),
                             md.getTimeStamp().substring(8, 14), parser.getMeteringValue(),
                             parser.getMeter(), DeviceType.MCU, parser.getMeter().getMcu().getSysID(),
                             DeviceType.Meter, parser.getMeterID(), parser.getMeterTime());*/
                 // 미터와 모뎀 최종 통신 시간과 값을 갱신한다.
                    // if (meter.getLastMeteringValue() == null || meter.getLastMeteringValue() < meteringValue)
                    //    meter.setLastMeteringValue(meteringValue);
                    Meter meter = parser.getMeter();
                    String dsttime = DateTimeUtil.getDST(null, md.getTimeStamp());
                    if (meter.getLastReadDate() == null || dsttime.compareTo(meter.getLastReadDate()) > 0) {
                        meter.setLastReadDate(dsttime);
                        Code meterStatus = CommonConstants.getMeterStatusByName(MeterStatus.Normal.name());
                        log.debug("METER_STATUS[" + (meterStatus==null? "NULL":meterStatus.getName()) + "]");
                        meter.setMeterStatus(meterStatus);
                        meter.setLastMeteringValue(parser.getMeteringValue());
                        
                        String meterTime = parser.getMeterTime();
                        if (meterTime != null && !"".equals(meterTime)) {
                            long diff = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(md.getTimeStamp()).getTime() - 
                                    DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime();
                            meter.setTimeDiff(diff / 1000);
                        }
                        
                     // 수검침과 같이 모뎀과 관련이 없는 경우는 예외로 처리한다.
                        if (meter.getModem() != null) {
                            meter.getModem().setLastLinkTime(dsttime);
                        }
                    }
                }
                catch (Exception ignore) {}
            }
	    log.info(parser.getMDevId() + " Metering END......!!!!");	
        }
        catch (Exception e) {
            log.error(e, e);
            throw e;
        }
        return true;
    }
	
    public void saveLPData(List<Double>[] chValue, List<Integer> flag, String startlpdate, double baseValue, DLMSStype parser)
            throws Exception {
        double[][] _lplist = new double[chValue.length][chValue[0].size()];
        for (int ch = 0; ch < _lplist.length; ch++) {
            for (int j = 0; j < _lplist[ch].length; j++) {
                if (chValue[ch].get(j) != null)
                    _lplist[ch][j] = chValue[ch].get(j);
                else
                    _lplist[ch][j] = 0.0;
            }
        }
        int[] _flag = new int[chValue[0].size()];
        for (int j = 0; j < _flag.length; j++) {
            _flag[j] = flag.get(j);
        }
        super.saveLPData(MeteringType.Normal, startlpdate.substring(0, 8), startlpdate.substring(8)+"00",
                _lplist, _flag, baseValue, parser.getMeter(),
                DeviceType.Modem, parser.getMeter().getModem().getDeviceSerial(),
                DeviceType.Meter, parser.getMeterID());
    }
	
    @Override
    public String relayValveOn(String mcuId, String meterId) {
        return null;
    }

    @Override
    public String relayValveOff(String mcuId, String meterId) {
        return null;
    }

    @Override
    public String relayValveStatus(String mcuId, String meterId) {
        return null;
    }

    @Override
    public String syncTime(String mcuId, String meterId) {
        Map<String, String> resultMap = new HashMap<String, String>();
        int result = 0;
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            resultMap = commandGw.cmdMeterTimeSyncByGtype(mcuId,meter.getMdsId());
            if(resultMap != null) {
                String before = (String) resultMap.get("beforeTime");
                String after = (String) resultMap.get("afterTime");
                String diff = String.valueOf((TimeUtil.getLongTime(after) - TimeUtil.getLongTime(before))/1000);
                resultMap.put("diff", diff);
                
                saveMeterTimeSyncLog(meter, before, after, result);
            }
        }
        catch (Exception e) {
            result = 1;
            resultMap.put("failReason", e.getMessage());
        }
        return MapToJSON(resultMap);
    }
}
