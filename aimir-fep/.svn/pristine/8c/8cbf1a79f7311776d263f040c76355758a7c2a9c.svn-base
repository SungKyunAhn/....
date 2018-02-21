/**
 * (@)# DLMSEMnVEType_1_0.java
 *
 * 2015. 5. 5.
 *
 * Copyright (c) 2013 NURITELECOM, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * NURITELECOM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITELECOM, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.fep.meter.parser;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.parser.DLMSEMnVEtype_1_0_Table.DLMSEMnVEType_1_0_Table;
import com.aimir.fep.meter.parser.DLMSEMnVEtype_1_0_Table.DLMSEMnVEType_1_0_VARIABLE.DLMS_CLASS;
import com.aimir.fep.meter.parser.DLMSEMnVEtype_1_0_Table.DLMSEMnVEType_1_0_VARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSEMnVEtype_1_0_Table.DLMSEMnVEType_1_0_VARIABLE.LOAD_PROFILE;
import com.aimir.fep.meter.parser.DLMSEMnVEtype_1_0_Table.DLMSEMnVEType_1_0_VARIABLE.METER_CONSTANT;
import com.aimir.fep.meter.parser.DLMSEMnVEtype_1_0_Table.DLMSEMnVEType_1_0_VARIABLE.OBIS;
import com.aimir.fep.meter.parser.DLMSKepcoTable.LPComparator;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVMeteringDataType;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.EnergyMeter;

/**
 * @author simhanger
 *
 */
