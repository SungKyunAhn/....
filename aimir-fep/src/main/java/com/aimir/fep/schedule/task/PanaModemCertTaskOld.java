package com.aimir.fep.schedule.task;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.nip.CommandNIProxy;
import com.aimir.fep.protocol.nip.command.RawRomAccess;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NIAttributeId;
import com.aimir.fep.protocol.security.OacServerApi;
import com.aimir.fep.schedule.task.MeterTimeSyncSoriaTask.syncTimeThread;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Modem;
import com.google.gson.JsonObject;

@Service
public class PanaModemCertTaskOld 
{
    private static Log log = LogFactory.getLog(PanaModemCertTaskOld.class);
    
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
    
    @Autowired
    private CommandGW gw;
    
    @Autowired
	private ModemDao modemDao;
    
    public static void main(String[] args) {
    	
    	ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring-fep-schedule.xml"});
    	
    	DataUtil.setApplicationContext(ctx);
    	PanaModemCertTaskOld task = ctx.getBean(PanaModemCertTaskOld.class); 
    	
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
        				getPanaCert(deviceId, mdsId);
        			}
        		}
        	}        	
        	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				
				if (result == 0) { // success
		    		
					String cert = json.get("cert").getAsString();
		    		String key = json.get("key").getAsString();
		    		String caCert = json.get("caCert").getAsString();
		    		String rootCert = json.get("rootCert").getAsString();
		    		
		    		String[] arrCert = {cert, key, caCert, rootCert};
		    		
					Map<String, String> _result = gw.cmdRawROMAccess(mdsId, "SET", arrModemCert, arrModemValid, arrCert);
					
					log.debug("cmdResult =====> " + _result.get("cmdResult"));
    			}
			} 
			
		} catch (Exception e) {
			log.error(e, e);			
		} 
		
		log.debug("========== getPanaCert END ============");
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