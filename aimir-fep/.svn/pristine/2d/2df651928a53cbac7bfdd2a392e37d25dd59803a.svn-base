package com.aimir.fep.protocol.fmp.processor;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.iot.domain.resources.Resource;

/**
 * Abstract Processor
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public abstract class Processor implements MessageListener
{
    // protected static ApplicationContext ctx = null;
    protected Log log = LogFactory.getLog(this.getClass().getName());
    protected String name = null;
    protected String serviceType = null;

    /*
    static {
        if (ctx == null) {
            ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring-listener.xml"});
        }
    }
    */

    /**
     * processing Service Data
     *
     * @param obj <code>Object</code> Object
     */
    public abstract int processing(Object obj) throws Exception;

    /**
     * restore backup data
     */
    public abstract void restore() throws Exception;


    public void onMessage(Message msg) {
        if (msg instanceof ObjectMessage) {
            try {
                processing(((ObjectMessage) msg).getObject());
            }
            catch (Exception e) {
                log.error(e,e);
            }
        }
        else if(msg instanceof MapMessage){
            try {
                processing(((MapMessage) msg));
            }
            catch (Exception e) {
                log.error(e,e);
            }        	
        }else if(msg instanceof Resource) {
        	try {
                processing(((Resource) msg));
            }
            catch (Exception e) {
                log.error(e,e);
            }
        }
        else {
            log.warn("Message is not object, check it!!");
        }
    }
}
