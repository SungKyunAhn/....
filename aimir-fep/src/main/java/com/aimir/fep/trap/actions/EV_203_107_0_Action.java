package com.aimir.fep.trap.actions;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.dao.device.EventAlertDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlert;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.notification.FMPTrap;
import com.aimir.util.DateTimeUtil;

/**
 * a large eventSensorPowerOutage 
 * (event 0:power fail,1:battery fault, 2:power restore, 3:line missing)
 * byteEntry (1)
 * gmtEntry  (11)
 * sensorID  (EUI64*n)
 * Event ID : 203.107.0 Processing Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Component
public class EV_203_107_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_203_107_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    EventAlertDao eaDao;

    /**
     * execute event action
     * event code : 0:power failure, 1: battery fault, 2:power restore, 3:phase error
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EV_203_107_0_Action : EventName[eventSensorPowerOutage] "
                +" EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");
        
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            
            MCU mcu = mcuDao.get(trap.getMcuId());
            
            if (mcu == null)
            {
                log.debug("no mcu["+trap.getMcuId()+"] intance exist");
                return;
            }
            String mcuInstName = mcu.getSysID();

            boolean isPowerFail = false;
            boolean isPowerRestore = false;
            boolean isBatteryFault = false;
            boolean isLineMissing = false;
            String eventClassName = "";
            int eventStatus = 0;
            String bit = event.getEventAttrValue("byteEntry");
            eventStatus = Integer.parseInt(bit);
            switch (eventStatus) {
            case 0 : isPowerFail = true;
                     eventClassName = "Power Alarm";
                     break;
            case 1 : isBatteryFault = true;
                     eventClassName = "Battery Alarm";
                     break;
            case 2 : isPowerRestore = true;
                     eventClassName = "Power Alarm";
                     break;
            case 3 : isLineMissing = true;
                     eventClassName = "Power Alarm";
                     break;
            }
            
            String timestamp = "";
            
            String str = event.getEventAttrValue("gmtEntry");
            String gmtTimezone = "gmtTimezone: ";
            String gmtDstValue = "gmtDstValue: ";
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
            int month = Integer.parseInt(_mon);
            int day = Integer.parseInt(_day);
            int hour = Integer.parseInt(_hour);
            int min = Integer.parseInt(_min);
            int sec = Integer.parseInt(_sec);
            
            log.debug("YEAR[" + year + "] MONTH[" + month + "] DAY[" + day + "]" +
                      "HOUR[" + hour + "] MINUTE[" + min + "] SECOND[" + sec + "]");
            
            timestamp = DateTimeUtil.getDST(null, year + (month < 10? "0"+month:""+month) +
                        (day < 10? "0"+day:""+day) + (hour < 10? "0"+hour:hour) +
                        (min < 10? "0"+min:""+min) + (sec < 10? "0"+sec:sec));

            if (timestamp.length() != 14) {
                timestamp = trap.getTimeStamp();
            }
            /*
            MIBUtil mibUtil = MIBUtil.getInstance();
            String oid = mibUtil.getOid("gmtEntry").toString();

            byte[] bx = ((OCTET)trap.getVarBinds().get(oid)).getValue();

            OPAQUE opaque = (OPAQUE) trap.getVarBinds().get(oid);
            byte[] bx = opaque.encode();

            TIMESTAMP timestamp1 = new TIMESTAMP(bx);
            log.debug(timestamp1.getValue());
            */
            event.remove("gmtEntry");
            
            /*
            Calendar cal = Calendar.getInstance();
            long currentTime = cal.getTimeInMillis();
            long time = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(timestamp).getTime();
            if (time > (currentTime + 7200000) || time < (currentTime - 7200000))
                throw new Exception("Event time[" + timestamp + "] is wrong");
                */
            event.setOpenTime(timestamp);
            
            String modemArray = event.getEventAttrValue("sensorID");
            event.remove("sensorID");
            
            int eui_len = 16;
            EventAlertAttr ea = null;
            
            int cnt = 0;
            boolean isPlannedPO = false;
            if(modemArray != null && modemArray.length() >= eui_len){
                cnt = modemArray.length()/eui_len;
                
                EventAlert eventAlert = eaDao.findByCondition("name", eventClassName);
                event.setEventAlert(eventAlert);
                
                if (isPowerFail) {
                    ea = EventUtil.makeEventAlertAttr("message", 
                                                 "java.lang.String", 
                                                 "Massive Power Failure, Meter Count("+ cnt +")");
                    for (int i = 0; i < modemArray.length(); i+=eui_len) {
                        Modem modem = modemDao.get(modemArray.substring(i, i+eui_len));
                        if (modem != null && modem.getMeter() != null) {
                            for (Meter m : modem.getMeter().toArray(new Meter[0])) {
                                if (m.getMeterStatus() != null && 
                                        !m.getMeterStatus().getName().equals(MeterStatus.CutOff.name())) {
                                    m.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.PowerDown.name()));
                                    meterDao.update(m);
                                }
                            }
                        }
                    }
                }
                else if (isPowerRestore) {
                    ea = EventUtil.makeEventAlertAttr("message", 
                                                 "java.lang.String", 
                                                 "Massive Power Restore, Meter Count("+ cnt +")");
                    for (int i = 0; i < modemArray.length(); i+=eui_len) {
                        Modem modem = modemDao.get(modemArray.substring(i, i+eui_len));
                        if (modem != null && modem.getMeter() != null) {
                            for (Meter m : modem.getMeter().toArray(new Meter[0])) {
                                if (m.getMeterStatus() != null && 
                                        !m.getMeterStatus().getName().equals(MeterStatus.CutOff.name())) {
                                    m.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.Normal.name()));
                                    meterDao.update(m);
                                }
                            }
                        }
                    }
                }
                
                event.append(ea);
                
                for(int i = 0; i < cnt; i++){
                    String modemId = modemArray.substring(i*eui_len, (i+1)*eui_len);

                    ea = getEventAlertAttr(modemId,eventStatus,event);
                    if (ea == null)
                        continue;
                    
                    event.append(ea);
                    isPlannedPO = checkPlannedPO(modemId, event);
                }
            }
            
            log.debug("Sensor Alarm Event[Massive power failure/restore/battery fault/phase Error] Action Compelte");
            
            if(!isPlannedPO){
                // String message = "count["+cnt+"]"+event.getMessage();
                // event.setMessage(message);
                if (isPowerRestore) {
                    event.setStatus(EventStatus.Cleared);
                }
            }
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }
    
    private boolean checkPlannedPO(String modemId, EventAlertLog event)
    {
        boolean isPlannedPO = false;
        try{
            
            Modem modem = modemDao.get(modemId);

            if (modem == null)
            {
                log.debug("sensor["+modemId+"] is not exist");
                return false;
            }
                                       
            
            String modemPort = event.getEventAttrValue("modemPort");
            // Meter meter = meterDao.getMeterByModemDeviceSerial(modemId, modemPort != null? Integer.parseInt(modemPort):0);
                                                   
            String searchdate = DateTimeUtil.getDateString(new Date());
            
            //TODO IMPLEMENT
            /*
            PowerOutageMgrDelegate poMgr = new PowerOutageMgrDelegate();
            
            Map<String, String> map = new LinkedHashMap<String, String>( 16, 0.74f, false );
            map.put("m.meter_id:=:str", meter.getPropertyValueString("id"));
            map.put("g.starttime:>:LongTime", searchdate);
            map.put("g.endtime:<:LongTime", searchdate);
            isPlannedPO 
            = poMgr.isPlannedPowerOutage(map,event.getLocationName());
            */
        }catch(Exception e){
            log.warn("EV_203_107_0_Action : checkPlannedPO failed. ",e);
        }
        return isPlannedPO;
    }
    
    
    private EventAlertAttr getEventAlertAttr(String modemId, int status, EventAlertLog event){
        
        try{
            Modem modem = modemDao.get(modemId);

            if (modem == null)
            {
                log.debug("Modem["+modemId+"] is not exist");
                return null;
            }
            
            String modemPort = event.getEventAttrValue("modemPort");
            /*
            Meter meter = meterDao.getMeterByModemDeviceSerial(modemId, (modemPort != null && !modemPort.equals(""))? Integer.parseInt(modemPort):0);
            if(meter != null){
                if(status == 0){
                    meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.PowerDown.name()));
                }
                else if (status == 2){
                    meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.Normal.name()));
                    EventAlertLog alert = EventUtil.findOpenAlert(event);

                    if (alert != null)
                    {
                        alert.setCloseTime(event.getOpenTime());
                        alert.setStatus(EventStatus.Cleared);
                    }
                }
                return EventUtil.makeEventAlertAttr("meterId",
                                           "java.lang.String",
                                           meter.getMdsId());
            }
            */
                                                          
        }catch(Exception e){
            log.warn("EV_203_107_0_Action : getEventAttr failed. ",e);
        }
        return null;
    }
}
