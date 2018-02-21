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
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.notification.FMPTrap;
import com.aimir.util.TimeUtil;

/**
 * Event ID : 200.16.0 Processing Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Component
public class EV_200_16_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_200_16_0_Action.class);

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
        log.debug("EV_200_16_0_Action : EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");

        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            String mcuId = trap.getMcuId();
            MCU mcu = mcuDao.get(mcuId);
            
            log.debug("EV_200_16_0_Action : mcuId[" + mcuId + "]");

            if(mcu != null)
            {
                mcu.setLastCommDate(TimeUtil.getCurrentTime());
                
                log.info("MCU Low Battery Action Started");
                EventAlertLog alert = EventUtil.findOpenAlert(event);
                
                if (alert == null)
                {
                    log.warn("MCU Low Battery Action Failed");
                    return;
                }
                
                Integer t = alert.getOccurCnt();
                int ti = t.intValue()+1;
                alert.setOccurCnt(new Integer(ti));
                
                event = alert;
            } 
            else
            {
                log.warn("MCU Low Battery Action failed : Unknown MCU");
            }
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }
}
