package com.aimir.fep.protocol.fmp.log;

import java.beans.IntrospectionException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.aimir.notification.FMPTrap;
import com.aimir.notification.Notification;
import com.aimir.notification.NotificationSupport;

/**
 * File AMU EventLogger
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 23. 오후 3:44:13$
 */
@Service
public class AMUEventLogger extends MessageLogger { 
	
    private Log log = LogFactory.getLog(AMUEventLogger.class);

    /**
     * Constructs 
     */ 
    private AMUEventLogger() throws IOException { 
        super();
    } 

    
	@Override
	public String writeObject(Serializable obj) {

        try 
        { 
            FMPTrap trap = (FMPTrap)obj;
            File f = null;
            f = new File(logDirName,"AMUEventLog-"
                    + trap.getMcuId()+"-"
                    + trap.getCode()+"-"
                    +System.currentTimeMillis()+".log");
            ObjectOutputStream os = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(f)));
            os.writeObject(trap);
            os.close();
        } catch (Exception e) {     
            log.error("********" + getClass().getName() 
                    + " write() Failed *********",e); 
        } 
        return null;
    }
    
    // send event
    Message msg = null;
    public synchronized void sendAmuEvent(final Notification data)
    {
    	try {
            log.debug("Queue name[" + queueName + "]");
            
            jmsTemplate.send(queueName, new MessageCreator(){
            	
            	public Message createMessage(Session session) throws JMSException {
            		try {
                        msg = NotificationSupport.createMessage(data,session);
                        return msg;
                    }catch (IntrospectionException e) {
                       
                        log.error(e);
                    }
                    return null;
                }
            });
            
            if (msg instanceof ObjectMessage) {
                Object obj = ((ObjectMessage)msg).getObject();
                log.info(obj.toString());
            }
            else log.warn("msg is not object message");
        }
        catch (Exception e) {
            log.error(e);
        }
    }


	@Override
	public void backupObject(Serializable obj) {
		
	}
}