public class DLMSEMnVEType_1_0 extends MeterDataParser implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(DLMSEMnVEType_1_0.class);
	LPData[] lpData = null;
	LinkedHashMap<String, Map<String, Object>> result = new LinkedHashMap<String, Map<String, Object>>();
	int interval = 0;
	Double ct = 1d;
	Double pt = 1d;
	Double st = 1d; // 서버적용배율
	double scaleUnit = 1000; // 2015.10.23 에관공 요청으로 추가
	Object beforeTime = null;
	Object afterTime = null;
	double activePulseConstant = 0;
	Double meteringValue = null;
	String maxdemandTime = null;
	Double maxdemand = 0.0;

	private EMnVMeteringDataType meteringDataType = EMnVMeteringDataType.LOAD_PROFILE; // E-Type1.0은 Load Profile 밖에 없음.

	@SuppressWarnings("unchecked")
	@Override
	public void parse(byte[] data) throws Exception {
		// 로그 확인 편하도록....
		log.info("    ");
		log.info("    ");
		log.info("    ");
		log.info("############################## 로그확인 시작 ################################################");

		log.info("DLMSEMnVE-type1.0 parse:[{}] [{}]", meteringDataType != null ? meteringDataType.name() : "", Hex.decode(data));

		int obisListCount = 0; // 미터의 수집된 전체 OBIS의 개수
		int mdLenth = 0; // Metering Data의 전체 Data의 길이

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
		DLMSEMnVEType_1_0_Table dlms = null;
		int obisLenth = 0;
		for (int i = 0; i < obisListCount; i++) {
			log.info("-----------------------------------------------------------------------");
			dlms = new DLMSEMnVEType_1_0_Table();

			if (pos + 30 <= data.length) {
				printHexByteString(data, pos, 30);
			} else {
				printHexByteString(data, pos, data.length - pos);
			}

			System.arraycopy(data, pos, CLAZZ, 0, CLAZZ.length);
			pos += CLAZZ.length;
			clazz = DataUtil.getIntToBytes(CLAZZ);
			dlms.setClazz(clazz);
			log.info("[{}][PROTOCOL][METERING_DATA] DLMS_CLASS_ID   (2):[{}] ==> HEX=[{}]", new Object[] { i, DLMS_CLASS.getItems(clazz), Hex.decode(CLAZZ) });

			System.arraycopy(data, pos, OBIS_DATA, 0, OBIS_DATA.length);
			pos += OBIS_DATA.length;
			obisCode = Hex.decode(OBIS_DATA);

			dlms.setObis(getObisType(), obisCode);
			log.info("[{}][PROTOCOL][METERING_DATA] DLMS_OBIS_CODE  (6):[{}][{}] ==> HEX=[{}]", new Object[] { i, getObisType(), OBIS.getObis(obisCode), Hex.decode(OBIS_DATA) });
			System.arraycopy(data, pos, ATTR, 0, ATTR.length);
			pos += ATTR.length;
			attr = DataUtil.getIntToBytes(ATTR);
			dlms.setAttr(attr);
			log.info("[{}][PROTOCOL][METERING_DATA] DLMS_ATTR_NUMBER(1):[{}] ==> HEX=[{}]", new Object[] { i, DLMS_CLASS_ATTR.getItems(attr), Hex.decode(ATTR) });

			obisLenth = dlms.setData(pos, data); // return 값 : 진행된 pos
			pos = obisLenth;

			//////// DEBUG //////
			Map<String, Object> dlmsData = (Map<String, Object>) dlms.getData();
			log.info("### OBIS  ==> {}", dlmsData.toString());

			// 동일한 obis 코드를 가진 값이 있을 수 있기 때문에 검사해서 _number를 붙인다.
			if (dlms.getDlmsHeader().getObis() == OBIS.LOAD_PROFILE || dlms.getDlmsHeader().getObis() == OBIS.LP_INTERVAL) {
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
		} //END For문

		log.debug("### OBIS TOTAL ==> {}", result.toString());
		log.info("### Number_of_OBIS={} Data_Len={} Last POS={}", new Object[] { obisListCount, mdLenth, pos });

		//		EnergyMeter meter = (EnergyMeter) this.getMeter();
		//
		//		this.ct = 1.0;
		//		if (meter != null && meter.getCt() != null && meter.getCt() > 0){
		//			ct = meter.getCt();
		//		}
		//
		//		setCt(ct);
		setMeterInfo();
		setPulseConstant();
		//		setMeteringValue();
		//		setLPData(null);

		//log.debug("DLMS parse result:" + result);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public LinkedHashMap<String, Map<String, Object>> getData() {
		Map<String, Object> data = new LinkedHashMap<String, Object>(16, 0.75f, false);
		return (LinkedHashMap) data;
	}

	public void setMeterInfo() {
		try {
			// 미터 아이디 생성
			String meterId = getCreateMeterId();
			meter.setMdsId(meterId);

			Map<String, Object> map = (Map<String, Object>) result.get(OBIS.METER_TIME.getCode());
			Object obj = map.get(OBIS.METER_TIME.name());
			if (obj != null) {
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
			log.debug("MDSID = {}, METER_TIME = {}, BEFORE_TIME = {}, AFTER_TIME = {}", new Object[] { meterId, meterTime, beforeTime, afterTime });

		} catch (Exception e) {
			log.error("ERROR - ", e);
		}
	}

	public void setPulseConstant() {
		try {
			Map<String, Object> activemap = (Map<String, Object>) result.get(OBIS.METER_CONSTANT_ACTIVE.getCode());
			if (activemap != null) {
				Object obj = activemap.get(METER_CONSTANT.ActiveC.name());
				if (obj instanceof Float) {
					activePulseConstant = (Float) obj;
				} else if (obj instanceof OCTET) {
					activePulseConstant = DataUtil.getFloat(((OCTET) obj).getValue(), 0);
				}
				log.debug("ACTIVE_PULSE_CONSTANT[{}]", activePulseConstant);

				// 미터 펄스 상수에 넣는다.
				meter.setPulseConstant(activePulseConstant);
			}
		} catch (Exception e) {
			log.error("ERROR-", e);
		}

	}

	@Override
	public byte[] getRawData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Double getMeteringValue() {
		return meteringValue;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	public EMnVMeteringDataType getObisType() {
		return meteringDataType;
	}

	public String getMeterID() {
		return this.meter.getMdsId();
	}

	public Double getSt() {
		return this.st;
	}

	public void setCt(Double ct) {
		this.ct = ct;
	}

	@Override
	public int getFlag() {
		// TODO Auto-generated method stub
		return 0;
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
			//			double laggingReactive = 0.0;
			//			double leadingReactive = 0.0;
			//			double apparentEnergy = 0.0;
			//			boolean isExportEnergy = false;
			//			double exportEnergy = 0.0;
			//			String status = null;

			for (int i = 0; i < result.size(); i++) {
				if (!result.containsKey(OBIS.LOAD_PROFILE.getCode() + "-" + i))
					break;

				lpMap = (Map<String, Object>) result.get(OBIS.LOAD_PROFILE.getCode() + "-" + i);
				cnt = 0;
				while ((value = lpMap.get(LOAD_PROFILE.ImportActive.name() + "-" + cnt)) != null) { //순방향 유효전력량
				//					isExportEnergy = false;

					if (value instanceof Long) {
						lp = ((Long) value).doubleValue();
					} else if (value instanceof OCTET) {
						lp = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
					}

					/**
					 * 2015.10.23 에관공 요청으로 옴니의 민수용계량기의 경우 계기정수를 나누지 않고 wh 를
					 * kwh로만 변경하여 보여주길 원함. 그래서 scaleUnit 도입.
					 */
					//lpValue = lp / activePulseConstant;					
					lpValue = lp / scaleUnit;
					lpValue = lpValue * st;
					active = lpValue;

					_lpData = new LPData((String) lpMap.get(LOAD_PROFILE.Date.name() + "-" + cnt), lp, lpValue); // 일자/시간
					//_lpData.setCh(new Double[] { active, laggingReactive, leadingReactive, apparentEnergy });
					_lpData.setCh(new Double[] { active });
					cnt++;

					//					value = lpMap.get(LOAD_PROFILE.Status.name() + "-" + cnt++);
					//					if (value != null){
					//						status = (String) value;
					//					}
					//					_lpData.setStatus(status);

//					if (_lpData.getDatetime() == null)
//						continue;
					
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
				_lpData.setCh(new Double[] { 0.0, lpData[0].getCh()[0] });

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
					if (lpData[i + 1].getCh()[0] < lpData[i].getCh()[0]) {
						active = lpData[i + 1].getCh()[0] + diffMaxValue(lpData[i].getCh()[0]);
					} else {
						active = lpData[i + 1].getCh()[0] - lpData[i].getCh()[0];
					}

					/*
					 * 미터로부터 오는 값이 누적값이어서 차이값을 구하기위해
					 * ch[0]에 차이값(실 사용치) ch[1]에 누적값을 저장한다.
					 */
					_lpData.setCh(new Double[] { active, lpData[i + 1].getCh()[0] });
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
		if (mm > 0 && mm < 15)
			mm = 0;
		else if (mm > 15 && mm < 30)
			mm = 15;
		else if (mm > 30 && mm < 45)
			mm = 30;
		else if (mm > 45)
			mm = 45;
		return String.format("%s%02d", lpTime.substring(0, 10), mm) + "00";
	}

	/**
	 * E-type1.0 미터 같은경우는 Load profile OBIS Code 밖에 없음.
	 */
	@Override
	public void setFlag(int flag) {

	}

	public void postParse() {
		/*
		 *  최초 미터가 등록되지 않았을때 Load Profile OBIS로 넘어올경우
		 *  pulseconstant가 없기때문에 디폴트 값(fmp.properties의 meter.pulse.constant.hmu=5000)을 넣어준다.
		 *  pulseconstant가 없으면 lpData만들때 에러남.
		 */
		if (activePulseConstant == 0.0) {
			activePulseConstant = meter.getPulseConstant();
		}

		//		setMeteringValue();

		EnergyMeter meter = (EnergyMeter) this.getMeter();
		if (meter != null && meter.getCt() != null && meter.getCt() > 0) {
			ct = meter.getCt();
		}

		if (meter != null && meter.getPt() != null && meter.getPt() > 0) {
			pt = meter.getPt();
		}

		st = ct * pt; // 서버 적용배율

		setLPData();
	}

	public void setMeteringValue() {
		try {
			Map<String, Object> map = (Map<String, Object>) result.get(OBIS.IMPORT_ACTIVE_ENERGY.getCode());
			if (map != null) {
				long active = (Long) map.get(OBIS.IMPORT_ACTIVE_ENERGY.name());
				BigDecimal bd = new BigDecimal(active);
				//meteringValue = bd.doubleValue() / this.activePulseConstant;
				meteringValue = bd.doubleValue() / scaleUnit;
				meteringValue = meteringValue * getSt();
			} else {
				// 현월 누적 사용량을 가져오지 않는 경우 LP의 마지막 누적 사용량을 가져온다.
				if (lpData != null && lpData.length > 0)
					meteringValue = lpData[lpData.length - 1].getCh()[1];
				else
					meteringValue = 0.0;
			}
			log.debug("METERING_VALUE[" + meteringValue + "]");
		} catch (Exception e) {
			log.error("ERROR - ", e);
		}
	}

	/**
	 * 미터아이디 생성 ~!! mfMeterid 앞2 + - + 뒤2 + customerMeterId7 = totoal 12byte
	 * 
	 * @return
	 */
	public String getCreateMeterId() {
		String mfMeterId = (String) (result.get(OBIS.MANUFACTURER_METER_ID.getCode())).get(OBIS.MANUFACTURER_METER_ID.name());
		String customerMeterId = (String) (result.get(OBIS.CUSTOMER_METER_ID.getCode())).get(OBIS.CUSTOMER_METER_ID.name());

		String mdsid = mfMeterId.substring(0, 2) + "-" + mfMeterId.substring(mfMeterId.length() - 2, mfMeterId.length()) + customerMeterId;

		log.info("MDSID = {}, manufacture meter id = {}, customer meter id = {}", new Object[] { mdsid, mfMeterId, customerMeterId });

		return mdsid;
	}

	/**
	 * System.out.println() 으로 HEX 출력.
	 * 
	 * @param data
	 * @param tPos
	 * @param showLength
	 */
	public void printHexByteString(byte[] data, int tPos, int showLength) {
		int loggingLenth = (data.length - tPos) < showLength ? data.length - tPos : showLength;

		byte[] logging = new byte[showLength];
		System.arraycopy(data, tPos, logging, 0, loggingLenth);
		log.info("### SHOW HEX POS[" + tPos + "] 부터 " + loggingLenth + "byte ==> " + Hex.getHexDump(logging));
	}

}
