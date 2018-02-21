package com.aimir.fep.protocol.fmp.log;

import java.io.BufferedOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.FireAlarmMessageLogDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.modem.EventMessage;
import com.aimir.fep.modem.ModemCommandData;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.Modem;
import com.aimir.model.system.FireAlarmMessageLog;
import com.aimir.notification.FMPTrap;
import com.aimir.notification.Notification;
import com.aimir.util.DateTimeUtil;

/**
 * FileAlarmLogger.java
 *
 * Logger which logs to a file
 *
 * Created: 2010.05.10
 *
 * @author  J.S Park (elevas@nuritelecom.com)
 * @version 0.1
 */
@Service
public class AlarmLogger extends MessageLogger implements MessageListener {
    private JmsTemplate activeJmsTemplate = null;
    private String activeAlarmName = null;
    private String activeCallbackName = null;
    
    @Autowired
    private FireAlarmMessageLogDao messageLogDao = null;
    
    @Autowired
    private ModemDao modemDao = null;

    /**
     * Constructs a FileAlarmLogger object
     */
    private AlarmLogger() throws IOException {
        super();
    }

    public JmsTemplate getActiveJmsTemplate() {
        return activeJmsTemplate;
    }

    public void setActiveJmsTemplate(JmsTemplate activeJmsTemplate) {
        this.activeJmsTemplate = activeJmsTemplate;
    }

    public String getActiveAlarmName() {
        return activeAlarmName;
    }

    public void setActiveAlarmName(String activeAlarmName) {
        this.activeAlarmName = activeAlarmName;
    }

    public String getActiveCallbackName() {
        return activeCallbackName;
    }

    public void setActiveCallbackName(String activeCallbackName) {
        this.activeCallbackName = activeCallbackName;
    }

    public FireAlarmMessageLogDao getFireAlarmMessageLogDao() {
        return messageLogDao;
    }

    public void setMessageLogDao(FireAlarmMessageLogDao messageLogDao) {
        this.messageLogDao = messageLogDao;
    }

    public ModemDao getModemDao() {
        return modemDao;
    }

    public void setModemDao(ModemDao modemDao) {
        this.modemDao = modemDao;
    }

    @Override
    public String writeObject(Serializable obj) {

        try
        {
            if (obj instanceof Notification) {
                Notification noti = (Notification)obj;
                FMPTrap trap = (FMPTrap)noti;
                File f = null;
                f = new File(logDirName,"FMPAlarmLog-"
                        + trap.getMcuId()+"-"
                        + trap.getCode()+"-"
                        +System.currentTimeMillis()+".log");
                ObjectOutputStream os = new ObjectOutputStream(
                    new BufferedOutputStream(new FileOutputStream(f)));
                os.writeObject(trap);
                os.reset();
                os.close();
            }
        } catch (Exception e) {
            log.error("********" + getClass().getName()
                    + " write() Failed *********",e);
        }
        return null;
    }

    public void sendAlarm(final Message msg, Long logId, boolean retry) {
        long starttime = System.currentTimeMillis();
        boolean sended = false;
        try {
            activeJmsTemplate.send(activeAlarmName, new MessageCreator() {
                public Message createMessage(Session session) throws JMSException {
                    if(log.isDebugEnabled()){
                        if(msg instanceof TextMessage) {
                            log.debug("TextMessage="+((TextMessage)msg).getText());
                        }
                    }
                    return msg;
                }
            });
            sended = true;
        }
        catch (Exception e) {
            log.error(e);
        }
        log.debug("send alarm duration[" + (System.currentTimeMillis() - starttime) + "]");
        saveMessageLog(msg, logId, true, sended, retry);
    }

    public void sendCallback(final Message msg, Long logId, boolean retry) {
        long starttime = System.currentTimeMillis();
        boolean sended = false;
        try {
            activeJmsTemplate.send(activeCallbackName, new MessageCreator() {
                public Message createMessage(Session session) throws JMSException {
                    if(log.isDebugEnabled()){
                        if(msg instanceof TextMessage) {
                            log.debug("TextMessage="+((TextMessage)msg).getText());
                        }
                    }
                    return msg;
                }
            });
            sended = true;
        }
        catch (Exception e) {
            log.error(e);
        }
        log.debug("send callback duration[" + (System.currentTimeMillis() - starttime) + "]");
        saveMessageLog(msg, logId, false, sended, retry);
    }


