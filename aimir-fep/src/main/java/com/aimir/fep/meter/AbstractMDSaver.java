package com.aimir.fep.meter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ChannelCalcMethod;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.ElectricityChannel;
import com.aimir.constants.CommonConstants.IntegratedFlag;
import com.aimir.constants.CommonConstants.MeterEventKind;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.MeterVendor;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.PowerEventStatus;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.MeterEventDao;
import com.aimir.dao.device.MeterEventLogDao;
import com.aimir.dao.device.MeterTimeSyncLogDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.ModemPowerLogDao;
import com.aimir.dao.device.PowerAlarmLogDao;
import com.aimir.dao.device.SNRLogDao;
import com.aimir.dao.mvm.BillingDayEMDao;
import com.aimir.dao.mvm.BillingMonthEMDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.DayGMDao;
import com.aimir.dao.mvm.DayHMDao;
import com.aimir.dao.mvm.DayHUMDao;
import com.aimir.dao.mvm.DaySPMDao;
import com.aimir.dao.mvm.DayTMDao;
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.mvm.LpGMDao;
import com.aimir.dao.mvm.LpHMDao;
import com.aimir.dao.mvm.LpSPMDao;
import com.aimir.dao.mvm.LpTMDao;
import com.aimir.dao.mvm.LpWMDao;
import com.aimir.dao.mvm.MeteringDataDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.MonthGMDao;
import com.aimir.dao.mvm.MonthHMDao;
import com.aimir.dao.mvm.MonthHUMDao;
import com.aimir.dao.mvm.MonthSPMDao;
import com.aimir.dao.mvm.MonthTMDao;
import com.aimir.dao.mvm.MonthWMDao;
import com.aimir.dao.mvm.PowerQualityDao;
import com.aimir.dao.mvm.PowerQualityStatusDao;
import com.aimir.dao.mvm.RealTimeBillingEMDao;
import com.aimir.dao.system.Co2FormulaDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.EnvData;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.meter.data.MeterTimeSyncData;
import com.aimir.fep.meter.data.PowerAlarmLogData;
import com.aimir.fep.meter.data.PowerQualityMonitor;
import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.link.MeterEventLink;
import com.aimir.fep.meter.parser.BatteryLog;
import com.aimir.fep.meter.parser.MeterDataParser;
import com.aimir.fep.meter.parser.ZEUPLS_Status;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Util;
import com.aimir.model.device.Meter;
import com.aimir.model.device.MeterEvent;
import com.aimir.model.device.MeterEventLog;
import com.aimir.model.device.MeterTimeSyncLog;
import com.aimir.model.device.Modem;
import com.aimir.model.device.ModemPowerLog;
import com.aimir.model.device.PowerAlarmLog;
import com.aimir.model.device.SNRLog;
import com.aimir.model.device.ZEUPLS;
import com.aimir.model.mvm.BillingDayEM;
import com.aimir.model.mvm.BillingMonthEM;
import com.aimir.model.mvm.ChannelConfig;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.mvm.DayGM;
import com.aimir.model.mvm.DayHM;
import com.aimir.model.mvm.DayPk;
import com.aimir.model.mvm.DaySPM;
import com.aimir.model.mvm.DayWM;
import com.aimir.model.mvm.LpEM;
import com.aimir.model.mvm.LpGM;
import com.aimir.model.mvm.LpHM;
import com.aimir.model.mvm.LpPk;
import com.aimir.model.mvm.LpSPM;
import com.aimir.model.mvm.LpTM;
import com.aimir.model.mvm.LpVC;
import com.aimir.model.mvm.LpWM;
import com.aimir.model.mvm.MeteringData;
import com.aimir.model.mvm.MeteringDataEM;
import com.aimir.model.mvm.MeteringDataGM;
import com.aimir.model.mvm.MeteringDataHM;
import com.aimir.model.mvm.MeteringDataSPM;
import com.aimir.model.mvm.MeteringDataWM;
import com.aimir.model.mvm.MeteringDay;
import com.aimir.model.mvm.MeteringLP;
import com.aimir.model.mvm.MeteringMonth;
import com.aimir.model.mvm.MonthEM;
import com.aimir.model.mvm.MonthGM;
import com.aimir.model.mvm.MonthHM;
import com.aimir.model.mvm.MonthSPM;
import com.aimir.model.mvm.MonthWM;
import com.aimir.model.mvm.PowerQuality;
import com.aimir.model.mvm.PowerQualityStatus;
import com.aimir.model.mvm.RealTimeBillingEM;
import com.aimir.model.system.Co2Formula;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Location;
import com.aimir.model.system.MeterConfig;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.Condition.Restriction;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * 검침데이타 저장
 *
 * @author 박종성 elevas@nuritelecom.com
 */
@Service
public abstract class AbstractMDSaver
{
    protected static Log log = LogFactory.getLog(AbstractMDSaver.class);

    @Resource(name="transactionManager")
    protected JpaTransactionManager txmanager;
    
    @Autowired
    protected MeterDao meterDao;

    @Autowired
    protected ModemDao modemDao;

    @Autowired
    protected LpEMDao lpEMDao;

    @Autowired
    protected LpGMDao lpGMDao;

    @Autowired
    protected LpWMDao lpWMDao;

    @Autowired
    protected LpHMDao lpHMDao;
    
    @Autowired
    protected LpSPMDao lpSPMDao;
    
    @Autowired
    protected LpTMDao lpTMDao;
    
    @Autowired
    protected DayEMDao dayEMDao;

    @Autowired
    protected DayGMDao dayGMDao;

    @Autowired
    protected DayWMDao dayWMDao;

    @Autowired
    protected DayHUMDao dayHUMDao;

    @Autowired
    protected DayTMDao dayTMDao;

    @Autowired
    protected DayHMDao dayHMDao;
    
    @Autowired
    protected DaySPMDao daySPMDao;

    @Autowired
    protected MonthEMDao monthEMDao;

    @Autowired
    protected MonthGMDao monthGMDao;

    @Autowired
    protected MonthWMDao monthWMDao;

    @Autowired
    protected MonthHMDao monthHMDao;

    @Autowired
    protected MonthHUMDao monthHUMDao;

    @Autowired
    protected MonthTMDao monthTMDao;
    
    @Autowired
    protected MonthSPMDao monthSPMDao;

    @Autowired
    protected MeteringDataDao meteringDataDao;

    @Autowired
    protected PowerQualityDao powerQualityDao;
    
    @Autowired
    protected PowerQualityStatusDao powerQualityStatusDao;

    @Autowired
    protected Co2FormulaDao co2FormulaDao;

    @Autowired
    protected ModemPowerLogDao modemPowerLogDao;

    @Autowired
    protected BillingDayEMDao billingDayEMDao;

    @Autowired
    protected BillingMonthEMDao billingMonthEMDao;

    @Autowired
    protected RealTimeBillingEMDao realTimeBillingEMDao;

    @Autowired
    protected MeterTimeSyncLogDao meterTimeSyncLogDao;

    @Autowired
    protected MeterEventDao meterEventDao;

    @Autowired
    protected MeterEventLogDao meterEventLogDao;

    //@Autowired
    protected MeterEventLink meterEventLink;

    @Autowired
    protected PowerAlarmLogDao powerAlarmLogDao;

    @Autowired
    protected CodeDao codeDao;
    
    @Autowired
    protected ContractDao contractDao;
    
    @Autowired
    protected SNRLogDao snrLogDao;
    
    @Autowired
    protected DeviceModelDao deviceModelDao;    
    
    final static DecimalFormat dformat = new DecimalFormat("#0.000000");
    
    private boolean lpSaveOnly = 
            Boolean.parseBoolean(FMPProperty.getProperty("lp.save.only"));
    
    private static Map<String, String> locMap;
    
    private MeterDataParser parser;
    
    /**
     * 파서종류별로 MeterDataSaver 추상클래스를 상속받아 데이타 저장을 구현한다.
     * @param md 파싱된 검침프레임
     * @return 성공여부
     * @throws Exception
     */
    protected abstract boolean save(IMeasurementData md) throws Exception;

    protected void setParser(MeterDataParser parser) {
    	this.parser = parser;
    }
    
    protected void saveMeterEventLog(Meter meter, EventLogData[] eventLogDatas) throws Exception
    {
        log.debug("save MeterEventLogData["+meter.getMdsId()+"]");

        try {
            List<MeterEvent> list = null;
            LinkedHashSet<Condition> condition = null;
            MeterEventLog meterEventLog = null;
            String openTime = null;
            List<MeterEventLog> meterEventLogs = null;
            for(int i = 0; eventLogDatas != null && i < eventLogDatas.length; i++){
                if (eventLogDatas[i] == null)
                    continue;
                
                log.debug("eventLogData="+eventLogDatas[i].toString());
                // 미터 이벤트를 찾아서 없으면 경고를 보내고 다음 이벤트 진행
                condition = new LinkedHashSet<Condition>();
    
                condition.add(new Condition("value", new Object[]{String.valueOf(eventLogDatas[i].getFlag())}, null, Restriction.EQ));
                condition.add(new Condition("kind", new Object[]{MeterEventKind.valueOf(eventLogDatas[i].getKind())}, null, Restriction.EQ));
                // condition.add(new Condition("name", new Object[]{eventLogDatas[i].getMsg()}, null, Restriction.EQ));
                condition.add(new Condition("model", new Object[]{meter.getModel().getName()}, null, Restriction.EQ));
    
                list = meterEventDao.findByConditions(condition);
    
                if (list == null || list.size() < 1) {
                    log.debug("################## 등록되지 않은 이벤트 발생~!! #################");
                    log.debug("################## 등록되지 않은 이벤트 발생~!! #################");
                    log.debug("################## 등록되지 않은 이벤트 발생~!! #################");
                    log.debug("################## 등록되지 않은 이벤트 발생~!! #################");
                    log.debug("################## 등록되지 않은 이벤트 발생~!! #################");
                    log.debug("################## 등록되지 않은 이벤트 발생~!! #################");
                    
                    log.warn("No maching meter event class!! eventcode=["+eventLogDatas[i].getFlag()+"]");
                    log.warn("value["+eventLogDatas[i].getFlag()+"] kind["+eventLogDatas[i].getKind()+"] model["+meter.getModel().getName()+"]");
                    continue;
                }
                MeterEvent meterEvent = list.get(0);
    
                // 미터 이벤트 로그를 찾는다.
                // 로그가 있으면 다음 이벤트 진행
                openTime = DateTimeUtil.getDST(null, eventLogDatas[i].getDate()+eventLogDatas[i].getTime());
                condition = new LinkedHashSet<Condition>();
                condition.add(new Condition("id.openTime", new Object[]{openTime}, null, Restriction.EQ));
                condition.add(new Condition("id.activatorId", new Object[]{meter.getMdsId()}, null, Restriction.EQ));
                condition.add(new Condition("id.meterEventId", new Object[]{meterEvent.getId()}, null, Restriction.EQ));
                // condition.add(new Condition("yyyymmdd", new Object[] {openTime.substring(0, 8)}, null, Restriction.EQ));
    
                meterEventLogs = meterEventLogDao.findByConditions(condition);
    
                if (meterEventLogs != null && meterEventLogs.size() != 0)
                    continue;
    
                meterEventLog = new MeterEventLog();
    
                meterEventLog.setMeterEventId(meterEvent.getId());
                meterEventLog.setActivatorId(meter.getMdsId());
                meterEventLog.setActivatorType(meter.getMeterType().getName());
                meterEventLog.setMessage(eventLogDatas[i].getAppend());
                meterEventLog.setOpenTime(openTime);
                meterEventLog.setWriteTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                meterEventLog.setYyyymmdd(meterEventLog.getOpenTime().substring(0, 8));
                meterEventLog.setSupplier(meter.getSupplier());
    
                log.debug("meterEvent.getId() "+meterEvent.getId());
                log.debug("meter.getMdsId() "+meter.getMdsId());
                log.debug("meter.getMeterType().getName() "+meter.getMeterType().getName());
                log.debug("event OpenDate "+ eventLogDatas[i].getDate() + eventLogDatas[i].getTime());
                log.debug("eventLogDatas[i].getAppend() "+eventLogDatas[i].getAppend());
                log.debug("meterEvent.getId() "+meterEvent.getId());
    
                meterEventLogDao.add(meterEventLog);
                // 연동
                //meterEventLink.execute(meterEventLog);
            }
        }
        catch (Exception e) {
            log.warn(e, e);
        }
    }

    protected void savePowerAlarmLog(Meter meter, PowerAlarmLogData[] powerAlarmLogDatas) throws Exception {
        log.debug("save PowerAlarmLogData["+meter.getMdsId()+"]");

        List<MeterEvent> list              = null;
        LinkedHashSet<Condition> condition       = null;
        PowerAlarmLog powerAlarmLog        = null;
        List<PowerAlarmLog> powerAlarmLogs = null;

        

        for (int i = 0; powerAlarmLogDatas != null && i < powerAlarmLogDatas.length; i++) {
            
            String openTime  = null;
            String closeTime = null;
            
            // 미터 이벤트를 찾아서 없으면 경고를 보내고 다음 이벤트 진행
            condition = new LinkedHashSet<Condition>();
            log.debug("PowerAlarmLog   bbb b");
            log.debug("PowerAlarmLog   powerAlarmLogDatas[i].getFlag() "+powerAlarmLogDatas[i].getFlag());
            log.debug("PowerAlarmLog   powerAlarmLogDatas[i].getKind() "+powerAlarmLogDatas[i].getKind());
            log.debug("PowerAlarmLog   powerAlarmLogDatas[i].getMsg() "+powerAlarmLogDatas[i].getMsg());
            log.debug("PowerAlarmLog   meter.getModel().getName() "+meter.getModel().getName());

            condition.add(new Condition("value", new Object[]{String.valueOf(powerAlarmLogDatas[i].getFlag())}, null, Restriction.EQ));
            condition.add(new Condition("kind", new Object[]{MeterEventKind.valueOf(powerAlarmLogDatas[i].getKind())}, null, Restriction.EQ));
            condition.add(new Condition("name", new Object[]{powerAlarmLogDatas[i].getMsg()}, null, Restriction.EQ));
            condition.add(new Condition("model", new Object[]{meter.getModel().getName()}, null, Restriction.EQ));

            list = meterEventDao.findByConditions(condition);

            if (list == null || list.size() < 1) {
                log.warn("No maching meter event class!! eventcode=["+powerAlarmLogDatas[i].getFlag()+"]");
                log.warn("value["+powerAlarmLogDatas[i].getFlag()+"] kind["+powerAlarmLogDatas[i].getKind()+"] name["+powerAlarmLogDatas[i].getMsg()+"] model["+meter.getModel().getName()+"]");
                continue;
            }
            MeterEvent meterEvent = list.get(0);
            log.debug("PowerAlarmLog   meterEvent Id= "+meterEvent.getId());
            log.debug("PowerAlarmLog   meterEvent getModel= "+meterEvent.getModel());
            log.debug("PowerAlarmLog   meterEvent getName= "+meterEvent.getName());

            log.debug("PowerAlarmLog  getDate = "+ powerAlarmLogDatas[i].getDate() );
            log.debug("PowerAlarmLog  getTime = "+ powerAlarmLogDatas[i].getTime() );
            openTime = DateTimeUtil.getDST(null, powerAlarmLogDatas[i].getDate()+powerAlarmLogDatas[i].getTime());

            if (powerAlarmLogDatas[i].getCloseDate() != null && powerAlarmLogDatas[i].getCloseTime() != null 
                    && powerAlarmLogDatas[i].getCloseDate().length() != 0 && powerAlarmLogDatas[i].getCloseTime().length() != 0 ) {
                closeTime = DateTimeUtil.getDST(null, powerAlarmLogDatas[i].getCloseDate()+powerAlarmLogDatas[i].getCloseTime());
            }

            // 이벤트 로그를 찾는다.
            // 로그가 있으면 다음 이벤트 진행
            condition = new LinkedHashSet<Condition>();
            
            log.debug("PowerAlarmLog  meter.getMdsId() = "+ meter.getMdsId() );
            log.debug("PowerAlarmLog  meter.getId() = "+ meter.getId() );
            condition.add(new Condition("meter.id", new Object[]{ meter.getId() }, null, Restriction.EQ));
            log.debug("PowerAlarmLog  openTime = "+ openTime );
            condition.add(new Condition("openTime", new Object[]{openTime}, null, Restriction.EQ));            
            log.debug("PowerAlarmLog  Message = " + powerAlarmLogDatas[i].getMsg());
            condition.add(new Condition("message", new Object[]{powerAlarmLogDatas[i].getMsg()}, null, Restriction.EQ));
            log.debug("PowerAlarmLog  condition ok");
            powerAlarmLogs = powerAlarmLogDao.findByConditions(condition);            

            if (powerAlarmLogs != null && powerAlarmLogs.size() != 0) {
                continue;
            } else {
                powerAlarmLog = new PowerAlarmLog();

                log.debug("setWriteTime "+DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                log.debug("powerAlarmLogDatas[i].getMsg() "+powerAlarmLogDatas[i].getMsg());
                powerAlarmLog.setMeter(meter);
                powerAlarmLog.setSupplier(meter.getSupplier());
                powerAlarmLog.setOpenTime(openTime);
                powerAlarmLog.setWriteTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                powerAlarmLog.setMessage(powerAlarmLogDatas[i].getMsg());

                if (closeTime != null) {
                    powerAlarmLog.setCloseTime(closeTime);
                    powerAlarmLog.setStatus(PowerEventStatus.Closed.name());
                    powerAlarmLog.setDuration(Util.getDurationSec(openTime, closeTime));

//                  long duration = powerAlarmLog.getDuration();
                    long duration = Util.getDuration(openTime, closeTime);

                    condition = new LinkedHashSet<Condition>();
                    if (duration == 0) {
                        condition.add(new Condition("name", new Object[]{"0"}, null, Restriction.EQ));
                    } else if (duration > 0 && duration <= 5) {
                        condition.add(new Condition("name", new Object[]{"5"}, null, Restriction.EQ));
                    } else if (duration > 15 && duration <= 30) {
                        condition.add(new Condition("name", new Object[]{"15"}, null, Restriction.EQ));
                    } else if (duration > 30 && duration <= 60) {
                        condition.add(new Condition("name", new Object[]{"30"}, null, Restriction.EQ));
                    } else if (duration > (1 * 60) && duration <= (6 * 60)) {
                        condition.add(new Condition("name", new Object[]{"1"}, null, Restriction.EQ));
                    } else if (duration > (6 * 60) && duration <= (12 * 60)) {
                        condition.add(new Condition("name", new Object[]{"6"}, null, Restriction.EQ));
                    } else if (duration > (12 * 60) && duration <= (24 * 60)) {
                        condition.add(new Condition("name", new Object[]{"12"}, null, Restriction.EQ));
                    } else if (duration > (24 * 60)) {
                        condition.add(new Condition("name", new Object[]{"24"}, null, Restriction.EQ));
                    }
                    
                    if(duration > 60*60){ //1hour over
                        condition.add(new Condition("code", new Object[]{"12.1.%"}, null, Restriction.LIKE));
                    }else {
                        condition.add(new Condition("code", new Object[]{"12.2.%"}, null, Restriction.LIKE));
                    }

                    List<Code> codes = codeDao.findByConditions(condition);
                    Code code = null;
                    if(codes.size() > 0){
                        code = codes.get(0);
                    }

                    powerAlarmLog.setTypeCode(code);

                } else {
                    condition = new LinkedHashSet<Condition>();
                    condition.add(new Condition("name", new Object[]{"0"}, null, Restriction.EQ));
                    List<Code> codes = codeDao.findByConditions(condition);
                    Code code = null;
                    if(codes.size() > 0){
                        code = codes.get(0);
                    }
                    powerAlarmLog.setTypeCode(code);
                    
                    powerAlarmLog.setStatus(PowerEventStatus.Open.name());
                }

                log.debug("powerAlarmLogDatas[i].getLineType() "+powerAlarmLogDatas[i].getLineType());
                if (powerAlarmLogDatas[i].getLineType() != null ) {
//                  powerAlarmLog.setLineType(powerAlarmLogDatas[i].getLineType().getName());
                    if(!powerAlarmLogDatas[i].getLineType().equals("")){
                        powerAlarmLog.setLineType(powerAlarmLogDatas[i].getLineType().toString());
                    }
                }
                powerAlarmLog.setId(TimeUtil.getCurrentLongTime());
                powerAlarmLogDao.add(powerAlarmLog);
            }
        }
    }

    protected void saveBatteryStatus(ZEUPLS modem, ZEUPLS_Status zbStatus) throws Exception
    {
        //ZEUPLS_Status zbStatus = ((ZEUPLS)parser).getStatus();
        log.debug("ModemId["+modem.getDeviceSerial()+"] zbStatus["
                  +zbStatus.toString()+"]");

        modem.setBatteryVolt(zbStatus.getBatteryVolt());
        modem.setOperatingDay(zbStatus.getOperatingDay());
        //modem.setBatteryStatus(1);
        modem.setResetCount(zbStatus.getResetCount());
        modem.setResetReason(zbStatus.getResetReason());
        modem.setCommState(1);
        modem.setLQI(zbStatus.getLqi());
        modem.setActiveTime(zbStatus.getActiveTime());

    }

    protected void saveBatteryLog(Modem modem, BatteryLog[] batteryLogs) throws Exception
    {
        String yyyymmddhhmmss = null;
        for (int i = 0; batteryLogs!= null && i < batteryLogs.length; i++) {

            ModemPowerLog modemPowerLog = new ModemPowerLog();
            for (int j = 0 ; j < batteryLogs[i].getValues().length; j++) {
                // yyyymmddhhmmss = String.format("%s%02d0000", batteryLogs[i].getYyyymmdd(), batteryLogs[i].getHourCnt());
                yyyymmddhhmmss = String.format("%s%s0000", batteryLogs[i].getYyyymmdd(), batteryLogs[i].getValues()[j][0]);
                log.info("Modem[" + modem.getDeviceSerial() + "] YYYYMMDDHHMMSS[" + yyyymmddhhmmss + "]");
                yyyymmddhhmmss = DateTimeUtil.getDST(null, yyyymmddhhmmss);
                modemPowerLog.setYyyymmdd(yyyymmddhhmmss.substring(0, 8));
                modemPowerLog.setHhmmss(yyyymmddhhmmss.substring(8, 14));
                modemPowerLog.setSupplier(modem.getSupplier());

                modemPowerLog.setDeviceId(modem.getDeviceSerial());
                modemPowerLog.setDeviceType(modem.getModemType().name());
                modemPowerLog.setBatteryVolt((Double)batteryLogs[i].getValues()[j][1]);
                modemPowerLog.setVoltageCurrent((Double)batteryLogs[i].getValues()[j][2]);
                modemPowerLog.setVoltageOffset(((Double)batteryLogs[i].getValues()[j][3]).intValue());
                
                modemPowerLog.setSolarADV((Double)batteryLogs[i].getValues()[j][4]);
                modemPowerLog.setSolarCHGBV((Double)batteryLogs[i].getValues()[j][5]);
                modemPowerLog.setSolarBCDV((Double)batteryLogs[i].getValues()[j][6]);
                
                if(batteryLogs[i].getValues()[j][7] instanceof Integer)
                    modemPowerLog.setResetCount(new Long((Integer)batteryLogs[i].getValues()[j][7]));
                else {
                    modemPowerLog.setResetCount((Long)batteryLogs[i].getValues()[j][7]);
                }
                
            }

            modemPowerLogDao.saveOrUpdate(modemPowerLog);
        }

    }
    
    protected void savePowerQualityStatus (Meter meter, String time, PowerQualityMonitor pqm,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId) 
    throws Exception {
        
        PowerQualityStatus pqstatus = new PowerQualityStatus();
        String _time = DateTimeUtil.getDST(null, time+"00");
        pqstatus.setDeviceId(deviceId);
        pqstatus.setDeviceType(deviceType);
        pqstatus.setMDevId(mdevId);
        pqstatus.setMDevType(mdevType.name());
        pqstatus.setYyyymmdd(_time.substring(0, 8));
        pqstatus.setYyyymmddhhmmss(_time.substring(0, 14));
        pqstatus.setHhmmss(_time.substring(8, 14));
        pqstatus.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
        
        
        pqstatus.setDistortion_a_cnt(pqm.getDISTORTION_A_CNT());
        pqstatus.setDistortion_a_dur(pqm.getDISTORTION_A_DUR());
        pqstatus.setDistortion_b_cnt(pqm.getDISTORTION_B_CNT());
        pqstatus.setDistortion_b_dur(pqm.getDISTORTION_B_DUR());
        pqstatus.setDistortion_c_cnt(pqm.getDISTORTION_C_CNT());
        pqstatus.setDistortion_c_dur(pqm.getDISTORTION_C_DUR());
        
        pqstatus.setDistortion_cnt(pqm.getDISTORTION_CNT());
        pqstatus.setDistortion_dur(pqm.getDISTORTION_DUR());
        pqstatus.setDistortion_ing(pqm.getDISTORTION_STAT() == 0 ? false : true);
        
        pqstatus.setHarmonic_cnt(pqm.getHARMONIC_CNT());
        pqstatus.setHarmonic_dur(pqm.getHARMONIC_DUR());
        pqstatus.setHarmonic_ing(pqm.getHARMONIC_STAT() == 0 ? false : true);
        
        pqstatus.setHigh_frequency_cnt(pqm.getHIGH_FREQUENCY_CNT());
        pqstatus.setHigh_frequency_dur(pqm.getHIGH_FREQUENCY_DUR());
        pqstatus.setHigh_frequency_ing(pqm.getHIGH_FREQUENCY_STAT() == 0 ? false : true);
        
        pqstatus.setHigh_neutral_curr_cnt(pqm.getHIGH_NEUTRAL_CURR_CNT());
        pqstatus.setHigh_neutral_curr_dur(pqm.getHIGH_NEUTRAL_CURR_DUR());   
        pqstatus.setHigh_neutral_curr_ing(pqm.getHIGH_NEUTRAL_CURR_STAT() == 0 ? false : true);
        
        pqstatus.setHigh_vol_cnt(pqm.getHIGH_VOL_CNT());
        pqstatus.setHigh_vol_dur(pqm.getHIGH_VOL_DUR());
        pqstatus.setHigh_vol_ing(pqm.getHIGH_VOL_STAT() == 0 ? false : true);       
        
        pqstatus.setImbalance_curr_cnt(pqm.getIMBALANCE_CURR_CNT());
        pqstatus.setImbalance_curr_dur(pqm.getIMBALANCE_CURR_DUR());
        pqstatus.setImbalance_curr_ing(pqm.getIMBALANCE_CURR_STAT() == 0 ? false : true);
        
        pqstatus.setImbalance_vol_cnt(pqm.getIMBALANCE_VOL_CNT());
        pqstatus.setImbalance_vol_dur(pqm.getIMBALANCE_VOL_DUR());
        pqstatus.setImbalance_vol_ing(pqm.getIMBALANCE_VOL_STAT() == 0 ? false : true);
        
        pqstatus.setLow_curr_cnt(pqm.getLOW_CURR_CNT());
        pqstatus.setLow_curr_dur(pqm.getLOW_CURR_DUR());
        pqstatus.setLow_curr_ing(pqm.getLOW_CURR_STAT() == 0 ? false : true);
        
        pqstatus.setLow_vol_cnt(pqm.getLOW_VOL_CNT());
        pqstatus.setLow_vol_dur(pqm.getLOW_VOL_DUR());
        pqstatus.setLow_vol_ing(pqm.getLOW_VOL_STAT() == 0 ? false : true);     
        
        pqstatus.setOver_curr_cnt(pqm.getOVER_CURR_CNT());
        pqstatus.setOver_curr_dur(pqm.getOVER_CURR_DUR());
        pqstatus.setOver_curr_ing(pqm.getOVER_CURR_STAT() == 0 ? false : true);
        
        pqstatus.setPfactor_cnt(pqm.getPFACTOR_CNT());
        pqstatus.setPfactor_dur(pqm.getPFACTOR_DUR());
        pqstatus.setPfactor_ing(pqm.getPFACTOR_STAT() == 0 ? false : true);
        
        pqstatus.setPolarity_cross_phase_cnt(pqm.getPOLARITY_CROSS_PHASE_CNT());
        pqstatus.setPolarity_cross_phase_dur(pqm.getPOLARITY_CROSS_PHASE_DUR());
        pqstatus.setPolarity_cross_phase_ing(pqm.getPOLARITY_CROSS_PHASE_STAT() == 0 ? false : true);
        
        pqstatus.setReverse_pwr_cnt(pqm.getREVERSE_PWR_CNT());
        pqstatus.setReverse_pwr_dur(pqm.getREVERSE_PWR_DUR());
        pqstatus.setReverse_pwr_ing(pqm.getREVERSE_PWR_STAT() == 0 ? false : true);
        
        pqstatus.setService_vol_cnt(pqm.getSERVICE_VOL_CNT());
        pqstatus.setService_vol_dur(pqm.getSERVICE_VOL_DUR());
        pqstatus.setService_vol_ing(pqm.getSERVICE_VOL_STAT() == 0 ? false : true);
        
        pqstatus.setTdd_cnt(pqm.getTDD_CNT());
        pqstatus.setTdd_dur(pqm.getTDD_DUR());
        pqstatus.setTdd_ing(pqm.getTDD_STAT() == 0 ? false : true);
        
        pqstatus.setThd_curr_cnt(pqm.getTHD_CURR_CNT());
        pqstatus.setThd_curr_dur(pqm.getTHD_CURR_DUR());
        pqstatus.setThd_curr_ing(pqm.getTHD_CURR_STAT() == 0 ? false : true);
        
        pqstatus.setThd_vol_cnt(pqm.getTHD_VOL_CNT());
        pqstatus.setThd_vol_dur(pqm.getTHD_VOL_DUR());
        pqstatus.setThd_vol_ing(pqm.getTHD_VOL_STAT() == 0 ? false : true);
        
        pqstatus.setVol_a_sag_cnt(pqm.getVOL_A_SAG_CNT());
        pqstatus.setVol_a_sag_dur(pqm.getVOL_A_SAG_DUR());
        pqstatus.setVol_a_swell_cnt(pqm.getVOL_A_SWELL_CNT());
        pqstatus.setVol_a_swell_dur(pqm.getVOL_A_SWELL_DUR());
        
        pqstatus.setVol_b_sag_cnt(pqm.getVOL_B_SAG_CNT());
        pqstatus.setVol_b_sag_dur(pqm.getVOL_B_SAG_DUR());
        pqstatus.setVol_b_swell_cnt(pqm.getVOL_B_SWELL_CNT());
        pqstatus.setVol_b_swell_dur(pqm.getVOL_B_SWELL_DUR());
        
        pqstatus.setVol_c_sag_cnt(pqm.getVOL_C_SAG_CNT());
        pqstatus.setVol_c_sag_dur(pqm.getVOL_C_SAG_DUR());
        pqstatus.setVol_c_swell_cnt(pqm.getVOL_C_SWELL_CNT());
        pqstatus.setVol_c_swell_dur(pqm.getVOL_C_SWELL_DUR());
        
        pqstatus.setVol_cut_cnt(pqm.getVOL_CUT_CNT());
        pqstatus.setVol_cut_dur(pqm.getVOL_CUT_DUR());
        pqstatus.setVol_cut_ing(pqm.getVOL_CUT_STAT() == 0 ? false : true);
        
        pqstatus.setVol_flicker_cnt(pqm.getVOL_FLICKER_CNT());
        pqstatus.setVol_flicker_dur(pqm.getVOL_FLICKER_DUR());
        pqstatus.setVol_flicker_ing(pqm.getVOL_FLICKER_STAT() == 0 ? false : true);     
        
        pqstatus.setVol_fluctuation_cnt(pqm.getVOL_FLUCTUATION_CNT());
        pqstatus.setVol_fluctuation_dur(pqm.getVOL_FLUCTUATION_DUR());
        pqstatus.setVol_fluctuation_ing(pqm.getVOL_FLICKER_STAT() == 0 ? false : true);
        
        pqstatus.setVol_sag_cnt(pqm.getVOL_SAG_CNT());
        pqstatus.setVol_sag_dur(pqm.getVOL_SAG_DUR());
        pqstatus.setVol_sag_ing(pqm.getVOL_SAG_STAT() == 0 ? false : true);
        
        pqstatus.setVol_swell_cnt(pqm.getVOL_SWELL_CNT());
        pqstatus.setVol_swell_dur(pqm.getVOL_SWELL_DUR());
        pqstatus.setVol_swell_ing(pqm.getVOL_SWELL_STAT() == 0 ? false : true);     
        
        switch (mdevType) {
        case Meter :
            pqstatus.setMeter(meter);
            pqstatus.setSupplier(meter.getSupplier());
            break;
        case Modem :
            Modem modem = modemDao.get(mdevId);
            pqstatus.setModem(modem);
            if(modem!=null && modem.getSupplier() != null){
                pqstatus.setSupplier(modem.getSupplier());
            }
            break;
        case EndDevice :
            // pw.setEnvdevice(enddevice);
        }
        powerQualityStatusDao.saveOrUpdate(pqstatus);
    }

    protected void savePowerQuality(Meter meter, String time, Instrument[] instrument,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    throws Exception
    {
        PowerQuality pw = null;
        String _time = "";
        int dst = 0;

        Set<Condition> cond = null;
        List<PowerQuality> pqlist = null;
        
        for (Instrument ins : instrument) {
            _time = DateTimeUtil.getDST(null, ins.getDatetime()+"00");
            dst = DateTimeUtil.inDST(null, _time);
            
            cond = new LinkedHashSet<Condition>();
            cond.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
            cond.add(new Condition("id.dst", new Object[]{dst}, null, Restriction.EQ));
            cond.add(new Condition("id.yyyymmddhhmm", new Object[]{_time.substring(0, 12)}, null, Restriction.EQ));
            cond.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));
            
            pqlist = powerQualityDao.findByConditions(cond);
            if (pqlist != null && pqlist.size() > 0)
                continue;
            
            pw = new PowerQuality();
            BeanUtils.copyProperties(pw, ins);

            pw.setDeviceId(deviceId);
            pw.setDeviceType(deviceType);
            pw.setDst(dst);
            pw.setLine_frequency(ins.getLINE_FREQUENCY());
            pw.setMDevId(mdevId);
            pw.setMDevType(mdevType.name());
            pw.setYyyymmdd(_time.substring(0, 8));
            pw.setHhmm(_time.substring(8,12));
            pw.setYyyymmddhhmm(_time.substring(0, 12));
            pw.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));

