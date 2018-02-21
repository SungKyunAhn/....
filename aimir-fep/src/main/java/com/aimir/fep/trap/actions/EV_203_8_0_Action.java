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
import com.aimir.util.TimeUtil;

/**
 * Event ID : 203.8.0 Processing Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Component
public class EV_203_8_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_203_8_0_Action.class);
    
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
        log.debug("EV_203_8_0_Action : EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");

        String modemId = event.getEventAttrValue("sensorID");
        
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
            Modem modem = modemDao.get(modemId);
            if (modem == null)
            {
                log.debug("Modem["+modemId+"] is not exist");
                try{
                    EventAlertAttr[] attrs = new EventAlertAttr[]
                    {
                        EventUtil.makeEventAlertAttr("modemId",
                                "java.lang.String",modemId)
                    };
                    /*
                    EventUtil.sendEvent("Equipment Unregistered",
                            "ZigBeeSensor",mcu,attrs); 
                    */
                    /*
                    Event unregEvt = EventUtil.makeEvent(
                            "Equipment Unregistered","ZigBeeSensor",
                            mcu,attrs); 
                    EventUtil.sendNotification(unregEvt);
                    */
                }catch(Exception ex) {
                    log.error("makeEvent failed",ex);
                }
                log.debug("Modem["+modemId+"] has no modem");
                return;
            }
    
            ModemType modemType = modem.getModemType();
            EventAlertAttr ea = EventUtil.makeEventAlertAttr("modemType", 
                    "java.lang.String", modemType.name());
            event.append(ea);
    
            // Log
            log.debug("EV_203_8_0_Action : modemId[" + modemId + "]"
                    + " ModemType["+modem.getModemType().name()+"] typeString["+modem.getModemType().name()+"]");
    
            String curTime = TimeUtil.getCurrentTime();    
            modem.setLastLinkTime(curTime);
    
            /*
            // Set Sensor Install Date
            EventActionUtil.setInstallDate(sensors[0], 
                    TimeUtil.getCurrentTime());
    
            // Set Unit connected Sensor Install Date
            EventActionUtil.setUnitInstallDate(sensors[0], 
                    TimeUtil.getCurrentTime());
    
            */
        }
        finally {
            if (txstatus != null) txmanager.commit(null);
        }
        log.debug("Unknown Modem Install Event Action Compelte");
    }
}
