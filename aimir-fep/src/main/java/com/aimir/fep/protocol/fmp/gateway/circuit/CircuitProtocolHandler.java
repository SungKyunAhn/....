package com.aimir.fep.protocol.fmp.gateway.circuit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.aimir.fep.protocol.fmp.exception.FMPException;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;

/**
 * {@link CircuitProtocolHandler}  Curcuit Data 
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2006-10-16 15:59:15 +0900 $,
 */
public class CircuitProtocolHandler extends IoHandlerAdapter
{
    private Log log = LogFactory.getLog(CircuitProtocolHandler.class);

    private CircuitListener listener = null; 

    private Object msgMonitor = new Object();
    private Object msg = null;

    private boolean isActiveFMP = false;
    private boolean connectOk = false;

    private int idleTime = Integer.parseInt(FMPProperty.getProperty(
                "Protocol.Circuit.Connect.Listener.IdleTime",""+(30*60)));

    private final char CR='\r';
    private final char LF='\n';
    private final String CRLF="\r\n";

    private final String RINGSIGNAL=CRLF+FMPProperty.getProperty(
            "Protocol.Circuit.TS.Listener.RING","RING")+CRLF;
    private final String ATA=FMPProperty.getProperty(
            "Protocol.Circuit.TS.Listener.ATA","ATA")+CRLF;
    private final String CONNECT=FMPProperty.getProperty(
            "Protocol.Circuit.TS.Listener.CONNECT","CONNECT");
    private final String NACS=FMPProperty.getProperty(
            "Protocol.Circuit.TS.Listener.Service.NACS","NURI/1.0 NACS");
    private final String ACCEPT=FMPProperty.getProperty(
            "Protocol.Circuit.TS.Listener.Service.ACCEPT","ACCEPT")+LF+LF;
    private final String CIRCUITCLOSEDMSG = "4E4F2043415252494552";

    /**
     * Constructor
     */
    public CircuitProtocolHandler(CircuitListener listener)
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
     * process FEP Protocol Data
     * send Protocol Data to Listener
     */
    private void processingFMPProtocolMessage(IoSession session, 
            Object message) throws Exception
    { 
        byte[] protoData = (byte[])message;
        log.debug("processingFMPProtocolMessage:MSG["+Hex.decode(protoData)
                +"]");

        listener.writeMessageToRelay(protoData);
    }

    /**
     * process Circuit Data
     * response ring signal and initialize service
     */
    private void processingInitCircuitMessage(IoSession session,
            Object message) throws Exception
    {
        byte[] data = (byte[])message;
        String msg = new String(data);

        log.debug("processingInitCircuitMessage::Received Message["+
                msg+"]");

        if(msg.toUpperCase().indexOf(RINGSIGNAL) >= 0)
        {
            //session.write(FrameUtil.getByteBuffer(ATA));
            log.debug("AUTO ANSWER");
        } else if(msg.toUpperCase().indexOf(CONNECT) >= 0)
        {
            connectOk = true;
        } else if(msg.toUpperCase().indexOf(NACS) >= 0)
        {
            session.write(FrameUtil.getByteBuffer(ACCEPT));
            setIsActiveFMP(true);
            listener.startRelayService();
        }
    }

    private void checkClosedCircuit(Object message)
    {
        byte[] bx = (byte[])message;
        if(Hex.decode(bx).indexOf(CIRCUITCLOSEDMSG) >= 0)
        {
            log.debug("checkClosedCircuit:: closed Circuit Connection");
            setIsActiveFMP(false);
            listener.stopRelayService();
        }
    }

    /**
     * inherited method from ProtocolHandlerAdapter
     * handling GeneralDataFrame
     *
     * @param session <code>IoSession</code> session
     * @param message <code>Object</code> decoded GeneralDataFrame
     */
    public void messageReceived(IoSession session, Object message )
    {
        log.debug("messageReceived::isActiveFMP["+isActiveFMP+"]");
        try 
        { 
            checkClosedCircuit(message);
            if(isActiveFMP)
                processingFMPProtocolMessage(session,message);
            else
            {
                String msg = new String((byte[])message);
                String cmsg = (String)session.getAttribute("circuitmsg");
                if(msg.indexOf("\n") < 0)
                {
                    if(cmsg != null)
                        cmsg+=msg;
                    else
                        cmsg=msg;

                    session.setAttribute("circuitmsg",cmsg);
                    return;
                } else
                {
                    if(cmsg != null)
                        cmsg+=msg;
                    else
                        cmsg=msg;

                    session.setAttribute("circuitmsg","");
                    message = cmsg.getBytes();
                }
                processingInitCircuitMessage(session,message);
            }
        }catch(Exception ex)
        {
            log.error("CircuitProtocolHandler::messageReceived "
                    + " failed" ,ex);
        }
    }

    public void sessionIdle(IoSession session, IdleStatus status)
    throws Exception
    {
        log.info("IDLE COUNT : " 
                + session.getIdleCount(IdleStatus.BOTH_IDLE));
        if(!isActiveFMP)
            session.write(FrameUtil.getByteBuffer(CRLF));
    }

    /**
     * inherited method from ProtocolHandlerAdapter
     */
    public void sessionOpened(IoSession session)
    {
        log.debug("sessionOpened : " + session.getRemoteAddress());
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE,idleTime);
    }

    /**
     * inherited method from ProtocolHandlerAdapter
     */
    public void sessionClosed(IoSession session)
    {
        log.debug("Session Closed : "+ session.getRemoteAddress());
        listener.restart();
    }

    /**
     * set whether is active FEP Protocol Data or not
     */
    public void setIsActiveFMP(boolean data)
    {
        log.debug("setIsActiveFMP("+data+")");
        this.isActiveFMP = data;
    }

    /**
     * get status of active FEP Protocol Data 
     */
    public boolean getIsActiveFMP()
    {
        return this.isActiveFMP;
    }

    /**
     * wait util received Message
     */
    private void waitMsg()
    {
        synchronized(msgMonitor)
        { 
            try { msgMonitor.wait(500); 
            } catch(InterruptedException ie) {ie.printStackTrace();}
        }
    }

    /**
     * wait util received command response data and return Response
     * 
     * @param session <code>IoSession</code> session
     * @param tid <code>int</code> command request id
     * @throws FMPException
     */
    public Object getMsg(IoSession session, long timeout) 
    throws FMPException
    {
        this.msg = null;
        long stime = System.currentTimeMillis();
        long ctime = 0;
        while(session.isConnected())
        { 
            waitMsg();
            //waitResponse();
            if(this.msg != null)
                break;
            ctime = System.currentTimeMillis();
            if(((ctime - stime)/1000) > timeout)
            {
                log.debug(" waitTime["+((ctime - stime)/1000)+"] timeout["
                    + timeout+"]");
                return null;
            }
        }
        return this.msg;
    }
}
