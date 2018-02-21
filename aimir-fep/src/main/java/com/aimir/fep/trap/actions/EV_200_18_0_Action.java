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
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.notification.FMPTrap;
import com.aimir.util.TimeUtil;

/**
 * Event ID : 200.18.0 Processing Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Component
public class EV_200_18_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_200_18_0_Action.class);
    
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
        log.debug("EV_200_18_0_Action : EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");

        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            
            String mcuId = trap.getMcuId();
            MCU mcu = mcuDao.get(mcuId);
            
            log.debug("EV_200_18_0_Action : mcuId[" + mcuId + "]");
    
            if(mcu != null)
            {
                log.debug("MCU Temp Range Exceed Action Started");
                mcu.setLastCommDate(TimeUtil.getCurrentTime());
                
                EventAlertLog alert = EventUtil.findOpenAlert(event);
                
                if (alert == null)
                {
                    log.debug("MCU Temp Range Exceed Action Failed");
                    String temp = event.getEventAttrValue("sysCurTemp");
                    mcu.setSysCurTemp(Integer.parseInt(temp));
                }
                else
                {
                    Integer t = alert.getOccurCnt();
                    int ti = t.intValue()+1;
                    alert.setOccurCnt(new Integer(ti));
                }
                
                mcuDao.update(mcu);
            } 
            else
            {
                log.debug("MCU Temp Range Exceed Action failed : Unknown MCU");
            }
            
            EventAlertAttr sysCurTempEA = event.getEventAttr("sysCurTemp");
            EventAlertAttr sysMinTempEA = event.getEventAttr("sysMinTemp");
            EventAlertAttr sysMaxTempEA = event.getEventAttr("sysMaxTemp");
            
            double sysCurTemp = Double.parseDouble(sysCurTempEA.getValue());
            double sysMinTemp = Double.parseDouble(sysMinTempEA.getValue());
            double sysMaxTemp = Double.parseDouble(sysMaxTempEA.getValue());
            
            sysCurTempEA.setValue(new Double(sysCurTemp * 0.1).toString());
            sysMinTempEA.setValue(new Double(sysMinTemp * 0.1).toString());
            sysMaxTempEA.setValue(new Double(sysMaxTemp * 0.1).toString());
            
            event.append(sysCurTempEA);
            event.append(sysMinTempEA);
            event.append(sysMaxTempEA);
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }
}
