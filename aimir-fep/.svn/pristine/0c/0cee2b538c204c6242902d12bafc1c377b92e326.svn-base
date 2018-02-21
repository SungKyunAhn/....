package com.aimir.fep.protocol.fmp.client;

import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsTemplate;

public class TestAlarmListener implements MessageListener {
    private static Log log = LogFactory.getLog(TestAlarmListener.class);
    
    private JmsTemplate activeJmsTemplate = null;
    private String activeAlarmName = null;
    
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

    public void init() throws JMSException {
        ConnectionFactory cf = activeJmsTemplate.getConnectionFactory();
        Connection conn = cf.createConnection();
        Session sess = conn.createSession(true, Session.AUTO_ACKNOWLEDGE);
        Queue queue = sess.createQueue(activeAlarmName);
        MessageConsumer consumer = sess.createConsumer(queue);
        consumer.setMessageListener(this);
        conn.setExceptionListener(new ExceptionListener() {

            public void onException(JMSException arg0) {
                // TODO Auto-generated method stub
                try {
                    init();
                } catch (JMSException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
        });
        conn.start();
    }
    
    public void onMessage(Message arg0) {
        // TODO Auto-generated method stub
        try {
            if (arg0 instanceof MapMessage) {
                MapMessage msg = (MapMessage)arg0;
                log.info("JMSMessageID=" + msg.getJMSMessageID());
                String key = null;
                for (Enumeration<?> i = msg.getMapNames(); i.hasMoreElements(); ) {
                    key = (String)i.nextElement();
                    log.info(key + "=" + msg.getString(key));
                }
            }
        }
        catch (JMSException e) {
            log.error(e);
        }
    }

}
