package com.aimir.fep.protocol.security;

import java.io.IOException;
import java.net.InetSocketAddress;

import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.springframework.stereotype.Component;


@Component
public class FepUdpAuthenticator {
//    private static final long serialVersionUID = 1L;
    private static Log log = LogFactory.getLog(FepUdpAuthenticator.class);
    private String name = "FepUdpAuthenticator";
    private  int PORT = 9002;
    
    NioDatagramAcceptor acceptor = null;

    public FepUdpAuthenticator() {
        acceptor = new NioDatagramAcceptor();      
    }


    
    /**
     * start FepUdpAuthenticator
     *
     * @throws Exception
     */
    public void start()
    {
    	try {
	        acceptor.setHandler(new AuthProtocolHandler());
	        acceptor.setDefaultLocalAddress(new InetSocketAddress(PORT));
	        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
	        chain.addLast("logger", new LoggingFilter());
	        chain.addLast("Executor", new ExecutorFilter(Executors.newCachedThreadPool()));
	        DatagramSessionConfig dcfg = acceptor.getSessionConfig();
	        dcfg.setReuseAddress(true);
	        
	        acceptor.bind(new InetSocketAddress(PORT));
//	        System.out.println("#####################");
//	        System.out.println(acceptor.toString());
//	        System.out.println("#####################");
		    log.info( "UdpAuthenticator(UDP) Listening on port " + PORT );
        } catch (IOException e) {
            log.error(e);
            e.printStackTrace();
        }
    }
    /**
     * get FepUdpAuthenticator Name
     *
     * @return name <code>String</code> name
     */
    public String getName()
    {
        return this.name;
    }
    /**
     * set FepUdpAuthenticator Name
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
     * stop FepUdpAuthenticator
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
            EMPA.setPort(9002);
            EMPA.start();
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
