package com.aimir.fep.meter.saver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ElectricityChannel;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.dao.mvm.RealTimeBillingEMDao;
import com.aimir.fep.command.conf.DLMSMeta.LOAD_CONTROL_STATUS;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.command.mbean.CommandGW.OnDemandOption;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.DLMSLSSmartMeterForEVN;
import com.aimir.fep.meter.parser.DLMSEVNTable.DLMSVARIABLE.EVENT_LOG;
import com.aimir.fep.meter.parser.DLMSEVNTable.DLMSVARIABLE.LP_STATUS;
import com.aimir.fep.meter.parser.DLMSEVNTable.DLMSVARIABLE.MEASUREMENT_STATUS_ALARM;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.LpEM;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

@Service
public class DLMSLSSmartMeterSaverForEVN extends AbstractMDSaver {
	private static Log log = LogFactory.getLog(DLMSLSSmartMeterSaverForEVN.class);

	@Autowired
	RealTimeBillingEMDao billingEmDao;

	@Override
	protected boolean save(IMeasurementData md) throws Exception {
		DLMSLSSmartMeterForEVN parser = (DLMSLSSmartMeterForEVN) md.getMeterDataParser();
		super.setParser(parser);

		log.info("    ");
		log.info("    ");
		log.info("    ");
		log.info("############################## [" + parser.getMDevId() + "] DLMSLSSmartMeter Saving Start!! ################################################");

		if (parser.getMeterModel() != null && !"".equals(parser.getMeterModel())) {
			updateMeterModel(parser.getMeter(), parser.getMeterModel());
		}

		String modemTime = parser.getMeteringTime();
		Double val = parser.getLQISNRValue();
		if (val != null && modemTime != null && !"".equals(modemTime) && modemTime.length() == 14) {
			saveSNRLog(parser.getDeviceId(), modemTime.substring(0, 8), modemTime.substring(8, 14), parser.getMeter().getModem(), val);
			log.debug("### 1/6 saveSNRLog complete ####");
		}

//		Properties prop = new Properties();
//		try {
//			prop.load(getClass().getClassLoader().getResourceAsStream("config/moe-integration.properties"));
//			boolean isMakeFile = Boolean.parseBoolean(prop.getProperty("integration.makefile", "false"));
//			if (isMakeFile) {
//				makeLpIntegrationData(parser);
//				makeDailyIntegrationData(parser);
//				makeMonthlyIntegrationData(parser);
//				makeEventIntegrationData(parser);
//			}
//		} catch (Exception e) {
//			log.error(e, e);
//		}

		saveLp(md, parser);
		log.debug("### 2/6 saveLp complete ####");
		saveAlarmLog(parser); // FUNCTION_STATUS_ALARM, DEVICE_STATUS_ALARM, MEASUREMENT_STATUS_ALARM
		log.debug("### 3/6 saveAlarmLog complete ####");
		saveEventLog(parser);
		log.debug("### 4/6 saveEventLog complete ####");
		//savePowerQuality(parser);
		//log.debug("### 6/8 savePowerQuality 완료 ####");
		saveDayProfile(parser);
		log.debug("### 5/6 saveDayProfile complete ####");
		saveMonthProfile(parser);
		log.debug("### 6/6 saveMonthProfile complete ####");

		//savePreBill(parser);
		//log.debug("### 3/8 savePreBill 완료 ####");
		//saveCurrBill(parser); // realtime billing em   
		//log.debug("### 7/7 saveCurrBill complete ####");  이거 필요있는지부터 확인!!

		try {
			Meter meter = parser.getMeter();
			String dsttime = DateTimeUtil.getDST(null, md.getTimeStamp());

			if (meter.getLastReadDate() == null || dsttime.compareTo(meter.getLastReadDate()) > 0) {
				meter.setLastReadDate(dsttime);

				String notTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim();
				log.debug("##### [SAVER] 임시 통신시간 저장 체크 [미터=" + meter.getMdsId() + "] [dsttime-" + dsttime + " / 현재시간-" + notTime);
				log.debug("##### [SAVER] 임시 통신시간 저장 체크 [미터=" + meter.getMdsId() + "] [dsttime-" + dsttime + " / 현재시간-" + notTime);
				log.debug("##### [SAVER] 임시 통신시간 저장 체크 [미터=" + meter.getMdsId() + "] [dsttime-" + dsttime + " / 현재시간-" + notTime);

				/**
				 * 이라크의 경우 선불이 없으므로 미터링 데이터 저장시 무조건 미터상태를 Normal로 바꿔준다.
				 */
				Code meterStatus = CommonConstants.getMeterStatusByName(MeterStatus.Normal.name());
				log.debug("METER_OLD_STATUS[" + (meter.getMeterStatus() == null ? "NULL" : meter.getMeterStatus()) + "]");
				meter.setMeterStatus(meterStatus);
				log.debug("METER_CHANGED_STATUS[" + meter.getMeterStatus() + "]");

				meter.setLastMeteringValue(parser.getMeteringValue());

				if (md.getTimeStamp() != null && !"".equals(md.getTimeStamp())) {

					long diff = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(notTime).getTime() - DateTimeUtil.getDateFromYYYYMMDDHHMMSS(md.getTimeStamp()).getTime();
					meter.setTimeDiff(Long.parseLong(String.valueOf(Math.round(diff / 1000 / 60))));

					if (0 < diff) {
						log.debug("### [" + meter.getMdsId() + "] TIME_DIFF 발생!! timestamp={" + md.getTimeStamp() + "}" + ", meterTime={" + notTime + "}, diff={" + diff + "}, set_diff={" + meter.getTimeDiff() + "}분");
					}
				}

				// 수검침과 같이 모뎀과 관련이 없는 경우는 예외로 처리한다.
				if (meter.getModem() != null) {
					meter.getModem().setLastLinkTime(dsttime);

					String tt = meter.getModem().getDeviceSerial();
					log.debug("##### [SAVER]모뎀 마지막 연결시간 저장 체크 [모뎀=" + tt + "] [dsttime-" + dsttime + " / 현재시간-" + DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim());
					log.debug("##### [SAVER]모뎀 마지막 연결시간 저장 체크 [모뎀=" + tt + "] [dsttime-" + dsttime + " / 현재시간-" + DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim());
					log.debug("##### [SAVER]모뎀 마지막 연결시간 저장 체크 [모뎀=" + tt + "] [dsttime-" + dsttime + " / 현재시간-" + DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim());
				}
			}
		} catch (Exception ignore) {
			log.error("Meter/Modem xxxTime save error - " + ignore);
		}

		log.info("############################## [" + parser.getMDevId() + "] DLMSLSSmartMeter Saving End!! ################################################");
		return true;
	}

