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
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.protocol.fmp.server.FMPSslContextFactory;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.system.Code;
import com.aimir.util.DateTimeUtil;

public class NiTcpCmdAdapter implements NiTcpCmdAdapterMBean, MBeanRegistration {
    private static Log log = LogFactory.getLog(NiTcpCmdAdapter.class);
    private int PORT = 8900;
    private ObjectName objectName = null;
    private Integer protocolType = 
            new Integer(FMPProperty.getProperty("protocol.type.default"));
    
    private IoAcceptor acceptor = null;
    
    public NiTcpCmdAdapter() throws IOException, MalformedObjectNameException {
        // objectName = new ObjectName("NiTcpAdapter");
        acceptor = new NioSocketAcceptor();
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
    
    /**
     * start NiTcpCmdAdapter
     *
     * @throws Exception
     */
    public void start() throws Exception
    {
        FMPSslContextFactory.setSslFilter(acceptor);
        ((NioSocketAcceptor)acceptor).setReuseAddress(true);	// INSERT 2016.11.14 SP-314
        acceptor.setHandler(new NiProtocolHandler(true, this.getClass().getSimpleName() + ":" + DateTimeUtil.getCurrentDateTimeByFormat(null)));
        acceptor.setDefaultLocalAddress(new InetSocketAddress(PORT));
        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
        chain.addLast("logger", new LoggingFilter());
        // NI 프로토콜이기 때문에 NiProtocolDecoder, NiProtocolEncoder와 같다고 봄.
        chain.addLast("codec", new ProtocolCodecFilter(new NiProtocolProvider()));
        chain.addLast("Executor", new ExecutorFilter(Executors.newCachedThreadPool()));
    
        try {
            acceptor.bind();
            log.info("#####################");
            log.info(acceptor.toString());
            log.info("#####################");
        } catch (IOException e) {
            log.error(e, e);
        }

        log.info( "NiTcpCmdAdapter Listening on port " + PORT );
    }

    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName name)
            throws Exception
    {
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
