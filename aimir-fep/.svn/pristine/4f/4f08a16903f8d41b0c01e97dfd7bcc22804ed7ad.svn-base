package com.aimir.fep.meter.parser;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.MeteringFlag;
import com.aimir.fep.command.conf.DLMSMeta.CONTROL_STATE;
import com.aimir.fep.command.conf.DLMSMeta.LOAD_CONTROL_STATUS;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.parser.DLMSEVNTable.DLMSEVNTable;
import com.aimir.fep.meter.parser.DLMSEVNTable.DLMSSCALAR;
import com.aimir.fep.meter.parser.DLMSEVNTable.DLMSVARIABLE;
import com.aimir.fep.meter.parser.DLMSEVNTable.DLMSVARIABLE.DEVICE_STATUS_ALARM;
import com.aimir.fep.meter.parser.DLMSEVNTable.DLMSVARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSEVNTable.DLMSVARIABLE.ENERGY_LOAD_PROFILE;
import com.aimir.fep.meter.parser.DLMSEVNTable.DLMSVARIABLE.EVENT_LOG;
import com.aimir.fep.meter.parser.DLMSEVNTable.DLMSVARIABLE.FUNCTION_STATUS_ALARM;
import com.aimir.fep.meter.parser.DLMSEVNTable.DLMSVARIABLE.MEASUREMENT_STATUS_ALARM;
import com.aimir.fep.meter.parser.DLMSEVNTable.DLMSVARIABLE.OBIS;
import com.aimir.fep.meter.parser.DLMSEVNTable.LPComparator;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

public class DLMSLSSmartMeterForEVN extends MeterDataParser implements java.io.Serializable {

	private static final long serialVersionUID = 5198091223934578776L;

	private static Log log = LogFactory.getLog(DLMSLSSmartMeterForEVN.class);

	LPData[] lpData = null;

	Map<String, Object> data = new LinkedHashMap<String, Object>(16, 0.75f, false);
	LinkedHashMap<String, Map<String, Object>> result = new LinkedHashMap<String, Map<String, Object>>();

	String meterID = "";
	String fwVersion = "";
	String meterModel = "";
	String logicalNumber = "";
	String manufactureSerial = "";
	String servicePointSerial = "";
	Long ct_num = 0L;
	Long vt_num = 0L;
	Long ct_den = 0L;
	Long vt_den = 0L;
	Long trans_num = 0L;
	String phaseType = null;
	byte[] relayStatus = null;

	int meterActiveConstant = 1;
	int meterReActiveConstant = 1;

	double activePulseConstant = 1;
	double reActivePulseConstant = 1;

	public BillingData getCurrBill() {
		return currBill;
	}

	public List<BillingData> getCurrBillLog() {
		return currBillLog;
	}

	public BillingData getPreviousMonthBill() {
		return previousMonthBill;
	}

	public List<BillingData> getDailyBill() {
		return dailyBill;
	}

	public void setDailyBill(List<BillingData> dailyBill) {
		this.dailyBill = dailyBill;
	}

	public List<BillingData> getMonthlyBill() {
		return monthlyBill;
	}

	public void setMonthlyBill(List<BillingData> monthlyBill) {
		this.monthlyBill = monthlyBill;
	}

	public LinkedHashMap<String, Map<String, Object>> getResult() {
		return result;
	}

	public void setResult(LinkedHashMap<String, Map<String, Object>> result) {
		this.result = result;
	}

	private List<EventLogData> meterAlarmLog = null;
	private List<BillingData> currBillLog = null;
	private BillingData currBill = null;
	private BillingData previousMonthBill = null;
	private List<BillingData> dailyBill = null;
	private List<BillingData> monthlyBill = null;

	Long ctRatio = 1L;

	int lpInterval = 60;

	Double meteringValue = 0d;
	Double ct = 1d;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public LinkedHashMap<String, Map<String, Object>> getData() {

		Map<String, Object> resultSubData = null;
		String key = null;

		DecimalFormat decimalf = null;
		SimpleDateFormat datef14 = null;

		if (meter != null && meter.getSupplier() != null) {
			Supplier supplier = meter.getSupplier();
			if (supplier != null) {
				String lang = supplier.getLang().getCode_2letter();
				String country = supplier.getCountry().getCode_2letter();
				decimalf = TimeLocaleUtil.getDecimalFormat(supplier);
				datef14 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(14, lang, country));
			}
		} else {
			//locail 정보가 없을때는 기본 포멧을 사용한다.
			decimalf = new DecimalFormat();
			datef14 = new SimpleDateFormat();
		}

