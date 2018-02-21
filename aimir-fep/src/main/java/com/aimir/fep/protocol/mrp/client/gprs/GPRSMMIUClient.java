package com.aimir.fep.protocol.mrp.client.gprs;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.protocol.fmp.client.Client;
import com.aimir.fep.protocol.fmp.common.GPRSTarget;
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
import com.aimir.fep.protocol.mrp.client.MRPListeningClientProtocolHandler;
import com.aimir.fep.protocol.mrp.client.MRPListeningClientProtocolProvider;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FrameUtil;
import com.aimir.model.device.CommLog;
import com.aimir.util.TimeUtil;

/**
 * MCU GSM Packet Client
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class GPRSMMIUClient implements Client
{    
	private Log log = LogFactory.getLog(GPRSMMIUClient.class);
    private GPRSTarget target = null;
    private MRPListeningClientProtocolProvider provider = null; 
    private static IoSession session = null;
    private long MAX_MCUID = 4294967295L;
    private ProcessorHandler logProcessor = null;
    private NioSocketAcceptor acceptor = null;
    MRPListeningClientProtocolHandler handler =null;
    /**
     * constructor
     */
    public GPRSMMIUClient()
    {
        log.debug("\r\n\r\n ####### GPRSMMIU \r\n");
    }

    /**
     * constructor
     * @param target <code>GSMTarget</code> target
     */
    public GPRSMMIUClient(GPRSTarget target)
    {
        this.target = target;
    }


    /**
     * initialize
     * create IoProtocolConnector
     * create FMPClientProtocolProvider
     */
    private void init()
    {
    	provider = new MRPListeningClientProtocolProvider();
    	acceptor = new NioSocketAcceptor(1);
    	
    	try
        {                	
        	if(!acceptor.isActive()) {
        		
	            acceptor.getFilterChain().addLast(getClass().getName(),
	                    new ProtocolCodecFilter(provider.getCodecFactory()));
	            acceptor.setDefaultLocalAddress(new InetSocketAddress(8090));
	            this.handler = (MRPListeningClientProtocolHandler)provider.getHandler();
	            acceptor.setHandler(handler);
	            acceptor.setReuseAddress(true);
	            try {
	            	acceptor.bind();	            	
	            }catch (Exception e) {
					log.error("Address is Already use",e);
				}
	            int tryCnt=0;
	            while(true) {
		            Thread.sleep(1000);
		            if(acceptor.isActive() && handler.getSession()!=null){
		                log.debug("ACCEPTOR CONNECTED");
		                this.session=handler.getSession();
		                break;
		            }else if(tryCnt>30){
		            	throw new Exception("ACCEPTOR CONNECTED FAIL TimeOut");
		            }else {
		            	log.debug("["+tryCnt+"] ACCEPTOR CONNECTED FAIL");
		            }
		            tryCnt++;
	            }
        	}else {
        		log.error("Accepter is not ativite");
        	}
        }
        catch( Exception e )
        {   
        	handler.sessionClosed(session);
        	acceptor.unbind();
            log.error( "Failed to accept bind host",e);
            //throw e;
        }
    	
    }

    /**
     * connect to Target
     *
     * @throws Exception
     */
    private synchronized void connect(GPRSTarget target) throws Exception
    {            	
        init();
    	handler.setTarget(target);
    }

    /**
     * set Target
     *
     * @param target <code>GSMTarget</code> target
     */
    public void setTarget(Target target) throws Exception
    {
        if(!(target instanceof GPRSTarget))
            throw new Exception("not supported target");
        this.target = (GPRSTarget)target;
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
        log.info("GSMClient::sendCommand("+command+")");
		try {
			connect(target);			
			
			CommLog commLog = new CommLog();
			ServiceDataFrame frame = new ServiceDataFrame();
			log.debug("target get model : "+target.getMeterModel());
			
			long mcuId = Long.parseLong(target.getTargetId());
			log.debug("Target ID: "+target.getTargetId());
			
            command.setAttr(ServiceDataConstants.C_ATTR_REQUEST);
            command.setTid(FrameUtil.getCommandTid());
            command.setMcuId(target.getTargetId());
                    
            log.debug("SEND DATA");            
            
            ServiceData sd = null;
            String startTime = TimeUtil.getCurrentTime();
            commLog.setStartDate(startTime.substring(0,8));
            commLog.setStartTime(startTime.substring(8,14));
            commLog.setStartDateTime(startTime);
            	            	            	            	            	            
            long s = System.currentTimeMillis();
            Integer sendBytes = 0;
            String cmd = command.getCmd().getValue();
            log.debug("CDMAMMIUClient::sendCommand::start Wait Response");
       
                frame = new ServiceDataFrame();
                frame.setSvc(GeneralDataConstants.SVC_C);
                frame.setMcuId(new UINT(mcuId));
                frame.setSvcBody(command.encode());
                //------------------
                //  SEND!!!!!!!
                //------------------
            	sendBytes = send(frame);
            	/*
            	log.debug("\r\n####send command raw:"+Hex.decode(frame.encode()));
            	byte[] message = (byte[])handler.getMsgForIF4(session, 20);
                sd = handler.getResult(message,0);
                */
                
           // 	sd = mrp.getResponse(session,command.getTid().getValue());
      //      }
            log.debug("CDMAMMIUClient::sendCommand::end Wait Response");
            long e = System.currentTimeMillis();
            if(sd == null) 
                return null;
            log.debug("Received Response TID : " + ((CommandData)sd).getTid());
            commLog.setEndTime(TimeUtil.getCurrentTime());
            commLog.setSendBytes(sendBytes);
            commLog.setRcvBytes(sd.getTotalLength());
            commLog.setUnconPressedRcvBytes(sd.getTotalLength());	            
            if(((CommandData)sd).getErrCode().getValue() > 0)
            {
                commLog.setCommResult(0);//0이 실패
                commLog.setDescr(ErrorCode.getMessage(((CommandData)sd).getErrCode().getValue()));
            }            
            else
            {
                commLog.setCommResult(1);//1이 성공
            }
            commLog.setTotalCommTime((int)(e-s));	            
            commLog.setSvcTypeCode(CommonConstants.getHeaderSvc("C"));
            commLog.setSuppliedId(target.getSupplierId());
            log.info(commLog.toString());

            saveCommLog(commLog);	            
            return (CommandData)sd;
		} catch (Exception ex) {			
			log.error("sendCommand failed : command[" + command + "]", ex);
			throw ex;
		} finally {
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
     * close GSMClient session
     */
    public void close()
    {
        close(false);
    }

    /**
     * close GSMClient session and wait completed
     *
     * @param wait <code>boolean</code> wait
     */
    public void close(boolean wait)
    {   
    	log.debug("  ##### close");
    	/*
        handler.closeCircuit(session);

        log.debug(wait);
        if(session != null) {
            session.write(FrameUtil.getEOT());
            CloseFuture future = session.closeNow();
            future.awaitUninterruptibly();
        }

        //if(connector != null) {
        //    connector.getFilterChain().remove(getClass().getName());
        //    connector.dispose();
        //}         
         * session = null;
        acceptor.dispose();
         */
    	if(session!=null) {
    		handler.sessionClosed(session);    
    		handler.setSession(null);
    	}
        acceptor.unbind();        
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
        MRPListeningClientProtocolHandler handler = 
           (MRPListeningClientProtocolHandler)session.getHandler(); 
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
                            GeneralDataConstants.FRAME_WINSIZE),seq); 
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

	private void connect() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
