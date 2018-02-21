package com.aimir.fep.trap.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aimir.dao.device.ModemDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 216.1.0 Processing Class
 * eventEndDeviceStatus
 * @author kaze
 */
@Component
public class EV_216_1_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_216_1_0_Action.class);
    @Autowired
    ModemDao modemDao;
    /**
     * execute event action
     *
     * @param trap
     *            - FMP Trap(MCU Event)
     * @param event
     *            - Event Alert Log Data
     */
    @SuppressWarnings("unchecked")
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
    	System.out.println("EV_216_1_0_Action Start");
    	log.debug("EV_216_1_0_Action Start");
    	
        log.debug("Event[eventEndDeviceStatus] EventCode[" + trap.getCode() + "] MCU["
                  + trap.getMcuId() + "] TriggerId["+event.getEventAttrValue("stringEntry")+"]");

        // Initialize
        String mcuId = trap.getMcuId();
        String taskId = event.getEventAttrValue("stringEntry");
        //TODO opaque 형태의 smivalue를 어떻게 꺼내올 것인가?
        EventAlertAttr att = event.getEventAttr("endDeviceEntry");
        //TODO 이벤트 수신 후 비즈니스 로직 추가 필요
        // EndDevice의 DR레벨 변경

        
        
    	System.out.println("EV_216_1_0_Action End");
    	log.debug("EV_216_1_0_Action End ");
    }
}