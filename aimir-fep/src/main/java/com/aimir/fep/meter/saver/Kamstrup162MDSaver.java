package com.aimir.fep.meter.saver;

import java.text.ParseException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.MeteringFlag;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.command.conf.KamstrupCIDMeta;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.Kamstrup;
import com.aimir.fep.meter.parser.Kamstrup162;
import com.aimir.fep.meter.parser.ModemLPData;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.util.DateTimeUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Service
public class Kamstrup162MDSaver extends AbstractMDSaver {

    @Override
    protected boolean save(IMeasurementData md) throws Exception {
        Kamstrup162 parser = (Kamstrup162)md.getMeterDataParser();
        
        int interval = 60 / (parser.getPeriod() != 0? parser.getPeriod():1);
        if (parser.getMeter().getLpInterval() == null || 
                interval != parser.getMeter().getLpInterval())
            parser.getMeter().setLpInterval(interval);
        
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
        if(parser.getLpData() != null  && parser.getLpData().length > 0) {
//        for (ModemLPData data : parser.getLpData()) {
        	ModemLPData data = parser.getLpData()[parser.getLpData().length - 1];
            if (data == null || data.getLp() == null || data.getLp()[0].length == 0) {
                log.warn("LP size is 0 then skip");
//                continue;
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
	            
	            saveLPData(MeteringType.Manual, data.getLpDate().substring(0, 8), 
	                    data.getLpDate().substring(8, 12),
	                    data.getLp(), flaglist, data.getBasePulse(),
	                    parser.getMeter(),  parser.getDeviceType(), parser.getDeviceId(),
	                    parser.getMDevType(), parser.getMDevId());
	            }
//        }
        }
        
        Kamstrup kamstrupMeta = parser.getKamstrupMeta();
        Instrument[] instrument = kamstrupMeta.getInstrument();
        
        savePowerQuality(parser.getMeter(), parser.getMeteringTime(), instrument, parser.getDeviceType(),
                parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
        savePreBill(kamstrupMeta);
        saveRealTimeBill(kamstrupMeta);
        
        return true;
    }

    private void savePreBill(Kamstrup parser) throws ParseException {
        Map<String, String[]> billMap = parser.getValue();
        
        BillingData bill = new BillingData();
        
        String[] date = billMap.get("10.0.0. Date");
        String preMonth = null;
        if (date != null && date.length > 0) {
            String billDay = date[0].substring(0, 6) + "01";
            bill.setBillingTimestamp(billDay);
            preMonth = DateTimeUtil.getPreDay(billDay).substring(0,6);
            
            int i = 0;
            date = billMap.get("A2.8." + i + ". RTC");
            if (date != null && date[0].substring(0,8).equals(billDay))  {
                bill.setActiveEnergyRate1(Double.parseDouble(billMap.get("A2.8." + i + ". Active energy A14 Tariff 1")[0]));
                bill.setActiveEnergyRate2(Double.parseDouble(billMap.get("A2.8." + i + ". Active energy A14 Tariff 2")[0]));
                bill.setActiveEnergyRate3(Double.parseDouble(billMap.get("A2.8." + i + ". Active energy A14 Tariff 3")[0]));
                bill.setActiveEnergyRate4(Double.parseDouble(billMap.get("A2.8." + i + ". Active energy A14 Tariff 4")[0]));
                bill.setActiveEnergyRateTotal(Double.parseDouble(billMap.get("A2.8." + i + ". Active energy A14")[0]));
                
                bill.setActiveEnergyImportRate1(bill.getActiveEnergyRate1());
                bill.setActiveEnergyImportRate2(bill.getActiveEnergyRate2());
                bill.setActiveEnergyImportRate3(bill.getActiveEnergyRate3());
                bill.setActiveEnergyImportRate4(bill.getActiveEnergyRate4());
                bill.setActiveEnergyImportRateTotal(bill.getActiveEnergyRateTotal());
            }
            
            date = billMap.get("10.0." + i + ". Max power P14 RTC");
            if (date != null && date[0].substring(0,6).equals(preMonth)) {
                bill.setActivePowerMaxDemandRateTotal(Double.parseDouble(billMap.get("10.0." + i + ". Max power P14 Tariff 1")[0]));
                bill.setActivePowerMaxDemandRate1(Double.parseDouble(billMap.get("10.0." + i + ". Max power P14 Tariff 1")[0]));
                // bill.setCummActivePwrDmdMaxExportRateTotal(Double.parseDouble(billMap.get(i + ". Accumulated max power P14")[0]));
                bill.setActivePowerDemandMaxTimeRateTotal(date[0].substring(0,12));
                bill.setActivePowerDemandMaxTimeRate1(bill.getActivePowerDemandMaxTimeRateTotal());
                
                bill.setMaxDmdkVah1RateTotal(bill.getActivePowerMaxDemandRateTotal());
                bill.setMaxDmdkVah1TimeRateTotal(bill.getActivePowerDemandMaxTimeRateTotal());
                bill.setMaxDmdkVah1Rate1(bill.getActivePowerMaxDemandRateTotal());
                bill.setMaxDmdkVah1TimeRate1(bill.getActivePowerDemandMaxTimeRateTotal());
            }
            
            saveMonthlyBilling(bill, parser.getMeter(), null, null, parser.getMDevType(), parser.getMDevId());
        }
    }
    
    private void saveRealTimeBill(Kamstrup parser) throws ParseException {
        Map<String, String[]> billMap = parser.getValue();
        
        BillingData rtBill = new BillingData();
        
        String[] date = billMap.get("10.0.0. RTC");
        if (date != null && date.length > 0) {
            rtBill.setBillingTimestamp(date[0]);
            
            int i = 0;
            String[] v = billMap.get("10.0." + i + ". Active energy A14 Tariff 1");
            if (v != null)
                rtBill.setActiveEnergyRate1(Double.parseDouble(v[0]));
            else return;
            
            v = billMap.get("10.0." + i + ". Active energy A14 Tariff 2");
            if (v != null)
                rtBill.setActiveEnergyRate2(Double.parseDouble(v[0]));
            else return;
            
            v = billMap.get("10.0." + i + ". Active energy A14 Tariff 3");
            if (v != null)
                rtBill.setActiveEnergyRate3(Double.parseDouble(v[0]));
            else return;
            
            v = billMap.get("10.0." + i + ". Active energy A14 Tariff 4");
            if (v != null)
                rtBill.setActiveEnergyRate4(Double.parseDouble(v[0]));
            else return;
            
            v = billMap.get("10.0." + i + ". Active energy A14");
            if (v != null)
                rtBill.setActiveEnergyRateTotal(Double.parseDouble(v[0]));
            else return;
            
            rtBill.setActiveEnergyImportRate1(rtBill.getActiveEnergyRate1());
            rtBill.setActiveEnergyImportRate2(rtBill.getActiveEnergyRate2());
            rtBill.setActiveEnergyImportRate3(rtBill.getActiveEnergyRate3());
            rtBill.setActiveEnergyImportRate4(rtBill.getActiveEnergyRate4());
            rtBill.setActiveEnergyImportRateTotal(rtBill.getActiveEnergyRateTotal());
            
            date = billMap.get("10.0." + i + ". Max power P14 RTC");
            if (date != null)
                rtBill.setActivePowerDemandMaxTimeRateTotal(date[0].substring(0,12));
            else return;
            
            v = billMap.get("10.0." + i + ". Max power P14 Tariff 1");
            if (v != null)
                rtBill.setActivePowerMaxDemandRateTotal(Double.parseDouble(v[0]));
            else return;
            
            rtBill.setActivePowerMaxDemandRate1(rtBill.getActivePowerMaxDemandRateTotal());
            // rtBill.setCummActivePwrDmdMaxExportRateTotal(Double.parseDouble(billMap.get(i + ". Accumulated max power P14")[0]));
            
            rtBill.setMaxDmdkVah1RateTotal(rtBill.getActivePowerMaxDemandRateTotal());
            rtBill.setMaxDmdkVah1Rate1(rtBill.getActivePowerMaxDemandRateTotal());
            rtBill.setMaxDmdkVah1TimeRateTotal(rtBill.getActivePowerDemandMaxTimeRateTotal());
            rtBill.setMaxDmdkVah1TimeRate1(rtBill.getActivePowerDemandMaxTimeRateTotal());
            
            log.debug(rtBill.toString());
            saveCurrentBilling(rtBill, parser.getMeter(), null, null, parser.getMDevType(), parser.getMDevId());
        }
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
    public String relayValveOn(String mcuId, String meterId) {
        Object[] result = null;
        try {
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            // relay released
            result = KamstrupCIDMeta.getResult(commandGw.cmdKamstrupCID(mcuId, meterId, 
                    new String[] {KamstrupCIDMeta.CID.SetCutOffState.getCommand(),
                        KamstrupCIDMeta.CID.SetCutOffState.getArgs()[1][0] }));
            
            if (result[0] != null && result[0].equals("Relays released for reconnection")) {
                result = KamstrupCIDMeta.getResult(commandGw.cmdKamstrupCID(mcuId, meterId,
                        new String[] {KamstrupCIDMeta.CID.SetCutOffState.getCommand(),
                        KamstrupCIDMeta.CID.SetCutOffState.getArgs()[2][0] }));
            }
        }
        catch (Exception e) {
            result = new String[]{e.getMessage()};
        }
        
        Meter meter = new Meter();
        meter.setMdsId(meterId);
        String str = MapToJSON((String[])result);
        JsonArray ja = StringToJsonArray(str).getAsJsonArray();
        JsonObject jo = null;
        for (int i = 0; i < ja.size(); i++) {
            jo = ja.get(i).getAsJsonObject();
            if (jo.get("value").getAsString().equals("Relays connected")) {
                updateMeterStatusNormal(meter);
                ja.add(StringToJsonArray("{\"name\":\"Result\", \"value\":\"Success\"}"));
            }
        }
        
        return ja.toString();
    }

    @Override
    public String relayValveOff(String mcuId, String meterId) {
        Object[] result = null;
        try {
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            result = KamstrupCIDMeta.getResult(commandGw.cmdKamstrupCID(mcuId, meterId,
                    new String[] {KamstrupCIDMeta.CID.SetCutOffState.getCommand(),
                        KamstrupCIDMeta.CID.SetCutOffState.getArgs()[0][0] }));
        }
        catch (Exception e) {
            result = new String[]{e.getMessage()};
        }
        
        Meter meter = new Meter();
        meter.setMdsId(meterId);
        String str = MapToJSON((String[])result);
        JsonArray ja = StringToJsonArray(str).getAsJsonArray();
        JsonObject jo = null;
        for (int i = 0; i < ja.size(); i++) {
            jo = ja.get(i).getAsJsonObject();
            if (jo.get("value").getAsString().equals("Relays disconnected by command")) {
                updateMeterStatusCutOff(meter);
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
            
            result = KamstrupCIDMeta.getResult(commandGw.cmdKamstrupCID(mcuId, meterId,
                            new String[] { KamstrupCIDMeta.CID.GetCutOffState.getCommand() }));
            
            for(Object o : result) {
                log.debug(o);
            }
            
            Meter meter = new Meter();
            meter.setMdsId(meterId);
            if (result[0] != null) {
                if (((String)result[0]).contains("Relays connected")) {
                    updateMeterStatusNormal(meter);
                }
                else if (((String)result[0]).contains("Disconnect relays") || ((String)result[0]).contains("Relays disconnected by command")) {
                    updateMeterStatusCutOff(meter);
                }
            }
        }
        catch (Exception e) {
            result = new String[]{e.getMessage()};
        }
        
        return MapToJSON((String[])result);
    }

    @Override
    public String syncTime(String mcuId, String meterId) {
        Object[] result = null;
        
        try {
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            result = KamstrupCIDMeta.getResult(commandGw.cmdKamstrupCID(mcuId, meterId,
                                        new String[] {KamstrupCIDMeta.CID.SetClock.getCommand(),
                                        KamstrupCIDMeta.CID.SetClock.getArgs()[0][0] }));
            
            // saveMeterTimeSyncLog(meter, (String)result[0], (String)result[1], 0);
        }
        catch (Exception e) {
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
            result = new String[]{e.getMessage()};
        }
        return MapToJSON((String[])result);
    }
}
