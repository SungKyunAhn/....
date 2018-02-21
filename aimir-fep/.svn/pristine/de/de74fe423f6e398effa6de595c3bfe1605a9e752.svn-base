package com.aimir.fep.meter.saver;

import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeteringFlag;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.MeteringFail;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.ModemLPData;
import com.aimir.fep.meter.parser.WFD1;
import com.aimir.fep.util.EventUtil;

@Service
public class WFD1MDSaver extends AbstractMDSaver {

    @Override
    protected boolean save(IMeasurementData md) throws Exception
    {
        WFD1 parser = (WFD1)md.getMeterDataParser();
        int interval = 60 / (parser.getPeriod() != 0? parser.getPeriod():1);
        if (parser.getMeter().getLpInterval() == null || 
                interval != parser.getMeter().getLpInterval())
            parser.getMeter().setLpInterval(interval);
        
        String time = parser.getTimestamp();

        // TODO 정기검침으로 설정했는데 후에 변경해야 함.
        saveMeteringData(MeteringType.Normal, md.getTimeStamp().substring(0,8),
                md.getTimeStamp().substring(8, 14), parser.getMeteringValue(),
                parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
                parser.getMDevType(), parser.getMDevId(), parser.getMeterTime());
        
        /*
        double[] lp = new double[parser.getLpData().length * parser.getLpData()[0].getLp().length];
        
        int lpcnt = 0;
        for (ModemLPData data : parser.getLpData()) {
            for (int i = 0 ; i < data.getLp().length; i++) {
                lp[lpcnt++] = data.getLp()[i];
            }
        }
        */

        int[] flaglist = null;
        for (ModemLPData data : parser.getLpData()) {
            flaglist = new int[data.getLp()[0].length];
            for (int flagcnt=0; flagcnt < flaglist.length; flagcnt++) {
                for (int ch = 0; ch < data.getLp().length; ch++) {
                    if (data.getLp()[ch][flagcnt] >= 65535) {
                        flaglist[flagcnt] = MeteringFlag.Fail.getFlag();
                        data.getLp()[ch][flagcnt] = 0.0;
                    }
                    else {
                        flaglist[flagcnt] = MeteringFlag.Correct.getFlag();
                    }
                }
            }
            
            saveLPData(MeteringType.Normal, data.getLpDate(), "0000",
                    data.getLp(), flaglist, data.getBasePulse()[0],
                    parser.getMeter(),  parser.getDeviceType(), parser.getDeviceId(),
                    parser.getMDevType(), parser.getMDevId());
        }
        
        MeteringFail meteringFailed = parser.getMeteringFail();
        if(meteringFailed != null){
            if(meteringFailed.getModemErrCode() > 0){
                MeterStatus meterStatus = MeterStatus.Abnormal;
                EventUtil.sendEvent("Metering Value Incorrect",
                                    TargetClass.valueOf(parser.getMeter().getMeterType().getName()),
                                    parser.getMeter().getMdsId(),
                                    new String[][] {{"message",
                                                 "Read Fail Modem Error Code[" + meteringFailed.getModemErrCodeName()+"]"}
                                });
            }
            String meterLog = meteringFailed.getModemErrCodeName();
        }
        
        return true;
    }

}