	private void saveLp(IMeasurementData md, DLMSLSSmartMeterForEVN parser) throws Exception {

		LPData[] lplist = parser.getLPData();
		if (lplist == null || lplist.length == 0) {
			log.warn("LP size is 0!!");
			return;
		}

		double lpSum = 0;
		double basePulse = 0;
		double current = parser.getMeteringValue() == null ? 0d : parser.getMeteringValue();

		double addBasePulse = 0;
		log.debug("lplist[0]:" + lplist[0]);
		log.debug("lplist[0].getDatetime():" + lplist[0].getDatetime());
		String inityyyymmddhh = lplist[0].getDatetime().substring(0, 10);

		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHH");

		Calendar cal = Calendar.getInstance();

		cal.setTime(dateFormatter.parse(inityyyymmddhh));
		cal.add(Calendar.HOUR, 1);

		inityyyymmddhh = dateFormatter.format(cal.getTime());

		log.debug("MD TimeStamp - " + md.getTimeStamp());

		log.debug("MeteringValue - " + parser.getMeteringValue());
		log.debug("MDS ID - " + parser.getMeter().getMdsId());
		log.debug("Meter Time - " + parser.getMeterTime());

		saveMeteringData(MeteringType.Normal, md.getTimeStamp().substring(0, 8), md.getTimeStamp().substring(8, 14), parser.getMeteringValue(), parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId(), parser.getMeterTime());

		for (int i = 0; i < lplist.length; i++) {

			lpSum += lplist[i].getLpValue();

			String yyyymmddhh = lplist[i].getDatetime().substring(0, 10);
			if (inityyyymmddhh.equals(yyyymmddhh)) {
				addBasePulse += lplist[i].getLpValue();
			}
			log.debug("time=" + lplist[i].getDatetime() + ":lp=" + lplist[i].getLp() + ":lpValue=" + lplist[i].getLpValue() + ":addBasePulse=" + addBasePulse);

		}

		basePulse = current - lpSum;
		// log.debug("lpSum:"+lpSum);
		// log.debug("basePulse:"+basePulse);
		// log.debug("##########LPSIZE => "+lplist.length);
		lpSave(md, lplist, parser, basePulse, addBasePulse);
	}

