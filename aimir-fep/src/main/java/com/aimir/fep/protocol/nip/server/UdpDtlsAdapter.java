package com.aimir.fep.protocol.nip.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.ErrorHandler;
import org.eclipse.californium.scandium.dtls.AlertMessage.AlertDescription;
import org.eclipse.californium.scandium.dtls.AlertMessage.AlertLevel;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.constants.CommonConstants.ThresholdName;
import com.aimir.fep.protocol.security.DtlsConnector;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.threshold.CheckThreshold;
import com.aimir.util.DateTimeUtil;

public class UdpDtlsAdapter implements UdpDtlsAdapterMBean, MBeanRegistration {
    private static Log log = LogFactory.getLog(UdpDtlsAdapter.class);
    private int PORT = 8002;
    private Integer protocolType = 0;
    private ObjectName objectName = null;
    private DTLSConnector dtlsConnector;
    private NiDtlsProtocolHandler dtlsHandler;
    
    public UdpDtlsAdapter() throws Exception, IOException, MalformedObjectNameException{
       // objectName = new ObjectName("UdpDtlsAdapter");
       log.debug("UdpDtlsAdapter");
    }
    
    public String getName() {
        return objectName.toString();
    }

    public int getPort() {
        return PORT;
    }

    public void setPort(int port) {
        this.PORT = port;
    }
       

    public void start() throws Exception{
        dtlsConnector = DtlsConnector.newDtlsServerConnector(PORT, PORT == 8002? false:true);
        dtlsHandler = new NiDtlsProtocolHandler(false, dtlsConnector, null, this.getClass().getSimpleName() + ":" + DateTimeUtil.getCurrentDateTimeByFormat(null));
        dtlsConnector.setRawDataReceiver(dtlsHandler);
        dtlsConnector.setErrorHandler(new ErrorHandler(){

            @Override
            public void onError(InetSocketAddress peerAddress, AlertLevel level,
                    AlertDescription description) {
                log.error("LEVEL[" + level.name() + " DESCR[" + description.getDescription() + "]");
                String activatorId = peerAddress.getHostString();
                if (activatorId.contains("/") && activatorId.contains(":"))
                    activatorId = activatorId.substring(activatorId.indexOf("/")+1, activatorId.lastIndexOf(":"));
                
                try {
                    EventUtil.sendEvent("Security Alarm",
                            TargetClass.Unknown, activatorId,
                            new String[][] {{"message", "Uncertificated Access"}});
                }
                catch (Exception e) {
                    log.error(e, e);
                }
                
                // INSERT START SP-193
                CheckThreshold.updateCount(peerAddress.toString(), ThresholdName.AUTHENTICATION_ERROR);
                // INSERT END SP-193
            }
        });
        dtlsConnector.start();
    };  
 

    public void stop() {
    	// server.stop();
        dtlsConnector.destroy();
    }

    public String getState() {
        // TODO Auto-generated method stub
        return null;
    }

	public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
	       log.debug("UdpDtlsAdapter.preRegister");
        if (name == null) 
        {
            name = new ObjectName(server.getDefaultDomain() 
                    + ":service=" + this.getClass().getName());
        }

        this.objectName = name;
        return this.objectName;
	}

	public void postRegister(Boolean registrationDone) {
		// TODO Auto-generated method stub
		
	}

	public void preDeregister() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void postDeregister() {
		// TODO Auto-generated method stub
		
	}

	public Integer getProtocolType() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getProtocolTypeString() {
		// TODO Auto-generated method stub
		return null;
	}
}
