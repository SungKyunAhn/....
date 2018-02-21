package com.aimir.fep.meter.saver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.ElectricityChannel;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.command.conf.DLMSMeta.LOAD_CONTROL_STATUS;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.command.mbean.CommandGW.OnDemandOption;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.PowerAlarmLogData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.DLMSNamjun;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.EVENT_LOG;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.LpEM;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;

@Service
public class DLMSNamjunSaver extends AbstractMDSaver {
	private static Log log = LogFactory.getLog(DLMSNamjunSaver.class);

	@Override
	protected boolean save(IMeasurementData md) throws Exception {
		DLMSNamjun parser = (DLMSNamjun) md.getMeterDataParser();		
		
		if(parser.getMeterModel() != null && !"".equals(parser.getMeterModel())){
			updateMeterModel(parser.getMeter(), parser.getMeterModel());
		}
		
		String modemTime = parser.getMeteringTime();
		Double val = parser.getLQISNRValue();
		if(val != null && modemTime != null && !"".equals(modemTime) && modemTime.length() == 14){
			saveSNRLog(parser.getDeviceId(), modemTime.substring(0,8), modemTime.substring(8,14), parser.getMeter().getModem(), val);
		}


		saveLp(md, parser);
		savePreBill(parser);
		saveCurrBill(parser);
		saveEventLog(parser);
		savePowerQuality(parser);
		
		return true;
	}
	
	private void saveLp(IMeasurementData md, DLMSNamjun parser) throws Exception {

		LPData[] lplist = parser.getLPData();
		if (lplist == null || lplist.length == 0) {
		    log.warn("LP size is 0!!");
		    return ;
		}
		
		String meterDate = parser.getMeterTime();
		double lpSum = 0;
		double basePulse = 0;
		double current = parser.getMeteringValue() == null ? 0d : parser.getMeteringValue();
		
		double addBasePulse = 0;
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
        log.debug(parser.getMeterTime());

        saveMeteringData(MeteringType.Normal, md.getTimeStamp().substring(0,8),
                md.getTimeStamp().substring(8, 14), parser.getMeteringValue(),
                parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
                parser.getMDevType(), parser.getMDevId(), parser.getMeterTime());
		
		for (int i = 0; i < lplist.length; i++) {
			
			lpSum += lplist[i].getLpValue();
			
			String yyyymmddhh = lplist[i].getDatetime().substring(0, 10);
			if(inityyyymmddhh.equals(yyyymmddhh)){
				addBasePulse+= lplist[i].getLpValue();
			}
			 log.debug("time=" + lplist[i].getDatetime() + ":lp="
			 + lplist[i].getLp() + ":lpValue=" + lplist[i].getLpValue()+":addBasePulse="+addBasePulse);

		}

		basePulse = current - lpSum;
		// log.debug("lpSum:"+lpSum);
		// log.debug("basePulse:"+basePulse);
		if (lplist == null) {
			log.debug("LPSIZE => 0");
		} else {
			// log.debug("##########LPSIZE => "+lplist.length);
			lpSave(md, lplist, parser, basePulse, addBasePulse);
		}
	}
	