		String clock = meterTime;
		try {
			clock = datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime));
		} catch (Exception e) {
			e.printStackTrace();
		}
		data.put("Meter Clock", clock);

		for (Iterator i = result.keySet().iterator(); i.hasNext();) {
			key = (String) i.next();
			resultSubData = result.get(key);

			if (resultSubData != null) {
				String idx = "";
				if (key.lastIndexOf("-") != -1) {
					idx = key.substring(key.lastIndexOf("-") + 1);
					key = key.substring(0, key.lastIndexOf("-"));
				}
				String subkey = null;
				Object subvalue = null;
				for (Iterator subi = resultSubData.keySet().iterator(); subi.hasNext();) {
					subkey = (String) subi.next();
					if (!subkey.contains(DLMSVARIABLE.UNDEFINED)) {
						subvalue = resultSubData.get(subkey);
						if (subvalue instanceof String) {
							if (((String) subvalue).contains(":date=")) {
								try {
									data.put(getKey(OBIS.getObis(key).getName(), idx, subkey), datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(((String) subvalue).substring(6) + "00")));
								} catch (Exception e) {
									data.put(getKey(OBIS.getObis(key).getName(), idx, subkey), subvalue);
								}
							} else if (subkey.contains("Date") && !((String) subvalue).contains(":date=") && ((String) subvalue).length() == 12) {
								try {
									data.put(getKey(OBIS.getObis(key).getName(), idx, subkey), datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(subvalue + "00")));
								} catch (Exception e) {
									data.put(getKey(OBIS.getObis(key).getName(), idx, subkey), subvalue);
								}
							} else {
								data.put(getKey(OBIS.getObis(key).getName(), idx, subkey), subvalue);
							}

						} else if (subvalue instanceof Number) {

							if (subvalue instanceof Long && !OBIS.getObis(key).getName().endsWith("Number") && !OBIS.getObis(key).getName().endsWith("Den") && !OBIS.getObis(key).getName().endsWith("num)") && !subkey.equals("LpInterval")) {
								data.put(getKey(OBIS.getObis(key).getName(), idx, subkey), decimalf.format(new Double(((Long) subvalue) * 0.001)));
							} else {
								data.put(getKey(OBIS.getObis(key).getName(), idx, subkey), decimalf.format(subvalue));
							}

						} else if (subvalue instanceof OCTET) {
							data.put(getKey(OBIS.getObis(key).getName(), idx, subkey), ((OCTET) subvalue).toHexString());
						} else if (subvalue instanceof LOAD_CONTROL_STATUS) {
							data.put(getKey(OBIS.getObis(key).getName(), idx, subkey), ((LOAD_CONTROL_STATUS) subvalue).name());
						} else if (subvalue instanceof CONTROL_STATE) {
							data.put(getKey(OBIS.getObis(key).getName(), idx, subkey), ((CONTROL_STATE) subvalue).name());
						} else
							data.put(getKey(OBIS.getObis(key).getName(), idx, subkey), subvalue);
					}
				}
			}
		}

		return (LinkedHashMap) data;
	}

	private String getKey(String mainKey, String idx, String subKey) {

		if (mainKey.equals(subKey) && "".equals(idx)) {
			return mainKey + idx;
		} else {
			return mainKey + idx + " : " + subKey;
		}
	}

	@Override
	public void parse(byte[] data) throws Exception {
		// 로그 확인 편하도록....
		log.info("    ");
		log.info("    ");
		log.info("    ");
		log.info("############################## OBIS Parsing Start!! ################################################");
		log.debug("##### DLMS parse:" + Hex.decode(data));

		String obisCode = "";
		int clazz = 0;
		int attr = 0;
		int pos = 0;
		int len = 0;
		// DLMS Header OBIS(6), CLASS(1), ATTR(1), LENGTH(2)
		// DLMS Tag Tag(1), DATA or LEN/DATA (*)
		byte[] OBIS = new byte[6];
		byte[] CLAZZ = new byte[2];
		byte[] ATTR = new byte[1];
		byte[] LEN = new byte[2];
		byte[] TAGDATA = null;

		DLMSEVNTable dlms = null;

		if (meter != null) {
			log.debug("DLMS Meter Name : [" + meter.getModel().getName() + "] , MeterId [" + meter.getMdsId() + "]");
		}

		while (pos < data.length) {
			log.info("-----------------------------------------------------------------------");
			dlms = new DLMSEVNTable();
			System.arraycopy(data, pos, OBIS, 0, OBIS.length);
			pos += OBIS.length;
			obisCode = Hex.decode(OBIS);
			dlms.setObis(obisCode);

			System.arraycopy(data, pos, CLAZZ, 0, CLAZZ.length);
			pos += CLAZZ.length;
			clazz = DataUtil.getIntTo2Byte(CLAZZ);
			dlms.setClazz(clazz);

			System.arraycopy(data, pos, ATTR, 0, ATTR.length);
			pos += ATTR.length;
			attr = DataUtil.getIntToBytes(ATTR);
			dlms.setAttr(attr);

			System.arraycopy(data, pos, LEN, 0, LEN.length);
			pos += LEN.length;
			len = DataUtil.getIntTo2Byte(LEN);
			dlms.setLength(len);

			TAGDATA = new byte[len];
			if (pos + TAGDATA.length <= data.length) {
				System.arraycopy(data, pos, TAGDATA, 0, TAGDATA.length);
				pos += TAGDATA.length;
			} else {
				System.arraycopy(data, pos, TAGDATA, 0, data.length - pos);
				pos += data.length - pos;
			}

			log.debug("OBIS[" + obisCode + "] CLASS[" + clazz + "] ATTR[" + attr + "] LENGTH[" + len + "] TAGDATA=[" + Hex.decode(TAGDATA) + "]");

			dlms.setMeter(meter);
			dlms.parseDlmsTag(TAGDATA);
			Map<String, Object> dlmsData = dlms.getData();
			
			// 동일한 obis 코드를 가진 값이 있을 수 있기 때문에 검사해서 _number를 붙인다.
			// obis code type으로 하지말고  profile-generic 타입에 대해 구분해야할거 같음 매번 추가해야 하는 번거로움
			//|| dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.TIME_CHANGE_BEFORE
			//|| dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.TIME_CHANGE_AFTER 
			if (dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.ENERGY_LOAD_PROFILE 
					|| dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.DAILY_LOAD_PROFILE 
					|| dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.MONTHLY_BILLING 
					|| dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.STANDARD_EVENT 
					|| dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.RELAY_EVENT
					|| dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.FRAUDDETECTIONLOGEVENT 
					|| dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.MEASUREMENT_EVENT) {

				for (int cnt = 0;; cnt++) {
					obisCode = dlms.getDlmsHeader().getObis().getCode() + "-" + cnt;
					if (!result.containsKey(obisCode)) {
						result.put(obisCode, dlmsData);
						break;
					}
				}
			} else if (dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.METER_TIME && dlms.getDlmsHeader().getAttr() != DLMS_CLASS_ATTR.CLOCK_ATTR02) {
				result.put(obisCode + "-" + dlms.getDlmsHeader().getAttr().getAttr(), dlmsData);
			} else if(dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.METER_MODEL){
				meterModel = dlmsData.get("Meter Model").toString();
				result.put(obisCode, dlmsData);
			} else if (dlmsData != null && !dlmsData.isEmpty()) {
				result.put(obisCode, dlmsData);
			}
		}

		EnergyMeter meter = (EnergyMeter) this.getMeter();

		this.ct = 1.0;
		if (meter != null && meter.getCt() != null && meter.getCt() > 0) {
			ct = meter.getCt();
		}
		if (meter != null && meter.getLpInterval() != null && meter.getLpInterval() > 0) {
			lpInterval = meter.getLpInterval();
		}

		setCt(ct);
		setMeterInfo();
		setLPData();
		setDailyBillingData();
		setMonthlyBillingData();
		//setCurrBillingData();
		setAlarmLog();
		/*
		Properties prop = new Properties();
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream(
			        "config/moe-integration.properties"));
			boolean isMakeFile = Boolean.parseBoolean(prop.getProperty("integration.makefile", "false"));
			if(isMakeFile){
		        makeLpIntegrationData();
		        makeDailyIntegrationData();
		        makeMonthlyIntegrationData();
		        makeEventIntegrationData();
			}
		} catch(Exception e){
			log.error(e,e);
		}
		*/
	}

	private void setAlarmLog() {

		byte[] meterStatus = null;
		String meterStatusBitString = "";

		byte[] measurementStatus = null;
		String measurementStatusBitString = "";

		byte[] deviceStatus = null;
		String deviceStatusBitString = "";

		meterAlarmLog = new ArrayList<EventLogData>();

		log.debug("##### ALARM LOG START #####");
		try {
			Map<String, Object> map = null;

			/*
			 * 기준시간 설정 Alaram은 시간값이 없기때문에 미터시간을 기준으로 저장한다
			 */
			EventLogData e = null;
			String hhmmss = meterTime.substring(8);
			String setDate = meterTime.substring(0, 8); // :date= 제거
			String setTime = hhmmss.substring(0, 6);

			/*
			 * 0.0.96.10.5.255 METER_STATUS : Function Alarm Message 처리
			 * MeterEvent Log 로 저장 하지만 알람정보 자체가 시간값이 없기때문에 미터 시간 기준으로 저장한다.
			 */
			map = (Map<String, Object>) result.get(OBIS.METER_STATUS.getCode());
			log.debug("## METER_STATUS(Function Alarm Message) Eevnt count = " + (map == null ? 0 : map.size()));
			if (map != null) {
				log.debug("[METER_STATUS] => " + map.toString());
				Object obj = map.get(OBIS.METER_STATUS.getName());
				if (obj != null) {
					//meterStatus = ((OCTET)obj).getValue(); // 2byte
					meterStatus = DataUtil.readByteString(obj.toString()); // 2byte HEX code
					meterStatusBitString = DataUtil.byteArrayToBinaryString(meterStatus);
					log.debug("[METER_STATUS:BitString] => " + meterStatusBitString);

					// Function Alarm 의 상태값을 비교해서 저장하기 위해 METER_CAUTION 컬럼을 사용한다. 
					EnergyMeter meter = (EnergyMeter) this.getMeter();
					if (meter.getMeterCaution() != null && meter.getMeterCaution().length() != 16) { // 2byte
						meter.setMeterCaution(null);
					}

					// METER_STATUS 값이 없거나 변경됬을때만 이벤트로 등록한다.
					if (meter.getMeterCaution() == null || !meter.getMeterCaution().equals(meterStatusBitString)) {
						int prevState = 0;
						int curState = 0;

						for (int value = 0; value < meterStatusBitString.length(); value++) {
							if (meter.getMeterCaution() == null) {
								prevState = 0;
							} else {
								prevState = Integer.parseInt(meter.getMeterCaution().substring(value, value + 1));
							}

							curState = Integer.parseInt(meterStatusBitString.substring(value, value + 1));
							if (prevState == 0 && curState == 1) {
								e = new EventLogData();
								e.setDate(setDate); // :date= 제거
								e.setTime(setTime);
								e.setFlag(EVENT_LOG.FunctionAlarm.getFlag());
								e.setKind("STE");
								e.setMsg(EVENT_LOG.FunctionAlarm.getMsg());

								switch (value) {
								case 0: // L1 relay status
									e.setAppend("[ON]" + FUNCTION_STATUS_ALARM.L1RelayStatus.getMsg());
									log.debug("[METER_STATUS - Function Alarm][ON][" + FUNCTION_STATUS_ALARM.L1RelayStatus.getMsg() + "] " + e.toString());
									break;
								case 1: // L2 relay status
									e.setAppend("[ON]" + FUNCTION_STATUS_ALARM.L2RelayStatus.getMsg());
									log.debug("[METER_STATUS - Function Alarm][ON][" + FUNCTION_STATUS_ALARM.L2RelayStatus.getMsg() + "] " + e.toString());
									break;
								case 2: // L3 relay status
									e.setAppend("[ON]" + FUNCTION_STATUS_ALARM.L3RelayStatus.getMsg());
									log.debug("[METER_STATUS - Function Alarm][ON][" + FUNCTION_STATUS_ALARM.L3RelayStatus.getMsg() + "] " + e.toString());
									break;
								case 3: // External relay status
									e.setAppend("[ON]" + FUNCTION_STATUS_ALARM.ExternalRelayStatus.getMsg());
									log.debug("[METER_STATUS - Function Alarm][ON][" + FUNCTION_STATUS_ALARM.ExternalRelayStatus.getMsg() + "] " + e.toString());
									break;
								case 4: // L1 relay error
									e.setAppend("[ON]" + FUNCTION_STATUS_ALARM.L1RelayError.getMsg());
									log.debug("[METER_STATUS - Function Alarm][ON][" + FUNCTION_STATUS_ALARM.L1RelayError.getMsg() + "] " + e.toString());
									break;
								case 5: // L2 relay error
									e.setAppend("[ON]" + FUNCTION_STATUS_ALARM.L2RelayError.getMsg());
									log.debug("[METER_STATUS - Function Alarm][ON][" + FUNCTION_STATUS_ALARM.L2RelayError.getMsg() + "] " + e.toString());
									break;
								case 6: // L3 relay error
									e.setAppend("[ON]" + FUNCTION_STATUS_ALARM.L3RelayError.getMsg());
									log.debug("[METER_STATUS - Function Alarm][ON][" + FUNCTION_STATUS_ALARM.L3RelayError.getMsg() + "] " + e.toString());
									break;
								case 7: // External relay error
									e.setAppend("[ON]" + FUNCTION_STATUS_ALARM.ExternalRelayError.getMsg());
									log.debug("[METER_STATUS - Function Alarm][ON][" + FUNCTION_STATUS_ALARM.ExternalRelayError.getMsg() + "] " + e.toString());
									break;
								case 8: // Open terminal cover
									e.setAppend("[ON]" + FUNCTION_STATUS_ALARM.OpenTerminalCover.getMsg());
									log.debug("[METER_STATUS - Function Alarm][ON][" + FUNCTION_STATUS_ALARM.OpenTerminalCover.getMsg() + "] " + e.toString());
									break;
								case 9: // Open terminal cover in power off 
									e.setAppend("[ON]" + FUNCTION_STATUS_ALARM.OpenTerminalCoverInPowerOff.getMsg());
									log.debug("[METER_STATUS - Function Alarm][ON][" + FUNCTION_STATUS_ALARM.OpenTerminalCoverInPowerOff.getMsg() + "] " + e.toString());
									break;
								case 10: // Open top cover
									e.setAppend("[ON]" + FUNCTION_STATUS_ALARM.OpenTopCover.getMsg());
									log.debug("[METER_STATUS - Function Alarm][ON][" + FUNCTION_STATUS_ALARM.OpenTopCover.getMsg() + "] " + e.toString());
									break;
								case 11: // Open top cover in power off
									e.setAppend("[ON]" + FUNCTION_STATUS_ALARM.OpenTopCoverInPowerOff.getMsg());
									log.debug("[METER_STATUS - Function Alarm][ON][" + FUNCTION_STATUS_ALARM.OpenTopCoverInPowerOff.getMsg() + "] " + e.toString());
									break;
								case 12: // Magnetic detection 1
									e.setAppend("[ON]" + FUNCTION_STATUS_ALARM.MagneticDetection1.getMsg());
									log.debug("[METER_STATUS - Function Alarm][ON][" + FUNCTION_STATUS_ALARM.MagneticDetection1.getMsg() + "] " + e.toString());
									break;
								case 13: // Magnetic detection 2
									e.setAppend("[ON]" + FUNCTION_STATUS_ALARM.MagneticDetection2.getMsg());
									log.debug("[METER_STATUS - Function Alarm][ON][" + FUNCTION_STATUS_ALARM.MagneticDetection2.getMsg() + "] " + e.toString());
									break;
								case 14: // program
									e.setAppend("[ON]" + FUNCTION_STATUS_ALARM.Program.getMsg());
									log.debug("[METER_STATUS - Function Alarm][ON][" + FUNCTION_STATUS_ALARM.Program.getMsg() + "] " + e.toString());
									break;
								case 15: // Factory status
									e.setAppend("[ON]" + FUNCTION_STATUS_ALARM.FactoryStatus.getMsg());
									log.debug("[METER_STATUS - Function Alarm][ON][" + FUNCTION_STATUS_ALARM.FactoryStatus.getMsg() + "] " + e.toString());
									break;
								default:
									break;
								}
								meterAlarmLog.add(e);
							} else if (prevState == 1 && curState == 0) {
								e = new EventLogData();
								e.setDate(setDate); // :date= 제거
								e.setTime(setTime);
								e.setFlag(EVENT_LOG.FunctionAlarm.getFlag());
								e.setKind("STE");
								e.setMsg(EVENT_LOG.FunctionAlarm.getMsg());

								switch (value) {
								case 0: // L1 relay status
									e.setAppend("[OFF]" + FUNCTION_STATUS_ALARM.L1RelayStatus.getMsg());
									log.debug("[METER_STATUS - Function Alarm][OFF][" + FUNCTION_STATUS_ALARM.L1RelayStatus.getMsg() + "] " + e.toString());
									break;
								case 1: // L2 relay status
									e.setAppend("[OFF]" + FUNCTION_STATUS_ALARM.L2RelayStatus.getMsg());
									log.debug("[METER_STATUS - Function Alarm][OFF][" + FUNCTION_STATUS_ALARM.L2RelayStatus.getMsg() + "] " + e.toString());
									break;
								case 2: // L3 relay status
									e.setAppend("[OFF]" + FUNCTION_STATUS_ALARM.L3RelayStatus.getMsg());
									log.debug("[METER_STATUS - Function Alarm][OFF][" + FUNCTION_STATUS_ALARM.L3RelayStatus.getMsg() + "] " + e.toString());
									break;
								case 3: // External relay status
									e.setAppend("[OFF]" + FUNCTION_STATUS_ALARM.ExternalRelayStatus.getMsg());
									log.debug("[METER_STATUS - Function Alarm][OFF][" + FUNCTION_STATUS_ALARM.ExternalRelayStatus.getMsg() + "] " + e.toString());
									break;
								case 4: // L1 relay error
									e.setAppend("[OFF]" + FUNCTION_STATUS_ALARM.L1RelayError.getMsg());
									log.debug("[METER_STATUS - Function Alarm][OFF][" + FUNCTION_STATUS_ALARM.L1RelayError.getMsg() + "] " + e.toString());
									break;
								case 5: // L2 relay error
									e.setAppend("[OFF]" + FUNCTION_STATUS_ALARM.L2RelayError.getMsg());
									log.debug("[METER_STATUS - Function Alarm][OFF][" + FUNCTION_STATUS_ALARM.L2RelayError.getMsg() + "] " + e.toString());
									break;
								case 6: // L3 relay error
									e.setAppend("[OFF]" + FUNCTION_STATUS_ALARM.L3RelayError.getMsg());
									log.debug("[METER_STATUS - Function Alarm][OFF][" + FUNCTION_STATUS_ALARM.L3RelayError.getMsg() + "] " + e.toString());
									break;
								case 7: // External relay error
									e.setAppend("[OFF]" + FUNCTION_STATUS_ALARM.ExternalRelayError.getMsg());
									log.debug("[METER_STATUS - Function Alarm][OFF][" + FUNCTION_STATUS_ALARM.ExternalRelayError.getMsg() + "] " + e.toString());
									break;
								case 8: // Open terminal cover
									e.setAppend("[OFF]" + FUNCTION_STATUS_ALARM.OpenTerminalCover.getMsg());
									log.debug("[METER_STATUS - Function Alarm][OFF][" + FUNCTION_STATUS_ALARM.OpenTerminalCover.getMsg() + "] " + e.toString());
									break;
								case 9: // Open terminal cover in power off 
									e.setAppend("[OFF]" + FUNCTION_STATUS_ALARM.OpenTerminalCoverInPowerOff.getMsg());
									log.debug("[METER_STATUS - Function Alarm][OFF][" + FUNCTION_STATUS_ALARM.OpenTerminalCoverInPowerOff.getMsg() + "] " + e.toString());
									break;
								case 10: // Open top cover
									e.setAppend("[OFF]" + FUNCTION_STATUS_ALARM.OpenTopCover.getMsg());
									log.debug("[METER_STATUS - Function Alarm][OFF][" + FUNCTION_STATUS_ALARM.OpenTopCover.getMsg() + "] " + e.toString());
									break;
								case 11: // Open top cover in power off
									e.setAppend("[OFF]" + FUNCTION_STATUS_ALARM.OpenTopCoverInPowerOff.getMsg());
									log.debug("[METER_STATUS - Function Alarm][OFF][" + FUNCTION_STATUS_ALARM.OpenTopCoverInPowerOff.getMsg() + "] " + e.toString());
									break;
								case 12: // Magnetic detection 1
									e.setAppend("[OFF]" + FUNCTION_STATUS_ALARM.MagneticDetection1.getMsg());
									log.debug("[METER_STATUS - Function Alarm][OFF][" + FUNCTION_STATUS_ALARM.MagneticDetection1.getMsg() + "] " + e.toString());
									break;
								case 13: // Magnetic detection 2
									e.setAppend("[OFF]" + FUNCTION_STATUS_ALARM.MagneticDetection2.getMsg());
									log.debug("[METER_STATUS - Function Alarm][OFF][" + FUNCTION_STATUS_ALARM.MagneticDetection2.getMsg() + "] " + e.toString());
									break;
								case 14: // program
									e.setAppend("[OFF]" + FUNCTION_STATUS_ALARM.Program.getMsg());
									log.debug("[METER_STATUS - Function Alarm][OFF][" + FUNCTION_STATUS_ALARM.Program.getMsg() + "] " + e.toString());
									break;
								case 15: // Factory status
									e.setAppend("[OFF]" + FUNCTION_STATUS_ALARM.FactoryStatus.getMsg());
									log.debug("[METER_STATUS - Function Alarm][OFF][" + FUNCTION_STATUS_ALARM.FactoryStatus.getMsg() + "] " + e.toString());
									break;
								default:
									break;
								}

								meterAlarmLog.add(e);
							}
						}

						meter.setMeterCaution(meterStatusBitString);
					}
				}

				result.remove(OBIS.METER_STATUS.getCode());
			}

			// 기존코드 나중에 필요없으면 삭제할것.            
			//            map = (Map<String, Object>)result.get(OBIS.METER_STATUS.getCode());
			//            if (map != null) {
			//	        	Object obj = map.get(OBIS.METER_STATUS.getName());
			//	        	if (obj != null) meterStatus = ((OCTET)obj).getValue();	        	
			//	        	meterStatusStr = DLMSTable.getOBIS_FUNCTION_STATUS((OCTET)obj);
			//	        	log.debug("METER_STATUS[" + Hex.decode(meterStatus) + "], OBIS_FUNCTION_STATUS["+ meterStatusStr + "]");	        	
			//	        	data.put(OBIS.METER_STATUS.getName(), meterStatusStr);
			//	        	result.remove(OBIS.METER_STATUS.getCode());
			//            }

			/*
			 * 0.0.96.10.6.255 DRIVE_STATUS : Drive(Device) Alarm Message 처리
			 * MeterEvent Log 로 저장 하지만 알람정보 자체가 시간값이 없기때문에 미터 시간 기준으로 저장한다.
			 */
			map = (Map<String, Object>) result.get(OBIS.DRIVE_STATUS.getCode());
			log.debug("## DRIVE_STATUS(Drive(Device) Alarm Message) Eevnt count = " + (map == null ? 0 : map.size()));
			if (map != null) {
				log.debug("[DRIVE_STATUS] => " + map.toString());
				Object obj = map.get(OBIS.DRIVE_STATUS.getName());
				if (obj != null) {
					//deviceStatus = ((OCTET)obj).getValue();  // 1byte
					deviceStatus = DataUtil.readByteString(obj.toString()); // 1byte HEX code
					deviceStatusBitString = DataUtil.byteArrayToBinaryString(deviceStatus);
					log.debug("[DRIVE_STATUS:BitString] => " + deviceStatusBitString);

					// Drive(Device) Alarm 의 상태값을 비교해서 저장하기 위해 METER_ERROR 컬럼을 사용한다. 
					EnergyMeter meter = (EnergyMeter) this.getMeter();
					if (meter.getMeterError() != null && meter.getMeterError().length() != 8) { // 1byte
						meter.setMeterError(null);
					}

					// DRIVE_STATUS 값이 없거나 변경됬을때만 이벤트로 등록한다.
					if (meter.getMeterError() == null || !meter.getMeterError().equals(deviceStatusBitString)) {
						int prevState = 0;
						int curState = 0;

						for (int flag = 0; flag < deviceStatusBitString.length(); flag++) {
							if (meter.getMeterError() == null) {
								prevState = 0;
							} else {
								prevState = Integer.parseInt(meter.getMeterError().substring(flag, flag + 1));
							}

							curState = Integer.parseInt(deviceStatusBitString.substring(flag, flag + 1));
							if (prevState == 0 && curState == 1) {
								e = new EventLogData();
								e.setDate(setDate); // :date= 제거
								e.setTime(setTime);
								e.setFlag(EVENT_LOG.DeviceAlarm.getFlag());
								e.setKind("STE");
								e.setMsg(EVENT_LOG.DeviceAlarm.getMsg());

								switch (flag) {
								case 0: // EPROM error
									e.setAppend("[ON]" + DEVICE_STATUS_ALARM.EPROMError.getMsg());
									log.debug("[DRIVE_STATUS - Device Alarm][ON][" + DEVICE_STATUS_ALARM.EPROMError.getMsg() + "] " + e.toString());
									break;
								case 1: // Clock error
									e.setAppend("[ON]" + DEVICE_STATUS_ALARM.ClockError.getMsg());
									log.debug("[DRIVE_STATUS - Device Alarm][ON][" + DEVICE_STATUS_ALARM.ClockError.getMsg() + "] " + e.toString());
									break;
								case 2: // Battery error
									e.setAppend("[ON]" + DEVICE_STATUS_ALARM.BatteryError.getMsg());
									log.debug("[DRIVE_STATUS - Device Alarm][ON][" + DEVICE_STATUS_ALARM.BatteryError.getMsg() + "] " + e.toString());
									break;
								case 3: // Read card errors
									e.setAppend("[ON]" + DEVICE_STATUS_ALARM.ReadCardError.getMsg());
									log.debug("[DRIVE_STATUS - Device Alarm][ON][" + DEVICE_STATUS_ALARM.ReadCardError.getMsg() + "] " + e.toString());
									break;
								case 4: // Data abnormal
									e.setAppend("[ON]" + DEVICE_STATUS_ALARM.DataAbnormal.getMsg());
									log.debug("[DRIVE_STATUS - Device Alarm][ON][" + DEVICE_STATUS_ALARM.DataAbnormal.getMsg() + "] " + e.toString());
									break;
								case 5: // External Battery Status
									e.setAppend("[ON]" + DEVICE_STATUS_ALARM.ExternalBatteryStatus.getMsg());
									log.debug("[DRIVE_STATUS - Device Alarm][ON][" + DEVICE_STATUS_ALARM.ExternalBatteryStatus.getMsg() + "] " + e.toString());
									break;
								case 6: // High low level input
									e.setAppend("[ON]" + DEVICE_STATUS_ALARM.HighLowLevelInput.getMsg());
									log.debug("[DRIVE_STATUS - Device Alarm][ON][" + DEVICE_STATUS_ALARM.HighLowLevelInput.getMsg() + "] " + e.toString());
									break;
								case 7: // Voltage detect input
									e.setAppend("[ON]" + DEVICE_STATUS_ALARM.VoltageDetectInput.getMsg());
									log.debug("[DRIVE_STATUS - Device Alarm][ON][" + DEVICE_STATUS_ALARM.VoltageDetectInput.getMsg() + "] " + e.toString());
									break;
								default:
									break;
								}
								meterAlarmLog.add(e);
							} else if (prevState == 1 && curState == 0) {
								e = new EventLogData();
								e.setDate(setDate); // :date= 제거
								e.setTime(setTime);
								e.setFlag(EVENT_LOG.DeviceAlarm.getFlag());
								e.setKind("STE");
								e.setMsg(EVENT_LOG.DeviceAlarm.getMsg());

								switch (flag) {
								case 0: // EPROM error
									e.setAppend("[OFF]" + DEVICE_STATUS_ALARM.EPROMError.getMsg());
									log.debug("[DRIVE_STATUS - Device Alarm][OFF][" + DEVICE_STATUS_ALARM.EPROMError.getMsg() + "] " + e.toString());
									break;
								case 1: // Clock error
									e.setAppend("[OFF]" + DEVICE_STATUS_ALARM.ClockError.getMsg());
									log.debug("[DRIVE_STATUS - Device Alarm][OFF][" + DEVICE_STATUS_ALARM.ClockError.getMsg() + "] " + e.toString());
									break;
								case 2: // Battery error
									e.setAppend("[OFF]" + DEVICE_STATUS_ALARM.BatteryError.getMsg());
									log.debug("[DRIVE_STATUS - Device Alarm][OFF][" + DEVICE_STATUS_ALARM.BatteryError.getMsg() + "] " + e.toString());
									break;
								case 3: // Read card errors
									e.setAppend("[OFF]" + DEVICE_STATUS_ALARM.ReadCardError.getMsg());
									log.debug("[DRIVE_STATUS - Device Alarm][OFF][" + DEVICE_STATUS_ALARM.ReadCardError.getMsg() + "] " + e.toString());
									break;
								case 4: // Data abnormal
									e.setAppend("[OFF]" + DEVICE_STATUS_ALARM.DataAbnormal.getMsg());
									log.debug("[DRIVE_STATUS - Device Alarm][OFF][" + DEVICE_STATUS_ALARM.DataAbnormal.getMsg() + "] " + e.toString());
									break;
								case 5: // External Battery Status
									e.setAppend("[OFF]" + DEVICE_STATUS_ALARM.ExternalBatteryStatus.getMsg());
									log.debug("[DRIVE_STATUS - Device Alarm][OFF][" + DEVICE_STATUS_ALARM.ExternalBatteryStatus.getMsg() + "] " + e.toString());
									break;
								case 6: // High low level input
									e.setAppend("[OFF]" + DEVICE_STATUS_ALARM.HighLowLevelInput.getMsg());
									log.debug("[DRIVE_STATUS - Device Alarm][OFF][" + DEVICE_STATUS_ALARM.HighLowLevelInput.getMsg() + "] " + e.toString());
									break;
								case 7: // Voltage detect input
									e.setAppend("[OFF]" + DEVICE_STATUS_ALARM.VoltageDetectInput.getMsg());
									log.debug("[DRIVE_STATUS - Device Alarm][OFF][" + DEVICE_STATUS_ALARM.VoltageDetectInput.getMsg() + "] " + e.toString());
									break;
								default:
									break;
								}

								meterAlarmLog.add(e);
							}
						}

						meter.setMeterError(deviceStatusBitString);
					}
				}

				result.remove(OBIS.DRIVE_STATUS.getCode());
			}

			// 원래 코드 이벤트 잘 저장되면 아래 삭제할것            
			//            map = (Map<String, Object>)result.get(OBIS.DRIVE_STATUS.getCode());
			//            if (map != null) {
			//	        	Object obj = map.get(OBIS.DRIVE_STATUS.getName());
			//	        	if (obj != null) deviceStatus = ((OCTET)obj).getValue();	        	
			//	        	devieStatusStr = DLMSTable.getOBIS_DRIVE_STATUS((OCTET)obj);
			//	        	log.debug("DRIVE_STATUS[" + Hex.decode(deviceStatus) + "], OBIS_DRIVE_STATUS[" + devieStatusStr + "]");
			//	        	data.put(OBIS.DRIVE_STATUS.getName(), devieStatusStr);
			//	        	result.remove(OBIS.DRIVE_STATUS.getCode());
			//            }
			//            for(DEVICE_STATUS_ALARM alarm : DEVICE_STATUS_ALARM.values()){
			//            	if(devieStatusStr.indexOf(alarm.getMsg()) >= 0){
			//    	        	log.debug("OBIS_DRIVE_STATUS[" + devieStatusStr + "] => " + EVENT_LOG.DeviceAlarm.name() + "[alarmTime=" + meterTime + ", value=" + alarm.getMsg()+"]");
			//    	            EventLogData e = new EventLogData();
			//    	            String hhmmss = meterTime.substring(8);
			//    	            e.setDate(meterTime.substring(0,8)); // :date= 제거
			//    	            e.setTime(hhmmss.substring(0, 6));
			//    	            e.setFlag(EVENT_LOG.DeviceAlarm.getFlag());
			//    	            e.setKind("STE");
			//    	            e.setMsg(EVENT_LOG.DeviceAlarm.getMsg());
			//    	            e.setAppend(alarm.getMsg());
			//    		        meterAlarmLog.add(e);
			//            	}            	
			//            }

			/*
			 * 0.0.96.10.7.255 MEASUREMENT_STATUS : Measurement Alarm Message 처리
			 * MeterEvent Log 로 저장 하지만 알람정보 자체가 시간값이 없기때문에 미터 시간 기준으로 저장한다.
			 * DR 미터만 이용하고 CT/CP미터는 새로 추가된 1.0.99.1.2.255 Measurement event log 를 이용하도록 한다.
			 */
			// 원래 코드 나중에 잘되면 삭제할것            
			//            map = (Map<String, Object>)result.get(OBIS.MEASUREMENT_STATUS.getCode());
			//            if (map != null) {
			//	        	Object obj = map.get(OBIS.MEASUREMENT_STATUS.getName());
			//	        	if (obj != null) measurementStatus = ((OCTET)obj).getValue();	        	
			//	        	measureStatusStr = DLMSTable.getOBIS_MEASUREMENT_STATUS((OCTET)obj);
			//	        	log.debug("MEASUREMENT_STATUS[" + Hex.decode(measurementStatus) + "], OBIS_MEASUREMENT_STATUS[" + measureStatusStr + "]");
			//	        	data.put(OBIS.MEASUREMENT_STATUS.getName(), measureStatusStr);                            
			//	        	result.remove(OBIS.MEASUREMENT_STATUS.getCode());
			//            }
			//            
			//            String meterName = meter.getModel().getName();
			//            if("LSIQ-3PCT".equals(meterName) || "LSIQ-3PCV".equals(meterName)) {
			//            	/*
			//            	 * Measurement event log(1.0.99.1.2.255) 이용하는 것으로 변경
			//            	 */
			//            }else{
			//                //0000000201860980
			//                for(MEASUREMENT_STATUS_ALARM alarm : MEASUREMENT_STATUS_ALARM.values()){
			//                	if(measureStatusStr.indexOf(alarm.getMsg()) >= 0){
			//        	        	log.debug("OBIS_MEASUREMENT_STATUS[" + measureStatusStr + "]=> " + EVENT_LOG.MeasurementAlarm.name() + "[alarmTime=" + meterTime + ", value=" + alarm.getMsg()+"]");
			//        	            EventLogData e = new EventLogData();
			//        	            String hhmmss = meterTime.substring(8);
			//        	            e.setDate(meterTime.substring(0,8)); // :date= 제거
			//        	            e.setTime(hhmmss.substring(0, 6));
			//        	            e.setFlag(EVENT_LOG.MeasurementAlarm.getFlag());
			//        	            e.setKind("STE");
			//        	            e.setMsg(EVENT_LOG.MeasurementAlarm.getMsg());
			//        	            e.setAppend(alarm.getMsg());
			//        		        meterAlarmLog.add(e);
			//                	}            	
			//                }            	
			//            }

			map = (Map<String, Object>) result.get(OBIS.MEASUREMENT_STATUS.getCode());
			log.debug("## MEASUREMENT_STATUS(Measurement Alarm Message) Eevnt count = " + (map == null ? 0 : map.size()));
			if (map != null) {
				log.debug("[MEASUREMENT_STATUS] => " + map.toString());
				Object obj = map.get(OBIS.MEASUREMENT_STATUS.getName());
				if (obj != null) {
					/*
					 * CT/CP 미터의 경우 Measurement event log(1.0.99.1.2.255) 이용하는 것으로 변경  
					 */
					String meterName = meter.getModel().getName();
					if (!"LSIQ-3PCT".equals(meterName) && !"LSIQ-3PCV".equals(meterName)) {
						//measurementStatus = ((OCTET)obj).getValue(); // 8byte
						measurementStatus = DataUtil.readByteString(obj.toString()); // 8byte HEX code
						measurementStatusBitString = DataUtil.byteArrayToBinaryString(measurementStatus);
						measurementStatusBitString = measurementStatusBitString.substring(21, measurementStatusBitString.length()); // length를 43으로 맞춤. 나머지는 Reserved이기때문
						log.debug("[MEASUREMENT_STATUS:BitString] => " + measurementStatusBitString);

						//  Measurement Alarm 의 상태값을 비교해서 저장하기 위해 FRIENDLY_NAME 컬럼을 사용한다. 
						EnergyMeter meter = (EnergyMeter) this.getMeter();
						if (meter.getFriendlyName() != null && meter.getFriendlyName().length() != 43) { // 43bit
							meter.setFriendlyName(null);
						}

						// MEASUREMENT_STATUS 값이 없거나 변경됬을때만 이벤트로 등록한다.
						if (meter.getFriendlyName() == null || !meter.getFriendlyName().equals(measurementStatusBitString)) {
							int prevState = 0;
							int curState = 0;

							for (int flag = 0; flag < measurementStatusBitString.length(); flag++) {
								if (meter.getFriendlyName() == null) {
									prevState = 0;
								} else {
									prevState = Integer.parseInt(meter.getFriendlyName().substring(flag, flag + 1));
								}

								curState = Integer.parseInt(measurementStatusBitString.substring(flag, flag + 1));
								if (prevState == 0 && curState == 1) {
									e = new EventLogData();
									e.setDate(setDate); // :date= 제거
									e.setTime(setTime);
									e.setFlag(EVENT_LOG.MeasurementAlarm.getFlag());
									e.setKind("STE");
									e.setMsg(EVENT_LOG.MeasurementAlarm.getMsg());

									switch (flag) {
									case 0: // L1 voltage loss
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L1VoltageLoss.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L1VoltageLoss.getMsg() + "] " + e.toString());
										break;
									case 1: // L2 voltage loss
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L2VoltageLoss.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L2VoltageLoss.getMsg() + "] " + e.toString());
										break;
									case 2: // L3 voltage loss
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L3VoltageLoss.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L3VoltageLoss.getMsg() + "] " + e.toString());
										break;
									case 3: // L1 current loss
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L1CurrentLoss.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L1CurrentLoss.getMsg() + "] " + e.toString());
										break;
									case 4: // L2 current loss
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L2CurrentLoss.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L2CurrentLoss.getMsg() + "] " + e.toString());
										break;
									case 5: // L3 current loss
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L3CurrentLoss.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L3CurrentLoss.getMsg() + "] " + e.toString());
										break;
									case 6: // L1 voltage cut
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L1VoltageCut.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L1VoltageCut.getMsg() + "] " + e.toString());
										break;
									case 7: // L2 voltage cut
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L2VoltageCut.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L2VoltageCut.getMsg() + "] " + e.toString());
										break;
									case 8: // L3 voltage cut
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L3VoltageCut.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L3VoltageCut.getMsg() + "] " + e.toString());
										break;
									case 9: // Voltage reverse phase sequence
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.VoltageReversePhaseSequence.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.VoltageReversePhaseSequence.getMsg() + "] " + e.toString());
										break;
									case 10: // Current reverse phase sequence
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.CurrentReversePhaseSequence.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.CurrentReversePhaseSequence.getMsg() + "] " + e.toString());
										break;
									case 11: // Voltage asymmetric
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.VoltageAsymmetric.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.VoltageAsymmetric.getMsg() + "] " + e.toString());
										break;
									case 12: // Current asymmetric
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.CurrentAsymmetric.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.CurrentAsymmetric.getMsg() + "] " + e.toString());
										break;
									case 13: // L1 over current
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L1OverCurrent.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L1OverCurrent.getMsg() + "] " + e.toString());
										break;
									case 14: // L2 over current
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L2OverCurrent.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L2OverCurrent.getMsg() + "] " + e.toString());
										break;
									case 15: // L3 over current
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L3OverCurrent.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L3OverCurrent.getMsg() + "] " + e.toString());
										break;
									case 16: // L1 current cut
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L1CurrentCut.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L1CurrentCut.getMsg() + "] " + e.toString());
										break;
									case 17: // L2 current cut
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L2CurrentCut.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L2CurrentCut.getMsg() + "] " + e.toString());
										break;
									case 18: // L3 current cut
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L3CurrentCut.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L3CurrentCut.getMsg() + "] " + e.toString());
										break;
									case 19: // L1 over voltage
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L1OverVoltage.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L1OverVoltage.getMsg() + "] " + e.toString());
										break;
									case 20: // L2 over voltage
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L2OverVoltage.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L2OverVoltage.getMsg() + "] " + e.toString());
										break;
									case 21: // L3 over voltage
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L3OverVoltage.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L3OverVoltage.getMsg() + "] " + e.toString());
										break;
									case 22: // L1 under voltage
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L1UnderVoltage.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L1UnderVoltage.getMsg() + "] " + e.toString());
										break;
									case 23: // L2 under voltage
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L2UnderVoltage.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L2UnderVoltage.getMsg() + "] " + e.toString());
										break;
									case 24: // L3 under voltage
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L3UnderVoltage.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L3UnderVoltage.getMsg() + "] " + e.toString());
										break;
									case 25: // All phases voltage loss
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.AllPhasesVoltageLoss.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.AllPhasesVoltageLoss.getMsg() + "] " + e.toString());
										break;
									case 26: // L1 over load
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L1OverLoad.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L1OverLoad.getMsg() + "] " + e.toString());
										break;
									case 27: // L2 over load
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L2OverLoad.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L2OverLoad.getMsg() + "] " + e.toString());
										break;
									case 28: // L3 over load
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L3OverLoad.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L3OverLoad.getMsg() + "] " + e.toString());
										break;
									case 29: // Total power factor exceeded
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.TotalPowerFactorExceeded.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.TotalPowerFactorExceeded.getMsg() + "] " + e.toString());
										break;
									case 30: // L1 voltage super top limit
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L1VoltageSuperTopLimit.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L1VoltageSuperTopLimit.getMsg() + "] " + e.toString());
										break;
									case 31: // L2 voltage super top limit
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L2VoltageSuperTopLimit.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L2VoltageSuperTopLimit.getMsg() + "] " + e.toString());
										break;
									case 32: // L3 voltage super top limit
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L3VoltageSuperTopLimit.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L3VoltageSuperTopLimit.getMsg() + "] " + e.toString());
										break;
									case 33: // L1 voltage qualification
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L1VoltageQualification.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L1VoltageQualification.getMsg() + "] " + e.toString());
										break;
									case 34: // L2 voltage qualification
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L2VoltageQualification.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L2VoltageQualification.getMsg() + "] " + e.toString());
										break;
									case 35: // L3 voltage qualification
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L3VoltageQualification.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L3VoltageQualification.getMsg() + "] " + e.toString());
										break;
									case 36: // L1 voltage super low limit
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L1VoltageSuperLowLimit.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L1VoltageSuperLowLimit.getMsg() + "] " + e.toString());
										break;
									case 37: // L2 voltage super low limit
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L2VoltageSuperLowLimit.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L2VoltageSuperLowLimit.getMsg() + "] " + e.toString());
										break;
									case 38: // L3 voltage super low limit
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L3VoltageSuperLowLimit.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L3VoltageSuperLowLimit.getMsg() + "] " + e.toString());
										break;
									case 39: // Neutral current unbalance
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.NeutralCurrentUnbalance.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.NeutralCurrentUnbalance.getMsg() + "] " + e.toString());
										break;
									case 40: // L1 reverse current
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L1ReverseCurrent.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L1ReverseCurrent.getMsg() + "] " + e.toString());
										break;
									case 41: // L2 reverse current
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L2ReverseCurrent.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L2ReverseCurrent.getMsg() + "] " + e.toString());
										break;
									case 42: // L3 reverse current
										e.setAppend("[ON]" + MEASUREMENT_STATUS_ALARM.L3ReverseCurrent.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][ON][" + MEASUREMENT_STATUS_ALARM.L3ReverseCurrent.getMsg() + "] " + e.toString());
										break;
									default:
										break;
									}
									meterAlarmLog.add(e);
								} else if (prevState == 1 && curState == 0) {
									e = new EventLogData();
									e.setDate(setDate); // :date= 제거
									e.setTime(setTime);
									e.setFlag(EVENT_LOG.MeasurementAlarm.getFlag());
									e.setKind("STE");
									e.setMsg(EVENT_LOG.MeasurementAlarm.getMsg());

									switch (flag) {
									case 0: // L1 voltage loss
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L1VoltageLoss.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L1VoltageLoss.getMsg() + "] " + e.toString());
										break;
									case 1: // L2 voltage loss
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L2VoltageLoss.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L2VoltageLoss.getMsg() + "] " + e.toString());
										break;
									case 2: // L3 voltage loss
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L3VoltageLoss.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L3VoltageLoss.getMsg() + "] " + e.toString());
										break;
									case 3: // L1 current loss
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L1CurrentLoss.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L1CurrentLoss.getMsg() + "] " + e.toString());
										break;
									case 4: // L2 current loss
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L2CurrentLoss.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L2CurrentLoss.getMsg() + "] " + e.toString());
										break;
									case 5: // L3 current loss
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L3CurrentLoss.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L3CurrentLoss.getMsg() + "] " + e.toString());
										break;
									case 6: // L1 voltage cut
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L1VoltageCut.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L1VoltageCut.getMsg() + "] " + e.toString());
										break;
									case 7: // L2 voltage cut
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L2VoltageCut.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L2VoltageCut.getMsg() + "] " + e.toString());
										break;
									case 8: // L3 voltage cut
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L3VoltageCut.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L3VoltageCut.getMsg() + "] " + e.toString());
										break;
									case 9: // Voltage reverse phase sequence
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.VoltageReversePhaseSequence.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.VoltageReversePhaseSequence.getMsg() + "] " + e.toString());
										break;
									case 10: // Current reverse phase sequence
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.CurrentReversePhaseSequence.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.CurrentReversePhaseSequence.getMsg() + "] " + e.toString());
										break;
									case 11: // Voltage asymmetric
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.VoltageAsymmetric.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.VoltageAsymmetric.getMsg() + "] " + e.toString());
										break;
									case 12: // Current asymmetric
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.CurrentAsymmetric.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.CurrentAsymmetric.getMsg() + "] " + e.toString());
										break;
									case 13: // L1 over current
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L1OverCurrent.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L1OverCurrent.getMsg() + "] " + e.toString());
										break;
									case 14: // L2 over current
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L2OverCurrent.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L2OverCurrent.getMsg() + "] " + e.toString());
										break;
									case 15: // L3 over current
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L3OverCurrent.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L3OverCurrent.getMsg() + "] " + e.toString());
										break;
									case 16: // L1 current cut
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L1CurrentCut.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L1CurrentCut.getMsg() + "] " + e.toString());
										break;
									case 17: // L2 current cut
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L2CurrentCut.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L2CurrentCut.getMsg() + "] " + e.toString());
										break;
									case 18: // L3 current cut
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L3CurrentCut.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L3CurrentCut.getMsg() + "] " + e.toString());
										break;
									case 19: // L1 over voltage
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L1OverVoltage.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L1OverVoltage.getMsg() + "] " + e.toString());
										break;
									case 20: // L2 over voltage
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L2OverVoltage.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L2OverVoltage.getMsg() + "] " + e.toString());
										break;
									case 21: // L3 over voltage
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L3OverVoltage.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L3OverVoltage.getMsg() + "] " + e.toString());
										break;
									case 22: // L1 under voltage
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L1UnderVoltage.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L1UnderVoltage.getMsg() + "] " + e.toString());
										break;
									case 23: // L2 under voltage
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L2UnderVoltage.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L2UnderVoltage.getMsg() + "] " + e.toString());
										break;
									case 24: // L3 under voltage
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L3UnderVoltage.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L3UnderVoltage.getMsg() + "] " + e.toString());
										break;
									case 25: // All phases voltage loss
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.AllPhasesVoltageLoss.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.AllPhasesVoltageLoss.getMsg() + "] " + e.toString());
										break;
									case 26: // L1 over load
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L1OverLoad.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L1OverLoad.getMsg() + "] " + e.toString());
										break;
									case 27: // L2 over load
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L2OverLoad.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L2OverLoad.getMsg() + "] " + e.toString());
										break;
									case 28: // L3 over load
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L3OverLoad.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L3OverLoad.getMsg() + "] " + e.toString());
										break;
									case 29: // Total power factor exceeded
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.TotalPowerFactorExceeded.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.TotalPowerFactorExceeded.getMsg() + "] " + e.toString());
										break;
									case 30: // L1 voltage super top limit
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L1VoltageSuperTopLimit.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L1VoltageSuperTopLimit.getMsg() + "] " + e.toString());
										break;
									case 31: // L2 voltage super top limit
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L2VoltageSuperTopLimit.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L2VoltageSuperTopLimit.getMsg() + "] " + e.toString());
										break;
									case 32: // L3 voltage super top limit
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L3VoltageSuperTopLimit.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L3VoltageSuperTopLimit.getMsg() + "] " + e.toString());
										break;
									case 33: // L1 voltage qualification
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L1VoltageQualification.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L1VoltageQualification.getMsg() + "] " + e.toString());
										break;
									case 34: // L2 voltage qualification
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L2VoltageQualification.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L2VoltageQualification.getMsg() + "] " + e.toString());
										break;
									case 35: // L3 voltage qualification
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L3VoltageQualification.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L3VoltageQualification.getMsg() + "] " + e.toString());
										break;
									case 36: // L1 voltage super low limit
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L1VoltageSuperLowLimit.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L1VoltageSuperLowLimit.getMsg() + "] " + e.toString());
										break;
									case 37: // L2 voltage super low limit
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L2VoltageSuperLowLimit.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L2VoltageSuperLowLimit.getMsg() + "] " + e.toString());
										break;
									case 38: // L3 voltage super low limit
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L3VoltageSuperLowLimit.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L3VoltageSuperLowLimit.getMsg() + "] " + e.toString());
										break;
									case 39: // Neutral current unbalance
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.NeutralCurrentUnbalance.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.NeutralCurrentUnbalance.getMsg() + "] " + e.toString());
										break;
									case 40: // L1 reverse current
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L1ReverseCurrent.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L1ReverseCurrent.getMsg() + "] " + e.toString());
										break;
									case 41: // L2 reverse current
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L2ReverseCurrent.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L2ReverseCurrent.getMsg() + "] " + e.toString());
										break;
									case 42: // L3 reverse current
										e.setAppend("[OFF]" + MEASUREMENT_STATUS_ALARM.L3ReverseCurrent.getMsg());
										log.debug("[MEASUREMENT_STATUS - Measurement Alarm][OFF][" + MEASUREMENT_STATUS_ALARM.L3ReverseCurrent.getMsg() + "] " + e.toString());
										break;
									default:
										break;
									}

									meterAlarmLog.add(e);
								}
							}

							meter.setFriendlyName(measurementStatusBitString);
						}
					} else {
						/*
						 * CT/CP 미터의 경우 Measurement event log(1.0.99.1.2.255) 이용하는 것으로 변경  
						 */
					}
				}
				result.remove(OBIS.MEASUREMENT_STATUS.getCode());
			}

			log.debug("##### ALARM LOG STOP #####");
		} catch (Exception e) {
			log.error(e, e);
		}

	}

	private void setDailyBillingData() {
		log.debug("##### DailyBillingData set START #####");

		/*
		 * EVN 단상 Single Tariff미터의 경우 Cumulative active energy -import값 하나만 올라옴.
		 * 나머지미터들은 모두 동일
		    Clock
			Cumulative active energy -import
			Cumulative active energy -import rate 1
			Cumulative active energy -import rate 2
			Cumulative active energy -import rate 3 
			Cumulative reactive energy -import
			Cumulative reactive energy -import rate 1
			Cumulative reactive energy -import rate 2
			Cumulative reactive energy -import rate 3 
		 */

		dailyBill = new ArrayList<BillingData>();
		Double val = 0.0;
		int cnt = 0;
		Double[] ch = new Double[8];
		Map<String, Object> dailyMap = null;
		Object value = null;
		BillingData day = null;

		for (int i = 0; i < result.size(); i++) {
			if (!result.containsKey(OBIS.DAILY_LOAD_PROFILE.getCode() + "-" + i))
				break;

			dailyMap = (Map<String, Object>) result.get(OBIS.DAILY_LOAD_PROFILE.getCode() + "-" + i);

			if (dailyMap != null && dailyMap.size() > 0) {
				cnt = 0;
				while ((value = dailyMap.get("Channel[1]" + "-" + cnt)) != null) {
					if (value instanceof OCTET)
						val = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
					else if (value instanceof Long)
						val = ((Long) value).doubleValue();
					else if (value instanceof Float)
						val = ((Float) value).doubleValue();
					val *= 0.001;
					val = val / activePulseConstant;
					ch[0] = val;
					if (val > meteringValue) {
						meteringValue = val;
					}

					String dayTime = (String) dailyMap.get("DateTime" + "-" + cnt++);

					log.debug("0. [Clock] DateTime = " + dayTime + ", Metering Value = " + meteringValue);
					log.debug("1. [Cumulative active energy -import] RAWDATA = " + ch[0].toString());

					value = dailyMap.get("Channel[2]" + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							ch[1] = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							ch[1] = ((Long) value).doubleValue();
						else if (value instanceof Float)
							ch[1] = ((Float) value).doubleValue();

						ch[1] /= activePulseConstant;
						ch[1] *= 0.001;
						log.debug("2. [Cumulative active energy -import rate 1] RAWDATA = " + ch[1].toString());
					}
					value = dailyMap.get("Channel[3]" + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							ch[2] = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							ch[2] = ((Long) value).doubleValue();
						else if (value instanceof Float)
							ch[2] = ((Float) value).doubleValue();
						ch[2] /= activePulseConstant;
						ch[2] *= 0.001;
						log.debug("3. [Cumulative active energy -import rate 2] RAWDATA = " + ch[2].toString());
					}
					value = dailyMap.get("Channel[4]" + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							ch[3] = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							ch[3] = ((Long) value).doubleValue();
						else if (value instanceof Float)
							ch[3] = ((Float) value).doubleValue();
						ch[3] /= activePulseConstant;
						ch[3] *= 0.001;
						log.debug("4. [Cumulative active energy -import rate 3] RAWDATA = " + ch[3].toString());
					}
					value = dailyMap.get("Channel[5]" + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							ch[4] = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							ch[4] = ((Long) value).doubleValue();
						else if (value instanceof Float)
							ch[4] = ((Float) value).doubleValue();
						ch[4] /= reActivePulseConstant;
						ch[4] *= 0.001;
						log.debug("5. [Cumulative reactive energy -import] RAWDATA = " + ch[4].toString());
					}
					value = dailyMap.get("Channel[6]" + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							ch[5] = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							ch[5] = ((Long) value).doubleValue();
						else if (value instanceof Float)
							ch[5] = ((Float) value).doubleValue();
						ch[5] /= reActivePulseConstant;
						ch[5] *= 0.001;
						log.debug("6. [Cumulative reactive energy -import rate 1] RAWDATA = " + ch[5].toString());
					}
					value = dailyMap.get("Channel[7]" + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							ch[6] = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							ch[6] = ((Long) value).doubleValue();
						else if (value instanceof Float)
							ch[6] = ((Float) value).doubleValue();
						ch[6] /= reActivePulseConstant;
						ch[6] *= 0.001;
						log.debug("7. [Cumulative reactive energy -import rate 2] RAWDATA = " + ch[6].toString());
					}
					value = dailyMap.get("Channel[8]" + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							ch[7] = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							ch[7] = ((Long) value).doubleValue();
						else if (value instanceof Float)
							ch[7] = ((Float) value).doubleValue();
						ch[7] /= reActivePulseConstant;
						ch[7] *= 0.001;
						log.debug("8. [Cumulative reactive energy -import rate 3] RAWDATA = " + ch[7].toString());
					}
					/********************************************************/

					day = new BillingData();
					day.setBillingTimestamp(dayTime);
					day.setActiveEnergyImportRateTotal(ch[0]); // Cumulative active energy -import
					day.setActiveEnergyImportRate1(ch[1]); // Cumulative active energy -import rate 1
					day.setActiveEnergyImportRate2(ch[2]); // Cumulative active energy -import rate 2
					day.setActiveEnergyImportRate3(ch[3]); // Cumulative active energy -import rate 3

					day.setReactiveEnergyRateTotal(ch[4]); // Cumulative reactive energy -import 
					day.setReactiveEnergyRate1(ch[5]); // Cumulative reactive energy -import rate 1
					day.setReactiveEnergyRate1(ch[6]); // Cumulative reactive energy -import rate 2
					day.setReactiveEnergyRate1(ch[7]); // Cumulative reactive energy -import rate 3

					dailyBill.add(day);
				}
			}
		}
		log.debug("##### DailyBillingData set STOP #####");
	}

	private void setMonthlyBillingData() {
		log.debug("##### MonthlyBillingData set START #####");

		/* 
		 * EVN 단상 Single Tariff미터의 경우 Cumulative active energy -import값 하나만 올라옴.
		 * 나머지미터들은 모두 동일
		 * 
			Clock
			Cumulative active energy -import
			Cumulative active energy -import rate 1
			Cumulative active energy -import rate 2
			Cumulative active energy -import rate 3 
			Cumulative reactive energy -import
			Cumulative reactive energy -import rate 1
			Cumulative reactive energy -import rate 2
			Cumulative reactive energy -import rate 3 
			Total maximum demand +A
			Total maximum demand +A
			Total maximum demand +A T1
			Total maximum demand +A T1
			Total maximum demand +A T2
			Total maximum demand +A T2
			Total maximum demand +A T3
			Total maximum demand +A T3
			
		 */

		monthlyBill = new ArrayList<BillingData>();
		Double val = 0.0;
		int cnt = 0;
		Double[] ch = new Double[12];
		Map<String, Object> dailyMap = null;
		Object value = null;
		BillingData month = null;

		for (int i = 0; i < result.size(); i++) {
			if (!result.containsKey(OBIS.MONTHLY_BILLING.getCode() + "-" + i))
				break;

			dailyMap = (Map<String, Object>) result.get(OBIS.MONTHLY_BILLING.getCode() + "-" + i);

			if (dailyMap != null && dailyMap.size() > 0) {
				cnt = 0;
				while ((value = dailyMap.get("Channel[1]" + "-" + cnt)) != null) {
					if (value instanceof OCTET)
						val = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
					else if (value instanceof Long)
						val = ((Long) value).doubleValue();
					else if (value instanceof Float)
						val = ((Float) value).doubleValue();
					val *= 0.001;
					val = val / activePulseConstant;
					ch[0] = val; // Cumulative active energy -import 

					String dayTime = (String) dailyMap.get("DateTime" + "-" + cnt++);

					log.debug("0. [Clock] DateTime = " + dayTime);
					log.debug("1. [Cumulative active energy -import] RAWDATA = " + ch[0].toString());

					value = dailyMap.get("Channel[2]" + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							ch[1] = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							ch[1] = ((Long) value).doubleValue();
						else if (value instanceof Float)
							ch[1] = ((Float) value).doubleValue();

						ch[1] /= activePulseConstant;
						ch[1] *= 0.001; // Cumulative active energy -import rate 1
						log.debug("2. [Cumulative active energy -import rate 1] RAWDATA = " + ch[1].toString());
					}
					value = dailyMap.get("Channel[3]" + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							ch[2] = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							ch[2] = ((Long) value).doubleValue();
						else if (value instanceof Float)
							ch[2] = ((Float) value).doubleValue();
						ch[2] /= activePulseConstant;
						ch[2] *= 0.001; // Cumulative active energy -import rate 2
						log.debug("3. [Cumulative active energy -import rate 2] RAWDATA = " + ch[2].toString());
					}
					value = dailyMap.get("Channel[4]" + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							ch[3] = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							ch[3] = ((Long) value).doubleValue();
						else if (value instanceof Float)
							ch[3] = ((Float) value).doubleValue();
						ch[3] /= activePulseConstant;
						ch[3] *= 0.001; // Cumulative active energy -import rate 3
						log.debug("4. [Cumulative active energy -import rate 3] RAWDATA = " + ch[3].toString());
					}
					value = dailyMap.get("Channel[5]" + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							ch[4] = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							ch[4] = ((Long) value).doubleValue();
						else if (value instanceof Float)
							ch[4] = ((Float) value).doubleValue();
						ch[4] /= reActivePulseConstant;
						ch[4] *= 0.001; // Cumulative reactive energy -import
						log.debug("5. [Cumulative reactive energy -import] RAWDATA = " + ch[4].toString());
					}
					value = dailyMap.get("Channel[6]" + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							ch[5] = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							ch[5] = ((Long) value).doubleValue();
						else if (value instanceof Float)
							ch[5] = ((Float) value).doubleValue();
						ch[5] /= reActivePulseConstant;
						ch[5] *= 0.001; // Cumulative reactive energy -import rate 1
						log.debug("6. [Cumulative reactive energy -import rate 1] RAWDATA = " + ch[5].toString());
					}
					value = dailyMap.get("Channel[7]" + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							ch[6] = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							ch[6] = ((Long) value).doubleValue();
						else if (value instanceof Float)
							ch[6] = ((Float) value).doubleValue();
						ch[6] /= reActivePulseConstant;
						ch[6] *= 0.001; // Cumulative reactive energy -import rate 2
						log.debug("7. [Cumulative reactive energy -import rate 2] RAWDATA = " + ch[6].toString());
					}
					value = dailyMap.get("Channel[8]" + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							ch[7] = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							ch[7] = ((Long) value).doubleValue();
						else if (value instanceof Float)
							ch[7] = ((Float) value).doubleValue();
						ch[7] /= reActivePulseConstant;
						ch[7] *= 0.001; // Cumulative reactive energy -import rate 3
						log.debug("8. [Cumulative reactive energy -import rate 2] RAWDATA = " + ch[7].toString());
					}

					value = dailyMap.get("Channel[9]" + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							ch[8] = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							ch[8] = ((Long) value).doubleValue();
						else if (value instanceof Float)
							ch[8] = ((Float) value).doubleValue();
						//ch[8] /= reActivePulseConstant;
						ch[8] *= 0.001; // Total maximum demand +A
						log.debug("9. [Total maximum demand +A] RAWDATA = " + ch[8].toString());
					}
					
					String totalMaximumDemandATime = String.valueOf(dailyMap.get("Channel[10]" + "-" + cnt));
					if (totalMaximumDemandATime != null && !totalMaximumDemandATime.equals("")) {
						log.debug("10. [Total maximum demand +A Time] RAWDATA = " + totalMaximumDemandATime);
					}
					value = dailyMap.get("Channel[11]" + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							ch[9] = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							ch[9] = ((Long) value).doubleValue();
						else if (value instanceof Float)
							ch[9] = ((Float) value).doubleValue();
						ch[9] /= reActivePulseConstant;
						ch[9] *= 0.001; // Total maximum demand +A T1
						log.debug("11. [Total maximum demand +A T1] RAWDATA = " + ch[9].toString());
					}
					String totalMaximumDemandAT1Time = String.valueOf(dailyMap.get("Channel[12]" + "-" + cnt));
					if (totalMaximumDemandAT1Time != null && !totalMaximumDemandAT1Time.equals("")) {
						log.debug("12. [Total maximum demand +A T1 Time] RAWDATA = " + totalMaximumDemandAT1Time);
					}
					value = dailyMap.get("Channel[13]" + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							ch[10] = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							ch[10] = ((Long) value).doubleValue();
						else if (value instanceof Float)
							ch[10] = ((Float) value).doubleValue();
						ch[10] /= activePulseConstant;
						ch[10] *= 0.001; // Total maximum demand +A T2
						log.debug("13. [Total maximum demand +A T2] RAWDATA = " + ch[10].toString());
					}
					String totalMaximumDemandAT2Time = String.valueOf(dailyMap.get("Channel[14]" + "-" + cnt));
					if (totalMaximumDemandAT2Time != null && !totalMaximumDemandAT2Time.equals("")) {
						log.debug("14. [Total maximum demand +A T2 Time] RAWDATA = " + totalMaximumDemandAT2Time);
					}
					value = dailyMap.get("Channel[15]" + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							ch[11] = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							ch[11] = ((Long) value).doubleValue();
						else if (value instanceof Float)
							ch[11] = ((Float) value).doubleValue();
						ch[11] /= activePulseConstant;
						ch[11] *= 0.001; // Total maximum demand +A T3
						log.debug("15. [Total maximum demand +A T3] RAWDATA = " + ch[11].toString());
					}
					String totalMaximumDemandAT3Time = String.valueOf(dailyMap.get("Channel[16]" + "-" + cnt));
					if (totalMaximumDemandAT3Time != null && !totalMaximumDemandAT3Time.equals("")) {
						log.debug("15. [Total maximum demand +A T3 Time] RAWDATA = " + totalMaximumDemandAT3Time);
					}
					/********************************************************/

					month = new BillingData();
					month.setBillingTimestamp(dayTime);       // Clock
					month.setActiveEnergyRateTotal(ch[0]);    // Cumulative active energy -import
					month.setActiveEnergyRate1(ch[1]);        // Cumulative active energy -import rate 1
					month.setActiveEnergyRate2(ch[2]);        // Cumulative active energy -import rate 2
					month.setActiveEnergyRate3(ch[3]);		  // Cumulative active energy -import rate 3

					month.setReactiveEnergyRateTotal(ch[4]);  // Cumulative reactive energy -import
					month.setReactiveEnergyRate1(ch[5]);      // Cumulative reactive energy -import rate 1
					month.setReactiveEnergyRate2(ch[6]);      // Cumulative reactive energy -import rate 1
					month.setReactiveEnergyRate3(ch[7]);      // Cumulative reactive energy -import rate 1

					month.setActivePowerMaxDemandRateTotal(ch[8]);     // Total maximum demand +A
					month.setActivePowerDemandMaxTimeRateTotal(totalMaximumDemandATime);     // Total maximum demand +A
					month.setMaxDmdkVah1Rate1(ch[9]);		  // Total maximum demand +A T1
					month.setActivePowerDemandMaxTimeRate1(totalMaximumDemandAT1Time);       // Total maximum demand +A T1
					month.setMaxDmdkVah1Rate2(ch[10]);		  // Total maximum demand +A T2
					month.setActivePowerDemandMaxTimeRate2(totalMaximumDemandAT2Time);       // Total maximum demand +A T2
					month.setMaxDmdkVah1Rate3(ch[11]);		  // Total maximum demand +A T3
					month.setActivePowerDemandMaxTimeRate3(totalMaximumDemandAT3Time);       // Total maximum demand +A T3

					monthlyBill.add(month);
				}
			}

		}
		log.debug("##### MonthlyBillingData set STOP #####");
	}

	@Override
	public void setFlag(int flag) {

	}

	@Override
	public String toString() {

		return null;
	}

	public LPData[] getLPData() {

		return lpData;
	}

	public double getOBISScalar(int channel) {
		double scalar = 1;

		if (this.meter != null) {
			String meterName = meter.getModel().getName();
			String code = "";

			if ("LSKLV3405CP-010M".equals(meterName)) {
				switch (channel) {
				case 1:
					code = DLMSSCALAR.OBIS.CONSUMPTION_ACTIVEENERGY_IMPORT.getCode();
					break;
				case 2:
					code = DLMSSCALAR.OBIS.CONSUMPTION_ACTIVEENERGY_EXPORT.getCode();
					break;
				case 3:
					code = DLMSSCALAR.OBIS.CONSUMPTION_REACTIVEENERGY_IMPORT.getCode();
					break;
				case 4:
					code = DLMSSCALAR.OBIS.CONSUMPTION_REACTIVEENERGY_EXPORT.getCode();
					break;
				}
			} else {
				switch (channel) {
				case 1:
					code = DLMSSCALAR.OBIS.CONSUMPTION_ACTIVEENERGY_IMPORT.getCode();
					break;
				case 2:
					code = DLMSSCALAR.OBIS.CONSUMPTION_ACTIVEENERGY_EXPORT.getCode();
					break;
				default:
					break;
				}
			}

			scalar = DLMSSCALAR.OBISSACLAR.getOBISScalar(code);
		}

		return scalar;
	}

	//    public void setLPData() {
	//    	log.debug("  ");
	//    	log.debug("##### LPData set START #####");
	//        try {
	//            List<LPData> lpDataList = new ArrayList<LPData>();
	//            
	//            Double lp = 0.0;
	//            Double lpValue = 0.0;
	//            Object value = null;
	//            Map<String, Object> lpMap = null;
	//            int cnt = 0;
	//            LPData _lpData = null;
	//
	//            //Double[] ch = new Double[]{0d,0d,0d,0d,0d,0d};
	//            //Double[] ch = new Double[]{0d,0d,0d};
	//            
	//            Double[] ch;
	//            Double chValue = 0.0;
	//            ArrayList<Double> chList = new ArrayList<Double>();
	//                        
	//            for (int i = 0; i < result.size(); i++) {
	//                if (!result.containsKey(OBIS.ENERGY_LOAD_PROFILE.getCode() + "-" + i))
	//                    break;
	//                
	//                lpMap = (Map<String, Object>) result.get(OBIS.ENERGY_LOAD_PROFILE.getCode() + "-" + i);
	//                cnt = 0;                
	//                
	//                if((value=lpMap.get("LpInterval")) != null){
	//                	lpInterval = Integer.parseInt(value.toString());
	//                }
	//                
	//                while ((value=lpMap.get("Channel[1]"+"-"+cnt)) != null) {
	//                	chList.clear();
	//                	
	//                	log.debug("channel[1] VALUE RAWDATA="+value.toString());
	//                	if (value instanceof OCTET)
	//                		chValue = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
	//                	else if (value instanceof Long)
	//                		chValue = ((Long)value).doubleValue();
	//                	else if (value instanceof Float)
	//                		chValue = ((Float)value).doubleValue();
	//                	
	//                	chValue = (chValue * getOBISScalar(1)) * 0.001; //Cumulative active energy -import
	//                	lp = chValue;
	//                	lpValue=chValue; //cumulative active energy - import
	//                	chList.add(chValue);
	//                	
	//                    value = lpMap.get("Channel[2]"+"-"+cnt);
	//                    if (value != null) {
	//                    	if (value instanceof OCTET)
	//                    		chValue = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
	//                    	else if (value instanceof Long)
	//                    		chValue = ((Long)value).doubleValue();
	//                    	else if (value instanceof Float)
	//                    		chValue = ((Float)value).doubleValue();
	//
	//                    	chValue = (chValue * getOBISScalar(2)) * 0.001; //Total reactive energy
	//                    	chList.add(chValue); 
	//                    }
	//                    value = lpMap.get("Channel[3]"+"-"+cnt);
	//                    if (value != null) {
	//                    	if (value instanceof OCTET)
	//                    		chValue = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
	//                    	else if (value instanceof Long)
	//                    		chValue = ((Long)value).doubleValue();
	//                    	else if (value instanceof Float)
	//                    		chValue = ((Float)value).doubleValue();
	//
	//                    	chValue = (chValue * getOBISScalar(3)) * 0.001; //Total import apparent energy
	//                    	chList.add(chValue); 
	//                    }
	//                    value = lpMap.get("Channel[4]"+"-"+cnt);
	//                    if (value != null) {
	//                    	if (value instanceof OCTET)
	//                    		chValue = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
	//                    	else if (value instanceof Long)
	//                    		chValue = ((Long)value).doubleValue();
	//                    	else if (value instanceof Float)
	//                    		chValue = ((Float)value).doubleValue();
	//
	//                    	chValue = (chValue * getOBISScalar(4)) * 0.001; //total maximum demand +A
	//                    	chList.add(chValue); 
	//                    }
	//                    value = lpMap.get("Channel[5]"+"-"+cnt);
	//                    if (value != null) {
	//                    	if (value instanceof OCTET)
	//                    		chValue = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
	//                    	else if (value instanceof Long)
	//                    		chValue = ((Long)value).doubleValue();
	//                    	else if (value instanceof Float)
	//                    		chValue = ((Float)value).doubleValue();
	//                    	
	//                    	chValue = (chValue * getOBISScalar(5)) * 0.001; //total apparent maximum demand +R
	//                    	chList.add(chValue); 
	//                    }                    
	//                    value = lpMap.get("Channel[6]"+"-"+cnt);
	//                    if (value != null) {
	//                    	if (value instanceof OCTET)
	//                    		chValue = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
	//                    	else if (value instanceof Long)
	//                    		chValue = ((Long)value).doubleValue();
	//                    	else if (value instanceof Float)
	//                    		chValue = ((Float)value).doubleValue();
	//
	//                    	chValue = (chValue * getOBISScalar(6)) * 0.001; //total maxmum demand +
	//                    	chList.add(chValue); 
	//                    }                    
	//                    value = lpMap.get("Channel[7]"+"-"+cnt);
	//                    if (value != null) {
	//                    	if (value instanceof OCTET)
	//                    		chValue = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
	//                    	else if (value instanceof Long)
	//                    		chValue = ((Long)value).doubleValue();
	//                    	else if (value instanceof Float)
	//                    		chValue = ((Float)value).doubleValue();
	//
	//                    	chValue = (chValue * getOBISScalar(7)) * 0.001; //total apparent maximum demand
	//                    	chList.add(chValue);
	//                    }
	//                    value = lpMap.get("Channel[8]"+"-"+cnt);
	//                    if (value != null) {
	//                    	if (value instanceof OCTET)
	//                    		chValue = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
	//                    	else if (value instanceof Long)
	//                    		chValue = ((Long)value).doubleValue();
	//                    	else if (value instanceof Float)
	//                    		chValue = ((Float)value).doubleValue();
	//                    	
	//                    	chValue = (chValue * getOBISScalar(8)) * 0.001;; //Voltage, phase B
	//                    	chList.add(chValue); 
	//                    }
	//                    value = lpMap.get("Channel[9]"+"-"+cnt);
	//                    if (value != null) {
	//                    	if (value instanceof OCTET)
	//                    		chValue = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
	//                    	else if (value instanceof Long)
	//                    		chValue = ((Long)value).doubleValue();
	//                    	else if (value instanceof Float)
	//                    		chValue = ((Float)value).doubleValue();
	//
	//                    	chValue = (chValue * getOBISScalar(9)) * 0.001;; //Current phase B
	//                    	chList.add(chValue); 
	//                    }
	//                    value = lpMap.get("Channel[10]"+"-"+cnt);
	//                    if (value != null) {
	//                    	if (value instanceof OCTET)
	//                    		chValue = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
	//                    	else if (value instanceof Long)
	//                    		chValue = ((Long)value).doubleValue();
	//                    	else if (value instanceof Float)
	//                    		chValue = ((Float)value).doubleValue();
	//
	//                    	chValue = (chValue * getOBISScalar(10)) * 0.001;; //power factor, phase B
	//                    	chList.add(chValue); 
	//                    }
	//                    value = lpMap.get("Channel[11]"+"-"+cnt);
	//                    if (value != null) {
	//                    	if (value instanceof OCTET)
	//                    		chValue = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
	//                    	else if (value instanceof Long)
	//                    		chValue = ((Long)value).doubleValue();
	//                    	else if (value instanceof Float)
	//                    		chValue = ((Float)value).doubleValue();
	//
	//                    	chValue = (chValue * getOBISScalar(11)) * 0.001; //consumption active energy -import for LP1
	//                    	chList.add(chValue); 
	//                    }
	//                    value = lpMap.get("Channel[12]"+"-"+cnt);
	//                    if (value != null) {
	//                    	if (value instanceof OCTET)
	//                    		chValue = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
	//                    	else if (value instanceof Long)
	//                    		chValue = ((Long)value).doubleValue();
	//                    	else if (value instanceof Float)
	//                    		chValue = ((Float)value).doubleValue();
	//
	//                    	chValue = (chValue * getOBISScalar(12)) * 0.001; //consumption reactive energy -import for LP1
	//                    	chList.add(chValue); 
	//                    }
	//                    
	//                    String meterName = meter.getModel().getName();
	//                    if("LSIQ-3PCT".equals(meterName) || "LSIQ-3PCV".equals(meterName)) {
	//                    	//CT Meter일 경우 시간별 사용량 index를 0번으로 변경 후 저장
	//                    	
	//                    	double swap = chList.get(CUMULATIVE_ACTIVE_ENERY); //Cumulative active energy
	//                    	
	//                    	chList.set(CUMULATIVE_ACTIVE_ENERY, chList.get(CONSUMPTION_ACTIVE_ENERGY));
	//                    	chList.set(CONSUMPTION_ACTIVE_ENERGY, swap);
	//                    }
	//                    
	//                    log.debug("Parse Channel cnt:["+cnt+"] ,Channel Length:["+chList.size()+"]");
	//                    ch = new Double[chList.size()];
	//                    for(int j=0; j<chList.size(); j++) {
	//                    	ch[j] = chList.get(j);
	//                    }
	//                    //1P, 3P는 ch수가 3개로 고정
	//                    //3PCT, 3PCV는 ch수가 늘어날 수 있음, 현재 예상 12개
	//                    //때문에 가변적으로 늘어날 수 있도록 해야함, 추후 Saver에서 해당 채널 수 만큼 배열을 제공함
	//                    
	//                    
	//                    String lpTime = (String) lpMap.get("DateTime"+ "-" + cnt);
	//                    //lpTime = Util.addMinYymmdd(lpTime, -(this.meter.getLpInterval()));//
	//                    // 미터에서 LP시간이 00시00분부터 00시15분 사이의 데이터는 00시 15분으로 저장하기 때문에
	//                    //서버에서는 해당 기간의 사용데이터는 00시 00분으로 계산하므로 주기만큼 빼야함.
	//                    
	//                    String revisionTime = Util.getQuaterYymmddhhmm(lpTime, lpInterval);
	//                    log.debug("### LP Time editing : "+ lpTime + " => " + revisionTime);
	//                    
	//                    _lpData = new LPData(revisionTime, lp, lpValue);
	//                    _lpData.setCh(ch);
	//                    _lpData.setPF(1d);
	//                    
	//                    // Status 값 저장
	//					value = lpMap.get(ENERGY_LOAD_PROFILE.Status.getName() + "-" + cnt++);
	//					if (value != null){
	//						_lpData.setStatus(String.valueOf(value));
	//					}else {
	//						_lpData.setStatus(null);
	//					}
	//                    
	//                    
	//                    if (_lpData.getDatetime() != null && !_lpData.getDatetime().substring(0, 4).equals("1792")) {
	//                    	lpDataList.add(_lpData);
	//                    	log.debug("LP"+ cnt +" ==> " + _lpData.toString());
	//                    }
	//                }
	//            }
	//            
	//            Collections.sort(lpDataList,LPComparator.TIMESTAMP_ORDER);        
	//            lpData = checkEmptyLP(lpDataList);
	//
	//            /*
	//             * no cumulative last metering value
	//            try{
	//            	LPData[] data = lpData;
	//            	if(data != null && data.length > 0 && data[data.length-1].getCh() != null && data[data.length-1].getCh().length > 2){
	//            		meteringValue = data[data.length-1].getCh()[2].doubleValue();// LP의 최종 누적값을 설정해준다.
	//            	}
	//            }catch(Exception e){
	//            	log.warn(e,e);
	//            }
	//            */
	//
	//            
	//            //lpData = lpDataList.toArray(new LPData[0]);
	//            log.debug("##### LPData set STOP!!  Total LP Count="+lpData.length + " #####");
	//        } catch (Exception e) {
	//            log.error("DLMSLSSmartMeter setLPData Error : " + e);
	//            e.printStackTrace();
	//        }
	//    }

	public void setLPData() {
		log.debug("  ");
		log.debug("##### LPData set START #####");
		try {
			List<LPData> lpDataList = new ArrayList<LPData>();

			Double lp = 0.0;
			Double lpValue = 0.0;
			Object value = null;
			Map<String, Object> lpMap = null;
			int cnt = 0;
			LPData _lpData = null;

			//Double[] ch = new Double[]{0d,0d,0d,0d,0d,0d};
			//Double[] ch = new Double[]{0d,0d,0d};

			Double[] ch;
			Double chValue = 0.0;
			ArrayList<Double> chList = new ArrayList<Double>();

			for (int i = 0; i < result.size(); i++) {
				if (!result.containsKey(OBIS.ENERGY_LOAD_PROFILE.getCode() + "-" + i)){
					/*
					 * EVN Project 에 납품되는 LSKLV1210DR-100S 미터의 경우 EnergyLoadProfile이 없고 DailyBilling, MonthlyBilling만 있음.
					 * 그래서 DailyBilling 정보를 이용해서 LP를 생성해줌.
					 */
					if(getMeterModel().equals("LSKLV1210DR-100S")){
						if (!result.containsKey(OBIS.DAILY_LOAD_PROFILE.getCode() + "-" + i)){
							break;
						}
						lpMap = (Map<String, Object>) result.get(OBIS.DAILY_LOAD_PROFILE.getCode() + "-" + i);
						if(lpMap != null && 0 < lpMap.size()){
							lpMap = (Map<String, Object>) result.get(OBIS.DAILY_LOAD_PROFILE.getCode() + "-" + (i+1));
							if(lpMap != null){
								log.debug("### MeterModel=" + getMeterModel() + " - Save  ENERGY_LOAD_PROFILE using DAILY_LOAD_PROFILE");
								lpMap.put("LpInterval", FMPProperty.getProperty("default.meter.interval", "1440"));
							}
						}else{
							break;
						}
					}else{
						break;						
					}

				}else{
					lpMap = (Map<String, Object>) result.get(OBIS.ENERGY_LOAD_PROFILE.getCode() + "-" + i);
				}				
				
				cnt = 0;
				
				if(lpMap == null){
					break;
				}else if((value = lpMap.get("LpInterval")) != null){
					lpInterval = Integer.parseInt(value.toString());
					log.debug("### LP Interval = " + lpInterval);										
				}
				
				while ((value = lpMap.get("Channel[1]" + "-" + cnt)) != null) {
					chList.clear();

					log.debug("channel[1] VALUE RAWDATA=" + value.toString());
					if (value instanceof OCTET)
						chValue = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
					else if (value instanceof Long)
						chValue = ((Long) value).doubleValue();
					else if (value instanceof Float)
						chValue = ((Float) value).doubleValue();

					chValue = (chValue * getOBISScalar(1)) * 0.001; // Consumption active energy -import for LP1  
					lp = chValue;
					lpValue = chValue;
					chList.add(chValue);

					value = lpMap.get("Channel[2]" + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							chValue = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							chValue = ((Long) value).doubleValue();
						else if (value instanceof Float)
							chValue = ((Float) value).doubleValue();

						// Consumption active energy -export for LP1. ** LSKLV3405CP-010M 미터가 아닌경우는Consumption reactive energy -import for LP1
						chValue = (chValue * getOBISScalar(2)) * 0.001;  

						chList.add(chValue);
					}
					value = lpMap.get("Channel[3]" + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							chValue = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							chValue = ((Long) value).doubleValue();
						else if (value instanceof Float)
							chValue = ((Float) value).doubleValue();

						chValue = (chValue * getOBISScalar(3)) * 0.001; // Consumption reactive energy -import for LP1  
						chList.add(chValue);
					}
					value = lpMap.get("Channel[4]" + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							chValue = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							chValue = ((Long) value).doubleValue();
						else if (value instanceof Float)
							chValue = ((Float) value).doubleValue();

						chValue = (chValue * getOBISScalar(4)) * 0.001; // Consumption reactive energy -export for LP1 
						chList.add(chValue);
					}

					log.debug("Parse Channel cnt:[" + cnt + "] ,Channel Length:[" + chList.size() + "]");
					ch = new Double[chList.size()];
					for (int j = 0; j < chList.size(); j++) {
						ch[j] = chList.get(j);
					}

//					String lpTime = (String) lpMap.get("DateTime" + "-" + cnt);
//					//lpTime = Util.addMinYymmdd(lpTime, -(this.meter.getLpInterval()));//
//					// 미터에서 LP시간이 00시00분부터 00시15분 사이의 데이터는 00시 15분으로 저장하기 때문에
//					//서버에서는 해당 기간의 사용데이터는 00시 00분으로 계산하므로 주기만큼 빼야함.
//
//					String revisionTime = Util.getQuaterYymmddhhmm(lpTime, lpInterval);
//					log.debug("### LP Time editing : " + lpTime + " => " + revisionTime);
//
//					_lpData = new LPData(revisionTime, lp, lpValue);
//					_lpData.setCh(ch);
//					_lpData.setPF(1d);

					
					_lpData = new LPData((String) lpMap.get("DateTime" + "-" + cnt), lp, lpValue);
					_lpData.setCh(ch);
					_lpData.setPF(1d);

					// Status 값 저장
					value = lpMap.get(ENERGY_LOAD_PROFILE.Status.getName() + "-" + cnt++);
					if (value != null) {
						_lpData.setStatus(String.valueOf(value));
					} else {
						_lpData.setStatus(null);
					}
					
					/*
					 * Modem에서 시간동기화를 할경우 00, 15, 30, 45 단위로 lp가 떨어지지 않고 03, 14.. 이런식으로 생성이되며
					 * 이렇게 생기는 LP는 버리기 위해서 아래 로직을 추가한다.
					 */
					int val = 0;
					if(_lpData.getDatetime() != null){
						int lpTime = Integer.parseInt(_lpData.getDatetime().substring(10, 12));
						val = lpTime % lpInterval;
					}
					
					if (_lpData.getDatetime() == null || (0 != val)) {
						log.debug("#### [잘못된 LP_TIME 발생!!!] Interval = {" + lpInterval + "}, LP Time = " + _lpData.getDatetime());
						log.debug("#### [잘못된 LP_TIME 발생!!!] Interval = {" + lpInterval + "}, LP Time = " + _lpData.getDatetime());
						log.debug("#### [잘못된 LP_TIME 발생!!!] Interval = {" + lpInterval + "}, LP Time = " + _lpData.getDatetime());
						continue;
					}					
					

					if (_lpData.getDatetime() != null) {
						lpDataList.add(_lpData);
						log.debug("LP" + cnt + " ==> " + _lpData.toString());
					}
				}
			}

			Collections.sort(lpDataList, LPComparator.TIMESTAMP_ORDER);
			//lpData = checkEmptyLP(lpDataList);
			
			lpData = checkDupLP(lpInterval, lpDataList).toArray(new LPData[0]);
			

			/*
			 * no cumulative last metering value
			try{
				LPData[] data = lpData;
				if(data != null && data.length > 0 && data[data.length-1].getCh() != null && data[data.length-1].getCh().length > 2){
					meteringValue = data[data.length-1].getCh()[2].doubleValue();// LP의 최종 누적값을 설정해준다.
				}
			}catch(Exception e){
				log.warn(e,e);
			}
			*/

			//lpData = lpDataList.toArray(new LPData[0]);
			log.debug("##### LPData set STOP!!  Total LP Count=" + lpData.length + " #####");
		} catch (Exception e) {
			log.error("DLMSLSSmartMeter setLPData Error : " + e);
			e.printStackTrace();
		}
	}

	private LPData[] checkEmptyLP(List<LPData> list) throws Exception {
		log.debug("Checking... Empty LP data.");
		ArrayList<LPData> emptylist = new ArrayList<LPData>();
		List<LPData> totalList = list;
		int channelCount = 4;
		if (list != null && list.size() > 0) {
			channelCount = list.get(0).getCh().length;
		}
		Double[] ch = new Double[channelCount];
		Double[] v = new Double[channelCount];

		for (int i = 0; i < channelCount; i++) {
			ch[i] = new Double(0.0);
			v[i] = new Double(0.0);
		}

		String prevTime = "";
		String currentTime = "";
		Double lp = 0.0;
		Double lpValue = 0.0;

		Iterator<LPData> it = totalList.iterator();
		while (it.hasNext()) {

			LPData prev = (LPData) it.next();
			currentTime = prev.getDatetime();
			lp = prev.getLp();
			lpValue = prev.getLpValue();
			currentTime = Util.getQuaterYymmddhhmm(currentTime, lpInterval);

			if (prevTime != null && !prevTime.equals("")) {
				String temp = Util.addMinYymmdd(prevTime, lpInterval);
				if (!temp.equals(currentTime)) {

					int diffMin = (int) ((Util.getMilliTimes(currentTime + "00") - Util.getMilliTimes(prevTime + "00")) / 1000 / 60) - lpInterval;

					if (diffMin > 0 && diffMin <= 1440) { //하루이상 차이가 나지 않을때 빈값으로 채운다. 
						for (int i = 0; i < (diffMin / lpInterval); i++) {

							log.debug("empty lp temp : " + currentTime + ", diff Min=" + diffMin);

							LPData data = new LPData();
							data.setLp(lp);
							data.setLpValue(lpValue);
							data.setV(v);
							data.setCh(ch);
							data.setFlag(MeteringFlag.Missing.getFlag());
							data.setPF(1.0);
							data.setDatetime(Util.addMinYymmdd(prevTime, lpInterval * (i + 1)));
							emptylist.add(data);
						}
					}

				}
			}
			prevTime = currentTime;

		}

		Iterator<LPData> it2 = emptylist.iterator();
		while (it2.hasNext()) {
			totalList.add((LPData) it2.next());
		}

		Collections.sort(totalList, LPComparator.TIMESTAMP_ORDER);
		totalList = checkDupLP(lpInterval, totalList);
		return totalList.toArray(new LPData[0]);
	}

	private List<LPData> checkDupLP(int lpInterval, List<LPData> list) throws Exception {
		log.debug("Checking... Duplication LP data.");
		List<LPData> totalList = list;
		List<LPData> removeList = new ArrayList<LPData>();
		LPData prevLPData = null;

		for (LPData lpData : totalList) {

			if (prevLPData != null && prevLPData.getDatetime() != null && !prevLPData.getDatetime().equals("")) {
				//if (lpData.getDatetime().equals(prevLPData.getDatetime()) && !lpData.getStatus().contains("Clock adjusted") && !lpData.getStatus().contains("Daylight saving")) {
				if (lpData.getDatetime().equals(prevLPData.getDatetime())) {
					log.debug(" ==>> found equal time lp : " + lpData.getDatetime() + ", LPStatus = " + (lpData == null ? "null" : lpData.getStatus()));
					removeList.add(lpData);
				}
			}
			prevLPData = lpData;
		}

		for (LPData lpData : removeList) {
			totalList.remove(lpData);
		}

		return totalList;
	}

	public void setMeterInfo() {
		try {
			Map<String, Object> map = null;
			map = (Map<String, Object>) result.get(OBIS.DEVICE_INFO.getCode());
			if (map != null) {
				Object obj = null;
				obj = map.get(OBIS.DEVICE_INFO.getName());
				if (obj != null)
					meterID = (String) obj;
				log.debug("METER_ID[" + meterID + "]");

				data.put("Meter Serial", meterID);
			}
			map = (Map<String, Object>) result.get(OBIS.MANUFACTURE_SERIAL.getCode());
			if (map != null) {
				Object obj = map.get(OBIS.MANUFACTURE_SERIAL.getName());
				if (obj != null)
					manufactureSerial = (String) obj;
				log.debug("MANUFACTURE_SERIAL[" + manufactureSerial + "]");
			}
			map = (Map<String, Object>) result.get(OBIS.METER_TIME.getCode());
			if (map != null) {
				Object obj = map.get(OBIS.METER_TIME.getName());
				if (obj != null)
					meterTime = (String) obj;
				if (meterTime != null && meterTime.length() != 14) {
					meterTime = meterTime + "00";
				}
				log.debug("METER_TIME[" + meterTime + "]");
			}
			map = (Map<String, Object>) result.get(OBIS.METER_MODEL.getCode());
			if (map != null) {
				Object obj = map.get(OBIS.METER_MODEL.getName());
				if (obj != null)
					meterModel = (String) obj;
				log.debug("METER_MODEL[" + meterModel + "]");
			}
			map = (Map<String, Object>) result.get(OBIS.PHASE_TYPE.getCode());
			if (map != null) {
				Object obj = map.get(OBIS.PHASE_TYPE.getName());
				if (obj != null)
					phaseType = String.valueOf(obj);
				log.debug("PHASE_TYPE[" + phaseType + "]");
			}
			map = (Map<String, Object>) result.get(OBIS.LOGICAL_NUMBER.getCode());
			if (map != null) {
				Object obj = map.get(OBIS.LOGICAL_NUMBER.getName());
				if (obj != null)
					logicalNumber = (String) obj;
				log.debug("LOGICAL_NUMBER[" + logicalNumber + "]");
			}
			map = (Map<String, Object>) result.get(OBIS.FW_VERSION.getCode());/* 없음 */
			if (map != null) {
				Object obj = map.get(OBIS.FW_VERSION.getName());
				if (obj != null)
					fwVersion = (String) obj;
				log.debug("FW_VERSION[" + fwVersion + "]");
			}

			/*
			map = (Map<String, Object>)result.get(OBIS.METER_STATUS.getCode());
			if (map != null) {
				Object obj = map.get(OBIS.METER_STATUS.getName());
				if (obj != null) meterStatus = (byte[])obj;
				log.debug("METER_STATUS[" + Hex.decode(meterStatus) + "]");
			}
			*/
			/*
			map = (Map<String, Object>)result.get(OBIS.RELAY_STATUS.getCode());
			if (map != null) {
				Object obj = map.get(OBIS.RELAY_STATUS.getName());
				if (obj != null) relayStatus = (byte[])obj;
				log.debug("RELAY_STATUS[" + Hex.decode(relayStatus) + "]");
			}
			*/

		} catch (Exception e) {
			log.error(e, e);
		}
	}

	//    public Instrument[] getPowerQuality(){    	
	//    	
	//    	List<Instrument> instruments = new ArrayList<Instrument>();
	//        Object value = null;
	//        Map<String, Object> lpMap = null;
	//        int cnt = 0;
	//
	//        double volA = 0.0;
	//        double volB = 0.0;
	//        double volC = 0.0;
	//        double currA = 0.0;
	//        double currB = 0.0;
	//        double currC = 0.0;
	//        double pfA = 0.0;
	//        double pfB = 0.0;
	//        double pfC = 0.0;
	//                
	//        for (int i = 0; i < result.size(); i++) {
	//            if (!result.containsKey(OBIS.ENERGY_LOAD_PROFILE.getCode() + "-" + i))
	//                break;
	//            Instrument instrument = new Instrument();
	//            lpMap = (Map<String, Object>) result.get(OBIS.ENERGY_LOAD_PROFILE.getCode() + "-" + i);
	//            cnt = 0;
	//            while ((value=lpMap.get("Channel[7]"+"-"+cnt)) != null) {
	//            	
	//            	log.debug("VOL_A="+value.toString());
	//            	if (value instanceof OCTET)
	//            		volA = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
	//            	else if (value instanceof Long)
	//            		volA = ((Long)value).doubleValue();
	//            	else if (value instanceof Float)
	//            		volA = ((Float)value).doubleValue();
	//            	
	//            	volA *= 0.001;
	//
	//                value = lpMap.get("Channel[8]"+"-"+cnt);
	//                if (value != null) {
	//                	if (value instanceof OCTET)
	//                		volB = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
	//                	else if (value instanceof Long)
	//                		volB = ((Long)value).doubleValue();
	//                	else if (value instanceof Float)
	//                		volB = ((Float)value).doubleValue();
	//
	//                	volB *= 0.001;
	//                }
	//                value = lpMap.get("Channel[9]"+"-"+cnt);
	//                if (value != null) {
	//                	if (value instanceof OCTET)
	//                		volC = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
	//                	else if (value instanceof Long)
	//                		volC = ((Long)value).doubleValue();
	//                	else if (value instanceof Float)
	//                		volC = ((Float)value).doubleValue();
	//                	volC *= 0.001;
	//                }
	//                value = lpMap.get("Channel[10]"+"-"+cnt);
	//                if (value != null) {
	//                	if (value instanceof OCTET)
	//                		currA = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
	//                	else if (value instanceof Long)
	//                		currA = ((Long)value).doubleValue();
	//                	else if (value instanceof Float)
	//                		currA = ((Float)value).doubleValue();
	//
	//                	currA *= 0.001;
	//                }
	//                value = lpMap.get("Channel[11]"+"-"+cnt);
	//                if (value != null) {
	//                	if (value instanceof OCTET)
	//                		currB = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
	//                	else if (value instanceof Long)
	//                		currB = ((Long)value).doubleValue();
	//                	else if (value instanceof Float)
	//                		currB = ((Float)value).doubleValue();
	//
	//                	currB *= 0.001;
	//                }
	//                
	//                value = lpMap.get("Channel[12]"+"-"+cnt);
	//                if (value != null) {
	//                	if (value instanceof OCTET)
	//                		currC = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
	//                	else if (value instanceof Long)
	//                		currC = ((Long)value).doubleValue();
	//                	else if (value instanceof Float)
	//                		currC = ((Float)value).doubleValue();
	//
	//                	currC *= 0.001;
	//                }
	//                
	//                value = lpMap.get("Channel[13]"+"-"+cnt);
	//                if (value != null) {
	//                	if (value instanceof OCTET)
	//                		pfA = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
	//                	else if (value instanceof Long)
	//                		pfA = ((Long)value).doubleValue();
	//                	else if (value instanceof Float)
	//                		pfA = ((Float)value).doubleValue();
	//
	//                	pfA *= 0.0001;
	//                }
	//                
	//                value = lpMap.get("Channel[14]"+"-"+cnt);
	//                if (value != null) {
	//                	if (value instanceof OCTET)
	//                		pfB = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
	//                	else if (value instanceof Long)
	//                		pfB = ((Long)value).doubleValue();
	//                	else if (value instanceof Float)
	//                		pfB = ((Float)value).doubleValue();
	//
	//                	pfB *= 0.0001;
	//                }
	//                
	//                value = lpMap.get("Channel[15]"+"-"+cnt);
	//                if (value != null) {
	//                	if (value instanceof OCTET)
	//                		pfC = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
	//                	else if (value instanceof Long)
	//                		pfC = ((Long)value).doubleValue();
	//                	else if (value instanceof Float)
	//                		pfC = ((Float)value).doubleValue();
	//
	//                	pfC *= 0.0001;
	//                }                
	//                
	//                String lpTime = (String) lpMap.get("DateTime"+ "-" + cnt++);
	//                instrument.setDatetime(lpTime);
	//                instrument.setVOL_A(volA);
	//                instrument.setVOL_B(volB);
	//                instrument.setVOL_C(volC);
	//                instrument.setCURR_A(currA);
	//                instrument.setCURR_B(currB);
	//                instrument.setCURR_C(currC);
	//                instrument.setPF_A(pfA);
	//                instrument.setPF_B(pfB);
	//                instrument.setPF_C(pfC);
	//                
	//                if (lpTime != null && !lpTime.substring(0, 4).equals("1792")) {
	//                	instruments.add(instrument);
	//                	//log.debug(instrument.toString());
	//                }
	//            }
	//        }
	//        
	//    	return instruments.toArray(new Instrument[0]);
	//    }

	public List<EventLogData> getMeterAlarmLog() {
		return meterAlarmLog;
	}

	public List<Map<String, Object>> getEventLog() {

		List<Map<String, Object>> eventLogs = new ArrayList<Map<String, Object>>();
		//Map<String, Object> eventLogs = new LinkedHashMap<String, Object>();           

		for (int i = 0; i < result.size(); i++) {
			if (result.get(OBIS.STANDARD_EVENT.getCode() + "-" + i) != null) {
				eventLogs.add(result.get(OBIS.STANDARD_EVENT.getCode() + "-" + i));
			}
		}

		for (int i = 0; i < result.size(); i++) {
			if (result.get(OBIS.RELAY_EVENT.getCode() + "-" + i) != null)
				eventLogs.add(result.get(OBIS.RELAY_EVENT.getCode() + "-" + i));
		}

		for (int i = 0; i < result.size(); i++) {
			if (result.get(OBIS.FRAUDDETECTIONLOGEVENT.getCode() + "-" + i) != null)
				eventLogs.add(result.get(OBIS.FRAUDDETECTIONLOGEVENT.getCode() + "-" + i));
		}

		for (int i = 0; i < result.size(); i++) {
			if (result.get(OBIS.MEASUREMENT_EVENT.getCode() + "-" + i) != null)
				eventLogs.add(result.get(OBIS.MEASUREMENT_EVENT.getCode() + "-" + i));
		}

		/*
		for (int i = 0; i < result.size(); i++ ) {
		    if (result.get(OBIS.MANUAL_DEMAND_RESET.getCode()+"-"+i) != null)
		        eventLogs.putAll(result.get(OBIS.MANUAL_DEMAND_RESET.getCode()+"-"+i));
		}
		*/
		/*
		for (int i = 0; i < result.size(); i++ ) {
		    if (result.get(OBIS.TAMPER_EVENT.getCode()+"-"+i) != null)
		        eventLogs.putAll(result.get(OBIS.TAMPER_EVENT.getCode()+"-"+i)); 
		}
		*/

		return eventLogs;
	}

	//    public void setPrevBillingData(){
	//    	
	//    	this.previousMonthBill = new BillingData();
	//        if (result.get(OBIS.LASTMONTH_ACTIVEENERGY_IMPORT1.getCode()) != null) {
	//        	Object obj = null;            	
	//        	obj = result.get(OBIS.LASTMONTH_ACTIVEENERGY_IMPORT1.getCode()).get(OBIS.LASTMONTH_ACTIVEENERGY_IMPORT1.getName());
	//            if (obj != null) {            	
	//            	double val = ((Long) obj).longValue()*0.001;
	//            	this.previousMonthBill.setActiveEnergyImportRateTotal(val);
	//                log.debug("LASTMONTH_ACTIVEENERGY_IMPORT[" + val + "]");
	//            }
	//        }
	//        if (result.get(OBIS.LASTMONTH_ACTIVEENERGY_IMPORT2.getCode()) != null) {
	//        	Object obj = null;            	
	//        	obj = result.get(OBIS.LASTMONTH_ACTIVEENERGY_IMPORT2.getCode()).get(OBIS.LASTMONTH_ACTIVEENERGY_IMPORT2.getName());
	//            if (obj != null) {            	
	//            	double val = ((Long) obj).longValue()*0.001;
	//            	
	//            	this.previousMonthBill.setActiveEnergyImportRateTotal(val);
	//                log.debug("LASTMONTH_ACTIVEENERGY_IMPORT[" + val + "]");
	//            }
	//        }
	//        if (result.get(OBIS.LASTMONTH_ACTIVEENERGY_EXPORT1.getCode()) != null) {
	//        	Object obj = null;            	
	//        	obj = result.get(OBIS.LASTMONTH_ACTIVEENERGY_EXPORT1.getCode()).get(OBIS.LASTMONTH_ACTIVEENERGY_EXPORT1.getName());
	//            if (obj != null) {            	
	//            	double val = ((Long) obj).longValue()*0.001;
	//            	
	//            	this.previousMonthBill.setActiveEnergyExportRateTotal(val);
	//                log.debug("LASTMONTH_ACTIVEENERGY_EXPORT[" + val + "]");
	//            }
	//        }   
	//        if (result.get(OBIS.LASTMONTH_ACTIVEENERGY_EXPORT2.getCode()) != null) {
	//        	Object obj = null;            	
	//        	obj = result.get(OBIS.LASTMONTH_ACTIVEENERGY_EXPORT2.getCode()).get(OBIS.LASTMONTH_ACTIVEENERGY_EXPORT2.getName());
	//            if (obj != null) {            	
	//            	double val = ((Long) obj).longValue()*0.001;
	//            	
	//            	this.previousMonthBill.setActiveEnergyExportRateTotal(val);
	//                log.debug("LASTMONTH_ACTIVEENERGY_EXPORT[" + val + "]");
	//            }
	//        } 
	//        if (result.get(OBIS.LASTMONTH_REACTIVEENERGY_IMPORT1.getCode()) != null) {
	//        	Object obj = null;            	
	//        	obj = result.get(OBIS.LASTMONTH_REACTIVEENERGY_IMPORT1.getCode()).get(OBIS.LASTMONTH_REACTIVEENERGY_IMPORT1.getName());
	//            if (obj != null) {            	
	//            	double val = ((Long) obj).longValue()*0.001;
	//                log.debug("LASTMONTH_REACTIVEENERGY_IMPORT[" + val + "]");
	//            }
	//        }
	//        if (result.get(OBIS.LASTMONTH_REACTIVEENERGY_IMPORT2.getCode()) != null) {
	//        	Object obj = null;            	
	//        	obj = result.get(OBIS.LASTMONTH_REACTIVEENERGY_IMPORT2.getCode()).get(OBIS.LASTMONTH_REACTIVEENERGY_IMPORT2.getName());
	//            if (obj != null) {            	
	//            	double val = ((Long) obj).longValue()*0.001;
	//                log.debug("LASTMONTH_REACTIVEENERGY_IMPORT[" + val + "]");
	//            }
	//        }
	//        if (result.get(OBIS.LASTMONTH_REACTIVEENERGY_EXPORT1.getCode()) != null) {
	//        	Object obj = null;            	
	//        	obj = result.get(OBIS.LASTMONTH_REACTIVEENERGY_EXPORT1.getCode()).get(OBIS.LASTMONTH_REACTIVEENERGY_EXPORT1.getName());
	//            if (obj != null) {            	
	//            	double val = ((Long) obj).longValue()*0.001;
	//                log.debug("LASTMONTH_REACTIVEENERGY_EXPORT[" + val + "]");
	//            }
	//        }  
	//        if (result.get(OBIS.LASTMONTH_REACTIVEENERGY_EXPORT2.getCode()) != null) {
	//        	Object obj = null;            	
	//        	obj = result.get(OBIS.LASTMONTH_REACTIVEENERGY_EXPORT2.getCode()).get(OBIS.LASTMONTH_REACTIVEENERGY_EXPORT2.getName());
	//            if (obj != null) {            	
	//            	double val = ((Long) obj).longValue()*0.001;
	//                log.debug("LASTMONTH_REACTIVEENERGY_EXPORT[" + val + "]");
	//            }
	//        }
	//    }    

	//    public void setCurrBillingData() {
	//    	
	//    	currBillLog = new ArrayList<BillingData>();    	    	
	//    	@SuppressWarnings("unused")
	//		double active = 0.0;
	//    	
	//    	if(lpData == null) {
	//    		log.info("DLMS Parse lpData is empty");
	//    		return;
	//    	}
	//    	
	//    	for (int i = 0; i < lpData.length; i++) {
	//			if (i + 1 == lpData.length)
	//				break;
	//			
	//			this.currBill = new BillingData();
	//			
	////			if("LSIQ-3PCT".equals(meter.getModel().getName()) || "LSIQ-3PCV".equals(meter.getModel().getName())) {
	////				if (lpData[i + 1].getCh()[0] < lpData[i].getCh()[0]) {
	////					active = lpData[i + 1].getCh()[0] + diffMaxValue(lpData[i].getCh()[0]);
	////				} else {
	////					active = lpData[i + 1].getCh()[0] - lpData[i].getCh()[0];
	////				}
	////			}else {
	////				active = lpData[i].getCh()[0];
	////			}
	//			
	////			this.currBill.setActiveEnergyImportRateTotal(active);
	//			this.currBill.setActiveEnergyImportRateTotal(lpData[i].getCh()[0]);
	//			this.currBill.setBillingTimestamp(lpData[i + 1].getDatetime());
	//			
	//			currBillLog.add(currBill);
	//    	}
	//    	
	//    	log.debug("DLMS Parse BillLog Data Size["+currBillLog.size()+"]");
	//    }

	/*
	public void setCurrBillingData(){
		
		this.currBill = new BillingData();
		
	    if (result.get(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT1.getCode()) != null) {
	    	Object obj = null;            	
	    	obj = result.get(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT1.getCode()).get(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT1.getName());
	        if (obj != null) {     
	        	double val = 0d;
	        	if(obj instanceof Long){
	        		val = ((Long) obj).doubleValue()*0.001;
	        	}else if(obj instanceof Float){
	        		val = ((Float) obj).doubleValue();
	        	}            	
	            meteringValue = val;                
	
	            this.currBill.setActiveEnergyImportRateTotal(val);
	            log.debug("METERING_VALUE[" + meteringValue + "]");        
	            log.debug("CUMULATIVE_ACTIVEENERGY_IMPORT[" + val + "]");
	        }
	    }
	    if (result.get(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT2.getCode()) != null) {
	    	Object obj = null;            	
	    	obj = result.get(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT2.getCode()).get(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT2.getName());
	        if (obj != null) {     
	        	double val = 0d;
	        	if(obj instanceof Long){
	        		val = ((Long) obj).doubleValue()*0.001;
	        	}else if(obj instanceof Float){
	        		val = ((Float) obj).doubleValue();
	        	}            	
	         
	
	            this.currBill.setActiveEnergyImportRateTotal(val);
	            log.debug("METERING_VALUE[" + meteringValue + "]");        
	            log.debug("CUMULATIVE_ACTIVEENERGY_IMPORT[" + val + "]");
	        }
	    }
	    if (result.get(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT1.getCode()) != null) {
	    	Object obj = null;            	
	    	obj = result.get(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT1.getCode()).get(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT1.getName());
	        if (obj != null) {            	
	        	double val = 0d;
	        	if(obj instanceof Long){
	        		val = ((Long) obj).doubleValue()*0.001;
	        	}else if(obj instanceof Float){
	        		val = ((Float) obj).doubleValue();
	        	}  
	            this.currBill.setActiveEnergyExportRateTotal(val);
	            log.debug("CUMULATIVE_ACTIVEENERGY_EXPORT[" + val + "]");
	        }
	    }
	    if (result.get(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT2.getCode()) != null) {
	    	Object obj = null;            	
	    	obj = result.get(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT2.getCode()).get(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT2.getName());
	        if (obj != null) {            	
	        	double val = 0d;
	        	if(obj instanceof Long){
	        		val = ((Long) obj).doubleValue()*0.001;
	        	}else if(obj instanceof Float){
	        		val = ((Float) obj).doubleValue();
	        	}  
	            this.currBill.setActiveEnergyExportRateTotal(val);
	            log.debug("CUMULATIVE_ACTIVEENERGY_EXPORT[" + val + "]");
	        }
	    }
	    if (result.get(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT1.getCode()) != null) {
	    	Object obj = null;            	
	    	obj = result.get(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT1.getCode()).get(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT1.getName());
	        if (obj != null) {            	
	        	double val = 0d;
	        	if(obj instanceof Long){
	        		val = ((Long) obj).doubleValue()*0.001;
	        	}else if(obj instanceof Float){
	        		val = ((Float) obj).doubleValue();
	        	}  
	            log.debug("CUMULATIVE_REACTIVEENERGY_IMPORT[" + val + "]");
	        }
	    }
	    if (result.get(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT2.getCode()) != null) {
	    	Object obj = null;            	
	    	obj = result.get(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT2.getCode()).get(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT2.getName());
	        if (obj != null) {            	
	        	double val = 0d;
	        	if(obj instanceof Long){
	        		val = ((Long) obj).doubleValue()*0.001;
	        	}else if(obj instanceof Float){
	        		val = ((Float) obj).doubleValue();
	        	}  
	            log.debug("CUMULATIVE_REACTIVEENERGY_IMPORT[" + val + "]");
	        }
	    }
	    if (result.get(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT1.getCode()) != null) {
	    	Object obj = null;            	
	    	obj = result.get(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT1.getCode()).get(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT1.getName());
	        if (obj != null) {            	
	        	double val = 0d;
	        	if(obj instanceof Long){
	        		val = ((Long) obj).doubleValue()*0.001;
	        	}else if(obj instanceof Float){
	        		val = ((Float) obj).doubleValue();
	        	}  
	            log.debug("CUMULATIVE_REACTIVEENERGY_EXPORT[" + val + "]");
	        }
	    }
	    if (result.get(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT2.getCode()) != null) {
	    	Object obj = null;            	
	    	obj = result.get(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT2.getCode()).get(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT2.getName());
	        if (obj != null) {            	
	        	double val = 0d;
	        	if(obj instanceof Long){
	        		val = ((Long) obj).doubleValue()*0.001;
	        	}else if(obj instanceof Float){
	        		val = ((Float) obj).doubleValue();
	        	}  
	            log.debug("CUMULATIVE_REACTIVEENERGY_EXPORT[" + val + "]");
	        }
	    }
	    if (result.get(OBIS.TOTAL_ACTIVEENERGY_IMPORT.getCode()) != null) {
	    	Object obj = null;            	
	    	obj = result.get(OBIS.TOTAL_ACTIVEENERGY_IMPORT.getCode()).get(OBIS.TOTAL_ACTIVEENERGY_IMPORT.getName());
	        if (obj != null) {            	
	        	double val = 0d;
	        	if(obj instanceof Long){
	        		val = ((Long) obj).doubleValue()*0.001;
	        	}else if(obj instanceof Float){
	        		val = ((Float) obj).doubleValue();
	        	}            	
	            log.debug("TOTAL_ACTIVEENERGY_IMPORT[" + val + "]");
	        }
	    }
	    if (result.get(OBIS.TOTAL_ACTIVEENERGY_EXPORT.getCode()) != null) {
	    	Object obj = null;            	
	    	obj = result.get(OBIS.TOTAL_ACTIVEENERGY_EXPORT.getCode()).get(OBIS.TOTAL_ACTIVEENERGY_EXPORT.getName());
	        if (obj != null) {            	
	        	double val = 0d;
	        	if(obj instanceof Long){
	        		val = ((Long) obj).doubleValue()*0.001;
	        	}else if(obj instanceof Float){
	        		val = ((Float) obj).doubleValue();
	        	}            	
	            log.debug("TOTAL_ACTIVEENERGY_EXPORT[" + val + "]");
	        }
	    }
	    if (result.get(OBIS.TOTAL_MAX_ACTIVEDEMAND_IMPORT.getCode()) != null) {
	    	Object obj = null;            	
	    	obj = result.get(OBIS.TOTAL_MAX_ACTIVEDEMAND_IMPORT.getCode()).get(OBIS.TOTAL_MAX_ACTIVEDEMAND_IMPORT.getName());
	        if (obj != null) {            	
	        	double val = ((Long) obj).longValue()*0.001;
	        	
	        	this.currBill.setActivePwrDmdMaxImportRateTotal(val);
	            log.debug("TOTAL_MAX_ACTIVEDEMAND_IMPORT[" + val + "]");
	        }
	    }
	    if (result.get(OBIS.TOTAL_MAX_ACTIVEDEMAND_EXPORT.getCode()) != null) {
	    	Object obj = null;            	
	    	obj = result.get(OBIS.TOTAL_MAX_ACTIVEDEMAND_EXPORT.getCode()).get(OBIS.TOTAL_MAX_ACTIVEDEMAND_EXPORT.getName());
	        if (obj != null) {            	
	        	double val = ((Long) obj).longValue()*0.001;
	        	
	        	this.currBill.setActivePwrDmdMaxExportRateTotal(val);
	            log.debug("TOTAL_MAX_ACTIVEDEMAND_EXPORT[" + val + "]");
	        }
	    }        
	    if (result.get(OBIS.TOTAL_MAX_REACTIVEDEMAND_IMPORT.getCode()) != null) {
	    	Object obj = null;            	
	    	obj = result.get(OBIS.TOTAL_MAX_REACTIVEDEMAND_IMPORT.getCode()).get(OBIS.TOTAL_MAX_REACTIVEDEMAND_IMPORT.getName());
	        if (obj != null) {            	
	        	double val = ((Long) obj).longValue()*0.001;
	
	            log.debug("TOTAL_MAX_REACTIVEDEMAND_IMPORT[" + val + "]");
	        }
	    }
	    if (result.get(OBIS.TOTAL_MAX_REACTIVEDEMAND_EXPORT.getCode()) != null) {
	    	Object obj = null;            	
	    	obj = result.get(OBIS.TOTAL_MAX_REACTIVEDEMAND_EXPORT.getCode()).get(OBIS.TOTAL_MAX_REACTIVEDEMAND_EXPORT.getName());
	        if (obj != null) {            	
	        	double val = ((Long) obj).longValue()*0.001;
	            log.debug("TOTAL_MAX_REACTIVEDEMAND_EXPORT[" + val + "]");
	        }
	    }        
	    if (result.get(OBIS.TOTAL_CUM_ACTIVEDEMAND_IMPORT.getCode()) != null) {
	    	Object obj = null;            	
	    	obj = result.get(OBIS.TOTAL_CUM_ACTIVEDEMAND_IMPORT.getCode()).get(OBIS.TOTAL_CUM_ACTIVEDEMAND_IMPORT.getName());
	        if (obj != null) {            	
	        	double val = ((Long) obj).longValue()*0.001;
	        	
	        	this.currBill.setCummActivePwrDmdMaxImportRateTotal(val);
	            log.debug("TOTAL_CUM_ACTIVEDEMAND_IMPORT[" + val + "]");
	        }
	    }
	    if (result.get(OBIS.TOTAL_CUM_ACTIVEDEMAND_EXPORT.getCode()) != null) {
	    	Object obj = null;            	
	    	obj = result.get(OBIS.TOTAL_CUM_ACTIVEDEMAND_EXPORT.getCode()).get(OBIS.TOTAL_CUM_ACTIVEDEMAND_EXPORT.getName());
	        if (obj != null) {            	
	        	double val = ((Long) obj).longValue()*0.001;
	        	this.currBill.setCummActivePwrDmdMaxExportRateTotal(val);
	            log.debug("TOTAL_CUM_ACTIVEDEMAND_EXPORT[" + val + "]");
	        }
	    }        
	    if (result.get(OBIS.TOTAL_CUM_REACTIVEDEMAND_IMPORT.getCode()) != null) {
	    	Object obj = null;            	
	    	obj = result.get(OBIS.TOTAL_CUM_REACTIVEDEMAND_IMPORT.getCode()).get(OBIS.TOTAL_CUM_REACTIVEDEMAND_IMPORT.getName());
	        if (obj != null) {            	
	        	double val = ((Long) obj).longValue()*0.001;
	        	
	            log.debug("TOTAL_CUM_REACTIVEDEMAND_IMPORT[" + val + "]");                
	        }
	    }
	    if (result.get(OBIS.TOTAL_CUM_REACTIVEDEMAND_EXPORT.getCode()) != null) {
	    	Object obj = null;            	
	    	obj = result.get(OBIS.TOTAL_CUM_REACTIVEDEMAND_EXPORT.getCode()).get(OBIS.TOTAL_CUM_REACTIVEDEMAND_EXPORT.getName());
	        if (obj != null) {            	
	        	double val = ((Long) obj).longValue()*0.001;
	            log.debug("TOTAL_CUM_REACTIVEDEMAND_EXPORT[" + val + "]");
	        }
	    }
	}
	*/

	public LinkedHashMap<String, Object> getRelayStatus() {
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		Map<String, Object> loadControlMap = result.get(OBIS.RELAY_STATUS.getCode());
		log.debug("LoadControlStatus : " + loadControlMap.get("LoadControlStatus"));
		map.put("LoadControlStatus", loadControlMap.get("LoadControlStatus"));
		return map;
	}

	public Double getLQISNRValue() {

		Object obj = null;
		if (result.get(OBIS.WEAK_LQI_VALUE.getCode()) != null) {
			obj = result.get(OBIS.WEAK_LQI_VALUE.getCode()).get(OBIS.WEAK_LQI_VALUE.getName());
			if (obj != null) {
				log.debug("LQI SNR[" + obj + "]");
				if (obj instanceof Double) {
					return (Double) obj;
				}
			}
		}

		return null;
	}

	public Integer getLpInterval() {
		return this.lpInterval;
	}

	public Double getActivePulseConstant() {
		return this.activePulseConstant;
	}

	public Double getReActivePulseConstant() {
		return this.reActivePulseConstant;
	}

	public String getMeterID() {
		return this.meterID;
	}

	public void setCt(Double ct) {
		this.ct = ct;
	}

	public Double getCt() {
		return this.ct;
	}

	public String getFwVersion() {
		return fwVersion;
	}

	public void setFwVersion(String fwVersion) {
		this.fwVersion = fwVersion;
	}

	public String getMeterModel() {
		//		if(meterModel == null || "".equals(meterModel)){
		//			return null;
		//		}
		//		if(meterModel.indexOf("CT") > 0)
		//			return "LSIQ-3PCT";
		//		if(meterModel.indexOf("CV") > 0 || meterModel.indexOf("CP") > 0)
		//			return "LSIQ-3PCV";
		//		if(meterModel.indexOf("3") > 0)
		//			return "LSIQ-3P";
		//		if(meterModel.indexOf("1210") > 0)
		//			return "LSIQ-1P";
		//		

		return meterModel;
	}

	@SuppressWarnings("unused")
	private double diffMaxValue(double value) {
		String strValue = "" + value;
		strValue = strValue.substring(0, strValue.indexOf("."));
		double maxvalue = 1.0;
		for (int i = 0; i < strValue.length(); i++) {
			maxvalue *= 10;
		}
		return maxvalue - value;
	}

	@Override
	public int getFlag() {
		return 0;
	}

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public Double getMeteringValue() {
		return meteringValue;
	}

	@Override
	public byte[] getRawData() {
		return null;
	}
}
