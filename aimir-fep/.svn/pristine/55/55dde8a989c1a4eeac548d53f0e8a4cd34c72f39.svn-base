/**
 * 
 */
package com.aimir.fep.protocol.nip.client.actions;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.californium.elements.RawData;
import org.eclipse.californium.scandium.DTLSConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.bypass.BypassDevice;
import com.aimir.fep.protocol.nip.CommandNIProxy;
import com.aimir.fep.protocol.nip.client.actions.NI_cmdModemOTAStart_Action_SP.NeedImangeBlockTransferRetry;
import com.aimir.fep.protocol.nip.client.multisession.MultiSession;
import com.aimir.fep.protocol.nip.command.RawRomAccess;
import com.aimir.fep.protocol.nip.common.GeneralDataFrame;
import com.aimir.fep.protocol.nip.common.MultiDataProcessor;
import com.aimir.fep.protocol.nip.exception.NiException;
import com.aimir.fep.protocol.nip.frame.GeneralFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameControl_Ack;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameOption_Type;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NIAttributeId;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.protocol.nip.frame.payload.Firmware;
import com.aimir.fep.protocol.security.OacServerApi;
import com.aimir.fep.trap.actions.SP.EV_SP_200_63_0_Action;
import com.aimir.fep.trap.actions.SP.EV_SP_200_65_0_Action;
import com.aimir.fep.trap.actions.SP.EV_SP_200_66_0_Action;
import com.aimir.fep.trap.common.EV_Action.OTA_UPGRADE_RESULT_CODE;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.Modem;
import com.aimir.util.DateTimeUtil;
import com.google.gson.JsonObject;

public class NI_cmdRawROMAccessStart_Action_SP extends NICommandAction {
	
	private static Logger logger = LoggerFactory.getLogger(NI_cmdRawROMAccessStart_Action_SP.class);
	
	public static final byte[] modemCertAddr = {0x00, 0x0f, 0x50, 0x00};
    public static final byte[] modemKeyAddr = {0x00, 0x0f, 0x58, 0x06};
    public static final byte[] modemCAAddr = {0x00, 0x0f, 0x5E, 0x12};
    public static final byte[] modemRootCertAddr = {0x00, 0x0f, 0x66, 0x18};
    public static final Object[] arrModemCert = {modemCertAddr, modemKeyAddr, modemCAAddr, modemRootCertAddr};
    
    public static final byte[] modemCertValid = {0x12, 0x12, 0x12, 0x12};
    public static final byte[] modemKeyValid = {0x34, 0x34, 0x34, 0x34};
    public static final byte[] modemCAValid = {0x56, 0x56, 0x56, 0x56};
    public static final byte[] modemRootCertValid = {0x78, 0x78, 0x78, 0x78};
    public static final Object[] arrModemValid = {modemCertValid, modemKeyValid, modemCAValid, modemRootCertValid};
    
    List<byte[]> packetList = new ArrayList<byte[]>();
	
	private TargetClass tClass;

	/************** Please don't change ***********************************/
	private final int NEED_IMAGE_BLOCK_TRANSFER_MAX_RETRY_COUNT = 5;
	private final int SEND_IAMGE_RETRY_TIMEOUT = 17;
	/**********************************************************************/

	private String actionTitle = "NI_cmdRawROMAccessStart_Action_SP";

	public NI_cmdRawROMAccessStart_Action_SP() {
		actionTitle += "_" + DateTimeUtil.getCurrentDateTimeByFormat(null);
		logger.debug("### Action Title = {}", actionTitle);
	}

	@Override
	public String getActionTitle() {
		return actionTitle;
	}

