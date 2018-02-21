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
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aimir.fep.protocol.security.FepTcpAuthenticatorAdapter;
import com.aimir.fep.protocol.security.FepUdpAuthenticatorAdapter;
import com.aimir.fep.command.mbean.CommandService;
import com.aimir.fep.protocol.fmp.server.FMPAdapter;
import com.aimir.fep.protocol.nip.server.NiTcpAdapter;
import com.aimir.fep.protocol.nip.server.NiTcpCmdAdapter;
import com.aimir.fep.protocol.nip.server.NiUdpAdapter;
import com.aimir.fep.protocol.nip.server.UdpDtlsAdapter;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;


/**
 * MOA Startup class
 *
 * 2003.11.17
 */
public class FepAuthAdapterDebug {
    private static Log logger = LogFactory.getLog(FepAuthAdapterDebug.class);

    public final static String SERVICE_DOMAIN = "Service";
    public final static String ADAPTER_DOMAIN = "Adapter";

    // private MBeanServer server = null;

    private String fepName;
    private String if4Port;
    private String niTcpPort;
    private String niUdpPort;
    private String commandPort;
    // INSERT START SP-121 //
    private String  authTcpPort;
    private String  authUdpPort;
    // INSERT END SP-121 //
    private boolean dtlsUse =  "true".equals(FMPProperty.getProperty("protocol.dtls.use"));
    
    public void init()
    {
        fepName = System.getProperty("name");
        System.setProperty("fepName", fepName);
    }

    public void setIf4Port(String if4Port) {
        this.if4Port = if4Port;
    }

    public void setNiTcpPort(String niTcpPort) {
        this.niTcpPort = niTcpPort;
    }

    public void setNiUdpPort(String niUdpPort) {
        this.niUdpPort = niUdpPort;
    }

    public void setCommandPort(String commandPort) {
        this.commandPort = commandPort;
    }

    public void setAuthTcpPort(String authTcpPort) {
        this.authTcpPort = authTcpPort;
    }

    public void setAuthUdpPort(String authUdpPort) {
        this.authUdpPort = authUdpPort;
    }

    public void startService() throws Exception {
        logger.info("Create MBean Server");

        // logger.info("Register FMPCommunicationAdapter");
        // registerFMPCommunicationAdapter();

        // INSERT START SP-121 //
        logger.info("Register TcpAuthenticatorAdapter");
        registerTcpAuthenticatorAdapter();
        
        logger.info("Register UdpAuthenticatorAdapter");
        registerUdpAuthenticatorAdapter();
        // INSERT END   SP-121 //
        
        // logger.info("Register NiTcpAdapter");
        // registerNiTcpAdapter();
        
        // logger.info("Register CommandAdapter");
        // registerNiTcpCommandAdapter();
        
        // if (Boolean.parseBoolean(FMPProperty.getProperty("protocol.ssl.dtls.test"))) {
        //    logger.info("Register UDP DTLS Adapter(JS) for Test");
        //    registerUdpDtlsAdapter();
        // }
        
        // UPDATE START SP-123 //
        // if ( dtlsUse ){
	    //    logger.info("Register NiUdpAdapter(DTLS)");
	    //    registerNiUdpDtlsAdapter();
        // }
        // else {
	    //    logger.info("Register NiUdpAdapter");
	    //    registerNiUdpAdapter();
        // }
        // UPDATE END SP-123 //
        
        logger.info("\t" + fepName + " FEPAuth is Ready for Service...\n");
    }

    protected void registerFMPCommunicationAdapter()
    throws Exception
    {
        ObjectName adapterName = null;
        FMPAdapter adapter = new FMPAdapter();

        if(if4Port == null || if4Port.length() < 1) {
            if4Port = "8000";
        }
        adapter.setPort(Integer.parseInt(if4Port));

        adapterName = new ObjectName(
                ADAPTER_DOMAIN+":name=FMPCommunicationAdapter, port="+if4Port);
        registerMBean(adapter, adapterName);
        adapter.start();

        logger.info("FMPCommunicationAdapter ready");
    }

    protected void registerNiTcpAdapter()
    throws Exception
    {
        ObjectName adapterName = null;
        NiTcpAdapter adapter = new NiTcpAdapter();
    
        if(niTcpPort == null || niTcpPort.length() < 1) {
            niTcpPort = "8001";
        }
        adapter.setPort(Integer.parseInt(niTcpPort));
        adapterName = new ObjectName(
                ADAPTER_DOMAIN+":name=NiTcpAdapter, port="+niTcpPort);
        registerMBean(adapter, adapterName);
        adapter.start();
    }
    
    protected void registerNiUdpAdapter()
    throws Exception
    {
        ObjectName adapterName = null;
        NiUdpAdapter adapter = new NiUdpAdapter();
    
        if(niUdpPort == null || niUdpPort.length() < 1) {
            niUdpPort = "8002";
        }
        adapter.setPort(Integer.parseInt(niUdpPort));
        adapterName = new ObjectName(
                ADAPTER_DOMAIN+":name=NiUdpAdapter, port="+niUdpPort);
        registerMBean(adapter, adapterName);
        adapter.start();
    }

    protected void registerUdpDtlsAdapter()
    throws Exception
    {
        ObjectName adapterName = null;
        UdpDtlsAdapter adapter = new UdpDtlsAdapter();
    
        adapter.setPort(8012);
        adapterName = new ObjectName(
                ADAPTER_DOMAIN+":name=UdpDtlsAdapter, port="+8012);
        registerMBean(adapter, adapterName);
        logger.debug("UdpDtlsAdapter Registerd");
        adapter.start();
    }
    
    private void registerMBean(Object obj, ObjectName objName)
    throws Exception
    {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        mbs.registerMBean(obj, objName);
    }

