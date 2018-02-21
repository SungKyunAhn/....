package com.aimir.fep.protocol.fmp.gateway.circuit.relay;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.aimir.fep.protocol.fmp.gateway.circuit.CircuitListener;

/**
 * {@link RelayProtocolHandler}  Curcuit Data Relay Service
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2006-10-16 15:59:15 +0900 $,
 */
public class RelayProtocolHandler extends IoHandlerAdapter
{
    private static Log log = LogFactory.getLog(RelayProtocolHandler.class);
    private CircuitListener listener = null; 

    /**
     * Constructor
     */
    public RelayProtocolHandler(CircuitListener listener)
    {
        this.listener = listener;
    }

    /**
     * inherited method from ProtocolHandlerAdapter
     */
    public void exceptionCaught(IoSession session, 
            Throwable cause )
    {
        // Close connection when unexpected exception is caught.
        cause.printStackTrace();
        session.close(true);
    }

    /**
     * inherited method from ProtocolHandlerAdapter
     * handling GeneralDataFrame
     *
     * @param session <code>IoSession</code> session
     * @param message <code>Object</code> decoded GeneralDataFrame
     */
    public void messageReceived(IoSession session, 
            Object message )
    {
        try 
        {
            IoBuffer byteBuffer = (IoBuffer)message;
            int len = byteBuffer.limit();
            byte[] bx = new byte[len];
            byteBuffer.get(bx,0,bx.length);
            listener.receviedMessageFromRelay(bx);
        }catch(Exception ex)
        {
            log.error("RelayProtocolHandler::messageReceived "
                    + " failed" ,ex);
        }
    }

    /**
     * inherited method from ProtocolHandlerAdapter
     */
    public void sessionIdle(IoSession session, 
            IdleStatus status) throws Exception
    {
        log.info("IDLE COUNT : " 
                + session.getIdleCount(IdleStatus.READER_IDLE));
    }

    /**
     * inherited method from ProtocolHandlerAdapter
     */
    public void sessionOpened(IoSession session)
    {
        log.debug("sessionOpened : " + session.getRemoteAddress());
    }

    /**
     * inherited method from ProtocolHandlerAdapter
     */
    public void sessionClosed(IoSession session)
    {
        this.listener.setIsActiveFMP(false);
        log.debug("Session Closed : "+ session.getRemoteAddress());
    }
}
