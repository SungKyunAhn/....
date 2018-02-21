package com.aimir.fep.trap.actions.GG;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.device.EventAlertDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlert;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.Modem;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : GG_200.2.1 (GPRS Modem Case Open)
 * <br>GPRS Modem
 * 
 * @author goodjob
 * @version $Rev: 1 $, $Date: 2014-10-28 15:59:15 +0900 $,
 */
@Component
public class EV_GG_200_9_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_GG_200_9_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    EventAlertDao eaDao;    
    
    EventAlertAttr ea = null;
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("Modem Event descr[" + event.toString() + "]");
        String activatorid = event.getActivatorId();        
        String sEventTime = event.getOpenTime();

        Modem modem = null;
        TransactionStatus txstatus = null;

        try {
            txstatus = txmanager.getTransaction(null);
            modem = modemDao.get(activatorid);
            if(modem == null){
                log.debug("no modem instance exist[" + activatorid + "]");
                return;
            }
            event.setLocation(modem.getLocation());
            
            ModemType modemType = modem.getModemType();
            ea = EventUtil.makeEventAlertAttr("modemType", 
                    "java.lang.String", modemType.name());
            event.append(ea);
            
            String serverIp = event.getEventAttrValue("evtServerIP");
            int serverPort = Integer.parseInt(event.getEventAttrValue("evtServerPort"));
            
            EventAlert eventAlert = eaDao.findByCondition("name", "Equipment Notification");
            event.setEventAlert(eventAlert);
            ea = EventUtil.makeEventAlertAttr("message",
                    "java.lang.String",
                    "SMSResponse ip[" + serverIp + " port[" + serverPort + "]");
            event.append(ea);
            
            modem.setIpAddr(serverIp+":"+serverPort);
            modemDao.update(modem);
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
        
        log.debug("Modem Event Action Compelte");
    }
}
