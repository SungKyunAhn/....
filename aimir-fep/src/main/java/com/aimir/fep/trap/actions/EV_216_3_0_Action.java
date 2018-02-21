package com.aimir.fep.trap.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.aimir.fep.trap.common.EV_Action;
import com.aimir.model.device.EventAlertLog;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 216.3.0 Processing Class
 * eventDLCConfirm
 * @author kaze
 */
@Component
public class EV_216_3_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_216_3_0_Action.class);
    
    /**
     * execute event action
     *
     * @param trap
     *            - FMP Trap(MCU Event)
     * @param event
     *            - Event Alert Log Data
     */
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
    	System.out.println("EV_216_3_0_Action Start");
    	log.debug("EV_216_3_0_Action Start");
    	
        log.debug("Event[eventDLCConfirm] EventCode[" + trap.getCode() + "] MCU["
                  + trap.getMcuId() + "] TriggerId["+event.getEventAttrValue("stringEntry")+"]");

        // Initialize
        String mcuId = trap.getMcuId();
        String taskId = event.getEventAttrValue("stringEntry");
        String deviceId = event.getEventAttrValue("deviceId");
        Boolean confirm = Boolean.parseBoolean(event.getEventAttrValue("boolEntry"));
        //TODO 이벤트 수신 후 비즈니스 로직 추가 필요
        
    	System.out.println("EV_216_3_0_Action End");
    	log.debug("EV_216_3_0_Action End ");
    }
}