package com.aimir.fep.protocol.nip.client;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.eclipse.californium.elements.RawData;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.ErrorHandler;
import org.eclipse.californium.scandium.dtls.AlertMessage.AlertDescription;
import org.eclipse.californium.scandium.dtls.AlertMessage.AlertLevel;

import com.aimir.fep.bypass.BypassDevice;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.fmp.server.FMPSslContextFactory;
//import com.aimir.fep.protocol.nip.client.NotiPlug.NotiObserver;
import com.aimir.fep.protocol.nip.client.actions.CommandActionResult;
import com.aimir.fep.protocol.nip.client.actions.NICommandAction;
import com.aimir.fep.protocol.nip.client.multisession.MultiSession;
import com.aimir.fep.protocol.nip.command.AlarmEventCommandOnOff;
import com.aimir.fep.protocol.nip.command.Apn;
import com.aimir.fep.protocol.nip.command.CloneOnOff;
import com.aimir.fep.protocol.nip.command.MeterBaud;
import com.aimir.fep.protocol.nip.command.MeteringInterval;
import com.aimir.fep.protocol.nip.command.ModemEventLog;
import com.aimir.fep.protocol.nip.command.ModemIpInformation;
import com.aimir.fep.protocol.nip.command.ModemMode;
import com.aimir.fep.protocol.nip.command.ModemPortInformation;
import com.aimir.fep.protocol.nip.command.ModemResetTime;
import com.aimir.fep.protocol.nip.command.NetworkJoinTimeout;
import com.aimir.fep.protocol.nip.command.NullBypassClose;
import com.aimir.fep.protocol.nip.command.NullBypassOpen;
import com.aimir.fep.protocol.nip.command.RawRomAccess;
import com.aimir.fep.protocol.nip.command.RealTimeMetering;
import com.aimir.fep.protocol.nip.command.RetryCount;
import com.aimir.fep.protocol.nip.command.RomRead;
import com.aimir.fep.protocol.nip.command.SnmpTrapOnOff;
import com.aimir.fep.protocol.nip.command.TransmitFrequency;
import com.aimir.fep.protocol.nip.common.MultiDataProcessor;
import com.aimir.fep.protocol.nip.frame.GeneralFrame;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.protocol.nip.frame.payload.Firmware;
import com.aimir.fep.protocol.nip.server.NiDtlsProtocolHandler;
import com.aimir.fep.protocol.nip.server.NiProtocolHandler;
import com.aimir.fep.protocol.nip.server.NiProtocolProvider;
import com.aimir.fep.protocol.security.DtlsConnector;
import com.aimir.fep.util.CRCUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.util.DateTimeUtil;

public class NiClient { //extends IoHandlerAdapter {
//public class NiClient implements NotiObserver { //extends IoHandlerAdapter {
    private static Log log = LogFactory.getLog(NiClient.class); 
    private final long CONNECT_TIMEOUT = 1000L * Integer.parseInt(FMPProperty.getProperty("protocol.connection.timeout", "30")); // seconds
    private boolean multiSendYn = true;
    private ConnectFuture connFuture;
    private IoConnector connector;
    private IoSession session;
    private DTLSConnector dtlsConnector;
    private InetSocketAddress peerAddr;
    private NiDtlsProtocolHandler dtlsHandler;
    private Object connectParam;
    private String nameSpace;
    private NICommandAction commandAction;
    private String actionCommandName;
    private double fwVer = Double.parseDouble(FMPProperty.getProperty("pana.modem.fw.ver"));
    
//    //for Observer
//	boolean isCommandAciontFinished = false;
//	private Object resMonitor = new Object();
	
    public void setSession(IoSession session) {
        this.session = session;
    }

    public GeneralFrame sendCommand(Target target, GeneralFrame command, byte[] data, String actionCommandName, Object param) throws Exception {
    	this.connectParam = param;
    	this.actionCommandName = actionCommandName;
    	this.nameSpace = target.getNameSpace();
    	
    	return sendCommand(target, command, data);
    }
    
