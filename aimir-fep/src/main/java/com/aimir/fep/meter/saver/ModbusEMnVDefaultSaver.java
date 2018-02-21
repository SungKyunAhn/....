/**
 * (@)# ModbusEMnVLSSaver.java
 *
 * 2015. 6. 10.
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
package com.aimir.fep.meter.saver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.MeterDataParser;
import com.aimir.fep.meter.parser.ModbusEMnVDefault;
import com.aimir.fep.meter.parser.ModbusEMnVHyundai;
import com.aimir.fep.meter.parser.ModbusEMnVLS;
import com.aimir.fep.meter.parser.ModbusEMnVRockwell;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.util.DateTimeUtil;

/**
 * @author simhanger
 *
 */
@Service
public class ModbusEMnVDefaultSaver extends AbstractMDSaver {
	private static Logger log = LoggerFactory.getLogger(ModbusEMnVDefaultSaver.class);
	private String meterId;

	@Autowired
	LpEMDao lpEMDao;

	@Autowired
	MonthEMDao monthEMDao;

	@SuppressWarnings("unchecked")
	@Override
	public boolean save(IMeasurementData md) throws Exception {
		try {
			MeterDataParser parser = null;
			LPData[] lplist = null;

			if (md.getMeterDataParser() instanceof ModbusEMnVDefault) {
				parser = (ModbusEMnVDefault) md.getMeterDataParser();
				lplist = ((ModbusEMnVDefault) parser).getLPData();
				meterId = ((ModbusEMnVDefault) parser).getMeterID();
			} else if (md.getMeterDataParser() instanceof ModbusEMnVLS) {
				parser = (ModbusEMnVLS) md.getMeterDataParser();
				lplist = ((ModbusEMnVLS) parser).getLPData();
				meterId = ((ModbusEMnVLS) parser).getMeterID();
			} else if (md.getMeterDataParser() instanceof ModbusEMnVHyundai) {
				parser = (ModbusEMnVHyundai) md.getMeterDataParser();
				lplist = ((ModbusEMnVHyundai) parser).getLPData();
				meterId = ((ModbusEMnVHyundai) parser).getMeterID();
			} else if (md.getMeterDataParser() instanceof ModbusEMnVRockwell) {
				parser = (ModbusEMnVRockwell) md.getMeterDataParser();
				lplist = ((ModbusEMnVRockwell) parser).getLPData();
				meterId = ((ModbusEMnVRockwell) parser).getMeterID();
			}

			if (lplist == null || lplist.length < 1) {
				log.debug("LPSIZE => 0");
			} else {
				int startLpListParam = 0; // 시작 lp. Modebus는 0부터 시작

				log.info("lplist[0]:" + lplist[startLpListParam]);
				log.info("lplist[0].getDatetime():" + lplist[startLpListParam].getDatetime());

				String startlpdate = lplist[startLpListParam].getDatetime();
				startlpdate = startlpdate.length() == 12 ? startlpdate + "00" : startlpdate;

				String lpdatetime = startlpdate;
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				cal.setTime(sdf.parse(startlpdate));
				List<Double>[] chValue = new ArrayList[lplist[startLpListParam].getCh().length];
				List<Integer> flag = new ArrayList<Integer>();
				double baseValue = lplist[startLpListParam].getCh()[0];

				for (int i = startLpListParam; i < lplist.length; i++) {
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
						if (chValue[ch] == null)
							chValue[ch] = new ArrayList<Double>();

						if (ch + 1 <= lplist[i].getCh().length)
							chValue[ch].add(lplist[i].getCh()[ch]);
						else
							chValue[ch].add(0.0);
					}
					cal.add(Calendar.MINUTE, parser.getMeter().getLpInterval());
					lpdatetime = sdf.format(cal.getTime());
				}

				saveLPData(chValue, flag, startlpdate, baseValue, parser);
				log.debug("### 1 saveLPData 완료 ####");

				try {
					Meter meter = parser.getMeter();
					String dsttime = DateTimeUtil.getDST(null, md.getTimeStamp());
					if (meter.getLastReadDate() == null || dsttime.compareTo(meter.getLastReadDate()) > 0) {
						meter.setLastReadDate(dsttime);
						String notTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim();

						log.debug("##### [INVERTER] 임시 통신시간 저장 체크 [미터={}] [dsttime-{} / 현재시간-{}", new Object[] { meter.getMdsId(), dsttime, notTime });
						log.debug("##### [INVERTER] 임시 통신시간 저장 체크 [미터={}] [dsttime-{} / 현재시간-{}", new Object[] { meter.getMdsId(), dsttime, notTime });
						log.debug("##### [INVERTER] 임시 통신시간 저장 체크 [미터={}] [dsttime-{} / 현재시간-{}", new Object[] { meter.getMdsId(), dsttime, notTime });

						Code meterStatus = CommonConstants.getMeterStatusByName(MeterStatus.Normal.name());
						log.debug("METER_STATUS[" + (meterStatus == null ? "NULL" : meterStatus.getName()) + "]");
						meter.setMeterStatus(meterStatus);
						meter.setLastMeteringValue(parser.getMeteringValue());

						//String meterTime = parser.getMeterTime();
						//meterTime = meterTime.length() != 14 ? meterTime + "00" : meterTime;
						if (md.getTimeStamp() != null && !"".equals(md.getTimeStamp())) {

							long diff = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(notTime).getTime() - DateTimeUtil.getDateFromYYYYMMDDHHMMSS(md.getTimeStamp()).getTime();
							meter.setTimeDiff(Long.parseLong(String.valueOf(Math.round(diff / 1000 / 60))));

							if (0 < diff) {
								log.debug("### [{}] TIME_DIFF 발생!! timestamp={}, meterTime={}, diff={}, set_diff={}분", new Object[] { meter.getMdsId(), md.getTimeStamp(), notTime, diff, meter.getTimeDiff() });
							}
						}

						// 수검침과 같이 모뎀과 관련이 없는 경우는 예외로 처리한다.
						if (meter.getModem() != null) {
							meter.getModem().setLastLinkTime(dsttime);

							String tt = meter.getModem().getDeviceSerial();
							log.debug("##### [INVERTER]모뎀 마지막 연결시간 저장 체크 [모뎀={}] [dsttime-{} / 현재시간-{}", new Object[] { tt, dsttime, DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim() });
							log.debug("##### [INVERTER]모뎀 마지막 연결시간 저장 체크 [모뎀={}] [dsttime-{} / 현재시간-{}", new Object[] { tt, dsttime, DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim() });
							log.debug("##### [INVERTER]모뎀 마지막 연결시간 저장 체크 [모뎀={}] [dsttime-{} / 현재시간-{}", new Object[] { tt, dsttime, DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim() });
						}
					}
				} catch (Exception ignore) {
				}
			}
			log.info("{} Metering END......!!!!", parser.getMDevId());
		} catch (Exception e) {
			log.error("Error - ", e);
			throw e;
		}
		return true;
	}

	//public void saveLPData(List<Double>[] chValue, List<Integer> flag, String startlpdate, double baseValue, ModbusEMnVLS parser)
	public void saveLPData(List<Double>[] chValue, List<Integer> flag, String startlpdate, double baseValue, MeterDataParser parser) throws Exception {
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
		//        super.saveLPData(MeteringType.Normal, startlpdate.substring(0, 8), startlpdate.substring(8)+"00",
		//                _lplist, _flag, baseValue, parser.getMeter(),
		//                DeviceType.MCU, parser.getMeter().getMcu().getSysID(),
		//                DeviceType.Meter, parser.getMeterID());

		//super.saveLPData(MeteringType.Normal, startlpdate.substring(0, 8), startlpdate.substring(8)+"00",
		//super.saveLPData(MeteringType.Normal, startlpdate.substring(0, 8), startlpdate.substring(8, 12), _lplist, _flag, baseValue, parser.getMeter(), DeviceType.Modem, parser.getMeter().getModem().getDeviceSerial(), DeviceType.Meter, meterId);
		super.saveLPData(MeteringType.OnDemand, startlpdate.substring(0, 8), startlpdate.substring(8, 12), _lplist, _flag, baseValue, parser.getMeter(), DeviceType.Modem, parser.getMeter().getModem().getDeviceSerial(), DeviceType.Meter, meterId);
	}

}
