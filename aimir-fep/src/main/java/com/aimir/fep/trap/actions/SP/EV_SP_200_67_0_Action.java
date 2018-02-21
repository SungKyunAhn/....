package com.aimir.fep.trap.actions.SP;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.Modem;
import com.aimir.notification.FMPTrap;
import com.aimir.model.device.MCU;

/**
 * Event ID : EV_SP_200.67.0 (Communication Fail Alarm Event)
 *
 * @author 
 * @version $Rev: 1 $, $Date: 2016-05-27 10:00:00 +0900 $,
 */
@Component
public class EV_SP_200_67_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_SP_200_67_0_Action.class);
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    ModemDao modemDao;

   
   /**
     * execute event action
     *
     * @param trap - FMP Trap(Communication Fail Alarm Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EV_SP_200_67_0_Action : EventName[evtCommFail] "
                +" EventCode[" + trap.getCode()
                +"] TargetClass[" + event.getActivatorType() + "]");

        String modemId = event.getEventAttrValue("moSPId");
        log.debug("MODEM_ID[" + modemId + "]");

        MCU mcu = mcuDao.get(trap.getMcuId());
        
        event.setActivatorIp(mcu.getIpv6Addr() != null ? mcu.getIpv6Addr():mcu.getIpAddr());
        event.setActivatorId(mcu.getSysID());
        event.setActivatorType(TargetClass.DCU);
        event.setSupplier(mcu.getSupplier());
        event.setLocation(mcu.getLocation());
        
        String message = "Communication Fail to "; 
        Modem modem = modemDao.get(modemId);
        
        if (modem != null) {
            message += "Modem[" + modemId + "]";
        }
        else {
            message += "FEP";
        }
        
        event.append(EventUtil.makeEventAlertAttr("moSPId", "java.lang.String", modemId));
        event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", message));
        
        log.debug("moSPId=[" + modemId +"], message=["+message+"]");          
        log.debug("Communication Fail Alarm Event Action Compelte");
    }
}
