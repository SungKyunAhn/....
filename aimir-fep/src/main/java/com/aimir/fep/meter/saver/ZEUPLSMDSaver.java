package com.aimir.fep.meter.saver;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.MeteringFlag;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.BatteryLog;
import com.aimir.fep.meter.parser.ModemLPData;
import com.aimir.fep.meter.parser.ZEUPLS;
import com.aimir.fep.meter.parser.ZEUPLS2;
import com.aimir.model.device.HMU;
import com.aimir.model.mvm.MeteringLP;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;

public class ZEUPLSMDSaver extends AbstractMDSaver {
	
    @Override
    protected boolean save(IMeasurementData md) throws Exception 
    {
        ZEUPLS parser = (ZEUPLS)md.getMeterDataParser();
        
        // 날짜포맷이 맞지 않거나 시스템시간의 일시가 다르면 
        if (parser.getCurrentTime() != null && 
                (parser.getCurrentTime().length() != 14)) {
            log.error("TIME[" + parser.getCurrentTime() + "] IS WRONG SO THEN RETURN NOT BACKUP");
            return false;
        }
        
        // period를 가져와서 주기를 검사한다.
        int interval = 60 / (parser.getPeriod() != 0? parser.getPeriod():1);
        if (interval != parser.getMeter().getLpInterval())
            parser.getMeter().setLpInterval(interval);
        
        // 올라온 검침데이타의 검침값을 저장한다.
        saveMeteringData(MeteringType.Normal, parser.getCurrentTime().substring(0, 8),
                parser.getCurrentTime().substring(8), parser.getMeteringValue(),
                parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
                parser.getMDevType(), parser.getMDevId(), parser.getMeterTime());
        
        // LP 데이타 저장
        int[] flaglist = null;
        double[] lp = null;
        HashSet<Condition> condition = null;
        MeteringLP mlp = null;
        int hh = 0;
        int mm = 0;
        int maxIdx = 0;
        double baseValue = 0.0;
        int startIdx = 0;
        String lpdate = null;
        List lplist = null;
        
        for (ModemLPData lpdata : parser.getLpData()) {
            hh = 0;
            maxIdx = 0;
            startIdx = 0;
            
            if (lpdata == null || lpdata.getLp() == null)
                continue;
            
            // LP Date의 날짜가 다르면 깨진 것
            lpdate = lpdata.getLpDate();
            // if (0 < lpdate.substring(0,4).compareTo(before2day.substring(0,4)) ||
            //        0 > lpdate.substring(0,4).compareTo(systemTime.substring(0,4)))
                 // continue;
            
            // 당일 것이 없으면 전일 것을 가져올 수 있도록 한번 돌린다. 
            for (int repeat = 0; repeat < 2; repeat++) {
                log.info("LP DATE[" + lpdate + "]");
                // lpDate의 lp를 조회해서 값이 있는 것까지의 주기를 구하고 base를 위해 value를 계산한다.
                condition = new HashSet<Condition>();
                condition.add(new Condition("id.mdevType", new Object[]{parser.getMDevType()}, null, Restriction.EQ));
                condition.add(new Condition("id.mdevId", new Object[]{parser.getMDevId()}, null, Restriction.EQ));
                // condition.add(new Condition("id.dst", new Object[]{dst}, null, Restriction.EQ));
                condition.add(new Condition("yyyymmdd", new Object[]{lpdate}, null, Restriction.EQ));
                condition.add(new Condition("id.channel", new Object[]{1}, null, Restriction.EQ));
                
                switch (MeterType.valueOf(parser.getMeter().getMeterType().getName())) {
                case EnergyMeter :
                    lplist = lpEMDao.getLpEMsByListCondition(condition); 
                    break;
                case WaterMeter :
                    lplist = lpWMDao.getLpWMsByListCondition(condition);
                    break;
                case GasMeter :
                    lplist = lpGMDao.getLpGMsByListCondition(condition);
                    break;
                case HeatMeter :
                    lplist = lpHMDao.getLpHMsByListCondition(condition);
                }
    
                // 값이 있는 것까지의 주기를 구하고 base를 계산한다.
                if (lplist != null && lplist.size() != 0) {
                    // 저장된 최근 시간을 가져온다.
                    for (int i = 0; i < lplist.size(); i++) {
                        mlp = (MeteringLP)lplist.get(i);
                        if (hh < Integer.parseInt(mlp.getHour()) ){
                            hh = Integer.parseInt(mlp.getHour());
                            maxIdx = i;
                        }
                    }
                    log.debug("MAX IDX[" + maxIdx + "]");
                    mlp = (MeteringLP)lplist.get(maxIdx);
                    // if (baseValue > mlp.getValue())
                    baseValue = mlp.getValue();
                    
                    /*
                    for (mm = 0; mm < 60; mm+=parser.getMeter().getLpInterval()) {
                        strLpValue = BeanUtils.getProperty(mlp, "value_" + (mm<10? "0":"")+mm);
                        if (strLpValue == null)
                            break;
                        else {
                            baseValue += Double.parseDouble(strLpValue);
                        }
                    }
                    */
                    break;
                }
                else {
                    baseValue = lpdata.getBasePulse()[0];
                    lpdate = DateTimeUtil.getPreDay(lpdate, 1).substring(0, 8);
                    log.info("PREDAY[" + lpdate + "]");
                }
            }
            if (mm == 60) {
                hh++;
                mm = 0;
            }
            
            log.debug("HH[" + hh + "] MM[" + mm + "]");
            
            // 같은 날짜에만 해당한다.
            if (lpdate.equals(lpdata.getLpDate())) {
                startIdx = hh * parser.getPeriod() + 
                            (mm / parser.getMeter().getLpInterval());
            }
            else {
                // if (hh == 24)
                // 날짜가 다르면 무조건 00시부터 2011.06.30
                hh = 0;
            }
            
            log.info("START IDX[" + startIdx + "]");
            if (startIdx > lpdata.getLp()[0].length) {
                log.info("LP length is lower than start index. so skip..");
                continue;
            }
            
            flaglist = new int[lpdata.getLp()[0].length-startIdx];
            
            // LP가 없으면 건너뛴다.
            if (flaglist.length == 0)
                continue;
            
            lp = new double[flaglist.length];
            
         // ModemLPData의 lp 배열구조가 변경됨. 주의
            for (int flagcnt=0; flagcnt < flaglist.length; flagcnt++) {
                lp[flagcnt] = lpdata.getLp()[0][startIdx+flagcnt];
                
                for (int ch = 0; ch < lpdata.getLp().length; ch++) {
                    if (lpdata.getLp()[ch][flagcnt] > 65535)
                        flaglist[flagcnt] = MeteringFlag.Fail.getFlag();
                    else
                        flaglist[flagcnt] = MeteringFlag.Correct.getFlag();
                }
            }
            
            saveLPData(MeteringType.Normal, lpdata.getLpDate(), (hh<10?"0":"")+ hh + (mm<10?"0":"")+mm,
                    new double[][]{lp}, flaglist, baseValue, parser.getMeter(),
                    parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
            
            // 모든 lp를 더해서 baseValue에 더하여 다음 LP 저장에 사용한다.
            for (double _lp : lp) {
                baseValue += _lp;
            }
            log.info("BASEVALUE[" + baseValue + "]");
        }

        // 설정 저장
        if (parser.getMeter().getModem() instanceof com.aimir.model.device.ZEUPLS) {
            com.aimir.model.device.ZEUPLS zeupls = (com.aimir.model.device.ZEUPLS)parser.getMeter().getModem();
            zeupls.setOperatingDay(parser.getOperatingDay());
            zeupls.setActiveTime(parser.getActiveMinute());
            zeupls.setAlarmFlag(parser.getAlarmFlag());
            zeupls.setBatteryVolt(parser.getBatteryVolt());
            zeupls.setVoltOffset(parser.getVoltOffset());
            zeupls.setFwRevision(parser.getFwBuild());
            zeupls.setFwVer(parser.getFwVersion());
            zeupls.setHwVer(parser.getHwVersion());
            zeupls.setLpChoice(parser.getLpChoice());
            zeupls.setLpPeriod(parser.getLpPeriod());
            zeupls.setNodeKind(CommonConstants.getModemNodeKind(parser.getNodeKind()).name());
            zeupls.setNetworkType(CommonConstants.getModemNetworkType(parser.getNetworkType()).name());
            zeupls.setRssi(parser.getRSSI());
            zeupls.setLQI((int)parser.getLQI());
        }
        else if (parser.getMeter().getModem() instanceof HMU) {
            HMU hmu = (HMU)parser.getMeter().getModem();
            hmu.setOperatingDay(parser.getOperatingDay());
            hmu.setActiveTime(parser.getActiveMinute());
            hmu.setAlarmFlag(parser.getAlarmFlag());
            hmu.setBatteryVolt(parser.getBatteryVolt());
            hmu.setVoltOffset(parser.getVoltOffset());
            hmu.setFwRevision(parser.getFwBuild());
            hmu.setFwVer(parser.getFwVersion());
            hmu.setHwVer(parser.getHwVersion());
            hmu.setLpChoice(parser.getLpChoice());
            hmu.setLpPeriod(parser.getLpPeriod());
            hmu.setNodeKind(CommonConstants.getModemNodeKind(parser.getNodeKind()).name());
            hmu.setNetworkType(CommonConstants.getModemNetworkType(parser.getNetworkType()).name());
            hmu.setRssi(parser.getRSSI());
            hmu.setLQI((int)parser.getLQI());
        }
        
        BatteryLog[] batteryLogs = new BatteryLog[1];
        batteryLogs[0] = new BatteryLog();
        batteryLogs[0].setYyyymmdd(parser.getCurrentTime().substring(0, 8));
        Object[][] values = {{parser.getCurrentTime().substring(8, 12),
            parser.getBatteryVolt(), 0.0, parser.getVoltOffset(), 0.0, 0.0, 0.0, 0L}};
        batteryLogs[0].setValues(values);
        saveBatteryLog(parser.getMeter().getModem(), parser.getBatteryLogs());
        
        return true;
    }

}
