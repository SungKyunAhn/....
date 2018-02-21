package com.aimir.fep.protocol.fmp.server;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.system.Code;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * (<b>Entry point</b>) MCUTrapAdapter server which processing event from MCU
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
@Component
public class FMPTrapAdapter
{
    private static Log log = LogFactory.getLog(FMPTrapAdapter.class);
    private String name = "FMPTrapAdapter";
    private int PORT = 7095;
    private IoAcceptor acceptor = null;
    private Integer protocolType = new Integer(FMPProperty.getProperty("protocol.type.default","3"));
    
    @Autowired
    private FMPProtocolProvider protocolProvider;
    
    /**
     * constructor
     */
    public FMPTrapAdapter()
    {
        
        // int maxPoolSize = Runtime.getRuntime().availableProcessors() * 2;
        ExecutorService executor = Executors.newCachedThreadPool();
        acceptor = new NioSocketAcceptor(executor, new NioProcessor(executor));
        
        // ExecutorService executor = Executors.newFixedThreadPool(maxPoolSize);
        // BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue();
        // Executor executor = new ThreadPoolExecutor(10, maxPoolSize, 60l, TimeUnit.SECONDS, blockingQueue);
        // acceptor = new NioSocketAcceptor(new NioProcessor(executor));
        
        // this.name=name;
        log.debug("load FMP MIB Completed. ### PORT=" + getPort() + " ###");
    }

    /**
     * get FMPTrapAdapter Name
     *
     * @return name <code>String</code> name
     */
    public String getName()
    {
        return this.name;
    }
    /**
     * set FMPTrapAdapter Name
     *
     * @param name <code>String</code> name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * get listen port
     *
     * @return port <code>int</code>
     */
    public int getPort()
    {
        return this.PORT;
    }

    /**
     * set listen port
     *
     * @param port <code>int</code>
     */
    public void setPort(int port)
    {
        this.PORT = port;
    }

    /**
     * start FMPTrapAdapter
     *
     * @throws Exception
     */
    public void start() throws Exception
    {
        FMPSslContextFactory.setSslFilter(acceptor);
        acceptor.getFilterChain().addLast(this.name,
                new ProtocolCodecFilter(protocolProvider.getCodecFactory()));
        acceptor.setDefaultLocalAddress(new InetSocketAddress(PORT));
        acceptor.setHandler(protocolProvider.getHandler());
        acceptor.bind();

        log.info( "FMPTrapAdapter Listening on port " + PORT );
    }

    /**
     * stop FMPTrapAdapter
     *
     * unbind adapter service
     */
    public void stop()
    {
        acceptor.unbind();
    }

    /**
     * set Protocol Type(1:CDMA,2:GSM,3:GPRS,4:PSTN,5:LAN)
     * @param protocolType <code>Integer</code> Protocol Type
     */
    public void setProtocolType(Integer protocolType)
    {
        this.protocolType = protocolType;
        protocolProvider.setProtocolType(protocolType);
    }

    /**
     * get Protocol Type(1:CDMA,2:GSM,3:GPRS,4:PSTN,5:LAN)
     * @return protocolType <code>Integer</code> Protocol Type
     */
    public Integer getProtocolType()
    {
        return this.protocolType;
    }

    /**
     * get Protocol Type String(1:CDMA,2:GSM,3:GPRS,4:PSTN,5:LAN)
     * @return protocolType <code>String</code> Protocol Type
     */
    public String getProtocolTypeString()
    {
        int proto = this.protocolType.intValue();
        Code code = CommonConstants.getProtocol(proto+"");
        return "[" + code.getName() + "]";
    }


    public static void main( String[] args ) throws Exception
    {
        try {
            FMPTrapAdapter EMPA =
                new FMPTrapAdapter();
            //EMPA.setPort(8000);
            EMPA.setPort(8001);
            EMPA.start();
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
