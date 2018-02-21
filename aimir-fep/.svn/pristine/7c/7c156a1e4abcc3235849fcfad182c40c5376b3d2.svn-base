package com.aimir.fep.trap.actions.SP;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.notification.FMPTrap;
import com.aimir.util.TimeUtil;

/**
 * Event ID : EV_SP_200_35_0 Processing Class
 *
 * @author Tatsumi
 * @version $Rev: 1 $, $Date: 2016-05-17 15:59:15 +0900 $,
 */
@Component
public class EV_SP_200_35_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_SP_200_35_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    MCUDao mcuDao;
    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EV_SP_200_35_0_Action : EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");
        
//        TransactionStatus txstatus = null;
        try {
//            txstatus = txmanager.getTransaction(null);
            
            String mcuId = trap.getMcuId();
            MCU mcu = mcuDao.get(mcuId);
            log.debug("EV_SP_200_35_0_Action : mcuId[" + mcuId + "]");
    
            if(mcu != null)
            {
                mcu.setLastCommDate(TimeUtil.getCurrentTime());
               
                log.debug("MCU Configuration Changed Action Started");
//                EventAlertLog alert = EventUtil.findOpenAlert(event);
//                
//                if (alert == null)
//                {
//                    log.debug("MCU Configuration Changed Action Failed");
//                    return;
//                }
//    
//                event = alert;
                byte configType = Byte.parseByte(event.getEventAttrValue("byteEntry"));
                String confName = event.getEventAttrValue("streamEntry");
                if ( confName == null ){
                	confName = "";
                }
                String changedDescr = event.getEventAttrValue("streamEntry.1");
                if ( changedDescr == null ){
                	changedDescr = "";
                }
                String msg = "";
                String operation = "";

                switch (configType) {
                case 0x00 :
                	operation = "Scheduler Added";
                	msg = operation + ":" + confName + "(" + changedDescr + ")";
                	break;
                case 0x01 :
                	operation = "Scheduler Modified";
                	msg = operation + ":" + confName + "(" + changedDescr + ")";
                	break;
                case 0x02 :
                	operation = "Scheduler Deleted";
                	msg = operation + ":" + confName;
                	break;
                default:
                	operation = "Scheduler Unknown operation";
                	msg = operation + ":" + confName;
                }
                
//                String oid = ((OID)trap.getVarBinds().get("1.10")).getValue();
//                if ( oid.length()> 0)
//                {
//                	configOid = MIBUtil.getInstance().getName(oid);
//                    event.append(EventUtil.makeEventAlertAttr("configoid",
//                            "java.lang.String",
//                            configOid));
//                }

                event.setActivatorId(trap.getSourceId());
                event.setActivatorType(TargetClass.DCU);
                event.append(EventUtil.makeEventAlertAttr("message",
                        "java.lang.String", msg));
                log.debug("MCU Configuration Changed Action " + msg);
            } 
            else
            {
                log.debug("MCU Configuration Changed Action failed : Unknown MCU");
            }
        }
        catch (Exception e)
        {
            log.error(e,e);
        }
//        finally {
//            if (txstatus != null) txmanager.commit(txstatus);
//        }
    }
}
