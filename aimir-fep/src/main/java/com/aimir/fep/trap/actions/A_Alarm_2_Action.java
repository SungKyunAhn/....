package com.aimir.fep.trap.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.dao.device.ZMUDao;
import com.aimir.fep.protocol.fmp.parser.alarm.Menix;
import com.aimir.fep.trap.common.A_Action;
import com.aimir.notification.Alarm;
import com.aimir.notification.FMPTrap;

/**
 * Alarm ID : Alarm Processing Class (alarm source type 2 : Menux)
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
public class A_Alarm_2_Action implements A_Action
{
    private Log log = LogFactory.getLog(A_Alarm_2_Action.class);
    
    @Autowired
    ZMUDao zmuDao;

    /**
     * execute alarm action
     *
     * @param trap - FMP Trap(MCU Alarm)
     * @param alarm - Alarm Data
     */
    public void execute(FMPTrap trap, Alarm alarm) throws Exception
    {
        log.debug("A_Alarm_2_Action : AlarmCode[" + trap.getCode()
                +"] Sensor["+trap.getSourceId()+
                "] SourceType["+trap.getSourceType()+"]");

        Object au = alarm.getAlarmMO();
        String sensorId = trap.getSourceId();
        log.debug("A_Alarm_2_Action : sensorId[" + sensorId + "]");

        if(au != null)
        {
            log.debug("Menix Alarm Started");
            int status = 0;
            try { status = Integer.parseInt(
                    alarm.getAlarmAttrValue("alarmSensorStatus"));
            }catch(Exception ex) {}

            String auInstanceName = alarm.getSystemKey();

            int type = Integer.parseInt(
                    alarm.getAlarmAttrValue("alarmSensorType"));
            int idx = Integer.parseInt(
                    alarm.getAlarmAttrValue("alarmSensorIndex"));
        }
/*
            boolean isFirst = false;
            MOINSTANCE hss = IUtil.getHomeSecuritySensor(
                    auInstanceName, type, idx);
            ZMU zmu = zmuDao.get(id)
            if (zmu != null)
            {
                if(hss.getPropertyValueString ("lastAlarmTime")
                        .length() == 0 )
                    isFirst = true;
                hss.setPropertyValue("lastAlarmTime", 
                        );
                if(status == 1)
                {
                    hss.setPropertyValue("lowBattFlag", 
                        "true");
                } else if(status == 0)
                {
                    hss.setPropertyValue("lowBattFlag", 
                        "false");
                }

                zmu.setLastLinkTime(trap.getTimeStamp());
                zmu.setL
                
                zmuDao.update(zmu);
            }
            else
            {
                log.error("no home security sensor exist "
                        +" [auInstanceName="+ auInstanceName+"]");
            }

            // Ignore Alarm
            if(status == 3 || isFirst)
            {
                log.debug("Alarm Sensor Status["+status 
                        + "] isFirst Alarm["+isFirst+"]");
                return;
            }
        
            // Invoke Alert
            String emer = 
                IUtil.getPropertyValue(auInstanceName,"emergency");
            if(emer.equals("2") 
                    || type == Menix.TYPE_REMOCON_DISARMS 
                    || type == Menix.TYPE_REMOCON_ARMS
                    || type == Menix.TYPE_FIRE) 
            {
                Alert alert = FaultUtil.makeAlert(alarm);
                FaultUtil.createNotification(alert);
            } else
            {
                log.debug("Alarm System["+auInstanceName
                        +"] DisArms");
            }

            if(status == 1)  // Case Low Battery, Send Event
            { 
                try 
                {
                    MOINSTANCE inst = IUtil.getMIOLocal().GetInstance(
                        alarm.getSystemKey(),null);
                    String alarmSensorIndex = 
                        alarm.getAlarmAttrValue("alarmSensorIndex");
                    String alarmSensorTypeString = 
                        getSensorTypeString(alarm.getAlarmAttrValue(
                                    "alarmSensorType"));
                    EventAttr[] attrs = new EventAttr[]
                    {
                        EventUtil.makeEventAttr(
                                "alarmSensorTypeString",
                                "java.lang.String",
                                alarmSensorTypeString),
                        EventUtil.makeEventAttr(
                                "alarmSensorId",
                                "java.lang.String",
                                alarm.getAlarmAttrValue(
                                    "alarmSensorId")),
                        EventUtil.makeEventAttr(
                                "alarmSensorIndex",
                                "java.lang.Integer",alarmSensorIndex)
                    };
                    Event event = EventUtil.makeEvent("Low Battery",
                            "HomeSecuritySensor",inst,attrs);
                    EventUtil.sendNotification(event);
                    EventLogger eventLogger = new EventLogger();
                    eventLogger.save(event);
                }catch(Exception ex) 
                {
                    log.error("send HomeSecuritySensor Low Battery "
                            + " Event failed : ",ex);
                }
            }

            // Set Alarm Unit Arm State
            if (type == Menix.TYPE_REMOCON_DISARMS 
                    || type == Menix.TYPE_REMOCON_ARMS) 
            { 
                IUtil.setPropertyValue(auInstanceName,"emergency",
                        Integer.toString(type));
            }
        } 
        else
        {
            log.debug("Menix Alarm Action failed : Unknown AU");
        }
        */
    }

    private String getSensorTypeString(String type)
    {
        int sensorType = 0;

        try { sensorType = Integer.parseInt(type);
        }catch(Exception ex) { return "Unknown "; }

        if (sensorType == Menix.TYPE_REMOCON_EMERGENCY) { 
            return " Called Remocon Emergency"; 
        } else if (sensorType == Menix.TYPE_REMOCON_DISARMS) { 
            return " Called Remocon Disarms"; 
        } else if (sensorType == Menix.TYPE_REMOCON_ARMS) { 
            return " Called Remocon Arms"; 
        } else if (sensorType == Menix.TYPE_WINDOW) { 
            return " Opened Window"; 
        } else if (sensorType == Menix.TYPE_EMERGENCY_SWITCH) { 
            return " Pushed Emergency Switch"; 
        } else if (sensorType == Menix.TYPE_DOOR) { 
            return " Opened Door"; 
        } else if (sensorType == Menix.TYPE_TRESPASS) { 
            return " Motion Detection"; 
        } else if (sensorType == Menix.TYPE_TEMPERATURE) { 
            return " Temperature"; 
        } else if (sensorType == Menix.TYPE_GAS) { 
            return " Gas Dectection"; 
        } else if (sensorType == Menix.TYPE_FIRE) { 
            return " Smoke Dectection"; 
        } 

        return "Unknown";
    }
}
