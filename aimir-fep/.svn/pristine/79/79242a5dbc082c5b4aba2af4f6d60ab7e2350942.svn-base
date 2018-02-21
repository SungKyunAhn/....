package com.aimir.fep.schedule.task;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.eclipse.californium.elements.RawData;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.ErrorHandler;
import org.eclipse.californium.scandium.dtls.AlertMessage.AlertDescription;
import org.eclipse.californium.scandium.dtls.AlertMessage.AlertLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.nip.CommandNIProxy;
import com.aimir.fep.protocol.nip.client.NiClient;
import com.aimir.fep.protocol.nip.command.RawRomAccess;
import com.aimir.fep.protocol.nip.common.GeneralDataFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameOption_Type;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NIAttributeId;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.protocol.nip.server.NiDtlsProtocolHandler;
import com.aimir.fep.protocol.nip.server.NiProtocolHandler;
import com.aimir.fep.protocol.nip.server.NiProtocolProvider;
import com.aimir.fep.protocol.security.DtlsConnector;
import com.aimir.fep.protocol.security.OacServerApi;
import com.aimir.fep.util.CmdUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.Modem;
import com.aimir.util.DateTimeUtil;
import com.google.gson.JsonObject;

@Service
public class PanaModemCertWriteTask 
{
    private static Log log = LogFactory.getLog(PanaModemCertWriteTask.class);
      
    private DTLSConnector dtlsConnector;
    private InetSocketAddress peerAddr;
    private NiDtlsProtocolHandler dtlsHandler;
    private ConnectFuture connFuture;
    private IoConnector connector;
    private IoSession session;
  
    private double fwVer = Double.parseDouble(FMPProperty.getProperty("pana.modem.fw.ver"));
    private final long CONNECT_TIMEOUT = 1000L * Integer.parseInt(FMPProperty.getProperty("protocol.connection.timeout", "30")); // seconds
    
    public static final byte[] modemCertAddr = {0x00, 0x0f, 0x50, 0x00};
    public static final byte[] modemKeyAddr = {0x00, 0x0f, 0x58, 0x06};
    public static final byte[] modemCAAddr = {0x00, 0x0f, 0x5E, 0x12};
    public static final byte[] modemRootCertAddr = {0x00, 0x0f, 0x66, 0x18};
    public static final Object[] arrModemCert = {modemCertAddr, modemKeyAddr, modemCAAddr, modemRootCertAddr};
    
    public static final byte[] modemCertValid = {0x12, 0x12, 0x12, 0x12};
    public static final byte[] modemKeyValid = {0x34, 0x34, 0x34, 0x34};
    public static final byte[] modemCAValid = {0x78, 0x78, 0x78, 0x78};
    public static final byte[] modemRootCertValid = {(byte) 0x90, (byte)0x90, (byte)0x90, (byte)0x90};
    public static final Object[] arrModemValid = {modemCertValid, modemKeyValid, modemCAValid, modemRootCertValid};
    
    private final double FIRMWARE_VER = 1.0;
    private int udpPort = 65333;
    private boolean udpUse = false;
	
    List<byte[]> packetList = new ArrayList<byte[]>();
    List<byte[]> result = new ArrayList<byte[]>();
    
    List<String> sendResultMap = new ArrayList<String>();

    @Autowired
	private ModemDao modemDao;
     
