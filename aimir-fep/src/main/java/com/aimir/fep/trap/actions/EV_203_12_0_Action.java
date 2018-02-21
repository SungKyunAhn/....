package com.aimir.fep.trap.actions;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Modem;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 203.12.0 Processing Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Component
public class EV_203_12_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_203_12_0_Action.class);

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
        log.debug("EV_203_12_0_Action(eventSensorPageRead : EventCode[" 
                + trap.getCode() +"] MCU["+trap.getMcuId()+"]");

        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            
            String modemId = event.getEventAttrValue("id");
            if (modemId == null)
            {
                log.error("sensor id is null");
                return;
            }
            
            MCU mcu = mcuDao.get(trap.getMcuId());
            if (mcu == null)
            {
                log.warn("does not exist MCU["
                        +trap.getMcuId()+"] connected sensor["
                        +modemId+"] in MI Repository");
                return;
            }
    
            // Log
            log.debug("EV_203_12_0_Action : modemId[" + modemId + "]");
    
            ArrayList<Object> splist = new ArrayList<Object>();
        	//TODO IMPLEMENT SENSOR PAGE INFORMATION
            // Decode
            /*
            String oid = null;
    
            SensorPage sp = null;
            Iterator iter = trap.getVarBinds().keySet().iterator();
            EventAttr ea = null;
            String pages = "";
    
            while (iter.hasNext())
            {
                oid = (String) iter.next();
                log.debug("oid="+oid);
                if (oid.matches("4\\.20\\..*"))
                {
                    sp = SensorPage.decode(1, 
                            ((OCTET)trap.getVarBinds().get(oid)).getValue());
                    log.debug("sensor page ="+sp);
                    event.remove(sp.getMIBName());
    
                    if(sp != null)
                    {
                        pages+=" "+sp.getPageNumber(); 
                        if(sp.getPageNumber() >= 70 && sp.getPageNumber() <= 100)
                            try{ 
                            	CmdOperationUtil.saveLPData(mcuId,modemId,sp); 
                            } catch(Exception ex) { log.error(ex,ex); }
                        else
                            splist.add(sp);
                    }
                }
            }
    
    
            ea = EventUtil.makeEventAttr("Read Pages", "java.lang.String", pages); 
            event.append(ea);
            */
            if(splist.size() > 0)
            { 
                Modem modem  = modemDao.get(modemId);
    
                if(modem != null)
                {
                	//TODO IMPLEMENT SENSOR PAGE INFORMATION
                	/*
                    SensorPage[] sps = 
                        (SensorPage[])splist.toArray(new SensorPage[0]);
    				*/
                    modem.setMcu(mcu);
                    modem.setDeviceSerial(modemId);
                }
            }
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }

        log.debug("Sensor Page Read Event Action Compelte");
    }
}