            switch (mdevType) {
            case Meter :
                pw.setMeter(meter);
                pw.setSupplier(meter.getSupplier());
                break;
            case Modem :
                Modem modem = modemDao.get(mdevId);
                pw.setModem(modem);
                if(modem!=null && modem.getSupplier() != null){
                    pw.setSupplier(modem.getSupplier());
                }
                break;
            case EndDevice :
                // pw.setEnvdevice(enddevice);
            }

            if (meter != null && meter.getContract() != null)
                pw.setContract(meter.getContract());

            pw.setCurr_1st_harmonic_mag_a(ins.getCURR_1ST_HARMONIC_MAG_A());
            pw.setCurr_1st_harmonic_mag_b(ins.getCURR_1ST_HARMONIC_MAG_B());
            pw.setCurr_1st_harmonic_mag_c(ins.getCURR_1ST_HARMONIC_MAG_C());
            pw.setCurr_2nd_harmonic_mag_a(ins.getCURR_2ND_HARMONIC_MAG_A());
            pw.setCurr_2nd_harmonic_mag_b(ins.getCURR_2ND_HARMONIC_MAG_B());
            pw.setCurr_2nd_harmonic_mag_c(ins.getCURR_2ND_HARMONIC_MAG_C());
            pw.setCurr_a(ins.getCURR_A());
            pw.setCurr_angle_a(ins.getCURR_ANGLE_A());
            pw.setCurr_angle_b(ins.getCURR_ANGLE_B());
            pw.setCurr_angle_c(ins.getCURR_ANGLE_C());
            pw.setCurr_b(ins.getCURR_B());
            pw.setCurr_c(ins.getCURR_C());
            pw.setCurr_harmonic_a(ins.getCURR_HARMONIC_A());
            pw.setCurr_harmonic_b(ins.getCURR_HARMONIC_B());
            pw.setCurr_harmonic_c(ins.getCURR_HARMONIC_C());
            pw.setCurr_seq_n(ins.getCURR_SEQ_N());
            pw.setCurr_seq_p(ins.getCURR_SEQ_P());
            pw.setCurr_seq_z(ins.getCURR_SEQ_Z());
            pw.setCurr_thd_a(ins.getCURR_THD_A());
            pw.setCurr_thd_b(ins.getCURR_THD_B());
            pw.setCurr_thd_c(ins.getCURR_THD_C());
            pw.setDistortion_kva_a(ins.getDISTORTION_KVA_A());
            pw.setDistortion_kva_b(ins.getDISTORTION_KVA_B());
            pw.setDistortion_kva_c(ins.getDISTORTION_KVA_C());
            pw.setDistortion_pf_a(ins.getDISTORTION_PF_A());
            pw.setDistortion_pf_b(ins.getDISTORTION_PF_B());
            pw.setDistortion_pf_c(ins.getDISTORTION_PF_C());
            pw.setDistortion_pf_total(ins.getDISTORTION_PF_TOTAL());
            pw.setKva_a(ins.getKVA_A());
            pw.setKva_b(ins.getKVA_B());
            pw.setKva_c(ins.getKVA_C());
            pw.setKvar_a(ins.getKVAR_A());
            pw.setKvar_b(ins.getKVAR_B());
            pw.setKvar_c(ins.getKVAR_C());
            pw.setKw_a(ins.getKW_A());
            pw.setKw_b(ins.getKW_B());
            pw.setKw_c(ins.getKW_C());
            pw.setPf_a(ins.getPF_A());
            pw.setPf_b(ins.getPF_B());
            pw.setPf_c(ins.getPF_C());
            pw.setPf_total(ins.getPF_TOTAL());
            pw.setPh_curr_pqm_a(ins.getPH_CURR_PQM_A());
            pw.setPh_curr_pqm_b(ins.getPH_CURR_PQM_B());
            pw.setPh_curr_pqm_c(ins.getPH_CURR_PQM_C());
            pw.setPh_fund_curr_a(ins.getPH_FUND_CURR_A());
            pw.setPh_fund_curr_b(ins.getPH_FUND_CURR_B());
            pw.setPh_fund_curr_c(ins.getPH_CURR_PQM_C());
            pw.setPh_fund_vol_a(ins.getPH_FUND_VOL_A());
            pw.setPh_fund_vol_b(ins.getPH_FUND_VOL_B());
            pw.setPh_fund_vol_c(ins.getPH_FUND_VOL_C());
            pw.setPh_vol_pqm_a(ins.getPH_VOL_PQM_A());
            pw.setPh_vol_pqm_b(ins.getPH_VOL_PQM_B());
            pw.setPh_vol_pqm_c(ins.getPH_VOL_PQM_C());
            pw.setSystem_pf_angle(ins.getSYSTEM_PF_ANGLE());
            pw.setTdd_a(ins.getTDD_A());
            pw.setTdd_b(ins.getTDD_B());
            pw.setTdd_c(ins.getTDD_C());
            pw.setVol_1st_harmonic_mag_a(ins.getVOL_1ST_HARMONIC_MAG_A());
            pw.setVol_1st_harmonic_mag_b(ins.getVOL_1ST_HARMONIC_MAG_B());
            pw.setVol_1st_harmonic_mag_c(ins.getVOL_1ST_HARMONIC_MAG_C());
            pw.setVol_2nd_harmonic_a(ins.getVOL_2ND_HARMONIC_A());
            pw.setVol_2nd_harmonic_b(ins.getVOL_2ND_HARMONIC_B());
            pw.setVol_2nd_harmonic_c(ins.getVOL_2ND_HARMONIC_C());
            pw.setVol_2nd_harmonic_mag_a(ins.getVOL_2ND_HARMONIC_MAG_A());
            pw.setVol_2nd_harmonic_mag_b(ins.getVOL_2ND_HARMONIC_MAG_B());
            pw.setVol_2nd_harmonic_mag_c(ins.getVOL_2ND_HARMONIC_MAG_C());
            pw.setVol_a(ins.getVOL_A());
            pw.setVol_b(ins.getVOL_B());
            pw.setVol_c(ins.getVOL_C());
            pw.setVol_angle_a(ins.getVOL_ANGLE_A());
            pw.setVol_angle_b(ins.getVOL_ANGLE_B());
            pw.setVol_angle_c(ins.getVOL_ANGLE_C());
            pw.setVol_seq_n(ins.getVOL_SEQ_N());
            pw.setVol_seq_p(ins.getVOL_SEQ_P());
            pw.setVol_seq_z(ins.getVOL_SEQ_Z());
            pw.setVol_thd_a(ins.getVOL_THD_A());
            pw.setVol_thd_b(ins.getVOL_THD_B());
            pw.setVol_thd_c(ins.getVOL_THD_C());
            pw.setLine_AB(ins.getLine_AB());
            pw.setLine_BC(ins.getLine_BC());
            pw.setLine_CA(ins.getLine_CA());

