package com.aimir.fep.trap.actions.NG;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aimir.dao.device.ChangeLogDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : NG_210.2.0 (Discovery result)
 * <br>PLC-G3 Modem
 *
 * @author goodjob
 * @version $Rev: 1 $, $Date: 2015-07-08 10:00:00 +0900 $,
 */
@Component
public class EV_NG_210_2_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_NG_210_2_0_Action.class);
    
    @Autowired
    MCUDao dcuDao;
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    CommandGW commandGW;
    
    @Autowired
    ChangeLogDao clDao;
    
    @Autowired
    DeviceModelDao deviceModelDao;
    
    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    LocationDao locationDao;
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(DCU Event)
     * @param event - Event Alert Log Data
     * 
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EventName[Discovery result] "+" EventCode[" + trap.getCode()+"] Modem["+trap.getSourceId()+"]");
        String mcuId = trap.getMcuId();
        String ipAddr = trap.getIpAddr();
        String modemSerial = trap.getSourceId();
        
        EventAlertAttr[] eventAttr = event.getEventAlertAttrs().toArray(new EventAlertAttr[0]);
        
        int i = 0;
        String targetShortID = eventAttr[i++].getValue();
        String result = "Success";
        int resultStatus = Integer.parseInt(eventAttr[i++].getValue());
        if(resultStatus != 0){
        	result = "Fail";
        }
        
        log.info("Target Short ID=["+targetShortID+"] Result Status=["+resultStatus+"]");
       
        int noOfForwardPath = Integer.parseInt(eventAttr[i++].getValue());
        
        for(int p = 0; p < noOfForwardPath; p++){
        	String pathId = eventAttr[i++].getValue();
        	String pathShortId = eventAttr[i++].getValue();
        	int pathLinkCost = Integer.parseInt(eventAttr[i++].getValue());        	
        	log.info("Forward Path, PathId=["+pathId+"] Path Short Id=["+pathShortId+"] Path Link Cost=["+pathLinkCost+"]");
        }

        int noOfReversePath = Integer.parseInt(eventAttr[i++].getValue());
        
        for(int r = 0; r < noOfReversePath; r++){
        	String pathId = eventAttr[i++].getValue();
        	String pathShortId = eventAttr[i++].getValue();
        	int pathLinkCost = Integer.parseInt(eventAttr[i++].getValue());
        	log.info("Reverse Path, PathId=["+pathId+"] Path Short Id=["+pathShortId+"] Path Link Cost=["+pathLinkCost+"]");
        }

        log.debug("PLC Modem Discovery result Action Compelte");
    }

}
