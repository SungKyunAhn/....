/**
 * 
 * (관련용어)<br>
 * <p>FEP (Front End Processor)</p>
 * <p>FMP (FEP and MCU Protocol)</p>
 * 
 */

package com.aimir.fep.adapter;

import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import com.aimir.fep.protocol.fmp.server.FMPSslContextFactory;
import com.aimir.fep.protocol.smcp.SMCPProtocolProvider;
import com.aimir.fep.util.DataUtil;

/**
 * SMCP Listener
 * 한전 SMCP프로토콜 처리
 * KEPCO FEP Protocol에 Mapping
 * 2014.09.30
 */
/**
 * FEPh Startup class
 *
 * 2017.06.21
 */
@SpringBootApplication
@EntityScan(basePackages={"com.aimir.model"})
@ComponentScan(basePackages={"com.aimir"}, excludeFilters= @ComponentScan.Filter(
        classes={FepAdapter.class, FepAdapter_IF4.class, FepProcessor.class, SnmpAdapter.class, FEPNewAdapter.class},
        type=FilterType.ASSIGNABLE_TYPE))
@Configuration(value="config/spring-feph.xml")
public class SMCPAdapter implements DynamicMBean, MBeanRegistration {
    private static Log logger = LogFactory.getLog(SMCPAdapter.class);

    public final static String SERVICE_DOMAIN = "Service";
    public final static String ADAPTER_DOMAIN = "Adapter";

    private IoAcceptor acceptor = null;
    
    private String fepName;
    private String commPort;
    private ObjectName objectName;
    
    private SMCPProtocolProvider protocolProvider;
    
    public void init(String commPort, SMCPProtocolProvider protocolProvider)
    {
        fepName = System.getProperty("name");
        System.setProperty("fepName", fepName);
        
        this.commPort = commPort;
        this.protocolProvider = protocolProvider;
        
        logger.info("name="+fepName+", commPort="+this.commPort);
        
        ExecutorService executor = Executors.newCachedThreadPool();
        acceptor = new NioSocketAcceptor(executor, new NioProcessor(executor));
    }
    
    /**
     * MBean 서비스를 생성하고 SMPP 리스너를 실행한다.
     * @throws Exception
     */
    public void startService() throws Exception {
        objectName = new ObjectName(
                ADAPTER_DOMAIN+":name="+fepName);
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        mbs.registerMBean(this, objectName);
        
        // acceptor 설정
        FMPSslContextFactory.setSslFilter(acceptor);
        acceptor.getFilterChain().addLast(this.fepName, new ProtocolCodecFilter(protocolProvider.getCodecFactory()));
        acceptor.setDefaultLocalAddress(new InetSocketAddress(Integer.parseInt(commPort)));
        acceptor.setHandler(protocolProvider.getHandler());
        acceptor.bind();
        
        logger.info("\t" + fepName + " FEPh is Ready for Service...\n");
    }
    
      
    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName name)
            throws Exception {
        if (objectName == null) 
        {
            objectName = new ObjectName(server.getDefaultDomain() 
                    + ":service=" + this.getClass().getName());
        }
        
        return this.objectName;
    }

    @Override
    public void postRegister(Boolean registrationDone) {
  
    }

    @Override
    public void preDeregister() throws Exception {

    }

    @Override
    public void postDeregister() {
    }

    @Override
    public Object getAttribute(String attribute)
            throws AttributeNotFoundException, MBeanException,
            ReflectionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setAttribute(Attribute attribute)
            throws AttributeNotFoundException, InvalidAttributeValueException,
            MBeanException, ReflectionException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature)
            throws MBeanException, ReflectionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MBeanInfo getMBeanInfo() {

        return new MBeanInfo(
                this.getClass().getName(),
                "SMCP Listener MBean",
                null,
                null,
                null,
                null);
    }
    
    public static void main(String[] args) {
        if (args.length < 1 ) {
            logger.info("SMCPAapter -DfepName AdapterName -jmxPort AdapterPort -commPort CommunicationPort");
            return;
        }

        SpringApplication.run(SMCPAdapter.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            String commPort = "8888";
            
            for (int i=0; i < args.length; i+=2) {

                String nextArg = args[i];

                if (nextArg.startsWith("-commPort")) {
                    commPort = new String(args[i+1]);
                }
            }
            
            /*
            Server server = new Server(Integer.parseInt(FMPProperty.getProperty("feph.webservice.port")));
            // server.setThreadPool(new QueuedThreadPool(Integer.parseInt(FMPProperty.getProperty("feph.jetty.thread.max"))));
            ServletContextHandler context = new 
                    ServletContextHandler(ServletContextHandler.SESSIONS); 
            context.setContextPath("/"); 
            server.setHandler(context); 
            ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{"/config/spring-feph.xml"}); 

            CXFNonSpringServlet servlet = new CXFNonSpringServlet(); 
            // Wire the bus that endpoint uses with the Servlet 
            servlet.setBus((Bus)applicationContext.getBean("cxf")); 
            
            context.addServlet(new ServletHolder(servlet),"/services/*"); 
            server.start(); 
            */
            
            /*
            Connector[] cons = server.getConnectors();
            if (cons == null) logger.info("cons are null");
            else {
                for (Connector con : cons) {
                    con.setMaxIdleTime(5 * 60  * 1000);
                    logger.info(con.getName() + " MaxIdleTime[" + con.getMaxIdleTime() + "]");
                }
            }
            */

            DataUtil.setApplicationContext(ctx);
            
            SMCPAdapter adapter = new SMCPAdapter();
            adapter.init(commPort, ctx.getBean(SMCPProtocolProvider.class));
            adapter.startService();
        };
    }
}