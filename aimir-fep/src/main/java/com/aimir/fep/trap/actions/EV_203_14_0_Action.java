package com.aimir.fep.trap.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.aimir.fep.trap.common.EV_Action;
import com.aimir.model.device.EventAlertLog;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 203.14.0 Processing Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Component
public class EV_203_14_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_203_14_0_Action.class);

    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EV_203_14_0_Action(eventSensorRecovery : EventCode[" 
                + trap.getCode() +"] MCU["+trap.getMcuId()+"]");
/*
        String mcuId = trap.getMcuId();
        String oid = null;
        SensorPage sp = null;
        Iterator iter = trap.getVarBinds().keySet().iterator();
        byte[] bx= null;
        byte[] tmpbx = new byte[8];
        byte[] tmpbx2 = null;
        String sensorId = null;
        //CmdOperationUtil.removeEventAttributeAll(event);
        EventAttr ea = null;
        String eaValue= null;
        while (iter.hasNext())
        {
            oid = (String) iter.next();
            log.debug("oid="+oid);
            bx = ((OCTET)trap.getVarBinds().get(oid)).getValue();
            //log.debug("data="+Hex.decode(bx));
            System.arraycopy(bx,0,tmpbx,0,tmpbx.length);
            sensorId = Hex.decode(tmpbx);
            log.debug("sensorId="+sensorId);
            tmpbx2 = new byte[bx.length - tmpbx.length];
            System.arraycopy(bx,8,tmpbx2,0,tmpbx2.length);
            //log.debug("data="+Hex.decode(tmpbx2));
            sp = SensorPage.decode(1,tmpbx2); 
            log.debug("sensor page ="+sp);
            try 
            { 
                //CmdOperationUtil.saveLPData(mcuId,sensorId,sp); //TODO IMPLEMENT
                if(sp.getPageNumber() >= 70 && sp.getPageNumber() <= 100)
                {
                    eaValue = ((ZRULPPage)sp).getDate(); 
                    ea = EventUtil.makeEventAttr(sensorId, 
                            "java.lang.String", eaValue);
                    event.append(ea);
                }
            }catch(Exception ex) { log.error(ex,ex); }
        }
*/
        log.debug("Sensor Recovery Event Action Compelte");
    }
}
