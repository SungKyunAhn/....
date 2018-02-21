package com.aimir.fep.schedule.task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.aimir.dao.device.MCUDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.fmp.exception.FMPMcuException;
import com.aimir.fep.protocol.security.OacServerApi;
import com.aimir.fep.util.CheckValidToCert;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.MCU;
import com.aimir.util.CalendarUtil;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.MD5Sum;
import com.google.gson.JsonObject;

@Service
public class PanaDCUCertRenewalTask {
	
	private static Log log = LogFactory.getLog(PanaDCUCertRenewalTask.class);
	
	public final static String CONTEXT_ROOT = FMPProperty.getProperty("feph.webservice.ota.context");
	public final static String FIRMWARE_IP = FMPProperty.getProperty("fep.ipv4.addr");
	public final static String FIRMWARE_PORT = FMPProperty.getProperty("ota.firmware.download.port");
	public final static String FIRMWARE_DIR = FMPProperty.getProperty("ota.firmware.download.dir") + "/cert/dcu";
	public final static String CERT3PASS_DIR = FMPProperty.getProperty("ota.firmware.download.dir") + "/cert/_3PASS";
	
	public final static String FIRMWARE_URL = "http://" + FIRMWARE_IP + ":" + FIRMWARE_PORT + "/" + CONTEXT_ROOT; 
	public final static int CERT_VALID_DAYS = 30;
	List<String> sendResultMap = new ArrayList<String>();
	
	@Autowired
	private CommandGW gw;

	@Autowired
	MCUDao mcuDao;
	 
