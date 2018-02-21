package com.aimir.fep.bypass;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.aimir.fep.protocol.fmp.server.FMPSslContextFactory;

public class BypassAdapter implements BypassAdapterMBean, MBeanRegistration {
	private static Log log = LogFactory.getLog(BypassAdapter.class);
	private ObjectName objectName = null;
	private final String name = "BypassAdapter";
	final String[] states = { "Stopped", "Stopping", "Starting", "Started", "Failed",
			"Destroyed" };
	final int STOPPED = 0;
	final int STOPPING = 1;
	final int STARTING = 2;
	final int STARTED = 3;
	final int FAILED = 4;
	final int DESTROYED = 5;
	private int state = STOPPED;
		    
	private int port=0;
	
	private IoAcceptor acceptor = null;
	
	public BypassAdapter(int port) throws GeneralSecurityException, IOException{
		
		ExecutorService executor = Executors.newCachedThreadPool();
		acceptor = new NioSocketAcceptor(executor, new NioProcessor(executor));
		
		FMPSslContextFactory.setSslFilter(acceptor);
		acceptor.getFilterChain().addLast(this.name,
                new ProtocolCodecFilter(new ProtocolCodecFactory()
                {
                    public ProtocolDecoder getDecoder(IoSession session) throws Exception
                    {
                        return new BypassDecoder();
                    }

                    public ProtocolEncoder getEncoder(IoSession session) throws Exception
                    {
                        return new BypassEncoder();
                    }
                }
                ));
        acceptor.setDefaultLocalAddress(new InetSocketAddress(port));
        acceptor.setHandler(new BypassHandler());
        //  acceptor.addListener(new BypassService());         
        
        this.port = port;
	}
	 
	
	/**
     * start BypassAdapter
     *
     * @throws Exception
     */
    public void start() throws Exception
    {
        log.debug(this.objectName  + " start");
        try {
            state=STARTING;
            acceptor.bind();
            log.info( "BypassAdapter Listening on port " + this.port );
            state=STARTED;
        }catch(Exception ex)
        {
            log.error("objectName["+this.objectName 
                    +"] start failed");
            state = STOPPED;
            throw ex;
        }
    }
    /**
     * stop FMPBypassAdapter
     */
    public void stop()
    {
        log.debug(this.objectName + " stop");
        state=STOPPING;
        acceptor.unbind();
        state=STOPPED;
    }
    
		    
	@Override
	public void postDeregister() {
		// TODO Auto-generated method stub
		
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
	public ObjectName preRegister(MBeanServer server, ObjectName name)
			throws Exception {
		if (name == null) 
        {
            name = new ObjectName(server.getDefaultDomain() 
                    + ":service=" + this.getClass().getName());
        }
		this.objectName = name;
//        this.trapAdapter.setName(name.toString());
        return name;
	}


	@Override
	public String getName() {
		return this.name;
	}


	@Override
	public int getPort() {
		return this.port;
	}


	@Override
	public String getState()
    {
        return states[state];
    }

}
