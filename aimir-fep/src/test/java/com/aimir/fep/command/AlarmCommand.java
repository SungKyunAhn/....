package com.aimir.fep.command;

import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class AlarmCommand {
    private static Log log = LogFactory.getLog(AlarmCommand.class);
    
    private JmsTemplate activeJmsTemplate = null;
    private String activeCommandName = null;
    
    public JmsTemplate getActiveJmsTemplate() {
        return activeJmsTemplate;
    }

    public void setActiveJmsTemplate(JmsTemplate activeJmsTemplate) {
        this.activeJmsTemplate = activeJmsTemplate;
    }

    public String getActiveCommandName() {
        return activeCommandName;
    }

    public void setActiveCommandName(String activeCommandName) {
        this.activeCommandName = activeCommandName;
    }

    public void sendAlarm(final Message msg) {
        activeJmsTemplate.send(activeCommandName, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {

                if (msg instanceof MapMessage) {
                    MapMessage mapMsg = (MapMessage)msg;
                    log.info("JMSMessageID=" + mapMsg.getJMSMessageID());
                    String key = null;
                    for (Enumeration<?> i = mapMsg.getMapNames(); i.hasMoreElements(); ) {
                        key = (String)i.nextElement();
                        log.info(key + "=" + mapMsg.getString(key));
                    }
                }
                
                return msg;
            }
            
        });
    }
}
