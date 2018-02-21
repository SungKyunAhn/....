package com.aimir.fep.trap.actions;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.device.MCUDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 200.10.0 Processing Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Service
public class EV_200_10_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_200_10_0_Action.class);
    
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
        log.debug("EV_200_10_0_Action : EventCode[" + trap.getCode()
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
            String ipAddr = trap.getIpAddr();
    
            EventAlertAttr[] eventAttrs = event.getEventAlertAttrs().toArray(new EventAlertAttr[0]);
    
            if (eventAttrs == null || eventAttrs.length == 0)
            {
                log.debug("no change attribute");
                return;
            }
    
            for (int i = 0; i < eventAttrs.length; i++)
            {
                log.debug("oid="+eventAttrs[i].getOid());
                log.debug("value="+eventAttrs[i].getValue());
    
                if(eventAttrs[i].getOid().toLowerCase().startsWith("ip")){
                    if(ipAddr == null || ipAddr.startsWith("/") || ipAddr.startsWith("\n")
                            || ipAddr.equals("127.0.0.1") || ipAddr.equals("localhost")){
                        ipAddr = eventAttrs[i].getValue();
                    }
                    mcu.setIpAddr(ipAddr);
                }else{
                    //mop.setValue(eventAttrs[i].getValue());//TODO 다른 어트리뷰트 업데이트 하는부분은 밉을 다 찾아야 함
                }
                log.debug("EV_200_10_Action can not find MO Property corresspond to event attribute["+eventAttrs[i].toString()+"]");
            }
            event.append(EventUtil.makeEventAlertAttr("mcuType", 
                    "java.lang.String",mcu.getMcuType().getName()));
        }
        catch (Exception e)
        {
            log.error(e,e);
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }

        log.debug("MCU Change Event Action Compelte");
    }

}