    private Properties makeProperties(String str) throws IOException {
        StringTokenizer st = new StringTokenizer(str, ",");
        Properties prop = new Properties();
        while (st.hasMoreTokens()) {
            prop.load(new StringReader(st.nextToken()));
        }
        return prop;
    }

    private void saveMessageLog(Message msg, Long logId, boolean alarm,
            boolean sended, boolean retry) {
        try {
            TextMessage cbMsg = (TextMessage)msg;
            FireAlarmMessageLog bean = new FireAlarmMessageLog();
            bean.setId(logId);
            bean.setAlarm(alarm);

            Properties prop = makeProperties(cbMsg.getText());

            String value = cbMsg.getJMSMessageID();
            bean.setWriteDate(DateTimeUtil.getDateString(new Date()));
            bean.setMessageId(value);
            value = cbMsg.getJMSCorrelationID();
            bean.setCorrelationId(value == null? "":value);
            value = prop.getProperty(MSGPROP.Message.getName());
            bean.setMessage(value == null? "":value);
            value = prop.getProperty(MSGPROP.Source.getName());
            bean.setSource(value == null? "":value);
            value = prop.getProperty(MSGPROP.Target.getName());
            bean.setTarget(value == null? "":value);
            value = prop.getProperty(MSGPROP.Timestamp.getName());
            bean.setTimestamp(value == null? "":value);
            value = prop.getProperty(MSGPROP.Temperature.getName());
            bean.setTemperature(value == null? "":value);
            value = prop.getProperty(MSGPROP.BatteryLevel.getName());
            bean.setBatteryLevel(value == null? "":value);
            value = prop.getProperty(MSGPROP.AlarmType.getName());
            bean.setAlarmType(value == null? "":value);
            value = prop.getProperty(MSGPROP.EventType.getName());
            bean.setEventType(value == null? "":value);
            value = prop.getProperty(MSGPROP.UnitType.getName());
            bean.setUnitType(value == null? "":value);
            value = prop.getProperty(MSGPROP.Status.getName());
            bean.setStatus(value == null? "":value);
            value = prop.getProperty(MSGPROP.Result.getName());
            bean.setResult(value == null? "":value);
            value = prop.getProperty(MSGPROP.Reason.getName());
            bean.setReason(value == null? "":value);
            bean.setSended(sended);
            if (sended) {
                bean.setSendDate(DateTimeUtil.getDateString(new Date()));
            }

            log.info(bean.toString());
            if (retry && sended)
                messageLogDao.update(bean);
            else if (!retry)
                messageLogDao.add(bean);
        }
        catch (JMSException e) {
            log.error(e);
        }
        catch (IOException e) {
            log.error(e);
        }
    }

    public void onMessage(Message msg) {

        TextMessage cbMsg = null;
        try {
            if (msg instanceof TextMessage) {
                TextMessage textMsg = (TextMessage)msg;
                String text = textMsg.getText();
                log.info("message='"+text+"'");

                Properties prop = makeProperties(text);

                String command = prop.getProperty(MSGPROP.Message.getName());
                if (command.equalsIgnoreCase(MESSAGE.ActivateAlarm.getName())) {
                    cbMsg = (TextMessage)activateAlarm(textMsg.getJMSMessageID(), prop);
                }
                else if (command.equalsIgnoreCase(MESSAGE.DeactivateAlarm.getName())) {
                    cbMsg = (TextMessage)deactivateAlarm(textMsg.getJMSMessageID(), prop);
                }
                else if (command.equalsIgnoreCase(MESSAGE.RemoveAlarmUnit.getName())) {
                    cbMsg = (TextMessage)removeAlarmUnit(textMsg.getJMSMessageID(), prop);
                }
                else if (command.equalsIgnoreCase(MESSAGE.AliveCheck.getName())) {
                    cbMsg = (TextMessage)checkAlive(textMsg.getJMSMessageID(), prop);
                }
                else if (command.equalsIgnoreCase(MESSAGE.CheckAlarmUnitStatus.getName())) {
                    cbMsg = (TextMessage)checkAlarmUnitStatus(textMsg.getJMSMessageID(), prop);
                }
                else if (command.equalsIgnoreCase(MESSAGE.InstallAlarmUnit.getName())) {
                    cbMsg = (TextMessage)installAlarmUnit(textMsg.getJMSMessageID(), prop);
                }
                boolean updated = false;
                sendCallback(cbMsg, null, updated);
            }
            else
                log.warn("Message is not text message.");
        }
        catch (JMSException e) {
            log.error(e);
        }
        catch (IOException e) {
            log.error(e);
        }
    }

