package com.aimir.fep.meter.saver;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.ElectricityChannel;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.device.MMIUDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.command.mbean.CommandGW.OnDemandOption;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.meter.data.PowerAlarmLogData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.DLMSKaifa;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_CLASS;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.MBUS_DEVICE_TYPE;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.METER_EVENT_LOG;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.OBIS;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.UNIT;
import com.aimir.fep.protocol.smsp.SMSConstants;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.fep.util.threshold.CheckThreshold;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.GasMeter;
import com.aimir.model.device.HeatMeter;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.WaterMeter;
import com.aimir.model.mvm.LpEM;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Supplier;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import java.text.DecimalFormat;
import java.text.ParseException;	// INSERT 2016.11.08 SP-303

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;


@Service
public class DLMSKaifaMDSaver extends AbstractMDSaver {
	private static Log log = LogFactory.getLog(DLMSKaifaMDSaver.class);

	@Autowired
	MMIUDao mmiuDao;	

	@Override
	protected boolean save(IMeasurementData md) throws Exception {
		DLMSKaifa parser = (DLMSKaifa) md.getMeterDataParser();

        //Save meter information
        saveMeterInfomation(parser);
        
        // UPDATE START SP-501
		//saveLpKaifa(md, parser);
        saveLpKaifaUsingLPTime(md, parser);
        // UPDATE END SP-501
		saveMeteringDataWithChannel(md,parser);
	
		saveEventLog(parser);

        savePowerQualityKaifa(parser);

        saveMbusDataKaifa(md, parser);
		
		// INSERT START SP-225
        String strMeterTime = parser.getMeterTime();
		if (strMeterTime != null) {
			long meterTime = Util.getMilliTimes(strMeterTime)/1000;
			String strModemTime = parser.getMeteringTime();
			if (strModemTime == null) {
				strModemTime = md.getTimeStamp().substring(0,14);
			}
			long modemTime = Util.getMilliTimes(strModemTime)/1000;
			long gap = 0; 
	
			log.debug("Meter[" + strMeterTime + "]->[" + meterTime + "] Modem[" + strModemTime + "]->[" + modemTime + "]");

			// INSERT START SP-406
            try {
    			long diff = modemTime - meterTime;
    			Meter meter = parser.getMeter();
    			if (meter.getTimeDiff() == null) {
    				log.debug("timeDiff is null. Update timeDiff. dest[" + diff + "]");
    				meter.setTimeDiff(diff);
    			}
    			else if (meter.getTimeDiff() != diff) {
    				log.debug("timeDiff is different. Update timeDiff. source[" + meter.getTimeDiff() + "] dest[" + diff + "]");
    				meter.setTimeDiff(diff);
    			}
    			else {
    				log.debug("timeDiff is same. Do not update here.");    				
    			}
            }
            catch (Exception e) {
                log.warn("Update timdDiff failed.");
            }
			// INSERT END SP-406
			
			if (meterTime < modemTime){
				gap = modemTime - meterTime; 
			} else {
				gap = meterTime - modemTime; 
			}
	        if (CheckThreshold.isOverMeterTimeGap((int)gap)) {
	        	Meter meter = parser.getMeter();
	        	CheckThreshold.addMeterTimeGapWarning(meter.getId(), (int)gap);
	        }
		}
		// INSERT START SP-406
		else {
			log.debug("meterTime is null. Can not check MeterTimeGap.");
		}
		// INSERT END SP-406
		// INSERT END SP-225
		
		return true;
	}
	
	private void saveMeteringDataKaifa(MeteringType meteringType, String meteringDate,
            String meteringTime, double meteringValue, Meter meter, DeviceType deviceType,
            String deviceId, DeviceType mdevType, String mdevId, String meterTime) throws Exception {
		try {
			saveMeteringData(MeteringType.Normal, meteringDate,
					meteringTime, meteringValue, meter, deviceType, 
					deviceId, mdevType, mdevId, meterTime);
		}finally {
			log.debug("saveMeteringDataKaifa finish");
		}
	}
	
	private void saveLpKaifa(IMeasurementData md, DLMSKaifa parser) throws Exception {
		try {
			LPData[] lplist = parser.getLPData();
			if (lplist == null || lplist.length == 0) {
			    log.warn("LP size is 0!!");
			    return;
			}
			
			 log.debug("active pulse constant:" +
			 parser.getActivePulseConstant());
			// log.debug("currentDemand:" + currentDemand);
			 
			double lpSum = 0;
			double basePulse = 0;
			double current = parser.getMeteringValue() == null ? -1d : parser.getMeteringValue();
			
			double addBasePulse = 0;
			
			if (lplist != null && lplist.length > 0) {
				log.debug("lplist[0]:"+lplist[0]);
				log.debug("lplist[0].getDatetime():"+lplist[0].getDatetime());
				String inityyyymmddhh =lplist[0].getDatetime().substring(0, 10);
				
				SimpleDateFormat dateFormatter = new SimpleDateFormat(
				"yyyyMMddHH");
		
		        Calendar cal = Calendar.getInstance();
		
		        cal.setTime(dateFormatter.parse(inityyyymmddhh));
		        cal.add(cal.HOUR, 1);
		
		        inityyyymmddhh = dateFormatter.format(cal.getTime());
		        
		        
		        log.debug(md.getTimeStamp().substring(0,8));
		        log.debug(md.getTimeStamp().substring(8,14));
		        log.debug(parser.getMeteringValue());
		        log.debug(parser.getMeter().getMdsId());
		        log.debug(parser.getDeviceType());
		        log.debug(parser.getDeviceId());
		        log.debug(parser.getMDevType());
		        log.debug(parser.getMDevId());
		        log.debug(parser.getMeterTime());
		        
		        //Save meter information
		        //saveMeterInfomation(parser); 
		        // UPDATE  START SP-280
//		        saveMeteringDataKaifa(MeteringType.Normal, md.getTimeStamp().substring(0,8),
//		                md.getTimeStamp().substring(8, 14), parser.getMeteringValue() == null ? 0d : parser.getMeteringValue(),
//		                parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
//		                parser.getMDevType(), parser.getMDevId(), parser.getMeterTime());			
				Double meteringValue =  parser.getMeteringValue() == null ? 0d : parser.getMeteringValue();
				Meter meter = parser.getMeter();
		        String dsttime = DateTimeUtil.getDST(null, md.getTimeStamp());
		        String meterTime =  parser.getMeterTime();
		        log.debug("MDevId[" + parser.getMDevId() + "] DSTTime["+dsttime+"]");
		        // if (meter.getLastReadDate() == null || dsttime.substring(0, 10).compareTo(meter.getLastReadDate().substring(0, 10)) >= 0) {
		        if (meterTime != null && !"".equals(meterTime))
		            meter.setLastReadDate(meterTime);
		        else
		            meter.setLastReadDate(dsttime);
		        meter.setLastMeteringValue(meteringValue);
		        //=> INSERT START 2016.12.08 SP-303
		        log.debug("MDevId[" + parser.getMDevId() + "] DSTTime["+dsttime+"] LASTREADDate[" + meter.getLastReadDate()+"]");
		        Code normalStatus = CommonConstants.getMeterStatusByName(MeterStatus.Normal.name());
		        log.debug("MDevId[" + parser.getMDevId() + "] METER_STATUS[" + (meter.getMeterStatus() == null ? "NULL" : meter.getMeterStatus()) + "]");
		        if (meter.getMeterStatus() == null || 
		                (meter.getMeterStatus() != null && 
		                !meter.getMeterStatus().getName().equals("CutOff") && 
		                !meter.getMeterStatus().getName().equals("Delete"))){
		            meter.setMeterStatus(normalStatus);
		            log.debug("MDevId[" + parser.getMDevId() + "] METER_CHANGED_STATUS[" + meter.getMeterStatus() + "]");
		        }
		        if (meterTime != null && !"".equals(meterTime)) {
		            try {
		                long diff = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(md.getTimeStamp()).getTime() - 
		                        DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime();
		                meter.setTimeDiff(diff / 1000);
	    				log.debug("MDevId[" + parser.getMDevId() + "] Update timeDiff. diff=[" + meter.getTimeDiff() + "]"); // INSERT SP-406
		            }
		            catch (ParseException e) {
		                log.warn("MDevId[" + parser.getMDevId() + "] Check MeterTime[" + meterTime + "] and MeteringTime[" + md.getTimeStamp() + "]");
		            }
		        }
		        //=> INSERT END 2016.12.08   SP-303

				//// UPDATE END   SP-280
				for (int i = 0; i < lplist.length; i++) {
					
					lpSum += lplist[i].getLpValue();
					
					String yyyymmddhh = lplist[i].getDatetime().substring(0, 10);
					if(inityyyymmddhh.equals(yyyymmddhh)){
						addBasePulse+= lplist[i].getLpValue();
					}
					 log.debug("MDevId[" + parser.getMDevId() + "]  time=" + lplist[i].getDatetime() + ":lp="
					 + lplist[i].getLp() + ":lpValue=" + lplist[i].getLpValue()+":addBasePulse="+addBasePulse);
		
				}
			}

			if ( current >= 0d ){
				basePulse = current - lpSum;
			}else{
				basePulse = lpSum;
			}
			 log.debug("MDevId[" + parser.getMDevId() + "] lpSum:"+lpSum);
			 log.debug("MDevId[" + parser.getMDevId() + "] MDevId[" + parser.getMDevId() + "] basePulse:"+basePulse);
			if (lplist == null || lplist.length == 0) {
				log.debug("MDevId[" + parser.getMDevId() + "]  LPSIZE => 0");
			} else {
				 log.debug("##########MDevId[" + parser.getMDevId() + "] LPSIZE => "+lplist.length);
				lpSave(md, lplist, parser, basePulse, addBasePulse);
			}
		} catch (Exception e) {
			log.error(e,e);
		}finally {
			log.debug("MDevId[" + parser.getMDevId() + "] saveLpKaifa finish");
		}
	}

	// INSERT START SP-501
	private void saveLpKaifaUsingLPTime(IMeasurementData md, DLMSKaifa parser) throws Exception {
		try {
			LPData[] lplist = parser.getLPData();
			if (lplist == null || lplist.length == 0) {
				log.warn("LP size is 0!!");
				return;
			}
			log.debug("saveLpKaifaUsingLPTime Start Total LPSIZE => " + lplist.length);
			
			log.debug("active pulse constant:" + parser.getActivePulseConstant());
	        log.debug(md.getTimeStamp().substring(0,8));
	        log.debug(md.getTimeStamp().substring(8,14));
	        log.debug(parser.getMeteringValue());
	        log.debug(parser.getMeter().getMdsId());
	        log.debug(parser.getDeviceType());
	        log.debug(parser.getDeviceId());
	        log.debug(parser.getMDevType());
	        log.debug(parser.getMDevId());
	        log.debug(parser.getMeterTime());
	        
	        //Save meter information
			Double meteringValue =  parser.getMeteringValue() == null ? 0d : parser.getMeteringValue();
			Meter meter = parser.getMeter();
	        String dsttime = DateTimeUtil.getDST(null, md.getTimeStamp());
	        String meterTime =  parser.getMeterTime();
	        log.debug("MDevId[" + parser.getMDevId() + "] DSTTime["+dsttime+"]");

	        if (meterTime != null && !"".equals(meterTime))
	            meter.setLastReadDate(meterTime);
	        else
	            meter.setLastReadDate(dsttime);
	        meter.setLastMeteringValue(meteringValue);

	        log.debug("MDevId[" + parser.getMDevId() + "] DSTTime["+dsttime+"] LASTREADDate[" + meter.getLastReadDate()+"]");
	        Code normalStatus = CommonConstants.getMeterStatusByName(MeterStatus.Normal.name());
	        log.debug("MDevId[" + parser.getMDevId() + "] METER_STATUS[" + (meter.getMeterStatus() == null ? "NULL" : meter.getMeterStatus()) + "]");
	        if (meter.getMeterStatus() == null || 
	                (meter.getMeterStatus() != null && 
	                !meter.getMeterStatus().getName().equals("CutOff") && 
	                !meter.getMeterStatus().getName().equals("Delete"))){
	            meter.setMeterStatus(normalStatus);
	            log.debug("MDevId[" + parser.getMDevId() + "] METER_CHANGED_STATUS[" + meter.getMeterStatus() + "]");
	        }
	        if (meterTime != null && !"".equals(meterTime)) {
	            try {
	                long diff = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(md.getTimeStamp()).getTime() - 
	                        DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime();
	                meter.setTimeDiff(diff / 1000);
    				log.debug("MDevId[" + parser.getMDevId() + "] Update timeDiff. diff=[" + meter.getTimeDiff() + "]"); // INSERT SP-406
	            }
	            catch (ParseException e) {
	                log.warn("MDevId[" + parser.getMDevId() + "] Check MeterTime[" + meterTime + "] and MeteringTime[" + md.getTimeStamp() + "]");
	            }
	        }     
	        
	        lpSaveUsingLPTime(md, lplist, parser);
	        
		} catch (Exception e) {
			log.error(e,e);
		}finally {
			log.debug("MDevId[" + parser.getMDevId() + "] saveLpKaifa finish");
		}
	}
	
