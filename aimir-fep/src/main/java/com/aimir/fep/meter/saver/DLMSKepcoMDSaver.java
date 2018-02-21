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

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.ElectricityChannel;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.PowerAlarmLogData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.DLMSKepco;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.BILLING_ENERGY_PROFILE;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.KEPCO_CURRENT_MAX_DEMAND;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.KEPCO_PREVIOUS_MAX_DEMAND;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.EVENT_LOG;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.MONTHLY_DEMAND_PROFILE;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.MONTHLY_ENERGY_PROFILE;
import com.aimir.model.mvm.LpEM;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;

@Service
public class DLMSKepcoMDSaver extends AbstractMDSaver {
	private static Log log = LogFactory.getLog(DLMSKepcoMDSaver.class);

	private String meterDate = "";
	@Override
	protected boolean save(IMeasurementData md) throws Exception {
		DLMSKepco parser = (DLMSKepco) md.getMeterDataParser();		
		
		if(parser.getMeterType() != null && !"".equals(parser.getMeterType())){
			updateMeterModel(parser.getMeter(), parser.getMeterType());
		}
		saveLp(md,parser);
		savePreBill(parser);
		saveCurrBill(parser);
		saveEventLog(parser);
		saveCurrBill2(parser);
		savePreBill2(parser);
		saveEtypeBilling(parser);
		return true;
	}
	
	public void saveLp(IMeasurementData md, DLMSKepco parser) throws Exception {

		LPData[] lplist = parser.getLPData();
		if (lplist == null || lplist.length == 0) {
		    log.warn("LP size is 0!!");
		    return;
		}
		
		meterDate = parser.getMeterTime();
		if(meterDate == null || "".equals(meterDate)){
			if(lplist[0].getDatetime() != null && !"".equals(lplist[0].getDatetime())){
				meterDate = lplist[0].getDatetime().substring(0, 10)+"0000";
			}else{
				meterDate = parser.getMeteringTime();
			}
		}
		// log.debug("active pulse constant:" +
		// parser.getActivePulseConstant());
		// log.debug("currentDemand:" + currentDemand);

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

        inityyyymmddhh =dateFormatter.format(cal.getTime());

        saveMeteringData(MeteringType.Normal, md.getTimeStamp().substring(0,8),
                md.getTimeStamp().substring(8, 14), current,
                parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
                parser.getMDevType(), parser.getMDevId(), meterDate);
		
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
		if (lplist == null || lplist.length < 2) {
			log.debug("LPSIZE => 0");
		} else {
			
            log.info("lplist[0]:"+lplist[1]);
            log.info("lplist[0].getDatetime():"+lplist[1].getDatetime());
			
            String startlpdate = lplist[1].getDatetime();
            String lpdatetime = startlpdate;
            //Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            cal.setTime(sdf.parse(startlpdate));
            List<Double>[] chValue = new ArrayList[lplist[1].getCh().length];
            List<Integer> flag = new ArrayList<Integer>();
            double baseValue = 0d;
	        
            for (int i = 1; i < lplist.length; i++) {
                if (!lpdatetime.equals(lplist[i].getDatetime())) {
                    saveLPData(chValue, flag, startlpdate, baseValue, parser);
	        		
                    startlpdate = lplist[i].getDatetime();
                    lpdatetime = startlpdate;
                    baseValue = lplist[i].getLpValue();
                    flag = new ArrayList<Integer>();
                    chValue = new ArrayList[lplist[i].getCh().length];
                }
                flag.add(lplist[i].getFlag());
        		
                for (int ch = 0; ch < chValue.length; ch++) {
                    if (chValue[ch] == null) chValue[ch] = new ArrayList<Double>();
        			
                    if (ch+1 <= lplist[i].getCh().length)
                        chValue[ch].add(lplist[i].getCh()[ch]);
                    else
                        chValue[ch].add(0.0);
                }
                cal.add(Calendar.MINUTE, parser.getMeter().getLpInterval());
                lpdatetime = sdf.format(cal.getTime());
            }
            saveLPData(chValue, flag, startlpdate, baseValue, parser);
		}
	}
	
    public void saveLPData(List<Double>[] chValue, List<Integer> flag, String startlpdate, double baseValue, DLMSKepco parser)
            throws Exception {
        double[][] _lplist = new double[chValue.length][chValue[0].size()];
        for (int ch = 0; ch < _lplist.length; ch++) {
            for (int j = 0; j < _lplist[ch].length; j++) {
                if (chValue[ch].get(j) != null)
                    _lplist[ch][j] = chValue[ch].get(j);
                else
                    _lplist[ch][j] = 0.0;
            }
        }
        int[] _flag = new int[chValue[0].size()];
        for (int j = 0; j < _flag.length; j++) {
            _flag[j] = flag.get(j);
        }
        super.saveLPData(MeteringType.Normal, startlpdate.substring(0, 8), startlpdate.substring(8)+"00",
                _lplist, _flag, baseValue, parser.getMeter(),
                DeviceType.Modem, parser.getMeter().getModem().getDeviceSerial(),
                DeviceType.Meter, parser.getMeter().getMdsId());
    }
	
