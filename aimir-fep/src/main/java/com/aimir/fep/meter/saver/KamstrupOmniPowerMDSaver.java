package com.aimir.fep.meter.saver;

import java.text.ParseException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.MeteringFlag;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.command.conf.KamstrupCIDMeta;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.KamstrupOmniPower;
import com.aimir.fep.meter.parser.ModemLPData;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Service
public class KamstrupOmniPowerMDSaver extends AbstractMDSaver {

    @Override
    protected boolean save(IMeasurementData md) throws Exception {
        KamstrupOmniPower parser = (KamstrupOmniPower)md.getMeterDataParser();
        
        int interval = 60 / (parser.getPeriod() != 0? parser.getPeriod():1);
        if (parser.getMeter().getLpInterval() == null || 
                interval != parser.getMeter().getLpInterval())
            parser.getMeter().setLpInterval(interval);
        
        if(parser.getMeterTime() == null || "".equals(parser.getMeterTime())){
        	ModemLPData[] data = parser.getLpData();
        	if(data != null && data.length > 0 && data[0].getBasePulse() != null && data[0].getBasePulse().length > 0){
        		parser.setMeterTime((data[0].getLpDate()+"00").substring(0,14));
        	}
        }
        
        if(parser.getMeteringValue() == null){
        	ModemLPData[] data = parser.getLpData();
        	if(data != null && data.length > 0 && data[0].getBasePulse() != null && data[0].getBasePulse().length > 0){
        		parser.setMeteringValue(data[0].getBasePulse()[0]);//TODO 누적값을 알아야 한다. LP의 최종 누적값을 설정해준다.
        	}
        }
        
        // TODO 정기검침으로 설정했는데 후에 변경해야 함.
        saveMeteringData(MeteringType.Manual, parser.getMeteringTime().substring(0,8),
                parser.getMeteringTime().substring(8, 14), parser.getMeteringValue(),
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
        if(parser.getLpData() != null && parser.getLpData().length > 0){
            for (ModemLPData data : parser.getLpData()) {
        	// ModemLPData data = parser.getLpData()[parser.getLpData().length - 1];
				
                if (data == null || data.getLp() == null || data.getLp()[0].length == 0) {
                    log.warn("LP size is 0 then skip");
                    //continue;
                } else {
                
	                flaglist = new int[data.getLp()[0].length];
	                for (int flagcnt=0; flagcnt < flaglist.length; flagcnt++) {
	                    for (int ch = 0; ch < data.getLp().length; ch++) {
	                        if (data.getLp()[ch][flagcnt] == 65535) {
	                            flaglist[flagcnt] = MeteringFlag.Fail.getFlag();
	                            data.getLp()[ch][flagcnt] = 0;
	                        }
	                        else
	                            flaglist[flagcnt] = MeteringFlag.Correct.getFlag();
	                    }
	                }
	                
	                saveLPData(MeteringType.Normal, data.getLpDate().substring(0, 8), 
	                        data.getLpDate().substring(8, 12),
	                        data.getLp(), flaglist, data.getBasePulse(),
	                        parser.getMeter(),  parser.getDeviceType(), parser.getDeviceId(),
	                        parser.getMDevType(), parser.getMDevId());
                }
            }
        }
        
        Instrument[] instrument = parser.getInstrument();
        if(parser.getInstrument() != null && parser.getInstrument().length > 0){
            savePowerQuality(parser.getMeter(), parser.getMeteringTime(), instrument, parser.getDeviceType(),
                    parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
        }

        saveDailyBill(parser);
        // saveMeterTimeSyncLog(parser.getMeter(), parser.getMeteringTime(), parser.getMeterTime());
        
        return true;
    }

    private void saveDailyBill(KamstrupOmniPower parser) throws ParseException {
        BillingData bill = new BillingData();
        bill.setBillingTimestamp(parser.getMeterTime());
        bill.setActiveEnergyImportRateTotal(parser.getActiveEnergyA14());
        bill.setReactiveEnergyRateTotal(parser.getActiveEnergyR12()+parser.getActiveEnergyR34());
        saveDailyBilling(bill, parser.getMeter(), null, null, parser.getMDevType(), parser.getMDevId());
    }
    
    @Override
    public String relayValveOn(String mcuId, String meterId) {
        Object[] result = null;
        try {
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            byte[] res = commandGw.cmdKamstrupCID(mcuId, meterId, 
                        new String[] {KamstrupCIDMeta.CID.SetCutOffState.getCommand(),
                            KamstrupCIDMeta.CID.SetCutOffState.getArgs()[1][0] });
            if (res.length > 1) {
                result = KamstrupCIDMeta.getResult(res);
                
                if (result[0] != null && result[0].equals("Relays released for reconnection")) {
                    result = KamstrupCIDMeta.getResult(commandGw.cmdKamstrupCID(mcuId, meterId,
                            new String[] {KamstrupCIDMeta.CID.SetCutOffState.getCommand(),
                            KamstrupCIDMeta.CID.SetCutOffState.getArgs()[2][0] }));
                    
                    if (result[0] != null && result[0].equals("Relays connected")) {
                        Meter meter = new Meter();
                        meter.setMdsId(meterId);
                        updateMeterStatusNormal(meter);
                    }
                }
        	}
        	else {
            	result = new String[] {"SUCCESS : Send SMS Command(RelayOn)"};
            } 

        }
        catch (Exception e) {
            log.error(e, e);
            result = new String[]{e.getMessage()};
        }
        
        String str = MapToJSON((String[])result);
        JsonArray ja = StringToJsonArray(str).getAsJsonArray();
        JsonObject jo = null;
        for (int i = 0; i < ja.size(); i++) {
            jo = ja.get(i).getAsJsonObject();
            if (jo.get("value").getAsString().equals("Relays connected")) {
                // updateMeterStatusNormal(meter);
                ja.add(StringToJsonArray("{\"name\":\"Result\", \"value\":\"Success\"}"));
            }
        }
        
        return ja.toString();
    }

    private String[] getMeterModelProtocol(String meterId) {
        TransactionStatus txstatus = null;
        String[] meterModelProtocolType = new String[2];
        try {
            txstatus = txmanager.getTransaction(null);
            Meter meter = meterDao.get(meterId);
            meterModelProtocolType[0] = meter.getModel().getName();
            if (meter.getModem() != null && meter.getModem().getProtocolType() != null)
                meterModelProtocolType[1] = meter.getModem().getProtocolType().name();
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            if (txstatus != null) txmanager.rollback(txstatus);
        }
        return meterModelProtocolType;
    }
    
    @Override
    public String relayValveOff(String mcuId, String meterId) {
        Object[] result = null;
        try {
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            byte[] res = commandGw.cmdKamstrupCID(mcuId, meterId,
                        new String[] {KamstrupCIDMeta.CID.SetCutOffState.getCommand(),
                            KamstrupCIDMeta.CID.SetCutOffState.getArgs()[0][0] });
            if (res.length > 1) {
                result = KamstrupCIDMeta.getResult(res);
                
                if (result[0] != null && result[0].equals("Relays disconnected by command")) {
                    Meter meter = new Meter();
                    meter.setMdsId(meterId);
                    updateMeterStatusCutOff(meter);
                }
        	}
        	else {
            	result = new String[] {"SUCCESS : Send SMS Command(RelayOff)"};
            } 

        }
        catch (Exception e) {
            log.error(e, e);
            result = new String[]{e.getMessage()};
        }
        
        String str = MapToJSON((String[])result);
        JsonArray ja = StringToJsonArray(str).getAsJsonArray();
        JsonObject jo = null;
        for (int i = 0; i < ja.size(); i++) {
            jo = ja.get(i).getAsJsonObject();
            if (jo.get("value").getAsString().equals("Relays disconnected by command")) {
                // updateMeterStatusCutOff(meter);
                ja.add(StringToJsonArray("{\"name\":\"Result\", \"value\":\"Success\"}"));
            }
        }
        
        return ja.toString();
    }

    @Override
    public String relayValveStatus(String mcuId, String meterId) {
        Object[] result = null;
        
        try {
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            byte[] res = commandGw.cmdKamstrupCID(mcuId, meterId,
                    new String[] { KamstrupCIDMeta.CID.GetCutOffState.getCommand() });
            if (res.length > 1) {
                result = KamstrupCIDMeta.getResult(res);
        	} else {
            	result = new String[] {"SUCCESS : Send SMS Command(RelayStatus)"};
            } 

            for(Object o : result) {
                log.debug(o);
            }
            
            Meter meter = new Meter();
            meter.setMdsId(meterId);
            if (result[0] != null) {
                if (((String)result[0]).contains("Relays connected")) {
                    updateMeterStatusNormal(meter);
                }
                else if (((String)result[0]).contains("Disconnect relays") ||  ((String)result[0]).contains("Relays disconnected by command")) {
                    updateMeterStatusCutOff(meter);
                }
            }
        }
        catch (Exception e) {
            log.error(e, e);
            result = new String[]{e.getMessage()};
        }
        
        return MapToJSON((String[])result);
    }

    @Override
    public String syncTime(String mcuId, String meterId) {
        Object[] result = null;
        try {
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
        	byte[] res = commandGw.cmdKamstrupCID(mcuId, meterId,
                    new String[] {KamstrupCIDMeta.CID.SetClock.getCommand(),
                    KamstrupCIDMeta.CID.SetClock.getArgs()[0][0] });
        	
        	if (res.length > 3 && res[0] == 0x00 && res[1] == 0x00 && res[2] == 0x00) {
                result = KamstrupCIDMeta.getResult(res);
        	}
        	else {
            	result = new String[] {"SUCCESS : Send SMS Command(MeterTimeSync)"};
            } 

            // saveMeterTimeSyncLog(meter, (String)result[0], (String)result[1], 0);
        }
        catch (Exception e) {
            log.error(e, e);
            result = new String[]{e.getMessage()};
        }
        return MapToJSON((String[])result);
    }

    @Override
    public String relayValveActivate(String mcuId, String meterId) {
        Object[] result = null;
        try {
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            // relay released
            result = KamstrupCIDMeta.getResult(commandGw.cmdKamstrupCID(mcuId, meterId, 
                    new String[] {KamstrupCIDMeta.CID.SetCutOffState.getCommand(),
                        KamstrupCIDMeta.CID.SetCutOffState.getArgs()[1][0] }));
        }
        catch (Exception e) {
            log.error(e, e);
            result = new String[]{e.getMessage()};
        }
        return MapToJSON((String[])result);
    }
}
