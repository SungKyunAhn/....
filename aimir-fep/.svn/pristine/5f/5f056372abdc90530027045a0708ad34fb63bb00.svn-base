package com.aimir.fep.trap.actions.GG;

import java.util.List;

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
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.fep.modem.EventLog;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.EventAlert;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : GG_200.2.1 (GPRS Modem Case Open)
 * <br>GPRS Modem
 * 
 * @author goodjob
 * @version $Rev: 1 $, $Date: 2014-10-28 15:59:15 +0900 $,
 */
@Component
public class EV_GG_200_2_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_GG_200_2_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
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
    
    EventAlertAttr ea = null;
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("Meter Event descr[" + event.toString() + "]");
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
            
            if(event.getEventAttrValue("evtCaseStatus") != null && !"".equals(event.getEventAttrValue("evtCaseStatus"))){
                int caseStatus = Integer.parseInt(event.getEventAttrValue("evtCaseStatus"));
                procCaseEvent(caseStatus, sEventTime, event);
            }
            if(event.getEventAttrValue("evtDoorStatus") != null && !"".equals(event.getEventAttrValue("evtDoorStatus"))){
                int caseStatus = Integer.parseInt(event.getEventAttrValue("evtDoorStatus"));
                procDoorEvent(caseStatus, sEventTime, event);
            }
            else if(event.getEventAttrValue("evtLineMissing") != null && !"".equals(event.getEventAttrValue("evtLineMissing"))){
    
                boolean isLineMissing = false;
                boolean isLineRestore = false;
                
                String evtAttrValue = event.getEventAttrValue("evtLineMissing").toString();
                byte[] lineMissingStatus = DataUtil.get4ByteToInt(Integer.parseInt(evtAttrValue));
                DataUtil.convertEndian(lineMissingStatus);
                log.debug(Hex.decode(lineMissingStatus));
                
                int isPower = 0;
                isPower = DataUtil.getIntToByte(lineMissingStatus[0]);
                
                if (isPower == 1)
                    isLineMissing = true;
                else if (isPower == 2)
                    isLineRestore = true;
                
                if(isLineMissing){
                    EventAlert eventAlert = eaDao.findByCondition("name", "Power Alarm");
                    event.setEventAlert(eventAlert);
                    ea = EventUtil.makeEventAlertAttr("isLineMissing", 
                            "java.lang.String", "true");
                    event.append(ea);
                    ea = EventUtil.makeEventAlertAttr("message",
                            "java.lang.String",
                            "Line Missing " +
                            EventLog.getMsg(evtAttrValue));
                    event.append(ea);
                }
                
                if(isLineRestore){
                    EventAlert eventAlert = eaDao.findByCondition("name", "Power Alarm");
                    event.setEventAlert(eventAlert);
                    
                    if (isLineRestore) {
                        ea = EventUtil.makeEventAlertAttr("isLineRestore", 
                                                     "java.lang.String", "true");
                        event.append(ea);
                        ea = EventUtil.makeEventAlertAttr("message",
                                                     "java.lang.String",
                                                     "Line Restore " +
                                                     EventLog.getMsg(evtAttrValue));
                        event.append(ea);
                        event.setStatus(EventStatus.Cleared);
                    }
                }
            }
            if(event.getEventAttrValue("evtRelayStatus") != null && !"".equals(event.getEventAttrValue("evtRelayStatus"))){
                
                String relayStatusStr =  event.getEventAttrValue("evtRelayStatus").toString();
                log.info("evtRelayStatus[" + relayStatusStr +"]");
                int relayStatusInt = Integer.parseInt(relayStatusStr);
                log.info("int change evtRelayStatus[" + relayStatusInt +"]");
                String relayStatusMsg = null;
                
                boolean relayOn = false;
                boolean relayOff =false;
                
                if(relayStatusInt == 0) {
                	relayStatusMsg = "Not yet Read";	
                } else if(relayStatusInt == 1) {
                	relayStatusMsg = "Relays disconnected by command";
                	relayOff = true;
                } else if(relayStatusInt == 4) {
                	relayStatusMsg = "Relays connected";
                	relayOn = true;
                } else if(relayStatusInt == 8) {
                	relayStatusMsg = "Relays disconnected by push button";
                	relayOff = true;
                } else if(relayStatusInt == 11) {
                	relayStatusMsg = "Relays released for reconnection";
                }
                log.info("relayStatusMsg[" + relayStatusMsg +"]");                
                Code meterStatus = null;
                Code contractStatus = null;
                if(relayOn) { 
                	meterStatus = codeDao.getMeterStatusCodeByName("Normal");
                	contractStatus = codeDao.getMeterStatusCodeByName("Normal","Status");
                }
                if(relayOff) {
                	meterStatus = codeDao.getMeterStatusCodeByName("CutOff");
                	contractStatus = codeDao.getMeterStatusCodeByName("Temporary Pause","Status");
                }
                
                meter.setMeterStatus(meterStatus);
                meterDao.update(meter);
                log.info("Update meter=" + meter.getMdsId());
                
                List<Contract> contractList = contractDao.getContractByMeterId(meter.getId());
                if(contractList.size() > 0) {
                	Contract contract = contractList.get(0);
                	contract.setStatus(contractStatus);
                	contractDao.update(contract);
                	log.info("Update contract=" + contract.getContractNumber());
                }
                
                
            }
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
