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
 * Event ID : 203.100.0 Processing Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Component
public class EV_203_100_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_203_100_0_Action.class);
    
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
        log.debug("EV_203_100_0_Action : EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");

        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            
            String sensorId = event.getEventAttrValue("id");
            if (sensorId == null)
            {
                log.error("sensor id is null");
                return;
            }
            
            String sensorType = event.getEventAttrValue("otherSensorType");
            if (sensorType == null)
            {
                log.error("sensor type is null");
                return;
            }
            
            MCU mcu = mcuDao.get(trap.getMcuId());
            
            if (mcu == null)
            {
                log.warn("does not exist MCU["
                        +trap.getMcuId()+"] connected sensor["
                        +sensorId+"] in MI Repository");
                return;
            }

            // Initialize
            //CommandGW gw = new CommandGW();
            //String mcuId = trap.getMcuId();
    
            // Log
            log.debug("EV_203_100_0_Action : sensorId[" + sensorId + "]");
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }

        /* TODO IMPELMENT SENSORPAGE DECODE
        // Decode
        String oid = null;
        SensorPage sp = null;
        Iterator iter = trap.getVarBinds().keySet().iterator();
        while (iter.hasNext())
        {
            oid = (String) iter.next();
            log.debug("oid="+oid);
            if (oid.matches("4\\.20\\..*"))
            {
                sp = SensorPage.decode(Integer.parseInt(sensorType), 
                    ((OCTET)trap.getVarBinds().get(oid)).getValue());
                log.debug("sensor page ="+sp);
                event.remove(sp.getMIBName());
            }
        }
        if (sp == null)
        {
            log.error("no sensor page exist");
            return;
        }


        Code modemType = CommonConstants.getModemType(sensorType);
        String eventClassName = FMPProperty.getProperty("event.class."+modemType.getName()+
                "Page"+sp.getPageNumber());
        log.debug("eventClassName="+eventClassName);

        if (eventClassName == null)
        {
            log.error("not define event class ["+sp.getMIBName()+"]");
            return;
        }

        event.setEventClassName(eventClassName);
        
        try
        {
            String typeString = modemType.getName();
            log.debug("sensorType="+typeString);
            EventAttr ea = EventUtil.makeEventAttr("sensorType", 
                    "java.lang.String", typeString);
            event.append(ea);
        }
        catch (Exception e)
        {
            log.error(e,e);
        }
                */

        log.debug("Sensor Event Action Compelte");
    }
}
