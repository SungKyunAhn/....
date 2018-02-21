package com.aimir.fep.meter.saver;

import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeteringFlag;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.MeteringFail;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.Aidon;
import com.aimir.fep.meter.parser.Aidon5530;
import com.aimir.fep.meter.parser.ModemLPData;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.Meter;

@Service
public class Aidon5530MDSaver extends AbstractMDSaver {

    @Override
    protected boolean save(IMeasurementData md) throws Exception
    {
        Aidon5530 parser = (Aidon5530)md.getMeterDataParser();
        int interval = 60 / (parser.getPeriod() != 0? parser.getPeriod():1);
        if (parser.getMeter().getLpInterval() == null || 
                interval != parser.getMeter().getLpInterval())
            parser.getMeter().setLpInterval(interval);
        
        String time = parser.getTimestamp();
        Aidon aidonMeta = parser.getAidonMeta();
        if(aidonMeta != null){
            EventLogData[] eventlogdata = aidonMeta.getEventLogData();
            Instrument[] instrument = aidonMeta.getInstrument();
            saveMeterEventLog(parser.getMeter(), eventlogdata);
            savePowerQuality(parser.getMeter(), time, instrument, parser.getDeviceType(),
                    parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
        }

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
            flaglist = new int[data.getLp().length];
         // ModemLPData의 lp 배열구조가 변경됨. 주의
            for (int ch = 0; ch < data.getLp().length; ch++) {
                for (int flagcnt=0; flagcnt < flaglist.length; flagcnt++) {
                    if (data.getLp()[ch][flagcnt] > 65535)
                        flaglist[flagcnt] = MeteringFlag.Fail.getFlag();
                    else
                        flaglist[flagcnt] = MeteringFlag.Correct.getFlag();
                }
            }
            
            saveLPData(MeteringType.Normal, data.getLpDate(), "0000",
                    data.getLp(), flaglist, data.getBasePulse()[0],
                    parser.getMeter(),  parser.getDeviceType(), parser.getDeviceId(),
                    parser.getMDevType(), parser.getMDevId());
        }
        
        MeterStatus meterStatus  = aidonMeta.getMeterStatus()? MeterStatus.Normal:MeterStatus.Abnormal;
        String meterLog = (String)aidonMeta.getData().get("Diagnosis");

        MeteringFail meteringFailed = parser.getMeteringFail();
        if(meteringFailed != null){
            if(meteringFailed.getModemErrCode() > 0){
                meterStatus = MeterStatus.Abnormal;
                EventUtil.sendEvent("Metering Value Incorrect",
                                    TargetClass.valueOf(parser.getMeter().getMeterType().getName()),
                                    parser.getMeter().getMdsId(),
                                    new String[][] {{"message",
                                                 "Read Fail Modem Error Code[" + meteringFailed.getModemErrCodeName()+"]"}
                                });
            }
            meterLog = meteringFailed.getModemErrCodeName();
        }
        
        return true;
    }

    @Override
    public String relayValveOn(String mcuId, String meterId) {
        String rtnStr = null;
        
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            rtnStr = commandGw.cmdAidonMCCB(mcuId, meter.getMdsId(), "Enable Use - Connect Now");
            
            if(!rtnStr.equals("frame is ok but message is not recognized") && 
                    !rtnStr.equals("MCCB Communication Fail") && 
                    !rtnStr.equals("Not Ready")){
                updateMeterStatusNormal(meter);
            }
        }
        catch (Exception e) {
            rtnStr = "failReason : " + e.getMessage();
        }
        
        return MapToJSON(new String[]{rtnStr});
    }

    @Override
    public String relayValveOff(String mcuId, String meterId) {
        String rtnStr = null;
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            rtnStr = commandGw.cmdAidonMCCB(mcuId, meter.getMdsId(), "Disable Use - Disconnect");
            
            if(!rtnStr.equals("frame is ok but message is not recognized") && 
                    !rtnStr.equals("MCCB Communication Fail") && 
                    !rtnStr.equals("Not Ready")){
                updateMeterStatusCutOff(meter);
            }
        }
        catch (Exception e) {
            rtnStr = "failReason : " + e.getMessage();
        }
        
        return MapToJSON(new String[]{rtnStr});
    }

    @Override
    public String relayValveStatus(String mcuId, String meterId) {
        String rtnStr = null;
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            rtnStr = commandGw.cmdAidonMCCB(mcuId, meter.getMdsId(), "Get Phase Status");                
            
            if(rtnStr.equals("Phases 1, 2 and 3 missing")){
                updateMeterStatusCutOff(meter);
            }
            else if (rtnStr.equals("All phases detected")) {
               updateMeterStatusNormal(meter);
            }
        }
        catch (Exception e) {
            rtnStr = "failReason : " + e.getMessage();
        }
        
        return MapToJSON(new String[]{rtnStr});
    }

    @Override
    public String relayValveActivate(String mcuId, String meterId) {
        String rtnStr = null;
        
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            rtnStr = commandGw.cmdAidonMCCB(mcuId, meter.getMdsId(), "Enable Use - Disconnected");
            
            if(!rtnStr.equals("frame is ok but message is not recognized") && 
                    !rtnStr.equals("MCCB Communication Fail") && 
                    !rtnStr.equals("Not Ready")){
                meter.setMeterStatus(CommonConstants.getMeterStatus(MeterStatus.Activation.name()));
                meterDao.update(meter);
            }
        }
        catch (Exception e) {
            rtnStr = "failReason : " + e.getMessage();
        }
        
        return MapToJSON(new String[]{rtnStr});
    }
    
    @Override
    public String relayValveDeactivate(String mcuId, String meterId) {
        String rtnStr = null;
        
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            rtnStr = commandGw.cmdAidonMCCB(mcuId, meter.getMdsId(), "Enable Use - Automatically");
            
            if(!rtnStr.equals("frame is ok but message is not recognized") && 
                    !rtnStr.equals("MCCB Communication Fail") && 
                    !rtnStr.equals("Not Ready")){
                meter.setMeterStatus(CommonConstants.getMeterStatus(MeterStatus.Deactivation.name()));
                meterDao.update(meter);
            }
        }
        catch (Exception e) {
            rtnStr = "failReason : " + e.getMessage();
        }
        
        return MapToJSON(new String[]{rtnStr});
    }
    
    @Override
    public String syncTime(String mcuId, String meterId) {
        Meter meter = meterDao.get(meterId);
        String[] result = null;
        
        try {
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            byte[] bx = commandGw.cmdMeterTimeSync(mcuId,meter.getMdsId());
            
            String beforeTime = String.format("%d%02d%02d%02d%02d%02d", 
                    DataUtil.getIntTo2Byte(new byte[]{bx[49], bx[48]}),
                    bx[50], bx[51], bx[52], bx[53], bx[54]);
            String afterTime = String.format("%d%02d%02d%02d%02d%02d",
                    DataUtil.getIntTo2Byte(new byte[]{bx[60], bx[59]}),
                    bx[61], bx[62], bx[63], bx[64], bx[65]);
            saveMeterTimeSyncLog(meter, beforeTime, afterTime, 1);
            
            result = new String[]{beforeTime, afterTime};
        }
        catch (Exception e) {
            result = new String[]{e.getMessage()};
        }
        
        return MapToJSON(result);
    }
}
