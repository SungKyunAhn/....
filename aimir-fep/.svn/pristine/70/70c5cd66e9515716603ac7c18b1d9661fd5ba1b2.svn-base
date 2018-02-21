package com.aimir.fep.protocol.fmp.gateway.test;

import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 * To test the GSM Terminal Server
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2006-10-16 15:59:15 +0900 $,
 */
public class GSMService
{
    private static Log log = LogFactory.getLog(GSMService.class);

    private int PORT = 2108; // server port
    private IoAcceptor acceptor = null;
    private String name = "GSMService";
    private GSMServiceProtocolProvider provider = null; 

    public GSMService()
    {
        this.acceptor = new NioSocketAcceptor();
        this.provider = new GSMServiceProtocolProvider();
    }

    /**
     * start GSMService
     *
     * @throws Exception
     */
    public void start() throws Exception
    {
        acceptor.getFilterChain().addLast(this.name,
                new ProtocolCodecFilter(provider.getCodecFactory()));
        acceptor.setDefaultLocalAddress(new InetSocketAddress(PORT));
        acceptor.setHandler(provider.getHandler());
        acceptor.bind();
        
        log.info( "GSMService Listening on port " + PORT );
    }

    public static void main(String[] args)
    {
        try
        {
            GSMService gsmService = new GSMService();
            gsmService.start();
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
