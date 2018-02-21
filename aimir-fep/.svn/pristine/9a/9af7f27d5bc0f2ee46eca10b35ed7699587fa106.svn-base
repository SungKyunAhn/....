package com.aimir.fep.trap.common;

/*
import nuri.aimir.moa.protocol.fmp.util.DataUtil;
import nuri.aimir.service.common.util.IUtil;
import nuri.nas.framework.notification.Alarm;
import nuri.nas.framework.notification.AlarmAttr;
import nuri.nas.framework.notification.FMPTrap;
import nuri.nas.framework.notification.VarBinds;
import nuri.nas.framework.mi.common.schema.MOINSTANCE;
import nuri.nas.framework.properties.NASProperty;
*/

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.dao.device.EventAlertDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.EventAlert;
import com.aimir.model.device.MCU;
import com.aimir.notification.Alarm;
import com.aimir.notification.AlarmAttr;
import com.aimir.notification.FMPTrap;
import com.aimir.notification.VarBinds;
import com.aimir.util.TimeUtil;

/**
 * make Alarm Data
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
public class AlarmMaker
{
    private static Log log = LogFactory.getLog(AlarmMaker.class);
    
    private static EventAlertDao eventAlertDao;
    private static MCUDao mcuDao;
    
    @Autowired
    public void setMCUDao(MCUDao _mcuDao) {
    	mcuDao = _mcuDao;
    }
    
    @Autowired
    public void setEventAlertDao(EventAlertDao _eventAlertDao) {
    	eventAlertDao = _eventAlertDao;
    }

    /**
     * make Alarm data
     * @param trap - FMP Trap(MCU Alarm)
     * @param rule - Alarm Rule
     * @return Alarm
     */
    @SuppressWarnings("unchecked")
	public static Alarm getAlarm(FMPTrap trap) throws Exception
    {
        Alarm alarm = new Alarm();
        String srcType = trap.getSourceType();
        String srcId = trap.getSourceId();
        String timeStamp = trap.getTimeStamp();
        
        MCU mcu = mcuDao.get(srcId);

        if(mcu != null)
        {
            alarm.setInstanceKey(mcu.getSysID());
            alarm.setAuId(mcu.getSysID());
            alarm.setSystemKey(mcu.getSysID());
            alarm.setSystemName(mcu.getSysName());
            alarm.setAlarmMO(mcu);
        }
        alarm.setSourceType(srcType);
        
        EventAlert eventAlert = eventAlertDao.findByCondition("oid", trap.getCode());
        if(eventAlert != null){
            String msg = DataUtil.buildFMPTrapMessage(null, trap,eventAlert.getMsgPattern());
            alarm.setMessage(msg);
        }

        alarm.setStatus(new Integer(0));
        alarm.setTimes(new Integer(1));
        alarm.setTime(
                new Long(TimeUtil.getLongTime(timeStamp)));
        alarm.setLastTime(new Long(System.currentTimeMillis()));
        
        log.debug("alarm : " + alarm.toString());
        VarBinds vb = trap.getVarBinds();
        Iterator iter = vb.keySet().iterator();
        AlarmAttr attr = null;
        String attrName = null;
        String variable = null;
        while(iter.hasNext())
        {
            attr = new AlarmAttr();
            attrName = (String)iter.next();
            variable = (String)vb.get(attrName);
            attr.setAttrName(attrName);
            attr.setValue(variable);
            alarm.append(attr);
        }
        return alarm;
    }
}
