package com.aimir.fep.command;

import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aimir.fep.protocol.fmp.log.AlarmLogger;


public class AlarmCommandTest {
    ApplicationContext ctx = 
        new ClassPathXmlApplicationContext("/config/spring-test.xml");
    AlarmCommand command = (AlarmCommand)ctx.getBean("alarmCommand");
    
    @Test
    public void test_AliveCheck() {
        try {
            TextMessage msg = new ActiveMQTextMessage();
            msg.setText(AlarmLogger.MSGPROP.Message.getName() + "=" +
                    AlarmLogger.MESSAGE.AliveCheck.getName());
            
            command.sendAlarm(msg);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void test_ActiveAlarm() {
        try {
            TextMessage msg = new ActiveMQTextMessage();
            StringBuffer buf = new StringBuffer();
            
            buf.append(AlarmLogger.MSGPROP.Message.getName() + "=" +
                    AlarmLogger.MESSAGE.ActivateAlarm.getName() + ",");
            buf.append(AlarmLogger.MSGPROP.Target.getName() + "=" + "000D6F00002FDE36");
            
            msg.setText(buf.toString());
            command.sendAlarm(msg);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void test_DeactiveAlarm() {
        try {
            TextMessage msg = new ActiveMQTextMessage();
            StringBuffer buf = new StringBuffer();
            buf.append(AlarmLogger.MSGPROP.Message.getName() + "=" +
                    AlarmLogger.MESSAGE.DeactivateAlarm.getName() + ",");
            buf.append(AlarmLogger.MSGPROP.Target.getName() + "=" + "000D6F00002FDE36");
            
            msg.setText(buf.toString());
            command.sendAlarm(msg);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
