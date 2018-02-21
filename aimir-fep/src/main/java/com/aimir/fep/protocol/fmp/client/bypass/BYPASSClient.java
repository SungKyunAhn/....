package com.aimir.fep.protocol.fmp.client.bypass;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.bypass.BypassDevice;
import com.aimir.fep.bypass.decofactory.protocolfactory.BypassFrameFactory;
import com.aimir.fep.protocol.fmp.client.Client;
import com.aimir.fep.protocol.fmp.common.BypassTarget;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.ErrorCode;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.protocol.fmp.frame.ServiceDataFrame;
import com.aimir.fep.protocol.fmp.frame.service.AlarmData;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.EventData;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.frame.service.RMDData;
import com.aimir.fep.protocol.fmp.frame.service.ServiceData;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.protocol.fmp.server.FMPSslContextFactory;
import com.aimir.fep.util.CmdUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.model.device.CommLog;

public class BYPASSClient implements Client 
{
    private Log log = LogFactory.getLog(BYPASSClient.class);
    private BypassTarget target = null;
    private int CONNECT_TIMEOUT = 
            Integer.parseInt(FMPProperty.getProperty("protocol.connection.timeout", 30+"")); // seconds
    private IoConnector connector = null;
    private BypassClientProtocolProvider provider = null;     
    private IoSession session = null;
    private long MAX_MCUID = 4294967295L;
    private ProcessorHandler logProcessor = DataUtil.getBean(ProcessorHandler.class);
    
    private Integer activatorType = new Integer(
            FMPProperty.getProperty("protocol.system.FEP","1"));
    private Integer targetType = new Integer(
            FMPProperty.getProperty("protocol.system.MCU","2"));
    private Integer protocolType = new Integer(
            FMPProperty.getProperty("protocol.type.GPRS",CommonConstants.getProtocolCode(Protocol.GPRS)+""));
    
    /**
     * constructor
     * @throws GeneralSecurityException 
     * @throws IOException 
     */
    public BYPASSClient() throws GeneralSecurityException, IOException, Exception
    {
    }

    /**
     * constructor
     * @param target <code>BypassTarget</code> target
     * @throws GeneralSecurityException 
     * @throws IOException 
     */
    public BYPASSClient(BypassTarget target) throws GeneralSecurityException, IOException, Exception
    {	
    	this.target = target;
    	setProtocol();
    }
    
    /**
     * initialize
     * create IoProtocolConnector
     * create FMPClientProtocolProvider
     * @throws GeneralSecurityException 
     * @throws IOException 
     * @throws IOException 
     */
    private void init() throws GeneralSecurityException, IOException
    {
        connector = new NioSocketConnector();
        FMPSslContextFactory.setSslFilter(connector);
        provider = new BypassClientProtocolProvider();        
        connector.setConnectTimeoutMillis(CONNECT_TIMEOUT*1000);
        connector.getFilterChain().addLast(getClass().getName(), 
                new ProtocolCodecFilter(provider.getCodecFactory()));
        connector.setHandler(provider.getHandler());
    }
    
