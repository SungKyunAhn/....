package com.aimir.fep.trap.actions;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.fep.protocol.fmp.frame.ErrorCode;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.notification.FMPTrap;


/**
 * Event ID : 203.109.0 (eventEnergyLevelChange)Processing Class
 *
 * @author C.H.S
 * @version $Rev: 1 $, $Date: 2012-01-09 15:59:15 +0900 $,
 */
@Component
public class EV_203_109_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_203_109_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    ContractDao contractDao;
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EV_203_109_0_Action : EventName[eventEnergyLevelChange] "+" EventCode[" + trap.getCode()+"] MCU["+trap.getMcuId()+"]");
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            
            for (EventAlertAttr ea : event.getEventAlertAttrs()) {
                log.debug("attr[" + ea.getAttrName() + "] type[" + ea.getAttrType() + "] value[" + ea.getValue()+"]");
            }
            
            String modemId = event.getEventAttrValue("eui64Entry");
            int prevLevel = Integer.parseInt(event.getEventAttrValue("byteEntry"));
            int currLevel = Integer.parseInt(event.getEventAttrValue("byteEntry.1"));
            int errorCode = Integer.parseInt(event.getEventAttrValue("intEntry"));
            String issueTime = event.getEventAttrValue("timeEntry");
            
            // 실제 방생 시간으로 변경한다.
            event.setOpenTime(issueTime);
            
            log.debug("MODEM_ID[" + modemId + "]");
            
            Modem modem = modemDao.get(modemId);
            
            Meter meter = null;
            
            if (modem != null) {
                if (modem.getMeter() != null && !modem.getMeter().isEmpty())
                    meter = modem.getMeter().iterator().next();
                if (meter != null && meter.getMdsId() != null)
                    log.debug("METER_ID[" + meter.getMdsId() + "]");
            }
            
            if(meter != null)
            {
                event.setActivatorId(meter.getMdsId());
                event.setActivatorType(meter.getMeterType().getName());
                event.setLocation(meter.getLocation());
            }
            else if (modem != null) {
                event.setActivatorId(modem.getDeviceSerial());
                event.setActivatorType(modem.getModemType().name());
                event.setLocation(modem.getLocation());
            }
            
            String message = "";
            if (errorCode != 0)
                message += ErrorCode.getCodeString(errorCode);
            
            if (meter != null) {
                if (prevLevel == 1 && currLevel == 15) {
                    message += " Relay Off";
                    if (meter.getMeterStatus() == null || !meter.getMeterStatus().getName().equals(MeterStatus.CutOff.name()))
                        meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.CutOff.name()));
                    
                    Contract contract = meter.getContract();
                    if(contract != null && contract.getStatus().getCode().equals(CommonConstants.ContractStatus.NORMAL.getCode())) {
                        Code pauseCode = codeDao.getCodeIdByCodeObject(CommonConstants.ContractStatus.PAUSE.getCode());
                        contractDao.updateStatus(contract.getId(), pauseCode);
                    }
                }
                else {
                    message += " Relay On";
                    if (meter.getMeterStatus() == null 
                            || ( meter.getMeterStatus() != null 
                            && meter.getMeterStatus().getName() != null 
                            && meter.getMeterStatus().getName().equals(MeterStatus.CutOff.name()) )
                            )
                        meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.Normal.name()));
                    
                    Contract contract = meter.getContract();
                    if(contract != null && contract.getStatus().getCode().equals(CommonConstants.ContractStatus.PAUSE.getCode())) {
                        Code normalCode = codeDao.getCodeIdByCodeObject(CommonConstants.ContractStatus.NORMAL.getCode());
                        contractDao.updateStatus(contract.getId(), normalCode);
                    }
                }
            }
            
            EventAlertAttr ea = EventUtil.makeEventAlertAttr("message", "java.lang.String", message);
            event.append(ea);
            
            log.debug(event.toString());
            
            log.debug(modemId + ":" + issueTime + ":ErrorCode[" + errorCode + "]:PreviousLevel[" + prevLevel + "]:CurrentLevel[" + currLevel + "]");
            log.debug(trap.toString());
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }
}
