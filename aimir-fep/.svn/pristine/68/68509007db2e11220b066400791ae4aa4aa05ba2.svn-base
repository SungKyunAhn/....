package com.aimir.fep.trap.actions.SP;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 200.58.0 evtReboot
 *
 * @author Elevas Park
 * @version $Rev: 1 $, $Date: 2016-09-18 15:59:15 +0900 $,
 */
@Service
public class EV_SP_200_58_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_SP_200_58_0_Action.class);
    
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

        // Initialize
        String ipAddr = trap.getIpAddr();

        log.debug("IP[" + ipAddr + "]");
        String _reason = event.getEventAttrValue("byteEntry");
        byte reason = (byte)0xFF;
        
        if (_reason != null && !"".equals(_reason))
            reason = Byte.parseByte(_reason);
        
        if (reason == 0x01)
            _reason = "LowBattery";
        else if (reason == 0x02)
            _reason = "User";
        else if (reason == 0x03)
            _reason = "Malfunction";
        else if (reason == 0x04)
            _reason = "Recovery";
        else if (reason == 0x05)
            _reason = "Scheduled";
        else if (reason == 0x06)
        	_reason = "USB Driver Loading Fail(for Mobile)";
        else
            _reason = "Unknown";
        
        event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", _reason));

        log.debug("DCU Reboot Event Action Compelte");

    }
}
