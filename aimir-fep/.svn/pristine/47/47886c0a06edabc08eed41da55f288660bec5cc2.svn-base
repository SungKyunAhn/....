package com.aimir.fep.meter.saver;

import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.Mk6N;
import com.aimir.fep.util.FMPProperty;
import com.aimir.util.DateTimeUtil;

@Service
public class Mk6NMDSaver extends AbstractMDSaver {

    @Override
    protected boolean save(IMeasurementData md) throws Exception {
    	
    	Mk6N parser = (Mk6N)md.getMeterDataParser();    	
        
        long diffTime = 0;
        String meterTime = md.getTimeStamp();
        long systemTime = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime();
        long limitTime = Long.parseLong(FMPProperty.getProperty("metertime.diff.limit.forcertain")) * 1000;
        boolean isCorrectTime = true;

        if(parser.getMeterTime() != null){
            diffTime = systemTime - DateTimeUtil.getDateFromYYYYMMDDHHMMSS((parser).getMeterTime()).getTime();
            if (diffTime < 0) {
                diffTime *= -1;
            }
            if (limitTime < diffTime) {
                isCorrectTime = false;
            }
            parser.getMeter().setTimeDiff(diffTime);
        }

        //Meter Status(Warning, Error)정보를 이벤트 화 하여 미터 이벤트 로그로 저장
        String meterStatusLog = parser.getMeterLog();
        if( meterStatusLog != null && !"".equals(meterStatusLog)){
        	parser.getMeter().setMeterCaution(meterStatusLog);
        }
        
        // 미터링 데이터 저장
        if (parser.getMeteringValue() != null) {
            saveMeteringData(MeteringType.Normal, md.getTimeStamp().substring(0,8),
                            md.getTimeStamp().substring(8, 14), parser.getMeteringValue(),
                            parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
                            parser.getMDevType(), parser.getMDevId(), parser.getMeterTime());
        }
        
        Instrument[] instrument = parser.getInstrument();
        if (instrument != null) {
            savePowerQuality(parser.getMeter(), parser.getMeterTime(), instrument, parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
        }
       
        // Meter Event Log 저장
        EventLogData[] meterEventLog = parser.getEventLog();

        if (meterEventLog != null) {
          	  saveMeterEventLog(parser.getMeter(), meterEventLog);
        }

        
        // 현재 시점 검침 사용량 및 Demand Power
        TOU_BLOCK[] tou_curr = parser.getCurrBilling();
        if(tou_curr != null){
        	saveCurrentBilling(tou_curr, parser.getMeter(),
            		parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
        }

        // 월별 TOU DATA저장
        TOU_BLOCK[] tou_month = parser.getPrevBilling();
        if(tou_month != null){
            saveMonthlyBilling(tou_month, parser.getMeter(),
            		parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
        }


        LPData[] lplist = parser.getLPData();
        
        if(lplist == null){
            log.debug("LPSIZE => 0");
        }
        else
        {
            String yyyymmdd = lplist[0].getDatetime().substring(0,8);

            String hhmm = lplist[0].getDatetime().substring(8,12);
            
            //여기서 계산할수 없기때문에 0으로 설정
            double basePulse = 0;//parser.getLpValue();
            
            
            double[][] lpValues = new double[lplist[0].getCh().length][lplist.length];
            int[] flaglist = new int[lplist.length];
            
            for (int ch = 0; ch < lpValues.length; ch++) {
                for (int lpcnt = 0; lpcnt < lpValues[ch].length; lpcnt++) {
                    lpValues[ch][lpcnt] = lplist[lpcnt].getCh()[ch];
                }
            }
            
            for (int i = 0; i < flaglist.length; i++) {
                flaglist[i] = lplist[i].getFlag();
            }
            // TODO Flag, PF 처리해야 함.
            saveLPData(MeteringType.Normal, yyyymmdd, hhmm, lpValues, flaglist, basePulse,
                    parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
                    parser.getMDevType(), parser.getMDevId());
        }
        
        /*
        PowerQualityMonitor pqm = parser.getPowerQality();
        if(pqm != null){
            savePowerQualtiyStatus(parser.getMeter(), parser.getMeterTime(), pqm, 
            		parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
        }
        */

    	return true;
    }
}
