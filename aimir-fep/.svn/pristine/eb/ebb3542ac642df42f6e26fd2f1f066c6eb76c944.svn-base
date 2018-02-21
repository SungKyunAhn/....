package com.aimir.fep.trap.actions;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 205.1.0 Processing Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Component
public class EV_205_3_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_205_3_0_Action.class);

    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    ModemDao modemDao;
    
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
        log.debug("EV_205_3_0_Action : EventName[eventCommZEUError] "
                +" EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");

        String modemId = event.getEventAttrValue("sensorID");
        int nReason = 0;
        try { 
            String tmpval = event.getEventAttrValue("byteEntry");
            log.debug("EV_205_3_0_Action : byteEntry[" + tmpval + "]");
            nReason = Integer.parseInt(tmpval);
        }catch(Exception ex) { log.error(ex,ex); }

        // Log
        log.debug("EV_205_3_0_Action : modemId[" + modemId + "]"
                +" nReason["+nReason+"] ");

        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            
            Modem modem = modemDao.get(modemId);
    
            if (modem != null)
            {
                event.setActivatorId(modem.getDeviceSerial());
                event.setActivatorType(TargetClass.Modem);
            }
            else {
                log.debug("Modem["+modemId+"] does not exist in MI Repository");
                event.append(EventUtil.makeEventAlertAttr("message",
                                                     "java.lang.String",
                                                     "Modem[" + modemId + "]"));
            }
            
            String modemPort = event.getEventAttrValue("modemPort");
            
            Meter meter = meterDao.getMeterByModemDeviceSerial(modemId, modemPort != null? Integer.parseInt(modemPort):0);
            String unitSerial = "Unknown";
            if (meter != null) {
                unitSerial = meter.getMdsId();
            }
            
            event.append(EventUtil.makeEventAlertAttr("meterID",
                                                 "java.lang.String",
                                                 unitSerial));
            
            // event.setEventClassName("Communication Error");
            if(nReason == 0)
            {
                event.append(EventUtil.makeEventAlertAttr("commState", 
                                                     "java.lang.String", 
                                                     "Restore"));
                if (modem != null){
                	modem.setCommState(1);
                }
            } else
            {
                event.append(EventUtil.makeEventAlertAttr("commState", 
                                                     "java.lang.String", 
                                                     "Failure"));
                if (modem != null){
                	modem.setCommState(0);
                }
            }
        }
        catch (Exception e)
        {
            log.error(e,e);
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }

        log.debug("Communication ZEUPLS Failure Event Action Compelte");
    }
}
