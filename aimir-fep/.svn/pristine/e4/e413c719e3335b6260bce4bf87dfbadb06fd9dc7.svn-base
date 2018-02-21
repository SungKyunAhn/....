package com.aimir.fep.protocol.mrp.client.lan;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.protocol.fmp.client.Client;
import com.aimir.fep.protocol.fmp.common.LANTarget;
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
import com.aimir.fep.protocol.mrp.MeterProtocolFactory;
import com.aimir.fep.protocol.mrp.MeterProtocolHandler;
import com.aimir.fep.protocol.mrp.client.MRPClientProtocolHandler;
import com.aimir.fep.protocol.mrp.client.MRPClientProtocolProvider;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.MIBUtil;
import com.aimir.model.device.CommLog;
import com.aimir.util.TimeUtil;

public class LANMMIUClient implements Client{

    private Log log = LogFactory.getLog(LANMMIUClient.class);
    private LANTarget target = null;
    private int CONNECT_TIMEOUT = 
        Integer.parseInt(FMPProperty.getProperty("protocol.connection.timeout", 30+"")); // seconds
    private IoConnector connector = null;
    private MRPClientProtocolProvider provider = null; 
    private IoSession session = null;
    private long MAX_MCUID = 4294967295L;
    private ProcessorHandler logProcessor = null;
    private Integer activatorType = new Integer(
            FMPProperty.getProperty("protocol.system.FEP","1"));
    private Integer targetType = new Integer(
            FMPProperty.getProperty("protocol.system.MCU","2"));
    private Integer protocolType = new Integer(
            FMPProperty.getProperty("protocol.type.LAN", CommonConstants.getProtocolCode(Protocol.LAN)+""));
    
    /**
     * constructor
     * @throws IOException 
     * @throws GeneralSecurityException 
     */
    public LANMMIUClient() throws GeneralSecurityException, IOException
    {
    }
    
    /**
     * constructor
     * @param target <code>TcpTarget</code> target
     * @throws IOException 
     * @throws GeneralSecurityException 
     */
    public LANMMIUClient(LANTarget target) throws GeneralSecurityException, IOException
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
        provider = new MRPClientProtocolProvider();
        if (!connector.getFilterChain().contains(getClass().getName()))
            connector.getFilterChain().addLast(getClass().getName(), 
                    new ProtocolCodecFilter(provider.getCodecFactory()));
        connector.setHandler(provider.getHandler());
    }
    
    /**
     * connect to Target
     *
     * @throws Exception
     */
    private synchronized void connect() throws Exception
    {
        init();
        
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

        /*
        MRPClientProtocolHandler handler = 
            (MRPClientProtocolHandler)session.getHandler();
        log.debug("Handler timeout set["+ target.getTimeout() + "]");
        try
        {
            handler.setResponseTimeout(target.getTimeout());
        }catch(Exception ex)
        {
            close();
            throw ex;
        }
        */
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
            CommLog commLog = new CommLog();
            ServiceDataFrame frame = new ServiceDataFrame();
            frame.setSvc(GeneralDataConstants.SVC_C);
            /* 집중기 아이디가 꼭 숫자형일 수는 없기 때문에 
            long mcuId = Long.parseLong(target.getTargetId());
            frame.setMcuId(new UINT(mcuId));
            if(mcuId > MAX_MCUID) 
                throw new Exception("mcuId is too Big: max["
                        + MAX_MCUID+"]");
                        */
            command.setAttr(ServiceDataConstants.C_ATTR_REQUEST);
            command.setTid(FrameUtil.getCommandTid());
            frame.setSvcBody(command.encode());
            
			MRPClientProtocolHandler mrp = (MRPClientProtocolHandler) session
			.getHandler();
			mrp.setResponseTimeout(target.getTimeout());

			MeterProtocolHandler handler = MeterProtocolFactory.getHandler(
			target.getIpAddr(), target.getMeterModel(), target
					.getGroupNumber(), target.getMemberNumber(), target
					.getFwVer());
            ServiceData sd;
            String startTime = TimeUtil.getCurrentTime();
            //TODO 데이터타입 추가.
            commLog.setSuppliedId(target.getSupplierId());
            commLog.setStartDate(startTime.substring(0,8));
            commLog.setStartTime(startTime.substring(8,14));
            commLog.setStartDateTime(startTime);
            commLog.setProtocolCode(CommonConstants.getProtocol(protocolType+""));
            commLog.setSenderTypeCode(CommonConstants.getSenderReceiver(activatorType+""));
            commLog.setSenderId(target.getTargetId());
            commLog.setSenderIp(DataUtil.getFepIdString());
            commLog.setReceiverTypeCode(CommonConstants.getSenderReceiver(targetType+""));
            commLog.setReceiverId(target.getTargetId());
            commLog.setOperationCode(MIBUtil.getInstance().getName(
                        command.getCmd().toString()));
            long s = System.currentTimeMillis();
            commLog.setSendBytes((int)session.getWrittenBytes());
            sd = handler.execute(mrp, session, command);
            long e = System.currentTimeMillis();
            if(sd == null) 
                return null;
            log.debug("Received Response TID : " 
                    + ((CommandData)sd).getTid());
            commLog.setEndTime(TimeUtil.getCurrentTime());
            commLog.setRcvBytes((int)session.getReadBytes());
            commLog.setUnconPressedRcvBytes(sd.getTotalLength());
            commLog.setTotalCommTime((int)(e-s));
            commLog.setSvcTypeCode(CommonConstants.getHeaderSvc("C"));
            if(((CommandData)sd).getErrCode().getValue() > 0)
            {
                commLog.setCommResult(0);
                commLog.setDescr(ErrorCode.getMessage(((CommandData)sd).getErrCode().getValue()));
            }            
            else
            {
                commLog.setCommResult(1);
            }
            commLog.setSuppliedId(target.getSupplierId());
            log.info(commLog.toString());
            saveCommLog(commLog);
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

			send(frame);

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
    /*
    private void waitAck(IoSession session, int sequence)
        throws Exception
    { 
        log.debug("waitACK.SEQ:" + sequence);
        MRPClientProtocolHandler handler = 
           (MRPClientProtocolHandler)session.getHandler(); 
        handler.waitAck(session,sequence);
    }
    */

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
        Iterator<?> iter = framelist.iterator(); 
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
                //session.write(wck);
                //session.setAttribute("wck",wck);
                //waitAck(session,cnt); 
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
                //session.write(wck); 
                //session.setAttribute("wck",wck); 
                //waitAck(session,cnt-1); 
            } 
        }
        //}
        //FrameUtil.waitSendFrameInterval();
        session.removeAttribute("wck");
        return sendBytes;
    }
}
