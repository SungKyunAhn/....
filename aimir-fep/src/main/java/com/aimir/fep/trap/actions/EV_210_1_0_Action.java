package com.aimir.fep.trap.actions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.device.EventAlertDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.modem.EventLog;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlert;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 207.1.0 Processing Class
 * Event for High Power Modem
 * 
 * @author J.S Park
 * @version $Rev: 1 $, $Date: 2008-01-23 15:59:15 +0900 $,
 */
@Component
public class EV_210_1_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_210_1_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    EventAlertDao eaDao;
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug(" Event descr[" + event.toString() + "]");

        int retryCount = Integer.parseInt(event.getEventAttrValue("modemPacketRetryCount"));
        String meterId = event.getEventAttrValue("modemMeterID");

        
        Modem modem = null;
        Meter meter = null;
        EventAlertAttr ea = null;
        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            
            // 이벤트 발생지가 모뎀일경우
            switch (event.getActivatorType()) {
            case Modem :
            case MMIU :
            case IEIU :
            case ZRU :
            case ZEUPLS :
            case ZEUMBus :
            case ZBRepeater :
            case ZMU :
            case HMU :
            case Converter :
                String activatorid = event.getActivatorId();
                modem = modemDao.get(activatorid);
                if(modem == null){
                    log.debug("no modemr instance exist[" + meterId + "]");
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
                meter = meterDao.get(meterId);
            }
            
            // 미터가 있으면 발생 장비 정보를 미터로 변경한다.
    		if (meter != null) {
    			event.setActivatorId(meter.getMdsId());
    			event.setActivatorType(meter.getMeterType().getName());
    			if (meter.getLocation() != null)
    			    event.setLocation(meter.getLocation());
    		}
    		
            EventAlertAttr[] eventAttrs = event.getEventAlertAttrs().toArray(new EventAlertAttr[0]);
            List<String> eventTime = new ArrayList<String>();event.getEventAttrValue("modemEventTime");
            List<String> eventCode = new ArrayList<String>();Integer.parseInt(event.getEventAttrValue("modemEventCode"));
            List<String> eventCodeE3 = new ArrayList<String>();
            
            for (int i = 0; i < eventAttrs.length; i++) {
                if (eventAttrs[i].getAttrName().indexOf("modemEventTime") != -1)
                    eventTime.add(eventAttrs[i].getValue());
                if (eventAttrs[i].getAttrName().indexOf("modemEventCode") != -1)
                    eventCode.add(eventAttrs[i].getValue());                
                if (eventAttrs[i].getAttrName().indexOf("modemEventCodeE3") != -1) //Elster A1140 E3이벤트 추가분
                	eventCodeE3.add(eventAttrs[i].getValue());
                
            }
            
            int iEventCode = 0;
            String sEventTime = null;
            ModemEvent modemEvent = null;
            
            Calendar cal = Calendar.getInstance();
            long time = 0;
            int E3CNT = 0;
            
            for (int i = 0; i < eventTime.size(); i++) {
                sEventTime = (String)eventTime.get(i);
                iEventCode = Integer.parseInt((String)eventCode.get(i));
                modemEvent = ModemEvent.getEvent(iEventCode);
                log.debug("Event Time[" + sEventTime + "] Code[" + iEventCode + "] Name[" + modemEvent.getEventName());
                
                if(modemEvent == ModemEvent.POWERFAIL)
                {
                    EventAlert eventAlert = eaDao.findByCondition("name", "Power Alarm");
                    event.setEventAlert(eventAlert);
                	ea = EventUtil.makeEventAlertAttr("message", 
                            "java.lang.String", 
                            "Meter Power Failure");
                    event.append(ea);
                    ea = EventUtil.makeEventAlertAttr("isPowerFail", 
                                                 "java.lang.String", "true");
                    event.append(ea);
                    
                    if(meter != null) {
                        if (meter.getMeterStatus() != null && 
                                !meter.getMeterStatus().getName().equals(MeterStatus.CutOff.name())) {
                        	String code = CommonConstants.getMeterStatusCode(MeterStatus.PowerDown);
                        	meter.setMeterStatus(CommonConstants.getMeterStatus(code));
                        }
                    }
                    ea = EventUtil.makeEventAlertAttr("meterId",
                            "java.lang.String",
                            meterId);
                    event.append(ea);
                    log.debug("Meter Power Failure Action Started Alert Name[" + event.getEventAlert().getName() + "]");
                } 
                else if (modemEvent == ModemEvent.POWERRESTORE)
                {
                    EventAlert eventAlert = eaDao.findByCondition("name", "Power Alarm");
                    event.setEventAlert(eventAlert);
                	ea = EventUtil.makeEventAlertAttr("message", 
                            "java.lang.String", 
                            "Meter Power Restore");
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
                    ea = EventUtil.makeEventAlertAttr("meterId",
                            "java.lang.String",
                            meterId);
                    event.append(ea);
                    event.setStatus(EventStatus.Cleared);
                    
                    log.debug("Meter Power Recovery Action Started Alert Name[" + event.getEventAlert().getName() + "]");
    
                }
                else if (modemEvent == ModemEvent.LINEMISSING)
                {
                    EventAlert eventAlert = eaDao.findByCondition("name", "Power Alarm");
                    event.setEventAlert(eventAlert);
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
                else if (modemEvent == ModemEvent.DOOROPEN)
                {
                	log.debug("ModemEvent.DOOROPEN");
                    EventAlert eventAlert = eaDao.findByCondition("name", "Door Alarm");
                    event.setEventAlert(eventAlert);
                    ea = EventUtil.makeEventAlertAttr("isDoorOpen", 
                            "java.lang.String", true+"");
                    event.append(ea);
                    // ea = EventUtil.makeEventAlertAttr("message", 
                    //        "java.lang.String", "Case Open");
                    // event.append(ea);
                    
                    ea = EventUtil.makeEventAlertAttr("doorState", "java.lang.String", "Open");
                    event.append(ea);
                    log.debug(event.getEventAlertAttrs());
                }
                else if (modemEvent == ModemEvent.DOORCLOSE)
                {
                	log.debug("ModemEvent.DOORCLOSE");
                    EventAlert eventAlert = eaDao.findByCondition("name", "Door Alarm");
                    event.setEventAlert(eventAlert);
                    ea = EventUtil.makeEventAlertAttr("isDoorOpen", 
                            "java.lang.String", false+"");
                    event.append(ea);
                    // ea = EventUtil.makeEventAlertAttr("message", 
                    //        "java.lang.String", "Case Close");
                    // event.append(ea);
                    
                    ea = EventUtil.makeEventAlertAttr("doorState", "java.lang.String", "Close");
                    event.append(ea);
                    event.setStatus(EventStatus.Cleared);
                    log.debug(event.getEventAlertAttrs());
                } else if(modemEvent == ModemEvent.COVEROPEN){
    
                	log.debug("############ [Elster A1140 E3] ModemEvent.COVEROPEN ##############");            	
                	log.debug("E3 Event Time : " + sEventTime);
                	
                	event.setOpenTime(sEventTime);
                	
                	String E3 = eventCodeE3.get(E3CNT++);
                	log.debug("eventCodeE3 size : " + eventCodeE3.size());
                	log.debug("E3 to String : " + E3);
                	
                	EventAlert eventAlert = eaDao.findByCondition("name", "Case Alarm");
            		event.setEventAlert(eventAlert);
            		
    	    		if(E3.equals("1")){
    	        		log.debug("ModemEvent.TERMINALCOVEROPEN");
    	        		ea = EventUtil.makeEventAlertAttr("caseState", "java.lang.String", "- Terminal Cover Open");
    	        		event.append(ea);
    	        		log.debug(event.getEventAlertAttrs());
    	        	} else if(E3.equals("2")){
    	        		log.debug("ModemEvent.MAINCOVEROPEN");
    	        		ea = EventUtil.makeEventAlertAttr("caseState", "java.lang.String", "- Main Cover Open");
    	        		event.append(ea);
    	        		log.debug(event.getEventAlertAttrs());
    	        	} else if(E3.equals("3")){
    	        		log.debug("ModemEvent.MAINCOVEROPEN AND ModemEvent.TERMINALCOVEROPEN");
    	        		ea = EventUtil.makeEventAlertAttr("caseState", "java.lang.String", "- Terminal and Main Cover Open");
    	        		event.append(ea);
    	        		log.debug(event.getEventAlertAttrs());
    	        	} else {
    	        		event = null;
    	        	}
                	
                }
                
                else if(modemEvent == ModemEvent.BATTERYFAULT)
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
            }
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
        log.debug("Modem Event Action Compelte");
    }
    
    enum ModemEvent {
        POWERFAIL(0, "Power Failure"),
        BATTERYFAULT(1, "Low Battery"),
        POWERRESTORE(2, "Power Failure"),
        LINEMISSING(3, "Power Failure"),
        DOOROPEN(4, "Case Alarm"),
        DOORCLOSE(5, "Case Alarm"),
        UNKNOWN(6, "Unknown"),
        COVEROPEN(7,"Cover Open");
        
        int eventCode;
        String eventName;
        ModemEvent(int eventCode, String eventName) {
            this.eventCode = eventCode;
            this.eventName = eventName;
        }
        
        public String getEventName() {
            return this.eventName;
        }
        
        public static ModemEvent getEvent(int eventCode) {
            for (int i = 0; i < ModemEvent.values().length; i++) {
                if (ModemEvent.values()[i].eventCode == eventCode)
                    return ModemEvent.values()[i];
            }
            return UNKNOWN;
        }
    }
}