	private void saveEventLog(DLMSNamjun parser) {
	    try {
    	    Map<String, Object> pf = parser.getEventLog();
    	    String key = null;
    	    List<EventLogData> events = new ArrayList<EventLogData>();
    	    List<PowerAlarmLogData> powerDowns = new ArrayList<PowerAlarmLogData>();
    	    List<PowerAlarmLogData> powerUps = new ArrayList<PowerAlarmLogData>();
    	    for (Iterator<String> i = pf.keySet().iterator(); i.hasNext(); ) {
    	        key = i.next();
    	        for (EVENT_LOG el : EVENT_LOG.values()) {
        	        if (key.contains(el.name())) {
        	            log.debug(el.name() + "[key=" + key + ", value=" + pf.get(key)+"]");
        	            EventLogData e = new EventLogData();
        	            e.setDate(((String)pf.get(key)).substring(6,14)); // :date= 제거
        	            e.setTime(((String)pf.get(key)).substring(14)+"00");
        	            e.setFlag(el.getFlag());
        	            e.setKind("STE");
        	            e.setMsg(el.getMsg());
        	            events.add(e);
        	            
        	            // 정복전 이벤트만 식별한다.
        	            if (key.contains(EVENT_LOG.PowerFailure.name())) {
        	                PowerAlarmLogData p = new PowerAlarmLogData();
        	                p.setDate(e.getDate());
        	                p.setTime(e.getTime());
        	                p.setFlag(e.getFlag());
        	                p.setKind(e.getKind());
        	                p.setMsg(e.getMsg());
        	                
        	                powerDowns.add(p);
        	            }
        	            else if (key.contains(EVENT_LOG.PowerRestore.name())) {
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

    	    // Power Down과 Up 시간을 비교하여 Down시간이 작으면 Down 시간을 Up의 DateTime에 설정한다.
    	    // 최근것부터 적용한다.
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
	        log.warn(e);
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
	
	private void saveCurrBill(DLMSNamjun parser) {
		
		try{
	        BillingData bill = parser.getCurrBill();
	        if(bill != null){
	            bill.setBillingTimestamp(parser.getMeterTime());
	            saveCurrentBilling(bill, parser.getMeter(), null, null, parser.getMDevType(), parser.getMDevId());
	        }
		}catch(Exception e){
			log.error(e,e);
		}
	}
	
	
	private void savePowerQuality(DLMSNamjun parser){
		try{
			
	        Instrument[] instrument = parser.getPowerQuality();
	        savePowerQuality(parser.getMeter(), parser.getMeteringTime(), instrument, parser.getDeviceType(),
	                parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
		}catch(Exception e){
			log.error(e,e);
		}
	}
	
	private void savePreBill(DLMSNamjun parser) {
		
		try{
	        BillingData bill = parser.getPreviousMonthBill();
	        if(bill != null){
	            bill.setBillingTimestamp(parser.getMeterTime().substring(0,6)+"01"+"000000");
	            saveMonthlyBilling(bill, parser.getMeter(), null, null, parser.getMDevType(), parser.getMDevId());
	        }
		}catch(Exception e){
			log.error(e,e);
		}
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
	
	private boolean lpSave(IMeasurementData md, LPData[] validlplist,
			DLMSNamjun parser, double base,double addBasePulse) throws Exception {
		LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
		String yyyymmdd = validlplist[0].getDatetime().substring(0, 8);
		String yyyymmddhh = validlplist[0].getDatetime().substring(0, 10);
		String hhmm = validlplist[0].getDatetime().substring(8, 12);
		String mm = validlplist[0].getDatetime().substring(10, 12);

		condition.add(new Condition("id.mdevType", new Object[] { parser
				.getMDevType() }, null, Restriction.EQ));
		condition.add(new Condition("id.mdevId", new Object[] { parser
				.getMDevId() }, null, Restriction.EQ));
		// log.debug("parser.getMDevType():"+parser.getMDevType()+":parser.getMDevId():"+parser.getMDevId()+":dst:"+DateTimeUtil
		// .inDST(null, parser.getMeterDate())+":yyyymmddhh:"+yyyymmddhh );
		condition
				.add(new Condition("id.dst", new Object[] { DateTimeUtil
						.inDST(null, parser.getMeterTime()) }, null,
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
					// log.debug("parser.getMDevType():"+parser.getMDevType()+":parser.getMDevId():"+parser.getMDevId()+":dst:"+DateTimeUtil
					// .inDST(null,
					// parser.getMeterDate())+":yyyymmddhh:"+yyyymmddhh );
				condition2.add(new Condition("id.dst",
							new Object[] { DateTimeUtil.inDST(null, parser
									.getMeterTime()) }, null, Restriction.EQ));

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
	
    @Override
    public String relayValveOn(String mcuId, String meterId) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            resultMap = commandGw.cmdOnDemandMeter( mcuId, meterId, OnDemandOption.WRITE_OPTION_RELAYON.getCode());
            
            
            if (resultMap != null && resultMap.get("LoadControlStatus") != null) {
                LOAD_CONTROL_STATUS ctrlStatus =  (LOAD_CONTROL_STATUS)resultMap.get("LoadControlStatus");
                
                if (ctrlStatus == LOAD_CONTROL_STATUS.CLOSE) {
                    updateMeterStatusNormal(meter);
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
            
            resultMap = commandGw.cmdOnDemandMeter( mcuId, meterId, OnDemandOption.WRITE_OPTION_RELAYOFF.getCode());
            
            
            if (resultMap != null && resultMap.get("LoadControlStatus") != null) {
                LOAD_CONTROL_STATUS ctrlStatus =  (LOAD_CONTROL_STATUS)resultMap.get("LoadControlStatus");
                
                if (ctrlStatus == LOAD_CONTROL_STATUS.OPEN) {
                    updateMeterStatusCutOff(meter);
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
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            int nOption = OnDemandOption.READ_OPTION_RELAY.getCode(); //read table
            resultMap = commandGw.cmdOnDemandMeter( mcuId, meterId, nOption);
            
            if (resultMap != null) {
                if ((LOAD_CONTROL_STATUS)resultMap.get("LoadControlStatus") == LOAD_CONTROL_STATUS.CLOSE) {
                    updateMeterStatusNormal(meter);
                }
                else if ((LOAD_CONTROL_STATUS)resultMap.get("LoadControlStatus") == LOAD_CONTROL_STATUS.OPEN) {
                    updateMeterStatusCutOff(meter); 
                }
            }
        }
        catch (Exception e) {
            log.error(e, e);
            resultMap.put("failReason", e.getMessage());
        }
        
        return MapToJSON(resultMap);
    }
	
    @Override
    public String syncTime(String mcuId, String meterId) {
        Map<String, String> resultMap = new HashMap<String, String>();
        int result = 0;
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            resultMap = commandGw.cmdOnDemandMeter( mcuId, meterId, OnDemandOption.WRITE_OPTION_TIMESYNC.getCode());
            
            if(resultMap != null) {

                String after = (String) resultMap.get("afterTime");
                String diff = "0";
                resultMap.put("diff", diff);
                
                //saveMeterTimeSyncLog(meter, "", after, result);
            }
        }
        catch (Exception e) {
            result = 1;
            resultMap.put("failReason", e.getMessage());
        }
        return MapToJSON(resultMap);
    }
}
