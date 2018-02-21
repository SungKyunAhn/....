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

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aimir.dao.device.ModemDao;
import com.aimir.fep.command.mbean.CommandService;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;

/**
 * MOA Startup class
 *
 * 2003.11.17
 */
public class FepIranTest {

	private static Log logger = LogFactory.getLog(FepIranTest.class);
	
	@Autowired
	private ModemDao modemDao;

    public static void main(String[] args) {
    	
        try {
            
            ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{"/config/spring-feph-iran.xml"});
            
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
            
            DataUtil.setApplicationContext(applicationContext);
            
            while(true) {
            	
            }
        }
        catch (Exception e) {
        	e.printStackTrace();
            logger.error(e, e);
        }
    }
}
