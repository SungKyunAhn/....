package com.aimir.fep.tool;

import java.util.Hashtable;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ActiveMQPurge {
    private static Log log = LogFactory.getLog(ActiveMQPurge.class);

    public static void main(String[] args) {
    	String activemq = "localhost";
    	if(args.length > 0 && !"".equals(args[0]) && args[0] != null) {
    		log.info("args["+args[0]+"]");
    		activemq = args[0];
    	}
    	String mqList[] = null;
    	if(activemq != null && !"".equals(activemq)) {
    		mqList = activemq.split(",");
    	} else {
    		mqList = new String[0];
    	}
    	
    	String amqDomain = "org.apache.activemq";
    	String runningQueue = null;
    	
    	String[] queueNames = {"AiMiR.Event","ActiveMQ.DLQ"};
    	String mqIp = null;
    	
    	for (int i = 0; i < mqList.length; i++) {
    		try {
    			mqIp = mqList[i];
    			activemq = "service:jmx:rmi:///jndi/rmi://"+ mqIp + ":1616/jmxrmi";
    			log.info("activeMQ[" + activemq + "] connect start");
	        	JMXServiceURL url = new JMXServiceURL(activemq);
	        	JMXConnector jmxc = JMXConnectorFactory.connect(url);
	        	MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
	        	log.info("activeMQ[" + activemq + "] connect success");
	        	Hashtable<String, String> params = new Hashtable<String, String>(); 
		        params.put("Type", "Queue"); 
		        params.put("BrokerName", "localhost"); 
		        
		        for (int j = 0; j < queueNames.length; j++) {
		        	try{
			        	runningQueue = queueNames[j];
			        	params.put("Destination", runningQueue);
				    	ObjectName queueObjectName = ObjectName.getInstance(amqDomain, params);
				    	
				    	QueueViewMBean queueMbean = (QueueViewMBean)
				 	           MBeanServerInvocationHandler.newProxyInstance(mbsc, queueObjectName, QueueViewMBean.class, true);
				    	
				    	queueMbean.purge();
				    	params.remove("Destination");
				    	log.info("IP["+ mqIp +"] QueueName["+runningQueue+"] purge is complete.");
		        	} catch (Exception e) {
	        		log.warn(e,e);
	        		log.info("IP["+ mqIp +"] QueueName["+runningQueue+"] cannot purge.");
	    		}
		        }
        	} catch (Exception e) {
        		log.warn(e,e);
        		log.info("IP["+ mqIp +"] QueueName["+runningQueue+"] cannot purge.");
    		}
    	}
    }
}