            powerQualityDao.add(pw);
        }
    }

    
    protected void saveMeteringDataWithMultiChannel(MeteringType meteringType, String meteringDate,
            String meteringTime, double meteringValue, Meter meter, DeviceType deviceType,
            String deviceId, DeviceType mdevType, String mdevId, String meterTime, Double[] channels) throws Exception
    {
        log.debug("MeteringType[" + meteringType + "] MeteringDate[" + meteringDate +
                "] MeteringTime[" + meteringTime + "] MeteringValue[" + meteringValue +
                "] DeviceType[" + deviceType + "] DeviceId[" + deviceId +
                "] MDevType[" + mdevType + "] MDevId[" + mdevId + "] MeterTime[" + meterTime + "]");

        //영속
        // meterDao.saveOrUpdate(meter);
        
        MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());

        MeteringData mdata = null;
        switch (meterType) {
        case EnergyMeter :
            mdata = new MeteringDataEM();
            break;
        case WaterMeter :
            mdata = new MeteringDataWM();
            break;
        case GasMeter :
            mdata = new MeteringDataGM();
            break;
        case HeatMeter :
            mdata = new MeteringDataHM();
            break;
        case SolarPowerMeter :
            mdata = new MeteringDataSPM();
            break;
        }

        mdata.setDeviceId(deviceId);
        mdata.setDeviceType(deviceType.name());
        mdata.setMDevId(mdevId);
        mdata.setMDevType(mdevType.name());
        mdata.setMeteringType(meteringType.getType());
        mdata.setValue(dformat(meteringValue));
  
        if(channels != null && channels.length > 0){
        	for(int i = 0; i < channels.length; i++){
        		String chN = "ch"+(i+1);
        		BeanUtils.copyProperty(mdata, chN, dformat(dformat(channels[i])));
        	}
        }

        // TODO timezoneId
        mdata.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
        String dsttime = DateTimeUtil.getDST(null, meteringDate+meteringTime);
        mdata.setYyyymmdd(dsttime.substring(0, 8));
        mdata.setHhmmss(dsttime.substring(8, 14));
        mdata.setYyyymmddhhmmss(dsttime);
        mdata.setDst(DateTimeUtil.inDST(null, mdata.getYyyymmddhhmmss()));

        // 미터 계약관계가 없을 수도 있다.
        if (meter!=null && meter.getContract() != null) {
            mdata.setContract(meter.getContract());
        }

        switch (mdevType) {
        case Meter :
            mdata.setMeter(meter);
            mdata.setLocation(meter.getLocation());
            if(meter!=null && meter.getSupplier() != null){
                mdata.setSupplier(meter.getSupplier());
            }
            if (meter.getModem() != null)
                mdata.setModem(meter.getModem());
            break;
        case Modem :
            Modem modem = modemDao.get(mdevId);
            mdata.setModem(modem);
            mdata.setLocation(modem.getLocation());
            if(modem!=null && modem.getSupplier() != null){
                mdata.setSupplier(modem.getSupplier());
            }
            break;
        case EndDevice :
        }

        // 미터와 모뎀 최종 통신 시간과 값을 갱신한다.
        // if (meter.getLastMeteringValue() == null || meter.getLastMeteringValue() < meteringValue)
        //    meter.setLastMeteringValue(meteringValue);
        log.debug("MDevId[" + mdevId + "] DSTTime["+dsttime+"] LASTREADDate[" + meter.getLastReadDate()+"]");
        // if (meter.getLastReadDate() == null || dsttime.substring(0, 10).compareTo(meter.getLastReadDate().substring(0, 10)) >= 0) {
            if (meterTime != null && !"".equals(meterTime))
                meter.setLastReadDate(meterTime);
            else
                meter.setLastReadDate(dsttime);
            
            meter.setLastMeteringValue(meteringValue);
            Code normalStatus = CommonConstants.getMeterStatusByName(MeterStatus.Normal.name());
            log.debug("MDevId[" + mdevId + "] METER_STATUS[" + (meter.getMeterStatus() == null ? "NULL" : meter.getMeterStatus()) + "]");
            //log.debug("METER_OLD_STATUS[" + (normalStatus==null? "NULL":normalStatus.getName()) + "]");
            if (meter.getMeterStatus() == null || 
                    (meter.getMeterStatus() != null && 
                    !meter.getMeterStatus().getName().equals("CutOff") && 
                    !meter.getMeterStatus().getName().equals("Delete"))){
                meter.setMeterStatus(normalStatus);
                log.debug("MDevId[" + mdevId + "] METER_CHANGED_STATUS[" + meter.getMeterStatus() + "]");
            }

            
            if (meterTime != null && !"".equals(meterTime)) {
                try {
                    long diff = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meteringDate+meteringTime).getTime() - 
                            DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime();
                    meter.setTimeDiff(diff / 1000);
                }
                catch (ParseException e) {
                    log.warn("MDevId[" + mdevId + "] Check MeterTime[" + meterTime + "] and MeteringTime[" + meteringDate + meteringTime + "]");
                }
            }
            //=> DELETE START 2017.02.28 SP-516
            //// 수검침과 같이 모뎀과 관련이 없는 경우는 예외로 처리한다.
            //if (meter.getModem() != null) {
            //    meter.getModem().setLastLinkTime(dsttime);
            //}
            //=> DELETE END   2017.02.28 SP-516
        // }
        try {
            meteringDataDao.saveOrUpdate(mdata);
            //=> DELETE START 2017.02.28 SP-516
            //modemDao.update(meter.getModem());
            //meterDao.update(meter);
            //=> DELETE END   2017.02.28 SP-516
        }
        catch (Exception e) {
            log.warn(e);
        }
        //=> INSERT START 2017.02.28 SP-516
        try {
        	if (meter.getModem() != null) {
        		meter.getModem().setLastLinkTime(dsttime);
        		modemDao.update(meter.getModem());
        	}
        }
        catch (Exception e) {
            log.warn(e);
        }
        try {
        	meterDao.update(meter);
        }
        catch (Exception e) {
        	log.warn(e);
        }
        //=> INSERT END   2017.02.28 SP-516
    }
    
    protected void saveMeteringData(MeteringType meteringType, String meteringDate,
            String meteringTime, double meteringValue, Meter meter, DeviceType deviceType,
            String deviceId, DeviceType mdevType, String mdevId, String meterTime) throws Exception
    {
        log.debug("MeteringType[" + meteringType + "] MeteringDate[" + meteringDate +
                "] MeteringTime[" + meteringTime + "] MeteringValue[" + meteringValue +
                "] DeviceType[" + deviceType + "] DeviceId[" + deviceId +
                "] MDevType[" + mdevType + "] MDevId[" + mdevId + "] MeterTime[" + meterTime + "]");

        //영속
        // meterDao.saveOrUpdate(meter);
        
        MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());

        MeteringData mdata = null;
        switch (meterType) {
        case EnergyMeter :
            mdata = new MeteringDataEM();
            break;
        case WaterMeter :
            mdata = new MeteringDataWM();
            break;
        case GasMeter :
            mdata = new MeteringDataGM();
            break;
        case HeatMeter :
            mdata = new MeteringDataHM();
            break;
        case SolarPowerMeter :
            mdata = new MeteringDataSPM();
            break;
        }

        mdata.setDeviceId(deviceId);
        mdata.setDeviceType(deviceType.name());
        mdata.setMDevId(mdevId);
        mdata.setMDevType(mdevType.name());
        mdata.setMeteringType(meteringType.getType());
        mdata.setValue(dformat(meteringValue));

        // TODO timezoneId
        mdata.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
        String dsttime = DateTimeUtil.getDST(null, meteringDate+meteringTime);
        mdata.setYyyymmdd(dsttime.substring(0, 8));
        mdata.setHhmmss(dsttime.substring(8, 14));
        mdata.setYyyymmddhhmmss(dsttime);
        mdata.setDst(DateTimeUtil.inDST(null, mdata.getYyyymmddhhmmss()));

        // 미터 계약관계가 없을 수도 있다.
        if (meter!=null && meter.getContract() != null) {
            mdata.setContract(meter.getContract());
        }

        switch (mdevType) {
        case Meter :
            mdata.setMeter(meter);
            mdata.setLocation(meter.getLocation());
            if(meter!=null && meter.getSupplier() != null){
                mdata.setSupplier(meter.getSupplier());
            }
            if (meter.getModem() != null)
                mdata.setModem(meter.getModem());
            break;
        case Modem :
            Modem modem = modemDao.get(mdevId);
            mdata.setModem(modem);
            mdata.setLocation(modem.getLocation());
            if(modem!=null && modem.getSupplier() != null){
                mdata.setSupplier(modem.getSupplier());
            }
            break;
        case EndDevice :
        }

        // 미터와 모뎀 최종 통신 시간과 값을 갱신한다.
        // if (meter.getLastMeteringValue() == null || meter.getLastMeteringValue() < meteringValue)
        //    meter.setLastMeteringValue(meteringValue);
        log.debug("MDevId[" + mdevId + "] DSTTime["+dsttime+"] LASTREADDate[" + meter.getLastReadDate()+"]");
        // if (meter.getLastReadDate() == null || dsttime.substring(0, 10).compareTo(meter.getLastReadDate().substring(0, 10)) >= 0) {
        if (meterTime != null && !"".equals(meterTime))
            meter.setLastReadDate(meterTime);
        else
            meter.setLastReadDate(dsttime);
        
        meter.setLastMeteringValue(meteringValue);
        Code normalStatus = CommonConstants.getMeterStatusByName(MeterStatus.Normal.name());
        log.debug("MDevId[" + mdevId + "] METER_STATUS[" + (meter.getMeterStatus() == null ? "NULL" : meter.getMeterStatus()) + "]");
        //log.debug("METER_OLD_STATUS[" + (normalStatus==null? "NULL":normalStatus.getName()) + "]");
        if (meter.getMeterStatus() == null || 
                (meter.getMeterStatus() != null && 
                !meter.getMeterStatus().getName().equals("CutOff") && 
                !meter.getMeterStatus().getName().equals("Delete"))){
            meter.setMeterStatus(normalStatus);
            log.debug("MDevId[" + mdevId + "] METER_CHANGED_STATUS[" + meter.getMeterStatus() + "]");
        }

        
        if (meterTime != null && !"".equals(meterTime)) {
            try {
                long diff = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meteringDate+meteringTime).getTime() - 
                        DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime();
                meter.setTimeDiff(diff / 1000);
            }
            catch (ParseException e) {
                log.warn("MDevId[" + mdevId + "] Check MeterTime[" + meterTime + "] and MeteringTime[" + meteringDate + meteringTime + "]");
            }
        }
        
        // 수검침과 같이 모뎀과 관련이 없는 경우는 예외로 처리한다.
        if (meter.getModem() != null) {
            meter.getModem().setLastLinkTime(dsttime);
        }
        // }
    
        try {
            meteringDataDao.saveOrUpdate(mdata);
            meterDao.update(meter);
        }
        catch (Exception e) {
            log.warn(e);
        }
    }

    protected void saveMeteringDataSP(String meteringDate, String meteringTime, Meter meter) throws Exception
    {
        log.debug("saveMeteringData SP MeteringDate[" + meteringDate +
                "] MeteringTime[" + meteringTime + "]");
   
        String dsttime = DateTimeUtil.getDST(null, meteringDate+meteringTime);
        
        try {
        	if (meter.getModem() != null) {
        		meter.getModem().setLastLinkTime(dsttime);
        		modemDao.update(meter.getModem());
        	}
        }
        catch (Exception e) {
            log.warn(e);
        }
        try {
        	meterDao.update(meter);
        }
        catch (Exception e) {
        	log.warn(e);
        }
    }    
    
    /**
     * 검침데이타를 채널별로 생성하고 0번 채널(탄소배출량), 100번 채널(상태)를 추가한다.
     * @param meteringType
     * @param lpDate    yyyyMMdd
     * @param lpTime    HHmm
     * @param lplist
     * @param flaglist
     * @param baseValue
     * @param meter
     * @param deviceType
     * @param deviceId
     * @param mdevType
     * @param mdevId
     * @return
     * @throws Exception
     */
    private List<MeteringLP>[] makeChLPs(MeteringType meteringType, String lpDate, String lpTime,
            double[][] lplist, int[] flaglist, double[] baseValue, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    throws Exception
    {
        double lpThreshold = Double.parseDouble(FMPProperty.getProperty("lp.threshold", "300"));
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(lpDate+lpTime+"00"));
        String dsttime = DateTimeUtil.getDST(null, DateTimeUtil.getDateString(cal.getTime()));
        int dst = DateTimeUtil.inDST(null, dsttime);
        cal.setTime(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(dsttime));

        // 처음 시작하는 분을 기억하고 있다가 사용해야 한다.
        int startmm = Integer.parseInt(dsttime.substring(10, 12));

        // 0 채널(탄소배출량), 98 채널(연동 전송여부), 100 채널 (플래그) 추가한다.
        List<MeteringLP>[] chLPs = new ArrayList[lplist.length+3];
        for (int ch=0; ch < chLPs.length; ch++) {
            chLPs[ch] = new ArrayList<MeteringLP>();
        }

        // 미터의 채널 정보를 가져온다.
        ChannelConfig[] channels = null;
        if (meter.getModel() != null && meter.getModel().getDeviceConfig() != null)
            channels = ((MeterConfig)meter.getModel().getDeviceConfig()).getChannels().toArray(new ChannelConfig[0]);
        else channels = new ChannelConfig[0];
        
        int _dst = 0;
        String _dsttime = null;
        MeteringLP[] lps = null;
        double[] sum = baseValue;
        MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());

        int valuecnt = 0;
        int mm = startmm;
        for (int lpcnt = 0; lpcnt < lplist[0].length;) {
            valuecnt = 0;
            
            // lp는 로우가 시간별로 존재하기 때문에 여기서 초기화한다.
            lps = newMeteringLP(meterType, lplist.length);
            for (int ch = 0; ch < lps.length; ch++) {
                lps[ch].setChannel(ch+1);
                lps[ch].setDeviceId(deviceId);
                lps[ch].setDeviceType(deviceType.name());
                lps[ch].setMDevId(mdevId);
                lps[ch].setMDevType(mdevType.name());
                lps[ch].setMeteringType(meteringType.getType());
                lps[ch].setValue(dformat(sum[ch]));

                if (meter.getContract() != null)
                    lps[ch].setContract(meter.getContract());

                switch (mdevType) {
                case Meter :
                    lps[ch].setMeter(meter);
                    lps[ch].setLocation(meter.getLocation());
                    lps[ch].setSupplier(meter.getSupplier());
                    if (meter.getModem() != null)
                        lps[ch].setModem(meter.getModem());
                    break;
                case Modem :
                    Modem modem = modemDao.get(mdevId);
                    lps[ch].setModem(modem);
                    lps[ch].setLocation(modem.getLocation());
                    lps[ch].setSupplier(modem.getSupplier());
                    break;
                case EndDevice :
                }
            }

            for (; mm < 60 && lplist[0].length > lpcnt; mm+=meter.getLpInterval(), lpcnt++) {
                _dsttime = DateTimeUtil.getDateString(cal.getTime());
                _dst = DateTimeUtil.inDST(null, _dsttime);

                if (!_dsttime.substring(0,8).equals(dsttime.substring(0,8)) || _dst != dst) {
                    dsttime = _dsttime;
                    dst = _dst;
                    break;
                }
                else {
                    double tmp = 0.0;
                    for (int ch = 0; ch < lps.length; ch++) {
                        // 2015.02.26 마이너스가 나오는 경우 0.0으로 처리한다.
                        tmp = dformat(lplist[ch][lpcnt]);
                        if (getChMethod(channels, lps[ch].getChannel()) == ChannelCalcMethod.SUM 
                                && (tmp < 0.0 || tmp > lpThreshold)
                                && meter.getModel().getName().equals(MeterVendor.KAMSTRUP.getName())) {
                            log.warn("MDevId[" + mdevId + "] LP Value[" + tmp + "] is minus or over "+lpThreshold+"!, It is replaced to null");
                            BeanUtils.copyProperty(lps[ch], String.format("value_%02d", mm), null);
                            try {
                                EventUtil.sendEvent("Meter Value Alarm",
                                        TargetClass.valueOf(meter.getMeterType().getName()),
                                        meter.getMdsId(),
                                        new String[][] {{"message", "LP DateTime[" + _dsttime + "] Value[" + tmp + "]"}}
                                        );
                            }
                            catch (Exception ignore) {
                            }
                        }
                        else {
                            BeanUtils.copyProperty(lps[ch], String.format("value_%02d", mm), dformat(lplist[ch][lpcnt]));
                            valuecnt++;
                        }

                        lps[ch].setHour(_dsttime.substring(8, 10));
                        lps[ch].setYyyymmdd(_dsttime.substring(0,8));
                        lps[ch].setYyyymmddhh(_dsttime.substring(0, 10));
                        lps[ch].setDst(_dst);
                        lps[ch].setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                        lps[ch].setValueCnt(valuecnt);
                        
                        switch (getChMethod(channels, lps[ch].getChannel())) {
                        case SUM : sum[ch] += lplist[ch][lpcnt];
                                   break;
                        case MAX : sum[ch] = lplist[ch][lpcnt];
                                   break;
                        case AVG : sum[ch] += lplist[ch][lpcnt];
                                   sum[ch] /= lps[ch].getValueCnt();
                        }
                    }
                    cal.add(Calendar.MINUTE, meter.getLpInterval());
                }
            }

            if (mm >= 60)
                mm = 0;

            for (int ch = 0; ch < lps.length; ch++) {
                if (lps[ch].getYyyymmdd() != null && lps[ch].getDst() != null) {
                    chLPs[ch+1].add(lps[ch]);
                    log.debug("MDevId[" + mdevId + "] CH[" + lps[ch].getChannel() +
                            "] YYYYMMDDHH[" + lps[ch].getYyyymmddhh() +
                            "] WRITEDATE[" + lps[ch].getWriteDate() +
                            "] DST[" + lps[ch].getDst() +
                            "] VALUE[" + lps[ch].getValue() + "]");
                }
            }
        }

        // 탄소배출량채널과 검침값상태채널을 생성한다.
        MeteringLP lp = null;
        MeteringLP[] co = new MeteringLP[chLPs[1].size()];
        MeteringLP[] flag = new MeteringLP[chLPs[1].size()];
        MeteringLP[] integratedFlag = new MeteringLP[chLPs[1].size()];
        Co2Formula co2f = co2FormulaDao.getCo2FormulaBySupplyType(meterType.getServiceType());

        double lpValue = 0.0;

        // 시작 분을 초기화한다.
        mm = startmm; // Integer.parseInt(dsttime.substring(10, 12));
        String str_mm = "";
        for (int i = 0, flagcnt = 0; i < co.length; i++) {
            lp = chLPs[1].get(i);
            co[i] = newMeteringLP(meterType, lp);
            flag[i] = newMeteringLP(meterType, lp);
            integratedFlag[i] = newMeteringLP(meterType, lp);

            co[i].setChannel(ElectricityChannel.Co2.getChannel());
            flag[i].setChannel(ElectricityChannel.ValidationStatus.getChannel());
            integratedFlag[i].setChannel(ElectricityChannel.Integrated.getChannel());

            for (; mm < 60 && flagcnt < flaglist.length; mm += meter.getLpInterval(), flagcnt++) {
                str_mm = String.format("value_%02d", mm);
                String val = BeanUtils.getProperty(lp, str_mm);
                if(val != null && !"".equals(val)){
                    lpValue = Double.parseDouble(val);
                    BeanUtils.copyProperty(co[i], str_mm, dformat(lpValue*co2f.getCo2factor()));
                    BeanUtils.copyProperty(flag[i], str_mm, flaglist[flagcnt]);
                    BeanUtils.copyProperty(integratedFlag[i], str_mm, IntegratedFlag.NOTSENDED.getFlag());
                }

            }
            chLPs[0].add(co[i]);
            chLPs[chLPs.length-1].add(flag[i]);
            chLPs[chLPs.length-2].add(integratedFlag[i]);

            if (mm >= 60)
                mm = 0;
        }

        return chLPs;
    }

    /**
     * @param meteringType : normal, ondemand, recovery
     * @param lpDate:yyyymmdd
     * @param lpTime:hhmm
     * @param lplist
     * @param flaglist
     * @param baseValue
     * @param meter
     * @param deviceType: MCU(0), Modem(1), Meter(2), EndDevice(3);
     * @param deviceId: 통신 장비 아이디(modem인 경우 eui64, mcu인 경우 mcu id)
     * @param mdevType : 검침 장비 타입(modem, meter)
     * @param mdevId : 검침 장비 아이디
     * @throws Exception
     */
    public void saveLPDataP(MeteringType meteringType, String lpDate, String lpTime,
            double[][] lplist, int[] flaglist, double baseValue, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId) throws Exception{
        saveLPData(meteringType, lpDate, lpTime, lplist, flaglist, baseValue, meter,
                deviceType, deviceId, mdevType, mdevId);
    }
    
    protected void saveLPData(MeteringType meteringType, String lpDate, String lpTime,
            double[][] lplist, int[] flaglist, double baseValue, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    throws Exception
    {
        double[] _baseValue = new double[lplist.length];
        _baseValue[0] = baseValue;
        for (int i = 1; i < _baseValue.length; i++) {
            _baseValue[i] = 0;
        }
        
        saveLPData(meteringType, lpDate, lpTime, lplist, flaglist, _baseValue, meter, deviceType, deviceId, mdevType, mdevId);
    }


    private ChannelCalcMethod getChMethod(ChannelConfig[] ccs, int ch) {
        for (ChannelConfig cc : ccs) {
            if (cc.getChannelIndex() == ch) {
                if (cc.getChannel().getChMethod() != null)
                    return cc.getChannel().getChMethod();
                else return ChannelCalcMethod.MAX;
            }
        }
        
        return ChannelCalcMethod.MAX;
    }
    
    /**
     * @param meteringType : normal, ondemand, recovery
     * @param lpDate:yyyymmdd
     * @param lpTime:hhmm
     * @param lplist
     * @param flaglist
     * @param baseValue
     * @param meter
     * @param deviceType: MCU(0), Modem(1), Meter(2), EndDevice(3);
     * @param deviceId: 통신 장비 아이디(modem인 경우 eui64, mcu인 경우 mcu id)
     * @param mdevType : 검침 장비 타입(modem, meter)
     * @param mdevId : 검침 장비 아이디
     * @throws Exception
     */
    protected void saveLPData1(MeteringType meteringType, String lpDate, String lpTime,
            double[][] lplist, int[] flaglist, double[] baseValue, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    throws Exception
    {
        log.debug("Meter Serial:"+meter.getMdsId()+" , Lp Date:"+lpDate+" baseValue_cnt:"+baseValue.length+" ");
        List<MeteringLP>[] chLPs = makeChLPs(meteringType, lpDate, lpTime,  lplist, flaglist,
                baseValue, meter, deviceType, deviceId, mdevType, mdevId);

        MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());

        // 미터의 채널 정보를 가져온다.
        ChannelConfig[] channels = null;
        if (meter.getModel() != null && meter.getModel().getDeviceConfig() != null)
            channels = ((MeterConfig)meter.getModel().getDeviceConfig()).getChannels().toArray(new ChannelConfig[0]);
        else channels = new ChannelConfig[0];
        
        if (chLPs[1].size() == 0) {
            throw new Exception("MDevId[" + mdevId + "] LP size is 0!!!");
        }

        // 날짜별로 LP를 조회한다.
        MeteringLP lp = chLPs[1].get(0);
        MeteringLP _lp = null;
        MeteringLP _integrated = null;
        String yyyymmdd = lp.getYyyymmdd();
        int dst = lp.getDst();

        LinkedHashSet<Condition> condition = null;
        Map<LpPk, MeteringLP>[] _chLPs = null;
        Map<DayPk, MeteringDay>[] _chDays = null;

        List<MeteringLP>[] addLPs = new ArrayList[chLPs.length];
        List<MeteringLP>[] updateLPs = new ArrayList[chLPs.length];
        List<MeteringDay>[] addDays = new ArrayList[chLPs.length];
        List<MeteringDay>[] updateDays = new ArrayList[chLPs.length];

        String lpValue = null;
        String _lpValue = null;
        boolean dayUpdated = false;
        int valueCnt = 0;
        String str_mm = "";
        for (int lpcnt = 0; lpcnt < chLPs[1].size();) {
            // 전 채널 검침데이타를 가져온다.
            condition = new LinkedHashSet<Condition>();
            condition.add(new Condition("id.yyyymmddhh", new Object[]{yyyymmdd+"00", yyyymmdd+"23"}, null, Restriction.BETWEEN));
            condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
            condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));
            condition.add(new Condition("id.dst", new Object[]{dst}, null, Restriction.EQ));
            
            // 주기 채널별로 검침데이타를 분리한다.
            _chLPs = listLP(condition, meterType, chLPs.length);
            condition = new LinkedHashSet<Condition>();
            condition.add(new Condition("id.yyyymmdd", new Object[]{yyyymmdd}, null, Restriction.EQ));
            condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
            condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));
            condition.add(new Condition("id.dst", new Object[]{dst}, null, Restriction.EQ));
            
            // 일 채널별로 검침데이타를 분리한다.
            _chDays = listDay(condition, meterType, _chLPs.length);

            log.debug("MDevId[" + mdevId + "] _chLPs[" + _chLPs.length + "] _chDays[" + _chDays.length + "]");
            
            // lp 주기 합산값을 day의 시간 값에 대입한다.
            MeteringDay _lpday = null;
            if (_chDays[0].size() == 0) {
                _chDays = new HashMap[chLPs.length];
                dayUpdated = false;
            }
            else {
                dayUpdated = true;
            }

            for (;lpcnt < chLPs[1].size();lpcnt++) {
                lp = chLPs[1].get(lpcnt);
                // 월일 또는 DST가 다르면 검침데이타를 다시 조회하여 비교할 수 있도록 한다.
                if (!yyyymmdd.equals(lp.getYyyymmdd()) || dst != lp.getDst()) {
                    yyyymmdd = lp.getYyyymmdd();
                    dst = lp.getDst();
                    // Metering Day를 업데이트 또는 생성한다.
                    for (int ch = 0; ch < chLPs.length; ch++) {
                        if (dayUpdated) {
                            if (updateDays[ch] == null) {
                                updateDays[ch] = new ArrayList<MeteringDay>();
                            }
                            updateDays[ch].add(_chDays[ch].get(0));
                        }
                        else {
                            if (addDays[ch] == null) {
                                addDays[ch] = new ArrayList<MeteringDay>();
                            }
                            addDays[ch].add(_chDays[ch].get(0));
                        }
                    }
                    break;
                }

                boolean needUpdate = false;
                if (_chLPs.length != 0 && _chLPs[1].size() != 0) {
                    // 같은 일에 대하여 시간에 대한 주기값이 존재하는지 검사하여 있으면 변경 없으면 추가
                    // 추가 변경 플래그
                    for (int i = 0; i < _chLPs[1].size(); i++) {
                        _lp = _chLPs[1].get(i);
                        _integrated = _chLPs[_chLPs.length-2].get(i);

                        // DB에 있는 동일 일시에 대한 주기별 데이타를 갱신하기 위해 _lp에 값을 채운다.
                        if (lp.getYyyymmddhh().equals(_lp.getYyyymmddhh())) {
                            needUpdate = true;
                            log.debug("MDevId[" + mdevId + "] ch count[" + chLPs.length + "]");
                            for (int ch = 0; ch < chLPs.length; ch++) {
                                
                                // 연동채널은 사용량과 같이 처리가 되어 건너뛴다.
                                if (ch == chLPs.length - 2)
                                    continue;

                                lp = chLPs[ch].get(lpcnt);
                                if (_chLPs[ch].size() > i) {
                                    _lp = _chLPs[ch].get(i);
                                    _lp.setWriteDate(lp.getWriteDate());
                                }
                                else {
                                    _lp = lp;
                                }

                                // base pulse 값이 db에 있는 것이 크면 변경한다.
                                // log.debug("ch[" + lp.getChannel() + "] lp value[" + lp.getValue() + "] _lp value[" + _lp.getValue() + "]");
                                if (lp.getValue() != null)
                                    _lp.setValue(dformat(lp.getValue()));

                                // 널이 아닌 주기값을 lp에 넣는다.
                                for (int _mm = 0; _mm < 60; _mm+=meter.getLpInterval()) {
                                    str_mm = String.format("value_%02d", _mm);
                                    _lpValue = BeanUtils.getProperty(_lp, str_mm);
                                    lpValue = BeanUtils.getProperty(lp, str_mm);
                                    
                                    if (lpValue != null && !lpValue.equals(_lpValue)) {
                                        // TODO _lpValue가 널이면 값을 채우게 되는데 널은 아닌데 값이 다르다면
                                        // 사용량이 이상한 것이므로 검침데이타가 깨졌을 수도 있다. 따라서, 이벤트 전송.
                                        if (_lpValue != null) {
                                        }
                                        // 값이 달라진 것에 대해서는 전송 상태더라도 미전송 상태로 변경한다.
                                        if (lp.getChannel() == ElectricityChannel.Usage.getChannel()) {
                                            BeanUtils.copyProperty(_integrated, str_mm,
                                                    IntegratedFlag.NOTSENDED.getFlag());
                                            _integrated.setWriteDate(lp.getWriteDate());
                                        }
                                        BeanUtils.copyProperty(_lp, str_mm, dformat(Double.parseDouble(lpValue)));
                                    }
                                }
                                chLPs[ch].set(lpcnt, _lp);
                                if (lp.getChannel() == ElectricityChannel.Usage.getChannel())
                                    chLPs[chLPs.length-2].set(lpcnt, _integrated);
                            }
                        }
                    }
                }
                
                // lp 개수를 파악한다.
                for (int ch = 0; ch < chLPs.length; ch++, valueCnt=0) {
                    _lp = chLPs[ch].get(lpcnt);
                    for (int _mm = 0; _mm < 60; _mm+=meter.getLpInterval()) {
                        str_mm = String.format("value_%02d", _mm);
                        _lpValue = BeanUtils.getProperty(_lp, str_mm);
                        if (_lpValue != null && !"".equals(_lpValue))
                            valueCnt++;
                    }
                    _lp.setValueCnt(valueCnt);
                    chLPs[ch].set(lpcnt, _lp);
                }
                
                // 추가와 변경을 선별한다.
                if (needUpdate) {
                    // 업데이트 리스트
                    for (int ch = 0; ch < chLPs.length; ch++) {
                        if (updateLPs[ch] == null)
                            updateLPs[ch] = new ArrayList<MeteringLP>();

                        updateLPs[ch].add(chLPs[ch].get(lpcnt));
                    }
                }
                else {
                    // 추가 리스트
                    for (int ch = 0; ch < chLPs.length; ch++) {
                        if (addLPs[ch] == null)
                            addLPs[ch] = new ArrayList<MeteringLP>();

                        addLPs[ch].add(chLPs[ch].get(lpcnt));
                    }
                }

                // DAY를 생성하거나 업데이트
                // DAY의 시간값을 구하기 위해 LP의 주기값을 합산한다.
                for (int ch = 0; ch < chLPs.length; ch++) {
                    lp = chLPs[ch].get(lpcnt);
                    if (_chDays.length != 0 && _chDays[ch] != null && _chDays[ch].size() != 0) {
                        _lpday = _chDays[ch].get(0);
                    }
                    else {
                        _chDays[ch] = new HashMap<DayPk, MeteringDay>();
                        _chDays[ch].put(_lpday.getId(), _lpday);
                        _lpday = newMeteringDay(meteringType, meterType,
                                meter, deviceType, deviceId, mdevType, mdevId);
                        _lpday.setChannel(lp.getChannel());
                        // base value가 채널 개수만큼 존재할 때 chLPs의 채널개수는 3개가 더 늘어나므로 그 개수만큼 늘려준다.
                        if (ch == 0|| ch==chLPs.length-2 || ch==chLPs.length-1)
                            _lpday.setBaseValue(0.0);
                        else
                            _lpday.setBaseValue(baseValue[ch-1]);
                        _lpday.setYyyymmdd(lp.getYyyymmdd());
                        _lpday.setDayType(getDayType(lp.getYyyymmdd()));
                        _lpday.setDst(lp.getDst());
                    }

                    if (meter.getContract() != null) {
                        _lpday.setContract(meter.getContract());
                        if (_lpday.getContract().getSic() != null)
                            _lpday.setSic(_lpday.getContract().getSic().getCode());
                    }
                        
                    if (_lpday.getChannel().equals(ElectricityChannel.ValidationStatus.getChannel())) {
                        double _flag = 0;
                        double _nextFlag = 0;
                        String _strNextFlag = null;
                        for (int _mm = 0; _mm < 60; _mm += meter.getLpInterval()) {
                            _strNextFlag = BeanUtils.getProperty(lp, String.format("value_%02d", _mm));
                            if (_strNextFlag != null) {
                                _nextFlag = Double.parseDouble(_strNextFlag);
                                if (_flag < _nextFlag)
                                    _flag = _nextFlag;
                            }
                        }
                        BeanUtils.copyProperty(_lpday, "value_" + lp.getHour(), _flag);

                        for (int _hh = 0; _hh < 24; _hh++) {
                            _strNextFlag = BeanUtils.getProperty(_lpday, String.format("value_%02d", _hh));
                            if (_strNextFlag != null) {
                                _nextFlag = Double.parseDouble(_strNextFlag);
                                if (_flag < _nextFlag)
                                    _flag = _nextFlag;
                            }
                        }
                        _lpday.setTotal(_flag);
                    }
                    else if (_lpday.getChannel().equals(ElectricityChannel.Integrated.getChannel())) {
                        double _flag = -1;
                        double _nextFlag = 0;
                        String _strNextFlag = null;
                        for (int _mm = 0; _mm < 60; _mm += meter.getLpInterval()) {
                            _strNextFlag = BeanUtils.getProperty(lp, String.format("value_%02d", _mm));
                            if (_strNextFlag != null) {
                                _nextFlag = Double.parseDouble(_strNextFlag);
                                if (_flag == -1)
                                    _flag = _nextFlag;
                                else if (_flag != _nextFlag) {
                                    _flag = IntegratedFlag.PARTIALSENDED.getFlag();
                                    break;
                                }
                            }
                        }
                        BeanUtils.copyProperty(_lpday, "value_" + lp.getHour(), _flag);

                        _flag = -1;
                        for (int _hh = 0; _hh < 24; _hh++) {
                            _strNextFlag = BeanUtils.getProperty(_lpday, String.format("value_%02d", _hh));
                            if (_strNextFlag != null) {
                                _nextFlag = Double.parseDouble(_strNextFlag);
                                if (_flag == -1)
                                    _flag = _nextFlag;
                                else if (_flag != _nextFlag) {
                                    _flag = IntegratedFlag.PARTIALSENDED.getFlag();
                                    break;
                                }
                            }
                        }
                        _lpday.setTotal(_flag);
                    }
                    else if (_lpday.getChannel().equals(ElectricityChannel.PowerFactor.getChannel())) {
                        // Power Factory 계산은 두 채널을 이용하여 재계산하여야 하므로 루프로 처리할 수 없다.
                    }
                    else {
                        double _sum = 0.0;
                        double _sumcount = 0.0;
                        double _maxValue = 0.0;
                        double _doubleValue = 0.0;
                        for (int _mm = 0; _mm < 60; _mm+=meter.getLpInterval()) {
                            _lpValue = BeanUtils.getProperty(lp, String.format("value_%02d", _mm));
                            if (_lpValue != null) {
                                _doubleValue = Double.parseDouble(_lpValue);
                                _sum += _doubleValue;
                                if (_maxValue < _doubleValue) _maxValue = _doubleValue;
                                _sumcount++;
                            }
                        }
                        // 채널의 계산방법을 적용한다. by elevas, 2012.06.12
                        switch (getChMethod(channels, _lpday.getChannel())) {
                        case SUM : 
                            BeanUtils.copyProperty(_lpday, "value_"+ lp.getHour(), dformat(_sum));
                            break;
                        case AVG :
                            BeanUtils.copyProperty(_lpday, "value_"+ lp.getHour(), dformat(_sum / _sumcount));
                            break;
                        case MAX :
                            BeanUtils.copyProperty(_lpday, "value_"+ lp.getHour(), _maxValue);
                        }
                            
                        // LP 00시 value가 Day의 base값이 된다.
                        if (lp.getHour().equals("00")) {
                            _lpday.setBaseValue(lp.getValue());
                        }

                        _doubleValue = 0.0;
                        _maxValue = 0.0;
                        _sum = 0.0;
                        _sumcount = 0.0;
                        for (int _hh = 0; _hh < 24; _hh++) {
                            _lpValue = BeanUtils.getProperty(_lpday, String.format("value_%02d", _hh));
                            if (_lpValue != null) {
                                _doubleValue = Double.parseDouble(_lpValue); 
                                _sum += _doubleValue;
                                if (_maxValue < _doubleValue) _maxValue = _doubleValue;
                                _sumcount++;
                            }
                        }
                        switch (getChMethod(channels, _lpday.getChannel())) {
                        case SUM :
                            _lpday.setTotal(dformat(_sum));
                            break;
                        case AVG :
                            _lpday.setTotal(dformat(_sum / _sumcount));
                            break;
                        case MAX :
                            _lpday.setTotal(dformat(_maxValue));
                        }
                    }
                    _chDays[ch].put(_lpday.getId(), _lpday);
                    log.debug("MDevId[" + mdevId + "] DAY CH[" + _lpday.getChannel() + "] YYYYMMDD[" +
                            _lpday.getYyyymmdd() + "] TOTAL[" + _lpday.getTotal() + "]");
                }
            }
        }
        // 날짜나 DST가 다르면 등록, 업데이트를 확인하여 리스트에 추가하도록 되어 있는데
        // 마지막으로 처리한 것은 루프 종료 후에 처리해야 한다.
        for (int ch = 0; ch < chLPs.length; ch++) {
            if (dayUpdated) {
                if (updateDays[ch] == null) {
                    updateDays[ch] = new ArrayList<MeteringDay>();
                }
                updateDays[ch].add(_chDays[ch].get(0));
            }
            else {
                if (addDays[ch] == null) {
                    addDays[ch] = new ArrayList<MeteringDay>();
                }
                addDays[ch].add(_chDays[ch].get(0));
            }
        }

        List<MeteringMonth>[] addMonths = new ArrayList[chLPs.length];
    }
    
    private void _saveLPData(MeteringType meteringType, List<MeteringLP>[] chLPs, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId) throws Exception
    {
        MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());
        // 미터의 채널 정보를 가져온다.
        ChannelConfig[] channels = null;
        if (meter.getModel() != null && meter.getModel().getDeviceConfig() != null)
            channels = ((MeterConfig)meter.getModel().getDeviceConfig()).getChannels().toArray(new ChannelConfig[0]);
        else channels = new ChannelConfig[0];
        
        if (chLPs[1].size() == 0) {
            throw new Exception("LP size is 0!!!");
        }

        // 날짜별로 LP를 조회한다.
        MeteringLP lp = chLPs[1].get(0);
        MeteringLP _lp = null;

        Map<LpPk, MeteringLP>[] _chLPs = null;
        Map<DayPk, MeteringDay>[] _chDays = null;

        Map<LpPk, MeteringLP>[] addLPs = new HashMap[chLPs.length];
        Map<LpPk, MeteringLP>[] updateLPs = new HashMap[chLPs.length];
        Map<DayPk, MeteringDay>[] addDays = new HashMap[chLPs.length];
        Map<DayPk, MeteringDay>[] updateDays = new HashMap[chLPs.length];

        String lpValue = null;
        String _lpValue = null;
        int valueCnt = 0;
        
        // 전 채널 검침데이타를 가져온다.
        LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
        condition.add(new Condition("id.yyyymmddhh", new Object[]{chLPs[1].get(0).getYyyymmddhh(),
                chLPs[1].get(chLPs[1].size()-1).getYyyymmddhh()}, null, Restriction.BETWEEN));
        condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
        condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));
        condition.add(new Condition("id.dst", new Object[]{chLPs[1].get(0).getDst()}, null, Restriction.EQ));
        
        // 주기 채널별로 검침데이타를 분리한다.
        _chLPs = listLP(condition, meterType, chLPs.length);
        
        condition = new LinkedHashSet<Condition>();
        condition.add(new Condition("id.yyyymmdd", new Object[]{chLPs[1].get(0).getYyyymmdd(),
                chLPs[1].get(chLPs[1].size()-1).getYyyymmdd()}, null, Restriction.BETWEEN));
        condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
        condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));
        condition.add(new Condition("id.dst", new Object[]{chLPs[1].get(0).getDst()}, null, Restriction.EQ));
        
        
        // 일 채널별로 검침데이타를 분리한다.
        _chDays = listDay(condition, meterType, chLPs.length);

        log.debug("MDevId[" + mdevId + "] _chLPs[" + _chLPs.length + "] _chDays[" + _chDays.length + "]");
        
        // lp 주기 합산값을 day의 시간 값에 대입한다.
        MeteringDay _lpday = null;
        boolean needUpdate = false;
        String str_mm = "";
        
        log.info("MDevId[" + mdevId + "] ch count[" + chLPs.length + "] ch[1] count[" + chLPs[1].size() + "]");
        for (int lpcnt = 0; lpcnt < chLPs[1].size();lpcnt++) {
            lp = chLPs[1].get(lpcnt);

            needUpdate = false;
            // value cnt가 lp 주기 개수만큼 채워져 있으면 건너뛴다.
            if (_chLPs.length != 0 && _chLPs[1].size() != 0 && (_lp=_chLPs[1].get(lp.getId())) != null) {
                // 같은 일에 대하여 시간에 대한 주기값이 존재하는지 검사하여 value cnt가 주기 개수만큼 있으면 건너뛴다.
                // 전체 검침데이타를 갱신하고 싶으면 이 부분을 주석처리하거나 프로퍼티로 설정할 수 있도록 한다.

                // 2012.10.16 추가. Normal 인 경우만 업데이트하지 않는다. 
                if (_lp.getValueCnt() == (60/meter.getLpInterval()) && meteringType.getType() == MeteringType.Normal.getType()) {
                    continue;
                }
                
                for (int ch = 0; ch < chLPs.length; ch++) {
                    needUpdate = false;
                    
                    if (updateLPs[ch] == null)
                        updateLPs[ch] = new HashMap<LpPk, MeteringLP>();
                    
                    lp = chLPs[ch].get(lpcnt);
                    _lp = _chLPs[ch].get(lp.getId());
                    if (_lp != null) {
                        _lp.setWriteDate(lp.getWriteDate());
                        needUpdate = true;
                    }
                    else {
                        _lp = lp;
                        needUpdate = false;
                    }

                    // base pulse 값이 db에 있는 것이 크면 변경한다.
                    // log.debug("ch[" + lp.getChannel() + "] lp value[" + lp.getValue() + "] _lp value[" + _lp.getValue() + "]");
                    if (lp.getValue() != null)
                        _lp.setValue(dformat(lp.getValue()));

                    // 널이 아닌 주기값을 lp에 넣는다.
                    if (meter.getLpInterval() == null || meter.getLpInterval() == 0)
                        throw new Exception("LP Interval["+meter.getLpInterval()+"] is 0 or null");
                    
                    for (int _mm = 0; _mm < 60; _mm+=meter.getLpInterval()) {
                        str_mm = String.format("value_%02d", _mm);
                        _lpValue = BeanUtils.getProperty(_lp, str_mm);
                        lpValue = BeanUtils.getProperty(lp, str_mm);
                        
                        // log.debug("CH[" + ch + "] MM[" + _mm + "] _LPVALUE[" + _lpValue + "] LPVALUE[" + lpValue + "]");
                        if (lpValue != null) {
                            // TODO _lpValue가 널이면 값을 채우게 되는데 널은 아닌데 값이 다르다면
                            // 사용량이 이상한 것이므로 검침데이타가 깨졌을 수도 있다. 따라서, 이벤트 전송.
                            if (_lpValue != null) {
                            }
                            BeanUtils.copyProperty(_lp, str_mm, dformat(Double.parseDouble(lpValue)));
                        }
                    }
                    
                    // check ip count
                    valueCnt = 0;
                    for (int _mm = 0; _mm < 60; _mm+=meter.getLpInterval()) {
                        _lpValue = BeanUtils.getProperty(_lp, String.format("value_%02d", _mm));
                        if (_lpValue != null && !"".equals(_lpValue))
                            valueCnt++;
                    }
                    _lp.setValueCnt(valueCnt);
                    if (_lp.getFullLocation() == null || "".equals(_lp.getFullLocation()))
                        _lp.setFullLocation(getFullLocation(meter.getLocation()));
                    
                    chLPs[ch].set(lpcnt, _lp);
                    updateLPs[ch].put(_lp.getId(), _lp);
                    
                    /*
                    if (needUpdate) {
                        Properties props = new Properties();
                        props.setProperty("mdevId", mdevId);
                        if (_lp instanceof LpEM)
                            lpEMDao.update((LpEM)_lp, props);
                        else if (_lp instanceof LpGM)
                            lpGMDao.update((LpGM)_lp, props);
                        else if (_lp instanceof LpHM)
                            lpHMDao.update((LpHM)_lp, props);
                        else if (_lp instanceof LpWM)
                            lpWMDao.update((LpWM)_lp, props);
                        else if (_lp instanceof LpSPM)
                            lpSPMDao.update((LpSPM)_lp, props);
                    }
                    else {
                        if (_lp instanceof LpEM)
                            lpEMDao.add((LpEM)_lp);
                        else if (_lp instanceof LpGM)
                            lpGMDao.add((LpGM)_lp);
                        else if (_lp instanceof LpHM)
                            lpHMDao.add((LpHM)_lp);
                        else if (_lp instanceof LpWM)
                            lpWMDao.add((LpWM)_lp);
                        else if (_lp instanceof LpSPM)
                            lpSPMDao.add((LpSPM)_lp);
                    }
                    */
                }
            }
            else {
                for (int ch = 0; ch < chLPs.length; ch++) {
                    if (addLPs[ch] == null)
                        addLPs[ch] = new HashMap<LpPk, MeteringLP>();
                    
                    _lp = chLPs[ch].get(lpcnt);
                    valueCnt = 0;
                    for (int _mm = 0; _mm < 60; _mm+=meter.getLpInterval()) {
                        _lpValue = BeanUtils.getProperty(_lp, String.format("value_%02d", _mm));
                        if (_lpValue != null && !"".equals(_lpValue))
                            valueCnt++;
                    }
                    _lp.setValueCnt(valueCnt);
                    if (_lp.getFullLocation() == null || "".equals(_lp.getFullLocation()))
                        _lp.setFullLocation(getFullLocation(meter.getLocation()));
                    
                    chLPs[ch].set(lpcnt, _lp);
                    addLPs[ch].put(_lp.getId(), _lp);
                    
                    /*
                    if (_lp instanceof LpEM)
                        lpEMDao.add((LpEM)_lp);
                    else if (_lp instanceof LpGM)
                        lpGMDao.add((LpGM)_lp);
                    else if (_lp instanceof LpHM)
                        lpHMDao.add((LpHM)_lp);
                    else if (_lp instanceof LpWM)
                        lpWMDao.add((LpWM)_lp);
                    else if (_lp instanceof LpSPM)
                        lpSPMDao.add((LpSPM)_lp);
                    */
                }
            }
            
            // lp 개수를 파악한다.
            /*
            for (int ch = 0; ch < chLPs.length; ch++, valueCnt=0) {
                _lp = chLPs[ch].get(lpcnt);
                for (int _mm = 0; _mm < 60; _mm+=meter.getLpInterval()) {
                    _lpValue = BeanUtils.getProperty(_lp, String.format("value_%02d", _mm));
                    if (_lpValue != null && !"".equals(_lpValue))
                        valueCnt++;
                }
                _lp.setValueCnt(valueCnt);
                if (_lp.getFullLocation() == null || "".equals(_lp.getFullLocation()))
                    _lp.setFullLocation(getFullLocation(meter.getLocation()));
                chLPs[ch].set(lpcnt, _lp);
            }
            */
            
            // DAY를 생성하거나 업데이트
            // DAY의 시간값을 구하기 위해 LP의 주기값을 합산한다.
            DayPk dayPk = null;
            for (int ch = 0; ch < chLPs.length; ch++) {
                needUpdate = true;
                lp = chLPs[ch].get(lpcnt);
                dayPk = new DayPk();
                dayPk.setChannel(lp.getChannel());
                dayPk.setDst(lp.getDst());
                dayPk.setMDevId(lp.getMDevId());
                dayPk.setMDevType(lp.getMDevType().name());
                dayPk.setYyyymmdd(lp.getYyyymmdd());
                _lpday = _chDays[ch].get(dayPk);
                
                if (addDays[ch] == null)
                    addDays[ch] = new HashMap<DayPk, MeteringDay>();
                if (updateDays[ch] == null) 
                    updateDays[ch] = new HashMap<DayPk, MeteringDay>();
                
                _lpday = addDays[ch].get(dayPk);
                if (_lpday == null)
                    _lpday = updateDays[ch].get(dayPk);
                if (_lpday == null)
                    _lpday = _chDays[ch].get(dayPk);
                
                if (_lpday == null)
                {
                    needUpdate = false;
                    _lpday = newMeteringDay(meteringType, meterType,
                            meter, deviceType, deviceId, mdevType, mdevId);
                    
                    // _chDays[ch] = new HashMap<String, MeteringDay>();
                    _lpday.setChannel(lp.getChannel());
                    // base value가 채널 개수만큼 존재할 때 chLPs의 채널개수는 3개가 더 늘어나므로 그 개수만큼 늘려준다.
                    if (ch == 0|| ch==chLPs.length-2 || ch==chLPs.length-1)
                        _lpday.setBaseValue(0.0);
                    else
                        _lpday.setBaseValue(lp.getValue());
                    _lpday.setYyyymmdd(lp.getYyyymmdd());
                    _lpday.setDayType(getDayType(lp.getYyyymmdd()));
                    _lpday.setDst(lp.getDst());
                }

                if (_lpday.getFullLocation() == null || "".equals(_lpday.getFullLocation()))
                    _lpday.setFullLocation(getFullLocation(meter.getLocation()));
                
                if (_lpday.getContract() == null || _lpday.getSic() == null) {
                    if (meter.getContract() != null) {
                        _lpday.setContract(meter.getContract());
                        if (meter.getContract().getSic() != null)
                            _lpday.setSic(meter.getContract().getSic().getCode());
                    }
                }
                
                if (_lpday.getChannel().equals(ElectricityChannel.ValidationStatus.getChannel())) {
                    double _flag = 0;
                    double _nextFlag = 0;
                    String _strNextFlag = null;
                    for (int _mm = 0; _mm < 60; _mm += meter.getLpInterval()) {
                        _strNextFlag = BeanUtils.getProperty(lp, String.format("value_%02d", _mm));
                        if (_strNextFlag != null) {
                            _nextFlag = Double.parseDouble(_strNextFlag);
                            if (_flag < _nextFlag)
                                _flag = _nextFlag;
                        }
                    }
                    BeanUtils.copyProperty(_lpday, "value_" + lp.getHour(), _flag);

                    for (int _hh = 0; _hh < 24; _hh++) {
                        _strNextFlag = BeanUtils.getProperty(_lpday, String.format("value_%02d", _hh));
                        if (_strNextFlag != null) {
                            _nextFlag = Double.parseDouble(_strNextFlag);
                            if (_flag < _nextFlag)
                                _flag = _nextFlag;
                        }
                    }
                    _lpday.setTotal(_flag);
                }
                else if (_lpday.getChannel().equals(ElectricityChannel.Integrated.getChannel())) {
                    double _flag = -1;
                    double _nextFlag = 0;
                    String _strNextFlag = null;
                    for (int _mm = 0; _mm < 60; _mm += meter.getLpInterval()) {
                        _strNextFlag = BeanUtils.getProperty(lp, String.format("value_%02d", _mm));
                        if (_strNextFlag != null) {
                            _nextFlag = Double.parseDouble(_strNextFlag);
                            if (_flag == -1)
                                _flag = _nextFlag;
                            else if (_flag != _nextFlag) {
                                _flag = IntegratedFlag.PARTIALSENDED.getFlag();
                                break;
                            }
                        }
                    }
                    BeanUtils.copyProperty(_lpday, "value_" + lp.getHour(), _flag);

                    _flag = -1;
                    for (int _hh = 0; _hh < 24; _hh++) {
                        _strNextFlag = BeanUtils.getProperty(_lpday, String.format("value_%02d", _hh));
                        if (_strNextFlag != null) {
                            _nextFlag = Double.parseDouble(_strNextFlag);
                            if (_flag == -1)
                                _flag = _nextFlag;
                            else if (_flag != _nextFlag) {
                                _flag = IntegratedFlag.PARTIALSENDED.getFlag();
                                break;
                            }
                        }
                    }
                    _lpday.setTotal(_flag);
                }
                else if (_lpday.getChannel().equals(ElectricityChannel.PowerFactor.getChannel())) {
                    // Power Factory 계산은 두 채널을 이용하여 재계산하여야 하므로 루프로 처리할 수 없다.
                }
                else {
                    double _sum = 0.0;
                    double _sumcount = 0.0;
                    double _maxValue = 0.0;
                    double _doubleValue = 0.0;
                    for (int _mm = 0; _mm < 60; _mm+=meter.getLpInterval()) {
                        _lpValue = BeanUtils.getProperty(lp, String.format("value_%02d", _mm));
                        if (_lpValue != null) {
                            _doubleValue = Double.parseDouble(_lpValue);
                            _sum += _doubleValue;
                            if (_maxValue < _doubleValue) _maxValue = _doubleValue;
                            _sumcount++;
                        }
                    }
                    // 채널의 계산방법을 적용한다. by elevas, 2012.06.12
                    switch (getChMethod(channels, _lpday.getChannel())) {
                    case SUM : 
                        BeanUtils.copyProperty(_lpday, "value_"+ lp.getHour(), dformat(_sum));
                        break;
                    case AVG :
                        _sumcount = _sumcount == 0 ? 1 : _sumcount; // 0일경우 에러나는것 방지. 
                        BeanUtils.copyProperty(_lpday, "value_"+ lp.getHour(), dformat(_sum / _sumcount));
                        break;
                    case MAX :
                        BeanUtils.copyProperty(_lpday, "value_"+ lp.getHour(), dformat(_maxValue));
                    }
                        
                    // LP 00시 value가 Day의 base값이 된다.
                    if (lp.getHour().equals("00")) {
                        _lpday.setBaseValue(lp.getValue());
                    }

                    _sum = 0.0;
                    _sumcount = 0.0;
                    _doubleValue = 0.0;
                    _maxValue = 0.0;
                    for (int _hh = 0; _hh < 24; _hh++) {
                        _lpValue = BeanUtils.getProperty(_lpday, String.format("value_%02d", _hh));
                        if (_lpValue != null) {
                            _doubleValue = Double.parseDouble(_lpValue);
                            _sum += _doubleValue;
                            if (_maxValue < _doubleValue) _maxValue = _doubleValue;
                            _sumcount++;
                        }
                    }
                    switch (getChMethod(channels, _lpday.getChannel())) {
                    case SUM :
                        _lpday.setTotal(dformat(_sum));
                        break;
                    case AVG :
                        _sumcount = _sumcount == 0 ? 1 : _sumcount; // 0일경우 에러나는것 방지.
                        _lpday.setTotal(dformat(_sum / _sumcount));
                        break;
                    case MAX :
                        _lpday.setTotal(dformat(_maxValue));
                    }
                }
                _chDays[ch].put(_lpday.getId(), _lpday);
                
                if (addDays[ch].containsKey(_lpday.getId()))
                    needUpdate = false;
                
                if (!needUpdate) {
                    log.debug("ADD CH[" + ch + "] MDEV_ID[" + _lpday.getMDevId() + "] CH[" + _lpday.getChannel() + "] YYYYMMDD[" + _lpday.getYyyymmdd() + "]");
                    addDays[ch].put(_lpday.getId(), _lpday);
                    
                    /*
                    if (_lpday instanceof DayEM)
                        dayEMDao.add((DayEM)_lpday);
                    else if (_lpday instanceof DayWM)
                        dayWMDao.add((DayWM)_lpday);
                    else if (_lpday instanceof DayGM)
                        dayGMDao.add((DayGM)_lpday);
                    else if (_lpday instanceof DayHM)
                        dayHMDao.add((DayHM)_lpday);
                    else if (_lpday instanceof DaySPM)
                        daySPMDao.add((DaySPM)_lpday);
                    */
                }
                else {
                    log.debug("UPDATE CH[" + ch + "] MDEV_ID[" + _lpday.getMDevId() + "] CH[" + _lpday.getChannel() + "] YYYYMMDD[" + _lpday.getYyyymmdd() + "]");
                    updateDays[ch].put(_lpday.getId(), _lpday);
                    
                    /*
                    if (_lpday instanceof DayEM)
                        dayEMDao.update((DayEM)_lpday);
                    else if (_lpday instanceof DayWM)
                        dayWMDao.update((DayWM)_lpday);
                    else if (_lpday instanceof DayGM)
                        dayGMDao.update((DayGM)_lpday);
                    else if (_lpday instanceof DayHM)
                        dayHMDao.update((DayHM)_lpday);
                    else if (_lpday instanceof DaySPM)
                        daySPMDao.update((DaySPM)_lpday);
                    */
                }
                
                // log.info("DAY MDEV_ID[" + _lpday.getMDevId() + "] CH[" + _lpday.getChannel() + "] YYYYMMDD[" +
                //        _lpday.getYyyymmdd() + "] TOTAL[" + _lpday.getTotal() + "]");
            }
        }

        List<MeteringMonth>[] addMonths = new ArrayList[chLPs.length];
        List<MeteringMonth>[] updateMonths = new ArrayList[chLPs.length];
        makeMonthList(addDays, updateDays, addMonths, updateMonths, meteringType,
                meter, deviceType, deviceId, mdevType, mdevId);

        addMeteringData(meterType, addLPs, addDays, addMonths);
        updateMeteringData(mdevId, meterType, updateLPs, updateDays, updateMonths);
    }
    
    private void _saveLPDataNoDayMonth(MeteringType meteringType, List<MeteringLP>[] chLPs, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId) throws Exception
    {
        MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());
        // 미터의 채널 정보를 가져온다.
        ChannelConfig[] channels = null;
        if (meter.getModel() != null && meter.getModel().getDeviceConfig() != null)
            channels = ((MeterConfig)meter.getModel().getDeviceConfig()).getChannels().toArray(new ChannelConfig[0]);
        else channels = new ChannelConfig[0];
        
        if (chLPs[1].size() == 0) {
            throw new Exception("LP size is 0!!!");
        }

        // 날짜별로 LP를 조회한다.
        MeteringLP lp = chLPs[1].get(0);
        MeteringLP _lp = null;

        Map<LpPk, MeteringLP>[] _chLPs = null;

        Map<LpPk, MeteringLP>[] addLPs = new HashMap[chLPs.length];
        Map<LpPk, MeteringLP>[] updateLPs = new HashMap[chLPs.length];

        String lpValue = null;
        String _lpValue = null;
        int valueCnt = 0;
        
        // 전 채널 검침데이타를 가져온다.
        LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
        condition.add(new Condition("id.yyyymmddhh", new Object[]{chLPs[1].get(0).getYyyymmddhh(),
                chLPs[1].get(chLPs[1].size()-1).getYyyymmddhh()}, null, Restriction.BETWEEN));
        condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
        condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));
        condition.add(new Condition("id.dst", new Object[]{chLPs[1].get(0).getDst()}, null, Restriction.EQ));
        
        // 주기 채널별로 검침데이타를 분리한다.
        _chLPs = listLP(condition, meterType, chLPs.length);
        
        String str_mm = "";
        
        log.info("MDevId[" + mdevId + "] ch count[" + chLPs.length + "] ch[1] count[" + chLPs[1].size() + "]");
        for (int lpcnt = 0; lpcnt < chLPs[1].size();lpcnt++) {
            lp = chLPs[1].get(lpcnt);

            // value cnt가 lp 주기 개수만큼 채워져 있으면 건너뛴다.
            if (_chLPs.length != 0 && _chLPs[1].size() != 0 && (_lp=_chLPs[1].get(lp.getId())) != null) {
                // 같은 일에 대하여 시간에 대한 주기값이 존재하는지 검사하여 value cnt가 주기 개수만큼 있으면 건너뛴다.
                // 전체 검침데이타를 갱신하고 싶으면 이 부분을 주석처리하거나 프로퍼티로 설정할 수 있도록 한다.

                // 2012.10.16 추가. Normal 인 경우만 업데이트하지 않는다. 
                if (_lp.getValueCnt() == (60/meter.getLpInterval()) && meteringType.getType() == MeteringType.Normal.getType()) {
                    continue;
                }
                
                for (int ch = 0; ch < chLPs.length; ch++) {
                    
                    if (updateLPs[ch] == null)
                        updateLPs[ch] = new HashMap<LpPk, MeteringLP>();
                    
                    lp = chLPs[ch].get(lpcnt);
                    _lp = _chLPs[ch].get(lp.getId());
                    if (_lp != null) {
                        _lp.setWriteDate(lp.getWriteDate());
                    }
                    else {
                        _lp = lp;
                    }

                    // base pulse 값이 db에 있는 것이 크면 변경한다.
                    // log.debug("ch[" + lp.getChannel() + "] lp value[" + lp.getValue() + "] _lp value[" + _lp.getValue() + "]");
                    if (lp.getValue() != null)
                        _lp.setValue(dformat(lp.getValue()));

                    // 널이 아닌 주기값을 lp에 넣는다.
                    if (meter.getLpInterval() == null || meter.getLpInterval() == 0)
                        throw new Exception("LP Interval["+meter.getLpInterval()+"] is 0 or null");
                    
                    for (int _mm = 0; _mm < 60; _mm+=meter.getLpInterval()) {
                        str_mm = String.format("value_%02d", _mm);
                        _lpValue = BeanUtils.getProperty(_lp, str_mm);
                        lpValue = BeanUtils.getProperty(lp, str_mm);
                        
                        // log.debug("CH[" + ch + "] MM[" + _mm + "] _LPVALUE[" + _lpValue + "] LPVALUE[" + lpValue + "]");
                        if (lpValue != null) {
                            // TODO _lpValue가 널이면 값을 채우게 되는데 널은 아닌데 값이 다르다면
                            // 사용량이 이상한 것이므로 검침데이타가 깨졌을 수도 있다. 따라서, 이벤트 전송.
                            if (_lpValue != null) {
                            }
                            BeanUtils.copyProperty(_lp, str_mm, dformat(Double.parseDouble(lpValue)));
                        }
                    }
                    
                    // check ip count
                    valueCnt = 0;
                    for (int _mm = 0; _mm < 60; _mm+=meter.getLpInterval()) {
                        _lpValue = BeanUtils.getProperty(_lp, String.format("value_%02d", _mm));
                        if (_lpValue != null && !"".equals(_lpValue))
                            valueCnt++;
                    }
                    _lp.setValueCnt(valueCnt);
                    if (_lp.getFullLocation() == null || "".equals(_lp.getFullLocation()))
                        _lp.setFullLocation(getFullLocation(meter.getLocation()));
                    
                    chLPs[ch].set(lpcnt, _lp);
                    updateLPs[ch].put(_lp.getId(), _lp);
                }
            }
            else {
                for (int ch = 0; ch < chLPs.length; ch++) {
                    if (addLPs[ch] == null)
                        addLPs[ch] = new HashMap<LpPk, MeteringLP>();
                    
                    _lp = chLPs[ch].get(lpcnt);
                    valueCnt = 0;
                    for (int _mm = 0; _mm < 60; _mm+=meter.getLpInterval()) {
                        _lpValue = BeanUtils.getProperty(_lp, String.format("value_%02d", _mm));
                        if (_lpValue != null && !"".equals(_lpValue))
                            valueCnt++;
                    }
                    _lp.setValueCnt(valueCnt);
                    if (_lp.getFullLocation() == null || "".equals(_lp.getFullLocation()))
                        _lp.setFullLocation(getFullLocation(meter.getLocation()));
                    
                    chLPs[ch].set(lpcnt, _lp);
                    addLPs[ch].put(_lp.getId(), _lp);
                }
            }
        }

        addMeteringData(meterType, addLPs);
        updateMeteringData(mdevId, meterType, updateLPs);
    }
    
    protected void saveOverridenLpData(MeteringType meteringType, String[] lptimelist,
            double[][] lplist, int[] flaglist, double[] baseValue, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    throws Exception
    {
        log.info("Meter Serial:"+meter.getMdsId()+" , Lp Date_Time:"+lptimelist[0]+", baseValue_cnt:"+baseValue.length+" ");
        MeteringDataEM mdem = new MeteringDataEM();
        mdem.setMDevType(mdevType.name());
        mdem.setMDevId(mdevId);
        mdem.setSupplier(meter.getSupplier());
        mdem.setYyyymmddhhmmss(lptimelist[0]+"00");
        mdem.setDst(0);
        
        Set<Condition> condition = new LinkedHashSet<Condition>();
        condition.add(new Condition("id.yyyymmddhhmmss", new Object[]{mdem.getYyyymmddhhmmss()}, null, Restriction.EQ));
        condition.add(new Condition("id.dst", new Object[]{mdem.getDst()}, null, Restriction.EQ));
        condition.add(new Condition("id.mdevId", new Object[]{mdem.getMDevId()}, null, Restriction.EQ));
        condition.add(new Condition("id.mdevType", new Object[]{mdem.getMDevType()}, null, Restriction.EQ));
        
        List<MeteringData> mdlist = meteringDataDao.findByConditions(condition);
        if (mdlist == null || mdlist.size() == 0) {
            mdem.setValue(1.0);
            meteringDataDao.add(mdem);
        }
        else {
            mdem = (MeteringDataEM)mdlist.get(0);
            mdem.setValue(mdem.getValue() + 1);
            meteringDataDao.update(mdem);
        }
        
        return;
        
        /*
        List<MeteringLP>[] chLPs = makeChLPsUsingLPTime(meteringType, lptimelist,  lplist, flaglist,
                baseValue, meter, deviceType, deviceId, mdevType, mdevId);

        _saveLPData(meteringType, chLPs, meter, deviceType, deviceId, mdevType, mdevId);
        */
    }
    
    // INSERT START SP-501
    // It must be Lp data at the correct interval.
    protected void saveLPDataUsingLPTime(MeteringType meteringType, String[] lptimelist,
            double[][] lplist, int[] flaglist, double[] baseValue, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    throws Exception
    {
        log.info("Meter Serial:"+meter.getMdsId()+" , Lp Date_Time:"+lptimelist[0]+", baseValue_cnt:"+baseValue.length+" ");
        List<MeteringLP>[] chLPs = makeChLPsUsingLPTime(meteringType, lptimelist,  lplist, flaglist,
                baseValue, meter, deviceType, deviceId, mdevType, mdevId);

        if (lpSaveOnly) {
            _saveLPDataNoDayMonth(meteringType, chLPs, meter, deviceType, deviceId, mdevType, mdevId);
        }
        else {
            _saveLPData(meteringType, chLPs, meter, deviceType, deviceId, mdevType, mdevId);
        }
    }
    
    private List<MeteringLP>[] makeChLPsUsingLPTime(MeteringType meteringType, String[] lptimelist, 
            double[][] lplist, int[] flaglist, double[] baseValue, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    throws Exception
    {
    	double lpThreshold = Double.parseDouble(FMPProperty.getProperty("lp.threshold", "300"));

        // 처음 시작하는 분을 기억하고 있다가 사용해야 한다.
        int startmm = Integer.parseInt(lptimelist[0].substring(10, 12));

        // 0 채널(탄소배출량), 98 채널(연동 전송여부), 100 채널 (플래그) 추가한다.
        List<MeteringLP>[] chLPs = new ArrayList[lplist.length+3];
        for (int ch=0; ch < chLPs.length; ch++) {
        	chLPs[ch] = new ArrayList<MeteringLP>();
        }

        // 미터의 채널 정보를 가져온다.
        ChannelConfig[] channels = null;
        if (meter.getModel() != null && meter.getModel().getDeviceConfig() != null)
            channels = ((MeterConfig)meter.getModel().getDeviceConfig()).getChannels().toArray(new ChannelConfig[0]);
        else channels = new ChannelConfig[0];
        
        int _dst = 0;
        String _dsttime = null;
        MeteringLP[] lps = null;
        double[] sum = baseValue;
        MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());

        int valuecnt = 0;
        int mm = startmm;
        for (int lpcnt = 0; lpcnt < lplist[0].length;) {
        	log.debug("lpcnt[" + lpcnt + "] lplist length[" + lplist[0].length + "]");
        	valuecnt = 0;
            
            // lp는 로우가 시간별로 존재하기 때문에 여기서 초기화한다.
            lps = newMeteringLP(meterType, lplist.length);
            for (int ch = 0; ch < lps.length; ch++) {
                lps[ch].setChannel(ch+1);
                lps[ch].setDeviceId(deviceId);
                lps[ch].setDeviceType(deviceType.name());
                lps[ch].setMDevId(mdevId);
                lps[ch].setMDevType(mdevType.name());
                lps[ch].setMeteringType(meteringType.getType());
                lps[ch].setValue(dformat(sum[ch]));

                if (meter.getContract() != null)
                    lps[ch].setContract(meter.getContract());

                switch (mdevType) {
                case Meter :
                    lps[ch].setMeter(meter);
                    lps[ch].setLocation(meter.getLocation());
                    lps[ch].setSupplier(meter.getSupplier());
                    if (meter.getModem() != null)
                        lps[ch].setModem(meter.getModem());
                    break;
                case Modem :
                    Modem modem = modemDao.get(mdevId);
                    lps[ch].setModem(modem);
                    lps[ch].setLocation(modem.getLocation());
                    lps[ch].setSupplier(modem.getSupplier());
                    break;
                case EndDevice :
                }
            }

            for (; mm < 60 && lplist[0].length > lpcnt; lpcnt++) {
            	mm = Integer.parseInt(lptimelist[lpcnt].substring(10, 12));
            	
            	log.debug("lpcnt[" + lpcnt + "] mm[" + mm + "] lptime[" + lptimelist[lpcnt] + "]");
            	
            	_dsttime = lptimelist[lpcnt];
            	_dst = DateTimeUtil.inDST(null, _dsttime);

            	double tmp = 0.0;
            	for (int ch = 0; ch < lps.length; ch++) {
            		// 2015.02.26 마이너스가 나오는 경우 0.0으로 처리한다.
            		tmp = dformat(lplist[ch][lpcnt]);
            		if (getChMethod(channels, lps[ch].getChannel()) == ChannelCalcMethod.SUM 
	                    && (tmp < 0.0 || tmp > lpThreshold)
	                    && meter.getModel().getName().equals(MeterVendor.KAMSTRUP.getName())) {
	                    log.warn("MDevId[" + mdevId + "] LP Value[" + tmp + "] is minus or over "+lpThreshold+"!, It is replaced to null");
	                    BeanUtils.copyProperty(lps[ch], String.format("value_%02d", mm), null);
	                    try {
	                        EventUtil.sendEvent("Meter Value Alarm",
	                                TargetClass.valueOf(meter.getMeterType().getName()),
	                                meter.getMdsId(),
	                                new String[][] {{"message", "LP DateTime[" + _dsttime + "] Value[" + tmp + "]"}}
	                                );
	                    }
	                    catch (Exception ignore) {
	                    }
            		}
            		else {
            			log.debug("copyProperty lpcnt[" + lpcnt + "] ch[" + ch + "] " + String.format("value_%02d", mm) + "[" + dformat(lplist[ch][lpcnt]) + "]");
            			BeanUtils.copyProperty(lps[ch], String.format("value_%02d", mm), dformat(lplist[ch][lpcnt]));
            			valuecnt++;
	                }

                    lps[ch].setHour(_dsttime.substring(8, 10));
                    lps[ch].setYyyymmdd(_dsttime.substring(0,8));
                    lps[ch].setYyyymmddhh(_dsttime.substring(0, 10));
                    lps[ch].setDst(_dst);
                    lps[ch].setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                    lps[ch].setValueCnt(valuecnt);
                    
                    switch (getChMethod(channels, lps[ch].getChannel())) {
                    case SUM : sum[ch] += lplist[ch][lpcnt];
                               break;
                    case MAX : sum[ch] = lplist[ch][lpcnt];
                               break;
                    case AVG : sum[ch] += lplist[ch][lpcnt];
                               sum[ch] /= lps[ch].getValueCnt();
                    }
            	}
            	
            	if (lpcnt+1 >= lplist[0].length) {
            		lpcnt++;
            		break;
            	}
            	else  {
            		int nextmm = Integer.parseInt(lptimelist[lpcnt+1].substring(10, 12));
            		if (mm >= nextmm) {
            			lpcnt++;
            			break;
            		}
            	}
            }

            for (int ch = 0; ch < lps.length; ch++) {
                if (lps[ch].getYyyymmdd() != null && lps[ch].getDst() != null) {
                    chLPs[ch+1].add(lps[ch]);
                    log.debug("MDevId[" + mdevId + "] CH[" + lps[ch].getChannel() +
                            "] YYYYMMDDHH[" + lps[ch].getYyyymmddhh() +
                            "] WRITEDATE[" + lps[ch].getWriteDate() +
                            "] DST[" + lps[ch].getDst() +
                            "] VALUE[" + lps[ch].getValue() + "]");
                }
            }
        }

        // 탄소배출량채널과 검침값상태채널을 생성한다.
        MeteringLP lp = null;
        MeteringLP[] co = new MeteringLP[chLPs[1].size()];
        MeteringLP[] flag = new MeteringLP[chLPs[1].size()];
        MeteringLP[] integratedFlag = new MeteringLP[chLPs[1].size()];
        Co2Formula co2f = co2FormulaDao.getCo2FormulaBySupplyType(meterType.getServiceType());

        double lpValue = 0.0;

        // 시작 분을 초기화한다.
        mm = startmm; // Integer.parseInt(dsttime.substring(10, 12));
        String str_mm = "";
        for (int i = 0, flagcnt = 0; i < co.length; i++) {
            lp = chLPs[1].get(i);
            co[i] = newMeteringLP(meterType, lp);
            flag[i] = newMeteringLP(meterType, lp);
            integratedFlag[i] = newMeteringLP(meterType, lp);

            co[i].setChannel(ElectricityChannel.Co2.getChannel());
            flag[i].setChannel(ElectricityChannel.ValidationStatus.getChannel());
            integratedFlag[i].setChannel(ElectricityChannel.Integrated.getChannel());

            for (; mm < 60 && flagcnt < flaglist.length;  flagcnt++) {
                mm = Integer.parseInt(lptimelist[flagcnt].substring(10, 12));
                str_mm = String.format("value_%02d", mm);
                String val = BeanUtils.getProperty(lp, str_mm);
                if(val != null && !"".equals(val)){
                    lpValue = Double.parseDouble(val);
                    BeanUtils.copyProperty(co[i], str_mm, dformat(lpValue*co2f.getCo2factor()));
                    BeanUtils.copyProperty(flag[i], str_mm, flaglist[flagcnt]);
                    BeanUtils.copyProperty(integratedFlag[i], str_mm, IntegratedFlag.NOTSENDED.getFlag());
                }

                // INSERT START SP-554
            	if (flagcnt+1 >= flaglist.length) {
            		flagcnt++;
            		break;
            	}
            	else  {
            		int nextmm = Integer.parseInt(lptimelist[flagcnt+1].substring(10, 12));
            		if (mm >= nextmm) {
            			flagcnt++;
            			break;
            		}
            	}
                // INSERT END SP-554
            }
            chLPs[0].add(co[i]);
            chLPs[chLPs.length-1].add(flag[i]);
            chLPs[chLPs.length-2].add(integratedFlag[i]);

            if (mm >= 60)
                mm = 0;
        }

        return chLPs;
    }
    // INSERT END SP-501        
    
    protected void saveLPData(MeteringType meteringType, String lpDate, String lpTime,
            double[][] lplist, int[] flaglist, double[] baseValue, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    throws Exception
    {
        log.info("Meter Serial:"+meter.getMdsId()+" , Lp Date:"+lpDate+", LP Time:"+lpTime+", baseValue_cnt:"+baseValue.length+" ");
        List<MeteringLP>[] chLPs = makeChLPs(meteringType, lpDate, lpTime,  lplist, flaglist,
                baseValue, meter, deviceType, deviceId, mdevType, mdevId);

        if (lpSaveOnly) {
            _saveLPDataNoDayMonth(meteringType, chLPs, meter, deviceType, deviceId, mdevType, mdevId);
        }
        else {
            _saveLPData(meteringType, chLPs, meter, deviceType, deviceId, mdevType, mdevId);
        }
    }

    private void makeMonthList(Map<DayPk, MeteringDay>[] addDays, Map<DayPk, MeteringDay>[] updateDays,
            List<MeteringMonth>[] addMonths, List<MeteringMonth>[] updateMonths, MeteringType meteringType,
            Meter meter, DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    throws Exception
    {
        MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());

        // 업데이트와 추가리스트의 일별 데이타를 시간별로 합산하여 MeteringMonth를 생성 또는 변경한다.

        // updateDays, addDays 에서 년월과 dst를 추출하여 리스트를 생성한다.
        List<String> yyyymmlist = new ArrayList<String>();
        makeYYYYMMDSTList(updateDays, yyyymmlist);
        makeYYYYMMDSTList(addDays, yyyymmlist);

        List<MeteringMonth>[] _chMonths = null;
        boolean monthUpdated = false;

        String _yyyymm = null;
        int _dst = 0;
        Set<Condition> condition = null;

        for (int monthcnt = 0; monthcnt < yyyymmlist.size(); monthcnt++) {
            monthUpdated = false;
            _yyyymm = yyyymmlist.get(monthcnt);
            condition = new LinkedHashSet<Condition>();
            condition.add(new Condition("id.yyyymm", new Object[]{_yyyymm}, null, Restriction.EQ));
            condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
            condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));
            condition.add(new Condition("id.dst", new Object[]{_dst}, null, Restriction.EQ));
            
            // 월 채널별로 검침데이타를 분리한다.
            _chMonths = listMonth(condition, meterType, addDays.length);

            if (_chMonths.length == 0 || (_chMonths.length !=0 && _chMonths[0].size() == 0)) {
                _chMonths = new ArrayList[addDays.length];
            }
            else {
                // 월 데이타가 있으므로 업데이트
                monthUpdated = true;
            }

            makeChMonths(_chMonths, updateDays, _yyyymm, _dst, meteringType, meterType,
                    meter, deviceType, deviceId, mdevType, mdevId);
            makeChMonths(_chMonths, addDays, _yyyymm, _dst, meteringType, meterType,
                    meter, deviceType, deviceId, mdevType, mdevId);

            log.debug("MONTH MDEV_ID[" + mdevId + "] UPDATED[" + monthUpdated + "]");
            if (!monthUpdated) {
                MeteringMonth _lpmonth = null;
                
                for (int ch = 0; ch < addDays.length; ch++) {
                    if (addMonths[ch] == null) {
                        addMonths[ch] = new ArrayList<MeteringMonth>();
                    }
                    _lpmonth = _chMonths[ch].get(0);
                    addMonths[ch].add(_lpmonth);
                    
                    /*
                    if (_lpmonth instanceof MonthEM)
                        monthEMDao.add((MonthEM)_lpmonth);
                    else if (_lpmonth instanceof MonthGM)
                        monthGMDao.add((MonthGM)_lpmonth);
                    else if (_lpmonth instanceof MonthWM)
                        monthWMDao.add((MonthWM)_lpmonth);
                    else if (_lpmonth instanceof MonthHM)
                        monthHMDao.add((MonthHM)_lpmonth);
                    else if (_lpmonth instanceof MonthSPM)
                        monthSPMDao.add((MonthSPM)_lpmonth);
                    */
                }
            }
            else {
                MeteringMonth _lpmonth = null;
                for (int ch = 0; ch < addDays.length; ch++) {
                    _lpmonth = _chMonths[ch].get(0);
                    if (updateMonths[ch] == null) 
                        updateMonths[ch] = new ArrayList<MeteringMonth>();
                    updateMonths[ch].add(_lpmonth);
                    /*
                    if (_lpmonth instanceof MonthEM)
                        monthEMDao.update((MonthEM)_lpmonth);
                    else if (_lpmonth instanceof MonthGM)
                        monthGMDao.update((MonthGM)_lpmonth);
                    else if (_lpmonth instanceof MonthWM)
                        monthWMDao.update((MonthWM)_lpmonth);
                    else if (_lpmonth instanceof MonthHM)
                        monthHMDao.update((MonthHM)_lpmonth);
                    else if (_lpmonth instanceof MonthSPM)
                        monthSPMDao.update((MonthSPM)_lpmonth);
                    */
                }
            }
        }
    }

    private void makeChMonths(List<MeteringMonth>[] chMonths, Map<DayPk, MeteringDay>[] days,
            String yyyymm, int dst, MeteringType meteringType, MeterType meterType,
            Meter meter, DeviceType deviceType, String deviceId, DeviceType mdevType,
            String mdevId)
    throws Exception
    {
        if (days[1] != null && days[1].size() > 0) {
            // 미터의 채널 정보를 가져온다.
            ChannelConfig[] channels = null;
            if (meter.getModel() != null && meter.getModel().getDeviceConfig() != null)
                channels = ((MeterConfig)meter.getModel().getDeviceConfig()).getChannels().toArray(new ChannelConfig[0]);
            else channels = new ChannelConfig[0];
            
            MeteringMonth _lpmonth = null;
            int chlen = days.length;
            double _dayValue = 0.0;
            String _strDayValue = null;

            // 채널 수로 돌린다.
            for (int ch = 0; ch < chlen; ch++) {
                for (MeteringDay _lpday : days[ch].values().toArray(new MeteringDay[0])) {
                    if (_lpday == null || (_lpday != null && !_lpday.getYyyymmdd().substring(0, 6).equals(yyyymm)))
                        continue;
                    
                    _dayValue = 0.0;
                    if (chMonths[ch] == null || chMonths[ch].size() == 0) {
                        chMonths[ch] = new ArrayList<MeteringMonth>();
                        _lpmonth = newMeteringMonth(meteringType, meterType,
                                meter, deviceType, deviceId, mdevType, mdevId);
                        _lpmonth.setChannel(_lpday.getChannel());
                        _lpmonth.setYyyymm(yyyymm);
                        // 월 검침테이블에서는 dst를 사용하지 않는다.
                        _lpmonth.setDst(0);
                        _lpmonth.setBaseValue(_lpday.getBaseValue());
                        chMonths[ch].add(_lpmonth);
                    }
                    _lpmonth = chMonths[ch].get(0);
                    _lpmonth.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                    _lpmonth.setLocation(meter.getLocation());
                    if (_lpmonth.getFullLocation() == null || "".equals(_lpmonth.getFullLocation()))
                        _lpmonth.setFullLocation(getFullLocation(meter.getLocation()));
                    
                    if (meter.getContract() != null) {
                        _lpmonth.setContract(meter.getContract());
                    }

                    if (_lpmonth.getChannel().equals(ElectricityChannel.ValidationStatus.getChannel())) {
                        String _strFlag = null;
                        double _flag = 0.0;
                        _strFlag = BeanUtils.getProperty(_lpmonth,
                                "value_"+_lpday.getYyyymmdd().substring(6,8));
                        if (_strFlag != null) {
                            _flag = Double.parseDouble(_strFlag);

                            if (_flag < _lpday.getTotal()) {
                                BeanUtils.copyProperty(_lpmonth,
                                        "value_"+_lpday.getYyyymmdd().substring(6,8), dformat(_lpday.getTotal()));
                                _lpmonth.setTotal(dformat(_lpday.getTotal()));
                            }
                        }
                        else {
                            BeanUtils.copyProperty(_lpmonth,
                                    "value_"+_lpday.getYyyymmdd().substring(6,8), dformat(_lpday.getTotal()));
                            if (_lpmonth.getTotal() == null)
                                _lpmonth.setTotal(dformat(_lpday.getTotal()));
                        }
                    }
                    else if (_lpmonth.getChannel().equals(ElectricityChannel.Integrated.getChannel())) {
                        BeanUtils.copyProperty(_lpmonth,
                                "value_"+_lpday.getYyyymmdd().substring(6,8), dformat(_lpday.getTotal()));
                        _lpmonth.setTotal(dformat(_lpday.getTotal()));
                    }
                    else {
                        _strDayValue = BeanUtils.getProperty(_lpmonth,
                                "value_"+_lpday.getYyyymmdd().substring(6,8));
                        if (_strDayValue != null)
                            _dayValue = Double.parseDouble(_strDayValue);

                        // 매월 1일 base값이 월 base값이 된다.
                        if (_lpday.getYyyymmdd().substring(6,8).equals("01")) {
                            _lpmonth.setBaseValue(_lpday.getBaseValue());
                        }

                        if (_strDayValue == null || _dayValue != _lpday.getTotal()) {
                            BeanUtils.copyProperty(_lpmonth,
                                    "value_"+_lpday.getYyyymmdd().substring(6,8), dformat(_lpday.getTotal()));
                            
                            switch (getChMethod(channels, _lpmonth.getChannel())) {
                            case SUM :
                                if (_lpmonth.getTotal() != null)
                                    _lpmonth.setTotal(dformat(_lpmonth.getTotal() - _dayValue + _lpday.getTotal()));
                                else
                                    _lpmonth.setTotal(dformat(_lpday.getTotal()));
                                break;
                            case AVG :
                                double sum = 0.0;
                                double sumcount = 0.0;
                                int _day = Integer.parseInt(_lpday.getYyyymmdd().substring(6,8));
                                for (int i = 1; i <= _day; i++) {
                                    _strDayValue = BeanUtils.getProperty(_lpmonth, String.format("value_%02d",i));
                                    if (_strDayValue != null) {
                                        sum += Double.parseDouble(_strDayValue);
                                        sumcount++;
                                    }
                                }
                                if (sumcount != 0.0) _lpmonth.setTotal(dformat(sum / sumcount));
                                else _lpmonth.setTotal(sum);
                                break;
                            case MAX :
                                if (_lpmonth.getTotal() == null || _lpmonth.getTotal() < _lpday.getTotal())
                                    _lpmonth.setTotal(_lpday.getTotal());
                            }
                        }
                    }

                    chMonths[ch].set(0, _lpmonth);

                    log.debug("MONTH MDEV_ID[" + mdevId + "] CH[" + _lpmonth.getChannel() + "] YYYYMM[" + _lpmonth.getYyyymm() +
                            "] DST[" + _lpmonth.getDst() + "] TOTAL[" + _lpmonth.getTotal() + "]");
                }
            }
        }
    }

    private void makeYYYYMMDSTList(Map<DayPk, MeteringDay>[] daylist, List<String> yyyymmlist)
    {
        String _yyyymm = null;

        if (daylist != null && daylist[1] != null) {
            for (MeteringDay _mday : daylist[1].values().toArray(new MeteringDay[0])) {
                _yyyymm = _mday.getYyyymmdd().substring(0, 6);
                if (!yyyymmlist.contains(_yyyymm)) {
                    yyyymmlist.add(_yyyymm);
                }
                /*
                else {
                    // dst를 비교한다.
                    for (int j = 0; j < yyyymmlist.size(); j++) {
                        if (yyyymmlist.get(j).equals(_yyyymm)) {
                            if (!dstlist.get(j).equals(_dst)) {
                                yyyymmlist.add(_yyyymm);
                                dstlist.add(_dst);
                                break;
                            }
                        }
                    }
                }
                */
            }
        }
    }

    public void addMeteringData(MeterType meterType, Map<LpPk, MeteringLP>[] addLPs,
            Map<DayPk, MeteringDay>[] addDays, List<MeteringMonth>[] addMonths)
    throws Exception
    {
        MeteringMonth lpmonth = null;
        String writeTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
        
        for (int ch = 0; ch < addLPs.length; ch++) {
            if (addLPs[ch] != null) {
                for (MeteringLP lp : addLPs[ch].values().toArray(new MeteringLP[0])) {
                    lp.setWriteDate(writeTime);
                    log.debug("[ADD][LP_EM] MDEV_ID[" + lp.getMDevId() + "] CH[" + lp.getChannel() + 
                            "] YYYYMMDDHH[" + lp.getYyyymmddhh() + "] WRITEDATE[" + lp.getWriteDate() + "] DST[" + lp.getDst() + "]");
                    switch(meterType) {
                    case EnergyMeter :
                        lpEMDao.add((LpEM)lp);
                        break;
                    case WaterMeter :
                        lpWMDao.add((LpWM)lp);
                        break;
                    case GasMeter :
                        lpGMDao.add((LpGM)lp);
                        break;
                    case HeatMeter :
                        lpHMDao.add((LpHM)lp);
                        break;
                    case SolarPowerMeter :
                        lpSPMDao.add((LpSPM)lp);
                        break;
                    case Inverter :
                        lpEMDao.add((LpEM)lp);
                        break;
                    case Compensator :
                        lpEMDao.add((LpEM)lp);
                        break;                        
                    }
                }
            }
        }

        for (int ch = 0; ch < addDays.length; ch++) {
            if (addDays[ch] != null) {
                for (MeteringDay lpday : addDays[ch].values().toArray(new MeteringDay[0])) {
                    lpday.setWriteDate(writeTime);
                    
                    log.debug("[ADD][DAY_EM] MDEV_ID[" + lpday.getMDevId() + "] CH[" + lpday.getChannel() + 
                            "] YYYYMMDD[" + lpday.getYyyymmdd() + "] WRITEDATE[" + lpday.getWriteDate() + "] DST[" + lpday.getDst() + "]");
                    
                    switch(meterType) {
                    case EnergyMeter :
                        dayEMDao.add((DayEM)lpday);
                        break;
                    case WaterMeter :
                        dayWMDao.add((DayWM)lpday);
                        break;
                    case GasMeter :
                        dayGMDao.add((DayGM)lpday);
                        break;
                    case HeatMeter :
                        dayHMDao.add((DayHM)lpday);
                        break;
                    case SolarPowerMeter :
                        daySPMDao.add((DaySPM)lpday);
                        break;
                    case Inverter :
                        dayEMDao.add((DayEM)lpday);
                        break;
                    case Compensator :
                        dayEMDao.add((DayEM)lpday);
                        break;                        
                    }
                }
            }
        }

        for (int ch = 0; ch < addMonths.length; ch++) {
            if (addMonths[ch] != null) {
                for (int i = 0; i < addMonths[ch].size(); i++) {
                    lpmonth = addMonths[ch].get(i);
                    lpmonth.setWriteDate(writeTime);
                    
                    log.debug("[ADD][MONTH_EM] MDEV_ID[" + lpmonth.getMDevId() + "] CH[" + lpmonth.getChannel() + 
                            "] YYYYMM[" + lpmonth.getYyyymm() + "] WRITEDATE[" + lpmonth.getWriteDate() + "] DST[" + lpmonth.getDst() + "]");
                    
                    switch(meterType) {
                    case EnergyMeter :
                        monthEMDao.add((MonthEM)lpmonth);
                        break;
                    case WaterMeter :
                        monthWMDao.add((MonthWM)lpmonth);
                        break;
                    case GasMeter :
                        monthGMDao.add((MonthGM)lpmonth);
                        break;
                    case HeatMeter :
                        monthHMDao.add((MonthHM)lpmonth);
                        break;
                    case SolarPowerMeter :
                        monthSPMDao.add((MonthSPM)lpmonth);
                        break;
                    case Inverter :
                        monthEMDao.add((MonthEM)lpmonth);
                        break; 
                    case Compensator :
                        monthEMDao.add((MonthEM)lpmonth);
                        break;
                    }
                }
            }
        }
    }
    
    public void updateMeteringData(String mdevId, MeterType meterType, Map<LpPk, MeteringLP>[] updateLPs)
    throws Exception
    {
        MeteringMonth lpmonth = null;
        String writeTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
        Properties props = new Properties();
        props.setProperty("mdevId", mdevId);
        
        for (int ch = 0; ch < updateLPs.length; ch++) {
            if (updateLPs[ch] != null) {
                for (MeteringLP lp : updateLPs[ch].values().toArray(new MeteringLP[0])) {
                    lp.setWriteDate(writeTime);
                    log.debug("[UPDATE][LP_EM] MDEV_ID[" + lp.getMDevId() + "] CH[" + lp.getChannel() + 
                            "] YYYYMMDDHH[" + lp.getYyyymmddhh() + "] WRITEDATE[" + lp.getWriteDate() + "] DST[" + lp.getDst() + "]");
                    switch(meterType) {
                    case EnergyMeter :
                        lpEMDao.update((LpEM)lp, props);
                        break;
                    case WaterMeter :
                        lpWMDao.update((LpWM)lp, props);
                        break;
                    case GasMeter :
                        lpGMDao.update((LpGM)lp, props);
                        break;
                    case HeatMeter :
                        lpHMDao.update((LpHM)lp, props);
                        break;
                    case SolarPowerMeter :
                        lpSPMDao.update((LpSPM)lp, props);
                        break;
                    case Inverter :
                        lpEMDao.update((LpEM)lp, props);
                        break;                        
                    }
                }
            }
        }
    }
    
    public void addMeteringData(MeterType meterType, Map<LpPk, MeteringLP>[] addLPs)
    throws Exception
    {
        MeteringMonth lpmonth = null;
        String writeTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
        
        for (int ch = 0; ch < addLPs.length; ch++) {
            if (addLPs[ch] != null) {
                for (MeteringLP lp : addLPs[ch].values().toArray(new MeteringLP[0])) {
                    lp.setWriteDate(writeTime);
                    log.debug("[ADD][LP_EM] MDEV_ID[" + lp.getMDevId() + "] CH[" + lp.getChannel() + 
                            "] YYYYMMDDHH[" + lp.getYyyymmddhh() + "] WRITEDATE[" + lp.getWriteDate() + "] DST[" + lp.getDst() + "]");
                    switch(meterType) {
                    case EnergyMeter :
                        lpEMDao.add((LpEM)lp);
                        break;
                    case WaterMeter :
                        lpWMDao.add((LpWM)lp);
                        break;
                    case GasMeter :
                        lpGMDao.add((LpGM)lp);
                        break;
                    case HeatMeter :
                        lpHMDao.add((LpHM)lp);
                        break;
                    case SolarPowerMeter :
                        lpSPMDao.add((LpSPM)lp);
                        break;
                    case Inverter :
                        lpEMDao.add((LpEM)lp);
                        break;
                    case Compensator :
                    	lpEMDao.add((LpEM)lp);
                        break;                        
                    }
                }
            }
        }
    }
    
    public void updateMeteringData(String mdevId, MeterType meterType, Map<LpPk, MeteringLP>[] updateLPs,
            Map<DayPk, MeteringDay>[] updateDays, List<MeteringMonth>[] updateMonths)
    throws Exception
    {
        MeteringMonth lpmonth = null;
        String writeTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
        Properties props = new Properties();
        props.setProperty("mdevId", mdevId);
        
        for (int ch = 0; ch < updateLPs.length; ch++) {
            if (updateLPs[ch] != null) {
                for (MeteringLP lp : updateLPs[ch].values().toArray(new MeteringLP[0])) {
                    lp.setWriteDate(writeTime);
                    log.debug("[UPDATE][LP_EM] MDEV_ID[" + lp.getMDevId() + "] CH[" + lp.getChannel() + 
                            "] YYYYMMDDHH[" + lp.getYyyymmddhh() + "] WRITEDATE[" + lp.getWriteDate() + "] DST[" + lp.getDst() + "]");
                    switch(meterType) {
                    case EnergyMeter :
                        lpEMDao.update((LpEM)lp, props);
                        break;
                    case WaterMeter :
                        lpWMDao.update((LpWM)lp, props);
                        break;
                    case GasMeter :
                        lpGMDao.update((LpGM)lp, props);
                        break;
                    case HeatMeter :
                        lpHMDao.update((LpHM)lp, props);
                        break;
                    case SolarPowerMeter :
                        lpSPMDao.update((LpSPM)lp, props);
                        break;
                    case Inverter :
                        lpEMDao.update((LpEM)lp, props);
                        break;                        
                    }
                }
            }
        }

        for (int ch = 0; ch < updateDays.length; ch++) {
            if (updateDays[ch] != null) {
                for (MeteringDay lpday : updateDays[ch].values().toArray(new MeteringDay[0])) {
                    lpday.setWriteDate(writeTime);
                    
                    log.debug("[UPDATE][DAY_EM] MDEV_ID[" + lpday.getMDevId() + "] CH[" + lpday.getChannel() + 
                            "] YYYYMMDD[" + lpday.getYyyymmdd() + "] WRITEDATE[" + lpday.getWriteDate() + "] DST[" + lpday.getDst() + "]");
                    switch(meterType) {
                    case EnergyMeter :
                        dayEMDao.update((DayEM)lpday);
                        break;
                    case WaterMeter :
                        dayWMDao.update((DayWM)lpday);
                        break;
                    case GasMeter :
                        dayGMDao.update((DayGM)lpday);
                        break;
                    case HeatMeter :
                        dayHMDao.update((DayHM)lpday);
                        break;
                    case SolarPowerMeter :
                        daySPMDao.update((DaySPM)lpday);
                        break;
                    case Inverter :
                        dayEMDao.update((DayEM)lpday);
                        break;                        
                    }
                }
            }
        }

        for (int ch = 0; ch < updateMonths.length; ch++) {
            if (updateMonths[ch] != null) {
                for (int i = 0; i < updateMonths[ch].size(); i++) {
                    lpmonth = updateMonths[ch].get(i);
                    lpmonth.setWriteDate(writeTime);
                    
                    log.debug("[UPDATE][MONTH_EM] MDEV_ID[" + lpmonth.getMDevId() + "] CH[" + lpmonth.getChannel() + 
                            "] YYYYMM[" + lpmonth.getYyyymm() + "] WRITEDATE[" + lpmonth.getWriteDate() + "] DST[" + lpmonth.getDst() + "]");
                    
                    switch(meterType) {
                    case EnergyMeter :
                        monthEMDao.update((MonthEM)lpmonth);
                        break;
                    case WaterMeter :
                        monthWMDao.update((MonthWM)lpmonth);
                        break;
                    case GasMeter :
                        monthGMDao.update((MonthGM)lpmonth);
                        break;
                    case HeatMeter :
                        monthHMDao.update((MonthHM)lpmonth);
                        break;
                    case SolarPowerMeter :
                        monthSPMDao.update((MonthSPM)lpmonth);
                        break;
                    case Inverter :
                        monthEMDao.update((MonthEM)lpmonth);
                        break;                        
                    }
                }
            }
        }
    }

    private MeteringLP newMeteringLP(MeterType meterType, MeteringLP lp) {
        MeteringLP _lp = null;
        switch (meterType) {
        case EnergyMeter :
            _lp = new LpEM();
            break;
        case WaterMeter :
            _lp = new LpWM();
            break;
        case GasMeter :
            _lp = new LpGM();
            break;
        case HeatMeter :
            _lp = new LpHM();
            break;
        case SolarPowerMeter :
            _lp = new LpSPM();
            break;
        case Inverter :
            _lp = new LpEM();
            break;
        case Compensator :
            _lp = new LpEM();
            break; 
        }

        _lp.setContract(lp.getContract());
        _lp.setDeviceId(lp.getDeviceId());
        _lp.setDeviceType(lp.getDeviceType().name());
        _lp.setDst(lp.getDst());
        _lp.setEnddevice(lp.getEnddevice());
        _lp.setYyyymmdd(lp.getYyyymmdd());
        _lp.setYyyymmddhh(lp.getYyyymmddhh());
        _lp.setHour(lp.getHour());
        _lp.setLocation(lp.getLocation());
        _lp.setMDevId(lp.getMDevId());
        _lp.setMDevType(lp.getMDevType().name());
        _lp.setMeteringType(lp.getMeteringType());
        _lp.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));

        switch (lp.getMDevType()) {
        case Meter :
            _lp.setMeter(lp.getMeter());
            _lp.setSupplier(lp.getSupplier());
            if (lp.getMeter().getModem() != null)
                _lp.setModem(lp.getMeter().getModem());
            break;
        case Modem :
            _lp.setModem(lp.getModem());
            _lp.setSupplier(lp.getSupplier());
            break;
        case EndDevice :
        }

        return _lp;
    }

    private MeteringLP[] newMeteringLP(MeterType meterType, int chCnt) {
        MeteringLP[] lps = null;
        switch (meterType) {
        case EnergyMeter :
            lps = new LpEM[chCnt];
            for (int i = 0; i < chCnt; i++) {
                lps[i] = new LpEM();
            }
            break;
        case WaterMeter :
            lps = new LpWM[chCnt];
            for (int i = 0; i < chCnt; i++) {
                lps[i] = new LpWM();
            }
            break;
        case GasMeter :
            lps = new LpGM[chCnt];
            for (int i = 0; i < chCnt; i++) {
                lps[i] = new LpGM();
            }
            break;
        case HeatMeter :
            lps = new LpHM[chCnt];
            for (int i = 0; i < chCnt; i++) {
                lps[i] = new LpHM();
            }
            break;
        case SolarPowerMeter :
            lps = new LpSPM[chCnt];
            for (int i = 0; i < chCnt; i++) {
                lps[i] = new LpSPM();
            }
            break;
        case Inverter :
            lps = new LpEM[chCnt];
            for (int i = 0; i < chCnt; i++) {
                lps[i] = new LpEM();
            }
            break;
        case Compensator :
        	lps = new LpEM[chCnt];
            for (int i = 0; i < chCnt; i++) {
                lps[i] = new LpEM();
            }
            break;
        }
        return lps;
    }

    private MeteringDay newMeteringDay(MeteringType meteringType, MeterType meterType,
            Meter meter, DeviceType deviceType, String deviceId, DeviceType mdevType,
            String mdevId) {
        MeteringDay day = null;
        switch (meterType) {
        case EnergyMeter :
            day = new DayEM();
            break;
        case WaterMeter :
            day = new DayWM();
            break;
        case GasMeter :
            day = new DayGM();
            break;
        case HeatMeter :
            day = new DayHM();
            break;
        case SolarPowerMeter :
            day = new DaySPM();
            break;
        case Inverter :
            day = new DayEM();
            break;
        case Compensator :
            day = new DayEM();
            break; 
        }

        day.setDeviceId(deviceId);
        day.setDeviceType(deviceType.name());
        day.setMDevId(mdevId);
        day.setMDevType(mdevType.name());
        day.setMeteringType(meteringType.getType());
        day.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));

        if (meter.getContract() != null) {
            day.setContract(meter.getContract());
            // 2012.04.13 sic를 정보를 입력한다.
            day.setSic(meter.getContract().getSic() != null ? meter.getContract().getSic().getCode() : null);
        }

        switch (mdevType) {
        case Meter :
            day.setMeter(meter);
            day.setLocation(meter.getLocation());
            day.setSupplier(meter.getSupplier());
            if (meter.getModem() != null)
                day.setModem(meter.getModem());
            break;
        case Modem :
            day.setModem(meter.getModem());
            day.setLocation(meter.getModem().getLocation());//TODO!!!!!!!! 로직 검증 필요 미터 객체가 없을때 모뎀정보를 가져오지 못함
            day.setSupplier(meter.getSupplier());
            break;
        case EndDevice :
        }
        return day;
    }

    private MeteringMonth newMeteringMonth(MeteringType meteringType, MeterType meterType,
            Meter meter, DeviceType deviceType, String deviceId, DeviceType mdevType,
            String mdevId) {
        MeteringMonth month = null;
        switch (meterType) {
        case EnergyMeter :
            month = new MonthEM();
            break;
        case WaterMeter :
            month = new MonthWM();
            break;
        case GasMeter :
            month = new MonthGM();
            break;
        case HeatMeter :
            month = new MonthHM();
            break;
        case SolarPowerMeter :
            month = new MonthSPM();
            break;
        case Inverter :
            month = new MonthEM();
            break;
        case Compensator :
            month = new MonthEM();
            break;
        }

        month.setDeviceId(deviceId);
        month.setDeviceType(deviceType.name());
        month.setMDevId(mdevId);
        month.setMDevType(mdevType.name());
        month.setMeteringType(meteringType.getType());
        month.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));

        if (meter.getContract() != null)
            month.setContract(meter.getContract());

        switch (mdevType) {
        case Meter :
            month.setMeter(meter);
            month.setLocation(meter.getLocation());
            month.setSupplier(meter.getSupplier());
            if (meter.getModem() != null)
                month.setModem(meter.getModem());
            break;
        case Modem :
            month.setModem(meter.getModem());
            month.setLocation(meter.getModem().getLocation());
            month.setSupplier(meter.getSupplier());
            break;
        case EndDevice :
        }
        return month;
    }

    private Map<LpPk, MeteringLP>[] listLP(Set<Condition> condition, MeterType meterType, int chcnt) {
        List list = null;
        switch (meterType) {
        case EnergyMeter :
            list = lpEMDao.findByConditions(condition);
            break;
        case WaterMeter :
            list = lpWMDao.findByConditions(condition);
            break;
        case GasMeter :
            list = lpGMDao.findByConditions(condition);
            break;
        case HeatMeter :
            list = lpHMDao.findByConditions(condition);
            break;
        case VolumeCorrector :
            break;
        case SolarPowerMeter :
            list = lpSPMDao.findByConditions(condition);
            break;
        case Inverter :
            list = lpEMDao.findByConditions(condition);
            break;
        case Compensator :
            list = lpEMDao.findByConditions(condition);
            break;
        }

        /*
        List<Integer> channelCnt = new ArrayList<Integer>();
        MeteringLP lp = null;
        for (int i = 0; i < list.size(); i++) {
            lp = (MeteringLP)list.get(i);
            if (!channelCnt.contains(lp.getChannel()))
                channelCnt.add(lp.getChannel());
        }
        */
        MeteringLP lp = null;
        log.debug("LP Channel Count[" + chcnt + "]");
        Map<LpPk, MeteringLP>[] channelLP = new HashMap[chcnt];
        for (int i = 0; i < channelLP.length; i++) {
            channelLP[i] = new HashMap<LpPk, MeteringLP>();
        }
        
        Collections.sort(list, new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof LpEM) {
                    LpEM lp1 = (LpEM)o1;
                    LpEM lp2 = (LpEM)o2;
                    return lp1.getId().hashCode() - lp2.getId().hashCode();
                }
                else if (o1 instanceof LpGM) {
                    LpGM lp1 = (LpGM)o1;
                    LpGM lp2 = (LpGM)o2;
                    return lp1.getId().hashCode() - lp2.getId().hashCode();
                }
                else if (o1 instanceof LpWM) {
                    LpWM lp1 = (LpWM)o1;
                    LpWM lp2 = (LpWM)o2;
                    return lp1.getId().hashCode() - lp2.getId().hashCode();
                }
                else if (o1 instanceof LpHM) {
                    LpHM lp1 = (LpHM)o1;
                    LpHM lp2 = (LpHM)o2;
                    return lp1.getId().hashCode() - lp2.getId().hashCode();
                }
                else if (o1 instanceof LpVC) {
                    LpVC lp1 = (LpVC)o1;
                    LpVC lp2 = (LpVC)o2;
                    return lp1.getId().hashCode() - lp2.getId().hashCode();
                }
                return 0;
            }
            
        });
        
        for (int i = 0; i < list.size(); i++) {
            lp = (MeteringLP)list.get(i);
            if (lp.getChannel().equals(ElectricityChannel.ValidationStatus.getChannel())) {
                if (channelLP[channelLP.length-1] == null)
                    channelLP[channelLP.length-1] = new HashMap<LpPk, MeteringLP>();
                channelLP[channelLP.length-1].put(lp.getId(), lp);
            }
            else if (lp.getChannel().equals(ElectricityChannel.PowerFactor.getChannel())) {
                if (channelLP[channelLP.length-2] == null)
                    channelLP[channelLP.length-2] = new HashMap<LpPk, MeteringLP>();
                channelLP[channelLP.length-2].put(lp.getId(), lp);
            }
            // TODO Power Factor 채널 처리를 하면 -3으로 변경해야 함.
            else if (lp.getChannel().equals(ElectricityChannel.Integrated.getChannel())) {
                if (channelLP[channelLP.length-2] == null)
                    channelLP[channelLP.length-2] = new HashMap<LpPk, MeteringLP>();
                channelLP[channelLP.length-2].put(lp.getId(), lp);
            }
            else {
                if(channelLP.length-1 >= lp.getChannel()){
                    if (channelLP[lp.getChannel()] == null)
                        channelLP[lp.getChannel()] = new HashMap<LpPk, MeteringLP>();
                    channelLP[lp.getChannel()].put(lp.getId(), lp);
                }

            }
        }

        return channelLP;
    }

    private Map<DayPk, MeteringDay>[] listDay(Set<Condition> condition, MeterType meterType, int chcnt) {
        List list = null;
        switch (meterType) {
        case EnergyMeter :
            list = dayEMDao.findByConditions(condition);
            break;
        case WaterMeter :
            list = dayWMDao.findByConditions(condition);
            break;
        case GasMeter :
            list = dayGMDao.findByConditions(condition);
            break;
        case HeatMeter :
            list = dayHMDao.findByConditions(condition);
            break;
        case SolarPowerMeter :
            list = daySPMDao.findByConditions(condition);
            break;
        case Inverter :
            list = dayEMDao.findByConditions(condition);
            break;
        case Compensator :
            list = dayEMDao.findByConditions(condition);
            break;
        }
        /*
        List<Integer> channelCnt = new ArrayList<Integer>();
        MeteringDay mday = null;
        for (int i = 0; i < list.size(); i++) {
            mday = (MeteringDay)list.get(i);
            if (!channelCnt.contains(mday.getChannel()))
                channelCnt.add(mday.getChannel());
        }
        */
        log.debug("LP Channel Count[" + chcnt + "]");
        MeteringDay mday = null;
        Map<DayPk, MeteringDay>[] channelDay = new HashMap[chcnt];
        for (int i = 0; i < channelDay.length; i++) {
            channelDay[i] = new HashMap<DayPk, MeteringDay>();
        }
        
        for (int i = 0; i < list.size(); i++) {
            mday = (MeteringDay)list.get(i);
            log.debug("MDEV_ID[" + mday.getMDevId() + "] CH[" + mday.getChannel() + 
                    "] YYYYMMDD[" + mday.getYyyymmdd()+ "] DST[" + mday.getDst() + "]");
            if (mday.getChannel().equals(ElectricityChannel.ValidationStatus.getChannel())) {
                if (channelDay[channelDay.length-1] == null)
                    channelDay[channelDay.length-1] = new HashMap<DayPk, MeteringDay>();
                channelDay[channelDay.length-1].put(mday.getId(), mday);
            }
            else if (mday.getChannel().equals(ElectricityChannel.PowerFactor.getChannel())) {
                if (channelDay[channelDay.length-2] == null)
                    channelDay[channelDay.length-2] = new HashMap<DayPk, MeteringDay>();
                channelDay[channelDay.length-2].put(mday.getId(), mday);
            }
            // TODO Power Factor 처리가 되면 -3으로 변경해야함.
            else if (mday.getChannel().equals(ElectricityChannel.Integrated.getChannel())) {
                if (channelDay[channelDay.length-2] == null)
                    channelDay[channelDay.length-2] = new HashMap<DayPk, MeteringDay>();
                channelDay[channelDay.length-2].put(mday.getId(), mday);
            }
            else {
                
                if(channelDay.length-1 >= mday.getChannel()){
                    if (channelDay[mday.getChannel()] == null)
                        channelDay[mday.getChannel()] = new HashMap<DayPk, MeteringDay>();
                    channelDay[mday.getChannel()].put(mday.getId(), mday);
                }

            }
        }

        return channelDay;
    }

    private List<MeteringMonth>[] listMonth(Set<Condition> condition, MeterType meterType, int chcnt) {
        List list = null;
        switch (meterType) {
        case EnergyMeter :
            list = monthEMDao.findByConditions(condition);
            break;
        case WaterMeter :
            list = monthWMDao.findByConditions(condition);
            break;
        case GasMeter :
            list = monthGMDao.findByConditions(condition);
            break;
        case HeatMeter :
            list = monthHMDao.findByConditions(condition);
            break;
        case SolarPowerMeter :
            list = monthSPMDao.findByConditions(condition);
            break;
        case Inverter :
            list = monthEMDao.findByConditions(condition);
            break;
        case Compensator :
            list = monthEMDao.findByConditions(condition);
            break; 
        }

        /*
        List<Integer> channelCnt = new ArrayList<Integer>();
        MeteringMonth mmonth = null;
        for (int i = 0; i < list.size(); i++) {
            mmonth = (MeteringMonth)list.get(i);
            if (!channelCnt.contains(mmonth.getChannel()))
                channelCnt.add(mmonth.getChannel());
        }
        */
        
        log.debug("LP Channel Count[" + chcnt + "]");
        List<MeteringMonth>[] channelMonth = new ArrayList[chcnt];
        MeteringMonth mmonth = null;
        for (int i = 0; i < channelMonth.length; i++) {
            channelMonth[i] = new ArrayList<MeteringMonth>();
        }
        
        for (int i = 0; i < list.size(); i++) {
            mmonth = (MeteringMonth)list.get(i);
            if (mmonth.getChannel().equals(ElectricityChannel.ValidationStatus.getChannel())) {
                if (channelMonth[channelMonth.length-1] == null)
                    channelMonth[channelMonth.length-1] = new ArrayList<MeteringMonth>();
                channelMonth[channelMonth.length-1].add(mmonth);
            }
            else if (mmonth.getChannel().equals(ElectricityChannel.PowerFactor.getChannel())) {
                if (channelMonth[channelMonth.length-2] == null)
                    channelMonth[channelMonth.length-2] = new ArrayList<MeteringMonth>();
                channelMonth[channelMonth.length-2].add(mmonth);
            }
            // TODO Power Factor가 처리되면 -3으로 변경해야함.
            else if (mmonth.getChannel().equals(ElectricityChannel.Integrated.getChannel())) {
                if (channelMonth[channelMonth.length-2] == null)
                    channelMonth[channelMonth.length-2] = new ArrayList<MeteringMonth>();
                channelMonth[channelMonth.length-2].add(mmonth);
            }
            else {
                if(channelMonth.length-1 >= mmonth.getChannel()){
                    if (channelMonth[mmonth.getChannel()] == null)
                        channelMonth[mmonth.getChannel()] = new ArrayList<MeteringMonth>();
                    channelMonth[mmonth.getChannel()].add(mmonth);
                }

            }
        }

        return channelMonth;
    }

    /**
     * Self Read Data(for use Daily Data)
     * @param tou_block
     * @param meter
     * @param deviceType
     * @param deviceId
     * @param mdevType
     * @param mdevId
     */
    protected void saveDayBilling(TOU_BLOCK[] tou_block, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    {
        log.debug("save DailyBillingData["+meter.getMdsId());
        
        try{  

            if(tou_block == null || tou_block.length ==0 || tou_block[0] == null){
                log.debug("MDevId[" + mdevId + "] save DailyBillingData TOU data empty!");
                return;
            }
            String billtime = tou_block[0].getResetTime();
            if(billtime != null && billtime.length() == 14){

                BillingDayEM bill = new BillingDayEM();

                LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
                condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
                condition.add(new Condition("id.hhmmss", new Object[]{billtime.substring(8,14)}, null, Restriction.EQ));
                condition.add(new Condition("id.yyyymmdd", new Object[]{billtime.substring(0,8)}, null, Restriction.EQ));
                condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));
                
                List<Object> ret = billingDayEMDao.findTotalCountByConditions(condition);
                
                Long count = (Long) ret.get(0);

                // 기존에 데이터가 있을 경우는 기존 정보에 검침된 빌링 정보를 설정한다.
                if(count != null && count.longValue() > 0) {
                   return;
                } 

                /*
                // 기존에 Billing정보가 존재하는지 검색한다. ADD START 20120404 by eunmiae
                // 스케줄러에서 변경된 빌링 정보가 본 기능 실행후 NULL로 변경되는 오류 수정
                BillingDayEM bill = null;

                LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
                condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));
                condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
                condition.add(new Condition("id.yyyymmdd", new Object[]{billtime.substring(0,8)}, null, Restriction.EQ));
                condition.add(new Condition("id.hhmmss", new Object[]{billtime.substring(8,14)}, null, Restriction.EQ));

                List<BillingDayEM> billsByPk = billingDayEMDao.findByConditions(condition);

                // 기존에 데이터가 있을 경우는 기존 정보에 검침된 빌링 정보를 설정한다.
                if(billsByPk != null && billsByPk.size() != 0) {
                    bill = billsByPk.get(0);
                } else {
                    bill = new BillingDayEM();
                }
                // 기존에 Billing정보가 존재하는지 검색한다. ADD END 20120404 by eunmiae
                 * 
                 */

                if(meter.getContract() != null){
                    bill.setContract(meter.getContract());
                }
                if(meter.getSupplier() != null){
                    bill.setSupplier(meter.getSupplier());
                }
                if(meter.getLocation() != null){
                    bill.setLocation(meter.getLocation());
                }
                //bill.setBill(bill);//TODO TARIFF SET
                bill.setMDevType(mdevType.name());
                bill.setMDevId(mdevId);
                bill.setMeter(meter);
                bill.setModem(meter.getModem());
                bill.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));

                if(tou_block.length >= 1){
                    bill.setYyyymmdd(tou_block[0].getResetTime().substring(0,8));
                    bill.setHhmmss(tou_block[0].getResetTime().substring(8,14));
                    bill.setActiveEnergyRateTotal((Double)tou_block[0].getSummation(0));
                    bill.setActiveEnergyImportRateTotal((Double)tou_block[0].getSummation(0));
                    bill.setActivePowerMaxDemandRateTotal((Double)tou_block[0].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRateTotal((String)tou_block[0].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRateTotal((Double)tou_block[0].getCurrDemand(0));
                    bill.setActivePwrDmdMaxTimeImportRateTotal((String)tou_block[0].getEventTime(0));
                    bill.setCumulativeDemandRateTotal((Double)tou_block[0].getCumDemand(0));
                    bill.setReactiveEnergyRateTotal((Double)tou_block[0].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRateTotal((String)tou_block[0].getEventTime(1));
                    bill.setReactivePowerMaxDemandRateTotal((Double)tou_block[0].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRateTotal((Double)tou_block[0].getCumDemand(1));
                }
                if(tou_block.length >= 2){
                    bill.setActiveEnergyRate1((Double)tou_block[1].getSummation(0));
                    bill.setActiveEnergyImportRate1((Double)tou_block[1].getSummation(0));
                    bill.setActivePowerMaxDemandRate1((Double)tou_block[1].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate1((String)tou_block[1].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate1((Double)tou_block[1].getCurrDemand(0));
                    bill.setActivePwrDmdMaxTimeImportRate1((String)tou_block[1].getEventTime(0));
                    bill.setCumulativeDemandRate1((Double)tou_block[1].getCumDemand(0));
                    bill.setReactiveEnergyRate1((Double)tou_block[1].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate1((String)tou_block[1].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate1((Double)tou_block[1].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate1((Double)tou_block[1].getCumDemand(1));
                }

                if(tou_block.length >= 3){
                    bill.setActiveEnergyRate2((Double)tou_block[2].getSummation(0));
                    bill.setActiveEnergyImportRate2((Double)tou_block[2].getSummation(0));
                    bill.setActivePowerMaxDemandRate2((Double)tou_block[2].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate2((String)tou_block[2].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate2((Double)tou_block[2].getCurrDemand(0));
                    bill.setActivePwrDmdMaxTimeImportRate2((String)tou_block[2].getEventTime(0));
                    bill.setCumulativeDemandRate2((Double)tou_block[2].getCumDemand(0));
                    bill.setReactiveEnergyRate2((Double)tou_block[2].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate2((String)tou_block[2].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate2((Double)tou_block[2].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate2((Double)tou_block[2].getCumDemand(1));
                }

                if(tou_block.length >= 4){
                    bill.setActiveEnergyRate3((Double)tou_block[3].getSummation(0));
                    bill.setActiveEnergyImportRate3((Double)tou_block[3].getSummation(0));
                    bill.setActivePowerMaxDemandRate3((Double)tou_block[3].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate3((String)tou_block[3].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate3((Double)tou_block[3].getCurrDemand(0));
                    bill.setActivePwrDmdMaxTimeImportRate3((String)tou_block[3].getEventTime(0));
                    bill.setCumulativeDemandRate3((Double)tou_block[3].getCumDemand(0));
                    bill.setReactiveEnergyRate3((Double)tou_block[3].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate3((String)tou_block[3].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate3((Double)tou_block[3].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate3((Double)tou_block[3].getCumDemand(1));
                }

                if(tou_block.length >= 5){
                    bill.setActiveEnergyRate4((Double)tou_block[4].getSummation(0));
                    bill.setActiveEnergyImportRate4((Double)tou_block[4].getSummation(0));
                    bill.setActivePowerMaxDemandRate4((Double)tou_block[4].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate4((String)tou_block[4].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate4((Double)tou_block[4].getCurrDemand(0));
                    bill.setActivePwrDmdMaxTimeImportRate4((String)tou_block[4].getEventTime(0));
                    bill.setCumulativeDemandRate4((Double)tou_block[4].getCumDemand(0));
                    bill.setReactiveEnergyRate4((Double)tou_block[4].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate4((String)tou_block[4].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate4((Double)tou_block[4].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate4((Double)tou_block[4].getCumDemand(1));
                }

                billingDayEMDao.add(bill);
            }
        }catch(Exception e){
         log.error(e,e);
        }

    }

    protected void saveMonthlyBilling(TOU_BLOCK[] tou_block, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    {
        log.debug("save MonthlyBillingData["+meter.getMdsId());
        
        try{  

            if(tou_block == null || tou_block.length ==0 || tou_block[0] == null){
                log.debug("MDevId[" + mdevId + "] save MonthlyBillingData TOU data empty!");
                return;
            }
            String billtime = tou_block[0].getResetTime();
            if(billtime != null && billtime.length() == 14){

                log.debug("MDevId[" + mdevId + "] billtime ["+billtime+"]");
                
                
                BillingMonthEM bill = new BillingMonthEM();

                LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
                condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
                condition.add(new Condition("id.hhmmss", new Object[]{billtime.substring(8,14)}, null, Restriction.EQ));
                condition.add(new Condition("id.yyyymmdd", new Object[]{billtime.substring(0,8)}, null, Restriction.EQ));
                condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));
                
                List<Object> ret = billingMonthEMDao.findTotalCountByConditions(condition);
                
                Long count = (Long) ret.get(0);

                // 기존에 데이터가 있을 경우는 기존 정보에 검침된 빌링 정보를 설정한다.
                if(count != null && count.longValue() > 0) {
                    return;
                }
                
                /*
//                BillingMonthEM bill = new BillingMonthEM();
                
                // 기존에 Billing정보가 존재하는지 검색한다. ADD START 20120404 by eunmiae
                // 스케줄러에서 변경된 빌링 정보가 본 기능 실행후 NULL로 변경되는 오류 수정
                BillingMonthEM bill = null;

                LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
                condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));
                condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
                condition.add(new Condition("id.yyyymmdd", new Object[]{billtime.substring(0,8)}, null, Restriction.EQ));
                condition.add(new Condition("id.hhmmss", new Object[]{billtime.substring(8,14)}, null, Restriction.EQ));

                List<BillingMonthEM> billsByPk = billingMonthEMDao.findByConditions(condition);

                // 기존에 데이터가 있을 경우는 기존 정보에 검침된 빌링 정보를 갱신한다.
                if(billsByPk != null && billsByPk.size() != 0) {
                    bill = billsByPk.get(0);
                } else {
                    bill = new BillingMonthEM();
                }
                // 기존에 Billing정보가 존재하는지 검색한다. ADD END 20120404 by eunmiae
                 * 
                 */

                if(meter.getContract() != null){
                    bill.setContract(meter.getContract());
                }
                if(meter.getSupplier() != null){
                    bill.setSupplier(meter.getSupplier());
                }
                if(meter.getLocation() != null){
                    bill.setLocation(meter.getLocation());
                }
                //bill.setBill(bill);//TODO TARIFF SET
                bill.setMDevType(mdevType.name());
                bill.setMDevId(mdevId);
                bill.setMeter(meter);
                bill.setModem(meter.getModem());
                bill.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));

                if(tou_block.length >= 1){
                    bill.setYyyymmdd(tou_block[0].getResetTime().substring(0,8));
                    bill.setHhmmss(tou_block[0].getResetTime().substring(8,14));
                    if(tou_block[0].getSummations()!=null && tou_block[0].getSummations().size()>0){
                        bill.setActiveEnergyRateTotal((Double)tou_block[0].getSummation(0));
                        bill.setActiveEnergyImportRateTotal((Double)tou_block[0].getSummation(0));
                        bill.setReactiveEnergyRateTotal((Double)tou_block[0].getSummation(1));
                    }
                    if(tou_block[0].getCurrDemand()!=null && tou_block[0].getCurrDemand().size()>0) {
                        bill.setActivePowerMaxDemandRateTotal((Double)tou_block[0].getCurrDemand(0));
                        bill.setActivePwrDmdMaxImportRateTotal((Double)tou_block[0].getCurrDemand(0));
                        bill.setReactivePowerMaxDemandRateTotal((Double)tou_block[0].getCurrDemand(1));
                    }
                    if(tou_block[0].getEventTime()!=null && tou_block[0].getCurrDemand().size()>0) {
                        bill.setActivePowerDemandMaxTimeRateTotal((String)tou_block[0].getEventTime(0));
                        bill.setActivePwrDmdMaxTimeImportRateTotal((String)tou_block[0].getEventTime(0));
                        bill.setReactivePowerDemandMaxTimeRateTotal((String)tou_block[0].getEventTime(1));
                    }
                    if(tou_block[0].getCumDemand()!=null && tou_block[0].getCumDemand().size()>0) {
                        bill.setCumulativeDemandRateTotal((Double)tou_block[0].getCumDemand(0));
                        bill.setCumulativeReactivePowerDemandRateTotal((Double)tou_block[0].getCumDemand(1));
                    }
                }
                if(tou_block.length >= 2){
                    bill.setActiveEnergyRate1((Double)tou_block[1].getSummation(0));
                    bill.setActiveEnergyImportRate1((Double)tou_block[1].getSummation(0));
                    bill.setActivePowerMaxDemandRate1((Double)tou_block[1].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate1((String)tou_block[1].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate1((Double)tou_block[1].getCurrDemand(0));
                    bill.setActivePwrDmdMaxTimeImportRate1((String)tou_block[1].getEventTime(0));
                    bill.setCumulativeDemandRate1((Double)tou_block[1].getCumDemand(0));
                    bill.setReactiveEnergyRate1((Double)tou_block[1].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate1((String)tou_block[1].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate1((Double)tou_block[1].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate1((Double)tou_block[1].getCumDemand(1));
                }

                if(tou_block.length >= 3){
                    bill.setActiveEnergyRate2((Double)tou_block[2].getSummation(0));
                    bill.setActiveEnergyImportRate2((Double)tou_block[2].getSummation(0));
                    bill.setActivePowerMaxDemandRate2((Double)tou_block[2].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate2((String)tou_block[2].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate2((Double)tou_block[2].getCurrDemand(0));
                    bill.setActivePwrDmdMaxTimeImportRate2((String)tou_block[2].getEventTime(0));
                    bill.setCumulativeDemandRate2((Double)tou_block[2].getCumDemand(0));
                    bill.setReactiveEnergyRate2((Double)tou_block[2].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate2((String)tou_block[2].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate2((Double)tou_block[2].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate2((Double)tou_block[2].getCumDemand(1));
                }

                if(tou_block.length >= 4){
                    bill.setActiveEnergyRate3((Double)tou_block[3].getSummation(0));
                    bill.setActiveEnergyImportRate3((Double)tou_block[3].getSummation(0));
                    bill.setActivePowerMaxDemandRate3((Double)tou_block[3].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate3((String)tou_block[3].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate3((Double)tou_block[3].getCurrDemand(0));
                    bill.setActivePwrDmdMaxTimeImportRate3((String)tou_block[3].getEventTime(0));
                    bill.setCumulativeDemandRate3((Double)tou_block[3].getCumDemand(0));
                    bill.setReactiveEnergyRate3((Double)tou_block[3].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate3((String)tou_block[3].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate3((Double)tou_block[3].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate3((Double)tou_block[3].getCumDemand(1));
                }

                if(tou_block.length >= 5){
                    bill.setActiveEnergyRate4((Double)tou_block[4].getSummation(0));
                    bill.setActiveEnergyImportRate4((Double)tou_block[4].getSummation(0));
                    bill.setActivePowerMaxDemandRate4((Double)tou_block[4].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate4((String)tou_block[4].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate4((Double)tou_block[4].getCurrDemand(0));
                    bill.setActivePwrDmdMaxTimeImportRate4((String)tou_block[4].getEventTime(0));
                    bill.setCumulativeDemandRate4((Double)tou_block[4].getCumDemand(0));
                    bill.setReactiveEnergyRate4((Double)tou_block[4].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate4((String)tou_block[4].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate4((Double)tou_block[4].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate4((Double)tou_block[4].getCumDemand(1));
                }

                billingMonthEMDao.add(bill);
            }
        }catch(Exception e){
         log.error(e,e);
        }


    }

    protected void saveCurrentBilling(TOU_BLOCK[] tou_block, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    {
        log.debug("save RealTimeTOUData["+meter.getMdsId());

        try{
            if(tou_block == null || tou_block.length ==0 || tou_block[0] == null){
                log.debug("MDevId[" + mdevId + "] save RealTime TOU data empty!");
                return;
            }
            String billtime = tou_block[0].getResetTime();
            if(billtime != null && billtime.length() == 14){
                log.debug("MDevId[" + mdevId + "] billtime ["+billtime+"]");
                
//                RealTimeBillingEM bill = new RealTimeBillingEM();

                // 기존에 Billing정보가 존재하는지 검색한다. ADD START 20120404 by eunmiae
                // 스케줄러에서 변경된 빌링 정보가 본 기능 실행후 NULL로 변경되는 오류 수정
                RealTimeBillingEM bill = new RealTimeBillingEM();

                LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
                condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
                condition.add(new Condition("id.hhmmss", new Object[]{billtime.substring(8,14)}, null, Restriction.EQ));
                condition.add(new Condition("id.yyyymmdd", new Object[]{billtime.substring(0,8)}, null, Restriction.EQ));
                condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));

                List<RealTimeBillingEM> billsByPk = realTimeBillingEMDao.findByConditions(condition);

                // 기존에 데이터가 있을 경우는 기존 정보에 검침된 빌링 정보를 설정한다.
                if(billsByPk != null && billsByPk.size() != 0) {
                    bill = billsByPk.get(0);
                    return;
                } else {
                    bill = new RealTimeBillingEM();
                }
                // 기존에 Billing정보가 존재하는지 검색한다. ADD END 20120404 by eunmiae

                if(meter.getContract() != null){
                    bill.setContract(meter.getContract());
                }

                //bill.setBill(bill);//TODO TARIFF SET
                bill.setMDevType(mdevType.name());
                bill.setMDevId(mdevId);
                
                switch (mdevType) {
                case Meter :
                    bill.setMeter(meter);
                   if(meter.getSupplier() != null){
                       bill.setSupplier(meter.getSupplier());
                   }
                   if(meter.getLocation() != null){
                       bill.setLocation(meter.getLocation());
                   }
                    break;
                case Modem :
                    Modem modem = modemDao.get(mdevId);
                    bill.setModem(modem);
                    if(modem!=null && modem.getSupplier() != null){
                        bill.setSupplier(modem.getSupplier());
                    }
                    if(modem.getLocation() != null){
                        bill.setLocation(modem.getLocation());
                    }
                    break;
                case EndDevice :
                    // pw.setEnvdevice(enddevice);
                }

                bill.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));

                if(tou_block.length >= 1){
                    if(tou_block[0].getResetTime()!=null) {
                        bill.setYyyymmdd(tou_block[0].getResetTime().substring(0,8));
                        bill.setHhmmss(tou_block[0].getResetTime().substring(8,14));
                    }
                    if(tou_block[0].getSummations()!=null && tou_block[0].getSummations().size()>0) {
                        bill.setActiveEnergyRateTotal((Double)tou_block[0].getSummation(0));
                        bill.setActiveEnergyImportRateTotal((Double)tou_block[0].getSummation(0));
                        bill.setReactiveEnergyRateTotal((Double)tou_block[0].getSummation(1));
                    }
                    if(tou_block[0].getCurrDemand()!=null && tou_block[0].getCurrDemand().size()>0) {
                        bill.setActivePowerMaxDemandRateTotal((Double)tou_block[0].getCurrDemand(0));
                        bill.setActivePwrDmdMaxImportRateTotal((Double)tou_block[0].getCurrDemand(0));
                        bill.setReactivePowerMaxDemandRateTotal((Double)tou_block[0].getCurrDemand(1));
                    }
                    if(tou_block[0].getEventTime()!=null && tou_block[0].getEventTime().size()>0) {
                        bill.setActivePowerDemandMaxTimeRateTotal((String)tou_block[0].getEventTime(0));
                        bill.setActivePwrDmdMaxTimeImportRateTotal((String)tou_block[0].getEventTime(0));
                        bill.setReactivePowerDemandMaxTimeRateTotal((String)tou_block[0].getEventTime(1));
                    }
                    if(tou_block[0].getCumDemand()!=null && tou_block[0].getCumDemand().size()>0) {
                        bill.setCumulativeDemandRateTotal((Double)tou_block[0].getCumDemand(0));
                        bill.setCumulativeReactivePowerDemandRateTotal((Double)tou_block[0].getCumDemand(1));
                    }
                }
                if(tou_block.length >= 2){
                    bill.setActiveEnergyRate1((Double)tou_block[1].getSummation(0));
                    bill.setActiveEnergyImportRate1((Double)tou_block[1].getSummation(0));
                    bill.setActivePowerMaxDemandRate1((Double)tou_block[1].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate1((String)tou_block[1].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate1((Double)tou_block[1].getSummation(0));
                    bill.setActivePwrDmdMaxTimeImportRate1((String)tou_block[1].getEventTime(0));
                    bill.setCumulativeDemandRate1((Double)tou_block[1].getCumDemand(0));
                    bill.setReactiveEnergyRate1((Double)tou_block[1].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate1((String)tou_block[1].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate1((Double)tou_block[1].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate1((Double)tou_block[1].getCumDemand(1));
                }

                if(tou_block.length >= 3){
                    bill.setActiveEnergyRate2((Double)tou_block[2].getSummation(0));
                    bill.setActiveEnergyImportRate2((Double)tou_block[2].getSummation(0));
                    bill.setActivePowerMaxDemandRate2((Double)tou_block[2].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate2((String)tou_block[2].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate2((Double)tou_block[2].getSummation(0));
                    bill.setActivePwrDmdMaxTimeImportRate2((String)tou_block[2].getEventTime(0));
                    bill.setCumulativeDemandRate2((Double)tou_block[2].getCumDemand(0));
                    bill.setReactiveEnergyRate2((Double)tou_block[2].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate2((String)tou_block[2].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate2((Double)tou_block[2].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate2((Double)tou_block[2].getCumDemand(1));
                }

                if(tou_block.length >= 4){
                    bill.setActiveEnergyRate3((Double)tou_block[3].getSummation(0));
                    bill.setActiveEnergyImportRate3((Double)tou_block[3].getSummation(0));
                    bill.setActivePowerMaxDemandRate3((Double)tou_block[3].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate3((String)tou_block[3].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate3((Double)tou_block[3].getSummation(0));
                    bill.setActivePwrDmdMaxTimeImportRate3((String)tou_block[3].getEventTime(0));
                    bill.setCumulativeDemandRate3((Double)tou_block[3].getCumDemand(0));
                    bill.setReactiveEnergyRate3((Double)tou_block[3].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate3((String)tou_block[3].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate3((Double)tou_block[3].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate3((Double)tou_block[3].getCumDemand(1));
                }

                if(tou_block.length >= 5){
                    bill.setActiveEnergyRate4((Double)tou_block[4].getSummation(0));
                    bill.setActiveEnergyImportRate4((Double)tou_block[4].getSummation(0));
                    bill.setActivePowerMaxDemandRate4((Double)tou_block[4].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate4((String)tou_block[4].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate4((Double)tou_block[4].getSummation(0));
                    bill.setActivePwrDmdMaxTimeImportRate4((String)tou_block[4].getEventTime(0));
                    bill.setCumulativeDemandRate4((Double)tou_block[4].getCumDemand(0));
                    bill.setReactiveEnergyRate4((Double)tou_block[4].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate4((String)tou_block[4].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate4((Double)tou_block[4].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate4((Double)tou_block[4].getCumDemand(1));
                }

                realTimeBillingEMDao.add(bill);
            }
        }catch(Exception e){
            log.error(e,e);
        }


    }

    protected void saveCurrentBilling(BillingData billData, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    {
         log.debug("save CurrentBillingData["+meter.getMdsId());
         
         try{  

             String yyyymmddOfBillData = "";
             String hhmmssOfBillData = "";
     
             if(billData == null){
                 log.debug("MDevId[" + mdevId + "] save MonthlyBillingData BillingData data empty!");
                 return;
             }

//              RealTimeBillingEM bill = new RealTimeBillingEM();

                // 기존에 Billing정보가 존재하는지 검색한다. ADD START 20120404 by eunmiae
                // 스케줄러에서 변경된 빌링 정보가 본 기능 실행후 NULL로 변경되는 오류 수정
                if(billData.getBillingTimestamp() != null){
                    yyyymmddOfBillData = billData.getBillingTimestamp().substring(0, 8);
                    hhmmssOfBillData = billData.getBillingTimestamp().substring(8, 14);  
                }else{
                    yyyymmddOfBillData = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").substring(0,8);
                    hhmmssOfBillData = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").substring(8,14);                
                }
             
                RealTimeBillingEM bill = new RealTimeBillingEM();
                
                // 데이터를 모두 복사(null포함)
                try {
                    BeanUtils.copyProperties(bill, billData);
                } catch (Exception e) {
                    log.warn(e);
                }
                
                /*
                LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
                condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));
                condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
                condition.add(new Condition("id.yyyymmdd", new Object[]{yyyymmddOfBillData}, null, Restriction.EQ));
                condition.add(new Condition("id.hhmmss", new Object[]{hhmmssOfBillData}, null, Restriction.EQ));


                List<RealTimeBillingEM> billsByPk = realTimeBillingEMDao.findByConditions(condition);

                // 기존에 데이터가 있을 경우는 기존 정보에 검침된 빌링 정보를 설정한다.
                if(billsByPk != null && billsByPk.size() != 0) {
                    bill = billsByPk.get(0);
                } else {
                    bill = new RealTimeBillingEM();
                }
                // 기존에 Billing정보가 존재하는지 검색한다. ADD END 20120404 by eunmiae
                */
                 if(meter.getContract() != null){
                    bill.setContract(meter.getContract());
                 }

                 bill.setMDevType(mdevType.name());
                 bill.setMDevId(mdevId);
                 
                 switch (mdevType) {
                 case Meter :
                    bill.setMeter(meter);
                    if(meter.getSupplier() != null){
                        bill.setSupplier(meter.getSupplier());
                    }
                    if(meter.getLocation() != null){
                        bill.setLocation(meter.getLocation());
                    }
                     break;
                 case Modem :
                     Modem modem = modemDao.get(mdevId);
                     bill.setModem(modem);
                     if(modem!=null && modem.getSupplier() != null){
                        bill.setSupplier(modem.getSupplier());
                     }
                     if(modem.getLocation() != null){
                         bill.setLocation(modem.getLocation());
                     }
                     break;
                 case EndDevice :
                     // pw.setEnvdevice(enddevice);
                 }

                 bill.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                 bill.setYyyymmdd(yyyymmddOfBillData);
                 bill.setHhmmss(hhmmssOfBillData);

                 log.debug("MDevId[" + mdevId + "] bill.getMDevType() "+bill.getMDevType());//4개는 null이여서는 안되는 값이다.
                 log.debug("MDevId[" + mdevId + "] bill.getMDevId()   "+bill.getMDevId());
                 log.debug("MDevId[" + mdevId + "] bill.getYyyymmdd() "+bill.getYyyymmdd());
                 log.debug("MDevId[" + mdevId + "] bill.getHhmmss() "+bill.getHhmmss());
                 log.debug("MDevId[" + mdevId + "] bill.getActiveEnergyImportRate1() " + bill.getActiveEnergyImportRate1());
                 log.debug("MDevId[" + mdevId + "] billData.getActiveEnergyImportRate1() " + billData.getActiveEnergyImportRate1());

                 realTimeBillingEMDao.saveOrUpdate(bill);
//             }
         }catch(Exception e){
             log.warn(e,e);
         }
    }
    
    protected void saveDailyBilling(BillingData billData, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    {
        log.debug("save DailyBillingData["+meter.getMdsId());
         
        try {
            String hhmmssOfBillData = "";
            if(billData == null){
                log.debug("MDevId[" + mdevId + "] save DailyBillingData BillingData data empty!");
                return;
            }

            // 기존에 Billing정보가 존재하는지 검색한다. ADD START 20120404 by eunmiae
            // 스케줄러에서 변경된 빌링 정보가 본 기능 실행후 NULL로 변경되는 오류 수정
             
            if(billData.getBillingTimestamp().length() == 8){
                hhmmssOfBillData = "000000";
            }else if(billData.getBillingTimestamp().length() == 10){
                hhmmssOfBillData = billData.getBillingTimestamp().substring(8,10)+"0000";
            }else if(billData.getBillingTimestamp().length() == 12){
                hhmmssOfBillData = billData.getBillingTimestamp().substring(8,12)+"00";
            }else if(billData.getBillingTimestamp().length() == 14){
                hhmmssOfBillData = billData.getBillingTimestamp().substring(8,14);
            }

            BillingDayEM bill = new BillingDayEM();

            LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
            condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
            condition.add(new Condition("id.hhmmss", new Object[]{hhmmssOfBillData}, null, Restriction.EQ));
            condition.add(new Condition("id.yyyymmdd", new Object[]{billData.getBillingTimestamp().substring(0,8)}, null, Restriction.EQ));
            condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));
            
            List<Object> ret = billingDayEMDao.findTotalCountByConditions(condition);
            
            Long count = (Long) ret.get(0);

            // 기존에 데이터가 있을 경우는 기존 정보에 검침된 빌링 정보를 설정한다.
            if(count != null && count.longValue() > 0) {
            	log.debug("already saved Daily Billing Data!");
                return;
            } 
            // 기존에 Billing정보가 존재하는지 검색한다. ADD END 20120404 by eunmiae
             
            // 데이터를 모두 복사(null포함)
            try {
                BeanUtils.copyProperties(bill, billData);
            } catch (Exception e) {
                log.error(e);
            }
             
            if(meter.getContract() != null){
                bill.setContract(meter.getContract());
            }

            bill.setMDevType(mdevType.name());
            bill.setMDevId(mdevId);
             
            switch (mdevType) {
            case Meter :
                bill.setMeter(meter);
                if(meter.getSupplier() != null){
                    bill.setSupplier(meter.getSupplier());
                }
                if(meter.getLocation() != null){
                    bill.setLocation(meter.getLocation());
                }
                break;
            case Modem :
                Modem modem = modemDao.get(mdevId);
                bill.setModem(modem);
                if(modem!=null && modem.getSupplier() != null){
                   bill.setSupplier(modem.getSupplier());
                }
                if(modem.getLocation() != null){
                    bill.setLocation(modem.getLocation());
                }
                break;
            case EndDevice :
                 // pw.setEnvdevice(enddevice);
            }

            bill.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
            bill.setYyyymmdd(billData.getBillingTimestamp().substring(0,8));
            bill.setHhmmss(hhmmssOfBillData);

            log.debug("bill.getMDevType() "+bill.getMDevType());
            log.debug("bill.getMDevId()   "+bill.getMDevId());
            log.debug("bill.getYyyymmdd() "+bill.getYyyymmdd());

            billingDayEMDao.add(bill);
        }
        catch(Exception e){
            log.warn(e,e);
        }
    }

    protected void saveMonthlyBilling(BillingData billData, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    {
        log.debug("save MonthlyBillingData["+meter.getMdsId());

        try{  
             
            String hhmmssOfBillData = "";
            if(billData == null){
                log.debug("MDevId[" + mdevId + "] save MonthlyBillingData BillingData data empty!");
                return;
            }

            // 기존에 Billing정보가 존재하는지 검색한다. ADD START 20120404 by eunmiae
            // 스케줄러에서 변경된 빌링 정보가 본 기능 실행후 NULL로 변경되는 오류 수정
            if(billData.getBillingTimestamp().length() == 8){
                hhmmssOfBillData= "000000";
            }else if(billData.getBillingTimestamp().length() == 10){
                hhmmssOfBillData= billData.getBillingTimestamp().substring(8,10)+"0000";
            }else if(billData.getBillingTimestamp().length() == 12){
                hhmmssOfBillData= billData.getBillingTimestamp().substring(8,12)+"00";
            }else if(billData.getBillingTimestamp().length() == 14){
                hhmmssOfBillData= billData.getBillingTimestamp().substring(8,14);
            }
             
            BillingMonthEM bill = new BillingMonthEM();

            LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
            condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
            condition.add(new Condition("id.hhmmss", new Object[]{hhmmssOfBillData}, null, Restriction.EQ));
            condition.add(new Condition("id.yyyymmdd", new Object[]{billData.getBillingTimestamp().substring(0,8)}, null, Restriction.EQ));
            condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));

            List<Object> ret = billingMonthEMDao.findTotalCountByConditions(condition);
            
            Long count = (Long) ret.get(0);

            // 기존에 데이터가 있을 경우는 기존 정보에 검침된 빌링 정보를 설정한다.
            if(count != null && count.longValue() > 0) {
            	log.debug("already saved Monthly Billing Data!");
                return;
            }

            // 데이터를 모두 복사(null포함)
            try {
                BeanUtils.copyProperties(bill, billData);
            } catch (Exception e) {
                log.warn(e);
            }
             
            // 기존에 Billing정보가 존재하는지 검색한다. ADD END 20120404 by eunmiae
            if(meter.getContract() != null){
                bill.setContract(meter.getContract());
            }

            bill.setMDevType(mdevType.name());
            bill.setMDevId(mdevId);
             
            switch (mdevType) {
            case Meter :
                bill.setMeter(meter);
                if(meter.getSupplier() != null){
                    bill.setSupplier(meter.getSupplier());
                }
                if(meter.getLocation() != null){
                    bill.setLocation(meter.getLocation());
                }
                break;
            case Modem :
                Modem modem = modemDao.get(mdevId);
                bill.setModem(modem);
                if(modem!=null && modem.getSupplier() != null){
                    bill.setSupplier(modem.getSupplier());
                }
                if(modem.getLocation() != null){
                    bill.setLocation(modem.getLocation());
                }
                break;
            case EndDevice :
                // pw.setEnvdevice(enddevice);
            }

            bill.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
            bill.setYyyymmdd(billData.getBillingTimestamp().substring(0,8));
            bill.setHhmmss(hhmmssOfBillData);
             
             
            if(billData.getBillingTimestamp().length() == 8){
                bill.setHhmmss("000000");
            }else if(billData.getBillingTimestamp().length() == 10){
                bill.setHhmmss(billData.getBillingTimestamp().substring(8,10)+"0000");
            }else if(billData.getBillingTimestamp().length() == 12){
                bill.setHhmmss(billData.getBillingTimestamp().substring(8,12)+"00");
            }else if(billData.getBillingTimestamp().length() == 14){
                bill.setHhmmss(billData.getBillingTimestamp().substring(8,14));
            }

            log.debug("bill.getMDevType() "+bill.getMDevType());
            log.debug("bill.getMDevId()   "+bill.getMDevId());
            log.debug("bill.getYyyymmdd() "+bill.getYyyymmdd());
             
            billingMonthEMDao.add(bill);

        }
        catch(Exception e){
            log.warn(e,e);
        }
    }

    protected void saveMeterTimeSyncLog(Meter meter, String before, String after,
            int result) throws Exception {

        log.debug("save MeterTimeSyncLog["+meter.getMdsId()+"]");

        MeterTimeSyncData mtdata = new MeterTimeSyncData();
        mtdata.setAtime(after);
        mtdata.setBtime(before);
        mtdata.setContent("");
        mtdata.setCtime(after);
        mtdata.setUserID("system");
        long diff = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(after).getTime() - 
                DateTimeUtil.getDateFromYYYYMMDDHHMMSS(before).getTime();
        mtdata.setTimediff(diff);
        mtdata.setResult(result);
        
        saveMeterTimeSyncLog(meter, mtdata);
    }
    
    protected void saveMeterTimeSyncLog(Meter meter, MeterTimeSyncData data) {

        log.debug("save MeterTimeSyncLog["+meter.getMdsId()+"]");
        try {
            if(data.getCtime() == null || "".equals(data.getCtime())){ //주키값이 없으면 insert 할수 없다.
                throw new Exception("Meter time is wrong. check meter time!!");
            }
        
            MeterTimeSyncLog mt = new MeterTimeSyncLog();
            mt.setAfterDate(data.getAtime());
            mt.setBeforeDate(data.getBtime());
            mt.setDescr(data.getContent());
            mt.setLocation(meter.getLocation());
            mt.setMeter(meter);
            mt.setMeterDate(data.getCtime());
            mt.setOperator(data.getUserID());
            mt.setOperatorType(OperatorType.SYSTEM.name());
    
            switch(data.getResult()){
                case 0 : mt.setResult(ResultStatus.SUCCESS.name());break;
                case 1 : mt.setResult(ResultStatus.FAIL.name());break;
                case 2 : mt.setResult(ResultStatus.INVALID_PARAMETER.name());break;
                case 3 : mt.setResult(ResultStatus.COMMUNICATION_FAIL.name());break;
            }
    
            mt.setSupplier(meter.getSupplier());
            mt.setTimeDiff(data.getTimediff());
            mt.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
            mt.setId(TimeUtil.getCurrentLongTime());
            meterTimeSyncLogDao.add(mt);
        }
        catch (Exception e) {
            log.warn(e, e);
        }
    }
    
    /**
     * 집중기나 모뎀의 검침시간과 미터시간이 거의 일치해야 한다.
     * 만약 오차가 많이 발생하면 시간 동기화를 할 수 있도록 한다.
     * @param meter
     * @param meteringTime
     * @param meterTime
     * @throws Exception
     */
    protected void saveMeterTimeSyncLog(Meter meter, String meteringTime, String meterTime) {

        log.debug("MDevId[" + meter.getMdsId() + "] save MeterTimeSyncLog[MeteringTime=" + meteringTime + ", MeterTime=" + meterTime +"]");
        try {
            if (meteringTime != null && meterTime != null) {
                MeterTimeSyncLog mt = new MeterTimeSyncLog();
                mt.setLocation(meter.getLocation());
                mt.setMeter(meter);
                mt.setMeterDate(meterTime);
                mt.setAfterDate("");
                mt.setBeforeDate("");
                mt.setOperator("system");
                mt.setOperatorType(OperatorType.SYSTEM.name());
                mt.setSupplier(meter.getSupplier());
                long diff = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meteringTime).getTime() - 
                        DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime();
                mt.setTimeDiff(diff / 1000);
                mt.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                mt.setResult(ResultStatus.SUCCESS.name());
                mt.setId(TimeUtil.getCurrentLongTime());
                meterTimeSyncLogDao.add(mt);
            }
        }
        catch (Exception e) {
            log.warn(e, e);
        }
    }
    
    public static Double dformat(Double n){
        if(n==null)
            return 0d;
        return new Double(dformat.format(n));
    }
    
    public static int getDayType(String yyyymmdd) throws ParseException {
        // 공휴일 프로퍼티가 없으면 fmp.properties에 설정이 없는 것이므로 영업일로 처리한다.
        // 태국 TCIS 기능을 위해서만 처리하므로 다른 사이트는 적용하지 않아도 된다. 
        String holidaylist = FMPProperty.getProperty("sic.day.holiday.list");
        if (holidaylist == null)
            return 0;
        
        StringTokenizer st = new StringTokenizer(holidaylist, ",");
        while (st.hasMoreTokens()) {
            if (yyyymmdd.substring(4).equals(st.nextToken()))
                return Integer.parseInt(FMPProperty.getProperty("sic.day.holiday"));
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateTimeUtil.getDateFromYYYYMMDD(yyyymmdd));
        return Integer.parseInt(FMPProperty.getProperty("sic.day." + cal.get(Calendar.DAY_OF_WEEK)));
    }
    
    public String relayValveOn(String mcuId, String meterId) {
        return "Not supported";
    }

    public String relayValveOff(String mcuId, String meterId) {
        return "Not supported";
    }

    public String relayValveStatus(String mcuId, String meterId) {
        return "Not supported";
    }

    public String syncTime(String mcuId, String meterId) {
        return "Not supported";
    }

    public String relayValveActivate(String mcuId, String meterId) {
        return "Not supported";
    }

    public String relayValveDeactivate(String mcuId, String meterId) {
        return "Not supported";
    }
    
    public MeterData onDemandMeterBypass(String mcuId, String meterId,String modemId, String nOption, String fromDate, String toDate)
            throws Exception
    {
    	return null;
    }
    // -> UPDATE START 2016/09/20 SP-117
    // protected void updateMeterStatusNormal(Meter meter) {
    public void updateMeterStatusNormal(Meter meter) {
    // <- UPDATE END   2016/09/20 SP-117
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            
            meter = meterDao.get(meter.getMdsId());
            meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.Normal.name()));
            meterDao.update(meter);
            
            Contract contract = meter.getContract();
            if(contract != null && (contract.getStatus() == null 
                    || contract.getStatus().getCode().equals(CommonConstants.ContractStatus.PAUSE.getCode()))) {
                Code normalCode = CommonConstants.getContractStatus(CommonConstants.ContractStatus.NORMAL.getCode());
                // codeDao.getCodeIdByCodeObject(CommonConstants.ContractStatus.NORMAL.getCode());
                contract.setStatus(normalCode);
                contractDao.update(contract);
                // contractDao.updateStatus(contract.getId(), normalCode);
            }
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            log.error(e, e);
            if (txstatus != null) txmanager.rollback(txstatus);
        }
    }
 
    protected void updateMeterModel(Meter meter, String meterModel){
        List<DeviceModel> models = deviceModelDao.getDeviceModelByName(meter.getSupplierId(), meterModel);
        if(models != null && models.size() > 0 && models.get(0) != null){
            DeviceModel org = meter.getModel();
            if(org != null){
                if(org.getId() != models.get(0).getId()){
                    meter.setModel(models.get(0));
                }
            }else{
                meter.setModel(models.get(0));
            }           
        }
    }
    
    // -> UPDATE START 2016/09/20 SP-117
    // protected void updateMeterStatusCutOff(Meter meter) {
    public void updateMeterStatusCutOff(Meter meter) {
    // <- UPDATE END   2016/09/20 SP-117
        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            
            meter = meterDao.get(meter.getMdsId());
            meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.CutOff.name()));
            meterDao.update(meter);
            
            Contract contract = meter.getContract();
            if(contract != null && (contract.getStatus() == null
                    || contract.getStatus().getCode().equals(CommonConstants.ContractStatus.NORMAL.getCode()))) {
                Code pauseCode = CommonConstants.getContractStatus(CommonConstants.ContractStatus.PAUSE.getCode());
                // codeDao.getCodeIdByCodeObject(CommonConstants.ContractStatus.PAUSE.getCode());
                contract.setStatus(pauseCode);
                contractDao.update(contract);
            }
            
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            log.error(e, e);
            if (txstatus != null) txmanager.rollback(txstatus);
        }
    }
 
    public String MapToJSON(String[] array) {
        StringBuffer rStr = new StringBuffer();
        rStr.append("[");
        for (int i = 0; array != null && i < array.length; i++) {
            rStr.append("{\"name\":\"");
            rStr.append("key[" + i + "]");
            rStr.append("\",\"value\":\"");
            rStr.append(array[i]);
            rStr.append("\"}");
            if (i < array.length - 1) {
                rStr.append(",");
            }
        }
        rStr.append("]");
        return rStr.toString();
    }

    @SuppressWarnings("unchecked")
    public String MapToJSON(Map map) {
        StringBuffer rStr = new StringBuffer();
        Iterator<String> keys = map.keySet().iterator();
        String keyVal = null;
        rStr.append("[");
        while (keys.hasNext()) {
            keyVal = (String) keys.next();
            rStr.append("{\"name\":\"");
            rStr.append(keyVal);
            rStr.append("\",\"value\":\"");
            rStr.append(map.get(keyVal));
            rStr.append("\"}");
            if (keys.hasNext()) {
                rStr.append(",");
            }
        }
        rStr.append("]");
        return rStr.toString();
    }
    
    public JsonElement StringToJsonArray(String str) {
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(str);
    }
    
    private String getFullLocation(Location loc) {
        if (loc == null)
            return "";
        
        // location 트리
        if (locMap == null)
            locMap = new HashMap<String, String>();
        
        String locid = "loc_" + loc.getId();
        String locTree = locMap.get(locid);
        if (locTree == null) {
            StringBuffer loc_buf = new StringBuffer();
            while (loc != null) {
                loc_buf.append("loc_" + loc.getId());
                loc = loc.getParent();
            }
            locTree = loc_buf.toString();
            locMap.put(locid, locTree);
        }
        return locid;
    }
    
    protected void saveSNRLog(String deviceId, String yyyymmdd, String hhmmss, Modem modem, Double val) 
    {
        try{
            SNRLog snrLog = new SNRLog();            
            snrLog.setDcuid(deviceId);
            snrLog.setDeviceId(modem.getDeviceSerial());
            snrLog.setDeviceType(modem.getModemType().name());
            snrLog.setYyyymmdd(yyyymmdd);
            snrLog.setHhmmss(hhmmss);
            snrLog.setSnr(val);
            snrLogDao.add(snrLog);
        }catch(Exception e){
            log.warn(e,e);
        }
    }
    
    protected void saveEnvData(Modem modem, List<EnvData> lpDatas) throws Exception
    {
        for (EnvData envData: lpDatas) {

            ZEUPLS envSensor = (ZEUPLS) modemDao.get(envData.getSensorId());
                
            try{
                if( envSensor == null){
                    envSensor = new ZEUPLS();
                    envSensor.setDeviceSerial(envData.getSensorId());
                    envSensor.setModemType(ModemType.ZEUPLS.name());
                    envSensor.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                    envSensor.setCommState(1);
                    envSensor.setLastLinkTime(modem.getInstallDate());
                    envSensor.setModem(modem);
                    List<DeviceModel> models = deviceModelDao.getDeviceModelByName(modem.getSupplierId(), "NZM-C001SR");
                    if (models.size() == 1)
                        envSensor.setModel(models.get(0));
                    modemDao.add(envSensor);
                    //modemDao.flushAndClear();
                }else{
                    envSensor.setCommState(1);
                    envSensor.setLastLinkTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                    //envSensor.setModem(modem);
                    modemDao.update(envSensor);
                    //modemDao.flushAndClear();
                }

            }catch(Exception e){
                log.warn(e,e);
            }
            envSensor = null;
            //com.aimir.model.device.EnvSensor dbenvSensor = (com.aimir.model.device.EnvSensor) modemDao.get(envData.getSensorId());
            
            for (int i = 0 ; i < envData.getChannelCnt(); i++) {
                LpTM lp = new LpTM();   
                String str_mm = "value_"+envData.getDatetime().substring(10,12);              
                BeanUtils.copyProperty(lp, str_mm, dformat(envData.getCh()[i]));
                lp.setChannel(i+1);
                lp.setDeviceId(envData.getSensorId());
                lp.setDeviceType(DeviceType.Modem.getCode());
                lp.setDst(0);
                lp.setYyyymmdd(envData.getDatetime().substring(0,8));
                lp.setYyyymmddhh(envData.getDatetime().substring(0,10));
                lp.setHour(envData.getDatetime().substring(8,10));
                lp.setMDevId(envData.getSensorId());
                lp.setMDevType(DeviceType.Modem.name());
                lp.setMeteringType(0);
                lp.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                //lp.setModem(dbenvSensor);
                
                log.debug("SENSORID["+envData.getSensorId()+"] YYYYMMDDHHMM["+envData.getDatetime().substring(0,12)+"] CHANNEL["+(i+1)+"] VALUE["+envData.getCh()[i]+"]");
                
                LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
                condition.add(new Condition("id.mdevId", new Object[]{envData.getSensorId()}, null, Restriction.EQ));
                condition.add(new Condition("id.yyyymmddhh", new Object[]{envData.getDatetime().substring(0,10)}, null, Restriction.EQ));
                condition.add(new Condition("id.mdevType", new Object[]{DeviceType.Modem}, null, Restriction.EQ));    
                condition.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
                condition.add(new Condition("id.channel", new Object[]{i+1}, null, Restriction.EQ));
                
                List<LpTM> dbList = lpTMDao.findByConditions(condition);
                
                try{

                    if(dbList != null && dbList.size() > 0){
                        LpTM updateLp = dbList.get(0);
                        updateLp.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                        BeanUtils.copyProperty(updateLp, str_mm, dformat(envData.getCh()[i]));
                        lpTMDao.update(updateLp);
                        lpTMDao.flushAndClear();
                    } else{
                        lpTMDao.add(lp);
                        lpTMDao.flushAndClear();
                    }
                }catch(Exception e){
                    log.warn(e,e);
                }
            }            
        }
    }
}
