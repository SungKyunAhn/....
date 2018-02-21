package com.aimir.fep.trap.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 203.1.0 Processing Class (Sensor Join Network)
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Component
public class EV_203_1_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_203_1_0_Action.class);
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EventName[eventSensorJoinNetwork] EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");
        
        String mcuId = trap.getMcuId();
        event.append(EventUtil.makeEventAlertAttr("mcuID", 
                                             "java.lang.String", mcuId));
        
        String modemId = event.getEventAttrValue("sensorID");
        log.debug("MODEM_ID[" + modemId + "]");
        
        event.setActivatorType(TargetClass.Modem);
        event.setActivatorId(modemId);
        event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", "Modem Join Network"));
    }
}