    // INSERT START SP-121 //
    protected void registerTcpAuthenticatorAdapter()
    throws Exception
    {
        ObjectName adapterName = null;
        //ReverseGPRSAdapter adapter = new ReverseGPRSAdapter();
        FepTcpAuthenticatorAdapter adapter = new FepTcpAuthenticatorAdapter();
        logger.info("registerTcpAuthenticatorAdapter  adapter Created");
        if(authTcpPort == null || authTcpPort.length() < 1) {
        	authTcpPort = "9001";
        }
        adapter.setPort(Integer.parseInt(authTcpPort));

        adapterName = new ObjectName(
                ADAPTER_DOMAIN+":name=TcpAuthenticatorAdapter, port="+authTcpPort);
        logger.debug(adapterName);
        registerMBean(adapter, adapterName);
        adapter.start();

        logger.info("registerTcpAuthenticatorAdapter ready");
    }
    
    protected void registerUdpAuthenticatorAdapter()
    		throws Exception
    {
    	ObjectName adapterName = null;
    	//ReverseGPRSAdapter adapter = new ReverseGPRSAdapter();
    	FepUdpAuthenticatorAdapter adapter = new FepUdpAuthenticatorAdapter();
    	logger.info("registerUdpAuthenticatorAdapter  adapter Created");
    	if(authUdpPort == null || authUdpPort.length() < 1) {
    		authUdpPort = "9002";
    	}
    	adapter.setPort(Integer.parseInt(authUdpPort));

    	adapterName = new ObjectName(
    			ADAPTER_DOMAIN+":name=UdpAuthenticatorAdapter, port="+authUdpPort);
    	logger.debug(adapterName);
    	registerMBean(adapter, adapterName);
    	adapter.start();

    	logger.info("registerUdpAuthenticatorAdapter ready");
    }
    
    private void registerNiTcpCommandAdapter() throws Exception {
    	
    	 ObjectName adapterName = null;
    	 NiTcpCmdAdapter adapter = new NiTcpCmdAdapter();
     
         if(commandPort == null || commandPort.length() < 1) {
        	 commandPort = "8900";
         }
         adapter.setPort(Integer.parseInt(commandPort));
         adapterName = new ObjectName(
                 ADAPTER_DOMAIN+":name=NiTcpCmdAdapter, port="+commandPort);
         registerMBean(adapter, adapterName);
         adapter.start();
	}
    // INSERT END SP-121 //
    
    public ObjectName registerMBean(String dom, String name, String clsname)
    throws Exception
    {
        ObjectName objName = null;

        objName = new ObjectName(dom+":name="+name);
        registerMBean(Class.forName(clsname).newInstance(), objName);
        return objName;
    }

    public static void main(String[] args) {
        String if4Port = "8000";
        String niTcpPort = "8001";
        String niUdpPort = "8002";
        String authTcpPort = "9001";
        String authUdpPort = "9002";
        String commandPort = "8900";
        
//        if (args.length < 1 ) {
//            logger.info("Usage:");
//            logger.info("FepAapter -DfepName AdapterName -jmxPort AdapterPort -if4Port IF4Port -niTcpPort niTcpPort -niUdpPort niUdpPort -authTcpPort authTcpPort -authUdpPort authUdpPort -commandPort CommandPort");
//            return;
//        }

        for (int i=0; i < args.length; i+=2) {

            String nextArg = args[i];
            if (nextArg.startsWith("-if4Port")) {
                if4Port = new String(args[i+1]);
            }
            else if (nextArg.startsWith("-niTcpPort")) {
                niTcpPort = new String(args[i+1]);
            }
            else if (nextArg.startsWith("-niUdpPort")) {
            	niUdpPort = new String(args[i+1]);
            }
            else if (nextArg.startsWith("-authTcpPort")) {
                authTcpPort = new String(args[i+1]);
            }
            else if (nextArg.startsWith("-authUdpPort")) {
                authUdpPort = new String(args[i+1]);
            }
            else if (nextArg.startsWith("-commandPort")) {
                commandPort = new String(args[i+1]);
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

            ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{"/config/spring-fepa.xml"});

            Tomcat tomcat = new Tomcat();
            tomcat.setPort(Integer.parseInt(FMPProperty.getProperty("fepa.webservice.port")));
            Context context = tomcat.addContext("", "");
            
            // SOAP WS
//            Wrapper servletWrap = context.createWrapper();
//            servletWrap.setName("cxf");
//            CXFNonSpringServlet servlet = new CXFNonSpringServlet();
//            // Wire the bus that endpoint uses with the Servlet
//            servlet.setBus((Bus)applicationContext.getBean("cxf"));
//            servletWrap.setServlet(servlet);
//            servletWrap.setLoadOnStartup(1);
//            context.addChild(servletWrap);
//            context.addServletMapping("/services/*", "cxf");
            
            // REST
            Wrapper restWrap = context.createWrapper();
            restWrap.setName("jaxrs");
            restWrap.setServletClass("org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet");
            restWrap.addInitParameter("jaxrs.serviceClasses", CommandService.class.getName());
            restWrap.setLoadOnStartup(1);
            context.addChild(restWrap);
            context.addServletMapping("/rest/*", "jaxrs");
            
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

        FepAuthAdapterDebug fep = new FepAuthAdapterDebug();
        fep.setIf4Port(if4Port);
        fep.setNiTcpPort(niTcpPort);
        fep.setNiUdpPort(niUdpPort);
        fep.setAuthTcpPort(authTcpPort);
        fep.setAuthUdpPort(authUdpPort);
        fep.setCommandPort(commandPort);
        fep.init();

        try {
            fep.startService();
        }
        catch (Exception e) {
            logger.error(e, e);
            System.exit(1);
        }
    }
}