    /**
     * connect to Target and wait ENQ
     *
     * @throws Exception
     */
    private synchronized void connect() throws Exception
    {
        init();
        
        log.debug("Bypass connect port : "+target.getPort());
        if(session != null && session.isConnected()) {
            return;
        }
        
        for( ;; )
        {
            try
            {
                ConnectFuture future = connector.connect(new InetSocketAddress(
                        target.getIpAddr(),
                        target.getPort()));
                future.awaitUninterruptibly();
                
                if (!future.isConnected()) {
                    //throw new Exception("not yet");
                	throw new Exception(ErrorCode.getMessage(ErrorCode.MCU_CANNOT_CONNECT));
                }
                session = future.getSession();
                session.getConfig().setWriteTimeout(CONNECT_TIMEOUT);
                session.getConfig().setIdleTime(IdleStatus.READER_IDLE,
                        CONNECT_TIMEOUT);
                session.setAttribute("nameSpace", target.getNameSpace() != null ? target.getNameSpace() : null);
                log.debug("SESSION CONNECTED[" + session.isConnected() + "]");
                log.debug("nameSpace[" + session.getAttribute("nameSpace") + "]");
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
    }
    
	@Override
	public void setTarget(Target target) throws Exception {
        if(!(target instanceof BypassTarget))
            throw new Exception("not supported target");
        this.target = (BypassTarget)target;
	}

	@Override
	public CommandData sendCommand(CommandData command) throws Exception {
		 return null;
	}
	
	public CommandData sendBypass(IoSession session, BypassFrameFactory gdFactory) throws Exception
	{
        BypassClientHandler handler = 
                (BypassClientHandler)session.getHandler();
        
    	gdFactory.start(session, null);	
		ServiceData sd = handler.getResponse(session);
		return (CommandData)sd;
	}
	
	public void cmdSetBypassStart() throws Exception {
	
		BypassDevice bd = (BypassDevice) session.getAttribute(session.getRemoteAddress());
		int timeout = 10;
		
		if (bd.getArgMap() != null && !bd.getArgMap().get("timeout").equals("")) {
			timeout = Integer.parseInt((String)bd.getArgMap().get("timeout"));
		}
		
		String ns = (String) session.getAttribute("nameSpace");
		List<SMIValue> params = new ArrayList<SMIValue>();
		params.add(DataUtil.getSMIValueByObject(ns, "cmdSetBypassStartTimeout", Integer.toString(timeout)));
	}
	
	@Override
	public void sendAlarm(AlarmData alarm) throws Exception {
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

	@Override
	public void sendEvent(EventData event) throws Exception {
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

	@Override
	public void sendMD(MDData mdData) throws Exception {
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
			frame.setSvcBody(mdData.encode());
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

	@Override
	public void sendRMD(RMDData rmdData) throws Exception {
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
            frame.setSvcBody(rmdData.encode());
            frame.setSvc(GeneralDataConstants.SVC_R);
    
            send(frame);
    
            log.info("sendMD : finished");
        } catch (Exception ex) {
            throw ex;
        } finally {
            close();
        }		
	}

	@Override
	public boolean isConnected() {
        if(session == null)
            return false;
        return session.isConnected();
	}

	@Override
	public void close() {
        // 큐가 다 빌 때까지 대기했다가 끊는다.
        close(false);
	}

	@Override
	public void close(boolean wait) {
        log.debug(wait);
        if(session != null && !session.isClosing()) {
            session.write(FrameUtil.getEOT());
            CloseFuture future = session.closeNow();
            future.awaitUninterruptibly();
        }

        if(connector != null) {
            connector.dispose();
        }

        session = null;
		
	}
	
    public void setProtocol() throws Exception 
    {
    	if(target == null) {    		    		
    		throw new NullPointerException();
    	}
    
    	Protocol mProtocol = null;
    	
    	if((Protocol.BYPASS).equals(target.getProtocol())) {    		
    		Target mtarget = CmdUtil.getTarget(target.getTargetId());    		
    		if(mtarget != null) {    			
    			mProtocol = mtarget.getProtocol();
    		}    		
    	} else {
    		mProtocol = target.getProtocol();
    	}
    	
    	if((Protocol.CDMA).equals(mProtocol)) {
    		protocolType = new Integer(FMPProperty.getProperty("protocol.type.CDMA",CommonConstants.getProtocolCode(Protocol.CDMA)+""));
    	} else if((Protocol.GSM).equals(mProtocol)) {
    		protocolType = new Integer(FMPProperty.getProperty("protocol.type.GSM",CommonConstants.getProtocolCode(Protocol.GSM)+""));
    	} else if((Protocol.GPRS).equals(mProtocol)) {
    		protocolType = new Integer(FMPProperty.getProperty("protocol.type.GPRS",CommonConstants.getProtocolCode(Protocol.GPRS)+""));
    	} else if((Protocol.PSTN).equals(mProtocol)) {
    		protocolType = new Integer(FMPProperty.getProperty("protocol.type.PSTN",CommonConstants.getProtocolCode(Protocol.PSTN)+""));
    	} else if((Protocol.LAN).equals(mProtocol)) {
    		protocolType = new Integer(FMPProperty.getProperty("protocol.type.LAN",CommonConstants.getProtocolCode(Protocol.LAN)+""));
    	} else if((Protocol.PLC).equals(mProtocol)) {
    		protocolType = new Integer(FMPProperty.getProperty("protocol.type.PLC",CommonConstants.getProtocolCode(Protocol.PLC)+""));
    	} else if((Protocol.BYPASS).equals(mProtocol)) {
    		protocolType = new Integer(FMPProperty.getProperty("protocol.type.GPRS",CommonConstants.getProtocolCode(Protocol.GPRS)+""));
    	} else {
    		throw new Exception("Unknow Ptorocal Type");
    	}
    } 
        
    public void setLogProcessor(ProcessorHandler logProcessor)
    {
        this.logProcessor = logProcessor;
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
            //try { Thread.sleep(1000); }catch(Exception ex){}

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

        //try { Thread.sleep(1000); }catch(Exception ex){}

        //if(frame.getSvc() != GeneralDataConstants.SVC_C) 
        //{ 
        if((cnt % GeneralDataConstants.FRAME_WINSIZE) != 0) 
        { 
            if(cnt > 1)
            {
                log.debug("WCK : start : " + (seq -(seq%
                    GeneralDataConstants.FRAME_WINSIZE))
                    + "end : " + seq);
                wck =FrameUtil.getWCK(seq-
                        (seq%GeneralDataConstants.FRAME_WINSIZE),seq); 
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
     * wait ACK
     */
    private void waitAck(IoSession session, int sequence)
        throws Exception
    { 
        log.debug("waitACK.SEQ:" + sequence);
        BypassClientHandler handler = 
           (BypassClientHandler)session.getHandler(); 
        handler.waitAck(session,sequence);        
    }
    
    public void onlyConnect() throws Exception {
    	connect();
    }
    
    public IoSession getSession() {
    	return session;
    }
}
