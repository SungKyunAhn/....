package com.aimir.fep.meter.saver;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeteringFlag;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.HMUParser;
import com.aimir.fep.meter.parser.ModemLPData;
import com.aimir.model.device.HMU;
import com.aimir.model.mvm.MeteringLP;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;

@Service
public class HMUMDSaver extends AbstractMDSaver {
	
    @Override
    protected boolean save(IMeasurementData md) throws Exception 
    {
        HMUParser parser = (HMUParser)md.getMeterDataParser();
        
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
                
                lplist = lpEMDao.getLpEMsByListCondition(condition); 
    
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
                    baseValue = mlp.getValue();
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
        
        return true;
    }

}