	private boolean lpSaveUsingLPTime(IMeasurementData md, LPData[] validlplist,
			DLMSKaifa parser) throws Exception {
        
		log.info("#########save mdevId:"+parser.getMDevId());
		double[][] lpValues = new double[validlplist[0].getCh().length][validlplist.length];
		int[] flaglist = new int[validlplist.length];
		double[] pflist = new double[validlplist.length];
		String[] timelist = new String[validlplist.length];

		for (int ch = 0; ch < lpValues.length; ch++) {
			for (int lpcnt = 0; lpcnt < lpValues[ch].length; lpcnt++) {
				// lpValues[ch][lpcnt] = validlplist[lpcnt].getLpValue();
				// Kw/h 단위로 저장
				lpValues[ch][lpcnt] = validlplist[lpcnt].getCh()[ch];
			}
		}

		for (int i = 0; i < flaglist.length; i++) {
			flaglist[i] = validlplist[i].getFlag();
			pflist[i] = validlplist[i].getPF();
			timelist[i] = validlplist[i].getDatetime();
		}

        double[] _baseValue = new double[lpValues.length];
        _baseValue[0] = validlplist[0].getLpValue();;
        for (int i = 1; i < lpValues.length; i++) {
            _baseValue[i] = 0;
        }		
		
		parser.getMeter().setLpInterval(parser.getLpInterval());
		// TODO Flag, PF 처리해야 함.
		saveLPDataUsingLPTime(MeteringType.Normal, timelist, lpValues, flaglist,
				_baseValue, parser.getMeter(), parser.getDeviceType(),
				parser.getDeviceId(), parser.getMDevType(), parser
						.getMDevId());
		return true;
	}	
	// INSERT END SP-501	
	
	private void saveEventLog(DLMSKaifa parser) {
	    try {
    	    Map<String, Object> pf = parser.getEventLog();
    	    String key = null;
    	    List<EventLogData> events = new ArrayList<EventLogData>();
    	    List<PowerAlarmLogData> powerDowns = new ArrayList<PowerAlarmLogData>();
    	    List<PowerAlarmLogData> powerUps = new ArrayList<PowerAlarmLogData>();
    	    for (Iterator<String> i = pf.keySet().iterator(); i.hasNext(); ) {
    	        key = i.next();
    	        for (METER_EVENT_LOG el : METER_EVENT_LOG.values()) {
    	        		
        	        if (key.contains(el.name())) {
        	            log.debug("MDevId[" + parser.getMDevId() + "] " + el.name() + "[key=" + key + ", value=" + pf.get(key)+"]");
        	            EventLogData e = new EventLogData();
        	            e.setDate(((String)pf.get(key)).substring(0, 8)); 
        	            e.setTime(((String)pf.get(key)).substring(8, 14));
        	            e.setFlag(el.getFlag());
        	            e.setKind("STE");
        	            e.setMsg(el.getMsg());
        	            e.setAppend(el.getMsg());
        	            events.add(e);
        	            log.debug("MDevId[" + parser.getMDevId() + "] Event date[" + e.getDate() + "] msg[" + e.getMsg() + "]");
        	            //Before the conquest identifies only events.
        	            if (key.contains(METER_EVENT_LOG.Poweroff.name())) {
        	                PowerAlarmLogData p = new PowerAlarmLogData();
        	                p.setDate(e.getDate());
        	                p.setTime(e.getTime());
        	                p.setFlag(e.getFlag());
        	                p.setKind(e.getKind());
        	                p.setMsg(e.getMsg());
        	                
        	                powerDowns.add(p);
        	            }
        	            else if (key.contains(METER_EVENT_LOG.PowerOn.name())) {
        	                PowerAlarmLogData p = new PowerAlarmLogData();
        	                p.setCloseDate(e.getDate());
        	                p.setCloseTime(e.getTime());
        	                p.setFlag(e.getFlag());
        	                p.setKind(e.getKind());
        	                p.setMsg(e.getMsg());
        	                
        	                powerUps.add(p);
        	            }
        	            break;
        	        }
    	        }
    	    }
    	    
    	    saveMeterEventLog(parser.getMeter(), events.toArray(new EventLogData[0]));

    	    // Down time is small in comparison with the Power Down Up Down Time Set the time of the DateTime Up.
    	    // Apply the most recent on top.
    	    PowerAlarmLogData powerdown = null;
    	    PowerAlarmLogData powerup = null;
    	    for (int i = powerUps.size()-1; i >= 0; i--) {
    	        powerup = powerUps.get(i);
    	        for (int j = powerDowns.size()-1; j >= 0; j--) {
    	            powerdown = powerDowns.get(j);
    	            if (powerdown.getCloseDate() == null || "".equals(powerdown.getCloseDate())) {
    	                if ((powerdown.getDate()+powerdown.getTime()).compareTo(powerup.getCloseDate()+powerup.getCloseTime()) <= 0) {
    	                    powerdown.setCloseDate(powerup.getCloseDate());
    	                    powerdown.setCloseTime(powerup.getCloseTime());
    	                    powerdown.setKind(powerup.getKind());
    	                    powerdown.setFlag(powerup.getFlag());
    	                    powerdown.setMsg(powerup.getMsg());
    	                    break;
    	                }
    	            }
    	        }
    	    }
    	    
    	    savePowerAlarmLog(parser.getMeter(), powerDowns.toArray(new PowerAlarmLogData[0]));
	    }
	    catch (Exception e) { // 미터 이벤트 로그 생성 중 에러는 경고로 끝냄.
	        log.warn(e, e);
	    }
	}

