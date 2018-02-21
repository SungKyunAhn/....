package com.aimir.fep.protocol.security;

import com.aimir.constants.CommonConstants.ThresholdName;
import com.aimir.fep.protocol.security.frame.AuthFrameConstants;
import com.aimir.fep.protocol.security.frame.FrameType;
import com.aimir.fep.protocol.security.frame.StatusCode;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.threshold.CheckThreshold;
import com.google.gson.JsonObject;
import com.penta.nuritelecom.nuritelecom;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
//INSERT START 2016.11.17 SP-318
import java.security.cert.CertificateExpiredException;
import java.util.Date;
import java.util.Calendar;
import org.apache.commons.lang.time.DateUtils;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.constants.CommonConstants.TargetClass;
// INSERT END  2016.11.17 SP-318
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class AuthISIoTClient {
    private static Log logger = LogFactory.getLog(AuthISIoTClient.class);

    static final int IOT_SUCCESS = 0;
    static final int IOT_FAIL = 1;
	// INSERT START 2016.11.17 SP-318
	static final int IOT_UPDATE = 2;
	static final int IOT_ENCRIPT_ERROR = -2;
	// INSERT END   2016.11.17 SP-318

    static final int CMD_AUTH_SPMODEM_SUCCESS = 2;
    static final int CMD_AUTH_SPMODEM_FAIL = 0;
    
    nuritelecom iNuriServer = null; 
    
	static final int RANDAM_LEN = 16;
	static final int PINCODE_LEN = 16;
	static final int SESSIONKEY_LEN = 16;
	
	public enum IoT_Function {
		IS_IoT_Make_Init_Token,
		IS_IoT_Make_Stoken,
		IS_IoT_Verify_Init_Token,
		IS_IoT_Verify_SToken,
		IS_IoT_Verify_Ctoken,
		IS_IoT_Get_Random,
		IS_IoT_Make_CToken,
		IS_IoT_Encrypt,
		IS_IoT_Decrypt,
		IS_IoT_PKI_Encrypt,
		IS_IoT_PKI_Decrypt,
		IS_IoT_Make_OrgMsg_For_Stoken,
		IS_IoT_SignedData_For_Stoken,
		IS_IoT_Make_Stoken_With_Sigdata
	}
	
	// Status of Device Communication
	private ArrayList<FrameType> receivedAuthFrameTypes = new ArrayList<FrameType>();  // FrameType of Received Frame ( from Device )
	private ArrayList<FrameType> sendedAuthFrameTypes = new ArrayList<FrameType>();       // FrameType of last Send Frame

	private ArrayList<HashMap>  iotFunctionCallHistory = new ArrayList<HashMap>();
	
	// OAC Server WebService IPAddress 
//	private String oacServerIpAddress = FMPProperty.getProperty("protocol.security.oacserver.webservice.ipaddress",
//			"127.0.0.1");
//	private String oacServerURL = FMPProperty.getProperty("protocol.security.oacserver.webservice.url",
//			"http://127.0.0.1/oac/api/");
	// Server information
	private String serverInfoString =  FMPProperty.getProperty("protocol.security.server.info","S012387-7654321");
	private byte[] serverInfo = null;   // s_info
	private Integer serverInfoLen = 0;
	
	// Server Randam
	// get from IS_IoT_Get_Random();
	private byte[] serverRandom = null; // s_randam
	private Integer serverRandomLen = 0;
    
	// get from IS_Iot_Verify_Token()
	private byte[] 		clientRandom = null;	//c_randam
	private Integer    	clientRandomLen = 0;
	private byte[] 		clientInfo  = null;  // c_Info
	private Integer    	clientInfoLen = 0;
	// device serial
	private byte[]	deviceSerial = null;
	
	// client-certificate
	private byte[]	clientCertBuff = null;
	private Integer	clientCertBuffLen = 0;
	
	// ip address
	private String	ipv6 = null;		// INSERT SP-193
	
	// get https://<IP_ADDRESS>/api/get_pin_argument/<CINFO>
	private byte[] pinCode = null; 
	private Integer    pinCodeLen = 0; 
	
	// Server certificate file path
    private String serverCertFilePath =  FMPProperty.getProperty("protocol.security.server.certificate.file" , 
    									"/home/aimir1/aimiramm/penta/ECDSA/cert3.der");
					
    // Server Signing　key file path
	String  serverPrivatekeyFilePath = FMPProperty.getProperty("protocol.security.server.privatekey.file",
			"/home/aimir1/aimiramm/penta/ECDSA/cert3.key");
	
	// Server Token
	private byte[] ServerToken = null;

	private int    ServerTokenLen = 0;
	// get CRL by API 
    byte[] CRL = null;
    Integer CRLLen = 0;
    // Temporary read from file
	String crlFilePath = FMPProperty.getProperty("protocol.security.crl.filepath", 
								"/home/aimir1/aimiramm/penta/ECDSA/crlTest.crl");
	
	// CA Certificate file path
	String caCertFilePath = FMPProperty.getProperty("protocol.security.ca.certificate.file",
			"/home/aimir1/aimiramm/penta/ECDSA/ca_1.der");

	String	rootcaCertFilePath = FMPProperty.getProperty("protocol.security.rootca.certificate.file",
    		"/home/aimir1/aimiramm/penta/ECDSA/rootCA_0.der");

    // Session Key
    byte[] SKEY= null; 
    Integer SKEYLen = 0;
	
    // PKY Certificate file path
    String pkiSertFilePath = FMPProperty.getProperty("protocol.security.pki.certificate.file",
    		"=/home/aimir1/aimiramm/penta/ECDSA/cert3.der");
    
    // PKI Private Key file path 
    String 	pkiPrivateKeyFilePath = FMPProperty.getProperty("protocol.security.pki.privatekey.file",
    		"/home/aimir1/aimiramm/penta/ECDSA/cert5.key");

    // SignedData_For_Stoken password
    byte[] signedpassWord = null;
    Integer signedpassWordLen = 0;
    // PKI_Encrypt password
    byte[] passWord = null;
    Integer passWordLen = 0;
    
    byte[] cipher = null;
 
	Integer ciperLen = 0;

	//=> INSERT START 2016.11.17 SP-318
	Integer renewalDays = Integer.parseInt(FMPProperty.getProperty("protocol.security.certificate.renewaldays", "30"));
	boolean renewalFlg = false;
	byte[] renewalCert = null;
	byte[] renewalKey = null;
	byte[] renewalSalt = null;
	byte[] renewalPayload = null;
	//=> INSERT END   2016.11.17 SP-318
    
    public AuthISIoTClient(){
    	iNuriServer = new nuritelecom();
    	
    	serverInfo = serverInfoString.getBytes();
    	serverInfoLen = new Integer(serverInfo.length);
    }

    
    public StatusCode cinfoRequest(byte[] initToken ) {
    	StatusCode retVal = StatusCode.LocalError;
    	int status;
    	
    	status = call_IS_IoT_Verify_Init_Token(initToken);
    	if ( status != IOT_SUCCESS ){
    		// error   		
    		return retVal;
    	}
    	
    	// get PinCode
    	//=> SP-121: UPDATE START
    	status = getPinCode(clientInfo);
    	if ( status != 0) {
    		 //error
    		 logger.error("Get PinCode Fail");
    		 SendCmdAuthSPModem(CMD_AUTH_SPMODEM_FAIL);
    		 SendEvent();
    		 return retVal;
    	}
    	//this.pinCode = "P000000-0000001".getBytes(); // FAT
		//this.pinCodeLen = this.pinCode.length;
		logger.debug("PinCode : " + this.pinCode + "(" + this.pinCodeLen +")" );
     	//=> SP-121: UPDATE END
    	 
    	 // get Server Random
    	 byte[] s_random = new byte[32];
    	 Integer s_random_len = s_random.length;
    	 status = call_IS_IoT_Get_Random(s_random, s_random_len); 
    	 
    	 if ( status !=  IOT_SUCCESS ){
    		 SendCmdAuthSPModem(CMD_AUTH_SPMODEM_FAIL);
    		 SendEvent();
    		 return retVal;
    	 }
    	 
    	 this.serverRandom = copyByteArray(s_random, s_random_len);
    	 this.serverRandomLen = s_random_len;
      	//##############################
      	// for Test
 		//byte[] dummy =  new byte[16];
 		//for ( int i = 0; i < 16; i++ ){
    	// dummy[i] = (byte) i ;
 		//}
 		//this.serverRandom = copyByteArray(dummy, dummy.length);
 		//this.serverRandomLen = dummy.length;
     	//##############################
    	 
     	status = getSignedDataPassword();
     	if ( status != IOT_SUCCESS ){
     		// error
     		logger.error("GET SignedDataPassword Fail");
 	   		SendCmdAuthSPModem(CMD_AUTH_SPMODEM_FAIL);
 	   		SendEvent();
     		return retVal;
     	}

    	 status = call_IS_IoT_Make_Stoken2();
    	 if ( status != IOT_SUCCESS) {
    		 SendCmdAuthSPModem(CMD_AUTH_SPMODEM_FAIL);
    		 SendEvent();
    		 return retVal;
    	 }
    	 
    	 retVal = StatusCode.Success;
    	 return retVal;
    }
    
    public StatusCode certificateRequest(byte[] clientToken) throws Exception
    {
    	StatusCode retVal = StatusCode.LocalError;
    	int status;
    	
    	// check 
    	if ( isIotFunctionCalled(IoT_Function.IS_IoT_Make_Stoken) == false &&
    			isIotFunctionCalled(IoT_Function.IS_IoT_Make_Stoken_With_Sigdata) == false ){
    		logger.error("IS_Iot_Make_Stoken() was not called");
	   		 SendCmdAuthSPModem(CMD_AUTH_SPMODEM_FAIL);
	   		 SendEvent();
    		return retVal;
    	}
    	
    	status = getCRL();
    	if ( status != IOT_SUCCESS ){
    		// error 
    		logger.error("GET CRL Fail");
	   		 SendCmdAuthSPModem(CMD_AUTH_SPMODEM_FAIL);
	   		 SendEvent();
    		return retVal;
    	}
    	
    	status = call_IS_IoT_Verify_Ctoken(clientToken, clientToken.length);
    	// FOR FAT
    	//if ( status != IOT_SUCCESS ){
    	if ( status != IOT_SUCCESS && status != -9 ){
    	// FOR FAT
        	 // error
    		//=> UPDATE START 2016.11.17 SP-318
    		//SendCmdAuthSPModem(CMD_AUTH_SPMODEM_FAIL);
    		//SendEvent();
    		//return StatusCode.ClientCertificateUnverified;
    		if ( status != IOT_UPDATE ) {
	    		SendCmdAuthSPModem(CMD_AUTH_SPMODEM_FAIL);
	    		if ( status == -316 ) {
	    			// Certificate Expired
	    			SendEvent("Network Authentication Failure(Certificate period was expired.)");
	    			return StatusCode.ClientCertificateExpired;
	    		} else {
	    			// other error
	    			SendEvent();
	    			return StatusCode.ClientCertificateUnverified;
	    		}
    		} else {
    			// renewal certificate
    			return StatusCode.Success;
    		}
     		//=> UPDATE END   2016.11.17 SP-318
    	}
    	logger.debug("IS_IoT_Verify_Ctoken()status=" + status);
    	
    	status = getPassword();
    	if ( status != IOT_SUCCESS ){
    		// error
    		logger.error("GET Passwrod Fail");
	   		SendCmdAuthSPModem(CMD_AUTH_SPMODEM_FAIL);
	   		SendEvent();
    		return retVal;
    	}
    	
    	status = call_IS_IoT_PKI_Encrypt();
    	if ( status != IOT_SUCCESS ){
    		// error
	   		 SendCmdAuthSPModem(CMD_AUTH_SPMODEM_FAIL);
	   		 SendEvent();
     		return StatusCode.ClientCertificateUnverified;
    	}
	   	 retVal = StatusCode.Success;
	   	 return retVal;
	   	 
    }
    
    private int  call_IS_IoT_Get_Random(byte[] random, Integer random_len) 
    {
    	int ret;
    	logger.debug("-------------IS_IoT_Get_Random Start-----------------");

    	ret = iNuriServer.IS_IoT_Get_Random(random, random_len);

    	if ( ret == IOT_SUCCESS ){    	
	    	logger.debug("randam : " + PrintHex(random,random_len));
	    	logger.debug("randam_len : " + random_len);
    	}
    	else{
			String ErrorCode = ISIoTErrorCode.Get_Random.NORMAL.getErrorMessage(ret);
			logger.error("IS_IoT_Get_Random() fail. retCode = " + ret +","+ ErrorCode);
			// error
    	}
    	addIotFunctionCallHistory(IoT_Function.IS_IoT_Get_Random, ret );
    	logger.debug("-------------IS_IoT_Get_Random End-----------------");
    	return ret;
    }
    
    private int  call_IS_IoT_Verify_Init_Token( byte[] initToken ){
    	
    	logger.debug("----------  IS_IoT_Verify_Init_Token Start ----------");
    	
    	byte[] init_token = initToken;
    	Integer init_token_len = init_token.length;

    	byte[] c_random_s = new byte[128]; // client randam data
    	Integer c_random_len_s = new Integer(c_random_s.length);
    	byte[] cinfo_s = new byte[128];
    	Integer cinfo_len_s = new Integer(cinfo_s.length);
    	logger.debug("InitToken(Length=" + initToken.length + ") : " + Hex.getHexDump(initToken));

    	//
    	int ret = iNuriServer.IS_IoT_Verify_Init_Token(
    			init_token,init_token_len
    			,c_random_s,c_random_len_s
    			,cinfo_s,cinfo_len_s);


    	if ( ret != IOT_SUCCESS ){
    		return ret;
    	}
    	//##################################
    	this.clientRandom = copyByteArray(c_random_s, c_random_len_s);
    	this.clientRandomLen = c_random_len_s;
    	//=> SP-121 FOR FAT last byte is null
    	this.clientInfo = copyByteArray(cinfo_s, cinfo_len_s);
    	this.clientInfoLen = cinfo_len_s;
//        byte[] cinfo_n = copyByteArray(cinfo_s, cinfo_len_s);
//        byte[] cinfo_c = new byte[128];
//        int i =0;
//        for (i = 0; i < cinfo_len_s; i++ )
//        {
//                if ( cinfo_n[i] != 0x00 ) {
//                        cinfo_c[i] = cinfo_n[i];
//                }else{
//                        break;
//                }
//        }
//        this.clientInfo = cinfo_c;
//        this.clientInfoLen = i;
    	//=> SP-121 FOR FAT
    	// for Test
    	
//		byte[] dummy =  new byte[16];
//		for ( int i = 0; i < 16; i++ ){
//			dummy[i] = (byte)( i + 10 );
//		}
//		this.clientRandom = dummy;
//		this.clientRandomLen = dummy.length;
//		
//		byte[] cinfo = "CLIENT-INFO".getBytes();
//		this.clientInfo = cinfo;
//		this.clientInfoLen = cinfo.length;
		//###################################

    	logger.debug("clientRandom:" + PrintHex(this.clientRandom,this.clientRandomLen));
    	logger.debug("clientRandomLen:" + this.clientRandomLen);
    	logger.debug("clientInfo:" + PrintHex(this.clientInfo,this.clientInfoLen));
    	logger.debug("clientInfoLen:" + this.clientInfoLen);

    	
    	logger.debug("----------------IS_IoT_Verify_Init_Token End-----------------");
    	addIotFunctionCallHistory(IoT_Function.IS_IoT_Verify_Init_Token, ret );
    	return ret;
    }
    
    private int call_IS_IoT_Make_Stoken()
    {
    	logger.debug("-------------IS_IoT_Make_Stoken Start -----------------");
    	
    	byte[] pincode_s = this.pinCode;
    	Integer pincode_len_s = PINCODE_LEN; /* fixed */
    	byte[] certificatefilepath_s =  this.serverCertFilePath.getBytes();
    	byte[] privatekeyfilepath_s =   this.serverPrivatekeyFilePath.getBytes();
    	byte[] stoken = new byte[1000];
    	Integer stoken_len = 1000;
	
    	logger.debug("clientRandm(" + this.clientRandomLen + "):" + PrintHex(this.clientRandom, this.clientInfoLen));
    	logger.debug("serverRandam(" + this.serverRandomLen + "):" + PrintHex(this.serverRandom, this.serverRandomLen));
    	logger.debug("serverInfo("+ this.serverInfoLen + "):" + PrintHex(this.serverInfo, this.serverInfoLen));
    	logger.debug("pinCode(" + this.pinCodeLen + "):" + PrintHex(this.pinCode, this.pinCodeLen));
    	logger.debug("serverCertFilePath:" + this.serverCertFilePath);
    	logger.debug("serverPrivatekeyFile:" + this.serverPrivatekeyFilePath);
    	
    	//2 IS_IOT_Make_Stoken
    	int ret = iNuriServer.IS_IoT_Make_Stoken(
    			this.clientRandom, this.clientRandomLen   // Cramdan,
    			, this.serverRandom, this.serverRandomLen // Srandam IS_IoT_Get_Random()
    			, this.serverInfo, this.serverInfoLen     // Sinfo
    			, this.pinCode, this.pinCodeLen           // PinCode
    			, certificatefilepath_s     // 
    			, privatekeyfilepath_s      // 
    			, stoken, stoken_len);      //　(OUT) Server Token

    	if ( ret == IOT_SUCCESS){
        	//######################
    		this.ServerToken = copyByteArray(stoken, stoken_len);
    		this.ServerTokenLen = stoken_len;
        	// for test
//    		byte[] dummy = new byte[256];
//    		for ( int i = 0; i < 256; i++ ){
//    			dummy[i] = (byte) i ;
//    		}
//    		this.ServerToken = dummy;
//    		this.ServerTokenLen = dummy.length;
        	//######################

    		logger.debug("ServerToken(" + ServerTokenLen +"):" + PrintHex(ServerToken,ServerTokenLen));
    	}
    	else {
			String ErrorCode = ISIoTErrorCode.Make_Stoken.NORMAL.getErrorMessage(ret);
			logger.error("IS_IoT_Make_Stoken() failed. retCode = " + ret +","+ ErrorCode);
    	}
    	addIotFunctionCallHistory(IoT_Function.IS_IoT_Make_Stoken, ret );
    	logger.debug(" -------------IS_IoT_Make_Stoken End-----------------");
    	return ret;
    }
    
    private int call_IS_IoT_Make_Stoken2()
    {
    	logger.debug(" -------------IS_IoT_Make_OrgMsg_For_Stoken Start-----------------");
    	byte[] orgmsg = new byte[1000];
    	Integer orgmsg_len = orgmsg.length;

    	int ret = iNuriServer.IS_IoT_Make_OrgMsg_For_Stoken(
    			this.serverRandom, this.serverRandomLen //s_random, s_random_len
    			, this.clientRandom, this.clientRandomLen // c_random_s, c_random_len_s
    			, this.serverInfo, this.serverInfoLen // sinfo_s, sinfo_len_s
    			, this.pinCode, PINCODE_LEN // pincode_s, pincode_len_s
    			, orgmsg, orgmsg_len);
    	
    	addIotFunctionCallHistory(IoT_Function.IS_IoT_Make_OrgMsg_For_Stoken, ret );
    	if ( ret != IOT_SUCCESS ){
			String ErrorCode = ISIoTErrorCode.Make_OrgMsg_For_Stoken.NORMAL.getErrorMessage(ret);
    		logger.error("IS_IoT_Make_OrgMsg_For_Stoken()failed. retCode=" + ret +","+ ErrorCode);
    		return ret;
    	}
    	
    	logger.debug("orgmsg:" + PrintHex(orgmsg,orgmsg_len));
	    logger.debug("orgmsg_len:" + orgmsg_len);
    	
    	logger.debug(" -------------IS_IoT_Make_OrgMsg_For_Stoken End-----------------");
    	
    	logger.debug("-------------IS_IoT_SignedData_For_Stoken Start-----------------");
    	byte[] passwd = this.signedpassWord;
    	Integer passwd_len = this.signedpassWordLen;
    	byte[] signedData = new byte[1000];
    	Integer signedData_len = 1000;

    	byte[] privatekeyfilepath_s = this.serverPrivatekeyFilePath.getBytes();

    	logger.debug("serverPrivatekeyFile:" + this.serverPrivatekeyFilePath);
    	logger.debug("passwd(" + passwd_len + "):" + PrintHex(passwd, passwd_len));
    	
    	ret = iNuriServer.IS_IoT_SignedData_For_Stoken(
    			orgmsg, orgmsg_len
    			, privatekeyfilepath_s
    			, passwd, passwd_len
    			, signedData, signedData_len);

    	addIotFunctionCallHistory(IoT_Function.IS_IoT_SignedData_For_Stoken, ret );
    	if ( ret != IOT_SUCCESS ){
			String ErrorCode = ISIoTErrorCode.SignedData_For_Stoken.NORMAL.getErrorMessage(ret);
     		logger.error("IS_IoT_SignedData_For_Stoken()failed. retCode=" + ret +","+ ErrorCode);
    		return ret;
    	}
    	
    	logger.debug("signedData(" + signedData_len +"):" + PrintHex(signedData,signedData_len));
    	logger.debug("-------------IS_IoT_SignedData_For_Stoken End-----------------");   	

    	logger.debug("-------------IS_IoT_Make_Stoken_With_Sigdata Start-----------------");
    	byte[] stoken2 = new byte[1000];
    	Integer stoken2_len = 1000;
    	byte[] certificatefilepath_s =  this.serverCertFilePath.getBytes();
    	
       	logger.debug("serverRandam(" + this.serverRandomLen + "):" + PrintHex(this.serverRandom, this.serverRandomLen));
    	logger.debug("serverInfo("+ this.serverInfoLen + "):" + PrintHex(this.serverInfo, this.serverInfoLen));
    	logger.debug("serverCertFilePath:" + this.serverCertFilePath);
    	logger.debug("pinCode(" + this.pinCodeLen + "):" + PrintHex(this.pinCode, this.pinCodeLen));
    	
    	ret = iNuriServer.IS_IoT_Make_Stoken_With_Sigdata(
    			this.serverRandom, this.serverRandomLen //s_random, s_random_len
    			, this.serverInfo, this.serverInfoLen //sinfo_s, sinfo_len_s
    			, certificatefilepath_s
    			, this.pinCode, PINCODE_LEN //pincode_s, pincode_len_s
    			, signedData, signedData_len
    			, stoken2, stoken2_len);

    	addIotFunctionCallHistory(IoT_Function.IS_IoT_Make_Stoken_With_Sigdata, ret );
    	if ( ret != IOT_SUCCESS ){
			String ErrorCode = ISIoTErrorCode.Make_Stoken_With_Sigdata.NORMAL.getErrorMessage(ret);
			logger.error("IS_IoT_Make_Stoken_With_Sigdata()failed. retCode=" + ret +","+ ErrorCode);
    		return ret;
    	}
    	logger.debug("stoken2:" + PrintHex(stoken2,stoken2_len));
    	logger.debug("stoken2_len:" + stoken2_len);
		this.ServerToken = copyByteArray(stoken2, stoken2_len);
		this.ServerTokenLen = stoken2_len;
		logger.debug("ServerToken(" + ServerTokenLen +"):" + PrintHex(ServerToken,ServerTokenLen));
    	logger.debug(" - -------------IS_IoT_Make_Stoken_With_Sigdata End-----------------");
    	
    	return ret;
    }
    
    private int call_IS_IoT_Verify_Ctoken(byte[] clientToken, Integer clientTokenLen)
    {
    	int retVal = IOT_FAIL;
    	byte[] SKEY= new byte[20]; 
    	Integer SKEY_len = SKEY.length;
    	byte[] cert_buffer = new byte[4096];
    	Integer cert_buffer_len = cert_buffer.length;
    	byte[] rootCA = file2byte(rootcaCertFilePath);
    	Integer rootCA_len = rootCA.length;
    	
    	logger.debug("-------------IS_IoT_Verify_Ctoken Start-----------------");
    	logger.debug("clientToken("+clientTokenLen+"):" + PrintHex(clientToken,clientTokenLen));
    	logger.debug("clientRandom("+clientRandomLen+"):" + PrintHex(clientRandom,clientRandomLen));
    	logger.debug("serverRandom("+serverRandomLen+"):" + PrintHex(serverRandom, serverRandomLen));
    	logger.debug("CRL(" + this.CRLLen + "):" + PrintHex(this.CRL, this.CRLLen));
    	logger.debug("rootCA(" + rootCA_len + "):" + PrintHex(rootCA, rootCA_len));
    	logger.debug("caCertFilePath:" + caCertFilePath);

    	retVal = iNuriServer.IS_IoT_Verify_Ctoken
    			(clientToken, clientTokenLen   // 
			      , clientRandom, clientRandomLen  //CRandam
			      , serverRandom, serverRandomLen      //Srandam
 			      , CRL, CRLLen                 //CRL
			      //, new byte[]{} , 0            //CRL ---> FOR FAT
			      , caCertFilePath.getBytes()   //CA Sertificate File path
			      , rootCA, rootCA_len
			      , SKEY, SKEY_len                 //SessionKey
			      , cert_buffer, cert_buffer_len); // 

     	if ( retVal == IOT_SUCCESS || retVal == -9 ){
     		if ( CRLLen > 0 && cert_buffer_len > 0 ){
    	    	byte[] certBuffer = copyByteArray(cert_buffer, cert_buffer_len);
	     		try {
		     		//crl Validation one more
		     		InputStream fr = new ByteArrayInputStream(certBuffer);
		     	    CertificateFactory cf =
		                    CertificateFactory.getInstance("X.509");
		     		X509Certificate cert = (X509Certificate)cf.generateCertificate(fr);
		     		//=> INSERT START 2016.11.17 SP-318
		     		//check Vaildity
		     		try{
                    	cert.checkValidity();
                    	logger.error("The certificate is Valid ");
                    }
                    catch (CertificateExpiredException cee) {
                		retVal = -361;
                       	logger.error("The certificate is expired ");
                    }
		     		if ( retVal != -361 ) {
		     			// UPDATE START 2016.12.06 SP-318
		     			//Date renewalTime = new Date(System.currentTimeMillis() + renewalDays*(1000*60*60*24));
		     			Date now = new Date(System.currentTimeMillis());
		     			Calendar cal = Calendar.getInstance();
		     	        cal.setTime(now);
		     	        cal.add(Calendar.DATE, renewalDays);
		     	        Date renewalTime = cal.getTime();
		     	        // UPDATE END  2016.12.06 SP-318
		     			Date renewalDate = DateUtils.truncate(renewalTime, Calendar.DAY_OF_MONTH);
		     			Date untilDate = cert.getNotAfter();
		     			// UPDATE START 2016.12.06 SP-318
		     			//if ( untilDate.after(renewalDate) ){ 
	            		if ( untilDate.before(renewalDate) ){ 
	            		// UPDATE END 2016.12.06 SP-318	
	                		this.renewalFlg = true;
	                    	logger.error("The certificate is expired. Update certificate.");
	                    	retVal = IOT_SUCCESS;
	                	}
		     		}
		     		//=> INSERT END   2016.11.17 SP-318
		     		
		     		fr = new ByteArrayInputStream(CRL);
		     		X509CRL crl2 = (X509CRL)cf.generateCRL(fr);
		     		
		     		if(crl2.isRevoked(cert))
		     		{
		     			logger.error("CRL certificate is revoked");
		     			retVal = -361; // IS_IoT_Verify_Ctoken 와 동일한 폐지오류코드를 출력
		     		}
	     		}
		     	catch (Exception e){
		     		logger.error("error occured in Certificate Check");
		     		logger.debug(e,e);
		     		retVal = IOT_FAIL;
		     	}
     		}
     	}
    	// FOR FAT
    	//if ( retVal == IOT_SUCCESS ){
     	if ( retVal == IOT_SUCCESS || retVal == -9 ){
    	// FOR FAT
	    	//#####################################
		    logger.debug("SKEY:" + PrintHex(SKEY,SKEY_len));
		    logger.debug("SKEY_len:" + SKEY_len);
		    logger.debug("cert_buffer:" + PrintHex(cert_buffer,cert_buffer_len));
		    logger.debug("cert_buffer_len:" + cert_buffer_len);
	    	 
	    	this.SKEY = copyByteArray(SKEY, SKEY_len);
	    	this.SKEYLen = SKEY_len;
	    	this.clientCertBuff = cert_buffer;
	    	this.clientCertBuffLen = cert_buffer_len;
	    	
//	    	this.SKEY = "0123456789ABCDEF".getBytes();
//	    	this.SKEYLen = this.SKEY.length;
	       	//=> INSERT START 2016.11.17 SP-318
	    	if ( this.renewalFlg ) {
	    		retVal = IOT_UPDATE;
	    	}
	    	//=> INSERT END   2016.11.17 SP-318
    	}
    	else {
			String ErrorCode = ISIoTErrorCode.Verify_Ctoken.NORMAL.getErrorMessage(retVal);
    		logger.error("IS_IoT_Verify_Ctoken()failed. retCode=" + retVal + "," + ErrorCode);
    	}
    	logger.debug("JAVA - -------------IS_IoT_Verify_Ctoken End-----------------");
       	addIotFunctionCallHistory(IoT_Function.IS_IoT_Verify_Ctoken, retVal );
    	return retVal;
    }
	 
    private int call_IS_IoT_PKI_Encrypt()
    {
    	logger.debug("-------------IS_IoT_PKI_Encrypt Start-----------------");
    	byte[] cipher_pki = new byte[1000];
    	Integer cipher_pki_len = cipher_pki.length;
    	
    	//byte[] cert_buffer_pki = file2byte(pkiSertFilePath);
    	//Integer cert_buffer_pki_len = cert_buffer_pki.length;
    	byte[] prikey_buffer_pki = file2byte(pkiPrivateKeyFilePath);
    	Integer prikey_buffer_pki_len = prikey_buffer_pki.length;
    	
    	logger.debug("pkySertFilePath:" +pkiSertFilePath);
    	//logger.debug("cert_buffer_pki("+cert_buffer_pki_len+"):"+PrintHex(cert_buffer_pki,cert_buffer_pki_len));
    	logger.debug("this.clientCertBuffLen("+this.clientCertBuffLen+"):"+PrintHex(this.clientCertBuff,this.clientCertBuffLen));
    	logger.debug("pkiPrivateKeyFilePath:"+pkiPrivateKeyFilePath);
    	logger.debug("prikey_buffer_pki"+prikey_buffer_pki_len+"):" + PrintHex(prikey_buffer_pki,prikey_buffer_pki_len) );
    	logger.debug("passWord("+passWordLen+"):" + PrintHex(passWord, passWordLen));
    	
    	int retVal = iNuriServer.IS_IoT_PKI_Encrypt
    			(this.SKEY, this.SKEYLen // session key
    	//				, cert_buffer_pki, cert_buffer_pki_len  // 
    					, this.clientCertBuff, this.clientCertBuffLen
    					, prikey_buffer_pki, prikey_buffer_pki_len  // 
    					, this.passWord, this.passWordLen           //
    					, cipher_pki, cipher_pki_len);
    	if ( retVal == IOT_SUCCESS ){
    		logger.debug("IS_IoT_PKI_Encrypt() SUCCESS");
	    	logger.debug("cipher_pki:" + PrintHex(cipher_pki,cipher_pki_len));
	    	logger.debug("cipher_pki_len:" + cipher_pki_len);
	    	this.cipher = copyByteArray(cipher_pki,cipher_pki_len );
	    	this.ciperLen = cipher_pki_len;
    	}
    	else{
			String ErrorCode = ISIoTErrorCode.PKI_Encrypt.NORMAL.getErrorMessage(retVal);
			logger.error("IS_IoT_PKI_Encrypt() fail. retCode = " + retVal +","+ ErrorCode);
    	}
       	addIotFunctionCallHistory(IoT_Function.IS_IoT_PKI_Encrypt, retVal );
    	logger.debug("-------------IS_IoT_PKI_Encrypt End-----------------");
    	return retVal;
    }
    
    private int getPinCode(byte[] cinfo) 
    {
    	logger.debug(" getPinCode Start ");
    	int retVal = IOT_FAIL;
//    	//#####################
//    	// for test
//    	this.pinCode = "PINCODE".getBytes();
//    	this.pinCodeLen = this.pinCode.length;
//    	return IOT_SUCCESS;
//    	//#####################
    	try {
    		String str = new String(cinfo, "UTF-8");
            OacServerApi api  = new OacServerApi();
    		JsonObject json = api.oacServerApi("get_pin_argument/" + str, null);
	    	if ( json != null && json.get("result_code") != null ){ 
		    	int result  = json.get("result_code").getAsInt();
		    	if ( result  == 0 ){ // success
		    		String pin_arg = json.get("pin_arg").getAsString();
		    		//this.pinCode = pin_arg.getBytes();
		    		this.pinCode = Hex.encode(pin_arg);
		    		this.pinCodeLen = this.pinCode.length;
		    		logger.debug("PinCode : " + pin_arg);
		    		retVal = IOT_SUCCESS;
		    	}
	    	}
    	}
    	catch ( Exception e){
    		logger.error(e,e);
    	}
    	return retVal;
    }
    
    public HashMap <String, String>  getMeterSharedKey(String serialNo){
    	HashMap <String, String> resultMap = null; 
    	try {
            OacServerApi api  = new OacServerApi();
    		JsonObject json = api.oacServerApi("get_sharedkey_and_pin_argument/" + serialNo, null);
    		if ( json == null || json.get("resul_tcode") == null ){
    			logger.error("get_sharedkey_and_pin_argument fail.");
    		}
    		else {
	    		int result  = json.get("result_code").getAsInt();
	    		if ( result  != 0  ){
	    			logger.error("get_sharedkey_and_pin_argument fail. result = " + result);
	    		}
	    		else {
		    		// success
		    		JsonObject sharedKeyObj = json.getAsJsonObject("shared_key");
		    		if ( sharedKeyObj != null ){ 
		    			resultMap = new HashMap <String, String>();
		    			String masterKey = sharedKeyObj.get("master_key") == null ? "" :sharedKeyObj.get("master_key").getAsString();
		    			String unicastKey = sharedKeyObj.get("encryption_unicast_key") == null ? "" : sharedKeyObj.get("encryption_unicast_key").getAsString();
		    			String multicastKey = sharedKeyObj.get("hls_secret_unique_key") == null ? "" : sharedKeyObj.get("encryption_global_key").getAsString();
		    			String authenticationKey = sharedKeyObj.get("authentication_key") == null ? "" :  sharedKeyObj.get("authentication_key").getAsString();
		    			resultMap.put("MasterKey", masterKey);
		    			resultMap.put("UnicastKey", unicastKey);
		    			resultMap.put("MulticastKey", multicastKey);
		    			resultMap.put("AuthenticationKey", authenticationKey);
			    		logger.debug("MasterKey:"+ masterKey + ", UnicastKey:" + unicastKey + ", MulticastKey:" + multicastKey + ", AuthenticationKey:" + authenticationKey);
		    		}
	    		}
    		}
    	}
    	catch ( Exception e){
    		logger.error(e,e);
    	}
	    return resultMap;
    }
    
    private int getCRL()
    {
    	this.CRL = file2byte(crlFilePath);
    	this.CRLLen = CRL.length;
    	return IOT_SUCCESS;
    }
    
    private int getSignedDataPassword()
    {
    	String signedpasswordStr = 
    			FMPProperty.getProperty("protocol.security.signedpassword",
    			"12345678");
   
    	this.signedpassWord = signedpasswordStr.getBytes();
    	this.signedpassWordLen = this.signedpassWord.length;
//    	String hexString = "35386435386162653334653737666363356430626632633165393162663763343330626533373162643264306239373964646266626364646463636261313366";
//    	this.passWord = Hex.encode(hexString);
//    	this.passWordLen = this.passWord.length;
    	return IOT_SUCCESS;
    }
    private int getPassword()
    {
    	String passwordStr = 
    			FMPProperty.getProperty("protocol.security.password",
    			"1234");
   
    	this.passWord = passwordStr.getBytes();
    	this.passWordLen = this.passWord.length;
//    	String hexString = "35386435386162653334653737666363356430626632633165393162663763343330626533373162643264306239373964646266626364646463636261313366";
//    	this.passWord = Hex.encode(hexString);
//    	this.passWordLen = this.passWord.length;
    	return IOT_SUCCESS;
    }
//	private JsonObject oacServerApi(String function, String params ) throws Exception 
//	{ 
//	   	URL url = null;   	
//	   	//=> UPDATE 
//		//String baseURL = "https://" + oacServerIpAddress + "/api/" + function;
//	   	String baseURL = oacServerURL + function;
//	   	HttpURLConnection urlConnection = null;
//		JsonObject  json = null;
//		try {
//			String sendURL = null;
//			if ( params != null && params.length() < 0 ){
//				sendURL = baseURL + "&" + params;
//			}
//			else {
//				sendURL = baseURL;
//			}
//			logger.info(sendURL);
//		 	    
//		 	url = new URL(sendURL);
//			urlConnection = (HttpURLConnection)url.openConnection();
//			
//			logger.debug("Connection Timeout[" + urlConnection.getConnectTimeout() + "]");
//			urlConnection.setConnectTimeout(10 * 1000);
//			urlConnection.setRequestMethod("GET");
//			
//			logger.debug(urlConnection.getResponseCode());
//			logger.debug(urlConnection.getResponseMessage());
//			
//			if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//              	 InputStreamReader isr = new InputStreamReader(urlConnection.getInputStream(),
//                         StandardCharsets.UTF_8);
//                 try {
//                	 BufferedReader reader = new BufferedReader(isr);
//                	 
//                	 StringBuilder sb = new StringBuilder();
//                	 int cp;
//                	 while ((cp = reader.read()) != -1) {
//                	      sb.append((char) cp);
//                	 }
//                	 String  jsonText =  sb.toString();
//                	 
//                	 JsonParser jp = new JsonParser();
//                	 JsonElement element = jp.parse(jsonText);
//                	  json = element.getAsJsonObject();
//                	 logger.debug("Json=" + json.toString());
//                 } finally {
//                	 isr.close();
//                 }
//			}
//		}
//		catch (Exception e) {
//			logger.error(e,e);
//		}
//		return json;
//    }	

	
	/**
	 * @return the serverToken
	 */
	public byte[] getServerToken() {
		return ServerToken;
	}

	/**
	 * @param serverToken the serverToken to set
	 */
	public void setServerToken(byte[] serverToken) {
		ServerToken = serverToken;
	}
    
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String PrintHex(byte[] bytes, int bytes_len)
    {
      char[] hexChars = new char[bytes.length * 2];
      for ( int j = 0; j < bytes_len; j++ ) {
        int v = bytes[j] & 0xFF;
        hexChars[j * 2] = hexArray[v >>> 4];
        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
      }
      return "HEX(" + bytes_len + ")" + new String(hexChars);
    }

    public static byte[] hexToByteArray(String hex) {
      if (hex == null || hex.length() == 0) {
          return null;
      }
   
      byte[] ba = new byte[hex.length() / 2];
      for (int i = 0; i < ba.length; i++) {
          ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
      }
      return ba;
    }

    public static byte[] file2byte(String filepath) {
      byte[] array = "".getBytes();
      try {
        array = Files.readAllBytes(new File(filepath).toPath());
      }catch (IOException e) { }

      return array;
    }
    
    private static byte[] copyByteArray(byte[] bytes, int bytes_len)
    {
    	byte[] array = new byte[bytes_len];
    	System.arraycopy(bytes, 0, array, 0, bytes_len);
    	return array;
    }
    
    public void addReceivedAuthFrameTypes(FrameType fType)
    {
    	receivedAuthFrameTypes.add(fType);
    }
    
    public void addSendedAuthFrameTypes(FrameType fType)
    {
    	sendedAuthFrameTypes.add(fType);
    
    }
    
    private void addIotFunctionCallHistory(IoT_Function func, int result)
    {
    	HashMap<String, Object> funcMap = new HashMap<String, Object>();
    	funcMap.put("function",func);
    	funcMap.put("result", new Integer(result));
    	iotFunctionCallHistory.add(funcMap);
    }
    
    public boolean isIotFunctionCalled(IoT_Function func)
    {
    	
    	Iterator iterator = iotFunctionCallHistory.iterator();
    	
    	while(iterator.hasNext()) {
    		HashMap map = (HashMap)iterator.next();
    	    if(map.get("function") == func ) {
    	        Integer result = (Integer)map.get("result");
    	        if ( result.intValue() == IOT_SUCCESS){
    	        	return true;
    	        }
    	    }
    	}
    	return false;
    }
    
    /**
 	 * @return the serverTokenLen
 	 */
 	public int getServerTokenLen() {
 		return ServerTokenLen;
 	}


 	/**
 	 * @param serverTokenLen the serverTokenLen to set
 	 */
 	public void setServerTokenLen(int serverTokenLen) {
 		ServerTokenLen = serverTokenLen;
 	}


 	/**
 	 *  @return renewal Flag
 	 */
 	public boolean getRenewalFlag() {
 		return this.renewalFlg;
 	}
 	
 	/**
 	 * @return the cipher
 	 */
 	public byte[] getCipher() {
 		return cipher;
 	}


 	/**
 	 * @param cipher the cipher to set
 	 */
 	public void setCipher(byte[] cipher) {
 		this.cipher = cipher;
 	}


 	/**
 	 * @return the ciperLen
 	 */
 	public Integer getCiperLen() {
 		return ciperLen;
 	}


 	/**
 	 * @param ciperLen the ciperLen to set
 	 */
 	public void setCiperLen(Integer ciperLen) {
 		this.ciperLen = ciperLen;
 	}
 	
    /**
	 * @return the clientInfo
	 */
	public byte[] getClientInfo() {
		return clientInfo;
	}


	/**
	 * @param clientInfo the clientInfo to set
	 */
	public void setClientInfo(byte[] clientInfo) {
		this.clientInfo = clientInfo;
	}


	/**
	 * @return the clientInfoLen
	 */
	public Integer getClientInfoLen() {
		return clientInfoLen;
	}


	/**
	 * @param clientInfoLen the clientInfoLen to set
	 */
	public void setClientInfoLen(Integer clientInfoLen) {
		this.clientInfoLen = clientInfoLen;
	}

	public void setdeviceSerial(byte[] deviceSerial)
	{
		this.deviceSerial = deviceSerial;
	}
	
	// INSERT START SP-193
	public void setIPAddress(String ip)
	{
		this.ipv6 = ip;
	}
	// INSERT END SP-193

	public void SendEvent() {
		if ( this.clientInfo == null){
			logger.error("ClientInfo is null, so can't send Authentication Alarm");
			return;		
		}
//		try {
//	        EventUtil.sendEvent("Authentication Alarm",
//	                TargetClass.Modem,
//	                new String(this.clientInfo, "UTF-8"),
//	                new String[][]{{"message", "Network Authentication Failure(3 Pass Authentication)"}});
//		}
//		catch (Exception ex){
//			logger.debug(ex,ex);
//		}
    	logger.error("==== SEND EVENT=====");
    }
	
	public void SendCmdAuthSPModem(int status)
	{
		if (this.deviceSerial == null ){
			logger.error("ClientInfo is null, so can't call cmdAuthSPModem");
			return;
		}
		
		HttpURLConnection urlConnection = null;
		try {
			logger.debug("deviceSerial[" + Hex.decode(this.deviceSerial) + "] STATUS[" + status + "]");
			
            String baseURL = null;
            
		    baseURL = "http://localhost:" + 
                    FMPProperty.getProperty("feph.webservice.port")+ 
                    "/rest/command/auth/" + Hex.decode(this.deviceSerial) + "/" + status;
			
			// INSERT START SP-193
			if ((status != CMD_AUTH_SPMODEM_SUCCESS) && (ipv6 != null)) {
				CheckThreshold.updateCount(ipv6, ThresholdName.AUTHENTICATION_ERROR);
			}
			// INSERT END SP-193
			
			URL url = new URL(baseURL);
            urlConnection = (HttpURLConnection)url.openConnection();
            
            logger.debug("Connection Timeout[" + urlConnection.getConnectTimeout() + "]");
            urlConnection.setConnectTimeout(10 * 1000);
            urlConnection.setRequestMethod("GET");
            
            logger.debug(urlConnection.getResponseCode());
            logger.debug(urlConnection.getResponseMessage());
            
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            }
		}
		catch (Exception ex){
			logger.debug(ex,ex);
		}
		finally {
		    if (urlConnection != null) {
		        try {
		            urlConnection.disconnect();
		        }
		        catch (Exception e) {}
		    }
		}
	}
	
    //=> INSERT START 2016.11.18 SP-318
    public StatusCode renewalCertificate() throws Exception
    {
    	StatusCode retVal = StatusCode.LocalError;
    	int status;
    	logger.debug("renewalCertificate start");
    	
    	// check 
    	if ( this.renewalFlg == false ){
    		logger.error("renewal certificate was not called");
    		return retVal;
    	}
    	
		// Renewal
    	status = getPassword();
    	if ( status != IOT_SUCCESS ){
    		// error
    		logger.error("GET Passwrod Fail");
	   		SendCmdAuthSPModem(CMD_AUTH_SPMODEM_FAIL);
	   		SendEvent();
    		return retVal;
    	}
    	status = updateCertificate(this.clientInfo);
    	if ( status != IOT_SUCCESS) {
    		 //error
    		 logger.error("updateCertificate Fail");
    		 SendCmdAuthSPModem(CMD_AUTH_SPMODEM_FAIL);
    		 SendEvent();
    		 return retVal;
    	}
    	retVal = StatusCode.Success;
    	SendEvent("Certificate was update.( Certificate renewal )");
    	return retVal;
    }

    private byte[] call_IS_IoT_PKI_EncryptParam(byte[] certBuff)
    {
    	logger.debug("-------------call_IS_IoT_PKI_EncryptParam Start-----------------");
    	byte[] cipher_pki = new byte[1000];
    	Integer cipher_pki_len = cipher_pki.length;
    	byte[] encryptBuff = null;
    	
    	byte[] prikey_buffer_pki = file2byte(pkiPrivateKeyFilePath);
    	Integer prikey_buffer_pki_len = prikey_buffer_pki.length;
    	
    	logger.debug("pkySertFilePath:" +pkiSertFilePath);
    	logger.debug("certBuff("+certBuff.length+"):"+PrintHex(certBuff,certBuff.length));
    	logger.debug("pkiPrivateKeyFilePath:"+pkiPrivateKeyFilePath);
    	logger.debug("prikey_buffer_pki"+prikey_buffer_pki_len+"):" + PrintHex(prikey_buffer_pki,prikey_buffer_pki_len) );
    	logger.debug("passWord("+passWordLen+"):" + PrintHex(passWord, passWordLen));
    	
    	int retVal = iNuriServer.IS_IoT_PKI_Encrypt
    			(this.SKEY, this.SKEYLen // session key
    					, certBuff, certBuff.length
    					, prikey_buffer_pki, prikey_buffer_pki_len  // 
    					, this.passWord, this.passWordLen           //
    					, cipher_pki, cipher_pki_len);
    	if ( retVal == IOT_SUCCESS ){
    		logger.debug("IS_IoT_PKI_Encrypt() SUCCESS");
	    	logger.debug("cipher_pki:" + PrintHex(cipher_pki,cipher_pki_len));
	    	logger.debug("cipher_pki_len:" + cipher_pki_len);
	    	encryptBuff = copyByteArray(cipher_pki,cipher_pki_len );
    	}
    	else{
			String ErrorCode = ISIoTErrorCode.PKI_Encrypt.NORMAL.getErrorMessage(retVal);
			logger.error("call_IS_IoT_PKI_EncryptParam() fail. retCode = " + retVal +","+ ErrorCode);
    	}
       	addIotFunctionCallHistory(IoT_Function.IS_IoT_PKI_Encrypt, retVal );
    	logger.debug("-------------call_IS_IoT_PKI_EncryptParam End-----------------");
    	return encryptBuff;
    }
    private int updateCertificate(byte[] cinfo) 
    {
    	logger.debug(" updateCertificate Start ");
    	int retVal = IOT_FAIL;
    	if ( cinfo == null ){
    		logger.debug(" updateCertificate cinfo is null ");
    		return retVal;
    	}
    	try {
    		String str = new String(cinfo, "UTF-8");
            OacServerApi api  = new OacServerApi();
    		JsonObject json = api.oacServerApi("update_certification/" + str, null);
	    	if ( json != null && json.get("result_code") != null ){ 
		    	int result  = json.get("result_code").getAsInt();
		    	if ( result  == 0 ){ // success
		    		logger.debug("update_certification result : " + json.getAsString());
		    		String cert = json.get("cert").getAsString();
		    		String key = json.get("key").getAsString();
		    		String salt = json.get("salt").getAsString();
		    		byte[] bArrCert = Hex.encode(cert);
		    		byte[] bArrKey = Hex.encode(key);
		    		byte[] bArrSalt = Hex.encode(salt);
		    		// encrypt parameters
		    		this.renewalCert = call_IS_IoT_PKI_EncryptParam(bArrCert);
		    		if ( this.renewalCert == null ) {
		    			return retVal;
		    		}
		    		this.renewalKey = call_IS_IoT_PKI_EncryptParam(bArrKey);
		    		if ( this.renewalKey == null ) {
		    			return retVal;
		    		}
		    		this.renewalSalt = call_IS_IoT_PKI_EncryptParam(bArrSalt);
		    		if ( this.renewalSalt == null ) {
		    			return retVal;
		    		}
		    		// make payload
		    		this.renewalPayload = makeRenewalPayload();
		    		
		    		retVal = IOT_SUCCESS;
		    	}
	    	}
    	}
    	catch ( Exception e){
    		logger.error(e,e);
    	}
    	return retVal;
    }
    
	private byte[] makeRenewalPayload() {
		boolean ret = false;
		int length = 2 + this.renewalCert.length + 2 + this.renewalKey.length + 2 + this.renewalSalt.length;
		byte[]  payload = new byte[length];
		int offset = 0;
		logger.debug("makeRenewalPayload Start : length=" + length);
		
		// certificate len
		byte[] clen = DataUtil.get2ByteToInt(this.renewalCert.length);
		System.arraycopy(clen, 0, payload, offset, AuthFrameConstants.RENEW_CERT_LEN);
		offset +=  AuthFrameConstants.RENEW_CERT_LEN;
		
		// certificate 
		System.arraycopy(this.renewalCert,  0,  payload, offset, this.renewalCert.length);
		offset += this.renewalCert.length;

		// key len
		byte[] klen = DataUtil.get2ByteToInt(this.renewalKey.length);
		System.arraycopy(klen, 0, payload, offset, AuthFrameConstants.RENEW_KEY_LEN);
		offset +=  AuthFrameConstants.RENEW_KEY_LEN;
		
		// key 
		System.arraycopy(this.renewalKey,  0,  payload, offset, this.renewalKey.length);
		offset += this.renewalKey.length;

		// salt len
		byte[] slen = DataUtil.get2ByteToInt(this.renewalSalt.length);
		System.arraycopy(slen, 0, payload, offset, AuthFrameConstants.RENEW_SALT_LEN);
		offset +=  AuthFrameConstants.RENEW_SALT_LEN;
		
		// salt 
		System.arraycopy(this.renewalSalt,  0,  payload, offset, this.renewalSalt.length);
		offset += this.renewalSalt.length;

		logger.debug("payload:" + Hex.getHexDump(payload));
		return payload;
	}
    
	public byte[] getRenewalPayload() {
		return renewalPayload;
	}
    
	public void SendEvent(String message) {
		if ( this.clientInfo == null){
			logger.error("ClientInfo is null, so can't send Authentication Alarm");
			return;		
		}
		try {
	        EventUtil.sendEvent("Authentication Alarm",
	                TargetClass.Modem,
	                new String(this.clientInfo, "UTF-8"),
	                new String[][]{{"message", message}});
		}
		catch (Exception ex){
			logger.debug(ex,ex);
		}
    	logger.error("==== SEND EVENT=====");
    }
    //=> INSERT END   2016.11.18 SP-318
	
	
}
