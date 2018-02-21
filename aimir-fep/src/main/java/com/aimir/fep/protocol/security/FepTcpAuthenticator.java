package com.aimir.fep.protocol.security;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * (<b>Entry point</b>) FepTcpAuthenticator
 *
 * @author 
 * @version 
 */
@Component
public class FepTcpAuthenticator
{
    private static Log log = LogFactory.getLog(FepTcpAuthenticator.class);
    private String name = "FepTcpAuthenticator";
    private int PORT = 9001;
    private IoAcceptor acceptor = null;
//  private Integer protocolType = new Integer(FMPProperty.getProperty("protocol.type.default","3"));
    
    @Autowired
    //private FMPProtocolProvider protocolProvider;
    private AuthTcpProtocolProvider protocolProvider;
    
    /**
     * constructor
     */
    public FepTcpAuthenticator()
    {
        
        // int maxPoolSize = Runtime.getRuntime().availableProcessors() * 2;
        ExecutorService executor = Executors.newCachedThreadPool();
        acceptor = new NioSocketAcceptor(executor, new NioProcessor(executor));
        
        // ExecutorService executor = Executors.newFixedThreadPool(maxPoolSize);
        // BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue();
        // Executor executor = new ThreadPoolExecutor(10, maxPoolSize, 60l, TimeUnit.SECONDS, blockingQueue);
        // acceptor = new NioSocketAcceptor(new NioProcessor(executor));
        
        // this.name=name;
        //log.debug("load FMP MIB Completed");
    }

    /**
     * get FepTcpAuthenticator Name
     *
     * @return name <code>String</code> name
     */
    public String getName()
    {
        return this.name;
    }
    /**
     * set FepTcpAuthenticator Name
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
     * start FepTcpAuthenticator
     *
     * @throws Exception
     */
    public void start() 
    {
    	
        try {
	      //  FMPSslContextFactory.setSslFilter(acceptor);
        	((NioSocketAcceptor)acceptor).setReuseAddress(true);	// INSERT 2016.11.14 SP-314
	        acceptor.getFilterChain().addLast(this.name,
	                new ProtocolCodecFilter(protocolProvider.getCodecFactory()));
	        acceptor.setDefaultLocalAddress(new InetSocketAddress(PORT));
	        acceptor.setHandler(protocolProvider.getHandler());
	        acceptor.bind();
//	        System.out.println("#####################");
//	        System.out.println(acceptor.toString());
//	        System.out.println("#####################");
	        log.info( "TcpAuthenticator(TCP) Listening on port " + PORT );
        } catch (IOException e) {
            log.error(e);
            e.printStackTrace();
        }
    }

    /**
     * stop FepTcpAuthenticator
     *
     * unbind adapter service
     */
    public void stop()
    {
        acceptor.unbind();
    }


    public static void main( String[] args ) throws Exception
    {
        try {
        	FepTcpAuthenticator EMPA =
                new FepTcpAuthenticator();
            //EMPA.setPort(8000);
            EMPA.setPort(9001);
            EMPA.start();
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