	public static void main(String[] args) {

		ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "/config/spring-fep-schedule.xml" });

		DataUtil.setApplicationContext(ctx);
		PanaDCUCertRenewalTask task = ctx.getBean(PanaDCUCertRenewalTask.class);
		
		log.info("======================== PanaDCUCertRenewalTask start. ========================");
		log.info(FIRMWARE_DIR);
		task.execute(args);
		log.info("======================== PanaDCUCertRenewalTask end. ========================");
		
		System.exit(0);
	}

	public void execute(String[] args) {

		String filename = "";
		RESULT result = RESULT.FAIL;

		try {

			if (args.length != 1) {
				log.debug("not found export.xml");
				System.exit(0);
			} else {
				filename = args[0];
				createShell();
				log.debug("filename =====> " + filename);
			}

			// 1. xml file read
			List<String> deviceList = readDeviceXML(filename);
			int total = deviceList.size();
			int i = 0;
			
			for (String deviceId : deviceList) {

				log.debug("## sendCommand sysSerialNumber [" + deviceId + "] " + (i + 1) + "/" + total);
						
				MCU mcu = null;
				Set<Condition> conditions = new HashSet<Condition>();
                conditions.add(new Condition("sysSerialNumber", new Object[]{deviceId}, null, Restriction.EQ));
                List<MCU> mculist = mcuDao.findByConditions(conditions);
                if(mculist != null) { mcu = mculist.get(0); }
                
				if (mcu != null) {
					String mcuId = String.valueOf(mcu.getSysID());
					result = sendCommand(deviceId, mcuId);
					log.debug("## sendCommand [" + deviceId + "] sysid [" + mcuId + "] RESULT : [" + result + "]");
				} else {
					result = RESULT.FAIL;
					log.debug("## sendCommand [" + deviceId + "] IS NULL");
				}
				
				i++;
				
				sendResultMap.add( "[" + deviceId + "]" +  result); 
			}
			
			log.debug("========================================================");
			for(String _result : sendResultMap) {
        		log.debug("## SEND RESULT " + _result);
        	}
			log.debug("========================================================");

		} catch (Exception e) {
			log.error(e, e);
		}
	}

	private RESULT sendCommand(String deviceId, String mcuId) {
		
		RESULT result = RESULT.FAIL;
		String zipfile = "";
		String checksum = "";

		// 1. 인증서 조회
		int resultPana = getCertFile(deviceId, CertType.PANA, "get_pana_cert");
		//int result3Pass = getCertFile(deviceId, CertType.ECDSA_3PASS, "get_pana_cert"); // to do 나중에 변경
		
		// 2. pana 인증서 파일
		getCertFile3PASS(deviceId);
		
		if (resultPana > -1) {
		
			// make checksum
			makeCheckSum(deviceId);
						
			// make zipfile
			zipfile = makeZip(deviceId);
			String downurl = FIRMWARE_URL + "/cert/dcu/" + deviceId + "/"+ deviceId +".tar.gz";
						 
			try {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					log.error(e, e);
				} 
				checksum = MD5Sum.getFileMD5Sum(new File(zipfile));
			} catch (IOException e1) {
				log.debug("## checksum file make error");
			}
			
			log.debug("## cmdReqCertRenewal [" + deviceId + "] mcuId [" + mcuId + "] downurl [" + downurl + "] checksum [" + checksum + "]");
		
			try {			
				cmdReqCertRenewal(mcuId, deviceId, downurl, checksum);
				result = RESULT.SUCCESS;
			} catch (FMPMcuException e) {
				result = RESULT.FAIL;
				log.error(e, e);
			} catch (Exception e) {
				result = RESULT.FAIL;
				log.error(e, e);
			}
		}
		return result;
	}

	private void cmdReqCertRenewal(String mcuId, String deviceId, String fileUrl, String checksum) throws FMPMcuException, Exception {
		try {
			gw.cmdReqCertRenewal(mcuId, deviceId, fileUrl, checksum);
		} finally {
			gw.close();
		}
	}

	/**
	 * readPanaXML
	 */
	private List<String> readDeviceXML(String filename) {

		InputSource is;
		List<String> deviceList = new ArrayList<String>();

		try {
			if (filename != null) {
				is = new InputSource(new FileReader(filename));
				Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

				// xpath 생성
				XPath xpath = XPathFactory.newInstance().newXPath();

				String expression = "//*/dc_child";
				NodeList cols = (NodeList) xpath.compile(expression).evaluate(document, XPathConstants.NODESET);

				for (int i = 0; i < cols.getLength(); i++) {
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

	/**
	 * getCertFile
	 * 
	 * @param certUrl, certType, deviceId
	 */
	public int getCertFile(String deviceId, CertType certType, String certUrl) {

		int result = 0;
		JsonObject json;

		OacServerApi api = new OacServerApi();

		try {
			json = api.oacServerApi(certUrl +"/" + deviceId, null);

			if (json != null && json.get("result_code") != null) {

				result = json.get("result_code").getAsInt();

				log.debug("## getCertFile RESULT [" + result + "]");

				if (result == 0) { // success
					
					String cert = json.get("cert").getAsString();							
		    		String key = json.get("key").getAsString();
		    		String caCert = json.get("caCert").getAsString();
		    		String rootCert = json.get("rootCert").getAsString();
					
					if(certType == CertType.ECDSA_3PASS) {						
						
						String salt = ""; // todo change
						// String salt = new String(Base64.decodeBase64(json.get("salt").getAsString().getBytes()));					
						writeFile(deviceId, certType, CertFlag.CERT, cert);
						writeFile(deviceId, certType, CertFlag.KEY, key);
						writeFile(deviceId, certType, CertFlag.CA_CERT, caCert);
						writeFile(deviceId, certType, CertFlag.ROOT_CERT, rootCert);
						writeFile(deviceId, certType, CertFlag.SALT, salt);		
						
					} else {
						
						writeFile(deviceId, certType, CertFlag.CERT, cert);
						writeFile(deviceId, certType, CertFlag.PANA_KEY, key);
						writeFile(deviceId, certType, CertFlag.CA_CERT, caCert);
						writeFile(deviceId, certType, CertFlag.ROOT_CERT, rootCert);
						
						tmpMakeDir(deviceId, certType.ECDSA_3PASS);  // to do delete after test
					}
					
					log.debug("## getCertFile RESULT [" + cert + "]");
				}
			}

		} catch (Exception e) {
			log.error(e, e);
			log.error(e, e);

			result = -1;
		}

		return result;
	}
	
	private void writeFile(String deviceId, CertType certType, CertFlag certflag, String dat) {
		
		String filepath = FIRMWARE_DIR + "/" + deviceId + "/" + deviceId + "/"+ certType.getCertTypeName() + "/";
		String filename = deviceId;
		String fileext = certflag.getCertFlagExt();
		
		String savefile = filepath + filename + fileext;

		log.debug("## writeFile savefile : " + savefile);
		
		byte[] _dat = Base64.decodeBase64(dat.getBytes());

		try {
			Path path = Paths.get(savefile);

			if (!Files.exists(path)) {
				Files.createDirectories(path.getParent());
				Files.createFile(path);
			}
			
			FileOutputStream fileOuputStream = new FileOutputStream(savefile); 
			fileOuputStream.write(_dat);
			fileOuputStream.close();			

		} catch (FileNotFoundException e) {
			log.error(e, e);
		} catch (Exception e) {
			log.error(e, e);
		}
	}
	
	private void getCertFile3PASS(String deviceId) {
		
		String filepath = CERT3PASS_DIR + "/" + deviceId + "/";		
		String copypath = FIRMWARE_DIR + "/" + deviceId + "/" + deviceId + "/"+ CertType.ECDSA_3PASS.getCertTypeName() + "/";
		
		Path path = Paths.get(filepath);
		
		String fileCert = deviceId + CertFlag.CERT.getCertFlagExt();
		String fileKey = deviceId + CertFlag.KEY.getCertFlagExt();
		String fileSalt = deviceId + CertFlag.SALT.getCertFlagExt();
		String fileCA = deviceId + CertFlag.CA_CERT.getCertFlagExt();
		String fileRootCA = deviceId + CertFlag.ROOT_CERT.getCertFlagExt();
		
		if (Files.exists(path)) {						
			
			Path sourceFileCert = Paths.get(filepath + fileCert);
			Path sourceFileKey = Paths.get(filepath + fileKey);
			Path sourceFileSalt = Paths.get(filepath + fileSalt);
			Path sourceFileCA = Paths.get(filepath + fileCA);
			Path sourceFileRootCA = Paths.get(filepath + fileRootCA);
			 
			Path targetFileCert = Paths.get(copypath + fileCert);
			Path targetFileKey = Paths.get(copypath + fileKey);
			Path targetFileSalt = Paths.get(copypath + fileSalt);
			Path targetFileCA = Paths.get(copypath + fileCA);
			Path targetFileRootCA = Paths.get(copypath + fileRootCA);
			
			try {
				// copypath directory
				if (!Files.exists(Paths.get(copypath))) {
					Files.createDirectories(Paths.get(copypath));
				}
				
				Files.copy(sourceFileCert, targetFileCert, StandardCopyOption.REPLACE_EXISTING);
				Files.copy(sourceFileKey, targetFileKey, StandardCopyOption.REPLACE_EXISTING);
				Files.copy(sourceFileSalt, targetFileSalt, StandardCopyOption.REPLACE_EXISTING);
				Files.copy(sourceFileCA, targetFileCA, StandardCopyOption.REPLACE_EXISTING);
				Files.copy(sourceFileRootCA, targetFileRootCA, StandardCopyOption.REPLACE_EXISTING);
				
			} catch (IOException e) {				
				log.error(e, e);
			}			
		} // end if
	}
	
	/**
	 * tmpMakeDir 3pass directory
	 * @param deviceId
	 * @param certType
	 */
	private void tmpMakeDir(String deviceId, CertType certType) {
		
		String filepath = FIRMWARE_DIR + "/" + deviceId + "/" + deviceId + "/"+ certType.getCertTypeName() + "/";
		String filename = deviceId;
		
		String savefile = filepath;

		log.debug("## writeFile savefile : " + savefile);

		try {
			Path path = Paths.get(savefile);

			if (!Files.exists(path)) {
				Files.createDirectories(path);				
			}

		} catch (FileNotFoundException e) {
			log.error(e, e);
		} catch (Exception e) {
			log.error(e, e);
		}
	}
	
	private void makeCheckSum(String deviceId) {

		String filepath = FIRMWARE_DIR + "/" + deviceId + "/";		
		String filename = "checksum.txt";		
		String savefile = filepath + filename;
				
		String[] checksum = new String[9];
		String[] checksum_file = new String[9];
		
		String cert3pass = FIRMWARE_DIR + "/" + deviceId + "/" + deviceId + "/"+ CertType.ECDSA_3PASS.getCertTypeName() + "/" + deviceId;
		String certpana = FIRMWARE_DIR + "/" + deviceId + "/" + deviceId + "/"+ CertType.PANA.getCertTypeName() + "/" + deviceId;
		
		String checksum_3pass = deviceId + "/" + CertType.ECDSA_3PASS.getCertTypeName() + "/" + deviceId;
		String checksum_pana = deviceId + "/" + CertType.PANA.getCertTypeName() + "/" + deviceId;
		
		log.debug("## makeCheckSum savefile : " + savefile);
		
		try {
			
			checksum[0] = MD5Sum.getFileMD5Sum(new File(cert3pass + CertFlag.CERT.getCertFlagExt()));
			checksum[1] = MD5Sum.getFileMD5Sum(new File(cert3pass + CertFlag.KEY.getCertFlagExt()));
			checksum[2] = MD5Sum.getFileMD5Sum(new File(cert3pass + CertFlag.SALT.getCertFlagExt()));
			checksum[3] = MD5Sum.getFileMD5Sum(new File(cert3pass + CertFlag.CA_CERT.getCertFlagExt()));
			checksum[4] = MD5Sum.getFileMD5Sum(new File(cert3pass + CertFlag.ROOT_CERT.getCertFlagExt()));
			
			checksum[5] = MD5Sum.getFileMD5Sum(new File(certpana + CertFlag.CERT.getCertFlagExt()));
			checksum[6] = MD5Sum.getFileMD5Sum(new File(certpana + CertFlag.PANA_KEY.getCertFlagExt()));			
			checksum[7] = MD5Sum.getFileMD5Sum(new File(certpana + CertFlag.CA_CERT.getCertFlagExt()));
			checksum[8] = MD5Sum.getFileMD5Sum(new File(certpana + CertFlag.ROOT_CERT.getCertFlagExt()));
			
			checksum_file[0] = checksum_3pass + CertFlag.CERT.getCertFlagExt();
			checksum_file[1] = checksum_3pass + CertFlag.KEY.getCertFlagExt();
			checksum_file[2] = checksum_3pass + CertFlag.SALT.getCertFlagExt();
			checksum_file[3] = checksum_3pass + CertFlag.CA_CERT.getCertFlagExt();
			checksum_file[4] = checksum_3pass + CertFlag.ROOT_CERT.getCertFlagExt();
			
			checksum_file[5] = checksum_pana + CertFlag.CERT.getCertFlagExt();
			checksum_file[6] = checksum_pana + CertFlag.PANA_KEY.getCertFlagExt();
			checksum_file[7] = checksum_pana + CertFlag.CA_CERT.getCertFlagExt();
			checksum_file[8] = checksum_pana + CertFlag.ROOT_CERT.getCertFlagExt();
					
			StringBuffer checksumBuffer = new StringBuffer();
			
			for(int i = 0; i < checksum.length; i++) {
				checksumBuffer.append(checksum[i] + "  " + checksum_file[i] + "\n");				
			}
			
			Path path = Paths.get(savefile);

			if (!Files.exists(path)) {
				Files.createDirectories(path.getParent());
				Files.createFile(path);
			}
			
			BufferedWriter writer = Files.newBufferedWriter(path);
			writer.write(checksumBuffer.toString());
			writer.close();
			
		} catch (IOException e) {
			log.error(e, e);
		} catch (Exception e) {
			log.error(e, e);
		}
		
	}

	private String makeZip(String deviceId) {

		String command = "sh " + FIRMWARE_DIR + "/cmdZipCertfile.sh " + deviceId;
		String zipfile = FIRMWARE_DIR + "/" + deviceId + "/" + deviceId + ".tar.gz";

		Process p = null;

		try {

			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				log.debug(line);
			}

		} catch (Exception e) {
			log.error(e, e);
		}

		log.debug("## makeZip zipfile : " + zipfile);
		return zipfile;
	}
	
	/**
	 * pana 인증서 유효성 체크
	 * @param deviceId
	 * @return
	 */
	public boolean checkValidCert(String deviceId) {
		
		boolean isValid = false;
		
		String filename = FIRMWARE_DIR + "/" + deviceId + "/" + deviceId + "/"+ CertType.PANA.getCertTypeName() + "/" + deviceId + CertFlag.CERT.getCertFlagExt();
		
		X509Certificate certificate = CheckValidToCert.checkValidCert(filename);
		String expiredDate = DateUtil.formatDate(certificate.getNotAfter(), "YYYYMMDD");		
		String today = CalendarUtil.getCurrentDate();
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		 
		try {
			
			Date beginDate = formatter.parse(today);
			Date endDate = formatter.parse(expiredDate);			
			long diff = endDate.getTime() - beginDate.getTime();
			long diffDays = diff / (24 * 60 * 60 * 1000);
			
			if(diffDays > CERT_VALID_DAYS) {
				isValid = true;
			}
			
			log.debug("## checkValidCert [" + deviceId + "] today [" + today + "] expiredDate [" + expiredDate + "] diffDays [" + diffDays + "] isValid [" + isValid + "]");
			
		} catch (ParseException e) {
			log.error(e, e);
		}
		
		return isValid;
	}

	public enum CertType {
		
		ECDSA_3PASS("0", "3PASS"), PANA("1", "PANA");

		private String code;
		private String name;

		CertType(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCertType() {
			return code;
		}

		public String getCertTypeName() {
			return name;
		}

		public static CertType getCertType(String certType) {
			for (CertType m : CertType.values()) {
				if (m.getCertType().equals(certType))
					return m;
			}
			return CertType.PANA;
		}

	}

	public enum CertFlag {
		
		CERT("0", ".der"), PANA_KEY("1", ".key"), KEY("2", ".key"), CA_CERT("3", "CA.der"), ROOT_CERT("4", "RootCA.der"), SALT("5", ".salt");
		
		private String code;
		private String ext;

		CertFlag(String code, String ext) {
			this.code = code;
			this.ext = ext;
		}

		public String getCertFlag() {
			return code;
		}
		
		public String getCertFlagExt() {
			return ext;
		}

		public static CertFlag getCertFlag(String certFlag) {
			for (CertFlag m : CertFlag.values()) {
				if (m.getCertFlag().equals(certFlag))
					return m;
			}
			return CertFlag.CERT;
		}
	}

	public enum RESULT {
		
		SUCCESS(0), FAIL(-1);

		private int code;

		RESULT(int code) {
			this.code = code;
		}

		public int getRESULT() {
			return code;
		}

		public static RESULT getRESULT(int result) {
			for (RESULT m : RESULT.values()) {
				if (m.getRESULT() == result)
					return m;
			}
			return RESULT.SUCCESS;
		}
	}
	
	public void createShell() {
		
		String savefile = FIRMWARE_DIR + "/cmdZipCertfile.sh";
		Path path = Paths.get(savefile);
		
		try {
		
			if (!Files.exists(path)) {			
				Files.createDirectories(path.getParent());			
				Files.createFile(path);
			}
			StringBuffer buffer = new StringBuffer();
			
			buffer.append("#!/bin/sh\n");
			buffer.append("filename=\"$1\"\n");
			buffer.append("cd "+ FIRMWARE_DIR +"/" +"\"$filename\"\n");
			buffer.append("tar -zcvf \"$filename\".tar.gz \"$filename\" checksum.txt\n");
			
			BufferedWriter writer = Files.newBufferedWriter(path);
			writer.write(buffer.toString());
			writer.close();
			
			Process p = null;

			try {
				String command = "chmod 755 " + savefile;
				p = Runtime.getRuntime().exec(command);
				p.waitFor();
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

				String line = "";
				while ((line = reader.readLine()) != null) {
					log.debug(line);
				}

			} catch (Exception e) {
				log.error(e, e);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}