package com.aimir.fep.trap.actions;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.device.MCUCodiDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUCodi;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 201.5.0 Processing Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Component
public class EV_201_5_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_201_5_0_Action.class);

    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    MCUCodiDao mcuCodiDao;
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EV_201_5_0_Action : EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");

        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            
            MCU mcu = mcuDao.get(trap.getMcuId());
            
            if (mcu == null)
            {
                log.debug("no mcu intance exist mcu["+trap.getMcuId()+"]");
                return;
            }
            
            // Initialize
            String eventOid=trap.getCode();
            
            processingCodi(event,eventOid,mcu);
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }
    
    
    private void processingCodi(EventAlertLog event, String eventOid, MCU mcu)
        throws Exception {

        String codiId = event.getEventAttrValue("id");
        if (codiId == null)
        {
            log.debug("no codi id in event");
            return;
        }

        
        MCUCodi mcuCodi = mcu.getMcuCodi();
        //TODO SET CODI PORT STATE
        //mo.setPropertyValue("portState", new MOVALUE("1"));

        if (mcuCodi == null )
        {
            log.debug("no codi associated by "+mcu.getSysID());
            return;
        }

        EventAlertAttr[] eventAttrs = event.getEventAlertAttrs().toArray(new EventAlertAttr[0]);

        if (eventAttrs == null || eventAttrs.length == 0)
        {
            log.debug("no change attribute");
            return;
        }

        for (int i = 0; i < eventAttrs.length; i++)
        {
            if ("id".equals(eventAttrs[i].getAttrName()))
            {
                continue;
            }
            log.debug("oid="+eventAttrs[i].getOid());
            log.debug("value="+eventAttrs[i].getValue());
            //TODO SET CODI ATTRIBUTES
            //mo.setPropertyValue("portState", new MOVALUE("1"));
        }
        mcuCodiDao.update(mcuCodi);
        
        log.debug("Codi Change Event Action Compelte");
    }
}
