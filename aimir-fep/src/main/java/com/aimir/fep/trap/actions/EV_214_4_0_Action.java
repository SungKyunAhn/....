package com.aimir.fep.trap.actions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aimir.constants.CommonConstants.FW_OTA;
import com.aimir.constants.CommonConstants.FW_STATE;
import com.aimir.constants.CommonConstants.FW_TRIGGER;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.FirmwareHistoryDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FirmwareUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.FirmwareHistory;
import com.aimir.model.device.Modem;
import com.aimir.notification.FMPTrap;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

/**
 * Event ID : 214.4.0 Processing Class
 *
 * @author kaze
 * @version $Rev: 1 $, $Date: 2008-10-16 15:59:15 +0900 $,
 */
@Component
public class EV_214_4_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_214_4_0_Action.class);
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    FirmwareHistoryDao firmwareHistoryDao;
    
    /**
     * execute event action
     *
     * @param trap
     *            - FMP Trap(MCU Event)
     * @param event
     *            - Event Alert Log Data
     */
    @SuppressWarnings("unchecked")
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
    	System.out.println("EV_214_4_0_Action Start");
    	log.debug("EV_214_4_0_Action Start");
    	
        log.debug("Event[eventOTAResult] EventCode[" + trap.getCode() + "] MCU["
                  + trap.getMcuId() + "] TriggerId["+event.getEventAttrValue("stringEntry")+"]");

        // Initialize
        String mcuId = trap.getMcuId();
        String triggerId = event.getEventAttrValue("stringEntry");
        // 문자가 섞여 있어 대체한다.
        triggerId = triggerId.replaceAll("[a-zA-Z]", "");
        String sensorId = event.getEventAttrValue("eui64Entry");
        FW_OTA OTAStep=FW_OTA.Init;
        //String OTAStepStr="";
        FW_STATE OTAState=FW_STATE.Fail;
        //String OTAStateStr="";
        int errCode=0;//AimirModel.FW_ERRCODE_NORMAL;
        String errStr="Normal";//AimirModel.FW_ERRSTR_NORMAL;
        
        int state = FW_STATE.Unknown.getState();
        
        try{
        	state = Integer.parseInt(event.getEventAttrValue("byteEntry"));
        }catch(NumberFormatException e){
        	log.error(e);
        }
        
        FW_STATE[] states = FW_STATE.values();
        OTAState = FW_STATE.Unknown;
        
        //state 값에 해당하는 enum을 찾아 설정한다.
        for (FW_STATE fwSTATE : states) {
        	if(fwSTATE.getState()==state){
        		OTAState = fwSTATE;
        		break;
        	}
		}
        
        int step = FW_OTA.All.getStep();
        try{
        	step = Integer.parseInt(event.getEventAttrValue("byteEntry.3"));
        }catch(NumberFormatException e){
        	log.error(e);
        }
        
        OTAStep=FW_OTA.stepOf(step);
        
        try{
        	errCode=Integer.parseInt(event.getEventAttrValue("byteEntry.4"));
        }catch(NumberFormatException e){
        	log.error(e);
        	errCode=0;
        }
        /*
        EventAttr[] eventAttr = event.getEventAttrs();
        int i = 0;
        if(eventAttr != null && eventAttr.length >= 2){
            if (eventAttr[i].getAttrName().indexOf("byteEntry") != -1){
                OTAState = Integer.parseInt(eventAttr[i++].getValue());
            }
            if (eventAttr[i].getAttrName().indexOf("byteEntry") != -1){
                OTAStep = Integer.parseInt(eventAttr[i++].getValue());
            }
            if (eventAttr[i].getAttrName().indexOf("byteEntry") != -1){
                errCode = Integer.parseInt(eventAttr[i++].getValue());
            }
        }
        */
        log.debug("triggerd["+triggerId+"], sensorId["+sensorId+"] type["+OTAState+"], otaStep["+OTAStep+"], errCode["+errCode+"]");

        if((OTAStep.getStep() & FW_OTA.All.getStep()) == FW_OTA.All.getStep()){
        	OTAStep=FW_OTA.All;
        }
        else if((OTAStep.getStep() & FW_OTA.Scan.getStep()) == FW_OTA.Scan.getStep()){
        	OTAStep=FW_OTA.Scan;
        }
        else if((OTAStep.getStep() & FW_OTA.Install.getStep()) == FW_OTA.Install.getStep()){
        	OTAStep=FW_OTA.Install;
        }
        else if((OTAStep.getStep() & FW_OTA.Verify.getStep()) == FW_OTA.Verify.getStep()){
        	OTAStep=FW_OTA.Verify;
        }
        else if((OTAStep.getStep() & FW_OTA.DataSend.getStep()) == FW_OTA.DataSend.getStep()){
        	OTAStep=FW_OTA.DataSend;
        }
        else if((OTAStep.getStep() & FW_OTA.Check.getStep()) == FW_OTA.Check.getStep()){
        	OTAStep=FW_OTA.Check;
        }

        StringBuffer buf = new StringBuffer();
        /*
        //OTAState
        switch (OTAState.getState())
        {
        case FW_STATE.Success.getState():
            OTAState=FW_STATE.Success;
            break;
        case FW_STATE.Fail.getState():
        	OTAState=FW_STATE.Fail;
            break;
        case FW_STATE.Cancel.getState():
        	OTAState=FW_STATE.Cancel;
            break;
        }
        */

        buf.append("MCU[" + mcuId + "] OTAStep["+OTAStep.name()+"] Result "+OTAState.name()+"!");

        //ErrCode
        switch(errCode){
        	case 0:
        		errStr="No Error";
        		break;
        	case 1:
        		errStr="Open Error";
        		break;
        	case 2:
        		errStr="Connect Error";
        		break;
        	case 3:
        		errStr="Inventory Scan Error";
        		break;
        	case 4:
        		errStr="Firmware Not Found";
        		break;
        	case 5:
        		errStr="Send Error";
        		break;
        	case 6:
        		errStr="Verify Error";
        		break;
        	case 7:
        		errStr="Install Error";
        		break;
        	case 8:
        		errStr="User Cancel";
        		break;
        	case 9:
        		errStr="Different Model Error";
        		break;
        	case 10:
        		errStr="Match Version Error";
        		break;
        	case 11:
        		errStr="Version Error";
        		break;
        	case 12:
        		errStr="Model Error";
        		break;
        	case 30:
        		errStr="Memory Error";
        		break;
        	case 40:
        		errStr="Coordinator Error";
        		break;
        }

        event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", buf
                .toString()));

        Modem modem = modemDao.get(sensorId);
        if (modem != null) {
            event.setActivatorId(modem.getDeviceSerial());
            event.setActivatorType(TargetClass.Modem);
            event.setActivatorIp(modem.getIpAddr());
            event.append(EventUtil.makeEventAlertAttr("message",
                                                 "java.lang.String",
                                                 "MCU["+mcuId+"] Trigger ID["+triggerId+"] Equip ID["+sensorId+"] OTAStep["+OTAStep+"] OTA Result["+OTAState.name()+"] Error["+errStr+"]"));

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
            log.debug("Event[eventOTAResult] Sensor Is Not Exist");
        }
        log.debug("updateOTAHistory TriggerId["+triggerId+"]");
        
        FW_TRIGGER trstep = FW_TRIGGER.End;
        int triggerState = TR_STATE.Unknown.getCode();
        
        if(FW_STATE.Success.getState() == 0){
            trstep = FW_TRIGGER.Success;
            triggerState = TR_STATE.Success.getCode();
        }
        if(FW_STATE.Fail.getState() == 1){
            trstep = FW_TRIGGER.End;
            triggerState = TR_STATE.Terminate.getCode();
        }
        if(FW_STATE.Cancel.getState() == 2){
            trstep = FW_TRIGGER.End;
            triggerState = TR_STATE.Terminate.getCode();
        }        
        
        FirmwareUtil.updateTriggerHistory(triggerId,trstep.getCode(),triggerState);
        FirmwareUtil.updateOTAHistory(triggerId, sensorId, OTAStep, OTAState, errStr);
    	log.debug("EV_214_4_0_Action End ");
    }
}