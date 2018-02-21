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
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiBindingEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiDeviceEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiMemoryEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiNeighborEntry;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUCodi;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 201.3.0 Processing Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Component
public class EV_201_3_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_201_3_0_Action.class);

    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    MCUCodiDao mcuCodiDao;
    
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
        log.debug("EV_201_3_0_Action : EventCode[" + trap.getCode()
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
            
            processingCodi(event,mcu);
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }

    }

    
    private void processingCodi(EventAlertLog event, MCU mcu)
        throws Exception{

        // Initialize
        String mcuInstName = mcu.getSysID();
        String mcuId = mcu.getSysID();
        
        CommandGW gw = new CommandGW();
        // find Codi
        String codiId = event.getEventAttrValue("id");
        codiEntry ce = gw.cmdGetCodiInfo(mcuId);
        codiDeviceEntry cde = gw.cmdGetCodiDevice(mcuId);
        codiBindingEntry cbe = gw.cmdGetCodiBinding(mcuId);
        codiNeighborEntry cne = gw.cmdGetCodiNeighbor(mcuId);
        codiMemoryEntry cme = gw.cmdGetCodiMemory(mcuId);
        
        MCUCodi mcuCodi = new MCUCodi();
        
        //TODO IMPLEMENT CODI API
        //mcuCodi.setCodiLinkKey(codiLinkKey);
        //mcuCodi.setMcuCodiBinding(codiBindingEntry);

        if (mcuInstName != null && codiId != null)
        {
            try
            {
                log.debug("mcu="+mcuInstName+",codi="+codiId);
                mcu.setMcuCodi(mcuCodi);
                // mcuDao.update(mcu);
            }
            catch (Exception e)
            {
                log.debug("create association failed : "+e);
            }
        }
        else
        {
            log.debug("mcu instance name or codi instance name is null");
        }
        
        log.debug("Codi Connect Event Action Compelte");
    }
}
