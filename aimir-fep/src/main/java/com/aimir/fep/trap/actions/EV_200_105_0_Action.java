package com.aimir.fep.trap.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.MIBUtil;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 200.105.0 Processing Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Service
public class EV_200_105_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_200_105_0_Action.class);

    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EV_200_105_0_Action(eventMcuDiagnosis : EventCode[" 
                + trap.getCode() +"] MCU["+trap.getMcuId()+"]");

        MIBUtil mibUtil = MIBUtil.getInstance();
        String oid = mibUtil.getOid("streamEntry").toString();
        byte[] bx = ((OCTET)trap.getVarBinds().get(oid)).getValue();
        event.remove("streamEntry");

        EventAlertAttr ea = null;
        String state = null;

        for(int i = 0 ; i < bx.length ; i++)
        {
            if(i==0) {
                ea = EventUtil.makeEventAlertAttr("MCU State",
                        "java.lang.String", getStateStr(bx[i]));
                event.append(ea);
            } else if( i == 1) {
                ea = EventUtil.makeEventAlertAttr("Sink State",
                        "java.lang.String", getStateStr(bx[i]));
                event.append(ea);
            } else if( i == 3) {
                ea = EventUtil.makeEventAlertAttr("Power State",
                        "java.lang.String", getStateStr(bx[i]));
                event.append(ea);
            } else if( i == 4) {
                ea = EventUtil.makeEventAlertAttr("Battery State",
                        "java.lang.String", getStateStr(bx[i]));
                event.append(ea);
            } else if( i == 5) {
                ea = EventUtil.makeEventAlertAttr("Temperature State",
                        "java.lang.String", getStateStr(bx[i]));
                event.append(ea);
            } else if( i == 6) {
                ea = EventUtil.makeEventAlertAttr("Memory State",
                        "java.lang.String", getStateStr(bx[i]));
                event.append(ea);
            } else if( i == 7) {
                ea = EventUtil.makeEventAlertAttr("Flash State",
                        "java.lang.String", getStateStr(bx[i]));
                event.append(ea);
            } else if( i == 8) {
                if(bx[i] == 0)
                    state = "Noraml";
                else if(bx[i] == 1)
                    state = "Abnormal(NO MODEM)";
                else if(bx[i] == 2)
                    state = "Abnormal(NO SIM CARD)";
                else if(bx[i] == 3)
                    state = "Abnormal(NOT READY)";
                else if(bx[i] == 4)
                    state = "Abnormal(BAD CSQ)";

                ea = EventUtil.makeEventAlertAttr("GSM State",
                        "java.lang.String", state);
                event.append(ea);
            } else if( i == 9) {
                ea = EventUtil.makeEventAlertAttr("Ethernet State",
                        "java.lang.String", getStateStr(bx[i]));
                event.append(ea);
            }
        }

        log.debug("MCU Diagnois Event Action Compelte");
    }

    private String getStateStr(byte sval)
    {
        String state = "Normal";
        if(sval == 1)
            state = "Abnormal";
        return state;
    }
}