    public static void main(String[] args) {
    	
    	ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring-fep-schedule.xml"});
    	
    	DataUtil.setApplicationContext(ctx);
    	PanaModemCertWriteTask task = ctx.getBean(PanaModemCertWriteTask.class); 
    	
    	log.info("======================== PanaModemCertWriteTask start. ========================");
    	task.execute(args);
        log.info("======================== PanaModemCertWriteTask end. ========================");
         
        System.exit(0);
    }
    
           
    public void execute(String[] args) {  
    	
    	String filename = "";
    	String sendResult = "FALSE";
    	
    	try {    	
    		
    		if (args.length < 1) {    			
    			log.debug("not found export.xml");
    			System.exit(0);
    			
	        } else {
	        	
	        	filename = args[0];
	        	
	        	if(args.length > 2) {
	        		udpPort = Integer.parseInt(args[1]);
	        		udpUse = ("true".equals(args[2]))? true : false;
	        	}
	        	
	        	log.debug("filename =====> " + filename);
	        }
    		    		
    		// 1. xml file read    	
        	List<String> deviceList = readDeviceXML(filename);
        	int total = deviceList.size();
			int i = 0;
			
        	for(String deviceId : deviceList) {
        		
        		log.debug("## execute deviceId [" + deviceId + "] " + (i + 1) + "/" + total);
        		
        		Modem modem = modemDao.get(deviceId);
        		
        		if(modem != null) {
        			
        			String mdsId = String.valueOf(modem.getId());
        			        			
        			if(modem.getModemType() == ModemType.SubGiga && modem.getProtocolType() == Protocol.IP) { // RF모뎀        				
            			getPanaCert(deviceId, mdsId);        				
        				sendResult = sendCommand(mdsId);
        			} else {
        				sendResult = "FAIL";
        				log.debug("## RF MODEM [" + deviceId + "] IS NULL");
        			}
        			sendResultMap.add(deviceId + " : " +  sendResult); 
        		} 
        		i++;
        	}       
        	
        	for(String result : sendResultMap) {
        		log.debug("## SEND RESULT " + result);
        	}
        	
		} catch (Exception e) {
			log.error(e, e);
		}
    }
    
    
    /**
     * getPanaCert
     * @param deviceId
     */
    public void getPanaCert(String deviceId, String mdsId) {
    	
    	log.debug("========== getPanaCert START ============");
    	
    	JsonObject json;
    	
    	OacServerApi api = new OacServerApi();
    	
		try {	
			json = api.oacServerApi("get_pana_cert/" + deviceId, null);
			
			if (json != null && json.get("result_code") != null ) {				
				
				int result  = json.get("result_code").getAsInt();
				log.debug("## get_pana_cert result : " + result);
				
				if (result == 0) { // success		    		
					String cert = json.get("cert").getAsString();						
		    		String key = json.get("key").getAsString();
		    		String caCert = json.get("caCert").getAsString();
		    		String rootCert = json.get("rootCert").getAsString();
		    		
		    		String[] arrCert = {cert, key, caCert, rootCert};
		    		
		    		makePacketList(mdsId, arrModemCert, arrModemValid, arrCert);
    			}
			} 
			
		} catch (Exception e) {
			log.error(e, e);			
		} 
		
		log.debug("========== getPanaCert END ============");
    }
    
    private String sendCommand(String mdsId) {

    	CommandNIProxy niProxy = new CommandNIProxy();
    	
    	// 커맨드 전송할 모뎀 조회
    	Modem modem = modemDao.get(Integer.parseInt(mdsId));
    	Target target;
    	String sendResult = "";
    	
		try {		
			target = CmdUtil.getNullBypassTarget(modem);
			
			NIAttributeId command = NIAttributeId.RawROMAccess;
			GeneralFrame generalFrame = niProxy.setGeneralFrameOption(FrameOption_Type.Command, command);			
			generalFrame.setFrame();
			generalFrame.setNIAttributeId(command.getCode());
			NiClient client = niProxy.getClient(target, generalFrame);
			target.setPort(udpPort);
			log.debug("## cudpPort : " + udpPort);
			log.debug("## cudpUse : " + udpUse);
			
			getConnection(target);
			
			int total = packetList.size();
			int i = 0;
			int success = 0;
				
			for(byte[] packet : packetList) {
				
				log.debug("## sendCommand [" + mdsId + "] packet list no ===> " + (i + 1) + "/" + total);
				
				HashMap info = new HashMap();
				info.put("data", packet);
				byte[] sendData = new GeneralDataFrame().make(generalFrame, info);
									
				RawRomAccess resultObj = null;
				
				try {				
					resultObj = (RawRomAccess)sendData(sendData, generalFrame, target);						
				} catch (Exception e) {
					sendResult = "FAIL";
					closeConnect();					
					throw new Exception("TIMEOUT EXCEPTION => sendCommand() EXIT ");
				}
				
				if(resultObj != null) {
					
					if(resultObj.getData() == null) {
						log.debug("## cmdResult : Reponse of RawRomAccess-Command is invalid.");														
					} else {						
						String sendPacket = Hex.decode(packet);
						String receivePacket = Hex.decode(resultObj.getPacket());
						
						log.debug("## cmdResult : result : " + Hex.decode(resultObj.getPacket()));							
						log.debug("## cmdResult : packet : " + Hex.decode(packet));
						
						if(sendPacket.equals(receivePacket)) { // send == receive							
							success++;							
							log.debug("## cmdResult : Execution OK");							
						} else {	
							log.debug("## cmdResult : Execution ERROR");
						}							
					}
					
				} else {			
					log.debug("## cmdResult : Failed to sending a command.");						
				}
				
				i++;
			} // end for
			
			if(packetList.size() == success && packetList.size() > 0) {

				// fwver필드는 모뎀이 올라오면서 업데이트 되므로
				// firmware version 구분용으로 powerthreshold 사용(OTA)
				modem.setPowerThreshold(FIRMWARE_VER);  
				modemDao.update(modem);
				sendResult = "SUCCESS";
				log.debug("## MODEM ["+mdsId+"] FIRMWARE_VER UPDATE : " + FIRMWARE_VER);
			} else {
				sendResult = "FAIL";
				log.debug("## MODEM ["+mdsId+"] FIRMWARE_VER UPDATE FAIL : success - " + success + "/ total " + packetList.size());
			}
			
			packetList = null;
			packetList = new ArrayList<byte[]>();
			closeConnect();
			
		} catch (Exception e) {
			
			sendResult = "FAIL";
			log.error(e, e);
			
		} finally {
			
			packetList = null;
			packetList = new ArrayList<byte[]>();
			closeConnect();
		}
		
		return sendResult;
    }
    
