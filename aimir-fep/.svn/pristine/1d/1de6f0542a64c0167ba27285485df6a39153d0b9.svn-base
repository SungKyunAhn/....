package com.aimir.fep.protocol.fmp.client.pstn;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Iterator;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.protocol.fmp.client.Client;
import com.aimir.fep.protocol.fmp.client.FMPClientProtocolHandler;
import com.aimir.fep.protocol.fmp.client.FMPClientProtocolProvider;
import com.aimir.fep.protocol.fmp.client.FMPClientResource;
import com.aimir.fep.protocol.fmp.client.FMPClientResourceFactory;
import com.aimir.fep.protocol.fmp.client.resource.ts.TerminalServerPort;
import com.aimir.fep.protocol.fmp.common.CircuitTarget;
import com.aimir.fep.protocol.fmp.common.PSTNTarget;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.ErrorCode;
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
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.protocol.fmp.server.FMPSslContextFactory;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.MIBUtil;
import com.aimir.model.device.CommLog;
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
 * MCU PSTN Packet Client
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class PSTNClient implements Client
{
    private Log log = LogFactory.getLog(PSTNClient.class);
    private PSTNTarget target = null;
    private int CONNECT_TIMEOUT = 
        Integer.parseInt(FMPProperty.getProperty("protocol.connection.timeout", 30+"")); // seconds
    private IoConnector connector = null;
    private FMPClientProtocolProvider provider = null; 
    private IoSession session = null;
    private long MAX_MCUID = 4294967295L;
    private ProcessorHandler logProcessor = null;
    private Integer activatorType = new Integer(
            FMPProperty.getProperty("protocol.system.FEP","1"));
    private Integer targetType = new Integer(
            FMPProperty.getProperty("protocol.system.MCU","2"));
    private Integer protocolType = new Integer(
            FMPProperty.getProperty("protocol.type.PSTN",CommonConstants.getProtocolCode(Protocol.PSTN)+""));

    /**
     * constructor
     * @throws IOException 
     * @throws GeneralSecurityException 
     */
    public PSTNClient() throws GeneralSecurityException, IOException
    {
    }

    /**
     * constructor
     * @param target <code>PSTNTarget</code> target
     * @throws IOException 
     * @throws GeneralSecurityException 
     */
    public PSTNClient(PSTNTarget target) throws GeneralSecurityException, IOException
    {
        this.target = target;
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

    private synchronized void connect(boolean meterConnect) throws Exception
    {
    	connect();
    }
    
    /**
     * connect to Target and wait ENQ
     *
     * @throws Exception
     */
    private synchronized void connect() throws Exception
    {
        init();
        
        if(session != null && session.isConnected())
            return;

        String ipaddr = null;
        int port = 1;

        int connMode = 1;
        try
        {
            connMode = Integer.parseInt(
                    FMPProperty.getProperty("Protocol.Circuit.Connection.Mode",FMPClientResource.TERMINAL_SERVER+""));
        } catch(Exception ex) { 
            throw new Exception("Can not find Circuit Connection Mode"); 
        }

        log.debug("PSTNClient Circuit Connection Mode["+connMode+"]");

        FMPClientResource resource = null;
        Object resourceObj = null;
        if(connMode == FMPClientResource.TERMINAL_SERVER)
        {
            resource = FMPClientResourceFactory.getResource(connMode);
            log.debug("##### PSTNClient TS : Acquire ######");
            if(target.getLocation() != null && !"".equals(target.getLocation().getName()))
            {
            	 resourceObj = resource.acquire(target.getLocation().getId());
            }
            else
            {
            	resourceObj = resource.acquire();
            }
            TerminalServerPort tsport = (TerminalServerPort)resourceObj;
            ipaddr = tsport.getIpAddr();
            port = tsport.getPort();
            log.debug("PSTNClient TS : ipaddr["+ipaddr+"] port["+port+"]");
            log.debug("PSTNClient: "+resource.getActiveResourcesString());
            log.debug("PSTNClient: "+resource.getIdleResourcesString());
        }
        else if(connMode == FMPClientResource.SERIAL_PORT)
        {
            throw new Exception(" Not Support Curcuit Connection Mode[Serial]");
        }
        else
        {
            throw new Exception(" Not Support Curcuit Connection Mode["
                    +connMode+"]");
        }

        int rty = 0;

        for( ;; )
        {
            rty++;
            try
            {
                connector.setConnectTimeoutMillis(CONNECT_TIMEOUT*1000);
                ConnectFuture future = connector.connect(new InetSocketAddress(
                        target.getIpaddr(),
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
                if(rty < 2)
                {
                    try{ Thread.sleep(5*1000); } catch(Exception ignore) {}
                    continue;
                }

                if(resource != null && resourceObj != null)
                {
                    try {
                        resource.release(resourceObj);
                        log.debug("PSTNClient: "
                                +resource.getActiveResourcesString());
                        log.debug("PSTNClient: "
                                +resource.getIdleResourcesString());
                    }catch(Exception ignoreEx) {}
                }
                log.error( "Failed to connect. host["
                        + ipaddr +"] port[" + port + "]",e );
                throw e;
            }
        }

        if(session == null)
            throw new Exception("Failed to connect. host["
                        + ipaddr +"] port[" + port +"]");

        FMPClientProtocolHandler handler = 
            (FMPClientProtocolHandler)session.getHandler();
        try
        {
            handler.setFMPResource(resource,resourceObj);
            handler.setResponseTimeout(target.getTimeout());

            handler.initCircuit(session,(CircuitTarget)target);
            log.debug("PSTNClient initCircuit End");

            handler.waitENQ();
            log.debug("PSTNClient waitENQ End");
        }catch(Exception ex)
        {
            close();
            throw ex;
        }
    }

    /**
     * set Target
     *
     * @param target <code>PSTNTarget</code> target
     */
    public void setTarget(Target target) throws Exception
    {
        if(!(target instanceof PSTNTarget))
            throw new Exception("not supported target");
        this.target = (PSTNTarget)target;
    }

    private void saveCommLog(CommLog commLog)
    {
        try
        {
            if(this.logProcessor != null)
                this.logProcessor.putServiceData(ProcessorHandler.LOG_COMMLOG, commLog);
            else
                log.warn("Log Processor not registered");
        } catch(Exception ex)
        {
            log.error("save Communication Log failed",ex);
        }
    }

    public void setLogProcessor(ProcessorHandler logProcessor)
    {
        this.logProcessor = logProcessor;
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
        //ProtocolSession session = connect();
        try {
            if(session == null || !session.isConnected())
                connect();
            CommLog commLog = new CommLog();
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
            commLog.setStartDate(startTime.substring(0,8));
            commLog.setStartTime(startTime.substring(8,14));
            commLog.setProtocolCode(CommonConstants.getProtocol(protocolType+""));
            commLog.setSenderTypeCode(CommonConstants.getSenderReceiver(activatorType+""));
            commLog.setSenderId(DataUtil.getFepIdString());
            commLog.setReceiverTypeCode(CommonConstants.getSenderReceiver(targetType+""));
            commLog.setReceiverId(target.getTargetId());
            commLog.setOperationCode(MIBUtil.getInstance().getName(
                        command.getCmd().toString()));
            long s = System.currentTimeMillis();
            commLog.setSendBytes(send(frame));
            sd = handler.getResponse(session,command.getTid().getValue());
            long e = System.currentTimeMillis();
            if(sd == null) 
                return null;
            log.debug("Received Response TID : " 
                    + ((CommandData)sd).getTid());
            commLog.setEndTime(TimeUtil.getCurrentTime());
            commLog.setRcvBytes(sd.getTotalLength());
            commLog.setUnconPressedRcvBytes(sd.getTotalLength());
            if(((CommandData)sd).getErrCode().getValue() > 0)
            {
                commLog.setCommResult(0);
                commLog.setDescr(ErrorCode.getMessage(((CommandData)sd).getErrCode().getValue()));
            }            
            else
            {
                commLog.setCommResult(1);
            }
            commLog.setTotalCommTime((int)(e-s));
            commLog.setSvcTypeCode(CommonConstants.getHeaderSvc(Byte.toString(sd.getSvc())));
            log.info(commLog.toString());
            //commonLogger.sendCommLog(commLog);
            saveCommLog(commLog);
            return (CommandData)sd;
        }
        catch(Exception ex)
        {
            log.error("sendCommand failed : command["+command+"]",ex);
            throw ex;
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
        frame.setSvcBody(alarm.encode());
        //log.debug("alarm size : " + alarm.encode().length);
        //frame.setAttrByte(GeneralDataConstants.ATTR_COMPRESS);
        frame.setSvc(GeneralDataConstants.SVC_A);

        send(frame);

        log.info("sendAlarm : finished");
    }

    /**
     * send Event to Target 
     *
     * @param event <code>EventData</code> event alarm
     * @throws Exception
     */
    public void sendEvent(EventData event) throws Exception
    {
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
        frame.setSvcBody(event.encode());
        //log.debug("event size : " + event.encode().length);
        //frame.setAttrByte(GeneralDataConstants.ATTR_COMPRESS);
        frame.setSvc(GeneralDataConstants.SVC_E);

        send(frame);

        log.info("sendEvent : finished");
    }

    /**
     * send Measurement Data to Target 
     *
     * @param md <code>MDData</code> Measurement Data
     * @throws Exception
     */
    public void sendMD(MDData md) throws Exception
    {
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
        frame.setSvcBody(md.encode());
        //frame.setAttrByte(GeneralDataConstants.ATTR_COMPRESS);
        frame.setSvc(GeneralDataConstants.SVC_M);

        send(frame);

        log.info("sendMD : finished");
    }
    
    /**
     * send R Measurement Data to Target 
     *
     * @param md <code>RMDData</code>R Measurement Data
     * @throws Exception
     */
    public void sendRMD(RMDData rmd) throws Exception
    {
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
    }

    /**
     * close PSTNClient session
     */
    public void close()
    {
        close(true);
    }

    /**
     * close PSTNClient session and immediately completed
     *
     * @param immediately <code>boolean</code> immediately
     */
    public void close(boolean immediately)
    {
        log.debug(immediately);
        
        FMPClientProtocolHandler handler = 
            (FMPClientProtocolHandler)session.getHandler();
        handler.closeCircuit(session);
        
        if(session != null) {
            session.write(FrameUtil.getEOT());
            CloseFuture future = session.closeNow();
            future.awaitUninterruptibly();
        }

        if(connector != null) {
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
                            GeneralDataConstants.FRAME_WINSIZE) 
                        ,seq); 
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