    private Message activateAlarm(String msgId, Properties prop) {
        TextMessage reply = new ActiveMQTextMessage();
        int step = 0;
        StringBuffer buf = new StringBuffer();
        try {
            reply.setJMSCorrelationID(msgId);
            buf.append(MSGPROP.Message.getName() + "=" + prop.getProperty(MSGPROP.Message.getName()) + ",");

            String target = prop.getProperty(MSGPROP.Target.getName());
            log.info("TARGET[" + target + "]");
            buf.append(MSGPROP.Source.getName() + "=" + target + ",");

            Modem modem = modemDao.get(target);
            String mcuId = modem.getMcu().getId().toString();
            if (mcuId == null || "".equals(mcuId))
                throw new Exception("MCU is not mapping");

            CommandGW gw = new CommandGW();
            gw.cmdCommandModem(mcuId, target,
                    ModemCommandData.CMD.StartSiren.getType(),
                    new byte[] {ModemCommandData.StartSiren.ON.getValue()});
            step++;

            byte[] result = gw.cmdCommandModem(mcuId, target,
                    ModemCommandData.CMD.StatusRequest.getType(), null);
            log.info(Hex.decode(result));
            EventMessage em = ModemCommandData.getEventMessage(result);

            buf.append(MSGPROP.Timestamp.getName() + "=" + em.getEventTime() + ",");
            buf.append(MSGPROP.Temperature.getName() + "=" + em.getTemperature() + ",");
            buf.append(MSGPROP.BatteryLevel.getName() + "=" + "" + ","); // not yet
            buf.append(MSGPROP.Status.getName() + "=" + getStatus(em.isEventStatus()).getName() + ",");
            buf.append(MSGPROP.Result.getName() + "=" + RESULT.Success.getName());
            step++;
        }
        catch (Exception e) {
            log.error(e);
            buf.append(MSGPROP.Result.getName() + "=" + RESULT.Fail.getName() + ",");
            if (step == 0)
                buf.append(MSGPROP.Reason.getName() + "=" + e.getMessage());
            else
                buf.append(MSGPROP.Reason.getName() + "=" +
                        "Start siren don't fail but can't get a status for " + e.getMessage());
        }

        try {
            reply.setIntProperty("content-length", buf.length());
            reply.setText(buf.toString());
        }
        catch (JMSException e) {
            log.error(e);
        }
        return reply;
    }

    private Message deactivateAlarm(String msgId, Properties prop) {
        TextMessage reply = new ActiveMQTextMessage();
        int step = 0;
        StringBuffer buf = new StringBuffer();
        try {
            reply.setJMSCorrelationID(msgId);
            buf.append(MSGPROP.Message.getName() + "=" + prop.getProperty(MSGPROP.Message.getName()) + ",");

            String target = prop.getProperty(MSGPROP.Target.getName());
            buf.append(MSGPROP.Source.getName() + "=" + target + ",");

            Modem modem = modemDao.get(target);
            String mcuId = modem.getMcu().getSysID();
            if (mcuId == null || "".equals(mcuId))
                throw new Exception("MCU is not mapping");

            CommandGW gw = new CommandGW();
            gw.cmdCommandModem(mcuId, target,
                    ModemCommandData.CMD.StartSiren.getType(),
                    new byte[] {ModemCommandData.StartSiren.OFF.getValue()});
            step++;

            byte[] result = gw.cmdCommandModem(mcuId, target,
                    ModemCommandData.CMD.StatusRequest.getType(), null);
            log.info(Hex.decode(result));
            EventMessage em = ModemCommandData.getEventMessage(result);

            buf.append(MSGPROP.Timestamp.getName() + "=" + em.getEventTime() + ",");
            buf.append(MSGPROP.Temperature.getName() + "=" + em.getTemperature() + ",");
            buf.append(MSGPROP.BatteryLevel.getName() + "=" + "" + ","); // not yet
            buf.append(MSGPROP.Status.getName() + "=" + getStatus(em.isEventStatus()).getName() + ",");
            buf.append(MSGPROP.Result.getName() + "=" + RESULT.Success.getName());
            step++;
        }
        catch (Exception e) {
            log.error(e);
            buf.append(MSGPROP.Result.getName() + "=" + RESULT.Fail.getName() + ",");
            if (step == 0)
                buf.append(MSGPROP.Reason.getName() + "=" + e.getMessage());
            else
                buf.append(MSGPROP.Reason.getName() + "=" +
                        "Start siren don't fail but can't get a status for " + e.getMessage());
        }

        try {
            reply.setIntProperty("content-length", buf.length());
            reply.setText(buf.toString());
        }
        catch (JMSException e) {
            log.error(e);
        }
        return reply;
    }

