package com.aimir.fep.trap.actions;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.device.MCUDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 203.11.0 Processing Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Component
public class EV_203_11_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_203_11_0_Action.class);

    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    MCUDao mcuDao;
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EV_203_11_0_Action(eventSensorPageChange : EventCode[" 
                + trap.getCode() +"] MCU["+trap.getMcuId()+"]");

        String modemId = event.getEventAttrValue("id");
        if (modemId == null)
        {
            log.error("sensor id is null");
            return;
        }
        
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            
            MCU mcu = mcuDao.get(trap.getMcuId());
    
            if (mcu == null)
            {
                log.warn("does not exist MCU["
                        +trap.getMcuId()+"] connected sensor["
                        +modemId+"] in MI Repository");
                return;
            }
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
        // Log
        log.debug("EV_203_11_0_Action : modemId[" + modemId + "]");

        // Not Implemented


        log.debug("Sensor Page Change Event Action Compelte");
    }
}
