package com.aimir.fep.meter.saver;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.dao.device.MMIUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.DLMSGtypeOmni;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.util.DateTimeUtil;

@Service
public class DLMSGtypeOmniMDSaver extends AbstractMDSaver {

	private static Log log = LogFactory.getLog(DLMSGtypeOmniMDSaver.class);

	@Autowired
	MeterDao meterDao;

	@Autowired
	MMIUDao mmiuDao;
	
	@Override
	protected boolean save(IMeasurementData md) throws Exception {

		DLMSGtypeOmni parser = (DLMSGtypeOmni) md.getMeterDataParser();

		// Save meter information
		saveMeterInfomation(parser);

		// LP
		saveLpUsingLPTime(md, parser);

		// 전력량 (realtimeBillingEM) 현재검침
		saveCurrentBillingMonthData(parser);

		// 전력량 (billingMonth) 정기검침
		saveBillingMonthData(parser);

		// 최대수요전력 (billingDaily)
		saveBillingDailyData(parser);

		// 이벤트 (정전/복전)
		saveEventLog(parser);

		// PQ : 순시전압/전류
		savePowerQuality(parser);

		return true;
	}
	
	/**
	 * saveMeterInfomation
	 * 
	 * @param parser
	 */
	public void saveMeterInfomation(DLMSGtypeOmni parser) throws Exception {

		String fwVer = parser.getFwVersion();
		String meterModel = parser.getMeterModel();
		Integer lpInterval = parser.getLpInterval();

		EnergyMeter energyMeter = null;
		switch (MeterType.valueOf(parser.getMeter().getMeterType().getName())) {
		case EnergyMeter:
			energyMeter = (EnergyMeter) parser.getMeter();
			break;
		}

		if (energyMeter == null) {
			log.debug("MDevId[" + parser.getMDevId() + "] MeterDataParser is null.");
			return;
		}

		boolean updateflg = false;

		if (fwVer.length() != 0) {
			if (energyMeter.getSwVersion() == null
					|| (energyMeter.getSwVersion() != null && !energyMeter.getSwVersion().equals(fwVer))) {
				energyMeter.setSwVersion(fwVer);
				log.debug("MDevId[" + parser.getMDevId() + "] set Swversion[" + fwVer + "]");
				updateflg = true;
			}
		}

		// Meter Model
		if (meterModel != null && meterModel.length() != 0 && !energyMeter.getModel().getName().equals(meterModel)) {
			List<DeviceModel> list = deviceModelDao.getDeviceModelByName(energyMeter.getSupplierId(), meterModel);
			if (list != null && list.size() == 1) {
				energyMeter.setModel(list.get(0));
				log.debug("MDevId[" + parser.getMDevId() + "] set Model[" + meterModel + "]");
				updateflg = true;
			}
		}

		// LpInterval
		if ((energyMeter.getLpInterval() != null && lpInterval != null) && energyMeter.getLpInterval() != lpInterval) {
			energyMeter.setLpInterval(lpInterval);
			log.debug("MDevId[" + parser.getMDevId() + "] set setLpInterval[" + lpInterval + "]");
			updateflg = true;
		}

		if (updateflg) {
			EnergyMeter meter = (EnergyMeter) meterDao.get(parser.getMDevId());
			meter.setPulseConstant(parser.getActivePulseConstant());
			meter.setSwVersion(parser.getFwVersion()); // SW 버전
			meter.setPt(parser.getActivePulseConstant()); // 유효
			meter.setCt2(parser.getReactivePulseConstant()); // 무효
			meter.setVt2(parser.getApparentPulseConstant()); // 피상
			meter.setGs1(parser.getMesurementDay()); // 정기검침일
			meter.setLpInterval(lpInterval);
			meterDao.update(meter);
			log.debug("MDevId[" + parser.getMDevId() + "] update meter information");
		}
	}