    private Message removeAlarmUnit(String msgId, Properties prop) {
        TextMessage reply = new ActiveMQTextMessage();
        StringBuffer buf = new StringBuffer();
        try {
            reply.setJMSCorrelationID(msgId);
            buf.append(MSGPROP.Message.getName() + "=" + prop.getProperty(MSGPROP.Message.getName()) + ",");

            //String target = prop.getProperty(MSGPROP.Target.getName());

            buf.append(MSGPROP.Result.getName() + "=" + RESULT.Success.getName());
        }
        catch (JMSException e) {
            buf.append(MSGPROP.Result.getName() + "=" + RESULT.Fail.getName() + ",");
            buf.append(MSGPROP.Reason.getName() + "=" + "Exception:"+e.getMessage());
        }

        try {
            reply.setIntProperty("content-length", buf.length());
            reply.setText(buf.toString());
        }
        catch (JMSException e) {
            log.error(e);
        }

        return reply;
    }

    private Message checkAlive(String msgId, Properties prop) {
        TextMessage reply = new ActiveMQTextMessage();
        StringBuffer buf = new StringBuffer();
        try {
            reply.setJMSCorrelationID(msgId);
            buf.append(MSGPROP.Message.getName() + "=" + prop.getProperty(MSGPROP.Message.getName()));
        }
        catch (JMSException e) {
            buf.append(MSGPROP.Result.getName() + "=" + RESULT.Fail.getName() + ",");
            buf.append(MSGPROP.Reason.getName() + "=" + "Exception:"+e.getMessage());
        }

        try {
            reply.setIntProperty("content-length", buf.length());
            reply.setText(buf.toString());
        }
        catch (JMSException e) {
            log.error(e);
        }
        return reply;
    }

	private Message checkAlarmUnitStatus(String msgId, Properties prop) {
        TextMessage reply = new ActiveMQTextMessage();
        StringBuffer buf = new StringBuffer();
        try {
            reply.setJMSCorrelationID(msgId);
            buf.append(MSGPROP.Message.getName() + "=" + prop.getProperty(MSGPROP.Message.getName()) + ",");

            String target = prop.getProperty(MSGPROP.Target.getName());
            buf.append(MSGPROP.Source.getName() + "=" + target + ",");

            Modem modem = modemDao.get(target);

            String mcuId = modem.getMcu().getId().toString();
            if (mcuId == null || "".equals(mcuId))
                throw new Exception("MCU is not mapping");

            CommandGW gw = new CommandGW();
            byte[] result = gw.cmdCommandModem(mcuId, target,
                    ModemCommandData.CMD.StatusRequest.getType(), null);
            EventMessage em = ModemCommandData.getEventMessage(result);

            buf.append(MSGPROP.Timestamp.getName() + "=" + em.getEventTime() + ",");
            buf.append(MSGPROP.Temperature.getName() + "=" + em.getTemperature() + ",");
            buf.append(MSGPROP.BatteryLevel.getName() + "=" + "" + ","); // not yet
            buf.append(MSGPROP.Status.getName() + "=" + getStatus(em.isEventStatus()).getName() + ",");
            buf.append(MSGPROP.Result.getName() + "=" + RESULT.Success.getName());
        }
        catch (Exception e) {
            buf.append(MSGPROP.Result.getName() + "=" + RESULT.Fail.getName() + ",");
            buf.append(MSGPROP.Reason.getName() + "=" + "Exception:"+e.getMessage());
        }

        try {
            reply.setIntProperty("content-length", buf.length());
            reply.setText(buf.toString());
        }
        catch (JMSException e) {
            log.error(e);
        }
        return reply;
    }