    public GeneralFrame sendCommand(Target target, GeneralFrame command, byte[] data)throws Exception {
        switch (command._networkType) {
            case MBB:
            	/*
            	 *  NI_MBB_Action_SP에서 넘어온경우 접속한 모뎀의 session을 이용하여 통신하도록 함.
            	 */
            	if(session != null){
            		log.debug("actionCommandName = " + actionCommandName + ", nameSpace = " + nameSpace);
            		if(actionCommandName != null && !actionCommandName.equals("")){
                		commandAction = ((NiProtocolHandler)session.getHandler()).setCommandAction(this.session, nameSpace, actionCommandName);            			
            		}

            		executeCommand(data, command);
            		return command;
            	}
            	else{
            		/*
            		 * 이부분 로직을 탈 일이 없다면 추후 삭제해도 무방할듯 함.
            		 */
            		log.debug("### 로그 찍혔음~!!! 이 부분 사용하고 있음 심한거 과장에게 알려주세요~~ !!!!!");
            		log.debug("### 로그 찍혔음~!!! 이 부분 사용하고 있음 심한거 과장에게 알려주세요~~ !!!!!");
            		log.debug("### 로그 찍혔음~!!! 이 부분 사용하고 있음 심한거 과장에게 알려주세요~~ !!!!!");
            		
                    connector = new NioSocketConnector();
                    FMPSslContextFactory.setSslFilter(connector);
                    log.debug("[NICL][NETWORKTYPE]MBB_ETHERNET[TCP]...");
                    connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
                    //connector.getFilterChain().addLast("logger", new LoggingFilter());
                    connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new NiProtocolProvider()));
                    connector.getFilterChain().addLast("Executor", new ExecutorFilter(Executors.newCachedThreadPool()));
                    //connector.setHandler(new NiProtocolHandler(true, commandAction));
                    connector.setHandler(new NiProtocolHandler(true, this.getClass().getSimpleName() + ":" + DateTimeUtil.getCurrentDateTimeByFormat(null) + ":MBB"));
                    connFuture = connector.connect( new InetSocketAddress(target.getIpAddr(), target.getPort()));
                    return connect(command,connFuture,data);
            	}
            case Ethernet:
                connector = new NioSocketConnector();
                FMPSslContextFactory.setSslFilter(connector);
                log.debug("[NICL][NETWORKTYPE]ETHERNET[TCP]...");
                connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
                //connector.getFilterChain().addLast("logger", new LoggingFilter());
                connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new NiProtocolProvider()));
                connector.getFilterChain().addLast("Executor", new ExecutorFilter(Executors.newCachedThreadPool()));
                //connector.setHandler(new NiProtocolHandler(true, commandAction));
                connector.setHandler(new NiProtocolHandler(true, this.getClass().getSimpleName() + ":" + DateTimeUtil.getCurrentDateTimeByFormat(null) + ":Ethernet"));
                log.debug("TCP-Sending.."+target.getIpAddr()+", TCP-Port.."+target.getPort());
                connFuture = connector.connect( new InetSocketAddress(target.getIpAddr(), target.getPort()));
                return connect(command,connFuture,data);
            default:
                if (Boolean.parseBoolean(FMPProperty.getProperty("protocol.ssl.use", "false"))) {
                    if (target.getIpv6Addr() != null && !"".equals(target.getIpv6Addr())) {
                        log.debug("[IPv6] DTLS-Sending.."+target.getIpv6Addr()+", [IPv6] DTLS-Port.."+target.getPort());
                        
                        if (Double.parseDouble(target.getFwVer()) >= fwVer)
                            dtlsConnector = DtlsConnector.newDtlsClientConnector(false, target.getProtocol(), 0, true);
                        else
                            dtlsConnector = DtlsConnector.newDtlsClientConnector(false, target.getProtocol(), 0, false);
                    }
                    else {
                        log.debug("[IPv4] DTLS-Sending.."+target.getIpAddr()+", [IPv4] DTLS-Port.."+target.getPort());
                        if (Double.parseDouble(target.getFwVer()) >= fwVer)
                            dtlsConnector = DtlsConnector.newDtlsClientConnector(true, target.getProtocol(), 0, true);
                        else
                            dtlsConnector = DtlsConnector.newDtlsClientConnector(true, target.getProtocol(), 0, false);
                    }
                    peerAddr = new InetSocketAddress(target.getIpAddr(), target.getPort());
                    dtlsHandler = new NiDtlsProtocolHandler(true, dtlsConnector, peerAddr, this.getClass().getSimpleName() + ":" + DateTimeUtil.getCurrentDateTimeByFormat(null) + ":DTLS");
                    dtlsConnector.setRawDataReceiver(dtlsHandler);
                    dtlsConnector.setErrorHandler(new ErrorHandler() {
                        @Override
                        public void onError(InetSocketAddress peerAddress, AlertLevel level, AlertDescription description) {
                            log.error("NiClient DTLSConnector Alert.Level[" + level.toString() + " DESCR[" + description.getDescription() + "] Peer[" + peerAddress.getHostName() + "]");
                        }                
                        
                    });
                    dtlsConnector.start();
                    
                	if(command.getPayload() instanceof Firmware && (actionCommandName != null && actionCommandName.equals("cmdModemOTAStart"))){
                		sendCommandActionData(command);
                	}else if(actionCommandName != null && actionCommandName.equals("cmdRawROMAccessStart")) {
                		sendCommandActionData(command);
                	}else{
                        sendData(data, command);
                        dtlsConnector.close(peerAddr);
                	}
                    
                    return command;
                }
                else {
                    connector = new NioDatagramConnector();
                    log.debug("[NICL][NETWORKTYPE]DEFAULT[UDP]...");
                    connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
                    connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new NiProtocolProvider()));
                    connector.getFilterChain().addLast("logger", new LoggingFilter());
                    connector.getFilterChain().addLast("Executor", new ExecutorFilter(Executors.newCachedThreadPool()));
                    connector.setHandler(new NiProtocolHandler(true, this.getClass().getSimpleName() + ":" + DateTimeUtil.getCurrentDateTimeByFormat(null) + ":UDP"));
                    
                    log.debug("[Remote] UDP-Sending " +target.getIpAddr()+", UDP-Port.."+target.getPort());
                    connFuture = connector.connect( new InetSocketAddress(target.getIpAddr(), target.getPort()));
                    return connect(command,connFuture,data);
                }
        }
    }
    
    public void executeCommand(byte[] data, GeneralFrame command) throws Exception {
    	/*
    	 * 특정 NICommand의 경우에는 NICommandAction에서 전송처리하도록 한다.
    	 */
    	if(command.getPayload() instanceof Firmware && (actionCommandName != null && actionCommandName.equals("cmdModemOTAStart"))){
    		sendCommandActionData(command);
    	}else if((actionCommandName != null && actionCommandName.equals("cmdRawROMAccessStart"))){
        	sendCommandActionData(command);	
    	}else{
            sendData(data, command);
    	}
    	
    }
	 
    @SuppressWarnings("rawtypes")
	public GeneralFrame connect(final GeneralFrame command,
            ConnectFuture connFuture, final byte[] data) throws Exception
    {
        connFuture.awaitUninterruptibly();
        connFuture.addListener(new IoFutureListener(){
            public void operationComplete(IoFuture future){
                ConnectFuture inConnFuture = (ConnectFuture)future;
                if( inConnFuture.isConnected() ){
                    session = future.getSession();
                    if(session.isConnected()){
                        try{
                        	log.debug("## =====>>> Start: sendData()");
                        	
                        	executeCommand(data, command);                        
                        	
                            log.debug("## =====>>> End  : sendData()");
                        }
                        catch(InterruptedException e){
	            			log.error(e, e);
	            		} catch (Exception e) {
	            			log.error(e, e);
						}
	            		log.debug("[NICL][CONNECT]session OK...");
	            	}
                    else{
	            		log.error("[NICL][CONNECT]session NO...");	
	            	}
	            }
                else {
                    log.error("Not connected...exiting");
                    log.error("[NICL][CONNECT]NO...");
                }
            }
        });
        return command;
    }
	 
	public HashMap<String, byte[]> createMultiData(byte[] orginal, GeneralFrame command){
        /**
         * First  Frame:StartFlag+NetworkType+FrameControl+FrameOption
         * Second Frame:SourceAddress+DestinationAddress+NetworkStatus+PayloadLength
         * Payload Divider + CRC
         * 
         */
        int secondPos =0;
        int maxByteCnt = 255;
        int firstByteCnt =0;
        int secondByteCnt =0;
        int payCheckCnt =0;
        int frameCheckCnt =0;
        int mapCnt =0;
        byte[] fisrstData = new byte[5];
        HashMap<String, byte[]> map = new HashMap<String, byte[]>();
         
        switch (command.foNetworkStatus){
            case Include:
                secondPos +=17;
                break;
		default:
			break;
        }

        switch (command.foAddrType){
            case Source:
            case Destination:
                secondPos +=8;
                break;
            case SrcDest:
                secondPos +=16;
                break;
		default:
			break;
        }
         
        secondPos +=2;//PayloadLength
        byte[] secondData = new byte[secondPos];
        System.arraycopy(orginal, 0, fisrstData, 0, fisrstData.length);
        System.arraycopy(orginal, 6, secondData, 0, secondData.length);//SequenceNumber 이후부터
        byte[] payloadData = new byte[orginal.length-fisrstData.length-secondData.length-2-1];//-CRC 2byte-SequenceNumber 1byte.         
        System.arraycopy(orginal, fisrstData.length+1+secondData.length, payloadData, 0, payloadData.length);
        firstByteCnt =fisrstData.length;
        secondByteCnt =secondData.length;
         
        int byteCnt = 5;
        boolean saveFlog = false;
        frameCheckCnt= firstByteCnt+1+secondByteCnt+2;
        payCheckCnt = maxByteCnt-frameCheckCnt;
        ByteBuffer buffer = ByteBuffer.allocate(maxByteCnt);
        buffer.put(fisrstData);
        buffer.put(DataUtil.getByteToInt(0));
        buffer.put(secondData);

        for(int i=0;i<payloadData.length;i++){
            byteCnt++;
            if((maxByteCnt-5)==(byteCnt)){//only payload 저장.
    	         saveFlog = true;
            }
            else if(i == payloadData.length-1){ //finish
                saveFlog = true;
            }
        	 
            buffer.put(payloadData[i]);
        	 
            if(saveFlog){
                byte[] crc = CRCUtil.Calculate_ZigBee_Crc(buffer.array(),(char)0x0000);
                DataUtil.convertEndian(crc);
                buffer.put(crc);
                map.put(""+mapCnt++, buffer.array());
                buffer.clear();
        	     
                //byteBuffer allocate in remaining data size
                if((payloadData.length-i) < payCheckCnt){
                    buffer = ByteBuffer.allocate(payloadData.length-i-1+frameCheckCnt);
                }
                else{
                    buffer = ByteBuffer.allocate(byteCnt+5);
                }
        		 
                buffer.put(fisrstData);
                buffer.put(DataUtil.getByteToInt(mapCnt));
                buffer.put(secondData);
                saveFlog=false;
                byteCnt=5;
            }
        }
        return map;
    }
    
	@SuppressWarnings("unchecked")
	private void sendCommandActionData(GeneralFrame generalFrame) throws Exception {
//		boolean useObserverMode = false;
//		int waittingTime = 0;
		
        log.debug("## NiClient sendCommandActionData()");
        
        long t0 = System.currentTimeMillis();
        MultiSession mSession;
        
        try {
        	// DTLS
        	if(dtlsConnector != null){
        		if(commandAction == null){ // RF Modem인 경우
//        			useObserverMode = true;
//        			waittingTime = Integer.parseInt(FMPProperty.getProperty("ota.firmware.modem.waiting.time.rf", "60"));
        			commandAction = dtlsHandler.setCommandAction(peerAddr, nameSpace, actionCommandName);
        		}
				mSession = commandAction.setMultiSession(peerAddr, dtlsConnector);
				log.debug("## NiClient sendCommandActionData() dtlsHandler : " + mSession);
        	}
        	// TLS, TCP, UDP
        	else{
        		if(commandAction == null){ // Ethernet Modem인 경우
        			commandAction = ((NiProtocolHandler)connector.getHandler()).setCommandAction(this.session, nameSpace, actionCommandName);
        		}else{  // MBB Modem인 경우
        			// sendCommand(Target target, GeneralFrame command,byte[] data) 에서 이미 정의
        		}
        		mSession = commandAction.setMultiSession(session);
        		log.debug("## NiClient sendCommandActionData() NiProtocolHandler : " + mSession);
        	}
        	
        	Map<String, Object> param = (Map<String, Object>) connectParam;
        	mSession.setBypassDevice((BypassDevice)param.get("BYPASS_DEVICE"));
    		
        	commandAction.executeStart(mSession, generalFrame);
//        	if(useObserverMode){
//        		commandAction.addObserver(this);
//        	}
//			
//        	/*
//        	 * Excute Start
//        	 */
//            log.debug("## NiClient::CommandAction Info - UseObserverMode = " + useObserverMode + ", Waiting Time = " + waittingTime);
//        	commandAction.executeStart(mSession, generalFrame);
//			
//			/*
//			 * waiting for CommandAction finished.
//			 */
//        	if(useObserverMode){
//        		log.debug("[START   ] Observer waiting for CommandAction");
//        		waitForJobFinish(actionCommandName, waittingTime);
//        		log.debug("[FINISHED] Observer waiting for CommandAction");
//        	}
        	
		} catch (Exception e) {
			
			CommandActionResult actionResult = generalFrame.getCommandActionResult();
			actionResult.setSuccess(false);
			actionResult.setResultValue("Modem OTA command excute error.");
			
			log.error("Modem OTA command excute error - " + e.getMessage(), e);
		}

        long t1 = System.currentTimeMillis();
        log.debug("[NICL][Sent commandAction messages delay]" + (t1 - t0));
    }

    private void sendData(byte[] data, GeneralFrame command) throws InterruptedException {
        try{           
            long t0 = System.currentTimeMillis();
            if(data.length > 255){
                HashMap<String, byte[]> multiData = new HashMap<String, byte[]>();
                //multiData = createMultiData(data ,command);
                multiData = new MultiDataProcessor().chgSFtoMF(data);
                log.debug("## NiClient multiData size() ==> " +multiData.size());
                for(int i=0;i<multiData.size();i++){
                    while (true) {
                        if(multiSendYn){
                            multiSendYn = false;
                            
                            if (dtlsConnector != null) {

                                log.debug("## NiClient multiData : " + i);
                                
                            	RawData sndRawData = new RawData((byte[])multiData.get(""+i), peerAddr);
                                dtlsConnector.send(sndRawData);            
                                
                            }
                            else {
                                session.write((byte[])multiData.get(""+i));
                            }
                            log.debug("## NiClient multiData responseData() ==> ");
                            responseData(command);
                            break;
                        }
                    }
                }
            }
            else{
                if (dtlsConnector != null) {
                    RawData sndRawData = new RawData(data, peerAddr);
                    dtlsConnector.send(sndRawData);
                }
                else {
                    session.write(data);
                }
                responseData(command);
            }
            long t1 = System.currentTimeMillis();
            log.debug("[NICL][Sent messages delay]" + (t1 - t0));
        }
        catch(Exception e){
            log.error(e, e);
        }
    }
	 
    private void responseData(GeneralFrame command) throws Exception {
    	
        long s = System.currentTimeMillis();
        GeneralFrame responseData = null;
        
        if (dtlsHandler != null) {
            responseData = dtlsHandler.getResponse();
        }
        else
        {
            NiProtocolHandler handler = (NiProtocolHandler) session.getHandler();
            //Ack 요청시 데이터 확인.
            long tid = session.getId();
            log.debug("[NICL][FCACK][TID]"+tid);
            //byte[] ackData = null;
            Object ackData = null;
            switch (command.fcAck) {
                case Ack:
                    ackData = handler.getResponse(session,tid);
                    if(ackData != null){
                        log.debug("[NICL][ACK]Success...");
                    }
                    tid++;
                    break;
                case Task:
                    break;
                default:
                    break;
            }
            
            responseData = handler.getResponse(session,tid);
        }
        
        long e = System.currentTimeMillis();
        log.debug("[NICL][RESPONSE TIME]"+(e-s));
        	
        if(responseData == null){
            throw new Exception("[NICL][ACK]ResponseData Null...");
        }

        switch(command.foType){
            case  Ack:
                log.debug("[NICL][FOTYPE]Ack");
                break;
            case  Bypass:
                log.debug("[NICL][FOTYPE]Bypass");
                break;
            case  Command:
                switch (command.niAttributeId) {
                    case  NetworkJoinTimeout:
                    	command.abstractCmd = new NetworkJoinTimeout(); 
                    	command.abstractCmd.decode(command.getPayloadData(),command._commandType);
                        break;
                    case NullBypassOpen:
        				Command commandPayload = (Command) responseData.getPayload();
        				byte[] value = (commandPayload.getAttribute().getData()[0]).getValue();
        				
                    	command.abstractCmd = new NullBypassOpen();
                    	command.abstractCmd.decode(value);
                    	break;
                    case NullBypassClose:
        				Command commandNullBypassClosePayload = (Command) responseData.getPayload();
        				byte[] nullBypassCloseValue = (commandNullBypassClosePayload.getAttribute().getData()[0]).getValue();
        				
                    	command.abstractCmd = new NullBypassClose();
                    	command.abstractCmd.decode(nullBypassCloseValue);
                    	break;
                    case MeterBaud:
                    case MeterBaud_GET:                    	
                    	Command meterBaudPayload = (Command) responseData.getPayload();
                    	byte[] meterBaudValue = (meterBaudPayload.getAttribute().getData()[0]).getValue();
                    	
                    	command.abstractCmd = new MeterBaud();
                    	command.abstractCmd.decode(meterBaudValue);
                    	break;
                    	
                    case RealTimeMetering:
                    	Command realTimeMeteringPayload = (Command) responseData.getPayload();
                    	byte[] realTimeMeteringValue = (realTimeMeteringPayload.getAttribute().getData()[0]).getValue();
                    	
                    	command.abstractCmd = new RealTimeMetering();
                    	command.abstractCmd.decode(realTimeMeteringValue);
                    	break;
                    	
                    case APN:
                    	Command getApn = (Command) responseData.getPayload();
                    	byte[] apn  = (getApn.getAttribute().getData()[0]).getValue();
                    	
                    	command.abstractCmd = new Apn();
                    	command.abstractCmd.decode(apn);
                    	break;
                    case ModemEventLog:
                    	Command modemEventLogPayload = (Command) responseData.getPayload();
                    	byte[] modemEventLogValue = (modemEventLogPayload.getAttribute().getData()[0]).getValue();
                    	                    	
                    	command.abstractCmd = new ModemEventLog();
                    	command.abstractCmd.decode(modemEventLogValue);                    	
                    	break;
                    case CloneOnOff:
                    	Command cloneOnOffPayload = (Command) responseData.getPayload();
                    	byte[] cloneOnOffValue = (cloneOnOffPayload.getAttribute().getData()[0]).getValue();
                    	                    	
                    	command.abstractCmd = new CloneOnOff();
                    	command.abstractCmd.decode(cloneOnOffValue);                    	
                    	break;	
                    case MeteringInterval:
                    case MeteringInterval_GET:
                        Command meteringIntervalPayload = (Command) responseData.getPayload();
                        byte[] meteringIntervalValue = (meteringIntervalPayload.getAttribute().getData()[0]).getValue();

                        command.abstractCmd = new MeteringInterval();
                        command.abstractCmd.decode(meteringIntervalValue);
                        break;
                    case RetryCount:
                    case RetryCount_GET:
                        Command retryCountPayload = (Command) responseData.getPayload();
                        byte[] retryCountValue = (retryCountPayload.getAttribute().getData()[0]).getValue();

                        command.abstractCmd = new RetryCount();
                        command.abstractCmd.decode(retryCountValue);
                        break;
                    case WatchdogTest:
                    	break;
                    case SnmpTrapOnOff:
                    case SnmpTrapOnOff_GET:
                        Command snmpTrapOnOffPayload = (Command) responseData.getPayload();
                        byte[] statusValue = (snmpTrapOnOffPayload.getAttribute().getData()[0]).getValue();

                        command.abstractCmd = new SnmpTrapOnOff();
                        command.abstractCmd.decode(statusValue);
                    	break;
                    case RawROMAccess:
                    case RawROMAccess_GET:
                        Command rawROMAccessPayload = (Command) responseData.getPayload();
                        byte[] rawRomAccessValue = (rawROMAccessPayload.getAttribute().getData()[0]).getValue();

                        command.abstractCmd = new RawRomAccess();
                        command.abstractCmd.decode(rawRomAccessValue);
                    	break;	
                    case ModemResetTime:
                    	Command modemResetTimePayload = (Command) responseData.getPayload();
                    	byte[] modemResetTimeValue = (modemResetTimePayload.getAttribute().getData()[0]).getValue();
                    	
                    	command.abstractCmd = new ModemResetTime();
                    	command.abstractCmd.decode(modemResetTimeValue);
                    	break;
                    case ModemMode:
                    case ModemMode_GET:	  	
                    	Command modemModePayload = (Command) responseData.getPayload();
                    	byte[] modemModeValue= (modemModePayload .getAttribute().getData()[0]).getValue();
                    	
                    	command.abstractCmd = new ModemMode();
                    	command.abstractCmd.decode(modemModeValue, command._commandType);
                    	break;
                    case Alarm_EventCommandON_OFF:
                    case Alarm_EventCommandON_OFF_GET:
                    	Command eventCommandOnOffPayload = (Command) responseData.getPayload();
                    	byte[] eventCommandOnOffValue= (eventCommandOnOffPayload .getAttribute().getData()[0]).getValue();
                    	
                    	command.abstractCmd = new AlarmEventCommandOnOff();
                    	command.abstractCmd.decode(eventCommandOnOffValue);
                    	break;
                    case TransmitFrequency:
                    case TransmitFrequency_GET:
                    	Command transmitFrequencyPayload = (Command) responseData.getPayload();
                    	byte[] transmitFrequencyValue= (transmitFrequencyPayload .getAttribute().getData()[0]).getValue();
                    	
                    	command.abstractCmd = new TransmitFrequency();
                    	command.abstractCmd.decode(transmitFrequencyValue,command._commandType);
                    	break;
                    case ROMRead:
                    	Command getRomRead = (Command) responseData.getPayload();
                    	byte[] romRead  = (getRomRead.getAttribute().getData()[0]).getValue();
                    	
                    	command.abstractCmd = new RomRead();
                    	command.abstractCmd.decode(romRead);
                    	break;
                    case ModemIpInformation_GET:
                    case ModemIpInformation:
                    	Command modemIpInformationPayload = (Command) responseData.getPayload() ;
                    	byte[] modemIpInformationValue = (modemIpInformationPayload.getAttribute().getData()[0]).getValue();
                    	command.abstractCmd = new ModemIpInformation();
                    	command.abstractCmd.decode(modemIpInformationValue);
                    	break;                  	
                    case ModemPortInformation_GET:
                    case ModemPortInformation:
                    	Command modemPortInformationPayload = (Command) responseData.getPayload() ;
                    	byte[] modemPortInformationValue = (modemPortInformationPayload.getAttribute().getData()[0]).getValue();
                    	command.abstractCmd = new ModemPortInformation();
                    	command.abstractCmd.decode(modemPortInformationValue);
                    	break;                        	
                    default:
                    	command.abstractCmd.decode(command.getPayloadData());
                        break;
                }
                break;
                
            case Firmware:
            	Firmware fw = (Firmware) responseData.getPayload();
            	command.setPayload(fw);
            	
            	break;
            case AlarmEvent:
            	break;
            case MeterEvent:
            	break;
            case Metering:
            	break;
		default:
			break;
        }
        multiSendYn = true;
    }
    
