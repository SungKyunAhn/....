package com.aimir.fep.protocol.fmp.client;

import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import net.sf.json.util.JSONStringer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.aimir.fep.modem.ModemCommandData;
import com.aimir.fep.protocol.fmp.common.GPRSTarget;
import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.HEX;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.service.EventData;
import com.aimir.fep.protocol.fmp.frame.service.entry.gmtEntry;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.MIBUtil;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml"} ) 
public class EV_203_10_0_Test {
    private static Log log = LogFactory.getLog(EV_203_10_0_Test.class);
    
    @Autowired
    JmsTemplate jmsTemplate;
    
    @Autowired
    Topic testTopic;
    
    @Autowired
    Queue testQueue;

    @Ignore
    public void test_doActiveButton() {
        try {
            
            log.info("targetIp="+System.getProperty("targetIp") + ", port=" + System.getProperty("targetPort"));
            MIBUtil mibUtil = MIBUtil.getInstance();
            EventData event = new EventData();
            event.setCode(mibUtil.getOid("eventSensorAlarm"));
            event.setSrcType(ServiceDataConstants.E_SRCTYPE_ZRU);
            event.setSrcId(new HEX("000D6F0000234633"));
            event.setTimeStamp(new TIMESTAMP(DateTimeUtil.getDateString(new Date())));
            SMIValue smiValue = null;
            smiValue = DataUtil.getSMIValueByObject("sensorID", "000D6F0000234633"); 
            event.append(smiValue);
            gmtEntry gmt = new gmtEntry();
            gmt.setGmtYear(new WORD(Integer.parseInt(event.getTimeStamp().toString().substring(0, 4))));
            gmt.setGmtMon(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(4, 6))));
            gmt.setGmtDay(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(6, 8))));
            gmt.setGmtHour(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(8, 10))));
            gmt.setGmtMin(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(10, 12))));
            gmt.setGmtSec(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(12,14))));
            
            smiValue = DataUtil.getSMIValueByObject("gmtEntry", gmt);
            event.append(smiValue);
            smiValue = DataUtil.getSMIValue(new BYTE(ModemCommandData.Event.SmokeDetectorTest.getId()));
            event.append(smiValue);
            byte[] eventStatus = ModemCommandData.makeEventStatus(ModemCommandData.Event.SmokeDetectorTest.getId(),
                    Boolean.parseBoolean(System.getProperty("status")), Double.parseDouble(System.getProperty("temperature")));
            DataUtil.convertEndian(eventStatus);
            smiValue = DataUtil.getSMIValueByObject("uintEntry", ""+DataUtil.getIntTo4Byte(eventStatus));
            event.append(smiValue);
            
            log.info("event : "+ event);
            GPRSTarget target = new GPRSTarget(System.getProperty("targetIp"),Integer.parseInt(System.getProperty("targetPort")));
            target.setTargetId("1001");
            Client client = ClientFactory.getClient(target);
            client.sendEvent(event);
            client.close();
        }
        catch (Exception e) {
            log.error(e, e);
        }
    }
    
    
    @Ignore
    public void test_doFireAlarmSmokeDetected() {
        try {
            MIBUtil mibUtil = MIBUtil.getInstance();
            EventData event = new EventData();
            event.setCode(mibUtil.getOid("eventSensorAlarm"));
            event.setSrcType(ServiceDataConstants.E_SRCTYPE_ZRU);
            event.setSrcId(new HEX("000D6F0000234633"));
            // event.setSrcId(new HEX("3000"));
            event.setTimeStamp(new TIMESTAMP(DateTimeUtil.getDateString(new Date())));
            SMIValue smiValue = null;
            smiValue = DataUtil.getSMIValueByObject("sensorID", "000D6F0000234633"); 
            event.append(smiValue);
            gmtEntry gmt = new gmtEntry();
            gmt.setGmtYear(new WORD(Integer.parseInt(event.getTimeStamp().toString().substring(0, 4))));
            gmt.setGmtMon(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(4, 6))));
            gmt.setGmtDay(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(6, 8))));
            gmt.setGmtHour(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(8, 10))));
            gmt.setGmtMin(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(10, 12))));
            gmt.setGmtSec(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(12,14))));
            
            smiValue = DataUtil.getSMIValueByObject("gmtEntry", gmt);
            event.append(smiValue);
            smiValue = DataUtil.getSMIValue(new BYTE(ModemCommandData.Event.FireAlarmSmokeDetected.getId()));
            event.append(smiValue);
            byte[] eventStatus = ModemCommandData.makeEventStatus(ModemCommandData.Event.FireAlarmSmokeDetected.getId(), 
                    Boolean.parseBoolean(System.getProperty("status")), Double.parseDouble(System.getProperty("temperature")));
            DataUtil.convertEndian(eventStatus);
            smiValue = DataUtil.getSMIValueByObject("uintEntry", ""+DataUtil.getIntTo4Byte(eventStatus));
            event.append(smiValue);
            
            log.info("event : "+ event);
            GPRSTarget target = new GPRSTarget(System.getProperty("targetIp"),Integer.parseInt(System.getProperty("targetPort")));
            target.setTargetId("1022100004");
            Client client = ClientFactory.getClient(target);
            client.sendEvent(event);
            client.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void test_doPowerFail() {
        // DataUtil.setApplicationContext(this.applicationContext);
        int loop = 1; // Integer.parseInt(System.getProperty("loop"));
        try {
            for (int i = 0; i < loop; i++) {
                MIBUtil mibUtil = MIBUtil.getInstance();
                EventData event = new EventData();
                event.setCode(mibUtil.getOid("eventSensorAlarm"));
                event.setSrcType(ServiceDataConstants.E_SRCTYPE_ZRU);
                event.setSrcId(new HEX("000D6F000036D081"));
                // event.setSrcId(new HEX("3000"));
                event.setTimeStamp(new TIMESTAMP(DateTimeUtil.getDateString(new Date())));
                SMIValue smiValue = null;
                smiValue = DataUtil.getSMIValueByObject("sensorID", "000D6F000036D081"); 
                event.append(smiValue);
                gmtEntry gmt = new gmtEntry();
                gmt.setGmtYear(new WORD(Integer.parseInt(event.getTimeStamp().toString().substring(0, 4))));
                gmt.setGmtMon(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(4, 6))));
                gmt.setGmtDay(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(6, 8))));
                gmt.setGmtHour(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(8, 10))));
                gmt.setGmtMin(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(10, 12))));
                gmt.setGmtSec(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(12,14))));
                
                byte alarmStatus = 0x00; // 0 : Power Alarm,  3: Line Missing
                byte[] eventStatus = new byte[] {0x00, 0x00, 0x00, 0x02}; // 00 00 00 01 Line Down/Restore 01 00 00 00 Power Down/Restore
                
                smiValue = DataUtil.getSMIValueByObject("gmtEntry", gmt);
                event.append(smiValue);
                smiValue = DataUtil.getSMIValue(new BYTE(alarmStatus));
                event.append(smiValue);
                DataUtil.convertEndian(eventStatus);
                smiValue = DataUtil.getSMIValueByObject("uintEntry", ""+DataUtil.getIntTo4Byte(eventStatus));
                event.append(smiValue);
                
                log.info("event : "+ event);
                GPRSTarget target = new GPRSTarget(System.getProperty("targetIp"),Integer.parseInt(System.getProperty("targetPort")));
                target.setTargetId("700");
                Client client = ClientFactory.getClient(target);
                client.sendEvent(event);
                client.close(true);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Ignore
    public void test_doLineMissing() {
        int loop = Integer.parseInt(System.getProperty("loop"));
        try {
            for (int i = 0; i < loop; i++) {
                MIBUtil mibUtil = MIBUtil.getInstance();
                EventData event = new EventData();
                event.setCode(mibUtil.getOid("eventSensorAlarm"));
                event.setSrcType(ServiceDataConstants.E_SRCTYPE_ZRU);
                event.setSrcId(new HEX("000D6F000030EF3B"));
                // event.setSrcId(new HEX("3000"));
                event.setTimeStamp(new TIMESTAMP(DateTimeUtil.getDateString(new Date())));
                SMIValue smiValue = null;
                smiValue = DataUtil.getSMIValueByObject("sensorID", "000D6F000030EF3B"); 
                event.append(smiValue);
                gmtEntry gmt = new gmtEntry();
                gmt.setGmtYear(new WORD(Integer.parseInt(event.getTimeStamp().toString().substring(0, 4))));
                gmt.setGmtMon(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(4, 6))));
                gmt.setGmtDay(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(6, 8))));
                gmt.setGmtHour(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(8, 10))));
                gmt.setGmtMin(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(10, 12))));
                gmt.setGmtSec(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(12,14))));
                
                byte alarmStatus = 0x03; // 0 : Power Alarm,  3: Line Missing
                byte[] eventStatus = new byte[] {0x00, 0x00, 0x70, 0x01}; // 00 00 00 01 Line Down/Restore 01 00 00 00 Power Down/Restore
                
                smiValue = DataUtil.getSMIValueByObject("gmtEntry", gmt);
                event.append(smiValue);
                smiValue = DataUtil.getSMIValue(new BYTE(alarmStatus));
                event.append(smiValue);
                DataUtil.convertEndian(eventStatus);
                smiValue = DataUtil.getSMIValueByObject("uintEntry", ""+DataUtil.getIntTo4Byte(eventStatus));
                event.append(smiValue);
                
                log.info("event : "+ event);
                GPRSTarget target = new GPRSTarget(System.getProperty("targetIp"),Integer.parseInt(System.getProperty("targetPort")));
                target.setTargetId("11010");
                Client client = ClientFactory.getClient(target);
                client.sendEvent(event);
                client.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Ignore
    public void test_doPowerRestore() {
        int loop = Integer.parseInt(System.getProperty("loop"));
        try {
            for (int i = 0; i < loop; i++) {
                MIBUtil mibUtil = MIBUtil.getInstance();
                EventData event = new EventData();
                event.setCode(mibUtil.getOid("eventSensorAlarm"));
                event.setSrcType(ServiceDataConstants.E_SRCTYPE_ZRU);
                event.setSrcId(new HEX("000D6F000030EF3B"));
                // event.setSrcId(new HEX("3000"));
                event.setTimeStamp(new TIMESTAMP(DateTimeUtil.getDateString(new Date())));
                SMIValue smiValue = null;
                smiValue = DataUtil.getSMIValueByObject("sensorID", "000D6F000030EF3B"); 
                event.append(smiValue);
                gmtEntry gmt = new gmtEntry();
                gmt.setGmtYear(new WORD(Integer.parseInt(event.getTimeStamp().toString().substring(0, 4))));
                gmt.setGmtMon(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(4, 6))));
                gmt.setGmtDay(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(6, 8))));
                gmt.setGmtHour(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(8, 10))));
                gmt.setGmtMin(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(10, 12))));
                gmt.setGmtSec(new BYTE(Integer.parseInt(event.getTimeStamp().toString().substring(12,14))));
                
                smiValue = DataUtil.getSMIValueByObject("gmtEntry", gmt);
                event.append(smiValue);
                smiValue = DataUtil.getSMIValue(new BYTE(0));
                event.append(smiValue);
                byte[] eventStatus = new byte[] {0x02, 0x00, 0x00, 0x02};
                DataUtil.convertEndian(eventStatus);
                smiValue = DataUtil.getSMIValueByObject("uintEntry", ""+DataUtil.getIntTo4Byte(eventStatus));
                event.append(smiValue);
                
                log.info("event : "+ event);
                GPRSTarget target = new GPRSTarget(System.getProperty("targetIp"),Integer.parseInt(System.getProperty("targetPort")));
                target.setTargetId("11010");
                Client client = ClientFactory.getClient(target);
                client.sendEvent(event);
                client.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
