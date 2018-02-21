package com.aimir.fep.protocol.nip.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.protocol.fmp.server.FMPSslContextFactory;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.system.Code;
import com.aimir.util.DateTimeUtil;

public class NiUdpAdapter implements NiUdpAdapterMBean, MBeanRegistration {
    private static Log log = LogFactory.getLog(NiUdpAdapter.class);
    public int PORT = 8002;
    private ObjectName objectName = null;
    private Integer protocolType = 
            new Integer(FMPProperty.getProperty("protocol.type.default"));
    private NioDatagramAcceptor acceptor;
    
    public NiUdpAdapter() throws IOException, MalformedObjectNameException{
        // objectName = new ObjectName("NiUdpAdapter");
        acceptor = new NioDatagramAcceptor();
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
       
    @Override
    public void start() throws Exception {
        // execute not command handler
        acceptor.setHandler(new NiProtocolHandler(false, this.getClass().getSimpleName() + ":" + DateTimeUtil.getCurrentDateTimeByFormat(null)));
        acceptor.setDefaultLocalAddress(new InetSocketAddress(PORT));
        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
        chain.addLast("logger", new LoggingFilter());
        chain.addLast("codec", new ProtocolCodecFilter(new NiProtocolProvider()));
        chain.addLast("Executor", new ExecutorFilter(Executors.newCachedThreadPool()));
        DatagramSessionConfig dcfg = acceptor.getSessionConfig();
        dcfg.setReuseAddress(true);
        try {
            acceptor.bind();
            log.info("#####################");
            log.info(acceptor.toString());
            log.info("#####################");
        } catch (IOException e) {
            log.error(e, e);
        }
        
        log.info( "NiUdpAdapter Listening on port " + PORT );
    }
    
    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName name)
            throws Exception {
        if (name == null) 
        {
            name = new ObjectName(server.getDefaultDomain() 
                    + ":service=" + this.getClass().getName());
        }

        this.objectName = name;
        return this.objectName;
    }

    @Override
    public void postRegister(Boolean registrationDone) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void preDeregister() throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void postDeregister() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Integer getProtocolType() {
        return this.protocolType;
    }

    @Override
    public String getProtocolTypeString() {
        int proto = this.protocolType.intValue();
        Code code = CommonConstants.getProtocol(proto+"");
        return "[" + code.getName() + "]";
    }

    @Override
    public void stop() {
        acceptor.unbind();
    }

    @Override
    public String getState() {
        // TODO Auto-generated method stub
        return null;
    }
}