	private void savePowerQualityKaifa(DLMSKaifa parser) {
		try {
	        savePowerQuality(parser.getMeter(), parser.getMeteringTime(), parser.getPowerQuality(), parser.getDeviceType(),
	                parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
		} catch (Exception e){
			 log.error(e,e);
		}finally {
			log.debug("MDevId[" + parser.getMDevId() + "] savePowerQualityKaifa finish");
		}
	}

	/*
	private void savePreBill(DLMSNamjun parser) {
	    Double value = 0.0;
        
        // 빌링 정보 저장
        Map<String, Object> prebill = parser.getPreviousMaxDemand();
        BillingData bill = new BillingData();
        
        // 순방향 유효전력량 [Q1+Q4]
        value = ((Long)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T1PreviousActive.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActiveEnergyRate1(value);
        
        value = ((Long)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T2PreviousActive.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActiveEnergyRate2(value);
        
        value = ((Long)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T3PreviousActive.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActiveEnergyRate3(value);
        // 지상 무효전력량 [Q1]
        value = ((Long)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T1PreviousReActive.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();
        bill.setReactiveEnergyRate1(value);
        
        value = ((Long)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T2PreviousReActive.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();
        bill.setReactiveEnergyRate2(value);
        
        value = ((Long)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T3PreviousReActive.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();
        bill.setReactiveEnergyRate3(value);
        
        // 순방향 최대 유효전력 [Q1 + Q4]
        value = ((Long)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T1PreviousActiveMax.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActivePowerMaxDemandRate1(value);
        
        value = ((Long)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T2PreviousActiveMax.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActivePowerMaxDemandRate2(value);
        
        value = ((Long)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T3PreviousActiveMax.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActivePowerMaxDemandRate3(value);
        
        // 누적 순방향 유효전력 [Q1 + Q4]
        value = ((Long)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T1PreviousActiveSum.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setCumulativeActivePowerDemandRate1(value);
        
        value = ((Long)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T2PreviousActiveSum.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setCumulativeActivePowerDemandRate2(value);
        
        value = ((Long)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T3PreviousActiveSum.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setCumulativeActivePowerDemandRate3(value);

        // 순방향 최대 유효 발생 일자/시간
        bill.setActivePowerDemandMaxTimeRate1(((String)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T1PreviousActiveMaxDate.name())).substring(6));
        bill.setActivePowerDemandMaxTimeRate2(((String)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T2PreviousActiveMaxDate.name())).substring(6));
        bill.setActivePowerDemandMaxTimeRate3(((String)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T3PreviousActiveMaxDate.name())).substring(6));
        
        bill.setActiveEnergyRateTotal(bill.getActiveEnergyRate1()+bill.getActiveEnergyRate2()+bill.getActiveEnergyRate3());
        bill.setReactiveEnergyRateTotal(bill.getReactiveEnergyRate1()+bill.getReactiveEnergyRate2()+bill.getReactiveEnergyRate3());
        bill.setActivePowerMaxDemandRateTotal(bill.getActivePowerMaxDemandRate1()+bill.getActivePowerMaxDemandRate2()+bill.getActivePowerMaxDemandRate3());
        bill.setCumulativeActivePowerDemandRateTotal(bill.getCumulativeActivePowerDemandRate1()+bill.getCumulativeActivePowerDemandRate2()+bill.getCumulativeActivePowerDemandRate3());
        
        bill.setBillingTimestamp(parser.getMeterTime().substring(0,6)+"01");
        
        saveMonthlyBilling(bill, parser.getMeter(), null, null, parser.getMDevType(), parser.getMDevId());
	}
	
	private void saveCurrBill(DLMSNamjun parser) {
        Double value = 0.0;
        
        // 빌링 정보 저장
        Map<String, Object> currbill = parser.getCurrentMaxDemand();
        BillingData bill = new BillingData();
        // 순방향 유효전력량 [Q1+Q4]
        value = ((Long)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T1CurrentActive.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActiveEnergyRate1(value);
        
        value = ((Long)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T2CurrentActive.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActiveEnergyRate2(value);
        
        value = ((Long)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T3CurrentActive.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActiveEnergyRate3(value);
        // 지상 무효전력량 [Q1]
        value = ((Long)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T1CurrentReActive.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();
        bill.setReactiveEnergyRate1(value);
        
        value = ((Long)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T2CurrentReActive.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();
        bill.setReactiveEnergyRate2(value);
        
        value = ((Long)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T3CurrentReActive.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();
        bill.setReactiveEnergyRate3(value);
        
        // 순방향 최대 유효전력 [Q1 + Q4]
        value = ((Long)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T1CurrentActiveMax.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActivePowerMaxDemandRate1(value);
        
        value = ((Long)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T2CurrentActiveMax.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActivePowerMaxDemandRate2(value);
        
        value = ((Long)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T3CurrentActiveMax.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActivePowerMaxDemandRate3(value);
        
        // 누적 순방향 유효전력 [Q1 + Q4]
        value = ((Long)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T1CurrentActiveSum.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setCumulativeActivePowerDemandRate1(value);
        
        value = ((Long)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T2CurrentActiveSum.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setCumulativeActivePowerDemandRate2(value);
        
        value = ((Long)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T3CurrentActiveSum.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setCumulativeActivePowerDemandRate3(value);

        // 순방향 최대 유효 발생 일자/시간
        bill.setActivePowerDemandMaxTimeRate1(((String)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T1CurrentActiveMaxDate.name())).substring(6));
        bill.setActivePowerDemandMaxTimeRate2(((String)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T2CurrentActiveMaxDate.name())).substring(6));
        bill.setActivePowerDemandMaxTimeRate3(((String)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T3CurrentActiveMaxDate.name())).substring(6));
        
        bill.setActiveEnergyRateTotal(bill.getActiveEnergyRate1()+bill.getActiveEnergyRate2()+bill.getActiveEnergyRate3());
        bill.setReactiveEnergyRateTotal(bill.getReactiveEnergyRate1()+bill.getReactiveEnergyRate2()+bill.getReactiveEnergyRate3());
        bill.setActivePowerMaxDemandRateTotal(bill.getActivePowerMaxDemandRate1()+bill.getActivePowerMaxDemandRate2()+bill.getActivePowerMaxDemandRate3());
        bill.setCumulativeActivePowerDemandRateTotal(bill.getCumulativeActivePowerDemandRate1()+bill.getCumulativeActivePowerDemandRate2()+bill.getCumulativeActivePowerDemandRate3());
        
        bill.setBillingTimestamp(parser.getMeterTime());
        saveCurrentBilling(bill, parser.getMeter(), null, null, parser.getMDevType(), parser.getMDevId());
    }
    */

	private Map<String, Object> getLpMap(LPData[] lplist, double basePulse) {
		Map<String, Object> lpMap = new HashMap<String, Object>();
		double basePulseSum = 0;
		for (int i = 0; i < lplist.length; i++) {
			String lpTime = lplist[i].getDatetime();
			String yyyymmdd = lpTime.substring(0, 8);

			basePulseSum += lplist[i].getLpValue();
			if (lpMap.get(yyyymmdd) != null) {
				Vector<LPData> lpVec = (Vector<LPData>) lpMap.get(yyyymmdd);
				lpVec.add(lplist[i]);
			} else {
				Vector<LPData> lpVec = new Vector<LPData>();
				lpVec.add(lplist[i]);
				lpMap.put(yyyymmdd, lpVec);

				if (i == 0) {
					lpMap.put(yyyymmdd + "_basePulse", basePulse);
				} else {
					lpMap
							.put(yyyymmdd + "_basePulse", basePulse
									+ basePulseSum);
				}

			}

		}
		return lpMap;
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
	
	private void saveMeterInfomation(DLMSKaifa parser) {
		try {				// INSERT SP-486		
			Long ct_ratio = parser.getCtRatio();
			Long vt_ratio = parser.getVtRatio();
			String fwVer = parser.getFwVersion();
			String meterModel = parser.getMeterModel();
			Integer lpInterval = parser.getLpInterval();
			Double threshold = parser.getLimitInfo();
			Double threshold_min = parser.getLimiterInfoMin();
			//=> UPDATE START 2016.10.11 SP-282
			//eMeter = (EnergyMeter)parser.getMeter();
			EnergyMeter eMeter = (EnergyMeter)parser.getMeter();

			if ( eMeter == null )
			{
			    log.debug("MDevId[" + parser.getMDevId() + "] MeterDataParser is null.");
				return;
			}
			//=> UPDATE END  2016.10.11 SP-282
			boolean updateflg = false;
			Integer swSta = 0;
	
			if  (parser.getRelayStat() != null) {
				swSta = parser.getRelayStat().getCode();
			}
			
			// set Ct Ratio
			if ( ct_ratio != 0L ){
				if ( (eMeter.getCt() == null && ct_ratio != null )
						|| ((eMeter.getCt() != null && ct_ratio != null) && (eMeter.getCt() != ct_ratio.doubleValue()) )) {
					eMeter.setCt(ct_ratio.doubleValue());
					log.debug("MDevId[" + parser.getMDevId() + "] set ct_ratio[" + ct_ratio.doubleValue() + "]");
					updateflg = true;
				}
			}
			
			// set Vt Ratio
			if ( vt_ratio != 0L ){
				if ( (eMeter.getVt() == null && vt_ratio != null )
						|| ((eMeter.getVt() != null && vt_ratio != null) && (eMeter.getVt() != vt_ratio.doubleValue()) )) {
					eMeter.setVt(vt_ratio.doubleValue());
					log.debug("MDevId[" + parser.getMDevId() + "] set vt_ratio[" + vt_ratio.doubleValue() + "]");
					updateflg = true;
				}
			}
			
			// FW Version 
			if ( fwVer.length() != 0 ) {
				if (eMeter.getSwVersion() == null ||
						(eMeter.getSwVersion() != null && !eMeter.getSwVersion().equals(fwVer))) {
					eMeter.setSwVersion(fwVer);
					log.debug("MDevId[" + parser.getMDevId() + "] set Swversion[" + fwVer + "]");
					updateflg = true;
				}
			}
			
			// Meter Model 
			if ( meterModel != null && meterModel.length() != 0 && !eMeter.getModel().getName().equals(meterModel)) {
				List<DeviceModel> list = deviceModelDao.getDeviceModelByName(eMeter.getSupplierId(),meterModel );
				if (list != null && list.size() == 1) {
	                eMeter.setModel(list.get(0));
					log.debug("MDevId[" + parser.getMDevId() + "] set Model[" + meterModel + "]");
	                updateflg = true;
	            }
				//Electric Meter
				/*
				Code code = codeDao.getCodeIdByCodeObject("1.3.1.1");
		        if (list != null && list.size() >= 1 ){
		        	for ( int i = 0; i < list.size(); i++ ){
		        		DeviceModel model = list.get(i);
		        		if ( model.getDeviceTypeCodeId() == code.getId() ) {
		        			if ( eMeter.getModelId() != null && eMeter.getModelId() != model.getId() ) { 
		        				eMeter.setModel(model);
		        				updateflg = true;
		        				break;
		        			}
		        		}
		        	}
		        }
		        */
			}
			
			
			// SwitchStatus
			if ((eMeter.getSwitchActivateStatus() != null && swSta != null) && eMeter.getSwitchActivateStatus() != swSta) {
				eMeter.setSwitchActivateStatus(swSta);
				log.debug("MDevId[" + parser.getMDevId() + "] set SwitchActivateStatus[" + swSta + "]");
				updateflg = true;
			}
			
			// LpInterval
			if ((eMeter.getLpInterval() != null && lpInterval != null) && eMeter.getLpInterval() != lpInterval) {
				eMeter.setLpInterval(lpInterval);
				log.debug("MDevId[" + parser.getMDevId() + "] set setLpInterval[" + lpInterval + "]");
				updateflg = true;
			}
			
			// Threshold
			if ((eMeter.getUsageThreshold() != null && threshold != null) && eMeter.getUsageThreshold() != threshold) { 
				eMeter.setUsageThreshold(threshold);
				log.debug("MDevId[" + parser.getMDevId() + "] set threshold[" + threshold + "]");
				updateflg = true;
			}
			
			// Min Threshold
			if ((eMeter.getPurchasePrice() != null && threshold_min != null) && eMeter.getPurchasePrice() != threshold_min) { 
				eMeter.setPurchasePrice(threshold_min);
				log.debug("MDevId[" + parser.getMDevId() + "] set threshold_min[" + threshold_min + "]");
				updateflg = true;
			}
			/*
			if ( updateflg ) {
				meterDao.update(eMeter);
				// meterDao.flush();
				log.debug("MDevId[" + parser.getMDevId() + "] update meter information");
			}
			*/
		// INSERT START SP-486
		} catch (Exception e) {
			log.error(e,e);
		} finally {
			log.debug("MDevId[" + parser.getMDevId() + "] saveMeterInfomation finish");
		}		
		// INSERT END SP-486
	}
	
	
	private boolean lpSave(IMeasurementData md, LPData[] validlplist,
			DLMSKaifa parser, double base,double addBasePulse) throws Exception {
	    String yyyymmdd = validlplist[0].getDatetime().substring(0, 8);
        String yyyymmddhh = validlplist[0].getDatetime().substring(0, 10);
        String hhmm = validlplist[0].getDatetime().substring(8, 12);
        double basePulse = validlplist[0].getLpValue();
        
		log.info("#########save base value:" + basePulse+":mdevId:"+parser.getMDevId()+":yyyymmddhh:"+yyyymmddhh);
		double[][] lpValues = new double[validlplist[0].getCh().length][validlplist.length];
		int[] flaglist = new int[validlplist.length];
		double[] pflist = new double[validlplist.length];

		for (int ch = 0; ch < lpValues.length; ch++) {
			for (int lpcnt = 0; lpcnt < lpValues[ch].length; lpcnt++) {
				// lpValues[ch][lpcnt] = validlplist[lpcnt].getLpValue();
				// Kw/h 단위로 저장
				lpValues[ch][lpcnt] = validlplist[lpcnt].getCh()[ch];
			}
		}

		for (int i = 0; i < flaglist.length; i++) {
			flaglist[i] = validlplist[i].getFlag();
			pflist[i] = validlplist[i].getPF();
		}

		parser.getMeter().setLpInterval(parser.getLpInterval());
		// TODO Flag, PF 처리해야 함.
		saveLPData(MeteringType.Normal, yyyymmdd, hhmm, lpValues, flaglist,
				basePulse, parser.getMeter(), parser.getDeviceType(),
				parser.getDeviceId(), parser.getMDevType(), parser
						.getMDevId());
		return true;
	}
	
	private boolean lpSave1(IMeasurementData md, LPData[] validlplist,
            DLMSKaifa parser, double base,double addBasePulse) throws Exception {
        LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
        String yyyymmdd = validlplist[0].getDatetime().substring(0, 8);
        String yyyymmddhh = validlplist[0].getDatetime().substring(0, 10);
        String hhmm = validlplist[0].getDatetime().substring(8, 12);
        String mm = validlplist[0].getDatetime().substring(10, 12);

        condition.add(new Condition("id.mdevType", new Object[] { parser
                .getMDevType() }, null, Restriction.EQ));
        condition.add(new Condition("id.mdevId", new Object[] { parser
                .getMDevId() }, null, Restriction.EQ));
        //log.debug("parser.getMDevType():"+parser.getMDevType()+":parser.getMDevId():"+parser.getMDevId()+":dst:"+DateTimeUtil
        // .inDST(null, parser.getMeterDate())+":yyyymmddhh:"+yyyymmddhh );
        condition
                .add(new Condition("id.dst", new Object[] { DateTimeUtil
//                      .inDST(null, parser.getMeterTime()) }, null,
                        .inDST(null, md.getTimeStamp()) }, null,
                        Restriction.EQ));

        condition.add(new Condition("id.channel",
                new Object[] { ElectricityChannel.Usage.getChannel() },
                null, Restriction.EQ));

        
        condition.add(new Condition("id.yyyymmddhh",
                new Object[] { yyyymmddhh }, null, Restriction.EQ));

        List<LpEM> lpEM = lpEMDao.findByConditions(condition);

        // String firstDate = lplist[0].getDatetime().substring(0,8);
        double basePulse = -1;

        try {
            if (lpEM != null && !lpEM.isEmpty()) {
                // 동시간대의 값을 가져올 경우 00분을 제외한 lp값을 더하여 base값을 구해야 한다.
                if (!mm.equals("00"))
                    basePulse = lpEM.get(0).getValue()+retValue(mm,lpEM.get(0).getValue_00(),lpEM.get(0).getValue_15(),lpEM.get(0).getValue_30(),lpEM.get(0).getValue_45());
                else
                    basePulse = lpEM.get(0).getValue();
            }else{
                LinkedHashSet<Condition> condition2 = new LinkedHashSet<Condition>();
                condition2.add(new Condition("id.mdevType",
                new Object[] { parser.getMDevType() }, null,
                            Restriction.EQ));
                condition2.add(new Condition("id.mdevId",
                            new Object[] { parser.getMDevId() }, null,
                            Restriction.EQ));
//                   log.debug("parser.getMDevType():"+parser.getMDevType()+":parser.getMDevId():"+parser.getMDevId()+":dst:"+DateTimeUtil
//                   .inDST(null,parser.getMeterTime())+":yyyymmddhh:"+yyyymmddhh );
                condition2.add(new Condition("id.dst",
//                          new Object[] { DateTimeUtil.inDST(null, parser
//                                  .getMeterTime()) }, null, Restriction.EQ));
                                    new Object[] { DateTimeUtil.inDST(null, 
                                    md.getTimeStamp()) }, null, Restriction.EQ));
                                    
                                    
                condition2.add(new Condition("id.channel",
                            new Object[] { ElectricityChannel.Usage
                                    .getChannel() }, null, Restriction.EQ));
                    
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat dateFormatter = new SimpleDateFormat(
                "yyyyMMddHH");
                cal.setTime(dateFormatter.parse(yyyymmddhh));
                
                cal.add(cal.HOUR, -1);

                condition2.add(new Condition("id.yyyymmddhh",
                                    new Object[] { dateFormatter.format(cal
                                            .getTime()) }, null, Restriction.EQ));
                List<LpEM> subLpEM = lpEMDao.findByConditions(condition2);
                if (subLpEM != null && !subLpEM.isEmpty()) {
                    // 전 시간 값을 가져온 것이기 때문에 전부 다 합산해야 한다.
                    basePulse = subLpEM.get(0).getValue()+retValue("00",subLpEM.get(0).getValue_00(),subLpEM.get(0).getValue_15(),subLpEM.get(0).getValue_30(),subLpEM.get(0).getValue_45());
                }
            }
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            log.error(e);
        }
        
        if (lpEM != null && !lpEM.isEmpty()) {  
            //basePulse = lpEM.get(0).getValue();
        } else {
            basePulse = base;
        }
        
        log.info("#########save base value:" + basePulse+":mdevId:"+parser.getMDevId()+":yyyymmddhh:"+yyyymmddhh);
        double[][] lpValues = new double[validlplist[0].getCh().length][validlplist.length];
        int[] flaglist = new int[validlplist.length];
        double[] pflist = new double[validlplist.length];

        for (int ch = 0; ch < lpValues.length; ch++) {
            for (int lpcnt = 0; lpcnt < lpValues[ch].length; lpcnt++) {
                // lpValues[ch][lpcnt] = validlplist[lpcnt].getLpValue();
                // Kw/h 단위로 저장
                lpValues[ch][lpcnt] = validlplist[lpcnt].getCh()[ch];
            }
        }

        for (int i = 0; i < flaglist.length; i++) {
            flaglist[i] = validlplist[i].getFlag();
            pflist[i] = validlplist[i].getPF();
        }

        parser.getMeter().setLpInterval(parser.getLpInterval());
        // TODO Flag, PF 처리해야 함.
        saveLPData(MeteringType.Normal, yyyymmdd, hhmm, lpValues, flaglist,
                basePulse, parser.getMeter(), parser.getDeviceType(),
                parser.getDeviceId(), parser.getMDevType(), parser
                        .getMDevId());
        return true;
    }

	private Meter checkAndRegisterMbusMeter( Meter parentMeter, String mdsId, MBUS_DEVICE_TYPE meterType, int port ){
		
		if ( mdsId == null || meterType == null ){
			return null;
		}
		Meter mBusMeter = meterDao.get(mdsId);
		Code code = null;
		if ( mBusMeter == null ){
			switch (meterType){
				case Electricity:
					mBusMeter = new EnergyMeter();
					mBusMeter.setMeterType(CommonConstants.getMeterTypeByName(MeterType.EnergyMeter.name()));
					code = codeDao.getCodeIdByCodeObject("1.3.1.1");
					break;
				case Gas:
					mBusMeter = new GasMeter();
					mBusMeter.setMeterType(CommonConstants.getMeterTypeByName(MeterType.GasMeter.name()));
					code = codeDao.getCodeIdByCodeObject("1.3.1.3");
					break;
				case Heat:
				case HeatCoolingLoad:
					mBusMeter = new HeatMeter();
					mBusMeter.setMeterType(CommonConstants.getMeterTypeByName(MeterType.HeatMeter.name()));
					code = codeDao.getCodeIdByCodeObject("1.3.1.4");
					break;
				case Water:
					mBusMeter = new WaterMeter();
					mBusMeter.setMeterType(CommonConstants.getMeterTypeByName(MeterType.WaterMeter.name()));
					code = codeDao.getCodeIdByCodeObject("1.3.1.2");
					break;
				default:			
					log.debug("MDevId[" + mdsId + "] Meter Device Type=" + meterType.name() + " is Not Support");
					break;
			}
			if ( mBusMeter != null){
				mBusMeter.setMdsId(mdsId);
				if ( parentMeter.getModemId() != null){
					mBusMeter.setModem(modemDao.get(parentMeter.getModemId()));
				}
				mBusMeter.setModemPort(new Integer(port));
				mBusMeter.setSupplier(parentMeter.getSupplier());
				if ( parentMeter.getLocation() != null)
					mBusMeter.setLocation(parentMeter.getLocation());
				if ( parentMeter.getAddress() != null)
					mBusMeter.setAddress(parentMeter.getAddress());
				mBusMeter.setInstallDate(DateTimeUtil.getDateString(new Date()));
				mBusMeter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.NewRegistered.name()));
				List<DeviceModel> list = deviceModelDao.getDeviceModelByTypeId(parentMeter.getSupplierId(), code.getId());
                if (list != null && list.size() >= 1 ){
                	for ( int i = 0; i < list.size(); i++ ){
                		DeviceModel model = list.get(i);
                		if ( code.getCode().equals(model.getDeviceType().getCode())){
                			mBusMeter.setModel(list.get(i));
                			break;
                		}
                	}
                }
	            meterDao.add(mBusMeter);
	            // meterDao.flush();
			}
		}
		// INSERT START SP-735
		else { 
			log.debug(" MDevId[" + mdsId + "] Already Exist, check UPDATE");
			boolean update = false;
			if ( parentMeter.getModem() != null ) {
				if ( mBusMeter.getModem() == null ||
					( !mBusMeter.getModem().getDeviceSerial().equals(parentMeter.getModem().getDeviceSerial()))){
					mBusMeter.setModem(parentMeter.getModem());
					log.debug("UPDATE MDevId[" + mdsId + "] MODEM[" +parentMeter.getModem().getDeviceSerial() + "]");
					update = true;
				}
			}
			Integer modemPort = new Integer(port);
			if ( mBusMeter.getModemPort() == null ||
					(modemPort.compareTo(mBusMeter.getModemPort()) != 0)){
				mBusMeter.setModemPort(new Integer(port));
				log.debug("UPDATE MDevId[" + mdsId + "] MODEM_PORT[" + port + "]");
				update = true;
			}
			
			if ( mBusMeter.getSupplier() == null ){
				mBusMeter.setSupplier(parentMeter.getSupplier());
				update = true;
			}

			Integer locationId = parentMeter.getLocationId();			
			if ( locationId != null ) {
				if ( mBusMeter.getLocation() == null ||
					( locationId.compareTo(mBusMeter.getLocationId()) != 0 )){
					mBusMeter.setLocation(parentMeter.getLocation());
					log.debug("UPDATE MDevId[" + mdsId + "] LOCATION [" + parentMeter.getLocation().getName() + "]");
					update = true;
				}
			}
			if ( parentMeter.getAddress() != null){
				if ( mBusMeter.getAddress() == null || "".equals(mBusMeter.getAddress())||
						!parentMeter.getAddress().equals(mBusMeter.getAddress())){
					mBusMeter.setAddress(parentMeter.getAddress());
					log.debug("UPDATE MDevId[" + mdsId + "] ADDRESS [" + parentMeter.getAddress() + "]");
					update = true;
				}
			}
				
			if ( mBusMeter.getInstallDate() == null || "".equals(mBusMeter.getInstallDate())){
				mBusMeter.setInstallDate(DateTimeUtil.getDateString(new Date()));
				log.debug("UPDATE MDevId[" + mdsId + "] INSTALLDATE ");
				update = true;
			}
			meterDao.update(mBusMeter);
		}
		// INSERT END SP-735
		return mBusMeter;
	}
	
