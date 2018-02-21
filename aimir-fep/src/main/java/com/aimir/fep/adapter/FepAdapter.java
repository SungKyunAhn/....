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
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aimir.fep.protocol.security.FepTcpAuthenticatorAdapter;
import com.aimir.fep.protocol.security.FepUdpAuthenticatorAdapter;
import com.aimir.constants.CommonConstants;
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
public class FepAdapter {
    private static Log logger = LogFactory.getLog(FepAdapter.class);

    public final static String SERVICE_DOMAIN = "Service";
    public final static String ADAPTER_DOMAIN = "Adapter";

    // private MBeanServer server = null;

    private String fepName;
    private String if4Port;
    private String niTcpPort;
    private String niUdpPort;
    private String niPanaPort;
    private String niCmdPort;
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

    public void setNiCmdPort(String niCmdPort) {
        this.niCmdPort = niCmdPort;
    }

    public void setAuthTcpPort(String authTcpPort) {
        this.authTcpPort = authTcpPort;
    }

    public void setAuthUdpPort(String authUdpPort) {
        this.authUdpPort = authUdpPort;
    }

    public String getNiPanaPort() {
        return niPanaPort;
    }

    public void setNiPanaPort(String niPanaPort) {
        this.niPanaPort = niPanaPort;
    }

