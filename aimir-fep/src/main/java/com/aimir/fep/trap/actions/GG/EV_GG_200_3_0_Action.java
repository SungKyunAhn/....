package com.aimir.fep.trap.actions.GG;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.device.EventAlertDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlert;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : GG_200.3.0 (SUNI Firmware upgrade result)
 * 
 * @author elevas
 * @version $Rev: 1 $, $Date: 2016-06-01 15:59:15 +0900 $,
 */
@Component
public class EV_GG_200_3_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_GG_200_3_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    EventAlertDao eaDao;    
    
    EventAlertAttr ea = null;
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("Event descr[" + event.toString() + "]");
        String activatorid = event.getActivatorId();        
        String sEventTime = event.getOpenTime();

        Modem modem = null;
        Meter meter = null;
        
        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
        
            // 이벤트 발생지가 모뎀일경우
            switch (event.getActivatorType()) {
            case Modem :
            case MMIU :           
                modem = modemDao.get(activatorid);
                if(modem == null){
                    log.debug("no modem instance exist[" + activatorid + "]");
                    return;
                }
                
                if (modem.getMeter() != null && modem.getMeter().size() > 0) 
                    meter = modem.getMeter().toArray(new Meter[0])[0];
                
                event.setLocation(modem.getLocation());
                
                ModemType modemType = modem.getModemType();
                ea = EventUtil.makeEventAlertAttr("modemType", 
                        "java.lang.String", modemType.name());
                event.append(ea);
                break;
            default :
                meter = meterDao.get(activatorid);
            }
            
            // 미터가 있으면 발생 장비 정보를 미터로 변경한다.
            if (meter != null) {
                event.setActivatorId(meter.getMdsId());
                event.setActivatorType(meter.getMeterType().getName());
                if (meter.getLocation() != null)
                    event.setLocation(meter.getLocation());
            }
            
            int status = Integer.parseInt(event.getEventAttrValue("evtSuniUpgradeStatus"));
            
            EventAlert eventAlert = eaDao.findByCondition("name", "OTA");
            event.setEventAlert(eventAlert);
            ea = EventUtil.makeEventAlertAttr("message", 
                    "java.lang.String", status == 0? "Success":"Fail");
            event.append(ea);
            
            ea = EventUtil.makeEventAlertAttr("otaStep", "java.lang.String", "Result");
            event.append(ea);
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
        log.debug("Modem Event Action Compelte");
    }
    
    public void procCaseEvent(int caseStatus, String sEventTime, EventAlertLog event){

        ModemCaseEvent modemEvent = ModemCaseEvent.getEvent(caseStatus);
        log.debug("Event Time[" + sEventTime + "] Code[" + caseStatus + "] Name[" + modemEvent.getEventName());
        
        if (modemEvent == ModemCaseEvent.CASEOPEN)
        {
            log.debug("ModemEvent.CASEOPEN");
            EventAlert eventAlert = eaDao.findByCondition("name", "Case Alarm");
            if (eventAlert == null) {
                eventAlert = eaDao.findByCondition("name", "Cover Alarm");
            }
            event.setEventAlert(eventAlert);
            ea = EventUtil.makeEventAlertAttr("isCaseOpen", "java.lang.String", true+"");
            event.append(ea);
            ea = EventUtil.makeEventAlertAttr("message", "java.lang.String", "Open");
            event.append(ea);
            log.debug(event.getEventAlertAttrs());
        }
        else if (modemEvent == ModemCaseEvent.CASECLOSE)
        {
            log.debug("ModemEvent.CASECLOSE");
            EventAlert eventAlert = eaDao.findByCondition("name", "Case Alarm");
            if (eventAlert == null) {
                eventAlert = eaDao.findByCondition("name", "Cover Alarm");
            }
            event.setEventAlert(eventAlert);
            ea = EventUtil.makeEventAlertAttr("isCaseOpen", "java.lang.String", false+"");
            event.append(ea);
            ea = EventUtil.makeEventAlertAttr("message", "java.lang.String", "Close");
            event.append(ea);
            event.setStatus(EventStatus.Cleared);
            log.debug(event.getEventAlertAttrs());
        } 
    }
    
    public void procDoorEvent(int doorStatus, String sEventTime, EventAlertLog event){

        ModemDoorEvent modemEvent = ModemDoorEvent.getEvent(doorStatus);
        log.debug("Event Time[" + sEventTime + "] Code[" + doorStatus + "] Name[" + modemEvent.getEventName());
        
        if (modemEvent == ModemDoorEvent.DOOROPEN)
        {
            log.debug("ModemEvent.DOOROPEN");
            EventAlert eventAlert = eaDao.findByCondition("name", "Door Alarm");
            event.setEventAlert(eventAlert);
            ea = EventUtil.makeEventAlertAttr("isDoorOpen", "java.lang.String", true+"");
            event.append(ea);
            ea = EventUtil.makeEventAlertAttr("message", "java.lang.String", "Open");
            event.append(ea);
            log.debug(event.getEventAlertAttrs());
        }
        else if (modemEvent == ModemDoorEvent.DOORCLOSE)
        {
            log.debug("ModemEvent.DOORCLOSE");
            EventAlert eventAlert = eaDao.findByCondition("name", "Door Alarm");
            event.setEventAlert(eventAlert);
            ea = EventUtil.makeEventAlertAttr("isDoorOpen", "java.lang.String", false+"");
            event.append(ea);
            ea = EventUtil.makeEventAlertAttr("message", "java.lang.String", "Close");
            event.append(ea);
            event.setStatus(EventStatus.Cleared);
            log.debug(event.getEventAlertAttrs());
        } 
    }
    
    enum ModemCaseEvent {
        UNKNOWN(0, "Unknown"),
        CASEOPEN(1, "Case Open"),
        CASECLOSE(2, "Case Close");
        
        int eventCode;
        String eventName;
        ModemCaseEvent(int eventCode, String eventName) {
            this.eventCode = eventCode;
            this.eventName = eventName;
        }
        
        public String getEventName() {
            return this.eventName;
        }
        
        public static ModemCaseEvent getEvent(int eventCode) {
            for (int i = 0; i < ModemCaseEvent.values().length; i++) {
                if (ModemCaseEvent.values()[i].eventCode == eventCode)
                    return ModemCaseEvent.values()[i];
            }
            return UNKNOWN;
        }
    }
    
    enum ModemDoorEvent {
        DOORCLOSE(0, "Door Close"),
        DOOROPEN(1, "Door Open");
        
        int eventCode;
        String eventName;
        ModemDoorEvent(int eventCode, String eventName) {
            this.eventCode = eventCode;
            this.eventName = eventName;
        }
        
        public String getEventName() {
            return this.eventName;
        }
        
        public static ModemDoorEvent getEvent(int eventCode) {
            for (int i = 0; i < ModemDoorEvent.values().length; i++) {
                if (ModemDoorEvent.values()[i].eventCode == eventCode)
                    return ModemDoorEvent.values()[i];
            }
            return null;
        }
    }
}
