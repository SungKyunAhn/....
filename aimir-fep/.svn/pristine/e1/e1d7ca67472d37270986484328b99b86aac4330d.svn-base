package com.aimir.fep.protocol.fmp.gateway.circuit.relay;

import java.net.InetSocketAddress;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.aimir.fep.protocol.fmp.gateway.circuit.CircuitListener;
import com.aimir.fep.util.FMPProperty;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Curcuit Data Relay Service
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2006-10-16 15:59:15 +0900 $,
 */
public class RelayService
{
    private Log log = LogFactory.getLog(RelayService.class);

    private String HOST = "127.0.0.1"; // server IP Address
    private int PORT = 8000; // server port
    private int CONNECT_TIMEOUT = Integer.parseInt(FMPProperty.getProperty("Protocol.connection.timeout", 30+"")); // seconds

    private IoConnector connector = null;
    private RelayProtocolProvider provider = null; 
    private IoSession session = null;
    private CircuitListener listener = null;

    /**
     * constructor
     */
    public RelayService(CircuitListener listener) 
    {
        this.listener = listener;
        this.connector = new NioSocketConnector();
        this.provider = new RelayProtocolProvider(this.listener);
    }

    /**
     * constructor
     */
    public RelayService(CircuitListener listener, int port) 
    {
        this.listener = listener;
        this.PORT = port;
        this.connector = new NioSocketConnector();
        this.provider = new RelayProtocolProvider(this.listener);
    }

    /**
     * constructor
     */
    public RelayService(CircuitListener listener, String host, int port) 
    {
        this.listener = listener;
        this.HOST = host;
        this.PORT = port;
        this.connector = new NioSocketConnector();
        this.provider = new RelayProtocolProvider(this.listener);
    }

    /**
     * stop Relay Service
     * initialize
     * create IoProtocolConnector
     * create FMPClientProtocolProvider
     */
    public void start() throws Exception
    {
        connect();
    }

    /**
     * stop Relay Service
     */
    public void stop()
    {
        log.info("RelayService stop");
        try
        {
            if(session != null && session.isConnected()) 
                session.close(true);
            session = null;
        }catch(Exception ex)
        {
            log.error(ex,ex);
        }
    }


    /**
     * connect to Target 
     *
     * @throws Exception
     */
    public void connect() throws Exception
    {
        if(session != null && session.isConnected())
        {
            log.debug("session is already connected");
            return;
        }

        for( ;; )
        {
            session = connector.connect(new InetSocketAddress(this.HOST,this.PORT )).getSession();
            connector.setConnectTimeoutMillis(CONNECT_TIMEOUT*1000);
            connector.getFilterChain().addLast("RelayService", 
                    new ProtocolCodecFilter(provider.getCodecFactory()));
            
            break;
        }

        if(session == null)
            throw new Exception("Failed to connect. host["
                        + this.HOST+"] port[" + this.PORT+"]");
    }

    public void sendMessage(IoBuffer buffer) throws Exception
    {
        this.session.write(buffer);
    }

    public void sendMessage(byte[] data) throws Exception
    {
        this.session.write(data);
    }
}
