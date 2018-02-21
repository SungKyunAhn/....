package com.aimir.fep.trap.actions;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.device.MeterDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.Meter;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 213.1.0 Processing Class
 *
 * @author Y.K.Park
 * @version $Rev: 1 $, $Date: 2008-07-30 15:59:15 +0900 $,
 */
@Component
public class EV_213_1_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_213_1_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    MeterDao meterDao;
    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EV_213_1_0_Action : EventName[eventFlowAlarm] "
                +" EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");

        String modemId = event.getEventAttrValue("sensorID");
        String vcId = event.getEventAttrValue("stringEntry");
        int errCode = 0;
        String errMessage = "";
        try { 
            errCode = Integer.parseInt(event.getEventAttrValue("uintEntry"));
            errMessage = event.getEventAttrValue("streamEntry");
            log.debug("EV_213_1_0_Action : byteEntry[" + errCode + "]");

        }catch(Exception ex) { log.error(ex,ex); }

        // Log
        log.debug("EV_213_1_0_Action : modemId[" + modemId + "]"
                +" nReason["+errCode+"] ");
        
        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            
            Meter meter = meterDao.get(vcId);
            
            if(meter != null)
            {
                event.setActivatorId(meter.getMdsId());
                event.setActivatorType(meter.getMeterType().getName());
                event.setLocation(meter.getLocation());
            }
            else {
                log.debug("volumecorrector["+vcId+"] does not exist in MI Repository");
                event.append(EventUtil.makeEventAlertAttr("message",
                                                     "java.lang.String",
                                                     "VC[" + vcId + "]"+errMessage));
            }
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }

        log.debug("eventFlowAlarm Event Action Compelte");
    }
}
