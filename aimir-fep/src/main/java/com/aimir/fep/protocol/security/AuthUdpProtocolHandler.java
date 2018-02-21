package com.aimir.fep.protocol.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;


/**
 * Class the extends IoHandlerAdapter in order to properly handle
 * connections and the data the connections send
 *
 * @author 
 */
public class AuthUdpProtocolHandler extends IoHandlerAdapter {

    private static Log log = LogFactory.getLog(AuthUdpProtocolHandler.class);
  //  private MemoryMonitor server;

    public AuthUdpProtocolHandler() {
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        session.closeNow();
    }
    public static byte[] fromShort(short value) {
        int arraySize = Short.SIZE / Byte.SIZE;
        ByteBuffer buffer = ByteBuffer.allocate(arraySize);
        return buffer.putShort(value).array();
    }
    
    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
    	
    	Charset charset = Charset.forName("UTF-8");
        if (message instanceof IoBuffer) {
            IoBuffer buffer = (IoBuffer) message;
            SocketAddress remoteAddress = session.getRemoteAddress();

            
            IoBuffer sbuffer = IoBuffer.allocate(8);
            sbuffer.putString("RECV-OK", charset.newEncoder());
            sbuffer.flip();
            session.write(sbuffer);
//            for (int i = 0; i < session.getService().getManagedSessions().values().toArray().length; i++) {
//
//                IoSession aSession=(IoSession) session.getService().getManagedSessions().values().toArray()[i];
//                aSession.write(sbuffer);
//            }
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        log.debug("Session closed...");
        SocketAddress remoteAddress = session.getRemoteAddress();
        //server.removeClient(remoteAddress);
        session.closeNow();
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {

       log.debug("Session created...");
        SocketAddress remoteAddress = session.getRemoteAddress();
//        server.addClient(remoteAddress);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
    	log.debug("Session idle...");
    	session.closeNow();
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        log.debug("Session Opened...");
    }
    

}
