package com.aimir.fep.trap.actions;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.EventAlertDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.fep.command.ws.client.CommandWS;
import com.aimir.fep.command.ws.client.CommandWS_Service;
import com.aimir.fep.command.ws.client.ResponseMap;
import com.aimir.fep.modem.EventLog;
import com.aimir.fep.modem.ModemCommandData;
import com.aimir.fep.modem.ModemCommandData.Event;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.sms.SendSMS;
import com.aimir.model.device.EventAlert;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.ZRU;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.MeterConfig;
import com.aimir.model.system.Supplier;
import com.aimir.notification.FMPTrap;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;

/**
 * Event ID : 203.10.0 Processing Class
 * 
 * 203.10	eventSensorAlarm	OID		O	센서 알람 이벤트	SAT2
 * 4.1.1			Sensor ID	EUI64	8	센서 ID	
 * 1.21				gmtEntry	STREAM	11	센서의 이벤트 발생 시간	
 * 1.4				byteEntry	BYTE	2	센서 값(0:Power Fail, 1:Battery Fault, 2:Power Restore, 3:Line Missing)	
 * 1.6					status	UINT	4	결상에 대한 센서 데이터	BYPASS
 * 4.1.1			Sensor ID	EUI64	8	센서 ID	
 * 1.5				alarmState	WORD	2	센서 값	
 * 1.9				intEntry	INT		4	결상에 대한 센서 데이터	
 *
 *
 * 0x00 Power Outage/Recovery
 * 0x01 Low Battery
 * 0x02 Reserved
 * 0x03 LX Missing
 * 0x04 Tilt Alarm
 * 0x05 Case Open
 * 0x06 Magnetic Tamper
 * 0x07 Detachment / Attachment
 * 0x08 Pulse Cut
 * 0x09 Battery Discharging Enable
 * 0x0A Back Pulse Detected
 * 0x0B Reserved
 * 0x0C State Alarm
 * @author goodjob
 * @version $Rev: 1 $, $Date: 2010-08-09 09:30:15 +0900 $,
 */