	private void saveEventLog(DLMSKepco parser) {
	    try {
    	    Map<String, Object> pf = parser.getEventLog();
    	    
    	    if(pf == null || pf.size() < 1){
    	    	return;
    	    }
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
	
	
	private void saveEtypeBilling(DLMSKepco parser){
		BillingData bill = null;
		bill= parser.getETypeBillingData();
		if(bill == null){
			return;
		}
        saveMonthlyBilling(bill, parser.getMeter(), null, null, parser.getMDevType(), parser.getMDevId());
	}
	
	private void savePreBill(DLMSKepco parser) {
	    Double value = 0.0;
        
        // 빌링 정보 저장
        Map<String, Object> prebill = parser.getPreviousMaxDemand();
        BillingData bill = new BillingData();
        
        if(prebill == null || prebill.size() < 1){
        	return;
        }
        
        // 순방향 유효전력량 [Q1+Q4]
        value = ((Number)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T1PreviousActive.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActiveEnergyRate1(value);
        
        value = ((Number)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T2PreviousActive.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActiveEnergyRate2(value);
        
        value = ((Number)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T3PreviousActive.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActiveEnergyRate3(value);
        // 지상 무효전력량 [Q1]
        value = ((Number)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T1PreviousReActive.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();
        bill.setReactiveEnergyRate1(value);
        
        value = ((Number)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T2PreviousReActive.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();
        bill.setReactiveEnergyRate2(value);
        
        value = ((Number)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T3PreviousReActive.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();
        bill.setReactiveEnergyRate3(value);
        
        // 순방향 최대 유효전력 [Q1 + Q4]
        value = ((Number)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T1PreviousActiveMax.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActivePowerMaxDemandRate1(value);
        
        value = ((Number)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T2PreviousActiveMax.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActivePowerMaxDemandRate2(value);
        
        value = ((Number)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T3PreviousActiveMax.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActivePowerMaxDemandRate3(value);
        
        // 누적 순방향 유효전력 [Q1 + Q4]
        value = ((Number)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T1PreviousActiveSum.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setCumulativeActivePowerDemandRate1(value);
        
        value = ((Number)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T2PreviousActiveSum.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setCumulativeActivePowerDemandRate2(value);
        
        value = ((Number)prebill.get(KEPCO_PREVIOUS_MAX_DEMAND.T3PreviousActiveSum.name())).doubleValue();
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
        
        bill.setBillingTimestamp(meterDate.substring(0,6)+"01");
        
        saveMonthlyBilling(bill, parser.getMeter(), null, null, parser.getMDevType(), parser.getMDevId());
	}
	
	private void saveCurrBill(DLMSKepco parser) {
        Double value = 0.0;
        
        // 빌링 정보 저장
        Map<String, Object> currbill = parser.getCurrentMaxDemand();
        BillingData bill = new BillingData();
        
        if(currbill == null || currbill.size() < 1){
        	return;
        }
        
        // 순방향 유효전력량 [Q1+Q4]
        value = ((Number)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T1CurrentActive.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActiveEnergyRate1(value);
        
        value = ((Number)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T2CurrentActive.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActiveEnergyRate2(value);
        
        value = ((Number)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T3CurrentActive.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActiveEnergyRate3(value);
        // 지상 무효전력량 [Q1]
        value = ((Number)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T1CurrentReActive.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();
        bill.setReactiveEnergyRate1(value);
        
        value = ((Number)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T2CurrentReActive.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();
        bill.setReactiveEnergyRate2(value);
        
        value = ((Number)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T3CurrentReActive.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();
        bill.setReactiveEnergyRate3(value);
        
        // 순방향 최대 유효전력 [Q1 + Q4]
        value = ((Number)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T1CurrentActiveMax.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActivePowerMaxDemandRate1(value);
        
        value = ((Number)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T2CurrentActiveMax.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActivePowerMaxDemandRate2(value);
        
        value = ((Number)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T3CurrentActiveMax.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActivePowerMaxDemandRate3(value);
        
        // 누적 순방향 유효전력 [Q1 + Q4]
        value = ((Number)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T1CurrentActiveSum.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setCumulativeActivePowerDemandRate1(value);
        
        value = ((Number)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T2CurrentActiveSum.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setCumulativeActivePowerDemandRate2(value);
        
        value = ((Number)currbill.get(KEPCO_CURRENT_MAX_DEMAND.T3CurrentActiveSum.name())).doubleValue();
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
        
        bill.setBillingTimestamp(meterDate);
        saveCurrentBilling(bill, parser.getMeter(), null, null, parser.getMDevType(), parser.getMDevId());
    }
	
	private void savePreBill2(DLMSKepco parser) {
	    Double value = 0.0;
        
        // 빌링 정보 저장
        Map<String, Object> prebillMaxDemand = parser.getBillingMaxDemand();
        Map<String, Object> prebillEnergy = parser.getBillingEnergy();
        BillingData bill = new BillingData();
        
        if(prebillMaxDemand == null || prebillMaxDemand.size() < 1){
        	return;
        }
        if(prebillEnergy == null || prebillEnergy.size() < 1){
        	return;
        }        
        
        value = ((Number)prebillEnergy.get(BILLING_ENERGY_PROFILE.Active.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActiveEnergyRateTotal(value);        
        
        value = ((Number)prebillEnergy.get(BILLING_ENERGY_PROFILE.T1Active.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActiveEnergyRate1(value);
        
        value = ((Number)prebillEnergy.get(BILLING_ENERGY_PROFILE.T2Active.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActiveEnergyRate2(value);
        
        value = ((Number)prebillEnergy.get(BILLING_ENERGY_PROFILE.T3Active.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActiveEnergyRate3(value);
        
        value = ((Number)prebillEnergy.get(BILLING_ENERGY_PROFILE.ReActive.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();
        bill.setReactiveEnergyRateTotal(value);        
        
        value = ((Number)prebillEnergy.get(BILLING_ENERGY_PROFILE.T1ReActive.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();
        bill.setReactiveEnergyRate1(value);

        value = ((Number)prebillEnergy.get(BILLING_ENERGY_PROFILE.T2ReActive.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();
        bill.setReactiveEnergyRate2(value);
        
        value = ((Number)prebillEnergy.get(BILLING_ENERGY_PROFILE.T3ReActive.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();
        bill.setReactiveEnergyRate3(value);        

        value = ((Number)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.Active.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActivePowerMaxDemandRateTotal(value);       
        
        bill.setActivePowerDemandMaxTimeRateTotal(((String)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.ActiveDate.name())).substring(6));
        
        value = ((Number)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.ActiveSum.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();        
        bill.setCumulativeActivePowerDemandRateTotal(value);
        
        value = ((Number)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.T1Active.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActivePowerMaxDemandRate1(value);       
        
        bill.setActivePowerDemandMaxTimeRate1(((String)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.T1ActiveDate.name())).substring(6));
        
        value = ((Number)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.T1ActiveSum.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();        
        bill.setCumulativeActivePowerDemandRate1(value);
        
        value = ((Number)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.T2Active.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActivePowerMaxDemandRate2(value);       
        
        bill.setActivePowerDemandMaxTimeRate2(((String)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.T2ActiveDate.name())).substring(6));
        
        value = ((Number)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.T2ActiveSum.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();        
        bill.setCumulativeActivePowerDemandRate2(value);        
        
        value = ((Number)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.T3Active.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();
        bill.setActivePowerMaxDemandRate3(value);       
        
        bill.setActivePowerDemandMaxTimeRate3(((String)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.T3ActiveDate.name())).substring(6));
        
        value = ((Number)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.T3ActiveSum.name())).doubleValue();
        value /= parser.getActivePulseConstant();
        value *= parser.getCt();        
        bill.setCumulativeActivePowerDemandRate3(value);       

        value = ((Number)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.ReActive.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();
        bill.setReactivePowerMaxDemandRateTotal(value);       
        
        bill.setReactivePowerDemandMaxTimeRateTotal(((String)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.ReActiveDate.name())).substring(6));
        
        value = ((Number)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.ReActiveSum.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();        
        bill.setCumulativeReactivePowerDemandRateTotal(value);

        value = ((Number)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.T1ReActive.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();
        bill.setReactivePowerMaxDemandRate1(value);       
        
        bill.setReactivePowerDemandMaxTimeRate1(((String)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.T1ReActiveDate.name())).substring(6));
        
        value = ((Number)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.T1ReActiveSum.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();        
        bill.setCumulativeReactivePowerDemandRate1(value);
        
        value = ((Number)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.T2ReActive.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();
        bill.setReactivePowerMaxDemandRate2(value);       
        
        bill.setReactivePowerDemandMaxTimeRate2(((String)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.T2ReActiveDate.name())).substring(6));
        
        value = ((Number)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.T2ReActiveSum.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();        
        bill.setCumulativeReactivePowerDemandRate2(value);        
        
        value = ((Number)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.T3ReActive.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();
        bill.setReactivePowerMaxDemandRate3(value);       
        
        bill.setReactivePowerDemandMaxTimeRate3(((String)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.T3ReActiveDate.name())).substring(6));
        
        value = ((Number)prebillMaxDemand.get(MONTHLY_DEMAND_PROFILE.T3ReActiveSum.name())).doubleValue();
        value /= parser.getReActivePulseConstant();
        value *= parser.getCt();        
        bill.setCumulativeReactivePowerDemandRate3(value);
        
        bill.setBillingTimestamp(meterDate.substring(0,6)+"01");        
        saveMonthlyBilling(bill, parser.getMeter(), null, null, parser.getMDevType(), parser.getMDevId());
	}
	
	private void saveCurrBill2(DLMSKepco parser) {
        Double value = 0.0;        
        // 빌링 정보 저장
        Map<String, Object> map = parser.getMonthlyEnergyProfile();
        BillingData bill = new BillingData();
        
        if(map == null || map.size() < 1){
        	return;
        }        
        
        try {
            value = ((Number) map
                    .get(MONTHLY_ENERGY_PROFILE.Active.name())).doubleValue();
            value /= parser.getActivePulseConstant();
            value *= parser.getCt();
            bill.setActiveEnergyRateTotal(value);
            
            value = ((Number) map
                    .get(MONTHLY_ENERGY_PROFILE.ReActive.name())).doubleValue();
            value /= parser.getReActivePulseConstant();
            value *= parser.getCt();    
            bill.setReactiveEnergyRateTotal(value);
            
            value = ((Number) map
                    .get(MONTHLY_ENERGY_PROFILE.T1Active.name())).doubleValue();
            value /= parser.getActivePulseConstant();
            value *= parser.getCt();
            bill.setActiveEnergyRate1(value);
            
            value = ((Number) map
                    .get(MONTHLY_ENERGY_PROFILE.T1ReActive.name())).doubleValue();
            value /= parser.getReActivePulseConstant();
            value *= parser.getCt();
            bill.setReactiveEnergyRate1(value);
            
            value = ((Number) map
                    .get(MONTHLY_ENERGY_PROFILE.T2Active.name())).doubleValue();
            value /= parser.getActivePulseConstant();
            value *= parser.getCt();
            bill.setActiveEnergyRate2(value);
            
            value = ((Number) map
                    .get(MONTHLY_ENERGY_PROFILE.T2ReActive.name())).doubleValue();
            value /= parser.getReActivePulseConstant();
            value *= parser.getCt();
            bill.setReactiveEnergyRate2(value);
            
            value = ((Number) map
                    .get(MONTHLY_ENERGY_PROFILE.T3Active.name())).doubleValue();
            value /= parser.getActivePulseConstant();
            value *= parser.getCt();
            bill.setActiveEnergyRate3(value);
            
            value = ((Number) map
                    .get(MONTHLY_ENERGY_PROFILE.T3ReActive.name())).doubleValue();
            value /= parser.getReActivePulseConstant();
            value *= parser.getCt();
            bill.setReactiveEnergyRate3(value);

            bill.setBillingTimestamp(meterDate);
            saveCurrentBilling(bill, parser.getMeter(), null, null, parser.getMDevType(), parser.getMDevId());
            
        } catch (Exception e) {
            log.error(e);
        }        
	}

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
	
	private boolean lpSave(IMeasurementData md, LPData[] validlplist,
			DLMSKepco parser, double base,double addBasePulse) throws Exception {
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
						.inDST(null, yyyymmddhh+"0000") }, null,
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
							new Object[] { DateTimeUtil.inDST(null, yyyymmddhh+"0000") }, null, Restriction.EQ));

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
					basePulse = subLpEM.get(0).getValue() == null ? 0d : subLpEM.get(0).getValue()
							+retValue("00",subLpEM.get(0).getValue_00(),subLpEM.get(0).getValue_15(),subLpEM.get(0).getValue_30(),subLpEM.get(0).getValue_45());
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

		parser.getMeter().setLpInterval(parser.getInterval());
		// TODO Flag, PF 처리해야 함.
		saveLPData(MeteringType.Normal, yyyymmdd, hhmm, lpValues, flaglist,
				basePulse, parser.getMeter(), parser.getDeviceType(),
				parser.getDeviceId(), parser.getMDevType(), parser
						.getMDevId());
		return true;
	}
}