    private Message installAlarmUnit(String msgId, Properties prop) {
        TextMessage reply = new ActiveMQTextMessage();
        StringBuffer buf = new StringBuffer();

        try {
            reply.setJMSCorrelationID(msgId);
            buf.append(MSGPROP.Message.getName() + "=" + prop.getProperty(MSGPROP.Message.getName()) + ",");

            // install alarm unit (zmu)
            buf.append(MSGPROP.Result.getName() + "=" + RESULT.Success.getName());
        }
        catch (JMSException e) {
            buf.append(MSGPROP.Result.getName() + "=" + RESULT.Fail.getName() + ",");
            buf.append(MSGPROP.Reason.getName() + "=" + "Exception:"+e.getMessage());
        }

        try {
            reply.setIntProperty("content-length", buf.length());
            reply.setText(buf.toString());
        }
        catch (JMSException e) {
            log.error(e);
        }

        return reply;
    }

    public enum MSGPROP {
        Message("Message"),
        Source("Source"),
        Timestamp("Timestamp"),
        Temperature("Temperature"),
        BatteryLevel("Battery Level"),
        Status("Status"),
        Result("Result"),
        Target("Target"),
        Reason("Reason"),
        AlarmType("Alarm Type"),
        EventType("Event Type"),
        UnitType("Unit Type");

        private String name;

        MSGPROP(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum RESULT {
        Success("Success"),
        Fail("Fail");

        private String name;

        RESULT(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum MESSAGE {
        Alarm("Alarm"),
        Event("Event"),
        ActivateAlarm("Activate Alarm"),
        SystemError("System Error"),
        InstallAlarmUnit("Install Alarm Unit"),
        RemoveAlarmUnit("Remove Alarm Unit"),
        DeactivateAlarm("Deactivate Alarm"),
        AliveCheck("Alive Check"),
        CheckAlarmUnitStatus("Check Alarm Unit Status");

        private String name;

        MESSAGE(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum ALARMTYPE {
        Smoke("Smoke"),
        Silence("Silence"),
        HighTemperature("High Temperature");

        private String name;

        ALARMTYPE(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static STATUS getStatus(boolean isStatus) {
        for (STATUS status : STATUS.values()) {
            if (status.isStatus() == isStatus)
                return status;
        }
        return null;
    }

    public enum STATUS {
        ON(true, "On"),
        OFF(false, "Off");

        private boolean status;
        private String name;

        STATUS(boolean status, String name) {
            this.status= status;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public boolean isStatus() {
            return status;
        }
    }

    public enum EVENTTYPE {
        BatteryLow("Battery Low"),
        Tampered("Tampered"),
        CleanMe("Clean Me"),
        ButtonPushed("Button Pushed"),
        NoConnection("No Connection"),
        ConnectionFailure("Connection Failure"),
        ConnectionRestored("Connection Restored"),
        HeartBeat("Heart Beat"),
        CaseOpen("Case Open");

        private String name;

        EVENTTYPE(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum UNITTYPE {
        MCU("MCU"),
        Server("Server"),
        SmokeDetector("Smoke Detector"),
        AlarmUnit("Alarm Unit");

        private String name;

        UNITTYPE(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    public void backupObject(Serializable obj) {

        try 
        { 
            if (obj instanceof Notification) {
                Notification noti = (Notification)obj;
                FMPTrap trap = (FMPTrap)noti;
                File f = null;
                f = new File(getBackupDir(),"FMPAlarmLog-"
                        + trap.getMcuId()+"-"
                        + trap.getCode()+"-"
                        +System.currentTimeMillis()+".log");
                ObjectOutputStream os = new ObjectOutputStream(
                    new BufferedOutputStream(new FileOutputStream(f)));
                os.writeObject(trap);
                os.reset();
                os.close();
            }
        } catch (Exception e) {     
            log.error("********" + getClass().getName() 
                    + " backup() Failed *********",e); 
        } 
    }
}