	@Override
	public Object executeStart(MultiSession session, GeneralFrame generalFrame) throws Exception {
		
		/*
		 * 1. F/W Image 준비
		 */
		long startTime = System.currentTimeMillis();
		BypassDevice bd = session.getBypassDevice();		
		bd.setStartOTATime(startTime);
		
		ModemDao modemDao = DataUtil.getBean(ModemDao.class);
		Modem modem = modemDao.get(bd.getModemId());
		
		/*
		 * 2. make packet list 
		 */
		getPanaCert(modem.getDeviceSerial(), String.valueOf(modem.getId()));
		
		byte[] packet = (byte[])packetList.get(0);
		
		/*
		 * 2. Frame 구성 및 첫번째 Request  - Upgrade Start Request
		 */
		NIAttributeId command = NIAttributeId.RawROMAccess;
		GeneralFrame newGFrame = CommandNIProxy.setGeneralFrameOption(FrameOption_Type.Command, command);
		newGFrame.setFrame();
		newGFrame.setNetworkType(generalFrame.getNetworkType());
		newGFrame.setNIAttributeId(command.getCode());
		
		HashMap info = new HashMap();
		info.put("data", packet);
		byte[] sendData = new GeneralDataFrame().make(newGFrame, info);
		
		session.write(sendData);
		logger.debug("### [Upgrade Start Request] Session write => {}", newGFrame.toString());

		CommandActionResult actionResult = generalFrame.getCommandActionResult();
		actionResult.setSuccess(true);
		actionResult.setResultValue("Proceeding...");

		tClass = TargetClass.valueOf(modem.getModemType().name());
		
		logger.debug("### session info => {}", session);
		logger.debug("### session connected => {}", session.isConnected());
		
		
		return null;
	}
	
	@Override
	public Object executeTransaction(MultiSession session, GeneralFrame gFrame) throws Exception {
		return null;
	}

	@Override
	public void executeAck(MultiSession session, GeneralFrame generalFrame) throws Exception {
		
		BypassDevice bd = session.getBypassDevice();
		logger.debug("### [cmdModemOTA Received] ModemId={}, MeterId={}, GeneralFrame = {}", bd.getModemId(), bd.getMeterId(), generalFrame.toString());
		
		/*
		 * 보냈던 sequence number가 아니면 동일한 블럭을 재전송한다.
		 */
		int receivedSeq = generalFrame.getFrameSequence();
		int sendedSeq = bd.getFrameSequence();
		if(receivedSeq == sendedSeq) {
			
			byte[] packet = (byte[])packetList.get(bd.getNextFrameSequence());
			if(packet != null) {
			
				NIAttributeId command = NIAttributeId.RawROMAccess;
				GeneralFrame newGFrame = CommandNIProxy.setGeneralFrameOption(FrameOption_Type.Command, command);
				newGFrame.setFrame();
				newGFrame.setNetworkType(generalFrame.getNetworkType());
				newGFrame.setNIAttributeId(command.getCode());
				newGFrame.setPayloadData(packet);
				
				byte[] generalFrameData = newGFrame.encode(null);
	
				try {
					session.write(generalFrameData);

					logger.debug("### [Upgrade End Request] Session write => [{}][{}]", newGFrame.toString(), Hex.decode(generalFrameData));
					
				} catch (Exception e) {
					
					throw new Exception("Send CMD EXEC Error - ", e);
				}
			} else {
				deleteMultiSession(session);
			}
			
		} else {
			logger.warn("Invalid frame sequence received. SendedFrameSeq=[{}], ReceivedFrameSeq=[{}]", sendedSeq, receivedSeq);
		}
	}

	@Override
	public void executeStop(MultiSession session) throws Exception {
		logger.debug("call executeStop1 - Modem={}", session.getBypassDevice().getModemId());
		deleteMultiSession(session);
		logger.debug("call executeStop2 - Modem={}", session.getBypassDevice().getModemId());
	}
	
