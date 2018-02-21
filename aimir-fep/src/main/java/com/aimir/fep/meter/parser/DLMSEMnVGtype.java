package com.aimir.fep.meter.parser;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeTable;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.DLMS_CLASS;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.EVENT;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.LOAD_PROFILE;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.METER_CONSTANT;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.MONTHLY_DEMAND_PROFILE;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.MONTHLY_ENERGY_PROFILE;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.OBIS;
import com.aimir.fep.meter.parser.DLMSKepcoTable.LPComparator;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVMeteringDataType;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.model.device.EnergyMeter;

public class DLMSEMnVGtype extends MeterDataParser implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(DLMSEMnVGtype.class);
	LPData[] lpData = null;
	LinkedHashMap<String, Map<String, Object>> result = new LinkedHashMap<String, Map<String, Object>>();

	private EMnVMeteringDataType meteringDataType;  // Billing or Load Profile
	int meterConstnt = 0;

	int meterActiveConstant = 0;
	int meterReActiveConstant = 0;

	double activePulseConstant = 0;
	double reActivePulseConstant = 0;

	Long ctRatio = 0L;

	int interval = 0;

	Double meteringValue = null;
	Double ct = 1d;
	Double pt = 1d;
	Double st = 1d;  // 서버적용배율

	Object beforeTime = null;
	Object afterTime = null;

	String maxdemandTime = null;
	Double maxdemand = 0.0;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public LinkedHashMap<String, Map<String, Object>> getData() {
		Map<String, Object> data = new LinkedHashMap<String, Object>(16, 0.75f, false);
//		Map<String, Object> resultSubData = null;
//		String key = null;
//
//		SimpleDateFormat datef14 = null;
//		DecimalFormat decimalf = null;
//
//		if (meter != null && meter.getSupplier() != null) {
//			Supplier supplier = meter.getSupplier();
//			if (supplier != null) {
//				String lang = supplier.getLang().getCode_2letter();
//				String country = supplier.getCountry().getCode_2letter();
//
//				decimalf = TimeLocaleUtil.getDecimalFormat(supplier);
//				datef14 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(14, lang, country));
//			}
//		} else {
//			//locail 정보가 없을때는 기본 포멧을 사용한다.
//			decimalf = new DecimalFormat();
//			datef14 = new SimpleDateFormat();
//		}
//
//		for (Iterator i = result.keySet().iterator(); i.hasNext();) {
//			key = (String) i.next();
//			resultSubData = result.get(key);
//			// 정복전 결과가 많아서 최근적으로만 넣는다.
//			if (key.equals(OBIS.METER_TIME.getCode())) {
//				log.debug("METER_TIME[" + (String) resultSubData.get(OBIS.METER_TIME.name()) + "]");
//				try {
//					data.put(OBIS.getObis(key).getName(), datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS((String) resultSubData.get(OBIS.METER_TIME.name()))));
//				} catch (Exception e) {
//				}
//				
//			} else {
///*				if (resultSubData != null) {
//					String idx = "";
//					if (key.lastIndexOf("-") != -1) {
//						idx = key.substring(key.lastIndexOf("-") + 1);
//						key = key.substring(0, key.lastIndexOf("-"));
//					} else if (key.lastIndexOf(".") != -1) {
//						idx = key.substring(key.lastIndexOf(".") + 1);
//						key = key.substring(0, key.lastIndexOf("."));
//					}
//					String subkey = null;
//					Object subvalue = null;
//					for (Iterator subi = resultSubData.keySet().iterator(); subi.hasNext();) {
//						subkey = (OBIS) subi.next();
//						if (!subkey.contains(DLMSEMnVGtypeVARIABLE.UNDEFINED)) {
//							subvalue = resultSubData.get(subkey);
//							if (subvalue instanceof String) {
//								if (((String) subvalue).contains(":date=")) {
//									try {
//										data.put(OBIS.getObis(key).getName() + idx + " : " + subkey, datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(((String) subvalue).substring(6) + "00")));
//									} catch (Exception e) {
//										data.put(OBIS.getObis(key).getName() + idx + " : " + subkey, subvalue);
//									}
//								} else if (subkey.contains("Date") && !((String) subvalue).contains(":date=") && ((String) subvalue).length() == 12) {
//									try {
//										data.put(OBIS.getObis(key).getName() + idx + " : " + subkey, datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(subvalue + "00")));
//									} catch (Exception e) {
//										data.put(OBIS.getObis(key).getName() + idx + " : " + subkey, subvalue);
//									}
//								} else
//									data.put(OBIS.getObis(key).getName() + idx + " : " + subkey, subvalue);
//							} else if (subvalue instanceof Number) {
//								if ((subkey.contains("Reactive") || subkey.contains("Apparent")) && !subkey.contains("ReactiveC"))
//									data.put(OBIS.getObis(key).getName() + idx + " : " + subkey, decimalf.format(((Number) subvalue).doubleValue() / reActivePulseConstant));
////								else if (OBIS.getObis(key) != OBIS.METER_CONSTANT_ACTIVE && OBIS.getObis(key) != OBIS.METER_CONSTANT_REACTIVE && !subkey.contains("PowerFactor") && !subkey.contains("Interval") && OBIS.getObis(key) != OBIS.POWER_QUALITY && !subkey.contains("Count"))
////									data.put(OBIS.getObis(key).getName() + idx + " : " + subkey, decimalf.format(((Number) subvalue).doubleValue() / activePulseConstant));
//								else
//									data.put(OBIS.getObis(key).getName() + idx + " : " + subkey, decimalf.format(subvalue));
//							} else
//								data.put(OBIS.getObis(key).getName() + idx + " : " + subkey, subvalue);
//						}
//					}
//				}*/
//			}
//		}
		return (LinkedHashMap) data;
	}

	
	
	@SuppressWarnings("unchecked")
	@Override
	public void parse(byte[] data) throws Exception {
		// 로그 확인 편하도록....
		log.info("    ");
		log.info("    ");
		log.info("    ");
		log.info("############################## OBIS 파싱 시작 ################################################");
		
		
        log.info("DLMSEMnVGtype parse:[{}] [{}]", meteringDataType != null ? meteringDataType.name() : "", Hex.decode(data));

        int obisListCount = 0; // 미터의 수집된 전체 OBIS의 개수
        int mdLenth = 0;       // Metering Data의 전체 Data의 길이

		String obisCode = "";
		int clazz = 0;
		int attr = 0;

		int pos = 0;
		/*
		 *  DLMS Header CLASS(1), OBIS(6), ATTR(1)
		 *  DLMS Tag Tag(1), DATA or LEN/DATA (*)
		 */
		
		byte[] CLAZZ = new byte[2];
		byte[] OBIS_DATA = new byte[6];
		byte[] ATTR = new byte[1];
		
        byte[] temp = new byte[EMnVConstants.OBIS_LIST_LEN];
        System.arraycopy(data, pos, temp, 0, temp.length);
        obisListCount = DataUtil.getIntToByte(temp[0]);
        pos += EMnVConstants.OBIS_LIST_LEN;
        log.info("[PROTOCOL][METERING_DATA] NUMBER_OF_OBIS(1):[{}] ==> HEX=[{}]", obisListCount, Hex.decode(temp));
        
        byte[] temp2 = new byte[EMnVConstants.OBIS_MDDATA_LEN];
        System.arraycopy(data, pos, temp2, 0, temp2.length);
        mdLenth = DataUtil.getIntTo2Byte(temp2);
        pos += EMnVConstants.OBIS_MDDATA_LEN;
        log.info("[PROTOCOL][METERING_DATA] DATA_LEN(2):[{}] ==> HEX=[{}]", mdLenth, Hex.decode(temp2));
        
        log.info("총 OBIS 갯수 ==> {}, MD Data 전체길이 ==> {}", obisListCount, mdLenth);
        
        //OBIS리스트 갯수만큼 for loop
        DLMSEMnVGtypeTable dlms = null;
        int obisLenth = 0;
        String vz = "";   // VZ값(검침횟수)
        for(int i=0; i<obisListCount; i++){
        	log.info("-----------------------------------------------------------------------");
        	dlms = new DLMSEMnVGtypeTable();
        	
        	if(pos+30 <= data.length){
            	printHexByteString(data, pos, 30);        		
        	}else{
        		printHexByteString(data, pos, data.length - pos);
        	}
			
        	System.arraycopy(data, pos, CLAZZ, 0, CLAZZ.length);
			pos += CLAZZ.length;
			clazz = DataUtil.getIntToBytes(CLAZZ);
			dlms.setClazz(clazz);
			log.info("[{}][PROTOCOL][METERING_DATA] DLMS_CLASS_ID   (2):[{}] ==> HEX=[{}]", new Object[]{i, DLMS_CLASS.getItems(clazz), Hex.decode(CLAZZ)});
			
			System.arraycopy(data, pos, OBIS_DATA, 0, OBIS_DATA.length);
			pos += OBIS_DATA.length;
			obisCode = Hex.decode(OBIS_DATA);
			
			/* VZ 처리 */
			if(obisCode.equals("0100000102" + vz)){  
				obisCode = OBIS.LPTIME_LAST_MONTH.getCode();
			}else if(obisCode.equals("0000620101" + vz)){
				obisCode = OBIS.MONTHLY_ENERGY_PROFILE_LAST.getCode();
			}else if(obisCode.equals("0000620102" + vz)){
				obisCode = OBIS.MONTHLY_DEMAND_PROFILE_LAST.getCode();
			}else if(obisCode.equals("0000620103" + vz)){
				obisCode = OBIS.R_MONTHLY_ENERGY_PROFILE_LAST.getCode();
			}
			
			dlms.setObis(getObisType(), obisCode);
			log.info("[{}][PROTOCOL][METERING_DATA] DLMS_OBIS_CODE  (6):[{}][{}] ==> HEX=[{}]", new Object[]{i, getObisType(), OBIS.getObis(obisCode), Hex.decode(OBIS_DATA)});
			System.arraycopy(data, pos, ATTR, 0, ATTR.length);
			pos += ATTR.length;
			attr = DataUtil.getIntToBytes(ATTR);
			dlms.setAttr(attr);
			log.info("[{}][PROTOCOL][METERING_DATA] DLMS_ATTR_NUMBER(1):[{}] ==> HEX=[{}]", new Object[]{i, DLMS_CLASS_ATTR.getItems(attr), Hex.decode(ATTR)});
			
			obisLenth = dlms.setData(pos, data);   // return 값 : 진행된 pos
			pos = obisLenth;
			
			if(OBIS.getObis(obisCode) == OBIS.LP_COUNT){  // LP_COUNT(검침횟수) is VZ   0100000100FF
				Map<String, Object> dlmsData = (Map<String, Object>) dlms.getData(); 
				Object object = dlmsData.get(OBIS.LP_COUNT.name());
				byte[] tt = new byte[1];				
				tt[0] = (byte)Integer.parseInt(object.toString());
				vz = Hex.decode(tt);
				
				log.info("# VZ => Value={} HEX={}", Integer.parseInt(object.toString()), vz);
			}
			
			
	        //////// DEBUG //////
	        Map<String, Object> dlmsData = (Map<String, Object>) dlms.getData();
	        log.info("### OBIS  ==> {}", dlmsData.toString());  
	        
			// 동일한 obis 코드를 가진 값이 있을 수 있기 때문에 검사해서 _number를 붙인다.
			if (dlms.getDlmsHeader().getObis() == DLMSEMnVGtypeVARIABLE.OBIS.LOAD_PROFILE
					|| dlms.getDlmsHeader().getObis() == DLMSEMnVGtypeVARIABLE.OBIS.LP_INTERVAL
					|| dlms.getDlmsHeader().getObis() == DLMSEMnVGtypeVARIABLE.OBIS.A_MAX_LOAD_ELECTRIC   // A상 최대 부하전류
					|| dlms.getDlmsHeader().getObis() == DLMSEMnVGtypeVARIABLE.OBIS.B_MAX_LOAD_ELECTRIC   // B상 최대 부하전류
					|| dlms.getDlmsHeader().getObis() == DLMSEMnVGtypeVARIABLE.OBIS.C_MAX_LOAD_ELECTRIC) { // C상 최대 부하전류
				if (dlmsData.get(OBIS.LP_INTERVAL.name()) != null) {
					//interval = (int) ((Long) dlmsData.get(OBIS.LP_INTERVAL.name()) / 60);
					interval = Integer.parseInt(String.valueOf(dlmsData.get(OBIS.LP_INTERVAL.name())));
					meter.setLpInterval(interval);
				}
				for (int cnt = 0;; cnt++) {
					obisCode = dlms.getDlmsHeader().getObis().getCode() + "-" + cnt; 
					if (!result.containsKey(obisCode)) {
						result.put(obisCode, dlmsData);
						break;
					}
				}
			} else {
				if (result.containsKey(obisCode)) {
					log.debug("같은 OBIS코드 있음 ==> {}", obisCode);
					result.put(obisCode + ".1", dlmsData);
				} else {
					result.put(obisCode, dlmsData);
				}
			}
        }  //END For문


        log.debug("### OBIS TOTAL ==> {}", result.toString());
        log.info("### Number_of_OBIS={} Data_Len={} Last POS={}", new Object[]{obisListCount, mdLenth, pos});
        
//		EnergyMeter meter = (EnergyMeter) this.getMeter();
//
//		this.ct = 1.0;
//		this.pt = 1.0;
//		this.st = 1.0;
//		
//		if (meter != null && meter.getCt() != null && meter.getCt() > 0){
//			ct = meter.getCt();
//		}
//		
//		if (meter != null && meter.getPt() != null && meter.getPt() > 0){
//			pt = meter.getPt();
//		}
//		
//		st = ct * pt;  // 서버 적용배율
//		
//
//
//		setCt(ct);
		setMeterInfo();
		setPulseConstant();
//		setMeteringValue();
//		setLPData(null);

		//log.debug("DLMS parse result:" + result);



	}
	
	public void postParse(){
		/*
		 *  최초 미터가 등록되지 않았을때 Load Profile OBIS로 넘어올경우
		 *  pulseconstant가 없기때문에 디폴트 값(fmp.properties의 meter.pulse.constant.hmu=5000)을 넣어준다.
		 *  pulseconstant가 없으면 lpData만들때 에러남.
		 */
		
		if(activePulseConstant == 0.0){
			activePulseConstant = meter.getPulseConstant();	
		}
		
		if(reActivePulseConstant == 0.0){
			reActivePulseConstant = meter.getPulseConstant();	
		}
		
//		setMeteringValue();
		
		EnergyMeter meter = (EnergyMeter) this.getMeter();
		if (meter != null && meter.getCt() != null && meter.getCt() > 0){
			ct = meter.getCt();
		}
		
		if (meter != null && meter.getPt() != null && meter.getPt() > 0){
			pt = meter.getPt();
		}
		
		st = ct * pt;  // 서버 적용배율
		
		
		setLPData();
	}
	

	/**
	 * GType미터 같은경우는 Billing OBIS Code와 Load profile OBIS Code가 다르기때문에 구분값이 필요함.
	 */
	@Override
	public void setFlag(int flag) {
		meteringDataType = EMnVMeteringDataType.getItem(DataUtil.getByteToInt(flag));		
	}

	@Override
	public String toString() {

		return null;
	}

	public LPData[] getLPData() {

		return lpData;
	}

	public void setLPData() {
		interval = meter.getLpInterval();
		
		try {
			Map<String, LPData> lpDataMap = new HashMap<String, LPData>();

			Double lp = 0.0;
			Double lpValue = 0.0;
			Object value = null;
			Map<String, Object> lpMap = null;
			int cnt = 0;
			LPData _lpData = null;
			double active = 0.0;
			double laggingReactive = 0.0;
			double leadingReactive = 0.0;
			double apparentEnergy = 0.0;
			boolean isExportEnergy = false;
			double exportEnergy = 0.0;
			String status = null;

			for (int i = 0; i < result.size(); i++) {
				if (!result.containsKey(OBIS.LOAD_PROFILE.getCode() + "-" + i))
					break;

				lpMap = (Map<String, Object>) result.get(OBIS.LOAD_PROFILE.getCode() + "-" + i);
				cnt = 0;
				while ((value = lpMap.get(LOAD_PROFILE.ImportActive.name() + "-" + cnt)) != null) {
					isExportEnergy = false;

					if (value instanceof Long) {
						lp = ((Long) value).doubleValue();
					} else if (value instanceof OCTET) {
						lp = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
					}
					lpValue = lp / activePulseConstant;
					//lpValue = lpValue * ct;
					lpValue = lpValue * st;
					active = lpValue;
					
					value = lpMap.get(LOAD_PROFILE.ImportLaggingReactive.name() + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET) {
							laggingReactive = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						} else if (value instanceof Long) {
							laggingReactive = ((Long) value).doubleValue();
						}
						laggingReactive /= reActivePulseConstant;
						//laggingReactive *= ct;
						laggingReactive *= st;
					}
					value = lpMap.get(LOAD_PROFILE.ImportLeadingReactive.name() + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET) {
							leadingReactive = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						} else if (value instanceof Long) {
							leadingReactive = ((Long) value).doubleValue();
						}
						leadingReactive /= reActivePulseConstant;
						//leadingReactive *= ct;
						leadingReactive *= st;
					}
					value = lpMap.get(LOAD_PROFILE.ImportApparentEnergy.name() + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET) {
							apparentEnergy = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						} else if (value instanceof Long) {
							apparentEnergy = ((Long) value).doubleValue();
						}
						apparentEnergy /= reActivePulseConstant;
						//apparentEnergy *= ct;
						apparentEnergy *= st;
					}

					_lpData = new LPData((String) lpMap.get(LOAD_PROFILE.Date.name() + "-" + cnt), lp, lpValue);
					_lpData.setCh(new Double[] { active, laggingReactive, leadingReactive, apparentEnergy });

					value = lpMap.get(LOAD_PROFILE.Status.name() + "-" + cnt++);
					if (value != null)
						status = (String) value;

					_lpData.setStatus(status);

					
					/*
					 * Modem에서 시간동기화를 할경우 00, 15, 30, 45 단위로 lp가 떨어지지 않고 03, 14.. 이런식으로 생성이되며
					 * 이렇게 생기는 LP는 버리기 위해서 아래 로직을 추가한다.
					 */
					int val = 0;
					if(_lpData.getDatetime() != null){
						int lpTime = Integer.parseInt(_lpData.getDatetime().substring(10, 12));
						val = lpTime % interval;
					}
					
					if (_lpData.getDatetime() == null || (0 != val)) {
						log.debug("#### [잘못된 LP_TIME 발생!!!] Interval = {}, LP Time = {}", interval, _lpData.getDatetime());
						log.debug("#### [잘못된 LP_TIME 발생!!!] Interval = {}, LP Time = {}", interval, _lpData.getDatetime());
						log.debug("#### [잘못된 LP_TIME 발생!!!] Interval = {}, LP Time = {}", interval, _lpData.getDatetime());
						continue;
					}

					lpDataMap.put(_lpData.getDatetime(), _lpData);
					// log.debug(_lpData.toString());
				}
			}

			lpData = lpDataMap.values().toArray(new LPData[0]);

			Arrays.sort(lpData, LPComparator.TIMESTAMP_ORDER);
			// lpData = checkEmptyLP(lpDataList);

			List<LPData> lpDataList = new ArrayList<LPData>();
			String dateTime = null;

			if (lpData.length > 0) {
				_lpData = new LPData();
				// BeanUtils.copyProperties(lpData[0], _lpData);
				if (lpData[0].getLp() instanceof Double) {
					_lpData.setLp(lpData[0].getLp());
					_lpData.setLpValue(lpData[0].getLpValue());
					_lpData.setStatus(lpData[0].getStatus());
				}
				_lpData.setCh(new Double[] { 0.0, 0.0, 0.0, 0.0	, lpData[0].getCh()[0], lpData[0].getCh()[1], lpData[0].getCh()[2], lpData[0].getCh()[3] });
				_lpData.setDatetime(checkLpTime(lpData[0].getDatetime()));
				dateTime = lpData[0].getDatetime();
				lpDataList.add(_lpData);

				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
				cal.setTime(sdf.parse(dateTime));

				for (int i = 0; i < lpData.length; i++) {
					if (i + 1 == lpData.length)
						break;

					_lpData = new LPData();
					BeanUtils.copyProperties(_lpData, lpData[i + 1]);

					// 데이타가 깨지는 경우가 발생하는 것으로 보임.
					cal.add(Calendar.MINUTE, interval);
					if (lpData[i + 1].getDatetime().equals(sdf.format(cal.getTime()))) {
						if ((lpData[i + 1].getCh()[0] != 0.0 && lpData[i].getCh()[0] != 0.0) && (lpData[i + 1].getCh()[0] > lpData[i].getCh()[0] * 2)) {
							lpData = null;
							return;
						}
					}
					cal.add(Calendar.MINUTE, interval * -1);

					// Turn over 계량기의 유효전력량이 최대치가 최면 0부터 다시 시작함.
					
					/**
					 * KEMCO 는 누적값이 옴. 그래서 차이값을 구하기 위해 빼줌.
					 */
					if (lpData[i + 1].getCh()[0] < lpData[i].getCh()[0]) {
						active = lpData[i + 1].getCh()[0] + diffMaxValue(lpData[i].getCh()[0]);
					} else {
						active = lpData[i + 1].getCh()[0] - lpData[i].getCh()[0];
					}

					if (lpData[i + 1].getCh()[1] < lpData[i].getCh()[1]) {
						laggingReactive = lpData[i + 1].getCh()[1] + diffMaxValue(lpData[i].getCh()[1]);
					} else {
						laggingReactive = lpData[i + 1].getCh()[1] - lpData[i].getCh()[1];
					}

					if (lpData[i + 1].getCh()[2] < lpData[i].getCh()[2]) {
						leadingReactive = lpData[i + 1].getCh()[2] + diffMaxValue(lpData[i].getCh()[2]);
					} else {
						leadingReactive = lpData[i + 1].getCh()[2] - lpData[i].getCh()[2];
					}

					if (lpData[i + 1].getCh()[3] < lpData[i].getCh()[3]) {
						apparentEnergy = lpData[i + 1].getCh()[3] + diffMaxValue(lpData[i].getCh()[3]);
					} else {
						apparentEnergy = lpData[i + 1].getCh()[3] - lpData[i].getCh()[3];
					}



					if (laggingReactive < 0 || leadingReactive < 0 || apparentEnergy < 0) {
						lpData = null;
						return;
					}

					double laggingPowerFactor = 1;
					if (active != 0 || laggingReactive != 0) {
						laggingPowerFactor = active / Math.sqrt(active * active + laggingReactive * laggingReactive);
					}
					double leadingPowerFactor = 1;
					if (active != 0 || leadingReactive != 0) {
						leadingPowerFactor = active / Math.sqrt(active * active + leadingReactive * leadingReactive);
					}

					double pf = 1.0;
					if (active == 0 && laggingReactive == 0 && leadingReactive == 0)
						pf = 1.0;
					else
						pf = active / Math.sqrt(active * active + (laggingReactive + leadingReactive) * (laggingReactive + leadingReactive));

                    /*
                     * channel 0~3 은 차이값, 4~7은 누적값.
                     *
                     */
					_lpData.setCh(new Double[] { active, laggingReactive, leadingReactive, apparentEnergy
							, lpData[i + 1].getCh()[0], lpData[i + 1].getCh()[1], lpData[i + 1].getCh()[2], lpData[i + 1].getCh()[3]});

					_lpData.setDatetime(checkLpTime(_lpData.getDatetime()));

					lpDataList.add(_lpData);

					if (_lpData.getDatetime().equals(sdf.format(cal.getTime()))) {
						// Max Demand를 설정한다.
						if (maxdemand <= active * 4) {
							maxdemand = active * 4;
							maxdemandTime = _lpData.getDatetime();
						}
					} else {
						cal.setTime(sdf.parse(_lpData.getDatetime()));
					}
					cal.add(Calendar.MINUTE, interval);
				}

				lpData = lpDataList.toArray(new LPData[0]);
				for (LPData l : lpData) {
					log.debug(l.toString());
				}
			}
			log.info("######################## LpData.length:" + lpData.length);
		} catch (Exception e) {
			log.error("ERROR-", e);
		}
	}

	private double diffMaxValue(double value) {
		String strValue = "" + value;
		strValue = strValue.substring(0, strValue.indexOf("."));
		double maxvalue = 1.0;
		for (int i = 0; i < strValue.length(); i++) {
			maxvalue *= 10;
		}
		return maxvalue - value;
	}

	private String checkLpTime(String lpTime) {
		// 시간을 체크한다. 시간 변경시 00, 15, 30, 45로 떨어지지 않는다.
		int mm = Integer.parseInt(lpTime.substring(10, 12));
		if (mm > 0 && mm < 15){
			mm = 0;
		} else if (mm > 15 && mm < 30){
			mm = 15;
		} else if (mm > 30 && mm < 45){
			mm = 30;
		} else if (mm > 45){
			mm = 45;
		}
		
		String revsionTime = String.format("%s%02d", lpTime.substring(0, 10), mm); 
		log.debug("### LP Time editing : "+ lpTime + " => " + revsionTime);
		return revsionTime + "00";
	}

	private LPData[] checkEmptyLP(List<LPData> list) throws Exception {
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

			if (prevTime != null && !prevTime.equals("")) {
				String temp = Util.addMinYymmdd(prevTime, interval);
				if (!temp.equals(currentTime)) {

					int diffMin = (int) ((Util.getMilliTimes(currentTime + "00") - Util.getMilliTimes(prevTime + "00")) / 1000 / 60) - interval;

					if (diffMin > 0 && diffMin <= 1440) { //하루이상 차이가 나지 않을때 빈값으로 채운다. 
						for (int i = 0; i < (diffMin / interval); i++) {

							log.debug("empty lp temp : " + currentTime + ", diff Min=" + diffMin);

							LPData data = new LPData();
							data.setLp(lp);
							data.setLpValue(lpValue);
							data.setV(v);
							data.setCh(ch);
							data.setFlag(0);
							data.setPF(1.0);
							data.setDatetime(Util.addMinYymmdd(prevTime, interval * (i + 1)));
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

		return totalList.toArray(new LPData[0]);
	}

	public void setPulseConstant() {
		try {
			Map<String, Object> activemap = (Map<String, Object>) result.get(OBIS.METER_CONSTANT_ACTIVE.getCode());  
			if (activemap != null) {
				Object obj = activemap.get(METER_CONSTANT.ActiveC.name());
				if (obj instanceof Float){
					activePulseConstant = (Float) obj;
				} else if (obj instanceof OCTET){
					activePulseConstant = DataUtil.getFloat(((OCTET) obj).getValue(), 0);
				}
				log.debug("ACTIVE_PULSE_CONSTANT[{}]", activePulseConstant);
				
				// 미터 펄스 상수에 넣는다.
				meter.setPulseConstant(activePulseConstant);

				Map<String, Object> reactivemap = (Map<String, Object>) result.get(OBIS.METER_CONSTANT_REACTIVE.getCode());
				if (reactivemap != null) {
					obj = reactivemap.get(METER_CONSTANT.ReactiveC.name());
					if (obj instanceof Float){
						reActivePulseConstant = (Float) obj;
					} else if (obj instanceof OCTET){
						reActivePulseConstant = DataUtil.getFloat(((OCTET) reactivemap.get(METER_CONSTANT.ReactiveC.name())).getValue(), 0);
					}
				} else {
					log.warn("Reactive Pulse not exist");
					reActivePulseConstant = activePulseConstant;
				}

				log.debug("REACTIVE_PULSE_CONSTANT[" + reActivePulseConstant + "]");
			}		
		} catch (Exception e) {
			log.error("ERROR-", e);
		}

	}

	public void setMeterInfo() {
		try {
			// 미터 아이디 생성
			String meterId = getCreateMeterId();
			meter.setMdsId(meterId);
			
			Map<String, Object> map = (Map<String, Object>) result.get(OBIS.METER_TIME.getCode());
			Object obj = map.get(OBIS.METER_TIME.name());
			if (obj != null){
				meterTime = (String) obj;
			}
			
			map = (Map<String, Object>) result.get(OBIS.METER_TIME.getCode() + ".1");
			
			if (map != null) {
				obj = map.get(OBIS.METER_TIME.name());
				if (obj != null) {
					beforeTime = meterTime;
					afterTime = (String) obj;
					meterTime = (String) afterTime;
				}
			}
			log.debug("MDSID = {}, METER_TIME = {}, BEFORE_TIME = {}, AFTER_TIME = {}", new Object[]{meterId, meterTime, beforeTime, afterTime});
			
			
			
		} catch (Exception e) {
			log.error("ERROR - ", e);
		}
	}

	public Instrument getInstrument() {  
		Instrument pq = new Instrument();
		
		try {
			double current_a = getDoubleFromLongOctet(result.get(OBIS.A_MAX_LOAD_ELECTRIC));
			log.debug("CURRENT_A[" + current_a + "]");
			double voltage_a = getDoubleFromLongOctet(result.get(OBIS.DEFAULT_VOLTAGE_AB));
			log.debug("VOLTAGE_A[" + voltage_a + "]");

			double current_b = getDoubleFromLongOctet(result.get(OBIS.B_MAX_LOAD_ELECTRIC));
			log.debug("CURRENT_B[" + current_b + "]");
			double voltage_b = getDoubleFromLongOctet(result.get(OBIS.DEFAULT_VOLTAGE_BA));
			log.debug("VOLTAGE_B[" + voltage_b + "]");

			double current_c = getDoubleFromLongOctet(result.get(OBIS.C_MAX_LOAD_ELECTRIC));
			log.debug("CURRENT_C[" + current_c + "]");
			double voltage_c = getDoubleFromLongOctet(result.get(OBIS.DEFAULT_VOLTAGE_CA));
			log.debug("VOLTAGE_C[" + voltage_c + "]");
			
			pq.setCURR_A(current_a);
			pq.setVOL_A(voltage_a);

			pq.setCURR_B(current_b);
			pq.setVOL_B(voltage_b);

			pq.setCURR_C(current_c);
			pq.setVOL_C(voltage_c);
		} catch (Exception e) {
			log.error("ERROR -", e);
		}   
		
		return pq;
	}

	public void setPreviousMaxDemand() {
		Map<String, Object> map = null;
		log.debug("###################  살펴볼것 setPreviousMaxDemand #################");
//		try {
//			map = (Map<String, Object>) result.get(OBIS.PREVIOUS_MAX_DEMAND.getCode());
//			long t1PreActive = (Long) map.get(PREVIOUS_MAX_DEMAND.T1PreviousActive.name());
//			log.debug("T1_PREVIOUS_ACTIVE[" + t1PreActive + "]");
//			long t2PreActive = (Long) map.get(PREVIOUS_MAX_DEMAND.T2PreviousActive.name());
//			log.debug("T2_PREVIOUS_ACTIVE[" + t2PreActive + "]");
//			long t3PreActive = (Long) map.get(PREVIOUS_MAX_DEMAND.T3PreviousActive.name());
//			log.debug("T3_PREVIOUS_ACTIVE[" + t3PreActive + "]");
//			double previousDemandData = ((t1PreActive + t2PreActive + t3PreActive) / this.activePulseConstant);
//			log.debug("PREVIOUS_DEMAND[" + previousDemandData + "]");
//		} catch (Exception e) {
//			log.error("ERROR - ", e);
//		}
	}

	public void setCurrentMaxDemand() {
		Map<String, Object> map = null;
		log.debug("###################  살펴볼것 setCurrentMaxDemand #################");
//		try {
//			map = (Map<String, Object>) result.get(OBIS.CURRENT_MAX_DEMAND.getCode());
//			long t1CurActive = (Long) map.get(CURRENT_MAX_DEMAND.T1CurrentActive.name());
//			long t2CurActive = (Long) map.get(CURRENT_MAX_DEMAND.T2CurrentActive.name());
//			long t3CurActive = (Long) map.get(CURRENT_MAX_DEMAND.T3CurrentActive.name());
//			double currentDemandData = (t1CurActive + t2CurActive + t3CurActive) / this.activePulseConstant;
//			log.debug("CURRENT_DEMAND[" + currentDemandData + "]");
//		} catch (Exception e) {
//			log.error("ERROR - ", e);
//		}
	}

	public Map<String, Object> getPreviousMaxDemand() {
		//return (Map<String, Object>) result.get(OBIS.PREVIOUS_MAX_DEMAND.getCode());
		log.debug("###################  살펴볼것 getPreviousMaxDemand #################");
		return null;
	}

	public Map<String, Object> getCurrentMaxDemand() {
		//return (Map<String, Object>) result.get(OBIS.CURRENT_MAX_DEMAND.getCode());
		log.debug("###################  살펴볼것 getCurrentMaxDemand #################");
		return null;
	}

	public EventLogData[] getMeterEvent() {
		List<EventLogData> elist = new ArrayList<EventLogData>();
		Map<String, Object> sub = null;
		if ((sub = result.get(OBIS.POWER_FAILURE.getCode())) != null) {
			elist.addAll(makeEventLog(17, sub));
		}
//		if ((sub = result.get(OBIS.POWER_RESTORE.getCode())) != null) {
//			elist.addAll(makeEventLog(117, sub));
//		}
//		if ((sub = result.get(OBIS.SAG.getCode())) != null) {
//			elist.addAll(makeEventLog(8, sub));
//		}
//		if ((sub = result.get(OBIS.SWELL.getCode())) != null) {
//			elist.addAll(makeEventLog(108, sub));
//		}

		return elist.toArray(new EventLogData[0]);
	}

	private List<EventLogData> makeEventLog(int flag, Map<String, Object> sub) {
		List<EventLogData> elist = new ArrayList<EventLogData>();
		String eventTime = null;
		for (int i = 0;; i++) {
			eventTime = (String) sub.get(EVENT.EventTime.name() + "-" + i);
			if (eventTime == null)
				break;
			else {
				EventLogData e = new EventLogData();
//				e.setDate(eventTime.substring(6, 14));
//				e.setTime(eventTime.substring(14, 18) + "00");
				e.setDate(eventTime.substring(0, 8));
				e.setTime(eventTime.substring(8, 14));
				e.setFlag(flag);
				log.debug(e.toString());
				elist.add(e);
			}
		}
		return elist;
	}

	public Map<String, Object> getEventLog() {
		Map<String, Object> eventLogs = new LinkedHashMap<String, Object>();
		if (result.get(OBIS.POWER_FAILURE.getCode()) != null)
			eventLogs.putAll(result.get(OBIS.POWER_FAILURE.getCode()));
//		if (result.get(OBIS.POWER_RESTORE.getCode()) != null)
//			eventLogs.putAll(result.get(OBIS.POWER_RESTORE.getCode()));
//		if (result.get(OBIS.TIME_CHANGE_FROM.getCode()) != null)
//			eventLogs.putAll(result.get(OBIS.TIME_CHANGE_FROM.getCode()));
//		if (result.get(OBIS.TIME_CHANGE_TO.getCode()) != null)
//			eventLogs.putAll(result.get(OBIS.TIME_CHANGE_TO.getCode()));
//		if (result.get(OBIS.DEMAND_RESET.getCode()) != null)
//			eventLogs.putAll(result.get(OBIS.DEMAND_RESET.getCode()));
//		if (result.get(OBIS.MANUAL_DEMAND_RESET.getCode()) != null)
//			eventLogs.putAll(result.get(OBIS.MANUAL_DEMAND_RESET.getCode()));
//		if (result.get(OBIS.SELF_READ.getCode()) != null)
//			eventLogs.putAll(result.get(OBIS.SELF_READ.getCode()));
//		if (result.get(OBIS.PROGRAM_CHANGE.getCode()) != null)
//			eventLogs.putAll(result.get(OBIS.PROGRAM_CHANGE.getCode()));
		
		return eventLogs;
	}

	private double getDoubleFromLongOctet(Object value) throws Exception {
		if (value instanceof Float)
			return ((Float) value).doubleValue();
		else if (value instanceof Long)
			return ((Long) value).doubleValue();
		else if (value instanceof OCTET)
			return DataUtil.getFloat(((OCTET) value).getValue(), 0);

		return 0;
	}

	public BillingData[] getCurrentMonthly() {
		BillingData[] billEm = new BillingData[2];

		try {
			Map<String, Object> map = (Map<String, Object>) result.get(OBIS.METER_TIME.getCode());
			billEm[0] = new BillingData();
			//Object obj = map.get("MeterTime");
			Object obj = map.get(OBIS.METER_TIME.name());
			if (obj != null) {
				meterTime = (String) obj;
				billEm[0].setBillingTimestamp(meterTime + "00");

				map = (Map<String, Object>) result.get(OBIS.MONTHLY_ENERGY_PROFILE_CURRENT.getCode());
				if (map != null) {
					// 전체 순방향 유효전력량
					Object value = map.get(MONTHLY_ENERGY_PROFILE.ActiveEnergy.name());
					billEm[0].setActiveEnergyImportRateTotal(getDoubleFromLongOctet(value) / this.activePulseConstant);

					// 전체 순방향 피상전력량을 무효전력량
					value = map.get(MONTHLY_ENERGY_PROFILE.ApparentEnergy.name());
					billEm[0].setCummkVah1RateTotal(getDoubleFromLongOctet(value) / this.activePulseConstant);
					// 전체 순방향 지상 무효전력량을 
					value = map.get(MONTHLY_ENERGY_PROFILE.LaggingReactiveEnergy.name());
					billEm[0].setReactiveEnergyLagImportRateTotal(getDoubleFromLongOctet(value) / this.reActivePulseConstant);
					// 전체 순방향 진상 무효전력량
					value = map.get(MONTHLY_ENERGY_PROFILE.LeadingReactiveEnergy.name());
					billEm[0].setReactiveEnergyLeadImportRateTotal(getDoubleFromLongOctet(value) / this.reActivePulseConstant);
					// 전체 순방향 평균 역률
					value = map.get(MONTHLY_ENERGY_PROFILE.AveragePowerFactor.name());
					billEm[0].setPf(getDoubleFromLongOctet(value));

					// T1 순방향 유효전력량
					value = map.get(MONTHLY_ENERGY_PROFILE.T1ActiveEnergy.name());
					billEm[0].setActiveEnergyImportRate1(getDoubleFromLongOctet(value) / this.activePulseConstant);
					// T1 순방향 피상전력량을 무효전력량
					value = map.get(MONTHLY_ENERGY_PROFILE.T1ApparentEnergy.name());
					billEm[0].setCummkVah1Rate1(getDoubleFromLongOctet(value) / this.activePulseConstant);
					// T1 순방향 지상 무효전력량을 
					value = map.get(MONTHLY_ENERGY_PROFILE.T1LaggingReactiveEnergy.name());
					billEm[0].setReactiveEnergyLagImportRate1(getDoubleFromLongOctet(value) / this.reActivePulseConstant);
					// T1 순방향 진상 무효전력량
					value = map.get(MONTHLY_ENERGY_PROFILE.T1LeadingReactiveEnergy.name());
					billEm[0].setReactiveEnergyLeadImportRate1(getDoubleFromLongOctet(value) / this.reActivePulseConstant);

					// T2 순방향 유효전력량
					value = map.get(MONTHLY_ENERGY_PROFILE.T2ActiveEnergy.name());
					billEm[0].setActiveEnergyImportRate2(getDoubleFromLongOctet(value) / this.activePulseConstant);
					// T2 순방향 피상전력량을 무효전력량
					value = map.get(MONTHLY_ENERGY_PROFILE.T2ApparentEnergy.name());
					billEm[0].setCummkVah1Rate2(getDoubleFromLongOctet(value) / this.activePulseConstant);
					// T2 순방향 지상 무효전력량을 
					value = map.get(MONTHLY_ENERGY_PROFILE.T2LaggingReactiveEnergy.name());
					billEm[0].setReactiveEnergyLagImportRate2(getDoubleFromLongOctet(value) / this.reActivePulseConstant);
					// T2 순방향 진상 무효전력량
					value = map.get(MONTHLY_ENERGY_PROFILE.T2LeadingReactiveEnergy.name());
					billEm[0].setReactiveEnergyLeadImportRate2(getDoubleFromLongOctet(value) / this.reActivePulseConstant);

					// T3 순방향 유효전력량
					value = map.get(MONTHLY_ENERGY_PROFILE.T3ActiveEnergy.name());
					billEm[0].setActiveEnergyImportRate3(getDoubleFromLongOctet(value) / this.activePulseConstant);
					// T3 순방향 피상전력량을 무효전력량
					value = map.get(MONTHLY_ENERGY_PROFILE.T3ApparentEnergy.name());
					billEm[0].setCummkVah1Rate3(getDoubleFromLongOctet(value) / this.activePulseConstant);
					// T3 순방향 지상 무효전력량을 
					value = map.get(MONTHLY_ENERGY_PROFILE.T3LaggingReactiveEnergy.name());
					billEm[0].setReactiveEnergyLagImportRate3(getDoubleFromLongOctet(value) / this.reActivePulseConstant);
					// T3 순방향 진상 무효전력량
					value = map.get(MONTHLY_ENERGY_PROFILE.T3LeadingReactiveEnergy.name());
					billEm[0].setReactiveEnergyLeadImportRate3(getDoubleFromLongOctet(value) / this.reActivePulseConstant);

					// T4 순방향 유효전력량
					value = map.get(MONTHLY_ENERGY_PROFILE.T4ActiveEnergy.name());
					billEm[0].setActiveEnergyImportRate4(getDoubleFromLongOctet(value) / this.activePulseConstant);
					// T4 순방향 피상전력량을 무효전력량
					value = map.get(MONTHLY_ENERGY_PROFILE.T4ApparentEnergy.name());
					billEm[0].setCummkVah1Rate4(getDoubleFromLongOctet(value) / this.activePulseConstant);
					// T4 순방향 지상 무효전력량을 
					value = map.get(MONTHLY_ENERGY_PROFILE.T4LaggingReactiveEnergy.name());
					billEm[0].setReactiveEnergyLagImportRate4(getDoubleFromLongOctet(value) / this.reActivePulseConstant);
					// T4 순방향 진상 무효전력량
					value = map.get(MONTHLY_ENERGY_PROFILE.T4LeadingReactiveEnergy.name());
					billEm[0].setReactiveEnergyLeadImportRate4(getDoubleFromLongOctet(value) / this.reActivePulseConstant);
				}

				map = (Map<String, Object>) result.get(OBIS.MONTHLY_DEMAND_PROFILE_CURRENT.getCode());
				if (map != null && map.size() > 0) {
					Object value = map.get(MONTHLY_DEMAND_PROFILE.Active.name());
					if (value != null) {
						billEm[0].setActivePwrDmdMaxImportRateTotal(getDoubleFromLongOctet(value) / this.activePulseConstant);
						billEm[0].setActivePwrDmdMaxTimeImportRateTotal(checkDate((String) map.get(MONTHLY_DEMAND_PROFILE.ActiveDate.name())));
						billEm[0].setCummActivePwrDmdMaxImportRateTotal(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.CummlativeActive.name())) / this.activePulseConstant);
						billEm[0].setMaxDmdkVah1RateTotal(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.Apparent.name())) / this.activePulseConstant);
						billEm[0].setMaxDmdkVah1TimeRateTotal(checkDate((String) map.get(MONTHLY_DEMAND_PROFILE.ApparentDate.name())));
						billEm[0].setCummkVah1RateTotal(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.CummlativeApparent.name())) / this.activePulseConstant);
					}
					value = map.get(MONTHLY_DEMAND_PROFILE.T1Active.name());
					if (value != null) {
						billEm[0].setActivePwrDmdMaxImportRate1(getDoubleFromLongOctet(value) / this.activePulseConstant);
						billEm[0].setActivePwrDmdMaxTimeImportRate1(checkDate((String) map.get(MONTHLY_DEMAND_PROFILE.T1ActiveDate.name())));
						billEm[0].setCummActivePwrDmdMaxImportRate1(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T1CummlativeActive.name())) / this.activePulseConstant);
						billEm[0].setMaxDmdkVah1Rate1(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T1Apparent.name())) / this.activePulseConstant);
						billEm[0].setMaxDmdkVah1TimeRate1(checkDate((String) map.get(MONTHLY_DEMAND_PROFILE.T1ApparentDate.name())));
						billEm[0].setCummkVah1Rate1(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T1CummlativeApparent.name())) / this.activePulseConstant);
					}
					value = map.get(MONTHLY_DEMAND_PROFILE.T2Active.name());
					if (value != null) {
						billEm[0].setActivePwrDmdMaxImportRate2(getDoubleFromLongOctet(value) / this.activePulseConstant);
						billEm[0].setActivePwrDmdMaxTimeImportRate2(checkDate((String) map.get(MONTHLY_DEMAND_PROFILE.T2ActiveDate.name())));
						billEm[0].setCummActivePwrDmdMaxImportRate2(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T2CummlativeActive.name())) / this.activePulseConstant);
						billEm[0].setMaxDmdkVah1Rate2(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T2Apparent.name())) / this.activePulseConstant);
						billEm[0].setMaxDmdkVah1TimeRate2(checkDate((String) map.get(MONTHLY_DEMAND_PROFILE.T2ApparentDate.name())));
						billEm[0].setCummkVah1Rate2(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T2CummlativeApparent.name())) / this.activePulseConstant);
					}
					value = map.get(MONTHLY_DEMAND_PROFILE.T3Active.name());
					if (value != null) {
						billEm[0].setActivePwrDmdMaxImportRate3(getDoubleFromLongOctet(value) / this.activePulseConstant);
						billEm[0].setActivePwrDmdMaxTimeImportRate3(checkDate((String) map.get(MONTHLY_DEMAND_PROFILE.T3ActiveDate.name())));
						billEm[0].setCummActivePwrDmdMaxImportRate3(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T3CummlativeActive.name())) / this.activePulseConstant);
						billEm[0].setMaxDmdkVah1Rate3(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T3Apparent.name())) / this.activePulseConstant);
						billEm[0].setMaxDmdkVah1TimeRate3(checkDate((String) map.get(MONTHLY_DEMAND_PROFILE.T3ApparentDate.name())));
						billEm[0].setCummkVah1Rate3(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T3CummlativeApparent.name())) / this.activePulseConstant);
					}
					value = map.get(MONTHLY_DEMAND_PROFILE.T4Active.name());
					if (value != null) {
						billEm[0].setActivePwrDmdMaxImportRate4(getDoubleFromLongOctet(value) / this.activePulseConstant);
						billEm[0].setActivePwrDmdMaxTimeImportRate4(checkDate((String) map.get(MONTHLY_DEMAND_PROFILE.T4ActiveDate.name())));
						billEm[0].setCummActivePwrDmdMaxImportRate4(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T4CummlativeActive.name())) / this.activePulseConstant);
						billEm[0].setMaxDmdkVah1Rate4(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T4Apparent.name())) / this.activePulseConstant);
						billEm[0].setMaxDmdkVah1TimeRate4(checkDate((String) map.get(MONTHLY_DEMAND_PROFILE.T4ApparentDate.name())));
						billEm[0].setCummkVah1Rate4(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T4CummlativeApparent.name())) / this.activePulseConstant);
					}
				}
			}

			if (meterTime != null) {
				billEm[1] = new BillingData();
				billEm[1].setBillingTimestamp(meterTime);
				// 익월 1일로 변경한다.
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
				cal.setTime(sdf.parse(billEm[1].getBillingTimestamp()));
				cal.add(Calendar.MONTH, 1);
				billEm[1].setBillingTimestamp(sdf.format(cal.getTime()).substring(0, 6) + "01");

				LPData lastLP = lpData[lpData.length - 1];
				
				
				// 전체 순방향 유효전력량
				//billEm[1].setActiveEnergyImportRateTotal(lastLP.getCh()[1]);   // 순방향 유효전력량 0
				billEm[1].setActiveEnergyImportRateTotal(lastLP.getCh()[0]);   // 순방향 유효전력량 0
				//billEm[1].setActiveEnergyRateTotal(lastLP.getCh()[1]);
				billEm[1].setActiveEnergyRateTotal(lastLP.getCh()[0]);

				// 전체 순방향 피상전력량을 무효전력량
				//billEm[1].setCummkVah1RateTotal(lastLP.getCh()[4]);         
				billEm[1].setCummkVah1RateTotal(lastLP.getCh()[3]);           // 순방향 피상 전력량
				// 전체 순방향 지상 무효전력량을 
				//billEm[1].setReactiveEnergyLagImportRateTotal(lastLP.getCh()[2]);   
				billEm[1].setReactiveEnergyLagImportRateTotal(lastLP.getCh()[1]);   // 순방향 지상 무효전력량 1
				// 전체 순방향 진상 무효전력량
				//billEm[1].setReactiveEnergyLeadImportRateTotal(lastLP.getCh()[3]);
				billEm[1].setReactiveEnergyLeadImportRateTotal(lastLP.getCh()[2]);   // 순방향 진상 무효전력량 2
				if (lastLP.getCh().length == 5) {
					//billEm[1].setActiveEnergyExportRateTotal(lastLP.getCh()[4]); 
					billEm[1].setActiveEnergyExportRateTotal(0.0);   // 0.0
				}
				
				// 15분 사용량에 4를 곱하면 수요 전력으로 변경됨.
				billEm[1].setActivePwrDmdMaxImportRateTotal(maxdemand);
				billEm[1].setActivePwrDmdMaxTimeImportRateTotal(maxdemandTime);
			}
		} catch (Exception e) {
			log.warn("WARN - ", e);
		}
		return billEm;
	}

	private String checkDate(String date) {
		if (date != null && date.contains(":date"))
			return date.substring(6);
		return date;
	}

	public void setMeteringValue() {
		try {
			Map<String, Object> map = (Map<String, Object>) result.get(OBIS.MONTHLY_ENERGY_PROFILE_CURRENT.getCode());
			if (map != null) {
				long active = (Long) map.get(MONTHLY_ENERGY_PROFILE.ActiveEnergy.name());
				BigDecimal bd = new BigDecimal(active);
				meteringValue = bd.doubleValue() / this.activePulseConstant;
				//meteringValue = meteringValue * getCt();
				meteringValue = meteringValue * st;
			} else {
				// 현월 누적 사용량을 가져오지 않는 경우 LP의 마지막 누적 사용량을 가져온다.
				if (lpData != null && lpData.length > 0)
					//meteringValue = lpData[lpData.length - 1].getCh()[1];
					meteringValue = lpData[lpData.length - 1].getCh()[4];
				else
					meteringValue = 0.0;
			}
			log.debug("METERING_VALUE = [" + meteringValue + "]");
		} catch (Exception e) {
			log.error("ERROR - ", e);
		}
	}

	public LinkedHashMap<String, Object> getRelayStatus() {
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
//		Map<String, Object> loadControlMap = result.get(OBIS.LOAD_CONTROL_STATUS.getCode());
//		Map<String, Object> outputSignalMap = result.get(OBIS.OUTPUT_SIGNAL.getCode());
//
//		log.debug("LoadControlStatus : " + loadControlMap.get("LoadControlStatus"));
//		log.debug("OutSignal : " + outputSignalMap.get("OutputSignal"));
//
//		map.put("LoadControlStatus", loadControlMap.get("LoadControlStatus"));
//		map.put("OutputSignal", outputSignalMap.get("OutputSignal"));
		
		log.debug("################# 확인 할것 getRelayStatus ###################");
		return map;
	}

	public LinkedHashMap<String, Object> getMeterSyncTime() {
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("beforeTime", beforeTime);
		map.put("afterTime", afterTime);
		return map;
	}

	public Integer getInterval() {
		return this.interval;
	}

	public Double getActivePulseConstant() {
		return this.activePulseConstant;
	}

	public Double getReActivePulseConstant() {
		return this.reActivePulseConstant;
	}

	public String getMeterID() {
		return this.meter.getMdsId();
	}

	public void setCt(Double ct) {
		this.ct = ct;
	}

	public Double getCt() {
		return this.ct;
	}

	public EMnVMeteringDataType getObisType(){
		return meteringDataType;
	}
	
	@Override
	public int getFlag() {
		return 0;    // getObisType() 으로 대체
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
		System.out.println("################# getRawData 여기 확인해볼것");
		return null;
	}
	
	
	/**
	 * 미터아이디 생성 ~!! mfMeterid 앞2 + - + 뒤2 + customerMeterId7  = totoal 12byte
	 * @return
	 */
	public String getCreateMeterId(){
		String mfMeterId = (String) (result.get(OBIS.MANUFACTURER_METER_ID.getCode())).get(OBIS.MANUFACTURER_METER_ID.name());
		String customerMeterId = (String) (result.get(OBIS.CUSTOMER_METER_ID.getCode())).get(OBIS.CUSTOMER_METER_ID.name());
		
		String mdsid = mfMeterId.substring(0, 2) + "-" + mfMeterId.substring(mfMeterId.length()-2, mfMeterId.length()) + customerMeterId;
		
		log.info("MDSID = {}, manufacture meter id = {}, customer meter id = {}", new Object[]{mdsid, mfMeterId, customerMeterId});
		
		return mdsid;
	}



//    public int getResolution(){
//
//        try{
//            if(interval != 0){
//                return interval;
//            }
//            else{
//                return Integer.parseInt(FMPProperty.getProperty("lp.resolution.default"));
//            }
//        } catch(Exception e){
//        	log.error("Error - ", e);
//        }
//        return 60;
//    }
	

    /**
     * System.out.println() 으로 HEX 출력.
     * @param data
     * @param tPos
     * @param showLength
     */
    public void printHexByteString(byte[] data, int tPos, int showLength){
		int loggingLenth = (data.length - tPos) < showLength ? data.length - tPos : showLength;
		
		byte[] logging = new byte[showLength];
		System.arraycopy(data, tPos, logging, 0, loggingLenth);
		log.info("### SHOW HEX POS[" + tPos + "] 부터 "+ loggingLenth +"byte ==> " + Hex.getHexDump(logging));
    }
	
}
