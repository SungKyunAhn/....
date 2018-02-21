package com.aimir.fep.protocol.fmp.gateway.circuit;

import java.net.InetSocketAddress;

import com.aimir.fep.protocol.fmp.gateway.circuit.relay.RelayService;
import com.aimir.fep.util.Hex;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 * Cirucuit Data Listener
 * initialize circuit connection and relay Protocol data from MCU to FEP
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2006-10-16 15:59:15 +0900 $,
 */
public class CircuitListener
{
    private Log log = LogFactory.getLog(CircuitListener.class);
    private RelayService relay = null;
    private String HOST = "127.0.0.1"; // Terminal Server IP Address
    private int PORT = 2108; // Terminal Server port
    private int CONNECT_TIMEOUT = 30; // seconds

    private IoConnector connector = null;
    private CircuitProtocolProvider provider = null; 
    private IoSession session = null;
    private CircuitProtocolHandler handler = null;

    /**
     * Constructor
     */
    public CircuitListener(String ipaddr, int port, int clport)
    {
        this.HOST = ipaddr;
        this.PORT = port;
        this.relay = new RelayService(this,clport);
    }

    /**
     * start Circuit Listener
     */
    public void start() throws Exception
    {
        log.info("CircuitListener start");
        connector = new NioSocketConnector();
        provider = new CircuitProtocolProvider(this);
        connect();
    }

    /**
     * stop Circuit Listener
     */
    public void stop() 
    {
        log.info("CircuitListener stop");
        try
        {
            if(session != null && session.isConnected())
                session.close(true);
            session = null;

            if(relay != null)
                relay.stop();
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
        for( ;; )
        {
            session = connector.connect(new InetSocketAddress(this.HOST,this.PORT )).getSession();
            connector.setConnectTimeoutMillis(CONNECT_TIMEOUT*1000);
            connector.getFilterChain().addLast("FMPCircuit", 
                    new ProtocolCodecFilter(provider.getCodecFactory()));
            
            break;
        }

        if(session == null)
            throw new Exception("Failed to connect. host["
                        + this.HOST+"] port[" + this.PORT+"]");
        handler = (CircuitProtocolHandler)session.getHandler();
    }

    /**
     * Re-start Circuit Listener
     */
    public void restart() 
    {
        if(session != null && session.isConnected())
            session.close(true);
        Thread thread = new Thread()
        {
            @SuppressWarnings("static-access")
			public void run()
            {
                while(true)
                {
                    try{ connect(); break; } catch(Exception ex)
                    {
                        try{ Thread.currentThread().sleep(10*1000);
                        }catch(Exception exx){}
                    }
                } 
            }
        };
        thread.start();
    }

    /**
     * set whether is active FEP Protocol Data or not
     */
    public void setIsActiveFMP(boolean data)
    {
        handler.setIsActiveFMP(data);
    }

    /**
     * Start Relay Service
     */
    public void startRelayService() throws Exception
    {
        this.relay.start();
    }

    /**
     * Stop Relay Service
     */
    public void stopRelayService()
    {
        this.relay.stop();
    }

    /** 
     * Set Relay Service
     */
    public void setRelayService(RelayService relay)
    {
        this.relay = relay;
    }

    /**
     * Get Relay Service
     */
    public RelayService getRelayService()
    {
        return this.relay;
    }

    /**
     * receive message from relay service and send to MCU
     */
    public void receviedMessageFromRelay(byte[] data) throws Exception
    {
        log.info("receivedMessageFromRelay data["+Hex.decode(data)+"]");
        session.write(data);
    }

    /**
     * receivce message from MCU and send to relay service
     */
    public void writeMessageToRelay(byte[] data) throws Exception
    {
        log.info("writeMessageToRelay["+Hex.decode(data)+"]");
        this.relay.sendMessage(data);
    }

    /**
     * for Cricuit Listener Test
     */
    public static void main(String[] args)
    {
        try
        {
            String ipaddr = "127.0.0.1";
            int port = 2108;
            int clport = 8005;
            CircuitListener listener = new CircuitListener(ipaddr,port,clport);
            listener.start();
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