    public void startService() throws Exception {
        logger.info("Create MBean Server");

        logger.info("Register FMPCommunicationAdapter");
        registerFMPCommunicationAdapter();

        // INSERT START SP-121 //
        // logger.info("Register TcpAuthenticatorAdapter");
        // registerTcpAuthenticatorAdapter();
        
        // logger.info("Register UdpAuthenticatorAdapter");
        // registerUdpAuthenticatorAdapter();
        // INSERT END   SP-121 //
        
        logger.info("Register NiTcpAdapter");
        registerNiTcpAdapter();
        
        logger.info("Register NicMDAdapter");
        registerNiTcpCmdAdapter();
        
        // UPDATE START SP-123 //
        if ( dtlsUse ){
            logger.info("Register NiUdpAdapter(DTLS)");
            registerUdpDtlsAdapter();
        }
        else {
            logger.info("Register NiUdpAdapter");
            registerNiUdpAdapter();
        }
        // UPDATE END SP-123 //
        
        logger.info("Register NiPanaAdapter");
        registerNiPanaAdapter();
        
        CommonConstants.refreshContractStatus();
        CommonConstants.refreshDataSvc();
        CommonConstants.refreshGasMeterAlarmStatus();
        CommonConstants.refreshGasMeterStatus();
        CommonConstants.refreshHeaderSvc();
        CommonConstants.refreshInterface();
        CommonConstants.refreshMcuType();
        CommonConstants.refreshMeterStatus();
        CommonConstants.refreshMeterType();
        CommonConstants.refreshModemPowerType();
        CommonConstants.refreshModemType();
        CommonConstants.refreshProtocol();
        CommonConstants.refreshSenderReceiver();
        CommonConstants.refreshWaterMeterAlarmStatus();
        CommonConstants.refreshWaterMeterStatus();
        
        logger.info("\t" + fepName + " FEPh is Ready for Service...\n");
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
    
    protected void registerNiPanaAdapter()
    throws Exception
    {
        ObjectName adapterName = null;
        UdpDtlsAdapter adapter = new UdpDtlsAdapter();
    
        if(niPanaPort == null || niPanaPort.length() < 1) {
            niPanaPort = "8003";
        }
        adapter.setPort(Integer.parseInt(niPanaPort));
        adapterName = new ObjectName(
                ADAPTER_DOMAIN+":name=NiPanaAdapter, port="+niPanaPort);
        registerMBean(adapter, adapterName);
        adapter.start();
    }

    protected void registerUdpDtlsAdapter()
    throws Exception
    {
        ObjectName adapterName = null;
        UdpDtlsAdapter adapter = new UdpDtlsAdapter();
    
        if(niUdpPort == null || niUdpPort.length() < 1) {
            niUdpPort = "8002";
        }
        adapter.setPort(Integer.parseInt(niUdpPort));
        adapterName = new ObjectName(
                ADAPTER_DOMAIN+":name=UdpDtlsAdapter, port="+niUdpPort);
        registerMBean(adapter, adapterName);
        logger.debug("UdpDtlsAdapter Registerd");
        adapter.start();
    }
    
    protected void registerNiTcpCmdAdapter()
    throws Exception
    {
        ObjectName adapterName = null;
        NiTcpCmdAdapter adapter = new NiTcpCmdAdapter();
    
        if(niCmdPort == null || niCmdPort.length() < 1) {
            niCmdPort = "8900";
        }
        adapter.setPort(Integer.parseInt(niCmdPort));
        adapterName = new ObjectName(
                ADAPTER_DOMAIN+":name=NiTcpCmdAdapter, port="+niCmdPort);
        registerMBean(adapter, adapterName);
        logger.debug("NiTcpCmdAdapter Registerd");
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
        String niPanaPort = "8003";
        String authTcpPort = "9001";
        String authUdpPort = "9002";
        String niCmdPort = "8900";
        
        if (args.length < 1 ) {
            logger.info("Usage:");
            logger.info("FepAapter -DfepName AdapterName -jmxPort AdapterPort -if4Port IF4Port -niTcpPort niTcpPort -niUdpPort niUdpPort -niPanaPort niPanaPort -authTcpPort authTcpPort -authUdpPort authUdpPort -commandPort CommandPort");
            return;
        }

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
            else if (nextArg.startsWith("-niPanaPort")) {
                niPanaPort = new String(args[i+1]);
            }
            else if (nextArg.startsWith("-authTcpPort")) {
                authTcpPort = new String(args[i+1]);
            }
            else if (nextArg.startsWith("-authUdpPort")) {
                authUdpPort = new String(args[i+1]);
            }
            else if (nextArg.startsWith("-commandPort")) {
                niCmdPort = new String(args[i+1]);
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
            
            Connector httpsConnector = new Connector();
            httpsConnector.setPort(Integer.parseInt(FMPProperty.getProperty("feph.webservice.port.ssl")));
            httpsConnector.setSecure(true);
            httpsConnector.setScheme("https");
            httpsConnector.setAttribute("keyAlias", "tomcat");
            httpsConnector.setAttribute("keystorePass", FMPProperty.getProperty("protocol.ssl.keystore.password","aimiramm"));
            httpsConnector.setAttribute("keystoreFile", FMPProperty.getProperty("protocol.ssl.keystore.tomcat"));
            httpsConnector.setAttribute("clientAuth", "false");
            httpsConnector.setAttribute("sslProtocol", "TLSv1.2");
            httpsConnector.setAttribute("SSLEnabled", true);
            
            org.apache.catalina.Service service = tomcat.getService();
            service.addConnector(httpsConnector);

            Context context = tomcat.addContext("", "");
            // SOAP WS
            Wrapper servletWrap = context.createWrapper();
            servletWrap.setName("cxf");
            CXFNonSpringServlet servlet = new CXFNonSpringServlet();
            // Wire the bus that endpoint uses with the Servlet
            servlet.setBus((Bus)applicationContext.getBean("cxf"));
            servletWrap.setServlet(servlet);
            servletWrap.setLoadOnStartup(1);
            context.addChild(servletWrap);
            context.addServletMapping("/services/*", "cxf");
            
            // REST
            Wrapper restWrap = context.createWrapper();
            restWrap.setName("jaxrs");
            restWrap.setServletClass("org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet");
            restWrap.addInitParameter("jaxrs.serviceClasses", CommandService.class.getName());
            restWrap.setLoadOnStartup(1);
            context.addChild(restWrap);
            context.addServletMapping("/rest/*", "jaxrs");
            
            File webapps = new File(context.getCatalinaBase().getAbsolutePath() + File.separator + "webapps");            
            logger.debug("OTA Context => " + FMPProperty.getProperty("feph.webservice.ota.context"));
            logger.debug("OTA Context path => " + webapps.getAbsolutePath());

            if (!webapps.exists()) webapps.mkdir();
            tomcat.addWebapp("/" + FMPProperty.getProperty("feph.webservice.ota.context"), webapps.getAbsolutePath());            
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

        FepAdapter fep = new FepAdapter();
        fep.setIf4Port(if4Port);
        fep.setNiTcpPort(niTcpPort);
        fep.setNiUdpPort(niUdpPort);
        fep.setNiPanaPort(niPanaPort);
        fep.setAuthTcpPort(authTcpPort);
        fep.setAuthUdpPort(authUdpPort);
        fep.setNiCmdPort(niCmdPort);
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