	private void saveAlarmLog(DLMSLSSmartMeterForEVN parser) {
		try {
			List<EventLogData> alarms = parser.getMeterAlarmLog();
			if (alarms == null || alarms.size() == 0) {
				return;
			}
			saveMeterEventLog(parser.getMeter(), alarms.toArray(new EventLogData[0]));
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	private void saveEventLog(DLMSLSSmartMeterForEVN parser) {
		try {
			/*
			 * 미터State 값 적용
			 * 
			 * Load profile status description 
			 * 0번 => Power down 
			 * 1번 => Not used
			 * 2번 => Clock adjusted 
			 * 3번 => Not used 
			 * 4번 => Daylight saving 
			 * 5번 => Data not valid 
			 * 6번 => Clock invalid 
			 * 7번 => Critical error
			 * 
			 */
			Meter meter = parser.getMeter();
			if (meter.getInstallProperty() != null && meter.getInstallProperty().length() != "00000000".length()) {
				meter.setInstallProperty(null);
			}

			LPData[] lpData = parser.getLPData();
			EventLogData e = null;
			for (LPData lpD : lpData) {
				log.debug("#### LP Status Info - [" + lpD.getDatetime() + "][OLD][" + (meter.getInstallProperty() == null ? "null" : meter.getInstallProperty()) 
						+ "] ==> [NEW][" + (lpD.getStatus() == null ? "null" : lpD.getStatus()) + "]");

				if (meter.getInstallProperty() == null || !meter.getInstallProperty().equals(lpD.getStatus())) {
					// 미터 이벤트 상태가 다르면 발생한다.
					List<EventLogData> events = new ArrayList<EventLogData>();
					int prevState = 0;
					int curState = 0;
					if (lpD.getStatus() != null) {
						for (int flag = 0; flag < lpD.getStatus().length(); flag++) {
							if (meter.getInstallProperty() == null)
								prevState = 0;
							else
								prevState = Integer.parseInt(meter.getInstallProperty().substring(flag, flag + 1));

							curState = Integer.parseInt(lpD.getStatus().substring(flag, flag + 1));
							if (prevState == 0 && curState == 1) {
								e = new EventLogData();
								e.setDate(lpD.getDatetime().substring(0, 8)); 
								e.setTime(lpD.getDatetime().substring(8));
								e.setFlag(EVENT_LOG.LPStatus.getFlag());
								e.setKind("STE");
								e.setMsg(EVENT_LOG.LPStatus.getMsg());
								
								switch (flag) {
								case 0: // Bit0 Critical error
									e.setAppend("[ON]" + LP_STATUS.CriticalError.getMsg());
									log.debug("[LP_STATUS][ON][" + LP_STATUS.CriticalError.getMsg() + "] " + e.toString());
									break;
								case 1: // Bit1 Clock invalid
									e.setAppend("[ON]" + LP_STATUS.ClockInvalid.getMsg());
									log.debug("[LP_STATUS][ON][" + LP_STATUS.ClockInvalid.getMsg() + "] " + e.toString());
									break;
								case 2: // Bit2 Data not valid
									e.setAppend("[ON]" + LP_STATUS.DataNotValid.getMsg());
									log.debug("[LP_STATUS][ON][" + LP_STATUS.DataNotValid.getMsg() + "] " + e.toString());
									break;
								case 3: // Bit3 Daylight saving
									e.setAppend("[ON]" + LP_STATUS.DaylightSaving.getMsg());
									log.debug("[LP_STATUS][ON][" + LP_STATUS.DaylightSaving.getMsg() + "] " + e.toString());
									break;
								case 4: // Not used.
									e.setAppend("[ON]" + LP_STATUS.NotUsed04.getMsg());
									log.debug("[LP_STATUS][ON][" + LP_STATUS.NotUsed04.getMsg() + "] " + e.toString());
									break;
								case 5: // Clock adjusted
									e.setAppend("[ON]" + LP_STATUS.ClockAdjusted.getMsg());
									log.debug("[LP_STATUS][ON][" + LP_STATUS.ClockAdjusted.getMsg() + "] " + e.toString());
									break;
								case 6: // Not used
									e.setAppend("[ON]" + LP_STATUS.NotUsed06.getMsg());
									log.debug("[LP_STATUS][ON][" + LP_STATUS.NotUsed06.getMsg() + "] " + e.toString());
									break;
								case 7: // Bit7 Power down
									e.setAppend("[ON]" + LP_STATUS.PowernDown.getMsg());
									log.debug("[LP_STATUS][ON][" + LP_STATUS.PowernDown.getMsg() + "] " + e.toString()); 
									break;
								default:
									break;
								}
								
								events.add(e);
							} else if (prevState == 1 && curState == 0) {
								e = new EventLogData();
								e.setDate(lpD.getDatetime().substring(0, 8)); 
								e.setTime(lpD.getDatetime().substring(8));
								e.setFlag(EVENT_LOG.LPStatus.getFlag());
								e.setKind("STE");
								e.setMsg(EVENT_LOG.LPStatus.getMsg());
								
								switch (flag) {
								case 0: // Bit0 Critical error
									e.setAppend("[OFF]" + LP_STATUS.CriticalError.getMsg());
									log.debug("[LP_STATUS][OFF][" + LP_STATUS.CriticalError.getMsg() + "] " + e.toString());
									break;
								case 1: // Bit1 Clock invalid
									e.setAppend("[OFF]" + LP_STATUS.ClockInvalid.getMsg());
									log.debug("[LP_STATUS][OFF][" + LP_STATUS.ClockInvalid.getMsg() + "] " + e.toString());
									break;
								case 2: // Bit2 Data not valid
									e.setAppend("[OFF]" + LP_STATUS.DataNotValid.getMsg());
									log.debug("[LP_STATUS][OFF][" + LP_STATUS.DataNotValid.getMsg() + "] " + e.toString());
									break;
								case 3: // Bit3 Daylight saving
									e.setAppend("[OFF]" + LP_STATUS.DaylightSaving.getMsg());
									log.debug("[LP_STATUS][OFF][" + LP_STATUS.DaylightSaving.getMsg() + "] " + e.toString());
									break;
								case 4: // Not used.
									e.setAppend("[OFF]" + LP_STATUS.NotUsed04.getMsg());
									log.debug("[LP_STATUS][OFF][" + LP_STATUS.NotUsed04.getMsg() + "] " + e.toString());
									break;
								case 5: // Clock adjusted
									e.setAppend("[OFF]" + LP_STATUS.ClockAdjusted.getMsg());
									log.debug("[LP_STATUS][OFF][" + LP_STATUS.ClockAdjusted.getMsg() + "] " + e.toString());
									break;
								case 6: // Not used
									e.setAppend("[OFF]" + LP_STATUS.NotUsed06.getMsg());
									log.debug("[LP_STATUS][OFF][" + LP_STATUS.NotUsed06.getMsg() + "] " + e.toString());
									break;
								case 7: // Bit7 Power down
									e.setAppend("[OFF]" + LP_STATUS.PowernDown.getMsg());
									log.debug("[LP_STATUS][OFF][" + LP_STATUS.PowernDown.getMsg() + "] " + e.toString()); 
									break;
								default:
									break;
								}
								
								events.add(e);
							}
						}
						saveMeterEventLog(meter, events.toArray(new EventLogData[0]));
						meter.setInstallProperty(lpD.getStatus());
					}
				}
			}

			List<Map<String, Object>> list = parser.getEventLog();
			List<EventLogData> events = new ArrayList<EventLogData>();
			for (Map<String, Object> pf : list) {
				String eventTime = "";
				String eventCode = "";
				String key = null;
				for (Iterator<String> i = pf.keySet().iterator(); i.hasNext();) {
					key = i.next();
					if (key.equals("Entry"))
						continue;
					//System.out.println("KEY STR="+key+","+pf.get(key));
					if (key.indexOf("EventTime") >= 0) {
						eventTime = (String) pf.get(key);
						key = i.next();
						if (key.indexOf("EventCode") >= 0) {
							eventCode = (String) pf.get(key);
						}
					}
					log.debug("event code=" + eventTime + "," + eventCode);

					/*
					 * Standard event log, Fraud detection Log event, Disconnector control Log 처리
					 */
					for (EVENT_LOG el : EVENT_LOG.values()) {
						if (eventCode.equals(el.getMsg())) {
							//log.debug(el.name() + "[eventTime=" + eventTime + ", value=" + eventCode + "]");
							e = new EventLogData();
							String hhmmss = eventTime.substring(14) + "00";
							e.setDate(eventTime.substring(6, 14)); // :date= 제거
							e.setTime(hhmmss.substring(0, 6));
							e.setFlag(el.getFlag());
							e.setKind("STE");
							e.setMsg(el.getMsg());
							events.add(e);

							log.debug("EventSize=" + events.size() + ", added Event = [" + e.toString() + "]");
							
							/*
							// 정복전 이벤트만 식별한다.
							if (eventCode.contains((EVENT_LOG.PowerFailure.getMsg()))){
							    PowerAlarmLogData p = new PowerAlarmLogData();
							    p.setDate(e.getDate());
							    p.setTime(e.getTime());
							    p.setFlag(e.getFlag());
							    p.setKind(e.getKind());
							    p.setMsg(e.getMsg());
							}
							else if (eventCode.contains(EVENT_LOG.PowerRestore.name())) {
							    PowerAlarmLogData p = new PowerAlarmLogData();
							    p.setCloseDate(e.getDate());
							    p.setCloseTime(e.getTime());
							    p.setFlag(e.getFlag());
							    p.setKind(e.getKind());
							    p.setMsg(e.getMsg());
							
							}
							*/
							break;
						}
					}

					/*
					 * Measurement event log 처리
					 */
					for (MEASUREMENT_STATUS_ALARM alarm : MEASUREMENT_STATUS_ALARM.values()) {
						if (eventCode.equals(alarm.getMsg())) {
							log.debug(alarm.name() + "[eventTime=" + eventTime + ", value=" + eventCode + "]");
							e = new EventLogData();
							String hhmmss = eventTime.substring(14) + "00";
							e.setDate(eventTime.substring(6, 14)); // :date= 제거
							e.setTime(hhmmss.substring(0, 6));
							//e.setFlag(alarm.getFlag());
							e.setFlag(EVENT_LOG.MeasurementAlarm.getFlag());
							e.setKind("STE");
							//e.setMsg(alarm.getMsg());
							e.setMsg(EVENT_LOG.MeasurementAlarm.getMsg());
							e.setAppend(alarm.getMsg());
							events.add(e);
						}
					}
				}
			}

			//List<PowerAlarmLogData> powerDowns = new ArrayList<PowerAlarmLogData>();
			//List<PowerAlarmLogData> powerUps = new ArrayList<PowerAlarmLogData>();

			saveMeterEventLog(parser.getMeter(), events.toArray(new EventLogData[0]));

			// Power Down과 Up 시간을 비교하여 Down시간이 작으면 Down 시간을 Up의 DateTime에 설정한다.
			// 최근것부터 적용한다.
			/*
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
			*/
		} catch (Exception e) { // 미터 이벤트 로그 생성 중 에러는 경고로 끝냄.
			log.warn(e, e);
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

	private void saveDayProfile(DLMSLSSmartMeterForEVN parser) {

		try {
			List<BillingData> dailyProfiles = parser.getDailyBill();
			if (dailyProfiles == null || dailyProfiles.isEmpty()) {
				return;
			}
			log.debug("DailyBilling Data List size = " + dailyProfiles.size());
			
			for (BillingData dailyProfile : dailyProfiles) {
				saveDailyBilling(dailyProfile, parser.getMeter(), null, null, parser.getMDevType(), parser.getMDevId());
			}
		} catch (Exception e) {
			log.error("Daily Profile Saving error - " + e, e);
		}
	}

	private void saveMonthProfile(DLMSLSSmartMeterForEVN parser) {

		try {
			List<BillingData> monthProfiles = parser.getMonthlyBill();
			if (monthProfiles == null || monthProfiles.isEmpty()) {
				return;
			}
			log.debug("MonthlyBilling Data List size = " + monthProfiles.size());
			
			for (BillingData monthProfile : monthProfiles) {
				saveMonthlyBilling(monthProfile, parser.getMeter(), null, null, parser.getMDevType(), parser.getMDevId());
			}
		} catch (Exception e) {
			log.error("Monthly Profile Saving error - " + e, e);
		}
	}

	private Double retValue(String mm, Double value_00, Double value_15, Double value_30, Double value_45) {

		Double retVal_00 = value_00 == null ? 0d : value_00;
		Double retVal_15 = value_15 == null ? 0d : value_15;
		Double retVal_30 = value_30 == null ? 0d : value_30;
		Double retVal_45 = value_45 == null ? 0d : value_45;
		if ("15".equals(mm)) {
			return retVal_00;
		}

		if ("30".equals(mm)) {
			return retVal_00 + retVal_15;
		}

		if ("45".equals(mm)) {
			return retVal_00 + retVal_15 + retVal_30;
		}

		if ("00".equals(mm)) {
			return retVal_00 + retVal_15 + retVal_30 + retVal_45;
		}

		return retVal_00 + retVal_15;

	}

	private boolean lpSave(IMeasurementData md, LPData[] validlplist, DLMSLSSmartMeterForEVN parser, double base, double addBasePulse) throws Exception {
		HashSet<Condition> condition = new HashSet<Condition>();
		String yyyymmdd = validlplist[0].getDatetime().substring(0, 8);
		String yyyymmddhh = validlplist[0].getDatetime().substring(0, 10);
		String hhmm = validlplist[0].getDatetime().substring(8, 12);
		String mm = validlplist[0].getDatetime().substring(10, 12);

		condition.add(new Condition("id.mdevType", new Object[] { parser.getMDevType() }, null, Restriction.EQ));
		condition.add(new Condition("id.mdevId", new Object[] { parser.getMDevId() }, null, Restriction.EQ));
		// log.debug("parser.getMDevType():"+parser.getMDevType()+":parser.getMDevId():"+parser.getMDevId()+":dst:"+DateTimeUtil
		// .inDST(null, parser.getMeterDate())+":yyyymmddhh:"+yyyymmddhh );
		condition.add(new Condition("id.dst", new Object[] { DateTimeUtil.inDST(null, parser.getMeterTime()) }, null, Restriction.EQ));

		condition.add(new Condition("id.channel", new Object[] { ElectricityChannel.Usage.getChannel() }, null, Restriction.EQ));

		condition.add(new Condition("id.yyyymmddhh", new Object[] { yyyymmddhh }, null, Restriction.EQ));

		List<LpEM> lpEM = lpEMDao.findByConditions(condition);

		// String firstDate = lplist[0].getDatetime().substring(0,8);
		double basePulse = -1;

		try {
			if (lpEM != null && !lpEM.isEmpty()) {
				// 동시간대의 값을 가져올 경우 00분을 제외한 lp값을 더하여 base값을 구해야 한다.
				if (!mm.equals("00"))
					basePulse = lpEM.get(0).getValue() + retValue(mm, lpEM.get(0).getValue_00(), lpEM.get(0).getValue_15(), lpEM.get(0).getValue_30(), lpEM.get(0).getValue_45());
				else
					basePulse = lpEM.get(0).getValue();
			} else {
				HashSet<Condition> condition2 = new HashSet<Condition>();
				condition2.add(new Condition("id.mdevType", new Object[] { parser.getMDevType() }, null, Restriction.EQ));
				condition2.add(new Condition("id.mdevId", new Object[] { parser.getMDevId() }, null, Restriction.EQ));
				// log.debug("parser.getMDevType():"+parser.getMDevType()+":parser.getMDevId():"+parser.getMDevId()+":dst:"+DateTimeUtil
				// .inDST(null,
				// parser.getMeterDate())+":yyyymmddhh:"+yyyymmddhh );
				condition2.add(new Condition("id.dst", new Object[] { DateTimeUtil.inDST(null, parser.getMeterTime()) }, null, Restriction.EQ));

				condition2.add(new Condition("id.channel", new Object[] { ElectricityChannel.Usage.getChannel() }, null, Restriction.EQ));

				Calendar cal = Calendar.getInstance();
				SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHH");
				cal.setTime(dateFormatter.parse(yyyymmddhh));

				cal.add(Calendar.HOUR, -1);

				condition2.add(new Condition("id.yyyymmddhh", new Object[] { dateFormatter.format(cal.getTime()) }, null, Restriction.EQ));
				List<LpEM> subLpEM = lpEMDao.findByConditions(condition2);
				if (subLpEM != null && !subLpEM.isEmpty()) {
					// 전 시간 값을 가져온 것이기 때문에 전부 다 합산해야 한다.
					basePulse = subLpEM.get(0).getValue() + retValue("00", subLpEM.get(0).getValue_00(), subLpEM.get(0).getValue_15(), subLpEM.get(0).getValue_30(), subLpEM.get(0).getValue_45());
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e, e);
		}

		if (lpEM != null && !lpEM.isEmpty()) {
			//basePulse = lpEM.get(0).getValue();
		} else {
			basePulse = base;
		}

		log.info("#########save base value:" + basePulse + ":mdevId:" + parser.getMDevId() + ":yyyymmddhh:" + yyyymmddhh);
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
		log.debug("LP Interval Saving - MeterModel=" + parser.getMeterModel() +", MeterId=" + parser.getMeterID() + ", LPInterval=" + parser.getLpInterval());
		
		// TODO Flag, PF 처리해야 함.
		saveLPData(MeteringType.Normal, yyyymmdd, hhmm, lpValues, flaglist, basePulse, parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
		return true;
	}

	@Override
	public String relayValveOn(String mcuId, String meterId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);
			Meter meter = meterDao.get(meterId);

			txmanager.commit(txstatus);

			CommandGW commandGw = DataUtil.getBean(CommandGW.class);

			resultMap = commandGw.cmdOnDemandMeter(mcuId, meterId, OnDemandOption.WRITE_OPTION_RELAYON.getCode());

			if (resultMap != null && resultMap.get("LoadControlStatus") != null) {

				LOAD_CONTROL_STATUS ctrlStatus = (LOAD_CONTROL_STATUS) resultMap.get("LoadControlStatus");

				if (ctrlStatus.getCode() == LOAD_CONTROL_STATUS.CLOSE.getCode()) {
					meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.Normal.name()));

					Contract contract = meter.getContract();
					if (contract != null && (contract.getStatus() == null || contract.getStatus().getCode().equals(CommonConstants.ContractStatus.PAUSE.getCode()))) {
						Code normalCode = codeDao.getCodeIdByCodeObject(CommonConstants.ContractStatus.NORMAL.getCode());
						contract.setStatus(normalCode);
					}
				}
			}
		} catch (Exception e) {
			if (txstatus != null && !txstatus.isCompleted())
				txmanager.rollback(txstatus);
			resultMap.put("failReason", e.getMessage());
		}

		return MapToJSON(resultMap);
	}

	@Override
	public String relayValveOff(String mcuId, String meterId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);
			Meter meter = meterDao.get(meterId);

			txmanager.commit(txstatus);

			CommandGW commandGw = DataUtil.getBean(CommandGW.class);

			resultMap = commandGw.cmdOnDemandMeter(mcuId, meterId, OnDemandOption.WRITE_OPTION_RELAYOFF.getCode());

			if (resultMap != null && resultMap.get("LoadControlStatus") != null) {

				LOAD_CONTROL_STATUS ctrlStatus = (LOAD_CONTROL_STATUS) resultMap.get("LoadControlStatus");

				if (ctrlStatus.getCode() == LOAD_CONTROL_STATUS.OPEN.getCode()) {
					meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.CutOff.name()));
					Contract contract = meter.getContract();
					if (contract != null && (contract.getStatus() == null || contract.getStatus().getCode().equals(CommonConstants.ContractStatus.NORMAL.getCode()))) {
						Code pauseCode = codeDao.getCodeIdByCodeObject(CommonConstants.ContractStatus.PAUSE.getCode());
						contract.setStatus(pauseCode);
					}
				}
			}
		} catch (Exception e) {
			if (txstatus != null && !txstatus.isCompleted())
				txmanager.rollback(txstatus);
			resultMap.put("failReason", e.getMessage());
		}

		return MapToJSON(resultMap);
	}

	@Override
	public String relayValveStatus(String mcuId, String meterId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);
			Meter meter = meterDao.get(meterId);

			txmanager.commit(txstatus);

			CommandGW commandGw = DataUtil.getBean(CommandGW.class);

			int nOption = OnDemandOption.READ_OPTION_RELAY.getCode(); //read table
			resultMap = commandGw.cmdOnDemandMeter(mcuId, meterId, nOption);

			if (resultMap != null) {

				log.debug(resultMap.toString());

				if (resultMap.get("LoadControlStatus") != null) {
					if (((LOAD_CONTROL_STATUS) resultMap.get("LoadControlStatus")).getCode() == LOAD_CONTROL_STATUS.CLOSE.getCode()) {
						meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.Normal.name()));

						Contract contract = meter.getContract();
						if (contract != null && (contract.getStatus() == null || contract.getStatus().getCode().equals(CommonConstants.ContractStatus.PAUSE.getCode()))) {
							Code normalCode = codeDao.getCodeIdByCodeObject(CommonConstants.ContractStatus.NORMAL.getCode());
							contract.setStatus(normalCode);
						}
					} else if (((LOAD_CONTROL_STATUS) resultMap.get("LoadControlStatus")).getCode() == LOAD_CONTROL_STATUS.OPEN.getCode()) {
						meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.CutOff.name()));
						Contract contract = meter.getContract();
						if (contract != null && (contract.getStatus() == null || contract.getStatus().getCode().equals(CommonConstants.ContractStatus.NORMAL.getCode()))) {
							Code pauseCode = codeDao.getCodeIdByCodeObject(CommonConstants.ContractStatus.PAUSE.getCode());
							contract.setStatus(pauseCode);
						}
					}
				} else if (resultMap.get("SendSMSStatus") != null) {
					// Iraq MOE GPRS Modem의 경우 해당.
				}
			}
		} catch (Exception e) {
			if (txstatus != null && !txstatus.isCompleted())
				txmanager.rollback(txstatus);
			log.error(e, e);
			resultMap.put("failReason", e.getMessage());
		}

		return MapToJSON(resultMap);
	}

	/**
	 * Meter Time Sync
	 */
	@Override
	public String syncTime(String mcuId, String meterId) {
		log.info("[syncTime] mcuId =  " + mcuId + ", meterId = " + meterId);

		Map<String, String> resultMap = new HashMap<String, String>();
		int result = 0;
		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);
			Meter meter = meterDao.get(meterId);

			txmanager.commit(txstatus);

			CommandGW commandGw = DataUtil.getBean(CommandGW.class);
			log.info("OnDemandOption = " + OnDemandOption.WRITE_OPTION_TIMESYNC.getCode());

			resultMap = commandGw.cmdOnDemandMeter(mcuId, meterId, OnDemandOption.WRITE_OPTION_TIMESYNC.getCode());

			if (resultMap != null) {

				String after = (String) resultMap.get("afterTime");
				String before = DateTimeUtil.getDateString(TimeUtil.getLongTime(after) - (meter.getTimeDiff()*1000));

				String diff = String.valueOf((TimeUtil.getLongTime(after) - TimeUtil.getLongTime(before))/1000);
				resultMap.put("diff", diff);
				
				// {afterTime=20170606022829, Result=SUCCESS}
				if(resultMap.get("Result") != null && String.valueOf(resultMap.get("Result")).equals("SUCCESS")){
					result = 0;
				}else{
					result = 1;
				}
				
				saveMeterTimeSyncLog(meter, before, after, result);
				//saveMeterTimeSyncLog(meter, "", after, result);
			}
		} catch (Exception e) {
			if (txstatus != null && !txstatus.isCompleted())
				txmanager.rollback(txstatus);
				resultMap.put("failReason", e.getMessage());
			log.error("syncTime error - " + e.getMessage(), e);
		}
		return MapToJSON(resultMap);
	}

}
