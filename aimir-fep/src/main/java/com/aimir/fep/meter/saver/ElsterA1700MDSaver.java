package com.aimir.fep.meter.saver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.ElectricityChannel;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.adapter.AdapterInterface;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.PowerAlarmLogData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.ElsterA1700;
import com.aimir.model.mvm.LpEM;
import com.aimir.model.system.MeterConfig;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;

/**
 * 
 * @author choiEJ
 *
 */
@Service
public class ElsterA1700MDSaver extends AbstractMDSaver {

    public boolean save(String meteringTime, ElsterA1700 parser) throws Exception {
        Instrument[] instrument = parser.getInstrument();
        log.debug("instrument start..");
        if (instrument != null) {
            savePowerQuality(parser.getMeter(), parser.getMeterTime(), instrument, parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
        }
        log.debug("instrument Success..");
        
        // 미터링 데이터 저장
        log.debug("미터링 start..");
        if (parser.getMeteringValue() != null) {
            saveMeteringData(MeteringType.Normal, meteringTime.substring(0,8),
                    meteringTime.substring(8, 14), parser.getMeteringValue(),
                    parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
                    parser.getMDevType(), parser.getMDevId(), parser.getMeterTime());
        }
        log.debug("미터링 Success..");
        
   
        // 현재 시점 검침 사용량 및 Demand Power
        log.debug("BillingData start..");
        BillingData billingData = parser.getBillingData();
        if(billingData != null){ 
            saveMonthlyBilling(billingData, parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
        }
        log.debug("BillingData Success..");
        
        //Current Billing Data
        log.debug("Current BillingData start..");
        BillingData cb = parser.getCurrentBillingData();
        
        //Current Billing Time값을 설정하기 위하여 미터시간 가져옴.
        String meterTime = parser.getMeterTime();        
        cb.setBillingTimestamp(meterTime);
        
        if(cb != null){
            saveCurrentBilling(cb, parser.getMeter(),
                    parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
        }
        log.debug("Current BillingData Success..");
        
        // Meter Event Log 저장
        log.debug("Meter Event Log start..");
        List<EventLogData> meterEventLogList = parser.getMeterEventLog();
            if (meterEventLogList != null) {
              EventLogData[] meterEventLog = new EventLogData[meterEventLogList.size()];
              for (int i = 0; i < meterEventLogList.size(); i++) {
                  meterEventLog[i] = meterEventLogList.get(i);
              }
              saveMeterEventLog(parser.getMeter(), meterEventLog);
        }
        log.debug("Meter Event Log Success..");
       
        LPData[] lpList = parser.getLpData();
 
     /*   log.debug("getLPData start");
        String meterTimeOfMD     = md.getTimeStamp();
        log.debug("meterTimeOfMD "+meterTimeOfMD);
        String meterTimeOfParser = parser.getMeterTime();
        log.debug("meterTimeOfParser "+meterTimeOfParser);
        long diffTime    = 0;
        long systemTime  = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTimeOfMD).getTime();
        log.debug("systemTime "+systemTime);
        long limitTime   = Long.parseLong(FMPProperty.getProperty("metertime.diff.limit.forcertain")) * 1000;
        log.debug("limitTime "+limitTime);
        boolean isCorrectTime = true;
        if (meterTimeOfParser != null) {
            diffTime = systemTime - DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTimeOfParser).getTime();
            log.debug("diffTime "+diffTime);
            if (diffTime < 0) {
                diffTime *= -1;
            }
            
            if (limitTime < diffTime) {
                isCorrectTime = false;
            }
        }*/
        log.debug("getLPData Success..");

        // 순시값 (Voltage,Current) 데이터 저장
        // lp 값 저장
        log.debug("lp saver start");
        int lpPeriod = 0;
        try{
            lpPeriod = parser.getResolution() != 0 ? parser.getResolution() : 15;
        }catch(Exception e){
            e.printStackTrace();
        }
        
        if (lpPeriod != parser.getMeter().getLpInterval()) {
            log.debug("SET LP_PERIOD=[" + lpPeriod + "]");
            parser.getMeter().setLpInterval(lpPeriod);
        }
        log.debug("LP_PERIOD=[" + lpPeriod + "]");
        
        if (lpList == null || lpList.length == 0) {
            log.debug("LPSIZE=[0]");
        } else {
            log.debug("LPSIZE=[" + lpList.length + "]");
            
            String mdevId = parser.getMDevId();
            log.debug("mdevId=[" + mdevId + "]");
            
//            double basePulse = lpList[0].getBasePulse();

            String yyyymmdd = lpList[0].getDatetime().substring(0, 8);
            String hhmm     = lpList[0].getDatetime().substring(8, 12);
            int hh = new Integer(lpList[0].getDatetime().substring(8,10));
            int mm = new Integer(lpList[0].getDatetime().substring(10,12));
            log.debug("YYYYMDD=[" + yyyymmdd + "] HH=[" + hh + "] MM=[" + mm + "]");
            
//            double basePulse = lpList[0].getBasePulse();
            
            // double basePulse = retunBaseVal(lpList[0].getDatetime(),lpPeriod, parser);
            double basePulse = parser.getMeteringValue();
            log.debug("basePulse = "+basePulse);
            
            double[][] lpValues = new double[lpList[0].getCh().length][lpList.length];
            int[] flaglist = new int[lpList.length];
            
            for (int ch = 0; ch < lpValues.length; ch++) {
                for (int lpcnt = 0; lpcnt < lpValues[ch].length; lpcnt++) {
                    lpValues[ch][lpcnt] = lpList[lpcnt].getCh()[ch];
//                    log.debug("lpList[lpcnt].getDatetime() "+lpList[lpcnt].getDatetime()+" "+"lpValues["+ch+"]["+lpcnt+"] "+lpValues[ch][lpcnt]);
                    if (ch == 0) basePulse -= lpValues[ch][lpcnt];
                }
            }
            
            for (int i = 0; i < flaglist.length; i++) {
                flaglist[i] = lpList[i].getFlag();
            }
            saveLPData(MeteringType.Normal, yyyymmdd, hhmm, lpValues, flaglist, basePulse,
                    parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(), 
                    parser.getMDevType(), parser.getMDevId());
        }
        log.debug("lp saver Success..");
        // lp에서 올라오는 power alarm log (marker) 저장
        log.debug(" power alarm log from lp start..");
        List<PowerAlarmLogData> lpPowerAlarmLogList = parser.getLpPowerEventLog();
        if (lpPowerAlarmLogList != null) {
            PowerAlarmLogData[] lpPowerAlarmLog = new PowerAlarmLogData[lpPowerAlarmLogList.size()];
            for (int i = 0; i < lpPowerAlarmLogList.size(); i++) {
                lpPowerAlarmLog[i] = lpPowerAlarmLogList.get(i);
            }
            saveMeterEventLog(parser.getMeter(), lpPowerAlarmLog);
        }
        log.debug(" power alarm log from lp Success..");
        
        // Power Event Log 저장
        log.debug("Power Event Log start..");
        List<PowerAlarmLogData> powerAlarmLogList = parser.getPowerEventLog();
        log.debug("Power Event Log  1");
        if (powerAlarmLogList != null) {
            PowerAlarmLogData[] powerEventLog = new PowerAlarmLogData[powerAlarmLogList.size()];
            log.debug("powerAlarmLogList.size() "+powerAlarmLogList.size());
            for (int i = 0; i < powerAlarmLogList.size(); i++) {
                powerEventLog[i] = powerAlarmLogList.get(i);
            }
            if(powerEventLog.length >0 ){
                savePowerAlarmLog(parser.getMeter(), powerEventLog);
            }
        }
        log.debug("Power Event Log Success..");

        
        // lp에서 올라오는 meter event log (marker, status) 저장
       log.debug(" meter event log from lp start..");
        List<EventLogData> lpMeterEventLogList = parser.getLpMeterEventLog();
        if (lpMeterEventLogList != null) {
            EventLogData[] lpMeterEventLog = new EventLogData[lpMeterEventLogList.size()];
            for (int i = 0; i < lpMeterEventLogList.size(); i++) {
                lpMeterEventLog[i] = lpMeterEventLogList.get(i);
            }
            saveMeterEventLog(parser.getMeter(), lpMeterEventLog);
        }
        log.debug(" meter event log from lp Success..");
        
        log.debug(" Ampere consumption threshold START ");
        MeterConfig mc = (MeterConfig)parser.getMeter().getModel().getDeviceConfig();
        String adapterClassName = mc.getAdapterClassName();
        
        if (adapterClassName != null) {
            AdapterInterface ai = (AdapterInterface)Class.forName(adapterClassName).newInstance();
            ai.execute(parser);
        }
        log.debug(" Ampere consumption threshold END ");

        log.debug("End!");
        return true;
    }
    
	@Override
	protected boolean save(IMeasurementData md) throws Exception {
    	log.debug("ElsterA1700MDSaver start2");
        ElsterA1700 parser = (ElsterA1700)md.getMeterDataParser();
   	    // 모뎀 정보 저장
       /* HashMap<String, String> modemData = parser.getModemData();
        log.debug("modemData "+modemData);
        if (modemData != null) {
        	log.debug("(String) modemData.get(protocolType) "+ (String) modemData.get("protocolType"));
        	parser.getMeter().getModem().setProtocolType((String) modemData.get("protocolType"));
        	log.debug("modemData.get(fwVersion) "+ modemData.get("fwVersion"));
        	parser.getMeter().getModem().setFwVer(modemData.get("fwVersion"));
        	log.debug("modemData.get(fwBuild) "+ modemData.get("fwBuild"));
        	parser.getMeter().getModem().setFwRevision(modemData.get("fwBuild"));
        	log.debug("modemData.get(hwVersion) "+ modemData.get("hwVersion"));
        	parser.getMeter().getModem().setHwVer(modemData.get("hwVersion"));
        	log.debug("modemData.get(simNumber) "+ (String) modemData.get("simNumber"));
        	((MMIU)(parser.getMeter().getModem())).setPhoneNumber((String) modemData.get("simNumber"));
        	log.debug("modemData.get(rssi) "+ (String) modemData.get("rssi"));
        	((MMIU)(parser.getMeter().getModem())).setRfPower(Integer.parseInt(((String) modemData.get("rssi"))));
        }
        log.debug("aaa 222");*/
        
        return save(md.getTimeStamp(), parser);
    }
    
    private double retunBaseVal(String dateTime, int period, ElsterA1700 parser){
    	LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
		String yyyymmddhh = dateTime.substring(0, 10);
		String mm = String.valueOf(period);//validlplist[0].getDatetime().substring(10, 12);
		double basePulse = -1;
		
    	try {
    		
    	    condition.add(new Condition("id.yyyymmddhh",new Object[] { yyyymmddhh }, null, Restriction.EQ));
            condition.add(new Condition("id.channel",new Object[] { ElectricityChannel.Usage.getChannel() },
                    null, Restriction.EQ));
            condition.add(new Condition("id.dst", new Object[] { DateTimeUtil.inDST(null, dateTime) }, null,Restriction.EQ));
            // condition.add(new Condition("id.mdevType", new Object[] { parser.getMDevType() }, null, Restriction.EQ));
            condition.add(new Condition("id.mdevId", new Object[] { parser.getMDevId() }, null, Restriction.EQ));

			List<LpEM> lpEM = lpEMDao.findByConditions(condition);
			
			try {
				if (lpEM != null && !lpEM.isEmpty()) {	
					basePulse = lpEM.get(0).getValue()+retValue(mm,lpEM.get(0).getValue_00(),lpEM.get(0).getValue_15(),lpEM.get(0).getValue_30(),lpEM.get(0).getValue_45());
					
				}else{
				    LinkedHashSet<Condition> condition2 = new LinkedHashSet<Condition>();
                    
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHH");
                    cal.setTime(dateFormatter.parse(yyyymmddhh));
                    
                    cal.add(cal.HOUR, -1);

                    condition2.add(new Condition("id.yyyymmddhh",new Object[] { dateFormatter.format(cal.getTime()) }, null, Restriction.EQ));
                    condition2.add(new Condition("id.channel",  new Object[] { ElectricityChannel.Usage.getChannel() }, null, Restriction.EQ));
                    condition2.add(new Condition("id.dst",  new Object[] { DateTimeUtil.inDST(null, dateTime) }, null, Restriction.EQ));
                    // condition2.add(new Condition("id.mdevType",  new Object[] { parser.getMDevType() }, null,Restriction.EQ));
                    condition2.add(new Condition("id.mdevId",new Object[] { parser.getMDevId() }, null, Restriction.EQ));
                    
					List<LpEM> subLpEM = lpEMDao.findByConditions(condition2);
					if (subLpEM != null && !subLpEM.isEmpty()) {	
							basePulse = subLpEM.get(0).getValue()+retValue(mm,subLpEM.get(0).getValue_00(),subLpEM.get(0).getValue_15(),subLpEM.get(0).getValue_30(),subLpEM.get(0).getValue_45());
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
        }catch(Exception e){
        	e.printStackTrace();
        }
    
    	return basePulse;
    }
    
	private Double retValue(String mm,Double value_00,Double value_15,Double value_30,Double value_45){
		
		Double retVal_00 = value_00 == null ? 0d:value_00;
		Double retVal_15 = value_15 == null ? 0d:value_15;
		Double retVal_30 = value_30 == null ? 0d:value_30;
		Double retVal_45 = value_45 == null ? 0d:value_45;
		if("15".equals(mm)){
			return retVal_00;
		}
		
		if("30".equals(mm)){
			return retVal_00+retVal_15;
		}
		
		if("45".equals(mm)){
			return retVal_00+retVal_15+retVal_30;
		}
		
		if("00".equals(mm)){
			return retVal_00+retVal_15+retVal_30+retVal_45;
		}
		
	    return retVal_00+retVal_15;
		
	}
	
}
