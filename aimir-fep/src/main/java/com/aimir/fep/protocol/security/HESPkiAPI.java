/**
 * 
 */
package com.aimir.fep.protocol.security;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.util.Hex;
import com.penta.nuritelecom.nuriclient;

/**
 * @author simhanger
 *
 */
public class HESPkiAPI {
	private static Logger logger = LoggerFactory.getLogger(HESPkiAPI.class);
	private String decPriKeyPath;
	private String decCertKeyPath;
	private String decSaltPath;
	private nuriclient iNuriClient;

	public HESPkiAPI() {
		Properties prop = new Properties();
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("config/fmp.properties"));
		} catch (IOException e) {
			logger.error("Properties loading error - {}", e.getMessage());
		}
		iNuriClient = new nuriclient();
		
		decPriKeyPath = prop.getProperty("protocol.security.hes.decPriKeyPath"); // /home/aimir1/aimiramm/penta/ECDSA/000H000000000001/000H000000000001.key
		decCertKeyPath = prop.getProperty("protocol.security.hes.decCertKeyPath"); // /home/aimir1/aimiramm/penta/ECDSA/000H000000000001/000H000000000001.der
		decSaltPath = prop.getProperty("protocol.security.hes.decSaltPath"); // /home/aimir1/aimiramm/penta/ECDSA/000H000000000001/000H000000000001.salt
		
		logger.debug("######### decPriKeyPath={}", decPriKeyPath);
		logger.debug("######### decCertKeyPath={}", decCertKeyPath);
		logger.debug("######### decSaltPath={}", decSaltPath);
	}

	public byte[] file2byte(String filepath) {
		byte[] array = "".getBytes();
		try {
			Path fPath = Paths.get(filepath);
			array = Files.readAllBytes(fPath);
		} catch (IOException e) {
			logger.error("File to byte[] Error - {}", e);
		}

		return array;
	}

	/*
	 * CINFO
	 */
	public byte[] getCInfo(String filepath) {
		byte[] cinfoArray = "".getBytes();

		try {
			Path fPath = Paths.get(filepath);
			String fileName = fPath.getFileName().toString();
			String cinfo = fileName.substring(0, fileName.lastIndexOf("."));
			cinfoArray = cinfo.getBytes();
			logger.debug("[cinfo         ] => {}", cinfo);
		} catch (Exception e) {
			logger.error("CInfo to byte[] Error - {}", e);
		}

		return cinfoArray;
	}

	@SuppressWarnings("static-access")
	public byte[] doPkiDecrypt(byte[] cipher_pki, byte[] pinCodeArray) {
		logger.debug("### cipher_pki => {}, cipher_pki.length => {}", Hex.decode(cipher_pki), cipher_pki.length);
		logger.debug("### pinCode => {}, pinCodeLength => {}", Hex.decode(pinCodeArray), pinCodeArray.length);

		byte[] cinfo = getCInfo(decPriKeyPath);
		logger.debug("### cinfo => {}, cinfoLength => {}", Hex.decode(cinfo), cinfo.length);

		byte[] salt = file2byte(decSaltPath);
		logger.debug("[decSaltPath   ] => {}[{}]", decSaltPath, Hex.decode(salt));
		logger.debug("### salt => {}, saltLength => {}", Hex.decode(salt), salt.length);

		byte[] prikey_buffer_pki = file2byte(decPriKeyPath);
		logger.debug("[decPriKeyPath ] => {}[{}]", decPriKeyPath, Hex.decode(prikey_buffer_pki));
		logger.debug("### prikey_buffer_pki => {}, prikeyBbufferPkiLength => {}", Hex.decode(prikey_buffer_pki), prikey_buffer_pki.length);

		byte[] cert_buffer_pki = file2byte(decCertKeyPath);
		logger.debug("[decCertKeyPath] => {}[{}]", decCertKeyPath, Hex.decode(cert_buffer_pki));
		logger.debug("### cert_buffer_pki => {}, certBufferPkiLength => {}", Hex.decode(cert_buffer_pki), cert_buffer_pki.length);

		byte[] plain = new byte[100];
		int ret = iNuriClient.IS_IoT_PKI_Decrypt_forClient(
				  plain, new Integer(plain.length)     // 펜타씨큐리티에서 만들때 내부적으로 static으로 설정되어있는것 같음. Integer로 새로생성하지 않으면 지옥을 맛보게 됨. ㅡ,.ㅡ;
				, cipher_pki, cipher_pki.length
				, pinCodeArray, pinCodeArray.length
				, cinfo, cinfo.length
				, salt, salt.length
				, prikey_buffer_pki
				, prikey_buffer_pki.length
				, cert_buffer_pki, cert_buffer_pki.length);

		byte[] resultArra = new byte[16];
		System.arraycopy(plain, 0, resultArra, 0, resultArra.length);

		logger.debug("## [iNuriClient.IS_IoT_PKI_Decrypt_forClient] Ret={}, Plain Text={}, Length={}", ret, Hex.decode(resultArra), resultArra.length);
		return resultArra;
	}
}