@Component
public class EV_203_10_0_Action implements EV_Action
{  
    private static Log log = LogFactory.getLog(EV_203_10_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    EventAlertDao eaDao;
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    ContractDao contractDao;
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
    	
        log.info("EV_203_10_0_Action : EventName[eventSensorAlarm] "
                +" EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"] TargetClass[" + event.getActivatorType() + "]");

        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            
            TargetClass targetClass = event.getActivatorType();
            MCU mcu = null;
            Modem modem = null;
            Meter meter = null;
            
            if (targetClass == TargetClass.DCU)
                mcu = mcuDao.get(event.getActivatorId());
            else if (targetClass == TargetClass.Modem)
                modem = modemDao.get(event.getActivatorId());
            else if (targetClass.name().lastIndexOf("Meter") != -1)
                meter = meterDao.get(event.getActivatorId());
            
            if (mcu == null && modem == null && meter == null)
            {
                log.debug("event source is null in mcu["+trap.getMcuId()+"]");
                return;
            }
            else if (mcu == null && modem != null) {
                mcu = modem.getMcu();
            }
            else if (mcu == null && meter != null) {
                modem = meter.getModem();
                mcu = modem.getMcu();
            }

            String modemId = event.getEventAttrValue("sensorID");
            log.debug("MODEM_ID[" + modemId + "]");
            
            if (modem == null) {
                modem = modemDao.get(modemId);
            }
            
            if (modem != null) {
                if (modem.getMeter() != null && !modem.getMeter().isEmpty())
                    meter = modem.getMeter().iterator().next();
                if (meter != null && meter.getMdsId() != null)
                    log.debug("METER_ID[" + meter.getMdsId() + "]");
            }
            
            String unitSerial = null;
            
            if (modem == null)
            {
                log.debug("modem["+modemId+"] is not exist");
                modem = new ZRU();
                modem.setDeviceSerial(modemId);
                modem.setSupplier(mcu.getSupplier());
                modem.setLocation(mcu.getLocation());
                modem.setMcu(mcu);
                // return;
            }
            event.setActivatorType(TargetClass.Modem);
            event.setActivatorId(modemId);
            
            // add by elevas, 2011.05.20
            if (mcu != null && modem.getMcu() == null)
                modem.setMcu(mcu);
            
            event.setLocation(modem.getMcu().getLocation());
            
            String timestamp = "";
            
            String str = event.getEventAttrValue("gmtEntry");
            //String gmtTimezone = "gmtTimezone: ";
            //String gmtDstValue = "gmtDstValue: ";
            String gmtYear = "gmtYear: ";
            String gmtMon = "gmtMon: ";
            String gmtDay = "gmtDay: ";
            String gmtHour = "gmtHour: ";
            String gmtMin = "gmtMin: ";
            String gmtSec = "gmtSec: ";
            
            String _year = str.substring(str.indexOf(gmtYear)+gmtYear.length(),
                                        str.indexOf(gmtMon)).trim();
            String _mon = str.substring(str.indexOf(gmtMon)+gmtMon.length(),
                                       str.indexOf(gmtDay)).trim();
            String _day = str.substring(str.indexOf(gmtDay)+gmtDay.length(),
                                       str.indexOf(gmtHour)).trim();
            String _hour = str.substring(str.indexOf(gmtHour)+gmtHour.length(),
                                        str.indexOf(gmtMin)).trim();
            String _min = str.substring(str.indexOf(gmtMin)+gmtMin.length(),
                                       str.indexOf(gmtSec)).trim();
            String _sec = str.substring(str.indexOf(gmtSec)+gmtSec.length()).trim();
            
            log.debug("YEAR[" + _year + "] MONTH[" + _mon + "] DAY[" + _day + 
                      "] HOUR[" + _hour + "] MIN[" + _min + "] SEC[" + _sec + "]");
            
            int year = Integer.parseInt(_year);
            // byte[] bYear = DataUtil.get2ByteToInt(year);
            // DataUtil.convertEndian(bYear);
            // year = DataUtil.getIntTo2Byte(bYear);
            int month = Integer.parseInt(_mon);
            int day = Integer.parseInt(_day);
            int hour = Integer.parseInt(_hour);
            int min = Integer.parseInt(_min);
            int sec = Integer.parseInt(_sec);
            
            log.debug("YEAR[" + year + "] MONTH[" + month + "] DAY[" + day + "]" +
                      "HOUR[" + hour + "] MINUTE[" + min + "] SECOND[" + sec + "]");
            
            timestamp = DateTimeUtil.getDST(mcu.getSupplier().getTimezone().getName(), year + (month < 10? "0"+month:""+month) +
                    (day < 10? "0"+day:""+day) + (hour < 10? "0"+hour:hour) +
                    (min < 10? "0"+min:""+min) + (sec < 10? "0"+sec:sec));
            
            if (timestamp.substring(0, 8).equals(trap.getTimeStamp().substring(0, 8)) && timestamp.length() == 14)
                event.setOpenTime(timestamp);
            else
                event.setOpenTime(trap.getTimeStamp());
            
            int alarmStatus = 0;
            try { 
                String tmpval = event.getEventAttrValue("byteEntry");
                log.debug("EV_203_10_0_Action : byteEntry[" + tmpval + "]");
                alarmStatus = Integer.parseInt(tmpval);
            }catch(Exception ex) { log.error(ex,ex); }

            ModemType modemType = modem.getModemType();
            if (modemType == null)
                modemType = ModemType.ZRU;
            
            if (!ModemType.ZMU.equals(modemType)) {
                boolean isPowerFail = false;
                boolean isPowerRestore = false;
                boolean isBatteryFault = false;
                boolean isLineMissing = false;
                boolean isLineRestore = false;
                boolean isCaseAlarm = false;
                boolean isCaseOpen = false;
                boolean isMagneticTamper = false;
                boolean isTiltAlarm = false;
                boolean isDetachment = false;
                boolean isPulseCut = false;
                boolean isBatteryDischargingEnable = false;
                boolean isBackPulseDetected = false;
                boolean isAttachment = false;
                boolean isValveAlarm = false;
                
                switch (alarmStatus) 
                {
                case 0 : int isPower = getPowerState(alarmStatus, event);
                         if (isPower == 1)
                             isPowerFail = true;
                         else if (isPower == 2)
                             isPowerRestore = true;
                         break;
                case 1 : isBatteryFault = true;
                         break;
                case 2 : isPowerRestore = true;
                         break;
                case 3 : isPower = getPowerState(alarmStatus, event);
                         if (isPower == 1)
                             isLineMissing = true;
                         else if (isPower == 2)
                             isLineRestore = true;
                         break;
                case 4 : isTiltAlarm = true;
                         break;
                case 5 : isCaseAlarm = true;
                         isCaseOpen = getCaseState(alarmStatus, event);
                         break;
                case 6 : isMagneticTamper = true;
                         break;
                case 7 : int mrstate = getDetachmentState(event);
                         if (mrstate == 0)
                             isDetachment = true;
                         else if (mrstate == 1)
                             isAttachment = true;
                         break;
                case 8 : isPulseCut = true;
                         break;
                case 9 : isBatteryDischargingEnable = true;
                         break;
                case 10 : isBackPulseDetected = true;
                         break;
                case 12 : // TODO 모델이 극동 가스인 경우에만 해당하는데 현재 12에 대한 이벤트는 여기밖에 없음. 
                         isValveAlarm = true;
                         break;
                }
                
                // Log
                log.info("TIMESTAMP[" + timestamp + "] modemId[" + modemId + "]"
                        +" alarmStatus["+alarmStatus
                        +"] isPowerFail["+isPowerFail +"] isPowerRestore[" + isPowerRestore 
                        +"] isLineMissing[" + isLineMissing + "] isLineRestore[" + isLineRestore
                        +"] isBatteryFault["+isBatteryFault+"] isTiltAlarm[" + isTiltAlarm 
                        +"] isCaseAlarm[" + isCaseAlarm + "] isCaseOpen[" + isCaseOpen +"] isMagneticTamper[" + isMagneticTamper
                        +"] isDetachment[" + isDetachment + "] isPulseCut[" + isPulseCut 
                        +"] isBatteryDischargingEnable[" + isBatteryDischargingEnable
                        +"] isBackPulseDetected[" + isBackPulseDetected +"] isAttachment[" + isAttachment 
                        +"] isValveAlarm[" + isValveAlarm + "]");
        
                if(meter != null && !isBatteryFault)
                {
                    event.setActivatorId(meter.getMdsId());
                    event.setActivatorType(meter.getMeterType().getName());
                    event.setLocation(meter.getLocation());
                    unitSerial = meter.getMdsId();
                }
        
                try
                {
                    log.debug("othermodemType="+modemType.name());

                    EventAlertAttr ea = EventUtil.makeEventAlertAttr("modemType", 
                            "java.lang.String", modemType.name());
                    event.append(ea);
                    
                    // 단상인데 3상처럼 처리되는 것을 막기 위해 미터 모델의 상을 이용하여 결상이벤트 처리를 하지 않는다.
                    // 2016.03.09
                    MeterConfig mc = null;
                    if (meter != null && meter.getModel() != null && meter.getModel().getDeviceConfig() != null)
                        mc = (MeterConfig)meter.getModel().getDeviceConfig();
                    
                    if (mc != null && mc.getPhase().toLowerCase().contains("1p")) {
                        isLineMissing = false;
                        isLineRestore = false;
                    }
                    
                    if(isPowerFail || isLineMissing)
                    {
                        EventAlert eventAlert = eaDao.findByCondition("name", "Power Alarm");
                        event.setEventAlert(eventAlert);
                        
                        if (isLineMissing) 
                        {
                            ea = EventUtil.makeEventAlertAttr("isLineMissing", 
                                                         "java.lang.String", "true");
                            event.append(ea);
                            String evtAttrValue = event.getEventAttrValue("uintEntry").toString();
                            ea = EventUtil.makeEventAlertAttr("message",
                                                         "java.lang.String",
                                                         "Line Missing " +
                                                         EventLog.getMsg(evtAttrValue));
                            event.append(ea);
                        }
                        else 
                        {
                            ea = EventUtil.makeEventAlertAttr("isPowerFail", 
                                    "java.lang.String", "true");
                            event.append(ea);
                            if (modemType.equals(ModemType.ZBRepeater)) {
                                ea = EventUtil.makeEventAlertAttr("message", 
                                                             "java.lang.String", 
                                                             "Repeater Power Failure");
                            }
                            else {
                                ea = EventUtil.makeEventAlertAttr("message", 
                                        "java.lang.String", 
                                        "Meter Power Failure");
                            }
                            event.append(ea);
                            if(meter != null) {
                                if (meter.getMeterStatus() != null && 
                                        !meter.getMeterStatus().getName().equals(MeterStatus.CutOff.name())) {
                                    String code = CommonConstants.getMeterStatusCode(MeterStatus.PowerDown);
                                    meter.setMeterStatus(CommonConstants.getMeterStatus(code));
                                }
                            }

                            // 제주 실증단지 미터 이벤트 연동을 위해 추가
                            ea = EventUtil.makeEventAlertAttr("mRID", "java.lang.String", "3.26.9.185");
                            event.append(ea);
                        }
                        ea = EventUtil.makeEventAlertAttr("meterId",
                                                     "java.lang.String",
                                                     unitSerial);
                        event.append(ea);
                        
                        log.debug("Meter Power Fail Action Started Alert Name[" + event.getEventAlert().getName() + "]");
                                        
                        //boolean isPlannedPO = false;
        
                        //String meter_id = unitSerial;
                        /*
                        String searchdate = DateTimeUtil.getDateString(cal.getTime());
                        
                        PowerOutageMgrDelegate poMgr = new PowerOutageMgrDelegate();
                        
                        Map<String, String> map = new LinkedHashMap<String, String>( 16, 0.74f, false );
                        map.put("m.meter_id:=:str", meter_id);
                        map.put("g.starttime:>:LongTime", searchdate);
                        map.put("g.endtime:<:LongTime", searchdate);
                        
                        isPlannedPO 
                            = poMgr.isPlannedPowerOutage(map,event.getLocationName());
        
                        if(!isPlannedPO && meter != null)
                        {
                            ArrayList alerts = FaultUtil.findOpenAlert(meter.getName(), 
                                                                       event.getFaultClassName(),
                                                                       1);
        
                            if (alerts == null || alerts.size() == 0)
                            {
                                Alert alert = FaultUtil.makeAlert(event);
                                FaultUtil.createNotification(alert);
                            }
                            else
                            {
                                for (int i = 0; i < alerts.size(); i++)
                                {
                                    Alert alert = (Alert) alerts.get(i);
                                    Integer t = alert.getTimes();
                                    int ti = t.intValue()+1;
                                    alert.setTimes(new Integer(ti));
                                    FaultUtil.updateNotification(alert);
                                }
                            }
                        }
                        
                        */
                        log.debug("Meter Power Fail Action End Alert Name[" + event.getEventAlert().getName() + "]");
                    } 
                    else if (isPowerRestore || isLineRestore)
                    {
                        EventAlert eventAlert = eaDao.findByCondition("name", "Power Alarm");
                        event.setEventAlert(eventAlert);
                        
                        if (isLineRestore) {
                            ea = EventUtil.makeEventAlertAttr("isLineRestore", 
                                                         "java.lang.String", "true");
                            event.append(ea);
                            String evtAttrValue = event.getEventAttrValue("uintEntry").toString();
                            ea = EventUtil.makeEventAlertAttr("message",
                                                         "java.lang.String",
                                                         "Line Restore " +
                                                         EventLog.getMsg(evtAttrValue));
                            event.append(ea);
                            event.setStatus(EventStatus.Cleared);
                        }
                        else {
                            if (modemType.equals(ModemType.ZBRepeater)) {
                                ea = EventUtil.makeEventAlertAttr("message", 
                                                             "java.lang.String", 
                                                             "Repeater Power Restore");
                            }
                            else {
                                ea = EventUtil.makeEventAlertAttr("message", 
                                        "java.lang.String", 
                                        "Meter Power Restore");
                            }
                            event.append(ea);
                            ea = EventUtil.makeEventAlertAttr("isPowerFail", 
                                                         "java.lang.String", "false");
                            event.append(ea);
                            
                            if(meter != null) {
                                if (meter.getMeterStatus() != null && 
                                        !meter.getMeterStatus().getName().equals(MeterStatus.CutOff.name())) {
                                    String code = CommonConstants.getMeterStatusCode(MeterStatus.Normal);
                                    meter.setMeterStatus(CommonConstants.getMeterStatus(code));
                                }
                            }
                            
                            // 제주 실증단지 미터 이벤트 연동을 위해 추가
                            ea = EventUtil.makeEventAlertAttr("mRID", "java.lang.String", "3.26.9.216");
                            event.append(ea);
                            event.setStatus(EventStatus.Cleared);
                        }
                        ea = EventUtil.makeEventAlertAttr("meterId",
                                                     "java.lang.String",
                                                     unitSerial);
                        event.append(ea);
                        log.debug("Meter Power Recovery Action Started Alert Name[" + event.getEventAlert().getName() + "]");
                    }
                        
                    //boolean isPlannedPO = false;
        
                        /*
                        String searchdate = DateTimeUtil.getDateString(cal.getTime());
                        
                        PowerOutageMgrDelegate poMgr = new PowerOutageMgrDelegate();
                        
                        Map<String, String> map = new LinkedHashMap<String, String>( 16, 0.74f, false );
                        map.put("m.meter_id:=:str", unitSerial);
                        map.put("g.starttime:>:LongTime", searchdate);
                        map.put("g.endtime:<:LongTime", searchdate);
                        
                        isPlannedPO 
                            = poMgr.isPlannedPowerOutage(map,event.getLocationName());
        
                        if(!isPlannedPO && meter != null)
                        {
                            ArrayList alerts = FaultUtil.findOpenAlert(meter.getName(), event.getFaultClassName(), 1);
        
                            if (alerts == null || alerts.size() == 0)
                            {
                                // get modem event log
                                Map eventMap = CmdOperationUtil.getmodemEventLog(mcu.getPropertyValueString("id"),
                                                                                    modemId,
                                                                                    5, 1, "event");
                                String commandMethod = (String)eventMap.get("commandMethod");
                                if ("GetmodemEvent".equals(commandMethod)) {
                                    List eventList = (List)eventMap.get("eventLog");
                                    Collections.sort(eventList, new Comparator() {
                                        public int compare(Object o1, Object o2)
                                        {
                                            int comp = ((EventLog)o1).getGmtTime().compareTo(((EventLog)o2).getGmtTime());
                                            if (comp > 0)
                                                return -1;
                                            else if (comp < 0)
                                                return 1;
                                            else return 0;
                                        }
                                        
                                    });
                                    EventLog eventLog = null;
                                    Alert alert = FaultUtil.makeAlert(event);
                                    alert.setCloseTime(event.getTime());
                                    alert.setStatus(new Integer(2));
                                    
                                    for (int i = 0; i < eventList.size(); i++) {
                                        eventLog = (EventLog)eventList.get(i);
                                        if (isPowerRestore) {
                                            if (eventLog.getEventMsg().equals(EventLog.Event.PowerOutage.getMsg()[1])) {
                                                alert.setTime(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(eventLog.getGmtTime()).getTime());
                                            }
                                        }
                                        else if (isLineRestore) {
                                            if (eventLog.getEventMsg().equals(EventLog.Event.LXMissing.getMsg()[1])) {
                                                alert.setTime(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(eventLog.getGmtTime()).getTime());
                                            }
                                        }
                                    }
                                
                                    FaultUtil.createNotification(alert);
                                }
                            }
                            else
                            {
                                for (int i = 0; i < alerts.size(); i++)
                                {
                                    Alert alert = (Alert) alerts.get(i);
                                    alert.setCloseTime(event.getTime());
                                    alert.setStatus(new Integer(2));
                                    FaultUtil.updateNotification(alert);
                                }
                            }
                        }
                        log.debug("Meter Power Recovery Action End FaultClassName[" + event.getFaultClassName() + "]");
                    }
                    
                    */
                    if(isBatteryFault)
                    {
                        EventAlert eventAlert = eaDao.findByCondition("name", "Battery Alarm");
                        event.setEventAlert(eventAlert);
                        ea = EventUtil.makeEventAlertAttr("isBatteryFault", 
                                "java.lang.String", "true");
                        event.append(ea);
                        ea = EventUtil.makeEventAlertAttr("message", 
                                                     "java.lang.String", 
                                                     "Low Battery");
                        event.append(ea);
                    }
                    else if(isCaseAlarm)
                    {
                        EventAlert eventAlert = eaDao.findByCondition("name", "Case Alarm");
                        String message = "";
                        
                        if (eventAlert == null) {
                            eventAlert = eaDao.findByCondition("name", "Cover Alarm");
                        }
                        
                        event.setEventAlert(eventAlert);
                        ea = EventUtil.makeEventAlertAttr("isCaseOpen", 
                                "java.lang.String", isCaseOpen+"");
                        event.append(ea);
                        ea = EventUtil.makeEventAlertAttr("message", 
                                "java.lang.String", 
                                message + (isCaseOpen? "Open":"Close"));
                        if (isCaseOpen) {
                            event.setStatus(EventStatus.Open);
                            
                            message += disconnectRelay(mcu, meter);
                        }
                        else {
                            event.setStatus(EventStatus.Cleared);
                        }
                        event.append(ea);
                    }
                    else if(isMagneticTamper)
                    {
                        EventAlert eventAlert = eaDao.findByCondition("name", "Magnetic Tamper");
                        event.setEventAlert(eventAlert);
                        ea = EventUtil.makeEventAlertAttr("isMagnetic Tamper", 
                                "java.lang.String", "true");
                        event.append(ea);
                        ea = EventUtil.makeEventAlertAttr("message", 
                                "java.lang.String", 
                                "Magnetic Tamper");
                        event.append(ea);
                    }
                    else if(isTiltAlarm)
                    {
                        EventAlert eventAlert = eaDao.findByCondition("name", "Tilt Alarm");
                        event.setEventAlert(eventAlert);
                        ea = EventUtil.makeEventAlertAttr("isTiltAlarm", 
                                "java.lang.String", "true");
                        event.append(ea);
                        ea = EventUtil.makeEventAlertAttr("message", 
                                "java.lang.String", 
                                "Tilt Alarm(Handling Meter)");
                        event.append(ea);
                    }
                    else if (isDetachment)
                    {
                        EventAlert eventAlert = eaDao.findByCondition("name", "Attachment or Detachment");
                        event.setEventAlert(eventAlert);
                        ea = EventUtil.makeEventAlertAttr("isDetachment", "java.lang.String", "true");
                        event.append(ea);
                        ea = EventUtil.makeEventAlertAttr("message",
                                "java.lang.String",
                                "Detachment");
                        event.append(ea);
                    }
                    else if (isPulseCut)
                    {
                        EventAlert eventAlert = eaDao.findByCondition("name", "Pulse Cut");
                        event.setEventAlert(eventAlert);
                        ea = EventUtil.makeEventAlertAttr("isPulseCut", "java.lang.String", "true");
                        event.append(ea);
                        ea = EventUtil.makeEventAlertAttr("message",
                                "java.lang.String",
                                "Pulse Cut");
                        event.append(ea);
                        
                        if(meter != null) {
                            String code = CommonConstants.getMeterStatusCode(MeterStatus.CutOff);
                            meter.setMeterStatus(CommonConstants.getMeterStatus(code));
                        }
                    }
                    else if (isBatteryDischargingEnable)
                    {
                        EventAlert eventAlert = eaDao.findByCondition("name", "Battery Alarm");
                        event.setEventAlert(eventAlert);
                        ea = EventUtil.makeEventAlertAttr("isBatteryDischargingEnable", "java.lang.String", "true");
                        event.append(ea);
                        ea = EventUtil.makeEventAlertAttr("message",
                                "java.lang.String",
                                "Battery Discharging Enable");
                        event.append(ea);
                    }
                    else if (isBackPulseDetected)
                    {
                        EventAlert eventAlert = eaDao.findByCondition("name", "Back Pulse Alarm");
                        event.setEventAlert(eventAlert);
                        ea = EventUtil.makeEventAlertAttr("isBackPulseDetected", "java.lang.String", "true");
                        event.append(ea);
                        ea = EventUtil.makeEventAlertAttr("message",
                                "java.lang.String",
                                "Back Pulse Detected");
                        event.append(ea);
                    }
                    else if (isAttachment)
                    {
                        EventAlert eventAlert = eaDao.findByCondition("name", "Detachment");
                        event.setEventAlert(eventAlert);
                        ea = EventUtil.makeEventAlertAttr("isAttachment", "java.lang.String", "true");
                        event.append(ea);
                        ea = EventUtil.makeEventAlertAttr("message",
                                "java.lang.String",
                                "Attachment Detected");
                        event.append(ea);
                    }
                    else if (isValveAlarm) {
                        String evtAttrValue = event.getEventAttrValue("uintEntry").toString();
                        byte[] status = DataUtil.get4ByteToInt(Integer.parseInt(evtAttrValue));
                        
                        Code meterStatus = getMeterStatus(meter.getMeterType(), status[0]);   // Meter Status
                        String[] _alarmStatus = getMeterAlarmStatus(meter.getMeterType(), status[1]);   // Alarm Status
                        
                        log.debug("MeterStatus[" + meterStatus.getDescr() + "("+status[0] +
                                ")] AlarmStatus[" + _alarmStatus + "(" + status[1] + ")]");
                        
                        if (status[0] != 0) {
                            // 미터 상태를 변경한다.
                            if (meter.getMeterStatus() != null && 
                                    !meter.getMeterStatus().getName().equals(MeterStatus.CutOff.name())) {
                                meter.setMeterStatus(meterStatus);
                            }
                        }
                        
                        EventAlert eventAlert = eaDao.findByCondition("name", "");
                        event.setEventAlert(eventAlert);
                        ea = EventUtil.makeEventAlertAttr("isValveAlarm", "java.lang.String", "true");
                        event.append(ea);
                        ea = EventUtil.makeEventAlertAttr("message", "java.lang.String", 
                                "Meter[" + meterStatus+"] Alarm[" + _alarmStatus +"]");
                        event.append(ea);
                        
                        for (String alarm : _alarmStatus) {
                            EventUtil.sendEvent(alarm, meter, new EventAlertAttr[]{});
                        }
                    }

                }
                catch (Exception e)
                {
                    log.error(e,e);
                }
            }
            else {
                String mcuId = modem.getMcu().getSysID();
                if (mcuId == null || "null".equals(mcuId) || !trap.getMcuId().equals(mcuId)) {
                    modem.setMcu(mcu);
                }
              //  ModemCommandData mcd = new ModemCommandData();
                log.info("###############################ModemCommandData.getEvent start###############################################");
                Event e = ModemCommandData.getEvent((byte)alarmStatus);
                log.info("###############################ModemCommandData.getEvent end###############################################");
                // ModemCommandData.Event e = ModemCommandData.getEvent((byte)alarmStatus);
                String evtAttrValue = event.getEventAttrValue("uintEntry").toString();
                byte[] eventStatus = DataUtil.get4ByteToInt(Long.parseLong(evtAttrValue));
                DataUtil.convertEndian(eventStatus);
                log.debug(Hex.decode(eventStatus));
                
                EventAlert eventAlert = eaDao.findByCondition("name", "Equipment Notification");
                event.setEventAlert(eventAlert);
                event.append(EventUtil.makeEventAlertAttr("message",
                        "java.lang.String", 
                        e.getDescr() + "[" + (ModemCommandData.isEventStatus(e.getId(), eventStatus)? "ON":"OFF") + "]" +
                        " Temp[" + ModemCommandData.getTemperatureAlarmLevel(new byte[]{eventStatus[2], eventStatus[3]}) + "]"));
                
                String sendToAlarmSW = FMPProperty.getProperty("send.to.alarmsw") == null? 
                        "false":FMPProperty.getProperty("send.to.alarmsw");
                log.info("###############################modemType.equals(ModemType.ZMU):"+modemType.equals(ModemType.ZMU)+"###############################################");
                log.info("###############################sendToAlarmSW:"+sendToAlarmSW+"###############################################");
                if (modemType.equals(ModemType.ZMU) && "true".equals(sendToAlarmSW)) {
                    try {
                        
                        /*
                        CmdUtil.sendAlarmEvent("sendAlarmEvent", 
                                new Object[] {modemId, new Byte((byte)alarmStatus),
                                new Integer(DataUtil.getIntTo4Byte(eventStatus)), timestamp},
                                new String[] {String.class.getName(), Byte.class.getName(), 
                                Integer.class.getName(), String.class.getName()});
                                
                                */
                        
//                        EventUtil.sendEvent(eventAlert.getName(), TargetClass.Modem, modemId,
//                              timestamp, new String[][] {}, event);
                        
                    }
                    catch (Exception ex) {
                        log.error(ex);
                    }
                }
            }
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }

        log.debug("modem Alarm Event Action Compelte");
    }
    
    private String disconnectRelay(MCU mcu, Meter meter) {
        // 2013.09.12 ECG 요구사항 Cover Open시 Cut Off
        String message = "";
        
        if (meter != null && Boolean.parseBoolean(FMPProperty.getProperty("case.alarm.cut.off", "false"))) {
            String meterIdList = FMPProperty.getProperty("case.alarm.cut.off.meters");
            
            boolean isCutOff = false;
            if (meterIdList != null && !"".equals(meterIdList)) {
                isCutOff = meterIdList.contains(meter.getMdsId());
            }
            else {
                isCutOff = true;
            }
            
            if (isCutOff) {
                Protocol protocol = Protocol.valueOf(mcu.getProtocolType().getName());
                
                try {
                    CommandWS command = getCommandWS(protocol);
                    ResponseMap response = command.relayValveOff(mcu.getSysID(), meter.getMdsId());
                    meter = meterDao.get(meter.getId());
                    
                    if (meter.getMeterStatus().getName().equals(MeterStatus.CutOff.name())) {
                        if (meter.getContract() != null)
                            SMSNotification(meter.getContract(), "The Power was blocked for cover open");
                    }
                    
                    message = (String)response.getResponse().getEntry().get(0).getValue();
                }
                catch (Exception e) {
                    message = e.getMessage();
                }
            }
        }
        
        return message;
    }
    
    /**
     * method name : SMSNotification
     * method Desc : Charge에 대한 SMS 통보
     * @param contract, message
     */
    private void SMSNotification(Contract contract, String message) {
        try {
            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("prepayCreditId", codeDao.getCodeIdByCode("2.2.1"));
            condition.put("emergencyICreditId", codeDao.getCodeIdByCode("2.2.2"));
            condition.put("smsYn", true);
            condition.put("contractId", contract.getId());
            List<Map<String, Object>> contractInfo = contractDao.getContractSMSYN(condition);
            
            if(contractInfo.size() > 0) {
                String mobileNo = contractInfo.get(0).get("MOBILENO").toString().replace("-", "");
                
                Supplier supplier = contract.getSupplier();
                DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());
                String text = null;
                text =  message
                        + "\n Customer Name : " + contractInfo.get(0).get("CUSTOMERNAME")
                        + "\n Supply Type : " + contractInfo.get(0).get("SERVICETYPE")
                        + "\n Current Credit : " +  cdf.format(Double.parseDouble(contractInfo.get(0).get("CURRENTCREDIT").toString())).toString();
            
//                Properties prop = new Properties();
//    			prop.load(getClass().getClassLoader().getResourceAsStream("config/fmp.properties"));
                
                Properties prop = FMPProperty.getProperties();                
				String smsClassPath = prop.getProperty("smsClassPath");
				SendSMS obj = (SendSMS) Class.forName(smsClassPath).newInstance();
				
				Method m = obj.getClass().getDeclaredMethod("send", String.class, String.class, Properties.class);
				String messageId = (String) m.invoke(obj, mobileNo, text, prop);
				
				if(!"".equals(messageId)) {
					contractDao.updateSmsNumber(contract.getId(), messageId);
				} 
            }
        } catch (Exception e) {
            log.error(e,e);
        }
    }
    
    private int getPowerState(int alarmStatus, EventAlertLog event) {
        String evtAttrValue = event.getEventAttrValue("uintEntry").toString();
        byte[] status = DataUtil.get4ByteToInt(Integer.parseInt(evtAttrValue));
        DataUtil.convertEndian(status);
        log.debug(Hex.decode(status));
        
        int isPower = 0;
        if (alarmStatus == 0)
            isPower = DataUtil.getIntToByte(status[3]);
        else if (alarmStatus == 3)
            isPower = DataUtil.getIntToByte(status[0]);
        
        return isPower;
    }
    
    private boolean getCaseState(int alarmStatus, EventAlertLog event) {
    	if (event.getEventAttrValue("uintEntry") == null)
    		return true;
    	
    	String evtAttrValue = event.getEventAttrValue("uintEntry").toString();
    	byte[] status = DataUtil.get4ByteToInt(Integer.parseInt(evtAttrValue));
        DataUtil.convertEndian(status);
        log.debug(Hex.decode(status));
        
    	int isCase = DataUtil.getIntToByte(status[3]);
    	
    	if (isCase == 1)
    		return true;

    	return false;
    }
    
    private int getDetachmentState(EventAlertLog event) {
        String evtAttrValue = event.getEventAttrValue("uintEntry").toString();
        byte[] status = DataUtil.get4ByteToInt(Integer.parseInt(evtAttrValue));
        DataUtil.convertEndian(status);
        if (log.isDebugEnabled())
            log.debug(Hex.decode(status));

        return DataUtil.getIntToByte(status[3]);
    }
    
    private Code getMeterStatus(Code meterType, byte status) {
        if (meterType.getName().equals(MeterType.GasMeter.name()))
            return CommonConstants.getGasMeterStatus((int)status+3000+"");
        else if (meterType.getName().equals(MeterType.WaterMeter.name()))
            return CommonConstants.getWaterMeterStatus((int)status+2000+"");
        else
            return CommonConstants.getMeterStatus(status+"");
    }
    
    private String[] getMeterAlarmStatus(Code meterType, byte status) {
        Hashtable<String, Code> codes = null;
        if (meterType.getName().equals(MeterType.GasMeter.name()))
            codes = CommonConstants.getGasMeterAlarmStatusCodes();
        else if (meterType.getName().equals(MeterType.WaterMeter.name()))
            codes = CommonConstants.getWaterMeterAlarmStatusCodes();
        
        StringBuffer buf = new StringBuffer();
        
        List<String> alarmList = new ArrayList<String>();
        String key = null;
        for (Enumeration<String> e = codes.keys(); e.hasMoreElements(); ) {
            if (buf.length() != 0)
                buf.append(" ");
            
            key = e.nextElement();
            
            // Open인 경우만 표현한다.
            if ((Byte.parseByte(key) & status) != 0x00) {
                alarmList.add(codes.get(key).getDescr());
            }
        }
        
        return alarmList.toArray(new String[0]);
    }
    
    public static CommandWS getCommandWS(Protocol protocol) throws IOException {
        CommandWS_Service ss = new CommandWS_Service(getURL(protocol));
        CommandWS port = ss.getCommandWSPort();
        
        Client client = ClientProxy.getClient(port);
        HTTPConduit http = (HTTPConduit)client.getConduit();
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setReceiveTimeout(300000);
        httpClientPolicy.setAllowChunking(false);
        http.setClient(httpClientPolicy);
        
        return port;
    }

    private static URL getURL(Protocol protocol) throws MalformedURLException {
        String url = null;
        if (protocol != null)
            url = (String)FMPProperty.getProperty("fep.ws." +protocol.name());
        
        if (url == null || "".equals(url))
            url = (String)FMPProperty.getProperty("fep.ws");
        
        return new URL(url);
    }
    
    private Protocol getProtocolType(String mcuId, String meterId, String modemId) {
        MCU mcu = null;
        if (mcuId != null && !"".equals(mcuId))
            mcu = mcuDao.get(mcuId);
        
        if (mcu != null)
            return Protocol.valueOf(mcu.getProtocolType().getName());
        
        Meter meter = null;
        if (meterId != null && !"".equals(meterId)) {
            meter = meterDao.get(meterId);
            if (meter != null && meter.getModem() != null)
                return meter.getModem().getProtocolType();
        }
        
        Modem modem = null;
        if (modemId != null && !"".equals(modem)) {
            modem = modemDao.get(modemId);
            
            if (modem != null)
                return modem.getProtocolType();
        }
        
        return Protocol.LAN;
    }
}
