package com.aimir.fep.trap.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.constants.CommonConstants.FW_EQUIP;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FirmwareUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 211.1.0 Processing Class
 * Event for MCU Event Job
 * 
 * @author Hun Jeong
 * @version $Rev: 1 $, $Date: 2008-05-21 15:00:00 +0900 $,
 */
@Component
public class EV_211_1_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_211_1_0_Action.class);

    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug(" Event descr[" + event.toString() + "]");
        int equipType = Integer.parseInt(event.getEventAttrValue("equipType"));
        String triggerId = event.getEventAttrValue("triggerId");
        String equipId = event.getEventAttrValue("equipId");
        int jobState = Integer.parseInt(event.getEventAttrValue("jobState"));
        int jobErrorCode = Integer.parseInt(event.getEventAttrValue("jobErrorCode"));
        FW_EQUIP fw_equip = null;
        
        //Object targetMO = null;
        if(equipType==FW_EQUIP.MCU.getKind()){
            //targetMO = mcuDao.get(equipId);
            fw_equip = FW_EQUIP.MCU;
        }
        else if(equipType==FW_EQUIP.Modem.getKind()){
            //targetMO = modemDao.get(equipId);
            fw_equip = FW_EQUIP.Modem;
        }
        else if(equipType==FW_EQUIP.Coordinator.getKind()){
        	//targetMO = mcuDao.get(equipId).getMcuCodi();
        	fw_equip = FW_EQUIP.Coordinator;
        }

        EventUtil.sendEvent("Equipment Firmware Update",
                            TargetClass.valueOf(fw_equip.name()),
                            equipId,
                            trap.getTimeStamp(),
                            new String[][] {},
                            event);

        FirmwareUtil.updateTriggerAndHistory(triggerId, equipType, equipId, jobState, jobErrorCode);
        
    }
}