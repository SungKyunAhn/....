package com.aimir.fep.command;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;

public class SchedulerTest {
    private static Log log = LogFactory.getLog(SchedulerTest.class);
    
    @Test
    public void test_doGetCalendar() {
        JMXConnector jmxc = null;
        try {
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9000/jmxrmi");
            jmxc = JMXConnectorFactory.connect(url);
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
            ObjectName objectName = new ObjectName("Adapter:name=Scheduler");
            
            Scheduler scheduler = JMX.newMBeanProxy(mbsc, objectName, Scheduler.class, true);
            String[] names = scheduler.getCalendarNames().toArray(new String[0]);
            for (String name : names) {
                log.info(name);
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        finally {
            try {
                if (jmxc != null)
                    jmxc.close();
            }
            catch (Exception e){}
        }
    }
    
    @Test
    public void test_connect() {
        JMXConnector jmxc = null;
        try {
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9000/jmxrmi");
            log.info(url.getURLPath());
            jmxc = JMXConnectorFactory.connect(url);
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
            ObjectName objectName = new ObjectName("Adapter:name=Scheduler");
            
            Scheduler scheduler = JMX.newMBeanProxy(mbsc, objectName, Scheduler.class, true);
            String[] names = scheduler.getJobGroupNames().toArray(new String[0]);
            for (String name : names) {
                log.info(name);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (jmxc != null)
                    jmxc.close();
            }
            catch (Exception e){}
        }
    }
    
    @Test
    public void test_doAddJob() {
        JMXConnector jmxc = null;
        try {
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9000/jmxrmi");
            jmxc = JMXConnectorFactory.connect(url);
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
            ObjectName objectName = new ObjectName("Adapter:name=Scheduler");
            
            Scheduler scheduler = JMX.newMBeanProxy(mbsc, objectName, Scheduler.class, true);
            
            // JobDetail job = new JobDetail("dumbJob", null, org.quartz.jobs.NativeJob.class);
            // job.getJobDataMap().put(org.quartz.jobs.NativeJob.PROP_COMMAND, "echo \"hi\" >> foobar.txt");
            // Trigger trigger = TriggerUtils.makeSecondlyTrigger(5);
            // trigger.setName("dumbTrigger");
            // scheduler.scheduleJob(job, trigger);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        finally {
            try {
                if (jmxc != null)
                    jmxc.close();
            }
            catch (Exception e){}
        }
    }
}
