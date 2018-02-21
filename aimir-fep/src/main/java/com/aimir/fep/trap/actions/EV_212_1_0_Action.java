package com.aimir.fep.trap.actions;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 212.1.0 Processing Class
 *
 * @author J.S Park
 * @version $Rev: 1 $, $Date: 2008-06-25 15:59:15 +0900 $,
 */
@Component
@Transactional(value = "transactionManager", propagation=Propagation.REQUIRES_NEW)
public class EV_212_1_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_212_1_0_Action.class);

    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    MeterDao meterDao;
    
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
        log.debug("EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");

        // Initialize
        String modemId = event.getEventAttrValue("sensorID");
        String meterId = event.getEventAttrValue("stringEntry");
        int errorCode = 0;
        int errorStatus = 0;
        
        EventAlertAttr[] eventAttrs = event.getEventAlertAttrs().toArray(new EventAlertAttr[0]);
        for (int i = 0; i < eventAttrs.length; i++) {
            if (eventAttrs[i].getAttrName().equals("uintEntry")) {
                errorCode = Integer.parseInt(eventAttrs[i].getValue());
            }
            else if (eventAttrs[i].getOid().indexOf("uintEntry.") != -1) {
                errorStatus = Integer.parseInt(eventAttrs[i].getValue());
            }
        }
        
        StringBuffer buf = new StringBuffer();
        switch (errorCode) {
        case 0 : buf.append("Clock Error");
            break;
        }
        switch (errorStatus) {
        case 0 : buf.append(" Recovery");
            break;
        }
        
        event.append(EventUtil.makeEventAlertAttr("message", 
                                             "java.lang.String", 
                                             buf.toString()));
        
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            
            Meter meter = meterDao.get(meterId);
            
            if(meter != null)
            {
                event.setActivatorId(meter.getMdsId());
                event.setActivatorType(meter.getMeterType().getName());
                event.setLocation(meter.getLocation());
            }
            else
            {
                Modem sensor = modemDao.get(modemId);
                if (sensor != null) {
                    event.setActivatorId(sensor.getDeviceSerial());
                    event.setActivatorType(TargetClass.Modem);
                    //event.setLocationCode(sensor.getLocation().getId()+"");
                   // event.setLocationName(sensor.getLocation().getName());
                }
            }
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }
}
