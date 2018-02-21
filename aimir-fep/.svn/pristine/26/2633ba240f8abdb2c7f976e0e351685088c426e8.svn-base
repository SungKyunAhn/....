package com.aimir.fep.protocol.fmp.client.cdma;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

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

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.Interface;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.protocol.fmp.client.AMUClientProtocolHandler;
import com.aimir.fep.protocol.fmp.client.AMUClientProtocolProvider;
import com.aimir.fep.protocol.fmp.client.Client;
import com.aimir.fep.protocol.fmp.client.FMPClientResource;
import com.aimir.fep.protocol.fmp.client.FMPClientResourceFactory;
import com.aimir.fep.protocol.fmp.client.resource.ts.TerminalServerPort;
import com.aimir.fep.protocol.fmp.common.CDMATarget;
import com.aimir.fep.protocol.fmp.common.CircuitTarget;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.protocol.fmp.frame.AMUFrameControl;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataFrame;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.ServiceDataFrame;
import com.aimir.fep.protocol.fmp.frame.amu.AMUFramePayLoad;
import com.aimir.fep.protocol.fmp.frame.service.AlarmData;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.EventData;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.frame.service.RMDData;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.CommLog;
import com.aimir.util.TimeUtil;

/**
 * CDMA AMU Client
 * 
 * MIU CDMA Packet Client
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오후 5:40:00$
 */
public class CDMAAMUClient implements Client {
	
	private Log log = LogFactory.getLog(CDMAAMUClient.class);
    private CDMATarget target = null;
    private int CONNECT_TIMEOUT = Integer.parseInt(FMPProperty.getProperty("Protocol.connection.timeout", 30+"")); // seconds
    private IoConnector connector = null;
    private AMUClientProtocolProvider provider = null; 
    private IoSession session = null;
    private long MAX_MCUID = 4294967295L;
    private ProcessorHandler logProcessor = null;
    private Integer activatorType = new Integer(
            FMPProperty.getProperty("protocol.system.FEP","1"));
    private Integer targetType = new Integer(
            FMPProperty.getProperty("protocol.system.MCU","2"));
    private Integer protocolType = new Integer(
            FMPProperty.getProperty("protocol.type.CDMA", CommonConstants.getProtocolCode(Protocol.CDMA)));
	
    /**
     * constructor
     */
    public CDMAAMUClient(){
    }
    
