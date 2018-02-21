
package com.aimir.fep.moclasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;

public class TimerListener<E> implements 
				TimerListenerMBean, MBeanRegistration, NotificationListener {
    private static Log log = LogFactory.getLog(TimerListener.class);
	MBeanServer server = null;
	int counter = 0;

	public TimerListener() { }

	public void handleNotification(Notification noti, Object obj) {
		try {
			ObjectName moname = (ObjectName)noti.getUserData();
			//String type = noti.getType();
			//Integer retval = null;

			counter++;
			/*
			retval = (Integer)server.getAttribute(moname, "State");
			System.out.println(counter + ": mo=" + moname.toString() + ",state=" + retval.intValue());
			*/
			log.info(moname);
			HashMap<?, ?> hm = (HashMap<?, ?>) server.invoke(
					new ObjectName("MoAdapter:name=relationService"),
					"findReferencingRelations",
					new Object []  {moname,"myRelationType",null},
					new String [] {"javax.management.ObjectName",
						"java.lang.String", "java.lang.String"}
					);
			for(Iterator<?> ki = (hm.keySet()).iterator(); ki.hasNext();) {
				Object co = ki.next();
				log.debug("obj:" + co.toString());
				log.debug("value:");
				ArrayList<?> al = (ArrayList<?>) (hm.get(co));
				for(Iterator<?> i = al.iterator();i.hasNext();){
				    log.debug("\t"+ i.next().toString());
				}
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void postDeregister() { }
	public void postRegister(Boolean registrationDone) { }
	public void preDeregister() { }

	public ObjectName preRegister(MBeanServer server, ObjectName name) {
		this.server = server;

		try {

		} catch(Exception e) {
			e.printStackTrace();
		}

		return name;
	}
}