	// INSERT START SP-501
	public void saveLpUsingLPTime(IMeasurementData md, DLMSGtypeOmni parser) throws Exception {
		try {
			LPData[] lplist = parser.getLPData();
			if (lplist == null || lplist.length == 0) {
				log.warn("LP size is 0!!");
				return;
			}
			log.debug("saveLpUsingLPTime Start Total LPSIZE => " + lplist.length);

			log.debug("active pulse constant:" + parser.getActivePulseConstant());
			log.debug(md.getTimeStamp().substring(0, 8));
			log.debug(md.getTimeStamp().substring(8, 14));
			log.debug(parser.getMeteringValue());
			log.debug(parser.getMeter().getMdsId());
			log.debug(parser.getDeviceType());
			log.debug(parser.getDeviceId());
			log.debug(parser.getMDevType());
			log.debug(parser.getMDevId());
			log.debug(parser.getMeterTime());
			log.debug(parser.getMeter().getLastReadDate());

			// Save meter information
			Double meteringValue = parser.getMeteringValue() == null ? 0d : parser.getMeteringValue();
			Meter meter = parser.getMeter();

			String ns = meter.getModem().getNameSpace();
			String mdTimestamp = md.getTimeStamp();
			log.debug("ns : " + ns);

			String dsttime = DateTimeUtil.getDST(null, mdTimestamp);
			String meterTime = parser.getMeterTime();
			log.debug("MDevId[" + parser.getMDevId() + "] DSTTime[" + dsttime + "]");

			if (meterTime != null && !"".equals(meterTime)) {
				meter.setLastReadDate(meterTime);
				meter.getModem().setLastLinkTime(meterTime);
				parser.getMeter().setLastReadDate(meterTime);
				parser.getMeter().getModem().setLastLinkTime(meterTime);
				log.debug("MDevId[" + parser.getMDevId() + "] MODEM LastLinkTime [" + meterTime + "]");
			} else {
				meter.setLastReadDate(dsttime);
				meter.getModem().setLastLinkTime(dsttime);
				parser.getMeter().setLastReadDate(dsttime);
				parser.getMeter().getModem().setLastLinkTime(dsttime);
				log.debug("MDevId[" + parser.getMDevId() + "] MODEM LastLinkTime [" + dsttime + "]");
			}

			meter.setLastMeteringValue(meteringValue);

			log.debug("MDevId[" + parser.getMDevId() + "] DSTTime[" + dsttime + "] LASTREADDate["
					+ meter.getLastReadDate() + "]");
			Code normalStatus = CommonConstants.getMeterStatusByName(MeterStatus.Normal.name());
			log.debug("MDevId[" + parser.getMDevId() + "] METER_STATUS["
					+ (meter.getMeterStatus() == null ? "NULL" : meter.getMeterStatus()) + "]");
			if (meter.getMeterStatus() == null
					|| (meter.getMeterStatus() != null && !meter.getMeterStatus().getName().equals("CutOff")
							&& !meter.getMeterStatus().getName().equals("Delete"))) {
				meter.setMeterStatus(normalStatus);
				log.debug("MDevId[" + parser.getMDevId() + "] METER_CHANGED_STATUS[" + meter.getMeterStatus() + "]");
			}
			if (meterTime != null && !"".equals(meterTime)) {
				try {

					long diff = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(mdTimestamp).getTime()
							- DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime();
					meter.setTimeDiff(diff / 1000);

					log.debug("MDevId[" + parser.getMDevId() + "] Update timeDiff. diff=[" + meter.getTimeDiff() + "]"); // INSERT
																															// SP-406
				} catch (ParseException e) {
					log.warn("MDevId[" + parser.getMDevId() + "] Check MeterTime[" + meterTime + "] and MeteringTime["
							+ mdTimestamp + "]");
				}
			}

			// Split in the correct interval
			ArrayList<ArrayList<LPData>> tmpList = new ArrayList<ArrayList<LPData>>();
			ArrayList<LPData> tmp = new ArrayList<LPData>();
			int count = 0;
			long currentTime = 0;
			long nextTime = 0;
			int diffMin = 0;
			while (count < lplist.length) {
				if (count + 1 >= lplist.length) {
					tmp.add(lplist[count]);
					tmpList.add(tmp);
					break;
				}

				currentTime = Util.getMilliTimes(lplist[count].getDatetime() + "00") / 1000;
				nextTime = Util.getMilliTimes(lplist[count + 1].getDatetime() + "00") / 1000;
				if (nextTime <= currentTime) {
					// LP time returns or duplicate
					log.debug("LP time returns or duplicate. current[" + lplist[count].getDatetime() + "] next["
							+ lplist[count + 1].getDatetime() + "]");
					tmp.add(lplist[count]);
					tmpList.add(tmp);
					tmp = null;
					tmp = new ArrayList<LPData>();
					count++;
					continue;
				}

				diffMin = (int) ((nextTime - currentTime) / 60 - parser.getLpInterval());
				if (diffMin == 0) {
					tmp.add(lplist[count]);
				} else {
					// different interval
					log.debug("LP time interval different. current[" + lplist[count].getDatetime() + "] next["
							+ lplist[count + 1].getDatetime() + "]");
					tmp.add(lplist[count]);
					tmpList.add(tmp);
					tmp = null;
					tmp = new ArrayList<LPData>();
				}

				count++;
			}
			
			saveMeteringData(MeteringType.Normal, md.getTimeStamp().substring(0, 8), md.getTimeStamp().substring(8, 14),
					parser.getMeteringValue(), parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
					parser.getMDevType(), parser.getMDevId(), parser.getMeterTime());

			for (ArrayList<LPData> lp : tmpList) {
				lplist = null;
				lplist = (LPData[]) lp.toArray(new LPData[0]);
				for (int i = 0; i < lplist.length; i++) {
					log.debug("MDevId[" + parser.getMDevId() + "]  time=" + lplist[i].getDatetime() + ":lp="
							+ lplist[i].getLp() + ":lpValue=" + lplist[i].getLpValue());
				}

				if (lplist == null || lplist.length == 0) {
					log.debug("MDevId[" + parser.getMDevId() + "]  LPSIZE => 0");
				} else {
					log.debug("##########MDevId[" + parser.getMDevId() + "] LPSIZE => " + lplist.length);
					lpSaveUsingLPTime(md, lplist, parser);
				}
			}
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			log.debug("MDevId[" + parser.getMDevId() + "] saveLpGtypeOmni finish");
		}
	}