    /**
     * constructor
     * @param target <code>CDMATarget</code> target
     */
    public CDMAAMUClient(CDMATarget target)
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
        provider = new AMUClientProtocolProvider();
        if (!connector.getFilterChain().contains(getClass().getName())) {
			connector.getFilterChain().addLast(getClass().getName(),
                    new ProtocolCodecFilter(provider.getCodecFactory()));
		}
        connector.setHandler(provider.getHandler());
    }
    
    public synchronized void connect(boolean meterConnect) throws Exception
    {
    	connect();
    }
    
    /**
     * connect to Target 
     *
     * @throws Exception
     */
    public synchronized void connect() throws Exception
    {
        init();
        
        if(session != null && session.isConnected()) {
			return;
		}

        String ipaddr = null;
        int port = 1;

        int connMode = FMPClientResource.TERMINAL_SERVER;
        try
        {
            connMode = Integer.parseInt(FMPProperty.getProperty("protocol.circuit.connection.mode"));
        } catch(Exception ex) {
            throw new Exception("Can not find Circuit Connection Mode");
        }

        log.debug("CDMAAMUClient Circuit Connection Mode["+connMode+"]");

        FMPClientResource resource = null;
        Object resourceObj = null;
        SocketAddress socketAddr=null;
        if(connMode == FMPClientResource.TERMINAL_SERVER)
        {
        	resource = FMPClientResourceFactory.getResource(connMode);
        	if(target.getLocation() != null && !"".equals(target.getLocation()))
            {
            	 log.debug("##### CDMAAMUClient TS : Acquire ###### With Location = "+target.getLocation());
            	 resourceObj = resource.acquire(target.getLocation().getId());
            	 if(resourceObj == null){
            		 resourceObj = resource.acquire();
            	 }
            }
            else
            {
            	 log.debug("##### CDMAAMUClient TS : Acquire ######");
            	 resourceObj = resource.acquire();
            }
            TerminalServerPort tsport = (TerminalServerPort)resourceObj;
            ipaddr = tsport.getIpAddr();
            port = tsport.getPort();
            socketAddr = new InetSocketAddress(ipaddr, port);
            log.debug("CDMAAMUClient TS : ipaddr["+ipaddr+"] port["+port+"]");
            log.debug("CDMAAMUClient: "+resource.getActiveResourcesString());
            log.debug("CDMAAMUClient: "+resource.getIdleResourcesString());
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
            throw new Exception(" Not Support Curcuit Connection Mode["+connMode+"]");
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
                    try{ Thread.sleep(5*1000); } catch(Exception ignore) {}
                    continue;
                }
                if(resource != null && resourceObj != null)
                {
                    try {
                        resource.release(resourceObj);
                        log.debug("CDMAAMUClient: "
                                +resource.getActiveResourcesString());
                        log.debug("CDMAAMUClient: "
                                +resource.getIdleResourcesString());
                    }catch(Exception ignoreEx) {}
                }
                log.error( "Failed to connect. host["+ipaddr+"] port["
                        + port+"]",e );
                throw e;
            }
        }

        if(session == null) {
			throw new Exception("Failed to connect. host["
                        + target.getIpaddr()+"] port["
                        + target.getPort()+"]");
		}

        AMUClientProtocolHandler handler =
            (AMUClientProtocolHandler)session.getHandler();
        try
        {
            handler.setFMPResource(resource,resourceObj);
            handler.setResponseTimeout(target.getTimeout());

            handler.initCircuit(session,(CircuitTarget)target);
            log.debug("CDMAAMUClient initCircuit End");
    
        }catch(Exception ex)
        {
            close();
            throw ex;
        }
    }

    /**
     * set Target
     *
     * @param target <code>CDMATarget</code> target
     */
    public void setTarget(Target target) throws Exception
    {
        if(!(target instanceof CDMATarget))
            throw new Exception("not supported target");
        this.target = (CDMATarget)target;
    }
    
    private void saveCommLog(CommLog commLog)
    {
        log.debug("CDMAAMUClient:: saveCommLog");
        try
        {
            if(this.logProcessor != null)
            {
                this.logProcessor.putServiceData(ProcessorHandler.LOG_COMMLOG, commLog);
                log.debug("CDMAAMUClient:: saveCommLog :: putServiceData");
            }
            else
            {
                log.warn("Log Processor not registered");
            }
        } catch(Exception ex)
        {
            log.error("save Communication Log failed",ex);
        }
    }
    
    
    /**
     * set Log Processor
     * @param logProcessor
     */
    public void setLogProcessor(ProcessorHandler logProcessor)
    {
        log.debug("CDMAAMUClient::setLogProcessor");
        this.logProcessor = logProcessor;
    }
    
    /**
     * request AMUGeneralDataFrame to Target and response
     *
     * @param gdf <code>AMUGeneralDataFrame</code> request gdf
     * @return response <code>AMUGeneralDataFrame</code> response
     * @throws Exception
     */	
    public AMUGeneralDataFrame amuSendCommand(AMUGeneralDataFrame amuCommand)
        throws Exception
    {
    	//ProtocolSession session = connect();
        log.info("CDMAAMUClient::amuSendCommand start");
        log.info("send Command " + amuCommand);
        try {
            long s = System.currentTimeMillis();
            
            if(session == null || !session.isConnected()) {
    			connect();
    		}
            CommLog commLog = new CommLog();
    
            long mcuId = Long.parseLong(target.getTargetId());
            if(mcuId > MAX_MCUID)
                throw new Exception("mcuId is too Big: max["
                       + MAX_MCUID+"]");

            AMUClientProtocolHandler handler =
                (AMUClientProtocolHandler)session.getHandler();
            AMUGeneralDataFrame responseFrame= null;
            String startTime = TimeUtil.getCurrentTime();
            commLog.setStartDateTime(startTime);//Start Time은 sessionOpen 시 지정
            commLog.setStartDate(startTime.substring(0,8));
            commLog.setStartTime(startTime.substring(8,14));
            commLog.setStartDateTime(startTime);
            commLog.setInterfaceCode(CommonConstants.getInterface(Interface.AMU.getName()));
            commLog.setProtocolCode(CommonConstants.getProtocol(protocolType+""));
            commLog.setSenderTypeCode(CommonConstants.getSenderReceiver(activatorType+""));
            commLog.setSenderId(DataUtil.getFepIdString());
            commLog.setReceiverTypeCode(CommonConstants.getSenderReceiver(targetType+""));
            commLog.setReceiverId(target.getTargetId());
            commLog.setSendBytes(new Integer(send(amuCommand)));
            commLog.setCommResult(1);      
            
            log.debug("CDMAAMUClient::amuSendCommand::start Wait Response");
            long targetId = DataUtil.getLongToBytes(amuCommand.getDest_addr());
            String key = Long.toString(targetId);
            responseFrame = handler.getResponse(session , key);
            log.debug("CDMAAMUClient::amuSendCommand::end Wait Response");
            long e = System.currentTimeMillis();
            if(responseFrame == null) {
            	log.debug("response Frame is null!!");
    			return null;
    		}
            AMUFrameControl fc = responseFrame.getAmuFrameControl();
            AMUFramePayLoad fp = responseFrame.getAmuFramePayLoad();
            
            log.debug("Received Response AMU COMMAND : "+ fc.getFrameTypeMessage());
            commLog.setOperationCode(fc.getFrameTypeMessage());
            commLog.setEndTime(TimeUtil.getCurrentTime());
            commLog.setRcvBytes((int)responseFrame.encode().length);
            commLog.setUnconPressedRcvBytes((int)responseFrame.encode().length);
            commLog.setTotalCommTime((int)(e - s));
            // svctype 확인해봐야함.
            commLog.setSvcTypeCode(CommonConstants.getHeaderSvc(
                    String.valueOf(FrameUtil.getAmuServiceType(fc.getFrameType()))));
            commLog.setSuppliedId(target.getSupplierId());
            if(fp.getStatusCode() > 0)
            {
                commLog.setCommResult(0);
                commLog.setDescr(fp.getStatMessage());
            }
            else
            {
                commLog.setCommResult(1);
            }
            log.info(commLog.toString());
            saveCommLog(commLog);
            return responseFrame;
        }catch(Exception ex)
        {
            log.error("amuSendCommand failed : AMUGeneralDataFrame["+amuCommand+"]",ex);
            throw ex;
        }
        finally {
            close();
        }
    }
    
    /**
     * close AMUClient session
     */
    public void close()
    {
        close(true);
    }

    /**
     * close AMUClient session and wait completed
     *
     * @param wait <code>boolean</code> wait
     */
    public void close(boolean wait)
    {
        log.debug(wait);
        AMUClientProtocolHandler handler = 
            (AMUClientProtocolHandler)session.getHandler();
        handler.closeCircuit(session);
        
        if(session != null && !session.isClosing()) {
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
     * AMUGeneralDataFrame AMU to Target
     *
     * @param frame <code>AMUGeneralDataFrame</code>
     */
    private synchronized int send(AMUGeneralDataFrame frame) 
        throws Exception
    { 
    	int sendBytes = 0;
    	try{
    		
            byte[] bx = frame.encode(); 
            sendBytes = bx.length;
            
            IoBuffer buf = null;
            buf = IoBuffer.allocate(bx.length);
            buf.put(bx,0, sendBytes);
            buf.flip();
            log.debug("######### CDMAAMUClient Sended  [ " + Hex.decode(bx) + "] ########");
            session.write(buf);
            
    	}catch(Exception e){
    		log.error("CDMAAMUClient Send Failed : " + e);
    	}
        
        return sendBytes;
    }

	public void sendAlarm(AlarmData alarmData) throws Exception {
	}

	public CommandData sendCommand(CommandData commandData) throws Exception {
		return null;
	}

	public void sendEvent(EventData eventData) throws Exception {
	}

	public void sendMD(MDData mdData) throws Exception {
	}
	
	/**
     * send R Measurement Data to Target 
     *
     * @param md <code>RMDData</code>R Measurement Data
     * @throws Exception
     */
    public void sendRMD(RMDData rmd) throws Exception
    {
    }
}