	private void registerMbusMeters( DLMSKaifa parser)
	{
		Meter parentMeter = parser.getMeter();
		if ( parentMeter == null && parser.getMeterID() != null ){
			String parentMeterId = parser.getMeterID();
			parentMeter = meterDao.get(parentMeterId);
		}
		if ( parentMeter == null){
			log.error("parser MeterId is null");
			return;
		}
		checkAndRegisterMbusMeter(parentMeter, parser.getMBus1MeterID(), parser.getMBus1MeterType(), 1);
		checkAndRegisterMbusMeter(parentMeter, parser.getMBus2MeterID(), parser.getMBus2MeterType(), 2);
		checkAndRegisterMbusMeter(parentMeter, parser.getMBus3MeterID(), parser.getMBus3MeterType(), 3);
		checkAndRegisterMbusMeter(parentMeter, parser.getMBus4MeterID(), parser.getMBus4MeterType(), 4);
	}
	
	private void saveMbusDataKaifa(IMeasurementData md, DLMSKaifa parser) throws Exception {
		try {
			//register MBUS Meter
			registerMbusMeters(parser);
	        //MBUS Meter Save
			Modem modem = null;
	        String modemId = parser.getModemId();
	        if (modemId == null || modemId.isEmpty()) {
	        	Meter meter = parser.getMeter(); // meterDao.get(parser.getMDevId());
	        	
	        	// It's error on simulator. I add to check meter null or not.
	        	// 2017.01.21
	        	if (meter != null) {
    	        	modem = meter.getModem();
    	        	modemId = modem.getDeviceSerial();
	        	}
	        	else
	        	    return;
	        }
	        else {
	        	modem = modemDao.get(modemId);
	        }
	        
	        if (modem == null) {
	        	log.debug("MDevId[" + parser.getMDevId() + "] modem is null.");
	        }
	        
	        // INSERT START SP-735
	        String lastReadDate; 
	        if ( parser.getMeterTime() != null && !"".equals(parser.getMeterTime())){
	        	lastReadDate = parser.getMeterTime();
	        }
	        else {
	        	lastReadDate = DateTimeUtil.getDateString(new Date());
	        }
	        // INSERT END SP-735
	        Meter mbus1Meter = null;
	        Meter mbus2Meter = null;
	        Meter mbus3Meter = null;
	        Meter mbus4Meter = null;

	        Iterator iterator = modem.getMeter().iterator();
	        while( iterator.hasNext() ){
	            Meter meter = (Meter)iterator.next();
	            log.debug("MODEM[" + modem.getDeviceSerial() + "] has METER[" + meter.getMdsId() + "]");
	        	if (meter.getModemPort() == null)
	        		continue;
	            else if (meter.getModemPort() == 1)
	        		mbus1Meter = meter;
	        	else if (meter.getModemPort() == 2)
	        		mbus2Meter = meter;
	        	else if (meter.getModemPort() == 3)
	        		mbus3Meter = meter;
	        	else if (meter.getModemPort() == 4)
	        		mbus4Meter = meter;
	        }        
	        
	        LPData[] mbus1Lplist = parser.getMBUS1LPData();
	        LPData[] mbus2Lplist = parser.getMBUS2LPData();
	        LPData[] mbus3Lplist = parser.getMBUS3LPData();
	        LPData[] mbus4Lplist = parser.getMBUS4LPData();

	        if (mbus1Meter == null) {
				log.debug("MBUS1 MDevId[" + parser.getMDevId() + "] Address is not found.");        	
	        } else if (mbus1Lplist == null || mbus1Lplist.length == 0) {
				log.debug("MBUS1 MDevId[" + parser.getMDevId() + "] LPSIZE => 0");
			} else {
				log.debug("##########MBUS1 LPSIZE => "+mbus1Lplist.length);
//		        KaifaMBusMDSaver mbus1Saver = new KaifaMBusMDSaver();
//			    mbus1Saver.setMeterInfo(mbus1Meter, 
//			    		modemId, 
//			    		DeviceType.Modem, 
//			    		mbus1Meter.getMdsId(), 
//			    		parser.getMDevType());
//			    mbus1Saver.setLpData(mbus1Lplist);
//			    mbus1Saver.save(null);
				mbus1Meter.setLpInterval(parser.getMbus1LpInterval());
				// UPDATE START SP-735,736
				//saveMbusDataChannel(mbus1Meter, modemId, DeviceType.Modem, mbus1Meter.getMdsId(), parser.getMDevType(), mbus1Lplist);
				saveMbusDataChannelUsingLPTime(mbus1Meter, modemId, DeviceType.Modem, mbus1Meter.getMdsId(), parser.getMDevType(), mbus1Lplist);
				if (lastReadDate != null && !"".equals(lastReadDate)){
					log.debug("MBUS1 MdevId[" + mbus1Meter.getMdsId() + "] UPDATE LAST_READ_DATE[" + lastReadDate + "]");
					mbus1Meter.setLastReadDate(lastReadDate);
				}
				mbus1Meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.Normal.name()));
				meterDao.update(mbus1Meter);
				// UPDATE END   SP-735,736
			}

