package com.aimir.fep.meter.saver;

import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.PG200;
import com.aimir.model.device.MMIU;

@Service
public class PG200MDSaver extends AbstractMDSaver {
	
    private double[][] makeLp(int lpPeriod, int pressurePeriod,
            List<Double> lplist, List<Double>pressurelist) {
        double[][] lp = null;
        
        if (lpPeriod < pressurePeriod) {
            int jump = pressurePeriod / lpPeriod;
            lp = new double[2][24 * pressurePeriod];
            
            for (int j = 0, k = 0; j < lplist.size(); j++, k+=jump) {
                lp[0][k] = lplist.get(j);
            }
            for (int j = 0; j < pressurelist.size(); j++) {
                lp[1][j] = lplist.get(j);
            }
        }
        else {
            int jump = lpPeriod / pressurePeriod;
            lp = new double[2][24 * lpPeriod];
            
            for (int j = 0; j < lplist.size(); j++) {
                lp[0][j] = lplist.get(j);
            }
            for (int j = 0, k = 0; j < pressurelist.size(); j++, k+=jump) {
                lp[1][k] = lplist.get(j);
            }
        }
        
        return lp;
    }
    
    private int[] makeFlag(int length) {
        int[] flags = new int[length];
        for (int i = 0; i < length; i++) {
            flags[i] = 0;
        }
        
        return flags;
    }
    
    private void saveMeteringData(PG200 pg200) throws Exception {
     // period를 가져와서 주기를 검사한다.
        int interval = 60 / pg200.getPeriod();
        
        if (interval != pg200.getMeter().getLpInterval())
            pg200.getMeter().setLpInterval(interval);
        
        if (pg200.getCurrentTime() != null) {
            // 올라온 검침데이타의 검침값을 저장한다.
            saveMeteringData(MeteringType.Normal, pg200.getCurrentTime().substring(0, 8),
                    pg200.getCurrentTime().substring(8), pg200.getMeteringValue(),
                    pg200.getMeter(), pg200.getDeviceType(), pg200.getDeviceId(),
                    pg200.getMDevType(), pg200.getMDevId(), pg200.getMeterTime());
        }
        
        // LP 데이타 저장
        double[][] lp = null;
        int[] flaglist = null;
        
        String lpdate = null;
        double baseValue = 0.0;
        for (Iterator<String> i = pg200.listLp().keySet().iterator(); i.hasNext();) {
            lpdate = i.next();
            baseValue = pg200.listBasePulse().get(lpdate);
            lp = makeLp(pg200.getPeriod(), pg200.getPressurePeriod(), 
                    pg200.listLp().get(lpdate), pg200.listPressure().get(lpdate));
            
            if (lp.length == 0 || lp[0].length == 0) {
                log.warn("Date[" + lpdate + "] LP is null");
                continue;
            }
            
            flaglist = makeFlag(lp[0].length);
            saveLPData(MeteringType.Normal, lpdate, "0000", lp, flaglist, baseValue, pg200.getMeter(),
                    pg200.getDeviceType(), pg200.getDeviceId(), pg200.getMDevType(), pg200.getMDevId());
        }
        
        MMIU mmiu = (MMIU)pg200.getMeter().getModem();
        Integer rfPowerVal = pg200.getCsq();
        mmiu.setRfPower(Long.valueOf(rfPowerVal.longValue()));
        // mmiu.setRfPower(pg200.getCsq());
    }
    
    private void saveEventLog(PG200 pg200) throws Exception {
        EventLogData e = null;
        int eventCode = 0;
        List<String> eventtimelist = null;
        for (Iterator<Integer> i = pg200.listEventLog().keySet().iterator(); i.hasNext();) {
            eventCode = i.next();
            eventtimelist = pg200.listEventLog().get(eventCode);
            for (int j = 0; j < eventtimelist.size(); j++) {
                e = new EventLogData();
                e.setDate(eventtimelist.get(j).substring(0, 8));
                e.setTime(eventtimelist.get(j).substring(8));
                e.setFlag(eventCode);
                
                saveMeterEventLog(pg200.getMeter(), new EventLogData[]{e});
            }
        }
    }
    
    @Override
    protected boolean save(IMeasurementData md) throws Exception 
    {
        PG200 parser = (PG200)md.getMeterDataParser();
        
        switch (parser.getFrameType()) {
        case MeteringDataFrame :
            saveMeteringData(parser);
            break;
        case EventFrame :
            saveEventLog(parser);
            break;
        }
        
        return true;
    }

}
