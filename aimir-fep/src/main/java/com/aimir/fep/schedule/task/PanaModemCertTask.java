package com.aimir.fep.schedule.task;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
 
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.fep.bypass.BypassDevice;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.nip.CommandNIProxy;
import com.aimir.fep.protocol.nip.client.NiClient;
import com.aimir.fep.protocol.nip.client.actions.CommandActionResult;
import com.aimir.fep.protocol.nip.command.RawRomAccess;
import com.aimir.fep.protocol.nip.frame.GeneralFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameOption_Type;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NIAttributeId;
import com.aimir.fep.protocol.security.OacServerApi;
import com.aimir.fep.schedule.task.MeterTimeSyncSoriaTask.syncTimeThread;
import com.aimir.fep.util.CmdUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Modem;
import com.google.gson.JsonObject;

@Service
public class PanaModemCertTask 
{
    private static Log log = LogFactory.getLog(PanaModemCertTask.class);
    
    @Autowired
	private ModemDao modemDao;
    private IoSession session;
    
    public static void main(String[] args) {
    	
    	ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring-fep-schedule.xml"});
    	
    	DataUtil.setApplicationContext(ctx);
    	PanaModemCertTask task = ctx.getBean(PanaModemCertTask.class); 
    	
    	log.info("======================== PanaModemAuthTask start. ========================");
    	task.execute(args);
        log.info("======================== PanaModemAuthTask end. ========================");
        
        System.exit(0);
    }
    
    public void execute(String[] args) {        	
    	
    	String filename = "";
    	    	
    	try {
    		
    		if (args.length != 1 ) {    						
    			log.debug("not found export.xml");
	        } else {
	        	filename = args[0];
	        	log.debug("filename =====> " + filename);
	        }
    		    		
    		// 1. xml file read    	
        	List<String> deviceList = readDeviceXML(filename);
        	
        	for(String deviceId : deviceList) {
        		
        		log.info("deviceId : " + deviceId);
        		
        		Modem modem = modemDao.get(deviceId);
        		
        		if(modem != null) {
        			
        			String mdsId = String.valueOf(modem.getId());
        			
        			log.debug("mdsId : " + mdsId);
        			
        			if(modem.getModemType() == ModemType.SubGiga && modem.getProtocolType() == Protocol.IP) { // RF모뎀
        				sendCommand(mdsId);
        			}
        		}
        	}        	
        	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void sendCommand(String mdsId) {
    	
    	CommandNIProxy niProxy = new CommandNIProxy();
    	
    	// 커맨드 전송할 모뎀 조회
    	Modem modem = modemDao.get(Integer.parseInt(mdsId));
    	Target target;
    	
		try {
		
			target = CmdUtil.getNullBypassTarget(modem);
			
			GeneralFrame generalFrame = CommandNIProxy.setGeneralFrameOption(FrameOption_Type.Command, NIAttributeId.RawROMAccess);			
			generalFrame.setFrame();
			
			NiClient client = niProxy.getClient(target, generalFrame);
			log.debug("tempCode ClientHASh = "+ client.hashCode());
			
					
			BypassDevice bd = new BypassDevice();			
			bd.setModemId(target.getModemId());
			bd.setPacket_size(Integer.parseInt(FMPProperty.getProperty("ota.firmware.modem.packetsize.rf", "256")));
						
			Map<String, Object> conParams = new HashMap<String, Object>();
			conParams.put("NAME_SPACE", target.getNameSpace());
			conParams.put("COMMAND", "cmdRawROMAccessStart");
			conParams.put("BYPASS_DEVICE", bd);
			
			/*
			 * Return value setting
			 */
			CommandActionResult actionResult = new CommandActionResult();
			actionResult.setCommnad("cmdRawROMAccessStart");
			actionResult.setSuccess(false);
			actionResult.setResultValue("Modem ROM ACCESS Fail.");
			generalFrame.setCommandActionResult(actionResult);
			
			log.debug("[PanaModemCertTask][{}] Excute START :" + target.getModemId());
			generalFrame = client.sendCommand(target, generalFrame, null, "cmdRawROMAccessStart", conParams);
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
    
 
}