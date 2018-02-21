package com.aimir.fep.udp;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;

public class UdpClient extends IoHandlerAdapter implements Runnable {
	private String myName;
	private String localAddress;
	private int localPort;
	private String destAddress;
	private int destPort;
	
	private IoConnector connector;
	private IoSession session;

	public UdpClient(String myName, String localAddress, int localPort, String destAddress, int destPort) {
		this.myName = myName;
		this.localAddress = localAddress;
		this.localPort = localPort;
		this.destAddress = destAddress;
		this.destPort = destPort;
		
		init();
	}

	private void init() {
		connector = new NioDatagramConnector();
		connector.setHandler(this);

		IoFilter CODEC_FILTER = new ProtocolCodecFilter(new TextLineCodecFactory());
		IoFilter LOGGING_FILTER = new LoggingFilter();
		connector.getFilterChain().addLast("codec", CODEC_FILTER);
		connector.getFilterChain().addLast("logger", LOGGING_FILTER);

		//ConnectFuture connFuture = connector.connect(new InetSocketAddress(destAddress, destPort)); // remote
				
		
		ConnectFuture connFuture = connector.connect(new InetSocketAddress(destAddress, destPort) // remote
				, new InetSocketAddress(localAddress, localPort)); // local

		connFuture.awaitUninterruptibly();   
		session = connFuture.getSession();
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		System.out.println("[" + this.myName + "][C <- S] : " + message.toString() +" 받음~" + "SESSION[" + session.toString() + "]");
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {

	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		
	}

	@Override
	public void run() {
		System.out.println(this.myName + " 시작~");	
		
		for (int i = 0; i <= 100; i++) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println("[" + this.myName + "][C -> S] : " + i +" 보냄~");
			session.write(this.myName + i);

			
		}
		session.write("quit");

		System.out.println("끝~!");
		connector.dispose(true);
	}
	
	public static void main(String args[]){
		
		try {
			InetAddress ia = InetAddress.getLocalHost();
			String myName = ia.getHostName();
			
			UdpClient client = new UdpClient(myName, "localhost", 8006, "localhost", 8002);
			
			client.run();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
}