//    @Test
//    public void test_topicmessage() {
//        try {
//            for (int i = 0; i < 10; i++) {
//                log.info("loop #"+i);
//                jmsTemplate.send(testTopic, new MessageCreator() {
//                    public Message createMessage(Session session) throws JMSException {
//                        return session.createTextMessage("msg 1 #" + System.currentTimeMillis());
//                    }
//                });
//            }
//        }
//        catch (Exception e) {
//            log.error(e);
//        }
//    }

    @Ignore
    public void test_topicmessage() {
        try {
            for (int i = 0; i < 1; i++) {
                log.info("loop #" + i);
                jmsTemplate.send(testTopic, new MessageCreator() {
                    public Message createMessage(Session session) throws JMSException {
                        TextMessage message = session.createTextMessage();
                        JSONStringer js = new JSONStringer();

                        String openTime = null;
                        String closeTime = null;
                        String writeTime = null;

                        try {
                            openTime = TimeUtil.getCurrentTime();
                            closeTime = TimeUtil.getCurrentTime();
                            writeTime = TimeUtil.getCurrentTime();
                        } catch(ParseException pe) {
                            pe.printStackTrace();
                        }

                        String openTimef = openTime;
                        String closeTimef = closeTime;
                        String writeTimef = writeTime;

                        js.object().key("eventLogId").value(8)
                                   .key("activatorId").value("2010")
                                   .key("activatorType").value("MCU")
                                   .key("eventAlertName").value("Equipment Installation")
                                   .key("status").value("Open")
                                   .key("severity").value("Normal")
                                   .key("openTime").value(openTimef)
                                   .key("activatorIp").value("10.50.0.7")
                                   .key("eventOid").value("200.1.0,203.8.0,201.1.0,203.105.0")
                                   .key("eventMessage").value("Install ID=[6] Type=[MCU]")
                                   .key("closeTime").value(closeTimef)
                                   .key("duration").value(13)
                                   .key("writeTime").value(writeTimef)
                                   .key("supplierId").value(22)
                                   .key("location").value("SongDangRi")
                                   .endObject();

                        message.setText(js.toString());                        

                        return message;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
    }

    @Ignore
    public void test_queuemessage() {
        try {
            for (int i = 0; i < 1; i++) {
                log.info("loop #" + i);
                jmsTemplate.send(testQueue, new MessageCreator() {
                    public Message createMessage(Session session) throws JMSException {
                        TextMessage message = session.createTextMessage();
                        JSONStringer js = new JSONStringer();

                        String openTime = null;
                        String closeTime = null;
                        String writeTime = null;

                        try {
                            openTime = TimeUtil.getCurrentTime();
                            closeTime = TimeUtil.getCurrentTime();
                            writeTime = TimeUtil.getCurrentTime();
                        } catch(ParseException pe) {
                            pe.printStackTrace();
                        }

                        String openTimef = openTime;
                        String closeTimef = closeTime;
                        String writeTimef = writeTime;

                        js.object().key("eventLogId").value(8)
                                   .key("activatorId").value("2010")
                                   .key("activatorType").value("MCU")
                                   .key("eventAlertName").value("Equipment Installation")
                                   .key("status").value("Open")
                                   .key("severity").value("Normal")
                                   .key("openTime").value(openTimef)
                                   .key("activatorIp").value("10.50.0.7")
                                   .key("eventOid").value("200.1.0,203.8.0,201.1.0,203.105.0")
                                   .key("eventMessage").value("Install ID=[6] Type=[MCU]")
                                   .key("closeTime").value(closeTimef)
                                   .key("duration").value(13)
                                   .key("writeTime").value(writeTimef)
                                   .key("supplierId").value(22)
                                   .key("location").value("SongDangRi")
                                   .endObject();

                        message.setText(js.toString());
                        message.setIntProperty("supplierId", 22);

                        return message;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
    }
    
    @Ignore
    public void test_queuemapmessage() {
        try {
            for (int i = 0; i < 1; i++) {
                log.info("loop #" + i);
                jmsTemplate.send(testQueue, new MessageCreator() {
                    public Message createMessage(Session session) throws JMSException {
                        final MapMessage message = session.createMapMessage();
                        
                        String openTime = null;
                        String closeTime = null;
                        String writeTime = null;

                        try {
                            openTime = TimeUtil.getCurrentTime();
                            closeTime = TimeUtil.getCurrentTime();
                            writeTime = TimeUtil.getCurrentTime();
                        } catch(ParseException pe) {
                            pe.printStackTrace();
                        }

                        String openTimef = openTime;
                        String closeTimef = closeTime;
                        String writeTimef = writeTime;
                        
                        message.setLong("eventLogId", 8);
                        message.setString("activatorId", "2010");
                        message.setString("activatorType", "MCU");
                        message.setString("eventAlertName", "Equipement Installation");
                        message.setString("status", "Open");
                        message.setString("severity", "Normal");
                        message.setString("openTime", openTimef);
                        message.setString("activatorIp", "10.50.0.7");
                        message.setString("eventOid", "200.1.0,203.8.0,201.1.0,203.105.0");
                        message.setString("eventMessage", "Install ID=[6] Type=[MCU]");
                        message.setString("closeTime", closeTimef);
                        message.setInt("duration", 13);
                        message.setString("writeTime", writeTimef);
                        message.setInt("supplierId", 22);
                        message.setString("location", "SongDangRi");
                        message.setIntProperty("supplierId", 22);
                        
                        return message;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
    }
    
    @Ignore
    public void test_queuemessage_sample() {
        try {
            InputStream is = this.getClass().getResourceAsStream("/MeteringReading_sample.txt");
            
            for (int i = 0; i < 60000000; i++) {
                log.info("loop #"+i);
                jmsTemplate.send(testQueue, new MessageCreator() {
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage("msg #" + System.currentTimeMillis());
                    }
                });
            }
        }
        catch (Exception e) {
            log.error(e);
        }
    }
    
    @Ignore
    public void test_topicselector() throws Exception {
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:1399/jmxrmi");
        JMXConnector jmxc = JMXConnectorFactory.connect(url);
        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
        // ObjectName objectName = new ObjectName("bean:name=JmsIntegrator");
        // JmsIntegratorMBean integrator = JMX.newMBeanProxy(mbsc,  objectName, JmsIntegratorMBean.class, true);
        // integrator.addSelector("systemKey='NURI#EnergyMeter/14326076'");
    }
}
