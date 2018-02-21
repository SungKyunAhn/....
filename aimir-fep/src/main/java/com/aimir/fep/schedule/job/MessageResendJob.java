package com.aimir.fep.schedule.job;

import javax.jms.TextMessage;

import com.aimir.dao.system.FireAlarmMessageLogDao;
import com.aimir.fep.protocol.fmp.log.AlarmLogger;
import com.aimir.model.system.FireAlarmMessageLog;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageResendJob {
    private static Log log = LogFactory.getLog(MessageResendJob.class);
    private AlarmLogger alarmLogger;
    private FireAlarmMessageLogDao messageLogDao;

    public AlarmLogger getAlarmLogger() {
        return alarmLogger;
    }

    public void setAlarmLogger(AlarmLogger alarmLogger) {
        this.alarmLogger = alarmLogger;
    }

    public FireAlarmMessageLogDao getMessageLogDao() {
        return messageLogDao;
    }

    public void setMessageLogDao(FireAlarmMessageLogDao messageLogDao) {
        this.messageLogDao = messageLogDao;
    }

    public void execute() {
        try {
            FireAlarmMessageLog[] logDatas = messageLogDao.listNotSended();
            for (FireAlarmMessageLog logData : logDatas) {
                log.info(logData.toString());
                
                TextMessage msg = new ActiveMQTextMessage();
                msg.setJMSCorrelationID(logData.getCorrelationId());
                
                StringBuffer buf = new StringBuffer();
                buf.append(AlarmLogger.MSGPROP.Message.getName() + "=" + logData.getMessage() + ",");
                buf.append(AlarmLogger.MSGPROP.Source.getName() + "=" + logData.getSource() + ",");
                buf.append(AlarmLogger.MSGPROP.Target.getName() + "=" + logData.getTarget() + ",");
                buf.append(AlarmLogger.MSGPROP.Timestamp.getName() + "=" + logData.getTimestamp() + ",");
                buf.append(AlarmLogger.MSGPROP.Temperature.getName() + "=" + logData.getTemperature() + ",");
                buf.append(AlarmLogger.MSGPROP.BatteryLevel.getName() + "=" + logData.getBatteryLevel() + ",");
                buf.append(AlarmLogger.MSGPROP.AlarmType.getName() + "=" + logData.getAlarmType() + ","); // or High Temperature
                buf.append(AlarmLogger.MSGPROP.EventType.getName() + "=" + logData.getEventType() + ",");
                buf.append(AlarmLogger.MSGPROP.UnitType.getName() + "=" + logData.getUnitType() + ",");
                buf.append(AlarmLogger.MSGPROP.Status.getName() + "=" + logData.getStatus() + ",");
                buf.append(AlarmLogger.MSGPROP.Result.getName() + "=" + logData.getResult() + ",");
                buf.append(AlarmLogger.MSGPROP.Reason.getName() + "=" + logData.getReason());
                
                msg.setIntProperty("content-length", buf.length());
                msg.setText(buf.toString());
                
                if (logData.isAlarm())
                    alarmLogger.sendAlarm(msg, logData.getId(), true);
                else
                    alarmLogger.sendCallback(msg, logData.getId(), true);
            }
        }
        catch (Exception e) {
            log.error(e);
        }
    }
}