	        if (mbus2Meter == null) {
				log.debug("MBUS2 MDevId[" + parser.getMDevId() + "] Address is not found.");        	
	        } else if (mbus2Lplist == null  || mbus2Lplist.length == 0) {
				log.debug("MBUS2 MDevId[" + parser.getMDevId() + "] LPSIZE => 0");
			} else {
				log.debug("##########MBUS2 MDevId[" + parser.getMDevId() + "] LPSIZE => "+mbus2Lplist.length);
//		        KaifaMBusMDSaver mbus2Saver = new KaifaMBusMDSaver();
//			    mbus2Saver.setMeterInfo(mbus2Meter, 
//			    		modemId, 
//			    		DeviceType.Modem, 
//			    		mbus2Meter.getMdsId(),
//			    		parser.getMDevType());
//			    mbus2Saver.setLpData(mbus2Lplist);
//			    mbus2Saver.save(null);
				mbus2Meter.setLpInterval(parser.getMbus2LpInterval());
				// UPDATE START SP-735,736
				//saveMbusDataChannel(mbus2Meter, modemId, DeviceType.Modem, mbus2Meter.getMdsId(), parser.getMDevType(), mbus2Lplist);
				saveMbusDataChannelUsingLPTime(mbus2Meter, modemId, DeviceType.Modem, mbus2Meter.getMdsId(), parser.getMDevType(), mbus2Lplist);
				if (lastReadDate != null && !"".equals(lastReadDate)){
					log.debug("MBUS2 MdevId[" + mbus2Meter.getMdsId() + "] UPDATE LAST_READ_DATE[" + lastReadDate + "]");
					mbus2Meter.setLastReadDate(lastReadDate);
				}
				mbus2Meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.Normal.name()));
				meterDao.update(mbus2Meter);
				// UPDATE END   SP-735,736
			}

	        if (mbus3Meter == null) {
				log.debug("MBUS3 MDevId[" + parser.getMDevId() + "] Address is not found.");     
	        } else if (mbus3Lplist == null || mbus3Lplist.length == 0) {
				log.debug("MBUS3 MDevId[" + parser.getMDevId() + "] LPSIZE => 0");
			} else {
				log.debug("##########MBUS3 MDevId[" + parser.getMDevId() + "] LPSIZE => "+mbus3Lplist.length);
//		        KaifaMBusMDSaver mbus3Saver = new KaifaMBusMDSaver();
//			    mbus3Saver.setMeterInfo(mbus3Meter, 
//			    		modemId, 
//			    		DeviceType.Modem, 
//			    		mbus3Meter.getMdsId(), 
//			    		parser.getMDevType());
//			    mbus3Saver.setLpData(mbus1Lplist);
//			    mbus3Saver.save(null);
				mbus3Meter.setLpInterval(parser.getMbus3LpInterval());
				// UPDATE START SP-735,736
				//saveMbusDataChannel(mbus3Meter, modemId, DeviceType.Modem, mbus3Meter.getMdsId(), parser.getMDevType(), mbus3Lplist);
				saveMbusDataChannelUsingLPTime(mbus3Meter, modemId, DeviceType.Modem, mbus3Meter.getMdsId(), parser.getMDevType(), mbus3Lplist);
				if (lastReadDate != null && !"".equals(lastReadDate)){
					mbus3Meter.setLastReadDate(lastReadDate);
					log.debug("MBUS3 MdevId[" + mbus3Meter.getMdsId() + "] UPDATE LAST_READ_DATE[" + lastReadDate + "]");
				}
				mbus3Meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.Normal.name()));
				meterDao.update(mbus3Meter);
				// UPDATE END   SP-735,736
			}

	        if (mbus4Meter == null) {
				log.debug("MBUS4 MDevId[" + parser.getMDevId() + "] Address is not found.");     
	        } else if (mbus4Lplist == null || mbus4Lplist.length == 0) {
				log.debug("MBUS4 MDevId[" + parser.getMDevId() + "] LPSIZE => 0");
			} else {
				log.debug("##########MBUS4 LPSIZE => "+mbus4Lplist.length);
//		        KaifaMBusMDSaver mbus4Saver = new KaifaMBusMDSaver();
//			    mbus4Saver.setMeterInfo(mbus4Meter, 
//			    		modemId, 
//			    		DeviceType.Modem, 
//			    		mbus4Meter.getMdsId(),
//			    		parser.getMDevType());
//			    mbus4Saver.setLpData(mbus4Lplist);
//			    mbus4Saver.save(null);
				mbus4Meter.setLpInterval(parser.getMbus4LpInterval());
				// UPDATE START SP-735,736				
				//saveMbusDataChannel(mbus4Meter, modemId, DeviceType.Modem, mbus4Meter.getMdsId(), parser.getMDevType(), mbus4Lplist);
				saveMbusDataChannelUsingLPTime(mbus4Meter, modemId, DeviceType.Modem, mbus4Meter.getMdsId(), parser.getMDevType(), mbus4Lplist);
				if (lastReadDate != null && !"".equals(lastReadDate)){
					mbus4Meter.setLastReadDate(lastReadDate);
					log.debug("MBUS4 MdevId[" + mbus4Meter.getMdsId() + "] UPDATE LAST_READ_DATE[" + lastReadDate + "]");
				}
				mbus4Meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.Normal.name()));
				meterDao.update(mbus4Meter);
				// UPDATE END   SP-735,736
			}
		}
		catch (Exception e) {
		    log.error("MdevId[" + parser.getMDevId() + "] saveMbusDataKaifa error");
		    log.error(e, e);
		}finally {
			log.debug("MDevId[" + parser.getMDevId() + "] saveMbusDataKaifa finish");
		}
	}

	private void saveMbusDataChannel(Meter meter, String devId, DeviceType devType, String mdevId,
	        DeviceType mdevType, LPData[] mbusLplist) throws Exception {
		try {
			log.debug("saveMbusDataChannel devId[" + devId + 
					"] devType[" + devType.name() + "] mdevId[" + mdevId + 
					"] mdevType[" + mdevType.name() + "]" );
			
	        if(mbusLplist == null){
	            log.debug("MDevId[" + mdevId + "] LPSIZE => 0");
	        }
	        else
	        {
	            log.debug("MDevId[" + mdevId + "] LPSIZE => "+ mbusLplist.length);
	            String yyyymmdd = mbusLplist[0].getDatetime().substring(0, 8);
	            String hhmm = mbusLplist[0].getDatetime().substring(8, 12);
	            
	            double[][] lpValues = new double[mbusLplist[0].getCh().length][mbusLplist.length];
	            int[] flaglist = new int[mbusLplist.length];
	            
	            for (int ch = 0; ch < lpValues.length; ch++) {
	                for (int lpcnt = 0; lpcnt < lpValues[ch].length; lpcnt++) {
	                    lpValues[ch][lpcnt] = mbusLplist[lpcnt].getCh()[ch];
	                    log.debug("MDevId[" + mdevId + "] lpValues[" + ch + "][" + lpcnt + "] = " + lpValues[ch][lpcnt]);
	                }
	            }
	            
	            for (int i = 0; i < flaglist.length; i++) {
	                flaglist[i] = mbusLplist[i].getFlag();
	                log.debug("MDevId[" + mdevId + "] flaglist[" + i + "] = " + flaglist[i]);
	            }
	            
	            saveLPData(MeteringType.Normal, yyyymmdd, hhmm, lpValues, flaglist, 0,
	            		meter, devType, devId, mdevType, mdevId);
	        }
		}finally {
			log.debug("MDevId[" + mdevId + "] saveMbusDataChannel finish");
		}
	}
	
    @Override
    public String relayValveOn(String mcuId, String meterId) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            int nOption = OnDemandOption.WRITE_OPTION_RELAYON.getCode();
            //resultMap = commandGw.cmdOnDemandMeter( mcuId, meterId, nOption);
            resultMap = commandGw.cmdMeterRelay(mcuId, meterId, nOption);
//            if ( resultMap != null && !"Success".equals(resultMap.get("RESULT_VALUE"))) {
//            	resultMap = commandGw.cmdMeterRelay(mcuId, meterId, nOption);
//            }
            if (resultMap != null && "Success".equals(resultMap.get("RESULT_VALUE"))) {
            	// -> UPDATE START 2016/09/14 SP-117
            	// CONTROL_STATE ctrlStatus =  (CONTROL_STATE)resultMap.get("value");
    			// resultMap.put("Relay Status", ctrlStatus.name());
                // if (ctrlStatus == CONTROL_STATE.Connected) {
                //     updateMeterStatusNormal(meter);
                // }
            	
            	// -> DELETE START 2016/09/20 SP-117
            	// Boolean ctrlStatus = (Boolean)resultMap.get( "value" );
        		// if ( ctrlStatus != null ) {
        		//	if( ctrlStatus == true ) {
        		//		resultMap.put( "Relay Status", RELAY_STATUS_KAIFA.Connected );
                //        updateMeterStatusNormal( meter );
        		//	}
        		//	else {
        		//		resultMap.put( "Relay Status", RELAY_STATUS_KAIFA.Disconnected );
	        	//	}
        		// }
            	// <- DELETE END   2016/09/20 SP-117
            	// <- UPDATE END   2016/09/14 SP-117
            }
            else {
            	if ( resultMap == null ){
            		log.error("MDevId[" + meterId + "] resultMap is NULL");
            		resultMap = new HashMap<String, Object>();
            		resultMap.put("failReason", "UNKNOWN");
            	}
            	if ( resultMap.get("RESULT_VALUE") == null ){
            		log.error("MDevId[" + meterId + "] resultMap RESULT_VALUE is NULL");
            		resultMap.put("RESULT_VALUE", "Fail");
           		 	resultMap.put("failReason", "UNKNOWN");
            	}
            }
        }
        catch (Exception e) {
            resultMap.put("failReason", e.getMessage());
        }
        
        return MapToJSON(resultMap);
    }

    @Override
    public String relayValveOff(String mcuId, String meterId) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            int nOption = OnDemandOption.WRITE_OPTION_RELAYOFF.getCode();
            //resultMap = commandGw.cmdOnDemandMeter( mcuId, meterId, nOption);
            resultMap = commandGw.cmdMeterRelay(mcuId, meterId, nOption);