	@Override
	public void executeResponse(MultiSession session, GeneralFrame generalFrame) throws Exception {
		
		BypassDevice bd = session.getBypassDevice();
		logger.debug("### [cmdModemOTA Received] ModemId={}, MeterId={}, GeneralFrame = {}", bd.getModemId(), bd.getMeterId(), generalFrame.toString());
		
		/*
		 * 보냈던 sequence number가 아니면 동일한 블럭을 재전송한다.
		 */
		int receivedSeq = generalFrame.getFrameSequence();
		int sendedSeq = bd.getFrameSequence();
		if(receivedSeq == sendedSeq) {
			
			byte[] packet = (byte[])packetList.get(bd.getNextFrameSequence());
			if(packet != null) {
			
				NIAttributeId command = NIAttributeId.RawROMAccess;
				GeneralFrame newGFrame = CommandNIProxy.setGeneralFrameOption(FrameOption_Type.Command, command);
				newGFrame.setFrame();
				newGFrame.setNetworkType(generalFrame.getNetworkType());
				newGFrame.setNIAttributeId(command.getCode());
				newGFrame.setPayloadData(packet);
				
				byte[] generalFrameData = newGFrame.encode(null);
	
				try {
					session.write(generalFrameData);

					logger.debug("### [Upgrade End Request] Session write => [{}][{}]", newGFrame.toString(), Hex.decode(generalFrameData));
					
				} catch (Exception e) {
					
					throw new Exception("Send CMD EXEC Error - ", e);
				}
			} else {
				deleteMultiSession(session);
			}
			
		} else {
			logger.warn("Invalid frame sequence received. SendedFrameSeq=[{}], ReceivedFrameSeq=[{}]", sendedSeq, receivedSeq);
		}
	}
	
	
	/**
     * getPanaCert
     * @param deviceId
     */
    public void getPanaCert(String deviceId, String mdsId) {
    	
    	logger.debug("========== getPanaCert START ============");
    	
    	JsonObject json;
    	
    	OacServerApi api = new OacServerApi();
    	
		try {	
			json = api.oacServerApi("get_pana_cert/" + deviceId, null);
			
			if (json != null && json.get("result_code") != null ) {				
				
				int result  = json.get("result_code").getAsInt();
				
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
			logger.error("getPanaCert", e);			
		} 
		
		logger.debug("========== getPanaCert END ============");
    }
    
    /**
	 * NI 프로토콜을 통한 커맨드 전송 : Raw ROM Access 설정 모뎀아이디, 요청타입(get/set),
	 * set인경우 설정할 값
	 **/
	public void makePacketList(String mdsId, Object[] arrModemRomAddr, Object[] arrModemValid, String[] arrCert) throws Exception {
		
		// 결과 맵
		Map map = new HashMap<String, String>();
		
		// 커맨드 전송할 모뎀 조회
		ModemDao modemDao = DataUtil.getBean(ModemDao.class);
		Modem modem = modemDao.get(Integer.parseInt(mdsId));
		
		int packetMaxLen = 250;
		int packetAddrLen = 4;
		int packetLen = 2;
		
		int certMaxLen = 244;		
		int certPaddingLen = 6;
		
		try {
			
			for(int i = 0; i < arrCert.length; i++) {
				
				int modemRomAddr = DataUtil.getIntTo4Byte((byte[])arrModemRomAddr[i]); // rom address
				byte[] modemValid = (byte[])arrModemValid[i]; // valid check value
				String certData	= arrCert[i];
				int certLen = certData.getBytes().length;
				
				ByteBuffer certBuffer = ByteBuffer.allocate(certLen); // 인증서 buffer
				certBuffer.put(certData.getBytes());
				certBuffer.clear();
				
				int loop = (int) Math.ceil((double)(certLen + certPaddingLen) / (double)certMaxLen); // 244		
				int packetSize = 0;			
				int paddingSize = 0;
				int nextAddr = 0;
				
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
						packet.put(DataUtil.get2ByteToInt(cert.length)); // 인증서 길이(2)
						packet.put(cert); // 인증서(n)
						
					} else {
						
						packetSize = (certLen > certMaxLen) ? certMaxLen : certLen;
						paddingSize = packetAddrLen + packetLen;
						
						byte[] cert = new byte[packetSize];
						certBuffer.get(cert); // packet size 만큼 인증서 가져오기
						
						packet = ByteBuffer.allocate(packetSize + paddingSize);
						
						packet.put(DataUtil.get4ByteToInt(modemRomAddr + nextAddr)); // rom address						
						packet.put(DataUtil.get2ByteToInt(cert.length)); // 인증서 길이(2)
						packet.put(cert); // 인증서(n)
					}
					
					certLen = certLen - packetSize;
					nextAddr += packetSize + certPaddingLen;
					
					packetList.add(packet.array());
				}			
			}			
		} catch (Exception e) {
			logger.error("error : " + e);
		}
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
			logger.error("error : " + e);
		} catch (SAXException e) {
			logger.error("error : " + e);
		} catch (IOException e) {
			logger.error("error : " + e);
		} catch (ParserConfigurationException e) {
			logger.error("error : " + e);
		} catch (XPathExpressionException e) {
			logger.error("error : " + e);
		}
		
		return deviceList;
    }   
}
