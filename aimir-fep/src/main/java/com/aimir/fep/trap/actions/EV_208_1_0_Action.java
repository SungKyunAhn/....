package com.aimir.fep.trap.actions;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 208.1.0 Processing Class
 *
 * @author Yeon Kyoung Park
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Component
public class EV_208_1_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_208_1_0_Action.class);

    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    MeterDao meterDao;
    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU SyncSensorIndex Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EV_208_1_0_Action : EventName[eventSynchSensorIndex] "
                +" EventCode[" + trap.getCode()
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
            log.debug("EV_208_1_0_Action : event[" + event.toString() + "]");

            
            String mcuInstName = mcu.getSysID();
            String eventOid = trap.getCode();

            EventAlertAttr[] eventAttrs = event.getEventAlertAttrs().toArray(new EventAlertAttr[0]);
            
            if (eventAttrs == null || eventAttrs.length == 0)
            {
                log.debug("no change attribute");
                return;
            }
            
            int k = 0;
            for(int i = 0; k < eventAttrs.length; i++){
              
                String sensorIndex = eventAttrs[k++].getValue();
                String modemId = eventAttrs[k++].getValue();
                String meterId = eventAttrs[k++].getValue();

                // Log
                log.debug("EV_208_1_0_Action : modemId[" + modemId + "]");
                
                Modem modem = modemDao.get(modemId);

                if (modem == null)
                {
                    log.debug("Modem["+modemId+"] does not exist in MI Repository");
                    return;
                }
               
                Meter meter = meterDao.get(meterId);
                meter.setModem(modem);
                meterDao.update(meter);
            }

        }
        catch(Exception ex) {
            log.error(ex,ex);
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }

        log.debug("Communication SynchSensorIndex change Event Action Compelte");
    }
}
