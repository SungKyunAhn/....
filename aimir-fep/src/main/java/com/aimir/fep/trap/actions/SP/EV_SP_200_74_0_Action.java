package com.aimir.fep.trap.actions.SP;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.dao.device.MCUDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.notification.FMPTrap;
import com.aimir.util.TimeUtil;

/**
 * Event ID : EV_SP_200_74_0 Processing Class
 *
 * @author Tatsumi
 * @version $Rev: 1 $, $Date: 2016-05-17 15:59:15 +0900 $,
 */
@Component
public class EV_SP_200_74_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_SP_200_74_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    MCUDao mcuDao;
    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EV_SP_200_74_0_Action : EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");
        
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            
            String mcuId = trap.getMcuId();
            MCU mcu = mcuDao.get(mcuId);
            log.debug("EV_SP_200_74_0_Action : mcuId[" + mcuId + "]");
    
            //EventAlertAttr eabatteryADC = event.getEventAttr("batteryADC");
            //if(eabatteryADC != null){
            //    double batteryADCCur = Double.parseDouble(eabatteryADC.getValue());
            //	eabatteryADC.setValue(new Double(batteryADCCur * 0.1).toString());
            //    event.append(eabatteryADC);
            //	log.debug("batteryADC["+ eabatteryADC.getValue() +"]");
            //}

            if(mcu != null)
            {
                mcu.setLowBatteryFlag(0);
                mcu.setLastCommDate(TimeUtil.getCurrentTime());
                mcu.setPowerState(0);
                
                log.debug("MCU Battery Fully Charged Action Started");
//                EventAlertLog alert = EventUtil.findOpenAlert(event);
//                
//                if (alert == null)
//                {
//                    log.warn("MCU Battery Fully Charged Action failed");
//                    event.setCloseTime(event.getOpenTime());
//                    event.setOpenTime("");
//                    event.setStatus(EventStatus.Cleared);
//                }
//                else {
//                    alert.setCloseTime(event.getOpenTime());
//                    alert.setStatus(EventStatus.Cleared);
//                    event = alert;
//                }
                event.setActivatorId(trap.getSourceId());
                event.setActivatorType(TargetClass.DCU);
                event.setStatus(EventStatus.Cleared);
                // batteryADC is wordEntry
                EventAlertAttr eamsg = EventUtil.makeEventAlertAttr("message",
                                                   "java.lang.String",
                                                   // "Battery Fully Charged : Battery ADC["+event.getEventAttrValue("wordEntry")+"]");
                                                   "Battery Charging End");
                event.append(eamsg);
           } 
            else
            {
                log.debug("MCU Battery Fully Charged Action failed : Unknown MCU");
            }
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }
}
