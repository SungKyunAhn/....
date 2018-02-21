package com.aimir.fep.trap.actions;

import java.util.Iterator;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.MIBUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Modem;
import com.aimir.notification.FMPTrap;
import com.aimir.notification.VarBinds;

/**
 * Event ID : 203.3.0 Processing Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Component
public class EV_203_3_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_203_3_0_Action.class);

    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    MCUDao mcuDao;
    
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
        log.debug("EV_203_3_0_Action : EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");

        TransactionStatus txstatus = null;
        String modemId = event.getEventAttrValue("id");
        
        try {
            txstatus = txmanager.getTransaction(null);
            
            removePageAttr(trap,event);
            if (modemId == null)
            {
                log.error("sensor id is null");
                return;
            }
            
            String sensorType = 
                event.getEventAttrValue("otherSensorType");
    
            if (sensorType == null)
            {
                log.error("sensor type is null");
                return;
            }
            
            MCU mcu = mcuDao.get(trap.getMcuId());
            if (mcu == null)
            {
                log.warn("does not exist MCU["
                        +trap.getMcuId()+"] connected sensor["
                        +modemId+"] in MI Repository");
                return;
            }
    
            // Initialize
            CommandGW gw = new CommandGW();
            String mcuId = trap.getMcuId();
            String eventOid = trap.getCode();
    
            // Log
            log.debug("EV_203_3_0_Action : modemId[" + modemId + "]");
          
            
            Modem modem = modemDao.get(modemId);
            if(modem == null)
            {
                log.warn("does not exist Sensor["+modemId
                        +"] in MI Repository ");
                return;
            }
    
            ModemType modemType = null;
            modemType = modem.getModemType();
        }catch(Exception ex) 
        {
            log.warn("sensor Type does not setting sensor["
                    + modemId+"]");
            return;
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }

        //TODO DECODE SENSOR PAGE
        /*
        // Decode
        String oid = null;
        ArrayList al = new ArrayList();
        SensorPage sp = null;
        VarBinds vb = trap.getVarBinds();
        Iterator iter = vb.keySet().iterator();
        FMPVariable variable = null;
        while (iter.hasNext())
        {
            oid = (String) iter.next();
            log.debug("oid="+oid);
            if(!DataUtil.isSubMIBByOid("sensorPageEntry",oid))
                continue;
            variable = (FMPVariable)vb.get(oid);
            sp = SensorPage.decode(sType,
                    ((OCTET)variable).getValue());
            al.add(sp);
        }

        SensorPage[] sps = 
            (SensorPage[])al.toArray( new SensorPage[0]);

        MOINSTANCE[] modSensorMOs = 
            SensorPageUtil.getSensorToMO(sps);

        log.debug("modSensorMOs length["+modSensorMOs.length+"]");
        for(int i = 0 ; i < modSensorMOs.length ; i++)
        {
            updateModem(sensorMO,modSensorMOs[i],event);
        }

        // set lastModified and lastLinkTime
        String curTime = TimeUtil.getCurrentTime();
        MOINSTANCE mo = IUtil.makeDummyMO(sensorMO.getClassName());
        mo.setName(sensorMO.getName());
        if(event.getEventAttrs().length > 1)
            mo.addProperty(
                    new MOPROPERTY("lastModifiedDate",curTime)); 
        mo.addProperty(new MOPROPERTY("lastLinkTime",curTime));
        try 
        { 
            //IUtil.getMIO().SetInstance(mo); 
            CmdOperationUtil.updateMO(mo, eventOid, true);
        } 
        catch(Exception ex){}

        if (sp == null)
        {
            log.error("no sensor page exist");
            return;
        }

        try
        {
            String typeString = 
                AimirModel.getSensorClass(sp.getSensorType());
            log.debug("sensorType="+typeString);
            EventAttr ea = EventUtil.makeEventAttr("sensorType", 
                    "java.lang.String", typeString);
            event.append(ea);
        }
        catch (Exception e)
        {
            log.error(e,e);
        }
        */

        log.debug("Sensor Change Event Action Compelte");
    }

    private void removePageAttr(FMPTrap trap, EventAlertLog event)
    {
        MIBUtil mibUtil = MIBUtil.getInstance();
        VarBinds vb = trap.getVarBinds();
        Iterator iter = vb.keySet().iterator();
        String[] keys = (String[])vb.keySet().toArray(new String[0]);
        for(int i = 0; i < keys.length ; i++)
        {
            //if(keys[i].startsWith("4.20."))
            if(DataUtil.isSubMIBByOid(null, "sensorPageEntry",keys[i]))
            {
                String attrName = mibUtil.getName(keys[i]);
                event.remove(attrName);
            }
        }
    }

    
    /*
    private void updateMO(MOINSTANCE oldmo, MOINSTANCE newmo,
            Event event) throws Exception
    {
        log.debug("updateMO["+oldmo.getName()+"]");
        Vector props = newmo.getProperties();
        int plen = props.size();
        MOPROPERTY prop = null;
        String pname = null;
        String ptype = null;
        String oldval = null;
        String newval = null;
        EventAttr attr = null;
        for(int i = 0 ; i < plen ; i++)
        {
            prop = (MOPROPERTY)props.elementAt(i);
            pname = prop.getName();
            if(!oldmo.isHasProperty(pname))
                continue;
            newval = newmo.getPropertyValueString(pname);
            oldval = oldmo.getPropertyValueString(pname);
            if(newval.equals(oldval))
            {
                newmo.removeProperty(pname);
                continue;
            }
            ptype = oldmo.getProperty(pname).getType();
            attr = EventUtil.makeEventAttr(pname,ptype,newval);
            event.append(attr);
        }

        if(newmo.getProperties().size() > 0)
        {
            newmo.setName(oldmo.getName());
            //IUtil.getMIO().SetInstance(newmo);
            CmdOperationUtil.updateMO(newmo, true);
        }
    }

    */
    private void updateModem(Modem omodem, Modem nmodem,
            EventAlertLog event) throws Exception
    {
    	//TODO IMPLEMENT Modem Update
    	/*
        if(!osensor.getClassName().equals(nsensor.getClassName()))
        {
            MOINSTANCE[] insts  = CmdOperationUtil.getSensorSPCMO(
                    osensor.getName());
            log.debug("getSensorSPCMO("+osensor.getName()
                    +") insts.length["+insts.length+"]");
            for(int i = 0 ; i < insts.length ; i++)
            {
                log.debug("insts["+i+"] = ["+insts[i].getName()+"]");
                updateMO(insts[i],nsensor,event);
            }
            return;
        } 
        updateMO(osensor,nsensor,event);
        */
    }
}
