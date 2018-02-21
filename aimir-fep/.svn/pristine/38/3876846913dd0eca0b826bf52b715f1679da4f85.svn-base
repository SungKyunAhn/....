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
 * (<b>Entry point</b>) IF4NewAdapter server which processing event from DCU, MIU
 *
 * @author goodjob (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2014-08-19 10:00:00 +0900 $,
 */
@Component
public class IF4NewAdapter
{
    private static Log log = LogFactory.getLog(IF4NewAdapter.class);
    private String name = "IF4NewAdapter";
    private int PORT = 7095;
    private IoAcceptor acceptor = null;
    private Integer protocolType = new Integer(FMPProperty.getProperty("protocol.type.default","3"));
    
    @Autowired
    private IF4NewProtocolProvider protocolProvider;
    
    /**
     * constructor
     */
    public IF4NewAdapter()
    {
        ExecutorService executor = Executors.newCachedThreadPool();
        acceptor = new NioSocketAcceptor(executor, new NioProcessor(executor));

        log.debug("IF4NewAdapter Completed");
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

        log.info( "IF4NewAdapter Listening on port " + PORT );
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
}