//            if ( resultMap != null && !"Success".equals(resultMap.get("RESULT_VALUE")) ) {
//            	resultMap = commandGw.cmdMeterRelay(mcuId, meterId, nOption);
//            }
            if ( resultMap != null && "Success".equals(resultMap.get("RESULT_VALUE"))){
            	// -> UPDATE START 2016/09/14 SP-117
        		// CONTROL_STATE ctrlStatus = (CONTROL_STATE)resultMap.get("value");
        		// if ( ctrlStatus != null ) {
	        	// 	resultMap.put("Relay Status", ctrlStatus.name());
	        	// 	if ( ctrlStatus == CONTROL_STATE.Disconnected ){
	        	// 		updateMeterStatusCutOff(meter);
	        	// 	}
        		// }
            	
            	// -> DELETE START 2016/09/20 SP-117
            	// Boolean ctrlStatus = (Boolean)resultMap.get( "value" );
        		// if ( ctrlStatus != null ) {
        		//	if( ctrlStatus == true ) {
        		//		resultMap.put( "Relay Status", RELAY_STATUS_KAIFA.Connected );
        		//	}
        		//	else {
        		//		resultMap.put( "Relay Status", RELAY_STATUS_KAIFA.Disconnected );
	        	//		updateMeterStatusCutOff( meter );
	        	//	}
        		//}
            	// <- DELETE END   2016/09/20 SP-117
            	// <- UPDATE END   2016/09/14 SP-117
            }
            else {
            	if ( resultMap == null ){
            		log.error("resultMap is NULL");
            		resultMap = new HashMap<String, Object>();
            		 resultMap.put("failReason", "UNKNOWN");
            	}
            	if ( resultMap.get("RESULT_VALUE") == null ){
            		log.error("resultMap RESULT_VALUE is NULL");
            		resultMap.put("RESULT_VALUE", "Fail");
           		 	resultMap.put("failReason", "UNKNOWN");
            	}
            }
        }
        catch (Exception e) {
            resultMap.put("failReason", e.getMessage());
        }
        
        return MapToJSON(resultMap);
    }

    
    
    @Override
    public String relayValveStatus(String mcuId, String meterId) {
        Map<String, Object> resultMap  = new HashMap<String, Object>();
        // Map<String, Object> resultMap2 = new HashMap<String, Object>(); // INSERT 2016/08/25 SP117 // DELETE 2016/09/20 SP-117
        try {
        	log.debug( "relayValveStatus(" + mcuId + ", " + meterId + ") enter"); // INSERT 2016/08/25 SP117
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            int nOption = OnDemandOption.READ_OPTION_RELAY.getCode(); //read table
            // -> UPDATE START 2016/08/24 SP117
            // resultMap = commandGw.cmdOnDemandMeter( mcuId, meterId, nOption);
            // //resultMap = commandGw.cmdMeterRelay( mcuId, meterId, nOption);
            // //resultMap = commandGw.cmdMeterRelayLoadCtrl(mcuId, meterId);
            // if (resultMap != null) {
            // -> UPDATE START 2016/09/20 SP-117
            // resultMap2 = commandGw.cmdMeterRelay( mcuId, meterId, nOption );
            // if( resultMap2 != null ) {
            resultMap = commandGw.cmdMeterRelay( mcuId, meterId, nOption );
            if( resultMap != null ) {
            // <- UPDATE START 2016/09/20 SP-117
            // <- UPDATE END   2016/08/24 SP117
//            	if ( resultMap.get("RESULT_VALUE").toString() == "Success"  ){
//        			CONTROL_STATE state = CONTROL_STATE.getValue(Integer.valueOf(resultMap.get("value").toString()).intValue());
//        			if(state != null){
//        				switch(state){
//        				case Disconnected :  
//        					resultMap.put("LoadControlStatus", CONTROL_STATE.Disconnected);
//        					break;
//        				case Connected :
//        					resultMap.put("LoadControlStatus", CONTROL_STATE.Connected);
//        					break;
//        				case ReadyForReconnection :
//        					resultMap.put("LoadControlStatus", CONTROL_STATE.ReadyForReconnection);
//        					break;
//        				}
//        				
//        				log.debug("LoadControlStatus : " + resultMap.get("LoadControlStatus"));
//        			}
//            	}else{
//            		resultMap.put("LoadControlStatus", "Fail");
//            	}
            	// -> UPDATE START 2016/08/29 SP117
                // if ((CONTROL_STATE)resultMap.get("LoadControlStatus") == CONTROL_STATE.Disconnected) {
                //     updateMeterStatusCutOff(meter);
                // }
                // else if ((CONTROL_STATE)resultMap.get("LoadControlStatus") == CONTROL_STATE.Connected) {
                //     updateMeterStatusNormal(meter);
                // }
            	
                // -> DELETE START 2016/09/20 SP-117
            	// // Set [Relay Status]
                // if( (Boolean)resultMap2.get("RelayStatus") == true ) {
                //    resultMap.put( "Relay Status", RELAY_STATUS_KAIFA.Connected );
                //    updateMeterStatusNormal(meter);
                // }
                // else {
                //    resultMap.put( "Relay Status", RELAY_STATUS_KAIFA.Disconnected );
                //    updateMeterStatusCutOff(meter);
                // }
           	
            	// // Set [LoadControlStatus]
                // if( (Integer)resultMap2.get("LoadControlStatus") == CONTROL_STATE.Disconnected.ordinal() ) {
                //     // updateMeterStatusCutOff(meter);
                //     resultMap.put( "LoadControlStatus", CONTROL_STATE.Disconnected );
                // }
                // else if( (Integer)resultMap2.get("LoadControlStatus") == CONTROL_STATE.Connected.ordinal() ) {
                //     // updateMeterStatusNormal(meter);
                //     resultMap.put( "LoadControlStatus", CONTROL_STATE.Connected );
                // }
                // else if( (Integer)resultMap2.get("LoadControlStatus") == CONTROL_STATE.ReadyForReconnection.ordinal() ) {
                // 	resultMap.put( "LoadControlStatus", CONTROL_STATE.ReadyForReconnection );
                // }
                // <- DELETE END   2016/09/20 SP-117
            	// <- UPDATE END   2016/08/29 SP117
            }
        }
        catch (Exception e) {
            log.error(e, e);
            resultMap.put("failReason", e.getMessage());
            resultMap.put("Result", ResultStatus.FAIL);
        }
        
        return MapToJSON(resultMap);
    }

    /**
     * Hex ObisCode => x.x.x.x.x.x 
     * 
     * @param obisCode
     * @return
     */
    private String convertObis(String obisCode) {
    	String returnData = "";
    	if(obisCode.length() == 12) {
    		byte[] obisCodeArr = Hex.encode(obisCode);
    		obisCode="";
    		for (int i = 0; i < obisCodeArr.length; i++) {
    			if(i == 0) {
    				obisCode += DataUtil.getIntToByte(obisCodeArr[i]);
    			} else {
    				obisCode += "."+DataUtil.getIntToByte(obisCodeArr[i]);
    			}
			}
    		returnData = obisCode;
    	} else {
    		returnData = "Wrong Obis";
    	}
    	
    	return returnData;
    }

    // INSERT START SP-279
    public String getTravelTime(String meterId) {
        Meter meter = meterDao.get(meterId);
        Modem modem = meter.getModem();

        String travelTime = "0";
        if (modem.getModemType() == ModemType.MMIU) {
        	travelTime = FMPProperty.getProperty("soria.meter.synctime.traveltime.mmiu", "0");
        } else if (modem.getModemType() == ModemType.IEIU) {
        	travelTime = FMPProperty.getProperty("soria.meter.synctime.traveltime.ieiu", "0");
        } else if (modem.getModemType() == ModemType.SubGiga) {
        	travelTime = FMPProperty.getProperty("soria.meter.synctime.traveltime.subgiga", "0");
        } else {
        	travelTime = "0";
        }
        
        return travelTime;
    }
    // INSERT END SP-279
    
    @Override
    public String syncTime(String mcuId, String meterId) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int result = 0;
        try {
            Meter meter = meterDao.get(meterId);
            Modem modem = meter.getModem();
            String travelTime = "0";
            if (modem.getModemType() == ModemType.MMIU) {
            	travelTime = FMPProperty.getProperty("soria.meter.synctime.traveltime.mmiu", "0");
            } else if (modem.getModemType() == ModemType.IEIU) {
            	travelTime = FMPProperty.getProperty("soria.meter.synctime.traveltime.ieiu", "0");
            } else if (modem.getModemType() == ModemType.SubGiga) {
            	travelTime = FMPProperty.getProperty("soria.meter.synctime.traveltime.subgiga", "0");
            } else {
            	travelTime = "0";
            }
            
    		log.debug("syncTime() MeterID[" + meter.getMdsId() + "] ModemType[" + modem.getModemType().name() + 
    				"] diff[" + meter.getTimeDiff() + "] travel[" + travelTime + "]");
    		            
			String obisCode = this.convertObis(OBIS.CLOCK.getCode());
			int classId = DLMS_CLASS.CLOCK.getClazz();
			int attrId = DLMS_CLASS_ATTR.CLOCK_ATTR02.getAttr();
			String accessRight = "RW";
			String dataType = "octet-string";
            String param = obisCode+"|"+classId+"|"+attrId+"|"+accessRight+"|"+dataType+"|"+travelTime;

            CommandGW commandGw = DataUtil.getBean(CommandGW.class);                        
            
            if((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) {
            	// MBB
            	// for AutoTimeSync Task
       			String cmd = "cmdSyncTime";
				String rtnString="";
       			Map<String, String> paramMap = new HashMap<String, String>();
    			//paramSet
    			paramMap.put("paramSet", param);        			
    			paramMap.put("meterId", meter.getMdsId());
				paramMap.put("option", "synctime");

        		MMIU mmiuModem = mmiuDao.get(modem.getId());           				
   				List<String> paramListForSMS  = new ArrayList<String>();
   				Properties prop = new Properties();

   				//smpp.hes.fep.server=10.40.200.46
   				//soria.modem.tls.port=8900*
   				//smpp.auth.port=9001
   		        paramListForSMS.add(FMPProperty.getProperty("smpp.hes.fep.server", ""));
   		        paramListForSMS.add(FMPProperty.getProperty("soria.modem.tls.port", ""));
   		        paramListForSMS.add(FMPProperty.getProperty("smpp.auth.port", ""));
           		        
   		        String cmdMap = null;
   		        ObjectMapper om = new ObjectMapper();
   		        if(paramMap != null)
   		        	cmdMap = om.writeValueAsString(paramMap);
           		                  		    	
   		    	rtnString = commandGw.sendSMS(
   		    			cmd, 
   		    			SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), 
   		    			mmiuModem.getPhoneNumber(), 
   		    			modem.getDeviceSerial(),
   		    			SMSConstants.COMMAND_TYPE.NI.getTypeCode(), 
   		    			paramListForSMS, cmdMap);
            }
            else {            
	            // if synctime option, value param is travel time.
	            resultMap = commandGw.cmdMeterParamSetWithOption(modem.getDeviceSerial(),param, "synctime");                       
	            if(resultMap != null) {
	            	log.debug("MDevId[" + meterId + "] resultMap[" + resultMap.toString() + "]");
	
	            	String afterTime = resultMap.get("aftertime").toString();
	            	if (afterTime != null) {
		        		String beforeTime = DateTimeUtil.getDateString(TimeUtil.getLongTime(afterTime) - (meter.getTimeDiff()*1000));
		
		            	String diff = String.valueOf((TimeUtil.getLongTime(afterTime) - TimeUtil.getLongTime(beforeTime))/1000);
		                resultMap.put("diff", diff);
		                
		                if (resultMap.get("RESULT_VALUE") == null || resultMap.get("RESULT_VALUE").toString() != "Success") {
		                	result = 1;
		                }
		                saveMeterTimeSyncLog(meter, beforeTime, afterTime, result);
	            	}
	            }
            }
        }
        catch (Exception e) {
            result = 1;
            resultMap.put("failReason", e.getMessage());
        }
        return MapToJSON(resultMap);
    }

    // INSERT START SP-279
    public void updateMeterTimeSyncLog(String meterId, String afterTime, int result) {
        try {
        	Meter meter = meterDao.get(meterId);

        	if (afterTime != null) {
        		String beforeTime = DateTimeUtil.getDateString(TimeUtil.getLongTime(afterTime) - (meter.getTimeDiff()*1000));

            	String diff = String.valueOf((TimeUtil.getLongTime(afterTime) - TimeUtil.getLongTime(beforeTime))/1000);
                saveMeterTimeSyncLog(meter, beforeTime, afterTime, result);
        	}
        }
        catch (Exception e) {
        	log.debug(e,e);
        }
    }    
    // INSERT END SP-279
    
    public MeterData onDemandMeterBypass(String mcuId, String meterId,String modemId, String nOption, String fromDate, String toDate)
                    throws Exception
    {
        log.info("mcuId[" + mcuId + "] meterId[" + meterId+"] modemId[" + modemId+"] nOption[" + nOption + "] fromDate[" + fromDate + "] toDate[" + toDate +"]");

        MeterData mdata = new MeterData();
		
        TransactionStatus txstatus = null;
        int modemPort = 0;
		try {
            txstatus = txmanager.getTransaction(null);
			Meter meter = meterDao.get(meterId);
			if (meter == null)
				return null;
			if ( meter.getModemPort() != null ){
				modemPort = meter.getModemPort().intValue();
			}
			if ( modemPort > 5){
				throw new Exception("ModemPort:" + modemPort + " is not Support");
			}
            txmanager.commit(txstatus);	
        }
        catch (Exception e) {
        	log.debug(e,e);
        	if (txstatus != null) txmanager.rollback(txstatus);
        	throw e;
        } 
		
		Map<String,Object> result = null;
		long activeEnergyImport = 0;
		long activeEnergyExport = 0;
		long reactiveEnergyImport = 0;
		long reactiveEnergyExport = 0;
		long lastMeterValue = 0;
		try {				                           
			CommandGW commandGw = DataUtil.getBean(CommandGW.class);
	        if ( modemPort == 0 ){
	        	result = (Map<String,Object>)commandGw.cmdGetLoadProfile(modemId, fromDate, toDate);
	        }
	        else {
	        	result = commandGw.cmdGetLoadProfileChannel(modemId, modemPort, fromDate, toDate);
	        		 
	        }     
	        // Get Load Profile Rawdata
			byte[] rawdata = (byte[])result.get("lprawdata");
			log.debug("MDevId[" + meterId + "] LoadProfile Data =" + Hex.decode(rawdata));

            txstatus = txmanager.getTransaction(null);	

            //ModemDao modemDao = DataUtil.getBean(ModemDao.class);
			Meter meter = meterDao.get(meterId);
			Modem modem = modemDao.get(modemId);
	
//			DeviceConfig deviceConfig = meter.getModel().getDeviceConfig();
//			if (deviceConfig == null)
//				deviceConfig = meter.getModem().getModel().getDeviceConfig();
			
			DLMSKaifa mdp = new DLMSKaifa();
//			if (deviceConfig.getOndemandParserName() != null && !"".equals(deviceConfig.getOndemandParserName()))
//				mdp = (MeterDataParser) Class.forName(deviceConfig.getOndemandParserName()).newInstance();
//			else
//				mdp = (MeterDataParser) Class.forName(deviceConfig.getParserName()).newInstance();

			mdp.setModemId(modemId);
			mdp.setMeter(meter);
			mdp.setMeteringTime(DateTimeUtil.getDateString(new Date()));
			mdp.setMeterTime(mdp.getMeteringTime());
			mdp.setModemPort(modemPort);
			mdp.parse(rawdata);
			//DLMSKaifa kaifa = (DLMSKaifa) mdp;
			
			// 
			mdata.setMeterId(meterId);
			mdata.setParser(mdp);
	        
			// Save LP Data
			// UPDATE START SP-487
//			MeasurementData md = new MeasurementData(meter);
//			md.setMeterDataParser(mdp);
//			md.setTimeStamp(mdp.getMeteringTime());	
//			save(md);
			commandGw.saveMeteringDataByQueue(mcuId, meterId, modemId, rawdata);
			// UPDATE END SP-487
			
			// Update Meter and Modem
			BigDecimal lastValue = null;
			if ( modemPort == 0 ){
				MeterData.Map meterMap = mdata.getMap();
				MeterData.Map.Entry entry ;

				// INSERT START SP-369
		        DecimalFormat decimalf=null;		         
		        if(meter!=null && meter.getSupplier()!=null){
		            Supplier supplier = meter.getSupplier();
		            if(supplier !=null){
		                String lang = supplier.getLang().getCode_2letter();
		                String country = supplier.getCountry().getCode_2letter();
		                
		                decimalf = TimeLocaleUtil.getDecimalFormat(supplier);
		            }
		        }else{
		            //locail, If no information is to use the default format.
		            decimalf = new DecimalFormat();
		        }				
		        // INSERT END SP-369
				
				OBIS cumulatives[] =  new OBIS [4];
				cumulatives[0] = OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT;
				cumulatives[1] = OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT;
				cumulatives[2] = OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT;
				cumulatives[3] = OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT; 
				for ( int i = 0; i < cumulatives.length; i++ ){
					if ( result.get(cumulatives[i].name()) != null && 
							result.get(cumulatives[i].name()+ "_UNIT") != null){
						long value = (long)result.get(cumulatives[i].name());
						Map<String, Object> scaleunit = (Map<String, Object>)result.get(cumulatives[i].name()+ "_UNIT");
						UNIT unit = UNIT.getItem((int)scaleunit.get("unit"));
						int scaler = (int)scaleunit.get("scaler");
						BigDecimal dcValue = new BigDecimal( new BigInteger(String.valueOf(value)), -scaler);
						// INSERT START SP-369
						BigDecimal multiplicand = new BigDecimal(new BigInteger("1"), 3);
						dcValue = dcValue.multiply(multiplicand);	
						// INSERT END SP-369
						log.debug("MDevId[" + meterId + "] OBIS=" + cumulatives[i].name() +",value=" + value + ",unit=" + unit.getName() + ",scaler=" + scaler + ",dcValue=" + dcValue.toString());

						entry= new MeterData.Map.Entry();
						entry.setKey(cumulatives[i].getName());
						// UPDATE START SP-369
				        //entry.setValue(dcValue.toPlainString() + " " + unit.getName());
						String newUnit = unit.getName().replace("[", "[k");
						entry.setValue(decimalf.format(dcValue.doubleValue()) + " " + newUnit);
						// UPDATE END SP-369
				        meterMap.getEntry().add(entry);
				        if (cumulatives[i] == OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT ||
				        		cumulatives[i] == OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT){
				        	if ( dcValue.compareTo(new BigDecimal(0)) > 0 ) {
				        		lastValue = dcValue;
				        	}
				        }
					}
				}
	
		        if( lastValue != null ){
		        	meter.setLastMeteringValue((double)lastValue.doubleValue());
		        }
				meter.setLastReadDate(DateTimeUtil.getDateString(new Date()));
				meterDao.update(meter);
			}
			modem.setLastLinkTime(meter.getLastReadDate());
			modemDao.update(modem);			

            txmanager.commit(txstatus);	
        }
        catch (Exception e) {
        	log.debug(e,e);
        	if (txstatus != null) txmanager.rollback(txstatus);
        	throw e;
        }       
        return mdata;
    }
    
    public MeterData parseAndSaveData(String meterId, String modemId, byte[] rawdata, Map<String, Object> result)
            throws Exception
	{
		log.info("meterId[" + meterId+"] modemId[" + modemId+"] ");
		
		MeterData mdata = new MeterData();
		
		TransactionStatus txstatus = null;
		int modemPort = 0;
		try {
		    txstatus = txmanager.getTransaction(null);
			Meter meter = meterDao.get(meterId);
			if (meter == null)
				return null;
			if ( meter.getModemPort() != null ){
				modemPort = meter.getModemPort().intValue();
			}
			if ( modemPort > 5){
				throw new Exception("ModemPort:" + modemPort + " is not Support");
			}
		    txmanager.commit(txstatus);	
		}
		catch (Exception e) {
			log.debug(e,e);
			if (txstatus != null) txmanager.rollback(txstatus);
			throw e;
		} 
		
		//Map<String,Object> result = null;

		try {				                           
			log.debug("MDevId[" + meterId + "] LoadProfile Data =" + Hex.decode(rawdata));
		
		    txstatus = txmanager.getTransaction(null);	
		
			Meter meter = meterDao.get(meterId);
			Modem modem = modemDao.get(modemId);
		
			DLMSKaifa mdp = new DLMSKaifa();
		
			mdp.setModemId(modemId);
			mdp.setMeter(meter);
			mdp.setMeteringTime(DateTimeUtil.getDateString(new Date()));
			mdp.setMeterTime(mdp.getMeteringTime());
			mdp.setModemPort(modemPort);
			mdp.parse(rawdata);
			
			mdata.setMeterId(meterId);
			mdata.setParser(mdp);
		    
			// Save LP Data
			// UPDATE START SP-487
//			MeasurementData md = new MeasurementData(meter);
//			md.setMeterDataParser(mdp);
//			md.setTimeStamp(mdp.getMeteringTime());	
//			save(md);
			CommandGW commandGw = DataUtil.getBean(CommandGW.class);
			commandGw.saveMeteringDataByQueue("127.0.0.1", meterId, modemId, rawdata);
			// UPDATE END SP-487
			
			// Update Meter and Modem
			BigDecimal lastValue = null;
			if ( modemPort == 0 ){
				MeterData.Map meterMap = mdata.getMap();
				MeterData.Map.Entry entry ;

				// INSERT START SP-369
		        DecimalFormat decimalf=null;		         
		        if(meter!=null && meter.getSupplier()!=null){
		            Supplier supplier = meter.getSupplier();
		            if(supplier !=null){
		                String lang = supplier.getLang().getCode_2letter();
		                String country = supplier.getCountry().getCode_2letter();
		                
		                decimalf = TimeLocaleUtil.getDecimalFormat(supplier);
		            }
		        }else{
		            //locail, If no information is to use the default format.
		            decimalf = new DecimalFormat();
		        }				
		        // INSERT END SP-369				
				
				OBIS cumulatives[] =  new OBIS [4];
				cumulatives[0] = OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT;
				cumulatives[1] = OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT;
				cumulatives[2] = OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT;
				cumulatives[3] = OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT; 
				for ( int i = 0; i < cumulatives.length; i++ ){
					if ( result.get(cumulatives[i].name()) != null && 
							result.get(cumulatives[i].name()+ "_UNIT") != null){
						long value = (long)result.get(cumulatives[i].name());
						Map<String, Object> scaleunit = (Map<String, Object>)result.get(cumulatives[i].name()+ "_UNIT");
						UNIT unit = UNIT.getItem((int)scaleunit.get("unit"));
						int scaler = (int)scaleunit.get("scaler");
						BigDecimal dcValue = new BigDecimal( new BigInteger(String.valueOf(value)), -scaler);
						// INSERT START SP-369
						BigDecimal multiplicand = new BigDecimal(new BigInteger("1"), 3);
						dcValue = dcValue.multiply(multiplicand);	
						// INSERT END SP-369
						log.debug("MDevId[" + meterId + "] OBIS=" + cumulatives[i].name() +",value=" + value + ",unit=" + unit.getName() + ",scaler=" + scaler + ",dcValue=" + dcValue.toString());
		
						entry= new MeterData.Map.Entry();
						entry.setKey(cumulatives[i].getName());
				        // UPDATE START SP-369
				        //entry.setValue(dcValue.toPlainString() + " " + unit.getName());
						String newUnit = unit.getName().replace("[", "[k");
						entry.setValue(decimalf.format(dcValue.doubleValue()) + " " + newUnit);
						// UPDATE END SP-369
				        meterMap.getEntry().add(entry);
				        if (cumulatives[i] == OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT ||
				        		cumulatives[i] == OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT){
				        	if ( dcValue.compareTo(new BigDecimal(0)) > 0 ) {
				        		lastValue = dcValue;
				        	}
				        }
					}
				}
		
		        if( lastValue != null ){
		        	meter.setLastMeteringValue((double)lastValue.doubleValue());
		        }
				meter.setLastReadDate(DateTimeUtil.getDateString(new Date()));
				meterDao.update(meter);
			}
			modem.setLastLinkTime(meter.getLastReadDate());
			modemDao.update(modem);			
		
		    txmanager.commit(txstatus);	
		}
		catch (Exception e) {
			log.debug(e,e);
			if (txstatus != null) txmanager.rollback(txstatus);
			throw e;
		}       
		return mdata;
	}
    
    // DELETE START SP-487
