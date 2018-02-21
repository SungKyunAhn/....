package com.aimir.fep.trap.actions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.FW_TRIGGER;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.FirmwareHistoryDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FirmwareUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.FirmwareHistory;
import com.aimir.model.device.MCU;
import com.aimir.notification.FMPTrap;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

/**
 * Event ID : 214.2.0 Processing Class
 *
 * @author kaze
 * @version $Rev: 1 $, $Date: 2008-10-16 15:59:15 +0900 $,
 */
@Component
public class EV_214_2_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_214_2_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    FirmwareHistoryDao firmwareHistoryDao;

    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Alert Log Data 
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
    	log.debug("EV_214_2_0_Action Start");
    	
        log.debug("Event[eventOTAStart] EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"] TriggerId["+
                event.getEventAttrValue("stringEntry")+"] TriggerState["+event.getEventAttrValue("byteEntry")+"]");

        // Initialize
        String triggerId = event.getEventAttrValue("stringEntry");
        // 문자가 섞여 있어 대체한다.
        triggerId = triggerId.replaceAll("[a-zA-Z]", "");
        
        String mcuId=trap.getMcuId();

        event.append(EventUtil.makeEventAlertAttr("message",
                                             "java.lang.String",
                                             "MCU["+mcuId+"] OTA Start"));

        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            
            MCU mcu = mcuDao.get(mcuId);
    
            if(mcu != null)
            {
                event.append(EventUtil.makeEventAlertAttr("message",
                                                     "java.lang.String",
                                                     "MCU["+mcuId+"] Trigger ID["+triggerId+"] OTA Start"));
    
    		    Set<Condition> condition = new HashSet<Condition>();
                condition.add(new Condition("id.trId", new Object[]{Long.parseLong(triggerId)}, null, Restriction.EQ));
                
                List<FirmwareHistory> fwHistoryList = firmwareHistoryDao.findByConditions(condition);
                if (fwHistoryList.size() > 0) {
                    FirmwareHistory firmwareHistory = fwHistoryList.get(0);
                    if(firmwareHistory != null){                	
                    	
                        if(firmwareHistory.getEquipKind()!= null && !"".equals(firmwareHistory.getEquipKind())){
                            event.append(EventUtil.makeEventAlertAttr("equipKind",
                                                                 "java.lang.String",
                                                                 firmwareHistory.getEquipKind()));
                        }
                        if(firmwareHistory.getEquipVendor() != null && !"".equals(firmwareHistory.getEquipVendor())){
                            event.append(EventUtil.makeEventAlertAttr("equipType",
                                                                 "java.lang.String",
                                                                 firmwareHistory.getEquipType()));
                        }
                        if(firmwareHistory.getEquipVendor() != null && !"".equals(firmwareHistory.getEquipVendor())){
                            event.append(EventUtil.makeEventAlertAttr("equipVendor",
                                                                 "java.lang.String",
                                                                 firmwareHistory.getEquipVendor()));
                        }
                        if(firmwareHistory.getEquipModel() != null && !"".equals(firmwareHistory.getEquipModel())){
                            event.append(EventUtil.makeEventAlertAttr("equipModel",
                                                                 "java.lang.String",
                                                                 firmwareHistory.getEquipModel()));
                        }
                    }
                }
            }else{
                log.debug("Event[eventOTAStart] MCU Is Not Exist");
            }
            
            FirmwareUtil.updateTriggerHistory(triggerId,FW_TRIGGER.Start.getCode(),TR_STATE.Running.getCode());
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    	log.debug("EV_214_2_0_Action End");
    }
}