//    public IoSession getConSession(){
//    	return session;
//    }
    
	public void dispose() {
		log.debug("### NIClient 자원 해제 Start. ##");
		if(session != null && session.isConnected()){
			session.closeOnFlush();
		}
		
		if(connector != null){
			connector.dispose();			
		}
		if(dtlsConnector != null && peerAddr != null) {
		    dtlsConnector.close(peerAddr);
		    dtlsConnector.destroy();
		}
		connector = null;
		
		log.debug("### NIClient 자원 해제 Complete. ##");
	}


//	/**
//	 * 최대 waittingTime 동안 관찰하면서 notify가 오기를 기다린다.
//	 * @param commandName
//	 * @param waittingTime
//	 * @return true is by notify, false is by timeout
//	 */
//	private boolean waitForJobFinish(String commandName, int waittingTime){
//		boolean result = true;
//        long stime = System.currentTimeMillis();
//        long ctime = 0;
//        
//		while(!isCommandAciontFinished) {
//			waitResponse();
//		    ctime = System.currentTimeMillis();
//		    if(((ctime - stime)/1000) > waittingTime){
//		    	log.debug("[CommandAction wait for Timeout [CommadAction name=" + commandName + ""
//		    			+ ", waittingTime=" + waittingTime + "s] SESSION IDLE COUNT [" + session.getIdleCount(IdleStatus.BOTH_IDLE) + "]");
//		    	result = false;
//		     }		    	 
//		 }
//		 return result;
//	}
//
//    public void waitResponse() {
//        synchronized(resMonitor) { 
//            try { 
//            	resMonitor.wait(500);
//            } catch(InterruptedException ie) {
//            	ie.printStackTrace();
//            }
//        }
//    }
//	
//	@Override
//	public void notify(String name, Map<?, ?> params) {
//		log.debug("###### CommandAction Finished - " + name + " #####");
//		isCommandAciontFinished = true;
//	}
}