    private AbstractCommand sendData(final byte[] data, GeneralFrame command, Target target) throws Exception {
    	
    	long s = System.currentTimeMillis();
    	
    	if(dtlsConnector != null) {
    		RawData sndRawData = new RawData(data, peerAddr);
            dtlsConnector.send(sndRawData);
    	} else {
	        connFuture.awaitUninterruptibly();
	        connFuture.addListener(new IoFutureListener(){
	            public void operationComplete(IoFuture future){
	                ConnectFuture inConnFuture = (ConnectFuture)future;
	                if( inConnFuture.isConnected()) {
	                    session = future.getSession();
	                    if(session.isConnected()) {
	                        try{
	                        	log.debug("## =====>>> Start: sendData()");
	                        	
	                        	session.write(data);                     
	                        	
	                            log.debug("## =====>>> End  : sendData()");
	                        }
	                        catch (Exception e) {
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
    	}
        
        return responseData(command);
    }
    
    
    private AbstractCommand responseData(GeneralFrame command) throws Exception {
    	
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
        	
        if(responseData == null) {        	
            throw new Exception("[NICL][ACK]ResponseData Null...");
        }
        
        switch(command.foType) {
        	case  Command:
            switch (command.niAttributeId) {                  
                case RawROMAccess:
                case RawROMAccess_GET:
                    Command rawROMAccessPayload = (Command) responseData.getPayload();
                    byte[] rawRomAccessValue = (rawROMAccessPayload.getAttribute().getData()[0]).getValue();

                    command.abstractCmd = new RawRomAccess();
                    command.abstractCmd.decode(rawRomAccessValue);
                	break;              	
                default:
                	command.abstractCmd.decode(command.getPayloadData());
                    break;
            }
            break;
        default:
        	break;
        }
        
        return (AbstractCommand) command.abstractCmd; 
    }
        
    /**
     * readPanaXML
     */
    private List<String> readDeviceXML(String filename) {
    	
    	InputSource is;
    	List<String> deviceList = new ArrayList<String>();
    	
		try {
			if(filename != null) {
				is = new InputSource(new FileReader(filename));
				Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
				
				// xpath 생성
				XPath  xpath = XPathFactory.newInstance().newXPath();
				   
				String expression = "//*/dc_child";
				NodeList cols = (NodeList) xpath.compile(expression).evaluate(document, XPathConstants.NODESET);
				
				for(int i = 0; i < cols.getLength(); i++) {
		            String deviceId = cols.item(i).getAttributes().getNamedItem("D_NUMBER").getTextContent();
		            deviceList.add(deviceId);
		        }
			}
			
		} catch (FileNotFoundException e) {
			log.error(e, e);
		} catch (SAXException e) {
			log.error(e, e);
		} catch (IOException e) {
			log.error(e, e);
		} catch (ParserConfigurationException e) {
			log.error(e, e);
		} catch (XPathExpressionException e) {
			log.error(e, e);
		}
		
		return deviceList;
    }
    
	public void makePacketList(String mdsId, Object[] arrModemRomAddr, Object[] arrModemValid, String[] arrCert) throws Exception {
		
		log.debug("## makePacketList : " + mdsId);
				
		int packetMaxLen = 250;
		int packetAddrLen = 4;
		int packetLen = 2;
		
		int certMaxLen = 244;		
		int certPaddingLen = 6;
		
		try {
			
			for(int i = 0; i < arrCert.length; i++) {
				
				int modemRomAddr = DataUtil.getIntTo4Byte((byte[])arrModemRomAddr[i]); // rom address
				byte[] modemValid = (byte[])arrModemValid[i]; // valid check value				
				byte[] certData = Base64.decodeBase64(arrCert[i].getBytes());
				
				int certLen = certData.length;
				
				ByteBuffer certBuffer = ByteBuffer.allocate(certLen); // 인증서 buffer
				certBuffer.put(certData);
				certBuffer.clear();
				
				int loop = (int) Math.ceil((double)(certLen + certPaddingLen) / (double)certMaxLen); // 244		
				int packetSize = 0;			
				int paddingSize = 0;
				int nextAddr = 0;
				int nextPadding = 0; 
				
				ByteBuffer packet;
				
				for(int l = 0; l < loop; l++) {
					
					if(l == 0) {
						
						packetSize = (certLen > certMaxLen - certPaddingLen) ? certMaxLen - certPaddingLen : certLen; // 인증서 길이	
						paddingSize = packetAddrLen + packetLen + certPaddingLen;
						
						byte[] cert = new byte[packetSize];
						certBuffer.get(cert); // packet size 만큼 인증서 가져오기						
						packet = ByteBuffer.allocate(packetSize + paddingSize);
						
						packet.put(DataUtil.get4ByteToInt(modemRomAddr)); // rom address
						packet.put(DataUtil.get2ByteToInt(packetSize + certPaddingLen)); // 검증코드(4) + 개인키길이(2) + 인증서(n)
						packet.put(modemValid); // 검증코드 (4) 
						packet.put(DataUtil.get2ByteToInt(certLen)); // 인증서 길이(2)
						packet.put(cert); // 인증서(n)
						
						nextPadding = certPaddingLen;
						
					} else {
						
						packetSize = (certLen > certMaxLen) ? certMaxLen : certLen;
						paddingSize = packetAddrLen + packetLen;
						
						byte[] cert = new byte[packetSize];
						certBuffer.get(cert); // packet size 만큼 인증서 가져오기
						
						packet = ByteBuffer.allocate(packetSize + paddingSize);
						
						packet.put(DataUtil.get4ByteToInt(modemRomAddr + nextAddr)); // rom address						
						packet.put(DataUtil.get2ByteToInt(cert.length)); // 인증서 길이(2)
						packet.put(cert); // 인증서(n)
						
						nextPadding = 0;
					}
					
					
					certLen = certLen - packetSize;
					nextAddr += packetSize + nextPadding;
					
					log.debug("## nextAddr " + nextAddr);
					
					packetList.add(packet.array());

//					for(int p = 0; p < packet.array().length; p++) {
//						log.debug(String.format("%02x ", packet.array()[p]&0xff));
//					}
				} 
				
			}

		} catch (Exception e) {
			log.error(e, e);
		}
	}
	
	private void getConnection(Target target) {
		
		closeConnect();
		
		try {
			if (!udpUse) {				
	            if (target.getIpv6Addr() != null && !"".equals(target.getIpv6Addr())) {
	                log.debug("DTLS-Sending.."+target.getIpv6Addr()+", DTLS-Port.."+target.getPort());
	                
	                if (Double.parseDouble(target.getFwVer()) >= fwVer)
	                    dtlsConnector = DtlsConnector.newDtlsClientConnector(false, target.getProtocol(), 0, true);
	                else
	                    dtlsConnector = DtlsConnector.newDtlsClientConnector(false, target.getProtocol(), 0, false);
	            }
	            else {
	                log.debug("DTLS-Sending.."+target.getIpAddr()+", DTLS-Port.."+target.getPort());
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
	            
	        } else {
	        	connector = new NioDatagramConnector();
                log.debug("[NICL][NETWORKTYPE]DEFAULT[UDP]...");
                connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
                connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new NiProtocolProvider()));
                connector.getFilterChain().addLast("logger", new LoggingFilter());
                connector.getFilterChain().addLast("Executor", new ExecutorFilter(Executors.newCachedThreadPool()));
                connector.setHandler(new NiProtocolHandler(true, this.getClass().getSimpleName() + ":" + DateTimeUtil.getCurrentDateTimeByFormat(null) + ":UDP"));
                
                log.debug("[Remote] UDP-Sending " +target.getIpAddr()+", UDP-Port.."+target.getPort());
                connFuture = connector.connect( new InetSocketAddress(target.getIpAddr(), target.getPort()));
                
	        }
		} catch (Exception e) {
			log.error(e, e);
		}
	}
    
	private void closeConnect() {
		
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
	}
 
}