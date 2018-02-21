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
 * Event ID : 201.4.0 Processing Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Component
public class EV_202_4_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_202_4_0_Action.class);

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
        log.debug("EV_202_4_0_Action : EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");
        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            
            MCU mcu = mcuDao.get(trap.getMcuId());
            
            if (mcu == null)
            {
                log.debug("no mcu intance exist mcu["
                        +trap.getMcuId()+"]");
                return;
            }
    
            log.debug("Event["+event+"]");
            String ipaddr = event.getEventAttrValue("ethIpAddr");
            if (ipaddr != null && !ipaddr.equals("") && !ipaddr.equals("0.0.0.0"))
            {
                if (mcu.getIpAddr() != null && !mcu.getIpAddr().equals(ipaddr))
                    mcu.setIpAddr(ipaddr);
                
                // mcuDao.update(mcu);
            }
    
            log.debug("EV_202_4_0_Action Compelte");
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }
}
