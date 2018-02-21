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
import java.util.Date;

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

import com.aimir.fep.bypass.BypassAdapter;
import com.aimir.fep.protocol.fmp.gateway.circuit.TSCircuitListener;
import com.aimir.fep.protocol.fmp.server.FMPAdapter;
import com.aimir.fep.protocol.reversegprs.ReverseGPRSAdapter;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;


/**
 * MOA Startup class
 *
 * 2003.11.17
 */
public class FepAdapterDebug_IF4 {
    private static Log logger = LogFactory.getLog(FepAdapterDebug.class);

    public final static String SERVICE_DOMAIN = "Service";
    public final static String ADAPTER_DOMAIN = "Adapter";

    // private MBeanServer server = null;

    private String fepName;
    private String commPort;
    private String bypassPort;
    private String reverseGPRSPort;

    public void init(String commPort, String bypassPort, String reverseGPRSPort)
    {
        fepName = System.getProperty("name");
        System.setProperty("fepName", fepName);

        this.commPort = commPort;
        this.bypassPort = bypassPort;
        this.reverseGPRSPort = reverseGPRSPort;

        logger.info("name ="+fepName);
        logger.info("commPort="+this.commPort);
        logger.info("bypassPort=" + this.bypassPort);
        logger.info("reverseGPRSPort=" + this.reverseGPRSPort);
    }

    public void startService() throws Exception {
        logger.info("Create MBean Server");

        logger.info("Register BypassAdapter");
        registerBypassAdapter();

        logger.info("Register FMPCommunicationAdapter");
        registerFMPCommunicationAdapter();

        logger.info("Register FMPCircuitEventAdapter");
        registerFMPCircuitEventAdapter();
        
        registerReverseGPRSAdapter();

        logger.info("\t" + fepName + " FEPh is Ready for Service...\n");
    }

    private void registerBypassAdapter() throws Exception {
    	ObjectName adapterName = null;

    	BypassAdapter adapter = new BypassAdapter(Integer.parseInt(bypassPort));

        adapterName = new ObjectName(
                ADAPTER_DOMAIN+":name=BypassAdapter, port="+bypassPort);

        registerMBean(adapter, adapterName);
        adapter.start();
	}
    
    private void registerReverseGPRSAdapter() throws Exception {
    	
    	if(reverseGPRSPort != null && !"".equals(reverseGPRSPort)){
        	logger.info("Register ReverseGPRSAdapter");
        	ObjectName adapterName = null;

        	ReverseGPRSAdapter adapter = new ReverseGPRSAdapter();
        	adapter.setPort(Integer.parseInt(reverseGPRSPort));

            adapterName = new ObjectName(
                    ADAPTER_DOMAIN+":name=ReverseGPRSAdapter, port="+reverseGPRSPort);

            registerMBean(adapter, adapterName);
            adapter.start();
    	}
	}

	/*
    private void sendStartup() throws Exception {
        logger.info("JMX Connector Server Start");
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" +
                rmiRegistryFactoryBean.getPort() + "/" + fepName);
        JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbeanServer);
        cs.start();
    }
    */

    /*
    protected void registerHTTPAdapter()
    throws Exception
    {
        ObjectName adapterName = null;
        HtmlAdaptorServer adapter = new HtmlAdaptorServer();

        adapter.setPort(Integer.parseInt(httpPort));
        adapterName = new ObjectName(ADAPTER_DOMAIN+":name=http, port="+httpPort);
        registerMBean(adapter, adapterName);
        adapter.start();
    }
    */

    protected void registerFMPCommunicationAdapter()
    throws Exception
    {
        ObjectName adapterName = null;
        FMPAdapter adapter = new FMPAdapter();

        if(commPort == null || commPort.length() < 1) {
            commPort = "8000";
        }
        adapter.setPort(Integer.parseInt(commPort));

        adapterName = new ObjectName(
                ADAPTER_DOMAIN+":name=FMPCommunicationAdapter, port="+commPort);
        registerMBean(adapter, adapterName);
        adapter.start();

        logger.info("FMPCommunicationAdapter ready");
    }

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
        FMPAdapter adapter;
        String port;
        String protoType;
        protoType = circuitinfo.substring(0,circuitinfo.indexOf(":"));
        port = circuitinfo.substring(circuitinfo.indexOf(":")+1);

