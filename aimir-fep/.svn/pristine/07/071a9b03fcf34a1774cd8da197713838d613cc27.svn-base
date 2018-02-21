package com.aimir.fep.protocol.fmp.client.ats;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Iterator;

import com.aimir.fep.protocol.fmp.client.Client;
import com.aimir.fep.protocol.fmp.client.FMPClientProtocolHandler;
import com.aimir.fep.protocol.fmp.client.FMPClientProtocolProvider;
import com.aimir.fep.protocol.fmp.common.LANTarget;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.ServiceDataFrame;
import com.aimir.fep.protocol.fmp.frame.service.AlarmData;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.EventData;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.frame.service.RMDData;
import com.aimir.fep.protocol.fmp.frame.service.ServiceData;
import com.aimir.fep.protocol.fmp.server.FMPSslContextFactory;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.util.TimeUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 * MCU TCP Packet Client
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class ATSClient implements Client
{
    private Log log = LogFactory.getLog(ATSClient.class);
    private LANTarget target = null;
    private int CONNECT_TIMEOUT = 
        Integer.parseInt(FMPProperty.getProperty("protocol.connection.timeout", 30+"")); // seconds
    private IoConnector connector = null;
    private FMPClientProtocolProvider provider = null; 
    private IoSession session = null;
    private long MAX_MCUID = 4294967295L;

    /**
     * constructor
     * @throws IOException 
     * @throws GeneralSecurityException 
     */
    public ATSClient() throws GeneralSecurityException, IOException
    {
        init();
    }

    /**
     * constructor
     * @param target <code>TcpTarget</code> target
     * @throws IOException 
     * @throws GeneralSecurityException 
     */
    public ATSClient(String ipaddr, int port, String mcuId) throws GeneralSecurityException, IOException
    {
        this.target = new LANTarget(ipaddr, port);
        this.target.setTargetId(mcuId);
        init();
    }
    
    /**
     * constructor
     * @param target <code>TcpTarget</code> target
     * @throws IOException 
     * @throws GeneralSecurityException 
     */
    public ATSClient(LANTarget target) throws GeneralSecurityException, IOException
    {
        this.target = target;
        init();
    }

    /**
     * initialize
     * create IoProtocolConnector
     * create FMPClientProtocolProvider
     * @throws IOException 
     * @throws GeneralSecurityException 
     */
    private void init() throws GeneralSecurityException, IOException
    {
        connector = new NioSocketConnector();
        FMPSslContextFactory.setSslFilter(connector);
        provider = new FMPClientProtocolProvider();
        if (!connector.getFilterChain().contains(getClass().getName()))
            connector.getFilterChain().addLast(getClass().getName(), 
                    new ProtocolCodecFilter(provider.getCodecFactory()));
        connector.setHandler(provider.getHandler());
    }
    
    public synchronized void connect(boolean meterConnect) throws Exception
    {
        connect();
    }

    /**
     * connect to Target and wait ENQ
     *
     * @throws Exception
     */
    public synchronized void connect() throws Exception
    {
        if(session != null && session.isConnected())
            return;

        for( ;; )
        {
            try
            {
                connector.setConnectTimeoutMillis(CONNECT_TIMEOUT*1000);
                ConnectFuture future = connector.connect(new InetSocketAddress(
                        target.getIpAddr(),
                        target.getPort()));
                future.awaitUninterruptibly();
                
                if (!future.isConnected()) {
                    throw new Exception("not yet");
                }
                
                session = future.getSession();
                log.debug("SESSION CONNECTED[" + session.isConnected() + "]");
                
                break;
            }
            catch( Exception e )
            {
                /*
                try { LookupUtil.updateMCUNetworkStatus(
                        target.getMcuId(),"0");
                }catch(Exception ex){log.error(ex,ex);}
                */
                log.error( "Failed to connect. host["
                        + target.getIpAddr()+"] port["
                        + target.getPort()+"]",e );
                throw e;
            }
        }

        if(session == null)
            throw new Exception("Failed to connect. host["
                        + target.getIpAddr()+"] port["
                        + target.getPort()+"]");

        FMPClientProtocolHandler handler = 
            (FMPClientProtocolHandler)session.getHandler();
        handler.setResponseTimeout(target.getTimeout());
        log.debug("Handler timeout set["+ target.getTimeout() + "]");
        try
        {
            handler.waitENQ();
        }catch(Exception ex)
        {
            close();
            throw ex;
        }
    }

    /**
     * set Target
     *
     * @param target <code>TcpTarget</code> target
     */
    public void setTarget(Target target) throws Exception
    {
        if(!(target instanceof LANTarget))
            throw new Exception("not supported target");
        this.target = (LANTarget)target;
    }

    /**
     * request command data to Target and response 
     *
     * @param command <code>CommandData</code> request command
     * @return response <code>ServiceData</code> response
     * @throws Exception
     */
    public CommandData sendCommand(CommandData command)
        throws Exception
    {
        try {
            //ProtocolSession session = connect();
            if(session == null || !session.isConnected())
                connect();

            ServiceDataFrame frame = new ServiceDataFrame();
            frame.setSvc(GeneralDataConstants.SVC_C);
            long mcuId = Long.parseLong(target.getTargetId());
            frame.setMcuId(new UINT(mcuId));
            if(mcuId > MAX_MCUID) 
                throw new Exception("mcuId is too Big: max["
                        + MAX_MCUID+"]");
            command.setAttr(ServiceDataConstants.C_ATTR_REQUEST);
            command.setTid(FrameUtil.getCommandTid());
            frame.setSvcBody(command.encode());
            
            FMPClientProtocolHandler handler = 
                (FMPClientProtocolHandler)session.getHandler();
            ServiceData sd;
            String startTime = TimeUtil.getCurrentTime();
            long s = System.currentTimeMillis();
            sd = handler.getResponse(session,command.getTid().getValue());
            long e = System.currentTimeMillis();
            if(sd == null) 
                return null;
            log.debug("Received Response TID : " 
                    + ((CommandData)sd).getTid());
            if(((CommandData)sd).getErrCode().getValue() > 0)
            {
                log.info("Comm Result[Fail]");
            }            
            else
            {
                log.info("Comm Resut[Success]");
            }
            return (CommandData)sd;
        }
        catch (Exception e)
        {
            if(!command.getCmd().toString().equals("198.3.0"))
            {
                log.error("sendCommand failed : command["+command+"]",e);
            } else {
                log.error("sendCommand failed : command["+command.getCmd()
                        +"]",e);
            }
            throw e;
        }
        finally
        {
            close();
        }
    }

    /**
     * send Alarm to Target 
     *
     * @param alarm <code>AlarmData</code> send alarm
     * @throws Exception
     */
    public void sendAlarm(AlarmData alarm) throws Exception
    {
        try {
            // ProtocolSession session = connect();
            if (session == null || !session.isConnected())
                connect();
            ServiceDataFrame frame = new ServiceDataFrame();
            frame.setAttrByte(GeneralDataConstants.ATTR_ACK);
            frame.setAttrByte(GeneralDataConstants.ATTR_START);
            frame.setAttrByte(GeneralDataConstants.ATTR_END);
            long mcuId = Long.parseLong(target.getTargetId());
            if (mcuId > MAX_MCUID)
                throw new Exception("mcuId is too Big: max[" + MAX_MCUID + "]");
            frame.setMcuId(new UINT(mcuId));
            frame.setSvcBody(alarm.encode());
            // log.debug("alarm size : " + alarm.encode().length);
            // frame.setAttrByte(GeneralDataConstants.ATTR_COMPRESS);
            frame.setSvc(GeneralDataConstants.SVC_A);

            send(frame);

            log.info("sendAlarm : finished");
        } catch (Exception ex) {
            throw ex;
        } finally {
            close();
        }
    }

    /**
     * send Event to Target 
     *
     * @param event <code>EventData</code> event alarm
     * @throws Exception
     */
    public void sendEvent(EventData event) throws Exception
    {
        try {
            // ProtocolSession session = connect();
            if (session == null || !session.isConnected())
                connect();
            ServiceDataFrame frame = new ServiceDataFrame();
            frame.setAttrByte(GeneralDataConstants.ATTR_ACK);
            frame.setAttrByte(GeneralDataConstants.ATTR_START);
            frame.setAttrByte(GeneralDataConstants.ATTR_END);
            long mcuId = Long.parseLong(target.getTargetId());
            if (mcuId > MAX_MCUID)
                throw new Exception("mcuId is too Big: max[" + MAX_MCUID + "]");
            frame.setMcuId(new UINT(mcuId));
            frame.setSvcBody(event.encode());
            // log.debug("event size : " + event.encode().length);
            // frame.setAttrByte(GeneralDataConstants.ATTR_COMPRESS);
            frame.setSvc(GeneralDataConstants.SVC_E);

            send(frame);

            log.info("sendEvent : finished");
        } catch (Exception ex) {
            throw ex;
        } finally {
            close();
        }
    }

    /**
     * send Measurement Data to Target 
     *
     * @param md <code>MDData</code> Measurement Data
     * @throws Exception
     */
    public void sendMD(MDData md) throws Exception
    {
        try {
            // ProtocolSession session = connect();
            if (session == null || !session.isConnected())
                connect();
            ServiceDataFrame frame = new ServiceDataFrame();
            frame.setAttrByte(GeneralDataConstants.ATTR_ACK);
            frame.setAttrByte(GeneralDataConstants.ATTR_START);
            frame.setAttrByte(GeneralDataConstants.ATTR_END);
            long mcuId = Long.parseLong(target.getTargetId());
            frame.setMcuId(new UINT(mcuId));
            if (mcuId > MAX_MCUID)
                throw new Exception("mcuId is too Big: max[" + MAX_MCUID + "]");
            frame.setSvcBody(md.encode());
            // frame.setAttrByte(GeneralDataConstants.ATTR_COMPRESS);
            frame.setSvc(GeneralDataConstants.SVC_M);

            sendPartialFrame(frame);

            log.info("sendMD : finished");
        } catch (Exception ex) {
            throw ex;
        } finally {
            close();
        }
    }
    
    /**
     * send R Measurement Data to Target 
     *
     * @param md <code>RMDData</code>R Measurement Data
     * @throws Exception
     */
    public void sendRMD(RMDData rmd) throws Exception
    {
        try {
            //ProtocolSession session = connect();
            if(session == null || !session.isConnected())
                connect();
            ServiceDataFrame frame = new ServiceDataFrame();
            frame.setAttrByte(GeneralDataConstants.ATTR_ACK);
            frame.setAttrByte(GeneralDataConstants.ATTR_START);
            frame.setAttrByte(GeneralDataConstants.ATTR_END);
            long mcuId = Long.parseLong(target.getTargetId());
            frame.setMcuId(new UINT(mcuId));
            if(mcuId > MAX_MCUID) 
                throw new Exception("mcuId is too Big: max["
                        + MAX_MCUID+"]");
            frame.setSvcBody(rmd.encode());
            frame.setSvc(GeneralDataConstants.SVC_R);
    
            send(frame);
    
            log.info("sendMD : finished");
        } catch (Exception ex) {
            throw ex;
        } finally {
            close();
        }
    }

    /**
     * close TCPClient session
     */
    public void close()
    {
        close(false);
    }

    /**
     * close TCPClient session and wait completed
     *
     * @param immediately <code>boolean</code> immediately
     */
    public void close(boolean immediately)
    {
        log.debug(immediately);
        if(session != null) {
            session.write(FrameUtil.getEOT());
            CloseFuture future = session.closeNow();
            future.awaitUninterruptibly();
        }

        if(connector != null) {
            if (connector.getFilterChain().contains(getClass().getName()))
                connector.getFilterChain().remove(getClass().getName());
            connector.dispose();
        }

        session = null;
    }

    /**
     * check whether connected or not
     *
     * @return connected <code>boolean</code>
     */
    public boolean isConnected()
    {
        if(session == null)
            return false;
        return session.isConnected();
    }

    /**
     * wait ACK
     */
    private void waitAck(IoSession session, int sequence)
        throws Exception
    { 
        log.debug("waitACK.SEQ:" + sequence);
        FMPClientProtocolHandler handler = 
           (FMPClientProtocolHandler)session.getHandler(); 
        handler.waitAck(session,sequence);
    }

    /**
     * send GeneralDataFrame to Target
     *
     * @param frame <code>GeneralDataFrame</code>
    private synchronized void send(GeneralDataFrame frame) 
        throws Exception
    { 
        byte[] bx = frame.encode(); 
        byte[] mbx = null;
        ByteBuffer buf = null;
        ArrayList framelist = FrameUtil.makeMultiEncodedFrame(bx); 
        session.setAttribute("sendframes",framelist); 
        int lastIdx = framelist.size() - 1;
        mbx = (byte[])framelist.get(lastIdx);
        int lastSequence = DataUtil.getIntToByte(mbx[1]);
        boolean isSetLastSequence = false;
        if((lastIdx / GeneralDataConstants.FRAME_MAX_SEQ) < 1)
        {
            session.setAttribute("lastSequence", 
                    new Integer(lastSequence)); 
            isSetLastSequence = true;
        }
        Iterator iter = framelist.iterator(); 
        int cnt = 0; 
        int seq = 0;
        ControlDataFrame wck = null;
        while(iter.hasNext()) 
        { 
            if(!isSetLastSequence && 
                    ((lastIdx - cnt) / 
                     GeneralDataConstants.FRAME_MAX_SEQ) < 1)
            {
                session.setAttribute("lastSequence", 
                        new Integer(lastSequence));
                isSetLastSequence = true;
            }
            mbx = (byte[])iter.next();
            seq = DataUtil.getIntToByte(mbx[1]);
            buf = ByteBuffer.allocate(mbx.length);
            buf.put(mbx,0,mbx.length);
            buf.flip();
            session.write(buf); 
            FrameUtil.waitSendFrameInterval();
            if(((cnt+1) % GeneralDataConstants.FRAME_WINSIZE)== 0) 
            {
                log.debug("WCK : start : " + (seq - 
                    GeneralDataConstants.FRAME_WINSIZE + 1)
                        +"end : " + seq);
                wck =FrameUtil.getWCK((seq-
                            GeneralDataConstants.FRAME_WINSIZE + 1), 
                        seq);
                session.write(wck);
                session.setAttribute("wck",wck);
                waitAck(session,cnt); 
            }
            cnt++; 
        } 
        if(frame.getSvc() != GeneralDataConstants.SVC_C) 
        { 
            if((cnt % GeneralDataConstants.FRAME_WINSIZE) != 0) 
            { 
                if(cnt > 1)
                {
                    log.debug("WCK : start : " + (seq -(seq%
                        GeneralDataConstants.FRAME_WINSIZE))
                            + "end : " + seq);
                    wck =FrameUtil.getWCK(seq-(seq%
                                GeneralDataConstants.FRAME_WINSIZE)
                            ,seq); 
                    session.write(wck);
                    session.setAttribute("wck",wck);
                }
                waitAck(session,cnt-1); 
            }
        }
        FrameUtil.waitSendFrameInterval();
        session.removeAttribute("wck");
    }
    */
    /**
     * send GeneralDataFrame to Target
     *
     * @param frame <code>GeneralDataFrame</code>
     */
    private synchronized int send(GeneralDataFrame frame) 
        throws Exception
    { 
        int sendBytes = 0;
        byte[] bx = frame.encode(); 
        byte[] mbx = null;
        IoBuffer buf = null;
        ArrayList<?> framelist = FrameUtil.makeMultiEncodedFrame(bx, session); 
        session.setAttribute("sendframes",framelist); 
        int lastIdx = framelist.size() - 1;
        mbx = (byte[])framelist.get(lastIdx);
        int lastSequence = DataUtil.getIntToByte(mbx[1]);
        boolean isSetLastSequence = false;
        if((lastIdx / GeneralDataConstants.FRAME_MAX_SEQ) < 1)
        {
            session.setAttribute("lastSequence", 
                    new Integer(lastSequence)); 
            isSetLastSequence = true;
        }
        Iterator iter = framelist.iterator(); 
        int cnt = 0; 
        int seq = 0;
        ControlDataFrame wck = null;
        while(iter.hasNext()) 
        { 
            if(!isSetLastSequence && 
                    ((lastIdx - cnt) / 
                     GeneralDataConstants.FRAME_MAX_SEQ) < 1)
            {
                session.setAttribute("lastSequence", 
                        new Integer(lastSequence));
                isSetLastSequence = true;
            }
            mbx = (byte[])iter.next();
            sendBytes+=mbx.length;
            seq = DataUtil.getIntToByte(mbx[1]);
            buf = IoBuffer.allocate(mbx.length);
            buf.put(mbx,0,mbx.length);
            buf.flip();
            session.write(buf); 
            //FrameUtil.waitSendFrameInterval();
            if(((cnt+1) % GeneralDataConstants.FRAME_WINSIZE)== 0) 
            {
                log.debug("WCK : start : " + (seq - 
                    GeneralDataConstants.FRAME_WINSIZE + 1)
                        +"end : " + seq);
                wck =FrameUtil.getWCK((seq-
                            GeneralDataConstants.FRAME_WINSIZE + 1), 
                        seq);
                sendBytes+=GeneralDataConstants.HEADER_LEN
                    + GeneralDataConstants.TAIL_LEN
                    + wck.getArg().getValue().length;
                session.write(wck);
                session.setAttribute("wck",wck);
                waitAck(session,cnt); 
            }
            cnt++; 
        } 
        //if(frame.getSvc() != GeneralDataConstants.SVC_C) 
        //{ 
        if((cnt % GeneralDataConstants.FRAME_WINSIZE) != 0) 
        { 
            if(cnt > 1)
            {
                log.debug("WCK : start : " + (seq -(seq% 
                                GeneralDataConstants.FRAME_WINSIZE)) 
                        + "end : " + seq); 
                wck =FrameUtil.getWCK(seq-(seq% 
                            GeneralDataConstants.FRAME_WINSIZE) ,seq); 
                sendBytes+=GeneralDataConstants.HEADER_LEN 
                    + GeneralDataConstants.TAIL_LEN 
                    + wck.getArg().getValue().length; 
                session.write(wck); 
                session.setAttribute("wck",wck); 
                waitAck(session,cnt-1); 
            } 
        }
        //}
        //FrameUtil.waitSendFrameInterval();
        session.removeAttribute("wck");
        return sendBytes;
    }
    
    /**
     * send GeneralDataFrame to Target
     *
     * @param frame <code>GeneralDataFrame</code>
     */
    private synchronized int sendPartialFrame(GeneralDataFrame frame) 
        throws Exception
    { 
        int sendBytes = 0;
        byte[] bx = frame.encode(); 
        byte[] mbx = null;
        IoBuffer buf = null;
        ArrayList framelist = FrameUtil.makePartialFrame(bx); 
        session.setAttribute("sendframes",framelist); 
        int lastIdx = framelist.size() - 1;
        mbx = (byte[])framelist.get(lastIdx);
        int lastSequence = DataUtil.getIntToByte(mbx[1]);
        boolean isSetLastSequence = false;
        if((lastIdx / GeneralDataConstants.FRAME_MAX_SEQ) < 1)
        {
            session.setAttribute("lastSequence", 
                    new Integer(lastSequence)); 
            isSetLastSequence = true;
        }
        Iterator iter = framelist.iterator(); 
        int cnt = 0; 
        int seq = 0;
        ControlDataFrame wck = null;
        while(iter.hasNext()) 
        { 
            if(!isSetLastSequence && 
                    ((lastIdx - cnt) / 
                     GeneralDataConstants.FRAME_MAX_SEQ) < 1)
            {
                session.setAttribute("lastSequence", 
                        new Integer(lastSequence));
                isSetLastSequence = true;
            }
            mbx = (byte[])iter.next();
            sendBytes+=mbx.length;
            seq = DataUtil.getIntToByte(mbx[1]);
            buf = IoBuffer.allocate(mbx.length);
            buf.put(mbx,0,mbx.length);
            buf.flip();
            session.write(buf); 
            //FrameUtil.waitSendFrameInterval();
            if(((cnt+1) % GeneralDataConstants.FRAME_WINSIZE)== 0) 
            {
                log.debug("WCK : start : " + (seq - 
                    GeneralDataConstants.FRAME_WINSIZE + 1)
                        +"end : " + seq);
                wck =FrameUtil.getWCK((seq-
                            GeneralDataConstants.FRAME_WINSIZE + 1), 
                        seq);
                sendBytes+=GeneralDataConstants.HEADER_LEN
                    + GeneralDataConstants.TAIL_LEN
                    + wck.getArg().getValue().length;
                session.write(wck);
                session.setAttribute("wck",wck);
                waitAck(session,cnt); 
            }
            cnt++; 
        } 
        //if(frame.getSvc() != GeneralDataConstants.SVC_C) 
        //{ 
        if((cnt % GeneralDataConstants.FRAME_WINSIZE) != 0) 
        { 
            if(cnt > 1)
            {
                log.debug("WCK : start : " + (seq -(seq% 
                                GeneralDataConstants.FRAME_WINSIZE)) 
                        + "end : " + seq); 
                wck =FrameUtil.getWCK(seq-(seq% 
                            GeneralDataConstants.FRAME_WINSIZE) ,seq); 
                sendBytes+=GeneralDataConstants.HEADER_LEN 
                    + GeneralDataConstants.TAIL_LEN 
                    + wck.getArg().getValue().length; 
                session.write(wck); 
                session.setAttribute("wck",wck); 
                waitAck(session,cnt-1); 
            } 
        }
        //}
        //FrameUtil.waitSendFrameInterval();
        session.removeAttribute("wck");
        return sendBytes;
    }
}