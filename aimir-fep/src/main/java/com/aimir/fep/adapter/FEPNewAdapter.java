/**
 * 
 * (관련용어)<br>
 * <p>FEP (Front End Processor)</p>
 * <p>FMP (FEP and MCU Protocol)</p>
 * <p>MRP (Meter Read Protocol)</p>
 * 
 */

package com.aimir.fep.adapter;

import java.io.File;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.aimir.fep.protocol.fmp.server.IF4Adapter;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;


/**
 * IF4 New version adaptor
 *
 * 2014.08.20
 */
@Service
public class FEPNewAdapter {
    private static Log logger = LogFactory.getLog(FEPNewAdapter.class);

    public final static String SERVICE_DOMAIN = "Service";
    public final static String ADAPTER_DOMAIN = "Adapter";

    private String fepName;
    private String commPort;
    //private String bypassPort;
    
    public void init(String commPort, String bypassPort)
    {
        fepName = System.getProperty("name");
        System.setProperty("fepName", fepName);
        
        this.commPort = commPort;
        //this.bypassPort = bypassPort;

        logger.info("fepName ="+fepName);
        logger.info("commPort="+this.commPort);
        //logger.info("bypassPort=" + this.bypassPort);
    }

    public void startService() throws Exception {
        logger.info("Create MBean Server");

        //logger.info("Register BypassAdapter");
        //registerBypassAdapter();
        
        logger.info("Register FMPCommunicationAdapter");
        registerFMPCommunicationAdapter();
        
        //logger.info("Register FMPCircuitEventAdapter");
        //registerFMPCircuitEventAdapter();

        logger.info("\t" + fepName + " FEPh is Ready for Service...\n");
    }

    /*
    private void registerBypassAdapter() throws Exception {
    	ObjectName adapterName = null;

    	BypassAdapter adapter = new BypassAdapter(Integer.parseInt(bypassPort));
        
        adapterName = new ObjectName(
                ADAPTER_DOMAIN+":name=BypassAdapter, port="+bypassPort);
        
        registerMBean(adapter, adapterName);
        adapter.start();
	}
	*/

    protected void registerFMPCommunicationAdapter()
    throws Exception
    {
        ObjectName adapterName = null;
        IF4Adapter adapter = new IF4Adapter();

        if(commPort == null || commPort.length() < 1) {
            commPort = "8000";
        }
        adapter.setPort(Integer.parseInt(commPort));

        adapterName = new ObjectName(
                ADAPTER_DOMAIN+":name=FMPCommunicationAdapter, port="+commPort);
        registerMBean(adapter, adapterName);
        adapter.start();
        
        logger.info("IF4NewAdaptor ready");
    }

    /*
    protected void registerFMPCircuitEventAdapter()
    throws Exception
    {
        logger.info("registerFMPCircuitEventAdapter");
        ObjectName adapterName = null;

        String circuitinfo =
            FMPProperty.getProperty("protocol.fep.circuit.listenport");
        if(circuitinfo == null || circuitinfo.length() < 1) {
            return;
        }
        IF4Adapter adapter;
        String port;
        String protoType;
        protoType = circuitinfo.substring(0,circuitinfo.indexOf(":"));
        port = circuitinfo.substring(circuitinfo.indexOf(":")+1);

        adapter = new IF4Adapter();
        adapter.setPort(Integer.parseInt(port));
        adapter.setProtocolType(Integer.valueOf(protoType));

        registerTSCircuitListener(Integer.parseInt(port));

        adapterName = new ObjectName(
                ADAPTER_DOMAIN+":name=FMPCircuitAdapter, port="+port);
        registerMBean(adapter, adapterName);
        adapter.start();
    }

    protected void registerTSCircuitListener(int port)
    throws Exception
    {
        logger.info("registerTSCircuitListener PORT["+port+"]");
        ObjectName adapterName = null;
        TSCircuitListener adapter = null;

        adapter = new TSCircuitListener(port);
        adapterName = new ObjectName(
                ADAPTER_DOMAIN+":name=TSCircuitListener, port="+port);
        registerMBean(adapter, adapterName);
        adapter.start();
    }

    protected void registerFMPAlarmAdapter(String port)
    throws Exception
    {
        ObjectName adapterName = null;
        IF4Adapter adapter = new IF4Adapter();

        if(port == null || port.length() < 1) {
            port = "8001";
        }
        adapter.setPort(Integer.parseInt(port));
        adapterName = new ObjectName(
                ADAPTER_DOMAIN+":name=FMPAlarmAdapter, port="+port);
        registerMBean(adapter, adapterName);
        adapter.start();
    }
    */

    private void registerMBean(Object obj, ObjectName objName)
    throws Exception
    {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        mbs.registerMBean(obj, objName);
    }
    
    public ObjectName registerMBean(String dom, String name, String clsname)
    throws Exception
    {
        ObjectName objName = null;

        objName = new ObjectName(dom+":name="+name);
        registerMBean(Class.forName(clsname).newInstance(), objName);
        return objName;
    }

    public static void main(String[] args) {
        String commPort = "8000";
        String bypassPort = "8900";

        if (args.length < 1 ) {
            logger.info("Usage:");
            logger.info("FepAapter -DfepName AdapterName -jmxPort AdapterPort -commPort CommunicationPort -bypassPort BypassPort");
            return;
        }

        for (int i=0; i < args.length; i+=2) {

            String nextArg = args[i];
            if (nextArg.startsWith("-commPort")) {
                commPort = new String(args[i+1]);
            }
            else if (nextArg.startsWith("-bypassPort")) {
                bypassPort = new String(args[i+1]);
            }
        }

        try {
            ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{"/config/spring-feph.xml"});

            Tomcat tomcat = new Tomcat();
            tomcat.setPort(Integer.parseInt(FMPProperty.getProperty("feph.webservice.port")));
            Context context = tomcat.addContext("", "");
            Wrapper servletWrap = context.createWrapper();
            servletWrap.setName("cxf");
            CXFNonSpringServlet servlet = new CXFNonSpringServlet();
            // Wire the bus that endpoint uses with the Servlet
            servlet.setBus((Bus)applicationContext.getBean("cxf"));
            servletWrap.setServlet(servlet);
            servletWrap.setLoadOnStartup(1);
            context.addChild(servletWrap);
            context.addServletMapping("/services/*", "cxf");
            logger.info(context.getCatalinaHome().getAbsolutePath());
            File webapps = new File(context.getCatalinaBase().getAbsolutePath() + File.separator + "webapps");
            if (!webapps.exists()) webapps.mkdir();
            tomcat.start();

            DataUtil.setApplicationContext(applicationContext);
        }
        catch (Exception e) {
            logger.error(e, e);
        }
        
        FEPNewAdapter fep = new FEPNewAdapter();
        fep.init(commPort, bypassPort);

        try {
            fep.startService();
        }
        catch (Exception e) {
            logger.error(e, e);
            System.exit(1);
        }
    }
}