package com.aimir.fep.trap.actions;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Modem;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 203.105.0 (sensor uninstall)
 * <br> MCU 
 * <br>
 *
 * @author J.S Park
 * @version $Rev: 1 $, $Date: 2007-05-30 15:59:15 +0900 $,
 */
@Component
public class EV_203_106_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_203_106_0_Action.class);

    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
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
        log.debug("EventName[eventSensorUninstall] "+" EventCode[" + trap.getCode()+"] MCU["+trap.getMcuId()+"]");

        String mcuId = trap.getMcuId();
        String sensorId = event.getEventAttrValue("sensorID");
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            
            // get sensor
            Modem modem = modemDao.get(sensorId);
            
            if (modem != null) {
                // Modem을 제거했기 때문에 미터의 모뎀 관계를 null로 설정한다.
                /*if (meterList != null) {
                    for (Meter m : (Meter[])meterList.toArray(new Meter[0])) {
                        m.setModem(null);
                    }
                }*/
                MCU _mcu = modem.getMcu();
                if (_mcu != null) {
                	if ( mcuId.equals(_mcu.getSysID()) ) {
                	    modem.setMcu(null);
                    }
                    else {
                	    try {
                		    // CommandGW gw = new CommandGW();
                            // modemDao.delete(modem);
                        }
                        catch (Exception e) {
                            log.error("cmdDeleteSensor ERROR msg[" + e.getMessage() + "]");
                        }
                    }
                }
                
                modem.setMcu(null);
                event.setActivatorId(modem.getDeviceSerial());
                event.setActivatorType(TargetClass.Modem);
            }
            event.append(EventUtil.makeEventAlertAttr("mcuID", 
                                                 "java.lang.String", mcuId));
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }
    
    
}
