package com.aimir.fep.trap.actions;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Modem;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 203.9.0 Processing Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Component
public class EV_203_9_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_203_9_0_Action.class);

    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    ModemDao modemDao;
    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EV_203_9_0_Action : EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");

        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            
            MCU mcu = mcuDao.get(trap.getMcuId());
            
            if (mcu == null)
            {
                log.debug("no sensor intance exist mcu["+trap.getMcuId()+"]");
                return;
            }
    
            String sensorId = event.getEventAttrValue("id");
    
            // Log
            log.debug("EV_203_9_0_Action : sensorId[" + sensorId + "]");
            Modem modem = modemDao.get(sensorId);
    
            if (modem == null)
            {
                log.debug("sensor["+sensorId+"] is not exist");
                return;
            }

            ModemType sensorType = modem.getModemType();
            EventAlertAttr ea = EventUtil.makeEventAlertAttr("sensorType", 
                    "java.lang.String", sensorType.name());
            event.append(ea);
        }
        catch (Exception e)
        {
            log.error(e,e);
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }

        log.debug("Sensor Reset Event Action Compelte");
    }
}
