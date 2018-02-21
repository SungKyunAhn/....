package com.aimir.fep.trap.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.aimir.fep.trap.common.EV_Action;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 203.108.0 (eventSensorPulseApply)Processing Class
 *
 * @author P.Y.K
 * @version $Rev: 1 $, $Date: 2007-10-22 15:59:15 +0900 $,
 */
@Component
public class EV_203_108_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_203_108_0_Action.class);

    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        int beforePulse = 0;
        int afterPulse = 0;
        
        log.debug("EV_203_108_0_Action : EventName[eventSensorPulseApply] "
                +" EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");

        String sensorId = event.getEventAttrValue("sensorID");
        EventAlertAttr[] eventAttr = event.getEventAlertAttrs().toArray(new EventAlertAttr[0]);

        int i = 0;
        if(eventAttr != null && eventAttr.length >= 2){
            if (eventAttr[i].getAttrName().indexOf("intEntry") != -1){
                beforePulse = Integer.parseInt(eventAttr[i++].getValue());
            }
            if (eventAttr[i].getAttrName().indexOf("intEntry") != -1){
                afterPulse = Integer.parseInt(eventAttr[i++].getValue());
            }
        }
        log.debug("sensorId["+sensorId+"],beforePulse["+beforePulse+"],afterPulse["+afterPulse+"]");

        log.debug("Sensor eventSensorPulseApply Event Action Compelte");
    }
}
