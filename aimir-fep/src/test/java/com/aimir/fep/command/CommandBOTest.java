package com.aimir.fep.command;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;

import com.aimir.fep.BaseTestCase;
import com.aimir.fep.command.conf.KamstrupCIDMeta;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.command.mbean.CommandGWMBean;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.meter.parser.Kamstrup;
import com.aimir.fep.protocol.fmp.exception.FMPMcuException;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;


public class CommandBOTest extends BaseTestCase {
    private static Log log = LogFactory.getLog(CommandBOTest.class);
    
    private static Object lock = new Object();
    private static int waitCount = 0;
    private static int connCount = 0;
    private static final int MAX_COUNT = 50;
    
    private static void lock(int waitNum) throws InterruptedException {
        synchronized(lock) {
            if (connCount == MAX_COUNT) {
                waitNum = waitCount++;
                while (connCount == MAX_COUNT) {
                    log.debug("Waiting number[" + waitNum + "], wait count[" + waitCount + "]");
                    lock.wait();
                }
            }
            connCount++;
            log.debug("Connecting number[" + waitNum + "], wait count[" + waitCount + "], conn count[" + connCount + "]");
        }
    }

    private static void release() {
        synchronized(lock) {
            log.debug("Release connection count[" + connCount + "]");
            connCount--;
            if (waitCount > 0 || connCount > 0) {
                lock.notify();
                if (waitCount > 0)
                    waitCount--;
            }
        }
    }
    
    public void test_doMCUScanning() {
        try {
            for (int i = 0; i < 1000; i++) {
                new Thread(new Runnable() {

                    public void run() {

                        JMXConnector jmxc = null;
                        try {
                            int waitNum = 0;
                            lock(waitNum);
                            String rmiIp = System.getProperty("serverIp");
                            String rmiPort = System.getProperty("serverPort");
                            String fepName = System.getProperty("fepName");
                            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+rmiIp+":" + rmiPort+ "/"+fepName);
                            jmxc = JMXConnectorFactory.connect(url);
                            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
                            ObjectName objectName = new ObjectName("Service:name=FMPCommandGW");
                            
                            CommandGWMBean mbeanProxy = JMX.newMBeanProxy(mbsc, objectName, CommandGWMBean.class, true);
                            mbeanProxy.cmdStdGet("5000", "");
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
                            release();
                        }
                    }
                    
                }).start();
            }
            Thread.sleep(900000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Ignore
    public void test_connect() {
        try {
            JMXConnector jmxc = null;
            try {
                String rmiIp = "localhost"; // System.getProperty("serverIp");
                String rmiPort = "1199"; // System.getProperty("serverPort");
                JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+rmiIp+":" + rmiPort+ "/jmxrmi");
                log.info(url.getURLPath());
                jmxc = JMXConnectorFactory.connect(url);
                MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
                ObjectName objectName = new ObjectName("Service:name=FMPCommandGW");
                
                CommandGWMBean mbeanProxy = JMX.newMBeanProxy(mbsc, objectName, CommandGWMBean.class, true);
                mbeanProxy.cmdStdGet("201107", "");
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
                release();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Ignore
    public void test_kamstrup_relay() {
        CommandGW gw = null; // this.applicationContext.getBean(CommandGW.class);
        try {
            byte[] res = gw.cmdKamstrupCID("777", "14990045", new String[]{"GetCutOffState"});
            String[] result = (String[])KamstrupCIDMeta.getResult(res);
            for (String str : result) {
                log.info(str);
            }
        } catch (FMPMcuException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void test_kamstrup_ondemand() {
        CommandGW gw = this.applicationContext.getBean(CommandGW.class);
        DataUtil.setApplicationContext(this.applicationContext);
        try {
            
            // MeterData meterData = gw.cmdOnDemandMeter("3993","14990045","000D6F00003145AE","","20120910000000","20120910000000");
            Kamstrup kams = new Kamstrup();
            kams.parse(Hex.encode("0F403F1003E933040000E4BADDF4DF0D3C403F10000D020444001D443"
+"F0001020400000000BF0013020400000000BF001702040000000000001B02040000000000001F0204000000000045140D0F403F10003A0004000000000301800D36403F10041E21020000E5041F21020"
+"000000420210200000004342204420000000004352204420000000004362204420000000070AC0D3F403F1004193508000000000000000000040916044300000000041A3508000000000000000000040"
+"C16044300000000041B35080000000000000000005E050D34403F1003EB3004000001D84E03EA2F0400000271E903EC2E0400000014E1041735080082012102100A090C041500010000D6970D30403F1"
+"003F2330A0000000000000000E4BADD003333040000E4BADD003433040000000000003533040000000000C47A0D8E403FA20105000404173508008001000000010B0A00010204000000005B001302040"
+"00000005B001702040000000000001F0204000000000000058003000000010C0A000000870000008700000000000000000006800600000001010B000000BF000000BF000000000000000000078206000"
+"00001090C000000BF000000BF0000000000000000000700070098EC0D64403FA20103000404193508008005002D0B1D0A0A0027160443000008EE002B16044300000AA000058004002D13040B0A00000"
+"0A000000B4000068002002D0D150C0A000000CC00000C0C000700000000000000000000000000000C0C0007000700E5FC0D"));
            /*
            String[] result = KamstrupCIDMeta.getResult(res);
            for (String str : result) {
                log.info(str);
            }
            */
        } catch (FMPMcuException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
