package com.aimir.fep.protocol.fmp.gateway.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.aimir.fep.protocol.fmp.exception.FMPException;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;

/**
 * {@link GSMServiceProtocolHandler}  Curcuit Data 
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2006-10-16 15:59:15 +0900 $,
 */
public class GSMServiceProtocolHandler extends IoHandlerAdapter
{
    private static Log log = LogFactory.getLog(GSMServiceProtocolHandler.class);

    private Object msgMonitor = new Object();
    private Object msg = null;

    private boolean isActiveFMP = false;
    private boolean activeRing = true;
    private int sessionStatus = 0;
    private int idleCount = 0;


    /**
     * inherited method from ProtocolHandlerAdapter
     */
    public void exceptionCaught(IoSession session, Throwable cause )
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
    public void messageReceived(IoSession session, Object message )
    {
        log.debug("messageReceived::IsActiveFMP["+isActiveFMP+"]");
        try 
        { 
            String msg = new String((byte[])message);
            if(isActiveFMP)
            {
                log.debug("messageReceived::HEX["
                        +Hex.decode((byte[])message)+"]");
                byte[] EOT = Hex.encode("5E0080040000000005010000D18D");
                session.write(EOT);
                isActiveFMP = false;
                this.activeRing = true;
            }
            else
            {
                log.debug("messageReceived::MSG["+msg+"]");
                if(msg.toUpperCase().indexOf("ATA") >= 0)
                {
                    this.activeRing = false;
                    sleep(5);
                    session.write(FrameUtil.getByteBuffer(CRLF+"CONNECT/RLP ... "+CRLF));
                    sleep(1);
                    session.write(FrameUtil.getByteBuffer(CRLF+"NURI/1.0 NACS 8000 "+CRLF));
                    return;
                }

                if(msg.toUpperCase().indexOf("ACCEPT") >= 0)
                {
                    isActiveFMP = true;
                }
            }

        }catch(Exception ex)
        {
            log.error("GSMServiceProtocolHandler::messageReceived "
                    + " failed" ,ex);
        }
    }

    public void sessionIdle(IoSession session, IdleStatus status)
    throws Exception
    {
        log.info("IDLE COUNT : " 
                + session.getIdleCount(IdleStatus.READER_IDLE)
                +", idleCount:"+idleCount);
        if(activeRing)
        {
            idleCount++;
            if(idleCount > 10)
                session.write(FrameUtil.getByteBuffer(CRLF+"RING"+CRLF));
        } else
        {
            idleCount = 0;
        }
    }

    /**
     * inherited method from ProtocolHandlerAdapter
     */
    public void sessionOpened(IoSession session)
    {
        this.sessionStatus = 1;
        session.getConfig().setIdleTime(IdleStatus.READER_IDLE,5);
        log.debug("sessionOpened : " + session.getRemoteAddress());
    }

    /**
     * inherited method from ProtocolHandlerAdapter
     */
    public void sessionClosed(IoSession session)
    {
        log.debug("Session Closed : "+ session.getRemoteAddress());
        this.sessionStatus = 0;
    }


    public void setIsActiveFMP(boolean data)
    {
        log.debug("setIsActiveFMP("+data+")");
        this.isActiveFMP = data;
    }

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
                //throw new FMPACKTimeoutException(" Msg Receive Timeout["
                //        +timeout +"]");
            }
        }
        return this.msg;
    }

    private void sleep(int seconds)  throws Exception
    {
        Thread.sleep(seconds*1000); 
    }

    private final String CRLF="\r\n";
    private void initCircuit(IoSession session) throws Exception
    {
        Object recvObj;
        for(int i = 0 ; i < 30 ; i++)
        {
            log.debug("RING COUNT["+i+"]");
            session.write(FrameUtil.getByteBuffer(CRLF+"RING"+CRLF));
            recvObj = getMsg(session,1);
            if(recvObj != null)
            {
                String recvMsg = new String((byte[])recvObj);
                if(recvMsg.toUpperCase().indexOf(CRLF+"ATA"+CRLF) >= 0)
                {
                    sleep(40);
                    session.write(FrameUtil.getByteBuffer(CRLF+"CONNECT/RLP ... "+CRLF));
                    break;
                }
            }
        }
        sleep(1);
        session.write(FrameUtil.getByteBuffer(CRLF+"NURI/1.0 NACS 8000 "+CRLF));

        recvObj = getMsg(session,10);
        if(recvObj != null)
            log.info("DATA["+Hex.decode((byte[])recvObj)+"]");
        recvObj = getMsg(session,10);
        if(recvObj != null)
            log.info("DATA["+Hex.decode((byte[])recvObj)+"]");
        
        byte[] EOT = Hex.encode("5E0080040000000005010000D18D");
        session.write(EOT);
    }
}
