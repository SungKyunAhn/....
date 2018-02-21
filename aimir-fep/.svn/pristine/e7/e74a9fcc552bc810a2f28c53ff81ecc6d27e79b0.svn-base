package com.aimir.fep.protocol.fmp.client.sms;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.protocol.fmp.client.Client;
import com.aimir.fep.protocol.fmp.client.FMPClientProtocolHandler;
import com.aimir.fep.protocol.fmp.client.FMPClientProtocolProvider;
import com.aimir.fep.protocol.fmp.client.FMPClientResource;
import com.aimir.fep.protocol.fmp.client.FMPClientResourceFactory;
import com.aimir.fep.protocol.fmp.client.resource.ts.TerminalServerPort;
import com.aimir.fep.protocol.fmp.common.CircuitTarget;
import com.aimir.fep.protocol.fmp.common.SMSTarget;
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
import com.aimir.fep.protocol.mrp.client.MRPClientProtocolProvider;
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
import org.apache.mina.transport.serial.SerialAddress;
import org.apache.mina.transport.serial.SerialConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 * MCU SMS Packet Client
 * 
 * @author Y.K Park (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2008-11-11 13:59:15 +0900 $,
 */
public class SMSClient implements Client
{
    private Log log = LogFactory.getLog(SMSClient.class);
    private SMSTarget target = null;
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
            FMPProperty.getProperty("protocol.type.SMS",CommonConstants.getProtocolCode(Protocol.SMS)+""));

    /**
     * constructor
     */
    public SMSClient()
    {
    }

    /**
     * constructor
     * @param target <code>SMSTarget</code> target
     */
    public SMSClient(SMSTarget target)
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
    	int connMode = FMPClientResource.TERMINAL_SERVER;
    	try
        {
            connMode = Integer.parseInt(FMPProperty.getProperty("protocol.circuit.connection.mode"));
        }
    	catch(Exception ex)
        {
            log.error(ex, ex);
        }
        if(connMode==FMPClientResource.TERMINAL_SERVER) {
        	connector = new NioSocketConnector();
        }
        else if(connMode==FMPClientResource.SERIAL_PORT) {
        	connector = new SerialConnector();
        }
        provider = new FMPClientProtocolProvider();
        if (!connector.getFilterChain().contains(getClass().getName()))
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
        
        if(session != null && session.isConnected())
            return;
        SocketAddress socketAddr=null;
        String ipaddr = null;
        int port = 1;

        int connMode = 1;
        try
        {
            connMode = Integer.parseInt(
                FMPProperty.getProperty("protocol.circuit.connection.mode"));
        } catch(Exception ex) 
        { 
            throw new Exception("Can not find Circuit Connection Mode"); 
        }

        log.debug("SMSClient Circuit Connection Mode["+connMode+"]");

        FMPClientResource resource = null;
        Object resourceObj = null;
        if(connMode == FMPClientResource.TERMINAL_SERVER)
        {
            resource = FMPClientResourceFactory.getResource(connMode);
            log.debug("##### SMSClient TS : Acquire ######");
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
            socketAddr = new InetSocketAddress(ipaddr,port);
            log.debug("SMSClient TS : ipaddr["+ipaddr+"] port["+port+"]");
            log.debug("SMSClient: "+resource.getActiveResourcesString());
            log.debug("SMSClient: "+resource.getIdleResourcesString());
        }
        else if(connMode == FMPClientResource.SERIAL_PORT)
        {
        	//listPorts();
        	log.debug("Serial Mode!!");
        	//name
        	String name=FMPProperty.getProperty("protocol.serial.deviceName");
        	//bauds
        	int bauds=Integer.parseInt(FMPProperty.getProperty("protocol.serial.bauds"));

        	//DataBits
        	SerialAddress.DataBits dataBits = SerialAddress.DataBits.DATABITS_8;
        	if(FMPProperty.getProperty("protocol.serial.databits")==null ||
        	        FMPProperty.getProperty("protocol.serial.databits").length()==0) {
        		dataBits = SerialAddress.DataBits.DATABITS_8;
        	}else if(Integer.parseInt(FMPProperty.getProperty("protocol.serial.databits"))==5)
        	{
        		dataBits = SerialAddress.DataBits.DATABITS_5;
        	}else if(Integer.parseInt(FMPProperty.getProperty("protocol.serial.databits"))==6) {
        		dataBits = SerialAddress.DataBits.DATABITS_6;
        	}else if(Integer.parseInt(FMPProperty.getProperty("protocol.serial.databits"))==7) {
        		dataBits = SerialAddress.DataBits.DATABITS_7;
        	}else if(Integer.parseInt(FMPProperty.getProperty("protocol.serial.databits"))==8) {
        		dataBits = SerialAddress.DataBits.DATABITS_8;
        	}else {
        		throw new Exception("Please Check Serial DataBits or Configuration");
        	}

        	//StopBits
        	SerialAddress.StopBits stopBits = SerialAddress.StopBits.BITS_1;
        	if(FMPProperty.getProperty("protocol.serial.stopbits")==null ||
        	        FMPProperty.getProperty("protocol.serial.stopbits").length()==0) {
        		stopBits = SerialAddress.StopBits.BITS_1;
        	}else if(Integer.parseInt(FMPProperty.getProperty("protocol.serial.stopbits"))==5){
        		stopBits = SerialAddress.StopBits.BITS_1_5;
        	}else if(Integer.parseInt(FMPProperty.getProperty("protocol.serial.stopbits"))==1){
        		stopBits = SerialAddress.StopBits.BITS_1;
        	}else if(Integer.parseInt(FMPProperty.getProperty("protocol.serial.stopbits"))==2) {
        		stopBits = SerialAddress.StopBits.BITS_2;
        	}else {
        		throw new Exception("Please Check Serial StopBits or Configuration");
        	}

        	//parity
        	SerialAddress.Parity parity = SerialAddress.Parity.NONE;
        	if(FMPProperty.getProperty("protocol.serial.parity")==null ||
        	        FMPProperty.getProperty("protocol.serial.parity").length()==0) {
        		parity = SerialAddress.Parity.NONE;
        	}else if("even".equals(FMPProperty.getProperty("protocol.serial.parity"))){
        		parity = SerialAddress.Parity.EVEN;
        	}else if("mark".equals(FMPProperty.getProperty("protocol.serial.parity"))) {
        		parity = SerialAddress.Parity.MARK;
        	}else if("none".equals(FMPProperty.getProperty("protocol.serial.parity"))) {
        		parity = SerialAddress.Parity.NONE;
        	}else if("odd".equals(FMPProperty.getProperty("protocol.serial.parity"))) {
        		parity = SerialAddress.Parity.ODD;
        	}else {
        		throw new Exception("Please Check Serial parity or Configuration");
        	}

        	//flow
        	SerialAddress.FlowControl flow = SerialAddress.FlowControl.NONE;
        	if(FMPProperty.getProperty("protocol.serial.flow")==null ||
        	        FMPProperty.getProperty("protocol.serial.flow").length()==0) {
        		flow = SerialAddress.FlowControl.NONE;
        	}else if("none".equals(FMPProperty.getProperty("protocol.serial.flow"))){
        		flow = SerialAddress.FlowControl.NONE;
        	}else {
        		throw new Exception("Please Check Serial Flow Control or Configuration");
        	}
        	socketAddr = new SerialAddress( name, bauds, dataBits, stopBits, parity, flow);
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
                ConnectFuture future = connector.connect(socketAddr);
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
                    try{ Thread.currentThread().sleep(5*1000); } 
                    catch(Exception ignore) {}
                    continue;
                }
                if(resource != null && resourceObj != null)
                {
                    try {
                        resource.release(resourceObj);
                        log.debug("SMSClient: "
                                +resource.getActiveResourcesString());
                        log.debug("SMSClient: "
                                +resource.getIdleResourcesString());
                    }catch(Exception ignoreEx) {}
                }
                log.error( "Failed to connect. host["
                        + ipaddr+"] port[" + port+"]",e );
                throw e;
            }
        }

        if(session == null)
            throw new Exception("Failed to connect. host["
                        + target.getIpaddr()+"] port["
                        + target.getPort()+"]");

        FMPClientProtocolHandler handler = 
            (FMPClientProtocolHandler)session.getHandler();
        try
        {
            handler.setFMPResource(resource,resourceObj);
            handler.setResponseTimeout(target.getTimeout());

            handler.initCircuit(session,(CircuitTarget)target);
            log.debug("SMSClient initCircuit End");

            handler.waitENQ();
            log.debug("SMSClient waitENQ End");
        }catch(Exception ex)
        {
            close();
            throw ex;
        }
    }

    /**
     * set Target
     *
     * @param target <code>SMSTarget</code> target
     */
    public void setTarget(Target target) throws Exception
    {
        if(!(target instanceof SMSTarget))
            throw new Exception("not supported target");
        this.target = (SMSTarget)target;
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
        //IoSession session = connect();
        log.info("SMSClient::sendCommand("+command+")");
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
            commLog.setStartDateTime(startTime);
            commLog.setProtocolCode(CommonConstants.getProtocol(protocolType+""));
            commLog.setSenderTypeCode(CommonConstants.getSenderReceiver(activatorType+""));
            commLog.setSenderId(DataUtil.getFepIdString());
            commLog.setReceiverTypeCode(CommonConstants.getSenderReceiverByName(target.getReceiverType()));
            commLog.setReceiverId(target.getReceiverId());
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
            commLog.setSvcTypeCode(CommonConstants.getHeaderSvc("C"));
            commLog.setSuppliedId(target.getSupplierId());
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
    	try{
	        //IoSession session = connect();
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
	catch(Exception ex)
        {
            throw ex;
        }finally
        {
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
			// IoSession session = connect();
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

    /**
     * send Measurement Data to Target 
     *
     * @param md <code>MDData</code> Measurement Data
     * @throws Exception
     */
    public void sendMD(MDData md) throws Exception
    {
		try {
			// IoSession session = connect();
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
     * close SMSClient session
     */
    public void close()
    {
        close(true);
    }

    /**
     * close SMSClient session and wait completed
     *
     * @param wait <code>boolean</code> wait
     */
    public void close(boolean wait)
    {
        log.debug(wait);
        FMPClientProtocolHandler handler = 
            (FMPClientProtocolHandler)session.getHandler();
        handler.closeCircuit(session);
        
        if(session != null && !session.isClosing()) {
            session.write(FrameUtil.getEOT());
            CloseFuture future = session.closeNow();
            //future.awaitUninterruptibly();
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
     * SEQ를 생성한다.
     * @return
     */
    public static int getSEQ(){
    	int seq = new Random().nextInt(100) & 0xFF;
    	return seq;
    }
}