	private boolean lpSaveUsingLPTime(IMeasurementData md, LPData[] validlplist, DLMSGtypeOmni parser)
			throws Exception {

		log.info("#########save mdevId:" + parser.getMDevId());
		double[][] lpValues = new double[validlplist[0].getCh().length][validlplist.length];
		int[] flaglist = new int[validlplist.length];
		double[] pflist = new double[validlplist.length];
		String[] timelist = new String[validlplist.length];
		String[] statuslist = new String[validlplist.length];

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
			statuslist[i] = validlplist[i].getStatus();
		}

		double[] _baseValue = new double[lpValues.length];
		_baseValue[0] = validlplist[0].getLpValue();
		;
		for (int i = 1; i < lpValues.length; i++) {
			_baseValue[i] = 0;
		}

		parser.getMeter().setLpInterval(parser.getLpInterval());
		
		// TODO Flag, PF 처리해야 함.
		saveLPDataUsingLPTime(MeteringType.Normal, timelist, lpValues, flaglist, _baseValue, parser.getMeter(),
				parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());

		return true;
	}
	
	
	
	/**
	 * 전력량 saveCurrentBillingMonthData
	 * 
	 * @param parser
	 * @throws Exception
	 */
	public void saveCurrentBillingMonthData(DLMSGtypeOmni parser) throws Exception {
		try {
			BillingData dailyProfile = parser.getCurrentBillingMonthData();
			if (dailyProfile == null) {
				return;
			}
			saveCurrentBilling(dailyProfile, parser.getMeter(), null, null, parser.getMDevType(), parser.getMDevId());
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	/**
	 * 전력량 saveBillingMonthData
	 * 
	 * @param parser
	 * @throws Exception
	 */
	public void saveBillingMonthData(DLMSGtypeOmni parser) throws Exception {
		try {
			BillingData monthProfile = parser.getBillingMonthData();
			if (monthProfile == null) {
				return;
			}
			saveMonthlyBilling(monthProfile, parser.getMeter(), null, null, parser.getMDevType(), parser.getMDevId());
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	/**
	 * 순방향 최대수요전력 saveBillingDailyData
	 * 
	 * @param parser
	 * @throws Exception
	 */
	public void saveBillingDailyData(DLMSGtypeOmni parser) throws Exception {

		try {
			BillingData dailyProfile = parser.getBillingDayData();
			if (dailyProfile == null) {
				return;
			}
			saveDailyBilling(dailyProfile, parser.getMeter(), null, null, parser.getMDevType(), parser.getMDevId());
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	/**
	 * 정전 / 복전 saveEventLog
	 * 
	 * @param parser
	 * @throws Exception
	 */
	public void saveEventLog(DLMSGtypeOmni parser) throws Exception {

		try {
			Meter meter = parser.getMeter();

			List<EventLogData> events = parser.getEventLog();
			saveMeterEventLog(parser.getMeter(), events.toArray(new EventLogData[0]));

		} catch (Exception e) {
			log.warn(e);
		}
	}

	/**
	 * 순시/평균 전압전류 savePowerQuality
	 * 
	 * @param parser
	 * @throws Exception
	 */
	public void savePowerQuality(DLMSGtypeOmni parser) throws Exception {

		try {
			Instrument[] instrument = parser.getPowerQualityData();

			log.debug("instrument size : " + instrument.length);

			if (instrument.length > 0) {
				savePowerQuality(parser.getMeter(), parser.getMeteringTime(), instrument, parser.getDeviceType(),
						parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
			}
		} catch (Exception e) {
			log.error(e, e);
		}

	}

	public void saveMeterTimeSyncLog(DLMSGtypeOmni parser, Meter meter) throws Exception {

		String after = parser.getAfterTime();
		String before = parser.getBeforeTime();
		long timeDiff = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(after).getTime()
				- DateTimeUtil.getDateFromYYYYMMDDHHMMSS(before).getTime();

		log.debug("meter setLastTimesyncDate update");
		meter.setLastTimesyncDate(after);
		meter.setTimeDiff(timeDiff);
		meterDao.update(meter);

		log.debug("saveMeterTimeSyncLog");
		super.saveMeterTimeSyncLog(meter, before, after, 1);
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

	/**
	 * Hex ObisCode => x.x.x.x.x.x
	 * 
	 * @param obisCode
	 * @return
	 */
	private String convertObis(String obisCode) {
		String returnData = "";
		if (obisCode.length() == 12) {
			byte[] obisCodeArr = Hex.encode(obisCode);
			obisCode = "";
			for (int i = 0; i < obisCodeArr.length; i++) {
				if (i == 0) {
					obisCode += DataUtil.getIntToByte(obisCodeArr[i]);
				} else {
					obisCode += "." + DataUtil.getIntToByte(obisCodeArr[i]);
				}
			}
			returnData = obisCode;
		} else {
			returnData = "Wrong Obis";
		}

		return returnData;
	}
}