package com.aimir.fep.trap.common;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.EventAlertDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.trap.data.EventClass;
import com.aimir.fep.util.MIBUtil;
import com.aimir.fep.protocol.fmp.datatype.FMPVariable;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.model.device.EventAlert;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Modem;
import com.aimir.notification.FMPTrap;
import com.aimir.notification.VarBinds;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;

/**
 * make Event Data
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */

@Component
public class EventMaker
{
    private static Log log = LogFactory.getLog(EventMaker.class);
    @Autowired
    private EventAlertDao eventAlertDao;
    @Autowired
    private MCUDao mcuDao;
    @Autowired
    private ModemDao modemDao;
    
    /**
     * get event class
     */
    @SuppressWarnings("unused")
	private EventClass getEventClass(String eventOid)
        throws Exception
    {
        EventClass ec = new EventClass();

        try
        {
        	EventAlert eventAlert = eventAlertDao.findByCondition("oid", eventOid);
            ec.setClassId(eventAlert.getOid());
            ec.setName(eventAlert.getName());
            ec.setDescription(eventAlert.getDescr());
            // ec.setPriority(eventAlert.);
            ec.setTimeout(eventAlert.getTimeout());
            ec.setMessage(eventAlert.getMsgPattern());

            return ec;
        }
        catch(Exception ex)
        {
            log.error("getEventClass failed ",ex);
            return ec;
        }
    }

    /**
     * make Event Alert data
     * @param trap - FMP Trap(MCU Event)
     * @param rule - Event Rule
     * @return Event
     */
    public EventAlertLog getEventAlertLog(FMPTrap trap) throws Exception
    {
        int srcType = Byte.parseByte(trap.getSourceType());
        String srcId = trap.getSourceId();
        String timeStamp = trap.getTimeStamp();
        log.info("Before Event [" + trap.getCode() + "]");
        EventAlertLog event = new EventAlertLog();        
        Set<Condition> condition = new HashSet<Condition>();
        condition.add(new Condition("oid", new Object[]{"%"+trap.getCode()+"%"}, null, Restriction.LIKE));
        List<?> eventAlertList = eventAlertDao.findByConditions(condition);
        
        // log.info("event size[" + eventAlertList.size()+"]");
        if (eventAlertList == null || eventAlertList.size() == 0)
            throw new Exception(trap.getCode() + " event is not in eventalert table");
        
    	EventAlert eventAlert = (EventAlert)eventAlertList.get(0);
    	log.info("After Event: "+eventAlert );
    	// OID 해당하는 이벤트가 없으면 디폴트 203.10 을 찾는다. Equipment Notification
    	if (eventAlert == null) {
    	    condition = new HashSet<Condition>();
    	    condition.add(new Condition("oid", new Object[] {"%203.10.0%"}, null, Restriction.LIKE));
    	    eventAlertList = eventAlertDao.findByConditions(condition);
    	    
    	    if (eventAlertList == null || eventAlertList.size() == 0)
    	        throw new Exception("203.10.0 event is not in eventalert table");
    	    
    	    eventAlert = (EventAlert)eventAlertList.get(0);
    	}
    	event.setEventAlert(eventAlert);
    	event.setActivatorId(srcId);
        if(srcType == ServiceDataConstants.E_SRCTYPE_MCU.getValue()){
        	MCU mcu = mcuDao.get(srcId);
        	event.setActivatorType(TargetClass.DCU);
        	if(mcu != null){
                event.setActivatorIp(mcu.getIpAddr());
                event.setLocation(mcu.getLocation());
                event.setSupplier(mcu.getSupplier());
        	}
        }
        else {
        	Modem modem = modemDao.get(srcId);
        	if(modem != null && modem.getModemType() != null){
        	    if (modem.getModemType() == ModemType.ZRU)
                    event.setActivatorType(TargetClass.ZRU);
                else if (modem.getModemType() == ModemType.SubGiga)
                    event.setActivatorType(TargetClass.SubGiga);
                else if (modem.getModemType() == ModemType.MMIU)
                    event.setActivatorType(TargetClass.MMIU);
        	    
        		event.setActivatorIp(modem.getIpAddr());
            	event.setLocation(modem.getLocation());
                event.setSupplier(modem.getSupplier());
        	}
        	else
        	    event.setActivatorType(TargetClass.SubGiga);
        }
        event.setSeverity(eventAlert.getSeverity());     
        event.setStatus(EventStatus.Open);
        event.setOccurCnt(new Integer(1));
        event.setOpenTime(DateTimeUtil.getDST(null, timeStamp));
        event.setWriteTime(DateTimeUtil.getDateString(new Date()));
        // log.debug("timeStamp["+timeStamp+"]");

        // log.debug("event : " + event.toString());
        VarBinds vb = trap.getVarBinds();
        Iterator<?> iter = vb.keySet().iterator();
        FMPVariable variable = null;

        String oid = null;
        Object obj = null;
        while(iter.hasNext())
        {
            oid = (String)iter.next();
            obj = vb.get(oid);
            if (obj instanceof FMPVariable) {
                variable = (FMPVariable)obj;
                if (variable != null && variable.getJavaSyntax() != null && !"null".equals(variable.getJavaSyntax())) {
                    event.append(makeEventAlertAttr(trap.getCode(), oid, variable.getJavaSyntax(), variable.toString()));
                    if (variable instanceof OCTET) {
                        event.append(makeEventAlertAttr(trap.getCode(), oid+".hex", variable.getJavaSyntax(), ((OCTET)variable).toHexString()));
                    }
                }
                else {
                    event.append(makeEventAlertAttr(trap.getCode(), oid, String.class.getName(), ""));
                }
            }else {
            	event.append(makeEventAlertAttr(trap.getCode(), oid, String.class.getName(), ""));
            }
            // log.debug("OID[" + oid + "] NAME[" + attr.getAttrName() + "] VALUE[" + attr.getValue() + "]");
        }
        return event;
    }

    private EventAlertAttr makeEventAlertAttr(String code, String oid, String attrType, String value)
    throws Exception {
    	
        EventAlertAttr attr = new EventAlertAttr();
        String ext = "";
        if(oid.split("[.]").length > 3)
        {
            log.debug("oid length great than 3 : "+oid);
            int dot3rd = 0;
            for (int i = 0; i < 3; i++) {
                dot3rd = oid.indexOf(".", dot3rd+1);
            }
            ext = oid.substring(dot3rd);
            oid = oid.substring(0,dot3rd);
            log.debug("after make normal oid value ["+oid + "] ext[" + ext + "]");
        }
        attr.setOid(oid);
        if(code.indexOf("_") > 0){
        	String nameSpace = code.substring(0,2);
        	log.debug("CODE=["+code+"] OID="+oid+"]");
        	attr.setAttrName(MIBUtil.getInstance(nameSpace).getName(oid) + ext);
        }else{
        	attr.setAttrName(MIBUtil.getInstance().getName(oid) + ext);
        }
        
        attr.setAttrType(attrType);
        attr.setValue(value);
        log.debug("OID[" + oid + "] NAME[" + attr.getAttrName() + "] VALUE[" + attr.getValue() + "]");

        return attr;
    }

}