        adapter = new FMPAdapter();
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
        FMPAdapter adapter = new FMPAdapter();

        if(port == null || port.length() < 1) {
            port = "8001";
        }
        adapter.setPort(Integer.parseInt(port));
        adapterName = new ObjectName(
                ADAPTER_DOMAIN+":name=FMPAlarmAdapter, port="+port);
        registerMBean(adapter, adapterName);
        adapter.start();
    }

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
		Date startDate = new Date();
		long startTime = startDate.getTime();
		
        String commPort = "8000";
        String bypassPort = "8900";
        String reverseGPRSPort = "";

//        if (args.length < 1 ) {
//            logger.info("Usage:");
//            logger.info("FepAapter -DfepName AdapterName -jmxPort AdapterPort -commPort CommunicationPort -bypassPort BypassPort -rgp ReverseGPRSPort");
//            return;
//        }

        for (int i=0; i < args.length; i+=2) {

            String nextArg = args[i];
            if (nextArg.startsWith("-commPort")) {
                commPort = new String(args[i+1]);
            }
            else if (nextArg.startsWith("-bypassPort")) {
                bypassPort = new String(args[i+1]);
            }
            else if (nextArg.startsWith("-rgp")) {
            	reverseGPRSPort = new String(args[i+1]);
            }
            
        }

        try {
            // DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring-feph.xml"}));
            // jetty 9 버전에서 적용해야 함.
            // Server server = new Server(new QueuedThreadPool(Integer.parseInt(FMPProperty.getProperty("feph.jetty.thread.max"))));
            /*Server server = new Server(Integer.parseInt(FMPProperty.getProperty("feph.webservice.port")));
            server.setThreadPool(new QueuedThreadPool(Integer.parseInt(FMPProperty.getProperty("feph.jetty.thread.max"))));
            ServletContextHandler context = new
                    ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            server.setHandler(context);*/
            
            /* jetty 8
            Server server = new Server();
            SelectChannelConnector connector = new SelectChannelConnector();
            connector.setPort(Integer.parseInt(FMPProperty.getProperty("feph.webservice.port")));
            connector.setMaxIdleTime(30000);
            server.setConnectors(new Connector[]{connector});
            ServletContextHandler context = new
                    ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            server.setHandler(context);
            */
            
            /* jetty 9 버전
            ServerConnector http = new ServerConnector(server);
            http.setPort(Integer.parseInt(FMPProperty.getProperty("feph.webservice.port")));
            http.setIdleTimeout(12000);
            server.addConnector(http);
            */

            /**
             * Audit Target Name 설정.
             */
    		String fepName = System.getProperty("name");
            if(fepName == null || fepName.equals("")){
                System.setProperty("aimir.auditTargetName", "FEPh");
            }else{
                System.setProperty("aimir.auditTargetName", fepName);
            }

            ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{"/config/spring-feph.xml"});

            Tomcat tomcat = new Tomcat();
            tomcat.setPort(Integer.parseInt(FMPProperty.getProperty("feph.webservice.port")));
            Context context = tomcat.addContext("/", "");
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
            // for jetty
            // context.addServlet(new ServletHolder(servlet),"/services/*");
            // server.start();
            // server.join();
            
            /*
            Connector[] cons = server.getConnectors();
            if (cons == null) logger.info("cons are null");

            else {
                for (Connector con : cons) {
                    con.setMaxIdleTime(0);
                    logger.info(con.getName() + " MaxIdleTime[" + con.getMaxIdleTime() + "]");
                }
            }
            */
            
            DataUtil.setApplicationContext(applicationContext);
        }
        catch (Exception e) {
            logger.error(e, e);
        }

        FepAdapterDebug_IF4 fep = new FepAdapterDebug_IF4();
        fep.init(commPort, bypassPort, reverseGPRSPort);

        try {
            fep.startService();
        }
        catch (Exception e) {
            logger.error(e, e);
            System.exit(1);
        }
        
		long endTime = System.currentTimeMillis();
		logger.info("######## FepAdapterDebug Elapse Time : " + (endTime - startTime) / 1000.0f + "s");
    }
}