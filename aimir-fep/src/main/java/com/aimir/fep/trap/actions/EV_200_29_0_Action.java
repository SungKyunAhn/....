package com.aimir.fep.trap.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 200.29.0 Processing Class
 *
 * @author J.S (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2008-10-22 15:59:15 +0900 $,
 */
@Component
public class EV_200_29_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_200_29_0_Action.class);

    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");

        byte b = Byte.parseByte(event.getEventAttrValue("byteEntry"));
        String alertType = null;
        
        switch (b) {
        case 0x01 : 
            alertType = "Invalid Magic Number";
            break;
        case 0x02 :
            alertType = "Invalid EndDeviceList";
            break;
        case 0x03 :
            alertType = "Invalid EventConfigure";
            break;
        }
        EventAlertAttr ea = EventUtil.makeEventAlertAttr("alertType", "java.lang.String", alertType);
        event.append(ea);
        
        //MIBUtil mibUtil = MIBUtil.getInstance();
        //String oid = mibUtil.getOid("streamEntry").toString();
        //byte[] bx = ((OCTET)trap.getVarBinds().get(oid)).getValue();
        event.remove("streamEntry");
        
        EventAlertAttr[] eventAttr = event.getEventAlertAttrs().toArray(new EventAlertAttr[0]);
        for (int i = 0; i < eventAttr.length; i++) {
            log.debug("attr name[" + eventAttr[i].getAttrName() + "]");
            if (eventAttr[i].getAttrName().equals("streamEntry")) {
            }
        }
    }
}
