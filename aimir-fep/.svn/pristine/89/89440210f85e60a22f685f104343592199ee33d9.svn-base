package com.aimir.fep.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Date;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;

public class UdpServer extends IoHandlerAdapter {
	private String myName;

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		cause.printStackTrace();
		session.closeNow();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		String str = message.toString();
		if (str.trim().equalsIgnoreCase("quit")) {
			session.closeNow();
			return;
		}
		
		System.out.println("SESSION[" + session.toString() + "]");
		System.out.println("[" + this.myName + "][S <- C] : " + message.toString() +" 받음~");
		
		System.out.println("다시 보냄 : " + message.toString() + " 안녕?");
		session.write(message.toString() + " 안녕?");
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		System.out.println("Session closed...");
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		System.out.println("Session created...");
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		System.out.println("Session idle...");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		System.out.println("Session Opened...");
	}

	public UdpServer(String myName, int port) throws IOException {
		this.myName = myName;

		NioDatagramAcceptor acceptor = new NioDatagramAcceptor();
		acceptor.setHandler(this);

		DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
		chain.addLast("logger", new LoggingFilter());
		chain.addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));

		acceptor.bind(new InetSocketAddress(port));

		System.out.println("[" + this.myName + "] Server started...");
	}


	public static void main(String[] args) throws IOException {

		Date date = new Date();
		String myName = String.valueOf(date.getTime());
		new UdpServer(myName, 8006);
	}
}