//    public boolean saveMeterData(Meter meter, DLMSKaifa parser)
//    {
//    	boolean ret = false;
//    	try {
//			MeasurementData md = new MeasurementData(meter);
//			md.setMeterDataParser(parser);
//			md.setTimeStamp(parser.getMeteringTime());	
//			save(md);
//			ret = true;
//    	}
//    	catch (Exception e){
//			log.debug(e,e);
//    	}
//    	return ret;
//    }
    // DELETE END SP-487
    
    public static String getOndemandDetailHTML(MeterData.Map data) {
        StringBuffer html = new StringBuffer();
        html.append("<html>");
        html.append("<form name='f' method='post' style='display:none'>");
        html.append("<textarea name='excelData'></textarea></form>");
        html.append("<link href=/aimir-web/css/style.css rel=stylesheet type=text/css>");
        html.append("<ul><span style='float:right'><li><em class='am_button' style='margin-right: 50px;'><a href='javascript:openExcelReport2();'>excel</a></em></li></span></ul>");
        html.append("<table  border=1 cellpadding=0 cellspacing=0 bordercolor=#FFFFFF border=1 width=100% id=ondemandTable>");
        html.append("<caption style='text-align: center;font-size: 20px;'><b>Ondemand Result</b></caption>");
        html.append("<tr><td>&nbsp;</td><td>&nbsp;</td><tr>");
        List<MeterData.Map.Entry> iter = data.getEntry();
        MeterData.Map.Entry entry;
        Object value;
        Object key;

        boolean isMx2 = false;
        String volAng_a = null;
        String volAng_b = null;
        String volAng_c = null;

        String curAng_a = null;
        String curAng_b = null;
        String curAng_c = null;

        for (int cnt = 0; cnt < iter.size(); cnt++) {
            html.append("<tr>");

            entry = iter.get(cnt);
            key = entry.getKey();
            if (!"Status".equals(key) && !"LpValue".equals(key) && !"rowMap".equals(key)) {
                try{
                    value = entry.getValue();
                }catch(Exception e){
                    value = "";
                }

                html.append("<td height='auto' width='50%' align=left style=\"word-break:break-all\"><b>").append(key).append("</b></td>");
                html.append("<td height='auto' align=left style=\"word-break:break-all\">").append(value).append("</td>");

                if ("Model".equals(key) && "Mitsubishi-MX2".equals((String)value)) {
                    isMx2 = true;
                } else if ("Voltage Angle(A)".equals(key)) {
                    volAng_a = (String)value;
                } else if ("Voltage Angle(B)".equals(key)) {
                    volAng_b = (String)value;
                } else if ("Voltage Angle(C)".equals(key)) {
                    volAng_c = (String)value;
                } else if ("Current Angle(A)".equals(key)) {
                    curAng_a = (String)value;
                } else if ("Current Angle(B)".equals(key)) {
                    curAng_b = (String)value;
                } else if ("Current Angle(C)".equals(key)) {
                    curAng_c = (String)value;
                }
            }

            html.append("</tr>");
        }

        html.append("</table>");

        if (isMx2 && (!StringUtil.nullToBlank(volAng_a).isEmpty() || !StringUtil.nullToBlank(volAng_b).isEmpty() || !StringUtil.nullToBlank(volAng_c).isEmpty() 
                || !StringUtil.nullToBlank(curAng_a).isEmpty() || !StringUtil.nullToBlank(curAng_b).isEmpty() || !StringUtil.nullToBlank(curAng_c).isEmpty())) {
            html.append("<table id='phasorDiagramTbl' style='display:none; width:100%;'>");
            html.append("<tr>");
            html.append("<td height='auto' style='width:49%; vertical-align:middle; text-align:center;' id='phasorDiagram'></td>");
            html.append("<td height='auto' style='width:49%; vertical-align:middle;'><span id='angleGrid'></span></td>");
            html.append("</tr>");
            html.append("</table>");
            html.append("<input type='hidden' id='isMx2' value='true' style='display:none;'/>");
            html.append("<input type='hidden' id='volAng_a' value='").append(StringUtil.nullToBlank(volAng_a)).append("' style='display:none;'/>");
            html.append("<input type='hidden' id='volAng_b' value='").append(StringUtil.nullToBlank(volAng_b)).append("' style='display:none;'/>");
            html.append("<input type='hidden' id='volAng_c' value='").append(StringUtil.nullToBlank(volAng_c)).append("' style='display:none;'/>");
            html.append("<input type='hidden' id='curAng_a' value='").append(StringUtil.nullToBlank(curAng_a)).append("' style='display:none;'/>");
            html.append("<input type='hidden' id='curAng_b' value='").append(StringUtil.nullToBlank(curAng_b)).append("' style='display:none;'/>");
            html.append("<input type='hidden' id='curAng_c' value='").append(StringUtil.nullToBlank(curAng_c)).append("' style='display:none;'/>");
        }

        html.append("</html>");
        return html.toString();
    }    
    
    
    public static String getOndemandDetailString(MeterData.Map data) {
        StringBuffer html = new StringBuffer();

        List<MeterData.Map.Entry> iter = data.getEntry();
        MeterData.Map.Entry entry;
        Object value;
        Object key;

        boolean isMx2 = false;
        String volAng_a = null;
        String volAng_b = null;
        String volAng_c = null;

        String curAng_a = null;
        String curAng_b = null;
        String curAng_c = null;

        for (int cnt = 0; cnt < iter.size(); cnt++) {
            html.append("\n");

            entry = iter.get(cnt);
            key = entry.getKey();
            if (!"Status".equals(key) && !"LpValue".equals(key) && !"rowMap".equals(key)) {
                try{
                    value = entry.getValue();
                }catch(Exception e){
                    value = "";
                }

                html.append(key).append(" ").append(value).append("\n");

                if ("Model".equals(key) && "Mitsubishi-MX2".equals((String)value)) {
                    isMx2 = true;
                } else if ("Voltage Angle(A)".equals(key)) {
                    volAng_a = (String)value;
                } else if ("Voltage Angle(B)".equals(key)) {
                    volAng_b = (String)value;
                } else if ("Voltage Angle(C)".equals(key)) {
                    volAng_c = (String)value;
                } else if ("Current Angle(A)".equals(key)) {
                    curAng_a = (String)value;
                } else if ("Current Angle(B)".equals(key)) {
                    curAng_b = (String)value;
                } else if ("Current Angle(C)".equals(key)) {
                    curAng_c = (String)value;
                }
            }
        }

        html.append("\n");

        if (isMx2 && (!StringUtil.nullToBlank(volAng_a).isEmpty() || !StringUtil.nullToBlank(volAng_b).isEmpty() || !StringUtil.nullToBlank(volAng_c).isEmpty() 
                || !StringUtil.nullToBlank(curAng_a).isEmpty() || !StringUtil.nullToBlank(curAng_b).isEmpty() || !StringUtil.nullToBlank(curAng_c).isEmpty())) {

            html.append(StringUtil.nullToBlank(volAng_a)).append("\n");
            html.append(StringUtil.nullToBlank(volAng_b)).append("\n");
            html.append(StringUtil.nullToBlank(volAng_c)).append("\n");
            html.append(StringUtil.nullToBlank(curAng_a)).append("\n");
            html.append(StringUtil.nullToBlank(curAng_b)).append("\n");
            html.append(StringUtil.nullToBlank(curAng_c)).append("\n");
        }

        html.append("\n");
        return html.toString();
    }
    
    /**
     * SP-280
     * @param md
     * @param parser
     * @throws Exception
     */
    public void saveMeteringDataWithChannel(IMeasurementData md, DLMSKaifa parser) throws Exception {
		try {
			Double[] channels = parser.getMeteringDataChannelData();
			if (channels == null || channels.length == 0) {
			    log.debug("MDevId[" + parser.getMDevId() + "] MeteringDataChannelData is inull or size is 0!!");
			    saveMeteringDataSP(
						parser.getMeteringTime().substring(0,8),
						parser.getMeteringTime().substring(8, 14),
						parser.getMeter()
		                );
			}
			else {
				StringBuffer sb = new StringBuffer();
				for ( int i = 0; i < channels.length; i++ ){
					sb.append("MDevId[" + parser.getMDevId() + "] [" + i +":" + channels[i] + "]");
				}
				log.debug("Date:" +  parser.getMeteringTime().substring(0,8) + ", Time:" +parser.getMeteringTime().substring(8,14) +
						",Value:" + channels[DLMSKaifa.CHANNEL_IDX.CUMULATIVE_ACTIVEENERGY_IMPORT.getIndex() - 1] +
						",mdsId:" + parser.getMeter().getMdsId() +
						",deviceType:" +  parser.getDeviceType() +
						",deviceId:" + parser.getDeviceId()+
						",mdevTypeType:" + parser.getMDevType() +
						",mdevId:" + parser.getMDevId() +
						",meterType:" + parser.getMeterTime() +
						",channels:" + sb.toString());
							 
				saveMeteringDataWithMultiChannel(MeteringType.Normal, 
						parser.getMeteringTime().substring(0,8),
						parser.getMeteringTime().substring(8, 14),
		                channels[DLMSKaifa.CHANNEL_IDX.CUMULATIVE_ACTIVEENERGY_IMPORT.getIndex() - 1],
		                parser.getMeter(),
		                parser.getDeviceType(), 
		                parser.getDeviceId(),
		                parser.getMDevType(),
		                parser.getMDevId(),
		                parser.getMeterTime(),
		                channels);
			}
		}finally {
			log.debug("saveMeteringDataWithChannel finish");
		}
    }
    
	/**
	 * SP-736
	 * @param meter
	 * @param devId
	 * @param devType
	 * @param mdevId
	 * @param mdevType
	 * @param mbusLplist
	 * @throws Exception
	 */
	private void saveMbusDataChannelUsingLPTime(Meter meter, String devId, DeviceType devType, String mdevId,
	        DeviceType mdevType, LPData[] mbusLplist) throws Exception {
		try {
			log.debug("saveMbusDataChannelUsingLPTime devId[" + devId + 
					"] devType[" + devType.name() + "] mdevId[" + mdevId + 
					"] mdevType[" + mdevType.name() + "]" );
			
	        if(mbusLplist == null){
	            log.debug("MDevId[" + mdevId + "] LPSIZE => 0");
	        }
	        else
	        {
	            log.debug("MDevId[" + mdevId + "] LPSIZE => "+ mbusLplist.length);
	            String yyyymmdd = mbusLplist[0].getDatetime().substring(0, 8);
	            String hhmm = mbusLplist[0].getDatetime().substring(8, 12);
	    		String[] timelist = new String[mbusLplist.length];

	            double[][] lpValues = new double[mbusLplist[0].getCh().length][mbusLplist.length];
	            int[] flaglist = new int[mbusLplist.length];
	            
	            for (int ch = 0; ch < lpValues.length; ch++) {
	                for (int lpcnt = 0; lpcnt < lpValues[ch].length; lpcnt++) {
	                    lpValues[ch][lpcnt] = mbusLplist[lpcnt].getCh()[ch];
	                    log.debug("MDevId[" + mdevId + "] lpValues[" + ch + "][" + lpcnt + "] = " + lpValues[ch][lpcnt]);
	                }
	            }
	            
	            for (int i = 0; i < flaglist.length; i++) {
	                flaglist[i] = mbusLplist[i].getFlag();
	    			timelist[i] = mbusLplist[i].getDatetime();

	                log.debug("MDevId[" + mdevId + "] flaglist[" + i + "] = " + flaglist[i]);
	            }
	            double[] _baseValue = new double[lpValues.length];
	            _baseValue[0] = mbusLplist[0].getLpValue();;
	            for (int i = 1; i < lpValues.length; i++) {
	                _baseValue[i] = 0;
	            }	
	            saveLPDataUsingLPTime(MeteringType.Normal, timelist,lpValues, flaglist, _baseValue, 
	            		meter, devType, devId, mdevType, mdevId);
	        }
		}finally {
			log.debug("MDevId[" + mdevId + "] saveMbusDataChannelUsingLPTime finish");
		}
	}
}
